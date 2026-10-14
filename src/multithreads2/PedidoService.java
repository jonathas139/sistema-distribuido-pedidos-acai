package multithreads2;

import java.util.ArrayList;
import java.util.List;

/**
 * Regras de negocio minimas: catalogo, estoque, pedidos e fila FIFO.
 * Os metodos que leem ou alteram o estado compartilhado sao synchronized,
 * aplicando a sincronizacao apresentada na Aula 6.
 */
public class PedidoService extends Thread {

    private static class Produto {
        String id;
        String nome;
        String preco;
        String ingrediente;
        int quantidadeBase;
        int tempoSegundos;

        Produto(String id, String nome, String preco, String ingrediente,
                int quantidadeBase, int tempoSegundos) {
            this.id = id;
            this.nome = nome;
            this.preco = preco;
            this.ingrediente = ingrediente;
            this.quantidadeBase = quantidadeBase;
            this.tempoSegundos = tempoSegundos;
        }
    }

    private final List<Produto> catalogo = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();
    private final List<Pedido> fila = new ArrayList<>();
    private final int[] estoque = new int[6];
    private int proximoPedidoId = 1;

    public PedidoService() {
        setName("PedidoService");

        // ADAPTACAO DE REGRA DE NEGOCIO: catalogo e estoque do exemplo Acai do Nando.
        catalogo.add(new Produto("0", "Acai Natural 200ml", "10.00", "natural", 200, 3));
        catalogo.add(new Produto("1", "Acai Natural 400ml", "20.00", "natural", 400, 5));
        catalogo.add(new Produto("2", "Acai com Guarana 200ml", "12.00", "guarana", 200, 3));
        catalogo.add(new Produto("3", "Acai com Guarana 400ml", "24.00", "guarana", 400, 5));
        catalogo.add(new Produto("4", "Acai Zero Acucar 200ml", "14.00", "zero", 200, 3));
        catalogo.add(new Produto("5", "Acai Zero Acucar 400ml", "28.00", "zero", 400, 5));
        catalogo.add(new Produto("6", "Agua Geladinha", "3.00", "agua", 1, 0));

        // indices: natural, guarana, zero em ml; banana, bis e agua em unidades.
        estoque[0] = 1000;
        estoque[1] = 1000;
        estoque[2] = 1000;
        estoque[3] = 5;
        estoque[4] = 5;
        estoque[5] = 5;
    }

    /** ADAPTACAO: recebe comandos textuais e os converte em operacoes do negocio. */
    public synchronized List<String> processar(String cliente,
            TCPServerConnection conexao, String mensagem) {
        List<String> respostas = new ArrayList<>();
        String[] campos = mensagem.split("\\|", -1);

        if ("CATALOGO".equals(campos[0])) {
            for (Produto produto : catalogo) {
                respostas.add("CATALOGO_ITEM|" + produto.id + "|" + produto.nome
                        + "|" + produto.preco + "|" + (disponivel(produto) ? "1" : "0"));
            }
            respostas.add("FIM_CATALOGO");
        } else if ("PEDIR".equals(campos[0])) {
            criarPedido(cliente, conexao, campos, respostas);
        } else if ("STATUS".equals(campos[0])) {
            consultarStatus(cliente, campos, respostas);
        } else {
            respostas.add("ERRO|COMANDO_INVALIDO|Comando nao reconhecido");
        }
        return respostas;
    }

    /** Gera o estado atual para consulta ou notificacao de mudanca no estoque. */
    public synchronized List<String> gerarCatalogo() {
        List<String> respostas = new ArrayList<>();
        for (Produto produto : catalogo) {
            respostas.add("CATALOGO_ITEM|" + produto.id + "|" + produto.nome
                    + "|" + produto.preco + "|" + (disponivel(produto) ? "1" : "0"));
        }
        respostas.add("FIM_CATALOGO");
        return respostas;
    }

