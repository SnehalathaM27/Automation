package com.tripgain.collectionofpages;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.tripgain.common.Log;
import com.tripgain.common.ScreenShots;
import com.tripgain.common.TestExecutionNotifier;

public class TrainsPage {
WebDriver driver;
	

	public TrainsPage(WebDriver driver) {

        PageFactory.initElements(driver, this);
		this.driver=driver;
	}
	
	
	//Method for to click on enter from station
	
	@FindBy(xpath = "//*[text()='Enter From Station']/parent::div//input")
    private WebElement enterLocation;
    

public void enterFromLocation(String location) throws TimeoutException {
         enterLocation.clear();
         enterLocation.sendKeys(location);
         
         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
         wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='option']")));
         
         selectLocation(location);
     }
public void selectLocation(String location) throws TimeoutException {
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

//Method for to click on enter to station

	@FindBy(xpath = "//*[text()='Enter To Station']/parent::div//input")
  private WebElement entertoLocation;
  

public void enterToLocation(String location) throws TimeoutException {
	entertoLocation.clear();
	entertoLocation.sendKeys(location);
       
       WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
       wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='option']")));
       
       selectLocation(location);
   }

@FindBy(xpath = "//*[contains(@class,'DayPickerInput input')]")
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
                driver.findElement(By.xpath("(//div[@class='react-datepicker__month-container'])[1]//div[text()='"+day+"' and @aria-disabled='false']")).click();
                
                Thread.sleep(4000);
            }else {
                while(!Date.contentEquals(MonthandYear))
                {
                    Thread.sleep(500);
                    driver.findElement(By.xpath("//button[@aria-label='Next Month']")).click();
                    if(driver.findElement(By.xpath("//div[@class='react-datepicker__header ']/child::h2")).getText().contentEquals(MonthandYear))
                    {
                        driver.findElement(By.xpath("//*[@class='react-datepicker__month-container']//*[text()='"+day+"']")).click();
                        break;
                    }

                }
            }
        }

  //--------------------------------------------------------------------------------------------------------
        
        
    	//Method to check whether Home Page is displayed
   	    public void validateHomePgaeIsDisplayed(Log Log, ScreenShots ScreenShots) {
   	        try {
   	            Thread.sleep(3000);

   	            WebElement homePage = driver.findElement(By.xpath("//*[contains(@class,'train-page-title')]"));

   	            if (homePage.isDisplayed()) {
   	                System.out.println("Trains Home page is getting displayed successfully.");
   	                ScreenShots.takeScreenShot1();

   	                Log.ReportEvent("PASS", "Trains Home page is getting displayed successfully.");
   	            } else {
   	                System.out.println("Failed to load the Trains Home Page: Element found but not visible.");
   	                ScreenShots.takeScreenShot1();

   	                Log.ReportEvent("FAIL", "Failed to load the Trains Home Page: Element not visible.");
   	   		        ScreenShots.takeScreenShot1();

   	            }

   	        } catch (Exception e) {
   	            System.out.println("Exception while verifying Trains Home Page: " + e.getMessage());
   	            Log.ReportEvent("FAIL", "Exception while verifying Trains Home Page: " + e.getMessage());
   	        }

   		
   	    }

        
       //Method to click on quota
        
     public void clickQuotaDropdown() {
    	 driver.findElement(By.xpath("//*[contains(@class,'train-quota')]")).click();
     }
     
     public void enetrDropdownValues(String quota) {
    	 WebElement quotaName = driver.findElement(By.xpath("//li[text()='" + quota + "']"));
    	 quotaName.click();
     } 
     
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
     
     //Method to validate trains grid is displayed or not
     
     public void validateTrainsGrid(Log log,ScreenShots ScreenShots) {
    	    try {
    	        WebElement trainsGrid = driver.findElement(By.xpath("//*[contains(@class,'tg-train-results-card')]"));
    	        
    	        if (trainsGrid.isDisplayed()) {
    	            System.out.println("PASS: Trains grid is displayed.");
    	            log.ReportEvent("PASS", "Trains grid is displayed successfully");

    	        } else {
    	            System.out.println("FAIL: Trains grid element found but not displayed.");
    	            log.ReportEvent("FAIL", "Trains grid is Not displayed");
       		        ScreenShots.takeScreenShot1();


    	        }
    	        
    	    } catch (NoSuchElementException e) {
    	        System.out.println("FAIL: Trains grid not found.");
   		        ScreenShots.takeScreenShot1();

    	    } catch (Exception e) {
    	        System.out.println("FAIL: Exception occurred while validating trains grid - " + e.getMessage());
	            log.ReportEvent("FAIL", "Exception occurred while validating trains grid - " + e.getMessage());
   		        ScreenShots.takeScreenShot1();

    	    }
}
     
     //Method to get trains names 
     public List<String> getTrainNames() {
    	    List<WebElement> trainNameElements = driver.findElements(By.xpath("//*[contains(@class,'tg-train-name')]"));
    	    List<String> trainNames = new ArrayList<>();

    	    for (WebElement element : trainNameElements) {
    	        String name = element.getText().trim();
    	        System.out.println("Train Name: " + name);
    	        trainNames.add(name);
    	    }

    	    return trainNames;
    	}
     
     //Method to click check availability and fare based on index
     public String[] clickOnCheckAvailabilityBasedOnIndex(int index) {
    	    try {
    	        String xpath = "(//*[contains(@class,'tg-train-results-card')]//button[text()='Check Availabity And Fare'])[" + index + "]";
    	        WebElement button = driver.findElement(By.xpath(xpath));
    	        button.click();
    	        System.out.println("Clicked on 'Check Availability And Fare' button at index: " + index);
    	       
    	        
    	        WebElement resultPgOrigin = driver.findElement(By.xpath("(//*[contains(@class,'tg-train-results-card')][.//button[text()='Check Availabity And Fare']])[" + index + "]//h6[contains(@class,'tg-from-station')]"));
    	        String originText = resultPgOrigin.getText();
    	        System.out.println("origin : "+originText);
    	        
    	        WebElement resultPgDestination = driver.findElement(By.xpath("(//*[contains(@class,'tg-train-results-card')][.//button[text()='Check Availabity And Fare']])[" + index + "]//h6[contains(@class,'tg-to-station')]"));
    	        String destinationText = resultPgDestination.getText();
    	        System.out.println("destination : "+destinationText);

    	        WebElement resultPgDepartTime = driver.findElement(By.xpath("(//*[contains(@class,'tg-train-results-card')][.//button[text()='Check Availabity And Fare']])[" + index + "]//h6[contains(@class, 'tg-departure-time')]"));
    	        String DepartTimeText = resultPgDepartTime.getText();
    	        System.out.println("depart time : "+DepartTimeText);

    	        WebElement resultPgArrivalTime = driver.findElement(By.xpath("(//*[contains(@class,'tg-train-results-card')][.//button[text()='Check Availabity And Fare']])[" + index + "]//h6[contains(@class, 'tg-arrival-time')]"));
    	        String arrivalTimeText = resultPgArrivalTime.getText();
    	        System.out.println("arrival time : "+arrivalTimeText);
    	        
    	        WebElement resultPgDuration = driver.findElement(By.xpath("(//*[contains(@class,'tg-train-results-card')][.//button[text()='Check Availabity And Fare']])[" + index + "]//h6[contains(@class,'tg-train-duration')]"));
    	        String durationText = resultPgDuration.getText();
    	        System.out.println("duration : "+durationText);

    	        WebElement resultPgtrainNameText = driver.findElement(By.xpath("(//*[contains(@class,'tg-train-results-card')][.//button[text()='Check Availabity And Fare']])[" + index + "]//*[contains(@class,'tg-train-name')]"));
    	        String trainNameText = resultPgtrainNameText.getText();  // Example: "KARNATAKA EXP (12627)"

    	        // Remove train number part (everything from ' (' onwards)
    	        if (trainNameText.contains("(")) {
    	            trainNameText = trainNameText.split(" \\(")[0].trim();  // Result: "KARNATAKA EXP"
    	        }

    	        System.out.println("Train name only: " + trainNameText);


    	        return new String[] {originText, destinationText, DepartTimeText, arrivalTimeText, durationText, trainNameText};
    	        
    	    } catch (Exception e) {
    	        System.out.println("FAIL: Unable to click button at index " + index + " - " + e.getMessage());
    	        return null;
    	    }
    	}
     
     
   
    	

