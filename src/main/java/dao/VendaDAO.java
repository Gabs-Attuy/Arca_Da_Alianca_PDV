/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import enums.FormaPagamento;
import interfaces.AbstractDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
        venda.setTotalVenda(rs.getBigDecimal("total_venda"));
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
    
    public void saveWithItems(VendaModel venda, List<ItemPedidoModel> itens) {
        String vendaSql = "INSERT INTO " + getTableName() + " (data_venda, total_venda, forma_pagamento) VALUES (?, ?, ?)";
        String itemSql = "INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = UtilsDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement vendaStmt = conn.prepareStatement(vendaSql);
                 PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {

                vendaStmt.setTimestamp(1, new Timestamp(venda.getDataVenda().getTime()));
                vendaStmt.setBigDecimal(2, venda.getTotalVenda());
                vendaStmt.setString(3, venda.getFormaPagamento().name());
                vendaStmt.executeUpdate();

                for (ItemPedidoModel item : itens) {
                    itemStmt.setObject(1, venda.getUuid());
                    itemStmt.setObject(2, item.getProdutoId());
                    itemStmt.setInt(3, item.getQuantidade());
                    itemStmt.addBatch();
                }

                itemStmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}