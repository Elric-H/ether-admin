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
package vip.xiaonuo.urp.modular.controller;

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
import vip.xiaonuo.urp.modular.entity.UrpStorage;
import vip.xiaonuo.urp.modular.param.UrpStorageAddParam;
import vip.xiaonuo.urp.modular.param.UrpStorageEditParam;
import vip.xiaonuo.urp.modular.param.UrpStorageIdParam;
import vip.xiaonuo.urp.modular.param.UrpStoragePageParam;
import vip.xiaonuo.urp.modular.service.UrpService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 报表控制器
 *
 * @author xuyuxiang
 * @date 2022/7/6 14:14
 **/
@Api(tags = "报表控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class UrpController {

    @Resource
    private UrpService urpService;

    /**
     * 获取报表分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取报表分页")
    @GetMapping("/urp/storage/page")
    public CommonResult<Page<UrpStorage>> page(UrpStoragePageParam urpStoragePageParam) {
        return CommonResult.data(urpService.page(urpStoragePageParam));
    }

    /**
     * 添加报表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加报表")
    @CommonLog("添加报表")
    @PostMapping("/urp/storage/add")
    public CommonResult<String> add(@RequestBody @Valid UrpStorageAddParam urpStorageAddParam) {
        urpService.add(urpStorageAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑报表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑报表")
    @CommonLog("编辑报表")
    @PostMapping("/urp/storage/edit")
    public CommonResult<String> edit(@RequestBody @Valid UrpStorageEditParam urpStorageEditParam) {
        urpService.edit(urpStorageEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除报表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除报表")
    @CommonLog("删除报表")
    @PostMapping("/urp/storage/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           CommonValidList<UrpStorageIdParam> urpStorageIdParamList) {
        urpService.delete(urpStorageIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取报表详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取报表详情")
    @GetMapping("/urp/storage/detail")
    public CommonResult<UrpStorage> detail(@Valid UrpStorageIdParam urpStorageIdParam) {
        return CommonResult.data(urpService.detail(urpStorageIdParam));
    }
}
