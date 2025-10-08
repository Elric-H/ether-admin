package com.bstek.ureport.console.mapper;

import cn.hutool.json.JSONObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 动态SQL执行器
 *
 * @author xuyuxiang
 * @date 2022/7/20 19:19
 **/
public interface DynamicSqlExecutorMapper {

    /**
     * 动态查询SQL
     *
     * @author xuyuxiang
     * @date 2022/7/20 19:19
     **/
    List<JSONObject> dynamicSelectSqlExecutor(@Param("sql") String sql);
}
