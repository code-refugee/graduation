package com.yuhangTao.service;

import com.yuhangTao.impl.BgmService;
import com.yuhangTao.mapper.BgmMapper;
import com.yuhangTao.pojo.Bgm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    /**
     * 通过bgmId查询bgm信息
     * @param bgmId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Bgm queryBgmById(String bgmId) {
        Example example=new Example(Bgm.class);
        Example.Criteria criteria=example.or();
        criteria.andEqualTo("id",bgmId);
        Bgm result= (Bgm) bgmMapper.selectOneByExample(example);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }
}
