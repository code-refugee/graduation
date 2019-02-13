
//获取应用实例
const app = getApp();

Page({
  data: {
    reasonType: '请选择原因',
    reportReasonArray: app.reportReasonArray,
    publisherId: '',
    videoId: ''
  },

  onLoad: function(params){
    var me=this;
    // console.log(params)
    var publisherId = params.publisherId;
    var videoId = params.videoId;
    me.setData({
      publisherId: publisherId,
      videoId: videoId
    })
  },

  //滚动选择器值改变时相应位置的text也要变
  changeMe: function(e){
    var me=this;
    // console.log(e)
    var index=e.detail.value;
    var reasonType = app.reportReasonArray[index];
    if (reasonType == null || reasonType == '' || reasonType==undefined){
      reasonType ='请选择原因'
    }
    me.setData({
      reasonType: reasonType
    })
  },

  //提交举报
  submitReport: function(res){
    var me=this;
    // console.log(res)
    var reasonIndex = res.detail.value.reasonIndex;
    var content = res.detail.value.reasonContent;
    var userInfo = app.getGlobalUserInfo();
    if (reasonIndex == null || reasonIndex == '' || reasonIndex==undefined){
      wx.showToast({
        title: '请选择举报理由',
        icon: 'none',
        duration: 2000
      })
      return;
    }
    wx.request({
      url: app.serverUrl +'/reportUser',
      data: {
        dealUserId: me.data.publisherId,
        dealVideoId: me.data.videoId,
        content: content,
        title: app.reportReasonArray[reasonIndex],
        userid: userInfo.id
      },
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function(res){
        wx.showToast({
          title: '举报成功~',
          icon: 'none',
          duration: 2000
        })
        wx.navigateBack({
          delta: 1
        })
      }
    })
  }
})
