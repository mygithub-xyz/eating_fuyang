package com.offcn.cart.service;

import com.offcn.entity.Cart;

import java.util.List;

/**
 * 购物车服务接口
 * @author Administrator
 *
 */
public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartList 原购物车商品集合
     * @param itemId  sku编号
     * @param num     购买数量
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );
}