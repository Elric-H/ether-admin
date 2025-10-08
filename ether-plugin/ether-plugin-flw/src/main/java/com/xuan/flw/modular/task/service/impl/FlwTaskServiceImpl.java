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
package com.xuan.flw.modular.task.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.runtime.ProcessInstanceModificationBuilder;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xuan.common.exception.CommonException;
import com.xuan.common.page.CommonPageRequest;
import com.xuan.flw.core.enums.NodeApproveOperateTypeEnum;
import com.xuan.flw.core.listener.FlwGlobalCustomEventCenter;
import com.xuan.flw.core.node.FlwNode;
import com.xuan.flw.core.util.NodeInfoUtil;
import com.xuan.flw.core.util.NodeRuntimeUtil;
import com.xuan.flw.modular.process.param.FlwProcessIdParam;
import com.xuan.flw.modular.process.result.FlwProcessDetailResult;
import com.xuan.flw.modular.process.service.FlwProcessService;
import com.xuan.flw.modular.task.param.*;
import com.xuan.flw.modular.task.result.*;
import com.xuan.flw.modular.task.service.FlwTaskService;
import com.xuan.sys.api.SysOrgApi;
import com.xuan.sys.api.SysUserApi;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.camunda.bpm.engine.task.IdentityLinkType.ASSIGNEE;
import static org.camunda.bpm.engine.task.IdentityLinkType.CANDIDATE;

/**
 * 任务Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/5/22 15:33
 */
@Service
public class FlwTaskServiceImpl implements FlwTaskService {

    @Resource
    private SysOrgApi sysOrgApi;

    @Resource
    private SysUserApi sysUserApi;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private FlwProcessService flwProcessService;

    @Override
    public Page<FlwTodoTaskResult> todoPage(FlwTaskPageTodoParam flwTaskPageTodoParam) {
        NodeRuntimeUtil.handleTenAuth();
        TaskQuery taskQuery = taskService.createTaskQuery().active().taskAssignee(StpUtil.getLoginIdAsString()).orderByTaskCreateTime().desc();
        if(ObjectUtil.isNotEmpty(flwTaskPageTodoParam.getName())) {
            taskQuery.processDefinitionName(flwTaskPageTodoParam.getName());
        }
        if(ObjectUtil.isNotEmpty(flwTaskPageTodoParam.getInitiator())) {
            taskQuery.processVariableValueEquals("initiator", flwTaskPageTodoParam.getInitiator());
        }
        if (ObjectUtil.isNotEmpty(flwTaskPageTodoParam.getSearchKey())) {
            taskQuery.processVariableValueLike("initiatorName", "%" + flwTaskPageTodoParam.getSearchKey() + "%");
        }
        Page<FlwTodoTaskResult> defaultPage = CommonPageRequest.defaultPage();
        long current = defaultPage.getCurrent();
        long size = defaultPage.getSize();
        List<FlwTodoTaskResult> flwTodoTaskResultList = taskQuery.listPage(Convert.toInt((current - 1) * size), Convert.toInt(size)).stream()
                .map(task -> {
                    JSONObject variableJsonObject = JSONUtil.createObj();
                    historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list()
                            .forEach(historicVariableInstance -> variableJsonObject.set(historicVariableInstance.getName(),
                                    historicVariableInstance.getValue()));
                    FlwTodoTaskResult flwTodoTaskResult = new FlwTodoTaskResult();
                    flwTodoTaskResult.setId(task.getId());
                    flwTodoTaskResult.setProcessInstanceId(task.getProcessInstanceId());
                    flwTodoTaskResult.setSn(variableJsonObject.getStr("initiatorProcessSn"));
                    flwTodoTaskResult.setTitle(variableJsonObject.getStr("initiatorProcessTitle"));
                    flwTodoTaskResult.setAbstractTitle(variableJsonObject.getStr("initiatorProcessAbstract"));
                    flwTodoTaskResult.setProcessDefinitionId(task.getProcessDefinitionId());
                    flwTodoTaskResult.setCurrentActivityId(task.getTaskDefinitionKey());
                    flwTodoTaskResult.setCurrentActivityName(task.getName());
                    String taskAssignee = task.getAssignee();
                    if(ObjectUtil.isNotEmpty(taskAssignee)) {
                        JSONObject taskAssigneeUser = sysUserApi.getUserByIdWithoutException(taskAssignee);
                        if(ObjectUtil.isNotEmpty(taskAssigneeUser)) {
                            flwTodoTaskResult.setAssignees(taskAssigneeUser.getStr("name"));
                        }
                    } else {
                        String assignee = variableJsonObject.getStr("assignee" + StrUtil.UNDERLINE + task.getTaskDefinitionKey());
                        if(ObjectUtil.isNotEmpty(assignee)) {
                            List<String> userIdList = JSONUtil.toList(assignee, String.class);
                            flwTodoTaskResult.setAssignees(StrUtil.join("、", sysUserApi.getUserListByIdListWithoutException(userIdList).stream()
                                    .map(jsonObject -> jsonObject.getStr("name")).collect(Collectors.toList())));
                        }
                    }
                    flwTodoTaskResult.setInitiator(variableJsonObject.getStr("initiator"));
                    flwTodoTaskResult.setInitiatorName(variableJsonObject.getStr("initiatorName"));
                    flwTodoTaskResult.setInitiatorOrgId(variableJsonObject.getStr("initiatorOrgId"));
                    flwTodoTaskResult.setInitiatorOrgName(variableJsonObject.getStr("initiatorOrgName"));
                    flwTodoTaskResult.setInitiatorPositionId(variableJsonObject.getStr("initiatorPositionId"));
                    flwTodoTaskResult.setInitiatorPositionName(variableJsonObject.getStr("initiatorPositionName"));
                    flwTodoTaskResult.setInitiatorTime(variableJsonObject.getStr("initiatorTime"));
                    flwTodoTaskResult.setTenantId(task.getTenantId());
                    return flwTodoTaskResult;
                }).collect(Collectors.toList());
        defaultPage.setTotal(taskQuery.count());
        defaultPage.setRecords(flwTodoTaskResultList);
        return defaultPage;
    }

