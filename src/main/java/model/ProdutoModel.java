/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author jeffe
 */
public class ProdutoModel {
    
    private UUID uuid;
    private String nomeProduto;
    private String categoriaProduto;
    private BigDecimal precoProduto;
    private int estoqueProduto;
    private String codigoBarras;

    public ProdutoModel() {}

    public ProdutoModel(UUID uuid, String nomeProduto, String categoriaProduto, BigDecimal precoProduto, int estoqueProduto, String codigoBarras) {
        this.uuid = uuid;
        this.nomeProduto = nomeProduto;
        this.categoriaProduto = categoriaProduto;
        this.precoProduto = precoProduto;
        this.estoqueProduto = estoqueProduto;
        this.codigoBarras = codigoBarras;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getCategoriaProduto() {
        return categoriaProduto;
    }

    public BigDecimal getPrecoProduto() {
        return precoProduto;
    }

    public int getEstoqueProduto() {
        return estoqueProduto;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public void setCategoriaProduto(String categoriaProduto) {
        this.categoriaProduto = categoriaProduto;
    }

    public void setPrecoProduto(BigDecimal precoProduto) {
        this.precoProduto = precoProduto;
    }

    public void setEstoqueProduto(int estoqueProduto) {
        this.estoqueProduto = estoqueProduto;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
}
