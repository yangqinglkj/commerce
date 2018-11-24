package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.pinyougou.pojo.TbBrandExample.Criteria;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 品牌分页
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @return
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 添加商品
     * @param tbBrand
     */
    @Override
    public void addBrand(TbBrand tbBrand) {
           brandMapper.insert(tbBrand);

    }

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改品牌信息
     * @param tbBrand
     */
    @Override
    public void updateBrand(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    /**
     * 删除品牌信息
     * @param ids
     */
    @Override
    public void deleteBrand(long[] ids) {
        for (long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    /**
     * 分页条件查询
     * @param tbBrand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
        //分页
        PageHelper.startPage(pageNum,pageSize);

        TbBrandExample example = new TbBrandExample();

        Criteria criteria = example.createCriteria();
        if (tbBrand != null){
            if (tbBrand.getName() != null && tbBrand.getName().length() > 0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if (tbBrand.getFirstChar() != null && tbBrand.getFirstChar().length() > 0){
                criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 返回下拉列表
     * @return
     */
    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
