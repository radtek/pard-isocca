package com.pard.common.security;

import com.google.common.collect.Lists;
import com.pard.common.exception.UnknowAccountException;
import com.pard.modules.sys.entity.User;
import com.pard.modules.sys.repository.UserRepository;
import com.pard.modules.sys.service.RoleService;
import com.pard.modules.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Created by wawe on 17/5/3.
 */
@Service("CustomUserDetailsImpl")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        User user = userService.findByName(loginName);
        if (user == null) {
            throw new UnknowAccountException("用户不存在");
        }
        return new CustomUserDetails(user);
    }
}
