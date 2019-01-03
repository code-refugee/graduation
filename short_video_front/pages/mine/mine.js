//获取应用实例(也就是app.js中的APP（）)
const app = getApp()

Page({
  data: {
    faceurl: '../resource/images/noneface.png'
  },

  //加载页面
  onLoad: function(){
    var me=this;
    //从缓存中获取用户信息
    var userInfo = app.getGlobalUserInfo();
    wx.showLoading({
      title: '玩命加载中..'
    });
    wx.request({
      url: app.serverUrl +'/query?userId='+userInfo.id,
      method: 'GET',
      header: {
        'content-type': 'application/json'
      },
      success: function(res){
        wx.hideLoading();
        // console.log(res);
        if(res.data.status==200){
          var userInfo=res.data.data;
          var faceUrl = "../resource/images/noneface.png";//默认
          if (userInfo.faceImage != null && userInfo.faceImage != '' && userInfo.faceImage != undefined){
            faceUrl = app.serverUrl + userInfo.faceImage; //静态资源访问
          }
          //设置属性值
          me.setData({
            faceurl: faceUrl,
            fansCounts: userInfo.fansCounts,
            followCounts: userInfo.followCounts,
            receiveLikeCounts: userInfo.receiveLikeCounts,
            nickname: userInfo.nickname
          })
        }else{
          wx.hideLoading();
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000
          })
        }
      },
      fail: function(){
        wx.hideLoading();
        wx.showToast({
          title: '哎呀，网络出错了~~',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  //换头像
  changeFace: function(){
    var me=this;
    //选择图片
    wx.chooseImage({
      count: 1,//可以选择上传图片数量
      sizeType: ["compressed"],//选择上传的是原图(original)还是压缩图，这里是压缩图
      sourceType: ['album', 'camera'],//选择图片来源，自拍or相册
      //选择成功，上传至后端
      success: function(res) {
        // console.log(res);
        var filePath=res.tempFilePaths;//得到的是一个数组
        //从本地缓存中得到用户信息
        var userInfo = app.getGlobalUserInfo();
        var serverUrl = app.serverUrl;
        wx.showLoading({
          title: '上传中..',
        })
        //上传图片
        wx.uploadFile({
          url: serverUrl +'/uploadface?userId='+userInfo.id,
          filePath: filePath[0],
          name: 'file',
          header: {
            'content-type': 'multipart/form-data'
          },

          //成功
          success: function(res){
            // console.log(res);
            //res.data并非一个JSON数据，需要转换
            var data=JSON.parse(res.data);
            // console.log(data);
            wx.hideLoading();
            if(data.status==200){
              wx.showToast({
                title: '上传成功~~',
                icon: 'success',
                duration: 2000
              })
              var imageUrl=data.data;
              me.setData({
                faceurl: serverUrl + imageUrl //静态资源访问
              })
            }else{
              wx.hideLoading();
              wx.showToast({
                title: data.msg,
                icon: 'none',
                duration: 2000
              })
            }
          },

          //失败
          fail: function(){
            wx.hideLoading();
            wx.showToast({
              title: '哎呀，网络出错了..',
              icon: 'loading',
              duration: 2000
            })
          }
        })
      },

      //失败
      fail: function(){
        wx.showToast({
          title: '出错啦..',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  //上传视频
  uploadvideo: function(){
    wx.chooseVideo({
      sourceType: ['album','camera'],
      maxDuration: 20,
      camera: 'back',
      success: function(res){
        // console.log(res)
        var duration=res.duration;
        var height=res.height;
        var width=res.width;
        var videoUrl=res.tempFilePath;
        //规定上传的视频不能大于20s，小于1s
        if(duration>21){
          wx.showToast({
            title: '视频太大了呢~~',
            icon: 'none',
            duration: 2000
          })
        }else if(duration<1){
          wx.showToast({
            title: '视频不能太短哦~~',
            icon: 'none',
            duration: 2000
          })
        }else{
          //打开选择bgm的页面（跳转的同时将视频信息传过去）
          wx.navigateTo({
            url: '../chooseBgm/chooseBgm?duration=' + duration
              + '&height=' + height
              + '&width=' + width
              + '&videoUrl=' + videoUrl
          })
        }
      },
      fail: function(){
        wx.showToast({
          title: '好像出错了呢~',
          icon: none,
          duration: 2000
        })
      }
    })
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