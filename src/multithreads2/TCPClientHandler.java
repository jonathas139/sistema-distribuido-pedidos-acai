package multithreads2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Thread receptora do cliente, derivada do TCPClientHandler (multithreads2) da
 * Aula 6. Como no Bolinha3ClientHandler, cada mensagem e separada com
 * StringTokenizer e "|" e repassada ao formulario (caller).
 */
public class TCPClientHandler extends Thread {

    private Socket socket;
    private TCPClientForm caller;
    private BufferedReader input;
    // ADAPTACAO: o catalogo chega em varias linhas (CATALOGO_ITEM... FIM_CATALOGO).
    private List<String[]> catalogo = new ArrayList<String[]>();
    private boolean atualizacao = false;

    public TCPClientHandler(Socket socket, TCPClientForm caller) throws IOException {
        this.socket = socket;
        this.caller = caller;
        this.input = new BufferedReader(new InputStreamReader(
                this.socket.getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                if (this.socket.isConnected() && this.input != null) {
                    message = this.input.readLine();
                } else {
                    break;
                }
                if (message == null || message.equals("")) {
                    break;
                }
                if (this.isInterrupted()) {
                    break;
                }
                this.caller.escreverMensagem(message);
                interpretarMensagem(message);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        // ADAPTACAO: avisa o formulario quando o servidor encerra a conexao.
        if (!this.isInterrupted()) {
            this.caller.conexaoEncerrada();
        }
    }

    /** ADAPTACAO: traduz as respostas do protocolo em chamadas ao formulario. */
    private void interpretarMensagem(String message) {
        try {
            StringTokenizer tokens = new StringTokenizer(message, "|");
            String tipo = tokens.nextToken();
            if (tipo.equals("CONECTADO")) {
                caller.conectado(tokens.nextToken());
            } else if (tipo.equals("CATALOGO_ATUALIZADO")) {
                atualizacao = true;
                catalogo = new ArrayList<String[]>();
            } else if (tipo.equals("CATALOGO_ITEM")) {
                String id = tokens.nextToken();
                String nome = tokens.nextToken();
                String preco = tokens.nextToken();
                String disponivel = tokens.nextToken();
                String quantidade = tokens.nextToken();
                catalogo.add(new String[]{id, nome, preco, disponivel, quantidade});
            } else if (tipo.equals("FIM_CATALOGO")) {
                caller.atualizarCatalogo(catalogo, atualizacao);
                catalogo = new ArrayList<String[]>();
                atualizacao = false;
            } else if (tipo.equals("PEDIDO_CRIADO")) {
                int id = Integer.parseInt(tokens.nextToken());
                String status = tokens.nextToken();
                int previsao = Integer.parseInt(tokens.nextToken());
                caller.pedidoCriado(id, status, previsao);
            } else if (tipo.equals("STATUS_PEDIDO")) {
                int id = Integer.parseInt(tokens.nextToken());
                String status = tokens.nextToken();
                int previsao = Integer.parseInt(tokens.nextToken());
                caller.statusPedido(id, status, previsao);
            } else if (tipo.equals("EVENTO_STATUS")) {
                int id = Integer.parseInt(tokens.nextToken());
                caller.eventoStatus(id, tokens.nextToken());
            } else if (tipo.equals("ERRO")) {
                String codigo = tokens.nextToken();
                // a mensagem de erro pode conter "|", entao junta o que sobrou
                StringBuilder texto = new StringBuilder();
                while (tokens.hasMoreTokens()) {
                    if (texto.length() > 0) {
                        texto.append("|");
                    }
                    texto.append(tokens.nextToken());
                }
                caller.erro(codigo, texto.toString());
            }
        } catch (RuntimeException ex) {
            // mensagem malformada nao deve derrubar a thread receptora
            System.out.println("Mensagem ignorada: " + message);
        }
    }
}
