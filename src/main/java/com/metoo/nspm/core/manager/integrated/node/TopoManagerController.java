package com.metoo.nspm.core.manager.integrated.node;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.manager.admin.tools.ShiroUserHolder;
import com.metoo.nspm.core.manager.integrated.utils.RestTemplateUtil;
import com.metoo.nspm.core.service.zabbix.*;
import com.metoo.nspm.core.utils.NodeUtil;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.httpclient.UrlConvertUtil;
import com.metoo.nspm.dto.TopoNodeDto;
import com.metoo.nspm.dto.zabbix.HostDTO;
import com.metoo.nspm.dto.zabbix.ProblemDTO;
import com.metoo.nspm.entity.Performance;
import com.metoo.nspm.entity.SysConfig;
import com.metoo.nspm.entity.Threshold;
import com.metoo.nspm.entity.User;
import com.github.pagehelper.util.StringUtil;
import com.metoo.nspm.core.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Api("拓扑管理")
@RequestMapping("/nspm/topology")
@RestController
public class TopoManagerController {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private IUserService userService;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private IPolicyService policyService;
    @Autowired
    private UrlConvertUtil urlConvertUtil;
    @Autowired
    private ZabbixHostService zabbixHostService;
    @Autowired
    private ZabbixItemService zabbixItemService;
    @Autowired
    private ZabbixService zabbixService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private IThresholdService thresholdService;
    @Autowired
    private IPerformanceService performanceService;
    @Autowired
    private ZabbixHostInterfaceService zabbixHostInterfaceService;
    @Autowired
    private INetworkElementService networkElementService;

    // 获取健康度
    @ApiOperation("获取图层设备健康度")
    @GetMapping("/device/score")
    public Object getGrade(String deviceUuid){
        Double grade = this.policyService.HealthScore(deviceUuid);
        return ResponseUtil.ok(grade.intValue());
    }

    @ApiOperation("图层列表")
    @RequestMapping(value="/topology-layer/layerInfo/GET/listLayers")
    public Object listLayers(TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            String url = "/topology-layer/layerInfo/GET/listLayers";
//            if(dto.getBranchLevel() == null || dto.getBranchLevel().equals("")){
//                dto.setBranchLevel(user.getGroupLevel());
//            }
            Object result = this.nodeUtil.getBody(dto, url, token);
            JSONObject results = JSONObject.parseObject(result.toString());
            JSONArray arrays = JSONArray.parseArray(results.get("rows").toString());
            List list = new ArrayList();
            for(Object array : arrays){
                JSONObject obj = JSONObject.parseObject(array.toString());
                if(obj.get("layerUuid") != null){
                    String photosUrl = "/topology-layer/" + obj.get("layerUuid") + ".png";
                    photosUrl = this.urlConvertUtil.convert(photosUrl);
                    try {
                        String photo = null;
                        photo = this.restTemplateUtil.getInputStream(photosUrl);
                        obj.put("photo", photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                list.add(obj);
            }
            results.put("rows", list);
            return ResponseUtil.ok(results);
        }
        return ResponseUtil.error();
    }

//    @ApiOperation("图层列表")
//    @RequestMapping(value="/topology-layer/layerInfo/GET/listLayers")
//    public Object listLayers(TopoNodeDto dto){
//        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
//        String token = sysConfig.getNspmToken();
//        if(token != null){
//            User currentUser = ShiroUserHolder.currentUser();
//            User user = this.userService.findByUserName(currentUser.getUsername());
//            String url = "/topology-layer/layerInfo/GET/listLayers";
//            if(dto.getBranchLevel() == null || dto.getBranchLevel().equals("")){
//                dto.setBranchLevel(user.getGroupLevel());
//            }
//            Object result = this.nodeUtil.getBody(dto, url, token);
//            JSONObject results = JSONObject.parseObject(result.toString());
//            // 检测用户
//            List<String> users = this.userService.getObjByLevel(dto.getBranchLevel());
//            if(users == null || users.size() <= 0){
//                return ResponseUtil.ok();
//            }
//            JSONArray arrays = JSONArray.parseArray(results.get("rows").toString());
//            List list = new ArrayList();
//            for(Object array : arrays){
//                JSONObject obj = JSONObject.parseObject(array.toString());
//                String userName = obj.get("layerDesc").toString();
//                if(users.contains(userName)){
//                    if(obj.get("layerUuid") != null){
//                        String photosUrl = "/topology-layer/" + obj.get("layerUuid") + ".png";
//                        photosUrl = this.urlConvertUtil.convert(photosUrl);
//                try {
//                            String photo = null;
//                            photo = this.restTemplateUtil.getInputStream(photosUrl);
//                            obj.put("photo", photo);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    obj.put("userName", userName);
//                    list.add(obj);
//                }
//
//            }
//            results.put("rows", list);
//            return ResponseUtil.ok(results);
//        }
//        return ResponseUtil.error();
//    }

//    @ApiOperation("硬件性能")
//    @GetMapping("/performance")
//    public Object performance(String ip){
//        if(StringUtils.isNotEmpty(ip)){
//            HostDTO dto = new HostDTO();
//            Map map = new HashMap();
//            map.put("ip", Arrays.asList(ip));
//            dto.setFilter(map);
//            Object object = this.zabbixHostService.getHost(dto);
//            JSONObject jsonObject = JSONObject.parseObject(object.toString());
//            if(jsonObject.get("result") != null){
//                Map data = new HashMap();
//                JSONArray arrays = JSONArray.parseArray(jsonObject.getString("result"));
//                if(arrays.size() > 0){
//                    JSONObject host = JSONObject.parseObject(arrays.get(0).toString());
//                    Integer hostid = host.getInteger("hostid");
//                    if(hostid != null){
//                        ItemDTO itemDto = new ItemDTO();
//                        itemDto.setHostids(Arrays.asList(hostid));
//                        Object itemObejct = this.zabbixItemService.getItem(itemDto);
//                        JSONObject itemJSON = JSONObject.parseObject(itemObejct.toString());
//                        if(itemJSON.get("result") != null){
//                            JSONArray itemArray = JSONArray.parseArray(itemJSON.getString("result"));
//                            if(itemArray.size() > 0){
//                                for(Object array : itemArray){
//                                    JSONObject item = JSONObject.parseObject(array.toString());
//                                    if(item.getString("name").equals("hwEntityCpuUsage")){
//                                        data.put("hwEntityCpuUsage", item.getString("lastclock"));
//                                    }
//                                    if(item.getString("name").equals("hwEntityMemUsage")){
//                                        data.put("hwEntityMemUsage", item.getString("lastclock"));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                return ResponseUtil.ok(data);
//            }
//        }
//        return ResponseUtil.badArgument();
//    }


    @RequestMapping("upload")
    public Object upload(){
        String url = "https://img2.360buyimg.com/pop/s1180x940_jfs/t1/198549/9/21811/83119/625f7592E8cad4ada/8a771626b433d9fb.png";
//        String url = "https://192.168.5.100/topology-layer/5d519a8c1c5c4b59ad7596eefe0ec365.png";
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);// 设置密钥
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity httpEntity = new HttpEntity(headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, byte[].class);
//        //获取entity中的数据
//        byte[] body = responseEntity.getBody();
//        //创建输出流  输出到本地
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\46075\\Desktop\\新建文件夹 (4)\\1.jpg"));
//            fileOutputStream.write(body);
//            //关闭流
//            fileOutputStream.close();
//            return new String(Base64.encodeBytes(body));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return responseEntity;
    }

    @RequestMapping("photo")
    public Object photo(){
        String url = "https://192.168.5.100/topology-layer/5d519a8c1c5c4b59ad7596eefe0ec365.png";
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);// 设置密钥
//        headers.setContentType(MediaType.);

        HttpEntity httpEntity = new HttpEntity(headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, byte[].class);

//        //获取entity中的数据
//        byte[] body = responseEntity.getBody();
//        //创建输出流  输出到本地
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\46075\\Desktop\\新建文件夹 (4)\\1.jpg"));
//            fileOutputStream.write(body);
//            //关闭流
//            fileOutputStream.close();
//            return new String(Base64.encodeBytes(body));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return responseEntity;
    }

