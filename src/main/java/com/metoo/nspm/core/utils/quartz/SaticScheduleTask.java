package com.metoo.nspm.core.utils.quartz;

import com.metoo.nspm.core.service.zabbix.ZabbixService;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration // 用于标记配置类，兼备Component
@EnableScheduling // 开启定时任务
public class SaticScheduleTask {

    @Value("${task.switch.is-open}")
    private boolean flag;
    @Autowired
    private ZabbixService zabbixService;
    @Autowired
    private ShiroFilterFactoryBean getShiroFilterFactoryBean;

    static DefaultWebSecurityManager manager = new DefaultWebSecurityManager();


    @Scheduled(cron = "0 */5 * * * ?")
    // 添加定时任务
    public void configureTask(){
//        ThreadContext.bind(manager);
        //下面正常使用业务代码即可
        if(flag){
            // 此处开启两个线程
            // 存在先后顺序，先录取arp，在根据arp解析数据
            try {
                this.zabbixService.gatherArp();
                this.zabbixService.gatherMac();
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                this.zabbixService.gatherRout();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.zabbixService.gatherIp();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