//     //Method to click on tier after clicking on button
//     public void selectTier(String tierName,Log log) {
//    	    try {
//    	        String xpath = "//div[@class='class-list xs-overflow']//div[text()='" + tierName + "']";
//    	        WebElement tier = driver.findElement(By.xpath(xpath));
//    	        tier.click();
//    	        log.ReportEvent("PASS", "Successfully clicked on tier: " + tierName);
//    	        
//    	       
//    	    } catch (Exception e) {
//    	        log.ReportEvent("FAIL", "Exception occurred while selecting tier '" + tierName + "' - " + e.getMessage());
//
//    	    }
//    	}
//     
//    
   //Method to click on tier after clicking on button and get tier name and price

     public String[] selectTierAndGetPrice(String tierName,Log log,ScreenShots ScreenShots) {
    	 try {
         // 1. Click on the tier
         driver.findElement(By.xpath("//div[@class='class-list xs-overflow']//div[text()='" + tierName + "']")).click();
         log.ReportEvent("PASS", "Successfully clicked on tier: " + tierName);
         // 2. Get the price
         String price = driver.findElement(By.xpath(
             "//span[text()='Total Fare']/following-sibling::h6"
         )).getText();
         
         // 3. Return both as array
         return new String[] {tierName, price};
     }
    	     catch (Exception e) {	    
    	    	 log.ReportEvent("FAIL", "Exception occurred while selecting tier '" + tierName + "' - " + e.getMessage());
    		        ScreenShots.takeScreenShot1();

    	     }
		 return null;

	    }
     
    
     
     
     
     //Method to select available classes
      
     public void clickAvailableClasses(Log log, ScreenShots ScreenShots,String... classes) throws InterruptedException {
    	    for (String availableClass : classes) {
    	        Thread.sleep(3000);
    	        try {
    	            driver.findElement(By.xpath("//*[text()='AVAILABLE CLASSES']//parent::div//span[text()='" + availableClass + "']//parent::div/parent::li//input")).click();
    	            log.ReportEvent("PASS", "Successfully clicked Available Class option: '" + availableClass + "'");
    	        } catch (Exception e) {
    	            log.ReportEvent("FAIL", "Failed to click Available Class option: '" + availableClass + "'. Exception: " + e.getMessage());
    		        ScreenShots.takeScreenShot1();

    	        }
    	    }
    	}


     
     //Method to select depart time
     
     public void clcikDepartTime(Log log,ScreenShots ScreenShots,String... time) throws InterruptedException {
    	    for (String departTime : time) {
    	        Thread.sleep(3000);
    	        try {
    	            driver.findElement(By.xpath("//*[text()='DEPART TIME']//parent::div//span[text()='" + departTime + "']//parent::div/parent::li//input")).click();
    	            log.ReportEvent("PASS", "Successfully clicked Depart Time option: '" + departTime + "'");
    	        } catch (Exception e) {
    	            log.ReportEvent("FAIL", "Failed to click Depart Time option: '" + departTime + "'. Exception: " + e.getMessage());
    		        ScreenShots.takeScreenShot1();

    	        }
    	    }
    	}

     
     //Method to select arrival time
     
     public void clcikArrivalTime(Log log,ScreenShots ScreenShots,String... time) throws InterruptedException {
    	    for (String arrivalTime : time) {
    	        Thread.sleep(3000);
    	        try {
    	            driver.findElement(By.xpath("//*[text()='ARRIVAL TIME']//parent::div//span[text()='" + arrivalTime + "']//parent::div/parent::li//input")).click();
    	            log.ReportEvent("PASS", "Successfully clicked Arrival Time option: '" + arrivalTime + "'");
    	        } catch (Exception e) {
    	            log.ReportEvent("FAIL", "Failed to click Arrival Time option: '" + arrivalTime + "'. Exception: " + e.getMessage());
    		        ScreenShots.takeScreenShot1();

    	        }
    	    }
    	}

     //Method of entire validation
     
     public void validationFromResultToBookingPage() {
    	  WebElement resultpgfromLocation = driver.findElement(By.xpath("(//div[contains(@class,' css-1dimb5e-singleValue')])[1]"));
    	  resultpgfromLocation.getText();
    	  WebElement resultpgtoLocation = driver.findElement(By.xpath("(//div[contains(@class,' css-1dimb5e-singleValue')])[2]"));
    	  resultpgtoLocation.getText();
    	  WebElement resultpgdate = driver.findElement(By.xpath("//*[contains(@class, 'DayPickerInput')]"));
    	  resultpgdate.getText();
    	  WebElement trainfromloc = driver.findElement(By.xpath("//*[contains(@class,'station-name')]"));
    	  trainfromloc.getText();
    	  WebElement traintoloc = driver.findElement(By.xpath("//*[contains(@class,'station-name')]"));
    	  traintoloc.getText();
    	 // WebElement traindate = driver.findElement(By.xpath(""));
    	  //traindate.getText();
    	  
    	  
     }
    	  
  //  method to click on book now button

     public String[] clickBookNowButtonInsideContainer(Log log,ScreenShots ScreenShots) throws TimeoutException {
    	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
    	    
    	    try {
    	        // Find all Book Now buttons
    	        List<WebElement> bookNowButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
    	            By.xpath("//button[text()='Book Now']")
    	        ));

    	        int totalButtons = bookNowButtons.size();
    	        log.ReportEvent("INFO", "Found " + totalButtons + " 'Book Now' buttons.");
    	        if (totalButtons == 0) {
    	            log.ReportEvent("FAIL", "No 'Book Now' buttons found. Cannot proceed with click.");
    		        ScreenShots.takeScreenShot1();
    	            return new String[0];
    	        }

    	        // Click random Book Now button
    	        int index = new Random().nextInt(totalButtons);
    	        log.ReportEvent("INFO", "Clicking on 'Book Now' button at index: " + index);
    	        bookNowButtons.get(index).click();
    	        log.ReportEvent("PASS", "Successfully clicked 'Book Now' button at index " + index);

    	        Thread.sleep(3000); 

    	        // Get availability information
    	        String availabilityText = wait.until(driver -> {
    	            WebElement element = driver.findElement(By.cssSelector(".train-seat-unknow"));
    	            return element.getText().trim().isEmpty() ? null : element.getText().trim();
    	        });

    	    /*    // Get date information (more robust XPath)
    	        String dateText = wait.until(driver -> {
    	            WebElement element = driver.findElement(
    	                By.xpath("//*[contains(@class,'tg-availablity-date')]"));
    	            return element.getText().trim().isEmpty() ? null : element.getText().trim();
    	        });

    	        return new String[]{availabilityText, dateText}; */
    	        
    	        return new String[]{availabilityText};

    	    } catch (NoSuchElementException e) {
    	        log.ReportEvent("FAIL", "Element not found: " + e.getMessage());
    	        return new String[0];
    	    } catch (Exception e) {
    	        log.ReportEvent("FAIL", "Unexpected error: " + e.getMessage());
    	        return new String[0];
    	    }
    	}

