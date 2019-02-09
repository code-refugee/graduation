//index.js
//获取应用实例
const app = getApp()

Page({
  data:{
    faceUrl: "../resource/images/noneface.png"
  },

  //加载页面
  onLoad: function(){
    var me=this;
    //从缓存中获取用户信息
    var useInfo = app.getGlobalUserInfo();
    wx.showLoading({
      title: '玩命加载中...',
    });
    wx.request({
      url: app.serverUrl +"/query?userId="+useInfo.id,
      method: 'GET',
      header: {
        'content-type': 'application/json',
      },
      success: function(res){
        wx.hideLoading();
        console.log(res);
        if(res.data.status==200){
          var userInfo=res.data.data;
          var faceUrl = "../resource/images/noneface.png";//默认
          if (userInfo.faceImage != null && userInfo.faceImage != '' && userInfo.faceImage
          !=undefined){
            faceUrl = app.serverUrl + userInfo.faceImage;//静态资源访问
          }
          //设置属性值
          me.setData({
            faceUrl: faceUrl,
            fansCounts: userInfo.fansCounts,
            followCounts: userInfo.followCounts,
            receiveLikeCounts: userInfo.receiveLikeCounts,
            nickname: userInfo.nickname
          })
        } 
      },
      fail: function(){
        wx.hideLoading();
        wx.showToast({
          title: '哎呀，网络出错了~',
          icon: 'loading',
          duration: 2000
        })
      }
    })
  },

  //用户注销
  logout: function(){
    wx.showLoading({
      title: '请等待...'
    })
    var user = app.getGlobalUserInfo();
    //调用后端
    wx.request({
      url: app.serverUrl +"/logout?userId="+user.id,
      method: "GET",
      header: {
        "content-type":"application/json"
      },
      success: function(res){
        wx.hideLoading();
        if(res.data.status==200){
          wx.showToast({
            title: '注销成功',
            icon: "success",
            duration: 2000
          });
          //注意将userInfo变为null
          // app.userInfo=null;
          //清空缓存
          wx.removeStorageSync('useInfo')
          //跳转
          wx.redirectTo({
            url: '../userLogin/login'
          })
        }
      },
      //访问出错
      fail: function(){
        wx.hideLoading();
        wx.showToast({
          title: '哎呀，网络出错了~',
          icon: 'loading',
          duration: 2000
        })
      }
    })
  },

  //用户上传头像
  changeFace: function(){
    var me=this;
    //选择图片
    wx.chooseImage({
      count: 1,//可以选择上传图片数量
      sizeType: ["compressed"],//选择上传的是原图还是压缩图，这里是压缩图
      sourceType: ['album', 'camera'],//选择图片来源，自拍or相册
      //选择成功，上传至后端
      success: function(res) {
        console.log(res);//打印
        var filePath=res.tempFilePaths;
        //从本地缓存中得到用户信息
        var user = app.getGlobalUserInfo();
        var serverUrl=app.serverUrl;
        wx.showLoading({
          title: '上传中..'
        })
        //上传图片
        wx.uploadFile({
          url: serverUrl +"/uploadface?userId="+user.id,
          filePath: filePath[0],
          name: 'file',
          header: {
            'content-type': 'application/json'
          },
          success: function(res){
            // console.log(res);
            var data=JSON.parse(res.data);//res.data并非一个JSON数据，需要转换
            // console.log(data);
            wx.hideLoading();
            if(data.status==200){
              wx.showToast({
                title: '上传成功~~',
                icon: "success",
                duration: 2000
              });
              var imageUrl=data.data;
              me.setData({
                faceUrl: serverUrl+imageUrl//静态资源访问
              })

            }else{
              wx.showToast({
                title: data.msg,
                icon: "none",
                duration: 2000
              })
            }

          },
          fail: function(){
            wx.showToast({
              title: '哎呀，网络出错了~',
              icon: "loading",
              duration: 2000
            })
          }
        })
      },
      fail: function(){
        wx.showToast({
          title: '出错了呢~',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  //用户上传视频
  uploadVideo: function(){
    wx.chooseVideo({
      sourceType: ['album', 'camera'],
      maxDuration: 11,
      camera: 'back',
      //选择成功
      success: function(res){
        console.log(res);

        var duration=res.duration;
        var tempwidth = res.width;
        var tempheight = res.height;
        var tempVideoUrl=res.tempFilePath;//视频地址
        var tempCoverUrl = res.thumbTempFilePath;
        //规定上传的视频不能大于25s，小于1s
        if (duration>25){
          wx.showToast({
            title: '视频太大了呢~~',
            icon: 'none',
            duration: 2000
          })
        } else if (duration<1){
          wx.showToast({
            title: '视频不能太短哦~~',
            icon: 'none',
            duration: 2000
          })
        }else{
          //打开选择bgm的页面（跳转的同时将视频信息传过去）
          wx.navigateTo({
            url: '../chooseBgm/chooseBgm?duration='+duration
              + '&tempwidth=' + tempwidth
              + '&tempheight=' + tempheight
              + '&tempVideoUrl=' + tempVideoUrl
              + '&tempCoverUrl=' + tempCoverUrl
          })
        }
      },
      fail: function(){
        wx.showToast({
          title: '好像出错了...',
          icon: 'none',
          duration: 2000
        })
      }
    })
  }
})
