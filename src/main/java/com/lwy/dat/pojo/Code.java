package com.lwy.dat.pojo;/**
 * Created by lwy on 2017/6/6.
 */

import org.springframework.stereotype.Component;

/**
 * check cod is expires?
 *
 * @author 陆文云
 * @create 2017-06-06 19:15
 **/
@Component
public class Code {
    private String code;
    private long expires;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }
}
