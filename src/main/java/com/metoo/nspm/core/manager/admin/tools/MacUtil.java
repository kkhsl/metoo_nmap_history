package com.metoo.nspm.core.manager.admin.tools;

import com.metoo.nspm.core.service.nspm.IMacVendorService;
import com.metoo.nspm.core.utils.MyStringUtils;
import com.metoo.nspm.entity.nspm.Mac;
import com.metoo.nspm.entity.nspm.MacVendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MacUtil {

    @Autowired
    private IMacVendorService macVendorService;

    public List<Mac> macJoint(List<Mac> macs){
        if(macs != null && macs.size() > 0) {
            for (Mac mac : macs) {
                if (mac.getMac() != null && !mac.getMac().equals("")) {
                    String macAddr = mac.getMac();
                    int index = MyStringUtils.acquireCharacterPosition(macAddr, ":", 3);
                    if(index != -1){
                        macAddr = macAddr.substring(0, index);
                        Map params = new HashMap();
                        params.clear();
                        params.put("mac", macAddr);
                        List<MacVendor> macVendors = this.macVendorService.selectObjByMap(params);
                        if (macVendors.size() > 0) {
                            MacVendor macVendor = macVendors.get(0);
                            mac.setVendor(macVendor.getVendor());
                        }
                    }
                }
            }
        }
        return macs;
    }

    public static void main(String[] args) {
        String macAddr = "50:0:0:26:0:2";
        String mac = supplement(macAddr);
        System.out.println(mac);
//        String[] strs = macAddr.split(":");
//        StringBuffer stringBuffer = new StringBuffer();
//        int i = 1;
//        for(String str : strs){
//            if(str.length() == 1){
//                stringBuffer.append(0).append(str);
//            }else{
//                stringBuffer.append(str);
//            }
//            if(i < strs.length){
//                stringBuffer.append(":");
//            }
//            i++;
//        }
//        System.out.println(stringBuffer.toString());
    }

    public List<Mac> supplements(List<Mac> macs){
        if(macs != null && macs.size() > 0) {
            for (Mac mac : macs) {
                if (mac.getMac() != null && !mac.getMac().equals("")) {
                    String macAddr = mac.getMac();
                    int one_index = macAddr.indexOf(":");
                    if(one_index != -1){
                        String[] strs = macAddr.split(":");
                        StringBuffer stringBuffer = new StringBuffer();
                        int i = 1;
                        for(String str : strs){
                            if(str.length() == 1){
                                stringBuffer.append(0).append(str);
                            }else{
                                stringBuffer.append(str);
                            }
                            if(i < str.length()){
                                stringBuffer.append(":");
                            }
                            i++;
                        }
                    }
                }
            }
        }
        return macs;
    }

    public static String supplement(String macAddr){
        int one_index = macAddr.indexOf(":");
        if(one_index != -1){
            String[] strs = macAddr.split(":");
            StringBuffer stringBuffer = new StringBuffer();
            int i = 1;
            for(String str : strs){
                if(str.length() == 1){
                    stringBuffer.append(0).append(str);
                }else{
                    stringBuffer.append(str);
                }
                if(i < strs.length){
                    stringBuffer.append(":");
                }
                i++;
            }
            macAddr = stringBuffer.toString();
        }
        return macAddr;
    }
}
