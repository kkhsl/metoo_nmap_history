package com.metoo.nspm.core.manager.admin.action;

import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.manager.admin.tools.DateTools;
import com.metoo.nspm.core.manager.admin.tools.GroupTools;
import com.metoo.nspm.core.manager.admin.tools.ShiroUserHolder;
import com.metoo.nspm.core.service.*;
import com.metoo.nspm.core.service.zabbix.ZabbixHostInterfaceService;
import com.metoo.nspm.core.service.zabbix.ZabbixHostService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.file.DownLoadFileUtil;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.dto.DeviceConfigDTO;
import com.metoo.nspm.dto.NetworkElementDto;
import com.github.pagehelper.Page;
import com.metoo.nspm.entity.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Api("网元管理")
@RequestMapping("/nspm/ne")
@RestController
public class NetworkElementManagerController {

    @Autowired
    private INetworkElementService networkElementService;
    @Autowired
    private IRsmsDeviceService rsmsDeviceService;
    @Autowired
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private IVendorService vendorService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private GroupTools groupTools;
    @Autowired
    private ZabbixHostInterfaceService zabbixHostInterfaceService;
    @Autowired
    private ZabbixHostService zabbixHostService;
    @Autowired
    private IAccessoryService accessoryService;
    @Autowired
    private IDeviceConfigService deviceConfigService;

    public static void main(String[] args) {
        System.out.println(Arrays.asList(0,1,2,5));
    }

