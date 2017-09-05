package com.lwy.dat.controller;/**
 * Created by lwy on 2017/3/30.
 */

import com.lwy.dat.pojo.Folder;
import com.lwy.dat.pojo.Table;
import com.lwy.dat.service.FolderService;
import com.lwy.dat.service.TableService;
import com.lwy.dat.service.UserService;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.sql.Date;
import java.util.*;

/**
 * upload file
 *
 * @author 陆文云
 * @create 2017-03-30 14:22
 **/
@Controller
@RequestMapping("/api/excel")
public class UploadFileController {
    private int status=0;
    private static  String error="";
    private int rows=0;
    private int columns=0;
    private String name="";
    private List<List> data;
    private List<Map> schema;
    private Map<String,Object> map= new HashMap<String,Object>();
    private String content;
    private ObjectMapper mapper=new ObjectMapper();
    private InputStream in;
    private Workbook workbook;
    private Sheet sheet;
    private Row row;
    private Cell cell;
    private static boolean isEmpty=false;
    private static int type;
    public static String username;
    private String tableType;
    private static String value;
    private final String REPOSITORY="repository";
    private final String PERSISTENT="persistent";
    private final String TEMP="temp";
    private String path;
    @Resource
    private UserService userService;
    @Resource
    private TableService tableService;
    @Resource
    private FolderService folderService;
    @Resource
    private Table table;
    @Resource
    private Folder folder;
    private void init(){
        status=0;
        error="";
//        rows=0;
//        columns=0;
    }
    @RequestMapping(value="/download",method= RequestMethod.GET)
    public String download(@RequestParam("fileName") String fileName,
                           @RequestParam("folderName")String folderName,
                           HttpServletRequest request, HttpServletResponse response) throws  Exception{
        try {
            //初始化返回json字符串
            String error="";
            boolean result=false;
            String content="";
            String status="0";
            Map map=new HashMap();
            ObjectMapper mapper=new ObjectMapper();
            System.out.println(fileName+":"+folderName);
            if(fileName==null||fileName.equals("")||folderName==null||folderName.equals("")){
                 error="文件名或者文件夹名不能为空";
                 map.put("error",error);
                 map.put("status",status);
                 map.put("result",result);
                 content=mapper.writeValueAsString(map);
                 byte[] b=content.getBytes();
                //输出流
                OutputStream os = response.getOutputStream();
                os.write(b);
                os.close();
                return null;
            }
            //文件类型
            String type=fileType(username,folderName,fileName);
            String name=fileName+"."+type;
            response.setCharacterEncoding("utf-8");//设置返回的文件编码
            //文件路径
            String path=request.getServletContext().getRealPath("/")+REPOSITORY+"\\"+username+"\\"+PERSISTENT
                    +"\\"+folderName;
            System.out.println(path);
            File file=new File(path, fileName+"."+type);
            //把文件读入到输入流中
            InputStream inputStream = new FileInputStream(file);
            response.setContentType("application/vnd.ms-excel");
            //response.setContentType("multipart/form-data");//设置文件类型
            response.setHeader("Content-Disposition", "attachment;filename="+name);//设置显示文件名
            //输出流
            OutputStream os = response.getOutputStream();
            //缓存字节数组
            byte[] b = new byte[2048];
            int length;
            //把输入流中的文件分段读入缓存字节数组，再把字节数组中的数据写到输出流。
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
            //关闭流关闭。
            os.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  返回值要返回null，因为response已经调用getOutputStream，其他不能使用.
        //java+getOutputStream() has already been called for this response
        return null;
    }

    /**
     * upload file from client,the type of file is csv,txt or xls and xlsx.
     * @param file
     * @param request
     * @return string as json.
     */
    @ResponseBody
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @RequestMapping(value ="/upload",produces = "text/html;charset=UTF-8")
    public String upload(@RequestParam(value="file",required = false)MultipartFile file,
                         HttpServletRequest request,
                         HttpServletResponse response){
//        //初始化返回json字符串
//        String error="";
//        boolean result=false;
//        String content="";
//        String status="0";
//        Map map=new HashMap();
//        ObjectMapper mapper=new ObjectMapper();
//        String name;
//        InputStream in;
//        Workbook workbook;
        try{
            long startTime=new java.util.Date().getTime();
            //检测上传的文件类型以及上传的文件是否为空
            if(file==null||!file.getContentType().equals("application/vnd.ms-excel")||file.getSize()>5E7) {
                error="只能操作application/vnd.ms-excel类型文件或者文件过大";
                status=1;
                throw new Exception(error);
            }
            //文件名
            name = file.getOriginalFilename();
            //文件输入流
            in=file.getInputStream();
            //根据上传的文件类型使用创建不同的工作簿对象
            if(in==null)
                return returnError();
            if(file.getContentType().equals("application/vnd.ms-excel")&&name.endsWith(".xls")){
                workbook=new HSSFWorkbook(in);
            }else if(name.endsWith(".xlsl")) {
                workbook = new XSSFWorkbook(in);
            }else if(name.endsWith(".txt")||name.endsWith(".csv")){
                workbook=convertCSVorTXTtoXLS(in,".xls");//把txt或者csv文件类型转换成xls
            }else{
                error="该文件类型系统暂时还没处理";
                return returnError();
            }
            //sheet
            sheet=workbook.getSheetAt(0);
            //rows、columns
            rows=sheet.getPhysicalNumberOfRows();
            row=sheet.getRow(0);
            columns=row.getLastCellNum();
            //schema
            schema=new ArrayList<Map>();
            if(row!=null){
                Iterator<Cell> iterator=row.cellIterator();
                while(iterator.hasNext()){
                    cell=iterator.next();
                    schema.add(getValue(cell));
                }

            }
            //data
            data =new ArrayList<List>();
            for(int i=0;i<rows;i++ ){
                List list=new ArrayList();
                row=sheet.getRow(i);
                if(row==null){
                    continue;
                }
                for(int j=0;j<columns;j++){
                    cell=row.getCell(j);
                    list.add(getValue(cell).get("value"));
                }
                data.add(list);
            }
            //封装json字符串再返回前端
            List<Map> listContainer=new ArrayList<Map>();
            Map<String,Object> mapContainer=new HashMap<String,Object>();
            mapContainer.put("name",name);
            mapContainer.put("row",rows);
            mapContainer.put("column",columns);
            mapContainer.put("data",data);
            mapContainer.put("schema",schema);
            listContainer.add(mapContainer);
            status=1;
            map.put("result",listContainer);
            map.put("error",error);
            map.put("status",status);
            content=mapper.writeValueAsString(map);
            //存储到用户临时文件夹
            storageTotemp(workbook,request,username);
            long endTime=new java.util.Date().getTime();
            System.out.println("sumTime:"+(endTime-startTime));
       }catch (Exception io){
            error="服务器出错!";
            io.printStackTrace();
        }
        return content;
    }

    @ResponseBody
    @RequestMapping(value = "/check",produces ="text/html;charset=UTF-8" )
    public String check(@RequestParam(value="fileName",required = false)String fileName,HttpServletRequest request,HttpServletResponse response){
        if(fileName==null) {
            error = "表单名不能为空！";
            return returnError();
        }
        boolean exist=false;
        Integer userId=userService.getUserIdByname(username);
        List<Integer> folderIdList=folderService.getFolderIdsByUserId(userId);
        for(int i=0;i<folderIdList.size();i++){
            List tableNameList=tableService.getTableNamesByFolderId(folderIdList.get(i));
            for(int j=0;j<tableNameList.size();j++) {
                if (tableNameList.get(j).equals(fileName)) {
                    exist=true;
                    break;
                }
            }
            if(exist==true){
                break;
            }
        }
        Map map=new HashMap();
        map.put("status",status);
        map.put("error",error);
        map.put("exist",exist);
        try{
            content=mapper.writeValueAsString(map);
        }catch (IOException io){
            error="服务器出错！";
            io.printStackTrace();
            return returnError();
        }
        return content;
    }
    @ResponseBody
    @ExceptionHandler(IOException.class)
    @RequestMapping(value = "/create",produces ="text/html;charset=UTF-8" )
    public String create(@RequestParam(value="fileName",required = false)String fileName,
                         @RequestParam(value="folderName",required = false)String folderName,
                         @RequestParam(value="label",required = false)String label,
                         @RequestParam(value="comments",required = false)String comments, HttpServletRequest request,
                         HttpServletResponse response){
//        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        //初始化
        init();
//        System.out.println("fileName:"+fileName+"\r\n"+"label:"+label+"\r\n"+"folderName:"+folderName+"\r\n"+"comments:"+comments);
       //空字符串检测
        if(fileName==null||folderName==null||label==null||comments==null){
            error="你提交的表单不能为空！";
            return returnError();
        }
//        System.out.println(name);
        //先使用用户名查出用户id,再根据id查出文件夹id，再根据每个文件夹id查出所有文件名。
        Integer userId=userService.getUserIdByname(username);
        System.out.println("userid:"+userId);
        List<Integer> folderIdList=folderService.getFolderIdsByUserId(userId);
        System.out.println("folderIdList"+folderIdList.size());
        for(int i=0;i<folderIdList.size();i++){
            List tableNameList=tableService.getTableNamesByFolderId(folderIdList.get(i));
            System.out.println("tableNameList"+folderIdList.size());
            for(int j=0;j<tableNameList.size();j++) {
                if (tableNameList.get(j).equals(fileName)) {
                    error = "你提交的表单已经存在";
                    System.out.println("error"+error);
                    return returnError();
                }
            }
        }
        //文件类型
        tableType=getTableType(name);
        if(tableType.equals("unknow")){
            error="未知表格类型错误！";
            return returnError();
        }
        if(columns==0||rows==0){
            String path=request.getSession().getServletContext().getRealPath("\\")+"\\"+REPOSITORY+"\\"+username+"\\"+TEMP;
            try{
                FileInputStream in=new FileInputStream(new File(path,name));
                if(tableType.equals("txt")||tableType.equals("csv"))
                    workbook=convertCSVorTXTtoXLS(in,".xlsx");
                else if(tableType.equals("xls"))
                    workbook=new HSSFWorkbook(in);
                else
                    workbook=new XSSFWorkbook(in);
                sheet=workbook.getSheetAt(0);
                rows=sheet.getPhysicalNumberOfRows();
                columns=sheet.getRow(0).getPhysicalNumberOfCells();
                if(workbook!=null){
                    workbook.close();
                }
            }catch (IOException io){
                error="创建表单失败！";
                io.printStackTrace();
            }
        }
        try{
            //在创建表单时，也同时查看仓库中是否已经存在了将要创建的仓库名，如果存在则仓库名不添加。
            if(folderService.getFolder(folderName,userId)==null){
                folder.setFolderName(folderName);//插入仓库名
                folder.setType("1");//默认的仓库类型
                folder.setCtime(new Date(new java.util.Date().getTime() ));//插入类型
                folder.setUserId(userId);//仓库类型的用户名
                folder.setStatus("1");
                folderService.insertFolder(folder);//插入文件夹信息到数据库
            }
            int folderId=folderService.getFolder(folderName,userId).getFolderId();
            System.out.println("folderId"+folderId);
            System.out.println("columns:"+columns+"\r\n"+"label:"+label);
            //把文件信息插入到数据库中
            table.setColumns(columns);
            table.setRows(rows);
            table.setOriginalName(name);
            table.setFolderId(folderId);
            table.setComments(comments);
            table.setLabel(label);
            table.setCtime(new Date(new java.util.Date().getTime()));
            table.setTableName(fileName);
            table.setType(tableType);
            table.setStatus("1");
            tableService.insert(table);
            //返回json字符串
            Map map=new HashMap();
            map.put("status",status);
            map.put("error",error);
            map.put("result","");
            content=mapper.writeValueAsString(map);
            //把文件移动到用户永久文件夹中
            tempTOuserFolder(request,folderName,fileName,name,tableType);
        }catch (IOException io){
            io.printStackTrace();
        }
        return content;
    }
    //file move、delete、rename
    @ResponseBody
    @ExceptionHandler(IOException.class)
    @RequestMapping(value = "/move",produces ="text/html;charset=UTF-8" )
    public String moveFile(@RequestParam(value = "sourceFolderName",required = false)String sourceFolder,
                           @RequestParam(value = "folderName",required = false)String objFolder,
                           @RequestParam(value = "fileName",required = false)String fileName,
                           HttpServletRequest request,HttpServletResponse response){
        String status = "0";
        String error = "";
        boolean result = false;
        String content = "";
        Map map = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        try{
            if (sourceFolder == null || sourceFolder.equals("")||objFolder==null||objFolder.equals("")||fileName==null|| fileName.equals("")) {
                error = "文件夹或文件名不能为空!";
                throw new Exception(error);
            }
            if (sourceFolder.matches("[*/\\:\"?<>|]") == true||objFolder.matches("[*/\\:\"?<>|]") == true||fileName.matches("[*/\\:\"?<>|]") == true) {
                error = "文件夹或者文件名不能含有[*/\\:\"?<>|]特殊字符";
                throw new Exception(error);
            }
            //文件类型
            String fileType=fileType(username,sourceFolder,fileName);
            //移动文件
            boolean isSuccess=sourceTableMoveObjTable(request, username,sourceFolder,objFolder,fileName,fileType);
            int delSum=0;
            if(isSuccess==true) {
                //更新数据库中的记录  目标路径添加移动表记录---源路径删除移动表记录
                //获取用户Id
                int userId = userService.getUserIdByname(username);
                //先根据用户Id和源文件夹名获取源文件夹Id
                Integer sourceFolderId = folderService.getFolderIdByName(sourceFolder, userId);
                //目标文件夹Id
                Integer objFolderId = folderService.getFolderIdByName(objFolder, userId);
                //获取该表的信息
                table = tableService.getTableByName(fileName, sourceFolderId);
                //其他信息不变，就是需要更改其所在的文件夹Id
                table.setFolderId(objFolderId);
                System.out.println("tableName:"+table.getTableName());
                //根据文件夹Id和文件名删除文件
                delSum = tableService.deleteTable(fileName, sourceFolderId);
                //添加表的信息到目标文件夹中
                tableService.insert(table);
            }
            if (delSum != 0) {
                result = true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
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
            content = "{\"status\":" + status + ",\"error\":" + error + ",\"result\":" + result + "}";
        }
        return content;
    }
    @ResponseBody
    @ExceptionHandler(IOException.class)
    @RequestMapping(value = "/delete",produces ="text/html;charset=UTF-8" )
    public String deleteFile(@RequestParam(value = "fileName",required = false)String fileName,
                             @RequestParam(value = "folderName",required = false)String folderName,
                           HttpServletRequest request,HttpServletResponse response){
        String status = "0";
        String error = "";
        boolean result = false;
        String content = "";
        Map map = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (fileName == null || fileName.equals("")&&(folderName==null||folderName.equals(""))) {
                error = "文件夹名不能为空!";
                throw new Exception(error);
            }
            if (fileName.matches("[*/\\:\"?<>|]") == true&&folderName.matches("[*/\\:\"?<>|]") == true) {
                error = "文件夹名不能含有[*/\\:\"?<>|]特殊字符";
                throw new Exception(error);
            }
            String fileType=fileType(username,folderName,fileName);
            //先删除物理中的文件，再去更新数据库中的数据。
            boolean isSuccess=delete(request,folderName,fileName,fileType);
            int sum=0;
            if(isSuccess==true) {
                int userId = userService.getUserIdByname(username);
                //先根据用户Id和源文件夹名获取源文件夹Id
                Integer folderId = folderService.getFolderIdByName(folderName, userId);
                //获取文件的母表,然后从该表的child字段删除文件名.
                String parentTableName=tableService.getTableByName(fileName,folderId).getParent();
                System.out.println("parentTableName:"+parentTableName);
                String [] parentList;
                if(parentTableName.contains("/")){
                    parentList=parentTableName.split("/");
                }else{
                    parentList=new String[1];
                    parentList[0]=parentTableName;
                }
                //遍历该用户下的所有文件夹，找到与parentTableName名相同的表，然后进行更新child字段.
                List<Integer> folderIds=folderService.getFolderIdsByUserId(userId);
                for(int i=0;i<folderIds.size();i++){//遍历所有的文件夹
                    List<String> tableNames=tableService.getTableNamesByFolderId(folderIds.get(i));
                    for(int j=0;j<tableNames.size();j++){//遍历每个文件夹下的所有文件
                        System.out.println("tableName:"+tableNames.get(j));
                       for(String parent:parentList){//遍历所有的母表
                           System.out.println("parent:"+parent);
                           if(parent.equals(tableNames.get(j))){//确定母表存在哪个文件夹下
                               //获取该表的child字段的信息
                               String child=tableService.getTableByName(tableNames.get(j),folderIds.get(i)).getChild();
                               System.out.println("child:"+child);
                               //获取该表的id
                               int fileId=tableService.getTableIdByName(tableNames.get(j),folderIds.get(i));
                               if(child.contains(fileName)){//再次确定该表是否是母表的子表
                                   //如果存在就把该表从父母表的child字段删除
                                   String newChild="";
                                   if(child.contains("/")){
                                       String [] children=child.split("/");
                                       int flag=0;
                                       for(String child_:children){
                                           if(child_.equals(fileName)){
                                               continue;
                                           }
                                           if(flag==0){
                                               newChild=child_;
                                           }else{
                                               newChild=newChild+"/"+child_;
                                           }
                                           flag++;
                                       }
                                   }
                                   System.out.println("newChild:"+newChild);
                                   //更新母表的child字段的信息
                                   tableService.updateFileChild(newChild,fileId);
                               }
                           }
                       }
                    }
                }
                //根据文件夹Id和文件名删除文件
                sum = tableService.deleteTable(fileName, folderId);
            }
            if(sum!=0){
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
            status="1";
            error = "map对象转换成json字符串失败！";
            content = "{\"status\":" + status + ",\"error\":" + error + ",\"result\":" + result + "}";
        }
        return content;
    }

    @ResponseBody
    @ExceptionHandler(IOException.class)
    @RequestMapping(value = "/update",produces ="text/html;charset=UTF-8" )
    public String UpdateFile(@RequestParam(value = "original_name",required = false)String sourceTable,
                             @RequestParam(value="folderName",required = false)String folderName,
                             @RequestParam(value = "objFileName",required = false)String fileName,
                           HttpServletRequest request,HttpServletResponse response){
        String status = "0";
        String error = "";
        boolean result = false;
        String content = "";
        Map map = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (sourceTable == null || sourceTable.equals("")|| (fileName == null || fileName.equals("") ||
                    folderName == null || folderName.equals(""))) {
                error = "文件夹或文件名不能为空!";
                throw new Exception(error);
            }
            if (sourceTable.matches("[*/\\:\"?<>|]") == true || fileName.matches("[*/\\:\"?<>|]") == true || folderName.matches("[*/\\:\"?<>|]") == true) {
                error = "文件夹或者文件名不能含有[*/\\:\"?<>|]特殊字符";
                throw new Exception(error);
            }
            int userId = userService.getUserIdByname(username);
            //先根据用户Id和源文件夹名获取源文件夹Id
            Integer folderId = folderService.getFolderIdByName(folderName, userId);
            if (folderId == 0) {
                error = "你输入的源文件夹名系统找不到";
                throw new Exception(error);
            }
            //修改物理文件名
            String fileType=fileType(username,folderName,sourceTable);
            String folderPath = request.getSession().getServletContext().getRealPath("/") + "\\" + REPOSITORY +
                    "\\" + username + "\\" + PERSISTENT + "\\" + folderName;
            File oldFile = new File(folderPath,sourceTable+"."+fileType);
            File newFile = new File(folderPath,fileName+"."+fileType);
            System.out.println(folderPath+oldFile.getName());
            Files.copy(oldFile.toPath(),newFile.toPath());
            oldFile.delete();
            //根据文件夹Id和文件名获取文件id
            Integer fileId = tableService.getTableIdByName(sourceTable, folderId);
            if (fileId <=0) {
                error = "你输入的文件名系统找不到";
                throw new Exception(error);
            }
            //根据文件Id更新数据库文件名
            int sum = tableService.updateFile(fileName, fileId);

            if(sum!=0){
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
    private String getTableType(String fileName){
        if(fileName.endsWith(".xls")){
            tableType="xls";
        }
        else if(fileName.endsWith(".xlsx")){
            tableType="xlsx";
        }
        else if(fileName.endsWith(".txt")){
            tableType="xls";
        }
        else if(fileName.endsWith(".csv")){
            tableType="xls";
        }else{
            tableType="unknow";
        }
        return tableType;
    }
    /**
     *
     * @param cell
     * @return Map key=value,isEmpty,type.
     */
    public static Map getValue(Cell cell){
        Map<String,Object> map1=new HashMap<String,Object>();
        if(cell==null){
//            System.out.println("celltype");
            map1.put("type",3);
            map1.put("value","");
            map1.put("isEmpty",isEmpty);
            return map1;
        }
        int cellType=cell.getCellType();
        switch (cellType){
            case 0:
                if(HSSFDateUtil.isCellDateFormatted(cell)){
                    value=String.valueOf(cell.getDateCellValue());
                    type=0;
                }else{
                    value=String.valueOf(cell.getNumericCellValue());
                    type=7;
                }
                break;
            case 1:
                value=String.valueOf(cell.getStringCellValue());
                type=1;
                break;
            case 2:
                value=String.valueOf(cell.getCellFormula());
                type=2;
                break;
            case 3:
                value="";
                type=3;
                break;
            case 4:
                value=String.valueOf(cell.getBooleanCellValue());
                type=4;
                break;
            case 5:
                value=String.valueOf(cell.getErrorCellValue());
                type=5;
                break;
            default:
                type=6;
                break;
        }
        map1.put("type",type);
        map1.put("value",value);
        map1.put("isEmpty",isEmpty);
        return map1;
    }

    /**
     *
     * @return Errorjson
     */
    private String returnError(){
        map.put("result","");
        map.put("error",error);
        map.put("status",status);
        try {
            content=mapper.writeValueAsString(map);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return content;
    }

    /**
     *
     * @param in
     * @param fileType
     * @return worbook
     */
    public static  Workbook convertCSVorTXTtoXLS(InputStream in, String fileType)throws IOException{
        //目标文件
//        File file=(File)multipartFile;
        BufferedReader buffer=null;
        String strIns="";
        Workbook workbook=null;
        Row row=null;
        Cell cell=null;
        Sheet sheet=null;
//        FileOutputStream out=null;
        try{
            buffer=new BufferedReader(new InputStreamReader(in));
            if(fileType.equals(".xlsx")){
                 workbook=new XSSFWorkbook();
            }else if(fileType.equals(".xls")){
                 workbook=new HSSFWorkbook();
            }else{
                return null;
            }
            int startRow=0;
            sheet=workbook.createSheet("0");
            while((strIns=buffer.readLine())!=null){
                row=sheet.createRow(startRow);
                if(strIns=="")
                    continue;
                String str[]=strIns.split(",");
                for(int i=0;i<str.length;i++){
                    cell=row.createCell(i);
                    if(str[i]==null||str[i].equals("")){
//                        System.out.println("cell:null");
                        cell.setCellValue("");
                    }else {
                        cell.setCellValue(str[i]);
                    }
                }
                startRow++;
            }
        }catch (IOException io){
            error="服务器出错！";
            io.printStackTrace();
            throw new IOException("转换文件失败！");
        }
        return workbook;
    }

    /**
     * <p>create temp file from workbook.
     * @param workbook
     * @param req
     * @param username
     */
    private void storageTotemp(Workbook workbook,HttpServletRequest req,String username){
        try{
            if(workbook==null||req==null||username==null||username.equals(""))
                return;
            String path=req.getSession().getServletContext().getRealPath("\\")+"\\"+REPOSITORY+"\\"+username+"\\"+TEMP;
            File filePath=new File(path);
            File targetFile=new File(filePath,name);
            if(!filePath.exists()){
                filePath.mkdirs();
            }
            if(targetFile.exists()){
                return;
            }
            FileOutputStream out=new FileOutputStream(targetFile);
            workbook.write(out);
            out.close();
            workbook.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * <p>will do that the file of temp will copy to persistent of user,and delete
     *   the file in temp</p>
     * @param request
     * @param folderName
     * @param fileName
     * @param original_name
     * @param fileType
     */
    private void tempTOuserFolder (HttpServletRequest request,String folderName,String fileName,String original_name,String fileType)throws IOException{
        try {
            String tempPath=request.getSession().getServletContext().getRealPath("/")+"\\"+REPOSITORY+"\\"+username+"\\"+TEMP;
            String persistentPath=request.getSession().getServletContext().getRealPath("/")+"\\"+REPOSITORY+"\\"+username+"\\"+PERSISTENT+"\\"+folderName;
            File newFile=new File(persistentPath);
            if(!newFile.exists()){
                newFile.mkdirs();//创建永久文件路径
            }
            if(original_name.endsWith(".csv")){
                original_name.replace("csv","xls");
                System.out.println(original_name);
            }
            if(original_name.endsWith(".txt")){
                original_name.replace("txt","xls");
                System.out.println(original_name);
            }
            //临时文件
            File oldFile=new File(tempPath,original_name);
            if(oldFile==null){
                return;
            }
            //永久文件
            StringBuffer strBuffer=new StringBuffer(fileName);
            strBuffer.append(".").append(fileType);
            System.out.println("strbuffer:"+strBuffer);
            File file=new File(newFile,new String(strBuffer));
            Files.copy(oldFile.toPath(), file.toPath());
            oldFile.delete();
        }catch (IOException io){
            io.printStackTrace();
            throw new IOException("文件移动失败！");

        }
    }

    /**
     * <p>Move the file of sourceFolder to objFolder</p>
     * @param request
     * @param fileName
     * @param sourceFolder
     * @param ojbFolder
     * @param username
     * @param fileType
     * @return
     * @throws Exception
     */
    private boolean sourceTableMoveObjTable(HttpServletRequest request,String username,String  sourceFolder,String ojbFolder,String fileName,String fileType) throws Exception{
        boolean result=false;
        try {
            String sourcePath=request.getSession().getServletContext().getRealPath("/")+REPOSITORY+"\\"+username+"\\"+PERSISTENT+"\\"+sourceFolder;
            String objPath=request.getSession().getServletContext().getRealPath("/")+REPOSITORY+"\\"+username+"\\"+PERSISTENT+"\\"+ojbFolder;
            System.out.println(sourcePath+"\\"+fileName+"."+fileType);
            System.out.println(objPath+"\\"+fileName+"."+fileType);
            File newFile=new File(objPath,fileName+"."+fileType);
            File newFilePath=new File(objPath);
            File oldFile=new File(sourcePath,fileName+"."+fileType);
            File oldFilePath=new File(sourcePath);
            if(!newFilePath.exists()){
                throw new Exception("目标路径不存在！");
            }
            if(!oldFilePath.exists()){
                throw new Exception("源路径不存在！");
            }
            if(newFile.exists()){
                throw new Exception("文件已经在目标路径中存在了！");
            }
            if(!oldFile.exists()){
                throw new Exception("源表不存在！");
            }
            //文件从源路径移动到目标路径
            Files.copy(oldFile.toPath(),newFile.toPath());
            oldFile.delete();
            result=true;
        }catch (IOException io){
            io.printStackTrace();
            throw new IOException("文件移动失败！");
        }
        return result;
    }

    /**
     * <p>get the type of file</p>
     * @param username
     * @param folderName
     * @param fileName
     * @return
     */
    private String fileType(String username,String folderName,String fileName){
        //从数据库中获取用户ID
        int userId=userService.getUserIdByname(username);
        //根据用户ID和文件夹名获取文件夹ID
        int folderId=folderService.getFolderIdByName(folderName,userId);
        //根据文件夹id和文件夹名获取文件实体,再取出type
        String fileType= tableService.getTableByName(fileName,folderId).getType();
        return fileType;
    }
    private boolean delete(HttpServletRequest request,String folderName, String fileName,String type)throws  Exception{
        boolean result=false;
        try{
            //定位要删除的文件路径
            String filePath=request.getSession().getServletContext().getRealPath("/")+REPOSITORY+
                    "\\"+username+"\\"+PERSISTENT+"\\"+folderName;
            System.out.println(filePath+fileName+"."+type);
            File file=new File(filePath,fileName+"."+type);
            if(!file.exists()){
                throw new Exception("文件不存在!");
            }
            file.delete();
            result=true;
        }catch(Exception ex){
            ex.printStackTrace();
            throw  new Exception(ex);
        }
        return result;
    }
}






