//index.js
//获取应用实例
const app = getApp()

Page({
  data:{
    faceUrl: "../resource/images/noneface.png",
    isMe: true,
    isFollow: false,
    publisherId: '',
    fansCounts: '',
    serverUrl: app.serverUrl,
    userId: '',

    videoSelClass: 'video-info',
    isSelectedWork: 'video-info-selected',
    isSelectedLike: '',
    isSelectedFollow: '',

    myWorkFalg: false,
    myLikesFalg: true,
    myFollowFalg: true,

    myVideoList: [],
    likeVideoList: [],
    followVideoList: [],

    //查询第几页
    myVideoPage: 1,
    likeVideoPage: 1,
    followVideoPage: 1,

    //数据库总共含有几页
    myVideoTotal: 1,
    likeVideoTotal: 1,
    followVideoTotal: 1
  },

  //加载页面
  onLoad: function(params){
    // console.log(params)
    var me=this;
    //从缓存中获取用户信息
    var useInfo = app.getGlobalUserInfo();
    var publisherId = params.publisherId;
    var id = useInfo.id;
    if (publisherId != null && publisherId != '' && publisherId !=undefined){
      if (id != publisherId){
        id = publisherId;
        me.setData({
          isMe: false,
          publisherId: publisherId,
        })
      }
    }
    me.setData({
      userId: id//等会在作品、收藏、关注里查询视频时有用
    })
    wx.showLoading({
      title: '玩命加载中...',
    });
    wx.request({
      url: app.serverUrl + "/query?userId=" + id + '&fanId=' + useInfo.id,
      method: 'GET',
      header: {
        'content-type': 'application/json',
      },
      success: function(res){
        wx.hideLoading();
        // console.log(res);
        if(res.data.status==200){
          var userInfo=res.data.data;
          var faceUrl = "../resource/images/noneface.png";//默认
          if (userInfo.faceImage != null && userInfo.faceImage != '' && userInfo.faceImage
          !=undefined){
            faceUrl = app.serverUrl + userInfo.faceImage;//静态资源访问
          }
          //设置属性值
          // bebugger;
          me.setData({
            faceUrl: faceUrl,
            fansCounts: userInfo.fansCounts,
            followCounts: userInfo.followCounts,
            receiveLikeCounts: userInfo.receiveLikeCounts,
            nickname: userInfo.nickname,
            isFollow: userInfo.follow
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
    });
    this.doSelectWork();
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
          title: '已取消~',
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
  },

  //用户点击关注或取消关注
  followMe: function(params){
    // console.log(params);
    var me=this;
    var userInfo = app.getGlobalUserInfo();
    var publisherId = me.data.publisherId;
    var userId = userInfo.id;
    var followtype = params.target.dataset.followtype;
    var toUrl='';
    if(followtype=='0'){
      toUrl = '/notyourfans?publisherId=' + publisherId + '&userId=' + userId;
    }else{
      toUrl = '/beyourfans?publisherId=' + publisherId + '&userId=' + userId;
    }
    wx.showLoading({
      title: '请等待..',
    })
    wx.request({
      url: app.serverUrl + toUrl,
      method: 'POST',
      header: {
        "content-type": "application/json"
      },
      success: function(res){
        console.log(res);
        wx.hideLoading();
        if(res.data.status==200){
          me.setData({
            isFollow: !me.data.isFollow
          })
          if (followtype == '0') {
            me.setData({
              fansCounts: --me.data.fansCounts
            })
            wx.showToast({
              title: '已取消关注',
              icon: 'none',
              duration: 2000
            })
          } else {
            me.setData({
              fansCounts: ++me.data.fansCounts
            })
            wx.showToast({
              title: '关注成功',
              icon: 'none',
              duration: 2000
            })
          }
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
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  //点击作品、收藏、或关注，实现相应的功能
  doSelectWork: function(){
    this.setData({
      //选中时，改变相应的颜色
      isSelectedWork: 'video-info-selected',
      isSelectedLike: '',
      isSelectedFollow: '',
      //显示对应的view并隐藏其它两个
      myWorkFalg: false,
      myLikesFalg: true,
      myFollowFalg: true,

      //初始化所有值
      myVideoList: [],
      likeVideoList: [],
      followVideoList: [],

      myVideoPage: 1,
      likeVideoPage: 1,
      followVideoPage: 1,

      myVideoTotal: 1,
      likeVideoTotal: 1,
      followVideoTotal: 1
    })

    this.getMyVideoList(1);
  },

  doSelectLike: function(){
    this.setData({
      isSelectedWork: '',
      isSelectedLike: 'video-info-selected',
      isSelectedFollow: '',

      myWorkFalg: true,
      myLikesFalg: false,
      myFollowFalg: true,

      myVideoList: [],
      likeVideoList: [],
      followVideoList: [],

      myVideoPage: 1,
      likeVideoPage: 1,
      followVideoPage: 1,

      myVideoTotal: 1,
      likeVideoTotal: 1,
      followVideoTotal: 1
    })

    this.getMyLikesList(1);
  },

  doSelectFollow: function(){
    this.setData({
      isSelectedWork: '',
      isSelectedLike: '',
      isSelectedFollow: 'video-info-selected',

      myWorkFalg: true,
      myLikesFalg: true,
      myFollowFalg: false,

      myVideoList: [],
      likeVideoList: [],
      followVideoList: [],

      myVideoPage: 1,
      likeVideoPage: 1,
      followVideoPage: 1,

      myVideoTotal: 1,
      likeVideoTotal: 1,
      followVideoTotal: 1
    })

    this.getMyFollowList(1);
  },

  //我发布的视频列表
  getMyVideoList: function(page){
    var me=this;

    wx.showLoading({
      title: '请等待..',
    })
    wx.request({
      url: app.serverUrl + '/quarryAll?page=' + page,
      method: 'POST',
      data: {
        userId: me.data.userId
      },
      header: {
        'content-type': 'application/json'
      },
      success: function(res){
        wx.hideLoading();
        // console.log(res);
        if(res.data.status==200){
          me.setData({
            myVideoList: me.data.myVideoList.concat(res.data.data.content),
            myVideoPage: res.data.data.page,
            myVideoTotal: res.data.data.allPages
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
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  //我点赞收藏的视频列表
  getMyLikesList: function(page){
    var me=this;
    wx.showLoading({
      title: '请等待..',
    })
    var userId = me.data.userId;
    wx.request({
      url: app.serverUrl + '/showMyLike?userId=' + userId +'&page='+page,
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function(res){
        wx.hideLoading();
        if (res.data.status == 200) {
          me.setData({
            likeVideoList: me.data.likeVideoList.concat(res.data.data.content),
            likeVideoPage: res.data.data.page,
            likeVideoTotal: res.data.data.allPages
          })
        } else {
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
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  //我关注的人发表的视频
  getMyFollowList: function(page){
    var me=this;
    wx.showLoading({
      title: '请等待..',
    })
    var userId =me.data.userId;
    wx.request({
      url: app.serverUrl + '/showMyFollow?userId=' + userId +'&page='+page,
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function (res) {
        wx.hideLoading();
        if (res.data.status == 200) {
          me.setData({
            followVideoList: me.data.followVideoList.concat(res.data.data.content),
            followVideoPage: res.data.data.page,
            followVideoTotal: res.data.data.allPages
          })
        } else {
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000
          })
        }
      },
      fail: function () {
        wx.hideLoading();
        wx.showToast({
          title: '出错了呢~',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  //播放视频
  showVideo: function(e){
    var me=this;
    // console.log(e);
    var index = e.target.dataset.arrindex;
    var myWorkFalg = me.data.myWorkFalg;
    var myLikesFalg = me.data.myLikesFalg;
    var myFollowFalg = me.data.myFollowFalg;
    var myVideoList = me.data.myVideoList;
    var likeVideoList = me.data.likeVideoList;
    var followVideoList = me.data.followVideoList;
    var videoInfo = '';
    if (!myWorkFalg){
      videoInfo = JSON.stringify(myVideoList[index])
    }
    if (!myLikesFalg){
      videoInfo = JSON.stringify(likeVideoList[index])
    }
    if (!myFollowFalg){
      videoInfo = JSON.stringify(followVideoList[index])
    }
    wx.navigateTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo
    })
  },

  //下拉刷新
  onReachBottom: function(){
    var me=this;

    var myWorkFalg = me.data.myWorkFalg;
    var myLikesFalg = me.data.myLikesFalg;
    var myFollowFalg = me.data.myFollowFalg;

    var myVideoPage = me.data.myVideoPage;
    var likeVideoPage = me.data.likeVideoPage;
    var followVideoPage = me.data.followVideoPage;

    var myVideoTotal = me.data.myVideoTotal;
    var likeVideoTotal = me.data.likeVideoTotal;
    var followVideoTotal = me.data.followVideoTotal;
    if (!myWorkFalg){
      if (myVideoPage == myVideoTotal){
        wx.showToast({
          title: '已经没有更多了~~',
          icon: 'none',
          duration: 2000
        })
        return;
      }
      var page = myVideoPage+1;
      me.getMyVideoList(page);
    } else if (!myLikesFalg){
      if (likeVideoPage == likeVideoTotal) {
        wx.showToast({
          title: '已经没有更多了~~',
          icon: 'none',
          duration: 2000
        })
        return;
      }
      var page = likeVideoPage + 1;
      me.getMyLikesList(page);
    } else if (!myFollowFalg){
      if (followVideoPage == followVideoTotal) {
        wx.showToast({
          title: '已经没有更多了~~',
          icon: 'none',
          duration: 2000
        })
        return;
      }
      var page = followVideoPage + 1;
      me.getMyFollowList(page);
    }
  }
})
