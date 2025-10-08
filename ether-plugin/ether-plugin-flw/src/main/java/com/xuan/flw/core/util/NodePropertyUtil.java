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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.UserTaskBuilder;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.CompletionCondition;
import org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.ModelInstance;
import com.xuan.common.exception.CommonException;
import com.xuan.flw.core.enums.NodeExecutionListenerKeyEnum;
import com.xuan.flw.core.enums.NodeTaskListenerKeyEnum;
import com.xuan.flw.core.node.FlwNode;

import java.util.List;

/**
 * 节点属性工具类，用于解析自定义属性
 *
 * @author xuyuxiang
 * @date 2022/5/4 12:17
 */
public class NodePropertyUtil {

    /**
     * 处理自定义属性
     *
     * @author xuyuxiang
     * @date 2022/5/4 12:19
     */
    @SuppressWarnings("ALL")
    public static void parseProperties(AbstractFlowNodeBuilder abstractFlowNodeBuilder, FlwNode flwNode) {
        ModelInstance modelInstance = abstractFlowNodeBuilder.getElement().getModelInstance();
        FlwNode.FlwNodeProperties properties = flwNode.getProperties();
        if(ObjectUtil.isNotEmpty(properties)) {
            // 定义扩展属性
            CamundaProperties camundaProperties = modelInstance.newInstance(CamundaProperties.class);
            // 添加基础配置
            addConfigInfo(abstractFlowNodeBuilder, modelInstance, properties, camundaProperties);
            // 添加参与人信息
            addParticipateInfo(modelInstance, properties, camundaProperties);
            // 添加按钮信息
            addButtonInfo(modelInstance, properties, camundaProperties);
            // 添加字段信息
            addFieldInfo(modelInstance, properties, camundaProperties);
            // 添加表单信息
            addFormInfo(modelInstance, properties, camundaProperties);
            // 添加执行监听器信息
            addExecutionListenerInfo(modelInstance, properties, camundaProperties, abstractFlowNodeBuilder);
            // 添加任务监听器信息
            addTaskListenerInfo(modelInstance, properties, camundaProperties, abstractFlowNodeBuilder);
            // 添加扩展属性
            abstractFlowNodeBuilder.addExtensionElement(camundaProperties);
        }
    }