    @RequestMapping("/download")
    public void dowload(HttpServletResponse response){
        String a = "abc";
        byte[] bytes = a.getBytes();
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=picture.txt");
        try {
            OutputStream os = response.getOutputStream();
            // 将字节流传入到响应流里,响应到浏览器
            os.write(bytes);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 将流输出到指定文件夹
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\46075\\Desktop\\新建文件夹 (4)\\1.jpg"));
//            //            fileOutputStream.write(body);
//            fileOutputStream.write(bytes);
//            fileOutputStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    @RequestMapping("generate")
    public void generate(String path, String photo){
        this.restTemplateUtil.generateImage(photo, "C:\\Users\\46075\\Desktop\\新建文件夹 (4)\\a.png");
    }



    @ApiOperation("默认图层")
    @RequestMapping(value="/topology-layer/layerInfo/GET/defaultLayer")
    public Object defaultLayer(){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/GET/defaultLayer";
            Object result = this.nodeUtil.getBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }


    @ApiOperation("图层编辑（修改拓扑名称）")
    @RequestMapping(value="/topology-layer/layerInfo/POST/editLayer")
    public Object editLayer(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/POST/editLayer";
            Object result = this.nodeUtil.postFormDataBody(dto  , url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("新建画布")
    @RequestMapping("/topology-layer/layerInfo/POST/saveLayer")
    public Object saveLayer(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/POST/saveLayer";
            if(dto.getBranchLevel() == null || dto.getBranchLevel().equals("")){
                User currentUser = ShiroUserHolder.currentUser();
                User user = this.userService.findByUserName(currentUser.getUsername());
                dto.setBranchLevel(user.getGroupLevel());
            }
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }
//
//    @ApiOperation("新建画布")
//    @RequestMapping("/topology-layer/layerInfo/POST/saveLayer")
//    public Object saveLayer(@RequestBody(required = false) TopoNodeDto dto){
//        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
//        String token = sysConfig.getNspmToken();
//        if(token != null){
//            User currentUser = ShiroUserHolder.currentUser();
//            User user = this.userService.findByUserName(currentUser.getUsername());
//            dto.setDesc(user.getUsername());
//            String url = "/topology-layer/layerInfo/POST/saveLayer";
//            Object result = this.nodeUtil.postBody(dto, url, token);
//            return ResponseUtil.ok(result);
//        }
//        return ResponseUtil.error();
//    }

    @ApiOperation("删除画布")
    @RequestMapping("/topology-layer/layerInfo/DELETE/layers")
    public Object DELETE(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/DELETE/layers";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设置默认图层")
    @RequestMapping("/topology-layer/layerInfo/PUT/defaultLayer")
    public Object defaultLayer(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/PUT/defaultLayer";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("硬件性能")
    @RequestMapping("/topology-layer/usage")
    public Object usage(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/GET/getLayerByUuid";
            Object object = this.nodeUtil.getBody(dto, url, token);
            JSONObject result = JSONObject.parseObject(object.toString());
            if(result.get("content") != null){
                JSONObject content = JSONObject.parseObject(result.get("content").toString());
                if(content.get("layout") != null){
                    JSONObject layout = JSONObject.parseObject(content.get("layout").toString());
                    Map map = new HashMap();
                    for (Map.Entry<String,Object> entry : layout.entrySet()){
                        JSONObject value = JSONObject.parseObject(entry.getValue().toString());
                        if(value.getString("nodeType").equals("router") || value.getString("nodeType").equals("firewall")){
                           // 性能
                            JSONObject nodeMessage = JSONObject.parseObject(value.getString("nodeMessage"));
                            String ip = nodeMessage.getString("primaryId");
                            List names = Arrays.asList("CpuUsage", "MemUsage", "System name");
                            Object usage = this.zabbixService.getUsage(ip, names);
                            map.put(entry.getKey(), usage);
                        }
                    }
                    return ResponseUtil.ok(map);
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设备信息")
    @RequestMapping("/topology-layer/deviceInfo")
    public Object deviceInfo(@RequestBody(required = false) TopoNodeDto dto){
        if(StringUtils.isEmpty(dto.getIp())){
            return ResponseUtil.badArgument();
        }
        List names = Arrays.asList("System name", "System description", "Uptime", "CpuUsage", "MemUsage");
        Object object = this.zabbixService.getDevice(dto.getIp(), names, dto.getLimit(), dto.getTime_till(), dto.getTime_from());
        return ResponseUtil.ok(object);
    }

    @ApiOperation("设备流量信息")
    @RequestMapping("/topology-layer/deviceHistoryInfo")
    public Object deviceHistoryInfo(@RequestBody(required = false) TopoNodeDto dto){
        if(StringUtils.isEmpty(dto.getIp())){
            return ResponseUtil.badArgument();
        }
//        List names = Arrays.asList("CPU utilization", "Load average (1m avg)", "Memory utilization", "System uptime");
        List names = Arrays.asList("Load average (1m avg)",
                "CPU utilization",
                "Memory utilization",
                "Number of CPUs",
                "Available memory",
                "Total memory",
                "/etc/hosts: Space utilization",
                "/etc/hosts: Total space",
                "/etc/hosts: Used space",
                "Host name of Zabbix agent running",
                "Operating system",
                "System uptime");
        Object object = this.zabbixService.getDeviceHistory(dto.getIp(), names, dto.getLimit(), dto.getTime_till(), dto.getTime_from());
        return ResponseUtil.ok(object);
    }


    @ApiOperation("服务器信息")
    @RequestMapping("/topology-layer/server")
    public Object server(@RequestBody(required = false) TopoNodeDto dto){
        if(StringUtils.isEmpty(dto.getName())){
            return ResponseUtil.badArgument();
        }
        Object obj = this.getServer(dto.getName());
        if(obj != null){
            return ResponseUtil.ok(obj);
        }
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        String url =  "/risk/api/danger/hostComputerSoftware/assetGroupTree";
        Map val = new HashMap();
        val.put("name", dto.getName());
        dto.setVal(val);
        dto.setPage(1);
        dto.setLimit(1000);
        Object object = this.nodeUtil.postBody(dto, url, token);
        JSONObject result = JSONObject.parseObject(object.toString());
        JSONArray datas = JSONArray.parseArray(result.getString("data"));
        if(datas.size() > 0){
            String id = null;
            for (Object data : datas){
                JSONObject ele = JSONObject.parseObject(data.toString());
                if(ele.getString("name").equals(dto.getName())){
                    id = ele.getString("id");
                }
            }

            if(StringUtil.isNotEmpty(id)){
                TopoNodeDto topoNodeDto = new TopoNodeDto();
                topoNodeDto.setLimit(1);
                topoNodeDto.setPage(1);
                Map val1 = new HashMap();
                val1.put("assetGroup", id);
                topoNodeDto.setVal(val1);
                String treeUrl = "/risk/api/danger/assetHost/pageTreeList";
                Object treeObject = this.nodeUtil.postBody(topoNodeDto, treeUrl, token);
                JSONObject treeResult = JSONObject.parseObject(treeObject.toString());
                if(treeResult.get("code").toString().equals("0")){
                    JSONObject data = JSONObject.parseObject(treeResult.getString("data"));
                    JSONArray list = JSONArray.parseArray(data.get("list").toString());
                    if(list.size() > 0){
                        JSONObject ele = JSONObject.parseObject(list.get(0).toString());
                        if(ele.getString("ipCorrespondUUIdMap") != null){
                            JSONObject ipMap = JSONObject.parseObject(ele.getString("ipCorrespondUUIdMap"));
                            String ip = null;
                            Set set = ipMap.keySet();
                            Iterator it = set.iterator();
                            while(it.hasNext()){
                                ip = it.next().toString();
                            }
                            if(ip != null){
                                // 查询设备组
                                Object server = this.zabbixService.getServer(ip);
                                return ResponseUtil.ok(server);
                            }
                        }
                    }
                }
            }
        }
        return ResponseUtil.ok();

    }

    public Object getServer(String name){
        if(!name.isEmpty()){
            Map map = new HashMap();
            if(name.equalsIgnoreCase("WEB")){
                map.put("load_average", 1);
                map.put("cpu_utilization", 2.3);
                map.put("memory_utilization", 42);
                map.put("used_memory", 13579);
                map.put("total_memory", 32000);
                map.put("available_memory", 18421);
                map.put("space_utilization", 75);
                map.put("used_space", 75);
                map.put("total_space", 100);
                map.put("flow", 128.6);
                map.put("linking_number", 29663);
                map.put("response_time", 18.9);
            }
            if(name.equalsIgnoreCase("WEB-2")){
                map.put("load_average", 8);
                map.put("cpu_utilization", 11.6);
                map.put("memory_utilization", 57);
                map.put("used_memory", 36540);
                map.put("total_memory", 27460);
                map.put("available_memory", 10964);
                map.put("space_utilization", 38);
                map.put("used_space", 230);
                map.put("total_space", 600);
                map.put("flow", 366.2);
                map.put("linking_number", 54962);
                map.put("response_time", 22.3);
            }
            if(name.equalsIgnoreCase("WEB-3")){
                map.put("load_average", 60);
                map.put("cpu_utilization", 55.6);
                map.put("memory_utilization", 65);
                map.put("used_memory", 21036);
                map.put("total_memory", 32000);
                map.put("available_memory", 10964);
                map.put("space_utilization", 66);
                map.put("used_space", 1856);
                map.put("total_space", 2800);
                map.put("flow", 21.3);
                map.put("linking_number", 32684);
                map.put("response_time", 14.5);
            }
            if(name.equalsIgnoreCase("DB")){
                map.put("load_average", 22);
                map.put("cpu_utilization", 32);
                map.put("memory_utilization", 67);
                map.put("used_memory", 86556);
                map.put("total_memory", 128000);
                map.put("available_memory", 41444);
                map.put("space_utilization", 60);
                map.put("used_space", 620);
                map.put("total_space", 1024);
                map.put("flow", 96.5);
                map.put("linking_number", 1458);
                map.put("response_time", 13.6);
            }
            if(name.equalsIgnoreCase("DB-2")){
                map.put("load_average", 18);
                map.put("cpu_utilization", 5.8);
                map.put("memory_utilization", 20);
                map.put("used_memory", 3250);
                map.put("total_memory", 16000);
                map.put("available_memory", 12750);
                map.put("space_utilization", 60);
                map.put("used_space", 1234);
                map.put("total_space", 2048);
                map.put("flow", 87.6);
                map.put("linking_number", 2563145);
                map.put("response_time", 17.4);
            }
            if(name.equalsIgnoreCase("DB-3")){
                map.put("load_average", 2);
                map.put("cpu_utilization", 3.5);
                map.put("memory_utilization", 35);
                map.put("used_memory", 5698);
                map.put("total_memory", 16000);
                map.put("available_memory", 10302);
                map.put("space_utilization", 55);
                map.put("used_space", 530);
                map.put("total_space", 960);
                map.put("flow", 3698.2);
                map.put("linking_number", 32554);
                map.put("response_time", 10.5);
            }
            if(name.equalsIgnoreCase("ERP")){
                map.put("load_average", 1);
                map.put("cpu_utilization", 2.2);
                map.put("memory_utilization", 45);
                map.put("used_memory", 14632);
                map.put("total_memory", 32000);
                map.put("available_memory", 17368);
                map.put("space_utilization", 21);
                map.put("used_space", 128);
                map.put("total_space", 600);
                map.put("flow", 8.9);
                map.put("linking_number", 874236);
                map.put("response_time", 12.4);
            }
            if(name.equalsIgnoreCase("ERP-2")){
                map.put("load_average", 43);
                map.put("cpu_utilization", 33.4);
                map.put("memory_utilization", 85);
                map.put("used_memory", 13669);
                map.put("total_memory", 16000);
                map.put("available_memory", 2331);
                map.put("space_utilization", 0.35);
                map.put("used_space", 35);
                map.put("total_space", 100);
                map.put("flow", 186.8);
                map.put("linking_number", 1256423);
                map.put("response_time", 28.6);
            }
            if(name.equalsIgnoreCase("ERP-3")){
                map.put("load_average", 15);
                map.put("cpu_utilization", 25.1);
                map.put("memory_utilization", 82);
                map.put("used_memory", 52789);
                map.put("total_memory", 64000);
                map.put("available_memory", 11211);
                map.put("space_utilization", 52);
                map.put("used_space", 420);
                map.put("total_space", 800);
                map.put("flow", 223.3);
                map.put("linking_number", 33256);
                map.put("response_time", 11.7);
            }
            return map;
        }
        return null;
    }

    @ApiOperation("/item历史数据")
    @GetMapping("/topology-layer/refresh")
    public Object refresh(@RequestParam("itemid") String itemid, @RequestParam("limit") Integer limit){
        if(StringUtils.isEmpty(itemid)){
            return ResponseUtil.badArgument();
        }
        return this.zabbixService.refresh(itemid, limit);
    }

    @ApiOperation("端口信息")
    @PostMapping("/topology-layer/port")
    public Object port(@RequestBody(required = false) TopoNodeDto dto){
        if(StringUtils.isEmpty(dto.getIp())){
            return ResponseUtil.badArgument();
        }
        Object object = this.zabbixService.getInterfaceInfo(dto.getIp());
        return ResponseUtil.ok(object);
    }

    @ApiOperation("流量")
    @GetMapping("/topology-layer/history")
    public Object history(TopoNodeDto dto){
        if(StringUtils.isEmpty(dto.getIp())){
            return ResponseUtil.badArgument();
        }
        Object object = this.zabbixService.getInterfaceHistory(dto.getIp(), dto.getLimit(), dto.getTime_till(), dto.getTime_from());
        return ResponseUtil.ok(object);
    }

    @ApiOperation("拓扑详情")
    @RequestMapping("/topology-layer/layerInfo/GET/getLayerGradeByUuid")
    public Object getLayerByUgetLayerGradeByUuiduid(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/GET/getLayerByUuid";
            Object object = this.nodeUtil.getBody(dto, url, token);
            JSONObject result = JSONObject.parseObject(object.toString());
            if(result.get("content") != null){
                JSONObject content = JSONObject.parseObject(result.get("content").toString());
                if(content.get("layout") != null){
                    JSONObject layout = JSONObject.parseObject(content.get("layout").toString());
                    Map map = new HashMap();
                    for (Map.Entry<String,Object> entry : layout.entrySet()){
                        JSONObject value = JSONObject.parseObject(entry.getValue().toString());
                        if(value.get("nodeType").toString().equals("router")
                                || value.get("nodeType").toString().equals("firewall")){
                            Double grade = this.policyService.HealthScore(entry.getKey());
                            if(grade != null){
                                map.put(entry.getKey(), grade);
                            }
                        }
                        // 添加主机可用状态

                    }
                    return ResponseUtil.ok(map);
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.error();
    }

    @ApiOperation("拓扑设备状态")
    @GetMapping("/topology-layer/layerInfo/GET/getLayerDeviceAvailable")
    public Object getLayerByUgetLayerGradeByUuiduid(String ips){
        Map map = new HashMap();
        if(ips != null && !ips.equals("")){
            String[] iparray = ips.split(",");
            for (String ip : iparray){
                String avaliable = this.zabbixHostInterfaceService.getInterfaceAvaliable(ip);
                map.put(ip, avaliable);
            }
        }
        return ResponseUtil.ok(map);
    }

    @ApiOperation("拓扑接口状态")
    @RequestMapping("/topology-layer/layerInfo/GET/interface")
    public Object links(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/GET/getLayerByUuid";
            Object object = this.nodeUtil.getBody(dto, url, token);
            JSONObject result = JSONObject.parseObject(object.toString());
            if(result.get("content") != null){
                JSONObject content = JSONObject.parseObject(result.get("content").toString());
                if(content.getString("links") != null){
                    // 获取设备ip
                    List list = new ArrayList();
                    JSONArray links = JSONArray.parseArray(content.get("links").toString());
                    for(Object element : links){
                        JSONObject link = JSONObject.parseObject(element.toString());
                        Map map = new HashMap();
                        map.put("from", link.get("from"));
                        map.put("to", link.get("to"));
                        String ip = link.getString("description");
                        if(StringUtils.isEmpty(link.getString("interfaceUuid")))
                            break;
                        Object flow = this.zabbixService.flow(ip, link.getString("interfaceName"));
                        Map flowValue = Json.fromJson(Map.class, Json.toJson(flow));
                        List values = new ArrayList();
                        if(flowValue != null && !flowValue.isEmpty()){
                            for (Object key : flowValue.keySet()){
                                JSONObject valueMap = JSONObject.parseObject(Json.toJson(flowValue.get(key)));
                                valueMap.put("interfaceName", link.get("interfaceName"));
                                // 计算阈值
                                Integer received = valueMap.getInteger("received");
                                Integer sent = valueMap.getInteger("sent");
                                Integer speed = valueMap.getInteger("speed");
                                // 计算
                                Integer flag = 0;
                                Threshold threshold = this.thresholdService.query();
                                if(received != null && received > 0){
                                    if(received / speed >= threshold.getFlow()){
                                        flag = 1;
                                    }
                                }
                                if(sent != null && sent > 0){
                                    if(sent / speed >= threshold.getFlow()){
                                        flag = 1;
                                    }
                                }
                                valueMap.put("flag", flag);
                                values.add(valueMap);
                            }
                            map.put("flow", values.size() > 0 ? values.get(0) : "");
                            list.add(map);
                        }
                    }
                    return ResponseUtil.ok(list);
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.error();
    }

    @ApiOperation("告警信息")
    @PostMapping("/topology-layer/problem")
    public Object problem(@RequestBody ProblemDTO dto){
        if(!StringUtil.isEmpty(dto.getIp())){
            HostDTO hostDTO = new HostDTO();
            Map map = new HashMap();
            map.put("ip", Arrays.asList(dto.getIp()));
            hostDTO.setFilter(map);
            Object obj = this.zabbixHostService.getHost(hostDTO);
            JSONObject json = JSONObject.parseObject(obj.toString());
            if(json.get("result") == null){
                return ResponseUtil.ok();
            }
            JSONArray array = JSONArray.parseArray(json.getString("result"));
            if(array.size() <= 0){
                return ResponseUtil.ok();
            }
            JSONObject result = JSONObject.parseObject(array.getString(0));
            dto.setHostids(result.getString("hostid"));
        }
        dto.setSortfield("");
        dto.setSortorder("DESC");
        Object object = this.problemService.get(dto);
        return ResponseUtil.ok(object);
    }

    @ApiOperation("拓扑详情")
    @RequestMapping("/topology-layer/layerInfo/GET/getLayerByUuid")
    public Object getLayerByUuid(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/GET/getLayerByUuid";
            Object object = this.nodeUtil.getBody(dto, url, token);
            JSONObject result = JSONObject.parseObject(object.toString());
            if(result.get("content") != null){
                JSONObject content = JSONObject.parseObject(result.get("content").toString());
                if(content.get("layout") != null){
                    JSONObject layout = JSONObject.parseObject(content.get("layout").toString());
                    Map map = new HashMap();
                    for (Map.Entry<String,Object> entry : layout.entrySet()){
//                        double grade = this.policyService.HealthScore(entry.getKey());
                        JSONObject value = JSONObject.parseObject(entry.getValue().toString());
                        value.put("grade", 0);
                        map.put(entry.getKey(), value);
                    }
                    content.put("layout", map);
                }
                result.put("content", content);
            }
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("复制图层")
    @RequestMapping("/topology-layer/layerInfo/POST/copyLayer")
    public Object copyLayer(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/POST/copyLayer";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("移动")
    @RequestMapping("/topology-layer/layerInfo/POST/editLayerBranch")
    public Object editLayerBranch(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/POST/editLayerBranch";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("源子网查询")
    @RequestMapping("/topology-layer/whale/GET/subnets")
    public Object subnets(@RequestBody TopoNodeDto dto){
        String ipAddr = dto.getIp4Addr();
        String srcIpaddr = null;
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/GET/subnets";
            Object object = this.nodeUtil.getBody(dto, url, token);
            JSONObject result = JSONObject.parseObject(object.toString());
            if(result.get("success").toString().equals("false")){
              return ResponseUtil.error(result.get("message").toString());
            }
            JSONArray arrays = JSONArray.parseArray(result.get("data").toString());
            if(arrays.get(0) != null){
                JSONObject data = JSONObject.parseObject(arrays.get(0).toString());
                srcIpaddr = data.get("ip4BaseAddress").toString();
                srcIpaddr = this.subIp(srcIpaddr);
                ipAddr = this.subIp(ipAddr);
            }
            if(ipAddr == null || ipAddr.equals("")){
                return ResponseUtil.ok(result);
            }else{
                if(srcIpaddr.equals(ipAddr)){
                    return ResponseUtil.ok(result);
                }else{
                    return ResponseUtil.badArgument();
                }
            }

        }
        return ResponseUtil.error();
    }

    public String subIp(String ipAddr){
        if(ipAddr != null && !ipAddr.equals("")){
            int index = ipAddr.indexOf(".");
            return ipAddr.substring(0,index);
        }
        return null;
    }
    @ApiOperation("子网（相关设备）")
    @GetMapping("/topology-layer/whale/GET/subnet/linkedDevice")
    public Object linkedDevice(TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/GET/subnet/linkedDevice";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("子网拆分（二层设备）记录")
    @RequestMapping("/topology-layer/whale/POST/topo/action/all-split-subnet-summary")
    public Object subnetSimmary(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/POST/topo/action/all-split-subnet-summary";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("二层设备(保存)")
    @RequestMapping("/topology-layer/whale/PUT/topo/action/splitSubnet")
    public Object topoSplitSubnet(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/PUT/topo/action/splitSubnet";
            Object result = this.nodeUtil.putBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }




    @ApiOperation("撤销（设备接入或子网拆分）")
    @RequestMapping("/topology-layer/whale/PUT/topo/action/undo/splitSubnet")
    public Object splitSubnet(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/PUT/topo/action/undo/splitSubnet";
            Object result = this.nodeUtil.putFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("VPN")
    @RequestMapping("/topology-layer/whale/GET/topo/action/linkVpn")
    public Object linkVpn(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/GET/topo/action/linkVpn";
            Object result = this.nodeUtil.getBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("VPN（设备）|二层设备（设备）")
    @RequestMapping("/topology-layer/whale/GET/devices/summary")
    public Object devicesSummary(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/GET/devices/summary";
            Object result = this.nodeUtil.getBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("VPN-选择设备-接口")
    @RequestMapping("/topology-layer/whale/GET/vpn/subnet")
    public Object vpnSubnet(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/GET/vpn/subnet";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("VPN-保存")
    @RequestMapping("/topology-layer/whale/PUT/topo/action/linkVpn")
    public Object putLingVpn(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/PUT/topo/action/linkVpn";
            Object result = this.nodeUtil.putBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("撤销")
    @RequestMapping("/topology-layer/whale/DELETE/topo/action/linkVpn")
    public Object deleteLinkVpn(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/DELETE/topo/action/linkVpn";
            Object result = this.nodeUtil.deleteBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("一键更新")
    @RequestMapping("/topology-layer/layerInfo/POST/updateLayerStatus")
    public Object updateLayerStatus(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/layerInfo/POST/updateLayerStatus";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("预置路径")
    @RequestMapping("/topology/queryRoutesByLayerUuid.action")
    public Object queryRoutesByLayerUuid(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology/queryRoutesByLayerUuid.action";
            Object object = this.nodeUtil.getBody(dto, url, token);
            JSONObject result = JSONObject.parseObject(object.toString());
            Map map1 = new HashMap();
            if(result.getString("data") != null){
                JSONArray arrays = JSONArray.parseArray(result.getString("data"));
                List list = new ArrayList();
                for (Object array : arrays){
                    JSONObject ele = JSONObject.parseObject(array.toString());
                    JSONObject content = JSONObject.parseObject(ele.getString("content"));
                    Map map = new HashMap();
                    map.put("ipType", "IP4");
                    map.put("isPathOnly", true);
                    map.put("dstNodeId", content.getString("dstNodeUuid"));
                    map.put("requestType", "REAL_TIME");
                    map.put("srcNodeId", content.getString("srcNodeUuid"));
                    List beginFlow = new ArrayList();
                    Map beginFlowMap = new HashMap();
                    String key = content.getString("desc");


                    List ip4SrcAddresses = new ArrayList();
                    Map ip4SrcAddressesMap = new HashMap();
                    int index1 = key.indexOf(",", 1);
                    int srcIpStart = key.indexOf("源ip:") + 4;
                    String srcIp = key.substring(srcIpStart, index1);
                    ip4SrcAddressesMap.put("start", srcIp);
                    ip4SrcAddressesMap.put("end", srcIp);
                    ip4SrcAddresses.add(ip4SrcAddressesMap);
                    beginFlowMap.put("ip4SrcAddresses", ip4SrcAddresses);


                    List ip4DstAddresses = new ArrayList();
                    Map ip4DstAddressesMap = new HashMap();
                    int index2 = key.indexOf(",", index1 + 1);
                    int dstIpStart = key.indexOf("目的ip:")+ 5;
                    int dstIpEnd = key.indexOf(",", index2);
                    String dstIp = key.substring(dstIpStart, dstIpEnd);

                    ip4DstAddressesMap.put("start", dstIp);
                    ip4DstAddressesMap.put("end", dstIp);
                    ip4DstAddresses.add(ip4DstAddressesMap);
                    beginFlowMap.put("ip4DstAddresses", ip4DstAddresses);

                    List srcPorts = new ArrayList();
                    Map srcPortsMap = new HashMap();
                    int index3 = key.indexOf(",", index2 + 1);
                    int srcPortStart = key.indexOf("源端口:")+4;
                    int srcPortEnd = key.indexOf(",", index3);
                    String srcPort = key.substring(srcPortStart, srcPortEnd);
                    srcPortsMap.put("start", srcPort.substring(0,srcPort.indexOf("-")));
                    srcPortsMap.put("end", srcPort.substring(srcPort.indexOf("-") + 1));
                    srcPorts.add(srcPortsMap);
                    beginFlowMap.put("srcPorts", srcPorts);


                    List dstPorts = new ArrayList();
                    int index4 = key.indexOf(",", index3 + 1);
                    int dstPortStart = key.indexOf("目的端口:")+5;
                    int dstPortEnd = key.indexOf(",", index4);
                    String dstPort = key.substring(dstPortStart, dstPortEnd);
                    Map dstPortMap = new HashMap();
                    dstPortMap.put("start", dstPort.substring(0, dstPort.indexOf("-")));
                    dstPortMap.put("end", dstPort.substring(dstPort.indexOf("-") + 1));
                    dstPorts.add(dstPortMap);
                    beginFlowMap.put("dstPorts", dstPorts);

                    List protocols = new ArrayList();
                    Map protocolsMap = new HashMap();
                    int protocolStart = index4 + 4;
                    String protocol = key.substring(protocolStart);

                    switch (protocol){
                        case "Any":
                            protocol = "Any";
                            break;
                        case "TCP":
                            protocol = "6";
                            break;
                        case "UDP":
                            protocol = "17";
                            break;
                        case "ICMP":
                            protocol = "1";
                            break;
                        case "ICMPv6":
                            protocol = "58";
                            break;
                            default:
                                protocol = "6";
                    }
                    protocolsMap.put("start", protocol);
                    protocolsMap.put("end", protocol);
                    protocols.add(protocolsMap);
                    beginFlowMap.put("protocols", protocols);
                    beginFlow.add(beginFlowMap);
                    map.put("beginFlow", beginFlow);

                    String runUrl = "/topology-layer/whale/GET/detailedPath/run";
                    Object runObject = this.nodeUtil.postBody(map, runUrl, token);
                    JSONObject json = JSONObject.parseObject(runObject.toString());
                    if(json.getString("data") != null && json.getString("success").equalsIgnoreCase("true")){
                        JSONArray array1 = JSONArray.parseArray(json.getString("data"));
                        if(array1.size() > 0){
                            System.out.println(array1.get(0).toString());
                            JSONObject data = JSONObject.parseObject(array1.get(0).toString());
                            JSONArray pathList = JSONArray.parseArray(data.get("pathList").toString());
                            if(pathList.size() > 0){
                                JSONObject path = JSONObject.parseObject(pathList.getString(0));
                                ele.put("pathStatus", path.get("pathStatus"));
                            }
                        }
                    }else{
                        ele.put("pathStatus", null);
                    }
                    list.add(ele);
                }
                map1.put("data", list);
            }
            return ResponseUtil.ok(map1);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("路径备份")
    @RequestMapping("/topology/addRoute.action")
    public Object addRoute(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology/addRoute.action";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("路径查询")
    @RequestMapping("/topology-layer/whale/GET/detailedPath/run")
    public Object run(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/GET/detailedPath/run";
            Object object = this.nodeUtil.postBody(dto, url, token);
            JSONObject result = JSONObject.parseObject(object.toString());
            // 性能分析
            if(dto.getName() != null){
                Map map = new HashMap();
                map.put("id", dto.getName());
                map.put("switch", dto.isFlag());
                Performance performance = this.performanceService.getObjBy(map);
                result.put("performance", performance);
            }
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("关联子网")
    @PostMapping("/topology-layer/whale/GET/device/subnets")
    public Object deviceSubnets(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/GET/device/subnets";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设备-所属逻辑域")
    @PostMapping("/risk/api/alarm/zone/listLogicZoneAndSubnets")
    public Object listLogicZoneAndSubnets(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/alarm/zone/listLogicZoneAndSubnets";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设备-防火墙安全域")
    @RequestMapping("/topology-layer/whale/GET/device/zones")
    public Object zones(TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-layer/whale/GET/device/zones";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("子网-关联主机")
    @PostMapping("/risk/api/danger/hostComputerSoftware/hostComputerList")
    public Object hostComputerList(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/danger/hostComputerSoftware/hostComputerList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }



    @ApiOperation("资产组列表")
    @PostMapping("/risk/api/danger/hostComputerSoftware/assetGroupList")
    public Object assetGroupList(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/danger/hostComputerSoftware/assetGroupList";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("资产管理-关联子网")
    @PostMapping("/risk/api/danger/assetHost/getSubnetByAssetGroup")
    public Object getSubnetByAssetGroup(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/danger/assetHost/getSubnetByAssetGroup";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("资产管理-主机列表")
    @PostMapping("/risk/api/danger/assetHost/pageList")
    public Object pageList(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/danger/assetHost/pageList";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("域-业务区域树")
    @PostMapping("/risk/api/danger/businessZone/businessZoneTree")
    public Object businessZoneTree(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/danger/businessZone/businessZoneTree";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("域-关联子网")
    @PostMapping("/risk/api/alarm/zone/listLogicZoneSubnetWithPage")
    public Object listLogicZoneSubnetWithPage(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/alarm/zone/listLogicZoneSubnetWithPage";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("域-主机列表")
    @PostMapping("/risk/api/danger/businessZone/pageList")
    public Object businessZone(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/danger/businessZone/pageList";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("原始日志")
    @PostMapping("/combing/api/hit/rawlog/findList")
    public Object findList(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/combing/api/hit/rawlog/findList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("安全域")
    @PostMapping("/risk/api/alarm/zone/listLogicZone")
    public Object listLogicZone(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/risk/api/alarm/zone/listLogicZone";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设备策略")
    @PostMapping("/topology-policy/pathAnaly/external/deviceDetail")
    public Object deviceDetail(@RequestBody(required = false) TopoNodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();

        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-policy/pathAnaly/external/deviceDetail";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }


}
