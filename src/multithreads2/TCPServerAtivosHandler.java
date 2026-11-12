package multithreads2;

import java.io.IOException;

/** Uma instancia por cliente, conforme TCPServerAtivosHandler da Aula 6. */
public class TCPServerAtivosHandler extends Thread {

    private final TCPServerConnection cliente;
    private final TCPServerAtivosMain servidor;
    private String nomeCliente;

    public TCPServerAtivosHandler(TCPServerConnection cliente,
            TCPServerAtivosMain servidor) {
        this.cliente = cliente;
        this.servidor = servidor;
        this.nomeCliente = null;
    }

    @Override
    public void run() {
        try {
            String mensagem;
            while ((mensagem = cliente.getInput().readLine()) != null) {
                if (mensagem.equals("SAIR")) break;
                if (nomeCliente == null) {
                    if (mensagem.startsWith("CONECTAR|") && mensagem.length() > 9) {
                        String nome = mensagem.substring(9).trim();
                        if (nome.isEmpty() || nome.contains("|")) {
                            cliente.enviar("ERRO|NOME_INVALIDO|Nome vazio ou com o caractere |");
                        } else if (!servidor.registrarNome(nome)) {
                            // ADAPTACAO: cada cliente precisa de um identificador unico.
                            cliente.enviar("ERRO|NOME_EM_USO|Ja existe um cliente conectado com este nome");
                        } else {
                            nomeCliente = nome;
                            cliente.enviar("CONECTADO|" + nomeCliente);
                        }
                    } else {
                        cliente.enviar("ERRO|NAO_CONECTADO|Envie CONECTAR|nome antes dos comandos");
                    }
                    continue;
                }

                // ADAPTACAO: o exemplo retransmitia mensagens; agora encaminha para as regras do pedido.
                boolean pedidoCriado = false;
                // processar() ja envia as respostas ao cliente (ver PedidoService).
                for (String resposta : servidor.getPedidoService()
                        .processar(nomeCliente, cliente, mensagem)) {
                    if (resposta.startsWith("PEDIDO_CRIADO|")) pedidoCriado = true;
                }
                if (pedidoCriado) servidor.notificarCatalogoAtualizado();
            }
        } catch (IOException ex) {
            System.out.println("Conexao encerrada: " + ex.getMessage());
        } finally {
            servidor.removerCliente(cliente);
            if (nomeCliente != null) servidor.liberarNome(nomeCliente);
        }
    }
}
