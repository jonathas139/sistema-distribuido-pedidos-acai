package multithreads2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Cliente grafico do sistema de pedidos de acai, derivado do TCPClientForm
 * (multithreads2) da Aula 6: o formulario cria o TCPClientMain ao conectar,
 * envia mensagens com writeMessage() e recebe as respostas da thread
 * TCPClientHandler por metodos como escreverMensagem().
 */
public class TCPClientForm extends javax.swing.JFrame {

    public TCPClientForm() {
        initComponents();
    }

    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Açaí do Nando - Pedidos");

        // jLabel5 e o banner da loja (texto, icone e cores em aplicarEstilo)

        jLabel1.setText("Server:");

        jTextField1.setText("127.0.0.1");

        jLabel2.setText("Porta:");

        jTextField2.setText("5000");

        jLabel6.setText("Nome:");

        jButton1.setText("Conectar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Desconectar");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Catálogo:");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[]{"Id", "Produto", "Preço", "Disponibilidade"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(50);
        jTable1.getColumnModel().getColumn(2).setMaxWidth(100);
        jTable1.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    atualizarFormulario();
                }
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel4.setText("Selecione um produto");

        jLabel7.setText("Quantidade:");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 99, 1));

        jCheckBox1.setText("Banana");

        jCheckBox2.setText("Bis");

        jButton3.setText("Fazer pedido");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Atualizar catálogo");
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel8.setText("Meus pedidos:");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
                new Object[]{"Pedido", "Produto", "Qtd", "Opcionais", "Status", "Previsão"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        jTable2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable2.getColumnModel().getColumn(0).setMaxWidth(70);
        jTable2.getColumnModel().getColumn(2).setMaxWidth(50);
        jScrollPane2.setViewportView(jTable2);

        jButton5.setText("Consultar status");
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel9.setText("Resposta:");

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4))
                    .addComponent(jLabel8)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                    .addComponent(jButton5)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(12, 12, 12)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addGap(12, 12, 12)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addGap(12, 12, 12)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap())
        );

        aplicarEstilo();
        pack();
    }

    // Conectar
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String server = jTextField1.getText().trim();
            int porta = Integer.parseInt(jTextField2.getText().trim());
            String nome = jTextField3.getText().trim();
            if (nome.equals("") || nome.contains("|")) {
                throw new Exception("Informe um nome sem o caractere |");
            }
            tcpClient = new TCPClientMain(server, porta, this);
            jButton1.setEnabled(false);
            jButton2.setEnabled(true);
            jTextField1.setEnabled(false);
            jTextField2.setEnabled(false);
            jTextField3.setEnabled(false);
            enviarComando("CONECTAR|" + nome);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "ERRO", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Desconectar
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        closeConnection();
    }

    // Fazer pedido
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        int linha = jTable1.getSelectedRow();
        if (tcpClient == null || linha < 0 || linha >= catalogoAtual.size()) {
            return;
        }
        String[] item = catalogoAtual.get(linha);
        int quantidade = (Integer) jSpinner1.getValue();
        String opcionais = "NENHUM";
        if (jCheckBox1.isSelected() && jCheckBox2.isSelected()) {
            opcionais = "BANANA+BIS";
        } else if (jCheckBox1.isSelected()) {
            opcionais = "BANANA";
        } else if (jCheckBox2.isSelected()) {
            opcionais = "BIS";
        }
        enviarComando("PEDIR|" + item[0] + "|" + quantidade + "|" + opcionais);
    }

    // Atualizar catalogo
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        if (tcpClient != null) {
            enviarComando("CATALOGO");
        }
    }

    // Consultar status do pedido selecionado
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        int linha = jTable2.getSelectedRow();
        if (tcpClient == null || linha < 0) {
            return;
        }
        enviarComando("STATUS|" + modeloPedidos().getValueAt(linha, 0));
    }

    /**
     * ADAPTACAO: o servidor responde aos comandos na ordem em que chegam.
     * Guardar os comandos enviados permite associar PEDIDO_CRIADO ou ERRO ao
     * comando que os originou.
     */
    private void enviarComando(String comando) {
        comandosEnviados.add(comando);
        tcpClient.writeMessage(comando);
    }

    private String proximoComando() {
        return comandosEnviados.poll();
    }

    private javax.swing.table.DefaultTableModel modeloCatalogo() {
        return (javax.swing.table.DefaultTableModel) jTable1.getModel();
    }

    private javax.swing.table.DefaultTableModel modeloPedidos() {
        return (javax.swing.table.DefaultTableModel) jTable2.getModel();
    }

    public void escreverMensagem(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jTextArea2.append(message + "\n");
            }
        });
    }

    public void closeConnection() {
        try {
            if (tcpClient != null) {
                tcpClient.closeConnection();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "ERRO", JOptionPane.ERROR_MESSAGE);
        }
        tcpClient = null;
        conectado = false;
        comandosEnviados.clear();
        catalogoAtual = new ArrayList<String[]>();
        modeloCatalogo().setRowCount(0);
        modeloPedidos().setRowCount(0);
        jTextArea2.setText("");
        jTextField1.setEnabled(true);
        jTextField2.setEnabled(true);
        jTextField3.setEnabled(true);
        jButton1.setEnabled(true);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
        jButton4.setEnabled(false);
        jButton5.setEnabled(false);
        jLabel4.setText("Selecione um produto");
    }

    // ------------------------------------------------------------------
    // Metodos chamados pelo TCPClientHandler (outra thread): todos repassam
    // o trabalho para a thread de eventos do Swing.
    // ------------------------------------------------------------------

    public void conexaoEncerrada() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (tcpClient == null) {
                    return;
                }
                closeConnection();
                JOptionPane.showMessageDialog(TCPClientForm.this,
                        "Conexão encerrada pelo servidor", "AVISO", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void conectado(final String nome) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (tcpClient == null) {
                    return;
                }
                proximoComando();
                conectado = true;
                setTitle("Açaí do Nando - Conectado como " + nome);
                jButton4.setEnabled(true);
                enviarComando("CATALOGO");
            }
        });
    }

    public void atualizarCatalogo(final List<String[]> itens, final boolean atualizacao) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (tcpClient == null) {
                    return;
                }
                // a resposta a CATALOGO consome o comando; a atualizacao assincrona nao
                if (!atualizacao) {
                    proximoComando();
                }
                String idSelecionado = null;
                int linha = jTable1.getSelectedRow();
                if (linha >= 0 && linha < catalogoAtual.size()) {
                    idSelecionado = catalogoAtual.get(linha)[0];
                }
                catalogoAtual = itens;
                modeloCatalogo().setRowCount(0);
                int novaLinha = -1;
                for (int i = 0; i < itens.size(); i++) {
                    String[] item = itens.get(i);
                    modeloCatalogo().addRow(new Object[]{item[0], item[1],
                        "R$ " + item[2].replace('.', ','),
                        item[3].equals("1") ? "Disponível" : "Esgotado"});
                    if (item[0].equals(idSelecionado)) {
                        novaLinha = i;
                    }
                }
                if (novaLinha >= 0) {
                    jTable1.setRowSelectionInterval(novaLinha, novaLinha);
                }
                atualizarFormulario();
            }
        });
    }

    public void pedidoCriado(final int id, final String status, final int previsao) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (tcpClient == null) {
                    return;
                }
                String comando = proximoComando();
                String produto = "?";
                String quantidade = "?";
                String opcionais = "?";
                if (comando != null) {
                    StringTokenizer tokens = new StringTokenizer(comando, "|");
                    if (tokens.countTokens() == 4) {
                        tokens.nextToken();
                        produto = nomeProduto(tokens.nextToken());
                        quantidade = tokens.nextToken();
                        opcionais = tokens.nextToken();
                    }
                }
                modeloPedidos().addRow(new Object[]{id, produto, quantidade, opcionais,
                    formatarStatus(status), previsao + " s"});
            }
        });
    }

    public void statusPedido(final int id, final String status, final int previsao) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (tcpClient == null) {
                    return;
                }
                proximoComando();
                atualizarStatusPedido(id, status, previsao + " s");
            }
        });
    }

    public void eventoStatus(final int id, final String status) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (tcpClient == null) {
                    return;
                }
                atualizarStatusPedido(id, status, null);
            }
        });
    }

    public void erro(final String codigo, final String texto) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (tcpClient == null) {
                    return;
                }
                proximoComando();
                if (!conectado) {
                    // falha no CONECTAR (ex.: nome ja em uso): volta ao estado inicial
                    closeConnection();
                }
                JOptionPane.showMessageDialog(TCPClientForm.this,
                        texto + " (" + codigo + ")", "ERRO", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // ------------------------------------------------------------------

    private void atualizarFormulario() {
        int linha = jTable1.getSelectedRow();
        if (linha < 0 || linha >= catalogoAtual.size()) {
            jLabel4.setText("Selecione um produto");
            jButton3.setEnabled(false);
            return;
        }
        String[] item = catalogoAtual.get(linha);
        jLabel4.setText(item[1]);
        // regra do servidor: Agua Geladinha nao aceita opcionais
        boolean agua = item[1].toLowerCase().startsWith("agua")
                || item[1].toLowerCase().startsWith("água");
        jCheckBox1.setEnabled(!agua);
        jCheckBox2.setEnabled(!agua);
        if (agua) {
            jCheckBox1.setSelected(false);
            jCheckBox2.setSelected(false);
        }
        jButton3.setEnabled(item[3].equals("1"));
    }

    private String nomeProduto(String id) {
        for (String[] item : catalogoAtual) {
            if (item[0].equals(id)) {
                return item[1];
            }
        }
        return "Produto " + id;
    }

    private String formatarStatus(String status) {
        if (status.equals("AGUARDANDO")) {
            return "Aguardando";
        } else if (status.equals("EM_PREPARACAO")) {
            return "Em preparação";
        } else if (status.equals("CONCLUIDO")) {
            return "Concluído";
        }
        return status;
    }

    private void atualizarStatusPedido(int id, String status, String previsao) {
        for (int i = 0; i < modeloPedidos().getRowCount(); i++) {
            if (((Integer) modeloPedidos().getValueAt(i, 0)) == id) {
                modeloPedidos().setValueAt(formatarStatus(status), i, 4);
                if (status.equals("CONCLUIDO")) {
                    modeloPedidos().setValueAt("-", i, 5);
                } else if (previsao != null) {
                    modeloPedidos().setValueAt(previsao, i, 5);
                }
                return;
            }
        }
    }

    // ------------------------------------------------------------------
    // Visual de loja de acai (somente aparencia, sem regra de negocio)
    // ------------------------------------------------------------------

    private static final Color ROXO_ESCURO = new Color(0x4A148C);
    private static final Color ROXO = new Color(0x7B1FA2);
    private static final Color LILAS = new Color(0xB39DDB);
    private static final Color SELECAO = new Color(0xE1BEE7);
    private static final Color FUNDO = new Color(0xFBF7FD);
    private static final Color LISTRA = new Color(0xF5EDF9);
    private static final Color TEXTO = new Color(0x2B1B3A);
    private static final Color VERDE = new Color(0x2E7D32);
    private static final Color LARANJA = new Color(0xE65100);
    private static final Color VERMELHO = new Color(0xC62828);
    private static final Color BERRY = new Color(0xAD1457);
    private static final Color DESABILITADO = new Color(0xD3C9DC);

    /** Chamado antes de criar a janela (como o Look and Feel nos exemplos do professor). */
    static void configurarTema() {
        // fontes normais (sem negrito em tudo) no tema padrao Metal
        javax.swing.UIManager.put("swing.boldMetal", Boolean.FALSE);
    }

    private void aplicarEstilo() {
        getContentPane().setBackground(FUNDO);
        setIconImage(imagemIcone());

        // banner da loja
        jLabel5.setIcon(new IconeAcai(64));
        jLabel5.setIconTextGap(16);
        jLabel5.setOpaque(true);
        jLabel5.setBackground(ROXO_ESCURO);
        jLabel5.setForeground(Color.WHITE);
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        jLabel5.setText("<html><span style='font-size:22pt'><b>Açaí do Nando</b></span><br>"
                + "<span style='font-size:10pt;color:#E1BEE7'>Faça seu pedido e acompanhe o preparo em tempo real</span></html>");

        // rotulos
        JLabel[] comuns = {jLabel1, jLabel2, jLabel6, jLabel7};
        for (JLabel rotulo : comuns) {
            rotulo.setForeground(TEXTO);
        }
        JLabel[] secoes = {jLabel3, jLabel8, jLabel9};
        for (JLabel rotulo : secoes) {
            rotulo.setForeground(ROXO);
            rotulo.setFont(rotulo.getFont().deriveFont(Font.BOLD, 14f));
        }
        jLabel4.setForeground(ROXO_ESCURO);
        jLabel4.setFont(jLabel4.getFont().deriveFont(Font.BOLD, 13f));

        // campos
        JTextField[] campos = {jTextField1, jTextField2, jTextField3};
        for (JTextField campo : campos) {
            campo.setForeground(TEXTO);
            campo.setDisabledTextColor(new Color(0x6B5B7B));
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LILAS),
                    BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        }
        jCheckBox1.setOpaque(false);
        jCheckBox2.setOpaque(false);
        jCheckBox1.setForeground(TEXTO);
        jCheckBox2.setForeground(TEXTO);

        // botoes
        estilizarBotao(jButton1, VERDE);
        estilizarBotao(jButton2, BERRY);
        estilizarBotao(jButton3, VERDE);
        estilizarBotao(jButton4, ROXO);
        estilizarBotao(jButton5, ROXO);

        // tabelas e area de resposta
        estilizarTabela(jTable1);
        estilizarTabela(jTable2);
        jTextArea2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        jTextArea2.setForeground(TEXTO);
        jTextArea2.setMargin(new java.awt.Insets(4, 6, 4, 6));
        javax.swing.JScrollPane[] paineis = {jScrollPane1, jScrollPane2, jScrollPane3};
        for (javax.swing.JScrollPane painel : paineis) {
            painel.setBorder(BorderFactory.createLineBorder(LILAS));
            painel.getViewport().setBackground(Color.WHITE);
        }
    }

    private void estilizarBotao(final JButton botao, final Color cor) {
        botao.setForeground(Color.WHITE);
        botao.setFont(botao.getFont().deriveFont(Font.BOLD));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        botao.setBackground(botao.isEnabled() ? cor : DESABILITADO);
        // botao desabilitado fica cinza-lilas; habilitado volta a cor da acao
        botao.addPropertyChangeListener("enabled", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                botao.setBackground(botao.isEnabled() ? cor : DESABILITADO);
            }
        });
    }

    private void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(26);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(SELECAO);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionBackground(SELECAO);
        tabela.setSelectionForeground(ROXO_ESCURO);
        tabela.setDefaultRenderer(Object.class, new RenderizadorTabela());
        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setBackground(ROXO);
        cabecalho.setForeground(Color.WHITE);
        cabecalho.setFont(cabecalho.getFont().deriveFont(Font.BOLD));
        cabecalho.setReorderingAllowed(false);
    }

    /** Linhas listradas e cores para disponibilidade e status dos pedidos. */
    private static class RenderizadorTabela extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionada, boolean foco, int linha, int coluna) {
            Component c = super.getTableCellRendererComponent(tabela, valor,
                    selecionada, false, linha, coluna);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            Font normal = tabela.getFont().deriveFont(Font.PLAIN);
            c.setFont(normal);
            if (!selecionada) {
                c.setBackground(linha % 2 == 0 ? Color.WHITE : LISTRA);
                c.setForeground(TEXTO);
                String texto = String.valueOf(valor);
                Color destaque = null;
                if (texto.equals("Disponível") || texto.equals("Concluído")) {
                    destaque = VERDE;
                } else if (texto.equals("Esgotado")) {
                    destaque = VERMELHO;
                } else if (texto.equals("Em preparação")) {
                    destaque = LARANJA;
                } else if (texto.equals("Aguardando")) {
                    destaque = ROXO;
                }
                if (destaque != null) {
                    c.setForeground(destaque);
                    c.setFont(normal.deriveFont(Font.BOLD));
                }
            }
            return c;
        }
    }

    private BufferedImage imagemIcone() {
        BufferedImage imagem = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        new IconeAcai(48).paintIcon(null, g, 0, 0);
        g.dispose();
        return imagem;
    }

    /** Tigela de acai desenhada com Java2D: acai, rodelas de banana e granola. */
    private static class IconeAcai implements Icon {

        private final int tamanho;

        IconeAcai(int tamanho) {
            this.tamanho = tamanho;
        }

        public int getIconWidth() {
            return tamanho;
        }

        public int getIconHeight() {
            return tamanho;
        }

        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(x, y);
            g.scale(tamanho / 64.0, tamanho / 64.0);
            // acai (cupula) e brilho
            g.setColor(new Color(0x9C27B0));
            g.fillArc(10, 14, 44, 56, 0, 180);
            g.setColor(new Color(0xBA68C8));
            g.fillOval(20, 22, 10, 6);
            // rodelas de banana
            g.setColor(new Color(0xFFD54F));
            g.fillOval(16, 24, 11, 9);
            g.fillOval(28, 17, 11, 9);
            g.fillOval(39, 25, 11, 9);
            g.setColor(new Color(0xFFB300));
            g.fillOval(20, 27, 3, 3);
            g.fillOval(32, 20, 3, 3);
            g.fillOval(43, 28, 3, 3);
            // granola
            g.setColor(new Color(0xD7A86E));
            g.fillOval(26, 33, 4, 3);
            g.fillOval(35, 32, 4, 3);
            g.fillOval(14, 33, 3, 3);
            // folha de hortela
            g.setColor(new Color(0x66BB6A));
            g.fillOval(44, 12, 11, 6);
            // tigela
            g.setColor(new Color(0xFFF3E0));
            g.fillArc(6, 22, 52, 40, 180, 180);
            g.setColor(new Color(0xFFE0B2));
            g.fillRoundRect(4, 40, 56, 5, 5, 5);
            g.dispose();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        configurarTema();
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TCPClientForm().setVisible(true);
            }
        });
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private TCPClientMain tcpClient;
    // estado (acessado somente na thread de eventos do Swing)
    private boolean conectado = false;
    private List<String[]> catalogoAtual = new ArrayList<String[]>();
    private final LinkedList<String> comandosEnviados = new LinkedList<String>();
}
