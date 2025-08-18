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
package vip.xiaonuo.flw.modular.process.service.impl;

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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.*;
import org.camunda.bpm.engine.migration.MigrationInstructionBuilder;
import org.camunda.bpm.engine.migration.MigrationPlanBuilder;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstanceModificationBuilder;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.pojo.CommonEntity;
import vip.xiaonuo.common.util.CommonTimeFormatUtil;
import vip.xiaonuo.flw.core.listener.FlwGlobalCustomEventCenter;
import vip.xiaonuo.flw.core.node.FlwNode;
import vip.xiaonuo.flw.core.util.NodeInfoUtil;
import vip.xiaonuo.flw.core.util.NodeRuntimeUtil;
import vip.xiaonuo.flw.modular.model.entity.FlwModel;
import vip.xiaonuo.flw.modular.model.enums.FlwModelFormTypeEnum;
import vip.xiaonuo.flw.modular.model.enums.FlwModelStatusEnum;
import vip.xiaonuo.flw.modular.model.service.FlwModelService;
import vip.xiaonuo.flw.modular.process.param.*;
import vip.xiaonuo.flw.modular.process.result.*;
import vip.xiaonuo.flw.modular.process.service.FlwProcessService;
import vip.xiaonuo.flw.modular.relation.entity.FlwRelation;
import vip.xiaonuo.flw.modular.relation.enums.FlwRelationCategoryEnum;
import vip.xiaonuo.flw.modular.relation.service.FlwRelationService;
import vip.xiaonuo.flw.modular.task.param.FlwTaskTurnParam;
import vip.xiaonuo.flw.modular.task.result.FlwTaskAttachmentResult;
import vip.xiaonuo.flw.modular.task.result.FlwTaskDetailResult;
import vip.xiaonuo.flw.modular.task.service.FlwTaskService;
import vip.xiaonuo.sys.api.SysOrgApi;
import vip.xiaonuo.sys.api.SysPositionApi;
import vip.xiaonuo.sys.api.SysUserApi;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/5/11 15:52
 **/
@Service
public class FlwProcessServiceImpl implements FlwProcessService {

    @Resource
    private SysUserApi sysUserApi;

    @Resource
    private SysOrgApi sysOrgApi;

    @Resource
    private SysPositionApi sysPositionApi;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private FlwModelService flwModelService;

    @Resource
    private FlwTaskService flwTaskService;

    @Resource
    private FlwRelationService flwRelationService;

