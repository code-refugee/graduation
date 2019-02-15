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
    serverUrl: '',

    commentFocus: false,
    contentValue: '',

    commentsList: [],
    commentsPage: 1,
    commentsTotalPage: 1
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

    this.getCommentsList(1)
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
      var videoInfo = JSON.stringify(me.data.videoInfo);
      var redirtUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo
      wx.navigateTo({
        url: '../userLogin/login?redirtUrl=' + redirtUrl,
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
  },

  //显示视频上传者的信息
  showPublisher: function(){
    var me=this;
    var userInfo = app.getGlobalUserInfo();
    var publisher = me.data.publisher;
    var realUrl = '../mine/mine#publisherId@' + publisher.id;
    //先判断当前用户是否登录，如果为登录，先让他登录再查看发布者的信息
    if(userInfo==null||userInfo==''||userInfo==undefined){
      wx.navigateTo({
        url: '../userLogin/login?redirtUrl=' + realUrl
      })
    }else{
      wx.navigateTo({
        url: '../mine/mine?publisherId=' + publisher.id,
      })
    }
    
  },

  //点击分享
  shareMe: function(){
    var me=this;
    //使用微信官方提供的api
    wx.showActionSheet({
      itemList: ['下载视频','分享至朋友圈','举报用户'],
      success: function(res){
        console.log(res.tapIndex)
        var index = res.tapIndex;
        if(index==0){
          wx.showLoading({
            title: '下载中..',
          })
          //下载视频(调用官方API即可)
          wx.downloadFile({
            url: app.serverUrl + me.data.videoInfo.videoPath,
            success: function(res){
              if (res.statusCode === 200){
                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success: function(params){
                    wx.hideLoading();
                    wx.showToast({
                      title: '下载成功~',
                      icon: 'none',
                      duration: 2000
                    })
                  },
                  fail: function(){
                    wx.hideLoading();
                    wx.showToast({
                      title: '保存失败..',
                      icon: 'none',
                      duration: 2000
                    })
                  }
                })
              }
            },
            fail: function(){
              wx.hideLoading();
              wx.showToast({
                title: '下载失败..',
                icon: 'none',
                duration: 2000
              })
            }
          })

        }else if(index==1){
          //分享
          wx.showToast({
            title: '暂不支持..',
            icon: 'none',
            duration: 2000
          })
        }else{
          //举报
          var userInfo=app.getGlobalUserInfo();
          //如果用户未登录则没有举报的权限
          if(userInfo==null||userInfo==''||userInfo==undefined){
            var videoInfo = JSON.stringify(me.data.videoInfo);
            var redirtUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo
            wx.navigateTo({
              url: '../userLogin/login?redirtUrl=' + redirtUrl,
            })
          }else{
            var publisherId = me.data.videoInfo.userId;
            var videoId = me.data.videoInfo.id;
            wx.navigateTo({
              url: '../report/report?videoId=' + videoId + '&publisherId=' + publisherId,
            })
          }
        }
      }
    })
  },

  //设置转发（必须在page实现这个函数）
  onShareAppMessage: function (res) {
    var me = this;
    var videoInfo = me.data.videoInfo;

    return {
      title: '我分享了短视频--' + videoInfo.videoDesc,
      path: '../videoInfo/videoInfo?videoInfo=' + JSON.stringify(videoInfo)
    }
  },

  //点击图标，弹出底部input输入框
  leaveComment: function(){
    var me=this;
    me.setData({
      commentFocus: true
    })
  },

  //保存评论
  saveComment: function(e){
    // console.log(e);
    var me=this;
    var userInfo = app.getGlobalUserInfo();
    //如果用户未登录则没有评论的权限
    if (userInfo == null || userInfo == '' || userInfo == undefined) {
      var videoInfo = JSON.stringify(me.data.videoInfo);
      var redirtUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo
      wx.navigateTo({
        url: '../userLogin/login?redirtUrl=' + redirtUrl,
      })
    } else {
      var comment = e.detail.value;
      if (comment == null || comment == '' || comment == undefined) {
        wx.showToast({
          title: '说点什么吧~',
          icon: 'none',
          duration: 2000
        })
        return
      }
      wx.showLoading({
        title: '请等待..',
      })
      wx.request({
        url: app.serverUrl +'/saveComment',
        method: 'POST',
        data: {
          videoId: me.data.videoInfo.id,
          fromUserId: userInfo.id,
          comment: comment
        },
        header: {
          "content-type": "application/json"
        },
        success: function(){
          wx.hideLoading();
          wx.showToast({
            title: '评论成功',
            icon: 'none',
            duration: 2000
          })
          me.setData({
            contentValue: '',
            commentsList: []
          })
          me.getCommentsList(1);
        },
        fail: function(){
          wx.hideLoading();
          wx.showToast({
            title: '出错了呢~',
            icon: 'none',
            duration: 2000
          })
        }
      })
    }
  },

  //评论上拉刷新
  onReachBottom: function(){
    var me=this;
    var commentsPage = me.data.commentsPage;
    var commentsTotalPage = me.data.commentsTotalPage;
    if (commentsPage == commentsTotalPage){
      return;
    }
    var page = commentsPage+1;
    this.getCommentsList(page);
  },
  
  //查询评论列表
  getCommentsList: function(page){
    var me=this;
    var videoId = me.data.videoInfo.id;
    wx.request({
      url: app.serverUrl + '/getVideoComments?videoId=' + videoId +'&page='+page,
      method: 'POST',
      header: {
        "content-type": "application/json"
      },
      success: function(res){
        // console.log(res)
        var commentsList = me.data.commentsList;
        me.setData({
          commentsList: commentsList.concat(res.data.data.content),
          commentsPage: page,
          commentsTotalPage: res.data.data.allPages
        })
      }
    })
  }


})