//     public String[] clickBookNowButtonInsideContainer(Log log,ScreenShots ScreenShots) throws TimeoutException {
//    	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
//
//    	    try {
//    	        // Find all Book Now buttons
//    	        List<WebElement> bookNowButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
//    	            By.xpath("//button[text()='Book Now']")
//    	        ));
//
//    	        int totalButtons = bookNowButtons.size();
//    	        log.ReportEvent("INFO", "Found " + totalButtons + " 'Book Now' buttons.");
//
//    	        if (totalButtons == 0) {
//    	            log.ReportEvent("FAIL", "No 'Book Now' buttons found. Cannot proceed with click.");
//    		        ScreenShots.takeScreenShot1();
//
//    	            return new String[0];
//    	        }
//
////    	        // Check if available seat element exists before clicking any Book Now button
////    	        List<WebElement> availableSeats = driver.findElements(By.xpath("//*[contains(@class,'train-seat-available')]"));
////    	        if (availableSeats.isEmpty()) {
////    	            log.ReportEvent("FAIL", "No available train seats found. Skipping click on 'Book Now' buttons.");
////    	            return new String[0];
////    	        }
//
//    	        // Click random Book Now button
//    	        int index = new Random().nextInt(totalButtons);
//    	        log.ReportEvent("INFO", "Clicking on 'Book Now' button at index: " + index);
//    	        bookNowButtons.get(index).click();
//    	        log.ReportEvent("PASS", "Successfully clicked 'Book Now' button at index " + index);
//
//    	        Thread.sleep(3000); 
//
//    	        // Get availability information
//    	        String availabilityText = wait.until(driver -> {
//    	            WebElement element = driver.findElement(By.xpath("//*[contains(@class,'tg-availablity-status')]"));
//    	            return element.getText().trim().isEmpty() ? null : element.getText().trim();
//    	        });
//    	        
//    	             // Get date information (more robust XPath)
//   	        String dateText = wait.until(driver -> {
//    	            WebElement element = driver.findElement(
//    	                By.xpath("//*[contains(@class,'tg-availablity-date')]"));
//    	            return element.getText().trim().isEmpty() ? null : element.getText().trim();
//    	        });
//
//    	        return new String[]{availabilityText, dateText};
//
//
//    	    } catch (NoSuchElementException e) {
//    	        log.ReportEvent("FAIL", "Element not found: " + e.getMessage());
//		        ScreenShots.takeScreenShot1();
//
//    	        return new String[0];
//    	    } catch (Exception e) {
//    	        log.ReportEvent("FAIL", "Unexpected error: " + e.getMessage());
//		        ScreenShots.takeScreenShot1();
//
//    	        return new String[0];
//    	    }
//    	}

     
//     public String[] clickBookNowButtonInsideContainer(Log log, ScreenShots screenShots) {
//    	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
//
//    	    try {
//    	        // Step 1: Find all Book Now buttons
//    	        List<WebElement> bookNowButtons = wait.until(
//    	            ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[text()='Book Now']"))
//    	        );
//
//    	        if (bookNowButtons.isEmpty()) {
//    	            log.ReportEvent("FAIL", "No 'Book Now' buttons found.");
//    	            screenShots.takeScreenShot1();
//    	            return new String[0];
//    	        }
//
//    	        // Step 2: Click a random Book Now button
//    	        int index = new Random().nextInt(bookNowButtons.size());
//    	        bookNowButtons.get(index).click();
//    	        log.ReportEvent("INFO", "Clicked 'Book Now' button at index: " + index);
//    	        Thread.sleep(3000); // Wait for popup or page to load
//
//    	        // Step 3: Get availability text
//    	        WebElement availabilityEl = wait.until(ExpectedConditions.visibilityOfElementLocated(
//    	        	    By.xpath("//*[contains(@class,'tg-availablity-status')]")));
//    	        
//    	        String availability = availabilityEl.getText().trim();
//
//    	        // Step 4: Get travel date text
//    	        WebElement dateEl = wait.until(ExpectedConditions.visibilityOfElementLocated(
//    	        	    By.xpath("//*[contains(@class,'tg-availablity-date')]")));
//    	        String travelDate = dateEl.getText().trim();
//
//    	        log.ReportEvent("INFO", "Popup Availability: " + availability);
//    	        log.ReportEvent("INFO", "Popup Travel Date: " + travelDate);
//
//    	        if (availability.isEmpty() || travelDate.isEmpty()) {
//    	            log.ReportEvent("FAIL", "Availability or Travel Date is empty.");
//    	            screenShots.takeScreenShot1();
//    	            return new String[0];
//    	        }
//
//    	        return new String[] { availability, travelDate };
//
//    	    } catch (Exception e) {
//    	        log.ReportEvent("FAIL", "Error while clicking Book Now: " + e.getMessage());
//    	        screenShots.takeScreenShot1();
//    	        return new String[0];
//    	    }
//    	}

     
     
		  
//Method to validate available status in booking screen
     public String getAvailableStatusTextInBookingPage(Log log,ScreenShots ScreenShots) throws InterruptedException {
    	    try {
    	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    	        WebElement availableStatusText = wait.until(
    	            ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'tg-availablity-status')]")));
    	      
    	       
    	        return availableStatusText.getText().trim();
    	    } catch (Exception e) {
    	        log.ReportEvent("FAIL", "Availability status element not found or not visible ");
		        ScreenShots.takeScreenShot1();

    	        return "";
    	    }
    	}

	  
   //Method to validate travelling date on booking screen

     public String getTravellingdateInBookingPage(ScreenShots screenShots, Log log) throws InterruptedException {
         try {
             WebElement travellingDateText = new WebDriverWait(driver, Duration.ofSeconds(50))
                 .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'tg-travel-date')]")));
             return travellingDateText.getText().trim();

         } catch (Exception e) {
             log.ReportEvent("FAIL", "Timeout waiting for travel-date element: " + e.getMessage());
             screenShots.takeScreenShot1();
             return "";  // safer than null
         }
     }

   		
   		
   		public void validateAvailabilityStatus(Log log, ScreenShots ScreenShots, String[] bookingInfo) throws InterruptedException {
   		    if (bookingInfo.length < 1) {
   		        log.ReportEvent("FAIL", "No availability information to validate.");
   		        return;
   		    }

   		    String availabilityFromPopup = bookingInfo[0];
   		    String availabilityFromBookingPage = getAvailableStatusTextInBookingPage(log, ScreenShots);

   		    log.ReportEvent("INFO", "Popup Availability: '" + availabilityFromPopup + "'");
   		    log.ReportEvent("INFO", "Booking Page Availability: '" + availabilityFromBookingPage + "'");

   		    if (availabilityFromPopup.equalsIgnoreCase(availabilityFromBookingPage)) {
   		        log.ReportEvent("PASS", "Availability text matches.");
   		    } else {
   		        log.ReportEvent("FAIL", "Availability mismatch.\nPopup: '" + availabilityFromPopup +
   		            "'\nBooking Page: '" + availabilityFromBookingPage + "'");
   		    }
   		}
   		


   		public void validateTravellingDateStatus(Log log, ScreenShots ScreenShots, String[] bookingInfo) throws InterruptedException {
   		    if (bookingInfo.length < 1) {
   		        log.ReportEvent("FAIL", "No availability information to validate.");
   		        return;
   		    }

   		    String traveldateFromPopup = bookingInfo[0];
   		    String TraveldateFromBookingPage = getTravellingdateInBookingPage(ScreenShots, log);

   		    log.ReportEvent("INFO", "Popup Travel date: '" + traveldateFromPopup + "'");
   		    log.ReportEvent("INFO", "Booking Page Travel date: '" + TraveldateFromBookingPage + "'");

   		    if (traveldateFromPopup.equalsIgnoreCase(TraveldateFromBookingPage)) {
   		        log.ReportEvent("PASS", "Travel date text matches.");
   		    } else {
   		        log.ReportEvent("FAIL", "Availability mismatch.\nPopup: '" + traveldateFromPopup +
   		            "'\nBooking Page: '" + TraveldateFromBookingPage + "'");
   		    }
   		}
   		
   		//Method to get depart time text from booking page
   		
   		public String getDepartTimeTextBookingPage() {
   		    try {
   		        WebElement departText = driver.findElement(By.xpath("//*[contains(@class,'tg-departure-time')]"));
   		        String depart = departText.getText();
   		        return depart;
   		        
   		    } catch (Exception e) {
   		        System.out.println("Error fetching departure time text: " + e.getMessage());
   		        return null;
   		    }
   		}
   		

   		//Method to get arrival time text from booking page
   		
   		public String getArrivalTimeTextBookingPage() {
   		    try {
   		        WebElement ArrivalText = driver.findElement(By.xpath("//*[contains(@class,'tg-arrival-time')]"));
   		        String arrival = ArrivalText.getText();
   		        return arrival;
   		        
   		    } catch (Exception e) {
   		        System.out.println("Error fetching arrival time text: " + e.getMessage());
   		        return null;
   		    }
   		}	
   		
//Method to get duration text from booking page
   		
   		public String getDurationTextBookingPage() {
   		    try {
   		        WebElement DurationText = driver.findElement(By.xpath("//*[contains(@class,'tg-train-duration')]"));
   		        String duration = DurationText.getText();
   		        return duration;
   		        
   		    } catch (Exception e) {
   		        System.out.println("Error fetching duration text: " + e.getMessage());
   		        return null;
   		    }
   		}	
   		
//Method to get price text from booking page
   		
   		public String getPriceTextBookingPage() {
   		    try {
   		        WebElement PriceText = driver.findElement(By.xpath("//*[contains(@class,'price')]"));
   		        String price = PriceText.getText();
   		        return price;
   		        
   		    } catch (Exception e) {
   		        System.out.println("Error fetching price text: " + e.getMessage());
   		        return null;
   		    }
   		}	
   		
//Method to get origin text from booking page
   			
   		public String getOriginTextBookingPage() {
   		    try {
   		        WebElement OriginText = driver.findElement(By.xpath("//*[contains(@class,'tg-origin-name')]"));
   		        String bookingPageOrigin = OriginText.getText();

   		        bookingPageOrigin = bookingPageOrigin.replaceAll("\\s*\\(.*\\)$", "").trim();

   		        return bookingPageOrigin;

   		    } catch (Exception e) {
   		        System.out.println("Error fetching booking Page Origin text: " + e.getMessage());
   		        return null;
   		    }
   		}

//Method to get destination text from booking page
   		
   		public String getDestinationTextBookingPage() {
   		    try {
   		        WebElement DestText = driver.findElement(By.xpath("//*[contains(@class,'tg-destination-name')]"));
   		        String bookingPageDest = DestText.getText();
   		        
   		     bookingPageDest = bookingPageDest.replaceAll("\\s*\\(.*\\)$", "").trim();

   		        return bookingPageDest;
   		        
   		    } catch (Exception e) {
   		        System.out.println("Error fetching booking Page Dest text: " + e.getMessage());
   		        return null;
   		    }
   		}	
   		
//Method to get train name  text from booking page
   	//-----can use this also but it fetches full text like train name along with train number---------------- 	
//   		public String getTrainNameTextBookingPage() {
//   		    try {
//   		        WebElement TrainNameText = driver.findElement(By.xpath("//*[contains(@class,'MuiTypography-root MuiTypography-h5 mb-16 xs-mb-0 css-13k35yx')]"));
//   		        String bookingPageTrainNameText = TrainNameText.getText();
//   		        return bookingPageTrainNameText;
//   		        
//   		    } catch (Exception e) {
//   		        System.out.println("Error fetching booking Page Train Name Text : " + e.getMessage());
//   		        return null;
//   		    }
//   		}	
   		
   		public String getTrainNameTextBookingPage() {
   		    try {
   		        WebElement TrainNameText = driver.findElement(By.xpath("//*[contains(@class,'tg-train-name')]"));
   		        String bookingPageTrainNameText = TrainNameText.getText(); // e.g., "KARNATAKA EXP / 12627"

   		        // Extract only the train name (before the slash)
   		        if (bookingPageTrainNameText.contains("/")) {
   		            bookingPageTrainNameText = bookingPageTrainNameText.split("/")[0].trim(); // "KARNATAKA EXP"
   		        }

   		        return bookingPageTrainNameText;

   		    } catch (Exception e) {
   		        System.out.println("Error fetching booking Page Train Name Text : " + e.getMessage());
   		        return null;
   		    }
   		}

   		
//Method to get tier name   text from booking page
   		
   		public String getTierNameTextBookingPage() {
   		    try {
   		        WebElement TierNameText = driver.findElement(By.xpath("//*[contains(@class,'tg-train-class')]"));
   		        String bookingPageTierNameText = TierNameText.getText();
   		        return bookingPageTierNameText;
   		        
   		    } catch (Exception e) {
   		        System.out.println("Error fetching booking Page tier Name Text : " + e.getMessage());
   		        return null;
   		    }
   		}	
   		
   		
