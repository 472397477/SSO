package com.lhj.sso.mapper;

import com.lhj.sso.model.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {


    int deleteByPrimaryKey(Long id);

    int insert(User record);

    User selectByPrimaryKey(Long id);

    List<User> selectAll();

    int updateByPrimaryKey(User record);

    @Select("select * from user where username = #{username} and password = #{password}")
    User selectUserByUsernameAndPassword(User user);

}
