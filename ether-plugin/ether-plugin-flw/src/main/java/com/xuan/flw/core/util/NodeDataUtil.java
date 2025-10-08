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
package com.xuan.flw.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.List;
import java.util.Map;

/**
 * 节点数据工具类
 *
 * @author xuyuxiang
 * @date 2022/8/24 14:59
 **/
public class NodeDataUtil {

    /**
     * 获取表单内主表的某字段数据，String类型，如：sys_leave.days
     *
     * @author xuyuxiang
     * @date 2022/6/14 16:45
     **/
    public static String handleDataJsonPrimaryTableFieldValueWithString(DelegateExecution delegateExecution, String primaryFieldKey) {
        JSONObject jsonObject = JSONUtil.parseObj(delegateExecution.getVariables());
        String initiatorDataJson = jsonObject.getStr("initiatorDataJson");
        if(!StrUtil.contains(primaryFieldKey, StrUtil.DOT)) {
            return null;
        }
        JSONObject dataObject = JSONUtil.parseObj(initiatorDataJson);
        List<String> primaryFieldKeyList = StrUtil.split(primaryFieldKey, StrUtil.DOT);
        String parentTableName = primaryFieldKeyList.get(1);
        for (Map.Entry<String, Object> entry : dataObject.entrySet()) {
            String dataKey = entry.getKey();
            if (dataKey.equals(parentTableName)) {
                Object dataValue = entry.getValue();
                JSONObject parentDataValue = JSONUtil.parseObj(dataValue);
                return parentDataValue.getStr(primaryFieldKeyList.get(0));
            }
        }
        return null;
    }

    /**
     * 获取表单内主表的某字段数据，Double类型，如：sys_leave.days
     *
     * @author xuyuxiang
     * @date 2022/6/14 16:45
     **/
    public static Double handleDataJsonPrimaryTableFieldValueWithDouble(DelegateExecution delegateExecution, String primaryFieldKey) {
        String value = handleDataJsonPrimaryTableFieldValueWithString(delegateExecution, primaryFieldKey);
        if(ObjectUtil.isNotEmpty(value)) {
            return Convert.toDouble(value);
        }
        return null;
    }
}