    /**
     * 添加基础配置
     *
     * @author xuyuxiang
     * @date 2022/5/4 12:19
     */
    @SuppressWarnings("ALL")
    private static void addConfigInfo(AbstractFlowNodeBuilder abstractFlowNodeBuilder, ModelInstance modelInstance,
                                      FlwNode.FlwNodeProperties properties, CamundaProperties camundaProperties) {
        // 先保存节点的属性
        FlwNode.FlwNodeConfigProp configInfo = properties.getConfigInfo();
        if(ObjectUtil.isNotEmpty(configInfo)) {
            CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
            camundaProperty.setCamundaName("configInfo");
            camundaProperty.setCamundaValue(JSONUtil.toJsonStr(configInfo));
            camundaProperties.addChildElement(camundaProperty);
            // 如果是用户任务，则部分属性字段影响节点构造，需特殊处理
            BpmnModelElementInstance element = abstractFlowNodeBuilder.getElement();
            boolean userTaskFlag = NodeInfoUtil.isUserTask(element.getElementType().getTypeName());
            if(userTaskFlag) {
                // 转为用户任务
                UserTask userTask = (UserTask) element;
                if(ObjectUtil.isNotEmpty(properties.getConfigInfo())) {
                    if(ObjectUtil.isNotEmpty(properties.getConfigInfo().getUserTaskType())) {
                        // 如果任务为人工任务，才设置
                        if(properties.getConfigInfo().getUserTaskType().equals("ARTIFICIAL")) {
                            MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = modelInstance
                                    .newInstance(MultiInstanceLoopCharacteristics.class);
                            multiInstanceLoopCharacteristics.setCamundaCollection("assignee_" + userTask.getId());
                            multiInstanceLoopCharacteristics.setCamundaElementVariable("assignee");
                            userTask.setCamundaAssignee("${" + multiInstanceLoopCharacteristics.getCamundaElementVariable() + "}");
                            // 审批类型，SEQUENTIAL依次审批，COUNTERSIGN 会签（须所有审批人同意）, ORSIGN 或签（一名审批人同意或拒绝即可）
                            String userTaskMulApproveType = configInfo.getUserTaskMulApproveType();
                            // 定义完成条件
                            CompletionCondition completionCondition = modelInstance
                                    .newInstance(CompletionCondition.class);
                            switch (userTaskMulApproveType) {
                                case "SEQUENTIAL":
                                    multiInstanceLoopCharacteristics.setSequential(true);
                                    // 依次审批，完成条件为，已完成数量等于全数量
                                    completionCondition.setTextContent("${nrOfCompletedInstances == nrOfInstances}");
                                    break;
                                case "COUNTERSIGN":
                                    multiInstanceLoopCharacteristics.setSequential(false);
                                    // 会签，完成条件为，已完成数量等于全数量
                                    completionCondition.setTextContent("${nrOfCompletedInstances == nrOfInstances}");
                                    break;
                                case "ORSIGN":
                                    multiInstanceLoopCharacteristics.setSequential(false);
                                    // 或签，完成条件为，已完成数量等于1
                                    completionCondition.setTextContent("${nrOfCompletedInstances == 1}");
                                    break;
                                default:
                                    throw new CommonException("流程JSON解析格式错误，不支持的userTaskMulApproveType类型：{}", userTaskMulApproveType);
                            }
                            // 设置完成条件
                            multiInstanceLoopCharacteristics.setCompletionCondition(completionCondition);
                            // 设置多任务配置
                            userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加参与人信息
     *
     * @author xuyuxiang
     * @date 2022/5/4 15:03
     */
    private static void addParticipateInfo(ModelInstance modelInstance, FlwNode.FlwNodeProperties properties,
                                           CamundaProperties camundaProperties) {
        List<FlwNode.FlwNodeParticipateProp> participateInfo = properties.getParticipateInfo();
        if(ObjectUtil.isNotEmpty(participateInfo)) {
            CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
            camundaProperty.setCamundaName("participateInfo");
            camundaProperty.setCamundaValue(JSONUtil.toJsonStr(participateInfo));
            camundaProperties.addChildElement(camundaProperty);
        }
    }

    /**
     * 添加按钮信息
     *
     * @author xuyuxiang
     * @date 2022/5/4 15:03
     */
    private static void addButtonInfo(ModelInstance modelInstance, FlwNode.FlwNodeProperties properties,
                                     CamundaProperties camundaProperties) {
        List<FlwNode.FlwNodeButtonProp> buttonInfo = properties.getButtonInfo();
        if(ObjectUtil.isNotEmpty(buttonInfo)) {
            CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
            camundaProperty.setCamundaName("buttonInfo");
            camundaProperty.setCamundaValue(JSONUtil.toJsonStr(buttonInfo));
            camundaProperties.addChildElement(camundaProperty);
        }
    }

    /**
     * 添加字段信息
     *
     * @author xuyuxiang
     * @date 2022/5/4 15:03
     */
    private static void addFieldInfo(ModelInstance modelInstance, FlwNode.FlwNodeProperties properties,
                                    CamundaProperties camundaProperties) {
        List<FlwNode.FlwNodeFieldProp> fieldInfo = properties.getFieldInfo();
        if(ObjectUtil.isNotEmpty(fieldInfo)) {
            CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
            camundaProperty.setCamundaName("fieldInfo");
            camundaProperty.setCamundaValue(JSONUtil.toJsonStr(fieldInfo));
            camundaProperties.addChildElement(camundaProperty);
        }
    }

    /**
     * 添加表单信息
     *
     * @author xuyuxiang
     * @date 2022/5/4 15:03
     */
    private static void addFormInfo(ModelInstance modelInstance, FlwNode.FlwNodeProperties properties,
                                     CamundaProperties camundaProperties) {
        List<FlwNode.FlwNodeFormProp> formInfo = properties.getFormInfo();
        if(ObjectUtil.isNotEmpty(formInfo)) {
            CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
            camundaProperty.setCamundaName("formInfo");
            camundaProperty.setCamundaValue(JSONUtil.toJsonStr(formInfo));
            camundaProperties.addChildElement(camundaProperty);
        }
    }

    /**
     * 添加执行监听器信息
     *
     * @author xuyuxiang
     * @date 2022/5/4 15:03
     */
    @SuppressWarnings("ALL")
    private static void addExecutionListenerInfo(ModelInstance modelInstance, FlwNode.FlwNodeProperties properties,
                                                 CamundaProperties camundaProperties, AbstractFlowNodeBuilder abstractFlowNodeBuilder) {
        List<FlwNode.FlwNodeExecutionListenerProp> executionListenerInfo = properties.getExecutionListenerInfo();
        if(ObjectUtil.isNotEmpty(executionListenerInfo)) {
            CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
            camundaProperty.setCamundaName("executionListenerInfo");
            camundaProperty.setCamundaValue(JSONUtil.toJsonStr(executionListenerInfo));
            camundaProperties.addChildElement(camundaProperty);
            executionListenerInfo.forEach(flwNodeExecutionListenerProp -> {
                String listenerPropKey = flwNodeExecutionListenerProp.getKey();
                NodeExecutionListenerKeyEnum.validate(listenerPropKey);
                if(NodeExecutionListenerKeyEnum.START.getValue().equals(listenerPropKey) || NodeExecutionListenerKeyEnum
                        .END.getValue().equals(listenerPropKey) || NodeExecutionListenerKeyEnum.TAKE.getValue().equals(listenerPropKey)) {
                    abstractFlowNodeBuilder.camundaExecutionListenerClass(listenerPropKey.toLowerCase(),
                            flwNodeExecutionListenerProp.getValue());
                }
            });
        }
    }

    /**
     * 添加任务监听器信息
     *
     * @author xuyuxiang
     * @date 2022/5/4 15:03
     */
    @SuppressWarnings("ALL")
    private static void addTaskListenerInfo(ModelInstance modelInstance, FlwNode.FlwNodeProperties properties,
                                            CamundaProperties camundaProperties, AbstractFlowNodeBuilder abstractFlowNodeBuilder) {
        BpmnModelElementInstance element = abstractFlowNodeBuilder.getElement();
        boolean userTaskFlag = NodeInfoUtil.isUserTask(element.getElementType().getTypeName());
        if(userTaskFlag) {
            UserTaskBuilder userTaskBuilder = (UserTaskBuilder) abstractFlowNodeBuilder;
            List<FlwNode.FlwNodeTaskListenerProp> taskListenerInfo = properties.getTaskListenerInfo();
            if(ObjectUtil.isNotEmpty(taskListenerInfo)) {
                CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
                camundaProperty.setCamundaName("taskListenerInfo");
                camundaProperty.setCamundaValue(JSONUtil.toJsonStr(taskListenerInfo));
                camundaProperties.addChildElement(camundaProperty);
                taskListenerInfo.forEach(flwNodeTaskListenerProp -> {
                    NodeTaskListenerKeyEnum.validate(flwNodeTaskListenerProp.getKey());
                    userTaskBuilder.camundaTaskListenerClass(flwNodeTaskListenerProp.getKey().toLowerCase(),
                            flwNodeTaskListenerProp.getValue());
                });
            }
        }
    }
}
