package com.sonic.website.app;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.website.app.security.vo.RoleVO;
import com.sonic.website.app.security.vo.UserVO;

public class JackSonTest {

    public static void main(String[] args) throws Exception {
         ObjectMapper mapper = new ObjectMapper();  
         RoleVO role = new RoleVO("id_test", "name_test");
         String json = mapper.writeValueAsString(role);
         System.out.println(json);
         
         RoleVO role2 = mapper.readValue("{\"id2\":null,\"name\":null,\"permissions\":[],\"status\":0}", role.getClass());
//         System.out.println(role);
         System.out.println(role2);
    }

}
