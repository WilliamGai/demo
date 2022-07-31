package com.sonic.website.app.shop;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ShopTest {
    @Test
    public void test(){
        List<GoodsVO> list = new ArrayList<>();
        list.add(new GoodsVO("1", "name1", "pic1", "goodsDec", 11));
        list.add(new GoodsVO("2", "name2", "pic2", "goodsDec2", 22));
        list.add(new GoodsVO("3", "name3", "pic3", "goodsDec3", 33));
        list.stream().filter(a->a.sn!=null).findAny().get().sn="AAAA";
        System.out.println(list);
        
        
        Optional<Integer> point_need = Optional.of(12);
        System.out.print(point_need.get());
        
        JSONObject object = new JSONObject();
        object.put("name", "wei");
        object.put("age", null);
         
        object.put("date", new Date());
        String jsonString = JSON.toJSONString(object,SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat);
        System.out.println(jsonString);
        
        String date_tag2 = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        DateTimeFormatter df= DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS");
        String tag2 = df.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1471337924226L),ZoneId.of("Asia/Shanghai")));
        System.out.println(date_tag2);
        System.out.println(tag2+1000_000);
        
        LocalTime specificSecondTime = LocalTime.ofSecondOfDay(10000);
        System.out.println("10000th second time= "+specificSecondTime);
        
        LocalDateTime ld = LocalDateTime.ofInstant(Instant.now(), TimeZone.getDefault().toZoneId());
        Instant specificTime = Instant.ofEpochMilli(Instant.now().toEpochMilli());
        System.out.println("Specific Time = "+specificTime);
        System.out.println("Specific Time = "+Instant.now().toEpochMilli());
        System.out.println("Specific ld = "+ld);
        System.out.println("Specific TimeZone.getDefault() = "+TimeZone.getDefault());
    }

}
