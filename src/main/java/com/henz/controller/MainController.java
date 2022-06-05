package com.henz.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.henz.auth.AuthenticationFacade;
import com.henz.auth.CustomUserDetails;
import com.henz.data_access.ProductRepository;
import com.henz.entity.User;
import com.henz.entity.VerificationToken;
import com.henz.event.registration.RegistrationCompleteEvent;
import com.henz.model.ProductCategoryModel;
import com.henz.model.UserModel;
import com.henz.service.EmailSenderService;
import com.henz.service.UserService;

@Controller
public class MainController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
	private AuthenticationFacade authenticationFacade;
	
	@Autowired
	private ProductRepository productRepository;
	
	@RequestMapping(value={"","/"}, method = RequestMethod.GET)
	public ModelAndView showHomePage() {
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("home");
		
		//get the top 5 sellers
		List<ProductCategoryModel> productList = this.productRepository.getTopFiveSellersAndReturnProductCategoryModel();
		
		mv.addObject("productList",productList);
		
		//get the newest product
		ProductCategoryModel newestProduct = this.productRepository.findNewestProductAndReturnProductCategoryModel();
		
		List<ProductCategoryModel> productListNewest = new ArrayList<ProductCategoryModel>();
		productListNewest.add(newestProduct);
		
		mv.addObject("productListNewest",productListNewest);
		
		return mv;
	}
	
	@RequestMapping(value={"/support"}, method = RequestMethod.GET)
	public ModelAndView showSupportPage() {
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("profile-dropdown/support");
		
		return mv;
	}

	@RequestMapping(value={"/myMedicare"}, method = RequestMethod.GET)
	public ModelAndView showMyMedicarePage() {
		
		Authentication auth = this.authenticationFacade.getAuthentication();
		ModelAndView mv = new ModelAndView();
		
		if(auth == null || auth instanceof AnonymousAuthenticationToken) {
			mv.setViewName("profile-dropdown/my-medicare/myMedicare-not-logged-in");
			return mv;
		}else {
			//get the current user 
			mv.setViewName("profile-dropdown/my-medicare/myMedicare-logged-in");
		    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
		    mv.addObject("user",userDetails.getUser());
			
			return mv;
		}
	}
	
	@RequestMapping(value="/register", method = RequestMethod.GET)
	public ModelAndView showRegisterForm() {
		
		Authentication auth = this.authenticationFacade.getAuthentication();
		ModelAndView mv = new ModelAndView();
		
		if(auth == null || auth instanceof AnonymousAuthenticationToken) {
			UserModel userModel = new UserModel();
			mv.addObject("userModel",userModel);
			mv.setViewName("profile-dropdown/registration/registration-form");
			
			return mv;
		}else {
			mv = new ModelAndView("redirect:/");
			
			return mv;
		}
	}
	
	@RequestMapping(value="/registerConfirmation", method = RequestMethod.POST)
	public ModelAndView registerUser(@ModelAttribute UserModel userModel, HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView();
		
		//generate user entity and publish registration event
		User user = this.userService.registerUser(userModel);
		
		if(user != null) {
			this.publisher.publishEvent(
					new RegistrationCompleteEvent(user, this.getApplicationUrl(request)));
			
			//return the view
			mv.setViewName("profile-dropdown/registration/result/registration-confirmation");
		}else {
			//user with this username or email already exists
			mv.setViewName("profile-dropdown/registration/result/user-already-exists.html");
		}
		
		return mv;
	}
	
	@RequestMapping(value="/verifyRegistration", method = RequestMethod.GET)
	public ModelAndView verifyRegistration(@RequestParam("token") String token) {
		
		Authentication auth = this.authenticationFacade.getAuthentication();
		ModelAndView mv = new ModelAndView();
		
		if(auth == null || auth instanceof AnonymousAuthenticationToken) {
			String result = this.userService.validateVerificationToken(token);
			
			if(result.equalsIgnoreCase("valid")) {
				ModelAndView mvValid = new ModelAndView("profile-dropdown/registration/verification/verify-registration-ok");
				return mvValid;
			}else if(result.equalsIgnoreCase("invalid token")){
				ModelAndView mvInvalid = new ModelAndView("profile-dropdown/registration/verification/verify-registration-nok-invalid");
				mvInvalid.addObject("result",result);
				return mvInvalid;
			}else if(result.equalsIgnoreCase("expired token")) {
				//user has the ability to re-generate a token
				ModelAndView mvExpired = new ModelAndView("profile-dropdown/registration/verification/verify-registration-nok-expired");
				mvExpired.addObject("result",result);
				//add the verification token so the user can re-generate a token
				mvExpired.addObject("oldVerificationToken",token);
				return mvExpired;
			}
			
			return null;
			
		}else {
			mv = new ModelAndView("redirect:/");
			return mv;
		}
	}
	
	@RequestMapping(value="/resendVerificationToken", method = RequestMethod.GET)
	public ModelAndView resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
		
		Authentication auth = this.authenticationFacade.getAuthentication();
		ModelAndView mv = new ModelAndView();
		
		if(auth == null || auth instanceof AnonymousAuthenticationToken) {
			VerificationToken newToken = this.userService.generateNewVerificationToken(oldToken);
			User user = newToken.getUser();
			resendVerificationTokenMail(user, this.getApplicationUrl(request), newToken);
			
			mv = new ModelAndView("profile-dropdown/registration/verification/resend-verification-token");
			return mv;
			
		}else {
			mv = new ModelAndView("redirect:/");
			return mv;
		}
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginPage() {
		Authentication auth = this.authenticationFacade.getAuthentication();
		
		if(auth == null || auth instanceof AnonymousAuthenticationToken) {
			return "profile-dropdown/login/login";
		}
		
		return "redirect:/";
	}
	
	/*
	 * methods
	 * 
	 * 
	 * */
	
	private String getApplicationUrl(HttpServletRequest request) {
		return "http://" + 
				request.getServerName() +
				":" + 
				request.getServerPort() + 
				request.getContextPath();
	}
	
	private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
		String url = applicationUrl+"/verifyRegistration?token="+verificationToken.getToken();
		this.emailSenderService.sendSimpleEmail(user.getEmail(), "Thanks for your registration at 'Kitchen Story'. Please open the following URL to finish your registration: "+url, "Registration for 'Kitchen Story'");
	}
}
