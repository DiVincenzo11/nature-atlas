package com.keshihua.ksh.service.serviceImpl;

import com.keshihua.ksh.mapper.UserMapper;
import com.keshihua.ksh.pojo.User;
import com.keshihua.ksh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceimpl implements UserService {

    @Autowired
    private  UserMapper usermapper;

    public int register(User user){
        if(usermapper.selectUser(user)==null){
            usermapper.register(user);
            return 1;
        }
        return 0;
    }

    public User login(User user){
        User u = usermapper.login(user);
        if(u==null){
            return null;
        }
        return u;
    }

    public void updateNickname(User user){
        usermapper.updateNickname(user);
    }

    public void updateHeadPortrait(User user){
        usermapper.updateHeadportrait(user);
    }
}
