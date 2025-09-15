package com.nobroker.pages;



import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;


public class HomePage extends BasePage {
	// Locators as constants
	private static final String CITY_XPATH = "//div[contains(@class,'nb-select__menu-list')]/div[contains(text(),'%s')]";
	// Page elements
	@FindBy (xpath="//div[contains(text(),'Log in')]")
	WebElement loginBtn;
	
	@FindBy(id="signUp-phoneNumber")
	WebElement phoneNoInput;
	
	@FindBy(xpath = "//div[@id='app']/div/div/div[2]/div[3]/div[2]")
	WebElement rentBtn;

	@FindBy(css = "div.prop-search-city-selector ~ button ")
	WebElement searchBtn;

	@FindBy(id = "listPageSearchLocality")
	WebElement inputLocal;

	@FindBy(id = "searchCity")
	WebElement cityDropdown;

	@FindBy(id = "autocomplete-dropdown-container")
	WebElement selectArea;

	// Constructor
	public HomePage(WebDriver driver) {
		super(driver);
	}
	
	public void loginNoBroker(String phoneNo) {
		loginBtn.click();
		waitUntilClick(phoneNoInput, 5);
		phoneNoInput.click();
		phoneNoInput.sendKeys(phoneNo);
//		signUpSubmitBtn.click();		
	}

	// Clicks the Rent button
	public void clickOnRent() {
		rentBtn.click();
	}

	// Selects a city from the dropdown
	// Selects a city from the dropdown
	public void selectCity(String city) {
		String cityXpath = String.format(CITY_XPATH, city);
		cityDropdown.click();
		WebElement selectCityFromMenu = driver.findElement(By.xpath(cityXpath));
		waitUntilClick(selectCityFromMenu, 5);
		selectCityFromMenu.click();
	}

	// Enters the area/locality in the input box
	public void enterArea(String area) {
		if (area == null || area.trim().isEmpty()) return;
		action = new Actions(driver);
		action.click(inputLocal).perform();
		inputLocal.sendKeys(area);
	}

	// Selects the first locality suggestion from the dropdown
	public void selectLocality() {
		try {
			waitUntilClick(selectArea, 5);
			action.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		} catch (Exception e) {
			return;
		}
	}

	// Clicks the Search button
	public void clickSearch() throws InterruptedException {
		Thread.sleep(1000);
		searchBtn.click();
	}
}
