package com.yuhangTao.controller;


import com.yuhangTao.impl.UserService;
import com.yuhangTao.org.n3r.idworker.Sid;
import com.yuhangTao.pojo.Users;
import com.yuhangTao.utils.IMoocJSONResult;
import com.yuhangTao.utils.MD5Utils;
import com.yuhangTao.vo.UsersVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/*
 * 用户注册、登录和注销
 *
 * @Api用于controller类上
 * paramType是参数存放位置
 * */

@RestController
@Api(value = "用户注册登录注销的接口",tags ={"注册、登录和注销的controller"})
public class RegistLoginController extends BasicController{

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
     *
     * @ApiOperation用在controller的方法上
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
        UsersVO usersVO=getUsersVO(user);
        return IMoocJSONResult.ok(usersVO);
    }

    @PostMapping("/Login")
    @ApiOperation(value = "用户登录",notes = "用户登录的接口")
    public IMoocJSONResult login(@RequestBody Users user){
        String username = user.getUsername();
        String password = user.getPassword();
        //判断用户名和密码是否为空
        if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }

        //对密码进行加密
        try {
            password= MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //验证用户名、密码是否存在
        Users result=userService.queryIsLegalUser(username, password);
        if(result==null){
            return IMoocJSONResult.errorMsg("用户名或密码错误");
        }else{
            result.setPassword("");//安全考虑不返回密码
            UsersVO usersVO=getUsersVO(result);
            return IMoocJSONResult.ok(usersVO);
        }

    }

    @GetMapping("/logout")
    @ApiOperation(value = "用户注销",notes = "用户注销接口")
    @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query")
    public IMoocJSONResult logout(String userId){
        if(StringUtils.isBlank(userId))
            return IMoocJSONResult.errorMsg("用户Id不能为空");
        //删除redis中的session
        redisOperator.del(USER_REDIS_SESSION+":"+userId);
        return IMoocJSONResult.ok();
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

    //通过redis设置的唯一一个值和小程序的值来维系关系（也称为无状态session）
    private UsersVO getUsersVO(Users user){

        /*UUID含义是通用唯一识别码,,是指在一台机器上生成的数字，它保证对在同一时空中的所有机器都是唯一的，
          是由一个十六位的数字组成,表现出来的形式。UUID 的目的，是让分布式系统中的所有元素，都能有唯一的辨识
          资讯，而不需要透过中央控制端来做辨识资讯的指定。如此一来，每个人都可以建立不与其它人冲突的 UUID。
          在这样的情况下，就不需考虑数据库建立时的名称重复问题。*/
        String uniqueToken= UUID.randomUUID().toString();

        //timeout:设置超时时间
        redisOperator.set(USER_REDIS_SESSION+":"+user.getId(),uniqueToken,1000*60*3);

        UsersVO usersVO=new UsersVO();

        //将user数据复制到usersVO
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserToken(uniqueToken);
        return  usersVO;
    }

}
