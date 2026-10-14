package multithreads2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Cliente de console para exercitar o protocolo antes da integracao com a GUI. */
public class TCPClientConsole {

    public static void main(String[] args) throws Exception {
        String endereco = args.length > 0 ? args[0] : "127.0.0.1";
        int porta = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        String nome = args.length > 2 ? args[2] : "ClienteConsole";

        Socket socket = new Socket(endereco, porta);
        BufferedReader input = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
        PrintWriter output = new PrintWriter(new java.io.OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8), true);

        // PADRAO DA AULA 6: thread dedicada a receber mensagens do servidor.
        Thread receiver = new Thread(() -> {
            try {
                String mensagem;
                while ((mensagem = input.readLine()) != null) {
                    System.out.println("Servidor> " + mensagem);
                }
            } catch (Exception ex) {
                System.out.println("Recepcao encerrada: " + ex.getMessage());
            }
        }, "TCPClientReceiver");
        receiver.start();

        output.println("CONECTAR|" + nome);
        System.out.println("Digite CATALOGO, PEDIR|produtoId|quantidade|opcionais, STATUS|pedidoId ou SAIR.");
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                String mensagem = scanner.nextLine();
                output.println(mensagem);
                if (mensagem.equals("SAIR")) break;
            }
        } finally {
            socket.close();
        }
    }
}