    @Override
    public Page<FlwDoneTaskResult> donePage(FlwTaskPageDoneParam flwTaskPageDoneParam) {
        NodeRuntimeUtil.handleTenAuth();
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().finished().taskAssignee(StpUtil.getLoginIdAsString())
                .orderByHistoricTaskInstanceEndTime().desc();
        if(ObjectUtil.isNotEmpty(flwTaskPageDoneParam.getName())) {
            historicTaskInstanceQuery.processDefinitionName(flwTaskPageDoneParam.getName());
        }
        if(ObjectUtil.isNotEmpty(flwTaskPageDoneParam.getInitiator())) {
            historicTaskInstanceQuery.processVariableValueEquals("initiator", flwTaskPageDoneParam.getInitiator());
        }
        if (ObjectUtil.isNotEmpty(flwTaskPageDoneParam.getSearchKey())) {
            historicTaskInstanceQuery.processVariableValueLike("initiatorName", "%" + flwTaskPageDoneParam.getSearchKey() + "%");
        }
        Page<FlwDoneTaskResult> defaultPage = CommonPageRequest.defaultPage();
        long current = defaultPage.getCurrent();
        long size = defaultPage.getSize();
        List<FlwDoneTaskResult> flwDoneTaskResultList = historicTaskInstanceQuery.listPage(Convert.toInt((current - 1) * size), Convert.toInt(size)).stream()
                .map(historicTaskInstance -> {
                    JSONObject variableJsonObject = JSONUtil.createObj();
                    historyService.createHistoricVariableInstanceQuery().processInstanceId(historicTaskInstance.getProcessInstanceId()).list()
                            .forEach(historicVariableInstance -> variableJsonObject.set(historicVariableInstance.getName(),
                                    historicVariableInstance.getValue()));
                    FlwDoneTaskResult flwDoneTaskResult = new FlwDoneTaskResult();
                    flwDoneTaskResult.setId(historicTaskInstance.getId());
                    flwDoneTaskResult.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
                    flwDoneTaskResult.setSn(variableJsonObject.getStr("initiatorProcessSn"));
                    flwDoneTaskResult.setTitle(variableJsonObject.getStr("initiatorProcessTitle"));
                    flwDoneTaskResult.setAbstractTitle(variableJsonObject.getStr("initiatorProcessAbstract"));
                    flwDoneTaskResult.setProcessDefinitionId(historicTaskInstance.getProcessDefinitionId());
                    flwDoneTaskResult.setDoneActivityId(historicTaskInstance.getTaskDefinitionKey());
                    flwDoneTaskResult.setDoneActivityName(historicTaskInstance.getName());
                    flwDoneTaskResult.setDoneTime(DateUtil.formatDateTime(historicTaskInstance.getEndTime()));
                    flwDoneTaskResult.setInitiator(variableJsonObject.getStr("initiator"));
                    flwDoneTaskResult.setInitiatorName(variableJsonObject.getStr("initiatorName"));
                    flwDoneTaskResult.setInitiatorOrgId(variableJsonObject.getStr("initiatorOrgId"));
                    flwDoneTaskResult.setInitiatorOrgName(variableJsonObject.getStr("initiatorOrgName"));
                    flwDoneTaskResult.setInitiatorPositionId(variableJsonObject.getStr("initiatorPositionId"));
                    flwDoneTaskResult.setInitiatorPositionName(variableJsonObject.getStr("initiatorPositionName"));
                    flwDoneTaskResult.setInitiatorTime(variableJsonObject.getStr("initiatorTime"));
                    flwDoneTaskResult.setTenantId(historicTaskInstance.getTenantId());
                    HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(flwDoneTaskResult.getProcessInstanceId()).singleResult();
                    String state = historicProcessInstance.getState();
                    switch (state) {
                        case "ACTIVE":
                            flwDoneTaskResult.setStateCode(state);
                            flwDoneTaskResult.setStateText("审批中");
                            break;
                        case "SUSPENDED":
                            flwDoneTaskResult.setStateCode(state);
                            flwDoneTaskResult.setStateText("已挂起");
                            break;
                        case "COMPLETED":
                            flwDoneTaskResult.setStateCode(state);
                            flwDoneTaskResult.setStateText("已完成");
                            break;
                        case "EXTERNALLY_TERMINATED":
                            String deleteReason = historicProcessInstance.getDeleteReason();
                            switch (deleteReason) {
                                case "END":
                                    flwDoneTaskResult.setStateCode(deleteReason);
                                    flwDoneTaskResult.setStateText("已终止");
                                    break;
                                case "REVOKE":
                                    flwDoneTaskResult.setStateCode(deleteReason);
                                    flwDoneTaskResult.setStateText("已撤回");
                                    break;
                                case "REJECT":
                                    flwDoneTaskResult.setStateCode(deleteReason);
                                    flwDoneTaskResult.setStateText("已拒绝");
                                    break;
                                default:
                                    throw new CommonException("流程实例状态错误，id值为：{}", historicProcessInstance.getId());
                            }
                            break;
                        case "INTERNALLY_TERMINATED":
                            flwDoneTaskResult.setStateCode(state);
                            flwDoneTaskResult.setStateText("内部终止");
                            break;
                        default:
                            throw new CommonException("流程实例状态错误，id值为：{}", historicProcessInstance.getId());
                    }
                    return flwDoneTaskResult;
                }).collect(Collectors.toList());
        defaultPage.setTotal(historicTaskInstanceQuery.count());
        defaultPage.setRecords(flwDoneTaskResultList);
        return defaultPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void adjust(FlwTaskAdjustParam flwTaskAdjustParam) {
        NodeRuntimeUtil.handleTenAuth();
        String taskId = flwTaskAdjustParam.getId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(ObjectUtil.isEmpty(task)) {
            throw new CommonException("任务不存在，id值为：{}", taskId);
        }
        NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, task.getProcessInstanceId(), NodeApproveOperateTypeEnum.RESTART.getValue(),
                "重新提交", null, CollectionUtil.newArrayList());
        // 更新sql
        handleDataJson(task.getProcessInstanceId(), task.getProcessDefinitionId(), flwTaskAdjustParam.getDataJson());

        // 执行调整
        taskService.complete(task.getId());
    }