    @Override
    public void saveDraft(FlwProcessSaveDraftParam flwProcessSaveDraftParam) {
        FlwRelation flwRelation = new FlwRelation();
        String id = flwProcessSaveDraftParam.getId();
        if(ObjectUtil.isNotEmpty(id)) {
            flwRelation = flwRelationService.getById(id);
            if(ObjectUtil.isEmpty(flwRelation)) {
                throw new CommonException("草稿不存在，id值为：{}", id);
            }
        }
        FlwModel flwModel = flwModelService.queryEntity(flwProcessSaveDraftParam.getModelId());
        flwRelation.setObjectId(StpUtil.getLoginIdAsString());
        flwRelation.setTargetId(flwModel.getId());
        if(ObjectUtil.isEmpty(id)) {
            flwRelation.setExtJson(JSONUtil.toJsonStr(JSONUtil.createObj().set("createTime", DateUtil.now())
                    .set("title", flwModel.getName())
                    .set("dataJson", flwProcessSaveDraftParam.getDataJson())));
        } else {
            flwRelation.setExtJson(JSONUtil.toJsonStr(JSONUtil.parseObj(flwRelation.getExtJson()).set("dataJson",
                    flwProcessSaveDraftParam.getDataJson())));
        }
        flwRelation.setCategory(FlwRelationCategoryEnum.FLW_USER_TO_DRAFT.getValue());
        flwRelationService.saveOrUpdate(flwRelation);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void start(FlwProcessStartParam flwProcessStartParam) {
        NodeRuntimeUtil.handleTenAuth();
        String modelId = flwProcessStartParam.getModelId();
        FlwModel flwModel = flwModelService.queryEntity(modelId);
        if (flwModel.getModelStatus().equals(FlwModelStatusEnum.DISABLED.getValue())) {
            throw new CommonException("模型未启用，名称为：{}", flwModel.getName());
        }
        JSONObject jsonObject = JSONUtil.createObj();
        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        jsonObject.set("initiator", ObjectUtil.isEmpty(flwProcessStartParam.getInitiator()) ?
                loginUser.getId() : flwProcessStartParam.getInitiator());
        jsonObject.set("initiatorName", ObjectUtil.isEmpty(flwProcessStartParam.getInitiatorName()) ?
                loginUser.getName() : flwProcessStartParam.getInitiatorName());
        jsonObject.set("initiatorOrgId", ObjectUtil.isEmpty(flwProcessStartParam.getInitiatorOrgId()) ?
                loginUser.getOrgId() : flwProcessStartParam.getInitiatorOrgId());
        jsonObject.set("initiatorOrgName", ObjectUtil.isEmpty(flwProcessStartParam.getInitiatorOrgName()) ?
                sysOrgApi.getNameById(jsonObject.getStr("initiatorOrgId")) : flwProcessStartParam.getInitiatorOrgName());
        jsonObject.set("initiatorPositionId", ObjectUtil.isEmpty(flwProcessStartParam.getInitiatorPositionId()) ?
                loginUser.getPositionId() : flwProcessStartParam.getInitiatorPositionId());
        jsonObject.set("initiatorPositionName", ObjectUtil.isEmpty(flwProcessStartParam.getInitiatorPositionName()) ?
                sysPositionApi.getNameById(jsonObject.getStr("initiatorPositionId")) : flwProcessStartParam.getInitiatorPositionName());
        jsonObject.set("initiatorTime", DateUtil.now());
        jsonObject.set("initiatorModelInfo", JSONUtil.createObj().set("initiatorModelId", flwModel.getId())
                .set("initiatorModelJson", flwModel.getProcessJson()).set("initiatorFormJson", flwModel.getFormJson())
                .set("initiatorFormType", flwModel.getFormType()));
        if(flwModel.getFormType().equals(FlwModelFormTypeEnum.DESIGN.getValue())) {
            if(ObjectUtil.isEmpty(flwModel.getTableJson())) {
                throw new CommonException("模型错误，设计表单类型的模型数据库表JSON不能为空");
            }
            if(ObjectUtil.isNotEmpty(flwProcessStartParam.getDataJson())) {
                String parentTablePrimaryId = IdWorker.getIdStr();
                JSONObject resultDataObj = NodeRuntimeUtil.handleInitiatorDataJson(flwModel.getTableJson(),
                        flwProcessStartParam.getDataJson(), parentTablePrimaryId);
                String initiatorSql = resultDataObj.getStr("sql");
                String initiatorDataJson = resultDataObj.getStr("data");
                jsonObject.set("initiatorSql", initiatorSql);
                jsonObject.set("initiatorTableJson", flwModel.getTableJson());
                jsonObject.set("initiatorDataJson", initiatorDataJson);
                runtimeService.startProcessInstanceById(flwModel.getDefinitionId(), parentTablePrimaryId, jsonObject);
                NodeRuntimeUtil.handleSqlExecution(initiatorSql);
            } else {
                runtimeService.startProcessInstanceById(flwModel.getDefinitionId(), jsonObject);
            }
        } else {
            if(ObjectUtil.isNotEmpty(flwProcessStartParam.getDataJson())) {
                if(!JSONUtil.isTypeJSONObject(flwProcessStartParam.getDataJson())) {
                    throw new CommonException("填写的数据请使用JSON格式");
                }
                jsonObject.set("initiatorDataJson", flwProcessStartParam.getDataJson());
                // 当自定义表单传入的json中有id主键时
                String id = JSONUtil.parseObj(flwProcessStartParam.getDataJson()).getStr("id");
                if(ObjectUtil.isNotEmpty(id)) {
                    // 则将其与流程关联，查看act_hi_procinst的business_key字段，可用于业务表联表查询
                    runtimeService.startProcessInstanceById(flwModel.getDefinitionId(), id, jsonObject);
                } else {
                    runtimeService.startProcessInstanceById(flwModel.getDefinitionId(), jsonObject);
                }
            } else {
                runtimeService.startProcessInstanceById(flwModel.getDefinitionId(), jsonObject);
            }
        }
    }

    @Override
    public Page<FlwProcessMyDraftResult> myDraftPage(FlwProcessMyDraftPageParam flwProcessMyDraftPageParam) {
        LambdaQueryWrapper<FlwRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询当前用户的
        lambdaQueryWrapper.eq(CommonEntity::getCreateUser, StpUtil.getLoginIdAsString());
        if(ObjectUtil.isNotEmpty(flwProcessMyDraftPageParam.getName())) {
            lambdaQueryWrapper.like(FlwRelation::getExtJson, flwProcessMyDraftPageParam.getName());
        }
        lambdaQueryWrapper.eq(FlwRelation::getCategory, FlwRelationCategoryEnum.FLW_USER_TO_DRAFT.getValue());
        Page<FlwRelation> page = flwRelationService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
        return new Page<FlwProcessMyDraftResult>(page.getCurrent(), page.getSize()).setRecords(page.getRecords().stream().map(flwRelation -> {
            String extJson = flwRelation.getExtJson();
            JSONObject jsonObject = JSONUtil.parseObj(extJson);
            String title = jsonObject.getStr("title");
            String createTime = jsonObject.getStr("createTime");
            FlwProcessMyDraftResult flwProcessMyDraftResult = new FlwProcessMyDraftResult();
            flwProcessMyDraftResult.setId(flwRelation.getId());
            flwProcessMyDraftResult.setModelId(flwRelation.getTargetId());
            flwProcessMyDraftResult.setDataJson(extJson);
            flwProcessMyDraftResult.setTitle(title);
            flwProcessMyDraftResult.setCreateTime(createTime);
            return flwProcessMyDraftResult;
        }).collect(Collectors.toList()));
    }

    @Override
    public FlwProcessDraftDetailResult draftDetail(FlwProcessDraftIdParam flwProcessDraftIdParam) {
        String id = flwProcessDraftIdParam.getId();
        FlwRelation flwRelation = flwRelationService.getById(id);
        if(ObjectUtil.isEmpty(flwRelation)) {
            throw new CommonException("草稿不存在，id值为：{}", id);
        }
        FlwModel flwModel = flwModelService.queryEntity(flwRelation.getTargetId());
        FlwProcessDraftDetailResult flwProcessDraftDetailResult = new FlwProcessDraftDetailResult();
        flwProcessDraftDetailResult.setId(id);
        flwProcessDraftDetailResult.setModelId(flwModel.getId());
        flwProcessDraftDetailResult.setFormType(flwModel.getFormType());
        flwProcessDraftDetailResult.setProcessJson(flwModel.getProcessJson());
        flwProcessDraftDetailResult.setFormJson(flwModel.getFormJson());
        flwProcessDraftDetailResult.setDataJson(JSONUtil.parseObj(flwRelation.getExtJson()).getStr("dataJson"));
        return flwProcessDraftDetailResult;
    }

    @Override
    public void deleteDraft(List<FlwProcessDraftIdParam> flwProcessDraftIdParamList) {
        List<String> draftIdList = flwProcessDraftIdParamList.stream().map(FlwProcessDraftIdParam::getId).collect(Collectors.toList());
        if(ObjectUtil.isNotEmpty(draftIdList)) {
            flwRelationService.removeBatchByIds(draftIdList);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<FlwProcessResult> monitorPage(FlwProcessMonitorPageParam flwProcessMonitorPageParam) {
        NodeRuntimeUtil.handleTenAuth();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime().desc();
        if(ObjectUtil.isNotEmpty(flwProcessMonitorPageParam.getInitiator())) {
            historicProcessInstanceQuery.startedBy(flwProcessMonitorPageParam.getInitiator());
        }
        if (ObjectUtil.isNotEmpty(flwProcessMonitorPageParam.getSearchKey())) {
            historicProcessInstanceQuery.variableValueLike("initiatorName", "%" + flwProcessMonitorPageParam.getSearchKey() + "%");
        }
        if (ObjectUtil.isNotEmpty(flwProcessMonitorPageParam.getName())) {
            historicProcessInstanceQuery.processDefinitionNameLike("%" + flwProcessMonitorPageParam.getName() + "%");
        }
        if (ObjectUtil.isNotEmpty(flwProcessMonitorPageParam.getProcessInstanceIdList())) {
            historicProcessInstanceQuery.processInstanceIds(CollectionUtil.newHashSet(flwProcessMonitorPageParam.getProcessInstanceIdList()));
        }

        Page<FlwProcessResult> defaultPage = CommonPageRequest.defaultPage();
        long current = defaultPage.getCurrent();
        long size = defaultPage.getSize();
        List<FlwProcessResult> flwProcessResultList = historicProcessInstanceQuery
                .listPage(Convert.toInt((current - 1) * size), Convert.toInt(size)).stream()
                .map(historicProcessInstance -> {
                    FlwProcessResult flwProcessResult = new FlwProcessResult();
                    flwProcessResult.setId(historicProcessInstance.getId());
                    JSONObject variableJsonObject = JSONUtil.createObj();
                    historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list()
                            .forEach(historicVariableInstance -> variableJsonObject.set(historicVariableInstance.getName(),
                                    historicVariableInstance.getValue()));
                    flwProcessResult.setSn(variableJsonObject.getStr("initiatorProcessSn"));
                    flwProcessResult.setTitle(variableJsonObject.getStr("initiatorProcessTitle"));
                    flwProcessResult.setAbstractTitle(variableJsonObject.getStr("initiatorProcessAbstract"));
                    flwProcessResult.setBusinessKey(historicProcessInstance.getBusinessKey());
                    flwProcessResult.setProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());
                    flwProcessResult.setProcessDefinitionName(historicProcessInstance.getProcessDefinitionName());
                    flwProcessResult.setProcessDefinitionVersion(StrUtil.format("V{}.0", historicProcessInstance.getProcessDefinitionVersion()));
                    flwProcessResult.setStartTime(DateUtil.formatDateTime(historicProcessInstance.getStartTime()));
                    flwProcessResult.setEndTime(ObjectUtil.isNotEmpty(historicProcessInstance.getEndTime()) ?
                            DateUtil.formatDateTime(historicProcessInstance.getEndTime()) : "-");
                    if (ObjectUtil.isNotEmpty(historicProcessInstance.getDurationInMillis())) {
                        flwProcessResult.setDurationInfo(CommonTimeFormatUtil
                                .formatSeconds(historicProcessInstance.getDurationInMillis() / 1000));
                    } else {
                        flwProcessResult.setDurationInfo("-");
                    }
                    flwProcessResult.setInitiator(variableJsonObject.getStr("initiator"));
                    flwProcessResult.setInitiatorName(variableJsonObject.getStr("initiatorName"));
                    flwProcessResult.setInitiatorOrgId(variableJsonObject.getStr("initiatorOrgId"));
                    flwProcessResult.setInitiatorOrgName(variableJsonObject.getStr("initiatorOrgName"));
                    flwProcessResult.setInitiatorPositionId(variableJsonObject.getStr("initiatorPositionId"));
                    flwProcessResult.setInitiatorPositionName(variableJsonObject.getStr("initiatorPositionName"));
                    String state = historicProcessInstance.getState();
                    switch (state) {
                        case "ACTIVE":
                            flwProcessResult.setStateCode(state);
                            flwProcessResult.setStateText("审批中");
                            break;
                        case "SUSPENDED":
                            flwProcessResult.setStateCode(state);
                            flwProcessResult.setStateText("已挂起");
                            break;
                        case "COMPLETED":
                            flwProcessResult.setStateCode(state);
                            flwProcessResult.setStateText("已完成");
                            break;
                        case "EXTERNALLY_TERMINATED":
                            String deleteReason = historicProcessInstance.getDeleteReason();
                            switch (deleteReason) {
                                case "END":
                                    flwProcessResult.setStateCode(deleteReason);
                                    flwProcessResult.setStateText("已终止");
                                    break;
                                case "REVOKE":
                                    flwProcessResult.setStateCode(deleteReason);
                                    flwProcessResult.setStateText("已撤回");
                                    break;
                                case "REJECT":
                                    flwProcessResult.setStateCode(deleteReason);
                                    flwProcessResult.setStateText("已拒绝");
                                    break;
                                default:
                                    throw new CommonException("流程实例状态错误，id值为：{}", historicProcessInstance.getId());
                            }
                            break;
                        case "INTERNALLY_TERMINATED":
                            flwProcessResult.setStateCode(state);
                            flwProcessResult.setStateText("内部终止");
                            break;
                        default:
                            throw new CommonException("流程实例状态错误，id值为：{}", historicProcessInstance.getId());
                    }
                    if(state.equals("ACTIVE")) {
                        Set<String> currentActivityNames = CollectionUtil.newHashSet();
                        List<JSONObject> currentActivityNameJsonObjectList = CollectionUtil.newArrayList();
                        taskService.createTaskQuery().processInstanceId(historicProcessInstance.getId()).list().forEach(task -> {
                            String assignee = task.getAssignee();
                            if(ObjectUtil.isNotEmpty(assignee)) {
                                currentActivityNameJsonObjectList.add(JSONUtil.createObj().set("taskName", task.getName()).set("assignee", assignee));
                            }
                        });
                        final List<String>[] assigneeNameList = new List[]{CollectionUtil.newArrayList()};
                        currentActivityNameJsonObjectList.stream().collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("taskName"))).forEach((key, value) -> {
                            List<String> assigneeList = value.stream().map(jsonObject -> jsonObject.getStr("assignee")).collect(Collectors.toList());
                            assigneeNameList[0] = sysUserApi.getUserListByIdListWithoutException(CollectionUtil.reverse(assigneeList)).stream()
                                    .map(jsonObject -> jsonObject.getStr("name")).collect(Collectors.toList());
                            currentActivityNames.add(key + StrUtil.BRACKET_START + StrUtil.join("、", assigneeNameList[0]) + StrUtil.BRACKET_END);
                        });
                        flwProcessResult.setCurrentActivityNames(StrUtil.join("、", CollectionUtil.newArrayList(currentActivityNames)));
                        flwProcessResult.setAssignees(StrUtil.join("、", assigneeNameList[0]));
                    } else {
                        flwProcessResult.setCurrentActivityNames("-");
                        flwProcessResult.setAssignees("-");
                    }
                    return flwProcessResult;
                })
                .collect(Collectors.toList());
        defaultPage.setTotal(historicProcessInstanceQuery.count());
        defaultPage.setRecords(flwProcessResultList);
        return defaultPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FlwProcessIdParam> flwProcessIdParamList) {
        NodeRuntimeUtil.handleTenAuth();
        List<String> processInstanceIdList = flwProcessIdParamList.stream()
                .map(FlwProcessIdParam::getId).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(processInstanceIdList)) {
            // 发布删除事件
            processInstanceIdList.forEach(FlwGlobalCustomEventCenter::publishDeleteEvent);
            // 执行删除
            runtimeService.deleteProcessInstancesIfExists(processInstanceIdList, "DELETE",
                    true, true, true);
            historyService.deleteHistoricProcessInstancesIfExists(processInstanceIdList);
            flwRelationService.remove(new LambdaQueryWrapper<FlwRelation>().in(FlwRelation::getObjectId, processInstanceIdList));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void end(List<FlwProcessIdParam> flwProcessIdParamList) {
        NodeRuntimeUtil.handleTenAuth();
        List<String> processInstanceIdList = flwProcessIdParamList.stream()
                .map(FlwProcessIdParam::getId).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(processInstanceIdList)) {
            processInstanceIdList.forEach(processInstanceId -> {
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();
                if (ObjectUtil.isNull(historicProcessInstance)) {
                    throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
                }
                if(!historicProcessInstance.getState().equals("ACTIVE")) {
                    throw new CommonException("该流程实例不在运行中无法终止，id值为：{}", processInstanceId);
                }
                // 发布终止事件
                FlwGlobalCustomEventCenter.publishCloseEvent(processInstanceId);
            });
            // 执行终止
            runtimeService.deleteProcessInstancesIfExists(processInstanceIdList, "END",
                    true, true, true);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void revoke(List<FlwProcessIdParam> flwProcessIdParamList) {
        NodeRuntimeUtil.handleTenAuth();
        List<String> processInstanceIdList = flwProcessIdParamList.stream()
                .map(FlwProcessIdParam::getId).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(processInstanceIdList)) {
            processInstanceIdList.forEach(processInstanceId -> {
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();
                if (ObjectUtil.isNull(historicProcessInstance)) {
                    throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
                }
                if(!historicProcessInstance.getState().equals("ACTIVE")) {
                    throw new CommonException("该流程实例不在运行中无法撤回，id值为：{}", processInstanceId);
                }
                FlwNode.FlwNodeConfigProp flwNodeConfigProp = getProcessNodeConfigProp(processInstanceId);
                Boolean enableRevoke = flwNodeConfigProp.getProcessEnableRevoke();
                if(!enableRevoke) {
                    throw new CommonException("该流程已配置不允许撤回，id值为：{}", processInstanceId);
                }
                // 发布撤回事件
                FlwGlobalCustomEventCenter.publishRevokeEvent(processInstanceId);
                // 执行撤回
                runtimeService.deleteProcessInstanceIfExists(processInstanceId, "REVOKE",
                        true, true, true, true);
            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void suspend(List<FlwProcessIdParam> flwProcessIdParamList) {
        NodeRuntimeUtil.handleTenAuth();
        List<String> processInstanceIdList = flwProcessIdParamList.stream()
                .map(FlwProcessIdParam::getId).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(processInstanceIdList)) {
            processInstanceIdList.forEach(processInstanceId -> {
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();
                if (ObjectUtil.isNull(historicProcessInstance)) {
                    throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
                }
                if(!historicProcessInstance.getState().equals("ACTIVE")) {
                    throw new CommonException("该流程实例不在运行中无法挂起，id值为：{}", processInstanceId);
                }
                runtimeService.suspendProcessInstanceById(processInstanceId);
            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void active(List<FlwProcessIdParam> flwProcessIdParamList) {
        NodeRuntimeUtil.handleTenAuth();
        List<String> processInstanceIdList = flwProcessIdParamList.stream()
                .map(FlwProcessIdParam::getId).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(processInstanceIdList)) {
            processInstanceIdList.forEach(processInstanceId -> {
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();
                if (ObjectUtil.isNull(historicProcessInstance)) {
                    throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
                }
                if(!historicProcessInstance.getState().equals("SUSPENDED")) {
                    throw new CommonException("该流程实例不在挂起状态无法激活，id值为：{}", processInstanceId);
                }
                runtimeService.activateProcessInstanceById(processInstanceId);
            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void turn(FlwProcessTurnParam flwProcessTurnParam) {
        NodeRuntimeUtil.handleTenAuth();
        String processInstanceId = flwProcessTurnParam.getId();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (ObjectUtil.isNull(historicProcessInstance)) {
            throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
        }
        if(!historicProcessInstance.getState().equals("ACTIVE")) {
            throw new CommonException("该流程实例不在运行中无法转办，id值为：{}", processInstanceId);
        }
        taskService.createTaskQuery().processInstanceId(processInstanceId).list().stream().map(Task::getId)
                .collect(Collectors.toList()).forEach(taskId -> {
            FlwTaskTurnParam flwTaskTurnParam = new FlwTaskTurnParam();
            flwTaskTurnParam.setId(taskId);
            flwTaskTurnParam.setUserId(flwProcessTurnParam.getUserId());
            flwTaskTurnParam.setRecordComment(false);
            flwTaskService.turn(flwTaskTurnParam, false);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void jump(FlwProcessJumpParam flwProcessJumpParam) {
        NodeRuntimeUtil.handleTenAuth();
        String processInstanceId = flwProcessJumpParam.getId();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (ObjectUtil.isNull(historicProcessInstance)) {
            throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
        }
        if(!historicProcessInstance.getState().equals("ACTIVE")) {
            throw new CommonException("该流程实例不在运行中无法跳转，id值为：{}", processInstanceId);
        }
        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        String targetActivityId = flwProcessJumpParam.getTargetActivityId();
        UserTask jumpUserTask = repositoryService.getBpmnModelInstance(processDefinitionId).getModelElementById(targetActivityId);
        // 执行跳转
        this.doProcessInstanceModify(processInstanceId, jumpUserTask);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void restart(FlwProcessRestartParam flwProcessRestartParam) {
        NodeRuntimeUtil.handleTenAuth();
        String processInstanceId = flwProcessRestartParam.getId();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (ObjectUtil.isNull(historicProcessInstance)) {
            throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
        }
        if(!historicProcessInstance.getState().equals("COMPLETED") && !historicProcessInstance.getState().equals("EXTERNALLY_TERMINATED")
                && !historicProcessInstance.getState().equals("INTERNALLY_TERMINATED")) {
            throw new CommonException("该流程实例不在结束、终止、撤回、拒绝状态无法复活，id值为：{}", processInstanceId);
        }
        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        String targetActivityId = flwProcessRestartParam.getTargetActivityId();
        UserTask restartUserTask = repositoryService.getBpmnModelInstance(processDefinitionId).getModelElementById(targetActivityId);
        Collection<SequenceFlow> incoming = restartUserTask.getIncoming();
        if(incoming.size() != 1) {
            throw new CommonException("流程模型数据错误，节点{}入口存在多个", restartUserTask.getName());
        }
        runtimeService.restartProcessInstances(processDefinitionId)
                .processInstanceIds(processInstanceId)
                .startTransition(incoming.iterator().next().getId())
                .execute();
        FlwProcessIdParam flwProcessIdParam = new FlwProcessIdParam();
        flwProcessIdParam.setId(processInstanceId);
        this.delete(CollectionUtil.newArrayList(flwProcessIdParam));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void migrate(FlwProcessMigrateParam flwProcessMigrateParam) {
        // TODO 流程迁移存在部分问题，待后续完善
        NodeRuntimeUtil.handleTenAuth();
        String processInstanceId = flwProcessMigrateParam.getId();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        String targetActivityId = flwProcessMigrateParam.getTargetActivityId();
        if (ObjectUtil.isNull(historicProcessInstance)) {
            throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
        }
        if(!historicProcessInstance.getState().equals("ACTIVE")) {
            throw new CommonException("该流程实例不在运行中无法迁移，id值为：{}", processInstanceId);
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        /*if(taskList.size() != 1) {
            throw new CommonException("流程实例当前处于多实例节点无法迁移，id值为：{}", processInstanceId);
        }*/
        Set<String> taskDefinitionKeySet = taskList.stream().map(Task::getTaskDefinitionKey)
                .collect(Collectors.toSet());
        if(taskDefinitionKeySet.size() != 1) {
            throw new CommonException("流程实例当前处于多实例节点无法迁移，id值为：{}", processInstanceId);
        }
        String taskDefinitionKey = taskDefinitionKeySet.iterator().next();
        FlwModel processUseModel = this.getProcessLastModel(processInstanceId);
        JSONObject jsonObject = JSONUtil.parseObj(runtimeService.getVariables(processInstanceId));
        jsonObject.set("initiatorModelInfo", JSONUtil.createObj().set("initiatorModelId",
                processUseModel.getId()).set("initiatorModelJson", processUseModel.getProcessJson()).set("initiatorFormJson",
                processUseModel.getFormJson()) .set("initiatorFormType", processUseModel.getFormType()));
        MigrationPlanBuilder migrationPlanBuilder = runtimeService.createMigrationPlan(historicProcessInstance.getProcessDefinitionId(),
                processUseModel.getDefinitionId())
                .setVariables(jsonObject);
        UserTask currentUserTask = repositoryService.getBpmnModelInstance(historicProcessInstance.getProcessDefinitionId()).getModelElementById(taskDefinitionKey);
        UserTask migrateUserTask = repositoryService.getBpmnModelInstance(processUseModel.getDefinitionId()).getModelElementById(targetActivityId);
        if(!isTaskStart(currentUserTask) && !isTaskStart(migrateUserTask)) {
            ModelElementInstance currentMultiInstanceLoopCharacteristics = currentUserTask.getUniqueChildElementByType(MultiInstanceLoopCharacteristics.class);
            if(ObjectUtil.isEmpty(currentMultiInstanceLoopCharacteristics)) {
                throw new CommonException("模型数据错误，当前节点：{}多实例属性为空", currentUserTask.getName());
            }
            ModelElementInstance migrateMultiInstanceLoopCharacteristics = migrateUserTask.getUniqueChildElementByType(MultiInstanceLoopCharacteristics.class);
            if(ObjectUtil.isEmpty(migrateMultiInstanceLoopCharacteristics)) {
                throw new CommonException("模型数据错误，目标节点：{}多实例属性为空", currentUserTask.getName());
            }
            String currentIsSequential = currentMultiInstanceLoopCharacteristics.getAttributeValue("isSequential");
            String targetIsSequential = migrateMultiInstanceLoopCharacteristics.getAttributeValue("isSequential");
            if(!currentIsSequential.equals(targetIsSequential)) {
                throw new CommonException("迁移失败，当前节点和目标节点多实例属性不一致");
            }
        }
        MigrationInstructionBuilder migrationInstructionBuilder;
        if(isTaskStart(migrateUserTask)) {
            migrationInstructionBuilder = migrationPlanBuilder.mapActivities(taskDefinitionKey, targetActivityId);
        } else {
            migrationInstructionBuilder = migrationPlanBuilder.mapActivities(taskDefinitionKey, targetActivityId)
                    .mapActivities(taskDefinitionKey +  "#multiInstanceBody", targetActivityId + "#multiInstanceBody");
        }
        runtimeService.newMigration(migrationInstructionBuilder.build()).processInstanceIds(processInstanceId).execute();
        taskList.forEach(task -> {
            try {
                boolean hasTask = taskService.createTaskQuery().taskId(task.getId()).list().size() > 0;
                if(hasTask) {
                    taskService.complete(task.getId());
                }
            } catch (Exception ignored) {
                // 忽略或签任务的办理错误
            }
        });
    }

    @Override
    public Page<FlwProcessVariableResult> variablePage(FlwProcessIdParam flwProcessIdParam) {
        NodeRuntimeUtil.handleTenAuth();
        HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService
                .createHistoricVariableInstanceQuery().processInstanceId(flwProcessIdParam.getId())
                .orderByVariableName().desc();
        Page<FlwProcessVariableResult> defaultPage = CommonPageRequest.defaultPage();
        long current = defaultPage.getCurrent();
        long size = defaultPage.getSize();
        List<FlwProcessVariableResult> flwProcessVariableResultList = historicVariableInstanceQuery
                .listPage(Convert.toInt((current - 1) * size), Convert.toInt(size)).stream()
                .map(historicVariableInstance -> {
                    FlwProcessVariableResult flwProcessVariableResult = new FlwProcessVariableResult();
                    flwProcessVariableResult.setId(historicVariableInstance.getId());
                    flwProcessVariableResult.setProcessInstanceId(historicVariableInstance.getProcessInstanceId());
                    flwProcessVariableResult.setExecutionId(historicVariableInstance.getExecutionId());
                    flwProcessVariableResult.setTypeName(historicVariableInstance.getTypeName());
                    flwProcessVariableResult.setName(historicVariableInstance.getName());
                    flwProcessVariableResult.setValue(Convert.toStr(historicVariableInstance.getValue()));
                    return flwProcessVariableResult;
                })
                .collect(Collectors.toList());
        defaultPage.setTotal(historicVariableInstanceQuery.count());
        defaultPage.setRecords(flwProcessVariableResultList);
        return defaultPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void variableUpdateBatch(FlwProcessVariableUpdateParam flwProcessVariableUpdateParam) {
        NodeRuntimeUtil.handleTenAuth();
        String processInstanceId = flwProcessVariableUpdateParam.getProcessInstanceId();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (ObjectUtil.isNull(historicProcessInstance)) {
            throw new CommonException("流程实例不存在，id值为：{}", processInstanceId);
        }
        if(!historicProcessInstance.getState().equals("ACTIVE")) {
            throw new CommonException("该流程实例不在运行中无法设置变量，id值为：{}", processInstanceId);
        }
        flwProcessVariableUpdateParam.getVariableInfoList().forEach(flwProcessVariable -> {
            String executionId = flwProcessVariable.getExecutionId();
            List<Execution> executionList = runtimeService.createExecutionQuery().executionId(executionId).list();
            if(ObjectUtil.isEmpty(executionList)) {
                throw new CommonException("执行实例不存在或已结束，无法设置变量，id值为：{}", executionId);
            }
            String variableValue = flwProcessVariable.getVariableValue();
            Object resultValue;
            String typeName = flwProcessVariable.getTypeName().toLowerCase();
            switch (typeName) {
                case "string":
                    resultValue = Convert.toStr(variableValue);
                    break;
                case "integer":
                    resultValue = Convert.toInt(variableValue);
                    break;
                case "boolean":
                    resultValue = Convert.toBool(variableValue);
                    break;
                case "long":
                    resultValue = Convert.toLong(variableValue);
                    break;
                case "double":
                    resultValue = Convert.toDouble(variableValue);
                    break;
                case "object":
                    if (JSONUtil.isTypeJSONObject(variableValue)) {
                        resultValue = JSONUtil.parseObj(variableValue);
                    } else if (JSONUtil.isTypeJSONArray(variableValue)) {
                        resultValue = JSONUtil.parseArray(variableValue);
                    } else if (ObjectUtil.isNotEmpty(Convert.toFloat(variableValue))) {
                        resultValue = Convert.toFloat(variableValue);
                    } else if (variableValue.length() == 1) {
                        resultValue = Convert.toChar(variableValue);
                    } else {
                        throw new CommonException("变量类型：{}不支持该变量值：", typeName, variableValue);
                    }
                    break;
                default:
                    throw new CommonException("不支持的变量类型：{}", typeName);
            }
            runtimeService.setVariable(flwProcessVariable.getExecutionId(), flwProcessVariable.getVariableKey(), resultValue);
        });
    }

    @Override
    public FlwProcessDetailResult detail(FlwProcessIdParam flwProcessIdParam) {
        NodeRuntimeUtil.handleTenAuth();
        FlwProcessDetailResult flwProcessDetailResult = new FlwProcessDetailResult();
        String processInstanceId = flwProcessIdParam.getId();
        flwProcessDetailResult.setModelId(this.getProcessModelIdVariable(processInstanceId));
        flwProcessDetailResult.setProcessInstanceId(processInstanceId);
        flwProcessDetailResult.setFormType(this.getProcessFormTypeVariable(processInstanceId));
        flwProcessDetailResult.setInitiatorModelJson(this.getProcessModelJsonVariable(processInstanceId));
        flwProcessDetailResult.setInitiatorFormJson(this.getProcessFormJsonVariable(processInstanceId));
        flwProcessDetailResult.setInitiatorDataJson(this.getProcessDataJsonVariable(processInstanceId));
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        List<Comment> processInstanceCommentList = taskService.getProcessInstanceComments(processInstanceId);
        List<FlwTaskDetailResult.FlwProcessComment> commonList = CollectionUtil.newArrayList();
        historicTaskInstanceList.forEach(historicTaskInstance -> {
            getTaskCommentList(processInstanceCommentList, historicTaskInstance.getId()).forEach(taskComment -> {
                FlwTaskDetailResult.FlwProcessComment flwProcessComment = new FlwTaskDetailResult.FlwProcessComment();
                flwProcessComment.setTaskId(historicTaskInstance.getId());
                flwProcessComment.setTaskName(historicTaskInstance.getName());
                JSONObject commonObject = JSONUtil.parseObj(taskComment.getFullMessage());
                flwProcessComment.setActivityId(historicTaskInstance.getTaskDefinitionKey());
                flwProcessComment.setUserId(taskComment.getUserId());
                flwProcessComment.setUserName(commonObject.getStr("USER_NAME"));
                flwProcessComment.setOperateType(commonObject.getStr("OPERATE_TYPE"));
                flwProcessComment.setOperateText(commonObject.getStr("OPERATE_TEXT"));
                flwProcessComment.setComment(ObjectUtil.isNotEmpty(commonObject.getStr("COMMENT"))?commonObject.getStr("COMMENT") : "无");
                flwProcessComment.setApproveTime(DateUtil.formatDateTime(taskComment.getTime()));
                flwProcessComment.setAttachmentList(taskService.getTaskAttachments(historicTaskInstance.getId()).stream().map(attachment -> {
                  FlwTaskAttachmentResult flwTaskAttachmentResult = new FlwTaskAttachmentResult();
                  flwTaskAttachmentResult.setAttachmentName(attachment.getName());
                  flwTaskAttachmentResult.setAttachmentUrl(attachment.getUrl());
                  return flwTaskAttachmentResult;
                }).collect(Collectors.toList()));
                commonList.add(flwProcessComment);
            });
        });
        CollectionUtil.sort(commonList, Comparator.comparing(FlwTaskDetailResult.FlwProcessComment::getApproveTime));
        flwProcessDetailResult.setCommentList(commonList);
        return flwProcessDetailResult;
    }

    @Override
    public JSONObject getProcessModelInfoVariable(String processInstanceId) {
        HistoricVariableInstance initiatorModelInfo = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).variableName("initiatorModelInfo").singleResult();
        if (ObjectUtil.isEmpty(initiatorModelInfo)) {
            throw new CommonException("数据错误，流程模型initiatorModelInfo数据快照不存在，id值为：{}", processInstanceId);
        }
        Object initiatorModelInfoValue = initiatorModelInfo.getValue();
        if (ObjectUtil.isEmpty(initiatorModelInfoValue)) {
            throw new CommonException("数据错误，流程模型initiatorModelInfo数据快照不存在，id值为：{}", processInstanceId);
        }
        return JSONUtil.parseObj(initiatorModelInfoValue);
    }

    @Override
    public String getProcessModelIdVariable(String processInstanceId) {
        return this.getProcessModelInfoVariable(processInstanceId).getStr("initiatorModelId");
    }

    @Override
    public String getProcessModelJsonVariable(String processInstanceId) {
        return this.getProcessModelInfoVariable(processInstanceId).getStr("initiatorModelJson");
    }

    @Override
    public FlwNode getProcessModelJsonNodeVariable(String processInstanceId) {
        return JSONUtil.toBean(this.getProcessModelJsonVariable(processInstanceId), FlwNode.class);
    }

    @Override
    public String getProcessFormJsonVariable(String processInstanceId) {
        return this.getProcessModelInfoVariable(processInstanceId).getStr("initiatorFormJson");
    }

    @Override
    public String getProcessFormTypeVariable(String processInstanceId) {
        return this.getProcessModelInfoVariable(processInstanceId).getStr("initiatorFormType");
    }

    @Override
    public FlwModel getProcessLastModel(String processInstanceId) {
        String processModelIdVariable = this.getProcessModelIdVariable(processInstanceId);
        if (ObjectUtil.isEmpty(processModelIdVariable)) {
            throw new CommonException("数据错误，流程模型initiatorModelId数据快照不存在，id值为：{}", processInstanceId);
        }
        return flwModelService.validModel(processModelIdVariable);
    }

    @Override
    public String getProcessDataJsonVariable(String processInstanceId) {
        HistoricVariableInstance initiatorDataJson = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).variableName("initiatorDataJson").singleResult();
        if (ObjectUtil.isNotEmpty(initiatorDataJson)) {
            return Convert.toStr(initiatorDataJson.getValue());
        }
        return null;
    }

    @Override
    public FlwNode.FlwNodeConfigProp getProcessNodeConfigProp(String processInstanceId) {
        return this.getProcessModelJsonNodeVariable(processInstanceId).getProperties().getConfigInfo();
    }

    @Override
    public void doCopy(String processInstanceId, List<String> userIdList) {
        List<FlwRelation> flwRelationList = CollectionUtil.newArrayList();
        String extJson = JSONUtil.toJsonStr(JSONUtil.createObj().set("IS_READ", false));
        userIdList.forEach(targetId -> {
            FlwRelation flwRelation = new FlwRelation();
            flwRelation.setObjectId(processInstanceId);
            flwRelation.setTargetId(targetId);
            flwRelation.setCategory(FlwRelationCategoryEnum.FLW_COPY_PROCESS_TO_USER.getValue());
            flwRelation.setExtJson(extJson);
            flwRelationList.add(flwRelation);
        });
       flwRelationService.saveBatch(flwRelationList);
    }

    @Override
    public List<String> getMyCopyUnreadProcessInstanceIdList() {
        return flwRelationService.list(new LambdaQueryWrapper<FlwRelation>().eq(FlwRelation::getTargetId, StpUtil.getLoginIdAsString())
                .eq(FlwRelation::getCategory, FlwRelationCategoryEnum.FLW_COPY_PROCESS_TO_USER.getValue())
                .eq(FlwRelation::getExtJson, JSONUtil.toJsonStr(JSONUtil.createObj().set("IS_READ", false))))
                .stream().map(FlwRelation::getObjectId).collect(Collectors.toList());
    }

    @Override
    public void readMyCopyProcess(List<FlwProcessIdParam> flwProcessIdParamList) {
        List<String> processIdList = flwProcessIdParamList.stream().map(FlwProcessIdParam::getId).collect(Collectors.toList());
        flwRelationService.update(new LambdaUpdateWrapper<FlwRelation>().eq(FlwRelation::getTargetId,
                StpUtil.getLoginIdAsString()).in(FlwRelation::getObjectId, processIdList).set(FlwRelation::getExtJson,
                JSONUtil.toJsonStr(JSONUtil.createObj().set("IS_READ", true))));
    }

    @Override
    public List<String> getMyCopyHasReadProcessInstanceIdList() {
        return flwRelationService.list(new LambdaQueryWrapper<FlwRelation>().eq(FlwRelation::getTargetId, StpUtil.getLoginIdAsString())
                .eq(FlwRelation::getCategory, FlwRelationCategoryEnum.FLW_COPY_PROCESS_TO_USER.getValue())
                .eq(FlwRelation::getExtJson, JSONUtil.toJsonStr(JSONUtil.createObj().set("IS_READ", true))))
                .stream().map(FlwRelation::getObjectId).collect(Collectors.toList());
    }

    @Override
    public void deleteMyHasReadProcess(List<FlwProcessIdParam> flwProcessIdParamList) {
        List<String> processIdList = flwProcessIdParamList.stream().map(FlwProcessIdParam::getId).collect(Collectors.toList());
        flwRelationService.remove(new LambdaUpdateWrapper<FlwRelation>().eq(FlwRelation::getTargetId,
                StpUtil.getLoginIdAsString()).in(FlwRelation::getObjectId, processIdList));
    }

    @Override
    public List<FlwProcessJumpNodeResult> getCanJumpNodeInfoList(FlwProcessIdParam flwProcessIdParam) {
        FlwNode flwNode = this.getProcessModelJsonNodeVariable(flwProcessIdParam.getId());
        return NodeInfoUtil.getUserTaskFlwNodeList(flwNode).stream().map(tempFlwNode -> {
            FlwProcessJumpNodeResult flwProcessJumpNodeResult = new FlwProcessJumpNodeResult();
            flwProcessJumpNodeResult.setId(tempFlwNode.getId());
            flwProcessJumpNodeResult.setName(tempFlwNode.getTitle());
            return flwProcessJumpNodeResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FlwProcessRestartNodeResult> getCanRestartNodeInfoList(FlwProcessIdParam flwProcessIdParam) {
        FlwNode flwNode = this.getProcessModelJsonNodeVariable(flwProcessIdParam.getId());
        return NodeInfoUtil.getUserTaskFlwNodeList(flwNode).stream().map(tempFlwNode -> {
            FlwProcessRestartNodeResult flwProcessRestartNodeResult = new FlwProcessRestartNodeResult();
            flwProcessRestartNodeResult.setId(tempFlwNode.getId());
            flwProcessRestartNodeResult.setName(tempFlwNode.getTitle());
            return flwProcessRestartNodeResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FlwProcessRestartNodeResult> getCanMigrateNodeInfoList(FlwProcessIdParam flwProcessIdParam) {
        FlwModel processUseModel = this.getProcessLastModel(flwProcessIdParam.getId());
        FlwNode flwNode = JSONUtil.toBean(processUseModel.getProcessJson(), FlwNode.class);
        return NodeInfoUtil.getUserTaskFlwNodeList(flwNode).stream().map(tempFlwNode -> {
            FlwProcessRestartNodeResult flwProcessRestartNodeResult = new FlwProcessRestartNodeResult();
            flwProcessRestartNodeResult.setId(tempFlwNode.getId());
            flwProcessRestartNodeResult.setName(tempFlwNode.getTitle());
            return flwProcessRestartNodeResult;
        }).collect(Collectors.toList());
    }

    /**
     * 执行流程实例修改
     *
     * @author xuyuxiang
     * @date 2023/5/23 18:13
     **/
    @Override
    public void doProcessInstanceModify(String processInstanceId, UserTask userTask) {
        // 创建流程实体修改构造器
        ProcessInstanceModificationBuilder processInstanceModificationBuilder = runtimeService
                .createProcessInstanceModification(processInstanceId);
        // 取消当前所有正在执行的节点
        taskService.createTaskQuery().processInstanceId(processInstanceId).list().stream().map(Task::getTaskDefinitionKey)
                .collect(Collectors.toSet()).forEach(processInstanceModificationBuilder::cancelAllForActivity);
        Collection<SequenceFlow> incoming = userTask.getIncoming();
        if(incoming.size() != 1) {
            throw new CommonException("流程模型数据错误，节点{}入口存在多个", userTask.getName());
        }
        // 执行
        processInstanceModificationBuilder.startTransition(incoming.iterator().next().getId()).execute();
    }

    @Override
    public List<Tree<String>> orgTreeSelector() {
        return sysOrgApi.orgTreeSelector();
    }

    @SuppressWarnings("ALL")
    @Override
    public Page<FlwProcessUserResult> userSelector(FlwProcessSelectorUserParam flwProcessSelectorUserParam) {
        return BeanUtil.toBean(sysUserApi.userSelector(flwProcessSelectorUserParam.getOrgId(), flwProcessSelectorUserParam.getSearchKey()), Page.class);
    }

    @Override
    public FlwModel modelDetail(FlwProcessModelIdParam flwProcessModelIdParam) {
        return flwModelService.queryEntity(flwProcessModelIdParam.getId());
    }

    /**
     * 获取任务审批记录
     *
     * @author xuyuxiang
     * @date 2020/8/13 16:35
     **/
    public List<Comment> getTaskCommentList(List<Comment> originDataList, String taskId) {
        return originDataList.stream().filter(comment -> comment.getTaskId().equals(taskId)).collect(Collectors.toList());
    }

    /**
     * 判断任务是否为发起申请
     *
     * @author xuyuxiang
     * @date 2022/6/9 19:52
     */
    public static boolean isTaskStart(UserTask userTask) {
        List<FlowNode> flowNodeList = userTask.getPreviousNodes().list();
        if(ObjectUtil.isNotEmpty(flowNodeList) && flowNodeList.size() == 1) {
            return flowNodeList.get(0).getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_START_EVENT);
        }
        return false;
    }
}
