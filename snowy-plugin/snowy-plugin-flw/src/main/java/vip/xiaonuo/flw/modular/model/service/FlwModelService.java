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
package vip.xiaonuo.flw.modular.model.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.flw.modular.model.entity.FlwModel;
import vip.xiaonuo.flw.modular.model.param.*;
import vip.xiaonuo.flw.modular.model.result.FlwModelOrgResult;
import vip.xiaonuo.flw.modular.model.result.FlwModelPositionResult;
import vip.xiaonuo.flw.modular.model.result.FlwModelRoleResult;
import vip.xiaonuo.flw.modular.model.result.FlwModelUserResult;

import java.util.List;

/**
 * 模型Service接口
 *
 * @author xuyuxiang
 * @date 2022/5/11 15:32
 **/
public interface FlwModelService extends IService<FlwModel> {

    /**
     * 获取模型分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<FlwModel> page(FlwModelPageParam flwModelPageParam);

    /**
     * 获取所有模型列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<FlwModel> allList(FlwModelListParam flwModelListParam);

    /**
     * 获取我可以发起的模型列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<FlwModel> myList(FlwModelMyParam flwModelMyParam);

    /**
     * 添加模型
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(FlwModelAddParam flwModelAddParam);

    /**
     * 编辑模型
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(FlwModelEditParam flwModelEditParam);

    /**
     * 删除模型
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<FlwModelIdParam> flwModelIdParamList);

    /**
     * 部署模型
     *
     * @author xuyuxiang
     * @date 2022/5/22 14:56
     */
    void deploy(FlwModelIdParam flwModelIdParam);

    /**
     * 获取模型详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    FlwModel detail(FlwModelIdParam flwModelIdParam);

    /**
     * 获取模型详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    FlwModel queryEntity(String id);

    /**
     * 停用模型
     *
     * @author xuyuxiang
     * @date 2022/8/14 18:58
     */
    void disableModel(FlwModelIdParam flwModelIdParam);

    /**
     * 启用模型
     *
     * @author xuyuxiang
     * @date 2022/8/14 18:58
     */
    void enableModel(FlwModelIdParam flwModelIdParam);

    /**
     * 模型降版
     *
     * @author xuyuxiang
     * @date 2022/8/14 18:58
     */
    void downVersion(FlwModelIdParam flwModelIdParam);

    /* ====模型部分所需要用到的选择器==== */

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取组织列表选择器
     *
     * @author xuyuxiang
     * @date 2022/7/22 13:34
     **/
    Page<FlwModelOrgResult> orgListSelector(FlwModelSelectorOrgListParam flwModelSelectorOrgListParam);

    /**
     * 获取职位选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    Page<FlwModelPositionResult> positionSelector(FlwModelSelectorPositionParam flwModelSelectorPositionParam);

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    Page<FlwModelRoleResult> roleSelector(FlwModelSelectorRoleParam flwModelSelectorRoleParam);

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<FlwModelUserResult> userSelector(FlwModelSelectorUserParam flwModelSelectorUserParam);

    /**
     * 获取执行监听器选择器
     *
     * @author xuyuxiang
     * @date 2023/5/11 15:05
     **/
    List<String> executionListenerSelector();

    /**
     * 获取自定义事件执行监听器选择器
     *
     * @author xuyuxiang
     * @date 2023/5/11 15:05
     **/
    List<String> executionListenerSelectorForCustomEvent();

    /**
     * 获取任务监听器选择器
     *
     * @author xuyuxiang
     * @date 2023/5/11 15:05
     **/
    List<String> taskListenerSelector();

    /**
     * 校验模型数据是否可被部署
     *
     * @author xuyuxiang
     * @date 2023/5/27 16:15
     */
    FlwModel validModel(String modelId);

}
