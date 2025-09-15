package com.nobroker.setup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.*;



public class BaseSteps {
	public static WebDriver driver;
	public static ChromeOptions chromeOptions;
	public static EdgeOptions edgeOptions;
	public static FirefoxOptions firefoxOptions;
	
	public static WebDriver firefoxDriver() {
	    firefoxOptions = new FirefoxOptions();
	    firefoxOptions.addArguments("-private"); 
	    firefoxOptions.addPreference("dom.webnotifications.enabled", false);
	    firefoxOptions.addPreference("permissions.default.desktop-notification", 2);
	    driver = new FirefoxDriver(firefoxOptions);
	    return driver;
	}

	public static WebDriver chromeDriver() {
	    chromeOptions = new ChromeOptions();
	    chromeOptions.addArguments(Arrays.asList(
	        "--start-maximized",
	        "incognito",
	        "--disable-notifications",
	        "--disable-popup-blocking",
	        "--deny-permission-prompt",
	        "disable-infobars",
	        "disable-extensions"
	    ));
	    driver = new ChromeDriver(chromeOptions);
	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	    return driver;
	}

	public static WebDriver edgeDriver() {
	    edgeOptions = new EdgeOptions();
	    edgeOptions.addArguments(Arrays.asList(
	        "--start-maximized",
	        "--inprivate",
	        "--disable-notifications",
	        "--disable-popup-blocking",
	        "--deny-permission-prompt",
	        "disable-infobars",
	        "disable-extensions"   
	    ));
	    driver = new EdgeDriver(edgeOptions);
	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	    return driver;
	}
	
	public static void changeTab() {
		List<String> handles = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(handles.get(1));
	}
	public static void changeTabBack() {
		List<String> handles = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(handles.get(0));
	}
	
	public static void tearDown() {
		driver.quit();
	}
	

}
