package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbBrand;

import java.util.List;

/**
 * 品牌接口
 */
public interface BrandService {

    /**
     *  查询所有
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     */
    public PageResult findPage(int pageNo, int pageSize);

    /**
     * 增加
     */
    public void add(TbBrand brand);

    /**
     * 根据id查询
     */
    public TbBrand findOne(Long id);

    /**
     * 修改
     */
    public void update(TbBrand brand);

    /**
     * 根据id集合删除
     */
    public void delete(Long[] ids);

    /**
     * 根据条件分页查询
     */
    public PageResult findPage(TbBrand brand, int pageNo, int pageSize);
}
