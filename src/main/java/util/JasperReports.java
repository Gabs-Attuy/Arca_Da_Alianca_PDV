/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import dao.VendaDAO;
import dto.ItemVendaDTO;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class JasperReports {
    
    public void gerarRelatorio(Date startDate, Date endDate) {
        try {
            
            // 1. Buscar dados
            VendaDAO vendaDAO = new VendaDAO();
            List<ItemVendaDTO> dados = vendaDAO.buscarRelatorioVendasPorPeriodo(startDate, endDate);

            // 2. Carregar JRXML
            String caminhoJrxml = "src/main/java/model/RelatorioVendaModel.jrxml"; // ajuste conforme seu projeto
            JasperReport jasperReport = JasperCompileManager.compileReport(caminhoJrxml);

            // 3. Preencher parâmetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("dataInicio", startDate);
            parametros.put("dataFim", endDate);

            // 4. Gerar fonte de dados
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

            // 5. Preencher relatório
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            // 6. Exportar para PDF
            String caminhoSaida = "src/main/resources/relatorio-vendas.pdf"; // será criado na raiz do projeto
            File outDir = new File("relatorios");
            if (!outDir.exists()) outDir.mkdirs();

            JasperExportManager.exportReportToPdfFile(jasperPrint, caminhoSaida);
            System.out.println("Relatório gerado em: " + new File(caminhoSaida).getAbsolutePath());

            // 7. Exibir na tela (opcional)
            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