    @RequestMapping("/list")
    public Object list(@RequestBody(required=false) NetworkElementDto dto){
        if(dto == null){
            dto = new NetworkElementDto();
        }
        User user = ShiroUserHolder.currentUser();
        dto.setUserId(user.getId());
        if(dto.getGroupId() != null){
            Group group = this.groupService.selectObjById(dto.getGroupId());
            if(group != null){
                Set<Long> ids = this.groupTools.genericGroupId(group.getId());
                dto.setGroupIds(ids);
            }
        }
        Page<NetworkElement> page = this.networkElementService.selectConditionQuery(dto);
        if(page.getResult().size() > 0){
            ExecutorService exe = Executors.newFixedThreadPool(page.getResult().size());
            for(NetworkElement ne : page.getResult()){
                if(ne.getIp() != null){
                    exe.execute(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject hostInterface = zabbixHostInterfaceService.getHostInterfaceInfo(ne.getIp());
                                if(hostInterface != null){
                                    ne.setAvailable(hostInterface.getString("available"));
                                    ne.setError(hostInterface.getString("error"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }));
                }
            }
            exe.shutdown();
            while (true) {
                if (exe.isTerminated()) {
                    return ResponseUtil.ok(new PageInfo<NetworkElement>(page));
                }
            }
        }
        return  ResponseUtil.ok();
    }

    @GetMapping("/add")
    public Object add(){
        Map map = new HashMap();
        User user = ShiroUserHolder.currentUser();
        Group parent = this.groupService.selectObjById(user.getGroupId());
        List<Group> groupList = new ArrayList<>();
        if(parent != null){
            this.groupTools.genericGroup(parent);
            groupList.add(parent);
        }
        map.put("group", groupList);
        // 厂商
        List<Vendor> vendors = this.vendorService.selectConditionQuery(null);
        map.put("vendor", vendors);
        // 设备类型

        Map params = new HashMap();
        params.put("types", Arrays.asList(0,1,2,5));
        List<DeviceType> deviceTypeList = this.deviceTypeService.selectObjByMap(params);
        map.put("device", deviceTypeList);
        return ResponseUtil.ok(map);
    }

    @GetMapping("/update")
    public Object update(Long id){
        if(id == null){
            return  ResponseUtil.badArgument();
        }
        Map map = new HashMap();
        User user = ShiroUserHolder.currentUser();
        Group parent = this.groupService.selectObjById(user.getGroupId());
        List<Group> groupList = new ArrayList<>();
        if(parent != null){
            this.groupTools.genericGroup(parent);
            groupList.add(parent);
        }
        map.put("group", groupList);
        // 设备类型
        Map params = new HashMap();
        params.put("types", Arrays.asList(0,1,2,5));
        List<DeviceType> deviceTypeList = this.deviceTypeService.selectObjByMap(params);
        map.put("device", deviceTypeList);
        // 厂商
        List<Vendor> vendors = this.vendorService.selectConditionQuery(null);
        map.put("vendor", vendors);
        NetworkElement networkElement = this.networkElementService.selectObjById(id);
        map.put("networkElement", networkElement);
        return ResponseUtil.ok(map);
    }

    @PostMapping("/save")
    public Object save(@RequestBody(required=false) NetworkElement instance){
        // 验证Ip唯一性
        if(instance.getIp() == null || instance.getIp().equals("")){
            return ResponseUtil.badArgument("请输入有效IP");
        }else{
            // 验证ip合法性
            boolean flag =  IpUtil.verifyIp(instance.getIp());
            if(!flag){
                return ResponseUtil.badArgument("ip不合法");
            }
            Map params = new HashMap();
            params.put("neId", instance.getId());
            params.put("ip", instance.getIp());
            List<NetworkElement> nes = this.networkElementService.selectObjByMap(params);
            if(nes.size() > 0){
                return ResponseUtil.badArgument("IP重复");
            }
        }
        // 验证设备名唯一性
        Map params = new HashMap();
        if(instance.getDeviceName() == null || instance.getDeviceName().equals("")){
            return ResponseUtil.badArgument("设备名不能为空");
        }else {
            params.clear();
            params.put("neId", instance.getId());
            params.put("deviceName", instance.getDeviceName());
            List<NetworkElement> nes = this.networkElementService.selectObjByMap(params);
            if (nes.size() > 0) {
                return ResponseUtil.badArgument("设备名称重复");
            }
        }
        // 校验分组
        if(instance.getGroupId() != null){
            Group group = this.groupService.selectObjById(instance.getGroupId());
            if(group != null){
                instance.setGroupId(group.getId());
                instance.setGroupName(group.getBranchName());
            }
        }else{
            User user = ShiroUserHolder.currentUser();
            Group group = this.groupService.selectObjById(user.getGroupId());
            if(group != null){
                instance.setGroupId(group.getId());
                instance.setGroupName(group.getBranchName());
            }
        }
        // 验证设备类型是否存在
        DeviceType deviceType = this.deviceTypeService.selectObjById(instance.getDeviceTypeId());
        if(deviceType == null){
            return ResponseUtil.badArgument("设备类型不存在");
        }else{
            instance.setDeviceTypeName(deviceType.getName());
        }
        // 验证厂商
        Vendor vendor = this.vendorService.selectObjById(instance.getVendorId());
        if(vendor != null){
            instance.setVendorName(vendor.getName());
        }

        if(this.networkElementService.save(instance) >= 1 ? true : false){
            // 同步设备信息
            // 验证名称是否唯一
            if(instance.isSync_device() && instance.getDeviceName() != null && !instance.getDeviceName().isEmpty()){
                params.clear();
                params.put("id", instance.getId());
                params.put("name", instance.getDeviceName());
                List<RsmsDevice> rsmsDeviceList = this.rsmsDeviceService.selectObjByMap(params);
                if(rsmsDeviceList.size() > 0){
                    return ResponseUtil.ok("添加成功【设备同步失败：设备已存在】");
                }else{
                    try {
                        RsmsDevice rsmsDevice = new RsmsDevice();// copy
                        rsmsDevice.setIp(instance.getIp());
                        rsmsDevice.setName(instance.getDeviceName());
                        rsmsDevice.setDeviceTypeId(instance.getDeviceTypeId());
                        rsmsDevice.setDeviceTypeName(instance.getDeviceTypeName());
                        rsmsDevice.setVendorId(instance.getVendorId());
                        rsmsDevice.setVendorName(instance.getVendorName());
                        rsmsDevice.setGroupId(instance.getGroupId());
                        rsmsDevice.setGroupName(instance.getGroupName());
                        rsmsDevice.setDescription(instance.getDescription());
                        rsmsDevice.setUuid(instance.getUuid());
                        this.rsmsDeviceService.save(rsmsDevice);
                        return ResponseUtil.ok("添加成功【设备同步成功】");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseUtil.ok("添加成功【设备同步失败】");
                    }
                }
            }
            return ResponseUtil.ok();
            }else{
                return ResponseUtil.badArgument();
            }
        }

    @DeleteMapping("/delete")
    public Object delete(String ids){
        if(ids != null && !ids.equals("")){
            User user = ShiroUserHolder.currentUser();
            for (String id : ids.split(",")){
                Map params = new HashMap();
                params.put("userId", user.getId());
                params.put("id", Long.parseLong(id));
                List<NetworkElement> nes = this.networkElementService.selectObjByMap(params);
                if(nes.size() > 0){
                    NetworkElement ne = nes.get(0);
                    try {
                        this.networkElementService.del(Long.parseLong(id));
                        // 同步删除网元配置
                        try {
                            this.zabbixHostService.deleteHost(ne.getIp());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return ResponseUtil.badArgument(ne.getDeviceName() + "删除失败");
                    }
                }else{
                    return ResponseUtil.badArgument();
                }
            }

            return ResponseUtil.ok();
        }
        return ResponseUtil.badArgument();
    }

    @PostMapping("/config/list")
    public Object configList(@RequestBody(required = true)DeviceConfigDTO dto){
        NetworkElement networkElement = this.networkElementService.selectObjById(dto.getNeId());
        if(networkElement != null){
            if(dto == null){
                dto = new DeviceConfigDTO();
            }
            Page<DeviceConfig> page = this.deviceConfigService.selectConditionQuery(dto);
            if(page.size() > 0){
                for(DeviceConfig deviceConfig : page.getResult()){
                    String data = this.info(deviceConfig.getId());
                    deviceConfig.setData(data);
                }
                return ResponseUtil.ok(new PageInfo<DeviceConfig>(page));
            }
        }
        return ResponseUtil.badArgument("请选择网元");
    }

    @RequestMapping("/config/upload")
    public Object uploadConfig(@RequestParam(value = "file", required = false) MultipartFile file, Long id){
        NetworkElement ne = this.networkElementService.selectObjById(id);
        if(ne != null){
            if(file != null){
                Accessory accessory = this.uploadFile(file);
                if(accessory != null){
                    DeviceConfig deviceConfig = new DeviceConfig();
                    deviceConfig.setAddTime(accessory.getAddTime());
                    deviceConfig.setName(accessory.getA_name());
                    deviceConfig.setNeId(ne.getId());
                    deviceConfig.setAccessoryId(accessory.getId());
                    this.deviceConfigService.save(deviceConfig);
                    return ResponseUtil.ok();
                }else{
                    return ResponseUtil.error();
                }
            }
            return ResponseUtil.badArgument();
        }
        return ResponseUtil.badArgument();
    }

    @RequestMapping("/config/down")
    public Object uploadConfig(HttpServletResponse response, Long id){
       DeviceConfig deviceConfig = this.deviceConfigService.selectObjById(id);
       if(deviceConfig != null){
           NetworkElement networkElement = this.networkElementService.selectObjById(deviceConfig.getNeId());
           if(networkElement != null){
                Accessory accessory = this.accessoryService.getObjById(deviceConfig.getAccessoryId());
                if(accessory != null){
                    // 下载文件
//                    String path = "C:\\Users\\46075\\Desktop\\新建文件夹\\";
                    String path = accessory.getA_path() + "/" + accessory.getA_name() + accessory.getA_ext();
                    File file = new File(path);
                    if(file.exists()){
                        boolean flag = DownLoadFileUtil.downloadZip(file, response);
                        if(flag){
                            return ResponseUtil.ok();
                        }else{
                            return ResponseUtil.error();
                        }
                    }
                }
           }
       }
       return ResponseUtil.badArgument();
    }

    public String info(Long id){
        DeviceConfig deviceConfig = this.deviceConfigService.selectObjById(id);
        if(deviceConfig != null){
            NetworkElement networkElement = this.networkElementService.selectObjById(deviceConfig.getNeId());
            if(networkElement != null){
                Accessory accessory = this.accessoryService.getObjById(deviceConfig.getAccessoryId());
                if(accessory != null){
                    // 读取文件
                    String path = accessory.getA_path() + "/" + accessory.getA_name() + accessory.getA_ext();
                    File file = new File(path);
                    if(file.exists()){
                        ByteArrayOutputStream bos = null;
                        BufferedInputStream bis = null;
                        try {
                            bos = new ByteArrayOutputStream();
                            bis = new BufferedInputStream(new FileInputStream(file));
                            byte[] bytes = new byte[1024];
                            int len = 0;
                            while ((len = bis.read(bytes)) > 0) {
                                bos.write(bytes,0,len);
                                bos.flush();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return bos.toString();
                    }
                }
            }
        }
        return "";
    }

    @RequestMapping("/config/info")
    public Object info(@RequestParam(value = "ids") String ids){
        Map map = new HashMap();
        for(String id : ids.split(",")){
            DeviceConfig deviceConfig = this.deviceConfigService.selectObjById(Long.parseLong(id));
            if(deviceConfig != null){
                NetworkElement networkElement = this.networkElementService.selectObjById(deviceConfig.getNeId());
                if(networkElement != null){
                    Accessory accessory = this.accessoryService.getObjById(deviceConfig.getAccessoryId());
                    if(accessory != null){
                        // 读取文件
                        String path = accessory.getA_path() + "/" + accessory.getA_name() + accessory.getA_ext();
                        File file = new File(path);
                        if(file.exists()){
                            ByteArrayOutputStream bos = null;
                            BufferedInputStream bis = null;
                            try {
                                bos = new ByteArrayOutputStream();
                                bis = new BufferedInputStream(new FileInputStream(file));
                                byte[] bytes = new byte[1024];
                                int len = 0;
                                while ((len = bis.read(bytes)) > 0) {
                                    bos.write(bytes,0,len);
                                    bos.flush();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            map.put(id, bos.toString()+" ");
                        }else{
                            map.put(id, "");
                        }
                    }
                }
            }
        }
        return ResponseUtil.ok(map);
    }

    public Accessory uploadFile(@RequestParam(required = false) MultipartFile file){
//        String path = "C:\\Users\\46075\\Desktop\\新建文件夹";
        try {
//            path = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/topology";
//            path = ResourceUtils.getURL("classpath:").getPath() + "/static/topology/config";
            String path = "/opt/topology/service/nspm/file";

//        String originalName = file.getOriginalFilename();
//        String fileName = UUID.randomUUID().toString().replace("-", "");
//        String ext = originalName.substring(originalName.lastIndexOf("."));
//        String picNewName = fileName + ext;
//        String imgRealPath = path  + File.separator + picNewName;
            Date currentDate = new Date();
            String fileName = DateTools.getCurrentDate(currentDate);
            String ext = ".conf";
            File folder = new File(URLDecoder.decode( path +  "/" + fileName + ext,"utf-8"));
            file.transferTo(folder);
            Accessory accessory = new Accessory();
            accessory.setA_name(fileName);
            accessory.setA_path(URLDecoder.decode(path, "utf-8"));
            accessory.setA_ext(ext);
            accessory.setA_size((int)file.getSize());
            accessory.setType(3);
            this.accessoryService.save(accessory);
            return accessory;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
                e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
