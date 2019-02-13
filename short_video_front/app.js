//app.js
App({
  serverUrl: "http://192.168.0.103:8081/short_video",
  useInfo: null,

  setGlobalUserInfo: function(user){
    wx.setStorageSync("useInfo", user)
  },

  getGlobalUserInfo: function(){
    return wx.getStorageSync("useInfo")
  },

  reportReasonArray: [
    '色情低俗',
    '政治敏感',
    '涉嫌诈骗',
    '辱骂谩骂',
    '广告垃圾',
    '诱导分享',
    '引人不适',
    '过于暴力',
    '违法违纪',
    '其它原因'
  ]
}
)