package spring.mvc.session13.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import spring.mvc.session13.entity.Employee;
import spring.mvc.session13.repository.EmployeeDao;

@Controller
@RequestMapping("/jdbc/employee")
public class EmployeeController {

	@Autowired
	private EmployeeDao employeeDao;

	private int getCountPage() {
		int pageCount = (int)Math.ceil((double)employeeDao.getCount()/employeeDao.LIMIT); 
		return pageCount;
	}
	
	@GetMapping("/")
	public String index(Model model , @ModelAttribute Employee employee) {
		model.addAttribute("_method" , "POST");
		model.addAttribute("employees" , employeeDao.query());
		model.addAttribute("pageCount" , getCountPage());
		return "session13/employee";
	}
		
	@GetMapping("/{eid}")
	public String get(@PathVariable("eid") Integer eid , Model model , HttpSession session) {
		model.addAttribute("_method" , "PUT");
		model.addAttribute("employees" , employeeDao.query(session.getAttribute("offset_emp")));
		model.addAttribute("employee" , employeeDao.get(eid));
		model.addAttribute("pageCount" , getCountPage());
		return "session13/employee";
	}
	
	@GetMapping("/page/{num}")
	public String page(@PathVariable("num") Integer num , Model model ,HttpSession httpSession , @ModelAttribute Employee employee) {
		int offset = (num-1) * EmployeeDao.LIMIT;
		System.out.println(offset);
		if(num >=0) {
			httpSession.setAttribute("offset_emp", offset);
		}else {
			httpSession.removeAttribute("offset_emp");
		}
		model.addAttribute("_method" , "POST");
		model.addAttribute("employees" , employeeDao.queryPage(offset));
		model.addAttribute("pageCount" , getCountPage());
		return "session13/employee";
	}
	
	@PostMapping("/")
	public String add(Model model , @Valid Employee employee , BindingResult result) {
		if(result.hasErrors()) {
			model.addAttribute("_method" , "POST");
			model.addAttribute("employees" , employeeDao.query());
			model.addAttribute("pageCount" , getCountPage());
			return "session13/employee";
		}
		    employeeDao.add(employee);
		    return "redirect:./";
	}
	
	@PutMapping("/")
	public String update(Model model , @Valid Employee employee , BindingResult result) {
		if(result.hasErrors()) {
			model.addAttribute("_method" , "PUT");
			model.addAttribute("employees" , employeeDao.query());
			model.addAttribute("employee" , employeeDao.get(employee.getEid()));
			model.addAttribute("pageCount" , getCountPage());
			return "session13/employee";
		}
		    employeeDao.update(employee);
		    return "redirect:./";
	}
	
	@DeleteMapping("/")
	public String delete(Model model , Employee employee ) {
		try {
			employeeDao.delete(employee.getEid());
			return "redirect:./";
		} catch (Exception e) {
			model.addAttribute("_method" , "PUT");
			model.addAttribute("employees" , employeeDao.query());
			model.addAttribute("employee" , employeeDao.get(employee.getEid()));
			model.addAttribute("pageCount" , getCountPage());
			model.addAttribute("message" , "此員工尚有工作");
			return "session13/employee";
		}
	}
	
}
