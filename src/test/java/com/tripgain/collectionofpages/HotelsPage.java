package com.tripgain.collectionofpages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.tripgain.common.Log;
import com.tripgain.common.ScreenShots;
import com.tripgain.common.TestExecutionNotifier;

public class HotelsPage {

	WebDriver driver;
	

	public HotelsPage(WebDriver driver) {

        PageFactory.initElements(driver, this);
		this.driver=driver;
	}
	
	// Method to get flight count on result page
	
	public void getFlightsCount(Log Log, ScreenShots ScreenShots) {
	    try {
	    	Thread.sleep(2000);
	        WebElement Flightscount = driver.findElement(By.className("total-hotels-count"));
	        String countText = Flightscount.getText(); 
	        System.out.println("Flight count is: " + countText);
	        Log.ReportEvent("PASS", "Flight count is: " + countText);
	        ScreenShots.takeScreenShot1();
	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Failed to retrieve flight count: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	    }
	}
	
	//------------------------------------------------------------------------------------------------------
	
	

	
	//Method to get the hotels name text for validating result screen is displayed or not 
	
	public void validateResultScreen(Log Log, ScreenShots ScreenShots) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(150));
	      //  WebElement allHotelsGrid = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'MuiGrid2-root') and contains(@class, 'css-lgz2hl')]")));
	        
	        WebElement allHotelsGrid = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[@title]")));
	        // Check if the hotel grid is displayed
	        if (allHotelsGrid.isDisplayed()) {
	            Log.ReportEvent("PASS", "Hotel Names is displayed.");
	        } else {
	            Log.ReportEvent("FAIL", "Hotel Names is not displayed.");
	        }

	        ScreenShots.takeScreenShot1();

	        // Get the total count of hotels
	        WebElement countElement = driver.findElement(By.className("total-hotels-count"));
	        String countText = countElement.getText();
	        String totalCountStr = countText.replaceAll("[^0-9]", ""); // Extract digits
	        int totalCount = Integer.parseInt(totalCountStr);

	        // Get the list of hotel names
	        List<WebElement> hotelNames = driver.findElements(By.xpath("//*[contains(@class,'tg-hl-name')]"));

	        if (hotelNames.size() > 0) {
	            // Collect all hotel names
	            List<String> allHotelNames = new ArrayList<>();
	            for (WebElement hotel : hotelNames) {
	                allHotelNames.add(hotel.getText());
	            }

	            Log.ReportEvent("PASS", "Total Hotels Count: " + totalCount + " | All Hotel Names: " + allHotelNames);
	            ScreenShots.takeScreenShot1();
	        } else {
	            Log.ReportEvent("FAIL", "No hotel names are displayed.");
	            ScreenShots.takeScreenShot1();
	        }

	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Exception during validation: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	    }
	}



	//------------------------------------------------------------------------------------------------------
	
	// Method to select hotel based on index
	
	public void selectHotelBasedOnIndex(int index, Log Log) {
	    try {
	        String hotelCardXpath = "(//div[@class='MuiGrid2-root MuiGrid2-container MuiGrid2-direction-xs-row  d-flex justify-content-between section-hotel-info section-recommended css-1kgnctx'])[" + index + "]";
	        WebElement hotelCard = driver.findElement(By.xpath(hotelCardXpath));
	        // click the 'Select' button based on index
	        WebElement selectButton = hotelCard.findElement(By.xpath(".//button[text()='Select']"));
	        selectButton.click();
	        Log.ReportEvent("PASS", "Clicked 'Select' button for hotel at index: " + index);
	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Failed to click 'Select' button at index " + index + ": " + e.getMessage());
	    }
	}

	//---------------------------------------------------------------------------------------------
	
	//Method for to validate selected hotel is dispalyed or not ---use this also
	
