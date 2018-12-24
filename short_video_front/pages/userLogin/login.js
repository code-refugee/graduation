//获取应用实例(也就是app.js中的APP（）)
const app = getApp()

Page({
  data: {
    
  },
  onLoad: function(){
    
  },
  doLogin: function(e){
    var formobject=e.detail.value;//我们可以通过打印e来看看e中包含了什么
    var username=formobject.username;
    var password=formobject.password;
    //判断用户名和密码是否为空
    if(username.length==0||password.length==0){
      //若为空则给提示
      wx.showToast({
        title: '用户名或密码不能为空',
        icon: 'none',
        duration: 2000 //2秒后消失
      })
    }else{
      var serverUrl = app.serverUrl;

      //出现提示正在连接..(改进：超过一定时间提示连接超时)
      wx.showLoading({
        title: '请等待...'
      });

      //调用api 访问后端(此处我们传的是一个json对象)
      wx.request({
        url: serverUrl +'/Login',
        method: 'POST',
        data: {
          username: username, //这里的变量名要与后端pojo类的字段一一对应
          password: password
        },
        header: {
          "content-type": "application/json"
        },
        //https请求成功
        success: function(res){
          //提示消失
          wx.hideLoading();
         //此处的status是后端传回来的
          var status=res.data.status;
          //200表示注册成功
          if(status==200){
            wx.showToast({
              title: '验证通过',
              icon: 'success',
              duration: 2000
            }),
              //将用户信息保存到本地缓存中去
              app.setGlobalUserInfo(res.data.data);
              wx.redirectTo({
                url: '../mine/mine',
              })
          }else{
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 2000
            })
          }
        },
        //访问失败
        fail: function(){
          wx.showToast({
            title: '请求超时',
            icon: 'loading',
            duration: 2000
          })
        }
      })
    }
  },
  
  goRegistPage: function(){
    wx.navigateTo({
      url: '../userRegist/regist'
    })

  },

  //写数据时隐藏Toast
  writing: function () {
    app.writing();
  }

})