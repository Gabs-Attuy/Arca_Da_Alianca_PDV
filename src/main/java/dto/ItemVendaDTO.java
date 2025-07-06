/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import enums.FormaPagamento;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author jeff_
 */
public class ItemVendaDTO {
    private Date dataVenda;
    private String itemNome;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemVendaDTO() {}

    public ItemVendaDTO(Date dataVenda, String itemNome, int quantidade, BigDecimal precoUnitario) {
        this.dataVenda = dataVenda;
        this.itemNome = itemNome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Date getDataVenda() {
        return dataVenda;
    }

    public String getItemNome() {
        return itemNome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public void setItemNome(String itemNome) {
        this.itemNome = itemNome;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
