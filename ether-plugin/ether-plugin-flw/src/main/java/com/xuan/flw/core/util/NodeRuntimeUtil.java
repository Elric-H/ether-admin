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
package com.xuan.flw.core.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.wnameless.json.flattener.JsonFlattener;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import com.xuan.auth.core.pojo.SaBaseLoginUser;
import com.xuan.auth.core.util.StpLoginUserUtil;
import com.xuan.common.exception.CommonException;
import com.xuan.dbs.api.DbsApi;
import com.xuan.dev.api.DevEmailApi;
import com.xuan.dev.api.DevMessageApi;
import com.xuan.flw.core.enums.NodeApproveOperateTypeEnum;
import com.xuan.flw.core.listener.FlwGlobalCustomEventCenter;
import com.xuan.flw.core.node.FlwNode;
import com.xuan.flw.modular.task.param.FlwTaskAttachmentParam;
import com.xuan.flw.modular.template.service.FlwTemplateSnService;
import com.xuan.sys.api.SysOrgApi;
import com.xuan.sys.api.SysRelationApi;
import com.xuan.sys.api.SysUserApi;
import com.xuan.ten.api.TenApi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 节点运行时工具类
 *
 * @author xuyuxiang
 * @date 2022/6/2 16:21
 **/
@Slf4j
public class NodeRuntimeUtil {

    /** 删除标志 */
    private static final String DELETE_FLAG = "DELETE_FLAG";

    /** 创建人 */
    private static final String CREATE_USER = "CREATE_USER";

    /** 创建时间 */
    private static final String CREATE_TIME = "CREATE_TIME";

    /** 更新人 */
    private static final String UPDATE_USER = "UPDATE_USER";

    /** 更新时间 */
    private static final String UPDATE_TIME = "UPDATE_TIME";

    /**
     * 流程主表字段设置为参数
     *
     * @author xuyuxiang
     * @date 2022/8/26 15:11
     **/
    public static void handleTableDataVariables(DelegateExecution delegateExecution) {
        Object initiatorDataJson = delegateExecution.getVariable("initiatorDataJson");
        Object initiatorTableJson = delegateExecution.getVariable("initiatorTableJson");
        if(ObjectUtil.isNotEmpty(initiatorDataJson)) {
            if(ObjectUtil.isNotEmpty(initiatorTableJson)) {
                String primaryTableName = JSONUtil.parseObj(JSONUtil.parseArray(initiatorTableJson)
                        .stream().filter(obj -> JSONUtil.parseObj(obj).getStr("tableType").equals("parent"))
                        .collect(Collectors.toList()).get(0)).getStr("tableName");
                JSONUtil.parseObj(initiatorDataJson).getJSONObject(primaryTableName).forEach((key, value) ->
                        delegateExecution.setVariable(StrUtil.toCamelCase(primaryTableName.toLowerCase()) +
                                StrUtil.UNDERLINE + StrUtil.toCamelCase(key.toLowerCase()), value));
            } else {
                JsonFlattener.flattenAsMap(Convert.toStr(initiatorDataJson)).forEach((key, value) ->
                        delegateExecution.setVariable(StrUtil.replace(key, StrUtil.DOT, StrUtil.UNDERLINE), value));
            }
        }
    }

    /**
     * 流程主表字段设置为参数
     *
     * @author xuyuxiang
     * @date 2022/8/26 15:11
     **/
    public static void handleTableDataVariables(String processInstanceId, Object initiatorTableJson, Object initiatorDataJson) {
        if(ObjectUtil.isNotEmpty(initiatorDataJson)) {
            RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
            if(ObjectUtil.isNotEmpty(initiatorTableJson)) {
                String primaryTableName = JSONUtil.parseObj(JSONUtil.parseArray(initiatorTableJson)
                        .stream().filter(obj -> JSONUtil.parseObj(obj).getStr("tableType").equals("parent"))
                        .collect(Collectors.toList()).get(0)).getStr("tableName");
                JSONUtil.parseObj(initiatorDataJson).getJSONObject(primaryTableName).forEach((key, value) ->
                        runtimeService.setVariable(processInstanceId, StrUtil.toCamelCase(primaryTableName.toLowerCase())
                                + StrUtil.UNDERLINE + StrUtil.toCamelCase(key.toLowerCase()), value));
            } else {
                JsonFlattener.flattenAsMap(Convert.toStr(initiatorDataJson)).forEach((key, value) -> {
                    if(key.contains(StrUtil.UNDERLINE)) {
                        key = StrUtil.toCamelCase(key.toLowerCase());
                    }
                    runtimeService.setVariable(processInstanceId, StrUtil.replace(key, StrUtil.DOT, StrUtil.UNDERLINE), value);
                });
            }
        }
    }

    /**
     * 设置标题与摘要
     *
     * @author xuyuxiang
     * @date 2022/6/13 16:40
     **/
    public static void handleTitleAndAbstract(DelegateExecution delegateExecution) {
        Collection<ExtensionElements> extensionElementsCollection = delegateExecution.getBpmnModelElementInstance().getParentElement()
                .getChildElementsByType(ExtensionElements.class);
        RepositoryService repositoryService = SpringUtil.getBean(RepositoryService.class);
        FlwTemplateSnService flwTemplateSnService = SpringUtil.getBean(FlwTemplateSnService.class);
        if(ObjectUtil.isNotEmpty(extensionElementsCollection)) {
            extensionElementsCollection.forEach(extensionElements ->
                extensionElements.getChildElementsByType(CamundaProperties.class).forEach(camundaProperties ->
                        camundaProperties.getChildElementsByType(CamundaProperty.class).forEach(camundaProperty -> {
                            if (camundaProperty.getCamundaName().equals("configInfo")) {
                                JSONObject configInfo = JSONUtil.parseObj(camundaProperty.getCamundaValue());

                                String processDefinitionId = delegateExecution.getProcessDefinitionId();
                                String processDefinitionName = repositoryService.createProcessDefinitionQuery()
                                        .processDefinitionId(processDefinitionId).singleResult().getName();

                                // 流水号模板id
                                String processSnTemplateId = configInfo.getStr("processSnTemplateId");

                                // 流水号内容
                                String processSnTemplate = flwTemplateSnService.getTemplateValueBydId(processSnTemplateId);

                                // 标题模板，发起人 initiator，发起时间：startTime，流程名称：processName，主表单内必填字段
                                String processTitleTemplate = configInfo.getStr("processTitleTemplate");

                                // 摘要模板，发起人 initiator，发起时间：startTime，流程名称：processName，主表单内必填字段
                                String processAbstractTemplate = configInfo.getStr("processAbstractTemplate");

                                // 获取发起人姓名
                                Object initiatorNameObj = delegateExecution.getVariable("initiatorName");
                                String initiatorName = "未知用户";
                                if(ObjectUtil.isNotEmpty(initiatorNameObj)) {
                                    initiatorName = Convert.toStr(initiatorNameObj);
                                }
                                AtomicReference<String> titleResult = new AtomicReference<>(StrUtil.replace(StrUtil.replace(StrUtil
                                                .replace(processTitleTemplate, "initiator", initiatorName),
                                        "startTime", DateUtil.now()), "processName", processDefinitionName));

                                AtomicReference<String> abstractResult = new AtomicReference<>(StrUtil.replace(StrUtil.replace(StrUtil
                                                 .replace(processAbstractTemplate, "initiator", initiatorName),
                                        "startTime", DateUtil.now()), "processName", processDefinitionName));

                                Object initiatorDataJson = delegateExecution.getVariable("initiatorDataJson");
                                Object initiatorTableJson = delegateExecution.getVariable("initiatorTableJson");
                                if(ObjectUtil.isNotEmpty(initiatorDataJson)) {
                                    if(ObjectUtil.isNotEmpty(initiatorTableJson)) {
                                        handleDataJsonPrimaryTableField(Convert.toStr(initiatorDataJson)).forEach((key, value) -> {
                                            titleResult.set(StrUtil.replace(titleResult.get(), key, Convert.toStr(value)));

                                            abstractResult.set(StrUtil.replace(abstractResult.get(), key,  Convert.toStr(value)));
                                        });
                                    } else {
                                        JSONUtil.parseObj(initiatorDataJson).forEach((key, value) -> {
                                            titleResult.set(StrUtil.replace(titleResult.get(), key, Convert.toStr(value)));
                                            abstractResult.set(StrUtil.replace(abstractResult.get(), key,  Convert.toStr(value)));
                                        });
                                    }
                                }
                                delegateExecution.setVariable("initiatorProcessSn", processSnTemplate);
                                delegateExecution.setVariable("initiatorProcessTitle", titleResult.get());
                                delegateExecution.setVariable("initiatorProcessAbstract", abstractResult.get());
                            }
                        })));
        }
    }

