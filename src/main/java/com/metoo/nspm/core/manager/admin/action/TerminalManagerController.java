package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.service.nspm.ITerminalService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.entity.nspm.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/terminal")
public class TerminalManagerController {

    @Autowired
    private ITerminalService terminalService;

    @PutMapping
    public Object update(@RequestBody Terminal instance){
        int i = this.terminalService.update(instance);
        if(i >= 1){
            return ResponseUtil.ok();
        }else{
            return ResponseUtil.error();
        }
    }
}
