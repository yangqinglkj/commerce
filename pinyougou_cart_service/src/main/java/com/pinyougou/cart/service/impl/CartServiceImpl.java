package com.pinyougou.cart.service.impl;

import

        com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现层
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品未上架");
        }
        //2.根据SKU对象得到商家ID
        String sellerId = item.getSellerId();

        //3.根据商家ID在购物车列表中查询购物车对象,
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4.如果购物车列表中不存在该商家的购物车 Cart=null
        if (cart == null) {
            //4.1 创建一个新的购物车对象 new Cart()
            cart = new Cart();
            cart.setSellerId(sellerId);//商家id
            cart.setSellerName(item.getSeller());//商家名
            List<TbOrderItem> orderItemList = new ArrayList<>();//创建购物车明细列表
            //创建购物车明细对象
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2 将新的购物车对象添加到购物车列表中
            cartList.add(cart);

        }
        //5 判断该商品是否在该购物车的明细列表中
        TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
        if (orderItem == null){
            //5.2 如果不存在，创建新的购物车明细对象，并添加到该购物车的明细列表中
            orderItem = createOrderItem(item,num);
            cart.getOrderItemList().add(orderItem);
        }else {
            //5.1 如果存在，在原有购物车上添加数量，并更新金额
            orderItem.setNum(orderItem.getNum()+num);//更改数量
            orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));//更新金额
            //当明细的数量小于等于0，移出此明细
            if (orderItem.getNum() <= 0){
                cart.getOrderItemList().remove(orderItem);
            }
            //当购物车明细数量为0，在购物车列表中移出此购物车
            if (cart.getOrderItemList().size() == 0){
                cartList.remove(cart);
            }
        }
        return cartList;
    }
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 从redis中获取购物车
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车"+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);//根据用户名取
        if (CollectionUtils.isEmpty(cartList)){
            cartList = new ArrayList<>();
        }
        return cartList;
    }
    /**
     * 将购物车列表存入redis
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
        System.out.println("将购物车列表存入redis");
    }

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList1 = addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList1;
    }

    /**
     * 根据商家id在购物车列表中 查询购物车对象
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 创建购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        //创建购物车明细对象
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    /**
     * 根据SKU ID 在购物车明细列表中查询购物车明细对象
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList ,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }
}
