package service;

import com.google.zxing.WriterException;
import dao.ProdutoDAO;
import dto.ProdutoCatalogoDTO;
import java.awt.HeadlessException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.ProdutoModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author gabs
 */
public class JasperReportsCatalogo {
    
    public void gerarCatalogo() {
        try {
            
            // 1. Buscar dados
            ProdutoDAO produtoDAO = new ProdutoDAO();
            List<ProdutoModel> produtos = produtoDAO.findAll();
            List<ProdutoCatalogoDTO> dados = converterParaCatalogo(produtos);
            
            // 2. Carregar JRXML
            String caminhoJrxml = "src/main/java/jrxml/CatalogoProdutosModel.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport(caminhoJrxml);

            // 3. Preencher parâmetros
            Map<String, Object> parametros = new HashMap<>();

            // 4. Gerar fonte de dados
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

            // 5. Preencher relatório
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
            
            // 6. Gerar nome do arquivo com a data e hora (Formato brasileiro)
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String dataHoraAtual = sdf.format(new Date());
            
            // Caminho para a área de trabalho
            String caminhoPastaRelatorios = "relatorios";
            
            // Caminho completo do arquivo PDF
            String caminhoSaida = caminhoPastaRelatorios + "/Catalogo_Produtos_" + dataHoraAtual + ".pdf";
            
            // 7. Verificar se o caminho está correto antes de gerar o arquivo
            File arquivoSaida = new File(caminhoSaida);
            if (arquivoSaida.getParentFile() != null && !arquivoSaida.getParentFile().exists()) {
                // Se o diretório não existir, criar
                arquivoSaida.getParentFile().mkdirs();
            }

            // 8. Exportar para PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, caminhoSaida);
            JOptionPane.showMessageDialog(null, 
                              "Catálogo gerado na pasta relatorios!",
                              "Sucesso", 
                              JOptionPane.INFORMATION_MESSAGE);

            // 7. Exibir na tela (opcional)
            JasperViewer.viewReport(jasperPrint, false);

        } catch (HeadlessException | JRException e) {
            e.printStackTrace();
        }
    }
    
    public List<ProdutoCatalogoDTO> converterParaCatalogo(List<ProdutoModel> produtos) {
        return produtos.stream().map(produto -> {
            try {
                ProdutoCatalogoDTO dto = new ProdutoCatalogoDTO(
                    produto.getNome(),
                    produto.getCodigoBarras(),
                    produto.getCategoria(),
                    GeradorCodBarrasService.gerarCodigoBarrasEAN13(produto.getCodigoBarras())
                );
                return dto;
            } catch (WriterException e) {
                throw new RuntimeException("Erro ao gerar código de barras para o produto: " + produto.getNome(), e);
            }
        }).collect(Collectors.toList());
    }
}
