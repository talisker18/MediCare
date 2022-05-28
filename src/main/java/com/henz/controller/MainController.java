package com.henz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	
	@RequestMapping(value={"","/"}, method = RequestMethod.GET)
	public ModelAndView showHomePage() {
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("home");
		
		return mv;
	}
	
	@RequestMapping(value={"support"}, method = RequestMethod.GET)
	public ModelAndView showSupportPage() {
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("profile-dropdown/support");
		
		return mv;
	}

	@RequestMapping(value={"myMedicare"}, method = RequestMethod.GET)
	public ModelAndView showMyMedicarePage() {
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("profile-dropdown/MyMedicare");
		
		return mv;
	}
}
