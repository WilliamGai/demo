package com.sonic.website.app.stats.flow;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.sonic.website.core.common.support.KeySupport;
/***
 * 配置用,汇总用
 */
public class GroupColumnItem extends ColumnItem implements KeySupport<String>{
    private static final long serialVersionUID = 4636946057886094508L;
    private GroupColumnItemType groupType;
    public GroupColumnItem(){
        
    }
    public GroupColumnItem(ColumnItem cltm) {
        super(cltm.getColumnName(), cltm.getDesc(), cltm.getColumnType());
    }
    public GroupColumnItemType getGroupType() {
        return groupType;
    }
    public void setGroupType(GroupColumnItemType groupType) {
        this.groupType = groupType;
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    @Override
    public String getKey() {
        return this.getColumnName() + this.getDesc();
    }
}
