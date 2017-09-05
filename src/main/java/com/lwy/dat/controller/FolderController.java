package com.lwy.dat.controller;/**
 * Created by lwy on 2017/5/2.
 */

import com.lwy.dat.pojo.Folder;
import com.lwy.dat.pojo.Table;
import com.lwy.dat.service.FolderService;
import com.lwy.dat.service.TableService;
import com.lwy.dat.service.UserService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * folder list
 *
 * @author 陆文云
 * @create 2017-05-02 20:05
 **/
@Controller
@RequestMapping("/api/folder")
public class FolderController {
    public static String username;
    private final String REPOSITORY="repository";
    private final String PERSISTENT="persistent";
    @Resource
    Folder folder;
    @Resource
    FolderService folderService;
    @Resource
    TableService tableService;
    @Resource
    UserService userService;
    @ResponseBody
    @RequestMapping(value="/changeFolderStatus",produces = "text/html;charset=UTF-8")
    public String setFileStatus(@RequestParam(value="folderName",required = false)String folderName,
                                @RequestParam(value="type",required = false)String type,
                                HttpServletRequest request,HttpServletResponse response)throws  IOException{
        String error="";
        String content;
        String status="0";
        boolean result=false;
        Map map=new HashMap();
        ObjectMapper mapper=new ObjectMapper();
        try{
            if(folderName==null||folderName.equals("")){
                error="文件夹名不能为空";
                throw new Exception(error);
            }
            if(type==null||type.equals("")){
                error="操作类型不能为空";
                throw new Exception(error);
            }
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            if(type.equals("0")){//设置文件夹失效
                folderService.updateFolderStatus(type,folderId);
            }else{//恢复文件夹
                folderService.updateFolderStatus(type,folderId);
            }
            status="1";
            result=true;
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            map.put("error",error);
            map.put("status",status);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    /**
     * <p>list all folders</p>
     * @param hastable
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list",produces = "text/html;charset=UTF-8")
    public String folderList(@RequestParam(value="hastable",required = false)boolean hastable, HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getCharacterEncoding());
        response.setCharacterEncoding("UTF-8");
        int userId=userService.getUserIdByname(username);
        System.out.println(userId);
        List<Folder> list= folderService.getAllFolders(userId);
        ObjectMapper mapper=new ObjectMapper();
        Map map= new HashMap();
        Map mapFolders=null;
        List arr=null;
        String content="";
        String error="";
        String status="0";
        System.out.println("hastable:"+hastable);
        try {
            if(hastable!=true) {
                arr = new ArrayList();
                //只是把文件夹取出来
                for (int i = 0; i < list.size(); i++) {
                    String folderName =list.get(i).getFolderName();
                    arr.add(folderName);
                }
                map.put("result",arr);
            }else{
                //把所有文件夹都取出来，并把每个文件夹下的所有文件名读取出来。
                mapFolders=new HashMap();
                for (int i = 0; i < list.size(); i++) {
                    if(list.get(i).getStatus().equals("0")){//不读取失效的文件夹
                        System.out.println("失效文件夹:"+list.get(i).getFolderName());
                        continue;
                    }
                    int folderId=list.get(i).getFolderId();
                    List<String> listTables=new ArrayList<String>();
                    String folderName=list.get(i).getFolderName();
                    listTables=tableService.getTableNamesByFolderId(folderId);//所有文件名
                    List tablesList=new ArrayList();//所有有效的文件名
                    for(int j=0;j<listTables.size();j++){
                        Table table=tableService.getTableByName(listTables.get(j),folderId);
                        if(table.getStatus().equals("0")){
                            continue;
                        }
                        tablesList.add(table.getTableName());
                    }
                    if(listTables.size()==0){
                        mapFolders.put(folderName, "");
                    }
                    System.out.println(folderName);
                    mapFolders.put(folderName, tablesList);
                }
                map.put("result",mapFolders);
            }
            map.put("status",status);
            map.put("error",error);

            content = mapper.writeValueAsString(map);
        }catch (Exception io){
            error="服务器发生错误!";
            map.put("error",error);
            io.printStackTrace();
        }
        return content;

    }

    /**
     * <p>create the folder</p>
     * @param folderName
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/create",produces = "text/html;charset=UTF-8")
    public String createFolder(@RequestParam(value ="folderName",required = false)String folderName,HttpServletRequest request,HttpServletResponse response){
        String status = "0";
        String error = "";
        boolean result = false;
        String content = "";
        Map map = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (folderName == null || folderName.equals("")) {
                error = "文件名不能为空!";
                throw new Exception(error);
            }
            if (folderName.matches("[*/\\:\"?<>|]") == true) {
                error = "文件夹名不能含有[*/\\:\"?<>|]特殊字符";
                throw new Exception(error);
            }
            System.out.println(folderName);
            int userId=userService.getUserIdByname(username);
            if (folderService.getFolder(folderName, userId) != null) {
                error = "不能创建重复的仓库名";
                throw new Exception(error);
            }
            folder.setUserId(userId);
            folder.setFolderName(folderName);
            folder.setCtime(new Date(new java.util.Date().getTime()));
            folder.setType("1");
            folder.setStatus("1");
            folderService.insertFolder(folder);
            result = true;
            //把文件夹写入到用户空间上
            String folderPath=request.getSession().getServletContext().getRealPath("/")+
                    "\\"+REPOSITORY+"\\"+username+"\\"+PERSISTENT+"\\"+folderName;
            File newFolder=new File(folderPath);
            if(!newFolder.exists()){
                newFolder.mkdirs();
            }
        } catch (Exception io) {
            io.printStackTrace();
        }finally {
            map.put("status", status);
            map.put("error", error);
            map.put("result", result);
        }
        try {
            content = mapper.writeValueAsString(map);
        } catch (IOException ioe) {
            status="1";
            error = "map对象转换成json字符串失败！";
           //当json插件包出错时，自定义返回的json字符串。
            content = "{\"status\":" + status + ",\"error\":" + error + ",\"result\":" + result + "}";
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/delete",produces = "text/html;charset=UTF-8")
    public String deleteFolder(@RequestParam(value = "folderName",required = false)String folderName,HttpServletRequest request,HttpServletResponse response){
        String status = "0";
        String error = "";
        boolean result = false;
        String content = "";
        Map map = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (folderName == null || folderName.equals("")) {
                error = "文件名不能为空!";
                throw new Exception(error);
            }
            if (folderName.matches("[*/\\:\"?<>|]") == true) {
                error = "文件夹名不能含有[*/\\:\"?<>|]特殊字符";
                throw new Exception(error);
            }
            int userId=userService.getUserIdByname(username);
            if (folderService.getFolder(folderName, userId) == null) {
                error = "不能删除没有的仓库名！";
                throw new Exception(error);
            }
            //从用户空间中执行删除该文件夹下所有的文件并并删除文件
            boolean isSuccess=delete(request,folderName);
            int sum=0;
            if(isSuccess==true){
                //删除文件夹时，先删除该文件夹下的所有文件
                //获取该文件夹id,并执行删除。
                int folderId=folderService.getFolderIdByName(folderName,userId);
                //然后根据该id遍历所有的文件
                List<String> tableList=new ArrayList<String>();
                tableList=tableService.getTableNamesByFolderId(folderId);
                //遍历文件夹下所有文件并删除
                for(int i=0;i<tableList.size();i++){
                    tableService.deleteTable(tableList.get(i),folderId);
                }
                //删除文件夹
                sum=folderService.deleteFolder(folderName,userId);

            }
            if(sum!=0){
                result=true;
            }
        }catch (Exception io){
            io.printStackTrace();
        }finally {
            map.put("status", status);
            map.put("error", error);
            map.put("result", result);
        }
        try{
            content=mapper.writeValueAsString(map);
        }catch (IOException io){
            status="1";
            error="map对象转换成Json字符串失败!";
            content="{\"status\":" + status + ",\"error\":" + error + ",\"result\":" + result + "}";
            io.printStackTrace();
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/update",produces = "text/html;charset=UTF-8")
    public String updateFolder(@RequestParam(value="original_name",required = false)String original_name,
                               @RequestParam(value="folderName",required = false)String folderName,
                               HttpServletRequest request,HttpServletResponse reponse){
        String status = "0";
        String error = "";
        boolean result = false;
        String content = "";
        Map map = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (original_name == null || original_name.equals("")||folderName==null||folderName.equals("")) {
                error = "文件夹名不能为空!";
                throw new Exception(error);
            }
            if (original_name.matches("[*/\\:\"?<>|]") == true||folderName.matches("[*/\\:\"?<>|]") == true) {
                error = "文件夹名不能含有[*/\\:\"?<>|]特殊字符";
                throw new Exception(error);
            }
            //修改文件夹
            String folderPath = request.getSession().getServletContext().getRealPath("/") + "\\" + REPOSITORY +
                    "\\" + username + "\\" + PERSISTENT + "\\" + original_name;
            String newPath=request.getSession().getServletContext().getRealPath("/")+"\\"+REPOSITORY+
                    "\\"+username+"\\"+PERSISTENT+"\\"+folderName;
            File newFolder=new File(newPath);
            File folder = new File(folderPath);
            boolean flag=folderRename(folder,newFolder);
            int sum=0;
            if(flag==true) {
                int userId = userService.getUserIdByname(username);
                //先根据用户Id和源文件夹名获取源文件夹Id
                Integer folderId = folderService.getFolderIdByName(original_name, userId);
                //根据文件夹Id更新文件名
                sum = folderService.updateFolder(folderName, folderId);
            }
            if(sum!=0&&flag==true){
                result=true;
            }
        } catch (Exception io) {
            io.printStackTrace();
        }finally {
            map.put("status", status);
            map.put("error", error);
            map.put("result", result);
        }
        try {
            content = mapper.writeValueAsString(map);
        } catch (IOException ioe) {
            error = "map对象转换成json字符串失败！";
            status="1";
            content = "{\"status\":" + status + ",\"error\":" + error + ",\"result\":" + result + "}";
        }
        return content;
    }

    public boolean folderRename(File sourceFile,File objFile)throws  Exception{
        boolean result=false;
        try {
            System.out.println(sourceFile);
            System.out.println(objFile);
            //文件夹从命名
            if(!sourceFile.isDirectory()){
                return result;
            }
            System.out.println(sourceFile.listFiles().length);
            //源文件夹为空
            if(sourceFile.listFiles().length==0){
                if(!objFile.exists()){
                    boolean mk=objFile.mkdirs();
                    boolean del=sourceFile.delete();
                    if(mk==true&&del==true){
                        result=true;
                    }else{
                        throw new Exception("文件夹创建和删除失败！");
                    }
                }else{
                    throw new Exception("重命名的文件夹已经存在了！");
                }
            }else{//源文件夹不为空，把源文件中的文件一个个复制到另一个文件夹.
                File[] files=sourceFile.listFiles();
                if(!objFile.exists()){
                    Files.createDirectory(objFile.toPath());
                }
                for(File oldFile:files){
                    String fileName=oldFile.getName();
                    File newFile=new File(objFile,fileName);
                    Files.copy(oldFile.toPath(),newFile.toPath());//复制文件到新的路径
                    oldFile.delete();//删除源文件
                }
                //再删除已经为空的源文件夹
                Files.deleteIfExists(sourceFile.toPath());
                result=true;

            }
        }catch(Exception ex){
            ex.printStackTrace();
            throw  new Exception(ex);
        }
        return result;
    }
    public boolean delete(HttpServletRequest request,String folderName)throws  Exception{
        boolean result=false;
        try{
            //路径
            String path=request.getSession().getServletContext().getRealPath("/")+REPOSITORY+
                    "\\"+username+"\\"+PERSISTENT+"\\"+folderName;
            System.out.println(path);
            File filePath=new File(path);
            if(!filePath.exists()){
                throw new Exception("路径不存在");
            }else{
                File [] files=filePath.listFiles();
                if(files.length==0){//文件夹下没有文件的话直接执行删除
                    filePath.delete();
                    result=true;
                }else{//文件夹下有文件时，先删除文件再删除文件夹。
                    for(File file:files){
                        file.delete();
                    }
                    filePath.delete();
                    result=true;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            throw new Exception(ex);
        }
        return result;
    }

}

