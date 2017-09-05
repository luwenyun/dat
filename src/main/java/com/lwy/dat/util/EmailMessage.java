package com.lwy.dat.util;/**
 * Created by lwy on 2017/6/6.
 */

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

//import javax.mail.NoSuchProviderException;
/**
 * send email of sureCode
 *
 * @author 陆文云
 * @create 2017-06-06 20:17
 **/
public class EmailMessage{
    private String checkcode="ejhaaaktbfpkfbfe";
    private String host;
    private String transportProtocol;
    private String auth;
    private String userName;
    private String fromPerson;
    private String toPerson;
    private Session session;
    private String title;
    private String content;
    private String contentType;
    private Transport transport;
    private Message message;
    private Properties property;
    private static EmailMessage emailMessage;
    public EmailMessage(){
        System.out.print(" CONSTRUTS");

    }
    //设置发送信息
    public  EmailMessage setEmailInformation(String host,String transportProtocol,String auth,String name
            ,String fromPerson,String toPerson,String title,String content,String contentType){
        this.auth=auth;
        this.host=host;
        this.transportProtocol=transportProtocol;
        this.userName=name;
        this.fromPerson=fromPerson;
        this.toPerson=toPerson;
        this.title=title;
        this.content=content;
        this.contentType=contentType;
        return emailMessage;
    }
    //设置邮箱服务器协议和发送方的用户名和密码
    public void setEmailServices(){
        property=new Properties();
        property.setProperty("mail.host",host);
        property.setProperty("mail.transport.protocol",transportProtocol);
        property.setProperty("mail.smtp.auth",auth);
        property.setProperty("mail.smtp.starttls.enable", "true");
    }
    //获取session
    public void getSession(){
        //1、创建session
        if(property !=null){
            session = Session.getInstance(property);
            //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
            session.setDebug(true);
            //2、通过session得到transport对象
        }
    }
    //连接邮箱服务器
    public  void connectEmailServer() throws MessagingException{
        //使用JavaMail发送邮件的5个步骤
        if(session !=null){
            transport = session.getTransport();
            System.out.println("transport:"+transport);
            //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            transport.connect(host,userName , checkcode);
        }
    }
    //封装对Message对象的操作，即设置邮件主题，接受方，正文，附件等。
    public void getMessage() throws MessagingException{
        //创建邮件对象
        MimeMessage mimeMessage = new MimeMessage(session);
        //指明邮件的发件人
        mimeMessage.setFrom(new InternetAddress(fromPerson));
        //指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(toPerson));
        //邮件的标题
        mimeMessage.setSubject(title);
        //邮件的文本内容
        mimeMessage.setContent(content, contentType);
        //返回创建好的邮件对象
        message=mimeMessage;
    }
    //发送邮件
    public void  sendEmailMessage() throws MessagingException {
        if(transport !=null&&message!=null){
            System.out.print("开始发送。。。");
            transport.sendMessage(message,message.getAllRecipients());
        }
    }
//    public static void main(String []args) throws MessagingException{
//        EmailMessage email=new EmailMessage();
//        String host="smtp.qq.com";
//        String port="smtp";
//        String auth="true";
//        String name="1623905995";
//        String fromPerson="1623905995@qq.com";
//        String toperson="18874047217@163.com";
//        String title="邮箱验证";
//        String conetent="8888";
//        String contentType="text/html; charset=utf-8";
//        email.setEmailInformation(host,port,auth,name,fromPerson,toperson,title,conetent,contentType);
//        email.setEmailServices();
//        email.getSession();
//        email.connectEmailServer();
//        email.getMessage();
//        email.sendEmailMessage();
//
//
//    }
}
