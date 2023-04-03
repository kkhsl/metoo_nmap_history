package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.service.nspm.IArpHistoryService;
import com.metoo.nspm.core.service.nspm.ITerminalService;
import com.metoo.nspm.core.service.zabbix.IGatherService;
import com.metoo.nspm.entity.nspm.Arp;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/gather")
public class GatherManagerController {

    Logger log = LoggerFactory.getLogger(GatherManagerController.class);

    @Autowired
    private IGatherService gatherService;
    @Autowired
    private IArpHistoryService arpHistoryService;

    @RequestMapping("gatherMac")
    public void test(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            this.gatherService.gatherMacItem(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherArp")
    public void gatherArp(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            this.gatherService.gatherArpItem(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherRoute")
    public void gatherRoute(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            this.gatherService.gatherRouteItem(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherMacBatch")
    public void gatherMacBatch(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            this.gatherService.gatherMacBatch(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherMacBatchStream")
    public void gatherMacBatchStream(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            this.gatherService.gatherMacBatchStream(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherMacThreadPool")
    public void gatherMacThreadPool(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            StopWatch watch = StopWatch.createStarted();
            this.gatherService.gatherMacThreadPool(date);
            watch.stop();
            System.out.println("采集总耗时：" + watch.getTime(TimeUnit.SECONDS) + " 秒.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherMacThreadPool2")
    public void gatherMacThreadPool2(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            StopWatch watch = StopWatch.createStarted();
            this.gatherService.gatherMacThreadPool2(date);
            watch.stop();
            System.out.println("采集总耗时：" + watch.getTime(TimeUnit.SECONDS) + " 秒.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherMacThreadPool3")
    public void gatherMacThreadPool3(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            StopWatch watch = StopWatch.createStarted();
            this.gatherService.gatherMacThreadPool3(date);
            watch.stop();
            System.out.println("采集总耗时：" + watch.getTime(TimeUnit.SECONDS) + " 秒.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherMacThreadPool4")
    public void gatherMacThreadPool4(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            StopWatch watch = StopWatch.createStarted();
            this.gatherService.gatherMacThreadPool4(date);
            watch.stop();
            System.out.println("采集总耗时：" + watch.getTime(TimeUnit.SECONDS) + " 秒.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherSpanningTreeProtocol")
    public void gatherStp(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            System.out.println("采集开始");
            StopWatch watch = StopWatch.createStarted();
            this.gatherService.gatherSpanningTreeProtocol(date);
            watch.stop();
            System.out.println("采集总耗时：" + watch.getTime(TimeUnit.SECONDS) + " 秒.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("gatherIp")
    public void gatherIp(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        Date date = cal.getTime();
        try {
            this.gatherService.gatherIpaddressItem(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ITerminalService terminalService;

    @GetMapping("/terminal")
    public void terminal(){
        this.terminalService.syncMacToTerminal();
    }


    // 测试删除Arp历史数据
    @GetMapping("/deleteArpHistory")
    public void deleteArpHistory(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")Date date){
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MINUTE, -2);
        Date beginOfDate = cal.getTime();


        Map params = new HashMap();
        params.put("addTime", beginOfDate);

        // 删除Arp history
        try {
            this.arpHistoryService.deleteObjByMap(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
