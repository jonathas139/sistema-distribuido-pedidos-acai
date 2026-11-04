package multithreads2;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Cliente grafico (Swing) do sistema de pedidos de acai.
 *
 * Toda a comunicacao fica em {@link ClienteAcai}; esta classe so apresenta os
 * dados e converte acoes do usuario em comandos. Como o servidor pode enviar
 * EVENTO_STATUS e CATALOGO_ATUALIZADO a qualquer momento, cada callback do
 * ouvinte e repassado a thread de eventos do Swing com invokeLater.
 */
public class AcaiClienteGUI extends JFrame {

    private static final Color VERDE = new Color(0x2E7D32);
    private static final Color LARANJA = new Color(0xE65100);
    private static final Color CINZA = new Color(0x9E9E9E);
    private static final Color ROXO = new Color(0x4A148C);

    private final CardLayout cartas = new CardLayout();
    private final JPanel raiz = new JPanel(cartas);

    // --- tela de login ---
    private final JTextField campoServidor = new JTextField("127.0.0.1", 14);
    private final JTextField campoPorta = new JTextField("5000", 6);
    private final JTextField campoNome = new JTextField(14);
    private final JButton botaoConectar = new JButton("Conectar");
    private final JLabel rotuloErroLogin = new JLabel(" ");

