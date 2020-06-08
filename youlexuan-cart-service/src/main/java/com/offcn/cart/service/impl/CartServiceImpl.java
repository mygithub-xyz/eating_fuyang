package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {


        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey( itemId );
        if(item == null){
            throw new RuntimeException("商品不存在");
        }
        if(!item.getStatus().equals( "1" )){
            throw new RuntimeException("商品信息异常");
        }
        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车,单独写个方法(联想前端给定对象在提供的集合中是否存在?返回对象:null)
        Cart cart = searchCartBySellerId(cartList, sellerId);//类似模板方法模式

        //4.如果购物车列表中不存在该商家的购物车
        if(cart == null){
            //4.1 新建购物车对象
            cart = new Cart();
            //商家id
            cart.setSellerId(  sellerId );
            //商家名称
            cart.setSellerName( item.getSeller() );
            //商家的购物明细(订单详细),添加明细(单独一个方法)
            TbOrderItem orderItem = createOrderItem(item, num);//类似模板方法模式
            List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>(  );
            orderItemList.add( orderItem );
            cart.setOrderItemList( orderItemList );
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add( cart );
        }else{
            //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            //5.1. 如果没有，新增购物车明细
            if(orderItem == null){
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add( orderItem );
            }else{
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                //累加数量
                orderItem.setNum( orderItem.getNum() + num );
                //累加价格
                orderItem.setTotalFee( new BigDecimal( orderItem.getNum() * orderItem.getPrice().doubleValue() ) );

                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove( orderItem );
                }

                if(cart.getOrderItemList().size() == 0) {
                    cartList.remove( cart );
                }
            }
        }
        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps( "cartList" ).get( username );

        if(cartList == null){
            cartList = new ArrayList<Cart>(  );
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps( "cartList" ).put( username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {

        for (Cart cart : cartList2) {
            for(TbOrderItem orderItem : cart.getOrderItemList()){
                cartList1 = addGoodsToCartList( cartList1, orderItem.getItemId(), orderItem.getNum() );
            }
        }

        return cartList1;
    }

    /**
     * 根据商家是否存在现有购物车列表是否存在
     */
    public Cart searchCartBySellerId(List<Cart> cartList, String sellerId){

        for (Cart cart : cartList) {
            if( cart.getSellerId().equals( sellerId )){
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据商品sku的id查找在购物车订单详细列表中是否存在
     */
    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().equals( itemId )) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 添加商品订单详细，并返回
     */
    public TbOrderItem createOrderItem(TbItem item, Integer num){

        TbOrderItem orderItem = new TbOrderItem();

        orderItem.setItemId( item.getId() );
        orderItem.setGoodsId( item.getGoodsId() );
        //`order_id` bigint(20) NOT NULL COMMENT '订单id',

        orderItem.setTitle( item.getTitle() );
        orderItem.setPrice( item.getPrice() );
        orderItem.setNum( num );
        orderItem.setTotalFee( new BigDecimal( item.getPrice().doubleValue() * num ) );
        orderItem.setPicPath( item.getImage() );
        orderItem.setSellerId( item.getSellerId() );

        return orderItem;
    }
}