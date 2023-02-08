package com.metoo.nspm;

import com.metoo.nspm.core.jwt.util.JwtUtil;
import com.metoo.nspm.core.service.nspm.ITopologyService;
import com.metoo.nspm.core.service.zabbix.ItemService;
import com.metoo.nspm.core.shiro.tools.ApplicationContextUtils;
import com.metoo.nspm.entity.nspm.Topology;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

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
