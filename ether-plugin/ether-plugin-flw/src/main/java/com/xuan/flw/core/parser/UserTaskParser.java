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

import cn.hutool.core.util.ObjectUtil;
import org.camunda.bpm.model.bpmn.builder.*;
import com.xuan.common.exception.CommonException;
import com.xuan.flw.core.node.FlwNode;
import com.xuan.flw.core.util.NodeInfoUtil;
import com.xuan.flw.core.util.NodePropertyUtil;

public class UserTaskParser {

    /**
     * 构建单个用户任务节点
     *
     * @author xuyuxiang
     * @date 2022/3/24 9:24
     **/
    @SuppressWarnings("ALL")
    public static UserTaskBuilder buildUserTaskSingle(AbstractFlowNodeBuilder flowNodeBuilder, FlwNode flwNode) {
        NodeInfoUtil.validFlwNode(flwNode);
        UserTaskBuilder userTaskBuilder = flowNodeBuilder.userTask(flwNode.getId()).name(flwNode.getTitle());
        NodePropertyUtil.parseProperties(userTaskBuilder, flwNode);
        return userTaskBuilder;
    }

    /**
     * 用户任务节点->用户任务节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildUserTask(UserTaskBuilder userTaskBuilder, FlwNode flwNode) {
        UserTaskBuilder userTaskBuilderNew = buildUserTaskSingle(userTaskBuilder, flwNode);
        // 执行递归
        return executeRecursion(userTaskBuilderNew, flwNode);
    }

    /**
     * 服务任务节点->用户任务节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildUserTask(ServiceTaskBuilder serviceTaskBuilder, FlwNode flwNode) {
        UserTaskBuilder userTaskBuilderNew = buildUserTaskSingle(serviceTaskBuilder, flwNode);
        // 执行递归
        return executeRecursion(userTaskBuilderNew, flwNode);
    }

    /**
     * 条件连接线->用户任务节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildUserTask(ExclusiveGatewayBuilder exclusiveGatewayBuilder, FlwNode flwNode) {
        UserTaskBuilder userTaskBuilderNew = buildUserTaskSingle(exclusiveGatewayBuilder, flwNode);
        // 执行递归
        return executeRecursion(userTaskBuilderNew, flwNode);
    }

    /**
     * 并行网关->用户任务节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildUserTask(ParallelGatewayBuilder parallelGatewayBuilder, FlwNode flwNode) {
        UserTaskBuilder userTaskBuilderNew = buildUserTaskSingle(parallelGatewayBuilder, flwNode);
        // 执行递归
        return executeRecursion(userTaskBuilderNew, flwNode);
    }

    /**
     * 抽象节点->用户任务节点
     *
     * @author xuyuxiang
     * @date 2022/3/18 16:40
     **/
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder buildUserTask(AbstractFlowNodeBuilder abstractFlowNodeBuilder, FlwNode flwNode) {
        UserTaskBuilder userTaskBuilderNew = buildUserTaskSingle(abstractFlowNodeBuilder, flwNode);
        // 执行递归
        return executeRecursion(userTaskBuilderNew, flwNode);
    }

    /**
     * 执行递归
     *
     * @author xuyuxiang
     * @date 2022/3/21 23:21
     */
    @SuppressWarnings("ALL")
    public static AbstractFlowNodeBuilder executeRecursion(UserTaskBuilder userTaskBuilder, FlwNode flwNode) {
        // 获取子节点
        FlwNode childNode = flwNode.getChildNode();
        // 如果子节点为空则结束
        if(ObjectUtil.isEmpty(childNode) || ObjectUtil.isEmpty(childNode.getId())) {
            return userTaskBuilder;
        } else {
            if(NodeInfoUtil.isUserTask(childNode)) {
                // 如果子节点是用户任务
                return UserTaskParser.buildUserTask(userTaskBuilder, childNode);
            } else if(NodeInfoUtil.isServiceTask(childNode)) {
                // 如果子节点是服务任务
                return ServiceTaskParser.buildServiceTask(userTaskBuilder, childNode);
            } else if(NodeInfoUtil.isExclusiveGateway(childNode)) {
                // 如果子节点是排他网关
                return ExclusiveGatewayParser.buildExclusiveGateway(userTaskBuilder, childNode);
            } else if(NodeInfoUtil.isParallelGateway(childNode)) {
                // 如果子节点是并行网关
                return ParallelGatewayParser.buildParallelGateway(userTaskBuilder, childNode);
            } else {
                throw new CommonException("流程JSON解析格式错误，不支持的类型：{}", childNode.getType());
            }
        }
    }
}
