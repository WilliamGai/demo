package com.sonic.website.core.common.support;

public class WireFormat {
    public static final int ARRAY            = 0b0001;        //数组
    
    public static final int NULL            = 0;            //空对象
    
    //基本类型
    public static final int BYTE            = 2;
    public static final int BOOLEAN        = 4;
    public static final int INT            = 6;
    public static final int LONG            = 8;
    public static final int FLOAT            = 10;
    public static final int DOUBLE            = 12;
    public static final int STRING            = 14;
    public static final int ENUM            = 16;
    public static final int OBJECT            = 20;
    
    //容器类型
    public static final int LIST            = 22;            //内置支持List对象
    public static final int SET            = 24;            //内置支持Set对象
    public static final int MAP            = 26;            //内置支持Map对象
    public static final int COLLECTION        = 28;            //内置支持COLLECTION对象
    
    //特殊类型
    public static final int DISTRIBUTED    = 50;            //支持继承了IDistributedSerilizable接口的对象
    public static final int MSG            = 52;            //ProtoBuff消息类型
}