//	public void validateSelectedHotel(int index, Log Log, ScreenShots ScreenShots) {
//	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
//
//	    try {
//	        Thread.sleep(3000);
//	        // select hotel based on index on result page
//	        String hotelCardXpath = "(//*[contains(@class,'hcard')])[" + index + "]";
//	        WebElement hotelCard = driver.findElement(By.xpath(hotelCardXpath));
//
//	        // Get hotel name from the selected index
//	        WebElement hotelTextElement = hotelCard.findElement(By.xpath(".//*[contains(@class,'tg-hl-name')]"));
//	        String selectedHotelName = hotelTextElement.getText().trim();
//
//	        // Clean up truncated hotel name
//	        String cleanedSelectedName = selectedHotelName.replace("...", "").trim();
//
//	        // Click 'Select' button 
//	        Thread.sleep(3000);
//
//	        WebElement selectButton = hotelCard.findElement(By.xpath(".//button[text()='Select']"));
//	        selectButton.click();
//	        Log.ReportEvent("PASS", "Clicked 'Select' on hotel index " + index + ": " + selectedHotelName);
//	        System.out.println("Clicked 'Select' on hotel index " + index + ": " + selectedHotelName);
//	        ScreenShots.takeScreenShot1();
//
//	        // Wait for the Selected Hotel Grid on next page
//	        Thread.sleep(6000);
//
//	        WebElement selectedHotelGrid = wait.until(ExpectedConditions.visibilityOfElementLocated(
//	            By.xpath("//*[contains(@class,' tg-hl-details-card')]")
//	        ));
//
//	        if (selectedHotelGrid.isDisplayed()) {
//	            Log.ReportEvent("PASS", "Selected hotel detail page is displayed.");
//	            System.out.println("Selected hotel detail page is displayed.");
//	        } else {
//	            Log.ReportEvent("FAIL", "Selected hotel detail page is NOT displayed.");
//	            System.out.println("Selected hotel detail page is not displayed.");
//
//	        }
//	        ScreenShots.takeScreenShot1();
//
//	        // Get hotel name from next page
//	        Thread.sleep(3000);
//
//	        WebElement nextPageHotelText = driver.findElement(By.xpath("//*[contains(@class,'tg-hl-hotalname')]"));
//	        String nextPageHotelName = nextPageHotelText.getText().trim();
//
//	        // Compare hotel names
//	        if (nextPageHotelName.toLowerCase().startsWith(cleanedSelectedName.toLowerCase())) {
//	            Log.ReportEvent("PASS", "Hotel name matched: '" + nextPageHotelName + "'");
//	            System.out.println("Hotel name matched");
//
//	        } else {
//	            Log.ReportEvent("FAIL", "Hotel name mismatch. Selected: '" + selectedHotelName + "', but found: '" + nextPageHotelName + "'");
//	            System.out.println("Hotel name not matched");
//
//	        }
//	        ScreenShots.takeScreenShot1();
//
//	    } catch (Exception e) {
//	        Log.ReportEvent("FAIL", "Exception in hotel selection validation: " + e.getMessage());
//	        ScreenShots.takeScreenShot1();
//	    }
//	}
	
	public void waitForResultPageHotelGrid (Log Log, ScreenShots ScreenShots) {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));

	    try {
	        By hotelGridLocator = By.xpath("//*[contains(@class,'tg-hl-results-list')]");
	        WebElement selectedHotelGrid = wait.until(ExpectedConditions.visibilityOfElementLocated(hotelGridLocator));

	        if (selectedHotelGrid.isDisplayed()) {
	            Log.ReportEvent("PASS", "Result Page hotel grid is displayed.");
	            System.out.println("Result Page hotel grid is displayed.");
	        } else {
	            Log.ReportEvent("FAIL", "Result Page hotel grid is NOT displayed.");
	            System.out.println("Result Page hotel grid is not displayed.");
	        }

	        ScreenShots.takeScreenShot1();
	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Error while waiting for hotel grid: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	        System.out.println("Exception occurred: " + e.getMessage());
	    }
	}
	
	
	
	
	public String getSelectedHotelNameTextBasedOnIndex(int index, Log Log, ScreenShots ScreenShots) {
	    try {
	        Thread.sleep(3000);
	        String hotelCardXpath = "(//*[contains(@class,'hcard')])[" + index + "]";
	        WebElement hotelCard = driver.findElement(By.xpath(hotelCardXpath));

	        WebElement hotelTextElement = hotelCard.findElement(By.xpath(".//*[contains(@class,'tg-hl-name')]"));
	        String selectedHotelName = hotelTextElement.getText().trim();
	        String cleanedSelectedName = selectedHotelName.replace("...", "").trim();

	        Thread.sleep(3000);
	        WebElement selectButton = hotelCard.findElement(By.xpath(".//button[text()='Select']"));
	        selectButton.click();

	        Log.ReportEvent("PASS", "Clicked 'Select' on hotel index " + index + ": " + selectedHotelName);
	        System.out.println("Clicked 'Select' on hotel index " + index + ": " + selectedHotelName);
	        ScreenShots.takeScreenShot1();

	        return cleanedSelectedName;

	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Exception while selecting hotel: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	        return null;
	    }
	}

	public String getHotelNameTextOnNextPage(Log Log, ScreenShots ScreenShots) {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
	    try {
	        Thread.sleep(6000);
	        WebElement selectedHotelGrid = wait.until(ExpectedConditions.visibilityOfElementLocated(
	            By.xpath("//*[contains(@class,' tg-hl-details-card')]")
	        ));

	        if (selectedHotelGrid.isDisplayed()) {
	            Log.ReportEvent("PASS", "Selected hotel detail page is displayed.");
	            System.out.println("Selected hotel detail page is displayed.");
	        } else {
	            Log.ReportEvent("FAIL", "Selected hotel detail page is NOT displayed.");
	            System.out.println("Selected hotel detail page is not displayed.");
	        }

	        ScreenShots.takeScreenShot1();

	        Thread.sleep(3000);
	        WebElement nextPageHotelText = driver.findElement(By.xpath("//*[contains(@class,'tg-hl-hotalname')]"));
	        String nextPageHotelName = nextPageHotelText.getText().trim();

	        return nextPageHotelName;

	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Exception while getting hotel name on next page: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	        return null;
	    }
	}

	public void waitForSelectedNextPageHotelGrid(Log Log, ScreenShots ScreenShots) {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));

	    try {
	        By hotelGridLocator = By.xpath("//*[contains(@class,' tg-hl-details-card')]");
	        WebElement selectedHotelGrid = wait.until(ExpectedConditions.visibilityOfElementLocated(hotelGridLocator));

	        if (selectedHotelGrid.isDisplayed()) {
	            Log.ReportEvent("PASS", "Selected hotel detail page is displayed.");
	            System.out.println("Selected hotel detail page is displayed.");
	        } else {
	            Log.ReportEvent("FAIL", "Selected hotel detail page is NOT displayed.");
	            System.out.println("Selected hotel detail page is not displayed.");
	        }

	        ScreenShots.takeScreenShot1();
	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Error while waiting for hotel detail page: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	        System.out.println("Exception occurred: " + e.getMessage());
	    }
	}

	public void compareHotelNames(String selectedHotelName, String nextPageHotelName, Log Log, ScreenShots ScreenShots) {
	    try {
	        if (selectedHotelName != null && nextPageHotelName != null &&
	            nextPageHotelName.toLowerCase().startsWith(selectedHotelName.toLowerCase())) {

	            Log.ReportEvent("PASS", "Hotel name matched: '" + nextPageHotelName + "'");
	            System.out.println("Hotel name matched");

	        } else {
	            Log.ReportEvent("FAIL", "Hotel name mismatch. Selected: '" + selectedHotelName + "', but found: '" + nextPageHotelName + "'");
	            System.out.println("Hotel name not matched");
	        }

	        ScreenShots.takeScreenShot1();

	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Exception while comparing hotel names: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	    }
	}



