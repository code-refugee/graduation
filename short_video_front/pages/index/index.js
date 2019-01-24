//index.js
//获取应用实例
const app = getApp();

Page({
  data: {
    totalPage: 1, //总页数
    page: 1, //当前页
    videoList: [],
    screenWidth: '350',
    serverUrl: app.serverUrl,
    searchValue: ' '
  },
  onLoad: function(params){
    var me=this;
    var screenWidth=wx.getSystemInfoSync().screenWidth;
    me.setData({
      screenWidth: screenWidth
    });
    wx.showLoading({
      title: '死命加载中~~',
    }); 
    var searchValue = params.searchValue;
    var isSaveRecord=params.isSaveRecord;
    if(isSaveRecord==null||isSaveRecord==''||isSaveRecord==undefined){
      isSaveRecord=0;
    }
    me.setData({
      searchValue: searchValue
    })
    var page=me.data.page;
    
    me.getVideoList(page, isSaveRecord);
  },

  //上拉刷新
  onReachBottom: function(){
    var me=this;
    var currentpage=me.data.page;
    var totalPages = me.data.totalPage;
    //判断当前页数是否等于总页数
    if (currentpage==totalPages){
      wx.showToast({
        title: '已经没有更多了~~',
        icon: 'none',
        duration: 2000
      })
      return;
    }
    var page =currentpage+1;
    me.getVideoList(page,0);
  },

  //下拉刷新
  onPullDownRefresh: function(){
    var me=this;
    wx.showNavigationBarLoading();
    me.getVideoList(1,0);
  },

//展示视频信息
  showVideoInfo: function(e){
    var me=this;
    var videoList = me.data.videoList;
    var arrindex=e.target.dataset.arrindex;
    //对象是无法通过页面跳转传到下一个页面的
    var videoInfo = JSON.stringify(videoList[arrindex]);
    wx.redirectTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  },

  //获取视频列表
  getVideoList: function (page, isSaveRecord){
    var me=this;
    wx.request({
      url: app.serverUrl + '/video/queryAll?page=' + page + '&isSaveRecords='+isSaveRecord,
      method: 'POST',
      data:{
        videoDesc: me.data.searchValue
      },
      header: {
        'content-type': 'application/json'
      },
      success: function (res) {
        // console.log(res)
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        if (res.data.status == 200) {
          /*这一步很重要，因为我们会有下拉刷新的操作
            而且每一页的内容都是拼接起来的刷新的时候
            要将以前的list清空*/
          if (page == 1) {
            me.setData({
              videoList: []
            });
          }

          //展示页面(将原来的和拿到的视频列表拼接)
          var videoList = me.data.videoList;

          var queryList = res.data.data.result;
          me.setData({
            videoList: videoList.concat(queryList),
            page: page,
            totalPage: res.data.data.pages
          });
          // console.log(me.data.videoList)
        }

      },
      fail: function(){
        wx.showToast({
          title: '出错了呢~~',
          icon: 'none',
          duration: 2000
        })
      }
    })
  }
})