//   		public void compareBookingPageDetailsWithResultCard(int index) {
//   		    try {
//   		        // Get result card details after clicking based on index
//   		        String[] resultCardDetails = clickOnCheckAvailabilityBasedOnIndex(index);
//   		        if (resultCardDetails == null) {
//   		            System.out.println("FAIL: No details returned from result card for index " + index);
//   		            return;
//   		        }
//
//   		        // Fetch details from the booking page
//   		        String bookingOrigin = getOriginTextBookingPage();
//   		        String bookingDestination = getDestinationTextBookingPage();
//   		        String bookingDepartTime = getDepartTimeTextBookingPage();
//   		        String bookingArrivalTime = getArrivalTimeTextBookingPage();
//   		        String bookingPrice = getPriceTextBookingPage();
//   		        String bookingDuration = getDurationTextBookingPage();
//
//   		        // Compare and print result
//   		        compareText("Origin", bookingOrigin, resultCardDetails[0]);
//   		        compareText("Destination", bookingDestination, resultCardDetails[1]);
//   		        compareText("Departure Time", bookingDepartTime, resultCardDetails[2]);
//   		        compareText("Arrival Time", bookingArrivalTime, resultCardDetails[3]);
//   		        compareText("Price", bookingPrice, resultCardDetails[4]);
//   		        compareText("Duration", bookingDuration, resultCardDetails[5]);
//
//   		    } catch (Exception e) {
//   		        System.out.println("FAIL: Exception occurred while comparing details - " + e.getMessage());
//   		    }
//   		}
//
//   		private void compareText(String label, String expected, String actual) {
//   		    if (expected == null || actual == null) {
//   		        System.out.println("FAIL: " + label + " - one of the values is null");
//   		    } else if (expected.trim().equalsIgnoreCase(actual.trim())) {
//   		        System.out.println("PASS: " + label + " matched: " + actual);
//   		    } else {
//   		        System.out.println("FAIL: " + label + " mismatch. Expected: '" + expected + "' but got: '" + actual + "'");
//   		    }
//   		}
//
//

   		public void compareBookingPageDetailsWithResultCard(String[] resultCardDetails, Log log,ScreenShots ScreenShots) {
   		    try {
   		        if (resultCardDetails == null || resultCardDetails.length < 6) {
   		            log.ReportEvent("FAIL", "Result card details are incomplete or null.");
   		            return;
   		        }

   		        // Fetch details from the booking page
   		        String bookingOrigin = getOriginTextBookingPage();
   		        String bookingDestination = getDestinationTextBookingPage();
   		        String bookingDepartTime = getDepartTimeTextBookingPage();
   		        String bookingArrivalTime = getArrivalTimeTextBookingPage();
   		        String bookingDuration = getDurationTextBookingPage();
   		        String bookingTrainNameText = getTrainNameTextBookingPage();

   		        // === Origin ===
   		        if (bookingOrigin == null || resultCardDetails[0] == null) {
   		            log.ReportEvent("FAIL", "Origin - one of the values is null");
   		        } else if (bookingOrigin.trim().equalsIgnoreCase(resultCardDetails[0].trim())) {
   		            log.ReportEvent("PASS", "Origin matches: " + bookingOrigin);
   		        } else {
   		            log.ReportEvent("FAIL", "Origin mismatch. Booking: '" + bookingOrigin + "', Expected: '" + resultCardDetails[0] + "'");
   			        ScreenShots.takeScreenShot1();

   		        }

   		        // === Destination ===
   		        if (bookingDestination == null || resultCardDetails[1] == null) {
   		            log.ReportEvent("FAIL", "Destination - one of the values is null");
   		        } else if (bookingDestination.trim().equalsIgnoreCase(resultCardDetails[1].trim())) {
   		            log.ReportEvent("PASS", "Destination matches: " + bookingDestination);
   		        } else {
   		            log.ReportEvent("FAIL", "Destination mismatch. Booking: '" + bookingDestination + "', Expected: '" + resultCardDetails[1] + "'");
   			        ScreenShots.takeScreenShot1();

   		        }

   		        // === Departure Time ===
   		        if (bookingDepartTime == null || resultCardDetails[2] == null) {
   		            log.ReportEvent("FAIL", "Departure Time - one of the values is null");
   		        } else if (bookingDepartTime.trim().equalsIgnoreCase(resultCardDetails[2].trim())) {
   		            log.ReportEvent("PASS", "Departure Time matches: " + bookingDepartTime);
   		        } else {
   		            log.ReportEvent("FAIL", "Departure Time mismatch. Booking: '" + bookingDepartTime + "', Expected: '" + resultCardDetails[2] + "'");
   			        ScreenShots.takeScreenShot1();

   		        }

   		        // === Arrival Time ===
   		        if (bookingArrivalTime == null || resultCardDetails[3] == null) {
   		            log.ReportEvent("FAIL", "Arrival Time - one of the values is null");
   		        } else if (bookingArrivalTime.trim().equalsIgnoreCase(resultCardDetails[3].trim())) {
   		            log.ReportEvent("PASS", "Arrival Time matches: " + bookingArrivalTime);
   		        } else {
   		            log.ReportEvent("FAIL", "Arrival Time mismatch. Booking: '" + bookingArrivalTime + "', Expected: '" + resultCardDetails[3] + "'");
   			        ScreenShots.takeScreenShot1();

   		        }

   		        // === Duration ===
   		        if (bookingDuration == null || resultCardDetails[4] == null) {
   		            log.ReportEvent("FAIL", "Duration - one of the values is null");
   		        } else if (bookingDuration.trim().equalsIgnoreCase(resultCardDetails[4].trim())) {
   		            log.ReportEvent("PASS", "Duration matches: " + bookingDuration);
   		        } else {
   		            log.ReportEvent("FAIL", "Duration mismatch. Booking: '" + bookingDuration + "', Expected: '" + resultCardDetails[4] + "'");
   			        ScreenShots.takeScreenShot1();

   		        }

   		        // === Train Name ===
   		        if (bookingTrainNameText == null || resultCardDetails[5] == null) {
   		            log.ReportEvent("FAIL", "Train Name - one of the values is null");
   		        } else if (bookingTrainNameText.trim().equalsIgnoreCase(resultCardDetails[5].trim())) {
   		            log.ReportEvent("PASS", "Train Name matches: " + bookingTrainNameText);
   		        } else {
   		            log.ReportEvent("FAIL", "Train Name mismatch. Booking: '" + bookingTrainNameText + "', Expected: '" + resultCardDetails[5] + "'");
   			        ScreenShots.takeScreenShot1();

   		        }

   		    } catch (Exception e) {
   		        log.ReportEvent("FAIL", "Exception during booking page comparison: " + e.getMessage());
		        ScreenShots.takeScreenShot1();

   		    }
   		}

   		
   		//Method for to validate tier and price text from result page to booking page 
   		public void compareTierAndPriceWithBookingPage(String[] selectedTierDetails, Log log,ScreenShots ScreenShots) {
   		    try {
   		        if (selectedTierDetails == null || selectedTierDetails.length < 2) {
   		            log.ReportEvent("FAIL", "Selected tier details are incomplete or null.");
   			        ScreenShots.takeScreenShot1();

   		            return;
   		        }

   		        String resultpgTierName = selectedTierDetails[0];
   		        String resultpgPrice = selectedTierDetails[1];

   		        String BookingpgTierName = getTierNameTextBookingPage();
   		        String bookingpgPrice = getPriceTextBookingPage();

   		        // === Tier Name Validation ===
   		        if (BookingpgTierName == null) {
   		            log.ReportEvent("FAIL", "Tier name on booking page is null.");
   			        ScreenShots.takeScreenShot1();

   		        } else if (BookingpgTierName.trim().equalsIgnoreCase(resultpgTierName.trim())) {
   		            log.ReportEvent("PASS", "Tier name matches: " + BookingpgTierName);
   		        } else {
   		            log.ReportEvent("FAIL", "Tier name mismatch. Booking: '" + BookingpgTierName + "', Expected: '" + resultpgTierName + "'");
   			        ScreenShots.takeScreenShot1();

   		        }

   		        // === Price Validation ===
   		        if (bookingpgPrice == null) {
   		            log.ReportEvent("FAIL", "Price on booking page is null.");
   			        ScreenShots.takeScreenShot1();

   		        } else if (bookingpgPrice.trim().equalsIgnoreCase(resultpgPrice.trim())) {
   		            log.ReportEvent("PASS", "Price matches: " + bookingpgPrice);
   		        } else {
   		            log.ReportEvent("FAIL", "Price mismatch. Booking: '" + bookingpgPrice + "', Expected: '" + resultpgPrice + "'");
   			        ScreenShots.takeScreenShot1();

   		        }

   		    } catch (Exception e) {
   		        log.ReportEvent("FAIL", "Exception during tier and price comparison: " + e.getMessage());
		        ScreenShots.takeScreenShot1();

   		    }
   		}
   		
   		public void clickBoardingPointBookingpg(Log log,ScreenShots ScreenShots) {
   		    try {
   		        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
   		        JavascriptExecutor js = (JavascriptExecutor) driver;

   		        //Click the dropdown
   		        WebElement bookingDropdown = wait.until(ExpectedConditions.elementToBeClickable(
   		            By.xpath("//label[text()='Select Boarding Station']/following-sibling::div")));
   		        bookingDropdown.click();
   		        log.ReportEvent("PASS", "Clicked on the Boarding Point dropdown on Booking Page.");
Thread.sleep(4000);
   		        List<WebElement> dropdownOptions = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
   		            By.xpath("//*[contains(@class,'tg-select__option css-10wo9uf-option')]")));

   		        // Picking a random option
   		        Random rand = new Random();
   		        int index = rand.nextInt(dropdownOptions.size());
   		        WebElement selectedOption = dropdownOptions.get(index);
   		        String selectedText = selectedOption.getText();

   		        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", selectedOption);
   		        Thread.sleep(500); // Optional pause to allow scroll to finish

   		        try {
   		        	Thread.sleep(2000);
   		            selectedOption.click(); 
   		            log.ReportEvent("PASS", "Selected random boarding point: " + selectedText);
   		        } catch (ElementClickInterceptedException ex) {
   		            js.executeScript("arguments[0].click();", selectedOption);
   		            log.ReportEvent("PASS", "Selected random boarding point using JS click: " + selectedText);
   		        }

   		    } catch (Exception e) {
   		        log.ReportEvent("FAIL", "Failed to select boarding point. Exception: " + e.getMessage());
		        ScreenShots.takeScreenShot1();

   		    }
   		}

   		//Click on traveller details
 
