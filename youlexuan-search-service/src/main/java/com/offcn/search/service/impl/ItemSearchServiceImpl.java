package com.offcn.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.promeg.pinyinhelper.Pinyin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.result.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 商品收搜模块
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        //1按关键字查询（高亮显示）
        map.putAll(searchLsit(searchMap));
        //2.根据关键字查询商品分类
         List categoryList = findCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //3.查询品牌和规格列表
        String category = (String) searchMap.get("category");
        if(!"".equals(category)){
            map.putAll(searchBrandAndSpecList(category));

        }else{
            if (categoryList.size()>0)
            {
                map.putAll(searchBrandAndSpecList((String)categoryList.get(0)));
            }
        }
        return map;
    }

    /**
     * 关键字查询（高亮显示）
     * @param searchMap
     * @return
     */
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
        //1.2按分类筛选
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按品牌筛选
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4按规格筛选
        //1.4过滤规格
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map) searchMap.get("spec");
            for(String key:specMap.keySet() ){
                Criteria filterCriteria=new Criteria("item_spec_"+Pinyin.toPinyin(key, "").toLowerCase()).is( specMap.get(key) );
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //发起高亮查询请求
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取查询结果集
        List<TbItem> tbItemsList = highlightPage.getContent();
        //循环结果集
        for (TbItem item : tbItemsList) {
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
        //将高亮结果集放到map集合中
       map.put("rows",tbItemsList);
        return map;
    }

    /**
     * 分类列表分组查询
     * @param searchMap
     * @return
     */
    private List findCategoryList(Map searchMap){
        List<String> list =new ArrayList();
        //创建查询器对象
        Query query = new SimpleQuery();
        //1,创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        // 2,关联条件到查询器对象
        query.addCriteria(criteria);
        //分组对象
        GroupOptions groupOptions = new GroupOptions();
        //添加分组添加
        groupOptions.addGroupByField("item_category");
        //将分组设置到查询器中
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query,TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> item_category = groupPage.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //循环结果集
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
           //将分组结果的名称封装到返回值中
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return  list;
    }
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询品牌和规格列表
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
        if(typeId!=null){
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }


}