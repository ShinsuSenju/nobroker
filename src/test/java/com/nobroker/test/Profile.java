package com.nobroker.test;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.nobroker.listeners.RetryFailed;
import com.nobroker.pages.HomePage;
import com.nobroker.pages.PropertyDetailsPage;
import com.nobroker.pages.PropertyListPage;
import com.nobroker.setup.BaseSteps;
import com.nobroker.utils.ExtentManager;
import com.nobroker.parameter.ExcelReader;
import com.nobroker.parameter.PropertyReader;

import java.lang.reflect.Method;

public class Profile {

    WebDriver driver;
    HomePage homePage;
    PropertyListPage propertyListPage;
    PropertyDetailsPage propertyDetailsPage;
    ExtentManager extentManager;

    String city;
    String locality1;
    String locality2;
    String locality3;

    // Setup driver, reporting, and test data before each test
    
    @BeforeClass
    public void setupReportandParameters() {
        extentManager = new ExtentManager(); 
        extentManager.reportGeneration();
        
    }
    @Parameters({"city", "locality1", "locality2", "locality3", "Browser"})
    @BeforeMethod(alwaysRun = true)
    public void setupTestContext(String city, String locality1, String locality2, String locality3, @Optional("edge") String Browser, Method method) {
    	
        this.city = city;
        this.locality1 = locality1;
        this.locality2 = locality2;
        this.locality3 = locality3;

        switch(Browser.toLowerCase()) {
            case "chrome": driver = BaseSteps.chromeDriver(); break;
            case "firefox": driver = BaseSteps.firefoxDriver(); break;
            default: driver = BaseSteps.edgeDriver();
        }
        extentManager.driver = driver;
        extentManager.reportCreation(method);
        driver.get("https://www.nobroker");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        extentManager.reportTestCompletetion(result);
        BaseSteps.tearDown();
    }

    @AfterClass(alwaysRun = true)
    public void finalizeReport() {
        extentManager.reportCompletion();
    }

    // Helper: Common search flow
   public  void performSearch(String city, String locality) throws InterruptedException {
        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity(city);
        homePage.enterArea(locality);
        homePage.selectLocality();
        homePage.clickSearch();

    }

    // Helper: Multi-locality search
    public void performMultiLocalitySearch(String city, String[] localities) throws InterruptedException {
        performSearch(city, localities[0]);
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        for (int i = 1; i < localities.length; i++) {
            propertyListPage.enterLocality(localities[i]);
            propertyListPage.selectLocality();
        }
        propertyListPage.clickSearch();
    }

    /**
     * Test Case 1
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate search with valid input
     */
    @Test(priority = 0, dataProvider = "validInputData", description = "To validate search with valid input")
    public void validInputTest(String city, String locality) throws InterruptedException {
        performSearch(city, locality);
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        boolean result = propertyListPage.areLocalitiesPresentInCards(locality);
        extentManager.test.info("City: "+city+" Locality: "+locality);
        Assert.assertTrue(result, "[TestCase 1] Expected: Locality '" + locality + "' should be present in property cards. Actual: Not found.");
    }

    /**
     * Test Case 2
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate error message for invalid locality input
     */
    @Test(priority = 1, dataProvider = "invalidInputData", description = "To validate error message for invalid locality input")
    public void invalidInputSearch(String city, String locality) throws InterruptedException {
        performSearch(city, locality);
        String error = homePage.checkError();
        extentManager.test.info("City: "+city+" Locality: "+locality);
        Assert.assertNotNull(error, "[TestCase 2] Expected: Error message for invalid locality input. Actual: No error message found.");
    }

    /**
     * Test Case 3
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate multi-locality search
     */
    @Test(priority = 2, dataProvider = "multiLocalityData", description = "To validate multi-locality search")
    public void validSearchWithLocalities(String city, String[] localities) throws InterruptedException {
    	extentManager.test.info("City: "+city+" Locality: "+localities[0]+", "+localities[1]+", "+localities[2]);
        performMultiLocalitySearch(city, localities);
        boolean result = propertyListPage.areLocalitiesPresentInCards(localities);
        Assert.assertTrue(result, "[TestCase 3] Expected: All cards should match at least one of the searched localities. Actual: Some cards do not match.");
    }

    /**
     * Test Case 4
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate error on adding more than 3 localities
     */
    @Test(priority = 3, dataProvider = "fourLocalityData", description = "To validate error on adding more than 3 localities")
    public void validSearchWith4Localities(String city, String baseLocality, String[] additionalLocalities) throws InterruptedException {
    	extentManager.test.info("City: "+city+" Locality: "+baseLocality+", "+additionalLocalities[0]+", "+additionalLocalities[1]+", "+additionalLocalities[2]);
        performSearch(city, baseLocality);
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        for (int i = 0; i < additionalLocalities.length; i++) {
            propertyListPage.enterLocality(additionalLocalities[i]);
            Thread.sleep(2000);
            if (i == 2) {
                String error = homePage.checkError();
                Assert.assertNotNull(error, "[TestCase 4] Expected: Error message when adding 4 localities. Actual: No error message found.");
                return;
            }
            propertyListPage.selectLocality();
        }
    }

