package model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author jeffe
 */
public class ProdutoModel {
    
    private UUID uuid;
    private String nome;
    private String categoria;
    private BigDecimal preco;
    private int estoque;
    private String codigoBarras;

    public ProdutoModel() {
        this.codigoBarras = generateBarCodeEAN13();
    }

    public ProdutoModel(UUID uuid, String nome, String categoria, BigDecimal preco, String codigoBarras) {
        this.uuid = uuid;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.estoque = 0;
        this.codigoBarras = codigoBarras;
    }
    
    public static String generateBarCodeEAN13() {
        long timestamp = System.currentTimeMillis();
        return String.valueOf(timestamp).substring(0, 12);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

   
}
