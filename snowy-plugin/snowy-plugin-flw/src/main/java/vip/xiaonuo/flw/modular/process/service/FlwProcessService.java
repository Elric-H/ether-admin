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
package vip.xiaonuo.flw.modular.process.service;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import vip.xiaonuo.flw.core.node.FlwNode;
import vip.xiaonuo.flw.modular.model.entity.FlwModel;
import vip.xiaonuo.flw.modular.process.param.*;
import vip.xiaonuo.flw.modular.process.result.*;

import java.util.List;

/**
 * 流程Service接口
 *
 * @author xuyuxiang
 * @date 2022/5/11 15:52
 **/
public interface FlwProcessService {

    /**
     * 保存草稿
     *
     * @author xuyuxiang
     * @date 2023/5/23 19:47
     **/
    void saveDraft(FlwProcessSaveDraftParam flwProcessSaveDraftParam);

    /**
     * 启动流程
     *
     * @author xuyuxiang
     * @date 2022/5/21 22:45
     */
    void start(FlwProcessStartParam flwProcessStartParam);

    /**
     * 获取我的草稿分页
     *
     * @author xuyuxiang
     * @date 2023/5/23 19:48
     **/
    Page<FlwProcessMyDraftResult> myDraftPage(FlwProcessMyDraftPageParam flwProcessMyDraftPageParam);

    /**
     * 获取草稿详情
     *
     * @author xuyuxiang
     * @date 2023/5/23 19:49
     **/
    FlwProcessDraftDetailResult draftDetail(FlwProcessDraftIdParam flwProcessDraftIdParam);

    /**
     * 删除草稿
     *
     * @author xuyuxiang
     * @date 2023/5/23 20:24
     **/
    void deleteDraft(List<FlwProcessDraftIdParam> flwProcessDraftIdParamList);

    /**
     * 获取流程分页
     *
     * @author xuyuxiang
     * @date 2022/5/11 16:26
     **/
    Page<FlwProcessResult> monitorPage(FlwProcessMonitorPageParam flwProcessMonitorPageParam);

    /**
     * 删除流程
     *
     * @author xuyuxiang
     * @date 2022/6/30 15:59
     **/
    void delete(List<FlwProcessIdParam> flwProcessIdParamList);

    /**
     * 终止流程
     *
     * @author xuyuxiang
     * @date 2022/8/23 16:49
     **/
    void end(List<FlwProcessIdParam> flwProcessIdParamList);

    /**
     * 撤回流程
     *
     * @author xuyuxiang
     * @date 2022/8/23 16:49
     **/
    void revoke(List<FlwProcessIdParam> flwProcessIdParamList);

    /**
     * 挂起流程
     *
     * @author xuyuxiang
     * @date 2022/8/23 16:49
     **/
    void suspend(List<FlwProcessIdParam> flwProcessIdParamList);

    /**
     * 激活流程
     *
     * @author xuyuxiang
     * @date 2022/8/23 16:49
     **/
    void active(List<FlwProcessIdParam> flwProcessIdParamList);

    /**
     * 转办流程
     *
     * @author xuyuxiang
     * @date 2022/8/23 16:49
     **/
    void turn(FlwProcessTurnParam flwProcessTurnParam);

    /**
     * 跳转流程
     *
     * @author xuyuxiang
     * @date 2022/8/23 16:49
     **/
    void jump(FlwProcessJumpParam flwProcessJumpParam);

    /**
     * 复活流程
     *
     * @author xuyuxiang
     * @date 2022/8/23 16:49
     **/
    void restart(FlwProcessRestartParam flwProcessRestartParam);

    /**
     * 迁移流程
     *
     * @author xuyuxiang
     * @date 2022/8/23 16:49
     **/
    void migrate(FlwProcessMigrateParam flwProcessMigrateParam);

    /**
     * 获取流程变量分页
     *
     * @author xuyuxiang
     * @date 2022/6/30 13:29
     **/
    Page<FlwProcessVariableResult> variablePage(FlwProcessIdParam flwProcessIdParam);

