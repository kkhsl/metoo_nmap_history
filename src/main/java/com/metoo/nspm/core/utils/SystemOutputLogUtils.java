package com.metoo.nspm.core.utils;

import com.metoo.nspm.core.manager.admin.tools.DateTools;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @description 系统日志输出
 */
@Component
public class SystemOutputLogUtils {

    public static void start(Logger log, Long startTime, String msg){
        log.info(msg + " :" +  DateTools.getCurrentDateByCh(startTime));
    }

    public static void diff(Logger log, Long startTime, Long endTime, String msg){
        log.info(
                    msg
                    + " "
                    + " 耗时:"
                    +  (endTime - startTime) / (60 * 1000) % 60
                    + " 分钟"
                    + (endTime - startTime) / 1000 % 60
                    + "秒 ");
    }


}