//------------------------------------------------------------------------------------------------------------
	
	//Method for to click on choose your room
	
	public void clickChooseyourRoom() throws InterruptedException {
		driver.findElement(By.xpath("//button[text()='Choose your Room']")).click();
		Thread.sleep(4000);
	}
	
	//------------------------------------------------------------------------------------------------------
	
	//Method for to click on select and continue based on rooms Selected ---use this also 
	
//	public void ClickOnSelectAndContinueBasedOnRoomsSelected(int index, Log Log, ScreenShots ScreenShots) {
//	    String roomText = "";
//
//	    try {
//	        List<WebElement> roomSelections = driver.findElements(By.xpath("//*[contains(@class,'tg-hl-rooms-card')]"));
//	        int count = roomSelections.size();
//
//	        // Fallback to first room if index is invalid
//	        int indexToUse = index;
//	        if (index > count || index < 1) {
//	            indexToUse = 1; // default to first room
//	        }
//	        
//	        String selectedRoomXPath = "(//*[contains(@class,'tg-hl-rooms-card')])[" + indexToUse + "]";
//	        WebElement selectedRoom = driver.findElement(By.xpath(selectedRoomXPath));
//
//	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectedRoom);
//
//	        WebElement selectedRoomTextElement = selectedRoom.findElement(By.xpath(".//*[contains(@class,'tg-hl-refundable-tag')]"));
//	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectedRoomTextElement);
//	        roomText = selectedRoomTextElement.getText().trim();
//
//	        WebElement selectContinueButton = selectedRoom.findElement(By.xpath(".//button[text()='Select & Continue']"));
//	        selectContinueButton.click();
//
//	        Log.ReportEvent("PASS", "Clicked 'Select & Continue' for room index " + indexToUse + ": " + roomText);
//	        ScreenShots.takeScreenShot1();
//
//	    } catch (Exception e) {
//	        Log.ReportEvent("FAIL", "Error selecting room: " + e.getMessage());
//	        ScreenShots.takeScreenShot1();
//	    }
//
//	    try {
//	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(75));
//	        WebElement reviewPage = wait.until(ExpectedConditions.visibilityOfElementLocated(
//	            By.xpath("//h6[contains(normalize-space(.), 'Review Your Booking')]")));
//
//	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'start' });", reviewPage);
//	        Log.ReportEvent("PASS", "Review Your Booking Page is Displayed");
//	        ScreenShots.takeScreenShot();
//
//	    } catch (Exception e) {
//	        Log.ReportEvent("FAIL", "Review Your Booking Page is Not Displayed: " + e.getMessage());
//	        ScreenShots.takeScreenShot();
//	        Assert.fail("Review Your Booking Page is Not Displayed: " + e.getMessage());
//	    }
//
//	    // === Compare room names ===
//	    try {
//	        WebElement bookingPageRoomTextElement = driver.findElement(By.xpath("//*[contains(@class,'tg-hb-refundable-tag')]"));
//	        String bookingRoomText = bookingPageRoomTextElement.getText().trim();
//
//	        // Log actual values
//	        Log.ReportEvent("INFO", "Selected Room Text: '" + roomText + "'");
//	        Log.ReportEvent("INFO", "Booking Page Room Text: '" + bookingRoomText + "'");
//
//	        // Remove common suffixes and normalize strings
//	        String roomSelected = roomText.replaceAll("(?i)(Non-refundable|Refundable)", "").trim().replaceAll("\\s+", " ").toLowerCase();
//	        String roomBooking = bookingRoomText.replaceAll("(?i)(Non-refundable|Refundable)", "").trim().replaceAll("\\s+", " ").toLowerCase();
//
//	        if (roomSelected.equals(roomBooking)) {
//	            Log.ReportEvent("PASS", "Room name matched: " + roomBooking);
//	        } else {
//	            Log.ReportEvent("FAIL", "Mismatch between selected room name and booking page room name.\n"
//	                + "Selected: " + roomSelected + "\nBooking Page: " + roomBooking);
//	            Assert.fail("Room name mismatch.");
//	        }
//
//	        ScreenShots.takeScreenShot1();
//
//	    } catch (Exception e) {
//	        Log.ReportEvent("FAIL", "Error validating booking page room name: " + e.getMessage());
//	        ScreenShots.takeScreenShot1();
//	    }
//	}
//


	public String selectRoomAndContinueBasedOnIndex(int index, Log Log, ScreenShots ScreenShots) {
	    String roomText = "";
	    try {
	        List<WebElement> roomSelections = driver.findElements(By.xpath("//*[contains(@class,'tg-hl-rooms-card')]"));
	        int count = roomSelections.size();

	        int indexToUse = index;
	        if (index > count || index < 1) {
	            indexToUse = 1; // fallback to first room
	        }

	        String selectedRoomXPath = "(//*[contains(@class,'tg-hl-rooms-card')])[" + indexToUse + "]";
	        WebElement selectedRoom = driver.findElement(By.xpath(selectedRoomXPath));

	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectedRoom);

	        WebElement selectedRoomTextElement = selectedRoom.findElement(By.xpath(".//*[contains(@class,'tg-hl-refundable-tag')]"));
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectedRoomTextElement);
	        roomText = selectedRoomTextElement.getText().trim();

	        WebElement selectContinueButton = selectedRoom.findElement(By.xpath(".//button[text()='Select & Continue']"));
	        selectContinueButton.click();

	        Log.ReportEvent("PASS", "Clicked 'Select & Continue' for room index " + indexToUse + ": " + roomText);
	        ScreenShots.takeScreenShot1();

	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Error selecting room: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	    }

	    return roomText;
	}

	public String validateReviewYourBookingText(Log Log, ScreenShots ScreenShots) {
	    String bookingRoomText = "";
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(75));
	        WebElement reviewPage = wait.until(ExpectedConditions.visibilityOfElementLocated(
	            By.xpath("//h6[contains(normalize-space(.), 'Review Your Booking')]")));

	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'start' });", reviewPage);
	        Log.ReportEvent("PASS", "Review Your Booking Page is Displayed");
	        ScreenShots.takeScreenShot();

	        WebElement bookingPageRoomTextElement = driver.findElement(By.xpath("//*[contains(@class,'tg-hb-refundable-tag')]"));
	        bookingRoomText = bookingPageRoomTextElement.getText().trim();

	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Review Your Booking Page is Not Displayed: " + e.getMessage());
	        ScreenShots.takeScreenShot();
	        Assert.fail("Review Your Booking Page is Not Displayed: " + e.getMessage());
	    }

	    return bookingRoomText;
	}

	public void compareRoomNames(String selectedRoomText, String bookingRoomText, Log Log, ScreenShots ScreenShots) {
	    try {
	        Log.ReportEvent("INFO", "Selected Room Text: '" + selectedRoomText + "'");
	        Log.ReportEvent("INFO", "Booking Page Room Text: '" + bookingRoomText + "'");

	        String roomSelected = selectedRoomText.replaceAll("(?i)(Non-refundable|Refundable)", "")
	                .trim().replaceAll("\\s+", " ").toLowerCase();
	        String roomBooking = bookingRoomText.replaceAll("(?i)(Non-refundable|Refundable)", "")
	                .trim().replaceAll("\\s+", " ").toLowerCase();

	        if (roomSelected.contains(roomBooking)) {
	            Log.ReportEvent("PASS", "Room name matched: " + roomBooking);
	        } else {
	            Log.ReportEvent("FAIL", "Mismatch between selected room name and booking page room name.\n"
	                    + "Selected: " + roomSelected + "\nBooking Page: " + roomBooking);
	            Assert.fail("Room name mismatch.");
	        }

	        ScreenShots.takeScreenShot1();

	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Error validating booking page room name: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	    }
	}

	
	public void ClickOnSelectAndContinueBasedOnRoomsSelected(int index, Log Log, ScreenShots ScreenShots) {
	    String selectedRoomText = selectRoomAndContinueBasedOnIndex(index, Log, ScreenShots);
	    String bookingRoomText = validateReviewYourBookingText(Log, ScreenShots);
	    compareRoomNames(selectedRoomText, bookingRoomText, Log, ScreenShots);
	}

	
	
	
	
	//--------------------------------------------------------------------------------------------
	
	//Method to click on Hotel dropdown
    public void hotelClick()
    {
        driver.findElement(By.xpath("(//*[@data-testid='KeyboardArrowDownIcon'])[1]")).click();
        driver.findElement(By.xpath("//a[text()='Hotels']")).click();
    }
    
	//--------------------------------------------------------------------------------------------

 //Method to check whether Home Page is displayed
    public void validateHomePgaeIsDisplayed(Log Log, ScreenShots ScreenShots) {
        try {
            Thread.sleep(3000);

            WebElement homePage = driver.findElement(By.xpath("//span[text()='Search']"));

            if (homePage.isDisplayed()) {
                System.out.println("Home page is getting displayed successfully.");
                ScreenShots.takeScreenShot1();

                Log.ReportEvent("PASS", "Home page is getting displayed successfully.");
            } else {
                System.out.println("Failed to load the Home Page: Element found but not visible.");
                ScreenShots.takeScreenShot1();

                Log.ReportEvent("FAIL", "Failed to load the Home Page: Element not visible.");
            }

        } catch (Exception e) {
            System.out.println("Exception while verifying Home Page: " + e.getMessage());
            Log.ReportEvent("FAIL", "Exception while verifying Home Page: " + e.getMessage());
        }
    }
    
	//--------------------------------------------------------------------------------------------

 //Method to click on City dropdown
    public void clickOnPropertySearchfield(String city) throws InterruptedException
    {
         try {
        Thread.sleep(3000);
        WebElement seachFieldClick = driver.findElement(By.xpath("//input[@aria-describedby='react-select-4-placeholder']"));
        //seachFieldClick.click();
        seachFieldClick.sendKeys(city);
        Thread.sleep(2000);
    List<WebElement> listOfProperty = driver.findElements(By.xpath("//div[@role='option']"));
    WebElement firstProperty = listOfProperty.get(0);
    firstProperty.click();
         }
         catch (Exception e) {
             System.out.println("Failed to select a property from the list: " + e.getMessage());
             return;
            }
    }
    
	//--------------------------------------------------------------------------------------------


    @FindBy(xpath = "//*[contains(@class,'tg-bs-date')]")
    WebElement datePickerInput;
    
    
