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
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.ModelInstance;
import com.xuan.common.exception.CommonException;
import com.xuan.flw.core.node.FlwNode;
import com.xuan.flw.core.parser.ProcessParser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 节点信息工具类
 *
 * @author xuyuxiang
 * @date 2023/5/11 0:18
 */
public class NodeInfoUtil {

    private static final String START_TASK = "startTask";

    public static boolean isStartEvent(FlwNode flwNode) {
        return BpmnModelConstants.BPMN_ELEMENT_START_EVENT.equals(flwNode.getType());
    }

    public static boolean isStartTask(FlwNode flwNode) {
        return START_TASK.equals(flwNode.getType());
    }

    public static boolean isUserTask(FlwNode flwNode) {
        return BpmnModelConstants.BPMN_ELEMENT_USER_TASK.equals(flwNode.getType());
    }

    public static boolean isServiceTask(FlwNode flwNode) {
        return BpmnModelConstants.BPMN_ELEMENT_SERVICE_TASK.equals(flwNode.getType());
    }

    public static boolean isExclusiveGateway(FlwNode flwNode) {
        return BpmnModelConstants.BPMN_ELEMENT_EXCLUSIVE_GATEWAY.equals(flwNode.getType());
    }

    public static boolean isParallelGateway(FlwNode flwNode) {
        return BpmnModelConstants.BPMN_ELEMENT_PARALLEL_GATEWAY.equals(flwNode.getType());
    }

    public static boolean isStartEvent(ModelInstance modelInstance) {
        return BpmnModelConstants.BPMN_ELEMENT_START_EVENT.equals(modelInstance.getDocumentElement().getElementType().getTypeName());
    }

    public static boolean isUserTask(String typeName) {
        return BpmnModelConstants.BPMN_ELEMENT_USER_TASK.equals(typeName);
    }

    public static boolean isServiceTask(String typeName) {
        return BpmnModelConstants.BPMN_ELEMENT_SERVICE_TASK.equals(typeName);
    }

    public static boolean isExclusiveGateway(String typeName) {
        return BpmnModelConstants.BPMN_ELEMENT_EXCLUSIVE_GATEWAY.equals(typeName);
    }

    public static boolean isParallelGateway(String typeName) {
        return BpmnModelConstants.BPMN_ELEMENT_PARALLEL_GATEWAY.equals(typeName);
    }

    public static boolean isEndEvent(String typeName) {
        return BpmnModelConstants.BPMN_ELEMENT_END_EVENT.equals(typeName);
    }

    public static void validFlwNode(FlwNode flwNode) {
        String id = flwNode.getId();
        String type = flwNode.getType();
        String title = flwNode.getTitle();
        if(ObjectUtil.isAllEmpty(id, type, title)) {
            throw new CommonException("流程JSON解析格式错误，某节点的id、type、title均为空");
        }
        if(ObjectUtil.hasEmpty(id, type, title)) {
            throw new CommonException("流程JSON解析格式错误，存在某节点的id、type、title为空");
        }
    }

    /**
     * 根据流程节点信息获取用户节点信息集合
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:56
     **/
    public static List<FlwNode> getUserTaskFlwNodeList(FlwNode flwNode) {
        return ProcessParser.buildBpmnModelInstance(JSONUtil.toJsonStr(flwNode)).getModelElementsByType(UserTask.class).stream()
                .filter(userTask -> !userTask.getName().equals("EMPTY")).map(userTask -> {
                    FlwNode tempFlwNode = new FlwNode();
                    tempFlwNode.setId(userTask.getId());
                    tempFlwNode.setTitle(userTask.getName());
                    return tempFlwNode;
                }).collect(Collectors.toList());
    }
}
