package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.service.nspm.ITerminalTypeService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.entity.nspm.TerminalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/terminal/type")
public class TerminalTypeManagerController {

    @Autowired
    private ITerminalTypeService terminalTypeService;

    @GetMapping
    public Object all(){
        List<TerminalType> list = this.terminalTypeService.selectObjAll();
        return ResponseUtil.ok(list);
    }
}
