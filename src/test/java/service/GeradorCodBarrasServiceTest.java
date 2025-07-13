package service;

import com.google.zxing.WriterException;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author gabs
 */
public class GeradorCodBarrasServiceTest {
    
    @Test
    void deveGerarCodigoDeBarrasParaCodigoEanValido() throws WriterException{
        String codigoValido = "123456789012";
        
        BufferedImage codigoDeBarras = GeradorCodBarrasService.gerarCodigoBarrasEAN13(codigoValido);
        
        assertNotNull(codigoDeBarras, "Imagem gerada não pode ser nula");
        assertEquals(240, codigoDeBarras.getWidth());
        assertEquals(80, codigoDeBarras.getHeight());
    }
    
    @Test
    void deveLancarExcecaoSeCodigoForNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            GeradorCodBarrasService.gerarCodigoBarrasEAN13(null);
        });

        assertEquals("Código EAN-13 precisa ter exatamente 12 dígitos numéricos.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoSeCodigoNaoTiver12Digitos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            GeradorCodBarrasService.gerarCodigoBarrasEAN13("123456");
        });

        assertEquals("Código EAN-13 precisa ter exatamente 12 dígitos numéricos.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoSeCodigoNaoForNumerico() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            GeradorCodBarrasService.gerarCodigoBarrasEAN13("ABCDEFGHIJKL");
        });

        assertEquals("Código EAN-13 precisa ter exatamente 12 dígitos numéricos.", exception.getMessage());
    }
}
