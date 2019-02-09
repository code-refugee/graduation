package com.yuhangTao.vo;

public class PublisherVO {

   private UsersVO usersVO;
   private boolean isUserLikeVideo;

    public UsersVO getUsersVO() {
        return usersVO;
    }

    public void setUsersVO(UsersVO usersVO) {
        this.usersVO = usersVO;
    }

    public boolean isUserLikeVideo() {
        return isUserLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        isUserLikeVideo = userLikeVideo;
    }
}
