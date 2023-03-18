package com.metoo.nspm.core.websocket.api;

import com.metoo.nspm.core.config.redis.util.MyRedisManager;
import com.metoo.nspm.core.manager.admin.tools.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class RedisResponseUtils {

    @Autowired
    private static MyRedisManager redisWss = new MyRedisManager("ws");

    public static void syncRedis(String sessionid, Object result, Integer type){
        if(sessionid != null && !sessionid.equals("")){
            String key = sessionid + ":" + type;
            String key0 = key + ":0";
            Object value = redisWss.get(key0);
            String key1 = "";

            boolean k0flag = false;
            boolean k1flag = false;


            if(value == null || "".equals(value)){
                key1 = key + ":1";
                value = redisWss.get(key1);
                if(value != null && !value.equals("")){
                    k1flag = true;
                }
            }else{
                k0flag = true;
            }
            if(value == null || "".equals(value)){
                redisWss.put(key + ":1", result);
            }else{
                boolean flag = Md5Crypt.getDiffrent(value, result);
                if(flag){
                    if(k0flag){
                        redisWss.remove(key0);
                    }
                    if(k1flag){
                        redisWss.remove(key1);
                    }
                    redisWss.put(key + ":0", result);
                }else{
                    if(k0flag){
                        redisWss.remove(key0);
                    }
                    if(k1flag){
                        redisWss.remove(key1);
                    }
                    redisWss.put(key + ":1", result);
                }
            }
        }
    }
}
