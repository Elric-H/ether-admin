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

import cn.hutool.core.util.StrUtil;
import org.camunda.bpm.model.bpmn.builder.ExclusiveGatewayBuilder;
import vip.xiaonuo.flw.core.node.FlwNode;

public class SequenceFlowParser {

    /**
     * 构建单个条件连接线
     *
     * @author xuyuxiang
     * @date 2022/3/24 9:24
     **/
    public static ExclusiveGatewayBuilder buildSequenceFlowSingle(ExclusiveGatewayBuilder exclusiveGatewayBuilder, FlwNode flwNode) {
        StringBuilder conditionResult = StrUtil.builder();
        conditionResult.append("${");
        StringBuilder finalConditionResult = conditionResult;
        flwNode.getProperties().getConditionInfo().forEach(flwNodeConditionProps -> {
            StringBuilder conditionItem = StrUtil.builder();
            conditionItem.append("(");
            StringBuilder finalConditionItem = conditionItem;
            flwNodeConditionProps.forEach(flwNodeConditionProp -> {
                String fieldOrigin;
                if(flwNodeConditionProp.getField().contains(StrUtil.UNDERLINE)) {
                    fieldOrigin = StrUtil.toCamelCase(flwNodeConditionProp.getField().toLowerCase());
                } else {
                    fieldOrigin = flwNodeConditionProp.getField().toLowerCase();
                }
                String field = StrUtil.replace(fieldOrigin, StrUtil.DOT, StrUtil.UNDERLINE);
                String logic = flwNodeConditionProp.getOperator();
                String value = flwNodeConditionProp.getValue();
                if(logic.equals("==") || logic.equals(">") || logic.equals(">=") || logic.equals("<") || logic.equals("<=")) {
                    finalConditionItem.append(field).append(logic).append(value).append("&&");
                } else {
                    if(logic.equals("include")) {
                        finalConditionItem.append(field).append(".contains(\"").append(value).append("\")").append("&&");
                    } else if(logic.equals("notInclude")) {
                        finalConditionItem.append("!").append(field).append(".contains(\"").append(value).append("\")").append("&&");
                    } else {
                        finalConditionItem.append(field).append(logic).append(value).append("&&");
                    }
                }
            });
            conditionItem = new StringBuilder(StrUtil.removeSuffix(conditionItem, "&&"));
            conditionItem.append(")");
            finalConditionResult.append(conditionItem).append("||");
        });
        conditionResult = new StringBuilder(StrUtil.removeSuffix(conditionResult, "||"));
        conditionResult.append("}");
        if(conditionResult.toString().equals("${}")) {
            return exclusiveGatewayBuilder.condition(flwNode.getTitle(), "${true}").sequenceFlowId(flwNode.getId());
        } else {
            return exclusiveGatewayBuilder.condition(flwNode.getTitle(), conditionResult.toString()).sequenceFlowId(flwNode.getId());
        }
    }
}