    /**
     * 给所有的用户任务节点设置办理人信息
     *
     * @author xuyuxiang
     * @date 2022/6/13 11:31
     **/
    public static void handleUserTaskParticipateInfo(DelegateExecution delegateExecution) {
        delegateExecution.getBpmnModelInstance().getModelElementsByType(UserTask.class).forEach(userTask -> {
            String userTaskParticipateInfo = getUserTaskParticipateInfo(userTask);
            if (ObjectUtil.isEmpty(userTaskParticipateInfo)) {
                // 是否用户任务（排除开始节点和开始任务）
                if(!isStartUserTask(delegateExecution)) {
                    handleUserTaskEmptyParticipateInfo(delegateExecution.getProcessInstanceId(), userTask);
                }
            } else {
                List<String> participateUserIdList = handleUserTaskParticipateInfo(delegateExecution.getProcessInstanceId(), userTaskParticipateInfo);
                if (ObjectUtil.isEmpty(participateUserIdList)) {
                    handleUserTaskEmptyParticipateInfo(delegateExecution.getProcessInstanceId(), userTask);
                } else {
                    delegateExecution.setVariable("assignee" + StrUtil.UNDERLINE + userTask.getId(), participateUserIdList);
                }
            }
        });
    }

    /**
     * 判断执行是否为发起申请
     *
     * @author xuyuxiang
     * @date 2022/6/9 19:52
     */
    public static boolean isStartUserTask(DelegateExecution delegateExecution) {
        FlowElement bpmnModelElementInstance = delegateExecution.getBpmnModelElementInstance();
        if(bpmnModelElementInstance instanceof StartEvent) {
            return true;
        }
        if(bpmnModelElementInstance instanceof UserTask) {
            UserTask userTask = (UserTask) bpmnModelElementInstance;
            return isStartUserTask(userTask);
        } else {
            return false;
        }
    }

