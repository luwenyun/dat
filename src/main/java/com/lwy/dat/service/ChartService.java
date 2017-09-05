package com.lwy.dat.service;/**
 * Created by lwy on 2017/5/2.
 */

import com.lwy.dat.pojo.Chart;

import java.util.List;

/**
 * @author 陆文云
 * @create 2017-05-02 21:45
 **/
public interface ChartService {
    public Chart getChart(String name);
    public List<Chart> getChartsById(int tableId);
    public int insertChart(Chart chart);
    public int getChartIdByName(String chartName,int tableId);
    public int deleteChartById(int chartId);
}
