package multithreads2;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/** Conexao do cliente, derivada do TCPClientMain (multithreads2) da Aula 6. */
public class TCPClientMain {

    public TCPClientMain(String serverAddress, int serverPort, TCPClientForm caller) throws UnknownHostException, IOException {
        this.socket = new Socket(serverAddress, serverPort);
        //this.socket.setKeepAlive(true);
        handler = new TCPClientHandler(socket, caller);
        this.handler.start();
        // ADAPTACAO: UTF-8 explicito, o mesmo charset usado pelo servidor.
        this.output = new PrintWriter(new OutputStreamWriter(
                this.socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    public void writeMessage(String outMessage) {
        this.output.println(outMessage);
    }

    public void closeConnection() throws IOException {
        // ADAPTACAO: interrupt() antes de fechar, para o handler saber que o
        // encerramento foi pedido pelo proprio cliente (e nao pelo servidor).
        this.handler.interrupt();
        this.output.close();
        this.socket.close();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.closeConnection();
        } finally {
            super.finalize();
        }
    }

    private TCPClientHandler handler;
    private Socket socket;
    private PrintWriter output;
}
