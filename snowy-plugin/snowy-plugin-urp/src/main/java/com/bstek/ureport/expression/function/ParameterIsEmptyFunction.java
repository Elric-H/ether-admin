package com.bstek.ureport.expression.function;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.model.Cell;

import java.util.List;

/**
 * @author Jacky.gao
 * @since 2017年12月7日
 */
public class ParameterIsEmptyFunction extends ParameterFunction {
    @Override
    public Object execute(List<ExpressionData<?>> dataList, Context context,
                          Cell currentCell) {
        Object obj = super.execute(dataList, context, currentCell);
        return obj == null || obj.toString().trim().equals("");
    }

    @Override
    public String name() {
        return "emptyparam";
    }
}
