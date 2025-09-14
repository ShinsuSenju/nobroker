package com.nobroker.pages;

import java.awt.event.InputEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the Property Listing Page on NoBroker.
 */
public class PropertyListPage extends BasePage {

    // ===================== Constants =====================
    private static final String XPATH_BHK_TYPE = "//div[contains(text(),'BHK Type')]/following::div[1]/div[contains(text(),'%s')]";
    private static final String XPATH_AVAILABILITY = "//div[contains(text(),'Availability')]/following::div[1]/label[contains(@for,'%s')]";
    private static final String XPATH_PREFERRED_TENANTS = "//div[contains(text(),'Preferred Tenants')]/following::label[contains(@for,'%s')]";
    private static final String XPATH_PROPERTY_TYPE = "//div[contains(text(),'Property Type')]/following::label[contains(@for,'%s')]";
    private static final String XPATH_FURNISHING = "//div[contains(text(),'Furnishing')]/following::label[contains(@for,'%s')]";
    private static final String XPATH_PARKING = "//div[contains(text(),'Parking')]/following::label[contains(@for,'%s')]";
    private static final String XPATH_PROPERTY_CARDS = "//article[contains(@id,'article')]";

    // ===================== Search Section =====================
    @FindBy(id = "loc")
    WebElement propertyLocation;

    @FindBy(id = "listPageSearchLocality")
    WebElement localityInput;

    @FindBy(id = "autocomplete-dropdown-container")
    WebElement selectArea;

    @FindBy(id = "search")
    WebElement searchBtn;

    @FindBy(id = "resetButton")
    WebElement resetFilterBtn;
    
    @FindBy(xpath="//div[contains(text(),'Filters')]/child::span")
    WebElement appliedFilterNo; // Shows number of applied filters

    @FindBy(xpath="//div[contains(text(),'NoBroker Rank')]")
    WebElement sortByInput; // Sort dropdown/button

    @FindBy(xpath="//div[contains(text(),'Rent(Low to High)')]")
    WebElement sortByPriceLow; // Option to sort by price low to high

    // ===================== Price Slider =====================
    @FindBy(className = "rc-slider-handle-1")
    WebElement priceSliderLeftBtn;

    @FindBy(className = "rc-slider-handle-2")
    WebElement priceSliderRightBtn;

    @FindBy(xpath = "//div[contains(text(),'BHK Type')]/following::div[8]")
    WebElement priceRange;

    // ===================== First Property Card Elements =====================
    @FindBy(xpath = "(//article)[1]/descendant::a[1]")
    WebElement firstPropertyLink;
    
    @FindBy(id = "minimumRent")
    WebElement firstPropertyRent;

    @FindBy(xpath = "(//div[contains(text(),'Furnishing')])[2]/preceding::div[1]")
    WebElement firstPropertyFurnishing;

    @FindBy(xpath = "(//div[contains(text(),'Apartment Type')])/preceding::div[1]")
    WebElement firstPropertyType;

    @FindBy(xpath = "(//div[contains(text(),'Preferred Tenants')])[2]/preceding::div[1]")
    WebElement firstPropertyTenants;

    @FindBy(xpath = "(//div[contains(text(),'Available From')])[1]/preceding::div[1]")
    WebElement firstPropertyDate;

    // ===================== Popups =====================
    @FindBy(xpath = "//div/span[contains(text(),'Skip')]")
    WebElement skipPopUp;
    

    // Constructor
    public PropertyListPage(WebDriver driver) {
        super(driver);
    }


    // Enters a locality in the search input
    public void enterLocality(String area) {
        if (area == null || area.trim().isEmpty()) return;
        action = new Actions(driver);
        waitUntilVisibility(localityInput, 2);
        action.click(localityInput).perform();
        localityInput.sendKeys(area);
    }


    // Selects the first suggestion from the dropdown
    public void selectLocality() {
        try {
            waitUntilVisibility(selectArea, 10);
            action.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
        } catch (Exception e) {
            // Ignore if dropdown doesn't appear
            System.out.println("DropDown Did not appear!");
        }
    }


