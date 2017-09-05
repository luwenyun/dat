package com.lwy.dat.service;

import com.lwy.dat.pojo.Table;

import java.util.List;

/**
 * Created by lwy on 2017/5/1.
 */
public interface TableService {
    public int insert(Table record);
    public Table getTableByName(String fileName,Integer folderId);
    public int updateFile(String fileName,Integer fileId);
    public int updateFileRows(int rows,int tableId);
    public int updateFileColumns(int columns,int tableId);
    public int getTableIdByName(String fileName,Integer folderId);
    public List<String> getTableNamesByFolderId(Integer folderId);
    public int deleteTable(String fileName,Integer folderId);
    public List<Integer> getTableIdsById(int folderId);
    public int updateFileChild(String child,int fileId);
    public int updateFileStatusById(String status,int fileId);

}