//Method to Select Check-In Date By Passing Two Paramenters(Date and MounthYear)
            public void selectDate(String day, String MonthandYear) throws InterruptedException
            {
                JavascriptExecutor js = (JavascriptExecutor) driver;

                // Method A: Using zoom
                js.executeScript("document.body.style.zoom='80%'");
                TestExecutionNotifier.showExecutionPopup();
                datePickerInput.click();
                String Date=driver.findElement(By.xpath("//h2[@class='react-datepicker__current-month']")).getText();
                if(Date.contentEquals(MonthandYear))
                {
                    Thread.sleep(4000);
                    driver.findElement(By.xpath("//*[@class='react-datepicker__month-container']//*[text()='"+day+"']")).click();
                    
                    Thread.sleep(4000);
                }else {
                    while(!Date.contentEquals(MonthandYear))
                    {
                        Thread.sleep(500);
                        driver.findElement(By.xpath("//button[@aria-label='Next Month']")).click();
                        if(driver.findElement(By.xpath("(//div[@class='react-datepicker__header ']/child::h2)[1]")).getText().contentEquals(MonthandYear))
                        {
                            driver.findElement(By.xpath("//*[@class='react-datepicker__month-container']//*[text()='"+day+"']")).click();
                            break;
                        }

                    }
                }
            }
            
        	//--------------------------------------------------------------------------------------------

          //Method to Click on Check-Out  Date
            public void clickOnReturnDate()
            {
                driver.findElement(By.xpath("(//input[@class='DayPickerInput input'])[2]")).click();
            }
            
