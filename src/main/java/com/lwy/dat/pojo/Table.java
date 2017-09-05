package com.lwy.dat.pojo;

import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class Table {
    private Integer tableId;

    private Integer rows;

    private Integer columns;

    private Date ctime;

    private String type;

    private String tableName;

    private String label;

    private String comments;

    private String originalName;

    private String parent;

    private String child;

    private String status;

    private Integer folderId;

    public Table(Integer tableId, Integer rows, Integer columns, Date ctime, String type, String tableName, String label, String comments, String originalName, String parent, String child,String status, Integer folderId) {
        this.tableId = tableId;
        this.rows = rows;
        this.columns = columns;
        this.ctime = ctime;
        this.type = type;
        this.tableName = tableName;
        this.label = label;
        this.comments = comments;
        this.originalName = originalName;
        this.parent = parent;
        this.child = child;
        this.status=status;
        this.folderId = folderId;
    }

    public Table() {
        super();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label == null ? null : label.trim();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName == null ? null : originalName.trim();
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent == null ? null : parent.trim();
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child == null ? null : child.trim();
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }
}