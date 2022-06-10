package com.henz.pages;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Slf4j
public class PageCollection {
	
	private WebDriver webDriver;
	private Wait<WebDriver> wait;
	
	@Bean
	public NavBar getNavBar() {
		return new NavBar();
	}
	
	@Bean
	public MyMediCarePageNotLoggedIn getMyMediCarePageNotLoggedIn() {
		return new MyMediCarePageNotLoggedIn();
	}
	
	@Bean
	public LoginPage getLoginPage() {
		return new LoginPage();
	}
	
	@Bean
	public ProductPage getProductPage() {
		return new ProductPage();
	}
	
	@Bean
	public ShoppingCartPage getShoppingCartPage() {
		return new ShoppingCartPage();
	}
	
	@Bean MyMediCarePageLoggedIn getMyMediCarePageLoggedIn() {
		return new MyMediCarePageLoggedIn();
	}
	
	public void setUpWebDriver() throws IOException {
		Properties prop = new Properties();
		
		FileInputStream configPropfile = new FileInputStream("config.properties");
		prop.load(configPropfile); //load input stream into properties object
		
		String br = prop.getProperty("browser");
		
		if(br.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver", prop.getProperty("chromepath"));
			this.webDriver = new ChromeDriver();
		}
		
		//use these to test with other browsers. But the web drivers are not included in this project for these browser
		
		if(br.equals("firefox")) {
			//System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//Drivers/chromedriver.exe");
			System.setProperty("webdriver.gecko.driver", prop.getProperty("firefoxpath"));
			this.webDriver = new FirefoxDriver();
		}
		
		if(br.equals("ie")) {
			//System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//Drivers/chromedriver.exe");
			System.setProperty("webdriver.ie.driver", prop.getProperty("iepath"));
			this.webDriver = new InternetExplorerDriver();
		}
		
		this.wait = new FluentWait<WebDriver>(this.webDriver)
				.withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(1))
				.ignoring(NoSuchElementException.class);
		
		log.info("webdriver is set up");
	}
	
	public void quitBrowser() {
		this.webDriver.quit();
		log.info("webdriver quit");
	}
	
	public void openWebsite(String appUrl, int portNumber) {
		log.info("open following URL: "+appUrl+portNumber);
		this.webDriver.get(appUrl+portNumber);
		this.webDriver.manage().window().maximize();
	}
	
	public String getCurrentPageTitle() {
		String currentPageTitle = this.webDriver.getTitle();
		log.info("getting page title: "+currentPageTitle);
		return currentPageTitle;
	}
	
	//methods to interact with UI
	
	public void findElementByIdAndClickOnIt(String elementId) {
		log.info("click on element with id: "+elementId);
		this.webDriver.findElement(By.id(elementId)).click();
	}
	
	public void findElementByIdAndDoTextInput(String elementId, String input) {
		log.info("sending textinput to element with id: "+elementId);
		this.webDriver.findElement(By.id(elementId)).sendKeys(input);
	}
	
	public String findElementByIdAndReturnItsContent(String elementId) {
		log.info("find element with id: "+elementId);
		return this.webDriver.findElement(By.id(elementId)).getText();
	}
	
	//wait helper method
	
	public void waitInSeconds(int seconds) throws InterruptedException {
		log.info("wait for "+seconds+" seconds");
		Thread.sleep(seconds * 1000);
	}
	
	public void waitForElementToBePresent(String elementId, boolean clickOnIt) {
		
		log.info("waiting for element to be present: "+elementId);
		
		WebElement element = wait.until(
				webdriver -> webdriver.findElement(By.id(elementId))
		);
		
		if(clickOnIt) {
			element.click();
		}
	}

}