    // Clicks the search button and waits for results
    public void clickSearch() {
        searchBtn.click();
        try {
            Thread.sleep(3000); // Consider replacing with explicit wait
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    // Skips the annoying popup if present
    public void skipAnnoyingPopUp() {
        try {
            waitUntilVisibility(skipPopUp, 5);
            skipPopUp.click();
        } catch (Exception e) {
            System.out.println("No pop up found");
        }
    }


    // Checks if at least 5 property cards contain the given locality (for search validation)
    public boolean areLocalitiesPresentInCards(String locality) {
        return areLocalitiesPresentInCards(new String[]{locality});
    }


    // Checks if at least 5 property cards contain any of the given localities (for multi-locality search validation)
    public boolean areLocalitiesPresentInCards(String[] localities) {
        waitUntilLoad(XPATH_PROPERTY_CARDS, 5);
        List<WebElement> cards = driver.findElements(By.xpath(XPATH_PROPERTY_CARDS));

        int matchCount = 0;
        for (WebElement card : cards) {
            String cardText = card.getText().toLowerCase();
            for (String loc : localities) {
                if (cardText.contains(loc.toLowerCase())) {
                    matchCount++;
                    break;
                }
            }
            if (matchCount >= 5) return true;
        }
        return false;
    }


    // Extracts the current min and max price range from the UI (slider values)
    public int[] getMinMaxPriceRange() {
        String range = priceRange.getText()
                .substring(14)
                .replace("₹", "")
                .replace(" ", "")
                .replaceAll("Lac|Lacs", "00000")
                .replaceAll("k", "000");

        String minValue = range.substring(0, range.indexOf('t'));
        if (minValue.contains(".")) {
            minValue = minValue.replace(".", "").replaceFirst("0", "");
        }
        int min = Integer.parseInt(minValue);

        String maxValue = range.substring(range.indexOf('o') + 1).replaceAll("[a-z]", "");
        if (maxValue.contains(".")) {
            maxValue = maxValue.replace(".", "").replaceFirst("0", "");
        }
        int max = Integer.parseInt(maxValue);

        return new int[]{min, max};
    }


    // Checks if the availability date matches the selected filter (Immediate, Within 15/30 days, etc.)
    public boolean isAvailabilityMatching(String dateStr, String selectedFilter) {
//    	System.out.println(selectedFilter);
        // Handle "Ready to Move" as immediate availability
        if (dateStr.equalsIgnoreCase("Ready to Move")) {
            return selectedFilter.equalsIgnoreCase("Immediate") || selectedFilter.equalsIgnoreCase("30Days") || selectedFilter.equalsIgnoreCase("15Days") || selectedFilter.equalsIgnoreCase("30Plus") ;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
        LocalDate listingDate = LocalDate.parse(dateStr, formatter);
        LocalDate today = LocalDate.now();

        switch (selectedFilter.toLowerCase()) {
            case "immediate":
                return listingDate.isEqual(today);
            case "within 15 days":
                return !listingDate.isBefore(today) && !listingDate.isAfter(today.plusDays(15));
            case "within 30 days":
                return !listingDate.isBefore(today) && !listingDate.isAfter(today.plusDays(30));
            case "after 30 days":
                return listingDate.isAfter(today.plusDays(30));
            default:
                return false;
        }
    }
    
    // Moves the price slider to match the given min and max price
    public void movePriceSlider(int minPrice, int maxPrice) {
        int[] minMax = getMinMaxPriceRange();
        int currentMin = minMax[0];
        int currentMax = minMax[1];
        Actions action = new Actions(driver);
        while (currentMin < minPrice) {
            action.click(priceSliderLeftBtn).sendKeys(Keys.ARROW_RIGHT).build().perform();
            currentMin = getMinMaxPriceRange()[0];
        }
        while (currentMax > maxPrice) {
            action.click(priceSliderRightBtn).sendKeys(Keys.ARROW_LEFT).build().perform();
            currentMax = getMinMaxPriceRange()[1];
        }
    }

    // Applies all filters (BHK, price, availability, tenant, type, furnishing, parking)
    public void applyFilter(String bhk, int minPrice, int maxPrice, String availability,
                               String preferredTenant, String propertyType, String furnishing, String parking)
            throws InterruptedException {

        // Group all WebElement retrievals for filter application
        String bhkElementPath = String.format(XPATH_BHK_TYPE, bhk.toUpperCase());
        WebElement bhkElement = driver.findElement(By.xpath(bhkElementPath));
        String availabilityElementPath = String.format(XPATH_AVAILABILITY, availability);
        WebElement availabilityFilter = driver.findElement(By.xpath(availabilityElementPath));
        String preferredTenantsElementPath = String.format(XPATH_PREFERRED_TENANTS, preferredTenant);
        WebElement preferredTenantFilter = driver.findElement(By.xpath(preferredTenantsElementPath));
        String propertyTypeElementPath = String.format(XPATH_PROPERTY_TYPE, propertyType);
        WebElement propertyTypeFilter = driver.findElement(By.xpath(propertyTypeElementPath));
        String furnishingElementPath = String.format(XPATH_FURNISHING, furnishing);
        WebElement furnishingFilter = driver.findElement(By.xpath(furnishingElementPath));
        String parkingElementPath = String.format(XPATH_PARKING, parking);
        WebElement parkingFilter = driver.findElement(By.xpath(parkingElementPath));

        // Now apply all filters
        bhkElement.click();
        movePriceSlider(minPrice, maxPrice);
        availabilityFilter.click();
        preferredTenantFilter.click();
        propertyTypeFilter.click();
        furnishingFilter.click();
        parkingFilter.click();
    
    
        
    }
    
    
    // Validates the first property card after filters are applied
    public boolean validateFilter(String bhk, int minPrice, int maxPrice, String availability,
            String preferredTenant, String propertyType, String furnishing, String parking) {
    	// Wait for results to load
        try {
            waitUntilLoad(XPATH_PROPERTY_CARDS, 5);
        } catch (Exception e) {
            System.out.println("No Properties found for applied filter");
            return false;
        }
        

        // Validate filters on first property card
        String cardTenant = firstPropertyTenants.getText().toLowerCase();
        String selectedTenant = preferredTenant.toLowerCase();
        System.out.println(cardTenant + "         "+ selectedTenant);
        boolean tenantMatches = cardTenant.contains(selectedTenant) || cardTenant.contains("all") || selectedTenant.contains(cardTenant);


        String cardFurnishing = firstPropertyFurnishing.getText().toLowerCase();
        boolean furnishingMatches = cardFurnishing.contains(furnishing.toLowerCase());

        String cardBHK = firstPropertyType.getText().trim().toLowerCase(); // e.g., "2 bhk", "1 rk"
        String selectedBHK = bhk.trim().toLowerCase(); // e.g., "2 bhk"
        boolean bhkMatches = cardBHK.contains(selectedBHK);

        String rentText = firstPropertyRent.getText();

        // Only proceed if '+' is present
        if (rentText.contains("+")) {
            rentText = rentText.substring(0, rentText.indexOf('+'));
        }

        // If there's a second ₹, cut before it
        if (rentText.indexOf("₹") != rentText.lastIndexOf("₹")) {
            rentText = rentText.substring(0, rentText.lastIndexOf("₹"));
        }

        // Remove all non-digit characters
        rentText = rentText.replaceAll("[^0-9]", "");

        // Only parse if there's something left
        
        int rent = Integer.parseInt(rentText.trim());
        
        boolean priceMatches = rent >= minPrice && rent <= maxPrice;

        String cardDateText = firstPropertyDate.getText(); // e.g., "17-Sep-2025"
        boolean availabilityMatches = isAvailabilityMatching(cardDateText, availability);

        boolean allFiltersApplied = tenantMatches && furnishingMatches && bhkMatches && priceMatches && availabilityMatches;

        System.out.println("Tenant Match: " + tenantMatches);
        System.out.println("Furnishing Match: " + furnishingMatches);
        System.out.println("Type Match: " + bhkMatches);
        System.out.println("Price Match: " + priceMatches);
        System.out.println("Availability Match: " + availabilityMatches);

        return allFiltersApplied;
    	
    }
    
    // Resets all applied filters and checks if filters are cleared
    public boolean resetFilter() {
    	scrollIntoView(resetFilterBtn);
    	robot.mouseMove(440, 330);
    	robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    	robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    	
    	try {
    	waitUntilInvisibile(appliedFilterNo, 5);
//    	System.out.println("Filters Still Applied");
    	return false;
    	}catch(Exception e) {
//    		System.out.println("Filters cleared");
    		return true;
    	}
    }
    
    // Opens the first property card in the list
    public void openFirstProperty() {
    	try {
    	waitUntilLoad(XPATH_PROPERTY_CARDS, 5);
    	firstPropertyLink.click();
    	}catch(Exception e) {
    		System.out.println("No Property Available for the selected Filter");
    		return;
    	}
    }
    
    // Sorts the property list by price (low to high)
    public void sortByPrice() {
    	sortByInput.click();
    	waitUntilVisibility(sortByPriceLow, 5);
    	sortByPriceLow.click();
    	waitUntilLoad(XPATH_PROPERTY_CARDS, 5);
    	
    }
    
    // Validates that the property list is sorted by price (low to high)
    public boolean validateSort() throws InterruptedException {
    	waitUntilLoad(XPATH_PROPERTY_CARDS, 5);
  
        List<WebElement> cards = driver.findElements(By.id("minimumRent"));
        List<Integer> propertiesRentInOrder = new ArrayList<>();

        for (WebElement card : cards) {


            String rentText = card.getText();

            // Only proceed if '+' is present
            if (rentText.contains("+")) {
                rentText = rentText.substring(0, rentText.indexOf('+'));
            }

            // If there's a second ₹, cut before it
            if (rentText.indexOf("₹") != rentText.lastIndexOf("₹")) {
                rentText = rentText.substring(0, rentText.lastIndexOf("₹"));
            }

            // Remove all non-digit characters
            rentText = rentText.replaceAll("[^0-9]", "");

            // Only parse if there's something left
            if (!rentText.isEmpty()) {
                int rent = Integer.parseInt(rentText.trim());
                propertiesRentInOrder.add(rent);
            }
        }

        List<Integer> sortedList = new ArrayList<>(propertiesRentInOrder);
        Collections.sort(sortedList);

        boolean isSorted = propertiesRentInOrder.equals(sortedList);
        return isSorted;
    }

   


}
