/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import enums.FormaPagamento;
import interfaces.AbstractDAO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.ItemPedidoModel;
import model.VendaModel;
import util.UtilsDB;

/**
 *
 * @author gabs
 */
public class VendaDAO extends AbstractDAO<VendaModel, UUID> {

    @Override
    protected VendaModel mapResultSetToEntity(ResultSet rs) throws SQLException {
        VendaModel venda = new VendaModel();
        venda.setUuid((UUID) rs.getObject("uuid"));
        venda.setDataVenda(rs.getTimestamp("data_venda"));
        venda.setTotalVenda(rs.getBigDecimal("valor_total"));
        venda.setFormaPagamento(FormaPagamento.valueOf(rs.getString("forma_pagamento")));
        
        return venda;
    }

    @Override
    protected String getTableName() {
        return "tb_venda";
    }

    @Override
    protected String getIdColumn() {
        return "uuid";
    }

    @Override
    public void save(VendaModel entity) {
        String sql = "INSERT INTO " + getTableName() + " (data_venda, total_venda, forma_pagamento) VALUES (?, ?, ?)";

        try (Connection conn = UtilsDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(entity.getDataVenda().getTime()));
            stmt.setBigDecimal(2, entity.getTotalVenda());
            stmt.setString(3, entity.getFormaPagamento().name());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Erro ao salvar venda.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(VendaModel entity) {
        throw new UnsupportedOperationException("Não é possível atualizar uma venda.");
    }
    
    public BigDecimal sumVendas() {
        String sql = "SELECT SUM(valor_total) FROM tb_venda WHERE data_venda >= CURRENT_DATE - INTERVAL '30 days'";
    
        try (Connection conn = UtilsDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    public int countVendas() {
        String sql = "SELECT COUNT(*) FROM tb_venda WHERE data_venda >= CURRENT_DATE - INTERVAL '30 days'";
        
        try (Connection conn = UtilsDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<VendaModel> listVendasDesc() {
        String sql = "SELECT * FROM tb_venda ORDER BY data_venda DESC" ;
        
        List<VendaModel> vendas = new ArrayList<>();
        
        try (Connection conn = UtilsDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                VendaModel venda = mapResultSetToEntity(rs);
                vendas.add(venda);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vendas;
    }
    
    
    public void saveItems(UUID vendaId, List<ItemPedidoModel> itens) {
        String sql = "INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = UtilsDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (ItemPedidoModel item : itens) {
                stmt.setObject(1, vendaId);
                stmt.setObject(2, item.getProdutoId());
                stmt.setInt(3, item.getQuantidade());
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean saveWithItems(VendaModel venda, List<ItemPedidoModel> itens) {
        String sqlVenda = "INSERT INTO " + getTableName() + " (data_venda, valor_total, forma_pagamento) VALUES (?, ?, ?::forma_pagamento_enum) RETURNING uuid";
        String sqlItem = "INSERT INTO item_pedido (venda_id, produto_id, quantidade) VALUES (?, ?, ?)";
        String sqlUpdateEstoque = "UPDATE tb_produto SET estoque = estoque - ? WHERE uuid = ?";

        try (Connection conn = UtilsDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda);
                 PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
                 PreparedStatement stmtEstoque = conn.prepareStatement(sqlUpdateEstoque)) {
                
                UUID uuidVenda = null;
                
                stmtVenda.setTimestamp(1, new java.sql.Timestamp(venda.getDataVenda().getTime()));
                stmtVenda.setBigDecimal(2, venda.getTotalVenda());
                stmtVenda.setObject(3, venda.getFormaPagamento().name(), java.sql.Types.OTHER);
                ResultSet rs = stmtVenda.executeQuery();
                if (rs.next()) {
                    uuidVenda = (UUID) rs.getObject("uuid");
                }
                
                if(uuidVenda != null) {
                    for (ItemPedidoModel item : itens) {
                        
                        stmtItem.setObject(1, uuidVenda);
                        stmtItem.setObject(2, item.getProdutoId());
                        stmtItem.setInt(3, item.getQuantidade());
                        stmtItem.executeUpdate();

                        stmtEstoque.setInt(1, item.getQuantidade());
                        stmtEstoque.setObject(2, item.getProdutoId());
                        stmtEstoque.executeUpdate();
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
}