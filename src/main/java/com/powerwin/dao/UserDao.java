package com.powerwin.dao;

import com.powerwin.entity.User;

/**
 * Created by fengjinman Administrator on 2018/7/10.
 */
public interface UserDao {

    User queryUser(int id);

    int insert(User user);
}
