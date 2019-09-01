package blog.entity;

import java.util.List;

import lombok.Data;

@Data
public class BlogRole {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色拥有的权限
     */
    private List<BlogPermission> permissionList;

    public BlogRole() {
    }

    public BlogRole(String roleName) {
        this.roleName = roleName;
    }

}
