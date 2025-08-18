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
package vip.xiaonuo.flw.modular.template.controller;

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
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.pojo.CommonValidList;
import vip.xiaonuo.flw.modular.template.entity.FlwTemplatePrint;
import vip.xiaonuo.flw.modular.template.param.*;
import vip.xiaonuo.flw.modular.template.service.FlwTemplatePrintService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 打印模板控制器
 *
 * @author xuyuxiang
 * @date 2022/6/15 16:37
 **/
@Api(tags = "打印模板控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class FlwTemplatePrintController {

    @Resource
    private FlwTemplatePrintService flwTemplatePrintService;

    /**
     * 获取打印模板分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取打印模板分页")
    @GetMapping("/flw/templatePrint/page")
    public CommonResult<Page<FlwTemplatePrint>> page(FlwTemplatePrintPageParam flwTemplatePrintPageParam) {
        return CommonResult.data(flwTemplatePrintService.page(flwTemplatePrintPageParam));
    }

    /**
     * 添加打印模板
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加打印模板")
    @CommonLog("添加打印模板")
    @PostMapping("/flw/templatePrint/add")
    public CommonResult<String> add(@RequestBody @Valid FlwTemplatePrintAddParam flwTemplatePrintAddParam) {
        flwTemplatePrintService.add(flwTemplatePrintAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑打印模板
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑打印模板")
    @CommonLog("编辑打印模板")
    @PostMapping("/flw/templatePrint/edit")
    public CommonResult<String> edit(@RequestBody @Valid FlwTemplatePrintEditParam flwTemplatePrintEditParam) {
        flwTemplatePrintService.edit(flwTemplatePrintEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除打印模板
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除打印模板")
    @CommonLog("删除打印模板")
    @PostMapping("/flw/templatePrint/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           CommonValidList<FlwTemplatePrintIdParam> flwTemplatePrintIdParamList) {
        flwTemplatePrintService.delete(flwTemplatePrintIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取打印模板详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取打印模板详情")
    @GetMapping("/flw/templatePrint/detail")
    public CommonResult<FlwTemplatePrint> detail(@Valid FlwTemplatePrintIdParam flwTemplatePrintIdParam) {
        return CommonResult.data(flwTemplatePrintService.detail(flwTemplatePrintIdParam));
    }

    /**
     * 获取打印模板列表选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("获取打印模板列表选择器")
    @GetMapping("/flw/templatePrint/flwTemplatePrintSelector")
    public CommonResult<List<FlwTemplatePrint>> flwTemplatePrintSelector(FlwTemplatePrintSelectorListParam flwTemplatePrintSelectorListParam) {
        return CommonResult.data(flwTemplatePrintService.flwTemplatePrintSelector(flwTemplatePrintSelectorListParam));
    }
}
