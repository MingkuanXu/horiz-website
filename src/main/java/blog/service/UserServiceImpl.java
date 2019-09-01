package blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.dao.UserDao;
import blog.entity.BlogUser;



@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    //@Cacheable(cacheNames = "authority", key = "#username")
    @Override
    public BlogUser getUserByName(String username) {
        return userDao.selectByName(username);
    }
    

}
