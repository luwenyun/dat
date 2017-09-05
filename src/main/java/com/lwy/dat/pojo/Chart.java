package com.lwy.dat.pojo;

import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class Chart {
    private Integer chartId;

    private String type;

    private String chartName;

    private Integer tableId;

    private Date ctime;

    private String comments;

    private String xAxis;

    private String yAxis;

    public Chart(Integer chartId, String type, String chartName, Integer tableId, Date ctime, String comments, String xAxis, String yAxis) {
        this.chartId = chartId;
        this.type = type;
        this.chartName = chartName;
        this.tableId = tableId;
        this.ctime = ctime;
        this.comments = comments;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public Chart() {
        super();
    }

    public Integer getChartId() {
        return chartId;
    }

    public void setChartId(Integer chartId) {
        this.chartId = chartId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName == null ? null : chartName.trim();
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }

    public String getxAxis() {
        return xAxis;
    }

    public void setxAxis(String xAxis) {
        this.xAxis = xAxis == null ? null : xAxis.trim();
    }

    public String getyAxis() {
        return yAxis;
    }

    public void setyAxis(String yAxis) {
        this.yAxis = yAxis == null ? null : yAxis.trim();
    }
}