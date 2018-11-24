package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import java.io.Serializable;
import java.util.List;

/**
 * 规格组合实体类
 * 规格本身+规格显像列表
 */
public class Specification implements Serializable{
    private TbSpecification specification;//规格本身
    private List<TbSpecificationOption> specificationOptionList;//规格显像列表

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
