package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    /**
     * 查询所有品牌
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 查询所有品牌分页
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page,int size){
        return brandService.findPage(page,size);
    }

    /**
     * 添加品牌
     * @param brand
     * @return 返回是否添加成功
     */
    @RequestMapping("/addBrand")
    public Result addBrand(@RequestBody TbBrand brand){
        try {
            brandService.addBrand(brand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(long id){
        return brandService.findOne(id);
    }

    /**
     * 修改品牌信息
     * @param tbBrand
     */
    @RequestMapping("/updateBrand")
    public Result updateBrand(@RequestBody TbBrand tbBrand){
        try {
            brandService.updateBrand(tbBrand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /**
     * 修改品牌信息
     * @param ids
     */
    @RequestMapping("/deleteBrand")
    public Result deleteBrand(long[] ids){
        try {
            brandService.deleteBrand(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /**
     * 分页条件查询
     * @param tbBrand
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand tbBrand,int page,int size){
        return brandService.findPage(tbBrand,page,size);
    }

    /**
     * 返回下拉列表
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }


}
