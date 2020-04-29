package com.offcn.search.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        //高亮查询列表
        map.putAll(searchLsit(searchMap));
        return map;
    }
    private Map searchLsit(Map searchMap){
        Map map = new HashMap();
        //创建支持高亮查询器对象
        HighlightQuery query = new SimpleHighlightQuery();
        //创建高亮选项对象
        HighlightOptions highlightOptions =new HighlightOptions();
        //设置高亮处理字段
        highlightOptions.addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //关联高亮项到高亮查询器对象
        query.setHighlightOptions(highlightOptions);
        //设置查询条件 根据关键字查询
        /**
         * 1,创建查询条件对象
         * 2,关联条件到查询器对象
         */
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //发起高亮查询请求
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取查询结果集
        List<TbItem> tbItems = highlightPage.getContent();
        //循环结果集
        for (TbItem item : tbItems) {
            //获取到针对对象TbItem 高亮集合
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(item);
            if (highlights!=null&&highlights.size()>0){
                //获取第一个字段高亮对象
                List<String> highlightSnipplets = highlights.get(0).getSnipplets();
                System.out.println("高亮："+highlightSnipplets.get(0));
               //使用高亮结果替换商品标题
                item.setTitle(highlightSnipplets.get(0));
            }
        }
       map.put("rows",highlightPage.getContent());
        return map;
    }
}