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
package vip.xiaonuo.flw.core.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 审批操作类型枚举
 *
 * @author xuyuxiang
 * @date 2023/5/11 13:26
 **/
@Getter
public enum NodeApproveOperateTypeEnum {

    /**
     * 发起申请
     */
    START("START"),

    /**
     * 重新提交
     */
    RESTART("RESTART"),

    /**
     * 自动通过
     */
    AUTO_COMPLETE("AUTO_COMPLETE"),

    /**
     * 自动拒绝
     */
    AUTO_REJECT("AUTO_REJECT"),

    /**
     * 终止
     */
    END("END"),

    /**
     * 撤回
     */
    REVOKE("REVOKE"),

    /**
     * 同意
     */
    PASS("PASS"),

    /**
     * 拒绝
     */
    REJECT("REJECT"),

    /**
     * 退回
     */
    BACK("BACK"),

    /**
     * 转办
     */
    TURN("TURN"),

    /**
     * 跳转
     */
    JUMP("JUMP"),

    /**
     * 加签
     */
    ADD_SIGN("ADD_SIGN");

    private final String value;

    NodeApproveOperateTypeEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = START.getValue().equals(value) || RESTART.getValue().equals(value) ||
                AUTO_COMPLETE.getValue().equals(value) || AUTO_REJECT.getValue().equals(value) ||
                END.getValue().equals(value) || REVOKE.getValue().equals(value) ||
                PASS.getValue().equals(value) || REJECT.getValue().equals(value) ||
                BACK.getValue().equals(value) || TURN.getValue().equals(value) ||
                JUMP.getValue().equals(value) || ADD_SIGN.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的审批操作类型：{}", value);
        }
    }
}
