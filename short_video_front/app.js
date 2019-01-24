//app.js
App({
  serverUrl: "http://192.168.0.102:8081/short_video",
  useInfo: null,

  setGlobalUserInfo: function(user){
    wx.setStorageSync("useInfo", user)
  },
  getGlobalUserInfo: function(){
    return wx.getStorageSync("useInfo")
  }
}
)