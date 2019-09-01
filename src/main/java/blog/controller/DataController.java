package blog.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

/**
 * This is an API used to provide data for external use.
 * @author xumingkuan
 *
 */
@RestController
public class DataController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/api/search/{key}")
	public String data(@PathVariable("key") String keyword) {
		String query = String.format("select * from BlogCollection where (title like '%%%s%%') or (content like '%%%s%%') ",keyword,keyword);
        List<Map<String, Object>> data = jdbcTemplate.queryForList(query);
		return JSON.toJSONString(data) ;
	}
}