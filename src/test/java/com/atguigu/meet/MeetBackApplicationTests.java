package com.atguigu.meet;

import com.atguigu.meet.mapper.user.UserMapper;
import com.atguigu.meet.model.entity.user.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MeetBackApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<SysUser> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
    }

}
