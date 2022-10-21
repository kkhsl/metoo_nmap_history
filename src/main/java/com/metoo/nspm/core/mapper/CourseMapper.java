package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.Course;
import com.metoo.nspm.vo.CourseVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper {

    Course selectByPrimaryKey(Long id);

    int insert(Course instance);

    int update(Course instance);

    int deleteByPrimaryKey(Long id);

    List<Course> query();

    List<Course> queryAll();

    List<Course> findBycondition(Map params);

    List<CourseVo> webCourseList();


}
