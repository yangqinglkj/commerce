package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.*;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 搜索方法
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map searchMap) {
        Map map = new HashMap();
        //空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));//关键字去掉空格
/*
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        map.put("rows",page.getContent());*/
        //1.查询列表
        map.putAll(searchList(searchMap));
        //2.分组查询商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //3.查询品牌和规格列表
        String category = (String) searchMap.get("category");
        if (!"".equals(category)) {//用户选择了其他的分类
            map.putAll(searchBrandAndSpecList(category));
        } else {
            if (categoryList.size() > 0) {//用户没有选择分类，取第一个分类

                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }
        return map;
    }

    //查询列表
    private Map searchList(Map searchMap) {
        Map map = new HashMap();

        //设置高亮选项
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightQuery = new HighlightOptions().addField("item_title");//在哪儿一列加
        //高亮前缀
        highlightQuery.setSimplePrefix("<em style='color:red'>");
        //高亮后缀
        highlightQuery.setSimplePostfix("</em>");
        //为查询对象设置高亮选项
        query.setHighlightOptions(highlightQuery);


        //1.1关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.2按照商品分类过滤
        if (!"".equals(searchMap.get("category"))) {//如果用户选择了才进行筛选
            FilterQuery filterQuery = new SimpleFilterQuery();//构建过滤查询
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按照品牌过滤
        if (!"".equals(searchMap.get("brand"))) {//如果用户选择了才进行筛选
            FilterQuery filterQuery = new SimpleFilterQuery();//构建过滤查询
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4按照规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.5按照价格过滤
        if (searchMap.get("price") != null) {
            String[] price = ((String) searchMap.get("price")).split("-");
            if (price.length > 1) {
                if (!"0".equals(price[0])) {//最低价格不等于0
                    FilterQuery filterQuery = new SimpleFilterQuery();//构建过滤查询
                    Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                    filterQuery.addCriteria(filterCriteria);
                    query.addFilterQuery(filterQuery);
                }
                if (!"*".equals(price[1])) {//最高价格不等于*
                    FilterQuery filterQuery = new SimpleFilterQuery();//构建过滤查询
                    Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                    filterQuery.addCriteria(filterCriteria);
                    query.addFilterQuery(filterQuery);
                }
            }
        }
        //1.6分页
        Integer pageNum = (Integer) searchMap.get("pageNum");//获取页码
        if (pageNum == null) {
            pageNum = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//获取页大小
        if (pageSize == null) {
            pageSize = 20;
        }
        query.setOffset((pageNum - 1) * pageSize);//起始索引  (当前页码-1)*每页记录数
        query.setRows(pageSize);//每页记录数

        //1.7排序
        String sortValue = (String) searchMap.get("sort");//升序ASC，降序DESC
        String sortField = (String) searchMap.get("sortField");//排序字段
        if (sortValue != null && !"".equals(sortValue)) {
            if ("ASC".equals(sortValue)) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if ("DESC".equals(sortValue)) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }


        //************获取高亮结果集**************
        //得到高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合(每条记录的高亮入口)  一个记录对应一个高亮入口
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList) {
            //获得所有的高亮列表(高亮域的个数)
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();//高亮对象
          /*  for (HighlightEntry.Highlight h : highlightList) {
                List<String> sns = h.getSnipplets();//每个域有可能存储多值
                System.out.println(sns);
            }*/
            if (highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0) {
                TbItem item = entry.getEntity();
                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());//设置总页数
        map.put("total", page.getTotalElements());//设置总记录数
        return map;
    }

    //分组查询(查询商品分类列表)
    private List<String> searchCategoryList(Map searchMap) {

        List<String> list = new ArrayList();
        Query query = new SimpleQuery("*:*");
        //根据关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");//指定分组条件
        query.setGroupOptions(groupOptions);
        //获取分组页对象 一个分组页可能包含多个分组结果，因为可能有多个分组列addGroupByField("item_category")
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果对象
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : entryList) {
            //将分组结果添加的list中
            list.add(entry.getGroupValue());
        }
        return list;
    }

    /**
     * 根据商品分类查询品牌和规格列表
     *
     * @param category 商品分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        //1.根据商品分类名称得到模板id
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (templateId != null) {
            //2.根据模板id获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList", brandList);
            //3.根据模板id获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList", specList);
        }

        return map;
    }

    /**
     * 导入列表
     *
     * @param list
     */
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除商品列表
     * @param goodsIds
     */
    @Override
    public void deleteByGoodsIds(List goodsIds) {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
