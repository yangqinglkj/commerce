package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 */
public interface BrandService {
    /**
     * 查询所有列表
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 品牌分页
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize);

    /**
     * 添加品牌
     * @param tbBrand
     */
    public void addBrand(TbBrand tbBrand);

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    public TbBrand findOne(long id);

    /**
     * 修改品牌信息
     * @param tbBrand
     */
    public void updateBrand(TbBrand tbBrand);

    /**
     * 删除品牌
     * @param ids
     */
    public void deleteBrand(long[] ids);

    /**
     * 分页条件查询
     * @param tbBrand
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    /**
     * 返回下拉列表
     * @return
     */
    public List<Map>  selectOptionList();

}
