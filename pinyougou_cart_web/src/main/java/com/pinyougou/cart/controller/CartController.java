package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.util.CookieUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Reference(timeout = 100000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    /**
     * 从cookie中获取购物车方法
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        //获取登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人：" + username);
        String cookieValue = CookieUtil.getCookieValue(request, "cartList", "UTF-8");

        if (cookieValue == null || "".equals(cookieValue)) {
            cookieValue = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cookieValue, Cart.class);
        if ("anonymousUser".equals(username)) {
            //用户未登陆 从cookie中获取购物车
            System.out.println("从cookie中提取购物车");
            return cartList_cookie;

        } else {
            // /用户已登陆  从redis中获取购物车
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            //判断当本地购物车存在数据才合并
            if (cartList_cookie.size() > 0) {
                //得到合并后的购物车
                List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
                //再次存入redis中
                cartService.saveCartListToRedis(username, cartList);
                //清楚本地购物车
                CookieUtil.deleteCookie(request, response, "cartList");
                System.out.println("执行合并购物车");
                return cartList;
            }
            return cartList_redis;
        }

    }

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")//跨域注解  allowCredentials(设置cookie) 属性默认等于true
    public Result addGoodsToCartList(Long itemId, Integer num) {
        //设置可以访问的域(如果此方法没有调用cookie，只配置这一个就够了)
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
//        //如果此方法中使用了cookie，就必须加上此设置
//        response.setHeader("Access-Control-Allow-Credentials", "true");




        //获取登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            //1.获取购物车
            List<Cart> cartList = findCartList();
            //2.调用服务方法操作购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if ("anonymousUser".equals(username)) {
                System.out.println("将新的购物车存入cookie");
                //3.如果未登陆 将新的购物车存入cookie
                String cartListString = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "UTF-8");
            } else {
                //如果登陆 将新的购物车存入redis
                cartService.saveCartListToRedis(username, cartList);

            }
            return new Result(true, "存入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "存入购物车失败");
        }

    }
}
