package blog.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.service.BlogService;

@Controller
@RequestMapping("/")
public class BlogController {

    @Autowired
    private BlogService blogService;
    
	@GetMapping("/home")
    public String mainpage(
    		@RequestParam(name="page", required=false, defaultValue="1") int page, 
    		Model model) {
		
		model = blogService.updateModel(model,page);
        
        return "functionpage/homepage";
    }
	

	@GetMapping("/search")
	public String searchEngine(@RequestParam(name="keyword", required=false, defaultValue = "") String keyword) throws UnsupportedEncodingException {
		if(keyword.isEmpty()) return "functionpage/searchpage";  //访问搜索引擎主页
		String re = "redirect:/result?keyword="/*+keyword;*/+URLEncoder.encode(keyword, "utf-8"); //进行搜索
		return re;
	}
	
	
	
	@GetMapping("/result")
    public String result(
    		@RequestParam(name="keyword", required=true/*, defaultValue="1"*/) String keyword,
    		@RequestParam(name="page", required=false, defaultValue="1") int page,
    		Model model) {
		//String keyword =  request.getParameter("search");

		model = blogService.updateModel(model, page,keyword);

		
		return "functionpage/resultpage";

	}
	

	/***
	 * Jump to the newblog page if no "title" is detected.
	 * Insert title & content into the database if "title is not null.
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@GetMapping("/newblog")
	public String doPost(HttpServletRequest request) throws ServletException, IOException{
       

		String title =  request.getParameter("title");
		
		if(title==null) return "functionpage/newblog"; 
		
		String content = request.getParameter("content");
		blogService.insert(title,content);

		return "redirect:/";

		
	}
	
}
