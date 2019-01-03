//app.js
App({
  serverUrl: "http://10.106.198.71:8081/short_video", //如果是在其他电脑运行，需要修改该处的ip地址
  useInfo: null,//用户信息
  
  //设置缓存
  setGlobalUserInfo: function(user){
    wx.setStorageSync('useInfo', user);
  },

  getGlobalUserInfo: function(){
    return wx.getStorageSync('useInfo')
  },

  writing: function () {
    wx.hideToast();
  }
})