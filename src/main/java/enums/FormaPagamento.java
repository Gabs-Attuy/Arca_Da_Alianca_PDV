/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
