package com.nobroker.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class PropertyDetailsPage extends BasePage {

	// 'Contacted' button (disabled when already contacted)

	@FindBy(id = "getOwnerDetails")
	WebElement ownerContacted;
	@FindBy(id= "payPlanType")
	WebElement subscribePlan;
	
	public PropertyDetailsPage(WebDriver driver){
		super(driver);
	}
	
    // Clicks the 'Get Owner Details' button using Actions
    public void clickToGetDetails() throws InterruptedException {
		waitUntilClick(getOwnerDetailsBtn, 5);

		action.moveToElement(getOwnerDetailsBtn);
 	    action.click(getOwnerDetailsBtn).build().perform();

//		clickUsingJs(getOwnerDetailsBtn);

	}
	
	public boolean checkContacted() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        // Wait until either 'Contacted' button is present or 'Subscribe Plan' is visible
	        wait.until(ExpectedConditions.or(
	            ExpectedConditions.presenceOfElementLocated(By.id("ownerContacted")),
	            ExpectedConditions.visibilityOf(subscribePlan)
	        ));

	        // Check if 'Contacted' button is disabled
	        if (ownerContacted.getAttribute("disabled") != null &&
	            ownerContacted.getText().equalsIgnoreCase("Contacted")) {
	            return true;
	        }

	        // Check if 'Subscribe Plan' is visible
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
//			System.out.println("Sign Up Pop Did not appear!");
			return false;
		}
	}
	

}
