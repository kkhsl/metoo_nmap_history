package com.metoo.nspm;

import com.metoo.nspm.core.jwt.util.JwtUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class NspmApplicationTests {

    @Test
    void contextLoads(){
        Map param = new HashMap();
        param.put("userName", "hkk");
        JwtUtil.getToken(param);
    }

    public static void main(String[] args) {
        Map param = new HashMap();
        param.put("userName", "hkk");
        JwtUtil.getToken(param);
    }


}
