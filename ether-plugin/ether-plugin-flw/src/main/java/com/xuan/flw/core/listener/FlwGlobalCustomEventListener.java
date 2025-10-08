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

import org.camunda.bpm.engine.history.HistoricProcessInstance;

/**
 * 自定义事件侦听器，你可以实现该侦听器接口，在流程拒绝、终止、删除时进行一些AOP操作
 *
 * @author xuyuxiang
 * @date 2023/3/3 10:14
 **/
public interface FlwGlobalCustomEventListener {

   /**
    * 执行发布流程拒绝事件
    *
    * @author xuyuxiang
    * @date 2023/8/18 1:07
    */
   void publishRejectEvent(HistoricProcessInstance historicProcessInstance);

   /**
    * 执行发布流程终止事件
    *
    * @author xuyuxiang
    * @date 2023/8/18 1:07
    */
   void publishCloseEvent(HistoricProcessInstance historicProcessInstance);

   /**
    * 执行发布流程撤回事件
    *
    * @author xuyuxiang
    * @date 2023/8/18 1:07
    */
   void publishRevokeEvent(HistoricProcessInstance historicProcessInstance);

   /**
    * 执行发布流程删除事件
    *
    * @author xuyuxiang
    * @date 2023/8/18 1:07
    */
   void publishDeleteEvent(HistoricProcessInstance historicProcessInstance);
}
