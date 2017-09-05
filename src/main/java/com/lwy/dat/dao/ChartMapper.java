package com.lwy.dat.dao;

import com.lwy.dat.pojo.Chart;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChartMapper {
    Integer deleteByPrimaryKey(Integer chartId);

    Integer insert(Chart record);

    Integer insertSelective(Chart record);
    Integer deleteChartById(Integer chartId);
    List<Chart> getChartsById(Integer tableId);
    Chart selectByPrimaryKey(Integer chartId);
    Integer getChartIdByName(@Param("chartName")String chartName,@Param("tableId")Integer tableId);
    Integer updateByPrimaryKeySelective(Chart record);

    Integer updateByPrimaryKey(Chart record);
}