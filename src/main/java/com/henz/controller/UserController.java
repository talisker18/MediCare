package com.henz.controller;

import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.henz.entity.User;
import com.henz.model.PasswordModel;
import com.henz.service.EmailSenderService;
import com.henz.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@RequestMapping(value = "/resetPasswordForm", method = RequestMethod.GET)
	public ModelAndView resetPassword() {
		PasswordModel passwordModel = new PasswordModel();
		ModelAndView mv = new ModelAndView("profile-dropdown/my-medicare/password-reset/reset-password-form");
		mv.addObject("passwordModel",passwordModel);
		return mv;
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public ModelAndView resetPassword(@ModelAttribute("passwordModel") PasswordModel passwordModel, HttpServletRequest request) {
		User user = this.userService.findUserByEmail(passwordModel.getEmail());
		if(user != null) {
			String token = UUID.randomUUID().toString();
			this.userService.createPasswordResetTokenForUser(user, token);
			passwordResetTokenMail(user, this.getApplicationUrl(request), token);
		}
		
		ModelAndView mv = new ModelAndView("profile-dropdown/my-medicare/password-reset/reset-password-confirm-sent-mail");
		mv.addObject("passwordModel",passwordModel);
		return mv;
	}
	
	@RequestMapping(value="/verifyPasswordReset", method = RequestMethod.GET)
	public ModelAndView verifyPasswordReset(@RequestParam("token") String token) {
		String result = this.userService.validatePasswordResetToken(token);
		
		if(result.equalsIgnoreCase("invalid token") || result.equalsIgnoreCase("expired token")) {
			ModelAndView mvInvalid = new ModelAndView("profile-dropdown/my-medicare/password-reset/verify-pw-resettoken-nok");
			mvInvalid.addObject("result",result);
			return mvInvalid;
		}else {
			ModelAndView mvValid = new ModelAndView("profile-dropdown/my-medicare/password-reset/verify-pw-resettoken-ok");
			mvValid.addObject("token",token);
			PasswordModel passwordModel = new PasswordModel();
			mvValid.addObject("passwordModel",passwordModel);
			return mvValid;
		}
	}
	
	@RequestMapping(value="/savePassword", method = RequestMethod.POST)
	public ModelAndView saveNewPassword(@RequestParam("token") String token, @ModelAttribute PasswordModel passwordModel) {
		//update the password
		Optional<User> user = this.userService.getUserByPasswordResetToken(token);
		if(user.isPresent()) {
			this.userService.changePassword(user.get(), passwordModel.getNewPassword());
			ModelAndView mvValid = new ModelAndView("profile-dropdown/my-medicare/password-reset/pw-reset-ok");
			return mvValid;
		}else {
			ModelAndView mvUserNotFound = new ModelAndView("profile-dropdown/my-medicare/password-reset/pw-reset-nok");
			return mvUserNotFound;
		}
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
	
	private void passwordResetTokenMail(User user, String applicationUrl, String token) {
		String url = applicationUrl+"/verifyPasswordReset?token="+token;
		this.emailSenderService.sendSimpleEmail(user.getEmail(), "Click on the following link to change your password: "+url, "Reset your password for 'Kitchen Story'");
	}
}