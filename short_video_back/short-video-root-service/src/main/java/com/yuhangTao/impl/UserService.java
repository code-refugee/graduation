package com.yuhangTao.impl;


import com.yuhangTao.pojo.Users;
import com.yuhangTao.pojo.UsersReport;


/**

 * 用户服务接口

 * 功能：1.查询用户是否存在

 * 2.创建用户

 * 3.检测用户是否合法（即用户名，密码是否正确）

 * 4.修改用户信息

 * 5.查询用户信息

 * 6.查询用户是否喜欢该视频

 * 7.用户关注视频发布者

 * 8.用户取消关注

 * 9.用户是否关注该发布者

 * 10.保存举报信息
 */
public interface UserService {

    //1.
    boolean queryUserNameIsExists(String username);

    //2.
    void createUser(Users user);

    //3
    Users queryIsLegalUser(String username,String password);

    //4
    void updateUserInfo(Users user);

    //5
    Users queryUserInfo(String userId);

    //6
    boolean isUserLikeVideo(String userId,String videoId);

    //7
    void followPublisher(String publisherId,String userId);

    //8
    void deleteFollow(String publisherId,String userId);

    //9
    boolean queryIsFollow(String publisherId,String userId);

    //10
    void saveReportReason(UsersReport usersReport);
}
