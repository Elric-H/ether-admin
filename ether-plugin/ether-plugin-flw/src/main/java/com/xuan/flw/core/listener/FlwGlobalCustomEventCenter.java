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
package com.xuan.flw.core.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import com.xuan.flw.core.enums.NodeExecutionListenerKeyEnum;
import com.xuan.flw.core.node.FlwNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 流程全局自定义事件中心 事件发布器
 *
 * @author xuyuxiang
 * @date 2023/3/3 10:14
 **/
public class FlwGlobalCustomEventCenter {


    /**
     * 获取流程实例
     *
     * @author xuyuxiang
     * @date 2023/8/18 1:22
     */
    private static HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        return SpringUtil.getBean(HistoryService.class).createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
    }

    /**
     * 获取流程实例配置的自定义事件监听器集合
     *
     * @author xuyuxiang
     * @date 2023/8/18 1:19
     */
    private static List<FlwGlobalCustomEventListener> getCustomEventListenerList(HistoricProcessInstance historicProcessInstance, String eventName) {
        List<FlwGlobalCustomEventListener> resultList = CollectionUtil.newArrayList();
        RepositoryService repositoryService = SpringUtil.getBean(RepositoryService.class);
        Collection<ExtensionElements> extensionElementsCollection =repositoryService.getBpmnModelInstance(historicProcessInstance.getProcessDefinitionId())
                .getDocumentElement().getUniqueChildElementByType(Process.class).getChildElementsByType(ExtensionElements.class);
        if(ObjectUtil.isNotEmpty(extensionElementsCollection)) {
            extensionElementsCollection.forEach(extensionElements ->
                    extensionElements.getChildElementsByType(CamundaProperties.class).forEach(camundaProperties ->
                            camundaProperties.getChildElementsByType(CamundaProperty.class).forEach(camundaProperty -> {
                                if (camundaProperty.getCamundaName().equals("executionListenerInfo")) {
                                    List<FlwNode.FlwNodeExecutionListenerProp> flwNodeExecutionListenerProps = JSONUtil
                                            .toList(JSONUtil.parseArray(camundaProperty.getCamundaValue()), FlwNode.FlwNodeExecutionListenerProp.class);
                                    if(ObjectUtil.isNotEmpty(flwNodeExecutionListenerProps)) {
                                        Map<String, FlwGlobalCustomEventListener> flwGlobalCustomEventListenerMap = SpringUtil
                                                .getBeansOfType(FlwGlobalCustomEventListener.class);
                                        flwNodeExecutionListenerProps.forEach(flwNodeExecutionListenerProp -> {
                                            if(flwNodeExecutionListenerProp.getKey().equals(eventName)) {
                                                String listenerPropValue = flwNodeExecutionListenerProp.getValue();
                                                flwGlobalCustomEventListenerMap.forEach((key, value) -> {
                                                    if(value.getClass().getName().equals(listenerPropValue)) {
                                                        resultList.add(value);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            })));
        }
        return resultList;
    }

    /**
     * 执行流程拒绝事件发布
     *
     * @author xuyuxiang
     * @date 2023/3/3 10:22
     **/
    public static void publishRejectEvent(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance(processInstanceId);
        if(ObjectUtil.isNotEmpty(historicProcessInstance)) {
            getCustomEventListenerList(historicProcessInstance, NodeExecutionListenerKeyEnum.REJECT.getValue()).forEach(flwGlobalCustomEventListener -> flwGlobalCustomEventListener
                    .publishRejectEvent(historicProcessInstance));
        }
    }

    /**
     * 执行流程终止事件发布
     *
     * @author xuyuxiang
     * @date 2023/3/3 10:22
     **/
    public static void publishCloseEvent(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance(processInstanceId);
        if(ObjectUtil.isNotEmpty(historicProcessInstance)) {
            getCustomEventListenerList(historicProcessInstance, NodeExecutionListenerKeyEnum.CLOSE.getValue()).forEach(flwGlobalCustomEventListener -> flwGlobalCustomEventListener
                    .publishCloseEvent(historicProcessInstance));
        }
    }

    /**
     * 执行流程撤回事件发布
     *
     * @author xuyuxiang
     * @date 2023/3/3 10:22
     **/
    public static void publishRevokeEvent(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance(processInstanceId);
        if(ObjectUtil.isNotEmpty(historicProcessInstance)) {
            getCustomEventListenerList(historicProcessInstance, NodeExecutionListenerKeyEnum.REVOKE.getValue()).forEach(flwGlobalCustomEventListener -> flwGlobalCustomEventListener
                    .publishRevokeEvent(historicProcessInstance));
        }
    }

    /**
     * 执行流程删除事件发布
     *
     * @author xuyuxiang
     * @date 2023/3/3 10:22
     **/
    public static void publishDeleteEvent(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance(processInstanceId);
        if(ObjectUtil.isNotEmpty(historicProcessInstance)) {
            getCustomEventListenerList(historicProcessInstance, NodeExecutionListenerKeyEnum.DELETE.getValue()).forEach(flwGlobalCustomEventListener -> flwGlobalCustomEventListener
                    .publishDeleteEvent(historicProcessInstance));
        }
    }
}
