package blog.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



/***
 * Create a new bean based on the information stored inside "userinfo" prefix of the application.yml
 * @author xumingkuan
 * @return userlist refers to a list of user information maps: {"username":"xxxx","password":"xxxx","role":"xxx"}
 */
@Component  
@ConfigurationProperties(prefix="userinfo") //接收application.yml中的userinfo下面的属性  
public class UserProperties {  
    private List<Map<String, String>> userlist = new ArrayList<>(); //接收userlist里面的属性值 

	public List<Map<String, String>> getUserlist() {
		return userlist;
	}


}  
