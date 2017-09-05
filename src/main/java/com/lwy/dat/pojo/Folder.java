package com.lwy.dat.pojo;

import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class Folder {
    private Integer folderId;

    private Date ctime;

    private String type;

    private Integer userId;

    private String folderName;

    private String originalName;

    private String status;

    public Folder(Integer folderId, Date ctime, String type, Integer userId, String folderName, String originalName, String status) {
        this.folderId = folderId;
        this.ctime = ctime;
        this.type = type;
        this.userId = userId;
        this.folderName = folderName;
        this.originalName = originalName;
        this.status=status;
    }

    public Folder() {
        super();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName == null ? null : folderName.trim();
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName == null ? null : originalName.trim();
    }
}