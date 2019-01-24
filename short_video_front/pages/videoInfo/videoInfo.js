//index.js
//获取应用实例
const app = getApp();
var uploadFile=require('../../utils/uploadVideo.js');

Page({
  data:{
    cover: '',
    videoId: '',
    sec: '',
    videoInfo: []
  },
  videoCtx: {},
  onLoad: function(params){
    var me=this;
    //获取上一个页面传入的数据(注意将字符串转换为JSON对象)
    var videoInfo=JSON.parse(params.videoInfo);
    var height=videoInfo.videoHeight;
    var width=videoInfo.videoWidth;
    var cover='cover';
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
  } ,
  onShow: function(){
    var me=this;
    me.videoCtx.play();
  },
  onHide: function(){
    var me = this;
    me.videoCtx.pause();
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
  showIndex: function(){
    wx.navigateTo({
      url: '../index/index',
    })
  },
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
  }
})
