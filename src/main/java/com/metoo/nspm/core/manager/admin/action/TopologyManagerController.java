package com.metoo.nspm.core.manager.admin.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.nspm.core.manager.admin.tools.DateTools;
import com.metoo.nspm.core.manager.admin.tools.GroupTools;
import com.metoo.nspm.core.manager.admin.tools.ShiroUserHolder;
import com.metoo.nspm.core.service.*;
import com.metoo.nspm.core.service.zabbix.IRoutService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.file.DownLoadFileUtil;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.dto.TopologyDTO;
import com.github.pagehelper.Page;
import com.metoo.nspm.dto.VideoDto;
import com.metoo.nspm.dto.zabbix.RoutDTO;
import com.metoo.nspm.entity.*;
import com.metoo.nspm.entity.zabbix.Rout;
import com.metoo.nspm.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RequestMapping("/admin/topology")
@RestController
public class TopologyManagerController {

    @Autowired
    private ITopologyService topologyService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private GroupTools groupTools;
    @Autowired
    private IPresetPathService presetPathService;
    @Autowired
    private IRoutService routService;
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private IAccessoryService accessoryService;

    @RequestMapping("/list")
    public Object list(@RequestBody(required = false) TopologyDTO dto){
        if(dto == null){
            dto = new TopologyDTO();
        }
        User user = ShiroUserHolder.currentUser();
        if(dto.getGroupId() == null){
            dto.setGroupId(user.getGroupId());
        }
        if(dto.getGroupId() != null){
            Group group = this.groupService.selectObjById(dto.getGroupId());
            if(group != null){
                Set<Long> ids = this.groupTools.genericGroupId(group.getId());
                dto.setGroupIds(ids);
            }
        }
        Page<Topology> page = this.topologyService.selectConditionQuery(dto);
        if(page.getResult().size() > 0) {
           if(page.getResult().size() == 1){
               // 设置默认拓扑
               try {
                   this.setTopologyDefualt();
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
            return ResponseUtil.ok(new PageInfo<Topology>(page));
        }
        return ResponseUtil.ok();
    }

    @GetMapping("/add")
    public Object add(){
        Map map = new HashMap();
        User user = ShiroUserHolder.currentUser();
        Group parent = this.groupService.selectObjById(user.getGroupId());
        List<Group> groupList = new ArrayList<>();
        if(parent != null){
            this.groupTools.genericGroup(parent);
            groupList.add(parent);
        }
        map.put("group", groupList);
        return ResponseUtil.ok(map);
    }

    @GetMapping("/update")
    public Object update(Long id){
        if(id == null){
            return  ResponseUtil.badArgument();
        }
        Map map = new HashMap();
        User user = ShiroUserHolder.currentUser();
        Group parent = this.groupService.selectObjById(user.getGroupId());
        List<Group> groupList = new ArrayList<>();
        if(parent != null){
            this.groupTools.genericGroup(parent);
            groupList.add(parent);
        }
        return ResponseUtil.ok(map);
    }

    @RequestMapping("/save")
    public Object save(@RequestBody(required = false) Topology instance){
        // 校验拓扑名称是否重复
        Map params = new HashMap();
        params.put("topologyId", instance.getId());
        params.put("name", instance.getName());
        List<Topology> topologList = this.topologyService.selectObjByMap(params);
        if(topologList.size() > 0){
            return ResponseUtil.badArgument("拓扑名称重复");
        }
        // 校验分组
        if(instance.getGroupId() != null){
            Group group = this.groupService.selectObjById(instance.getGroupId());
            if(group != null){
                instance.setGroupId(group.getId());
                instance.setGroupName(group.getBranchName());
            }
        }else{
            User user = ShiroUserHolder.currentUser();
            Group group = this.groupService.selectObjById(user.getGroupId());
            if(group != null){
                instance.setGroupId(group.getId());
                instance.setGroupName(group.getBranchName());
            }
        }
        if(instance != null && !instance.getContent().equals("")){
            String str = JSONObject.toJSONString(instance.getContent());
            instance.setContent(str);
        }
        int result = this.topologyService.save(instance);
        if(result >= 1){
            // 设置默认拓扑
            try {
                this.setTopologyDefualt();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.badArgument();
    }


    @DeleteMapping("/delete")
    public Object delete(String ids){
        if(ids != null && !ids.equals("")){
            for (String id : ids.split(",")){
                Topology obj = this.topologyService.selectObjById(Long.parseLong(id));
                if(obj.getIsDefault()){
                    return ResponseUtil.badArgument("拓扑【" + obj.getName() + "】为默认地址");
                }
                Map params = new HashMap();
                try {
                    User user = ShiroUserHolder.currentUser();
                    List<Topology> topologies = this.selectObjById(user, obj.getId());
                    if(topologies != null && topologies.size() >= 1){
                        int i = this.topologyService.delete(obj.getId());
                        if(i >= 1){
                            // 设置默认拓扑
                            try {
                                this.setTopologyDefualt();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(obj != null){
                                params.clear();
                                params.put("topologyId", obj.getId());
                                List<PresetPath> presetPaths = this.presetPathService.selectObjByMap(params);
                                for(PresetPath presetPath : presetPaths){
                                    try {
                                        presetPath.setTopologyName(null);
                                        presetPath.setTopologyId(null);
                                        this.presetPathService.update(presetPath);
//                                        this.presetPathService.delete(presetPath.getId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }else{
                        return ResponseUtil.badArgument();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseUtil.ok("拓扑【" + obj.getName() + "】删除失败");
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.badArgument();
    }

    /**
     * 设置乐观锁，多用户同时登陆，避免并发提交
     * @param id
     * @return
     */
    @ApiOperation("设置默认拓扑")
    @RequestMapping("/default")
    public Object isDefault(String id){
        Topology obj = this.topologyService.selectObjById(Long.parseLong(id));
        if(obj != null){
            obj.setIsDefault(true);
            User user = ShiroUserHolder.currentUser();
            List<Topology> topologyList = this.defaultList(user, true);
            if(topologyList.size() > 0){
                Topology topology = topologyList.get(0);
                if(obj == topology){
                    return ResponseUtil.ok();
                }else{
                    topology.setIsDefault(false);
                    this.topologyService.update(topology);
                }
            }
            this.topologyService.update(obj);
            return ResponseUtil.ok();
        }
        return ResponseUtil.ok();
    }

    @GetMapping("/info")
    public Object info(Long id){
        if(id == null){
            return  ResponseUtil.badArgument();
        }
        User user = ShiroUserHolder.currentUser();
        List<Topology> topologies = selectObjById(user, id);
        if(topologies != null && topologies.size() > 0){
            Topology topology = topologies.get(0);
            if(topology.getContent() != null && !topology.getContent().equals("")){
                JSONObject content = JSONObject.parseObject(topology.getContent().toString());
                topology.setContent(content);
            }
            return ResponseUtil.ok(topology);
        }
        return ResponseUtil.ok();
    }

    @ApiOperation("默认拓扑")
    @GetMapping("/default/topology")
    public Object defaultTopology(){
        Map map = new HashMap();
        User user = ShiroUserHolder.currentUser();
        Map params = new HashMap();
        Group group = this.groupService.selectObjById(user.getGroupId());
        if(group != null){
            Set<Long> ids = this.groupTools.genericGroupId(group.getId());
            params.put("groupIds", ids);
            params.put("isDefault", true);
            List<Topology> topologies = this.topologyService.selectObjByMap(params);
            if (topologies.size() > 0){
                Topology topology = topologies.get(0);
                if(topology.getContent() != null && !topology.getContent().equals("")){
                    System.out.println(JSON.toJSONString(topology.getContent()));
                    JSONObject content = JSONObject.parseObject(topology.getContent().toString());
                    topology.setContent(content);
                }
                map.put("topology", topologies.get(0));
                return ResponseUtil.ok(map);
            }
        }
        return ResponseUtil.ok(map);
    }


    @ApiOperation("拓扑路径列表")
    @PostMapping("/path")
    public Object path(@RequestParam(value = "id") Long id){
        // 是否改为当前用户所在组
        User user = ShiroUserHolder.currentUser();
       if(id != null && !id.equals("")){
           List<Topology> topologies = selectObjById(user, id);
           if(topologies != null && topologies.size() > 0){
               Map params = new HashMap();
               Topology topology = this.topologyService.selectObjById(id);
               params.put("topologyId", topology.getId());
               List<PresetPath> presetPaths = this.presetPathService.selectObjByMap(params);
               return ResponseUtil.ok(presetPaths);
           }
           return ResponseUtil.badArgument();
       }
        return ResponseUtil.ok();
    }


    public void setTopologyDefualt(){
        User user = ShiroUserHolder.currentUser();
        Map params = new HashMap();
        Group group = this.groupService.selectObjById(user.getGroupId());
        if(group != null) {
            Set<Long> ids = this.groupTools.genericGroupId(group.getId());
            params.put("groupIds", ids);
            List<Topology> topologies = this.topologyService.selectObjByMap(params);
            if(topologies.size() == 1){
                Topology topology = topologies.get(0);
                topology.setIsDefault(true);
                this.topologyService.update(topology);
            }
        }
    }

    public  List<Topology> topologyList(User user){
        Map params = new HashMap();
        Group group = this.groupService.selectObjById(user.getGroupId());
        if(group != null) {
            Set<Long> ids = this.groupTools.genericGroupId(group.getId());
            params.put("groupIds", ids);
            List<Topology> topologies = this.topologyService.selectObjByMap(params);
            return topologies;
        }
        return null;
    }

    public  List<Topology> defaultList(User user, boolean isDefault){
        Map params = new HashMap();
        Group group = this.groupService.selectObjById(user.getGroupId());
        if(group != null) {
            Set<Long> ids = this.groupTools.genericGroupId(group.getId());
            params.put("groupIds", ids);
            params.put("isDefault", true);
            List<Topology> topologies = this.topologyService.selectObjByMap(params);
            return topologies;
        }
        return null;
    }

    public  List<Topology> selectObjById(User user, Long id){
        Map params = new HashMap();
        Group group = this.groupService.selectObjById(user.getGroupId());
        if(group != null) {
            Set<Long> ids = this.groupTools.genericGroupId(group.getId());
            params.put("groupIds", ids);
            params.put("id", id);
            List<Topology> topologies = this.topologyService.selectObjByMap(params);
            return topologies;
        }
        return null;
    }

    @ApiOperation("设备路由列表")
    @PostMapping("/device/rout")
    public Object deviceRoutList(@RequestBody RoutDTO dto){
        if(StringUtil.isEmpty(dto.getDeviceUuid())){
            return ResponseUtil.badArgument();
        }
        Page<Rout> page = this.routService.selectConditionQuery(dto);
        if(page.getResult().size() > 0) {
            return ResponseUtil.ok(new PageInfo<Rout>(page));
        }
        return ResponseUtil.ok();
    }

    @RequestMapping("/upload")
    public Object upload(
                       @RequestParam(value = "file", required = false) MultipartFile file){
        if(file != null && file.getSize() > 0){
            boolean accessory = this.uploadFile(file);
            if(accessory){
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.error();
            }
        }
        return ResponseUtil.badArgument();
    }

    public boolean uploadFile(@RequestParam(required = false) MultipartFile file){
        String path = "C:\\Users\\46075\\Desktop\\新建文件夹";
        String originalName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String picNewName = fileName + ext;
        String imgRealPath = path  + File.separator + picNewName;
        Date currentDate = new Date();
        String fileName1 = DateTools.getCurrentDate(currentDate);
        System.out.println(path + "\\" + fileName1 + ".conf");
        java.io.File imageFile = new File(path +  "\\" + fileName1 + ".conf");
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdirs();
        }
        try {
            file.transferTo(imageFile);
            Accessory accessory = new Accessory();
            accessory.setA_name(picNewName);
            accessory.setA_path(path);
            accessory.setA_ext(ext);
            accessory.setA_size((int)file.getSize());
            accessory.setType(3);
            this.accessoryService.save(accessory);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/down")
    public Object down(HttpServletResponse response){
        String path = "C:\\Users\\46075\\Desktop\\新建文件夹\\20221017122442.conf";
        if(path != null && !path.equals("")){
            File file = new File(path);
            if(file != null){
                DownLoadFileUtil.downloadZip(file, response);
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.error();
            }
        }
        return ResponseUtil.badArgument();
    }
}