    /**
     * 判断任务是否为发起申请
     *
     * @author xuyuxiang
     * @date 2022/6/9 19:52
     */
    public static boolean isStartUserTask(UserTask userTask) {
        List<FlowNode> flowNodeList = userTask.getPreviousNodes().list();
        if(ObjectUtil.isNotEmpty(flowNodeList) && flowNodeList.size() == 1) {
            return flowNodeList.get(0).getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_START_EVENT);
        } else {
            return false;
        }
    }

    /**
     * 处理任务类型以及自动去重
     *
     * @author xuyuxiang
     * @date 2022/6/13 18:43
     **/
    public static void handleUserTaskTypeAndDistinctType(DelegateTask delegateTask) {
        ExtensionElements extensionElements = delegateTask.getBpmnModelElementInstance().getExtensionElements();
        if (ObjectUtil.isNotEmpty(extensionElements)) {
            extensionElements.getChildElementsByType(CamundaProperties.class).forEach(camundaProperties ->
                camundaProperties.getChildElementsByType(CamundaProperty.class).forEach(camundaProperty -> {
                    if (camundaProperty.getCamundaName().equals("configInfo")) {
                        JSONObject configInfo = JSONUtil.parseObj(camundaProperty.getCamundaValue());
                        String userTaskType = configInfo.getStr("userTaskType");
                        if (userTaskType.equals("ARTIFICIAL")) {
                            // 如果当前审批节点办理人为AUTO_COMPLETE，则自动通过
                            String assignee = delegateTask.getAssignee();
                            if (ObjectUtil.isNotEmpty(assignee) && assignee.equals("AUTO_COMPLETE")) {
                                // 自动通过任务
                                handleUserTaskCommentAndAttachment(delegateTask.getId(),
                                        delegateTask.getProcessInstanceId(), NodeApproveOperateTypeEnum.AUTO_COMPLETE.getValue(), "自动通过",
                                        "该节点审批人为空，审批自动通过", CollectionUtil.newArrayList());
                                delegateTask.getProcessEngineServices().getTaskService().complete(delegateTask.getId());
                            } else {
                                // 获取流程类型
                                Collection<ExtensionElements> processExtensionElementsCollection = delegateTask.getBpmnModelElementInstance().getParentElement().getChildElementsByType(ExtensionElements.class);
                                if(ObjectUtil.isNotEmpty(processExtensionElementsCollection)) {
                                    processExtensionElementsCollection.forEach(processExtensionElements ->
                                            processExtensionElements.getChildElementsByType(CamundaProperties.class).forEach(processCamundaProperties ->
                                                    processCamundaProperties.getChildElementsByType(CamundaProperty.class).forEach(processCamundaProperty -> {
                                                        if (processCamundaProperty.getCamundaName().equals("configInfo")) {
                                                            JSONObject processConfigInfo = JSONUtil.parseObj(processCamundaProperty.getCamundaValue());
                                                            Boolean processEnableAutoDistinct = processConfigInfo.getBool("processEnableAutoDistinct");
                                                            // 如果开启了自动去重
                                                            if (processEnableAutoDistinct) {
                                                                String processAutoDistinctType = processConfigInfo.getStr("processAutoDistinctType");
                                                                JSONObject jsonObject = JSONUtil.parseObj(delegateTask.getVariables());
                                                                String initiator = jsonObject.getStr("initiator");
                                                                if(ObjectUtil.isNotEmpty(initiator)) {
                                                                    if (processAutoDistinctType.equals("SAMPLE")) {
                                                                        if (ObjectUtil.isNotEmpty(assignee) && assignee.equals(initiator)) {
                                                                            // 当审批人和发起人是同一个人，审批自动通过；
                                                                            handleUserTaskCommentAndAttachment(delegateTask.getId(),
                                                                                    delegateTask.getProcessInstanceId(), NodeApproveOperateTypeEnum.AUTO_COMPLETE.getValue(), "自动通过",
                                                                                    "审批人和发起人是同一个人，审批自动通过", CollectionUtil.newArrayList());
                                                                            delegateTask.getProcessEngineServices().getTaskService().complete(delegateTask.getId());
                                                                        }
                                                                    } else {
                                                                        // 获取上一个节点的审批参与人，如果为一个参与人，且跟当前的参与人一致，则审批自动通过；
                                                                        List<FlowNode> previousNodeList = delegateTask.getBpmnModelElementInstance().getPreviousNodes().list();
                                                                        if (ObjectUtil.isNotEmpty(previousNodeList) && previousNodeList.size() == 1) {
                                                                            FlowNode flowNode = previousNodeList.get(0);
                                                                            if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_USER_TASK)) {
                                                                                // 获取上一节点的审批参与人
                                                                                AtomicReference<String> previousParticipateInfo = new AtomicReference<>();
                                                                                ExtensionElements previousExtensionElements = flowNode.getExtensionElements();
                                                                                if (ObjectUtil.isNotEmpty(previousExtensionElements)) {
                                                                                    previousExtensionElements.getChildElementsByType(CamundaProperties.class).forEach(previousCamundaProperties ->
                                                                                            previousCamundaProperties.getChildElementsByType(CamundaProperty.class).forEach(previousCamundaProperty -> {
                                                                                                if (previousCamundaProperty.getCamundaName().equals("participateInfo")) {
                                                                                                    previousParticipateInfo.set(previousCamundaProperty.getCamundaValue());
                                                                                                }
                                                                                            }));
                                                                                }
                                                                                if (ObjectUtil.isNotEmpty(previousParticipateInfo.get())) {
                                                                                    List<String> participateInfo = handleUserTaskParticipateInfo(delegateTask.getProcessInstanceId(), previousParticipateInfo.get());
                                                                                    if(ObjectUtil.isNotEmpty(participateInfo) && participateInfo.size() == 1) {
                                                                                        if(delegateTask.getAssignee().equals(participateInfo.get(0))) {
                                                                                            handleUserTaskCommentAndAttachment(delegateTask.getId(),
                                                                                                    delegateTask.getProcessInstanceId(), NodeApproveOperateTypeEnum.AUTO_COMPLETE.getValue(), "自动通过",
                                                                                                    "相同审批人连续出现，审批自动通过", CollectionUtil.newArrayList());
                                                                                            delegateTask.getProcessEngineServices().getTaskService().complete(delegateTask.getId());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    })
                                            )
                                    );
                                }
                            }
                        } else {
                            switch (userTaskType) {
                                case "EMPTY":
                                    // 空节点自动通过，不记录审批意见
                                    delegateTask.getProcessEngineServices().getTaskService().complete(delegateTask.getId());
                                    break;
                                case "COMPLETE":
                                    // 自动通过任务
                                    handleUserTaskCommentAndAttachment(delegateTask.getId(),
                                            delegateTask.getProcessInstanceId(), NodeApproveOperateTypeEnum.AUTO_COMPLETE.getValue(), "自动通过",
                                            "该节点为自动通过节点，审批自动通过", CollectionUtil.newArrayList());
                                    delegateTask.getProcessEngineServices().getTaskService().complete(delegateTask.getId());
                                    break;
                                case "REJECT":
                                    // 自动拒绝任务
                                    handleUserTaskCommentAndAttachment(delegateTask.getId(),
                                            delegateTask.getProcessInstanceId(), NodeApproveOperateTypeEnum.AUTO_COMPLETE.getValue(), "自动拒绝",
                                            "该节点为自动拒绝节点，审批自动拒绝", CollectionUtil.newArrayList());
                                    // 发送退回消息
                                    NodeRuntimeUtil.handleUserTaskBackNotice(delegateTask);
                                    // 发布拒绝事件
                                    FlwGlobalCustomEventCenter.publishRejectEvent(delegateTask.getProcessInstanceId());
                                    // 执行拒绝
                                    delegateTask.getProcessEngineServices().getRuntimeService()
                                            .deleteProcessInstancesIfExists(CollectionUtil.newArrayList(delegateTask.getProcessInstanceId()), "REJECT",
                                            true, true, true);
                                    break;
                                default:
                                    throw new CommonException("不支持的任务类型：{}", userTaskType);
                            }
                        }
                    }
                })
            );
        }
    }

    /**
     * 处理用户任务节点空处理人情况
     *
     * @author xuyuxiang
     * @date 2022/6/13 18:16
     **/
    public static void handleUserTaskEmptyParticipateInfo(String processInstanceId, UserTask userTask) {
        ExtensionElements extensionElements = userTask.getExtensionElements();
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        if(ObjectUtil.isNotEmpty(extensionElements)) {
            extensionElements.getChildElementsByType(CamundaProperties.class).forEach(camundaProperties ->
                camundaProperties.getChildElementsByType(CamundaProperty.class).forEach(camundaProperty -> {
                if (camundaProperty.getCamundaName().equals("configInfo")) {
                    JSONObject configInfo = JSONUtil.parseObj(camundaProperty.getCamundaValue());
                    String userTaskEmptyApproveType = configInfo.getStr("userTaskEmptyApproveType");
                    if (userTaskEmptyApproveType.equals("AUTO_TURN")) {
                        // 自动转交，设置转交人
                        runtimeService.setVariable(processInstanceId, "assignee" + StrUtil.UNDERLINE + userTask.getId(),
                                CollectionUtil.newArrayList(configInfo.getStr("userTaskEmptyApproveUser")));
                    } else if(userTaskEmptyApproveType.equals("AUTO_COMPLETE")) {
                        // 自动通过，则设置节点办理人为AUTO_COMPLETE
                        runtimeService.setVariable(processInstanceId, "assignee" + StrUtil.UNDERLINE + userTask.getId(),
                                CollectionUtil.newArrayList(userTaskEmptyApproveType));
                    }
                }
            }));
        }
    }

    /**
     * 处理流程参与人信息
     *
     * @author xuyuxiang
     * @date 2022/7/20 14:49
     **/
    public static List<String> handleProcessParticipateInfo(String participateInfo) {
        SysUserApi sysUserApi = SpringUtil.getBean(SysUserApi.class);
        SysRelationApi sysRelationApi = SpringUtil.getBean(SysRelationApi.class);
        JSONArray participateInfoArray = JSONUtil.parseArray(participateInfo);
        Set<String> resultUserIdSet = CollectionUtil.newHashSet();
        participateInfoArray.forEach(obj -> {
            FlwNode.FlwNodeParticipateProp flwNodeParticipateProp = JSONUtil.toBean(JSONUtil.parseObj(obj), FlwNode.FlwNodeParticipateProp.class);
            String key = flwNodeParticipateProp.getKey();
            switch (key) {
                case "ORG":
                    resultUserIdSet.addAll(sysUserApi.getUserIdListByOrgIdList(CollectionUtil
                            .newArrayList(StrUtil.split(flwNodeParticipateProp.getValue(), StrUtil.COMMA))));
                    break;
                case "ROLE":
                    resultUserIdSet.addAll(sysRelationApi.getUserIdListByRoleIdList(CollectionUtil
                            .newArrayList(StrUtil.split(flwNodeParticipateProp.getValue(), StrUtil.COMMA))));
                    break;
                case "POSITION":
                    resultUserIdSet.addAll(sysUserApi.getUserIdListByPositionIdList(CollectionUtil
                            .newArrayList(StrUtil.split(flwNodeParticipateProp.getValue(), StrUtil.COMMA))));
                    break;
                case "USER":
                    resultUserIdSet.addAll(StrUtil.split(flwNodeParticipateProp.getValue(), StrUtil.COMMA));
                    break;
                default:
                    break;
            }
        });
        return CollectionUtil.newArrayList(resultUserIdSet);
    }

    /**
     * 解析用户任务参与人
     *
     * @author xuyuxiang
     * @date 2022/6/6 9:58
     **/
    public static List<String> handleUserTaskParticipateInfo(String processInstanceId, String participateInfo) {
        SysUserApi sysUserApi = SpringUtil.getBean(SysUserApi.class);
        SysRelationApi sysRelationApi = SpringUtil.getBean(SysRelationApi.class);
        SysOrgApi sysOrgApi = SpringUtil.getBean(SysOrgApi.class);
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        JSONObject jsonObject = JSONUtil.parseObj(runtimeService.getVariables(processInstanceId));
        JSONArray participateInfoArray = JSONUtil.parseArray(participateInfo);
        String initiator = jsonObject.getStr("initiator");
        String initiatorOrgId = jsonObject.getStr("initiatorOrgId");
        String initiatorPositionId = jsonObject.getStr("initiatorPositionId");
        String initiatorDataJson = jsonObject.getStr("initiatorDataJson");
        Object initiatorTableJson = runtimeService.getVariable(processInstanceId, "initiatorTableJson");
        List<String> resultUserIdList = CollectionUtil.newArrayList();
        participateInfoArray.forEach(participateInfoJsonObject -> {
            FlwNode.FlwNodeParticipateProp flwNodeParticipateProp = JSONUtil.toBean(JSONUtil.parseObj(participateInfoJsonObject),
                    FlwNode.FlwNodeParticipateProp.class);
            String key = flwNodeParticipateProp.getKey();
            switch (key) {
                case "ORG":
                    resultUserIdList.addAll(sysUserApi.getUserIdListByOrgIdList(CollectionUtil.newArrayList(StrUtil
                            .split(flwNodeParticipateProp.getValue(), StrUtil.COMMA))));
                    break;
                case "ROLE":
                    resultUserIdList.addAll(sysRelationApi.getUserIdListByRoleIdList(CollectionUtil.newArrayList(StrUtil
                            .split(flwNodeParticipateProp.getValue(), StrUtil.COMMA))));
                    break;
                case "POSITION":
                    resultUserIdList.addAll(sysUserApi.getUserIdListByPositionIdList(CollectionUtil.newArrayList(StrUtil
                            .split(flwNodeParticipateProp.getValue(), StrUtil.COMMA))));
                    break;
                case "USER":
                    resultUserIdList.addAll(StrUtil.split(flwNodeParticipateProp.getValue(), StrUtil.COMMA));
                    break;
                case "ORG_LEADER":
                    if(ObjectUtil.isNotEmpty(initiatorOrgId)) {
                        String orgLeader = sysOrgApi.getSupervisorIdByOrgId(initiatorOrgId);
                        if (ObjectUtil.isNotEmpty(orgLeader)) {
                            resultUserIdList.add(orgLeader);
                        }
                    }
                    break;
                case "SUPERVISOR":
                    // 主管级别：-1 最高层级主管，1 直接主管，2 第2级主管，...10 第10级主管
                    String supervisorLevel = flwNodeParticipateProp.getValue();
                    if(ObjectUtil.isAllNotEmpty(initiator, initiatorOrgId, initiatorPositionId, supervisorLevel)) {
                        // 根据主管层级获取指定主管
                        String supervisor = sysUserApi.getSupervisorIdBySupervisorLevel(CollectionUtil.newArrayList(),
                                initiator, initiatorOrgId, supervisorLevel).getStr("id");
                        if (ObjectUtil.isNotEmpty(supervisor)) {
                            resultUserIdList.add(supervisor);
                        }
                    }
                    break;
                case "MUL_LEVEL_SUPERVISOR":
                    // 连续多级主管审批终点：-1 最高层级主管，1 直接主管，2 第2级主管，...10 第10级主管
                    String endLevel = flwNodeParticipateProp.getValue();
                    if(ObjectUtil.isAllNotEmpty(initiator, initiatorOrgId, initiatorPositionId, endLevel)) {
                        // 根据主管层级获取主管列表
                        List<String> supervisorList = sysUserApi.getMulSupervisorIdListByEndLevel(initiator, initiatorOrgId, endLevel);
                        if (ObjectUtil.isNotEmpty(supervisorList)) {
                            resultUserIdList.addAll(supervisorList);
                        }
                    }
                    break;
                case "FORM_USER":
                    String formUserValueForUser = flwNodeParticipateProp.getValue();
                    if (ObjectUtil.isNotEmpty(formUserValueForUser)) {
                        if(ObjectUtil.isNotEmpty(initiatorDataJson)) {
                            if(ObjectUtil.isNotEmpty(initiatorTableJson)) {
                                resultUserIdList.addAll(getFormUserIdListForDesignForm(initiatorTableJson, formUserValueForUser,
                                        processInstanceId));
                            } else {
                                resultUserIdList.addAll(getFormUserIdListForDefineForm(formUserValueForUser, processInstanceId));
                            }
                        }
                    }
                    break;
                case "FORM_USER_SUPERVISOR":
                    // 主管级别：-1 最高层级主管，1 直接主管，2 第2级主管，...10 第10级主管
                    String formUserSupervisorLevel = flwNodeParticipateProp.getValue();
                    // 扩展信息{"extJson":{"key":"FORM_USER","label":"表单内的人","value":"XXX_USER","extJson":""}}
                    String ormUserSupervisorExtJson = flwNodeParticipateProp.getExtJson();
                    if(ObjectUtil.isAllNotEmpty(formUserSupervisorLevel, ormUserSupervisorExtJson)) {
                        if(ObjectUtil.isNotEmpty(ormUserSupervisorExtJson)) {
                            JSONObject formUserSupervisorJsonObject = JSONUtil.parseObj(ormUserSupervisorExtJson);
                            if(ObjectUtil.isNotEmpty(formUserSupervisorJsonObject)) {
                                String formUserValueForSupervisor = formUserSupervisorJsonObject.getStr("value");
                                if(ObjectUtil.isNotEmpty(formUserValueForSupervisor)) {
                                    if(ObjectUtil.isNotEmpty(initiatorDataJson)) {
                                        List<String> formUserIdList;
                                        if(ObjectUtil.isNotEmpty(initiatorTableJson)) {
                                            formUserIdList = getFormUserIdListForDesignForm(initiatorTableJson,
                                                    formUserValueForSupervisor, processInstanceId);
                                        } else {
                                            formUserIdList = getFormUserIdListForDefineForm(formUserValueForSupervisor,
                                                    processInstanceId);
                                        }
                                        if(ObjectUtil.isNotEmpty(formUserIdList)) {
                                            resultUserIdList.addAll(getUserSupervisorIdList(formUserIdList,
                                                    formUserSupervisorLevel));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "FORM_USER_MUL_LEVEL_SUPERVISOR":
                    // 主管级别：-1 最高层级主管，1 直接主管，2 第2级主管，...10 第10级主管
                    String formUserMulLevelSupervisorLevel = flwNodeParticipateProp.getValue();
                    // 扩展信息{"extJson":{"key":"FORM_USER","label":"表单内的人","value":"XXX_USER","extJson":""}}
                    String formUserMulLevelSupervisorExtJson = flwNodeParticipateProp.getExtJson();
                    if(ObjectUtil.isAllNotEmpty(formUserMulLevelSupervisorLevel, formUserMulLevelSupervisorExtJson)) {
                        if(ObjectUtil.isNotEmpty(formUserMulLevelSupervisorExtJson)) {
                            JSONObject formUserMulLevelSupervisorJsonObject = JSONUtil.parseObj(formUserMulLevelSupervisorExtJson);
                            if(ObjectUtil.isNotEmpty(formUserMulLevelSupervisorJsonObject)) {
                                String formUserValueForMulLevelSupervisor = formUserMulLevelSupervisorJsonObject.getStr("value");
                                if(ObjectUtil.isNotEmpty(formUserValueForMulLevelSupervisor)) {
                                    if(ObjectUtil.isNotEmpty(initiatorDataJson)) {
                                        List<String> formUserIdList;
                                        if(ObjectUtil.isNotEmpty(initiatorTableJson)) {
                                            formUserIdList = getFormUserIdListForDesignForm(initiatorTableJson,
                                                    formUserValueForMulLevelSupervisor, processInstanceId);
                                        } else {
                                            formUserIdList = getFormUserIdListForDefineForm(formUserValueForMulLevelSupervisor,
                                                    processInstanceId);
                                        }
                                        if(ObjectUtil.isNotEmpty(formUserIdList)) {
                                            resultUserIdList.addAll(getMulLevelUserSupervisorIdList(formUserIdList,
                                                    formUserMulLevelSupervisorLevel));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "INITIATOR":
                    resultUserIdList.add(initiator);
                    break;
                default:
                    break;
            }
        });
        return CollectionUtil.distinct(resultUserIdList);
    }

    /**
     * 获取设计表单模式下表单内的人id集合
     *
     * @author xuyuxiang
     * @date 2023/7/25 23:00
     */
    private static List<String> getFormUserIdListForDesignForm(Object initiatorTableJson, String formUserValue, String processInstanceId) {
        List<String> resultUserIdList = CollectionUtil.newArrayList();
        String primaryTableName = JSONUtil.parseObj(JSONUtil.parseArray(initiatorTableJson)
                .stream().filter(obj -> JSONUtil.parseObj(obj).getStr("tableType").equals("parent"))
                .collect(Collectors.toList()).get(0)).getStr("tableName");
        String formUserVariableKey = StrUtil.toCamelCase(primaryTableName.toLowerCase()) + StrUtil.UNDERLINE +
                StrUtil.toCamelCase(formUserValue.toLowerCase());
        Object formUserVariableValue = SpringUtil.getBean(RuntimeService.class).getVariable(processInstanceId, formUserVariableKey);
        if(ObjectUtil.isNotEmpty(formUserVariableValue)) {
            if(JSONUtil.isTypeJSONArray(Convert.toStr(formUserVariableValue))) {
                JSONArray jsonArray = JSONUtil.parseArray(Convert.toStr(formUserVariableValue));
                resultUserIdList  = CollectionUtil.removeNull(jsonArray.stream().map(o -> {
                    JSONObject userItem = JSONUtil.parseObj(o);
                    if (ObjectUtil.isNotEmpty(userItem)) {
                        return userItem.getStr("id");
                    } else {
                        return null;
                    }
                }).collect(Collectors.toList()));
            }
        }
        return resultUserIdList;
    }

    /**
     * 获取自定义表单模式下表单内的人id集合
     *
     * @author xuyuxiang
     * @date 2023/7/25 23:00
     */
    private static List<String> getFormUserIdListForDefineForm(String formUserValues, String processInstanceId) {
        List<String> resultUserIdList = CollectionUtil.newArrayList();
        StrUtil.split(formUserValues, StrUtil.COMMA).forEach(formUserValueItem -> {
            Object formUserVariableValue = SpringUtil.getBean(RuntimeService.class).getVariable(processInstanceId, formUserValueItem);
            if(ObjectUtil.isNotEmpty(formUserVariableValue)) {
                resultUserIdList.addAll(StrUtil.split(Convert.toStr(formUserVariableValue), StrUtil.COMMA));
            }
        });
        return resultUserIdList;
    }

    /**
     * 获取用户指定上级主管id集合
     *
     * @author xuyuxiang
     * @date 2023/7/26 0:17
     */
    public static List<String> getUserSupervisorIdList(List<String> formUserIdList, String level) {
        List<String> resultUserIdList = CollectionUtil.newArrayList();
        SysUserApi sysUserApi = SpringUtil.getBean(SysUserApi.class);
        formUserIdList.forEach(formUserId -> {
            // 根据主管层级获取主管列表
            JSONObject formUser = sysUserApi.getUserByIdWithoutException(formUserId);
            if(ObjectUtil.isNotEmpty(formUser)) {
                String orgId = formUser.getStr("orgId");
                if(ObjectUtil.isNotEmpty(orgId)) {
                    JSONObject supervisorJsonObject = sysUserApi.getSupervisorIdBySupervisorLevel(CollectionUtil.newArrayList(), formUserId,
                            orgId, level);
                    if (ObjectUtil.isNotEmpty(supervisorJsonObject)) {
                        resultUserIdList.add(supervisorJsonObject.getStr("id"));
                    }
                }
            }
        });
        return resultUserIdList;
    }

    /**
     * 获取用户连续多级主管id集合
     *
     * @author xuyuxiang
     * @date 2023/7/26 0:17
     */
    public static List<String> getMulLevelUserSupervisorIdList(List<String> formUserIdList, String endLevel) {
        List<String> resultUserIdList = CollectionUtil.newArrayList();
        SysUserApi sysUserApi = SpringUtil.getBean(SysUserApi.class);
        formUserIdList.forEach(formUserId -> {
            // 根据主管层级获取主管列表
            JSONObject formUser = sysUserApi.getUserByIdWithoutException(formUserId);
            if(ObjectUtil.isNotEmpty(formUser)) {
                String orgId = formUser.getStr("orgId");
                String positionId = formUser.getStr("positionId");
                if(ObjectUtil.isAllNotEmpty(orgId, positionId)) {
                    List<String> supervisorList = sysUserApi.getMulSupervisorIdListByEndLevel(formUserId, orgId, endLevel);
                    if (ObjectUtil.isNotEmpty(supervisorList)) {
                        resultUserIdList.addAll(supervisorList);
                    }
                }
            }
        });
        return resultUserIdList;
    }

    /**
     * 处理任务待办通知
     *
     * @author xuyuxiang
     * @date 2022/6/13 17:35
     **/
    public static void handleUserTaskTodoNotice(DelegateTask delegateTask) {
        handleProcessNotice(delegateTask, null, "TODO", "processEnableTodoNotice",
                "processTodoNoticeChannel", "processTodoNoticeTemplate");
    }

    /**
     * 处理任务退回通知
     *
     * @author xuyuxiang
     * @date 2022/6/13 17:35
     **/
    public static void handleUserTaskBackNotice(DelegateTask delegateTask) {
        handleProcessNotice(delegateTask, null, "BACK", "processEnableBackNotice",
                "processBackNoticeChannel", "processBackNoticeTemplate");
    }

    /**
     * 处理流程抄送通知
     *
     * @author xuyuxiang
     * @date 2022/6/13 17:35
     **/
    public static void handleProcessCopyNotice(DelegateExecution delegateExecution) {
        handleProcessNotice(null, delegateExecution, "COPY", "processEnableCopyNotice",
                "processCopyNoticeChannel", "processCopyNoticeTemplate");
    }

    /**
     * 处理流程完成通知
     *
     * @author xuyuxiang
     * @date 2022/6/13 17:35
     **/
    public static void handleProcessCompleteNotice(DelegateExecution delegateExecution) {
        handleProcessNotice(null, delegateExecution, "COMPLETE", "processEnableCompleteNotice",
                "processCompleteNoticeChannel", "processCompleteNoticeTemplate");
    }

    /**
     * 处理流程通知
     *
     * @author xuyuxiang
     * @date 2022/6/13 17:35
     **/
    private static void handleProcessNotice(DelegateTask delegateTask,
                                            DelegateExecution delegateExecution,
                                            String noticeType,
                                            String processNoticeEnablePropertyKey,
                                            String processNoticeChannelPropertyKey,
                                            String processNoticeTemplatePropertyKey) {
        RepositoryService repositoryService = SpringUtil.getBean(RepositoryService.class);
        final boolean[] processNoticeEnabled = {false};
        AtomicReference<List<String>> processNoticeChannelList = new AtomicReference<>();
        AtomicReference<String> processNoticeTemplate = new AtomicReference<>();
        Collection<ExtensionElements> processExtensionElementsCollection;

        if(noticeType.equals("TODO") || noticeType.equals("BACK")) {
            if(noticeType.equals("BACK")) {
                processExtensionElementsCollection = repositoryService.getBpmnModelInstance(delegateTask.getProcessDefinitionId())
                        .getDocumentElement().getUniqueChildElementByType(Process.class).getChildElementsByType(ExtensionElements.class);
            } else {
                processExtensionElementsCollection = delegateTask.getBpmnModelElementInstance()
                        .getParentElement().getChildElementsByType(ExtensionElements.class);
            }
        } else {
            processExtensionElementsCollection = delegateExecution.getBpmnModelElementInstance()
                    .getParentElement().getChildElementsByType(ExtensionElements.class);
        }

        if(ObjectUtil.isNotEmpty(processExtensionElementsCollection)) {
            processExtensionElementsCollection.forEach(processExtensionElements ->
                    processExtensionElements.getChildElementsByType(CamundaProperties.class).forEach(processCamundaProperties ->
                            processCamundaProperties.getChildElementsByType(CamundaProperty.class).forEach(processCamundaProperty -> {
                                if (processCamundaProperty.getCamundaName().equals("configInfo")) {
                                    JSONObject processConfigInfo = JSONUtil.parseObj(processCamundaProperty.getCamundaValue());
                                    processNoticeEnabled[0] = processConfigInfo.getBool(processNoticeEnablePropertyKey);
                                    if(processNoticeEnabled[0]) {
                                        processNoticeChannelList.set(JSONUtil.toList(JSONUtil.parseArray(processConfigInfo.getStr(processNoticeChannelPropertyKey)), String.class));
                                        processNoticeTemplate.set(processConfigInfo.getStr(processNoticeTemplatePropertyKey));
                                    }
                                }
                            })));
        }
        if(processNoticeEnabled[0]) {
            if(noticeType.equals("TODO") || noticeType.equals("BACK")) {
                handleProcessNoticeExecute(delegateTask, null, noticeType, processNoticeTemplate.get(), processNoticeChannelList.get());
            } else {
                handleProcessNoticeExecute(null, delegateExecution, noticeType, processNoticeTemplate.get(), processNoticeChannelList.get());
            }
        }
    }

    private static void handleProcessNoticeExecute(DelegateTask delegateTask, DelegateExecution delegateExecution, String noticeType, String processNoticeTemplate,
                                                   List<String> processNoticeChannelList) {
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        DevMessageApi devMessageApi = SpringUtil.getBean(DevMessageApi.class);
        DevEmailApi devEmailApi = SpringUtil.getBean(DevEmailApi.class);
        // DevSmsApi devSmsApi = SpringUtil.getBean(DevSmsApi.class);
        SysUserApi sysUserApi = SpringUtil.getBean(SysUserApi.class);
        List<String> participateUserIdList = CollectionUtil.newArrayList();
        String processNoticeTemplateFormat;
        switch (noticeType) {
            case "TODO":
                participateUserIdList = Convert.toList(String.class, delegateTask
                        .getVariable("assignee" + StrUtil.UNDERLINE + delegateTask.getTaskDefinitionKey()));
                processNoticeTemplateFormat = handleProcessNoticeTemplateForDelegateTask(delegateTask, processNoticeTemplate);
                break;
            case "COPY":
                AtomicReference<String> participateInfo = new AtomicReference<>();
                delegateExecution.getBpmnModelElementInstance().getChildElementsByType(ExtensionElements.class).forEach(extensionElements ->
                        extensionElements.getChildElementsByType(CamundaProperties.class).forEach(camundaProperties ->
                                camundaProperties.getChildElementsByType(CamundaProperty.class).forEach(camundaProperty -> {
                                            if (camundaProperty.getCamundaName().equals("participateInfo")) {
                                                    participateInfo.set(camundaProperty.getCamundaValue());
                                            }
                                })));
                if (ObjectUtil.isNotEmpty(participateInfo.get())) {
                    participateUserIdList = handleUserTaskParticipateInfo(delegateExecution.getProcessInstanceId(), participateInfo.get());
                }
                processNoticeTemplateFormat = handleProcessNoticeTemplateForDelegateExecution(delegateExecution, processNoticeTemplate);
                break;
            case "COMPLETE":
                Object initiatorObjForComplete = delegateExecution.getVariable("initiator");
                if(ObjectUtil.isNotEmpty(initiatorObjForComplete)) {
                    participateUserIdList = CollectionUtil.newArrayList(Convert.toStr(initiatorObjForComplete));
                }
                processNoticeTemplateFormat = handleProcessNoticeTemplateForDelegateExecution(delegateExecution, processNoticeTemplate);
                break;
            case "BACK":
                Object initiatorObjForBack;
                try {
                    initiatorObjForBack = delegateTask.getVariable("initiator");
                } catch (Exception e) {
                    initiatorObjForBack = runtimeService.getVariable(delegateTask.getProcessInstanceId(), "initiator");
                }
                if(ObjectUtil.isNotEmpty(initiatorObjForBack)) {
                    participateUserIdList = CollectionUtil.newArrayList(Convert.toStr(initiatorObjForBack));
                }
                processNoticeTemplateFormat = handleProcessNoticeTemplateForDelegateTask(delegateTask, processNoticeTemplate);
                break;
            default:
                throw new CommonException("不支持的通知类型：{}", noticeType);
        }

        List<String> finalParticipateUserIdList = participateUserIdList;
        processNoticeChannelList.forEach(processNoticeChannel -> {
            if(ObjectUtil.isNotEmpty(finalParticipateUserIdList)) {
                switch (processNoticeChannel) {
                    case "MSG":
                        try {
                            // TODO 执行发送站内信
                            log.info("给" + finalParticipateUserIdList + "发送内容为：" + processNoticeTemplateFormat + "的MSG");
                            devMessageApi.sendMessage(finalParticipateUserIdList, processNoticeTemplateFormat);
                        } catch (Exception e) {
                            log.error("给" + finalParticipateUserIdList + "发送内容为：" + processNoticeTemplateFormat + "的MSG异常");
                            e.printStackTrace();
                        }
                        break;
                    case "EMAIL":
                        List<String> userEmailList = sysUserApi.getUserListByIdListWithoutException(finalParticipateUserIdList).stream()
                                .map(jsonObject -> jsonObject.getStr("email")).collect(Collectors.toList());
                        if(ObjectUtil.isNotEmpty(userEmailList)) {
                            try {
                                // TODO 执行发送邮件，使用本地邮件，可改为其他邮件引擎
                                log.info("给" + finalParticipateUserIdList + "发送内容为：" + processNoticeTemplateFormat + "的EMAIL");
                                /*devEmailApi.sendTextEmailLocal(StrUtil.join(StrUtil.COMMA, userEmailList), processNoticeTemplateFormat,
                                processNoticeTemplateFormat, CollectionUtil.newArrayList());*/
                            } catch (Exception e) {
                                log.error("给" + finalParticipateUserIdList + "发送内容为：" + processNoticeTemplateFormat + "的EMAIL异常");
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "SMS":
                        List<String> userPhoneList = sysUserApi.getUserListByIdListWithoutException(finalParticipateUserIdList).stream()
                                .map(jsonObject -> jsonObject.getStr("phone")).collect(Collectors.toList());
                        if(ObjectUtil.isNotEmpty(userPhoneList)) {
                            try {
                                // TODO 执行发送短信，使用阿里云短信，可改为其他短信引擎
                                log.info("给" + finalParticipateUserIdList + "发送内容为：" + processNoticeTemplateFormat + "的SMS");
                                /*devSmsApi.sendSmsAliyun(StrUtil.join(StrUtil.COMMA, userPhoneList), null, "模板编码", "模板参数");*/
                            } catch (Exception e) {
                                log.error("给" + finalParticipateUserIdList + "发送内容为：" + processNoticeTemplateFormat + "的SMS异常");
                                e.printStackTrace();
                            }
                        }
                        break;
                    default:
                        throw new CommonException("不支持的通知渠道类型：{}", processNoticeChannel);
                }
            }

        });
    }

    private static String handleProcessNoticeTemplateForDelegateTask(DelegateTask delegateTask, String processNoticeTemplate) {
        Object initiatorName;
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        try {
            initiatorName = delegateTask.getVariable("initiatorName");
        } catch (Exception e) {
            initiatorName = runtimeService.getVariable(delegateTask.getProcessInstanceId(), "initiatorName");
        }
        Object initiatorDataJson;
        Object initiatorTableJson;
        try {
            initiatorDataJson = delegateTask.getVariable("initiatorDataJson");
            initiatorTableJson = delegateTask.getVariable("initiatorTableJson");
        } catch (Exception e) {
            initiatorDataJson = runtimeService.getVariable(delegateTask.getProcessInstanceId(), "initiatorDataJson");
            initiatorTableJson = runtimeService.getVariable(delegateTask.getProcessInstanceId(), "initiatorTableJson");
        }
        return handleProcessNoticeTemplate(delegateTask.getProcessDefinitionId(),initiatorName, initiatorDataJson,
                initiatorTableJson, processNoticeTemplate);
    }

    private static String handleProcessNoticeTemplateForDelegateExecution(DelegateExecution delegateExecution, String processNoticeTemplate) {
        return handleProcessNoticeTemplate(delegateExecution.getProcessDefinitionId(),
                delegateExecution.getVariable("initiatorName"),
                delegateExecution.getVariable("initiatorDataJson"),
                delegateExecution.getVariable("initiatorTableJson"), processNoticeTemplate);
    }

    private static String handleProcessNoticeTemplate(String processDefinitionId, Object initiatorNameObj, Object initiatorDataJson,
                                                      Object initiatorTableJson, String processNoticeTemplate) {
        String processDefinitionName = SpringUtil.getBean(RepositoryService.class).createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult().getName();
        // 获取发起人姓名
        String initiatorName = "未知用户";
        if(ObjectUtil.isNotEmpty(initiatorNameObj)) {
            initiatorName = Convert.toStr(initiatorNameObj);
        }
        AtomicReference<String> processNoticeResult = new AtomicReference<>(StrUtil.replace(StrUtil
                .replace(StrUtil.replace(processNoticeTemplate, "initiator", initiatorName),
                        "startTime", DateUtil.now()), "processName", processDefinitionName));

        if(ObjectUtil.isNotEmpty(initiatorDataJson)) {
            if(ObjectUtil.isNotEmpty(initiatorTableJson)) {
                handleDataJsonPrimaryTableField(Convert.toStr(initiatorDataJson)).forEach((key, value) -> processNoticeResult
                        .set(StrUtil.replace(processNoticeResult.get(), key, Convert.toStr(value))));
            } else {
                JSONUtil.parseObj(initiatorDataJson).forEach((key, value) -> processNoticeResult
                        .set(StrUtil.replace(processNoticeResult.get(), key, Convert.toStr(value))));
            }
        }
        return processNoticeResult.get();
    }

    /**
     * 获取表单内主表数据对象
     *
     * @author xuyuxiang
     * @date 2022/6/14 16:45
     **/
    public static JSONObject handleDataJsonPrimaryTableField(String dataJson) {
        JSONObject dataObject = JSONUtil.parseObj(dataJson);
        AtomicReference<JSONObject> primaryTableJsonObject = new AtomicReference<>();
        dataObject.forEach((key, value) -> {
            String valueJson = JSONUtil.toJsonStr(value);
            boolean isPrimaryTableJson = JSONUtil.isTypeJSONObject(valueJson);
            if(isPrimaryTableJson) {
                primaryTableJsonObject.set(JSONUtil.parseObj(valueJson));
            }
        });
        return primaryTableJsonObject.get();
    }

    /**
     * 处理初始化的填写数据
     *
     * @author xuyuxiang
     * @date 2022/6/7 19:05
     **/
    public static JSONObject handleInitiatorDataJson(String tableJson, String dataJson, String parentTablePrimaryId) {
        JSONObject resultObject = JSONUtil.createObj();
        if (ObjectUtil.isNotEmpty(tableJson)) {
            AtomicReference<String> resultSql = new AtomicReference<>();
            AtomicReference<String> finalParentTablePrimaryId = new AtomicReference<>(parentTablePrimaryId);
            JSONObject primaryTableJsonObject = JSONUtil.parseObj(JSONUtil.parseArray(tableJson).stream().filter(obj -> JSONUtil.parseObj(obj)
                    .getStr("tableType").equalsIgnoreCase("parent")).collect(Collectors.toList()).get(0));
            String primaryTableName = primaryTableJsonObject.getStr("tableName");
            String primaryTablePrimaryKeyColumnName = primaryTableJsonObject.getStr("primaryKey");
            List<String> primaryTableColumnList = primaryTableJsonObject.getJSONArray("tableColumn").stream().map(obj -> JSONUtil.parseObj(obj)
                    .getStr("columnName")).collect(Collectors.toList());
            JSONObject primaryTableDataJsonObject = JSONUtil.parseObj(dataJson).getJSONObject(primaryTableName);
            if(ObjectUtil.isNull(primaryTableDataJsonObject)) {
                throw new CommonException("流程主表{}数据为空", primaryTableName);
            }
            List<String> primaryTableDataList = CollectionUtil.newArrayList();
            primaryTableColumnList.forEach(primaryTableColumn -> {
                if(primaryTableColumn.equalsIgnoreCase(primaryTablePrimaryKeyColumnName)) {
                    primaryTableDataList.add("'" + finalParentTablePrimaryId.get() + "'");
                } else {
                    TenApi tenApi = SpringUtil.getBean(TenApi.class);
                    if(primaryTableColumn.equalsIgnoreCase(tenApi.getDefaultTenColumnName())) {
                        if(tenApi.getTenEnabled()) {
                            primaryTableDataList.add("'" + tenApi.getCurrentTenId() + "'");
                        } else {
                            primaryTableDataList.add("'" + tenApi.getDefaultTenId() + "'");
                        }
                    } else if(primaryTableColumn.equalsIgnoreCase(DELETE_FLAG)) {
                        MybatisPlusProperties mybatisPlusProperties = SpringUtil.getBean(MybatisPlusProperties.class);
                        GlobalConfig.DbConfig dbConfig = mybatisPlusProperties.getGlobalConfig().getDbConfig();
                        primaryTableDataList.add("'" + dbConfig.getLogicNotDeleteValue() + "'");
                    } else if(primaryTableColumn.equalsIgnoreCase(CREATE_TIME)) {
                        primaryTableDataList.add("'" + DateUtil.now() + "'");
                    } else if(primaryTableColumn.equalsIgnoreCase(CREATE_USER)) {
                        primaryTableDataList.add("'" + getUserId() + "'");
                    } else {
                        Object o = primaryTableDataJsonObject.get(primaryTableColumn);
                        if(ObjectUtil.isNotEmpty(o)) {
                            primaryTableDataList.add("'" + o + "'");
                        } else {
                            primaryTableDataList.add(null);
                        }
                    }
                }
            });
            AtomicReference<String> primarySql = new AtomicReference<>();
            primarySql.set("INSERT INTO " + primaryTableName + "(" + StrUtil.join(StrUtil.COMMA, primaryTableColumnList) + ") VALUES (" + StrUtil.join(StrUtil.COMMA, primaryTableDataList) + ");");
            resultSql.set(primarySql.get());

            // 子表对象集合
            JSONUtil.parseArray(tableJson).stream().filter(obj -> JSONUtil.parseObj(obj)
                    .getStr("tableType").equalsIgnoreCase("child")).collect(Collectors.toList()).forEach(childTableObj -> {
                // 子表对象
                JSONObject childTableJsonObject = JSONUtil.parseObj(childTableObj);
                String childTableName = childTableJsonObject.getStr("tableName");
                String childTablePrimaryKeyColumnName = childTableJsonObject.getStr("primaryKey");
                String childTableForeignKeyColumnName = childTableJsonObject.getStr("foreignKey");
                List<String> childTableColumnList = childTableJsonObject.getJSONArray("tableColumn").stream().map(obj -> JSONUtil.parseObj(obj)
                        .getStr("columnName")).collect(Collectors.toList());

                // 子表字段SQL
                String childTableColumnSqlStr = StrUtil.join(StrUtil.COMMA, childTableColumnList);

                // 子表多条数据
                String childTableDataJsonStr = primaryTableDataJsonObject.getStr(childTableName);
                if(ObjectUtil.isNotEmpty(childTableDataJsonStr)) {
                    JSONArray childTableDataJsonArray = JSONUtil.parseArray(childTableDataJsonStr);
                    childTableDataJsonArray.forEach(childDataObj -> {
                        // 子表主键，暂时不用
                        /*AtomicReference<String> finalChildTablePrimaryId = new AtomicReference<>(parentTablePrimaryId);*/
                        JSONObject childTableDataJsonObject = JSONUtil.parseObj(childDataObj);
                        List<String> childTableDataList = CollectionUtil.newArrayList();
                        childTableColumnList.forEach(childTableColumn -> {
                            if(childTableColumn.equalsIgnoreCase(childTablePrimaryKeyColumnName)) {
                                childTableDataList.add("'" + IdWorker.getIdStr() + "'");
                            } else if(childTableColumn.equalsIgnoreCase(childTableForeignKeyColumnName)) {
                                childTableDataList.add("'" + finalParentTablePrimaryId + "'");
                            } else {
                                TenApi tenApi = SpringUtil.getBean(TenApi.class);
                                if(childTableColumn.equalsIgnoreCase(tenApi.getDefaultTenColumnName())) {
                                    if(tenApi.getTenEnabled()) {
                                        childTableDataList.add("'" + tenApi.getCurrentTenId() + "'");
                                    } else {
                                        childTableDataList.add("'" + tenApi.getDefaultTenId() + "'");
                                    }
                                } else if(childTableColumn.equalsIgnoreCase(DELETE_FLAG)) {
                                    MybatisPlusProperties mybatisPlusProperties = SpringUtil.getBean(MybatisPlusProperties.class);
                                    GlobalConfig.DbConfig dbConfig = mybatisPlusProperties.getGlobalConfig().getDbConfig();
                                    childTableDataList.add("'" + dbConfig.getLogicNotDeleteValue() + "'");
                                } else if(childTableColumn.equalsIgnoreCase(CREATE_TIME)) {
                                    childTableDataList.add("'" + DateUtil.now() + "'");
                                } else if(childTableColumn.equalsIgnoreCase(CREATE_USER)) {
                                    childTableDataList.add("'" + getUserId() + "'");
                                } else {
                                    Object o = childTableDataJsonObject.get(childTableColumn);
                                    if(ObjectUtil.isNotEmpty(o)) {
                                        childTableDataList.add("'" + o + "'");
                                    } else {
                                        childTableDataList.add(null);
                                    }
                                }
                            }
                        });

                        AtomicReference<String> childSql = new AtomicReference<>();
                        childSql.set("INSERT INTO " + childTableName + "(" + childTableColumnSqlStr + ") VALUES (" + StrUtil.join(StrUtil.COMMA, childTableDataList) + ");");
                        resultSql.set(resultSql.get() + childSql.get());
                    });
                }
            });
            resultObject.set("sql", resultSql);
            resultObject.set("data", dataJson);
        }
        return resultObject;
    }

    /**
     * 处理填写数据
     *
     * @author xuyuxiang
     * @date 2022/6/7 19:05
     **/
    public static JSONObject handleDataJson(String tableJson, String dataJson, String parentTablePrimaryId) {
        JSONObject resultObject = JSONUtil.createObj();
        if (ObjectUtil.isNotEmpty(tableJson)) {
            AtomicReference<String> resultSql = new AtomicReference<>();
            AtomicReference<String> finalParentTablePrimaryId = new AtomicReference<>(parentTablePrimaryId);
            JSONObject primaryTableJsonObject = JSONUtil.parseObj(JSONUtil.parseArray(tableJson).stream().filter(obj -> JSONUtil.parseObj(obj)
                    .getStr("tableType").equalsIgnoreCase("parent")).collect(Collectors.toList()).get(0));
            String primaryTableName = primaryTableJsonObject.getStr("tableName");
            String primaryTablePrimaryKeyColumnName = primaryTableJsonObject.getStr("primaryKey");
            List<String> primaryTableColumnList = primaryTableJsonObject.getJSONArray("tableColumn").stream().map(obj -> JSONUtil.parseObj(obj)
                    .getStr("columnName")).collect(Collectors.toList());
            JSONObject primaryTableDataJsonObject = JSONUtil.parseObj(dataJson).getJSONObject(primaryTableName);
            if(ObjectUtil.isEmpty(primaryTableDataJsonObject)) {
                throw new CommonException("流程主表数据格式错误");
            }
            List<String> primaryTableDataList = CollectionUtil.newArrayList();
            primaryTableColumnList.forEach(primaryTableColumn -> {
                if(primaryTableColumn.equalsIgnoreCase(primaryTablePrimaryKeyColumnName)) {
                    primaryTableDataList.add("'" + finalParentTablePrimaryId.get() + "'");
                } else {
                    TenApi tenApi = SpringUtil.getBean(TenApi.class);
                    if(primaryTableColumn.equalsIgnoreCase(tenApi.getDefaultTenColumnName())) {
                        if(tenApi.getTenEnabled()) {
                            primaryTableDataList.add("'" + tenApi.getCurrentTenId() + "'");
                        } else {
                            primaryTableDataList.add("'" + tenApi.getDefaultTenId() + "'");
                        }
                    } else if(primaryTableColumn.equalsIgnoreCase(DELETE_FLAG)) {
                        primaryTableDataList.add(null);
                    } else if(primaryTableColumn.equalsIgnoreCase(CREATE_TIME)) {
                        primaryTableDataList.add(null);
                    } else if(primaryTableColumn.equalsIgnoreCase(CREATE_USER)) {
                        primaryTableDataList.add(null);
                    }  else if(primaryTableColumn.equalsIgnoreCase(UPDATE_TIME)) {
                        primaryTableDataList.add("'" + DateUtil.now() + "'");
                    } else if(primaryTableColumn.equalsIgnoreCase(UPDATE_USER)) {
                        primaryTableDataList.add("'" + getUserId() + "'");
                    } else {
                        Object o = primaryTableDataJsonObject.get(primaryTableColumn);
                        if(ObjectUtil.isNotEmpty(o)) {
                            primaryTableDataList.add("'" + o + "'");
                        } else {
                            primaryTableDataList.add(null);
                        }
                    }
                }
            });
            AtomicReference<String> primarySql = new AtomicReference<>();
            primarySql.set("UPDATE " + primaryTableName + " SET ");
            for(int i = 0 ;i< primaryTableColumnList.size(); i++) {
                if(!primaryTableColumnList.get(i).equalsIgnoreCase(DELETE_FLAG) && !primaryTableColumnList.get(i)
                        .equalsIgnoreCase(CREATE_TIME) && !primaryTableColumnList.get(i).equalsIgnoreCase(CREATE_USER)) {
                    primarySql.set(primarySql.get() + primaryTableColumnList.get(i) + " = " + primaryTableDataList.get(i) + StrUtil.COMMA);
                }
            }
            resultSql.set(StrUtil.removeSuffix(primarySql.get(), StrUtil.COMMA) + " WHERE " + primaryTablePrimaryKeyColumnName + " = '" + finalParentTablePrimaryId.get() + "';");
            resultObject.set("sql", resultSql);
            resultObject.set("data", dataJson);
        }
        return resultObject;
    }

    /**
     * 处理任务意见
     *
     * @author xuyuxiang
     * @date 2022/6/15 10:06
     **/
    public static void handleUserTaskCommentAndAttachment(String taskId, String processInstanceId, String operateType, String operateText,
                                                          String comment, List<FlwTaskAttachmentParam> attachmentList) {
        NodeRuntimeUtil.handleTenAuth();
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        JSONObject jsonObject = JSONUtil.createObj();
        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        jsonObject.set("USER_ID", loginUser.getId());
        jsonObject.set("USER_NAME", loginUser.getName());
        jsonObject.set("OPERATE_TYPE", operateType);
        jsonObject.set("OPERATE_TEXT", operateText);
        jsonObject.set("COMMENT", comment);
        Comment taskComment = taskService.createComment(taskId, processInstanceId, JSONUtil.toJsonStr(jsonObject));
        if(ObjectUtil.isNotEmpty(attachmentList)) {
            attachmentList.forEach(flwTaskCommentParam -> {
                if(ObjectUtil.isAllNotEmpty(flwTaskCommentParam.getAttachmentName(), flwTaskCommentParam.getAttachmentUrl())) {
                    taskService.createAttachment(null, taskId, processInstanceId,
                            flwTaskCommentParam.getAttachmentName(), taskComment.getId(), flwTaskCommentParam.getAttachmentUrl());
                }
            });
        }
    }

    /**
     * 执行SQL
     *
     * @author xuyuxiang
     * @date 2022/6/16 10:24
     **/
    public static void handleSqlExecution(String sql) {
        if(ObjectUtil.isNotEmpty(sql)) {
            Connection conn;
            try {
                DbsApi dbsApi = SpringUtil.getBean(DbsApi.class);
                conn = dbsApi.getCurrentDataSource().getConnection();
            } catch (SQLException e) {
                throw new CommonException(e.getMessage());
            }
            try {
                conn.setAutoCommit(false);
                StrUtil.split(sql.trim(), ";").forEach(sqlItem -> {
                    if(ObjectUtil.isNotEmpty(sqlItem)) {
                        try {
                            SqlExecutor.execute(conn, sqlItem);
                        } catch (SQLException e) {
                            throw new CommonException(e.getMessage());
                        }
                    }
                });
                conn.commit();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            } finally {
                DbUtil.close(conn);
            }
        }
    }

    /**
     * 处理多租户的情况
     *
     * @author xuyuxiang
     * @date 2022/7/21 16:03
     **/
    public static void handleTenAuth() {
        TenApi tenApi = SpringUtil.getBean(TenApi.class);
        IdentityService identityService = SpringUtil.getBean(IdentityService.class);
        if(tenApi.getTenEnabled()) {
            identityService.setAuthentication(StpUtil.getLoginIdAsString(), null,
                    CollectionUtil.newArrayList(tenApi.getCurrentTenId()));
        } else {
            identityService.setAuthenticatedUserId(StpUtil.getLoginIdAsString());
        }
    }

    /**
     * 获取用户id
     */
    public static String getUserId() {
        try {
            String loginId = StpUtil.getLoginIdAsString();
            if (ObjectUtil.isNotEmpty(loginId)) {
                return loginId;
            } else {
                return "-1";
            }
        } catch (Exception e) {
            return "-1";
        }

    }

    /**
     * 处理使用【表单内的人】的节点的数据（排除当前所有任务的节点）
     *
     * @author xuyuxiang
     * @date 2023/7/19 9:33
     **/
    public static void handleUserTaskWithFormUserParticipateInfo(String processInstanceId, String processDefinitionId, String dataJson) {
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        Set<String> currentTaskDefinitionIdSet = taskService.createTaskQuery().processInstanceId(processInstanceId).list().stream()
                .map(Task::getTaskDefinitionKey).collect(Collectors.toSet());
        if(ObjectUtil.isEmpty(currentTaskDefinitionIdSet)) {
            throw new CommonException("数据错误，当前任务数量为空");
        }
        RepositoryService repositoryService = SpringUtil.getBean(RepositoryService.class);
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        repositoryService.getBpmnModelInstance(processDefinitionId)
                .getModelElementsByType(UserTask.class).forEach(userTask -> {
                    if(!isStartUserTask(userTask)) {
                        boolean isCurrentTaskDefinition = currentTaskDefinitionIdSet.contains(userTask.getId());
                        if(!isCurrentTaskDefinition) {
                            String userTaskParticipateInfo = getUserTaskParticipateInfo(userTask);
                            if (ObjectUtil.isNotEmpty(userTaskParticipateInfo)) {
                                boolean useFormUser = JSONUtil.parseArray(userTaskParticipateInfo).stream().anyMatch(o -> JSONUtil.toBean(JSONUtil.parseObj(o),
                                        FlwNode.FlwNodeParticipateProp.class).getKey().equals("FORM_USER"));
                                if(useFormUser) {
                                    List<String> participateUserIdList = handleUserTaskParticipateInfo(processInstanceId, userTaskParticipateInfo);
                                    if (ObjectUtil.isEmpty(participateUserIdList)) {
                                        handleUserTaskEmptyParticipateInfo(processInstanceId, userTask);
                                    } else {
                                        runtimeService.setVariable(processInstanceId, "assignee" + StrUtil.UNDERLINE + userTask.getId(), participateUserIdList);
                                    }
                                }
                            } else {
                                if(!"EMPTY".equals(userTask.getName())) {
                                    handleUserTaskEmptyParticipateInfo(processInstanceId, userTask);
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 根据用户任务获取当前节点的办理人配置数据
     *
     * @author xuyuxiang
     * @date 2023/7/19 10:37
     **/
    public static String getUserTaskParticipateInfo(UserTask userTask) {
        AtomicReference<String> participateInfo = new AtomicReference<>();
        ExtensionElements extensionElements = userTask.getExtensionElements();
        if (ObjectUtil.isNotEmpty(extensionElements)) {
            extensionElements.getChildElementsByType(CamundaProperties.class).forEach(camundaProperties ->
                    camundaProperties.getChildElementsByType(CamundaProperty.class).forEach(camundaProperty -> {
                        if (camundaProperty.getCamundaName().equals("participateInfo")) {
                            if(ObjectUtil.isNotEmpty(camundaProperty.getCamundaValue())) {
                                participateInfo.set(camundaProperty.getCamundaValue());
                            }
                        }
                    }));
        }
        return participateInfo.get();
    }
}
