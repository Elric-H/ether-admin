/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.xuan.flw.modular.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xuan.common.enums.CommonSortOrderEnum;
import com.xuan.common.exception.CommonException;
import com.xuan.common.page.CommonPageRequest;
import com.xuan.flw.modular.template.entity.FlwTemplatePrint;
import com.xuan.flw.modular.template.mapper.FlwTemplatePrintMapper;
import com.xuan.flw.modular.template.param.*;
import com.xuan.flw.modular.template.service.FlwTemplatePrintService;

import java.util.List;

/**
 * 打印模板Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/6/15 16:39
 **/
@Service
public class FlwTemplatePrintServiceImpl extends ServiceImpl<FlwTemplatePrintMapper, FlwTemplatePrint> implements FlwTemplatePrintService {

    @Override
    public Page<FlwTemplatePrint> page(FlwTemplatePrintPageParam flwTemplatePrintPageParam) {
        QueryWrapper<FlwTemplatePrint> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(flwTemplatePrintPageParam.getCategory())) {
            queryWrapper.lambda().eq(FlwTemplatePrint::getCategory, flwTemplatePrintPageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(flwTemplatePrintPageParam.getSearchKey())) {
            queryWrapper.lambda().like(FlwTemplatePrint::getName, flwTemplatePrintPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(flwTemplatePrintPageParam.getSortField(), flwTemplatePrintPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(flwTemplatePrintPageParam.getSortOrder());
            queryWrapper.orderBy(true, flwTemplatePrintPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(flwTemplatePrintPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(FlwTemplatePrint::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public void add(FlwTemplatePrintAddParam flwTemplatePrintAddParam) {
        FlwTemplatePrint flwTemplatePrint = BeanUtil.toBean(flwTemplatePrintAddParam, FlwTemplatePrint.class);
        flwTemplatePrint.setCode(RandomUtil.randomString(10));
        this.save(flwTemplatePrint);
    }

    @Override
    public void edit(FlwTemplatePrintEditParam flwTemplatePrintEditParam) {
        FlwTemplatePrint flwTemplatePrint = this.queryEntity(flwTemplatePrintEditParam.getId());
        BeanUtil.copyProperties(flwTemplatePrintEditParam, flwTemplatePrint);
        this.updateById(flwTemplatePrint);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FlwTemplatePrintIdParam> flwTemplatePrintIdParamList) {
        this.removeBatchByIds(CollStreamUtil.toList(flwTemplatePrintIdParamList, FlwTemplatePrintIdParam::getId));
    }

    @Override
    public FlwTemplatePrint detail(FlwTemplatePrintIdParam flwTemplatePrintIdParam) {
        return this.queryEntity(flwTemplatePrintIdParam.getId());
    }

    @Override
    public FlwTemplatePrint queryEntity(String id) {
        FlwTemplatePrint flwTemplatePrint = this.getById(id);
        if(ObjectUtil.isEmpty(flwTemplatePrint)) {
            throw new CommonException("打印模板不存在，id值为：{}", id);
        }
        return flwTemplatePrint;
    }

    @Override
    public List<FlwTemplatePrint> flwTemplatePrintSelector(FlwTemplatePrintSelectorListParam flwTemplatePrintSelectorListParam) {
        LambdaQueryWrapper<FlwTemplatePrint> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(flwTemplatePrintSelectorListParam.getCategory())) {
            lambdaQueryWrapper.eq(FlwTemplatePrint::getCategory, flwTemplatePrintSelectorListParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(flwTemplatePrintSelectorListParam.getSearchKey())) {
            lambdaQueryWrapper.eq(FlwTemplatePrint::getName, flwTemplatePrintSelectorListParam.getSearchKey());
        }
        return this.list(lambdaQueryWrapper);
    }
}
