//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    redirtUrl: ''
  },

onLoad: function(params){
  var me=this;
  var redirtUrl = params.redirtUrl;
  redirtUrl = redirtUrl.replace(/#/g,"?");
  redirtUrl = redirtUrl.replace(/@/g, "=");
  me.setData({
    redirtUrl: redirtUrl
  })
},

  doLogin: function(e){
    var me=this;
    var formObject=e.detail.value;
    var username=formObject.username;
    var password=formObject.password;
    //判断用户名和密码是否为空
    if(username.length==0||password.length==0){
      wx.showToast({
        title: '用户名和密码不能为空',
        icon: 'none',
        duration: 2000
      })
    }else{
      //出现提示正在连接..(改进：超过一定时间提示连接超时)
      wx.showLoading({
        title: '正在连接..'
      });

      wx.request({
        //显示正在连接
        url: app.serverUrl +"/Login",
        method: "POST",
        data: {
          username: username,
          password: password
        },
        header: {
          "content-type": "application/json"
        },
        //访问成功
        success: function(res){
          //提示消失
          wx.hideLoading();

          console.log(res);
          var status=res.data.status;
          if(status==200){
            wx.showToast({
              title: '验证通过',
              icon: 'success',
              duration: 2000
            });
            // app.useInfo = res.data.data;
            //将用户信息保存到本地缓存中去
            app.setGlobalUserInfo(res.data.data);
            var redirtUrl = me.data.redirtUrl
            if (redirtUrl != null && redirtUrl != undefined && redirtUrl!=''){
              wx.redirectTo({
                url: redirtUrl
              })
            }
            else{
              //登录成功跳转
              wx.redirectTo({
                url: '../mine/mine'
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
            title: '请求超时',
            icon: 'loading',
            duration: 2000  
          })
        }

      })
    }
  },
  //写数据时隐藏Toast
  writing: function () {
    wx.hideToast();
  },
  //点击注册时触发的函数
  goRegistPage: function(){
    wx.navigateTo({
      url: '../userRegist/regist'
    })
  }
})
