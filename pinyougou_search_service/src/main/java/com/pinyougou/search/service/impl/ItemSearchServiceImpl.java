package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    /**
     * 搜索方法
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map searchMap) {
        Map map = new HashMap();
/*
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        map.put("rows",page.getContent());*/
        //高亮显示
        HighlightQuery query = new SimpleHighlightQuery();
        //构建高亮选项
        HighlightOptions highlightQuery = new HighlightOptions().addField("item_title");//在哪儿一列加
        //高亮前缀
        highlightQuery.setSimplePrefix("<em style='color:red'>");
        //高亮后缀
        highlightQuery.setSimplePostfix("</em>");
        //为查询对象设置高亮选项
        query.setHighlightOptions(highlightQuery);
        //关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //返回一个高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合(每条记录的高亮入口)
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList) {
            //获得高亮列表
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();
            for (HighlightEntry.Highlight h : highlightList) {
                List<String> sns = h.getSnipplets();
                System.out.println(sns);
            }
        }
        return map;
    }
}
