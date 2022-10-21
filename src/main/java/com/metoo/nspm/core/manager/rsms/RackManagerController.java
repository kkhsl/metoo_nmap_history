package com.metoo.nspm.core.manager.rsms;

import com.metoo.nspm.core.manager.admin.tools.ShiroUserHolder;
import com.metoo.nspm.core.mapper.PlantRoomMapper;
import com.metoo.nspm.core.service.IPlantRoomService;
import com.metoo.nspm.core.service.IRackService;
import com.metoo.nspm.core.service.IRsmsDeviceService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.dto.PlantRoomDTO;
import com.metoo.nspm.vo.PlantRoomVO;
import com.github.pagehelper.Page;
import com.metoo.nspm.entity.PlantRoom;
import com.metoo.nspm.entity.Rack;
import com.metoo.nspm.entity.RsmsDevice;
import com.metoo.nspm.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("机柜")
@RequestMapping("/admin/rack")
@RestController
public class RackManagerController {

    @Autowired
    private IRackService rackService;
    @Autowired
    private IPlantRoomService plantRoomService;
    @Autowired
    private IRsmsDeviceService rsmsDeviceService;
    @Autowired
    private PlantRoomMapper plantRoomMapper;

    @GetMapping("/get")
    public Object get(@PathVariable("id") String id){
        Rack rack = this.rackService.getObjById(Long.parseLong(id));
        return ResponseUtil.ok(rack);
    }

    @ApiOperation("获取机柜信息")
    @RequestMapping("/getRack")
    public Object getTack(Long id){
        Object obj = this.rackService.rack(id);
        return ResponseUtil.ok(obj);
    }

//    @ApiOperation("机柜列表")
//    @PostMapping("/list")
//    public Object list(@RequestBody RackDTO dto){
//        Page<Rack> page = this.rackService.findBySelect(dto);
//        if(page.size() > 0){
//            return ResponseUtil.ok(new PageInfo<Rack>(page));
//        }
//        return ResponseUtil.ok();
//    }

    @ApiOperation("机柜列表")
    @PostMapping("/list")
    public Object list(@RequestBody PlantRoomDTO dto){
        Page<PlantRoom> page = this.plantRoomService.findBySelectAndRack(dto);
        if(page.size() > 0){
            return ResponseUtil.ok(new PageInfo<Rack>(page));
        }
        return ResponseUtil.ok();
    }

    @ApiOperation("添加机柜")
    @GetMapping("/add")
    public Object add(){
        List<PlantRoomVO> plantRoomList = this.plantRoomService.query(null);
        return ResponseUtil.ok(plantRoomList);
    }

    @ApiOperation("更新机柜")
    @GetMapping("/update")
    public Object update(String id){
        Rack rack = this.rackService.getObjById(Long.parseLong(id));
        if(rack != null){
            PlantRoom plantRoom = this.plantRoomService.getObjById(rack.getPlantRoomId());
            List<PlantRoomVO> plantRoomList = this.plantRoomService.query(null);
            Map map = new HashMap();
            map.put("rack", rack);
            map.put("plantRoomList", plantRoomList);
            return ResponseUtil.ok(map);
        }
        return ResponseUtil.badArgument();
    }

    @ApiOperation("保存机柜")
    @RequestMapping("/save")
    public Object save(@RequestBody Rack instance){
        if(instance == null){
            return ResponseUtil.badArgument();
        }
        if(instance.getSize() == null || instance.getSize().equals("")){
            return ResponseUtil.badArgument("请选择机柜大小");
        }else if(instance.getSize() == 0){
            return ResponseUtil.badArgument("择机柜大小不能小于等于0");
        }

        Map params = new HashMap();
        // 验证名称唯一性
        if(instance.getName() != null && !instance.getName().isEmpty()){
            params.clear();
            params.put("rackId", instance.getId());
            params.put("rackName", instance.getName());
            params.put("plantRoomId", instance.getPlantRoomId());
            List<Rack> rackList = this.rackService.selectObjByMap(params);
            if(rackList.size() > 0){
                return ResponseUtil.badArgumentRepeatedName();
            }
        }
        PlantRoom obj = this.plantRoomMapper.getObjById(instance.getPlantRoomId());
        if(obj != null){
            instance.setPlantRoomId(obj.getId());
            instance.setPlantRoomName(obj.getName());
        }else{
            // 查询预置机房
//            PlantRoom plantRoom = new PlantRoom();
//            plantRoom.setDeleteStatus(1);
//            List<PlantRoomVO> vo = this.plantRoomMapper.query(plantRoom);
//            instance.setPlantRoomId(vo.get(0).getId());
//            instance.setPlantRoomName(vo.get(0).getName());
            return ResponseUtil.badArgument("请选择机房");
        }


        // 验证资产编号唯一性
        if(instance.getAsset_number() != null && !instance.getAsset_number().isEmpty()){
            params.clear();
            params.put("rackId", instance.getId());
            params.put("asset_number", instance.getAsset_number());
            List<Rack> rackList = this.rackService.selectObjByMap(params);
            if(rackList.size() > 0){
                Rack rack = rackList.get(0);
                return ResponseUtil.badArgument("资产编号与(" + rack.getPlantRoomName() + ":" + rack.getName() + ")重复");
            }
        }


        int flag = this.rackService.save(instance);
        if (flag != 0){
            return ResponseUtil.ok();
        }
        return ResponseUtil.error("机柜保存失败");
    }

    @ApiOperation("删除机柜")
    @DeleteMapping("/del")
    public Object del(@RequestParam(value = "id") String id){
        Rack instance = this.rackService.getObjById(Long.parseLong(id));
        if(instance == null){
            return ResponseUtil.badArgument("资源不存在");
        }
        // 设备
        User user = ShiroUserHolder.currentUser();
        Map params = new HashMap();
        params.put("rackId", instance.getId());
        params.put("userId", user.getId());
        List<RsmsDevice> rsmsDevices = this.rsmsDeviceService.selectObjByMap(params);
        for (RsmsDevice rsmsDevice : rsmsDevices){
            rsmsDevice.setRackId(null);
            rsmsDevice.setRackName(null);
            try {
                this.rsmsDeviceService.update(rsmsDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int flag = this.rackService.delete(Long.parseLong(id));
        if (flag >= 1){

            return ResponseUtil.ok();
        }
        return ResponseUtil.error("机柜删除失败");
    }

    @ApiOperation("批量删除机柜")
    @DeleteMapping("/batch/del")
    public Object batchDel(@RequestParam(value = "ids") String ids){
        String[] l = ids.split(",");
        List<String> list = Arrays.asList(l);
        for (String id : list){
            Rack instance = this.rackService.getObjById(Long.parseLong(id));
            if(instance == null){
                return ResponseUtil.badArgument("id：" + id + "资源不存在");
            }
            // 设备
            User user = ShiroUserHolder.currentUser();
            Map params = new HashMap();
            params.put("rackId", instance.getId());
            params.put("userId", user.getId());
            List<RsmsDevice> rsmsDevices = this.rsmsDeviceService.selectObjByMap(params);
            for (RsmsDevice rsmsDevice : rsmsDevices){
                rsmsDevice.setRackId(null);
                rsmsDevice.setRackName(null);
                try {
                    this.rsmsDeviceService.update(rsmsDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int flag = this.rackService.batchDel(ids);
        if (flag != 0){
            return ResponseUtil.ok();
        }
        return ResponseUtil.error("机柜删除失败");
    }
}
