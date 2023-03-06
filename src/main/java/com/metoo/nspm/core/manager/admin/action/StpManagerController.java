package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.service.nspm.ISpanningTreeProtocolService;
import com.metoo.nspm.core.service.nspm.ISpanningTreeProtocolTempService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.entity.nspm.MacTemp;
import com.metoo.nspm.entity.nspm.SpanningTreeProtocol;
import io.swagger.annotations.ApiOperation;
import javafx.scene.SnapshotParameters;
import org.apache.commons.lang3.StringUtils;
import org.nutz.lang.random.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/admin/stp")
@RestController
public class StpManagerController {

    @Autowired
    private ISpanningTreeProtocolService spanningTreeProtocolService;


    @PostMapping("/list")
    public Object list(@RequestBody(required = false) Map params){
        List<SpanningTreeProtocol> snapshotParametersList = this.spanningTreeProtocolService.selectObjByMap(params);
        return ResponseUtil.ok(snapshotParametersList);
    }

    @ApiOperation("instance去重数据：加索引")
    @PostMapping("/distinct/instance")
    public Object instance(@RequestBody(required = false) Map params){
        List<SpanningTreeProtocol> snapshotParametersList = this.spanningTreeProtocolService.selectObjByMap(params);
        if(snapshotParametersList.size() > 0){
            List<SpanningTreeProtocol> list = snapshotParametersList.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator
                                    .comparing(SpanningTreeProtocol::getInstance, Comparator.nullsLast(String::compareTo)))),
                            ArrayList::new));
            return ResponseUtil.ok(list);
        }
        return ResponseUtil.ok();
    }

//    @ApiOperation("instance去重数据：加索引")
//    @GetMapping("/get/root/info")
//    public Object getRootInfo(@RequestParam String vlan){
//        Map params = new HashMap();
//        List<SpanningTreeProtocol> snapshotParametersList = this.spanningTreeProtocolService.selectObjByMap(params);
//        boolean flag = false;
//        SpanningTreeProtocol instance = null;
//        for(SpanningTreeProtocol spanningTreeProtocol : snapshotParametersList){
//            if(StringUtils.isNotEmpty(spanningTreeProtocol.getVlan())){
//                String[] vlans = spanningTreeProtocol.getVlan().split(",");
//                for (String s : vlans) {
//                    if(s.equals(vlan)){
//                        flag = true;
//                        instance = spanningTreeProtocol;
//                    }
//                }
//            }
//        }
//        if(/*snapshotParametersList.size() > 0*/ instance != null && flag){
////            SpanningTreeProtocol spanningTreeProtocol = snapshotParametersList.get(0);
//            SpanningTreeProtocol spanningTreeProtocol = instance;
//            params.clear();
//            params.put("instance", spanningTreeProtocol.getInstance());
//            params.put("ifRoot", 1);
//            List<SpanningTreeProtocol> spanningTreeProtocols = this.spanningTreeProtocolService.selectObjByMap(params);
//            List<SpanningTreeProtocol> list = spanningTreeProtocols.stream().collect(
//                    Collectors.collectingAndThen(
//                            Collectors.toCollection(() -> new TreeSet<>(Comparator
//                                    .comparing(SpanningTreeProtocol::getVlan, Comparator.nullsLast(String::compareTo)))),
//                            ArrayList::new));
//            return ResponseUtil.ok(list);
//        }
//        return ResponseUtil.ok();
//    }

    @ApiOperation("instance去重数据：加索引")
    @GetMapping("/get/root/info")
    public Object getRootInfo(@RequestParam String vlan){
        Map params = new HashMap();
        params.put("vlan", vlan);
        List<SpanningTreeProtocol> snapshotParametersList = this.spanningTreeProtocolService.selectObjByMap(params);
        if(snapshotParametersList.size() > 0){
            SpanningTreeProtocol spanningTreeProtocol = snapshotParametersList.get(0);
            params.clear();
            params.put("instance", spanningTreeProtocol.getInstance());
            params.put("ifRoot", 1);
            List<SpanningTreeProtocol> spanningTreeProtocols = this.spanningTreeProtocolService.selectObjByMap(params);
            List<SpanningTreeProtocol> list = spanningTreeProtocols.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator
                                    .comparing(SpanningTreeProtocol::getVlan, Comparator.nullsLast(String::compareTo)))),
                            ArrayList::new));
            return ResponseUtil.ok(list);
        }
        return ResponseUtil.ok();
    }

    @ApiOperation("instance去重数据：加索引")
    @GetMapping("/create/tree")
    public Object createTree(@RequestParam String vlan){
        Map params = new HashMap();
        params.put("vlan", vlan);
        List<SpanningTreeProtocol> snapshotParametersList = this.spanningTreeProtocolService.selectObjByMap(params);
        List<SpanningTreeProtocol> list = new ArrayList<>();
        if(snapshotParametersList.size() > 0){
            SpanningTreeProtocol spanningTreeProtocol = snapshotParametersList.get(0);
            params.clear();
            params.put("instance", spanningTreeProtocol.getInstance());
            params.put("portRole", "4");
            List<SpanningTreeProtocol> spanningTreeProtocols = this.spanningTreeProtocolService.selectObjByMap(params);

            spanningTreeProtocols.stream().forEach(e -> {
                if(e.getRemoteDevice() != null){
                    params.clear();
                    params.put("deviceUuid", e.getRemoteDevice());
                    List<SpanningTreeProtocol> remoteDevice = this.spanningTreeProtocolService.selectObjByMap(params);
                    if(remoteDevice.size() > 0){
                        list.add(remoteDevice.get(0));
                    }
                }
                list.add(e);
            });
            return ResponseUtil.ok(list);
        }
        return ResponseUtil.ok();
    }

}