//Method to Select Return Date By Passing Two Paramenters(Date and MounthYear)
        public void selectReturnDate(String returnDate, String returnMonthAndYear) throws InterruptedException
        {
            clickOnReturnDate();
            String Date=driver.findElement(By.xpath("(//div[@class='react-datepicker__header ']/child::h2)[1]")).getText();
            if(Date.contentEquals(returnMonthAndYear))
            {
                driver.findElement(By.xpath("(//div[@class='react-datepicker__month-container'])[1]//div[text()='"+returnDate+"' and @aria-disabled='false']")).click();
                Thread.sleep(4000);
            }else {
                while(!Date.contentEquals(returnMonthAndYear))
                {
                    Thread.sleep(500);
                    driver.findElement(By.xpath("//button[@aria-label='Next Month']")).click();
                    if(driver.findElement(By.xpath("(//div[@class='react-datepicker__header ']/child::h2)[1]")).getText().contentEquals(returnMonthAndYear))
                    {
                        driver.findElement(By.xpath("//div[text()='"+returnDate+"']")).click();
                        break;
                    }

                }
            }
        }
        
    	//--------------------------------------------------------------------------------------------

//Method to add room, adt and child
public void addRoom(int roomcount, int adtCount , int chdCount) throws InterruptedException {
            Thread.sleep(2000);
            
            try {
                System.out.println("Expanding room selection...");
                driver.findElement(By.xpath("//*[@data-testid='ExpandMoreIcon']")).click();
            } catch (Exception e) {
                System.out.println("Failed to click expand icon: " + e.getMessage());
            }

            for(int i = 0; i < roomcount - 1; i++) {
                try {
                    System.out.println("Adding room: " + (i + 2));
                    driver.findElement(By.xpath("//button[text()='Add Room']")).click();
                } catch (Exception e) {
                    System.out.println("Failed to click 'Add Room' button at index " + i + ": " + e.getMessage());
                }
            }

            Thread.sleep(3000);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[contains(text(),'Room')]")));
            } catch (Exception e) {
                System.out.println("Room elements not visible within timeout: " + e.getMessage());
            }

            List<WebElement> listOfRooms = driver.findElements(By.xpath("//h6[contains(text(),'Room')]"));
            System.out.println("Rooms found: " + listOfRooms.size());

            for(int i = 0; i < listOfRooms.size(); i++) {
                WebElement roomElement = listOfRooms.get(i);
                String roomText = roomElement.getText();
                String[] roomTextSplit1 = roomText.split(" ");
                String finalRoomText = roomTextSplit1[1].trim();

                System.out.println("Configuring Room " + finalRoomText);
                System.out.println("Total rooms: " + listOfRooms.size());
                System.out.println("Room index: " + i);
                System.out.println("Adult count: " + adtCount);

                // Add Adults
                for(int j = 0; j < adtCount - 1; j++) {
                    try {
                        WebElement addAdult = driver.findElement(By.xpath("(//h6[text()='" + finalRoomText + "']/parent::div/parent::div//p/parent::div)[1]//*[contains(normalize-space(@class), 'tg-hl-adult-plus')]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addAdult);
                        Thread.sleep(500);
                        addAdult.click();
                        System.out.println("Adult added to Room " + finalRoomText);
                    } catch (Exception e) {
                        System.out.println("Failed to add adult in Room " + finalRoomText + " at iteration " + j + ": " + e.getMessage());
                    }
                }

                // Add Children
                for (int k = 1; k < chdCount + 1; k++) {
                    try {
                        Thread.sleep(3000);
                        driver.findElement(By.xpath("(//h6[text()='" + finalRoomText + "']/parent::div/parent::div//p/parent::div)[2]//*[contains(normalize-space(@class), 'tg-hl-child-plus')]")).click();
                        System.out.println("Child " + k + " added to Room " + finalRoomText);
                    } catch (Exception e) {
                        System.out.println("Failed to add child in Room " + finalRoomText + " at iteration " + k + ": " + e.getMessage());
                    }

                    try {
                        driver.findElement(By.xpath("((//h6[text()='" + finalRoomText + "']/parent::div/parent::div//p/parent::div)[2]/parent::div//div[@aria-haspopup='listbox'])[" + k + "]")).click();
                        Thread.sleep(2000);
                        List<WebElement> childAgeList = driver.findElements(By.xpath("//ul//li"));
                        childAgeList.get(3).click();
                        System.out.println("Child " + k + " age set in Room " + finalRoomText);
                    } catch (Exception e) {
                        System.out.println("Failed to set child age in Room " + finalRoomText + " for child " + k + ": " + e.getMessage());
                    }
                }

                // Click Done Button after final room
                if (i == listOfRooms.size() - 1) {
                    try {
                        WebElement doneButton = driver.findElement(By.xpath("//button[text()='Done']"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", doneButton);
                        Thread.sleep(500);
                        doneButton.click();
                        System.out.println("Clicked 'Done' after completing Room " + finalRoomText);
                    } catch (Exception e) {
                        System.out.println("Failed to click 'Done' button: " + e.getMessage());
                    }
                }
            }
        }

//--------------------------------------------------------------------------------------------

// Method to click on search button
        public void clickOnSearch() {
            try {
                System.out.println("Attempting to click on the 'Search' button...");
                driver.findElement(By.xpath("//button[text()='Search']")).click();
                System.out.println("'Search' button clicked successfully.");
            } catch (Exception e) {
                System.out.println("Failed to click on the 'Search' button: " + e.getMessage());
            }
        }

    	//--------------------------------------------------------------------------------------------



        
@FindBy(xpath = "//*[text()='Search for City, Property']/parent::div//input")
       private WebElement enterLocation;
       

 public void enterCityOrHotelName(String location) throws TimeoutException {
            enterLocation.clear();
            enterLocation.sendKeys(location);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='option']")));
            
            selectCityOrHotelNameforHotels(location);
        }

        
         
 
        
	//---------------------------------------------------------------------------------
// public void selectCityOrHotelName(String location) {
//     WebElement dropDownBox = driver.findElement(By.xpath("//*[@role='listbox']/parent::div"));
//
//     if (dropDownBox.isDisplayed()) {
//         List<WebElement> options = driver.findElements(By.xpath("//div[@role='option']"));
//         
//         for (WebElement option : options) {
//             String hotelName = option.getText().trim();
//             if (hotelName.equalsIgnoreCase(location.trim())) {
//                 option.click();
//                 break;
//             }
//         }
//     }
//     
// }
 
// public void selectCityOrHotelNameforHotels(String location) throws TimeoutException {
//     try {
//         // Wait for dropdown to appear
//         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//         WebElement dropDownBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                 By.xpath("//*[@role='listbox']/parent::div")));
//
//         List<WebElement> options = driver.findElements(By.xpath("//div[@role='option']"));
//
//         if (options.isEmpty()) {
//             System.out.println("No suggestions found in dropdown.");
//             return;
//         }
//
//         String input = location.trim().toLowerCase();
//         WebElement bestMatch = null;
//         int bestScore = Integer.MAX_VALUE;
//
//         for (WebElement option : options) {
//             String suggestion = option.getText().trim().toLowerCase();
//             int score = levenshteinDistance(input, suggestion);
//
//             if (score < bestScore) {
//                 bestScore = score;
//                 bestMatch = option;
//             }
//         }
//
//         if (bestMatch != null) {
//             bestMatch.click();
//             System.out.println("Selected best match: " + bestMatch.getText());
//         } else {
//             System.out.println("No suitable match found for input: " + location);
//         }
//
//     } catch (NoSuchElementException e) {
//         System.out.println("Dropdown elements not found: " + e.getMessage());
//     } catch (Exception e) {
//         System.out.println("Unexpected error while selecting city or hotel: " + e.getMessage());
//     }
// }
//public int levenshteinDistance(String a, String b) {
//     int[][] dp = new int[a.length() + 1][b.length() + 1];
//
//     for (int i = 0; i <= a.length(); i++) {
//         for (int j = 0; j <= b.length(); j++) {
//             if (i == 0) {
//                 dp[i][j] = j;
//             } else if (j == 0) {
//                 dp[i][j] = i;
//             } else {
//                 int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
//                 dp[i][j] = Math.min(
//                         Math.min(dp[i - 1][j] + 1,     // deletion
//                                  dp[i][j - 1] + 1),    // insertion
//                                  dp[i - 1][j - 1] + cost); // substitution
//             }
//         }
//     }
//     return dp[a.length()][b.length()];
// }
//	
 
 public void selectCityOrHotelNameforHotels(String location) throws TimeoutException {
     try {
         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

         // Wait for dropdown container to appear
         wait.until(ExpectedConditions.visibilityOfElementLocated(
             By.xpath("//*[@role='listbox']/parent::div")));

         // Wait until options are loaded
         wait.until(driver -> driver.findElements(By.xpath("//div[@role='option']")).size() > 0);

         List<WebElement> initialOptions = driver.findElements(By.xpath("//div[@role='option']"));
         int bestScore = Integer.MAX_VALUE;
         String bestMatchText = null;

         String input = location.trim().toLowerCase();

         for (int i = 0; i < initialOptions.size(); i++) {
             try {
                 WebElement option = driver.findElements(By.xpath("//div[@role='option']")).get(i);
                 String suggestion = option.getText().trim().toLowerCase();
                 int score = levenshteinDistance(input, suggestion);

                 if (score < bestScore) {
                     bestScore = score;
                     bestMatchText = option.getText().trim();
                 }
             } catch (StaleElementReferenceException e) {
                 System.out.println("Stale element at index " + i + ", skipping.");
             }
         }

         if (bestMatchText != null) {
             // Retry clicking best match up to 3 times
             int attempts = 0;
             boolean clicked = false;
             while (attempts < 3 && !clicked) {
                 try {
                     WebElement bestMatch = wait.until(ExpectedConditions.elementToBeClickable(
                         By.xpath("//div[@role='option' and normalize-space(text())='" + bestMatchText + "']")));
                     bestMatch.click();
                     System.out.println("Selected best match: " + bestMatchText);
                     clicked = true;
                 } catch (StaleElementReferenceException e) {
                     System.out.println("Stale element on click attempt " + (attempts + 1) + ", retrying...");
                 }
                 attempts++;
             }

             if (!clicked) {
                 System.out.println("Failed to click the best match after retries.");
             }

         } else {
             System.out.println("No suitable match found for input: " + location);
         }

     } catch (NoSuchElementException e) {
         System.out.println("Input or dropdown not found: " + e.getMessage());
     } catch (Exception e) {
         System.out.println("Unexpected error while selecting city or hotel: " + e.getMessage());
     }
 }

 public int levenshteinDistance(String a, String b) {
     int[][] dp = new int[a.length() + 1][b.length() + 1];

     for (int i = 0; i <= a.length(); i++) {
         for (int j = 0; j <= b.length(); j++) {
             if (i == 0) {
                 dp[i][j] = j;
             } else if (j == 0) {
                 dp[i][j] = i;
             } else {
                 int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                 dp[i][j] = Math.min(Math.min(
                     dp[i - 1][j] + 1,       // deletion
                     dp[i][j - 1] + 1),      // insertion
                     dp[i - 1][j - 1] + cost // substitution
                 );
             }
         }
     }
     return dp[a.length()][b.length()];
 }

}
