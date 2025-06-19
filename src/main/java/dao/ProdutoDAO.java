package dao;

import interfaces.AbstractDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import model.ProdutoModel;
import util.UtilsDB;

/**
 *
 * @author jeffe
 * @author gabs
 */
public class ProdutoDAO extends AbstractDAO<ProdutoModel, UUID>{
    
    public ProdutoDAO() {}

    @Override
    protected ProdutoModel mapResultSetToEntity(ResultSet rs) throws SQLException {
        ProdutoModel p = new ProdutoModel();
        p.setUuid((UUID) rs.getObject("uuid"));
        p.setNome(rs.getString("nome"));
        p.setPreco(rs.getBigDecimal("preco"));
        p.setEstoque(rs.getInt("estoque"));
        p.setCategoria(rs.getString("categoria"));
        p.setCodigoBarras(rs.getString("codigo_barras"));
        
        return p;
    }

    @Override
    protected String getTableName() {
        return "tb_produto";
    }

    @Override
    protected String getIdColumn() {
        return "uuid";
    }

    @Override
    public void save(ProdutoModel p) {
        String sql = "INSERT INTO " + getTableName() + " (nome, categoria, preco, estoque, codigo_barras) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = UtilsDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setBigDecimal(3, p.getPreco());
            stmt.setInt(4, p.getEstoque());
            stmt.setString(5, p.getCodigoBarras());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir o produto. Nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    UUID idGerado = UUID.fromString(generatedKeys.getString(1));
                    p.setUuid(idGerado);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }

    @Override
    public void update(ProdutoModel p) {
        String sql = "UPDATE " + getTableName() + " SET nome = ?, categoria = ?, preco = ?, estoque = ?, codigo_barras = ? WHERE " + getIdColumn() + " = ?";

        try (Connection connection = UtilsDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setBigDecimal(3, p.getPreco());
            stmt.setInt(4, p.getEstoque());
            stmt.setString(5, p.getCodigoBarras());
            stmt.setObject(6, p.getUuid());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Produto não encontrado para atualização.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    public ProdutoModel findByCodigoBarras(String codigo) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE codigo_barras = ?";
        try (Connection conn = UtilsDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }

}
