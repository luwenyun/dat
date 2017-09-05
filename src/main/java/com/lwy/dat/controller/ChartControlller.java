package com.lwy.dat.controller;/**
 * Created by lwy on 2017/5/23.
 */

import com.lwy.dat.pojo.Chart;
import com.lwy.dat.service.ChartService;
import com.lwy.dat.service.FolderService;
import com.lwy.dat.service.TableService;
import com.lwy.dat.service.UserService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

/**
 * add、del the charts
 *
 * @author 陆文云
 * @create 2017-05-23 9:09
 **/
@Controller
@RequestMapping("/api/chart")
public class ChartControlller {
    public static String username;
    private final String REPOSITORY="repository";
    private final String PERSISTENT="persistent";
    @Resource
    UserService userService;
    @Resource
    FolderService folderService;
    @Resource
    TableService tableService;
    @Resource
    ChartService chartService;
    @Resource
    Chart chart;
    @ResponseBody
    @RequestMapping(value="/add",produces = "text/html;charset=UTF-8")
    public String add(@RequestParam("folderName")String folderName,
                      @RequestParam("fileName")String fileName,
                      @RequestParam("dimensions")String dimensions,
                      @RequestParam("values")String values,
                      @RequestParam("comments")String comments,
                      @RequestParam("chartName")String chartName,
                      HttpServletRequest request, HttpServletResponse response)throws IOException{

        String error="";
        String status="0";
        String content="";
        boolean result=false;
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
            String username=(String)request.getSession().getAttribute("username");
            if(username==null){
                error="强制退出，没有登录";
                throw new Exception(error);
            }
            //空字符串检测
            if(folderName==null||folderName.equals("")){
                error="文件夹名不能为空";
                throw new Exception(error);
            }
            if(fileName==null||fileName.equals("")){
                error="文件名不能为空";
                throw new Exception(error);
            }
            if(chartName==null||chartName.equals("")){
                error="图表名不能为空";
                throw new Exception(error);
            }
            if(dimensions==null||dimensions.equals("")){
                error="维度不能为空";
                throw new Exception(error);
            }
            if(values.equals("")||values==null){
                error="数值不能为空";
                throw new Exception(error);
            }
            //查询图表名是否已经存在
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            int tableId=tableService.getTableIdByName(fileName,folderId);
            //查询要添加的图表名是否已经存在。
            List<Chart> charts=chartService.getChartsById(tableId);
            for(Chart chart:charts){
                if(chartName.equals(chart.getChartName())){
                    error="你注册的图表已经存在";
                    throw new Exception(error);
                }
            }
//            String[] dimensionList=null;
//            String[] valueList=null;
//            if(dimensions.contains("/")){
//                dimensionList=dimensions.split("/");
//            }else{
//                dimensionList=new String[1];
//                dimensionList[0]=dimensions;
//            }
//            if(values.contains("/")){
//                valueList=dimensions.split("/");
//            }else{
//                valueList=new String[1];
//                valueList[0]=values;
//            }
            //插入图表
            chart.setCtime(new Date(new java.util.Date().getTime()));
            chart.setChartName(chartName);
            chart.setComments(comments);
            chart.setTableId(tableId);
            chart.setType("1");
            chart.setxAxis(dimensions);//维度
            chart.setyAxis(values);//数值
            chartService.insertChart(chart);
            result=true;
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            map.put("status",status);
            map.put("error",error);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/del",produces = "text/html;charset=UTF-8")
    public String del(@RequestParam("fileName")String fileName,
                      @RequestParam("folderName")String folderName,
                      @RequestParam("chartName")String chartName,
                      HttpServletRequest request,HttpServletResponse response)throws IOException{
        String error="";
        String status="0";
        String content="";
        boolean result=false;
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
            String username=(String)request.getSession().getAttribute("username");
            if(username==null){
                error="请重新登录";
                throw new Exception(error);
            }
            //空字符串检测
            if(folderName==null||folderName.equals("")){
                error="文件夹名不能为空";
                throw new Exception(error);
            }
            if(fileName==null||fileName.equals("")){
                error="文件名不能为空";
                throw new Exception(error);
            }
            if(chartName==null||chartName.equals("")){
                error="图表名不能为空";
                throw new Exception(error);
            }
            //查询图表名是否存在
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            int tableId=tableService.getTableIdByName(fileName,folderId);
            int chartId=chartService.getChartIdByName(chartName,tableId);
            if(chartId<=0){
                error="图表名不存在，无法执行删除操作！";
            }else{
                int flag=chartService.deleteChartById(chartId);
                if(flag>0){
                    result=true;
                }else{
                    error="删除失败!";
                    throw new Exception(error);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            map.put("status",status);
            map.put("error",error);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/list",produces = "text/html;charset=UTF-8")
    public String list(@RequestParam("fileName")String fileName,
                       @RequestParam("folderName")String folderName,
                       HttpServletRequest request,HttpServletResponse response)throws IOException{
        String error="";
        String status="0";
        String content="";
        List result=new ArrayList();
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
//            String username=(String)request.getSession().getAttribute("username");
//            if(username==null){
//                error="请登录";
//                throw new Exception(error);
//            }
            //空字符串检测
            if(folderName==null||folderName.equals("")){
                error="文件夹名不能为空";
                throw new Exception(error);
            }
            if(fileName==null||fileName.equals("")){
                error="文件名不能为空";
                throw new Exception(error);
            }
            //列出所有图表
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            int tableId=tableService.getTableIdByName(fileName,folderId);
            List<Chart> charts=chartService.getChartsById(tableId);
            if(charts==null){
                error="该文件还没有生成图表";
                throw new Exception(error);
            }
//            for(Chart chart:charts){
//                Map chartMap=new HashMap();
//                chartMap.put("charname",chart.getChartName());
//                String dimensions=chart.getxAxis();
//                chartMap.put("xAxis",getFieldValues(folderName,fileName,dimensions));
//                String values=chart.getyAxis();
//                chartMap.put("yAxis",getFieldValues(folderName,fileName,values));
//                result.add(chartMap);
//            }
        }catch (Exception ex){
            ex.printStackTrace();

        }finally {
            map.put("status",status);
            map.put("error",error);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/getFieldValues",produces = "text/html;charset=UTF-8")
    public String getFieldValues(@RequestParam("folderName") String folderName,
                                 @RequestParam("fileName") String fileName,
                                 @RequestParam("fields") String fields,
                                 HttpServletRequest request,HttpServletResponse response)throws IOException{
        String error="";
        String status="0";
        String content;
        Map result=new HashMap();
        List resultList=new ArrayList();
        Map map=new HashMap();
        ObjectMapper mapper=new ObjectMapper();
        try{
            if(folderName==null||folderName.equals("")){
                error="文件夹名不能为空";
                throw new Exception(error);
            }
            if(fileName==null||folderName.equals("")){
                error="文件名不能为空";
                throw new Exception(error);
            }
            if(fields==null||fields.equals("")){
                error="维度字段不能为空";
                throw new Exception(error);
            }

            //查询数据库中是否有该文件记录
            int userId=userService.getUserIdByname(username);
            int folderId=folderService.getFolderIdByName(folderName,userId);
            int tableId=tableService.getTableIdByName(fileName,folderId);
            if(tableId<=0){
                error="文件名不存在";
                throw new Exception(error);
            }
            //读取文件对应的字段
            String path = request.getSession().getServletContext().getRealPath("/") +
                    REPOSITORY + "\\" + username + "\\" + PERSISTENT + "\\" + folderName + "\\" + fileName;
            String fileType=fileType(username,folderName,fileName);
            System.out.println("path:"+path);
            File file=new File(path+"."+fileType);
            Workbook workbook=null;
            if(fileType.equals("xlsx")){
                workbook=new XSSFWorkbook(new FileInputStream(file));
            }else{
                workbook=new HSSFWorkbook(new FileInputStream(file));
            }
            Sheet sheet=workbook.getSheetAt(0);
            Row row=sheet.getRow(0);
            Cell cell;
            int rows=sheet.getPhysicalNumberOfRows();
            System.out.println("rows:"+rows);
            //把字段对应的列对应起来
            List fieldNOList=new ArrayList();
            Map map_=new HashMap();
            int columns=row.getLastCellNum();
            for(int i=0;i<columns;i++){
                cell=row.getCell(i);
                String cellValue=(String) UploadFileController.getValue(cell).get("value");
                if(fields.contains(cellValue)){
                    System.out.println("textFile:"+cellValue);
                    map_.put(cellValue,i);
                    fieldNOList.add(i);
                }
            }
            //取出表中所有对应字段的值[{"ff",sss,ss,},{[ss,sdssd,dsd]}]
            for(int i=1;i<rows;i++){
                Map  fieldValueMap=new HashMap();
                row=sheet.getRow(i);
                for(int j=0;j<columns;j++){
                    for(int num=0;num<fieldNOList.size();num++){
                        if(j==(int)fieldNOList.get(num)){
                            cell=row.getCell(j);
                            if(cell==null){
                                continue;
                            }
                            String cellValue=(String)UploadFileController.getValue(cell).get("value");
                            fieldValueMap.put(j,cellValue);
                        }
                    }
                }
                resultList.add(fieldValueMap);
            }
            result.put("fieldInfo",map_);
            result.put("fieldValue",resultList);
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

}