    @Override
    public void save(FlwTaskSaveParam flwTaskSaveParam) {
        NodeRuntimeUtil.handleTenAuth();
        String taskId = flwTaskSaveParam.getId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(ObjectUtil.isEmpty(task)) {
            throw new CommonException("任务不存在，id值为：{}", taskId);
        }
        String processInstanceId = task.getProcessInstanceId();
        runtimeService.setVariables(processInstanceId, JSONUtil.createObj().set("initiatorDataJson", flwTaskSaveParam.getDataJson()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pass(FlwTaskPassParam flwTaskPassParam) {
        Task task = validTask(flwTaskPassParam.getId(), flwTaskPassParam.getComment());
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, processInstanceId, NodeApproveOperateTypeEnum.PASS.getValue(), "同意申请",
                flwTaskPassParam.getComment(), flwTaskPassParam.getAttachmentList());
        // 更新sql
        handleDataJson(task.getProcessInstanceId(), task.getProcessDefinitionId(), flwTaskPassParam.getDataJson());

        // 执行通过
        taskService.complete(task.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reject(FlwTaskRejectParam flwTaskRejectParam) {
        Task task = validTask(flwTaskRejectParam.getId(), flwTaskRejectParam.getComment());
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, processInstanceId, NodeApproveOperateTypeEnum.REJECT.getValue(), "拒绝申请" ,
                flwTaskRejectParam.getComment(), flwTaskRejectParam.getAttachmentList());
        // 更新sql
        handleDataJson(task.getProcessInstanceId(), task.getProcessDefinitionId(), flwTaskRejectParam.getDataJson());
        // 发布拒绝事件
        FlwGlobalCustomEventCenter.publishRejectEvent(processInstanceId);
        // 执行拒绝
        runtimeService.deleteProcessInstancesIfExists(CollectionUtil.newArrayList(processInstanceId), "REJECT",
                true, true, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void back(FlwTaskBackParam flwTaskBackParam) {
        Task task = validTask(flwTaskBackParam.getId(), flwTaskBackParam.getComment());
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String processDefinitionId = task.getProcessDefinitionId();
        AtomicReference<String> targetActivityId = new AtomicReference<>(new ArrayList<>(repositoryService.getBpmnModelInstance(processDefinitionId)
                .getModelElementById(taskDefinitionKey).getParentElement().getChildElementsByType(StartEvent.class)).get(0).getId());
        AtomicBoolean backToStart = new AtomicBoolean(true);
        UserTask userTask = repositoryService.getBpmnModelInstance(processDefinitionId).getModelElementById(taskDefinitionKey);
        ExtensionElements extensionElements = userTask.getExtensionElements();
        if (ObjectUtil.isNotEmpty(extensionElements)) {
            extensionElements.getChildElementsByType(CamundaProperties.class).forEach(camundaProperties ->
                    camundaProperties.getChildElementsByType(CamundaProperty.class).forEach(camundaProperty -> {
                        if (camundaProperty.getCamundaName().equals("configInfo")) {
                            JSONObject configInfo = JSONUtil.parseObj(camundaProperty.getCamundaValue());
                            String userTaskRejectType = configInfo.getStr("userTaskRejectType");
                            if(ObjectUtil.isNotEmpty(userTaskRejectType)) {
                                switch (userTaskRejectType) {
                                    case "AUTO_END":
                                        targetActivityId.set("-1");
                                        break;
                                    case "TO_START":
                                        NodeRuntimeUtil.handleUserTaskBackNotice((DelegateTask) task);
                                        break;
                                    case "USER_SELECT":
                                        if (ObjectUtil.isEmpty(flwTaskBackParam.getTargetActivityId())) {
                                            throw new CommonException("退回目标节点id不能为空，任务id为：{}", taskId);
                                        } else {
                                            backToStart.set(false);
                                            targetActivityId.set(flwTaskBackParam.getTargetActivityId());
                                        }
                                        break;
                                    default:
                                        throw new CommonException("不支持的任务节点退回类型：{}", task.getProcessInstanceId());
                                }
                            }
                        }
                    }));
        } else {
            throw new CommonException("数据错误，流程模型扩展属性不存在，id值为：{}", processInstanceId);
        }
        // 更新sql
        handleDataJson(processInstanceId, task.getProcessDefinitionId(), flwTaskBackParam.getDataJson());
        // 直接结束
        if(targetActivityId.get().equals("-1")) {
            // 添加审批记录
            NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, processInstanceId, NodeApproveOperateTypeEnum.BACK.getValue(), "退回结束" ,
                    flwTaskBackParam.getComment(), flwTaskBackParam.getAttachmentList());
            // 发布拒绝事件
            FlwGlobalCustomEventCenter.publishRejectEvent(processInstanceId);
            // 执行拒绝
            runtimeService.deleteProcessInstancesIfExists(CollectionUtil.newArrayList(processInstanceId), "REJECT",
                    true, true, true);
        } else {
            // 创建流程实体修改构造器
            ProcessInstanceModificationBuilder processInstanceModificationBuilder = runtimeService
                    .createProcessInstanceModification(processInstanceId);
            // 取消当前所有正在执行的节点
            taskService.createTaskQuery().processInstanceId(processInstanceId).list().stream().map(Task::getTaskDefinitionKey)
                    .collect(Collectors.toSet()).forEach(processInstanceModificationBuilder::cancelAllForActivity);
            // 是否跳转到发起节点
            if(backToStart.get()) {
                // 添加审批记录
                NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, processInstanceId, NodeApproveOperateTypeEnum.BACK.getValue(),
                        "退回节点", "退回到发起节点：" + flwTaskBackParam.getComment(), flwTaskBackParam.getAttachmentList());
                // 执行跳转
                processInstanceModificationBuilder.startAfterActivity(targetActivityId.get()).execute();
            } else {
                UserTask backUserTask = repositoryService.getBpmnModelInstance(processDefinitionId).getModelElementById(targetActivityId.get());
                // 添加审批记录
                NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, processInstanceId, NodeApproveOperateTypeEnum.BACK.getValue(),
                        "退回节点", "退回到【" + backUserTask.getName() + "】节点：" + flwTaskBackParam.getComment(),
                        flwTaskBackParam.getAttachmentList());
                // 执行跳转
                Collection<SequenceFlow> incoming = backUserTask.getIncoming();
                if(incoming.size() != 1) {
                    throw new CommonException("流程模型数据错误，节点{}入口存在多个", userTask.getName());
                }
                processInstanceModificationBuilder.startTransition(incoming.iterator().next().getId()).execute();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void turn(FlwTaskTurnParam flwTaskTurnParam, boolean validComment) {
        Task task;
        if(validComment) {
            task = validTask(flwTaskTurnParam.getId(), flwTaskTurnParam.getComment());
        } else {
            NodeRuntimeUtil.handleTenAuth();
            task = taskService.createTaskQuery().taskId(flwTaskTurnParam.getId()).singleResult();
            if(ObjectUtil.isEmpty(task)) {
                throw new CommonException("任务不存在，id值为：{}", flwTaskTurnParam.getId());
            }
        }
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        if(flwTaskTurnParam.isRecordComment()) {
            NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, processInstanceId, NodeApproveOperateTypeEnum.TURN.getValue(),
                    "转办任务", "转办该任务给【" + sysUserApi.getUserByIdWithException(flwTaskTurnParam.getUserId()).getStr("name") + "】："
                            + flwTaskTurnParam.getComment(), flwTaskTurnParam.getAttachmentList());
        }
        // 更新sql
        handleDataJson(task.getProcessInstanceId(), task.getProcessDefinitionId(), flwTaskTurnParam.getDataJson());
        // 删除其办理人及候选人候选组
        taskService.getIdentityLinksForTask(taskId).forEach(identityLink -> {
            //获取人员类型
            String type = identityLink.getType();
            //删除其待办人
            if (ASSIGNEE.equals(type)) {
                String userId = identityLink.getUserId();
                if (ObjectUtil.isNotEmpty(userId)) {
                    taskService.deleteUserIdentityLink(taskId, null, type);
                }
            }
            //删除其候选人与候选组
            if (CANDIDATE.equals(type)) {
                String groupId = identityLink.getGroupId();
                if (ObjectUtil.isNotEmpty(groupId)) {
                    taskService.deleteCandidateGroup(taskId, groupId);
                }
                String userId = identityLink.getUserId();
                if (ObjectUtil.isNotEmpty(userId)) {
                    taskService.deleteCandidateUser(taskId, userId);
                }
            }
        });

        // 执行转办
        taskService.setAssignee(taskId, flwTaskTurnParam.getUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void jump(FlwTaskJumpParam flwTaskJumpParam) {
        Task task = validTask(flwTaskJumpParam.getId(), flwTaskJumpParam.getComment());
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        String processDefinitionId = task.getProcessDefinitionId();
        String targetActivityId = flwTaskJumpParam.getTargetActivityId();
        UserTask jumpUserTask = repositoryService.getBpmnModelInstance(processDefinitionId).getModelElementById(targetActivityId);
        NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, processInstanceId, NodeApproveOperateTypeEnum.JUMP.getValue(),
                "跳转节点", "跳转到【" + jumpUserTask.getName() + "】节点：" + flwTaskJumpParam.getComment(), flwTaskJumpParam.getAttachmentList());
        // 更新sql
        handleDataJson(task.getProcessInstanceId(), task.getProcessDefinitionId(), flwTaskJumpParam.getDataJson());
        // 执行跳转
        flwProcessService.doProcessInstanceModify(processInstanceId, jumpUserTask);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addSign(FlwTaskAddSignParam flwTaskAddSignParam) {
        Task task = validTask(flwTaskAddSignParam.getId(), flwTaskAddSignParam.getComment());
        String currentActivityId = flwTaskAddSignParam.getCurrentActivityId();
        UserTask currentUserTask = repositoryService.getBpmnModelInstance(task.getProcessDefinitionId()).getModelElementById(currentActivityId);
        ModelElementInstance currentMultiInstanceLoopCharacteristics = currentUserTask.getUniqueChildElementByType(MultiInstanceLoopCharacteristics.class);
        if(ObjectUtil.isEmpty(currentMultiInstanceLoopCharacteristics)) {
            throw new CommonException("模型数据错误，当前节点：{}多实例属性为空", currentUserTask.getName());
        }
        String currentIsSequential = currentMultiInstanceLoopCharacteristics.getAttributeValue("isSequential");
        if(ObjectUtil.isEmpty(currentIsSequential)) {
            throw new CommonException("模型数据错误，当前节点：{}多实例属性为空", currentUserTask.getName());
        }
        Boolean sequential = Convert.toBool(currentIsSequential);
        if(sequential) {
            throw new CommonException("当前节点：{}的节点类型不支持加签", currentUserTask.getName());
        }
        List<String> assigneeList = flwTaskAddSignParam.getAssigneeList();
        List<String> existAssigneeList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(flwTaskAddSignParam
                .getCurrentActivityId()).list().stream().map(HistoricTaskInstance::getAssignee).collect(Collectors.toList());
        Collection<String> intersection = CollectionUtil.intersection(assigneeList, existAssigneeList);
        if(ObjectUtil.isNotEmpty(intersection)) {
            String existAssigneeNames = StrUtil.join("、", sysUserApi.getUserListByIdListWithoutException(CollectionUtil
                    .newArrayList(intersection)).stream().map(jsonObject -> jsonObject.getStr("name")).collect(Collectors.toList()));
            throw new CommonException("被加签人员：{}已存在该任务中", existAssigneeNames);
        }
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        String assigneeNames = StrUtil.join("、", sysUserApi.getUserListByIdListWithoutException(assigneeList).stream()
                .map(jsonObject -> jsonObject.getStr("name")).collect(Collectors.toList()));
        NodeRuntimeUtil.handleUserTaskCommentAndAttachment(taskId, processInstanceId, NodeApproveOperateTypeEnum.ADD_SIGN.getValue(),
                "人员加签", "加签人员：【" + assigneeNames + "】:" + flwTaskAddSignParam.getComment(), flwTaskAddSignParam.getAttachmentList());
        assigneeList.forEach(assignee -> runtimeService.createProcessInstanceModification(task.getProcessInstanceId())
                .startBeforeActivity(currentActivityId)
                .setVariable("assignee",assignee)
                .execute());
    }

    @Override
    public FlwTaskDetailResult detail(FlwTaskIdParam flwTaskIdParam) {
        FlwTaskDetailResult flwTaskDetailResult = new FlwTaskDetailResult();
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(flwTaskIdParam.getId()).singleResult();
        FlwProcessIdParam flwProcessIdParam = new FlwProcessIdParam();
        flwProcessIdParam.setId(historicTaskInstance.getProcessInstanceId());
        FlwProcessDetailResult flwProcessDetailResult = flwProcessService.detail(flwProcessIdParam);
        BeanUtil.copyProperties(flwProcessDetailResult, flwTaskDetailResult);
        flwTaskDetailResult.setTaskId(historicTaskInstance.getId());
        return flwTaskDetailResult;
    }


    @Override
    public List<FlwBackNodeResult> getCanBackNodeInfoList(FlwTaskGetBackNodeInfoParam flwTaskGetBackNodeInfoParam) {
        return historyService.createHistoricActivityInstanceQuery().processInstanceId(flwTaskGetBackNodeInfoParam.getProcessInstanceId())
                .finished().activityType(BpmnModelConstants.BPMN_ELEMENT_USER_TASK).orderByHistoricActivityInstanceStartTime().asc()
                .list().stream().filter(historicActivityInstance -> !historicActivityInstance.getActivityName().equals("EMPTY") &&
                        !historicActivityInstance.getActivityId().equals(flwTaskGetBackNodeInfoParam.getCurrentActivityId()))
                .map(historicActivityInstance -> {
                    FlwBackNodeResult flwBackNodeResult = new FlwBackNodeResult();
                    flwBackNodeResult.setId(historicActivityInstance.getActivityId());
                    flwBackNodeResult.setName(historicActivityInstance.getActivityName());
                    return flwBackNodeResult;
                }).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator
                                .comparing(FlwBackNodeResult::getId))), ArrayList::new));
    }

    @Override
    public List<FlwJumpNodeResult> getCanJumpNodeInfoList(FlwTaskGetJumpNodeInfoParam flwTaskGetJumpNodeInfoParam) {
        FlwNode flwNode = flwProcessService.getProcessModelJsonNodeVariable(flwTaskGetJumpNodeInfoParam.getProcessInstanceId());
        return NodeInfoUtil.getUserTaskFlwNodeList(flwNode).stream().filter(tempFlwNode -> !tempFlwNode.getId()
                .equals(flwTaskGetJumpNodeInfoParam.getCurrentActivityId())).map(tempFlwNode -> {
                    FlwJumpNodeResult flwJumpNodeResult = new FlwJumpNodeResult();
                    flwJumpNodeResult.setId(tempFlwNode.getId());
                    flwJumpNodeResult.setName(tempFlwNode.getTitle());
                    return flwJumpNodeResult;
                }).collect(Collectors.toList());
    }

    @Override
    public List<Tree<String>> orgTreeSelector() {
        return sysOrgApi.orgTreeSelector();
    }

    @SuppressWarnings("ALL")
    @Override
    public Page<FlwTaskUserResult> userSelector(FlwTaskSelectorUserParam flwTaskSelectorUserParam) {
        return BeanUtil.toBean(sysUserApi.userSelector(flwTaskSelectorUserParam.getOrgId(), flwTaskSelectorUserParam.getSearchKey()), Page.class);
    }

    /**
     * 校验任务
     *
     * @author xuyuxiang
     * @date 2023/5/17 21:54
     */
    public Task validTask(String taskId, String comment) {
        NodeRuntimeUtil.handleTenAuth();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(ObjectUtil.isEmpty(task)) {
            throw new CommonException("任务不存在，id值为：{}", taskId);
        }
        String processInstanceId = task.getProcessInstanceId();
        FlwNode.FlwNodeConfigProp flwNodeConfigProp = flwProcessService.getProcessNodeConfigProp(processInstanceId);
        if(ObjectUtil.isEmpty(comment)) {
            Boolean enableCommentRequired = flwNodeConfigProp.getProcessEnableCommentRequired();
            if(enableCommentRequired) {
                throw new CommonException("该流程已配置意见必填，id值为：{}，请填写意见", processInstanceId);
            }
        }
        return task;
    }

    /**
     * 处理审批时的表单数据
     *
     * @author xuyuxiang
     * @date 2023/5/22 22:26
     */
    public void handleDataJson(String processInstanceId,String processDefinitionId, String dataJson) {
        if(ObjectUtil.isNotEmpty(dataJson) && !JSONUtil.parseObj(dataJson).isEmpty()) {
            Object initiatorTableJson = runtimeService.getVariables(processInstanceId).get("initiatorTableJson");
            if(ObjectUtil.isNotEmpty(initiatorTableJson)) {
                JSONObject resultDataObj = NodeRuntimeUtil.handleDataJson(Convert.toStr(initiatorTableJson), dataJson,
                        historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
                                .singleResult().getBusinessKey());
                String updateDataSql = resultDataObj.getStr("sql");
                NodeRuntimeUtil.handleSqlExecution(updateDataSql);
            }
            runtimeService.setVariables(processInstanceId, JSONUtil.createObj().set("initiatorDataJson", dataJson));
            NodeRuntimeUtil.handleTableDataVariables(processInstanceId, initiatorTableJson, dataJson);
            NodeRuntimeUtil.handleUserTaskWithFormUserParticipateInfo(processInstanceId, processDefinitionId, dataJson);
        }
    }
}
