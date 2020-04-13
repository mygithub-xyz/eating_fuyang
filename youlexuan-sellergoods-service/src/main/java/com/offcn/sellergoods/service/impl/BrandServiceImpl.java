package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbBrandMapper;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.TbBrandExample;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.Query;
import java.util.List;
import java.util.Map;

@Service
    public class BrandServiceImpl implements BrandService {

    /**
     * 本地注入
     */
    @Autowired
    private TbBrandMapper brandMapper;

    public List<TbBrand> findAll() {

        return brandMapper.selectByExample( null );//翻译sql select * from  tb_brand
    }

    /**
     * 要使用分页的对象
     * @param pageNo
     * @param pageSize
     * @return
     */
    public PageResult findPage(int pageNo, int pageSize) {

        PageHelper.startPage( pageNo, pageSize );
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample( null );
        return new PageResult( page.getTotal(), page.getResult() );
    }

    public void add(TbBrand brand) {
        brandMapper.insert( brand );
    }

    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey( id );//select * from tb_brand where id = ?
    }

    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey( brand );
    }

    public void delete(Long[] ids) {

        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey( id );
        }
    }

    public PageResult findPage(TbBrand brand, int pageNo, int pageSize) {

        PageHelper.startPage( pageNo, pageSize );


        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();

        //判断模糊名称是否存在
        if(brand != null){
            if(brand.getName() != null && brand.getName().length() > 0){
                criteria.andNameLike('%' + brand.getName() + "%");// where name like '%+brand.getName()+%'
            }
            if(brand.getFirstChar() != null && brand.getFirstChar().length() > 0){
                criteria.andFirstCharEqualTo( brand.getFirstChar() );// and frist_char = ?
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample( example );

        return new PageResult( page.getTotal(),page.getResult() );
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