    /**
     * Test Case 5
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate filter functionality
     */
    @Test(priority = 4, dataProvider = "filterDataProvider", dataProviderClass = ExcelReader.class, description = "To validate filter functionality")
    public void validFilters(String bhk, int minPrice, int maxPrice, String availability, String tenant, String propertyType, String furnishing, String parking) throws InterruptedException {
        String[] localities = {locality1,locality2,locality3};
        performSearch(city, localities[0]);
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.applyFilter(bhk, minPrice, maxPrice, availability, tenant, propertyType, furnishing, parking);

        extentManager.test.info("City: " + city +" Localities: " + localities[0] +" Filters: " + bhk + ", " + minPrice + ", " + maxPrice + ", " +availability + ", " + tenant + ", " + propertyType + ", " +furnishing + ", " + parking);

        boolean flag = propertyListPage.validateFilter(bhk, minPrice, maxPrice, availability, tenant, propertyType, furnishing, parking);
        Assert.assertTrue(flag, "[TestCase 5] Expected: All filters should be applied and validated on the first property card. Actual: Validation failed for: " + bhk + ", " + minPrice + ", " + maxPrice + ", " + availability + ", " + tenant + ", " + propertyType + ", " + furnishing + ", " + parking);
    }

    /**
     * Test Case 6
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate reset filter functionality
     */
    @Test(priority = 5, dataProvider = "resetFilterDataProvider", dataProviderClass = ExcelReader.class, description = "To validate reset filter functionality")
    public void resetFilters(String bhk, int minPrice, int maxPrice, String availability, String tenant, String propertyType, String furnishing, String parking) throws InterruptedException {
        String[] localities = {locality1,locality2,locality3};
        performSearch(city, localities[0]);
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.applyFilter(bhk, minPrice, maxPrice, availability, tenant, propertyType, furnishing, parking);
        extentManager.test.info("City: " + city +" Localities: " + localities[0] +" Filters: " + bhk + ", " + minPrice + ", " + maxPrice + ", " +availability + ", " + tenant + ", " + propertyType + ", " +furnishing + ", " + parking);
        boolean flag = propertyListPage.resetFilter();
        Assert.assertTrue(flag, "[TestCase 6] Expected: Filters should be reset and cleared. Actual: Filters still applied for: " + bhk + ", " + minPrice + ", " + maxPrice + ", " + availability + ", " + tenant + ", " + propertyType + ", " + furnishing + ", " + parking);
    }

    /**
     * Test Case 7
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate owner details popup without login
     */
    @Test(priority = 6, description = "To validate owner details popup without login")
    public void getOwnerDetailsWithoutLogin() throws InterruptedException {
        String[] localities = {locality1,locality2,locality3};
        performSearch(city, localities[0]);
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.openFirstProperty();
        BaseSteps.changeTab();
        propertyDetailsPage = new PropertyDetailsPage(driver);
        propertyDetailsPage.clickToGetDetails();
        boolean flag = propertyDetailsPage.checkSignUpPopup();
        extentManager.test.info("City: " + city +" Localities: " + localities[0]);
        Assert.assertTrue(flag, "[TestCase 7] Expected: Sign-up popup should appear when not logged in. Actual: Popup did not appear.");
    }

    /**
     * Test Case 8
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate owner details after login
     */
    @Test(priority = 7, description = "To validate owner details after login")
    public void getOwnerDetailsWithLogin() throws InterruptedException {
        String[] localities = {locality1,locality2,locality3};
        homePage = new HomePage(driver);
        homePage.loginNoBroker(PropertyReader.getDataFromPropertyFile("mobile"));
        Thread.sleep(20000);
        performSearch(city, localities[0]);
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.openFirstProperty();
        BaseSteps.changeTab();
        propertyDetailsPage = new PropertyDetailsPage(driver);
        propertyDetailsPage.clickToGetDetails();
        boolean flag = propertyDetailsPage.checkContacted();
        extentManager.test.info("City: " + city +" Localities: " + localities[0]);
        Assert.assertTrue(flag, "[TestCase 8] Expected: Contacted state should be shown after getting owner details with login. Actual: Not contacted.");
    }

    /**
     * Test Case 9
     * Created By: Anurag
     * Reviewed By: SME
     * Motive: To validate property sorting by price
     */
    @Test(priority = 8, description = "To validate property sorting by price", dependsOnMethods = "validInputTest",retryAnalyzer = RetryFailed.class)
    public void sortProperties() throws InterruptedException {
        String[] localities = {locality1,locality2,locality3};
        performSearch(city, localities[0]);
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.sortByPrice();
        boolean flag = propertyListPage.validateSort();
        extentManager.test.info("City: " + city +" Localities: " + localities[0]);
        Assert.assertTrue(flag, "[TestCase 9] Expected: Properties should be sorted by price (low to high). Actual: Sorting failed.");
    }

    // DataProviders
    @DataProvider(name = "validInputData")
    public Object[][] getValidInputData() {
        return new Object[][] {
            {"Mumbai", "airoli"},
            {"Pune", "baner"},
            {"Bangalore", "hsr layout"}
        };
    }

    @DataProvider(name = "invalidInputData")
    public Object[][] getInvalidInputData() {
        return new Object[][] {
            {"Pune", "Mumbai airoli"},
            {"Mumbai", "bangalore adfa delhi"},
            {"Bangalore", "thane mumbai"}
        };
    }

    @DataProvider(name = "multiLocalityData")
    public Object[][] getMultiLocalityData() {
        return new Object[][] {
            {"Mumbai", new String[]{"airoli", "rabale", "thane"}},
            {"Mumbai", new String[]{"thane", "mulund", "bhandup"}},
            {"Pune", new String[]{"baner", "kothrud", "wakad"}}
        };
    }

    @DataProvider(name = "fourLocalityData")
    public Object[][] getFourLocalityData() {
        return new Object[][] {
            {"Mumbai", "airoli", new String[]{"rabale", "thane", "thane"}},
            {"Delhi", "palam", new String[]{"mahipalpur", "karol", "shivajinagar"}}
        };
    
    }
}
