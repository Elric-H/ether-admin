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
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
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
import com.xuan.flw.modular.template.entity.FlwTemplateSn;
import com.xuan.flw.modular.template.mapper.FlwTemplateSnMapper;
import com.xuan.flw.modular.template.param.*;
import com.xuan.flw.modular.template.service.FlwTemplateSnService;

import java.util.List;

/**
 * 流水号模板Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/6/15 16:31
 **/
@Service
public class FlwTemplateSnServiceImpl extends ServiceImpl<FlwTemplateSnMapper, FlwTemplateSn> implements FlwTemplateSnService {

    @Override
    public Page<FlwTemplateSn> page(FlwTemplateSnPageParam flwTemplateSnPageParam) {
        QueryWrapper<FlwTemplateSn> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(flwTemplateSnPageParam.getCategory())) {
            queryWrapper.lambda().eq(FlwTemplateSn::getCategory, flwTemplateSnPageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(flwTemplateSnPageParam.getSearchKey())) {
            queryWrapper.lambda().like(FlwTemplateSn::getName, flwTemplateSnPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(flwTemplateSnPageParam.getSortField(), flwTemplateSnPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(flwTemplateSnPageParam.getSortOrder());
            queryWrapper.orderBy(true, flwTemplateSnPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(flwTemplateSnPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(FlwTemplateSn::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }



    @Override
    public void add(FlwTemplateSnAddParam flwTemplateSnAddParam) {
        FlwTemplateSn flwTemplateSn = BeanUtil.toBean(flwTemplateSnAddParam, FlwTemplateSn.class);
        flwTemplateSn.setCode(RandomUtil.randomString(10));
        this.save(flwTemplateSn);
    }

    @Override
    public void edit(FlwTemplateSnEditParam flwTemplateSnEditParam) {
        FlwTemplateSn flwTemplateSn = this.queryEntity(flwTemplateSnEditParam.getId());
        // 被使用则不允许编辑
        if(ObjectUtil.isNotEmpty(flwTemplateSn.getLatestValue())) {
            throw new CommonException("该流水号模板已经被使用，不允许编辑，id值为：{}", flwTemplateSn.getId());
        }
        BeanUtil.copyProperties(flwTemplateSnEditParam, flwTemplateSn);
        this.updateById(flwTemplateSn);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FlwTemplateSnIdParam> flwTemplateSnIdParamList) {
        this.removeBatchByIds(CollStreamUtil.toList(flwTemplateSnIdParamList, FlwTemplateSnIdParam::getId));
    }

    @Override
    public FlwTemplateSn detail(FlwTemplateSnIdParam flwTemplateSnIdParam) {
        return this.queryEntity(flwTemplateSnIdParam.getId());
    }

    @Override
    public FlwTemplateSn queryEntity(String id) {
        FlwTemplateSn flwTemplateSn = this.getById(id);
        if(ObjectUtil.isEmpty(flwTemplateSn)) {
            throw new CommonException("流水号模板不存在，id值为：{}", id);
        }
        return flwTemplateSn;
    }

    @Override
    public String getTemplateValueBydId(String id) {
        FlwTemplateSn flwTemplateSn = this.queryEntity(id);
        return genSnCode(flwTemplateSn);
    }

    @Override
    public List<FlwTemplateSn> flwTemplateSnSelector(FlwTemplateSnSelectorListParam flwTemplateSnSelectorListParam) {
        LambdaQueryWrapper<FlwTemplateSn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(flwTemplateSnSelectorListParam.getCategory())) {
            lambdaQueryWrapper.eq(FlwTemplateSn::getCategory, flwTemplateSnSelectorListParam.getCategory());
        }
        return this.list(lambdaQueryWrapper);
    }

    /**
     * 生成流水号
     *
     * @author xuyuxiang
     * @date 2022/6/21 9:56
     **/
    private String genSnCode(FlwTemplateSn flwTemplateSn) {
        // 获取最新值，最新值不为空，则表示使用过，将其返回，再基于上一个生成一个新的存起来，方便下次使用
        String latestValue = flwTemplateSn.getLatestValue();
        if(ObjectUtil.isNotEmpty(latestValue)) {
            // 校验上个值
            String oldSnCodeRemovePrefix = StrUtil.removePrefix(latestValue, flwTemplateSn.getPrefix());
            boolean sameDay = DateUtil.isSameDay(DateTime.now(), DateUtil.parse(StrUtil.sub(oldSnCodeRemovePrefix, 0,
                    oldSnCodeRemovePrefix.length() - flwTemplateSn.getSuffixDigits())));
            if(sameDay) {
                // 如果上次日期与当前日期是同一天，则再生成个新的，保存
                genAndSaveSnCodeByOldCode(latestValue, flwTemplateSn);
                // 返回上一个的
                return latestValue;
            } else {
                // 如果上次日期与当前日期不是同一天，则重新生成
                return this.doGetNewSn(flwTemplateSn);
            }
        } else {
            // 最新值为空，则表示还没使用过，则生成一个并返回，再基于上一个生成一个新的存起来，方便下次使用
            return this.doGetNewSn(flwTemplateSn);
        }
    }

    /**
     * 根据模板获取流水号
     *
     * @author xuyuxiang
     * @date 2023/7/27 16:04
     **/
    private String doGetNewSn(FlwTemplateSn flwTemplateSn) {
        // 前缀
        String prefix = flwTemplateSn.getPrefix();
        // 日期格式化
        String dateFormat = flwTemplateSn.getDateFormat();
        String formatResult = DateUtil.format(DateTime.now(), dateFormat);
        // 后缀位数
        Integer suffixDigits = flwTemplateSn.getSuffixDigits();

        // 流水号：后缀初始值
        int snCodeValue = flwTemplateSn.getSuffixInitialValue();

        // 如果流水号的值比位数大，则表示流水号用尽
        if(Convert.toStr(snCodeValue).length() > suffixDigits) {
            throw new CommonException("流水号使用已超出范围，id为：{}", flwTemplateSn.getId());
        }

        StringBuilder suffixDigitsFormat = new StringBuilder();
        for (int i = 0; i < suffixDigits; i++) {
            suffixDigitsFormat.append("0");
        }
        // 组装：前缀 + 日期 + 后缀
        String resultCode = prefix + formatResult + NumberUtil.decimalFormat(suffixDigitsFormat.toString(), snCodeValue);

        // 生成一个新的并保存
        genAndSaveSnCodeByOldCode(resultCode, flwTemplateSn);
        return resultCode;
    }

    /**
     * 根据老的流水号生成新流水号
     *
     * @author xuyuxiang
     * @date 2022/6/21 10:15
     **/
    private void genAndSaveSnCodeByOldCode(String oldSnCode, FlwTemplateSn flwTemplateSn) {
        // 前缀
        String prefix = flwTemplateSn.getPrefix();
        // 日期格式化
        String dateFormat = flwTemplateSn.getDateFormat();

        // 获取当前日期
        DateTime now = DateTime.now();

        String formatResult = DateUtil.format(now, dateFormat);
        // 后缀位数
        Integer suffixDigits = flwTemplateSn.getSuffixDigits();
        // 后缀增量
        Integer suffixIncrementalValue = flwTemplateSn.getSuffixIncrementalValue();

        // 对老的流水号进行拆解，截取后缀的位数
        int oldSuffixInitialValue = Convert.toInt(StrUtil.subSufByLength(oldSnCode, 4));

        // 定义流水号值
        int snCodeValue = oldSuffixInitialValue + suffixIncrementalValue;

        // 如果流水号的值比位数大，则表示流水号用尽
        if(Convert.toStr(snCodeValue).length() > suffixDigits) {
            throw new CommonException("流水号使用已超出范围，id为：{}", flwTemplateSn.getId());
        }

        // 使用位数来格式化流水号值
        StringBuilder suffixDigitsFormat = new StringBuilder();
        for (int i = 0; i < suffixDigits; i++) {
            suffixDigitsFormat.append("0");
        }

        // 组装：前缀 + 日期 + 后缀
        String resultCode = prefix + formatResult + NumberUtil.decimalFormat(suffixDigitsFormat.toString(), snCodeValue);

        // 设置并保存
        flwTemplateSn.setLatestValue(resultCode);
        this.updateById(flwTemplateSn);
    }
}
