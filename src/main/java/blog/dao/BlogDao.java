package blog.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BlogDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	

	/***
	 * The following two methods are used to extract information from the database that will be later delivered to present in 
	 * the home page.
	 * @return
	 */
	public long calTotalRow() {
        String query0 = "select count(*) as cnt from BlogCollection";
        Map<String, Object> totalrows = jdbcTemplate.queryForMap(query0);
		return (long)totalrows.get("cnt");
	}

	public List<Map<String, Object>> findData(int start, int end) {
        String query = String.format("select * from BlogCollection limit %d,%d",start,end);
        return jdbcTemplate.queryForList(query);
	}
	
	/***
	 * As opposed to the above two methods, these two methods are created to help searching for data to be presented as search result.
	 * @param keyword: this is the keyword input
	 * @return
	 */

	public long calTotalRow(String keyword) {
		String query_totalrows = String.format("select count(*) as cnt from BlogCollection where (title like '%%%s%%') or (content like '%%%s%%') ",keyword,keyword);
        Map<String, Object> totalrows = jdbcTemplate.queryForMap(query_totalrows);
        return (long) totalrows.get("cnt");

	}

	public List<Map<String, Object>> findData(int start, int end, String keyword) {
        String query_data = String.format("select * from BlogCollection where (title like '%%%s%%') or (content like '%%%s%%') limit %d,%d ",keyword,keyword,start,end);
		return jdbcTemplate.queryForList(query_data);
	}

	public void insertToDatabase(String title, String content) {
        String query = "insert into BlogCollection(CreateDate,Title,Content) values(CURRENT_TIMESTAMP,?,?)";
        jdbcTemplate.update(query, title,content);
		
	}

}
