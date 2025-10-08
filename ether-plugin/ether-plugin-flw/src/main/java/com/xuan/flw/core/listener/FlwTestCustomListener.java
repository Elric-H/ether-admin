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

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Component;
import com.xuan.common.exception.CommonException;

/**
 * 测试执行监听器
 *
 * @author xuyuxiang
 * @date 2023/5/22 21:41
 */
@Slf4j
@Component
public class FlwTestCustomListener implements FlwGlobalCustomEventListener {

    @Override
    public void publishRejectEvent(HistoricProcessInstance historicProcessInstance) {
        try {
            log.info(">>> 自定义事件执行监听器：拒绝事件触发，流程id为：{}", historicProcessInstance.getId());
        } catch (Exception e) {
            throw new CommonException("自定义事件执行监听器{}出现异常：{}", FlwTestCustomListener.class.getName(), e.getMessage());
        }
    }

    @Override
    public void publishCloseEvent(HistoricProcessInstance historicProcessInstance) {
        try {
            log.info(">>> 自定义事件执行监听器：终止事件触发，流程id为：{}", historicProcessInstance.getId());
        } catch (Exception e) {
            throw new CommonException("自定义事件执行监听器{}出现异常：{}", FlwTestCustomListener.class.getName(), e.getMessage());
        }
    }

    @Override
    public void publishRevokeEvent(HistoricProcessInstance historicProcessInstance) {
        try {
            log.info(">>> 自定义事件执行监听器：撤回事件触发，流程id为：{}", historicProcessInstance.getId());
        } catch (Exception e) {
            throw new CommonException("自定义事件执行监听器{}出现异常：{}", FlwTestCustomListener.class.getName(), e.getMessage());
        }
    }

    @Override
    public void publishDeleteEvent(HistoricProcessInstance historicProcessInstance) {
        try {
            log.info(">>> 自定义事件执行监听器：删除事件触发，流程id为：{}", historicProcessInstance.getId());
        } catch (Exception e) {
            throw new CommonException("自定义事件执行监听器{}出现异常：{}", FlwTestCustomListener.class.getName(), e.getMessage());
        }
    }
}
