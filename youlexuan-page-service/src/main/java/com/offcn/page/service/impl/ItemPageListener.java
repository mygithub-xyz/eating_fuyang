package com.offcn.page.service.impl;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 *监听用于静态页面的生成
 *
 */
@Component
public class ItemPageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        System.out.println("接收到生产静态页数据请求");
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            System.out.println("ItemPageListener 监听接收到消息..."+text);
            System.out.println(Long.getLong(text)+"============="+Long.parseLong(text));
            itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("成功生产静态页,在page-web中");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}