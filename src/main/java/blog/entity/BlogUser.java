package blog.entity;

import lombok.Data;

@Data

public class BlogUser {
	
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    /**
     * 用户角色
     */
    private BlogRole role;

    
	public BlogUser() {}
    
    public BlogUser(String username, String password, BlogRole role) {
    		this.username = username;
    		this.password = password;
    		this.role = role;
    }
   
}
