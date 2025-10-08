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
package com.xuan.flw.core.parser;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.camunda.bpm.model.bpmn.builder.*;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.ModelInstance;
import com.xuan.common.exception.CommonException;
import com.xuan.flw.core.enums.NodeExecutionListenerKeyEnum;
import com.xuan.flw.core.node.FlwNode;
import com.xuan.flw.core.util.NodeInfoUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排他网关构造器
 *
 * @author xuyuxiang
 * @date 2022/3/21 19:28
 **/
public class ExclusiveGatewayParser {

    /**
     * 构建单个排他网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/24 9:24
     **/
    @SuppressWarnings("ALL")
    public static ExclusiveGatewayBuilder buildExclusiveGatewaySingle(AbstractFlowNodeBuilder flowNodeBuilder, FlwNode flwNode) {
        return flowNodeBuilder.exclusiveGateway(flwNode.getId()).name(flwNode.getTitle()).getElement().builder();
    }

    /**
     * 用户任务节点->排他网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildExclusiveGateway(UserTaskBuilder userTaskBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ExclusiveGatewayBuilder gatewayBuilder = buildExclusiveGatewaySingle(userTaskBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 服务任务节点->排他网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildExclusiveGateway(ServiceTaskBuilder serviceTaskBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ExclusiveGatewayBuilder gatewayBuilder = buildExclusiveGatewaySingle(serviceTaskBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 条件连接线->排他网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildExclusiveGateway(ExclusiveGatewayBuilder exclusiveGatewayBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ExclusiveGatewayBuilder gatewayBuilder = buildExclusiveGatewaySingle(exclusiveGatewayBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 并行网关->排他网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildExclusiveGateway(ParallelGatewayBuilder parallelGatewayBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ExclusiveGatewayBuilder gatewayBuilder = buildExclusiveGatewaySingle(parallelGatewayBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 抽象节点->排他网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildExclusiveGateway(AbstractFlowNodeBuilder abstractFlowNodeBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ExclusiveGatewayBuilder gatewayBuilder = buildExclusiveGatewaySingle(abstractFlowNodeBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 执行递归
     *
     * @author xuyuxiang
     * @date 2022/3/21 23:21
     */
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder executeRecursion(ExclusiveGatewayBuilder exclusiveGatewayBuilder, FlwNode flwNode) {
        // 获取子节点
        FlwNode childNode = flwNode.getChildNode();
        // 子节点不为空
        boolean childNodeNotEmpty = ObjectUtil.isNotEmpty(childNode) && ObjectUtil.isNotEmpty(childNode.getId());
        // 获取条件子节点
        List<FlwNode> conditionNodeList = flwNode.getConditionNodeList();
        // 如果条件子节点为空则报错
        if(ObjectUtil.isEmpty(conditionNodeList)) {
            throw new CommonException("流程JSON解析格式错误，排他网关：{}的条件子节点为空", flwNode.getId());
        } else {
            // 再对条件节点进行排序，根据优先级来，数字越大优先级别越高
            conditionNodeList = CollectionUtil.sort(conditionNodeList, Comparator.comparingInt(conditionNode -> conditionNode
                    .getProperties().getConfigInfo().getPriorityLevel()));
            // 定义条件子节点构造的结果
            List<AbstractFlowNodeBuilder> conditionNodeReusltBuilderList = CollectionUtil.newArrayList();
            // 定义条件构造结果
            ExclusiveGatewayBuilder oneConditionBuilder = null;
            // 遍历条件节点，逐条构造
            for (FlwNode conditionNode: conditionNodeList) {
                // 获取这个条件的子节点
                FlwNode oneConditionChildNode = conditionNode.getChildNode();
                // 先构造一个条件
                oneConditionBuilder = SequenceFlowParser.buildSequenceFlowSingle(exclusiveGatewayBuilder, conditionNode);

                // 再根据条件子节点类型继续构造
                if(ObjectUtil.isNotEmpty(oneConditionChildNode) && ObjectUtil.isNotEmpty(oneConditionChildNode.getId())) {
                    if(NodeInfoUtil.isUserTask(oneConditionChildNode)) {
                        // 如果子条件的子节点类型是用户任务
                        conditionNodeReusltBuilderList.add(UserTaskParser.buildUserTask(oneConditionBuilder, oneConditionChildNode));
                    } else if(NodeInfoUtil.isServiceTask(oneConditionChildNode)) {
                        // 如果子条件的子节点类型是服务任务
                        conditionNodeReusltBuilderList.add(ServiceTaskParser.buildServiceTask(oneConditionBuilder, oneConditionChildNode));
                    } else if(NodeInfoUtil.isExclusiveGateway(oneConditionChildNode)) {
                        // 如果子条件的子节点类型是排他网关
                        conditionNodeReusltBuilderList.add(ExclusiveGatewayParser.buildExclusiveGateway(oneConditionBuilder, oneConditionChildNode));
                    } else if(NodeInfoUtil.isParallelGateway(oneConditionChildNode)) {
                        // 如果子条件的子节点类型是并行网关
                        conditionNodeReusltBuilderList.add(ParallelGatewayParser.buildParallelGateway(oneConditionBuilder, oneConditionChildNode));
                    } else {
                        throw new CommonException("流程JSON解析格式错误，不支持的类型：{}", oneConditionChildNode.getType());
                    }
                } else {
                    // 如果条件节点的子节点为空，则构造一个自动通过节点
                    conditionNodeReusltBuilderList.add(UserTaskParser.buildUserTaskSingle(oneConditionBuilder, createAutoCompleteNode()));
                }
            }
            // 对条件连接线构造监听器属性
            if(ObjectUtil.isNotEmpty(oneConditionBuilder)) {
                List<FlwNode> finalConditionNodeList = conditionNodeList;
                oneConditionBuilder.getElement().getOutgoing().forEach(sequenceFlow -> {
                    FlwNode conditionNode = finalConditionNodeList.stream().filter(tempNode -> {
                        return tempNode.getId().equals(sequenceFlow.getId());
                    }).collect(Collectors.toList()).get(0);
                    SequenceFlowBuilder sequenceFlowBuilder = sequenceFlow.builder();
                    ModelInstance modelInstance = sequenceFlowBuilder.getElement().getModelInstance();
                    CamundaProperties camundaProperties = modelInstance.newInstance(CamundaProperties.class);
                    FlwNode.FlwNodeProperties properties = conditionNode.getProperties();
                    if(ObjectUtil.isNotEmpty(properties)) {
                        List<FlwNode.FlwNodeExecutionListenerProp> executionListenerInfo = properties.getExecutionListenerInfo();
                        if (ObjectUtil.isNotEmpty(executionListenerInfo)) {
                            CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
                            camundaProperty.setCamundaName("executionListenerInfo");
                            camundaProperty.setCamundaValue(JSONUtil.toJsonStr(executionListenerInfo));
                            camundaProperties.addChildElement(camundaProperty);
                            executionListenerInfo.forEach(flwNodeExecutionListenerProp -> {
                                NodeExecutionListenerKeyEnum.validate(flwNodeExecutionListenerProp.getKey());
                                CamundaExecutionListener camundaExecutionListener = modelInstance.newInstance(CamundaExecutionListener.class);
                                camundaExecutionListener.setCamundaEvent(flwNodeExecutionListenerProp.getKey().toLowerCase());
                                camundaExecutionListener.setCamundaClass(flwNodeExecutionListenerProp.getValue());
                                sequenceFlowBuilder.addExtensionElement(camundaExecutionListener);
                            });
                        }
                    }
                });
            }
            // 获取条件子节点构造集合中的任意一个
            AbstractFlowNodeBuilder conditionNodeReusltBuilderListZero = conditionNodeReusltBuilderList.get(0);
            // 定义条件子节点构造结果
            AbstractFlowNodeBuilder conditionNodeResultBuilder = UserTaskParser.buildUserTaskSingle(conditionNodeReusltBuilderListZero, createAutoCompleteNode());
            // 将其他的定义条件子节点构造结果与之连接
            conditionNodeReusltBuilderList.remove(conditionNodeReusltBuilderListZero);
            conditionNodeReusltBuilderList.forEach(abstractFlowNodeBuilder -> {
                abstractFlowNodeBuilder.connectTo(conditionNodeResultBuilder.getElement().getAttributeValue("id"));
            });
            // 构造直接子节点
            if(childNodeNotEmpty) {
                // 如果直接子节点不为空
                if(NodeInfoUtil.isUserTask(childNode)) {
                    // 如果子节点是用户任务
                    return UserTaskParser.buildUserTask(conditionNodeResultBuilder, childNode);
                } else if(NodeInfoUtil.isServiceTask(childNode)) {
                    // 如果子节点是服务任务
                    return ServiceTaskParser.buildServiceTask(conditionNodeResultBuilder, childNode);
                } else if(NodeInfoUtil.isExclusiveGateway(childNode)) {
                    // 如果子节点是排他网关
                    return ExclusiveGatewayParser.buildExclusiveGateway(conditionNodeResultBuilder, childNode);
                } else if(NodeInfoUtil.isParallelGateway(childNode)) {
                    // 如果子节点是并行网关
                    return ParallelGatewayParser.buildParallelGateway(conditionNodeResultBuilder, childNode);
                } else {
                    throw new CommonException("流程JSON解析格式错误，流程JSON解析格式错误，不支持的类型：{}", childNode.getType());
                }
            } else {
                // 如果直接子节点为空，则直接返回条件子节点构造结果
                return conditionNodeResultBuilder;
            }
        }
    }

    /**
     * 构造自动通过空节点
     *
     * @author xuyuxiang
     * @date 2023/5/19 13:35
     **/
    private static FlwNode createAutoCompleteNode() {
        FlwNode emptyNode = new FlwNode();
        emptyNode.setId("xn" + StrUtil.sub(IdUtil.fastUUID() , 2, 38));
        emptyNode.setType("EMPTY");
        emptyNode.setTitle("EMPTY");
        FlwNode.FlwNodeProperties flwNodeProperties = new FlwNode.FlwNodeProperties();
        FlwNode.FlwNodeConfigProp flwNodeConfigProp = new FlwNode.FlwNodeConfigProp();
        flwNodeConfigProp.setUserTaskType("EMPTY");
        flwNodeProperties.setConfigInfo(flwNodeConfigProp);
        emptyNode.setProperties(flwNodeProperties);
        return emptyNode;
    }
}
