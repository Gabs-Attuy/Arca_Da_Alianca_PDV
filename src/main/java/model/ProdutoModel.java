package model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author jeffe
 * @author gabs
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
        String codigo12 = String.valueOf(timestamp).substring(0, 12);
        char dv = calcularDigitoVerificador(codigo12);
        return codigo12 + dv;
    }
    
    public static char calcularDigitoVerificador(String codigo12Digitos) {
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            int digito = Character.getNumericValue(codigo12Digitos.charAt(i));
            soma += digito * (i % 2 == 0 ? 1 : 3);
        }

        int resultado = soma % 10;
        int dv = (10 - resultado) % 10;

        return Character.forDigit(dv, 10);
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
