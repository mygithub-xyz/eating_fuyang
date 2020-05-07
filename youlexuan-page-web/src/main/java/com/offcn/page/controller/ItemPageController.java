package com.offcn.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.page.service.ItemPageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemPageController {
    @Reference(timeout=40000)
    private ItemPageService itemPageService;
    /**
     * 生成静态页（测试）
     * @param goodsId
     */
    @RequestMapping("/genHtml")
    public void genHtml(Long goodsId){
        itemPageService.genItemHtml(goodsId);
    }
}
