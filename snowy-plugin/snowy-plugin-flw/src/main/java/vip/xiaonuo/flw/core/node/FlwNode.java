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
package vip.xiaonuo.flw.core.node;

import lombok.Data;

import java.util.List;

/**
 * 流程子节点的抽象接口
 *
 * @author xuyuxiang
 * @date 2022/3/15 21:51
 */
@Data
public class FlwNode {

    /** Id */
    private String id;

    /** 类型 */
    private String type;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 子节点 */
    private FlwNode childNode;

    /** 条件字节点（仅排他网关、并行网关） */
    private  List<FlwNode> conditionNodeList;

    /** 节点属性 */
    private FlwNodeProperties properties;

    /** 属性 */
    @Data
    public static class FlwNodeProperties {

        /** 配置信息 */
        private FlwNodeConfigProp configInfo;

        /** 条件信息（条件节点特有） */
        private List<List<FlwNodeConditionProp>> conditionInfo;

        /** 参与人信息 */
        private List<FlwNodeParticipateProp> participateInfo;

        /** 按钮信息 */
        private List<FlwNodeButtonProp> buttonInfo;

        /** 字段信息 */
        private List<FlwNodeFieldProp> fieldInfo;

        /** 表单信息 */
        private List<FlwNodeFormProp> formInfo;

        /** 执行监听器信息 */
        private List<FlwNodeExecutionListenerProp> executionListenerInfo;

        /** 任务监听器信息（审批节点特有） */
        private List<FlwNodeTaskListenerProp> taskListenerInfo;
    }

    /** 基本配置属性类 */
    @Data
    public static class FlwNodeConfigProp {

        /** 流水号模板id */
        private String processSnTemplateId;

        /** 打印模板id */
        private String processPrintTemplateId;

        /** 标题模板 */
        private String processTitleTemplate;

        /** 摘要模板 */
        private String processAbstractTemplate;

        /** 开启退回通知 */
        private Boolean processEnableBackNotice;

        /** 退回通知渠道 */
        private String processBackNoticeChannel;

        /** 退回通知模板 */
        private String processBackNoticeTemplate;

        /** 开启待办通知 */
        private Boolean processEnableTodoNotice;

        /** 待办通知渠道 */
        private String processTodoNoticeChannel;

        /** 待办通知模板 */
        private String processTodoNoticeTemplate;

        /** 开启抄送通知 */
        private Boolean processEnableCopyNotice;

        /** 抄送通知渠道 */
        private String processCopyNoticeChannel;

        /** 抄送通知模板 */
        private String processCopyNoticeTemplate;

        /** 开启完成通知 */
        private Boolean processEnableCompleteNotice;

        /** 完成通知渠道 */
        private String processCompleteNoticeChannel;

        /** 完成通知模板 */
        private String processCompleteNoticeTemplate;

        /** 开启自动去重 */
        private Boolean processEnableAutoDistinct;

        /** 自动去重类型 */
        private String processAutoDistinctType;

        /** 开启审批撤销 */
        private Boolean processEnableRevoke;

        /** 开启意见必填 */
        private Boolean processEnableCommentRequired;

        /** 全局发起节点PC端表单 */
        private String processStartFormUrl;

        /** 全局发起节点移动端表单 */
        private String processStartMobileFormUrl;

        /** 全局审批节点PC端表单 */
        private String processApprovalFormUrl;

        /** 全局审批节点移动端表单 */
        private String processApprovalMobileFormUrl;

        /** 任务节点类型 */
        private String userTaskType;

        /** 任务节点退回类型 */
        private String userTaskRejectType;

        /** 多人审批时类型 */
        private String userTaskMulApproveType;

        /** 审批人为空时类型 */
        private String userTaskEmptyApproveType;

        /** 审批人为空时转交人 */
        private String userTaskEmptyApproveUser;

        /** 条件优先级（数字越大优先级别越高） */
        private Integer priorityLevel;
    }

    /** 条件属性类 */
    @Data
    public static class FlwNodeConditionProp {

        /** 条件字段 */
        private String field;

        /** 条件文字 */
        private String label;

        /** 条件运算符 */
        private String operator;

        /** 条件值 */
        private String value;

        /** 条件附加信息 */
        private String extJson;
    }

    /** 参与人属性类 */
    @Data
    public static class FlwNodeParticipateProp {

        /** 参与人键 */
        private String key;

        /** 参与人文字 */
        private String label;

        /** 参与人值 */
        private String value;

        /** 参与人值附加信息 */
        private String extJson;
    }

    /** 按钮属性类 */
    @Data
    public static class FlwNodeButtonProp {

        /** 按钮键 */
        private String key;

        /** 按钮文字 */
        private String label;

        /** 按钮值 */
        private String value;

        /** 按钮附加信息 */
        private String extJson;

    }

    /** 字段属性类 */
    @Data
    public static class FlwNodeFieldProp {

        /** 字段键 */
        private String key;

        /** 字段文字 */
        private String label;

        /** 字段值 */
        private String value;

        /** 字段附加信息 */
        private String extJson;
    }

    /** 表单属性类 */
    @Data
    public static class FlwNodeFormProp {

        /** 表单键 */
        private String key;

        /** 表单文字 */
        private String label;

        /** 表单值 */
        private String value;

        /** 表单附加信息 */
        private String extJson;
    }

    /** 执行监听器属性类 */
    @Data
    public static class FlwNodeExecutionListenerProp {

        /** 监听器键 */
        private String key;

        /** 监听器文字 */
        private String label;

        /** 监听器值 */
        private String value;

        /** 监听器附加信息 */
        private String extJson;
    }

    /** 任务监听器信息属性类 */
    @Data
    public static class FlwNodeTaskListenerProp {

        /** 监听器键 */
        private String key;

        /** 监听器文字 */
        private String label;

        /** 监听器值 */
        private String value;

        /** 监听器附加信息 */
        private String extJson;
    }
}
