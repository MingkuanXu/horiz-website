package blog.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import blog.dao.BlogDao;

@Service
public class BlogService {
	
	
	private final int NUMBER_ACTIVITIES_EACH_PAGE = 6;

	@Autowired
	BlogDao blogDao;
	
	/***
	 * Used to add page information and content to the model delivered to the home page
	 * @param model
	 * @param page
	 * @return
	 */
	public Model updateModel(Model model, int page) {
		
		long totalpage = blogDao.calTotalRow()/NUMBER_ACTIVITIES_EACH_PAGE;
		
        int start = (page-1)*NUMBER_ACTIVITIES_EACH_PAGE;
        int end = NUMBER_ACTIVITIES_EACH_PAGE;
        
        List<Map<String, Object>> rlist = blogDao.findData(start,end);
        
        model.addAttribute("rlist", rlist);    
        model.addAttribute("totalpage", totalpage);  
        model.addAttribute("pageno",page);
	
        return model;
	}
	
	/***
	 * Used to add page information and content to the model delivered to the result page
	 * @param model
	 * @param page: page number 
	 * @param keyword: search keyword provided by the user
	 * @return
	 */

	public Model updateModel(Model model, int page, String keyword) {
		
		long totalpage = blogDao.calTotalRow(keyword)/NUMBER_ACTIVITIES_EACH_PAGE;
 
        int start = (page-1)*NUMBER_ACTIVITIES_EACH_PAGE;
        int end = NUMBER_ACTIVITIES_EACH_PAGE;
        
        List<Map<String, Object>> data = blogDao.findData(start,end,keyword);    		
        
        model.addAttribute("pageno",page);
        model.addAttribute("totalpage", totalpage); 
        model.addAttribute("deliver", data);
        model.addAttribute("keyword", keyword);
        
        return model;
	}

	/***
	 * Used to get insertion request from the controller and deliver data to be inserted to the DAO level.
	 * @param title
	 * @param content
	 */
	public void insert(String name, String category) {
		blogDao.insertToDatabase(name,category);
		
	}
}
