package com.metoo.nspm.core.manager.rsms;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.metoo.nspm.core.service.nspm.IDeviceTypeService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.dto.DeviceTypeDTO;
import com.metoo.nspm.entity.nspm.DeviceType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("设备类型")
@RequestMapping("/admin/device/type")
@RestController
public class RsmsDeviceTypeManagerController {

    @Autowired
    private IDeviceTypeService deviceTypeService;

    @GetMapping("/count")
    public Object getCount(){
        List<DeviceType> deviceTypes = this.deviceTypeService.selectDeviceTypeAndNeByJoin();
        return ResponseUtil.ok(deviceTypes);
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public Object list(@RequestBody(required = true) DeviceTypeDTO dto){
       Page<DeviceType> page = this.deviceTypeService.selectConditionQuery(dto);
       if(page.getResult().size() > 0){
           return ResponseUtil.ok(new PageInfo<DeviceType>(page));
       }
        return ResponseUtil.ok();
    }

    @ApiOperation("添加")
    @PostMapping("/save")
    public Object save(@RequestBody(required = true) DeviceType instance){
        // 检查页面属性是否改变
        if(instance.getId() != null && !instance.getId().equals("")){
            DeviceType deviceType = this.deviceTypeService.selectObjById(instance.getId());
            if(deviceType == null){
                return ResponseUtil.badArgument("设备类型不存在");
            }
        }
        if(StringUtils.isEmpty(instance.getName())){
            return ResponseUtil.badArgument("名称不能为空");
        }else{
            Map params = new HashMap();
            params.put("name", instance.getName());
            params.put("deviceTypeId", instance.getId());
            List<DeviceType> deviceTypes = this.deviceTypeService.selectObjByMap(params);
            if(deviceTypes.size() > 0){
                return ResponseUtil.badArgument("名称重复");
            }
        }
        int i = this.deviceTypeService.save(instance);
        if(i >= 1){
            return ResponseUtil.ok();
        }else{
            return ResponseUtil.error();
        }
    }

    @ApiOperation("删除")
    @DeleteMapping("/del")
    public Object del(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "ids", required = false) Long[] ids){
        if(ids != null && ids.length > 0){
            int i = this.deviceTypeService.batcheDel(ids);
            if(i >= 1){
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.error();
            }
        }else  if(id != null && !id.equals("")){
            int i = this.deviceTypeService.delete(id);
            if(i >= 1){
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.error();
            }
        }
        return ResponseUtil.badArgument();
    }

}