    /**
     * 批量编辑流程变量
     *
     * @author xuyuxiang
     * @date 2022/6/30 13:29
     **/
    void variableUpdateBatch(FlwProcessVariableUpdateParam flwProcessVariableUpdateParam);

    /**
     * 流程详情
     *
     * @author xuyuxiang
     * @date 2022/8/23 15:12
     **/
    FlwProcessDetailResult detail(FlwProcessIdParam flwProcessIdParam);

    /**
     * 获取流程实例的模型JSON对象变量
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    JSONObject getProcessModelInfoVariable(String processInstanceId);

    /**
     * 获取流程实例的模型Id变量
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    String getProcessModelIdVariable(String processInstanceId);

    /**
     * 获取流程实例的流程JSON字符串变量
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    String getProcessModelJsonVariable(String processInstanceId);

    /**
     * 获取流程实例的流程JSON节点变量
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    FlwNode getProcessModelJsonNodeVariable(String processInstanceId);

    /**
     * 获取流程实例的表单JSON变量
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    String getProcessFormJsonVariable(String processInstanceId);

    /**
     * 获取流程实例的模型JSON变量
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    String getProcessFormTypeVariable(String processInstanceId);

    /**
     * 获取流程实例最新模型
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    FlwModel getProcessLastModel(String processInstanceId);

    /**
     * 获取流程实例的定义数据配置信息
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    FlwNode.FlwNodeConfigProp getProcessNodeConfigProp(String processInstanceId);

    /**
     * 获取流程实例的数据JSON变量
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:48
     **/
    String getProcessDataJsonVariable(String processInstanceId);

    /**
     * 执行抄送
     *
     * @author xuyuxiang
     * @date 2023/5/11 14:14
     **/
    void doCopy(String processInstanceId, List<String> userIdList);

    /**
     * 获取抄送给我的待阅流程id集合
     *
     * @author xuyuxiang
     * @date 2023/5/11 13:55
     **/
    List<String> getMyCopyUnreadProcessInstanceIdList();

    /**
     * 设置待阅流程为已阅
     *
     * @author xuyuxiang
     * @date 2023/5/11 14:38
     **/
    void readMyCopyProcess(List<FlwProcessIdParam> flwProcessIdParamList);

    /**
     * 获取抄送给我的已阅流程id集合
     *
     * @author xuyuxiang
     * @date 2023/5/11 13:55
     **/
    List<String> getMyCopyHasReadProcessInstanceIdList();

    /**
     * 删除我的已阅流程
     *
     * @author xuyuxiang
     * @date 2023/5/11 14:49
     **/
    void deleteMyHasReadProcess(List<FlwProcessIdParam> flwProcessIdParamList);

    /**
     * 获取可复活到节点列表
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:30
     **/
    List<FlwProcessJumpNodeResult> getCanJumpNodeInfoList(FlwProcessIdParam flwProcessIdParam);

    /**
     * 获取可复活到节点列表
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:30
     **/
    List<FlwProcessRestartNodeResult> getCanRestartNodeInfoList(FlwProcessIdParam flwProcessIdParam);

    /**
     * 获取可迁移到节点列表
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:30
     **/
    List<FlwProcessRestartNodeResult> getCanMigrateNodeInfoList(FlwProcessIdParam flwProcessIdParam);

    /**
     * 执行流程实例修改
     *
     * @author xuyuxiang
     * @date 2023/5/23 18:13
     **/
    void doProcessInstanceModify(String processInstanceId, UserTask userTask);

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2023/5/27 13:57
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2023/5/27 13:57
     */
    Page<FlwProcessUserResult> userSelector(FlwProcessSelectorUserParam flwProcessSelectorUserParam);

    /**
     * 获取流程模型详情
     *
     * @author xuyuxiang
     * @date 2023/8/28 10:04
     **/
    FlwModel modelDetail(FlwProcessModelIdParam flwProcessModelIdParam);
}
