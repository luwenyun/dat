package com.lwy.dat.controller;/**
 * Created by lwy on 2017/5/23.
 */

import com.lwy.dat.pojo.Code;
import com.lwy.dat.pojo.User;
import com.lwy.dat.pojo.UserInfo;
import com.lwy.dat.service.UserService;
import com.lwy.dat.util.EmailMessage;
import com.lwy.dat.util.SMSInformation;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * login、reg、update
 *
 * @author 陆文云
 * @create 2017-05-23 8:43
 **/
@Controller
@RequestMapping("/api/user")
public class UserController {
    private final int WIDTH=100;
    private final int HEIGHT=30;
    private final int FONT_SIZE=30;
    private final int FirX=10;
    private final int SECX=35;
    private final int THIX=55;
    private final int FOUX=75;
    private final int FirY=23;
    private final int SECY=22;
    private final int THIY=23;
    private final int FOUY=21;
    private int i=1;
    private boolean flag=false;
    public static String username;
    private Map userMap;
    @Resource
    private Code mycode;
    @Resource
    private User user;
    @Resource
    private UserInfo userInfo;
    @Resource
    private UserService userService;
    @ResponseBody
    @RequestMapping(value="/userInfo",produces = "text/html;charset=UTF-8")
    public String getUserInfo()throws  IOException{
        //初始化返回的json字符串
        String error="";
        String status="0";
        String content="";
        Map result=new HashMap();
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        //获取用户信息
        int userId=userService.getUserIdByname(username);
        user=userService.getUserById(userId);
        userInfo=userService.getUserInfoById(userId);
        result.put("username",user.getUserName());
        result.put("tel",userInfo.getTel());
        result.put("email",userInfo.getEmail());
        result.put("sex",userInfo.getSex());
        result.put("work",userInfo.getWork());
        result.put("company",userInfo.getCompany());
        result.put("nickname", userInfo.getNickName());
        map.put("error",error);
        map.put("status","1");
        map.put("result",result);
        content=mapper.writeValueAsString(map);
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "/login",produces = "text/html;charset=UTF-8")
    public String login(@RequestParam(value = "code",required = false)String code,
                        @RequestParam("username")String username_,
                        @RequestParam("password")String password,
                        HttpServletRequest request, HttpServletResponse response)throws IOException{
        //登录失败次数
        Integer loginTimes=(Integer) request.getSession().getAttribute("loginTimes");
        if(loginTimes==null){
            request.getSession().setAttribute("loginTimes",i);
            loginTimes=i;
        }
        //初始化返回的json字符串
        String error="";
        String status="0";
        String content="";
        Map result=new HashMap();
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try {
            //验证码合法检测
            if (loginTimes > 3) {
                System.out.println("need code:"+code);
                if (code == null || code.equals("")) {
                    error = "验证码为空!";
                    if (code.length() != 4) {
                        error = "验证码为4位";
                    }
                    throw new Exception(error);
                }
            }
            if (username_ == null || username_.equals("")) {//用户名合法检测
                error = "用户名不能为空";
                if (6 > username_.length() || username_.length() > 16) {
                    error = "用户名只能输入6-16位";
                }
                i++;
                request.getSession().setAttribute("loginTimes",i);
                throw new Exception(error);
            }
            if (password == null || password.equals("")) {//密码合法检测
                error = "密码不能为空";
                if (6 > password.length() || password.length() > 16) {
                    error = "密码只能在输入6-16位";
                }
                i++;
                request.getSession().setAttribute("loginTimes",i);
                throw new Exception(error);
            }
        //判断验证码输入是否正确
        if (loginTimes > 3) {
            System.out.println("code_:"+request.getSession().getAttribute("code"));
            if (!code.equals(request.getSession().getAttribute("code"))) {
                error = "验证码不正确";
                i++;
                request.getSession().setAttribute("loginTimes",i);
                throw new Exception(error);
            }
        }
        System.out.println("username:"+username_);
        //判断用户名输入是否正确
        Integer userId =userService.getUserIdByname(username_);
        System.out.println("userID:"+userId);
        if (userId<=0) {
            error = "用户名不正确!";
            i++;
            request.getSession().setAttribute("loginTimes",i);
            throw new Exception(error);
        }
        //判断密码输入是否正确
        String pwd=userService.getUserById(userId).getPwd();
        if (!password.equals(pwd)) {//
            error = "密码不正确！";
            i++;
            request.getSession().setAttribute("loginTimes",i);
            throw new Exception(error);
        }
        System.out.println("login success");
        user.setUserName(username_);
        request.getSession().setAttribute("user", user);
        result.put("username",username_);
        status="1";

        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            System.out.println("loginTimes:"+loginTimes.intValue());
            map.put("loginTimes",loginTimes);
            map.put("status",status);
            map.put("error",error);
            map.put("result",result);
            content=mapper.writeValueAsString(map);
        }
        return content;

    }
    @ResponseBody
    @RequestMapping(value = "/exit",produces = "text/html;charset=UTF-8")
    public String exit(HttpServletRequest request) throws IOException{
        //初始化返回的json字符串
        String error="";
        String status="0";
        String content="";
        boolean result=false;
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        request.getSession().removeAttribute("user");
        map.put("status","1");
        map.put("error",error);
        map.put("result","true");
        content=mapper.writeValueAsString(map);
        return content;

    }
    @ResponseBody
    @RequestMapping(value = "/reg",produces = "text/html;charset=UTF-8")
    public String reg(@RequestParam(value = "code",required = false)String code,
                      @RequestParam(value = "username",required = false)String username_,
                      @RequestParam(value = "pwd",required = false)String password,
                      @RequestParam(value="sureCode",required = false)String sureCode,
                      @RequestParam(value = "type")String type,
                      @RequestParam(value="telOrEmail",required = false)String telOrEmail,
                      HttpServletRequest request, HttpServletResponse response)throws  Exception{
        String error="";
        String status="0";
        String content="";
//        boolean flag=false;
        String informationType="";
        boolean result=false;
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try{
            System.out.println("type:"+type);
            if(type!=null&&type.equals("1")){
                //验证用户是否已经存在
                int userId=userService.getUserIdByname(username_);
                if(userId==0){
                    //不存在
                    status="1";
                    result=true;
                }else{
                    //存在
                    status="1";
                }
            }else if(type.equals("2")){
                //验证输入的是否符合要求
                //手机号码/邮箱
                if(telOrEmail==null||telOrEmail.equals("")){
                    error="手机号码/邮箱不能为空";
                    throw new Exception(error);
                }else{
                    if(telOrEmail.contains("@")) {
                        informationType = "email";
                        if (telOrEmail.length() < 3) {
                            error = "邮箱格式不对";
                            throw new Exception(error);
                        }
                    }else{
                        informationType="tel";
                        if(telOrEmail.length()!=11){
                            error="手机格式不对";
                            throw new Exception(error);
                        }
                    }
                }
                //用户名
                if (username_ == null || username_.equals("")) {
                    error = "用户名不能为空";
                    if (6 > username_.length() || username_.length() > 16) {
                        error = "用户名只能输入6-16位";
                    }
                    throw new Exception(error);
                }
                //密码
                if (password == null || password.equals("")) {
                    error = "密码不能为空";
                    if (6 > password.length() || password.length() > 16) {
                        error = "密码只能在输入6-16位";
                    }
                    throw new Exception(error);
                }
                //验证码
                if(code==null||code.equals("")){
                    error="验证码不能为空";
                    throw new Exception(error);
                }else{
                    if(code.length()!=4){
                        error="验证码只能是4位";
                        throw new Exception(error);
                    }else{
                        if(code.matches("^[0-9a-zA-Z]{4}$")==false){
                            error="验证码是有数字英文组成";
                            throw new Exception(error);
                        }
                        System.out.println("code:"+request.getSession().getAttribute("code"));
                        if(!code.equals(request.getSession().getAttribute("code"))){
                            error="验证码不正确";
                            throw new Exception(error);
                        }
                    }

                }
                //设置过期时间
                long date=new Date().getTime();
                mycode.setCode(new String(getRandCode()));
                mycode.setExpires(date+60*1000);
                request.getSession().setAttribute("sureCode",mycode);
                //发送验证
                if(informationType.equals("tel")){
                    //手机验证
                    SMSInformation sms=new SMSInformation(mycode.getCode(),telOrEmail);
                    sms.sendSMSInformation();
                }else{
                    //邮箱验证
                    EmailMessage email=new EmailMessage();
                    String host="smtp.qq.com";
                    String port="smtp";
                    String auth="true";
                    String name="1623905995";
                    String fromPerson="1623905995@qq.com";
                    String toperson=telOrEmail;
                    String title="邮箱验证";
                    String conetent="DAT:"+mycode.getCode();
                    String contentType="text/html; charset=utf-8";
                    email.setEmailInformation(host,port,auth,name,fromPerson,toperson,title,conetent,contentType);
                    email.setEmailServices();
                    email.getSession();
                    email.connectEmailServer();
                    email.getMessage();
                    email.sendEmailMessage();
                }
                result=true;
                flag=true;
                status="1";
                userMap=new HashMap();
                userMap.put("username",username_);
                userMap.put("password",password);
                userMap.put("telOrEmail",telOrEmail);

            }else if(type.equals("3")){
                if(flag==false){
                    error="你之前输入的值有错";
                    throw new Exception(error);
                }else{
                    //验证确认码
                    //手机
                    long date=new Date().getTime();
                    System.out.println("date:"+date);
                    mycode=(Code)request.getSession().getAttribute("sureCode");
                    System.out.println("Expires:"+mycode.getExpires());
                    if(date>mycode.getExpires()){
                        error="确认码已经过期";
                        throw new Exception(error);
                    }
                    if(!sureCode.equals(mycode.getCode())){
                        error="确认码不正确";
                        throw new Exception(error);
                    }
                    //把注册信息记录到数据库
                    user.setPwd((String) userMap.get("password"));
                    user.setUserName((String) userMap.get("username"));
                    userService.insertUser(user);
                    int userId=userService.getUserIdByname((String)userMap.get("username"));
                    userInfo.setCtime(new Date(new java.util.Date().getTime() ));
                    userInfo.setUserId(userId);
                    String telEmail=(String)userMap.get("telOrEmail");
                    if(telEmail.contains("@")){
                        userInfo.setEmail(telEmail);
                    }else{
                        userInfo.setTel(telEmail);
                    }
                    userService.insertUserInfo(userInfo);
                    result=true;
                    status="1";
                    //恢复之前flag的值
                    flag=false;
                }
            }else{
                error="未知操作";
                throw new Exception(error);
            }
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
    @RequestMapping(value = "/updateUserInfo",produces = "text/html;charset=UTF-8")
    public String updateUser(@RequestParam(value = "pwd" ,required = false)String password,
                             @RequestParam(value="tel" ,required = false)String tel,
                             @RequestParam(value="email",required = false)String email,
                             @RequestParam(value = "company",required = false)String company,
                             @RequestParam(value = "sex",required = false)String sex,
                             @RequestParam(value = "work",required = false)String work,
                             @RequestParam(value = "nickname",required = false)String nickname,
                             @RequestParam("type")String type)throws IOException{
        //初始化
        String error="";
        String status="0";
        String content="";
        boolean result=false;
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
    try{
            System.out.println("type:"+type);
            if(type==null||type.equals("")){
                error="操作类型不能为空";
                throw new Exception(error);
            }
            if(type.equals("3")){//修改用户信息和密码
                if(password==null||password.equals("")){//空字符检测
                    error="修改的用户名不能为空";
                    throw new Exception(error);
                }
                if(password.length()<6&&password.length()>16){//输入范围
                    error="修改的密码只能在6-16位";
                    throw new Exception(error);
                }
                if(tel==null||tel.equals("")){
                    error="修改的电话号码不能为空";
                    throw new Exception(error);
                }
                if(tel.length()!=11){
                    error="输入的电话号码格式不正确";
                    throw new Exception(error);
                }
                if(email==null||email.equals("")){
                    error="修改的电子邮件不能为空";
                    throw new Exception(error);
                }
                if(email.length()<6||email.length()>255){
                    error="修改的电子邮件过长或者过短";
                    throw new Exception(error);
                }
                if(company==null||company.equals("")){
                    error="修改的公司名不能为空";
                    throw new Exception(error);
                }
                if(3>company.length()||company.length()>255){
                    error="输入的公司名过长或者过短";
                }
                if(sex==null||sex.equals("")){
                    error="修改的性别不能为空";
                    throw new Exception(error);
                }
                if(!(sex.equals("男")||sex.equals("女"))){
                    error="性别只能输入男或者女";
                    throw  new Exception(error);
                }
                if(work==null||work.equals("")){
                    error="修改的职位不能为空";
                    throw new Exception(error);
                }
                if(work.length()>255){
                    error="修改的职位名过长";
                }
                if(nickname==null||nickname.equals("")){
                    error="修改的昵称不能为空";
                    throw new Exception(error);
                }
                if(nickname.length()>30){
                    error="修改的昵称过长";
                    throw new Exception(error);
                }
                //获取用户信息和密码
                int userId=userService.getUserIdByname(username);
                userInfo=userService.getUserInfoById(userId);
                if(!tel.equals(userInfo.getTel())){
                    userInfo.setTel(tel);
                }
                if(!email.equals(userInfo.getEmail())){
                    userInfo.setEmail(email);
                }
                if(!sex.equals(userInfo.getSex())){
                    userInfo.setSex(sex);
                }
                if(!company.equals(userInfo.getCompany())){
                    userInfo.setCompany(company);
                }
                if(!work.equals(userInfo.getWork())){
                    userInfo.setWork(work);
                }
                if(!nickname.equals(userInfo.getNickName())){
                    userInfo.setNickName(nickname);
                }
                //更新密码
                String pwd=userService.getUserById(userId).getPwd();
                if(pwd.equals(password)){
                    error="新密码与旧密码相同，没有修改";
                    throw new Exception(error);
                }else{
                    userService.updatePwdById(password,userId);
                }
                //执行更新操作
                userService.updateUserInfoById(userInfo);
            }else if(type.equals("2")){//修改用户信息
                if(tel==null||tel.equals("")){
                    error="修改的电话号码不能为空";
                    throw new Exception(error);
                }
                if(tel.length()!=11){
                    error="输入的电话号码格式不正确";
                    throw new Exception(error);
                }
                if(email==null||email.equals("")){
                    error="修改的电子邮件不能为空";
                    throw new Exception(error);
                }
                if(email.length()<6||email.length()>255){
                    error="修改的电子邮件过长或者过短";
                    throw new Exception(error);
                }
                if(company==null||company.equals("")){
                    error="修改的公司名不能为空";
                    throw new Exception(error);
                }
                if(3>company.length()||company.length()>255){
                    error="输入的公司名过长或者过短";
                }
                if(sex==null||sex.equals("")){
                    error="修改的性别不能为空";
                    throw new Exception(error);
                }
                if(!(sex.equals("男")||sex.equals("女"))){
                    error="性别只能输入男或者女";
                    throw  new Exception(error);
                }
                if(work==null||work.equals("")){
                    error="修改的职位不能为空";
                    throw new Exception(error);
                }
                if(work.length()>255){
                    error="修改的职位名过长";
                }
                if(nickname==null||nickname.equals("")){
                    error="修改的昵称不能为空";
                    throw new Exception(error);
                }
                if(nickname.length()>30){
                    error="修改的昵称过长";
                    throw new Exception(error);
                }
                //获取用户信息
                int userId=userService.getUserIdByname(username);
                userInfo=userService.getUserInfoById(userId);
                if(!tel.equals(userInfo.getTel())){
                    userInfo.setTel(tel);
                }
                if(!email.equals(userInfo.getEmail())){
                    userInfo.setEmail(email);
                }
                if(!sex.equals(userInfo.getSex())){
                    userInfo.setSex(sex);
                }
                if(!company.equals(userInfo.getCompany())){
                    userInfo.setCompany(company);
                }
                if(!work.equals(userInfo.getWork())){
                    userInfo.setWork(work);
                }
                if(!nickname.equals(userInfo.getNickName())){
                    userInfo.setNickName(nickname);
                }
                //执行更新操作
                userService.updateUserInfoById(userInfo);
            }else if(type.equals("1")){//修改密码
                if(password==null||password.equals("")){//空字符检测
                    error="修改的用户名不能为空";
                    throw new Exception(error);
                }
                //获取用户密码
                int  userId=userService.getUserIdByname(username);
                String pwd=userService.getUserById(userId).getPwd();
                if(pwd.equals(password)){
                    error="新密码与旧密码相同，没有修改";
                    throw new Exception(error);
                }else{
                    userService.updatePwdById(password,userId);
                }
            }else{
                error="未知操作";
                throw new Exception(error);
           }
        result=true;
        status="1";
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
    @RequestMapping("/checkCode")
    public String getCode(@RequestParam("code")String code,
                          HttpServletRequest request)throws  IOException{
        //初始化返回的json字符串
        String error="";
        String status="0";
        String content="";
        ObjectMapper mapper=new ObjectMapper();
        Map map=new HashMap();
        try {
            if(code==null||code.equals("")){
                error="验证码不能为空";
                throw new Exception(error);
            }
            String code_=(String)request.getSession().getAttribute("code");
            if(code_.equals(code)){
                status="1";
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            map.put("status",status);
            map.put("error",error);
            content=mapper.writeValueAsString(map);
        }
            return content;
    }
    @RequestMapping("/code")
    public void setCode(HttpServletRequest request,HttpServletResponse response)throws Exception{
        //生成session，存储生成的验证码，用于验证
        HttpSession session=request.getSession();
        //生成servlet输出流，把Image缓存数据输出到客户端
        ServletOutputStream sos=response.getOutputStream();
        //生成图片对象
        BufferedImage image=new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        //生成图形对象
        Graphics g=image.getGraphics();
        char checkCode[]=getRandCode();
        //随机生成验证码
        //对图片对象操作进行封装,来生成有干扰素的图片
        drawBackground(g);
        //对图片对象操作进行封装，来生成没有干扰素的图片
        drawCode(g,checkCode);
        g.dispose();
        //生成字节流，把Image缓存数据转换成字节流
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", bos);
        byte[] bytes=bos.toByteArray();
        //对响应进行封装
        //设置返回类型
        response.setContentType("image/jpeg");
        //设置编码类型
        //设置浏览器不缓存
        response.setHeader("Pragma", "No-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-cache");
        //设置返回内容长度
        response.setContentLength(bytes.length);
        //把内容写入到servlet输出流中
        sos.write(bytes);
        System.out.println("new code:"+new String(checkCode));
        session.setAttribute("code",new String(checkCode));
        // session.setMaxInactiveInterval(180);
        System.out.println(session.getAttribute("code")+"\r"+session.getId());
        //关闭流对象
        sos.close();
        bos.close();

    }
    private void drawCode(Graphics g, char[] checkCode) {
        g.setColor(Color.BLACK);
        g.setFont(new Font(null, Font.BOLD|Font.ITALIC, FONT_SIZE));
        g.drawString(""+checkCode[0], FirX,FirY);
        g.drawString(""+checkCode[1], SECX, SECY);
        g.drawString(""+checkCode[2], THIX, THIY);
        g.drawString(""+checkCode[3], FOUX, FOUY);
    }

    private char[] getRandCode() {
        String code="0123456789abcdefghijklmnopqrStuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] chars=new char[4];
        for(int i=0;i<4;i++){
            int rand=(int)(Math.random()*36);
            chars[i]=code.charAt(rand);
        }
        return chars;
    }
    private void drawBackground(Graphics g){
        g.setColor(new Color(0xFFFFFF));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for(int i=0;i<120;i++){
            int x=(int) (Math.random()*WIDTH);
            int y=(int) (Math.random()*HEIGHT);
            int green=(int) (Math.random()*255);
            int red=(int) (Math.random()*255);
            int blue=(int) (Math.random()*255);
            g.setColor(new Color(red, green, blue));
            g.drawOval(x, y, 1, 0);
        }

    }
}
