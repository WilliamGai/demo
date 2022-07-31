package com.sonic.website.app.code;

import java.util.Calendar;

import org.junit.Test;

public class CodeTest {
    @Test
    public void test() {
        CodeInfoVO vo = new CodeInfoVO();
        System.out.println(vo);
        String json = "{'close_time':1481868000000,'desc':'每周一都会刷新','fresh_type':1,'name':'舰指太平洋每周','open_time':1481868000000,'sn':'jztpy_week','status':0}";
        CodeInfoVO j = CodeInfoVO.parseObject(json);
        System.err.println(j);
        System.err.println(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
        
        Object o = null;
        CodeInfoVO o2 = (CodeInfoVO)o;
        System.err.println("o2="+o2);
    }
    @Test
    public void testFreshType(){
        for (CodeFreshType t : CodeFreshType.values()) {
            System.out.println(t.name());
        }
    }

}
