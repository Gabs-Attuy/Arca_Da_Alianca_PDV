package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import model.ProdutoModel;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.*;
import service.DatabaseMethodsService;

/**
 *
 * @author jeff_
 */
public class ProdutoDAOTest {
    
    private Connection mockConnection;
    private PreparedStatement mockStmt;
    private ResultSet mockRs;
    
    private ProdutoDAO produtoDAO;
    
    @BeforeEach
    void setUp() {
        mockConnection = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
        produtoDAO = new ProdutoDAO();
    }
    
    @Test
    void testFindByCodigoDeBarras() throws Exception {
        String codigo = "1234567890123";
        UUID uuid = UUID.randomUUID();
        
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getObject("uuid")).thenReturn(uuid);
        when(mockRs.getString("nome")).thenReturn("Livro Teste");
        when(mockRs.getBigDecimal("preco")).thenReturn(new BigDecimal("9.99"));
        when(mockRs.getInt("estoque")).thenReturn(5);
        when(mockRs.getString("categoria")).thenReturn("Livro");
        when(mockRs.getString("codigo_barras")).thenReturn(codigo);
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        
        try (MockedStatic<DatabaseMethodsService> mockedStatic = mockStatic(DatabaseMethodsService.class)) {
            mockedStatic.when(DatabaseMethodsService::getConnection).thenReturn(mockConnection);

            ProdutoModel produto = produtoDAO.findByCodigoBarras(codigo);

            assertNotNull(produto);
            assertEquals("Livro Teste", produto.getNome());
            assertEquals(codigo, produto.getCodigoBarras());
            assertEquals("Livro", produto.getCategoria());
            assertEquals(new BigDecimal("9.99"), produto.getPreco());
            assertEquals(5, produto.getEstoque());
            assertEquals(uuid, produto.getUuid());
        }
    }
    
    @Test
    void testSave() throws SQLException {
        ProdutoModel p = new ProdutoModel();
        UUID uuid = UUID.randomUUID();
        
        p.setNome("Livro Teste");
        p.setPreco(new BigDecimal("9.99"));
        p.setCategoria("Livro");
        p.setEstoque(5);
        p.setCodigoBarras("7894561237890");
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeUpdate()).thenReturn(1);
        when(mockStmt.getGeneratedKeys()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString(1)).thenReturn(uuid.toString());

        try (MockedStatic<DatabaseMethodsService> mockedStatic = mockStatic(DatabaseMethodsService.class)) {
            mockedStatic.when(DatabaseMethodsService::getConnection).thenReturn(mockConnection);

            produtoDAO.save(p);

            assertNotNull(p.getUuid());
            assertEquals(uuid, p.getUuid());

            verify(mockStmt).setString(1, "Livro Teste");
            verify(mockStmt).setString(2, "Livro");
            verify(mockStmt).setBigDecimal(3, new BigDecimal("9.99"));
            verify(mockStmt).setInt(4, 5);
            verify(mockStmt).setString(5, "7894561237890");

            verify(mockStmt).executeUpdate();
            verify(mockStmt).getGeneratedKeys();
            verify(mockRs).next();
            verify(mockRs).getString(1);
        }
        
    }
}
