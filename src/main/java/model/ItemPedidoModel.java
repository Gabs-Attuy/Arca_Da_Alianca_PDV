package model;

import java.util.UUID;

/**
 *
 * @author jeff_
 */
public class ItemPedidoModel {
    
    private UUID pedidoId;
    private UUID produtoId;
    private int quantidade;

    public ItemPedidoModel() {}

    public ItemPedidoModel(UUID pedidoId, UUID produtoId, int quantidade) {
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setPedidoId(UUID pedidoId) {
        this.pedidoId = pedidoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    
    
    
}
