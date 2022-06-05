package com.henz.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.henz.data_access.OrderRepository;
import com.henz.data_access.ProductRepository;
import com.henz.entity.Order;
import com.henz.entity.Product;
import com.henz.entity.ShoppingCart;
import com.henz.entity.User;
import com.henz.model.PasswordModel;
import com.henz.model.ProductCategoryModel;
import com.henz.service.EmailSenderService;
import com.henz.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ShoppingCart shoppingCart;
	
	@Autowired
	private AuthenticationFacade authenticationFacade;
	
	@Autowired
	private OrderRepository orderRepository;
	
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
	
	@RequestMapping(value = "/showAllProducts", method = RequestMethod.GET)
	public ModelAndView showAllProducts() {
		ModelAndView mv = new ModelAndView("user/user-show-products");
		List<ProductCategoryModel> listWithProducts = this.productRepository.getAllEnabledProductsByProductCategoryModel();
		mv.addObject("productList",listWithProducts);
		
		return mv;
	}
	
	@RequestMapping(value = "/addToCart", method = RequestMethod.POST)
	public String addProductToCart(@RequestParam("productId") Long productId) {
		ProductCategoryModel p = this.productRepository.findByIdAndReturnProductCategoryModel(productId);
		this.shoppingCart.addProductToShoppingCart(p);
		return "redirect:/showAllProducts";
	}
	
	@RequestMapping(value = "/shoppingCart", method = RequestMethod.GET)
	public ModelAndView showShoppingCart() {
		
		Authentication auth = this.authenticationFacade.getAuthentication();
		ModelAndView mv = new ModelAndView();
		
		if(auth == null || auth instanceof AnonymousAuthenticationToken) {
			mv.setViewName("user/shopping-cart-not-logged-in");
		}else {
			mv.setViewName("user/shopping-cart-logged-in");
		}
		
		List<ProductCategoryModel> productList = this.shoppingCart.getListOfProducts();
		mv.addObject("productList", productList);
		
		double total = 0;
		for(ProductCategoryModel p: productList) {
			total += p.getProductPrice();
		}
		
		mv.addObject("total",total);

		return mv;
	}
	
	@RequestMapping(value = "/removeFromCart", method = RequestMethod.POST)
	public String removeFromShoppingCart(@RequestParam("productId") Long productId) {
		this.shoppingCart.removeFromShoppingCart(productId);
		
		return "redirect:/shoppingCart";
	}
	
	@RequestMapping(value ="/order", method = RequestMethod.POST)
	public ModelAndView showOrderConfirmation() {
		ModelAndView mv = new ModelAndView("user/order-confirmation");

		if(this.shoppingCart.getListOfProducts().isEmpty()) {
			mv.addObject("confirmationMessage","Your shopping cart was empty. No order created!");
		}else {
			//create order
			Order o = new Order();
			User user = null;
			
			Authentication authentication = this.authenticationFacade.getAuthentication();
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
			    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			    user = userDetails.getUser();
			}
			
			o.setUser(user);
			
			List<Product> productList = this.productRepository
					.convertListWithModelObjectsToListWithProductEntities(this.shoppingCart.getListOfProducts());
			
			o.setProducts(productList);
			
			double total = 0;
			
			for(Product p: productList) {
				total += p.getPrice();
			}
			
			o.setTotal(total);
			
			this.orderRepository.saveNewOrder(o);
			mv.addObject("confirmationMessage","Thanks for your order! (Order No = "+o.getId()+")");
			
			//clear shopping cart for next order
			this.shoppingCart.emptyShoppingCart();
		}
		
		return mv;
	}
	
	@RequestMapping(value ="/showOrderlist", method = RequestMethod.GET)
	public ModelAndView showOrderList() {
		ModelAndView mv = new ModelAndView("profile-dropdown/my-medicare/order/order-list");
		
		User user = null;
		
		Authentication authentication = this.authenticationFacade.getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		    user = userDetails.getUser();
		}
		
		Optional<List<Order>> orderList = this.orderRepository.getAllOrdersByUserId(user.getId());
		mv.addObject("orderList", orderList.get());
		
		return mv;
	}
	
	@RequestMapping(value = "/showOrderDetails", method = RequestMethod.GET)
	public ModelAndView showOrderDetails(@RequestParam("orderId") Long orderId) {
		ModelAndView mv = new ModelAndView("profile-dropdown/my-medicare/order/order-details");
		Order order = this.orderRepository.findById(orderId);
		List<Product> productList = this.orderRepository.getAllProductsOfOrder(orderId);
		List<ProductCategoryModel> productModelList = 
				this.productRepository.convertListWithProductEntitiesToListWithModelObjects(productList);
		
		mv.addObject("productModelList", productModelList);
		mv.addObject("order", order);
		
		return mv;
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