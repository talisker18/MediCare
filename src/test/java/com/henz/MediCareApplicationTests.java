package com.henz;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.henz.data_access.OrderRepository;
import com.henz.data_access.UserRepository;
import com.henz.entity.Order;
import com.henz.entity.User;
import com.henz.pages.LoginPage;
import com.henz.pages.MyMediCarePageLoggedIn;
import com.henz.pages.MyMediCarePageNotLoggedIn;
import com.henz.pages.NavBar;
import com.henz.pages.PageCollection;
import com.henz.pages.ProductPage;
import com.henz.pages.ShoppingCartPage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = MediCareApplication.class,
  webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("testing")
class MediCareApplicationTests {
	
	//pages
	@Autowired
	private PageCollection pageCollection;
	
	@Autowired
	private NavBar navBar;
	@Autowired 
	private MyMediCarePageNotLoggedIn myMediCarePageNotLoggedIn;
	@Autowired
	private LoginPage loginPage;
	@Autowired
	private ProductPage productPage;
	@Autowired
	private ShoppingCartPage shoppingCartPage;
	@Autowired
	private MyMediCarePageLoggedIn myMediCarePageLoggedIn;
	
	//inject values from application-testing.properties file
	@Value("${server.port}")
	private int serverPort;
	
	@Value("${app.url}")
	private String appUrl;
	
	@Value("${testuser.name}")
	private String userName;
	
	@Value("${testuser.pw}")
	private String password;
	
	//repositories
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserRepository userRepository;
	
	//user information
	private Long userId;
	private List<Order> orderListOfUser;
	private int sizeOfCurrentOrderList;
	
	//setup, tearDown and test methods
	@BeforeEach
	public void setup() throws IOException {
		this.pageCollection.setUpWebDriver();
	}
	
	@AfterEach
	public void tearDown() {
		this.pageCollection.quitBrowser();
	}
	
	@Test
	void contextLoads() {
		System.out.println("app url: "+this.appUrl);
		System.out.println("server port: "+this.serverPort);
	}

	/*
	 * this test will run very fast because of the use of the FluentWait class. Switch to the implicit wait method 'waitInSeconds(int seconds)' of PageCollection class if you want
	 * to enable better observation what is going on on the UI
	 * 
	 * */
	@Test
	void endToEndTest() throws InterruptedException {
		
		//open application and go to login page
		this.pageCollection.openWebsite(appUrl, serverPort);
		
		this.pageCollection.waitForElementToBePresent(this.navBar.getNavbarDropDownButtonId(), true);
		this.pageCollection.waitForElementToBePresent(this.navBar.getLinkIdToMyMediCare(), true);
		this.pageCollection.waitForElementToBePresent(this.myMediCarePageNotLoggedIn.getLoginButtonId(), true);
		
		//perform login
		this.pageCollection.waitForElementToBePresent(this.loginPage.getInputFieldUsernameId(), false);
		this.pageCollection.findElementByIdAndDoTextInput(this.loginPage.getInputFieldUsernameId(), this.userName);
		
		this.pageCollection.waitForElementToBePresent(this.loginPage.getInputFieldPasswordId(), false);
		this.pageCollection.findElementByIdAndDoTextInput(this.loginPage.getInputFieldPasswordId(), this.password);
		
		this.pageCollection.waitForElementToBePresent(this.loginPage.getLoginButtonId(), true);
		
		this.pageCollection.waitInSeconds(5);
		
		assertThat(this.pageCollection.getCurrentPageTitle()).isEqualTo("Home");
		
		//get the logged in user and get all orders of that user
		
		User user = this.userRepository.findByUsername(this.userName);
		this.userId = user.getId();
		this.orderListOfUser = this.orderRepository.getAllOrdersByUserId(this.userId).get();
		this.sizeOfCurrentOrderList = this.orderListOfUser.size();
		
		//go to product page
		this.pageCollection.waitForElementToBePresent(this.navBar.getLinkToAllProductsUserViewId(), true);
			
		//put the first 2 items into shopping cart
		this.productPage.initListWithButtonIds();
		
		this.pageCollection.waitForElementToBePresent(this.productPage.getButtonIdsToAddProductToShoppingCart().get(0), true);
		
		//handle the alert box
		Alert alert = this.pageCollection.getWebDriver().switchTo().alert();
		alert.accept();
		this.pageCollection.waitInSeconds(3);
		
		this.pageCollection.waitForElementToBePresent(this.productPage.getButtonIdsToAddProductToShoppingCart().get(2), true); //product with id 3 is on second place on UI
		
		//handle the alert box
		alert = this.pageCollection.getWebDriver().switchTo().alert();
		alert.accept();
		this.pageCollection.waitInSeconds(3);
		
		//go to shopping cart and submit the order
		this.pageCollection.waitForElementToBePresent(this.navBar.getNavbarDropDownButtonId(), true);
		this.pageCollection.waitForElementToBePresent(this.navBar.getShoppingCartLinkId(), true);
		
		this.pageCollection.waitForElementToBePresent(this.shoppingCartPage.getSubmitOrderId(), true);
		
		this.pageCollection.waitInSeconds(5);
		assertThat(this.pageCollection.getCurrentPageTitle()).isEqualTo("Order Confirmation");
		
		//check if new order was saved
		this.orderListOfUser = this.orderRepository.getAllOrdersByUserId(this.userId).get();
		assertThat(this.orderListOfUser.size()).isEqualTo(this.sizeOfCurrentOrderList+1);
		
		//do logout
		this.pageCollection.waitForElementToBePresent(this.navBar.getNavbarDropDownButtonId(), true);
		this.pageCollection.waitForElementToBePresent(this.navBar.getLinkIdToMyMediCare(), true);
		this.pageCollection.waitForElementToBePresent(this.myMediCarePageLoggedIn.getLogoutButtonId(), true);
		this.pageCollection.waitInSeconds(5);
		
		assertThat(this.pageCollection.getCurrentPageTitle()).isEqualTo("Login");
		assertThat(this.pageCollection.findElementByIdAndReturnItsContent(this.loginPage.getLoggedOutMessageId())).isEqualTo("You have been logged out");
	}
}
