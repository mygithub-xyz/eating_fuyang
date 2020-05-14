package com.offcn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("接收到导入solr数据请求");
        if(message instanceof TextMessage){
            TextMessage textMessage=(TextMessage) message;

            try {
                String jsonStr = textMessage.getText();
                //转换json串为list集合
                List<TbItem> list = JSON.parseArray(jsonStr, TbItem.class);
                //保存sku集合到solr
                itemSearchService.importList(list);
                System.out.println("成功保存sku数据到solr");
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}