    private void criarPedido(String cliente, TCPServerConnection conexao,
            String[] campos, List<String> respostas) {
        if (campos.length != 4) {
            respostas.add("ERRO|FORMATO_INVALIDO|Use PEDIR|produtoId|quantidade|opcionais");
            return;
        }

        Produto produto = buscarProduto(campos[1]);
        if (produto == null) {
            respostas.add("ERRO|PRODUTO_INVALIDO|Produto nao encontrado");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(campos[2]);
        } catch (NumberFormatException ex) {
            respostas.add("ERRO|QUANTIDADE_INVALIDA|Quantidade deve ser um inteiro positivo");
            return;
        }
        if (quantidade < 1) {
            respostas.add("ERRO|QUANTIDADE_INVALIDA|Quantidade deve ser maior que zero");
            return;
        }

        String opcionais = campos[3];
        if (!opcionais.equals("NENHUM") && !opcionais.equals("BANANA")
                && !opcionais.equals("BIS") && !opcionais.equals("BANANA+BIS")) {
            respostas.add("ERRO|OPCIONAL_INVALIDO|Use NENHUM, BANANA, BIS ou BANANA+BIS");
            return;
        }
        if (produto.ingrediente.equals("agua") && !opcionais.equals("NENHUM")) {
            respostas.add("ERRO|OPCIONAL_INVALIDO|Agua Geladinha nao aceita opcionais");
            return;
        }

        if (!temEstoque(produto, quantidade, opcionais)) {
            respostas.add("ERRO|SEM_ESTOQUE|Estoque insuficiente para este pedido");
            return;
        }

        baixarEstoque(produto, quantidade, opcionais);
        int id = proximoPedidoId++;
        Pedido pedido = new Pedido(id, cliente, produto.id, quantidade, opcionais,
                produto.tempoSegundos, conexao);
        pedidos.add(pedido);
        fila.add(pedido);
        notifyAll();

        int previsao = produto.tempoSegundos;
        for (Pedido aguardando : fila) {
            if (aguardando.getId() != id) {
                previsao += aguardando.getTempoProducaoSegundos();
            }
        }
        respostas.add("PEDIDO_CRIADO|" + id + "|AGUARDANDO|" + previsao);
    }

    private void consultarStatus(String cliente, String[] campos, List<String> respostas) {
        if (campos.length != 2) {
            respostas.add("ERRO|FORMATO_INVALIDO|Use STATUS|pedidoId");
            return;
        }
        try {
            int id = Integer.parseInt(campos[1]);
            for (Pedido pedido : pedidos) {
                if (pedido.getId() == id && pedido.getCliente().equals(cliente)) {
                    respostas.add("STATUS_PEDIDO|" + id + "|" + pedido.getStatus()
                            + "|" + pedido.getTempoProducaoSegundos());
                    return;
                }
            }
            respostas.add("ERRO|PEDIDO_NAO_ENCONTRADO|Pedido nao encontrado para este cliente");
        } catch (NumberFormatException ex) {
            respostas.add("ERRO|ID_INVALIDO|Identificador do pedido invalido");
        }
    }

    private Produto buscarProduto(String id) {
        for (Produto produto : catalogo) {
            if (produto.id.equals(id)) return produto;
        }
        return null;
    }

    private boolean disponivel(Produto produto) {
        return estoque[indiceIngrediente(produto.ingrediente)] >= produto.quantidadeBase;
    }

    private boolean temEstoque(Produto produto, int quantidade, String opcionais) {
        int base = produto.quantidadeBase * quantidade;
        if (estoque[indiceIngrediente(produto.ingrediente)] < base) return false;
        if (opcionais.contains("BANANA") && estoque[3] < quantidade) return false;
        if (opcionais.contains("BIS") && estoque[4] < quantidade) return false;
        return true;
    }

    private void baixarEstoque(Produto produto, int quantidade, String opcionais) {
        estoque[indiceIngrediente(produto.ingrediente)] -= produto.quantidadeBase * quantidade;
        if (opcionais.contains("BANANA")) estoque[3] -= quantidade;
        if (opcionais.contains("BIS")) estoque[4] -= quantidade;
    }

    private int indiceIngrediente(String ingrediente) {
        if (ingrediente.equals("natural")) return 0;
        if (ingrediente.equals("guarana")) return 1;
        if (ingrediente.equals("zero")) return 2;
        return 5;
    }

    /** Thread consumidora da fila FIFO; wait/notify seguem o conteudo da Aula 6. */
    @Override
    public void run() {
        while (!isInterrupted()) {
            Pedido pedido;
            synchronized (this) {
                while (fila.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        interrupt();
                        return;
                    }
                }
                pedido = fila.remove(0);
                pedido.setStatus("EM_PREPARACAO");
            }

            notificar(pedido);
            try {
                Thread.sleep(pedido.getTempoProducaoSegundos() * 1000L);
            } catch (InterruptedException ex) {
                interrupt();
                return;
            }

            synchronized (this) {
                pedido.setStatus("CONCLUIDO");
            }
            notificar(pedido);
        }
    }

    private void notificar(Pedido pedido) {
        pedido.getConexao().enviar("EVENTO_STATUS|" + pedido.getId() + "|" + pedido.getStatus());
    }
}
