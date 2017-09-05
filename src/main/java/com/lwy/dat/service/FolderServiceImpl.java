package com.lwy.dat.service;/**
 * Created by lwy on 2017/5/2.
 */

import com.lwy.dat.dao.FolderMapper;
import com.lwy.dat.pojo.Folder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 陆文云
 * @create 2017-05-02 21:33
 **/
@Service
public class FolderServiceImpl implements FolderService {
    @Resource
    private FolderMapper folderMapper;

    @Override
    public int deleteFolder(String folderName, Integer userId) {
        Integer id=this.folderMapper.deleteFolderByName(folderName,userId);
        if(id==null){
            return 0;
        }
      return id.intValue();
    }

    @Override
    public int updateFolder(String folderName,Integer folderId) {
        Integer id=this.folderMapper.updateFolderByName(folderName,folderId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int getFolderIdByName(String folderName, Integer userId) {
        Integer id=this.folderMapper.getFolderIdByName(folderName,userId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public List<Folder> getAllFolders(int userId) {
        return this.folderMapper.findAllFolderByUserId(userId);
    }


    @Override
    public Folder getFolder(String folderName, int userId) {
        return this.folderMapper.getFolder(folderName,userId);
    }

    public List<Integer> getFolderIdsByUserId(int userId) {
        return this.folderMapper.getFolderIdsByUserId(userId);
    }

    @Override
    public int insertFolder(Folder record) {
        Integer id=this.folderMapper.insert(record);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public Folder getFolderById(int folderId) {
        return this.folderMapper.getFolderById(folderId);
    }

    @Override
    public int updateFolderStatus(String status, int folderId) {
        Integer id=this.folderMapper.updateFolderStatusById(status,folderId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }
}
