package multithreads2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Conexao TCP de um cliente. Mantem a mesma funcao da classe de conexao
 * fornecida na Aula 6.
 */
public class TCPServerConnection {

    private final Socket socket;
    private final BufferedReader input;
    private final PrintWriter output;

    public TCPServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
        this.output = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getInput() {
        return input;
    }

    /** ADAPTACAO: sincroniza escritas para que respostas e eventos nao se misturem. */
    public synchronized void enviar(String message) {
        output.println(message);
        output.flush();
    }

    /**
     * ADAPTACAO: envia varias linhas de forma atomica. Assim, uma resposta
     * multilinha (ex.: CATALOGO_ITEM... FIM_CATALOGO) nunca e intercalada com
     * mensagens assincronas enviadas por outras threads.
     */
    public synchronized void enviar(java.util.List<String> mensagens) {
        for (String message : mensagens) output.println(message);
        output.flush();
    }

    public void fechar() {
        try {
            input.close();
        } catch (IOException ignored) {
        }
        output.close();
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
