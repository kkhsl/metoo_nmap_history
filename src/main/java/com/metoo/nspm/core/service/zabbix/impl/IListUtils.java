package com.metoo.nspm.core.service.zabbix.impl;

import com.metoo.nspm.entity.nspm.Mac;
import com.metoo.nspm.entity.nspm.MacTemp;

import java.util.List;
import java.util.Vector;

public class IListUtils {

    private List<MacTemp> list = new Vector();

    public List<MacTemp> getList(){
        return this.list;
    }

    public synchronized void add(MacTemp macTemp){
        if(macTemp != null){
            this.list.add(macTemp);
        }
    }
}
