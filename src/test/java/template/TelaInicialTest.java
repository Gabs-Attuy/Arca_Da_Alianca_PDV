package template;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JComponent;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe de testes unitários para a tela inicial da aplicação.
 * Verifica se a interface gráfica {@link TelaInicial} é criada corretamente
 * e se os componentes (botões) estão visíveis e habilitados para interação.
 * @author jeff_
 */
public class TelaInicialTest {
    
    private TelaInicial telaInicial;
    
    @BeforeEach
    void setUp() {
        telaInicial = new TelaInicial();
    }
    
    /**
     * Verifica se a tela inicial foi criada corretamente e está pronta para exibição
     */
    @Test
    void testTelaInicialIsCreated() {
        assertNotNull(telaInicial);
        assertTrue(telaInicial.isDisplayable());
    }
    
    /**
     * Testa se os botões principais da interface estão visíveis e habilitados (clicáveis).
     * Os botões testados são:
     * <ul>
     *   <li><b>btnProdutos</b> — botão para acessar a seção de produtos</li>
     *   <li><b>btnVendas</b> — botão para registrar vendas</li>
     *   <li><b>btnRelatorioEstoque</b> — botão para gerar relatório de estoque</li>
     *   <li><b>btnRelatorioVendas</b> — botão para gerar relatório de vendas</li>
     * </ul>
     */
    @Test
    void testComponentsIsVisible() {
        assertAll(
            () -> {
                JButton btnProdutos = (JButton) getComponentByName(telaInicial, "btnProdutos");
                assertNotNull(btnProdutos, "btnProdutos não foi encontrado!");
                assertTrue(btnProdutos.isVisible(), "btnProdutos não está visível!");
                assertTrue(btnProdutos.isEnabled(), "btnProdutos não está habilitado!");
            },
            () -> {
                JButton btnVendas = (JButton) getComponentByName(telaInicial, "btnVendas");
                assertNotNull(btnVendas, "btnVendas não foi encontrado!");
                assertTrue(btnVendas.isVisible(), "btnVendas não está visível!");
                assertTrue(btnVendas.isEnabled(), "btnVendas não está habilitado!");
            },
            () -> {
                JButton btnRelatorioEstoque = (JButton) getComponentByName(telaInicial, "btnRelatorioEstoque");
                assertNotNull(btnRelatorioEstoque, "btnRelatorioEstoque não foi encontrado!");
                assertTrue(btnRelatorioEstoque.isVisible(), "btnRelatorioEstoque não está visível!");
                assertTrue(btnRelatorioEstoque.isEnabled(), "btnRelatorioEstoque não está habilitado!");
            },
            () -> {
                JButton btnRelatorioVendas = (JButton) getComponentByName(telaInicial, "btnRelatorioVendas");
                assertNotNull(btnRelatorioVendas, "btnRelatorioVendas não foi encontrado!");
                assertTrue(btnRelatorioVendas.isVisible(), "btnRelatorioVendas não está visível!");
                assertTrue(btnRelatorioVendas.isEnabled(), "btnRelatorioVendas não está habilitado!");
            }
        );
    }
    
    /**
     * Busca recursivamente um componente por nome dentro de um container.
     *
     * @param container O container principal onde a busca será iniciada.
     * @param name O nome do componente desejado (deve ter sido definido com setName()).
     * @return O componente encontrado com o nome correspondente, ou null se não for encontrado.
     */
    private JComponent getComponentByName(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComponent && name.equals(comp.getName())) {
                return (JComponent) comp;
            }
            if (comp instanceof Container) {
                JComponent child = getComponentByName((Container) comp, name);
                if (child != null) return child;
            }
        }
        return null;
    }

}
