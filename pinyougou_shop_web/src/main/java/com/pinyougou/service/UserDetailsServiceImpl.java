package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证类
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建一个角色列表
        //GrantedAuthority代表每一个角色
        List<GrantedAuthority> grantAuths = new ArrayList<>();
        //GrantedAuthority是一个接口，只能new它的实现类,添加一个角色
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //根据姓名得到一个商家对象
        TbSeller seller = sellerService.findOne(username);
        if (seller != null){
            if (seller.getStatus().equals("1")){
                //UserDetails是一个接口，只有返回它的实现类User 用户对象
                return new User(username,seller.getPassword(),grantAuths);//当用户输入的密码和password密码一样就通过验证
            }else {
                return null;
            }
        }else{
            return null;
        }

    }
}
