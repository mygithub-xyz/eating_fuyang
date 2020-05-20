package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offce.util.CookieUtil;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;

    /**
     * 从cooke中获取cartList
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response){

        //获取登录的名字,如果匿名登录(anonymousUser)的走cookie, 有名字走redis
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListStr = CookieUtil.getCookieValue( request, "cartList", "UTF-8" );

        if(cartListStr == null || cartListStr.equals( "" )){
            cartListStr = "[]";
        }
        //字符串转换成json
        List<Cart> cartList_Cookie = JSON.parseArray( cartListStr, Cart.class );
        if(name.equals( "anonymousUser" )){//未登录

            return cartList_Cookie;
        }else{//从redis
            List<Cart> cartList_Redis = cartService.findCartListFromRedis( name );

            //如果cookie中存在购物车,1则合并到reids, 2清除cookie中的购物车列表 3.合并和redis放到缓存中
            if(cartList_Cookie.size() >0){

                //1.合并到reids
                cartList_Redis = cartService.mergeCartList( cartList_Redis,  cartList_Cookie);
                //2.清除cookie中的购物车列表
                CookieUtil.deleteCookie( request, response, "cartList");
                //3.合并后的购物列表放回reids
                cartService.saveCartListToRedis( name, cartList_Redis);
            }

            return cartList_Redis;
        }
    }

    /**
     * 添加到购物车
     * 如果是未登录将购物车列表添加到Cookie，
     * 已登录就添加redis
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCartList(HttpServletRequest request, HttpServletResponse response, Long itemId, Integer num) {


        //设置跨域允许的域
/*        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");*/

        //获取登录的名字,如果匿名登录(anonymousUser)的走cookie, 有名字走redis
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        try {
            //获取购物车列表
            List<Cart> cartList = findCartList( request, response );
            //添加
            cartList = cartService.addGoodsToCartList( cartList, itemId, num );


            if(name.equals( "anonymousUser" )){//没登录
                //塞回到cookie
                CookieUtil.setCookie( request, response, "cartList",JSON.toJSONString( cartList )  ,3600*24, "UTF-8");
                System.out.println("将购物车列表塞到cookie中");
            }else{//已登录
                cartService.saveCartListToRedis(name, cartList );
                System.out.println("将购物车列表塞到redis中");
            }


            return  new Result( true,"添加成功" );
        }catch (Exception e){
            e.printStackTrace();
            return  new Result( false,"添加失败" );
        }


    }
}