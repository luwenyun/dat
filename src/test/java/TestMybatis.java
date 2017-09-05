/**
 * Created by lwy on 2017/4/30.
 */

import com.lwy.dat.service.UserService;
import com.lwy.dat.service.UserServiceImpl;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

/**
 * test class
 *
 * @author 陆文云
 * @create 2017-04-30 14:12
 **/
public class TestMybatis {
    private static Logger log = Logger.getLogger(TestMybatis.class);
    private ApplicationContext ac=null;
    @Resource
    private UserService userService = null;
    @Before
    public void before(){
        ac=new ClassPathXmlApplicationContext("spring-mvc.xml");
        userService=(UserServiceImpl) ac.getBean("userService");
    }
    @Test
    public void testUser(){
//        User user=userService.getUserById(1);
//        System.out.println("username"+user.getUsername());
//        log.info(user.getUsername());
    }



}
