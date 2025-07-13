package service;

/**
 *
 * @author gabs
 */
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import java.awt.image.BufferedImage;

public class GeradorCodBarrasService {
    
    public static BufferedImage gerarCodigoBarrasEAN13(String codigo) throws WriterException {
        if (codigo == null || codigo.length() != 12 || !codigo.matches("\\d+")) {
            throw new IllegalArgumentException("Código EAN-13 precisa ter exatamente 12 dígitos numéricos.");
        }

        EAN13Writer writer = new EAN13Writer();
        BitMatrix bitMatrix = writer.encode(codigo, BarcodeFormat.EAN_13, 240, 80);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
}