package multithreads2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada de protocolo do cliente (sem interface grafica).
 *
 * Mantem um socket TCP persistente e uma thread receptora, no mesmo padrao do
 * cliente multithreaded da Aula 6 (ver TCPClientConsole). Cada linha recebida
 * e interpretada e entregue ao {@link Ouvinte}. As chamadas ao ouvinte ocorrem
 * na thread receptora; a GUI deve usar SwingUtilities.invokeLater.
 *
 * Como o servidor responde aos comandos na ordem em que chegam, a classe guarda
 * uma fila dos comandos aguardando resposta. Isso permite associar um
 * PEDIDO_CRIADO ou ERRO ao comando PEDIR que o originou. Mensagens
 * assincronas (EVENTO_STATUS, CATALOGO_ATUALIZADO) nao consomem a fila.
 */
public class ClienteAcai {

    /** Linha do catalogo: CATALOGO_ITEM|id|nome|preco|disponivel. */
    public static class ItemCatalogo {
        public final String id;
        public final String nome;
        public final String preco;
        public final boolean disponivel;

        public ItemCatalogo(String id, String nome, String preco, boolean disponivel) {
            this.id = id;
            this.nome = nome;
            this.preco = preco;
            this.disponivel = disponivel;
        }
    }

    public interface Ouvinte {
        void conectado(String nome);

        /** @param atualizacao true quando veio por CATALOGO_ATUALIZADO (assincrono). */
        void catalogo(List<ItemCatalogo> itens, boolean atualizacao);

        /** @param comando linha PEDIR enviada que originou este pedido. */
        void pedidoCriado(String comando, int id, String status, int previsaoSegundos);

        void statusPedido(int id, String status, int previsaoSegundos);

        void eventoStatus(int id, String status);

        /** @param comando comando que originou o erro (null se nao houver). */
        void erro(String comando, String codigo, String mensagem);

        void desconectado(String motivo);

        /** Registro bruto do trafego: direcao "->" (enviado) ou "<-" (recebido). */
        void linha(String direcao, String texto);
    }

    private final Ouvinte ouvinte;
    private final ArrayDeque<String> aguardandoResposta = new ArrayDeque<>();
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter saida;
    private volatile boolean encerrado = false;

    // Estado do parser do catalogo (acessado apenas pela thread receptora).
    private List<ItemCatalogo> catalogoEmMontagem = new ArrayList<>();
    private boolean catalogoAssincrono = false;

    public ClienteAcai(Ouvinte ouvinte) {
        this.ouvinte = ouvinte;
    }

    /** Abre o socket, inicia a thread receptora e envia CONECTAR|nome. Bloqueante. */
    public void conectar(String endereco, int porta, String nome) throws IOException {
        Socket s = new Socket();
        s.connect(new InetSocketAddress(endereco, porta), 5000);
        socket = s;
        entrada = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        saida = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);

        // PADRAO DA AULA 6: thread dedicada a ler mensagens do servidor.
        Thread receptor = new Thread(this::receber, "ClienteAcaiReceptor");
        receptor.setDaemon(true);
        receptor.start();

        enviar("CONECTAR|" + nome);
    }

    /** Envia um comando (uma linha UTF-8 terminada em \n). */
    public synchronized void enviar(String comando) {
        if (encerrado || saida == null) return;
        String tipo = comando.split("\\|", 2)[0];
        if (!tipo.equals("SAIR")) aguardandoResposta.add(comando);
        saida.println(comando);
        saida.flush();
        ouvinte.linha("->", comando);
    }

    public void pedirCatalogo() {
        enviar("CATALOGO");
    }

    public void pedir(String produtoId, int quantidade, String opcionais) {
        enviar("PEDIR|" + produtoId + "|" + quantidade + "|" + opcionais);
    }

    public void consultarStatus(int pedidoId) {
        enviar("STATUS|" + pedidoId);
    }

    public void sair() {
        enviar("SAIR");
        fechar();
    }

    public void fechar() {
        encerrado = true;
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }

    private synchronized String proximoComando() {
        return aguardandoResposta.poll();
    }

    private void receber() {
        String motivo = "Conexao encerrada pelo servidor";
        try {
            String linha;
            while ((linha = entrada.readLine()) != null) {
                ouvinte.linha("<-", linha);
                interpretar(linha);
            }
        } catch (IOException ex) {
            if (!encerrado) motivo = "Conexao perdida: " + ex.getMessage();
        } finally {
            if (!encerrado) {
                encerrado = true;
                ouvinte.desconectado(motivo);
            } else {
                ouvinte.desconectado("Desconectado");
            }
        }
    }

    private void interpretar(String linha) {
        String[] c = linha.split("\\|", -1);
        try {
            switch (c[0]) {
                case "CONECTADO":
                    proximoComando();
                    ouvinte.conectado(c[1]);
                    break;
                case "CATALOGO_ATUALIZADO":
                    catalogoAssincrono = true;
                    catalogoEmMontagem = new ArrayList<>();
                    break;
                case "CATALOGO_ITEM":
                    catalogoEmMontagem.add(new ItemCatalogo(c[1], c[2], c[3], "1".equals(c[4])));
                    break;
                case "FIM_CATALOGO":
                    // Resposta a CATALOGO consome a fila; a atualizacao assincrona nao.
                    if (!catalogoAssincrono) proximoComando();
                    ouvinte.catalogo(catalogoEmMontagem, catalogoAssincrono);
                    catalogoEmMontagem = new ArrayList<>();
                    catalogoAssincrono = false;
                    break;
                case "PEDIDO_CRIADO":
                    ouvinte.pedidoCriado(proximoComando(), Integer.parseInt(c[1]), c[2],
                            Integer.parseInt(c[3]));
                    break;
                case "STATUS_PEDIDO":
                    proximoComando();
                    ouvinte.statusPedido(Integer.parseInt(c[1]), c[2], Integer.parseInt(c[3]));
                    break;
                case "EVENTO_STATUS":
                    ouvinte.eventoStatus(Integer.parseInt(c[1]), c[2]);
                    break;
                case "ERRO":
                    ouvinte.erro(proximoComando(), c[1], c.length > 2 ? c[2] : "");
                    break;
                default:
                    break; // mensagem desconhecida: fica apenas no log
            }
        } catch (RuntimeException ex) {
            // Mensagem malformada nao deve derrubar a thread receptora.
            ouvinte.linha("!!", "Mensagem ignorada (" + ex.getClass().getSimpleName() + "): " + linha);
        }
    }
}