    // --- tela principal ---
    private final JLabel rotuloConexao = new JLabel();
    private final JLabel rotuloMensagem = new JLabel(" ");
    private final DefaultTableModel modeloCatalogo = new DefaultTableModel(
            new Object[] {"Id", "Produto", "Preço", "Disponibilidade"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tabelaCatalogo = new JTable(modeloCatalogo);
    private final JSpinner campoQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JCheckBox caixaBanana = new JCheckBox("Banana");
    private final JCheckBox caixaBis = new JCheckBox("Bis");
    private final JLabel rotuloSelecionado = new JLabel("Selecione um produto no catálogo");
    private final JButton botaoPedir = new JButton("Fazer pedido");
    private final DefaultTableModel modeloPedidos = new DefaultTableModel(
            new Object[] {"Pedido", "Produto", "Qtd", "Opcionais", "Status", "Previsão"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tabelaPedidos = new JTable(modeloPedidos);
    private final JButton botaoStatus = new JButton("Consultar status");
    private final JTextArea areaLog = new JTextArea();

    // --- estado (acessado somente na thread de eventos do Swing) ---
    private Sessao sessao;
    private boolean naTelaPrincipal = false;
    private List<ClienteAcai.ItemCatalogo> catalogoAtual = new ArrayList<>();
    private final Map<Integer, String> statusAntecipado = new HashMap<>();

    public AcaiClienteGUI(String servidor, String porta, String nome) {
        super("Açaí — Cliente de Pedidos");
        campoServidor.setText(servidor);
        campoPorta.setText(porta);
        campoNome.setText(nome);

        raiz.add(criarTelaLogin(), "login");
        raiz.add(criarTelaPrincipal(), "principal");
        setContentPane(raiz);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                encerrarSessao();
                dispose();
                System.exit(0);
            }
        });
        setSize(860, 640);
        setMinimumSize(new Dimension(720, 520));
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(botaoConectar);
    }

    // ------------------------------------------------------------------
    // Construcao das telas
    // ------------------------------------------------------------------

    private JPanel criarTelaLogin() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Color.WHITE);

        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD1C4E9)),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));

        JLabel titulo = new JLabel("Açaí — Pedidos");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        titulo.setForeground(ROXO);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 4, 6, 4);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        g.insets = new Insets(0, 4, 16, 4);
        cartao.add(titulo, g);

        g.gridwidth = 1;
        g.insets = new Insets(6, 4, 6, 4);
        adicionarLinha(cartao, g, 1, "Servidor", campoServidor);
        adicionarLinha(cartao, g, 2, "Porta", campoPorta);
        adicionarLinha(cartao, g, 3, "Seu nome", campoNome);

        rotuloErroLogin.setForeground(new Color(0xC62828));
        g.gridx = 0;
        g.gridy = 4;
        g.gridwidth = 2;
        cartao.add(rotuloErroLogin, g);

        botaoConectar.addActionListener(e -> conectar());
        g.gridy = 5;
        g.fill = GridBagConstraints.HORIZONTAL;
        cartao.add(botaoConectar, g);

        painel.add(cartao);
        return painel;
    }

    private void adicionarLinha(JPanel painel, GridBagConstraints g, int linha,
            String rotulo, Component campo) {
        g.gridx = 0;
        g.gridy = linha;
        painel.add(new JLabel(rotulo), g);
        g.gridx = 1;
        painel.add(campo, g);
    }

    private JPanel criarTelaPrincipal() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));

        // Barra superior: quem esta conectado + sair
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(ROXO);
        barra.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        rotuloConexao.setForeground(Color.WHITE);
        rotuloConexao.setFont(rotuloConexao.getFont().deriveFont(Font.BOLD, 14f));
        JButton sair = new JButton("Sair");
        sair.addActionListener(e -> {
            encerrarSessao();
            mostrarLogin("");
        });
        barra.add(rotuloConexao, BorderLayout.WEST);
        barra.add(sair, BorderLayout.EAST);
        painel.add(barra, BorderLayout.NORTH);

        // Catalogo + formulario de pedido
        configurarTabela(tabelaCatalogo);
        tabelaCatalogo.getColumnModel().getColumn(0).setMaxWidth(50);
        tabelaCatalogo.getColumnModel().getColumn(2).setMaxWidth(100);
        tabelaCatalogo.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean foco, int linha, int coluna) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foco, linha, coluna);
                boolean disponivel = linha < catalogoAtual.size() && catalogoAtual.get(linha).disponivel;
                if (!sel) c.setForeground(disponivel ? Color.BLACK : CINZA);
                if (coluna == 3 && !sel) c.setForeground(disponivel ? VERDE : new Color(0xC62828));
                return c;
            }
        });
        tabelaCatalogo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) atualizarFormulario();
        });

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        formulario.add(rotuloSelecionado);
        formulario.add(new JLabel("Quantidade:"));
        formulario.add(campoQuantidade);
        formulario.add(caixaBanana);
        formulario.add(caixaBis);
        botaoPedir.setEnabled(false);
        botaoPedir.addActionListener(e -> fazerPedido());
        formulario.add(botaoPedir);
        JButton atualizar = new JButton("Atualizar catálogo");
        atualizar.addActionListener(e -> {
            if (sessao != null) sessao.cliente.pedirCatalogo();
        });
        formulario.add(atualizar);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createTitledBorder("Catálogo"));
        topo.add(new JScrollPane(tabelaCatalogo), BorderLayout.CENTER);
        topo.add(formulario, BorderLayout.SOUTH);

        // Pedidos acompanhados + log do protocolo
        configurarTabela(tabelaPedidos);
        tabelaPedidos.getColumnModel().getColumn(0).setMaxWidth(70);
        tabelaPedidos.getColumnModel().getColumn(2).setMaxWidth(50);
        tabelaPedidos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean foco, int linha, int coluna) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foco, linha, coluna);
                if (!sel) {
                    c.setForeground(Color.BLACK);
                    if (coluna == 4) {
                        String s = String.valueOf(v);
                        if (s.startsWith("Conclu")) c.setForeground(VERDE);
                        else if (s.startsWith("Em prep")) c.setForeground(LARANJA);
                    }
                }
                return c;
            }
        });
        tabelaPedidos.getSelectionModel().addListSelectionListener(
                e -> botaoStatus.setEnabled(tabelaPedidos.getSelectedRow() >= 0));
        botaoStatus.setEnabled(false);
        botaoStatus.addActionListener(e -> consultarStatusSelecionado());

        JPanel painelPedidos = new JPanel(new BorderLayout());
        painelPedidos.add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);
        JPanel botoesPedidos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoesPedidos.add(botaoStatus);
        painelPedidos.add(botoesPedidos, BorderLayout.SOUTH);

        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Meus pedidos", painelPedidos);
        abas.addTab("Log do protocolo", new JScrollPane(areaLog));

        JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topo, abas);
        divisao.setResizeWeight(0.5);
        divisao.setBorder(BorderFactory.createEmptyBorder(6, 6, 0, 6));
        painel.add(divisao, BorderLayout.CENTER);

        rotuloMensagem.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        painel.add(rotuloMensagem, BorderLayout.SOUTH);
        return painel;
    }

    private void configurarTabela(JTable tabela) {
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
    }

    // ------------------------------------------------------------------
    // Acoes do usuario
    // ------------------------------------------------------------------

    private void conectar() {
        String servidor = campoServidor.getText().trim();
        String nome = campoNome.getText().trim();
        int porta;
        try {
            porta = Integer.parseInt(campoPorta.getText().trim());
        } catch (NumberFormatException ex) {
            rotuloErroLogin.setText("Porta inválida");
            return;
        }
        if (servidor.isEmpty() || nome.isEmpty()) {
            rotuloErroLogin.setText("Informe o servidor e o seu nome");
            return;
        }
        if (nome.contains("|")) {
            rotuloErroLogin.setText("O nome não pode conter o caractere |");
            return;
        }

        rotuloErroLogin.setText("Conectando...");
        botaoConectar.setEnabled(false);
        encerrarSessao();
        final Sessao nova = new Sessao(servidor + ":" + porta);
        sessao = nova;
        // Conexao em thread propria para nao congelar a interface.
        new Thread(() -> {
            try {
                nova.cliente.conectar(servidor, porta, nome);
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    if (sessao != nova) return;
                    sessao = null;
                    rotuloErroLogin.setText("Não foi possível conectar: " + ex.getMessage());
                    botaoConectar.setEnabled(true);
                });
            }
        }, "ConectarCliente").start();
    }

    private void atualizarFormulario() {
        int linha = tabelaCatalogo.getSelectedRow();
        if (linha < 0 || linha >= catalogoAtual.size()) {
            rotuloSelecionado.setText("Selecione um produto no catálogo");
            botaoPedir.setEnabled(false);
            return;
        }
        ClienteAcai.ItemCatalogo item = catalogoAtual.get(linha);
        rotuloSelecionado.setText(item.nome);
        boolean agua = semOpcionais(item);
        caixaBanana.setEnabled(!agua);
        caixaBis.setEnabled(!agua);
        if (agua) {
            caixaBanana.setSelected(false);
            caixaBis.setSelected(false);
        }
        botaoPedir.setEnabled(item.disponivel);
    }

    /** Agua Geladinha nao aceita opcionais (regra do servidor). */
    private boolean semOpcionais(ClienteAcai.ItemCatalogo item) {
        String n = item.nome.toLowerCase();
        return n.startsWith("agua") || n.startsWith("água");
    }

    private void fazerPedido() {
        int linha = tabelaCatalogo.getSelectedRow();
        if (sessao == null || linha < 0 || linha >= catalogoAtual.size()) return;
        ClienteAcai.ItemCatalogo item = catalogoAtual.get(linha);
        int quantidade = (Integer) campoQuantidade.getValue();
        String opcionais = "NENHUM";
        if (caixaBanana.isSelected() && caixaBis.isSelected()) opcionais = "BANANA+BIS";
        else if (caixaBanana.isSelected()) opcionais = "BANANA";
        else if (caixaBis.isSelected()) opcionais = "BIS";
        sessao.cliente.pedir(item.id, quantidade, opcionais);
        mensagem("Enviando pedido de " + quantidade + "x " + item.nome + "...", null);
    }

    private void consultarStatusSelecionado() {
        int linha = tabelaPedidos.getSelectedRow();
        if (sessao == null || linha < 0) return;
        int id = (Integer) modeloPedidos.getValueAt(linha, 0);
        sessao.cliente.consultarStatus(id);
    }

    private void encerrarSessao() {
        Sessao antiga = sessao;
        sessao = null;
        if (antiga != null) antiga.cliente.sair();
    }

    // ------------------------------------------------------------------
    // Atualizacao da interface (sempre na thread de eventos)
    // ------------------------------------------------------------------

    private void mostrarLogin(String erro) {
        naTelaPrincipal = false;
        modeloCatalogo.setRowCount(0);
        modeloPedidos.setRowCount(0);
        catalogoAtual = new ArrayList<>();
        statusAntecipado.clear();
        areaLog.setText("");
        rotuloErroLogin.setText(erro.isEmpty() ? " " : erro);
        botaoConectar.setEnabled(true);
        cartas.show(raiz, "login");
        getRootPane().setDefaultButton(botaoConectar);
    }

    private void mensagem(String texto, Color cor) {
        rotuloMensagem.setForeground(cor == null ? Color.DARK_GRAY : cor);
        rotuloMensagem.setText(texto);
    }

    private void aplicarCatalogo(List<ClienteAcai.ItemCatalogo> itens) {
        int idSelecionado = -1;
        int linha = tabelaCatalogo.getSelectedRow();
        if (linha >= 0 && linha < catalogoAtual.size()) {
            idSelecionado = Integer.parseInt(catalogoAtual.get(linha).id);
        }
        catalogoAtual = itens;
        modeloCatalogo.setRowCount(0);
        int novaLinha = -1;
        for (int i = 0; i < itens.size(); i++) {
            ClienteAcai.ItemCatalogo item = itens.get(i);
            modeloCatalogo.addRow(new Object[] {item.id, item.nome, formatarPreco(item.preco),
                item.disponivel ? "Disponível" : "Esgotado"});
            if (Integer.parseInt(item.id) == idSelecionado) novaLinha = i;
        }
        if (novaLinha >= 0) tabelaCatalogo.setRowSelectionInterval(novaLinha, novaLinha);
        atualizarFormulario();
    }

    private String formatarPreco(String preco) {
        return "R$ " + preco.replace('.', ',');
    }

    private String nomeProduto(String id) {
        for (ClienteAcai.ItemCatalogo item : catalogoAtual) {
            if (item.id.equals(id)) return item.nome;
        }
        return "Produto " + id;
    }

    private String formatarStatus(String status) {
        switch (status) {
            case "AGUARDANDO": return "Aguardando";
            case "EM_PREPARACAO": return "Em preparação";
            case "CONCLUIDO": return "Concluído";
            default: return status;
        }
    }

    private int linhaDoPedido(int id) {
        for (int i = 0; i < modeloPedidos.getRowCount(); i++) {
            if ((Integer) modeloPedidos.getValueAt(i, 0) == id) return i;
        }
        return -1;
    }

    private void atualizarStatusPedido(int id, String status, Integer previsao) {
        int linha = linhaDoPedido(id);
        if (linha < 0) {
            // EVENTO_STATUS chegou antes de PEDIDO_CRIADO ser processado.
            statusAntecipado.put(id, status);
            return;
        }
        modeloPedidos.setValueAt(formatarStatus(status), linha, 4);
        if (status.equals("CONCLUIDO")) modeloPedidos.setValueAt("—", linha, 5);
        else if (previsao != null) modeloPedidos.setValueAt(previsao + " s", linha, 5);
    }

    private void registrarLog(String direcao, String texto) {
        areaLog.append(direcao + " " + texto + "\n");
        if (areaLog.getDocument().getLength() > 200_000) {
            try {
                areaLog.getDocument().remove(0, 100_000);
            } catch (javax.swing.text.BadLocationException ignored) {
            }
        }
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    // ------------------------------------------------------------------
    // Ouvinte do protocolo: uma instancia por conexao
    // ------------------------------------------------------------------

    private final class Sessao implements ClienteAcai.Ouvinte {
        final ClienteAcai cliente = new ClienteAcai(this);
        final String enderecoTexto;

        Sessao(String enderecoTexto) {
            this.enderecoTexto = enderecoTexto;
        }

        /** Executa na thread do Swing, ignorando eventos de conexoes antigas. */
        private void naInterface(Runnable acao) {
            SwingUtilities.invokeLater(() -> {
                if (sessao == this) acao.run();
            });
        }

        @Override
        public void conectado(String nome) {
            naInterface(() -> {
                naTelaPrincipal = true;
                rotuloConexao.setText("Conectado como " + nome + "  •  " + enderecoTexto);
                mensagem("Conectado. Selecione um produto e faça seu pedido.", null);
                cartas.show(raiz, "principal");
                getRootPane().setDefaultButton(null);
                cliente.pedirCatalogo();
            });
        }

        @Override
        public void catalogo(List<ClienteAcai.ItemCatalogo> itens, boolean atualizacao) {
            naInterface(() -> {
                aplicarCatalogo(itens);
                if (atualizacao) mensagem("Catálogo atualizado pelo servidor.", null);
            });
        }

        @Override
        public void pedidoCriado(String comando, int id, String status, int previsao) {
            naInterface(() -> {
                String[] c = comando == null ? new String[0] : comando.split("\\|", -1);
                String produto = c.length >= 4 ? nomeProduto(c[1]) : "?";
                String qtd = c.length >= 4 ? c[2] : "?";
                String opcionais = c.length >= 4 ? c[3] : "?";
                String statusAtual = statusAntecipado.containsKey(id)
                        ? statusAntecipado.remove(id) : status;
                modeloPedidos.addRow(new Object[] {id, produto, qtd, opcionais,
                    formatarStatus(statusAtual),
                    statusAtual.equals("CONCLUIDO") ? "—" : previsao + " s"});
                mensagem("Pedido #" + id + " recebido. Previsão: " + previsao + " s.", VERDE);
            });
        }

        @Override
        public void statusPedido(int id, String status, int previsao) {
            naInterface(() -> {
                atualizarStatusPedido(id, status, previsao);
                mensagem("Pedido #" + id + ": " + formatarStatus(status) + ".", null);
            });
        }

        @Override
        public void eventoStatus(int id, String status) {
            naInterface(() -> {
                atualizarStatusPedido(id, status, null);
                mensagem("Pedido #" + id + " agora está: " + formatarStatus(status) + ".", VERDE);
            });
        }

        @Override
        public void erro(String comando, String codigo, String texto) {
            naInterface(() -> {
                if (!naTelaPrincipal) {
                    // Falha no CONECTAR (ex.: nome ja em uso): volta ao login.
                    sessao = null;
                    cliente.fechar();
                    mostrarLogin(texto + " (" + codigo + ")");
                    return;
                }
                String titulo = comando != null && comando.startsWith("PEDIR")
                        ? "Pedido recusado" : "Aviso do servidor";
                mensagem(titulo + ": " + texto, new Color(0xC62828));
                JOptionPane.showMessageDialog(AcaiClienteGUI.this, texto, titulo,
                        JOptionPane.WARNING_MESSAGE);
            });
        }

        @Override
        public void desconectado(String motivo) {
            naInterface(() -> {
                sessao = null;
                if (naTelaPrincipal) {
                    mostrarLogin("Desconectado: " + motivo);
                } else {
                    rotuloErroLogin.setText("Desconectado: " + motivo);
                    botaoConectar.setEnabled(true);
                }
            });
        }

        @Override
        public void linha(String direcao, String texto) {
            // O log mostra tambem o trafego de uma sessao que ja terminou de fechar.
            SwingUtilities.invokeLater(() -> registrarLog(direcao, texto));
        }
    }

    // ------------------------------------------------------------------

    public static void main(String[] args) {
        String servidor = args.length > 0 ? args[0] : "127.0.0.1";
        String porta = args.length > 1 ? args[1] : "5000";
        String nome = args.length > 2 ? args[2] : "";
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new AcaiClienteGUI(servidor, porta, nome).setVisible(true));
    }
}
