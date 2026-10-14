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
                        nomeCliente = mensagem.substring(9).trim();
                        cliente.enviar("CONECTADO|" + nomeCliente);
                    } else {
                        cliente.enviar("ERRO|NAO_CONECTADO|Envie CONECTAR|nome antes dos comandos");
                    }
                    continue;
                }

                // ADAPTACAO: o exemplo retransmitia mensagens; agora encaminha para as regras do pedido.
                boolean pedidoCriado = false;
                for (String resposta : servidor.getPedidoService()
                        .processar(nomeCliente, cliente, mensagem)) {
                    cliente.enviar(resposta);
                    if (resposta.startsWith("PEDIDO_CRIADO|")) pedidoCriado = true;
                }
                if (pedidoCriado) servidor.notificarCatalogoAtualizado();
            }
        } catch (IOException ex) {
            System.out.println("Conexao encerrada: " + ex.getMessage());
        } finally {
            servidor.removerCliente(cliente);
        }
    }
}
