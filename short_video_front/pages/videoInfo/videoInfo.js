//index.js
//获取应用实例
const app = getApp();
var uploadFile=require('../../utils/uploadVideo.js');

Page({
  data:{
    cover: '',
    videoId: '',
    src: '',
    videoInfo: [],
    userLikeVideo: false,
    publisher: '',
    serverUrl: ''
  },
  videoCtx: {},//用来保存video的对象
  onLoad: function(params){
    var me=this;
    //获取上一个页面传入的数据(注意将字符串转换为JSON对象)
    var videoInfo=JSON.parse(params.videoInfo);
    var height=videoInfo.videoHeight;
    var width=videoInfo.videoWidth;
    var cover='cover';//cover是wxtml中的一个样式
    if(width>height){
      cover='';
    }
    me.setData({
      videoInfo: videoInfo,
      videoId: videoInfo.id,
      src: app.serverUrl+videoInfo.videoPath,
      cover: cover
    });
    //创建video的上下文（后面会用到，可以用它来打开和关闭视频）
    me.videoCtx = wx.createVideoContext("myVideo",me);

    //查询视频信息
    var serverUrl = app.serverUrl;
    var userInfo = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo;
    wx.showLoading({
      title: '请等待..'
    })
    wx.request({
      url: serverUrl + '/queryPublisher?loginUserId=' + userInfo.id + '&videoId=' + videoInfo.id
        + '&publisherId=' + videoInfo.userId,
      method: 'POST',
      header: {
        "content-type": "application/json"
      },
      success: function(res){
        wx.hideLoading();
        if(res.data.status==200){
          me.setData({
            publisher: res.data.data.usersVO,
            userLikeVideo: res.data.data.userLikeVideo,
            serverUrl: serverUrl
          })
        }else{
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
          title: '出错了呢~',
          icon: none,
          duration: 2000
        })
      }
    })
  } ,

  onShow: function(){
    var me=this;
    me.videoCtx.play();//页面展示时播放视频
  },

  onHide: function(){
    var me = this;
    me.videoCtx.pause();//页面隐藏时暂停视频
  },
  
  //点击搜索
  showSearch: function(){
    wx.navigateTo({
      url: '../searchView/searchView',
    })
  },

  //上传视频（当用户没有登录时跳到登录页，登录后再返回该页面）
  upload: function(){
    var me=this;
    var user = app.getGlobalUserInfo();
    if (user == null || user == '' || user == undefined) {
      var videoInfo = JSON.stringify(me.data.videoInfo);
      //这里要注意我们必须将？和=改为其它符号不然传不过去
      var redirtUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo
      wx.navigateTo({
        url: '../userLogin/login?redirtUrl=' + redirtUrl,
      })
    } else {
      uploadFile.uploadVideo()
    }
    
  },

  //返回主頁
  showIndex: function(){
    wx.navigateTo({
      url: '../index/index',
    })
  },

  //顯示用戶的個人信息
  showMine: function(){
    var user = app.getGlobalUserInfo();
    if(user==null||user==''||user==undefined){
      wx.navigateTo({
        url: '../userLogin/login',
      })
    }else{
      wx.navigateTo({
        url: '../mine/mine',
      })
    }
  },

  //显示用户是否喜欢该视频
  likeVideoOrNot: function(){
    var me=this;
    var userInfo = app.getGlobalUserInfo();
    var serverUrl = app.serverUrl;
    var videoInfo=me.data.videoInfo;
    // console.log(videoInfo);
    var userLikeVideo = me.data.userLikeVideo;
    var toUrl='';
    //1.首先判断要访问的url
    if (userLikeVideo){
      toUrl ='/userUnLike'
    }else{
      toUrl = '/userLike'
    }
    wx.showLoading({
      title: '请等待..',
    });
    wx.request({
      url: serverUrl + toUrl + '?userId=' + userInfo.id + '&videoId=' + videoInfo.id 
        + '&videoCreateId=' + videoInfo.userId,
      method: 'POST',
      header: {
        "content-type": "application/json"
      },
      success: function(){
        wx.hideLoading();
        //设置图标
        me.setData({
          userLikeVideo: !userLikeVideo
        });
      },
      fail: function(){
        wx.hideLoading();
        wx.showToast({
          title: '出错了呢~',
          duration: 2000,
          icon: 'none'
        })
      }
    })
  }


})
