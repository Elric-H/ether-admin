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
package vip.xiaonuo.flw.core.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import vip.xiaonuo.flw.core.util.NodeRuntimeUtil;
import vip.xiaonuo.flw.modular.process.service.FlwProcessService;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 服务任务（抄送）节点监听器
 *
 * @author xuyuxiang
 * @date 2022/5/26 14:15
 **/
public class FlwServiceTaskListener implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        AtomicReference<String> participateInfo = new AtomicReference<>();
        List<String> participateUserIdList = CollectionUtil.newArrayList();
        delegateExecution.getBpmnModelElementInstance().getChildElementsByType(ExtensionElements.class).forEach(extensionElements ->
                extensionElements.getChildElementsByType(CamundaProperties.class).forEach(camundaProperties ->
                        camundaProperties.getChildElementsByType(CamundaProperty.class).forEach(camundaProperty -> {
                            if (camundaProperty.getCamundaName().equals("participateInfo")) {
                                participateInfo.set(camundaProperty.getCamundaValue());
                            }
                        })));
        if (ObjectUtil.isNotEmpty(participateInfo.get())) {
            participateUserIdList = NodeRuntimeUtil.handleUserTaskParticipateInfo(delegateExecution.getProcessInstanceId(), participateInfo.get());
        }
        SpringUtil.getBean(FlwProcessService.class).doCopy(delegateExecution.getProcessInstanceId(), participateUserIdList);
        // 发送抄送通知
        NodeRuntimeUtil.handleProcessCopyNotice(delegateExecution);
    }
}
