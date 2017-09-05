package com.lwy.dat.service;/**
 * Created by lwy on 2017/5/2.
 */

import com.lwy.dat.dao.ChartMapper;
import com.lwy.dat.pojo.Chart;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 陆文云
 * @create 2017-05-02 21:46
 **/
@Service
public class ChartServiceImpl implements ChartService {
    @Resource
    ChartMapper chartMapper;
    @Override
    public Chart getChart(String name) {
        return null;
    }

    @Override
    public List<Chart> getChartsById(int tableId) {
        return this.chartMapper.getChartsById(tableId);
    }

    @Override
    public int insertChart(Chart chart) {
        Integer id=this.chartMapper.insert(chart);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int getChartIdByName(String chartName, int tableId) {
        Integer id=this.chartMapper.getChartIdByName(chartName,tableId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int deleteChartById(int chartId) {
        Integer id=this.chartMapper.deleteChartById(chartId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }
}
