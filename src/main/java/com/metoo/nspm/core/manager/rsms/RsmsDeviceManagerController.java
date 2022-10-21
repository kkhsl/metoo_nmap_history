package com.metoo.nspm.core.manager.rsms;

import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.config.annotation.OperationLogAnno;
import com.metoo.nspm.core.config.annotation.OperationType;
import com.metoo.nspm.core.manager.admin.tools.GroupTools;
import com.metoo.nspm.core.manager.admin.tools.ShiroUserHolder;
import com.metoo.nspm.core.service.*;
import com.metoo.nspm.core.service.zabbix.ZabbixHostInterfaceService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.dto.PlantRoomDTO;
import com.metoo.nspm.dto.RsmsDeviceDTO;
import com.github.pagehelper.Page;
import com.metoo.nspm.entity.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Api("设备")
@RequestMapping("/admin/rsms/device")
@RestController
public class RsmsDeviceManagerController {

    @Autowired
    private IRsmsDeviceService rsmsDeviceService;
    @Autowired
    private IPlantRoomService plantRoomService;
    @Autowired
    private IRackService rackService;
    @Autowired
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private GroupTools groupTools;
    @Autowired
    private IVendorService vendorService;
    @Autowired
    private ZabbixHostInterfaceService zabbixHostInterfaceService;
    @Autowired
    private IProjectService projectService;


    @GetMapping("/get")
    public Object get(String id){
        RsmsDevice rsmsDevice = this.rsmsDeviceService.getObjById(Long.parseLong(id));
        return Response.ok(rsmsDevice);
    }

    @RequestMapping("/list")
    public Object list(@RequestBody RsmsDeviceDTO dto){
        User user = ShiroUserHolder.currentUser();
        dto.setUserId(user.getId());
        if(dto.getGroupId() != null){
            Group group = this.groupService.selectObjById(dto.getGroupId());
            if(group != null){
                Set<Long> ids = this.groupTools.genericGroupId(group.getId());
                dto.setGroupIds(ids);
            }
        }
        if(dto.getStart_purchase_time() != null && dto.getEnd_purchase_time() != null){
            if(dto.getStart_purchase_time().after(dto.getEnd_purchase_time())){
                return ResponseUtil.badArgument("起始时间需要小于结束时间");
            }
        }
        if(dto.getStart_warranty_time() != null && dto.getEnd_warranty_time() != null){
            if(dto.getStart_warranty_time().after(dto.getEnd_warranty_time())){
                return ResponseUtil.badArgument("起始时间需要小于结束时间");
            }
        }
        Page<RsmsDevice> page = this.rsmsDeviceService.selectConditionQuery(dto);
        if(page.size() > 0){
            Map map = new HashMap();
            // 设备类型
            List<DeviceType> deviceTypeList = this.deviceTypeService.selectConditionQuery();
            map.put("deviceTypeList", deviceTypeList);
            // 分组
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
            // 项目
            Map params = new HashMap();
            params.put("userId", user.getId());
            List<Project> projectList = this.projectService.selectObjByMap(params);
            map.put("project", projectList);
            return ResponseUtil.ok(new PageInfo<Rack>(page, map));
        }
        return ResponseUtil.ok();
    }

