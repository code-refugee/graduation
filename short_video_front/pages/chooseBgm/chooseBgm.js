//获取应用实例(也就是app.js中的APP（）)
const app = getApp()

Page({
  data: {
    bgmlist: [],
    serverUrl: '',
    videoParms: ''
  },

  //页面加载
  onLoad: function(parms){
    // console.log(parms);
    var me=this;
    me.setData({
      videoParms: parms
    });
    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '玩命加载中..',
    })
    wx.request({
      url: serverUrl +'/queryBgm',
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function(res){
        // console.log(res)
        wx.hideLoading();
        var data=res.data;
        if(data.status==200){
          me.setData({
            serverUrl: serverUrl,
            bgmlist: data.data
          })
        }
      },
      fail: function(){
        wx.hideLoading();
        wx.showToast({
          title: '哎呀，网络出错了~',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  //上传视频
  uploadVideo: function(res){
    var me=this;
    // console.log(res)
    var bgmId = res.detail.value.bgmRadioId;
    var desc = res.detail.value.desc;
    var videoSeconds=me.data.videoParms.duration;
    var videoUrl = me.data.videoParms.videoUrl;
    var videoHeight = me.data.videoParms.height;
    var videoWidth = me.data.videoParms.width;
    var userInfo = app.getGlobalUserInfo();
    wx.showLoading({
      title: '正在上传...',
    });
    wx.uploadFile({
      url: app.serverUrl +'/uploadVideo',
      formData: {
        userId: userInfo.id,
        bgmId: bgmId,
        desc: desc,
        videoSeconds: videoSeconds,
        videoWidth: videoWidth,
        videoHeight: videoHeight
      },
      filePath: videoUrl,
      name: 'file',
      header: {
        'content-type': 'application/form-data'
      },
      success: function(res){
        var data=JSON.parse(res.data);
        if(data.status==200){
          wx.hideLoading();
          wx.showToast({
            title: '上传成功~~',
            icon: 'success',
            duration: 2000
          });
          //回退一页
          wx.navigateBack({
            delta: 1
          })
        }else{
          wx.hideLoading();
          wx.showToast({
            title: '上传失败( ╯□╰ )',
            icon: 'none',
            duration: 2000
          })
        }
      },
      fail: function(){
        wx.hideLoading();
        wx.showToast({
          title: '出错了呢...',
          icon: 'none',
          duration: 2000
        })
      }
    })
  }

  

})