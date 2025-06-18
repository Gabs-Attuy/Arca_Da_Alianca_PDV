package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.ProdutoModel;
import util.UtilsDB;

/**
 *
 * @author jeffe
 */
public class ProdutoDAO {
    
    public List<ProdutoModel> getAllProducts() {
        List<ProdutoModel> produtos = new ArrayList<>();
        
        try(Connection conn = UtilsDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tb_produto");
                ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ProdutoModel p = new ProdutoModel();
                p.setNome(rs.getString("nome"));
                p.setPreco(rs.getBigDecimal("preco"));
                p.setEstoque(rs.getInt("estoque"));
                p.setCategoria(rs.getString("categoria"));
                p.setCodigoBarras(rs.getString("codigo_barras"));
                
                produtos.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return produtos;
    }
    
    public boolean insertProduct(ProdutoModel p) {
        
        try(Connection conn = UtilsDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO tb_produto (nome, categoria, preco, estoque, codigo_barras)" 
                        + "VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)){
            
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setBigDecimal(3, p.getPreco());
            stmt.setInt(4, p.getEstoque());
            stmt.setString(5, p.getCodigoBarras());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }           
        return false;
    }
}
