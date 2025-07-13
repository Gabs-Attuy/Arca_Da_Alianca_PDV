/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.ProdutoDAO;
import dao.VendaDAO;
import dto.ItemVendaDTO;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * @author jeff_
 */
public class JasperReportsService {
    
    public void gerarRelatorioVendas(Date dataInicio, Date dataFim) {
        try {
            
            // 1. Buscar dados
            VendaDAO vendaDAO = new VendaDAO();
            List<ItemVendaDTO> dados = vendaDAO.buscarRelatorioVendasPorPeriodo(dataInicio, dataFim);

            // 2. Carregar JRXML
            String caminhoJrxml = "src/main/java/model/RelatorioVendaModel.jrxml"; // ajuste conforme seu projeto
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
            String caminhoAreaDeTrabalho = "C:\\Users\\jeff_\\OneDrive\\Desktop";
            
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
    
    public void gerarRelatorioProduto() throws JRException {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        List<ProdutoModel> produtos = produtoDAO.listarProdutosParaRelatorio(false);

        Map<String, Object> parametros = new HashMap<>();
        JasperReport jasperReport = JasperCompileManager.compileReport("src/main/java/model/RelatorioProdutoModel.jrxml");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JRBeanCollectionDataSource(produtos));
        JasperExportManager.exportReportToPdfFile(jasperPrint, "relatorio_produtos.pdf");
        
        JasperViewer.viewReport(jasperPrint, false);
        
    }
}
