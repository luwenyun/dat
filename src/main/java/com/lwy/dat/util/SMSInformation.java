package com.lwy.dat.util;/**
 * Created by lwy on 2017/6/6.
 */

//import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * send SMS of sureCode
 *
 * @author 陆文云
 * @create 2017-06-06 20:30
 **/
public class SMSInformation {
    //http://utf8.sms.webchinese.cn/?Uid=本站用户名&Key=接口安全秘钥&smsMob=手机号码&smsText=验证码:8888
    //http://www.smschinese.cn/api.shtml
    private String urlEncode="gbk";
    private String postMethod="http://"+urlEncode+".sms.webchinese.cn";
    private String contentTypeKey="Content-Type";
    private String contentTypeValue="application/x-www-form-urlencoded;charset=gbk";
    private String Uid="以码为生";
    private String key="b39d11d0e739fcfc0521";
    private String toTel="18874077217";
    private String text="验证码：8888";
    private String responseDecode="GBK";
    public SMSInformation(String code,String tel){
        text=code;
        toTel=tel;
    }
    public   void sendSMSInformation()throws Exception
    {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(postMethod);
        post.addRequestHeader(contentTypeKey,contentTypeValue);//在头文件中设置转码
        NameValuePair[] data ={ new NameValuePair("Uid",Uid),
                new NameValuePair("Key", key),
                new NameValuePair("smsMob",toTel),
                new NameValuePair("smsText",text)};
        post.setRequestBody(data);
        client.executeMethod(post);
       // Header[] headers = post.getResponseHeaders();
        int statusCode = post.getStatusCode();
        System.out.println("status:"+statusCode);
        //	System.out.println("statusCode:"+statusCode);
        //	for(Header h : headers)
        //	{
        //	System.out.println(h.toString());
        //	}
        String result = new String(post.getResponseBodyAsString().getBytes(responseDecode));
        //打印返回消息状态
        System.out.println(result);
        post.releaseConnection();
    }
//    public static void main(String []arg){
//        try{
//
//            SMSInformation sms=new SMSInformation("xx");
//            sms.sendSMSInformation();
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
//    }

}

