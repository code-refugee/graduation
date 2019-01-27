//index.js
//获取应用实例
const app = getApp()

Page({
  data:{
    bgmlist:[],
    serverUrl:'',
    videoParams:''
  },
  onLoad: function(params){
    // console.log(params);
    var me=this;
    me.setData({
      videoParams: params
    });
    wx.showLoading({
      title: '玩命加载中~~'
    })
    var serverUrl=app.serverUrl;
    wx.request({
      url: serverUrl +"/queryBgm",
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function(res){
        wx.hideLoading();
        console.log(res);
        var data=res.data;
        if(data.status==200){
          me.setData({
            bgmlist: data.data,
            serverUrl: serverUrl
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
  uploadVideo: function(e){
    var me=this;
    var bgmId = e.detail.value.bgmRadioId;
    // console.log('背景音乐Id'+bgmId);
    var desc = e.detail.value.desc;
    var duration = me.data.videoParams.duration;
    var tempCoverUrl = me.data.videoParams.tempCoverUrl;
    var tempVideoUrl = me.data.videoParams.tempVideoUrl;
    var tempheight = me.data.videoParams.tempheight;
    var tempwidth = me.data.videoParams.tempwidth;
    var user = app.getGlobalUserInfo();
    wx.showLoading({
      title: '正在上传...',
    });
    wx.uploadFile({
      url: app.serverUrl +"/uploadVideo",
      formData:{
        userId: user.id,
        bgmId: bgmId,
        desc: desc,
        videoSeconds: duration,
        videoWidth: tempwidth,
        videoHeight: tempheight
      },
      filePath: tempVideoUrl,
      name: 'file',
      header: {
        'content-type': 'application/json'
      },
      success: function(res){
        console.log(res)
        var data = JSON.parse(res.data);
        // debugger;
        if(data.status==200){

          wx.hideLoading();
          wx.showToast({
            title: '上传成功~~',
            icon: 'success',
            duration: 2000
          });
          wx.navigateBack({
            delta: 1
          })
          //视频上传成功后上传视频封面(pc端支持，但手机端不支持)
          // wx.uploadFile({
          //   url: app.serverUrl + '/video/uploadVideoCover',
          //   formData: {
          //     userId: app.useInfo.id,
          //     videoId: data.data
          //   },
          //   filePath: tempCoverUrl,
          //   name: 'file',
          //   header: {
          //     'content-type': 'application/json'
          //   },
          //   success: function (res) {
          //   var data2=JSON.parse(res.data);
          //    if(data2.status==200){
          //      wx.hideLoading();
          //      wx.showToast({
          //        title: '上传成功~~',
          //        icon: 'none',
          //        duration: 2000
          //      });
          //      wx.navigateBack({
          //        delta: 1
          //      })
          //    }else{
          //      wx.hideLoading();
          //      wx.showToast({
          //        title: data2.msg,
          //        icon: 'loading',
          //        duration: 2000
          //      })
          //    }
          //   }
          // })
        }else{
          wx.hideLoading();
          wx.showToast({
            title: '上传失败( ╯□╰ )',
            icon: 'loading',
            duration: 2000
          })
        }
        
      },
      fail: function(){
        wx.hideLoading();
        wx.showToast({
          title: '出错了呢...',
          icon: 'loading',
          duration: 2000
        })
      }
    })
  }
})
