package blog.service;

import blog.entity.BlogUser;

public interface UserService {

    /**
     * 根据用户名获取系统用户
     */
    BlogUser getUserByName(String username);
}
