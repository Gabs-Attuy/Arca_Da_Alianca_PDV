/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import enums.FormaPagamento;

/**
 *
 * @author jeffe
 */
public class VendaModel {
    
    private UUID uuid;
    private Date dataVenda;
    private BigDecimal totalVenda;
    private FormaPagamento formaPagamento;

    public VendaModel() {}

    public VendaModel(UUID uuid, Date dataVenda, BigDecimal totalVenda, FormaPagamento formaPagamento) {
        this.uuid = uuid;
        this.dataVenda = dataVenda;
        this.totalVenda = totalVenda;
        this.formaPagamento = formaPagamento;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getDataVenda() {
        return dataVenda;
    }

    public BigDecimal getTotalVenda() {
        return totalVenda;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public void setTotalVenda(BigDecimal totalVenda) {
        this.totalVenda = totalVenda;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
    
    
    
    
}
