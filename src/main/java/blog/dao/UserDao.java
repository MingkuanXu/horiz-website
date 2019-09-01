package blog.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import blog.entity. *;
import blog.properties.UserProperties;


@Repository

public class UserDao {
	
	@Autowired
	private UserProperties userProperties;
	
    private BlogRole admin = new BlogRole("admin");
    private BlogRole developer = new BlogRole("developer");
    private BlogRole client = new BlogRole("client"); 

    {
    		/***
    		 * Used to store permissions to pages that need additional authorization.
    		 * A "Preauthorization" annotation is also required at the controller side to complete the verification process.
    		 */
        BlogPermission p1 = new BlogPermission();
        p1.setCode("UserIndex");
        p1.setName("个人中心");
        p1.setUrl("/user/index.html");

        BlogPermission p2 = new BlogPermission();
        p2.setCode("BookList");
        p2.setName("图书列表");
        p2.setUrl("/book/list");

        BlogPermission p3 = new BlogPermission();
        p3.setCode("BookAdd");
        p3.setName("添加图书");
        p3.setUrl("/book/add");

        BlogPermission p4 = new BlogPermission();
        p4.setCode("BookDetail");
        p4.setName("查看图书");
        p4.setUrl("/book/detail");

        admin.setPermissionList(Arrays.asList(p1, p2, p3, p4));
        developer.setPermissionList(Arrays.asList(p1, p2));
        client.setPermissionList(Arrays.asList(p1));

    }

    /**
     * Get user information from the bean userProperties, from which search for the input username.
     * @param username
     * @return If the username is found, generate a new blogUser with its username, encoded password, and role. 
     *         Return null if not found.
     */
    public BlogUser selectByName(String username) {
    		List<Map<String,String>> allusers = userProperties.getUserlist();
    		for(Map<String,String> eachuser:allusers) {
    			if(eachuser.get("username").equals(username)) {
    				String password = eachuser.get("password");
    				String role = eachuser.get("role"); // each person can only have one role
    				if(role.equals("admin")) return new BlogUser(username,password,admin);
    				if(role.equals("developer")) return new BlogUser(username,password,developer);
    				if(role.equals("client")) return new BlogUser(username,password,client);
    			}
    		}
    		return null;	
    }
}