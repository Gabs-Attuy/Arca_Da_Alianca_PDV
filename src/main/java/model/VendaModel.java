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
}
