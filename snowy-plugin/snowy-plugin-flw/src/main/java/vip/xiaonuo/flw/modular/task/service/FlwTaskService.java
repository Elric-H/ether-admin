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
package vip.xiaonuo.flw.modular.task.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.flw.modular.task.param.*;
import vip.xiaonuo.flw.modular.task.result.*;

import java.util.List;

/**
 * 任务Service接口
 *
 * @author xuyuxiang
 * @date 2022/5/22 15:32
 */
public interface FlwTaskService {

    /**
     * 获取待办任务分页
     *
     * @author xuyuxiang
     * @date 2022/5/22 16:20
     */
    Page<FlwTodoTaskResult> todoPage(FlwTaskPageTodoParam flwTaskPageTodoParam);

    /**
     * 获取已办任务分页
     *
     * @author xuyuxiang
     * @date 2022/5/22 16:20
     */
    Page<FlwDoneTaskResult> donePage(FlwTaskPageDoneParam flwTaskPageDoneParam);

    /**
     * 调整申请
     *
     * @author xuyuxiang
     * @date 2022/6/15 19:31
     **/
    void adjust(FlwTaskAdjustParam flwTaskAdjustParam);

    /**
     * 审批保存
     *
     * @author xuyuxiang
     * @date 2022/6/15 19:31
     **/
    void save(FlwTaskSaveParam flwTaskSaveParam);

    /**
     * 审批同意
     *
     * @author xuyuxiang
     * @date 2022/6/15 19:31
     **/
    void pass(FlwTaskPassParam flwTaskPassParam);

    /**
     * 审批拒绝
     *
     * @author xuyuxiang
     * @date 2022/6/15 19:31
     **/
    void reject(FlwTaskRejectParam flwTaskRejectParam);

    /**
     * 审批退回
     *
     * @author xuyuxiang
     * @date 2022/6/15 19:31
     **/
    void back(FlwTaskBackParam flwTaskBackParam);

    /**
     * 审批转办
     *
     * @author xuyuxiang
     * @date 2022/6/15 19:31
     **/
    void turn(FlwTaskTurnParam flwTaskTurnParam, boolean validComment);

    /**
     * 审批跳转
     *
     * @author xuyuxiang
     * @date 2022/6/15 19:31
     **/
    void jump(FlwTaskJumpParam flwTaskJumpParam);

    /**
     * 审批跳转
     *
     * @author xuyuxiang
     * @date 2022/6/15 19:31
     **/
    void addSign(FlwTaskAddSignParam flwTaskAddSignParam);

    /**
     * 任务详情
     *
     * @author xuyuxiang
     * @date 2022/8/23 15:12
     **/
    FlwTaskDetailResult detail(FlwTaskIdParam flwTaskIdParam);

    /**
     * 获取可驳回节点列表
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:30
     **/
    List<FlwBackNodeResult> getCanBackNodeInfoList(FlwTaskGetBackNodeInfoParam flwTaskGetBackNodeInfoParam);

    /**
     * 获取可跳转节点列表
     *
     * @author xuyuxiang
     * @date 2023/5/19 15:30
     **/
    List<FlwJumpNodeResult> getCanJumpNodeInfoList(FlwTaskGetJumpNodeInfoParam flwTaskGetJumpNodeInfoParam);

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2023/5/27 13:57
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2023/5/27 13:57
     */
    Page<FlwTaskUserResult> userSelector(FlwTaskSelectorUserParam flwTaskSelectorUserParam);
}
