package com.yuhangTao.controller;

import com.yuhangTao.impl.BgmService;
import com.yuhangTao.pojo.Bgm;
import com.yuhangTao.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "Bgm相关业务接口",tags = {"Bgm相关业务的Controller"})
public class BgmBusinessController {

    @Autowired
    private BgmService bgmService;

    @PostMapping("/queryBgm")
    @ApiOperation(value = "获取背景音乐列表",notes = "获取背景音乐列表的接口")
    public IMoocJSONResult queryBgmList(){
        List<Bgm> result=bgmService.queryBgmList();
        return IMoocJSONResult.ok(result);
    }
}
