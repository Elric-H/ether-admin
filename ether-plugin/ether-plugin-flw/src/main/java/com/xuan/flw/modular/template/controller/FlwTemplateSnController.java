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
package com.xuan.flw.modular.template.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.xuan.common.annotation.CommonLog;
import com.xuan.common.pojo.CommonResult;
import com.xuan.common.pojo.CommonValidList;
import com.xuan.flw.modular.template.entity.FlwTemplateSn;
import com.xuan.flw.modular.template.param.*;
import com.xuan.flw.modular.template.service.FlwTemplateSnService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 流水号模板控制器
 *
 * @author xuyuxiang
 * @date 2022/6/15 16:38
 **/
@Api(tags = "流水号模板控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 2)
@RestController
@Validated
public class FlwTemplateSnController {

    @Resource
    private FlwTemplateSnService flwTemplateSnService;

    /**
     * 获取流水号模板分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取流水号模板分页")
    @GetMapping("/flw/templateSn/page")
    public CommonResult<Page<FlwTemplateSn>> page(FlwTemplateSnPageParam flwTemplateSnPageParam) {
        return CommonResult.data(flwTemplateSnService.page(flwTemplateSnPageParam));
    }

    /**
     * 添加流水号模板
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加流水号模板")
    @CommonLog("添加流水号模板")
    @PostMapping("/flw/templateSn/add")
    public CommonResult<String> add(@RequestBody @Valid FlwTemplateSnAddParam flwTemplateSnAddParam) {
        flwTemplateSnService.add(flwTemplateSnAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑流水号模板
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑流水号模板")
    @CommonLog("编辑流水号模板")
    @PostMapping("/flw/templateSn/edit")
    public CommonResult<String> edit(@RequestBody @Valid FlwTemplateSnEditParam flwTemplateSnEditParam) {
        flwTemplateSnService.edit(flwTemplateSnEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除流水号模板
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除流水号模板")
    @CommonLog("删除流水号模板")
    @PostMapping("/flw/templateSn/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           CommonValidList<FlwTemplateSnIdParam> flwTemplateSnIdParamList) {
        flwTemplateSnService.delete(flwTemplateSnIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取流水号模板详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取流水号模板详情")
    @GetMapping("/flw/templateSn/detail")
    public CommonResult<FlwTemplateSn> detail(@Valid FlwTemplateSnIdParam flwTemplateSnIdParam) {
        return CommonResult.data(flwTemplateSnService.detail(flwTemplateSnIdParam));
    }

    /**
     * 获取流水号模板列表选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("获取流水号模板列表选择器")
    @GetMapping("/flw/templateSn/flwTemplateSnSelector")
    public CommonResult<List<FlwTemplateSn>> flwTemplateSnSelector(FlwTemplateSnSelectorListParam flwTemplateSnSelectorListParam) {
        return CommonResult.data(flwTemplateSnService.flwTemplateSnSelector(flwTemplateSnSelectorListParam));
    }

}
