package blog.support;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import blog.entity. *;
import blog.service.UserService;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BlogUser blogUser = userService.getUserByName(username);
        if (null == blogUser) {
            throw new UsernameNotFoundException(username);
        }
        
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (BlogPermission permission : blogUser.getRole().getPermissionList()) {
        		authorities.add(new SimpleGrantedAuthority(permission.getCode()));
        	}
        return new User(blogUser.getUsername(), blogUser.getPassword(), authorities);
    }
         
}
