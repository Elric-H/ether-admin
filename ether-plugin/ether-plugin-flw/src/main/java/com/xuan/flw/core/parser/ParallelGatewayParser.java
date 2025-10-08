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
import cn.hutool.core.util.ObjectUtil;
import org.camunda.bpm.model.bpmn.builder.*;
import com.xuan.common.exception.CommonException;
import com.xuan.flw.core.node.FlwNode;
import com.xuan.flw.core.util.NodeInfoUtil;

import java.util.List;

/**
 * 并行网关构造器
 *
 * @author xuyuxiang
 * @date 2022/3/23 15:53
 **/
public class ParallelGatewayParser {

    /**
     * 构建单个并行网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/24 9:24
     **/
    @SuppressWarnings("ALL")
    public static ParallelGatewayBuilder buildParallelGatewaySingle(AbstractFlowNodeBuilder flowNodeBuilder, FlwNode flwNode) {
        if(ObjectUtil.isNotEmpty(flwNode) && ObjectUtil.isNotEmpty(flwNode.getId())) {
            return flowNodeBuilder.parallelGateway(flwNode.getId()).name(flwNode.getTitle());
        } else {
            return flowNodeBuilder.parallelGateway();
        }
    }


    /**
     * 用户任务节点->并行网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildParallelGateway(UserTaskBuilder userTaskBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ParallelGatewayBuilder gatewayBuilder = buildParallelGatewaySingle(userTaskBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 服务任务节点->并行网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildParallelGateway(ServiceTaskBuilder serviceTaskBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ParallelGatewayBuilder gatewayBuilder = buildParallelGatewaySingle(serviceTaskBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 条件连接线->并行网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildParallelGateway(ExclusiveGatewayBuilder exclusiveGatewayBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ParallelGatewayBuilder gatewayBuilder = buildParallelGatewaySingle(exclusiveGatewayBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 并行网关->并行网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildParallelGateway(ParallelGatewayBuilder parallelGatewayBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ParallelGatewayBuilder gatewayBuilder = buildParallelGatewaySingle(parallelGatewayBuilder, flwNode);
        // 执行递归
        return executeRecursion(gatewayBuilder, flwNode);
    }

    /**
     * 抽象节点->并行网关节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildParallelGateway(AbstractFlowNodeBuilder abstractFlowNodeBuilder, FlwNode flwNode) {
        // 先构造一个网关
        ParallelGatewayBuilder gatewayBuilder = buildParallelGatewaySingle(abstractFlowNodeBuilder, flwNode);
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
    public static AbstractFlowNodeBuilder executeRecursion(ParallelGatewayBuilder parallelGatewayBuilder, FlwNode flwNode) {
        // 获取子节点
        FlwNode childNode = flwNode.getChildNode();
        // 子节点不为空
        boolean childNodeNotEmpty = ObjectUtil.isNotEmpty(childNode) && ObjectUtil.isNotEmpty(childNode.getId());
        // 获取条件子节点
        List<FlwNode> conditionNodeList = flwNode.getConditionNodeList();
        // 如果条件子节点为空则报错
        if(ObjectUtil.isEmpty(conditionNodeList)) {
            throw new CommonException("流程JSON解析格式错误，并行网关：{}的条件子节点为空", flwNode.getId());
        } else {
            // 先获取条件子节点构造的结果
            List<AbstractFlowNodeBuilder> conditionNodeReusltBuilderList = CollectionUtil.newArrayList();
            // 遍历条件节点，逐条构造
            for (FlwNode conditionNode: conditionNodeList) {
                if(NodeInfoUtil.isUserTask(conditionNode)) {
                    // 如果条件子节点类型是用户任务
                    conditionNodeReusltBuilderList.add(UserTaskParser.buildUserTask(parallelGatewayBuilder, conditionNode));
                } else {
                    throw new CommonException("流程JSON解析格式错误，并行网关{}的条件节点类型必须为用户任务", flwNode.getId());
                }
            }
            // 先获取到条件子节点构造集合中的任意一个
            AbstractFlowNodeBuilder conditionNodeReusltBuilderListZero = conditionNodeReusltBuilderList.get(0);
            // 定义条件子节点构造结果
            AbstractFlowNodeBuilder conditionNodeResultBuilder = ParallelGatewayParser.buildParallelGatewaySingle(conditionNodeReusltBuilderListZero, null);
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
}
