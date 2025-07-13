package enums;

/**
 *
 * @author jeffe
 */
public enum FormaPagamento {
    PIX("PIX"),
    CREDITO("CREDITO"),
    DEBITO("DEBITO"),
    DINHEIRO("DINHEIRO");
    
    final String descricao;
    
    private FormaPagamento(String descricao) {
        this.descricao = descricao;
    }
}
