package com.powerwin.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DevuserDao {

    List<Map<String,Object>> queryData(@Param("start") long start, @Param("end") long end);

    List<Map<String,Object>> queryMap(@Param("start") long start, @Param("end") long end);

    void get_update(@Param("statusres") String statusres, @Param("id") int id);

    void get_update2(@Param("statusres") String statusres, @Param("status") int status, @Param("id") int id);

}