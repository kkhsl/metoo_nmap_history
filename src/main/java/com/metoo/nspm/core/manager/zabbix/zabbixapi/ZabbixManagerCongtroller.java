package com.metoo.nspm.core.manager.zabbix.zabbixapi;

import com.metoo.nspm.core.manager.zabbix.utils.ItemUtil;
import com.metoo.nspm.core.service.topo.ITopoNodeService;
import com.metoo.nspm.core.service.zabbix.*;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.entity.zabbix.Rout;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;

@RequestMapping("/zabbix")
@RestController
public class ZabbixManagerCongtroller {

    @Autowired
    private ITopoNodeService topoNodeService;
    @Autowired
    private ZabbixService zabbixService;
    @Autowired
    private ZabbixItemService zabbixItemService;
    @Autowired
    private IArpService arpService;
    @Autowired
    private IMacService macService;
    @Autowired
    private IIPAddressServie ipaddressService;
    @Autowired
    private ItemUtil itemUtil;
    @Autowired
    private ZabbixHostService zabbixHostService;
    @Autowired
    private IRoutService routService;

    @RequestMapping("/getsystem")
    public Object getSystem(HttpServletRequest request ){
        Map map = new HashMap();
        try {
            String resourceUtilUrl = ResourceUtils.getURL("classpath:").getPath() + "static/routs" + "/routTable.conf";
            map.put("resourceUtilUrl", URLDecoder.decode(resourceUtilUrl, "utf-8"));
            String contextPath = request.getContextPath();
            map.put("contextPath", contextPath);
            String servletContextPath = request.getServletContext().getContextPath();
            map.put("servletContextPath", servletContextPath);
            String realPath = request.getServletContext().getRealPath("/");
            map.put("realPath", realPath);
            String realPath2 = request.getServletContext().getRealPath("");
            map.put("realPath2", realPath2);

            Resource resource = new ClassPathResource("");
            map.put("resource", resource.getFile().getAbsolutePath());

            return map;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping("/arp")
    public void getItem() {
        this.zabbixService.gatherArp();
    }

    @RequestMapping("/arpThread")
    public void arpThread() {
        this.zabbixService.gatherArpThread();
    }

    @RequestMapping("/mac")
    public void getMac(){
        this.zabbixService.gatherMac();
    }

    @RequestMapping("/arpMac")
    public Object get(){
        this.zabbixService.gatherArpThread();
        this.zabbixService.gatherMacThread();
        return "ok";
    }

    @ApiOperation("采集Ipaddrss")
    @GetMapping(value = {"/ipaddress"})
    public Object ipaddress(HttpServletRequest request) throws IOException {
        this.zabbixService.gatherIp();
        return ResponseUtil.ok();
    }

    @ApiOperation("采集Rout")
    @GetMapping(value = {"/rout"})
    public Object rout(HttpServletRequest request) throws IOException {
       this.zabbixService.gatherRout();
        return ResponseUtil.ok();
    }

//    @ApiOperation("采集Rout")
//    @GetMapping(value = {"/rout"})
//    public Object rout(HttpServletRequest request) throws IOException {
//        List<Map> ipList = this.topoNodeService.query();
//        if(ipList != null && ipList.size() > 0) {
//            // truncate
//            this.ipaddressService.truncateTable();
//            this.routService.truncateTable();
//            for (Map map : ipList) {
//                boolean flag = this.zabbixHostService.verifyHost(String.valueOf(map.get("ip")));
//                if (flag) {
//                    System.out.println(map.get("ip").toString());
////                    this.zabbixService.gatherIpaddress(map.get("ip").toString(), map.get("deviceName").toString());
//                    this.zabbixService.gatherRout(map.get("ip").toString(), map.get("deviceName").toString());
//                }
//            }
//        }
//        return ResponseUtil.ok();
//    }

    @ApiOperation("获取路由表")
    @GetMapping(value = {"/static/routs/{ip}", "/static/routs"})
    public Object rout(HttpServletRequest request, @PathVariable("ip") String ip) throws IOException {
        String path = ResourceUtils.getURL("classpath:").getPath() + "/static/routs/routTable.conf";
        path = "C:\\Users\\46075\\Desktop\\metoo\\需求记录\\4，策略可视化\\监控系统（Zabbix）\\routTable.conf";
        File file = new File(URLDecoder.decode(path, "utf-8"));
        FileWriter fileWriter =new FileWriter(file);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"), 1024*1000);
        out.write("display ip routing-table");
        out.write("\r\n");
        out.write(itemUtil.strLenComplement("Destination/Mask", 25));
        out.write(itemUtil.strLenComplement("Proto", 10));
        out.write(itemUtil.strLenComplement("Pre", 10));
        out.write(itemUtil.strLenComplement("Cost", 10));
        out.write(itemUtil.strLenComplement("Flags", 10));
        out.write(itemUtil.strLenComplement("NextHop", 30));
        out.write(itemUtil.strLenComplement("interface", 20));
        out.write("\r\n");
        List<Map<String, String>> maps = this.zabbixService.getItemRoutByIp(ip);
        Map params = new HashMap();
        if(!maps.isEmpty() && maps.size()>0){
            this.routService.truncateTable();
            for (Map<String, String> map : maps){
                out.write(itemUtil.strLenComplement(map.get("destination") + "/" + map.get("mask"), 25));
                out.write(itemUtil.strLenComplement(map.get("proto"), 10));
                out.write(itemUtil.strLenComplement("0", 10));
                out.write(itemUtil.strLenComplement(map.get("routemetric") == null ? "0" : map.get("routemetric"), 10));
                out.write(itemUtil.strLenComplement(map.get("flags"), 10));
                out.write(itemUtil.strLenComplement(map.get("nextHop"), 30));
                out.write(itemUtil.strLenComplement(map.get("interfaceName") == null ? "" : map.get("interfaceName"), 20));
                out.write("\r\n");
                params.clear();
                params.put("interfaceName", map.get("interface_name"));
                try {
                    Rout rout = new Rout();
                    rout.setDestination(map.get("destination"));
                    rout.setMask(map.get("mask"));
                    rout.setCost(map.get("routemetric"));
                    rout.setFlags(map.get("flags"));
                    rout.setInterfaceName(map.get("interfaceName"));
                    rout.setProto(map.get("proto"));
                    rout.setNextHop(map.get("nextHop"));
                    this.routService.save(rout);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        out.flush();
        out.close();
        return maps;
    }

}
