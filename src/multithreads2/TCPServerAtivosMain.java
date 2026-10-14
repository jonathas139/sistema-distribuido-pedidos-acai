package multithreads2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/** Servidor TCP derivado do TCPServerAtivosMain fornecido na Aula 6. */
public class TCPServerAtivosMain extends Thread {

    private final List<TCPServerConnection> clientes = new ArrayList<>();
    private final ServerSocket server;
    private final PedidoService pedidoService;

    public TCPServerAtivosMain(int porta) throws IOException {
        server = new ServerSocket(porta);
        pedidoService = new PedidoService();
        pedidoService.start();
        System.out.println("Servidor TCP rodando na porta: " + server.getLocalPort());
    }

    @Override
    public void run() {
        while (!server.isClosed()) {
            try {
                Socket socket = server.accept();
                TCPServerConnection cliente = new TCPServerConnection(socket);
                synchronized (clientes) {
                    clientes.add(cliente);
                }
                // PADRAO DA AULA 6: cada conexao recebida ganha sua propria thread.
                new TCPServerAtivosHandler(cliente, this).start();
            } catch (IOException ex) {
                if (!server.isClosed()) System.out.println("Erro no accept: " + ex.getMessage());
            }
        }
    }

    public PedidoService getPedidoService() {
        return pedidoService;
    }

    /** ADAPTACAO DE REGRA: informa clientes conectados quando uma compra altera disponibilidade. */
    public void notificarCatalogoAtualizado() {
        List<TCPServerConnection> ativos;
        synchronized (clientes) {
            ativos = new ArrayList<>(clientes);
        }
        for (TCPServerConnection cliente : ativos) {
            cliente.enviar("CATALOGO_ATUALIZADO");
            for (String linha : pedidoService.gerarCatalogo()) cliente.enviar(linha);
        }
    }

    public void removerCliente(TCPServerConnection cliente) {
        synchronized (clientes) {
            clientes.remove(cliente);
        }
        cliente.fechar();
    }

    public void encerrar() throws IOException {
        pedidoService.interrupt();
        server.close();
        synchronized (clientes) {
            for (TCPServerConnection cliente : clientes) cliente.fechar();
            clientes.clear();
        }
    }

    public static void main(String[] args) throws IOException {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        new TCPServerAtivosMain(porta).start();
    }
}
