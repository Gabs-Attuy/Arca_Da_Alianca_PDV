package service;

import com.google.zxing.WriterException;
import dao.ProdutoDAO;
import dao.VendaDAO;
import dto.ItemVendaDTO;
import dto.ProdutoCatalogoDTO;
import java.awt.HeadlessException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.ButtonGroup;
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
import template.InternalFrameRelatorioProduto;

/**
 *
 * @author jeff_
 */
public class JasperReportsService {
    
    public void gerarRelatorioVendas(Date dataInicio, Date dataFim) {
        try {
            
            // 1. Buscar dados
            VendaDAO vendaDAO = new VendaDAO();
            List<ItemVendaDTO> dados = vendaDAO.buscarRelatorioVendasPorPeriodo(dataInicio, dataFim);

            // 2. Carregar JRXML
            String caminhoJrxml = "src/main/java/jrxml/RelatorioVendaModel.jrxml"; // ajuste conforme seu projeto
            JasperReport jasperReport = JasperCompileManager.compileReport(caminhoJrxml);

            // 3. Preencher parâmetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("dataInicio", dataInicio);
            parametros.put("dataFim", dataFim);

            // 4. Gerar fonte de dados
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

            // 5. Preencher relatório
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
            
            // 6. Gerar nome do arquivo com a data e hora (Formato brasileiro)
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String dataHoraAtual = sdf.format(new Date());
            
            // Caminho para a área de trabalho
            String caminhoAreaDeTrabalho = "relatorios";
            
            // Caminho completo do arquivo PDF
            String caminhoSaida = caminhoAreaDeTrabalho + "/Relatorio_Venda_" + dataHoraAtual + ".pdf";
            
            // 7. Verificar se o caminho está correto antes de gerar o arquivo
            File arquivoSaida = new File(caminhoSaida);
            if (arquivoSaida.getParentFile() != null && !arquivoSaida.getParentFile().exists()) {
                // Se o diretório não existir, criar
                arquivoSaida.getParentFile().mkdirs();
            }

            // 8. Exportar para PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, caminhoSaida);
            JOptionPane.showMessageDialog(null, 
                              "Relatório gerado na área de trabalho com sucesso!",
                              "Sucesso", 
                              JOptionPane.INFORMATION_MESSAGE);

            // 7. Exibir na tela (opcional)
            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void gerarRelatorioProduto(ButtonGroup btn) throws JRException {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        List<ProdutoModel> produtos;
        
        String filtro = InternalFrameRelatorioProduto.setFiltroRelatorio(btn);

        
        if (filtro.equals("Apenas produtos sem estoque")) {
            produtos = produtoDAO.listarProdutosParaRelatorio(true);
        } else {
            produtos = produtoDAO.listarProdutosParaRelatorio(false);
        }

        Map<String, Object> parametros = new HashMap<>();
        JasperReport jasperReport = JasperCompileManager.compileReport("src/main/java/jrxml/RelatorioProdutoModel.jrxml");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JRBeanCollectionDataSource(produtos));
        JasperExportManager.exportReportToPdfFile(jasperPrint, "relatorios/relatorio_produtos.pdf");
        
        JasperViewer.viewReport(jasperPrint, false);
        
    }
    
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
                    produto.getCodigoBarras().substring(0, 11),
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
