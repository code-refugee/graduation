package com.yuhangTao.interceptor;


import com.yuhangTao.utils.IMoocJSONResult;
import com.yuhangTao.utils.JsonUtils;
import com.yuhangTao.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/*
* 这是一个拦截器
* 在spring中拦截器是定义在xml中
* springboot则是写在代码中，减少了xml的编写
* */
public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION="USER-REDIS-SESSION";

    /*
    * 拦截请求，在controller调用之前
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId=request.getHeader("userId");
        String userToken=request.getHeader("userToken");
        //首先判断用户id和token是否为空
        if(StringUtils.isNotBlank(userId)&&StringUtils.isNotBlank(userToken)){
            String userTokenNow=redisOperator.get(USER_REDIS_SESSION+":"+userId);

            //为什么要判空呢？是因为我们对userToken在redis中的存储加了时间限制
            if(StringUtils.isEmpty(userTokenNow)||StringUtils.isBlank(userTokenNow)){
                returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("请重新登录.."));
                return false;
            }else if(!userToken.equals(userTokenNow)){
                //当账号在其它设备登录时出现这种状况
                returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("在其它设备登录"));
                return false;
            }
        }else{
            returnErrorResponse(response,IMoocJSONResult.errorMsg("还未登录.."));
            return false;
        }

        return true;
    }

    /*
    * 请求controller之后，渲染视图之前
    * */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /*
    * 返回视图之后
    * */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result){
        OutputStream out=null;
        BufferedOutputStream bufferedOutputStream=null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out=response.getOutputStream();
            bufferedOutputStream=new BufferedOutputStream(out);
            bufferedOutputStream.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            bufferedOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
