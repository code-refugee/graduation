package com.yuhangTao.impl;


import com.yuhangTao.pojo.Users;

/**

 * 用户服务接口

 * 功能：1.查询用户是否存在

 * 2.创建用户

 * 3.检测用户是否合法（即用户名，密码是否正确）

 * 4.修改用户信息

 * 5.查询用户信息

 */
public interface UserService {

    //1.
    boolean queryUserNameIsExists(String username);

    //2.
    void createUser(Users user);
}
