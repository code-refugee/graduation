package com.yuhangTao.service;

import com.yuhangTao.impl.UserService;
import com.yuhangTao.mapper.UsersMapper;
import com.yuhangTao.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/*
 * 1、PROPAGATION_REQUIRED:如果存在一个事务，则支持当前事务。如果没有事务则开启。

 * 2、PROPAGATION_SUPPORTS:如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行。

 * 3、PROPAGATION_MANDATORY:如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。

 * 4、PROPAGATION_REQUIRES_NEW:总是开启一个新的事务。如果一个事务存在，则将这个存在的事务挂起。

 * 5、PROPAGATION_NOT_SUPPORTED:总是非事务地执行，并挂起任何存在的事务。

 * 6、PROPAGATION_NEVER:总是非事务地执行，如果存在一个活动事务，则抛出异常。

 * 7、 PROPAGATION_NESTED:如果一个活动的事务存在，则运行在一个嵌套的事务中，

 * 如果没有活动事务，则按TransactionDefinition.PROPAGATION_REQUIRED属性执行
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    /**
     *
     * 查询用户是否存在
     * @param username
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUserNameIsExists(String username) {
        Users user=new Users();
        user.setUsername(username);
        Users result= (Users) usersMapper.selectOne(user);
        return result==null?false:true;
    }

    /**
     * 创建用户
     * @param user
     * Propagation.REQUIRED:如果存在一个事务，则支持当前事务。如果没有事务则开启。
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createUser(Users user) {

        usersMapper.insert(user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryIsLegalUser(String username, String password) {
        //Example类可以用来生成一个几乎无限的where子句.
        Example example=new Example(Users.class);
        /*
        * Example类包含一个内部静态类 Criteria 包含一个用 anded 组合在where子句中的条件列表.
        * Example类包含一个 List 属性,所有内部类Criteria中的子句会用 ored组合在一起.
        * 使用不同属性的 Criteria 类允许您生成无限类型的where子句.
        * */
        Example.Criteria criteria=example.or();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result= (Users) usersMapper.selectOneByExample(example);
        return result;

    }

    /*修改用户信息*/
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserInfo(Users user) {
        Example example=new Example(Users.class);
        Example.Criteria criteria=example.or();
        criteria.andEqualTo("id",user.getId());
        /*如果使用updateByExample()方法，他会把空的数据也更新进去
         * 第一个参数 是要修改的部分值组成的对象，其中有些属性为null则表示该项不修改。
         * 第二个参数 是一个对应的查询条件的类， 通过这个类可以实现 order by 和一部分
         * 的where 条件*/
        usersMapper.updateByExampleSelective(user,example);
    }

    /*查询用户信息*/
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserInfo(String userId) {
        Example example=new Example(Users.class);
        Example.Criteria criteria=example.or();
        criteria.andEqualTo("id",userId);
        Users user= (Users) usersMapper.selectOneByExample(example);
        return user;
    }
}
