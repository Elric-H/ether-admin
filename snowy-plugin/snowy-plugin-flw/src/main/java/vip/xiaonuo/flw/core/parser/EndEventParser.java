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
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;

import java.util.Collection;

/**
 * 结束节点构造器
 *
 * @author xuyuxiang
 * @date 2022/3/22 20:57
 */
public class EndEventParser {

    /**
     * 处理最后所有没有结束节点的任务节点
     *
     * @author xuyuxiang
     * @date 2022/3/22 20:58
     */
    public static void handleEndEvent(BpmnModelInstance bpmnModelInstance) {
        // 遍历处理所有的任务节点，如果其目标节点为空，则给其追加endEvent
        Collection<Task> taskCollection = bpmnModelInstance.getModelElementsByType(Task.class);
        for (Task task: taskCollection) {
            Collection<SequenceFlow> outgoing = task.getOutgoing();
            if(ObjectUtil.isEmpty(outgoing)) {
                task.builder().endEvent();
            }
        }
        // 遍历处理所有的网关节点，如果其目标节点为空，则给其追加endEvent
        Collection<Gateway> gatewayCollection = bpmnModelInstance.getModelElementsByType(Gateway.class);
        for (Gateway gateway: gatewayCollection) {
            Collection<SequenceFlow> outgoing = gateway.getOutgoing();
            if(ObjectUtil.isEmpty(outgoing)) {
                gateway.builder().endEvent();
            }
        }
    }
}