//   		public void enterTravellerDetails() throws InterruptedException {
//   		    Random random = new Random();
//
//   		    List<WebElement> travelers = driver.findElements(By.xpath("//*[contains(@class,'MuiTypography-root MuiTypography-subtitle1 bold mb-8 css-17jtg62')]"));
//   		    int travellerCount = travelers.size();
//
//   		    // If only 1 traveler
//   		    if (travellerCount == 1) {
//   		        WebElement traveler = travelers.get(0); //get(0) means 1st traveler
//
//   		        // ID Card Type
//   		        try {
//   		        	Thread.sleep(3000);
//   		            WebElement idCardType = traveler.findElement(By.xpath(".//input[@name='idcardtype']"));
//   		            idCardType.click();
//   		            List<WebElement> options = driver.findElements(By.xpath("//ul//li"));
//   		            if (!options.isEmpty()) {
//   		                options.get(random.nextInt(options.size())).click();
//   		            }
//   		        } catch (Exception ignored) {}
//
//   		        // ID Card Number
//   		        try {
//   		            WebElement idCardNumber = traveler.findElement(By.xpath(".//input[@name='idcardnumber']"));
//   		            idCardNumber.clear();
//   		            idCardNumber.sendKeys("ID" + random.nextInt(999999));
//   		        } catch (Exception ignored) {}
//
//   		        // Seat Preference
//   		        try {
//   		            WebElement seat = traveler.findElement(By.xpath(".//input[@name='seatpref']"));
//   		            seat.click();
//   		            List<WebElement> options = driver.findElements(By.xpath("//ul//li"));
//   		            if (!options.isEmpty()) {
//   		                options.get(random.nextInt(options.size())).click();
//   		            }
//   		        } catch (Exception ignored) {}
//
//   		        // Meal Preference (only if present)
//   		        try {
//   		            List<WebElement> meals = traveler.findElements(By.xpath(".//input[@name='mealpref']"));
//   		            if (!meals.isEmpty()) {
//   		                WebElement meal = meals.get(0);
//   		                meal.click();
//   		                List<WebElement> options = driver.findElements(By.xpath("//ul//li"));
//   		                if (!options.isEmpty()) {
//   		                    options.get(random.nextInt(options.size())).click();
//   		                }
//   		            }
//   		        } catch (Exception ignored) {}
//   		    }
//
//   		    // If more than 1 traveler
//   		    if (travellerCount > 1) {
//   		    	Thread.sleep(3000);
//   		        for (WebElement traveler : travelers) {
//   		            // Title
//   		            try {
//   	   		        	Thread.sleep(3000);
//
//   		                WebElement title = traveler.findElement(By.xpath(".//input[@name='title']"));
//   		                title.click();
//   		                List<WebElement> options = driver.findElements(By.xpath("//ul//li"));
//   		                if (!options.isEmpty()) {
//   		                    options.get(random.nextInt(options.size())).click();
//   		                }
//   		            } catch (Exception ignored) {}
//
//   		            // First Name
//   		            try {
//   		                WebElement firstName = traveler.findElement(By.xpath(".//input[@name='firstname']"));
//   		                firstName.clear();
//   		                firstName.sendKeys("First" + random.nextInt(1000));
//   		            } catch (Exception ignored) {}
//
//   		            // Last Name
//   		            try {
//   		                WebElement lastName = traveler.findElement(By.xpath(".//input[@name='lastname']"));
//   		                lastName.clear();
//   		                lastName.sendKeys("Last" + random.nextInt(1000));
//   		            } catch (Exception ignored) {}
//
//   		            // Age
//   		            try {
//   		                WebElement age = traveler.findElement(By.xpath(".//input[@name='age']"));
//   		                age.clear();
//   		                age.sendKeys(String.valueOf(18 + random.nextInt(50)));
//   		            } catch (Exception ignored) {}
//
//   		            // Gender
//   		            try {
//   		                WebElement gender = traveler.findElement(By.xpath(".//input[@name='gender']"));
//   		                gender.click();
//   		                List<WebElement> options = driver.findElements(By.xpath("//ul//li"));
//   		                if (!options.isEmpty()) {
//   		                    options.get(random.nextInt(options.size())).click();
//   		                }
//   		            } catch (Exception ignored) {}
//
//   		            // ID Card Type
//   		            try {
//   		                WebElement idCardType = traveler.findElement(By.xpath(".//input[@name='idcardtype']"));
//   		                idCardType.click();
//   		                List<WebElement> options = driver.findElements(By.xpath("//ul//li"));
//   		                if (!options.isEmpty()) {
//   		                    options.get(random.nextInt(options.size())).click();
//   		                }
//   		            } catch (Exception ignored) {}
//
//   		            // ID Card Number
//   		            try {
//   		                WebElement idCardNumber = traveler.findElement(By.xpath(".//input[@name='idcardnumber']"));
//   		                idCardNumber.clear();
//   		                idCardNumber.sendKeys("ID" + random.nextInt(999999));
//   		            } catch (Exception ignored) {}
//
//   		            // Seat Preference
//   		            try {
//   		                WebElement seat = traveler.findElement(By.xpath(".//input[@name='seatpref']"));
//   		                seat.click();
//   		                List<WebElement> options = driver.findElements(By.xpath("//ul//li"));
//   		                if (!options.isEmpty()) {
//   		                    options.get(random.nextInt(options.size())).click();
//   		                }
//   		            } catch (Exception ignored) {}
//
//   		            // Meal Preference (only if present)
//   		            try {
//   		                List<WebElement> meals = traveler.findElements(By.xpath(".//input[@name='mealpref']"));
//   		                if (!meals.isEmpty()) {
//   		                    WebElement meal = meals.get(0);
//   		                    meal.click();
//   		                    List<WebElement> options = driver.findElements(By.xpath("//ul//li"));
//   		                    if (!options.isEmpty()) {
//   		                        options.get(random.nextInt(options.size())).click();
//   		                    }
//   		                }
//   		            } catch (Exception ignored) {}
//   		        }
//   		    }
//   		}
//   		
//   		
//   		
//   		
   		//Method to add travellers
   		public void clickAddTravellers(int totalTravellerCount, Log log) {
   		    if (totalTravellerCount <= 1) {
   		        log.ReportEvent("INFO", "No additional travellers to add. Total travellers: " + totalTravellerCount);
   		        return;
   		    }

   		    for (int i = 1; i < totalTravellerCount; i++) {
   		        WebElement addTravellerBtn = driver.findElement(By.xpath("//strong[text()='Add Traveller']"));
   		        
   		        // Scroll to the button before clicking
   		        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", addTravellerBtn);
   		        
   		        addTravellerBtn.click();
   		        log.ReportEvent("PASS", "Clicked 'Add Traveller' button " + i + " time(s)");
   		        
   		        try {
   		            Thread.sleep(500);
   		        } catch (InterruptedException e) {
   		            // Optional: handle or log the interruption
   		        }
   		    }
   		}

   		public void enterTravellerDetails() throws InterruptedException {
   		    Random random = new Random();
   		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

   		    // Find traveler containers (adjust this XPath if needed to target the form section)
   		    List<WebElement> travelers = driver.findElements(By.xpath("//*[contains(@class,'MuiTypography-root MuiTypography-subtitle1 bold mb-8 css-17jtg62')]"));
   		    int travellerCount = travelers.size();
   		    System.out.println("Found " + travellerCount + " traveler(s)");

   		    for (int i = 0; i < travellerCount; i++) {
   		        WebElement traveler = travelers.get(i);

   		        // Scroll to traveler element
   		        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", traveler);
   		        Thread.sleep(1000);

   		        System.out.println("Filling details for Traveler " + (i + 1));

   		        // For travelers > 1, fill full details from Title to Gender
   		        if (i > 0) {
   		            // Title
   		            try {
   		                WebElement title = traveler.findElement(By.xpath(""));
   		                clickElementWithWait(title, wait);
   		                selectRandomOptionFromVisibleDropdown(wait);
   		            } catch (Exception e) {
   		                System.out.println("Title not found or not interactable for traveler " + (i + 1));
   		            }

   		            // First Name
   		            try {
   		                WebElement firstName = traveler.findElement(By.xpath(""));
   		                wait.until(ExpectedConditions.visibilityOf(firstName)).clear();
   		                firstName.sendKeys("First" + random.nextInt(1000));
   		            } catch (Exception e) {
   		                System.out.println("First Name not found or not interactable for traveler " + (i + 1));
   		            }

   		            // Last Name
   		            try {
   		                WebElement lastName = traveler.findElement(By.xpath(""));
   		                wait.until(ExpectedConditions.visibilityOf(lastName)).clear();
   		                lastName.sendKeys("Last" + random.nextInt(1000));
   		            } catch (Exception e) {
   		                System.out.println("Last Name not found or not interactable for traveler " + (i + 1));
   		            }

   		            // Age
   		            try {
   		                WebElement age = traveler.findElement(By.xpath(""));
   		                wait.until(ExpectedConditions.visibilityOf(age)).clear();
   		                age.sendKeys(String.valueOf(18 + random.nextInt(50)));
   		            } catch (Exception e) {
   		                System.out.println("Age not found or not interactable for traveler " + (i + 1));
   		            }

   		            // Gender
   		            try {
   		                WebElement gender = traveler.findElement(By.xpath(""));
   		                clickElementWithWait(gender, wait);
   		                selectRandomOptionFromVisibleDropdown(wait);
   		            } catch (Exception e) {
   		                System.out.println("Gender not found or not interactable for traveler " + (i + 1));
   		            }
   		        }

   		        // For all travelers (including traveler 1): ID Card Type, ID Card Number, Seat Pref, Meal Pref (if any)

   		        // ID Card Type
   		     try {
   		      List<WebElement> idCardTypes = driver.findElements(By.xpath(""));
   		      if (idCardTypes.size() > i) {
   		          WebElement idCardType = idCardTypes.get(i);
   		          clickElementWithWait(idCardType, wait);
   		          selectRandomOptionFromVisibleDropdown(wait);
   		      } else {
   		          System.out.println("ID Card Type input not found for traveler " + (i + 1));
   		      }
   		  } catch (Exception e) {
   		      System.out.println("Exception handling ID Card Type for traveler " + (i + 1) + ": " + e.getMessage());
   		  }


   		        // ID Card Number
   		        try {
   		            WebElement idCardNumber = traveler.findElement(By.xpath(""));
   		            wait.until(ExpectedConditions.visibilityOf(idCardNumber)).clear();
   		            idCardNumber.sendKeys("ID" + random.nextInt(999999));
   		        } catch (Exception e) {
   		            System.out.println("ID Card Number not found or not interactable for traveler " + (i + 1));
   		        }

   		        // Seat Preference
   		        try {
   		            WebElement seat = traveler.findElement(By.xpath(""));
   		            clickElementWithWait(seat, wait);
   		            selectRandomOptionFromVisibleDropdown(wait);
   		        } catch (Exception e) {
   		            System.out.println("Seat Preference not found or not interactable for traveler " + (i + 1));
   		        }

   		        // Meal Preference (optional)
   		        try {
   		            List<WebElement> meals = traveler.findElements(By.xpath(""));
   		            if (!meals.isEmpty()) {
   		                WebElement meal = meals.get(0);
   		                clickElementWithWait(meal, wait);
   		                selectRandomOptionFromVisibleDropdown(wait);
   		            }
   		        } catch (Exception e) {
   		            System.out.println("Meal Preference not found or not interactable for traveler " + (i + 1));
   		        }
   		    }
   		}

   		// Helper method to click element with wait and JS fallback
   		private void clickElementWithWait(WebElement element, WebDriverWait wait) {
   		    try {
   		        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
   		        Thread.sleep(500); // wait for dropdown/options to render
   		    } catch (Exception e) {
   		        // fallback JS click
   		        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
   		        try {
   		            Thread.sleep(500);
   		        } catch (InterruptedException ie) {
   		            // ignore
   		        }
   		    }
   		}

   		// Helper method to select random option from visible dropdown menu
   		private void selectRandomOptionFromVisibleDropdown(WebDriverWait wait) {
   		    try {
   		        // Wait for the dropdown menu that is visible and enabled (filter out hidden)
   		        WebElement dropdownMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(
   		            By.xpath("//ul[contains(@class,'MuiMenu-list') and not(contains(@style,'display: none'))]")
   		        ));
   		        List<WebElement> options = dropdownMenu.findElements(By.tagName("li"));
   		        if (!options.isEmpty()) {
   		            options.get(new Random().nextInt(options.size())).click();
   		            Thread.sleep(500); // small wait after selecting option
   		        }
   		    } catch (Exception e) {
   		        System.out.println("Dropdown options not found or not interactable");
   		    }
   		}

   		
   		//method to click on send approval button
   		
   		public void clickSendApprovalButton() throws InterruptedException {
   			Thread.sleep(3000);
   			driver.findElement(By.xpath("//button[text()='Send for Approval']")).click();
   		}
   		
   		//method to validate send for approval
   		public void validateApprovalPageIsDisplayingOrNot() throws InterruptedException {
   			Thread.sleep(5000);
   			WebElement approvalText = driver.findElement(By.xpath("//h5[text()='My Requests Awaiting Approval']"));
   			if(approvalText.isDisplayed()) {
   				System.out.println("PASS");
   			}else {
   				System.out.println("fail");
   			}
   		}

   		//Method to click and validate Train name filter ascending
   		
   		public void clickTrainNameFilterAscending() throws InterruptedException {
   			JavascriptExecutor js = (JavascriptExecutor) driver;
            //Scroll to top
			    js.executeScript("window.scrollBy(0, -document.body.scrollHeight)");
   			Thread.sleep(3000);
   			WebElement asc = driver.findElement(By.xpath("//button[text()='Train Name']"));
   			asc.click();
   		}
   		
   		public void validateTrainNameFilterAscending(Log log,ScreenShots ScreenShots) {
   		    // Get all train name elements
   		    List<WebElement> trainNameElements = driver.findElements(
   		        By.xpath("//*[contains(@class,'tg-train-name')]"));

   		    // Store train names as text
   		    List<String> actualNames = new ArrayList<>();
   		    for (WebElement element : trainNameElements) {
   		        actualNames.add(element.getText().trim());
   		    }

   		    // Create a copy and sort it alphabetically
   		    List<String> sortedTrainNames = new ArrayList<>(actualNames);
   		    Collections.sort(sortedTrainNames, String.CASE_INSENSITIVE_ORDER);  //Collections.sort--by default sorts a list in ascending order.

   		    // Compare actual with sorted
   		    if (actualNames.equals(sortedTrainNames)) {
   		        log.ReportEvent("PASS", "Train names are sorted in ascending alphabetical order.");
   		    } else {
   		        log.ReportEvent("FAIL", "Train names are NOT sorted in ascending order. Actual: " + actualNames + ", Expected: " + sortedTrainNames);
		        ScreenShots.takeScreenShot1();

   		    }
   		}

   		//Method to click and validate Train name filter descending
   		
   		public void clickTrainNameFilterDescending() throws InterruptedException {
   			Thread.sleep(3000);
   			JavascriptExecutor js = (JavascriptExecutor) driver;
               //Scroll to top
   			    js.executeScript("window.scrollBy(0, -document.body.scrollHeight)");
   		    WebElement trainNameFilter = driver.findElement(By.xpath("//button[text()='Train Name']"));

   		    for (int i = 0; i <= 2; i++) {
   		        trainNameFilter.click();
   		    }
   		}

   		
   		public void validateTrainNameFilterDescending(Log log,ScreenShots ScreenShots) {
   		    // Get all train name elements
   			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
   			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'tg-train-name')]")));
   					
   		
   		    List<WebElement> trainNameElements = driver.findElements(
   		        By.xpath("//*[contains(@class,'tg-train-name')]"));

   		    // Store train names as text
   		    List<String> actualNames = new ArrayList<>();
   		    for (WebElement element : trainNameElements) {
   		        actualNames.add(element.getText().trim());
   		    }

   		    // Create a copy and sort it in descending alphabetical order
   		    List<String> sortedDescending = new ArrayList<>(actualNames);
   		    sortedDescending.sort(Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER));

   		    // Compare actual with expected descending list
   		    if (actualNames.equals(sortedDescending)) {
   		        log.ReportEvent("PASS", "Train names are sorted in descending alphabetical order.");
   		    } else {
   		        log.ReportEvent("FAIL", "Train names are NOT sorted in descending order.\nActual: " + actualNames + "\nExpected: " + sortedDescending);
		        ScreenShots.takeScreenShot1();

   		    }
   		}

