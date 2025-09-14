package com.nobroker.pages;

import java.awt.AWTException;
import java.awt.Robot;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class BasePage {
    // Core Selenium objects
    WebDriver driver;
    WebDriverWait wait;
    Actions action;
    Robot robot;
    JavascriptExecutor jsExecutor;

    // Common page elements
    @FindBy(id = "alertMessageBox")
    WebElement errorToast;
    
    @FindBy(id = "getOwnerDetails")
	WebElement getOwnerDetailsBtn;
	
	@FindBy(id = "signUpSubmit")
	WebElement signUpSubmitBtn;
	


    // Constructor: initializes driver, actions, robot, jsExecutor, and page elements
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.action = new Actions(driver);
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        this.jsExecutor = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // Returns error message from alert box if present, else null
    public String checkError() {
        try {
            waitUntilVisibility(errorToast, 5);
            return errorToast.getText();
        } catch (Exception e) {
            return null;
        }
    }

    // Scrolls the given element into view
    public void scrollIntoView(WebElement webElement) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true)", webElement);
    }
    
    public void clickUsingJs(WebElement webElement) {
    	jsExecutor.executeScript("arguments[0].click();", webElement);
    }

    // Waits until the element is visible
    public void waitUntilVisibility(WebElement webElement, int sec) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(sec));
        wait.until(ExpectedConditions.visibilityOf(webElement));
    }

    // Waits until the element is clickable
    public void waitUntilClick(WebElement webElement, int sec) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(sec));
        wait.until(ExpectedConditions.elementToBeClickable(webElement));
    }

    // Waits until all elements matching the locator are visible
    public void waitUntilLoad(String locator, int sec) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(sec));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
    }
    
    public void waitUntilInvisibile(WebElement webElement,int sec) {
    	wait = new WebDriverWait(driver,Duration.ofSeconds(sec));
    	wait.until(ExpectedConditions.invisibilityOf(webElement));
    }
    
    public void waitUntilDisabled(WebElement webElement,int sec) {
    	wait = new WebDriverWait(driver,Duration.ofSeconds(sec));
    	wait.until(ExpectedConditions.attributeContains(webElement,"disabled",""));
    }
    
}