    @RequestMapping("/type/list")
    public Object type(){
        List<DeviceType> deviceTypeList = this.deviceTypeService.selectCountByJoin();
        if(deviceTypeList.size() > 0){
            ExecutorService exe = Executors.newFixedThreadPool(deviceTypeList.size());
            for(DeviceType deviceType : deviceTypeList){
                if(deviceType.getNetworkElementList().size() > 0){
                    for(NetworkElement ne : deviceType.getNetworkElementList()){
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
                            return ResponseUtil.ok(deviceTypeList);
                        }
                    }
                }
            }
        }
        return ResponseUtil.ok();
    }

    @GetMapping("/add")
    public Object add(){
        Map map = new HashMap();
        List<Rack> rackList = this.rackService.query(null);
        // 设备类型
        List<DeviceType> deviceTypeList = this.deviceTypeService.selectConditionQuery();
        map.put("deviceTypeList", deviceTypeList);
        PlantRoomDTO dto = new PlantRoomDTO();
        dto.setCurrentPage(1);
        dto.setPageSize(100000);
        Page<PlantRoom> page = this.plantRoomService.findBySelectAndRack(dto);
        map.put("plantRoomList", page.getResult());

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
        // 项目
        Map params = new HashMap();
        params.put("userId", user.getId());
        List<Project> projectList = this.projectService.selectObjByMap(params);
        map.put("project", projectList);
        return ResponseUtil.ok(map);
    }

    @GetMapping("/update")
    public Object update(String id){
        RsmsDevice rsmsDevice = this.rsmsDeviceService.getObjById(Long.parseLong(id));
        if(rsmsDevice != null){
            Map map = new HashMap();
            // 机房
            PlantRoom plantRoom = this.plantRoomService.getObjById(rsmsDevice.getPlantRoomId());
            // 机柜
            Rack rack = this.rackService.getObjById(rsmsDevice.getRackId());
//            List<Rack> rackList = this.rackService.query(null);
            // 设备类型
            List<DeviceType> deviceTypeList = this.deviceTypeService.selectConditionQuery();
            PlantRoomDTO dto = new PlantRoomDTO();
            dto.setCurrentPage(1);
            dto.setPageSize(100000);
            Page<PlantRoom> page = this.plantRoomService.findBySelectAndRack(dto);
            map.put("plantRoom", plantRoom);
            map.put("plantRoomList", page.getResult());
            map.put("rack", rack);
//            map.put("rackList", rackList);
            map.put("deviceTypeList", deviceTypeList);
            map.put("device", rsmsDevice);

            // 分组
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
            // 项目
            Map params = new HashMap();
            params.put("userId", user.getId());
            List<Project> projectList = this.projectService.selectObjByMap(params);
            map.put("project", projectList);
            return ResponseUtil.ok(map);
        }
        return ResponseUtil.badArgument();
    }

    @ApiOperation("设备详情")
    @RequestMapping("/detail")
    public Object detail(String id){
        RsmsDevice rsmsDevice = this.rsmsDeviceService.getObjAndProjectById(Long.parseLong(id));
        if(rsmsDevice != null){
            Map map = new HashMap();
            map.put("device", rsmsDevice);
            return ResponseUtil.ok(map);
        }
        return ResponseUtil.badArgument("设备不存在");
    }


