package com.lwy.dat.dao;

import com.lwy.dat.pojo.Table;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TableMapper {
    Integer deleteTableByName(@Param("fileName") String fileName,@Param("folderId") Integer folderId);

    Integer insert(Table record);
    Integer insertSelective(Table record);
    Integer getTableIdByName(@Param("fileName")String fileName,@Param("folderId") Integer folderId);
    Table getTableByName(@Param("fileName")String fileName,@Param("folderId")Integer folderId);
    List<String> getTableNamesByFolderId(Integer folderId);
    Integer updateFileInfoById(@Param("rows")Integer rows,@Param("tableId")Integer tableId);
    Integer updateFileColumnsById(@Param("columns")Integer rows,@Param("tableId")Integer tableId);
    List<Integer> getTableIdsById(Integer folderId);
    Integer updateFileByName(@Param("fileName")String fileName,@Param("fileId")Integer fileId);
    Integer updateFileChild(@Param("child")String child,@Param("fileId")Integer fileId);
    Integer updateFileStatusById(@Param("status")String status,@Param("fileId")Integer fileId);
}