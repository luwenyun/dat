package com.lwy.dat.service;/**
 * Created by lwy on 2017/5/1.
 */

import com.lwy.dat.dao.TableMapper;
import com.lwy.dat.pojo.Table;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 陆文云
 * @create 2017-05-01 10:58
 **/
@Service("tableService")
public class TableServiceImpl implements TableService {
    @Resource
    private TableMapper tableMapper;

    public List<String> getTableNamesByFolderId(Integer folderId) {
        return this.tableMapper.getTableNamesByFolderId( folderId);
    }

    @Override
    public Table getTableByName(String fileName, Integer folderId) {
        return this.tableMapper.getTableByName(fileName,folderId);
    }

    @Override
    public int updateFile(String fileName, Integer fileId) {
        return this.tableMapper.updateFileByName(fileName,fileId);
    }

    @Override
    public int getTableIdByName(String fileName, Integer folderId) {
        Integer id=this.tableMapper.getTableIdByName(fileName,folderId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int deleteTable(String fileName, Integer folderId) {
        Integer id=this.tableMapper.deleteTableByName(fileName,folderId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    public int insert(Table record) {
        Integer id=this.tableMapper.insert(record);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public List<Integer> getTableIdsById(int folderId) {
        return this.tableMapper.getTableIdsById(folderId);
    }

    @Override
    public int updateFileRows(int rows,int tableId)
    {
        Integer id=this.tableMapper.updateFileInfoById(rows,tableId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int updateFileColumns(int columns, int tableId) {
        Integer id=this.tableMapper.updateFileColumnsById(columns,tableId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int updateFileChild(String child, int fileId) {
        Integer id=this.tableMapper.updateFileChild(child,fileId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int updateFileStatusById(String status, int fileId) {
        Integer id=this.tableMapper.updateFileStatusById(status,fileId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }
}
