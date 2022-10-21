package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.service.IThresholdService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.dto.ThresholdDTO;
import com.metoo.nspm.entity.Threshold;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/threshold")
@RestController
public class ThresholdManagerController {

    @Autowired
    private IThresholdService thresholdService;

    @GetMapping("/list")
    public Object get(){
        return ResponseUtil.ok(this.thresholdService.query());
    }

    @PutMapping("/update")
    public Object update(@RequestBody ThresholdDTO dto){
        if(dto.getCpu() < 0 || dto.getFlow() < 0 || dto.getMemory() < 0){
            return ResponseUtil.badArgument();
        }
        Threshold threshold = new Threshold();
        BeanUtils.copyProperties(dto, threshold);
        int result = this.thresholdService.update(threshold);
        return result >= 1 ? ResponseUtil.ok() : ResponseUtil.error();
    }
}
