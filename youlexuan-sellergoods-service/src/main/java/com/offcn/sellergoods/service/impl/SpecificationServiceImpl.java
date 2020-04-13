package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Specification;
import com.offcn.mapper.TbSpecificationMapper;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample;
import com.offcn.pojo.TbSpecificationExample.Criteria;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
	    //添加规格
		specificationMapper.insert(specification.getSpecification());
		//添加规格项
        Long id = specification.getSpecification().getId();
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            tbSpecificationOption.setSpecId(id);
            tbSpecificationOptionMapper.insert(tbSpecificationOption);
        }
    }

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
        //修改规格
        specificationMapper.updateByPrimaryKey(specification.getSpecification());
        /**
         * 修改规格项（一对多）
         * 先删除，在添加
         */
        //删除
        Long id =specification.getSpecification().getId();
        TbSpecificationOptionExample example =new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        tbSpecificationOptionMapper.deleteByExample(example);
        //添加
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            tbSpecificationOption.setSpecId(id);
            tbSpecificationOptionMapper.insert(tbSpecificationOption);
        }
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
	    //根据id查询规格
	    Specification specification =new Specification();
	    specification.setSpecification(specificationMapper.selectByPrimaryKey(id));
	    //根据规格id查询规格项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();//表
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();//where
        criteria.andSpecIdEqualTo(id);//条件
        specification.setSpecificationOptionList(tbSpecificationOptionMapper.selectByExample(example));
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
		    //删除规格
			specificationMapper.deleteByPrimaryKey(id);
            //删除规格项
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            tbSpecificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

    /**
     * 列表数据
     */
    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }

}