//Method to click and validate departure filter ascending
   		
   		public void clickDepartureFilterAscending() throws InterruptedException {
   			Thread.sleep(3000);
   			JavascriptExecutor js = (JavascriptExecutor) driver;
            //Scroll to top
			    js.executeScript("window.scrollBy(0, -document.body.scrollHeight)");
   			WebElement asc = driver.findElement(By.xpath("//button[text()='Departure']"));
   			asc.click();
   		}
   		
   		//Method to validate departure ascending filter
   	
   		public void validateDepatureAscendingFIlter(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);
   		    // Get departure time elements
   		    List<WebElement> departureElements = driver.findElements(By.xpath("//*[contains(@class,'tg-departure-time')]")); 

   		    List<String> actualTimes = new ArrayList<>();
   		    for (WebElement element : departureElements) {
   		        actualTimes.add(element.getText().trim());  // e.g., "14:45", "19:20"
   		    }

   		    // Copy and sort for comparison
   		    List<String> expectedTimes = new ArrayList<>(actualTimes);
   		    Collections.sort(expectedTimes); // Lexicographic sort works for HH:mm

   		    // Log and compare
   		    if (actualTimes.equals(expectedTimes)) {
   		        log.ReportEvent("PASS", "Departure times are sorted in ascending order: " + actualTimes);
   		    } else {
   		        log.ReportEvent("FAIL", "Departure times are NOT in ascending order.\nActual: " + actualTimes + "\nExpected: " + expectedTimes);
		        ScreenShots.takeScreenShot1();

   		    }
   		}

   	
   		
//Method to click and validate departure filter descending
   		
   		public void clickdepatureFilterDescending() throws InterruptedException {
   			Thread.sleep(3000);
   			JavascriptExecutor js = (JavascriptExecutor) driver;
            //Scroll to top
			    js.executeScript("window.scrollBy(0, -document.body.scrollHeight)");
   		    WebElement departFilter = driver.findElement(By.xpath("//button[text()='Departure']"));

   		    for (int i = 0; i <= 2; i++) {
   		    	departFilter.click();
   		    }
   		}	
   		
   		//Method to valiadte for departure time descending order
   		
   		public void validateDepatureDescendingFilter(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   		    // Get the list of departure time elements 
   		    List<WebElement> departureElements = driver.findElements(By.xpath("//*[contains(@class,'tg-departure-time')]")); 

   		    List<String> actualTimes = new ArrayList<>();

   		    for (WebElement element : departureElements) {
   		        actualTimes.add(element.getText().trim());
   		    }

   		    log.ReportEvent("INFO", "Actual Departure Times from card: " + actualTimes);

   		    // sort it in descending order
   		    List<String> expectedTimes = new ArrayList<>(actualTimes);
   		    Collections.sort(expectedTimes, Collections.reverseOrder());

   		    log.ReportEvent("INFO", "Expected Sorted Departure Times (Descending): " + expectedTimes);

   		    //  Validate if the actual list is equal to sorted (descending) list
   		    if (actualTimes.equals(expectedTimes)) {
   		        log.ReportEvent("PASS", "Departure times are sorted in descending order.");
   		    } else {
   		        log.ReportEvent("FAIL", "Departure times are NOT in descending order.\nActual: " + actualTimes + "\nExpected: " + expectedTimes);
		        ScreenShots.takeScreenShot1();

   		    }
   		}

   		
