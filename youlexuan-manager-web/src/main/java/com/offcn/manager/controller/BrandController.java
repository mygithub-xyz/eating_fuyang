package com.offcn.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    /**
     * 远程的注入
     * @return
     */
    @Reference(timeout = 300000)
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    /**
     * 分页查询
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return brandService.findPage( page, rows );
    }

    /**
     * 新增
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand) {
        try {
            brandService.add( brand );
            return new Result( true, "增加成功" );
        }catch (Exception e){
            e.printStackTrace();
            return new Result( false, "增加失败" );
        }
    }

    /**
     * 根据id查询品牌
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne( id );
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update( brand );
            return new Result( true, "修改成功" );
        }catch (Exception e){
            e.printStackTrace();
            return new Result( false, "修改失败" );
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/dele")
    public Result dele(Long[] ids) {
        try {
            brandService.delete( ids );
            return new Result( true, "删除成功" );
        }catch (Exception e){
            e.printStackTrace();
            return new Result( false, "删除失败" );
        }
    }


    /**
     * 条件分页查询
     * 接收前端的请求
     * 调用业务
     * 返回结果
     * 要接收带json格式的对象就要用注解 @RequestBody
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, int page, int rows) {
        return brandService.findPage(brand, page, rows );
    }

    /**
     * 查询品牌
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
