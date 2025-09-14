package com.nobroker.test;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.nobroker.pages.HomePage;
import com.nobroker.pages.PropertyDetailsPage;
import com.nobroker.pages.PropertyListPage;
import com.nobroker.setup.BaseSteps;
import com.nobroker.utils.ExtentManager;
import com.nobroker.parameter.ExcelReader;

import java.lang.reflect.Method;


// TestNG test class for NoBroker property search and filter scenarios
public class Profile {


    // ===================== Test Context Fields =====================
    WebDriver driver;
    HomePage homePage;
    PropertyListPage propertyListPage;
    PropertyDetailsPage propertyDetailsPage;
    ExtentManager extentManager;


    // ===================== Setup & Teardown =====================
    // Initializes the extent report before all tests
    @BeforeClass
    public void setupReport() {
        extentManager = new ExtentManager(); 
        extentManager.reportGeneration();
    }


    // Initializes WebDriver and page objects before each test
    @BeforeMethod
    public void initTest(Method method) {
        driver = BaseSteps.edgeDriver();
        extentManager.driver = driver;
        extentManager.reportCreation(method);
        driver.get("https://www.nobroker.in");
    }



    /**
     * ----------------- TestCase 1 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Validates property search with valid locality 'airoli'.
     */
    @Test(priority = 0, dataProvider = "validInputData", description = "Validates property search with valid city/locality")
    public void validInputTest(String city, String locality) throws InterruptedException {
        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity(city);
        homePage.enterArea(locality);
        homePage.selectLocality();
        homePage.clickSearch();
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        boolean result = propertyListPage.areLocalitiesPresentInCards(locality);
        Assert.assertTrue(result, "[TestCase 1] Expected: Locality '" + locality + "' should be present in property cards. Actual: Not found.");
    }



    /**
     * ----------------- TestCase 2 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Checks error message for invalid locality input 'airoli mumbai' in Pune.
     */
    @Test(priority = 1, dataProvider = "invalidInputData", description = "Checks error message for invalid city/locality input")
    public void invalidInputSearch(String city, String locality) {
        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity(city);
        homePage.enterArea(locality);
        homePage.selectLocality();
        homePage.clickSearch();
        String error = homePage.checkError();
        Assert.assertNotNull(error, "[TestCase 2] Expected: Error message for invalid locality input. Actual: No error message found.");
    }

    
    
    /**
     * ----------------- TestCase 3 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Validates search with multiple localities: airoli, rabale, ghansoli.
     */
    @Test(priority = 2,  dataProvider = "multiLocalityData", description = "Validates search with multiple localities")
    public void validSearchWithLocalities(String city, String[] localities) throws InterruptedException {
        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity(city);
        homePage.enterArea(localities[0]);
        homePage.selectLocality();
        homePage.clickSearch();
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        for (int i = 1; i < localities.length; i++) {
            propertyListPage.enterLocality(localities[i]);
            propertyListPage.selectLocality();
        }
        propertyListPage.clickSearch();
        boolean result = propertyListPage.areLocalitiesPresentInCards(localities);
        Assert.assertTrue(result, "[TestCase 3] Expected: All cards should match at least one of the searched localities. Actual: Some cards do not match.");
    }



    /**
     * ----------------- TestCase 4 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Tests error when adding 4 localities in search.
     */
    @Test(priority = 3, dataProvider = "fourLocalityData", description = "Tests error when adding 4 localities in search")
    public void validSearchWith4Localities(String city, String baseLocality, String[] additionalLocalities) {
        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity(city);
        homePage.enterArea(baseLocality);
        homePage.selectLocality();
        homePage.clickSearch();
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        for (int i = 0; i < additionalLocalities.length; i++) {
            propertyListPage.enterLocality(additionalLocalities[i]);
            if (i == 2) {
                String error = homePage.checkError();
                Assert.assertNotNull(error, "[TestCase 4] Expected: Error message when adding 4 localities. Actual: No error message found.");
                return;
            }
            propertyListPage.selectLocality();
        }
    }


    /**
     * ----------------- TestCase 5 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Validates filter functionality on property search.
     */
    @Test(priority = 4, dataProvider = "filterDataProvider", dataProviderClass = ExcelReader.class, description = "Validates filter functionality on property search (DP)")
    public void validFilters(String bhk, int minPrice, int maxPrice, String availability, String tenant, String propertyType, String furnishing, String parking) throws InterruptedException {
        String[] localities = {"airoli", "rabale", "ghansoli"};
        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity("Mumbai");
        homePage.enterArea(localities[0]);
        homePage.selectLocality();
        homePage.clickSearch();
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.applyFilter(bhk, minPrice, maxPrice, availability, tenant, propertyType, furnishing, parking);
        boolean flag = propertyListPage.validateFilter(bhk, minPrice, maxPrice, availability, tenant, propertyType, furnishing, parking);
        Assert.assertTrue(flag, "[TestCase 5] Expected: All filters should be applied and validated on the first property card. Actual: Validation failed for: " + bhk + ", " + minPrice + ", " + maxPrice + ", " + availability + ", " + tenant + ", " + propertyType + ", " + furnishing + ", " + parking);
    }
    