//Method to click and validate arrival filter ascending
   		
   		public void clickArrivalFilterAscending() throws InterruptedException {
   			Thread.sleep(3000);
   			JavascriptExecutor js = (JavascriptExecutor) driver;
            //Scroll to top
			    js.executeScript("window.scrollBy(0, -document.body.scrollHeight)");
   			WebElement asc = driver.findElement(By.xpath("//button[text()='Arrival']"));
   			asc.click();
   		}	
   		
   		
   	//Method to validate arrival ascending filter
   	   	
   		public void validateArrivalAscendingFIlter(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   		    // Get arrival time elements
   		    List<WebElement> arrivalElements = driver.findElements(By.xpath("//*[contains(@class,'tg-arrival-time')]")); 

   		    List<String> actualTimes = new ArrayList<>();
   		    for (WebElement element : arrivalElements) {
   		        actualTimes.add(element.getText().trim());  // e.g., "14:45", "19:20"
   		    }

   		    // Copy and sort for comparison
   		    List<String> expectedTimes = new ArrayList<>(actualTimes);
   		    Collections.sort(expectedTimes); // Lexicographic sort works for HH:mm

   		    if (actualTimes.equals(expectedTimes)) {
   		        log.ReportEvent("PASS", "Arrival times are sorted in ascending order: " + actualTimes);
   		    } else {
   		        log.ReportEvent("FAIL", "Arrival times are NOT in ascending order.\nActual: " + actualTimes + "\nExpected: " + expectedTimes);
		        ScreenShots.takeScreenShot1();

   		    }
   		}
   		
//Method to click and validate arrival filter descending
   		
   		public void clickArrivalFilterDescending() throws InterruptedException {
   			Thread.sleep(3000);
   			JavascriptExecutor js = (JavascriptExecutor) driver;
            //Scroll to top
			    js.executeScript("window.scrollBy(0, -document.body.scrollHeight)");
   		    WebElement arrivalFilter = driver.findElement(By.xpath("//button[text()='Arrival']"));

   		    for (int i = 0; i <= 2; i++) {
   		    	arrivalFilter.click();
   		    }
   		}	
   		
