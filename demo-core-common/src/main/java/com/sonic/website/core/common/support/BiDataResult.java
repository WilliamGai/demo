package com.sonic.website.core.common.support;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BiDataResult extends DataResult{
    
    public Object data_other;
    public BiDataResult(int result) {
        super(result);
    }
    public BiDataResult(int result, Object data, Object data_other) {
        super(result, data);
        this.data_other = data_other;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("result", result)
                .append("data", data)
                .append("data_other", data_other).toString();
    }
}
