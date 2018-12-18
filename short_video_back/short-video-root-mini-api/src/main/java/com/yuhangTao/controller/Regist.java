package com.yuhangTao.controller;

import com.yuhangTao.impl.UserService;
import com.yuhangTao.org.n3r.idworker.Sid;
import com.yuhangTao.pojo.Users;
import com.yuhangTao.utils.IMoocJSONResult;
import com.yuhangTao.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

/*
* 用户注册
* @RestController注解相当于@ResponseBody ＋ @Controller合在一起的作用。
* 返回json数据不需要在方法前面加@ResponseBody注解了
*
* 1) 如果只是使用@RestController注解Controller，则Controller中的方法无法返回jsp页面，
* 或者html，配置的视图解析器 InternalResourceViewResolver不起作用，
* 返回的内容就是Return 里的内容。
*
*2) 如果需要返回到指定页面，则需要用 @Controller配合视图解析器
* InternalResourceViewResolver才行。如果需要返回JSON，XML或自定义mediaType内容到页面，
* 则需要在对应的方法上加上@ResponseBody注解。
* */
@RestController
@Api(value = "用户注册接口",tags = {"注册的controller"})
public class Regist {

    @Autowired
    private UserService userService;

    @Autowired
    private Sid sid;

    /*
    * 1、@requestBody注解常用来处理content-type不是默认的
    * application/x-www-form-urlcoded编码的内容，比如说：
    * application/json或者是application/xml等。
    * 一般情况下来说常用其来处理application/json类型。
    * 通过@requestBody可以将请求体中的JSON字符串绑定到相应的bean上，
    * 当然，也可以将其分别绑定到对应的字符串上。
    *
    * 因为提交的是表单，所以用PostMapping
    * 这里我们返回的是IMoocJSONResult工具类
    * 该工具类和直接返回json数据并没有太大区别
    * 只不过多了状态等数据，更人性化
    * */
    @PostMapping("/regist")
    @ApiOperation(value = "用户注册",notes = "用户注册接口")
    public IMoocJSONResult regist(@RequestBody Users user){

        //1.判断用户名，密码是否为空(这里使用apache的commons工具类中的StringUtils来判断)
        if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())){
            return IMoocJSONResult.errorMsg("用户名或密码不能为空");
        }

        ////2.判断用户名是否存在
        boolean isExists=userService.queryUserNameIsExists(user.getUsername());
        if(isExists){
            return IMoocJSONResult.errorMsg("该用户已存在");
        }else{
            //完善用户信息
            perfectUserInfo(user);
            //创建用户
            userService.createUser(user);
        }
        user.setPassword("");//安全考虑，不返回密码

        return IMoocJSONResult.ok(user);
    }

    /**
     * 完善用户信息
     * 其中Nickname默认为用户名，Id由工具类创建
     * FansCounts，FollowCounts，ReceiveLikeCounts默认为0
     * FaceImage默认为null
     * @param user
     */
    private void perfectUserInfo(Users user) {
        //使用加密算法对密码进行加密
        try {
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        user.setNickname(user.getUsername());//昵称默认为用户名
        user.setId(sid.nextShort());//使用common工程下的工具类生成唯一的id
        user.setFansCounts(0);
        user.setFollowCounts(0);
        user.setReceiveLikeCounts(0);
        user.setFaceImage(null);
    }

}
