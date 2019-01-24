//index.js
//获取应用实例
const app = getApp()


//用户上传视频
function uploadVideo() {
  wx.chooseVideo({
    sourceType: ['album', 'camera'],
    maxDuration: 11,
    camera: 'back',
    //选择成功
    success: function(res) {
      console.log(res);

      var duration = res.duration;
      var tempwidth = res.width;
      var tempheight = res.height;
      var tempVideoUrl = res.tempFilePath; //视频地址
      var tempCoverUrl = res.thumbTempFilePath;
      //规定上传的视频不能大于30s，小于1s
      if (duration > 31) {
        wx.showToast({
          title: '视频太大了呢~~',
          icon: 'none',
          duration: 2000
        })
      } else if (duration < 1) {
        wx.showToast({
          title: '视频不能太短哦~~',
          icon: 'none',
          duration: 2000
        })
      } else {
        //打开选择bgm的页面（跳转的同时将视频信息传过去）
        wx.navigateTo({
          url: '../chooseBgm/chooseBgm?duration=' + duration +
            '&tempwidth=' + tempwidth +
            '&tempheight=' + tempheight +
            '&tempVideoUrl=' + tempVideoUrl +
            '&tempCoverUrl=' + tempCoverUrl
        })
      }
    },
    fail: function() {
      wx.showToast({
        title: '好像出错了...',
        icon: 'none',
        duration: 2000
      })
    }
  })
}

//将公用的方法导出去
module.exports={
  uploadVideo: uploadVideo
}