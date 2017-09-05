package com.lwy.dat.service;

import com.lwy.dat.pojo.Folder;

import java.util.List;

/**
 * Created by lwy on 2017/5/2.
 */
public interface FolderService {
    public List<Folder> getAllFolders(int  userId);
    public Folder getFolder(String folderName,int userId);
    public int insertFolder(Folder folder);
    public List<Integer> getFolderIdsByUserId(int userId);
    public int deleteFolder(String folderName,Integer userId);
    public int updateFolder(String folderName,Integer folderId);
    public int getFolderIdByName(String folderName,Integer userId);
    public Folder getFolderById(int folderId);
    public int updateFolderStatus(String status,int folderId);
}
