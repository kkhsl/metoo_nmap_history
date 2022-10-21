package com.metoo.nspm.core.manager.ntas;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.nspm.core.http.NtasHttpBase;
import com.metoo.nspm.core.manager.admin.tools.DateTools;
import com.metoo.nspm.core.utils.BasicDate.BasicDataConvertUtil;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.bytes.ByteConvertUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequestMapping("/ntas")
@RestController
public class NtasManagerController {

    public static void main(String[] args) {
        double i =  0.49548076923076934;
        System.out.println(i);
    }

    @ApiOperation("网络性能概览")
    @GetMapping("/performance/net")
    public Object performance_net(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start_time,
                                  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end_time,
                                  @RequestParam(value = "name") String name){
        if(!StringUtil.isEmpty(name)){
            if(start_time.after(end_time)){
                return ResponseUtil.badArgument("开始时间大于结束时间");
            }
            String url = "/performance/net";
            Map params = new HashMap();
            params.put("start_time", DateTools.dateToLong(start_time));
            params.put("end_time", DateTools.dateToLong(end_time));
            NtasHttpBase base = new NtasHttpBase(url, params);
            JSONObject result = base.get();
            if(result != null){
                if(result.get("data") != null){
                    JSONObject data = JSONObject.parseObject(result.getString("data"));
                    if(data.get("rows") != null){
                        JSONArray rows = JSONArray.parseArray(data.getString("rows"));
                        if(rows.size() > 0){
                            for (Object item : rows){
                                JSONObject ele = JSONObject.parseObject(item.toString());
                                if(ele.getString("name").equals(name)){
                                    Map map = new HashMap();
                                    ele.put("rtt", BasicDataConvertUtil.bigDecimalFormat(BasicDataConvertUtil.formatDouble(ele.getDouble("rtt"))));
                                    ele.put("delay", BasicDataConvertUtil.bigDecimalFormat(BasicDataConvertUtil.formatDouble(ele.getDouble("delay"))));
                                    int total_bytes = ByteConvertUtil.byteConverterMb(ele.getString("total_bytes"));
                                    ele.put("total_bytes", total_bytes);
                                    map.put("rows", ele);
                                    return ResponseUtil.ok(map);
                                }
                            }
                        }
                    }
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.ok();
    }

    @ApiOperation("用户共享分析")
    @GetMapping("/monitor/share/list")
    public Object monitorShare(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start_time,
                               @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end_time){
        if(start_time.after(end_time)){
            return ResponseUtil.badArgument("开始时间大于结束时间");
        }
        String url = "/monitor/share/list";
        Map params = new HashMap();
        params.put("start_time", DateTools.dateToLong(start_time));
        params.put("end_time", DateTools.dateToLong(end_time));
        NtasHttpBase base = new NtasHttpBase(url, params);
        JSONObject result = base.get();
        return result;
    }

    @ApiOperation("服务器流量列表")
    @GetMapping("/monitor/server/list")
    public Object test(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start_time,
                                @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end_time){
        if(start_time.after(end_time)){
            return ResponseUtil.badArgument("开始时间大于结束时间");
        }
        String url = "/monitor/server";
        Map params = new HashMap();
        params.put("start_time", DateTools.dateToLong(start_time));
        params.put("end_time", DateTools.dateToLong(end_time));
        NtasHttpBase base = new NtasHttpBase(url, params);
        JSONObject result = base.get();
        if(result != null){
            if(result.get("data") != null){
                JSONObject data = JSONObject.parseObject(result.getString("data"));
                if(data.get("rows") != null){
                    JSONArray rows = JSONArray.parseArray(data.getString("rows"));
                    if(rows.size() > 0){
                        List list = new ArrayList();
                        rows.forEach((item) -> {
                            JSONObject ele = JSONObject.parseObject(item.toString());
                            String total_bytes = ByteConvertUtil.byteSize(ele.getString("total_bytes"));
                            ele.put("total_bytes", total_bytes);
                            String byte_up = ByteConvertUtil.byteSize(ele.getString("byte_up"));
                            String byte_down = ByteConvertUtil.byteSize(ele.getString("byte_down"));
                            ele.put("byte_up", byte_up);
                            ele.put("byte_down", byte_down);
                            list.add(ele);
                        });
                        data.put("rows", list);
                    }
                }
                result.put("data", data);
            }
        }
        return result;
    }

    @ApiOperation("区域流量分析(网段互访流量列表)")
    @GetMapping("/monitor/location_mutual")
    public Object location_mutual(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start_time,
                                  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end_time){
        if(start_time.after(end_time)){
            return ResponseUtil.badArgument("开始时间大于结束时间");
        }
        String url = "/monitor/location_mutual";
        Map params = new HashMap();
        params.put("start_time", DateTools.dateToLong(start_time));
        params.put("end_time", DateTools.dateToLong(end_time));
        NtasHttpBase base = new NtasHttpBase(url, params);
        JSONObject result = base.get();
        if(result != null){
            if(result.get("data") != null){
                JSONObject data = JSONObject.parseObject(result.getString("data"));
                if(data.get("rows") != null){
                    JSONArray rows = JSONArray.parseArray(data.getString("rows"));
                    if(rows.size() > 0){
                        for (Object item : rows){
                            JSONObject ele = JSONObject.parseObject(item.toString());
                            Map map = new HashMap();
                            String total_bytes = ByteConvertUtil.byteSize(ele.getString("total_bytes"));
                            ele.put("total_bytes", total_bytes);
                            String byte_up = ByteConvertUtil.byteSize(ele.getString("byte_up"));
                            String byte_down = ByteConvertUtil.byteSize(ele.getString("byte_down"));
                            ele.put("byte_up", byte_up);
                            ele.put("byte_down", byte_down);
                            return ResponseUtil.ok(map);
                        }
                    }
                }
            }
        }
        return result;
    }

    @ApiOperation("(业务状态感知)")
    @GetMapping("/business/status")
    public Object business_status(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start_time,
                                  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end_time){
        if(start_time.after(end_time)){
            return ResponseUtil.badArgument("开始时间大于结束时间");
        }
        String url = "/business/status";
        Map params = new HashMap();
        params.put("start_time", DateTools.dateToLong(start_time));
        params.put("end_time", DateTools.dateToLong(end_time));
        NtasHttpBase base = new NtasHttpBase(url, params);
        JSONObject result = base.get();
        if(result != null){
            if(result.get("data") != null){
                JSONObject data = JSONObject.parseObject(result.getString("data"));
                if(data.get("rows") != null){
                    JSONArray rows = JSONArray.parseArray(data.getString("rows"));
                    if(rows.size() > 0){
                        List list = new ArrayList();
                        for (Object item : rows){
                            JSONObject ele = JSONObject.parseObject(item.toString());
                            ele.put("score", BasicDataConvertUtil.bigDecimalFormat(BasicDataConvertUtil.formatDouble(ele.getDouble("score"))));
                            ele.put("delay_time", BasicDataConvertUtil.stringFormat(BasicDataConvertUtil.formatDouble(ele.getDouble("delay_time") / 1000000)));
                            list.add(ele);
                        }
                        data.put("rows", list);
                    }
                }
                result.put("data", data);
            }
        }
        return result;
    }

    @ApiOperation("服务器异常统计列表")
    @GetMapping("/monitor/abnormal/list")
    public Object abnormal(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start_time,
                               @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end_time){
        if(start_time.after(end_time)){
            return ResponseUtil.badArgument("开始时间大于结束时间");
        }
        String url = "/monitor/abnormal/server_list";
        Map params = new HashMap();
        params.put("start_time", DateTools.dateToLong(start_time));
        params.put("end_time", DateTools.dateToLong(end_time));
        NtasHttpBase base = new NtasHttpBase(url, params);
        JSONObject result = base.get();
        return result;
    }

    @ApiOperation("服务器异常统计趋势图")
    @GetMapping("/monitor/abnormal/server_trend")
    public Object server_trend(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start_time,
                               @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end_time){
        if(start_time.after(end_time)){
            return ResponseUtil.badArgument("开始时间大于结束时间");
        }
        String url = "/monitor/abnormal/server_trend";
        Map params = new HashMap();
        params.put("start_time", DateTools.dateToLong(start_time));
        params.put("end_time", DateTools.dateToLong(end_time));
        NtasHttpBase base = new NtasHttpBase(url, params);
        JSONObject result = base.get();
        return result;
    }

    @ApiOperation("用户历史流量列表")
    @GetMapping("/monitor/user")
    public Object monitor_user(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start_time,
                       @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end_time){
        if(start_time.after(end_time)){
            return ResponseUtil.badArgument("开始时间大于结束时间");
        }
        String url = "/monitor/user";
        Map params = new HashMap();
        params.put("start_time", DateTools.dateToLong(start_time));
        params.put("end_time", DateTools.dateToLong(end_time));
        NtasHttpBase base = new NtasHttpBase(url, params);
        JSONObject result = base.get();
        return result;
    }

}
