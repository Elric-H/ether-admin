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
package vip.xiaonuo.flw.core.parser;

import cn.hutool.core.util.ObjectUtil;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;
import org.camunda.bpm.model.bpmn.builder.UserTaskBuilder;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.flw.core.node.FlwNode;
import vip.xiaonuo.flw.core.util.NodeInfoUtil;
import vip.xiaonuo.flw.core.util.NodePropertyUtil;

/**
 * 开始节点构造器
 *
 * @author xuyuxiang
 * @date 2022/3/22 21:36
 */
public class StartEventParser {

    /**
     * 构造开始节点
     *
     * @author xuyuxiang
     * @date 2022/3/22 21:40
     */
    public static void buildStartEvent(ProcessBuilder processBuilder, FlwNode flwNode) {
        NodeInfoUtil.validFlwNode(flwNode);
        // 构造开始节点
        StartEventBuilder startEventBuilder = processBuilder.startEvent(flwNode.getId()).name(flwNode.getTitle())
                .camundaInitiator("initiator");

        // 获取发起节点
        FlwNode childNode = flwNode.getChildNode();
        if(NodeInfoUtil.isStartTask(childNode)) {
            // 构造发起节点，处理人为${initiator}
            UserTaskBuilder startTaskBuilder = startEventBuilder.userTask(childNode.getId()).name(childNode.getTitle())
                    .camundaAssignee("${initiator}");

            // 处理发起节点的属性
            NodePropertyUtil.parseProperties(startTaskBuilder, childNode);

            // 获取后续的子节点
            FlwNode secondChildNode = childNode.getChildNode();
            if(ObjectUtil.isEmpty(secondChildNode) || ObjectUtil.isEmpty(secondChildNode.getId())) {
                startTaskBuilder.endEvent();
            } else if(NodeInfoUtil.isUserTask(secondChildNode)) {
                UserTaskParser.buildUserTask(startTaskBuilder, secondChildNode);
            } else if(NodeInfoUtil.isServiceTask(secondChildNode)) {
                ServiceTaskParser.buildServiceTask(startTaskBuilder, secondChildNode);
            } else if(NodeInfoUtil.isExclusiveGateway(secondChildNode)) {
                ExclusiveGatewayParser.buildExclusiveGateway(startTaskBuilder, secondChildNode);
            } else if(NodeInfoUtil.isParallelGateway(secondChildNode)) {
                ParallelGatewayParser.buildParallelGateway(startTaskBuilder, secondChildNode);
            } else {
                throw new CommonException("流程JSON解析格式错误，不支持的类型：{}", secondChildNode.getType());
            }
        } else {
            throw new CommonException("开始节点{}的子节点类型必须为发起节点", flwNode.getId());
        }
    }
}
