/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.awt.image.BufferedImage;

/**
 *
 * @author gabs
 */
public class ProdutoCatalogoDTO {
    private String nome;
    private String codigoEAN;
    private String categoria;
    private BufferedImage codigoDeBarras;

    public ProdutoCatalogoDTO(String nome, String codigoEAN, String categoria, BufferedImage codigoBarras) {
        this.nome = nome;
        this.codigoEAN = codigoEAN;
        this.categoria = categoria;
        this.codigoDeBarras = codigoBarras;
    }

    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoEAN() {
        return codigoEAN;
    }

    public void setCodigoEAN(String codigoEAN) {
        this.codigoEAN = codigoEAN;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BufferedImage getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(BufferedImage codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }
    
}
