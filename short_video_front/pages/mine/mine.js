//获取应用实例(也就是app.js中的APP（）)
const app = getApp()

Page({
  data: {
    faceurl: '../resource/images/noneface.png'
  },

  //换头像
  changeFace: function(){

  },

  //注销
  logout: function(){
    wx.showLoading({
      title: '请等待',
    });
    var userInfo = app.getGlobalUserInfo;
    wx.request({
      url: app.serverUrl +'/logout?userId='+userInfo.id,
      method: 'GET',
      header: {
        "content-type": "application/json"
      },
      success: function(res){
        wx.hideLoading();
        if(res.data.status==200){
          wx.showToast({
            title: '注销成功',
            icon: 'success',
            duration: 2000
          });
          //清空缓存
          wx.removeStorageSync('useInfo');
          wx.redirectTo({
            url: '../userLogin/login',
          })
        }
      },
      //访问出错
      fail: function(){
        wx.hideLoading();
        wx.showToast({
          title: '哎呀，网络出错了..',
          icon: 'loading',
          duration: 2000
        })
      }
    })
  }


})