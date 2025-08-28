package template;

import dao.ProdutoDAO;
import dao.VendaDAO;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.ItemPedidoModel;
import model.ProdutoModel;
import model.VendaModel;
import service.SnsService;
import service.ValidatorService;
/**
 *
 * @author jeff_
 * @author gabs
 */
public class InternalFrameVenda extends javax.swing.JInternalFrame {

    private JDesktopPane desktop;
    private ProdutoModel produtoAtual;

    /**
     * Creates new form InternalFrameVenda
     * @param desktop
     */
    public InternalFrameVenda(JDesktopPane desktop) {
        initComponents();
        this.desktop = desktop;
        
        lblTrocoTitle.setVisible(false);
        txtTroco.setVisible(false);
        btnCalcTroco.setVisible(false);
        lblTroco.setVisible(false);
        
        ValidatorService validator = new ValidatorService();
        
        txtBarCodeSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                validator.barCodeSearchValidate(evt);
            }
        });
        
        txtBarCodeSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String codigoBarras = txtBarCodeSearch.getText().trim();
                        
                if (codigoBarras.length() == 13) {
                    ProdutoDAO dao = new ProdutoDAO();
                    produtoAtual = dao.findByCodigoBarras(codigoBarras);
                    
                    if (produtoAtual != null) {
                        txtNomeProduto.setText(produtoAtual.getNome());
                        txtNomeProduto.setForeground(Color.BLACK);
                        txtEstoqueAtual.setText(String.valueOf(produtoAtual.getEstoque()));
                    }
                }
            }
        });
        setTableDesign(tblVendas);
        
        rbPix.addActionListener(evt -> atualizarFormaPagamento());
        rbCredito.addActionListener(evt -> atualizarFormaPagamento());
        rbDebito.addActionListener(evt -> atualizarFormaPagamento());
        rbDinheiro.addActionListener(evt -> atualizarFormaPagamento());
        
        btnCalcTroco.addActionListener(e -> {
            try {
                String valorRecebidoStr = txtTroco.getText().trim().replace(",", ".");
                BigDecimal valorRecebido = new BigDecimal(valorRecebidoStr);

                String totalVendaStr = lblTotalVenda.getText().replace("TOTAL: R$", "").trim().replace(",", ".");
                BigDecimal totalVenda = new BigDecimal(totalVendaStr);

                BigDecimal troco = valorRecebido.subtract(totalVenda);

                if (troco.compareTo(BigDecimal.ZERO) < 0) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Valor insuficiente para a compra!", 
                            "Valor insuficiente",
                            JOptionPane.INFORMATION_MESSAGE);
                    lblTroco.setText("TROCO: R$0.00");
                    return;
                }
                
                lblTroco.setText("TROCO: R$ " + troco.setScale(2, RoundingMode.HALF_UP));

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "O valor de troco inserido é inválido!",
                            "Erro no troco",
                    JOptionPane.INFORMATION_MESSAGE);
                }
        });
        
        btnFinalizarVenda.addActionListener(e -> finalizarVenda());
    }
        
    private String getSelectedButtonText(ButtonGroup group) {
        for (java.util.Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }
    
    private void atualizarFormaPagamento() {
        String formaPagamento = getSelectedButtonText(buttonGroup1);
        lblFormaPagamento.setText("PAGAMENTO: " + (formaPagamento != null ? formaPagamento.toUpperCase() : "À DEFINIR"));
        
        boolean isDinheiro = rbDinheiro.isSelected();
        lblTrocoTitle.setVisible(isDinheiro);
        txtTroco.setVisible(isDinheiro);
        btnCalcTroco.setVisible(isDinheiro);
        lblTroco.setVisible(isDinheiro);
    }
    
    public void setTableDesign(JTable table) {
        
        ((javax.swing.table.DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(13, 45, 89));
        table.getTableHeader().setForeground(Color.BLACK);
    }
    
    private void finalizarVenda() {
        DefaultTableModel model = (DefaultTableModel) tblVendas.getModel();

        if (model.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Adicione itens à venda antes de finalizar.",
                    "Erro ao finalizar venda",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String formaSelecionada = getSelectedButtonText(buttonGroup1);
        if (formaSelecionada == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecione uma forma de pagamento.",
                    "Erro ao finalizar a venda",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        try {
            VendaModel venda = new VendaModel();
            venda.setDataVenda(new Date());
            venda.setTotalVenda(parseTotalVenda());
            venda.setFormaPagamento(enums.FormaPagamento.valueOf(formaSelecionada.toUpperCase().replace("É", "E")));

            java.util.List<ItemPedidoModel> itens = new java.util.ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                String codigo = (String) model.getValueAt(i, 0);
                int qtd = ((BigDecimal) model.getValueAt(i, 3)).intValue();

                ProdutoDAO pdao = new ProdutoDAO();
                ProdutoModel produto = pdao.findByCodigoBarras(codigo);

                ItemPedidoModel item = new ItemPedidoModel(null, produto.getUuid(), qtd);
                itens.add(item);
            }

            VendaDAO vdao = new VendaDAO();
            boolean sucesso = vdao.saveWithItems(venda, itens);

            if (sucesso) {
                javax.swing.JOptionPane.showMessageDialog(this, "Venda finalizada com sucesso!", 
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);

                try {
                    SnsService snsService = new SnsService();
                    java.text.SimpleDateFormat formatoData = new java.text.SimpleDateFormat("EEEE, dd/MM/yyyy 'às' HH:mm", new Locale("pt", "BR"));
                    String dataFormatada = formatoData.format(venda.getDataVenda());

                    String msg = String.format(
                        "Nova venda realizada!\nValor: R$ %.2f\nData: %s",
                        venda.getTotalVenda(),
                        dataFormatada
                    );

                    snsService.enviarMensagem(msg);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                model.setRowCount(0);
                lblTotalVenda.setText("TOTAL: R$ 0.00");
                lblFormaPagamento.setText("PAGAMENTO: À DEFINIR");
                lblTroco.setText("TROCO: R$ 0.00");
                txtTroco.setText("");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Erro ao finalizar venda.", 
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Erro ao finalizar venda.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private BigDecimal parseTotalVenda() {
        String totalStr = lblTotalVenda.getText().replace("TOTAL: R$", "").trim().replace(",", ".");
        return new BigDecimal(totalStr);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton5 = new javax.swing.JRadioButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtBarCodeSearch = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVendas = new javax.swing.JTable();
        txtNomeProduto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtEstoqueAtual = new javax.swing.JTextField();
        txtQtdVenda = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnAddItemPedido = new javax.swing.JButton();
        lblTotalVenda = new javax.swing.JLabel();
        lblFormaPagamento = new javax.swing.JLabel();
        rbPix = new javax.swing.JRadioButton();
        rbCredito = new javax.swing.JRadioButton();
        rbDebito = new javax.swing.JRadioButton();
        rbDinheiro = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        btnDeleteItem = new javax.swing.JButton();
        txtTroco = new javax.swing.JTextField();
        lblTrocoTitle = new javax.swing.JLabel();
        lblTroco = new javax.swing.JLabel();
        btnCalcTroco = new javax.swing.JButton();
        btnFinalizarVenda = new javax.swing.JButton();

        jRadioButton5.setText("jRadioButton5");

        setBackground(new java.awt.Color(255, 255, 255));
        setFrameIcon(null);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(13, 45, 89));
        jLabel1.setText("Finalizar Venda");

        jLabel2.setText("Insira o código de barras do produto");

        txtBarCodeSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtBarCodeSearch.setToolTipText("");
        txtBarCodeSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(13, 45, 89), 2));
        txtBarCodeSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBarCodeSearchActionPerformed(evt);
            }
        });

        tblVendas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblVendas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Produto", "Preço unitário", "Quantidade", "Preço total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblVendas.setCellSelectionEnabled(true);
        tblVendas.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblVendas.setGridColor(new java.awt.Color(204, 204, 204));
        tblVendas.setSelectionBackground(new java.awt.Color(13, 45, 89));
        tblVendas.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblVendas.getTableHeader().setResizingAllowed(false);
        jScrollPane2.setViewportView(tblVendas);

        txtNomeProduto.setEditable(false);
        txtNomeProduto.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        txtNomeProduto.setToolTipText("");
        txtNomeProduto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(13, 45, 89), 2));
        txtNomeProduto.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtNomeProduto.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtNomeProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeProdutoActionPerformed(evt);
            }
        });

        jLabel3.setText("Produto");

        jLabel4.setText("Estoque atual");

        jLabel5.setText("Qtd. da venda");

        txtEstoqueAtual.setEditable(false);
        txtEstoqueAtual.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        txtEstoqueAtual.setToolTipText("");
        txtEstoqueAtual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(13, 45, 89), 2));
        txtEstoqueAtual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstoqueAtualActionPerformed(evt);
            }
        });

        txtQtdVenda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtQtdVenda.setToolTipText("");
        txtQtdVenda.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(13, 45, 89), 2));
        txtQtdVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQtdVendaActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(13, 45, 89));
        jLabel6.setText("Itens da venda");

        btnAddItemPedido.setBackground(new java.awt.Color(13, 45, 89));
        btnAddItemPedido.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddItemPedido.setForeground(new java.awt.Color(255, 255, 255));
        btnAddItemPedido.setText("Adicionar à venda ");
        btnAddItemPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemPedidoActionPerformed(evt);
            }
        });

        lblTotalVenda.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTotalVenda.setForeground(new java.awt.Color(13, 45, 89));
        lblTotalVenda.setText("TOTAL: R$ 0.00");

        lblFormaPagamento.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblFormaPagamento.setForeground(new java.awt.Color(13, 45, 89));
        lblFormaPagamento.setText("PAGAMENTO: À DEFINIR");

        rbPix.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbPix);
        rbPix.setText("PIX");
        rbPix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPixActionPerformed(evt);
            }
        });

        rbCredito.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbCredito);
        rbCredito.setText("CRÉDITO");

        rbDebito.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbDebito);
        rbDebito.setText("DÉBITO");

        rbDinheiro.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbDinheiro);
        rbDinheiro.setText("DINHEIRO");
        rbDinheiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDinheiroActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(13, 45, 89));
        jLabel7.setText("Forma de pagamento");

        btnDeleteItem.setBackground(new java.awt.Color(13, 45, 89));
        btnDeleteItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDeleteItem.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteItem.setText("Delete");
        btnDeleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteItemActionPerformed(evt);
            }
        });

        txtTroco.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTroco.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(13, 45, 89), 2));

        lblTrocoTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTrocoTitle.setForeground(new java.awt.Color(13, 45, 89));
        lblTrocoTitle.setText("Cálculo de troco");

        lblTroco.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTroco.setForeground(new java.awt.Color(13, 45, 89));
        lblTroco.setText("TROCO: R$ 0.00");

        btnCalcTroco.setBackground(new java.awt.Color(13, 45, 89));
        btnCalcTroco.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCalcTroco.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcTroco.setText("Calcular");

        btnFinalizarVenda.setBackground(new java.awt.Color(13, 45, 89));
        btnFinalizarVenda.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnFinalizarVenda.setForeground(new java.awt.Color(255, 255, 255));
        btnFinalizarVenda.setText("Finalizar");
        btnFinalizarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtBarCodeSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
                                .addGap(37, 37, 37)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(189, 189, 189))
                                    .addComponent(txtNomeProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEstoqueAtual, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(txtQtdVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnAddItemPedido)
                                    .addGap(616, 616, 616))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnDeleteItem))
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 773, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(97, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnFinalizarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(rbPix)
                                        .addGap(12, 12, 12)
                                        .addComponent(rbCredito)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(rbDebito)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(rbDinheiro))
                                    .addComponent(jLabel7)
                                    .addComponent(lblTrocoTitle)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnCalcTroco)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblFormaPagamento)
                                    .addComponent(lblTotalVenda)
                                    .addComponent(lblTroco))))
                        .addGap(97, 97, 97))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBarCodeSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(22, 22, 22)
                                    .addComponent(txtNomeProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEstoqueAtual, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtQtdVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddItemPedido)
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(btnDeleteItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotalVenda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFormaPagamento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTroco)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnFinalizarVenda))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbPix)
                            .addComponent(rbCredito)
                            .addComponent(rbDebito)
                            .addComponent(rbDinheiro))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTrocoTitle)
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCalcTroco))))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBarCodeSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBarCodeSearchActionPerformed
    }//GEN-LAST:event_txtBarCodeSearchActionPerformed

    private void txtNomeProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeProdutoActionPerformed
    }//GEN-LAST:event_txtNomeProdutoActionPerformed

    private void txtEstoqueAtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstoqueAtualActionPerformed
    }//GEN-LAST:event_txtEstoqueAtualActionPerformed

    private void txtQtdVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtdVendaActionPerformed
    }//GEN-LAST:event_txtQtdVendaActionPerformed

    private void btnAddItemPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemPedidoActionPerformed
        
        if (produtoAtual == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nenhum produto selecionado!");
            return;
        }
        
        try {
            int qtdItem = Integer.parseInt(txtQtdVenda.getText());
            
            if (qtdItem <= 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Quantidade não pode ser 0!");
                return;
            }
            
            if (qtdItem > produtoAtual.getEstoque()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Quantidade excede o estoque disponível!");
                return;
            }
            
            BigDecimal precoUnit = produtoAtual.getPreco();
            BigDecimal qtd = new BigDecimal(qtdItem);
            BigDecimal precoTotal = precoUnit.multiply(qtd);
            
            DefaultTableModel model = (DefaultTableModel) tblVendas.getModel();

            model.addRow(new Object[] {
                produtoAtual.getCodigoBarras(),
                produtoAtual.getNome(),
                "R$ " + String.format("%.2f", precoUnit),
                qtd,
                "R$ " + String.format("%.2f", precoTotal),
            });
            
            atualizarTotalVenda();
            
             // Limpa campos
            txtBarCodeSearch.setText("");
            txtNomeProduto.setText("");
            txtEstoqueAtual.setText("");
            txtQtdVenda.setText("");
            produtoAtual = null;
            tblVendas.clearSelection();
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Quantidade inválida.");
        }
    }//GEN-LAST:event_btnAddItemPedidoActionPerformed

    private void atualizarTotalVenda() {
        DefaultTableModel model = (DefaultTableModel) tblVendas.getModel();
        BigDecimal total = BigDecimal.ZERO;
        for(int i = 0; i < model.getRowCount(); i++) {
            Object precoTotalObj = model.getValueAt(i, 4);
            
            if (precoTotalObj instanceof String) {
                String valorStr = ((String) precoTotalObj).replace("R$", "").trim().replace(",", ".");
                try {
                    BigDecimal valor = new BigDecimal(valorStr);
                    total = total.add(valor);
                } catch (NumberFormatException e) {
                    
                }
            }
        }
        
        lblTotalVenda.setText("TOTAL: R$ " + total.setScale(2, RoundingMode.HALF_UP));
    }
    
    private void rbPixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPixActionPerformed
    }//GEN-LAST:event_rbPixActionPerformed

    private void btnDeleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteItemActionPerformed
        int selectedRow = tblVendas.getSelectedRow();
        
        if(selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecione um item para remover!");
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) tblVendas.getModel();
        model.removeRow(selectedRow);
        
        atualizarTotalVenda();
    }//GEN-LAST:event_btnDeleteItemActionPerformed

    private void rbDinheiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDinheiroActionPerformed
    }//GEN-LAST:event_rbDinheiroActionPerformed

    private void btnFinalizarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarVendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFinalizarVendaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItemPedido;
    private javax.swing.JButton btnCalcTroco;
    private javax.swing.JButton btnDeleteItem;
    private javax.swing.JButton btnFinalizarVenda;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblFormaPagamento;
    private javax.swing.JLabel lblTotalVenda;
    private javax.swing.JLabel lblTroco;
    private javax.swing.JLabel lblTrocoTitle;
    private javax.swing.JRadioButton rbCredito;
    private javax.swing.JRadioButton rbDebito;
    private javax.swing.JRadioButton rbDinheiro;
    private javax.swing.JRadioButton rbPix;
    private javax.swing.JTable tblVendas;
    private javax.swing.JTextField txtBarCodeSearch;
    private javax.swing.JTextField txtEstoqueAtual;
    private javax.swing.JTextField txtNomeProduto;
    private javax.swing.JTextField txtQtdVenda;
    private javax.swing.JTextField txtTroco;
    // End of variables declaration//GEN-END:variables
}
