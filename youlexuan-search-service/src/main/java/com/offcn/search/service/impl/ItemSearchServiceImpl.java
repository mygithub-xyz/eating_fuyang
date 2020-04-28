package com.offcn.search.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
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

        Query query = new SimpleQuery();

        // is：基于分词后的结果 和 传入的参数匹配
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        // 添加查询条件
        query.addCriteria(criteria);

        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

        map.put("rows", page.getContent());

        return map;
    }
}