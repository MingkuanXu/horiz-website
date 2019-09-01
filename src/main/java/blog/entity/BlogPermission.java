package blog.entity;

import lombok.Data;

@Data
public class BlogPermission {
    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限字符串
     */
    private String code;
    
    /**
     * URL
     */
    private String url;

}
