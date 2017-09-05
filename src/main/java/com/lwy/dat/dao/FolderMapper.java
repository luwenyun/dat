package com.lwy.dat.dao;

import com.lwy.dat.pojo.Folder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FolderMapper {
    Integer deleteFolderByName(@Param("folderName")String folderName,@Param("userId")Integer userId);

    Integer insert(Folder record);

    Integer insertSelective(Folder record);
    Integer getFolderIdByName(@Param("folderName")String folderName,@Param("userId")Integer userId);
    Folder selectByPrimaryKey(Integer folderId);
    List<Integer> getFolderIdsByUserId(Integer userId);
    List<Folder> findAllFolderByUserId(Integer userId);
    Folder getFolder(@Param("folderName")String folderName,@Param("userId")Integer userId);
    Integer updateByPrimaryKeySelective(Folder record);
    Folder getFolderById(Integer folderId);
    Integer updateFolderStatusById(@Param("status")String status,@Param("folderId")Integer folderId);
    Integer updateFolderByName(@Param("folderName")String folderName,@Param("folderId")Integer folderId);
}