    /**
     * ----------------- TestCase 6 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Resets filters and checks if filters are cleared.
     */
    @Test(priority = 5, dataProvider = "filterDataProvider", dataProviderClass = ExcelReader.class, description = "Resets filters and checks if filters are cleared (DP)")
    public void resetFilters(String bhk, int minPrice, int maxPrice, String availability, String tenant, String propertyType, String furnishing, String parking) throws InterruptedException {
        String[] localities = {"airoli"};
        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity("Mumbai");
        homePage.enterArea(localities[0]);
        homePage.selectLocality();
        homePage.clickSearch();
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.applyFilter(bhk, minPrice, maxPrice, availability, tenant, propertyType, furnishing, parking);
        boolean flag = propertyListPage.resetFilter();
        Assert.assertTrue(flag, "[TestCase 6] Expected: Filters should be reset and cleared. Actual: Filters still applied for: " + bhk + ", " + minPrice + ", " + maxPrice + ", " + availability + ", " + tenant + ", " + propertyType + ", " + furnishing + ", " + parking);
    }
    
    /**
     * ----------------- TestCase 7 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Attempts to get owner details without login, expects sign-up popup.
     */
    @Test(priority = 6, description = "Attempts to get owner details without login, expects sign-up popup.")
    public void getOwnerDetailsWithoutLogin() throws InterruptedException {
    	String[] localities = {"airoli"};

        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity("Mumbai");
        homePage.enterArea(localities[0]);
        homePage.selectLocality();
        homePage.clickSearch();
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.openFirstProperty();
    	BaseSteps.changeTab();
    	propertyDetailsPage = new PropertyDetailsPage(driver);
    	propertyDetailsPage.clickToGetDetails();
    	
    	boolean flag = propertyDetailsPage.checkSignUpPopup();
        Assert.assertTrue(flag, "[TestCase 7] Expected: Sign-up popup should appear when not logged in. Actual: Popup did not appear.");
    	
    }
    
    /**
     * ----------------- TestCase 8 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Attempts to get owner details after login, expects contacted state.
     */
    @Test(priority = 7, description = "Attempts to get owner details after login, expects contacted state.")
    public void getOwnerDetailsWithLogin() throws InterruptedException {
    	String[] localities = {"Palam"};

        homePage = new HomePage(driver);
        homePage.loginNoBroker("7982969325");
        Thread.sleep(20000);
        homePage.clickOnRent();
        homePage.selectCity("Delhi");
        homePage.enterArea(localities[0]);
        homePage.selectLocality();
        homePage.clickSearch();
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        
        propertyListPage.openFirstProperty();
    	BaseSteps.changeTab();
    	
    	propertyDetailsPage = new PropertyDetailsPage(driver);
    	propertyDetailsPage.clickToGetDetails();
    	boolean flag = propertyDetailsPage.checkContacted();
        Assert.assertTrue(flag, "[TestCase 8] Expected: Contacted state should be shown after getting owner details with login. Actual: Not contacted.");
    		
    }
    
    /**
     * ----------------- TestCase 9 ---------------------
     * Created By: Anurag
     * Reviewed By: SME
     * Description: Sorts properties by price and validates sorting.
     */
    @Test(priority = 8, description = "Sorts properties by price and validates sorting.")
    public void sortProperties() throws InterruptedException {
    	String[] localities = {"Lajpat"};
        homePage = new HomePage(driver);
        homePage.clickOnRent();
        homePage.selectCity("Delhi");
        homePage.enterArea(localities[0]);
        homePage.selectLocality();
        homePage.clickSearch();
        propertyListPage = new PropertyListPage(driver);
        propertyListPage.skipAnnoyingPopUp();
        propertyListPage.sortByPrice();
        Thread.sleep(2000);
        boolean flag = propertyListPage.validateSort();
        Assert.assertTrue(flag, "[TestCase 9] Expected: Properties should be sorted by price (low to high). Actual: Sorting failed.");
        
        	
    }
    
    
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
            {"Mumbai", new String[]{"airoli", "rabale", "ghansoli"}},
            {"Mumbai", new String[]{"thane", "mulund", "bhandup"}},
            {"Pune", new String[]{"baner", "kothrud", "wakad"}}
        };
    }

    @DataProvider(name = "fourLocalityData")
    public Object[][] getFourLocalityData() {
        return new Object[][] {
            {"Mumbai", "airoli", new String[]{"rabale", "ghansoli", "thane"}},
            {"Pune", "baner", new String[]{"kothrud", "wakad", "shivajinagar"}}
        };
    }
 



    // Cleans up after each test
    @AfterMethod
    public void tearDown(ITestResult result) {
        extentManager.reportTestCompletetion(result);
        BaseSteps.tearDown();
    }

    // Finalizes the extent report after all tests
    @AfterClass
    public void finalizeReport() {
        extentManager.reportCompletion();
    }
}
