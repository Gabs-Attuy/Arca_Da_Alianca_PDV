package dao;

import interfaces.AbstractDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import model.ProdutoModel;

/**
 *
 * @author jeffe
 * @author gabs
 */
public class ProdutoDAO extends AbstractDAO<ProdutoModel, UUID>{
    
    public ProdutoDAO() {
        super();
    }

    @Override
    protected ProdutoModel mapResultSetToEntity(ResultSet rs) throws SQLException {
        ProdutoModel p = new ProdutoModel();
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

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        } finally {
            closeConnection();
        }
        
    }

    @Override
    public void update(ProdutoModel p) {
        String sql = "UPDATE " + getTableName() + " SET nome = ?, categoria = ?, preco = ?, estoque = ?, codigo_barras = ? WHERE " + getIdColumn() + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        } finally {
            closeConnection();
        }
        
    }

}
