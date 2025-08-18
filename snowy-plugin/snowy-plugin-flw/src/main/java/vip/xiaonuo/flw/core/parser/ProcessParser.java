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
import cn.hutool.json.JSONUtil;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.ModelInstance;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.flw.core.enums.NodeExecutionListenerKeyEnum;
import vip.xiaonuo.flw.core.listener.FlwGlobalEventListener;
import vip.xiaonuo.flw.core.node.FlwNode;
import vip.xiaonuo.flw.core.util.NodeInfoUtil;

import java.util.List;

/**
 * 流程构造器
 *
 * @author xuyuxiang
 * @date 2022/3/22 21:38
 */
public class ProcessParser {

    /**
     * 构造流程
     *
     * @author xuyuxiang
     * @date 2022/3/22 21:39
     */
    public static BpmnModelInstance buildBpmnModelInstance(String flwJson) {
        FlwNode process = JSONUtil.toBean(flwJson, FlwNode.class);
        NodeInfoUtil.validFlwNode(process);
        ProcessBuilder processBuilder = Bpmn.createExecutableProcess(process.getId()).name(process.getTitle());
        ModelInstance modelInstance = processBuilder.getElement().getModelInstance();
        CamundaProperties camundaProperties = modelInstance.newInstance(CamundaProperties.class);
        CamundaExecutionListener comProcessEndListener = modelInstance.newInstance(CamundaExecutionListener.class);
        comProcessEndListener.setCamundaEvent(NodeExecutionListenerKeyEnum.END.getValue().toLowerCase());
        comProcessEndListener.setCamundaClass(FlwGlobalEventListener.class.getName());
        processBuilder.addExtensionElement(comProcessEndListener);
        FlwNode.FlwNodeProperties properties = process.getProperties();
        if(ObjectUtil.isNotEmpty(properties)) {
            FlwNode.FlwNodeConfigProp configInfo = properties.getConfigInfo();
            if(ObjectUtil.isNotEmpty(configInfo)) {
                CamundaProperty camundaProperty = modelInstance.newInstance(CamundaProperty.class);
                camundaProperty.setCamundaName("configInfo");
                camundaProperty.setCamundaValue(JSONUtil.toJsonStr(configInfo));
                camundaProperties.addChildElement(camundaProperty);
                processBuilder.addExtensionElement(camundaProperties);
            }
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
                        CamundaExecutionListener camundaExecutionListener = modelInstance.newInstance(CamundaExecutionListener.class);
                        camundaExecutionListener.setCamundaEvent(listenerPropKey.toLowerCase());
                        camundaExecutionListener.setCamundaClass(flwNodeExecutionListenerProp.getValue());
                        processBuilder.addExtensionElement(camundaExecutionListener);
                    }
                });
            }
        }
        FlwNode flwNode = process.getChildNode();
        if(NodeInfoUtil.isStartEvent(flwNode)) {
            StartEventParser.buildStartEvent(processBuilder, flwNode);
            BpmnModelInstance bpmnModelInstance = processBuilder.done();
            EndEventParser.handleEndEvent(bpmnModelInstance);
            return bpmnModelInstance;
        } else {
            throw new CommonException("流程{}的第一个子节点类型必须为开始节点", process.getId());
        }
    }
}
