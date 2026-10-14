package multithreads2;

/** Registro simples mantido em memoria durante a execucao do servidor. */
public class Pedido {

    private final int id;
    private final String cliente;
    private final String produtoId;
    private final int quantidade;
    private final String opcionais;
    private final int tempoProducaoSegundos;
    private final TCPServerConnection conexao;
    private String status;

    public Pedido(int id, String cliente, String produtoId, int quantidade,
            String opcionais, int tempoProducaoSegundos,
            TCPServerConnection conexao) {
        this.id = id;
        this.cliente = cliente;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.opcionais = opcionais;
        this.tempoProducaoSegundos = tempoProducaoSegundos;
        this.conexao = conexao;
        this.status = "AGUARDANDO";
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public String getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }
    public String getOpcionais() { return opcionais; }
    public int getTempoProducaoSegundos() { return tempoProducaoSegundos; }
    public TCPServerConnection getConexao() { return conexao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
