package com.lwy.dat.controller;/**
 * Created by lwy on 2017/5/14.
 */

import com.lwy.dat.pojo.Chart;
import com.lwy.dat.pojo.Folder;
import com.lwy.dat.pojo.Table;
import com.lwy.dat.service.ChartService;
import com.lwy.dat.service.FolderService;
import com.lwy.dat.service.TableService;
import com.lwy.dat.service.UserService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Date;
import java.util.*;

/**
 * oper file with reading and writing
 *
 * @author 陆文云
 * @create 2017-05-14 8:04
 **/
@Controller("fileController")
@RequestMapping("/api/file")
public class FileController {
    private final String RESPOSITORY="repository";
    private final String  PERSISTENT="persistent";
    public static String username;
    private Workbook workbook;
    private Sheet sheet;
    private Row row;
    private Cell cell;
    @Resource
    UserService userService;
    @Resource
    FolderService folderService;
    @Resource
    TableService tableService;
    @Resource
    ChartService chartService;
    @Resource
    Table table;

    @ResponseBody
    @RequestMapping(value="/getFileField",produces = "text/html;charset=UTF-8")
    public String getFileField(@RequestParam("folderName")String folderName,
                               @RequestParam("fileName")String fileName,
                               HttpServletRequest request,HttpServletResponse response)throws  IOException{
        //初始化返回的json字符串
        String error="";
        String status="0";
        String content;
        Map result=new HashMap();
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
            if(fileName==null||fileName.equals("")){
                error="文件名不能为空";
                throw new Exception(error);
            }
            if(folderName==null||folderName.equals("")){
                error="文件夹名不能为空";
                throw new Exception(error);
            }
            //查看数据库中是否有该文件
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            int tableId=tableService.getTableIdByName(fileName,folderId);
            if(tableId<=0){
                error="文件名不存在";
                throw new Exception(error);
            }
            //读取文件系统中的文件
            String path=request.getSession().getServletContext().getRealPath("/")+RESPOSITORY+
                    "\\"+username+"\\"+PERSISTENT+"\\"+folderName+"\\"+fileName;
            String fileType=fileType(username,folderName,fileName);
            File file=new File(path+"."+fileType);
            if(fileType.equals("xlsx")){
                workbook=new XSSFWorkbook(new FileInputStream(file));
            }else{
                workbook=new HSSFWorkbook(new FileInputStream(file));
            }
            Sheet sheet=workbook.getSheetAt(0);
            Row row=sheet.getRow(0);
            int columns=row.getPhysicalNumberOfCells();
            String cellList="";
            for(int i=0;i<columns;i++){
                Cell cell=row.getCell(i);
                String cellValue=(String)UploadFileController.getValue(cell).get("value");
                if(i==0){
                    cellList=cellValue;
                }else{
                    cellList=cellList+"/"+cellValue;
                }
            }
            result.put("field",cellList);
            status="1";
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            map.put("error",error);
            map.put("status",status);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }

    @ResponseBody
    @RequestMapping(value="/all_lose_file",produces ="text/html;charset=UTF-8")
    public String allLoseFile()throws IOException{
        //初始化返回的json字符串
        String error="";
        String content;
        String status="0";
        List result=new ArrayList();
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
            //
            int userId=userService.getUserIdByname(username);
            List<Folder> folderList=folderService.getAllFolders(userId);
            for(int i=0;i<folderList.size();i++){
                Folder folder=folderList.get(i);
                if(folder.getStatus().equals("0")){
                    Map folderMap=new HashMap();
                    map.put("ctime",folder.getCtime());
                    map.put("folderName",folder.getFolderName());
                    map.put("type","folder");
                    result.add(folderMap);
                }
                int folderId=folder.getFolderId();
                String folderName=folder.getFolderName();
                List<String> tableNames=tableService.getTableNamesByFolderId(folderId);
                for(int j=0;j<tableNames.size();j++){
                    table=tableService.getTableByName(tableNames.get(j),folderId);
                    String status_=table.getStatus();
                    if(status_.equals("0")){
                        Map tableMap=new HashMap();
                        tableMap.put("ctime",table.getCtime());
                        tableMap.put("fileName",table.getTableName());
                        tableMap.put("folderName",folderName);
                        tableMap.put("child",table.getChild());
                        tableMap.put("parent",table.getParent());
                        tableMap.put("comments",table.getComments());
                        tableMap.put("type","file");
                        result.add(tableMap);
                    }
                }
            }
            status="1";
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            //返回json字符串
            map.put("status",status);
            map.put("error",error);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/changeFileStatus",produces = "text/html;charset=utf-8")
    public String setFileStatus(@RequestParam(value="fileName",required = false)String fileName,
                                @RequestParam(value="folderName",required = false)String folderName,
                                @RequestParam(value="type",required =false)String type,
                                HttpServletRequest request,HttpServletRequest response)throws IOException{
        //初始化返回的json字符串
        String error="";
        String content;
        String status="0";
        boolean result=false;
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
            //合法性检测
            if(fileName==null||fileName.equals("")){
                error="文件名不能为空";
                throw new Exception(error);
            }
            if(folderName==null||folderName.equals("")){
                error="文件夹名不能为空";
                throw new Exception(error);
            }
            if(type==null||type.equals("")){

            }
            System.out.println("fileName:"+fileName+"\n\r"+"folderName:"+folderName);
            //获取文件id
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            System.out.println("folderId:"+folderId);
            int tableId=tableService.getTableIdByName(fileName,folderId);
            System.out.println("tableId:"+tableId);
            if(type.equals("0")){
                //设置文件为失效状态
                tableService.updateFileStatusById(type,tableId);
            }else{
                //恢复文件状态
                tableService.updateFileStatusById(type,tableId);
            }
            result=true;
        }catch (Exception ex){
            ex.printStackTrace();
        }finally{
            //返回json字符串
            map.put("status",status);
            map.put("error",error);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value="/orm",produces = "text/html;charset=UTF-8")
    public String getOrm(@RequestParam(value="fileName",required = false)String fileName,
                         @RequestParam(value="folderName",required = false)String folderName,
                         HttpServletRequest request,HttpServletRequest response)throws  IOException{
        String error="";
        String status="0";
        String content;
        List parents=null;
        List children=null;
        List chartNameList=null;
        Map result=new HashMap();
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
            //合法性检测
            if(fileName==null||fileName.equals("")){
                error="文件名不能为空";
                throw new Exception(error);
            }
            if(folderName==null||folderName.equals("")){
                error="文件夹名不能为空";
                throw new Exception(error);
            }
            System.out.println("enter orm");
            //获取文件信息
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            table=tableService.getTableByName(fileName,folderId);
            //获取图表信息
            int tableId=tableService.getTableIdByName(fileName,folderId);
            List<Chart> chartList=chartService.getChartsById(tableId);
            //获取表的父母子表
            String parent=table.getParent();
            System.out.println("parent:"+parent);
            parents=new ArrayList();
            if(parent!=null){
                if(parent.contains("/")){
                    String [] parentList=parent.split("/");
                    for(int i=0;i<parentList.length;i++){
                        parents.add(parentList[i]);
                    }
                }else{
                    parents.add(parent);
                }
            }
            String child=table.getChild();
            System.out.println("child:"+child);
            children=new ArrayList();
            if(child!=null){
                if(child.contains("/")){
                    String [] childList=child.split("/");
                    for(int i=0;i<childList.length;i++){
                        children.add(childList[i]);
                    }
                }else{
                    children.add(child);
                }
            }
            //获取图表名
            chartNameList=new ArrayList();
            if(chartList!=null){
                for(Chart chart:chartList){
                    String chartName=chart.getChartName();
                    System.out.println("chartName:"+chartName);
                    chartNameList.add(chartName);
                }
            }
            result.put("parents",parents);
            result.put("children",children);
            result.put("charts",chartNameList);
            status="1";
        }catch (Exception ex){
            error="服务器错误";
            ex.printStackTrace();
        }finally{
            map.put("error",error);
            map.put("status",status);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/read",produces = "text/html;charset=UTF-8")
    public String readFile(@RequestParam(value ="fileName",required = false)String fileName,
                           @RequestParam(value="folderName",required = false)String folderName,
                           HttpServletRequest request, HttpServletResponse response)throws IOException{
        System.out.println("enter readFile method");
        String error="";
        String status="0";
        Map map=new HashMap();
        ObjectMapper mapper=new ObjectMapper();
        Map result=new HashMap();
        String content="";
        int rows=0;
        int columns=0;
        try {
            if (fileName == null || folderName == null || fileName.equals("") || folderName.equals("")) {
                error = "你提交的文件夹或者文件名为空";
                throw new Exception(error);
            }
            if(fileName.matches("[*/\\:\"?<>|]")==true||folderName.matches("[*/\\:\"?<>|]")==true){
                error="文件夹或者文件不能含有特殊的字符!";
                throw new Exception(error);
            }
            System.out.println("enter normal");
            String filePath = request.getSession().getServletContext().getRealPath("/") + "\\" + RESPOSITORY+"\\" + username +"\\"+ PERSISTENT + "\\" + folderName + "\\" + fileName;
            String fileType=fileType(username,folderName,fileName);
            StringBuffer buffer=new StringBuffer();
            buffer.append(filePath).append(".").append(fileType);
            File file = new File(new String(buffer));
            //工作簿对象
            if(fileType.equals("xls")){
                System.out.println("xls");
                workbook= new HSSFWorkbook(new FileInputStream(file));
            }else if(fileType.equals("xlsx")){
                System.out.println("xlsx");
                workbook=new XSSFWorkbook(new FileInputStream(file));
            }else{
                workbook=UploadFileController.convertCSVorTXTtoXLS(new FileInputStream(file),".xlsx");
            }
            //表单对象
            sheet=workbook.getSheetAt(0);
            //行对象
            row=sheet.getRow(0);
            //表格对象
            cell=row.getCell(0);
            //物理行数
            rows=sheet.getPhysicalNumberOfRows();
            //物理列数
            columns=row.getPhysicalNumberOfCells();
            System.out.println("lastNum:"+row.getLastCellNum()+"  "+"rowNUM:"+row.getRowNum());
            //读取表中每一行的数据
            List data=new ArrayList();
            System.out.println("row、columns："+rows+"、"+columns);
            int flag=0;//测试迭代次数
            for(int i=0;i<rows;i++){
                row=sheet.getRow(i);
                List list=new ArrayList();
                Iterator<Cell> iterator=row.cellIterator();//使用内置的行迭代器来迭代单元格
                while (iterator.hasNext()){
                    cell=iterator.next();
                    list.add(UploadFileController.getValue(cell).get("value"));
                    if(i==0){
                        flag++;
                    }
                }
                data.add(list);
            }
            System.out.println("flag:"+flag);
            result.put("data",data);
            status="1";
        }catch (Exception ex){
            ex.printStackTrace();
        }finally{
            map.put("rows",rows);
            map.put("columns",columns);
            map.put("status",status);
            map.put("error",error);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/create",produces = "text/html;charset=UTF-8")
    public String createFile(@RequestParam(value = "sourceTableName",required = false)String sourceTableName,
                             @RequestParam(value="folderName",required = false)String folderName,
                             @RequestParam(value="folderNameList",required = false)String folderNameList,
                             @RequestParam(value = "objTableName",required = false)String objTableName,
                             @RequestParam(value="fields",required = false)String fields,
                             @RequestParam(value = "startRow",required = false)String  startRow,
                             @RequestParam(value="endRow",required = false)String  endRow,
                             @RequestParam(value = "flag",required = false)String flag,
                             HttpServletRequest request, HttpServletResponse response)throws  IOException{

        //初始化变量
        String error="";
        String status="";
        String content="";
        boolean result=false;
        ObjectMapper mapper=new ObjectMapper();
        Map map = new HashMap();
        try{
            //验证用户过来的数据是否合法
            if(sourceTableName==null||objTableName==null||sourceTableName.equals("")||objTableName.equals("")){
                error="文件夹或者文件名不能为空！";
                throw new Exception(error);
            }
            if(sourceTableName.matches("[*\\:\"?<>|]")==true||objTableName.matches("[*/\\:\"?<>|]")==true){
                error="文件夹或者文件名不能含有特殊字符!";
                throw new Exception(error);
            }
            if(flag==null||flag.equals("")){
                error="请输入数据处理的操作方法";
                throw new Exception(error);
            }
            if(startRow==null||startRow.equals("")){
                startRow="1";
            }
            if(endRow==null||endRow.equals("")){
                endRow="0";
            }
            System.out.println("folderName:"+folderName);
            //目标表路径
            String  newFilePath=request.getSession().getServletContext().getRealPath("/")+
                    RESPOSITORY+"\\"+username+"\\"+PERSISTENT+"\\"+folderName+"\\"+objTableName;
            String fileType;
            String oldFilePath;
            //创建分表
            if(flag.equals("split")) {
                System.out.println("enter split");
                //源表路径
                oldFilePath=request.getSession().getServletContext().getRealPath("/")+"\\"+
                        RESPOSITORY+"\\"+username+"\\"+PERSISTENT+"\\"+folderName+"\\"+sourceTableName;
                //字段
                String[] fieldsList=null;
                fileType=fileType(username,folderName,sourceTableName);
                if(fields.contains("/")){
                    fieldsList=fields.split("/");
                }else{
                    fieldsList=new String[1];
                    fieldsList[0]=fields;
                }
                //在仓库中创建分表
                Map fileMap=createSplitTable(newFilePath, oldFilePath,fieldsList,startRow,endRow,fileType);
                //需要在数据库记录生成的分表信息
                System.out.println("tableId:"+table.getTableId());
                table.setCtime(new Date(new java.util.Date().getTime()));
                table.setTableName(objTableName);
                table.setOriginalName(objTableName);
                table.setParent(sourceTableName);
                table.setColumns(fieldsList.length);
                table.setStatus("1");
                //除了开始行到终止行外，还有字段一行。
//                int rows=Integer.parseInt(endRow)+2-Integer.parseInt(startRow);
                table.setRows((int)fileMap.get("rows"));
                //获取文件夹ID
                int userId=userService.getUserIdByname(username);
                int folderId=folderService.getFolderIdByName(folderName,userId);
                table.setFolderId(folderId);
                table.setType(fileType);
                tableService.insert(table);
                //更新源表的子表字段信息-追加
                String child=tableService.getTableByName(sourceTableName,folderId).getChild();
                System.out.println("child:"+child);
                int tableId=tableService.getTableIdByName(sourceTableName,folderId);
                if(child==null||child.equals("")){
                    //如果源表中的child字段没有值就直接添加
                    tableService.updateFileChild(objTableName,tableId);
                }else{
                    //如果源表中的child字段有值就追加
                    String children=child+"/"+objTableName;
                    tableService.updateFileChild(children,tableId);
                }
                result=true;
            }else{//创建合表表
                System.out.println("enter join");
                //把文件名列表与文件对应的类型用键值对对应起来。
                Map fileMap=new HashMap();
                String [] filesList=null;
                String [] folderList=null;
                if(sourceTableName.contains("/")){
                    filesList=sourceTableName.split("/");
                    folderList=folderNameList.split("/");
                    System.out.println(sourceTableName+":"+folderNameList);
                    for(int i=0;i<filesList.length;i++){
                        String fileType_=fileType(username,folderList[i],filesList[i]);
                        fileMap.put(filesList[i],fileType_);
                    }
                }else{
                    String fileType_=fileType(username,folderNameList,sourceTableName);
                    fileMap.put(sourceTableName,fileType_);
                }
                String [] oldFilePathList=new String[filesList.length];
                for(int i=0;i<filesList.length;i++){
                    oldFilePath=request.getSession().getServletContext().getRealPath("/")+"\\"+
                                        RESPOSITORY+"\\"+username+"\\"+PERSISTENT+"\\"+folderList[i]+"\\"+filesList[i];
                    oldFilePathList[i]=oldFilePath+"."+fileMap.get(filesList[i]);
                }
                //测试文件路径是否正确
                for(int i=0;i<oldFilePathList.length;i++){
                    System.out.println(oldFilePathList[i]);
                }
                //在仓库中创建和表
                Map fileInfoMap=createJoinTable(newFilePath,oldFilePathList);
                if(fileMap==null){
                    error="创建合表失败!";
                    throw  new Exception("");
                }
                //需要在数据库记录生成的合表信息
                table.setCtime(new Date(new java.util.Date().getTime()));
                table.setTableName(objTableName);
                table.setOriginalName(objTableName);
                table.setParent(sourceTableName);
                table.setColumns((int)fileInfoMap.get("columns"));
                table.setRows((int)fileInfoMap.get("rows"));
                table.setStatus("1");
                //获取文件夹ID
                int userId=userService.getUserIdByname(username);
                int folderId=folderService.getFolderIdByName(folderName,userId);
                table.setFolderId(folderId);
                table.setType((String) fileInfoMap.get("fileType"));
                tableService.insert(table);
                //更新源表的子表信息
                for(int i=0;i<folderList.length;i++){
                    int folderId_=folderService.getFolderIdByName(folderList[i],userId);

                    String child=tableService.getTableByName(filesList[i],folderId_).getChild();
                    System.out.println("child:"+child);
                    int tableId=tableService.getTableIdByName(filesList[i],folderId_);
                    if(child==null||child.equals("")){
                        tableService.updateFileChild(objTableName,tableId);
                    }else{
                        System.out.println("objTableName:"+objTableName);
                        String children=child+"/"+objTableName;
                        System.out.println("children:"+children);
                        tableService.updateFileChild(children,tableId);
                    }
                }
                result=true;
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            //返回的json字符串
            map.put("error",error);
            map.put("status",status);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/update",produces = "text/html;charset=UTF-8")
    public String updateFile(@RequestParam(value = "fileName",required = false)String fileName,
                             @RequestParam(value="folderName",required = false)String folderName,
                             @RequestParam(value="fieldValueList",required = false)String appendData,
                             @RequestParam(value="matchText",required = false)String matchData,
                             @RequestParam(value="replaceText",required = false)String replaceData,
                             @RequestParam(value="fields",required = false)String fields,
                             @RequestParam(value="startRow",required = false)String startRow,
                             @RequestParam(value="endRow",required = false)String endRow,
                             @RequestParam(value="type",required = false) String type,
                             HttpServletRequest request, HttpServletResponse response)throws Exception{
        String error="";
        String status="";
        String content="";
        boolean result=false;
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
            //验证数据
            if(fileName==null||fileName.equals("")||folderName==null||folderName.equals("")
                    ||type==null||type.equals("")){
                error="你输入的数据不能为空!";
                throw new Exception(error);
            }
            if(fileName.matches("[*/\\:\"?<>|]")==true||folderName.matches("[*/\\:\"?<>|]")==true){
                error="你输入的数据含有非法字符!";
                throw new Exception(error);
            }
            //源表路径
            String sourceFilePath=request.getSession().getServletContext().getRealPath("/")+
                    "\\"+RESPOSITORY+"\\"+username+"\\"+PERSISTENT+"\\"+folderName+"\\"+fileName;
            //表类型
            String fileType=fileType(username,folderName,fileName);
            //获取文件ID
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            int tableId=tableService.getTableIdByName(fileName,folderId);
            //存储文件信息
            Map fileInfoMap=null;
            //判断操作类型:追加数据、替换数据、添加字段、删除字段
            System.out.println("type:"+type);
            System.out.println(fields);
            switch (type){
                case "1":
                    System.out.println(appendData);
                    //追加数据
                    if(appendData==null||appendData.equals("")){
                        error="请输入追加的数据！";
                        throw  new Exception(error);
                    }
                    System.out.println("enter appendData");
                    fileInfoMap=appendData(sourceFilePath,appendData,fileType);
                    //更新数据库行数记录
                    tableService.updateFileRows((int)fileInfoMap.get("rows"),tableId);
                    result=true;
                    break;
                case "2":
                    //替换数据
                    System.out.println("enter replace");
                    if(fields==null||fields.equals("")){
                        error="请先选择你要替换的字段！";
                        throw new Exception(error);
                    }
                    if(replaceData==null||replaceData.equals("")){
                        error="请输入你要替换的数据！";
                        throw new Exception(error);
                    }
                    if(matchData==null||fields.equals("")){
                        error="请输入你要匹配的数据！";
                        throw  new Exception(error);
                    }
                    if(startRow==null||startRow.equals("")){
                        startRow="1";
                    }
                    if(endRow==null||endRow.equals("")){
                        endRow="0";
                    }
                    replaceData(sourceFilePath,fields,replaceData,matchData,startRow,endRow,fileType);
                    result=true;
                    break;
                case "3":
                    //添加字段
                    if(fields==null||fields.equals("")){
                        error="请输入你要添加的字段！";
                        throw new Exception(error);
                    }
                    fileInfoMap=addField(sourceFilePath,fields,fileType);
                    //更新数据库列数记录
                    tableService.updateFileColumns((int)fileInfoMap.get("columns"),tableId);
                    result=true;
                    break;
                case "4":
                    //删除字段
                    if(fields==null||fields.equals("")){
                        error="请输入你要删除的字段";
                        throw new Exception(error);
                    }
                    fileInfoMap=delField(sourceFilePath,fields,fileType);
                    //更新数据数据库列数记录
                    tableService.updateFileColumns((int)fileInfoMap.get("columns"),tableId);
                    result=true;
                    break;
                default:

                    break;
            }

        }catch (Exception ex){
            error="操作失败";
            ex.printStackTrace();
        }finally{
            map.put("status",status);
            map.put("error",error);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    private Map createSplitTable(String newFilePath,String oldFilePath,String[] fieldsList,String _startRow,String _endRow,String fileType)throws Exception{
        //返回新表的信息
        Map map=new HashMap();
        Workbook oldFileWorkbook=null,newFileWorkbook=null;
        Sheet oldFileSheet,newFileSheet;
        Row oldFileRow,newFileRow;
        Cell oldFileCell,newFileCell;
        int rows;
        int newRows=0;
        int columns;
        int startRow=Integer.parseInt(_startRow);
        int endRow=Integer.parseInt(_endRow);
        try {

            //记录创建的新表记录
            int newFileRecord=1;
            File oldFile = new File(oldFilePath+"."+fileType);
            System.out.println("创建文件对象1成功");
            File newFile = new File(newFilePath+"."+fileType);
            System.out.println("创建文件对象2成功");
            //新、源工作簿对象
            if(fileType.equals("xls")){
                oldFileWorkbook=new HSSFWorkbook(new FileInputStream(oldFile));
                newFileWorkbook=new HSSFWorkbook();
            }else{
                oldFileWorkbook=new XSSFWorkbook(new FileInputStream(oldFile));
                newFileWorkbook=new XSSFWorkbook();
            }
            System.out.println("创建工作簿对象成功");
            //新、源表单对象
            oldFileSheet=oldFileWorkbook.getSheetAt(0);
            newFileSheet=newFileWorkbook.createSheet("newFile");
            oldFileRow=oldFileSheet.getRow(0);
            newFileRow=newFileSheet.createRow(0);
            //源表的行数和列数
            rows=oldFileSheet.getPhysicalNumberOfRows();
            columns=oldFileRow.getLastCellNum();
            if(startRow<1||startRow>=endRow){
                startRow=1;
            }
            if(endRow<1||endRow>rows){
                endRow=rows;
            }
//            //合法性检测
//            if(rows<=startRow||startRow<=1){
//                startRow=2;
//            }
//            if(rows<endRow||endRow<=1||startRow>=endRow){
//                endRow=startRow+10;
//            }
            //把用源表的字段生成分表字段对应的列数取出来。
            List newFieldNOList=new ArrayList();
            for(int i=0;i<columns;i++){
                oldFileCell=oldFileRow.getCell(i);
                String cellValue=(String)UploadFileController.getValue(oldFileCell).get("value");
                for(int j=0;j<fieldsList.length;j++){
                    if(cellValue.equals(fieldsList[j])){
                        newFieldNOList.add(i);
                    }
                }
            }
            System.out.println("创建字段成功");
            //把源表的字段先写入目标表
            row=oldFileSheet.getRow(0);
            int flag=0;
            for(int i=0;i<columns;i++){
                oldFileCell=oldFileRow.getCell(i);
                String cellValue=(String)UploadFileController.getValue(oldFileCell).get("value");
                for(int j=0;j<newFieldNOList.size();j++){
                    if(i==(int)newFieldNOList.get(j)){
                        newFileCell=newFileRow.createCell(flag++);
                        newFileCell.setCellValue(cellValue);
                    }
                }
            }
            //遍历源表记录
            //取出满足条件的记录
            System.out.println(startRow+":"+endRow);
            for(int i=startRow;i<endRow;i++){
                //源表行对象
                oldFileRow=oldFileSheet.getRow(i);
                if(oldFileRow==null||oldFileRow.equals("")){
                    continue;
                }
                //新表行对象
                newFileRow=newFileSheet.createRow(newFileRecord++);
                for (int j = 0; j < columns; j++) {
                    //把源表单元格的数据放进新表的单元格
                    for(int num=0;num<newFieldNOList.size();num++){
                        //新表单元格对象
                        if(j==(int)newFieldNOList.get(num)){
                            //源表单元格对象
                            oldFileCell = oldFileRow.getCell(j);
                            String oldFileCellValue = (String) UploadFileController.getValue(oldFileCell).get("value");
                            //System.out.println(oldFileCellValue);
                            newFileCell=newFileRow.createCell(num);
                            newFileCell.setCellValue(oldFileCellValue);
                        }
                    }
                }

            }
            FileOutputStream fileOUT=new FileOutputStream(newFile);
            newFileWorkbook.write(fileOUT);
            fileOUT.close();
        }catch (FileNotFoundException fileEX){
            throw new FileNotFoundException("文件没有发现");
        }
        catch(Exception ex){
            throw new Exception("创建分表失败");
        }finally {

            if(oldFileWorkbook!=null){
                oldFileWorkbook.close();
            }
            if(newFileWorkbook!=null){
                newFileWorkbook.close();
            }
        }
        newRows=endRow+2-startRow;
        System.out.println("newRows:"+newRows);
        map.put("rows",newRows);
        return map;
    }

    /**
     * <p>make a newFile with all the file that had exist</p>
     * @param newFilePath
     * @param oldFilePathList
     * @throws Exception
     */
    private Map createJoinTable(String newFilePath,String[] oldFilePathList)throws  Exception {
        //开始时间
        long startTime=new java.util.Date().getTime();
        //返回生成的合表的信息
        Map map=new HashMap();
        Workbook workbook = null, newWorkbook = null;
        try {
            Sheet sheet, newSheet;
            Row row, newRow;
            Cell cell, newCell;
            int rows=0;
            int columns=0;
            int newRows = 1;
            //先用一张源表复制到新表
            File newFile = new File(newFilePath + ".xlsx");
            newWorkbook = new SXSSFWorkbook();
            newSheet = newWorkbook.createSheet("0");
            for (int i = 0; i < oldFilePathList.length; i++) {
                //新、源工作簿对象
                if (oldFilePathList[i].endsWith(".xls")) {
                    workbook = new HSSFWorkbook(new FileInputStream(new File(oldFilePathList[i])));
                } else {
                    workbook = new XSSFWorkbook(new FileInputStream(new File(oldFilePathList[i])));
                }
                sheet = workbook.getSheetAt(0);
                row = sheet.getRow(0);
                //文件列数
                columns = row.getPhysicalNumberOfCells();
                //读取文件的实质行数
                rows = sheet.getLastRowNum();
                //给新表添加字段
                if(i==0) {
                    newRow = newSheet.createRow(0);
                    for (int j = 0; j < columns; j++) {
                        cell = row.getCell(j);
                        String cellValue = (String) UploadFileController.getValue(cell).get("value");
                        System.out.print(cellValue);
                        newCell = newRow.createCell(j);
                        newCell.setCellValue(cellValue);
                    }
                }
                for (int j = 1; j < rows; j++) {
                    row = sheet.getRow(j);
                    if (row == null || row.equals("")) {
                        continue;
                    }
                    newRow = newSheet.createRow(newRows++);
                    for (int i_ = 0; i_ < columns; i_++) {
                        cell = row.getCell(i_);
                        String cellValue = (String) UploadFileController.getValue(cell).get("value");
                        newCell = newRow.createCell(i_);
                        newCell.setCellValue(cellValue);
                    }
                }

            }
            map.put("rows",rows);
            map.put("columns",columns);
            map.put("fileType","xlsx");
            FileOutputStream fileOUT=new FileOutputStream(newFile);
            newWorkbook.write(fileOUT);
            fileOUT.close();

        }catch (Exception ex){
            throw new Exception("创建合表失败");
        }finally {
            if(newWorkbook!=null){
                newWorkbook.close();
            }
            if(workbook!=null){
                workbook.close();
            }
        }
        long endTime=new java.util.Date().getTime();
        System.out.println("sumTime:"+(endTime-startTime));
        return map;
    }

    /**
     * <p>get the type of file</p>
     * @param username
     * @param folderName
     * @param fileName
     * @return
     */
    public  String fileType(String username,String folderName,String fileName ){
        //从数据库中获取用户ID
        int userId=userService.getUserIdByname(username);
        //根据用户ID和文件夹名获取文件夹ID
        int folderId=folderService.getFolderIdByName(folderName,userId);
        //根据文件夹id和文件夹名获取文件实体,再取出type
        String fileType= tableService.getTableByName(fileName,folderId).getType();
        return fileType;
    }

    /**
     * <p>append data to the sourceFile</p>
     * @param path
     * @param appendData
     * @param type
     * @throws Exception
     */
    private Map appendData(String path,String appendData,String type)throws Exception{
        Map map=new HashMap();
        Workbook workbook=null;
        Sheet sheet;
        Row row;
        Cell cell;
        int rows;
        int columns;
        try {
            //文件对象
            System.out.println(path+"."+type);
            File file=new File(path+"."+type);
            //根据文件类型创建工作簿对象
            if(type.equals("xls")){
                workbook=new HSSFWorkbook(new FileInputStream(file));
            }else{
                workbook=new XSSFWorkbook(new FileInputStream(file));
            }
            //把要追加的数据分离到字符串数组中
            sheet=workbook.getSheetAt(0);
            //行数
            rows=sheet.getPhysicalNumberOfRows();
            row=sheet.getRow(0);
            //列数
            columns=row.getLastCellNum();
            //先确定要追加字段的位置
            List fieldNOList=new ArrayList();
            for(int i=0;i<columns;i++){
                cell=row.getCell(i);
                if(cell!=null){
                    fieldNOList.add(i);
                }
            }
            String[] fieldValues = appendData.split("/");
            System.out.println(fieldValues.length);
            int num=0;
            //追加数据
            row=sheet.createRow(rows);
            for(int i=0;i<columns;i++){
                cell=row.createCell(i);
                for(int j=0;j<fieldNOList.size();j++) {
                    if (i == (int) fieldNOList.get(j)) {
                        if (i == columns - 1) {
                            if (appendData.endsWith("/")) {
                                cell.setCellValue("");
                            } else {
                                cell.setCellValue(fieldValues[num++]);
                            }
                        } else {
                            cell.setCellValue(fieldValues[num++]);
                        }
                    }
                }
            }
            //写入数据
            map.put("rows",rows+1);
            FileOutputStream fileOUT=new FileOutputStream(file);
            workbook.write(fileOUT);
            fileOUT.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(workbook!=null){
                workbook.close();
            }
        }
        return map;

    }
    private void replaceData(String path,String field,String replaceData,String matchData,String startRow,String endRow,String type)throws  Exception{
        Workbook workbook;
        Sheet sheet;
        Row row;
        Cell cell;
        int columns;
        int rows;
        int startRows=Integer.parseInt(startRow);
        int endRows=Integer.parseInt(endRow);
        try{
            System.out.println(matchData+":"+replaceData);
            System.out.println(path+"."+type);
            //源表对象
            File file=new File(path+"."+type);
            System.out.println("field:"+field);

            //初始化
            if(type.equals("xls")){
                workbook=new HSSFWorkbook(new FileInputStream(file));
            }else{
                workbook =new XSSFWorkbook(new FileInputStream(file));
            }
            sheet=workbook.getSheetAt(0);
            row=sheet.getRow(0);
            rows=sheet.getPhysicalNumberOfRows();
            columns=row.getLastCellNum();
            if(startRows<1||startRows>=endRows){
                startRows=1;
            }
            if(endRows<1||endRows>rows){
                endRows=rows;
            }
            System.out.println(rows);
            //取出字段对应的列
            List fieldNoList=new ArrayList();
            for(int i=0;i<columns;i++){
                cell=row.getCell(i);
                String cellValue=(String)UploadFileController.getValue(cell).get("value");
                System.out.print("cellValue:"+cellValue);
                if(cellValue.equals(field)){
                    fieldNoList.add(i);
                }
            }
            //查找匹配
            System.out.println(startRows+":"+endRows);
            for(int i=startRows-1;i<endRows;i++){
                //行对象
                row=sheet.getRow(i);
                if(row==null||row.equals("")){
                    continue;
                }
                //查找匹配字段
                for(int j=0;j<columns;j++) {
                    System.out.println("fieldNoList:" + (int) fieldNoList.get(0));
                    cell = row.getCell(j);
                    String cellValue = (String) UploadFileController.getValue(cell).get("value");
//                    System.out.println("字段值:" + cellValue);
                    if(j==(int)fieldNoList.get(0)) {
                        if (cellValue.equals(matchData)) {//判断字段值是否与匹配值相同
//                            System.out.println("matchDATA:" + cellValue);
                            cell.setCellValue(replaceData);
                        }
                    }

                }

            }
            //写入
            FileOutputStream fileOUT=new FileOutputStream(file);
            workbook.write(fileOUT);
            fileOUT.close();
        }catch (Exception ex){

        }
    }
    private Map addField(String path,String fields,String type)throws  Exception{
        Map map=new HashMap();
        int newFileColumns;
        Workbook workbook=null;
        Sheet sheet;
        Row row;
        Cell cell;
        int rows;
        int columns;
        try{
            //源表名
            File file=new File(path+"."+type);
            if(type.equals("xls")){
                workbook=new HSSFWorkbook(new FileInputStream(file));
            }else{
                workbook=new XSSFWorkbook(new FileInputStream(file));
            }
            //添加的字段名
            String[] fieldValue = null;
            if (fields.contains("/")) {
                fieldValue = fields.split("/");
            }else{
                fieldValue=new String[1];
                fieldValue[0]=fields;
            }
            //初始化
            sheet=workbook.getSheetAt(0);
            row=sheet.getRow(0);
            rows=sheet.getPhysicalNumberOfRows();
            columns=row.getLastCellNum();
            newFileColumns=columns+fieldValue.length;
            //添加字段
            for(int i=0;i<rows;i++){
                row=sheet.getRow(i);
                if(row==null||row.equals("")){
                    continue;
                }
                if(i==0){
                    int index=columns - 1;//添加字段（列数）位置
                    for(int j=0;j<fieldValue.length;j++) {
                        cell = row.createCell(++index);
                        cell.setCellValue(fieldValue[j]);
                    }
                }else {
                    int index=columns-1;
                    for (int j = 0; j < fieldValue.length; j++) {
                        cell = row.createCell(++index);
                        cell.setCellValue("");
                    }
                }
            }
            map.put("columns",newFileColumns);
            //写入
            FileOutputStream fileOUT=new FileOutputStream(file);
            workbook.write(fileOUT);
            fileOUT.close();
        }catch (Exception ex){
          throw new Exception();
        }finally{
            if(workbook!=null){
                workbook.close();
            }
        }
        return map;
    }
    private Map delField(String path,String fields,String type)throws  Exception{
        //存储文件信息
        Map map=new HashMap();
        //新文件列数
        int newFileColumns;
        Workbook workbook=null;
        Sheet sheet;
        Row row;
        Cell cell;
        int rows;
        int columns;
        try{
            //字段名
            String[] fieldValue = null;
            if (fields.contains("/")) {
                fieldValue = fields.split("/");
            }else{
                fieldValue=new String[1];
                fieldValue[0]=fields;
            }
            //源表名
            File file=new File(path+"."+type);
            if(type.equals("xls")){
                workbook=new HSSFWorkbook(new FileInputStream(file));
            }else{
                workbook=new XSSFWorkbook(new FileInputStream(file));
            }
            //初始化
            sheet=workbook.getSheetAt(0);
            row=sheet.getRow(0);
            rows=sheet.getPhysicalNumberOfRows();
            columns=row.getPhysicalNumberOfCells();
            System.out.println("columns:"+columns);
            newFileColumns=columns-fieldValue.length;
            //取出字段对应的列
            int [] fieldNoList=new int[fieldValue.length];
            Iterator<Cell> iterator=row.cellIterator();
            int flag=0;//标志删除字段
            while (iterator.hasNext()){
                cell=iterator.next();
                String cellValue=(String)UploadFileController.getValue(cell).get("value");
                for(int j=0;j<fieldValue.length;j++){
                    if(cellValue.equals(fieldValue[j])){
                        fieldNoList[j]=flag;
                        System.out.println("j:"+flag);
                    }
                }
                flag++;
            }
            //查找删除
            for(int i=0;i<rows;i++){//遍历整个表
                row=sheet.getRow(i);
                if(row==null||row.equals("")){
                    continue;
                }
                iterator=row.cellIterator();
                flag=0;
                while(iterator.hasNext()){//迭代一行
                    cell=iterator.next();
                    for(int num=0;num<fieldNoList.length;num++){//查找匹配的字段
                        if(flag==fieldNoList[num]){
                            System.out.println("del:"+flag);
                            row.removeCell(cell);
                        }
                    }
                    flag++;
                }
            }
            System.out.println("newFileColumns:"+newFileColumns);
            map.put("columns",newFileColumns);
            //删除
            FileOutputStream fileOUT=new FileOutputStream(file);
            workbook.write(fileOUT);
            fileOUT.close();
        }catch (Exception ex){
            throw new Exception();
        }finally {
            if(workbook!=null){
                workbook.close();
            }
        }
        return map;
    }

}