//    @RequestMapping("/detail")
//    public Object detail(String id){
//        RsmsDevice rsmsDevice = this.rsmsDeviceService.getObjById(Long.parseLong(id));
//        if(rsmsDevice != null){
//            Map map = new HashMap();
//            map.put("device", rsmsDevice);
//            return ResponseUtil.ok(map);
//        }
//        return ResponseUtil.badArgument("设备不存在");
//    }

    @OperationLogAnno(operationType= OperationType.CREATE, name = "device")
    @RequestMapping("/save")
    public Object save(@RequestBody RsmsDevice instance){
//        int i = 1/0;
        if(instance == null){
            return ResponseUtil.badArgument();
        }
        Map params = new HashMap();
        // 验证名称是否唯一
        if(instance.getName() != null && !instance.getName().isEmpty()){
            params.clear();
            params.put("name", instance.getName());
            params.put("deviceId", instance.getId());
            List<RsmsDevice> rsmsDeviceList = this.rsmsDeviceService.selectObjByMap(params);
            if(rsmsDeviceList.size() > 0){
                return ResponseUtil.badArgumentRepeatedName();
            }
        }
        // 验证资产编号唯一性
        if(instance.getAsset_number() != null && !instance.getAsset_number().isEmpty()){
            params.clear();
            params.put("asset_number", instance.getAsset_number());
            params.put("deviceId", instance.getId());
            List<RsmsDevice> rsmsDeviceList = this.rsmsDeviceService.selectObjByMap(params);
            if(rsmsDeviceList.size() > 0){
                RsmsDevice rsmsDevice = rsmsDeviceList.get(0);
                return ResponseUtil.badArgument("资产编号与(" + rsmsDevice.getName() + ")设备重复");
            }
        }
//        if(instance.getPlantRoomId() != null || instance.getRackId() != null){
//            if(instance.getStart() <= 0 || instance.getSize() <= 0){
//                return ResponseUtil.badArgument("未选择设备位置");
//            }
//        }

        if(instance.getPlantRoomId()!= null && !instance.getPlantRoomId().equals("")){
            PlantRoom plantRoom = this.plantRoomService.getObjById(instance.getPlantRoomId());
            if(plantRoom != null){
                instance.setPlantRoomId(plantRoom.getId());
                instance.setPlantRoomName(plantRoom.getName());
            }else{
                return ResponseUtil.badArgument("机房参数错误");
            }
        }
        if(instance.getRackId() != null && !instance.getRackId().equals("")){
            Rack rack = this.rackService.getObjById(instance.getRackId());
            if(rack != null){
                int start = 0;
                int size = 0;
                if(instance.getStart() != null){
                    start = instance.getStart();
                }
                if(instance.getStart() != null){
                    size = instance.getSize();

                }

                int length = (start - 1) + size;
                if(length > rack.getSize()){
                    return ResponseUtil.badArgument("超出可使用机柜长度");
                }
                // 判断是否修改位置
                boolean isVerify = true;
                if(instance.getId() != null){
                    RsmsDevice rsmsDevice = this.rsmsDeviceService.getObjById(instance.getId());
                    if(rsmsDevice != null){
                        if(rsmsDevice.getSize() == instance.getSize() && rsmsDevice.getStart() == instance.getStart()){
                            isVerify = false;
                        }
                    }
                }
                if(instance.getStart() == null||instance.getStart() <= 0 || instance.getSize() == null || instance.getSize() <= 0){
                    isVerify = false;
                }
                // 判断改长度内是否存在设备
                if(isVerify){
                    boolean verify = this.rackService.verifyRack(rack, instance.getStart(), instance.getSize(), instance.isRear(), instance.getId());
                    if(!verify){
                        return ResponseUtil.badArgument("当前位置已有设备");
                    }
                }

                instance.setRackId(rack.getId());
                instance.setRackName(rack.getName());
            }else{
                return ResponseUtil.badArgument("机柜参数错误");
            }
        }

        // 验证日期
        if(instance.getWarranty_time() != null && instance.getPurchase_time() != null){
            if(instance.getWarranty_time().before(instance.getPurchase_time())){
                return ResponseUtil.badArgument("过保时间必须大于采购时间");
            }
        }
        // 验证厂商
        Vendor vendor = this.vendorService.selectObjById(instance.getVendorId());
        if(vendor != null){
            instance.setVendorName(vendor.getName());
        }

        // 验证项目
        if(instance.getProjectId() != null){
            Project project = this.projectService.selectObjById(instance.getProjectId());
            if(project == null){
                return ResponseUtil.badArgument("请输入正确项目参数");
            }
        }

        int flag = this.rsmsDeviceService.save(instance);
        if (flag != 0){
            return ResponseUtil.ok();
        }
        return ResponseUtil.error("设备保存失败");
    }

    @ApiOperation("删除设备")
    @DeleteMapping("/del")
    public Object del(@RequestParam(value = "id") String id){
        RsmsDevice instance = this.rsmsDeviceService.getObjById(Long.parseLong(id));
        if(instance == null){
            return ResponseUtil.badArgument("资源不存在");
        }
        int flag = this.rsmsDeviceService.delete(Long.parseLong(id));
        if (flag != 0){
            return ResponseUtil.ok();
        }
        return ResponseUtil.error("设备删除失败");
    }

    @ApiOperation("批量设备")
    @DeleteMapping("/batch/del")
    public Object batchDel(@RequestParam(value = "ids") String ids){
        String[] l = ids.split(",");
        List<String> list = Arrays.asList(l);
        for (String id : list){
            RsmsDevice instance = this.rsmsDeviceService.getObjById(Long.parseLong(id));
            if(instance == null){
                return ResponseUtil.badArgument("id：" + id + "资源不存在");
            }
        }
        int flag = this.rsmsDeviceService.batchDel(ids);
        if (flag != 0){
            return ResponseUtil.ok();
        }
        return ResponseUtil.error("设备删除失败");
    }

}