//Method to validate for arrival time descending order
   		
   		public void validateArrivalDescendingFilter(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   		    // Get the list of arrival time elements 
   		    List<WebElement> arrivalElements = driver.findElements(By.xpath("//*[contains(@class,'tg-arrival-time')]")); 

   		    List<String> actualTimes = new ArrayList<>();

   		    for (WebElement element : arrivalElements) {
   		        actualTimes.add(element.getText().trim());
   		    }

   		    log.ReportEvent("INFO", "Actual arrival Times from card : " + actualTimes);

   		    // sort it in descending order
   		    List<String> expectedTimes = new ArrayList<>(actualTimes);
   		    Collections.sort(expectedTimes, Collections.reverseOrder());

   		    log.ReportEvent("INFO", "Expected Sorted Arrival Times (Descending): " + expectedTimes);

   		    //  Validate if the actual list is equal to sorted (descending) list
   		    if (actualTimes.equals(expectedTimes)) {
   		        log.ReportEvent("PASS", "Arrival times are sorted in descending order.");
   		    } else {
   		        log.ReportEvent("FAIL", "Arrival times are NOT in descending order.\nActual: " + actualTimes + "\nExpected: " + expectedTimes);
		        ScreenShots.takeScreenShot1();

   		    }
   		}
   		
   		//Method to validate "Depart time before 6AM"
   		
   		public void validateDepartTimeBefore6AM(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   		    List<WebElement> timeElements = driver.findElements(
   		        By.xpath("//*[contains(@class,'tg-departure-time')]")
   		    );

   		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
   		    LocalTime sixAM = LocalTime.of(6, 0);
   		    LocalTime midnight = LocalTime.MIDNIGHT; // 00:00

   		    boolean allValid = true;

   		    for (WebElement element : timeElements) {
   		        String timeText = element.getText().trim(); 

   		        try {
   		            LocalTime departureTime = LocalTime.parse(timeText, formatter);

   		            if (!departureTime.isBefore(sixAM)) {
   		            	Thread.sleep(2000);
   		                log.ReportEvent("FAIL", "One or more Departure time " + timeText + " is NOT before 6:00 AM.");
   		   		        ScreenShots.takeScreenShot1();
   		                allValid = false;
   		            } else {
   		                log.ReportEvent("INFO", "Departure time " + timeText + " is valid (before 6:00 AM).");
   		            }

   		        } catch (DateTimeParseException e) {
   		            log.ReportEvent("FAIL", "Invalid time format found: '" + timeText + "'. Error: " + e.getMessage());
   	   		        ScreenShots.takeScreenShot1();

   		            allValid = false;
   		        }
   		    }

   		    if (allValid) {
   		        log.ReportEvent("PASS", "All departure times are between 12:00 AM and 6:00 AM.");
   		    } else {
   		        log.ReportEvent("FAIL", "One or more departure times are NOT between 12:00 AM and 6:00 AM.");
   		        ScreenShots.takeScreenShot1();

   		    }
   		}

   		//Method to validate "Depart time 6AM TO 12PM"
   		
   		public void validateDepartTime6AMTo12PM(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   		    // Find all time elements
   		    List<WebElement> timeElements = driver.findElements(
   		        By.xpath("//*[contains(@class,'tg-departure-time')]")
   		    );

   		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
   		    LocalTime startTime = LocalTime.of(6, 0);   // 06:00 AM
   		    LocalTime endTime = LocalTime.of(12, 0);    // 12:00 PM

   		    boolean allValid = true;

   		    for (WebElement element : timeElements) {
   		        String timeText = element.getText().trim();

   		        try {
   		            LocalTime departureTime = LocalTime.parse(timeText, formatter);

   		         boolean isAfter6AM = !departureTime.isBefore(startTime);  // true if time is 06:00 or later
   		      boolean isBefore12PM = departureTime.isBefore(endTime);   // true if time is before 12:00

   		      if (isAfter6AM && isBefore12PM) {
   		    	  Thread.sleep(2000);
   		         
		                log.ReportEvent("INFO", "Departure time " + timeText + " is within 06:00 AM to 12:00 PM.");

   		      }
   		           else {
   		                log.ReportEvent("FAIL", "One or more Departure time " + timeText + " is NOT in the range 06:00 AM to 12:00 PM.");
   		   		        ScreenShots.takeScreenShot1();

   		                allValid = false;
   		            }

   		        } catch (DateTimeParseException e) {
   		            log.ReportEvent("FAIL", "Invalid time format found: '" + timeText + "'. Error: " + e.getMessage());
   	   		        ScreenShots.takeScreenShot1();

   		            allValid = false;
   		        }
   		    }

   		    if (allValid) {
   		        log.ReportEvent("PASS", "All departure times are between 06:00 AM and 12:00 PM.");
   		    } else {
   		        log.ReportEvent("FAIL", "One or more departure times are NOT in the range 06:00 AM to 12:00 PM.");
   		        ScreenShots.takeScreenShot1();

   		    }
   		}
   		
   		//Method to validate depart time 12PM to 6PM
   		
   		public void validateDepartTime12PMTo6PM(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   		    List<WebElement> timeElements = driver.findElements(
   		        By.xpath("//*[contains(@class,'tg-departure-time')]")
   		    );

   		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
   		    LocalTime startTime = LocalTime.of(12, 0);  // 12:00 PM
   		    LocalTime endTime = LocalTime.of(18, 0);    // 06:00 PM

   		    boolean allValid = true;

   		    for (WebElement element : timeElements) {
   		        String timeText = element.getText().trim();

   		        try {
   		            LocalTime departureTime = LocalTime.parse(timeText, formatter);

   		            boolean isAfterOrAt12PM = !departureTime.isBefore(startTime);  // 12:00 or later
   		            boolean isBefore6PM = departureTime.isBefore(endTime);         // before 18:00

   		            if (isAfterOrAt12PM && isBefore6PM) {
   		    			Thread.sleep(2000);

   		                log.ReportEvent("INFO", "Departure time " + timeText + " is within 12:00 PM to 06:00 PM.");
   		            } else {
   		                log.ReportEvent("FAIL", "One or more Departure time " + timeText + " is NOT in the range 12:00 PM to 06:00 PM.");
   		   		        ScreenShots.takeScreenShot1();

   		                allValid = false;
   		            }

   		        } catch (DateTimeParseException e) {
   		            log.ReportEvent("FAIL", "Invalid time format found: '" + timeText + "'. Error: " + e.getMessage());
   	   		        ScreenShots.takeScreenShot1();

   		            allValid = false;
   		        }
   		    }

   		    if (allValid) {
   		        log.ReportEvent("PASS", "All departure times are between 12:00 PM and 06:00 PM.");
   		    } else {
   		        log.ReportEvent("FAIL", "One or more departure times are NOT in the range 12:00 PM to 06:00 PM.");
   		        ScreenShots.takeScreenShot1();

   		    }
   		}

   		
   		//Method to validate depart After 6 PM
   		public void validateDepartTimeAfter6PM(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   		    // Find all time elements
   		    List<WebElement> timeElements = driver.findElements(
   		        By.xpath("//*[contains(@class,'tg-departure-time')]")
   		    );

   		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
   		    LocalTime startTime = LocalTime.of(18, 0);    // 06:00 PM
   		    LocalTime endTime = LocalTime.of(23, 59);     // 11:59 PM

   		    boolean allValid = true;

   		    for (WebElement element : timeElements) {
   		        String timeText = element.getText().trim();

   		        try {
   		            LocalTime departureTime = LocalTime.parse(timeText, formatter);

   		            boolean isInRange = !departureTime.isBefore(startTime) && !departureTime.isAfter(endTime);

   		            if (isInRange) {
   		    			Thread.sleep(2000);

   		                log.ReportEvent("INFO", "Departure time " + timeText + " is between 06:00 PM and 11:59 PM.");
   		            } else {
   		                log.ReportEvent("FAIL", "One or more Departure time " + timeText + " is NOT in the range 06:00 PM to 11:59 PM.");
   		   		        ScreenShots.takeScreenShot1();

   		                allValid = false;
   		            }

   		        } catch (DateTimeParseException e) {
   		            log.ReportEvent("FAIL", "Invalid time format found: '" + timeText + "'. Error: " + e.getMessage());
		   		        ScreenShots.takeScreenShot1();

   		            allValid = false;
   		        }
   		    }

   		    // Final summary log
   		    if (allValid) {
   		        log.ReportEvent("PASS", "All departure times are between 06:00 PM and 11:59 PM.");
   		    } else {
   		        log.ReportEvent("FAIL", "One or more departure times are NOT in the range 06:00 PM to 11:59 PM.");
	   		        ScreenShots.takeScreenShot1();

   		    }
   		}

   	
   		//Method for to validate arrival time Before 6 AM
   		   			
   		public void validateArrivalTimeBefore6AM(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   	   		    List<WebElement> timeElements = driver.findElements(
   	   		        By.xpath("//*[contains(@class,'tg-arrival-time')]")
   	   		    );

   	   		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
   	   		    LocalTime sixAM = LocalTime.of(6, 0);
   	   		    LocalTime midnight = LocalTime.MIDNIGHT; // 00:00

   	   		    boolean allValid = true;

   	   		    for (WebElement element : timeElements) {
   	   		        String timeText = element.getText().trim(); 

   	   		        try {
   	   		            LocalTime arrivalTime = LocalTime.parse(timeText, formatter);

   	   		            if (!arrivalTime.isBefore(sixAM)) {
   	   		   			Thread.sleep(2000);

   	   		                log.ReportEvent("FAIL", "Arrival time " + timeText + " is NOT before 6:00 AM.");
   	 	   		        ScreenShots.takeScreenShot1();

   	   		                allValid = false;
   	   		            } else {
   	   		                log.ReportEvent("INFO", "one or more Arrival time " + timeText + " is valid (before 6:00 AM).");
   	 	   		        ScreenShots.takeScreenShot1();

   	   		            }

   	   		        } catch (DateTimeParseException e) {
   	   		            log.ReportEvent("FAIL", "Invalid time format found: '" + timeText + "'. Error: " + e.getMessage());
   		   		        ScreenShots.takeScreenShot1();

   	   		            allValid = false;
   	   		        }
   	   		    }

   	   		    if (allValid) {
   	   		        log.ReportEvent("PASS", "All Arrival times are between 12:00 AM and 6:00 AM.");
   	   		    } else {
   	   		        log.ReportEvent("FAIL", "One or more Arrival times are NOT between 12:00 AM and 6:00 AM.");
	   		        ScreenShots.takeScreenShot1();

   	   		    }
   	   		}
   		
   		//Method to validate arrival time 6 AM to 12 PM
   		
   			public void validateArrivalTime6AMTo12PM(Log log,ScreenShots ScreenShots) throws InterruptedException {
   	   			Thread.sleep(5000);

   	   		    List<WebElement> timeElements = driver.findElements(
   	   		        By.xpath("//*[contains(@class,'tg-arrival-time')]")
   	   		    );

   	   		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
   	   		    LocalTime startTime = LocalTime.of(6, 0);   // 06:00 AM
   	   		    LocalTime endTime = LocalTime.of(12, 0);    // 12:00 PM

   	   		    boolean allValid = true;

   	   		    for (WebElement element : timeElements) {
   	   		        String timeText = element.getText().trim();

   	   		        try {
   	   		            LocalTime arrivalTime = LocalTime.parse(timeText, formatter);

   	   		         boolean isAfter6AM = !arrivalTime.isBefore(startTime);  // true if time is 06:00 or later
   	   		      boolean isBefore12PM = arrivalTime.isBefore(endTime);   // true if time is before 12:00

   	   		      if (isAfter6AM && isBefore12PM) {
   	      			Thread.sleep(2000);

   			                log.ReportEvent("INFO", "Arrival time " + timeText + " is within 06:00 AM to 12:00 PM.");

   	   		      }
   	   		           else {
   	   		                log.ReportEvent("FAIL", "One or more Arrival time " + timeText + " is NOT in the range 06:00 AM to 12:00 PM.");
   	 	   		        ScreenShots.takeScreenShot1();

   	   		                allValid = false;
   	   		            }

   	   		        } catch (DateTimeParseException e) {
   	   		            log.ReportEvent("FAIL", "Invalid time format found: '" + timeText + "'. Error: " + e.getMessage());
   		   		        ScreenShots.takeScreenShot1();

   	   		            allValid = false;
   	   		        }
   	   		    }

   	   		    if (allValid) {
   	   		        log.ReportEvent("PASS", "All Arrival times are between 06:00 AM and 12:00 PM.");
   	   		    } else {
   	   		        log.ReportEvent("FAIL", "One or more Arrival times are NOT in the range 06:00 AM to 12:00 PM.");
	   		        ScreenShots.takeScreenShot1();

   	   		    }
   	   		}

   		//Method to validate Arrival time 12 PM to 6 PM
   			
   				public void validateArrivalTime12PMTo6PM(Log log,ScreenShots ScreenShots) throws InterruptedException {
   		   			Thread.sleep(5000);

   		   		    List<WebElement> timeElements = driver.findElements(
   		   		        By.xpath("//*[contains(@class,'tg-arrival-time')]")
   		   		    );

   		   		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
   		   		    LocalTime startTime = LocalTime.of(12, 0);  // 12:00 PM
   		   		    LocalTime endTime = LocalTime.of(18, 0);    // 06:00 PM

   		   		    boolean allValid = true;

   		   		    for (WebElement element : timeElements) {
   		   		        String timeText = element.getText().trim();

   		   		        try {
   		   		            LocalTime arrivalTime = LocalTime.parse(timeText, formatter);

   		   		            boolean isAfterOrAt12PM = !arrivalTime.isBefore(startTime);  // 12:00 or later
   		   		            boolean isBefore6PM = arrivalTime.isBefore(endTime);         // before 18:00

   		   		            if (isAfterOrAt12PM && isBefore6PM) {
   		   		   			Thread.sleep(2000);

   		   		                log.ReportEvent("INFO", "Arrival time " + timeText + " is within 12:00 PM to 06:00 PM.");
   		   		            } else {
   		   		                log.ReportEvent("FAIL", "One or more Arrival time " + timeText + " is NOT in the range 12:00 PM to 06:00 PM.");
   		 	   		        ScreenShots.takeScreenShot1();

   		   		                allValid = false;
   		   		            }

   		   		        } catch (DateTimeParseException e) {
   		   		            log.ReportEvent("FAIL", "Invalid time format found: '" + timeText + "'. Error: " + e.getMessage());
   			   		        ScreenShots.takeScreenShot1();

   		   		            allValid = false;
   		   		        }
   		   		    }

   		   		    if (allValid) {
   		   		        log.ReportEvent("PASS", "All Arrival times are between 12:00 PM and 06:00 PM.");
   		   		    } else {
   		   		        log.ReportEvent("FAIL", "One or more Arrival times are NOT in the range 12:00 PM to 06:00 PM.");
   		   		        ScreenShots.takeScreenShot1();

   		   		    }
   		   		}
   				
 //Method to validate Arrival time After 6 PM
   				
   		public void validateArrivalTimeAfter6PM(Log log,ScreenShots ScreenShots) throws InterruptedException {
   			Thread.sleep(5000);

   		    // Find all time elements
   		    List<WebElement> timeElements = driver.findElements(
   		        By.xpath("//*[contains(@class,'tg-arrival-time')]")
   		    );

   		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
   		    LocalTime startTime = LocalTime.of(18, 0);    // 06:00 PM
   		    LocalTime endTime = LocalTime.of(23, 59);     // 11:59 PM

   		    boolean allValid = true;

   		    for (WebElement element : timeElements) {
   		        String timeText = element.getText().trim();

   		        try {
   		            LocalTime arrivalTime = LocalTime.parse(timeText, formatter);

   		            boolean isInRange = !arrivalTime.isBefore(startTime) && !arrivalTime.isAfter(endTime);

   		            if (isInRange) {
   		    			Thread.sleep(2000);

   		                log.ReportEvent("INFO", "Arrival time " + timeText + " is between 06:00 PM and 11:59 PM.");
   		            } else {
   		                log.ReportEvent("FAIL", "One or more Arrival time " + timeText + " is NOT in the range 06:00 PM to 11:59 PM.");
   		   		        ScreenShots.takeScreenShot1();

   		                allValid = false;
   		            }

   		        } catch (DateTimeParseException e) {
   		            log.ReportEvent("FAIL", "Invalid time format found: '" + timeText + "'. Error: " + e.getMessage());
		   		        ScreenShots.takeScreenShot1();

   		            allValid = false;
   		        }
   		    }

   		    if (allValid) {
   		        log.ReportEvent("PASS", "All Arrival times are between 06:00 PM and 11:59 PM.");
   		    } else {
   		        log.ReportEvent("FAIL", "One or more Arrival times are NOT in the range 06:00 PM to 11:59 PM.");
	   		        ScreenShots.takeScreenShot1();

   		    }
   		}
   		
   		
   //Method to quit when trains not found messsage is displayed 
   		public void QuitWhenTrainsNotFoundMsgIsDisplayed(Log log, ScreenShots screenShots) {
   		    List<WebElement> noTrainsMessages = driver.findElements(By.xpath("//*[text()='Oops! No trains found.']"));

   		    if (!noTrainsMessages.isEmpty() && noTrainsMessages.get(0).isDisplayed()) {
   		        log.ReportEvent("FAIL", "'Oops! No trains found.' message is displayed. Exiting test.");
   		        screenShots.takeScreenShot1();
   		        driver.quit();
   		        throw new RuntimeException("Stopping test: No trains found.");
   		    }
   		    // If not found, method just ends — test continues
   		}


   				
   			
   		
   		
   		
}