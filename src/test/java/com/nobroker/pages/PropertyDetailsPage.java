package com.nobroker.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class PropertyDetailsPage extends BasePage {

    // Page Web WebElements
	@FindBy(id = "getOwnerDetails")
	WebElement ownerContacted;
	@FindBy(id= "payPlanType")
	WebElement subscribePlan;
	
	public PropertyDetailsPage(WebDriver driver){
		super(driver);
	}
	
    // Clicks the 'Get Owner Details' button 
    public void clickToGetDetails() {
		waitUntilClick(getOwnerDetailsBtn, 5);

		action.moveToElement(getOwnerDetailsBtn);
 	    action.click(getOwnerDetailsBtn).build().perform();

 	    //clickUsingJs(getOwnerDetailsBtn);

	}
	
    //check if already contacted or prompt to subscribe appear
	public boolean checkContacted() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        wait.until(ExpectedConditions.or(
	            ExpectedConditions.presenceOfElementLocated(By.id("ownerContacted")),
	            ExpectedConditions.visibilityOf(subscribePlan)
	        ));


	        if (!ownerContacted.isEnabled() &&
	            ownerContacted.getText().equalsIgnoreCase("Contacted")) {
	            return true;
	        }
	        if (subscribePlan.isDisplayed()) {
	            return true;
	        }

	    } catch (Exception e) {
	        System.out.println("Neither 'Contacted' nor 'Subscribe Plan' appeared.");
	    }

	    return false;
	}


	
    // Checks if the sign-up popup is present
    public boolean checkSignUpPopup() {
		try {
			waitUntilClick(signUpSubmitBtn, 5);
			return true;
		}catch(Exception e) {
			//System.out.println("Sign Up Pop Did not appear!");
			return false;
		}
	}
	

}