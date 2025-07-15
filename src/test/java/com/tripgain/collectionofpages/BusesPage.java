package com.tripgain.collectionofpages;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
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

public class BusesPage {
WebDriver driver;
	

	public BusesPage(WebDriver driver) {

        PageFactory.initElements(driver, this);
		this.driver=driver;
	}
	
    
@FindBy(xpath = "//*[text()='Enter From']/parent::div//input")
   private WebElement enterLocation;

	public void enterfrom(String location) throws TimeoutException {
        enterLocation.clear();
        enterLocation.sendKeys(location);
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='option']")));
        
        selectCityfrom(location);
    }

	 public void selectCityfrom(String location) throws TimeoutException {
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

	
	 public void validateHomePgaeIsDisplayed(Log Log, ScreenShots ScreenShots) {
	        try {
	            Thread.sleep(3000);

	            WebElement homePage = driver.findElement(By.xpath("//*[contains(@class,'train-page-title')]"));

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
	 
	 @FindBy(xpath = "//*[text()='Enter To']/parent::div//input")
	   private WebElement entertoLocation;

		public void enterTo(String location) throws TimeoutException {
			entertoLocation.clear();
			entertoLocation.sendKeys(location);
	        
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='option']")));
	        
	        selectCityto(location);
	    }

		 public void selectCityto(String location) throws TimeoutException {
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

//---------------------------------------------------------------------------------------------------------------

		 //Method to click on search button
	
		 public void clicksearchButton() throws InterruptedException {
			 driver.findElement(By.xpath("//button[text()='Search']")).click();
			 Thread.sleep(6000);
		 }

//---------------------------------------------------------------------------------------------------------------
		 
	//Method to get the text of all operator names
		 
		 public void getAllOperatorNames(Log Log, ScreenShots ScreenShots) {
			    JavascriptExecutor js = (JavascriptExecutor) driver;
			    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));

			    try {
			        Thread.sleep(2000); 

			        // Wait for at least one operator element to be visible
			        wait.until(ExpectedConditions.visibilityOfElementLocated(
			                By.xpath("//*[text()='OPERATOR NAME']/following::span[contains(@class,'MuiListItemText-primary')]")
			        ));

			        // Find all operator name elements
			        List<WebElement> operatorElements = driver.findElements(
			                By.xpath("//*[text()='OPERATOR NAME']/following::span[contains(@class,'MuiListItemText-primary')]")
			        );

			        if (operatorElements.isEmpty()) {
			            Log.ReportEvent("FAIL", "No operator names found on the page.");
			            System.out.println("No operator names found.");
			        } else {
			            Log.ReportEvent("PASS", "Found " + operatorElements.size() + " operator names.");

			            for (int i = 0; i < operatorElements.size(); i++) {
			                WebElement element = operatorElements.get(i);

			                // Scroll to this element 
			                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
			                Thread.sleep(500); // small wait for smooth scroll

			                String name = element.getText().trim();
			                Log.ReportEvent("PASS", "Operator " + (i + 1) + ": " + name);
			                System.out.println("Operator " + (i + 1) + ": " + name);
			            }
			        }

			        ScreenShots.takeScreenShot1();

			    } catch (Exception e) {
			        Log.ReportEvent("FAIL", "Exception while retrieving operator names: " + e.getMessage());
			        ScreenShots.takeScreenShot1();
			    }
			}

//---------------------------------------------------------------------------------------------------------
		 
			//Method for to click view seats based on index
		 
		 public String clickViewSeatsBasedOnIndex(int index, Log Log, ScreenShots ScreenShots) {
			    try {
			        Thread.sleep(4000); 

			        String buscardxpath = "(//*[contains(@class,'MuiPaper-root MuiPaper-elevation MuiPaper-rounded MuiPaper-elevation1 p-3 mb-2')])[" + index + "]";
			        WebElement busCard = driver.findElement(By.xpath(buscardxpath));
			        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", busCard);

			        WebElement busText = busCard.findElement(By.xpath(".//*[contains(@class,'MuiGrid2-root MuiGrid2-direction-xs-row MuiGrid2-grid-lg-3 MuiGrid2-grid-xs-12')]//h6"));
			        String busName = busText.getText().trim();

			        Log.ReportEvent("PASS", "Bus at index " + index + ": " + busName);
			        System.out.println("Bus at index " + index + ": " + busName);

			        WebElement viewSeatsButton = busCard.findElement(By.xpath(".//button[text()='View Seats']"));
			        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", viewSeatsButton);
			        Thread.sleep(500); 
			        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewSeatsButton);

			        Log.ReportEvent("PASS", "Clicked 'View Seats' on bus index " + index);
			        System.out.println("Clicked 'View Seats' on bus index " + index);

			        ScreenShots.takeScreenShot1();
			        return busName;

			    } catch (Exception e) {
			        Log.ReportEvent("FAIL", "Exception while clicking 'View Seats': " + e.getMessage());
			        ScreenShots.takeScreenShot1();
			    }
				return null;
			}

		 //--------------------------------------------------------------------------------------------
		 
		//Method for picking seats 
		 public void selectSeat() {
		        try {
		            List<WebElement> pickSeats = driver.findElements(By.xpath("//*[contains(@class,'tg-bsrs-seatavailable')]"));
		            if (!pickSeats.isEmpty()) {
		                pickSeats.get(pickSeats.size() - 1).click();  // Click the last available seat
		                System.out.println("Seat selected successfully.");
		            } else {
		                System.out.println("No available seats found.");
		            }
		        } catch (Exception e) {
		            System.out.println("An error occurred while selecting a seat: " + e.getMessage());
		            e.printStackTrace();  // Optional: prints the full stack trace for debugging
		        }
		    }
		 
//--------------------------------------------------------------------------------------------------
		 
//Method to select boarding points based on user passed index
		 public String selectBoardingPoints(Log Log, ScreenShots ScreenShots) throws InterruptedException {
			    try {
			        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));

			        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
			            By.xpath("//*[contains(@class,'boarding-point')]//div[contains(@class,'MuiSelect-select')]")
			        ));
			        dropdown.click();
			        Log.ReportEvent("PASS", "Clicked on the boarding point dropdown.");

			        List<WebElement> boardingPoints = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
			            By.xpath("//li[contains(@class,'MuiMenuItem-root')]")
			        ));
			        Log.ReportEvent("INFO", "Fetched boarding point list. Total options found: " + boardingPoints.size());

			        if (!boardingPoints.isEmpty()) {
			            Random rand = new Random();
			            WebElement selectedPoint = boardingPoints.get(rand.nextInt(boardingPoints.size()));
			            String rawText = selectedPoint.getText();

			            // Split text by new lines
			            String[] lines = rawText.split("\\r?\\n");

			            String firstLine = lines[0].trim();
			            String secondLine = (lines.length > 1) ? lines[1].trim() : "";

			            // Check if firstLine contains time pattern (like (18:05))
			            if (firstLine.matches(".*\\(\\d{1,2}:\\d{2}\\).*")) {
			                // Remove the time part from first line
			                firstLine = firstLine.replaceAll("\\s*\\(\\d{1,2}:\\d{2}\\)", "").trim();
			               
			            } else if (!secondLine.isEmpty()) {
			                // No time in first line, use second line instead if available
			               // Log.ReportEvent("INFO", "No time found in first line; using second line: " + secondLine);
			                firstLine = secondLine;
			            }

			            Log.ReportEvent("INFO", "Selected boarding point : " + firstLine);

			            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectedPoint);
			            Thread.sleep(300);
			            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", selectedPoint);

			            Log.ReportEvent("PASS", "Clicked on the selected boarding point.");
			            ScreenShots.takeScreenShot1();
			            System.out.println("boarding text final: " + firstLine);
			            return firstLine;
			        } else {
			            Log.ReportEvent("FAIL", "No boarding points found.");
			            ScreenShots.takeScreenShot1();
			            return null;
			        }
			    } catch (Exception e) {
			        Log.ReportEvent("FAIL", "Exception while selecting boarding point: " + e.getMessage());
			        ScreenShots.takeScreenShot1();
			        return null;
			    }
			}
			
//----------------------------------------------------------------------------------------------------------
		
		//Method to select dropping points based on user passed index

		 public String selectDroppingPoint(Log Log, ScreenShots ScreenShots) throws InterruptedException {
			    try {
			        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));

			        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
			            By.xpath("//*[contains(@class,'dropping-point')]//div[contains(@class,'MuiSelect-select')]")
			        ));
			        dropdown.click();
			        Log.ReportEvent("PASS", "Clicked on the dropping point dropdown.");

			        List<WebElement> droppingPoints = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
			            By.xpath("//li[contains(@class,'MuiMenuItem-root')]")
			        ));
			        Log.ReportEvent("INFO", "Fetched boarding point list. Total options found: " + droppingPoints.size());

			        if (!droppingPoints.isEmpty()) {
			            Random rand = new Random();
			            WebElement selectedPoint = droppingPoints.get(rand.nextInt(droppingPoints.size()));
			            String rawText = selectedPoint.getText();

			            // Split text by new lines
			            String[] lines = rawText.split("\\r?\\n");

			            String firstLine = lines[0].trim();
			            String secondLine = (lines.length > 1) ? lines[1].trim() : "";

			            // Check if firstLine contains time pattern (like (18:05))
			            if (firstLine.matches(".*\\(\\d{1,2}:\\d{2}\\).*")) {
			                // Remove the time part from first line
			                firstLine = firstLine.replaceAll("\\s*\\(\\d{1,2}:\\d{2}\\)", "").trim();
			             
			            } else if (!secondLine.isEmpty()) {
			                // No time in first line, use second line instead if available
			                firstLine = secondLine;
			            }

			            Log.ReportEvent("INFO", "Selected droppingPoints point: " + firstLine);

			            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectedPoint);
			            Thread.sleep(300);
			            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", selectedPoint);

			            Log.ReportEvent("PASS", "Clicked on the selected droppingPoints point.");
			            ScreenShots.takeScreenShot1();
			            System.out.println("droppingPoints text final: " + firstLine);
			            return firstLine;
			        } else {
			            Log.ReportEvent("FAIL", "No droppingPoints points found.");
			            ScreenShots.takeScreenShot1();
			            return null;
			        }
			    } catch (Exception e) {
			        Log.ReportEvent("FAIL", "Exception while selecting droppingPoints point: " + e.getMessage());
			        ScreenShots.takeScreenShot1();
			        return null;
			    }
			}
			
			
//		 public String selectDroppingPoint(Log Log, ScreenShots ScreenShots) throws InterruptedException {
//			    try {
//			        Thread.sleep(4000); 
//
//			        WebElement dropdown = driver.findElement(By.xpath("//*[contains(@class,'dropping-point')]//div[contains(@class,'MuiSelect-select')]"));
//			        dropdown.click();
//			        Log.ReportEvent("PASS", "Clicked on the dropping point dropdown.");
//
//			        Thread.sleep(1000);
//
//			        List<WebElement> droppingPoints = driver.findElements(By.xpath("//li[contains(@class,'MuiMenuItem-root')]"));
//			        Log.ReportEvent("INFO", "Fetched dropping point list. Total options found: " + droppingPoints.size());
//
//			        if (!droppingPoints.isEmpty()) {
//			            Random rand = new Random();
//			            WebElement selectedPoint = droppingPoints.get(rand.nextInt(droppingPoints.size()));
//			            String selectedPointText = selectedPoint.getText();
//
//			            Log.ReportEvent("INFO", "Selected dropping point: " + selectedPointText);
//
//			            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectedPoint);
//			            Thread.sleep(300);
//			            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", selectedPoint);
//
//			            Log.ReportEvent("PASS", "Clicked on the selected dropping point.");
//			            ScreenShots.takeScreenShot1();
//			            System.out.println("dropping text: "+selectedPointText);
//
//			            return selectedPointText;
//			        } else {
//			            Log.ReportEvent("FAIL", "No dropping points found.");
//			            ScreenShots.takeScreenShot1();
//			            return null;
//			        }
//			    } catch (Exception e) {
//			        Log.ReportEvent("FAIL", "Exception while selecting dropping point: " + e.getMessage());
//			        ScreenShots.takeScreenShot1();
//			        return null;
//			    }
//		 }
//			    
			    
	 //------------------------------------------------------------------------------------------
			    
			    //Method for to click on conform seat button
		    
public void confirmseatbutton() {
	driver.findElement(By.xpath("//button[text()='Confirm Seat']")).click();
}
	
//----------------------------------------------------------------------------------------------------------

//Method to close reason For Selection PopUp
public void reasonForSelectionPopUp() throws InterruptedException, TimeoutException {
    String value = "Personal Preference";

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(70));
    Thread.sleep(8000);
	WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(
	    By.xpath("//h2[@id='alert-dialog-title']")
   ));

	if (popup.isDisplayed()) {
	    WebElement reasonOption = driver.findElement(
	        By.xpath("//span[text()='" + value + "']//parent::label")
	    );
	    reasonOption.click();
	    //click on Proceed to Booking
	    driver.findElement(By.xpath("//button[text()='Proceed to Booking']")).click();
	    Thread.sleep(3000);
	  
	}
}

//--------------------------------------------------------------------------------------
//Method to validate booking screen is displayed or not

public void validateBookingScreenIsDisplayed(Log Log, ScreenShots ScreenShots) {
    try {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(35));
        WebElement reviewPage = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//h6[contains(., 'Review Your Trip')]")));

        if (reviewPage.isDisplayed()) {
            Log.ReportEvent("PASS", "Review Your Trip Page is Displayed");
            ScreenShots.takeScreenShot();
        } else {
            Log.ReportEvent("FAIL", "Review Your Trip Page is present but not visible.");
            ScreenShots.takeScreenShot();
            Assert.fail("Review Your Trip Page is present but not visible.");
        }

    } catch (Exception e) {
        Log.ReportEvent("FAIL", "Review Your Trip Page is Not Displayed: " + e.getMessage());
        ScreenShots.takeScreenShot();
        Assert.fail("Review Your Trip Page is Not Displayed: " + e.getMessage());
    }
}

//------------------------------------------------------------------------------------------------------------

//Method to get boarding point text from booking page 
public String getBookingBoardingPointText(WebDriver driver, Log Log, ScreenShots ScreenShots) {
    try {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        WebElement bookingTextElement = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[contains(@class,'tg-bsbk-boardingpoint')]")
        ));

        String text = bookingTextElement.getText().trim();
        Log.ReportEvent("INFO", "Extracted booking boarding point text: " + text);
        System.out.println("Booking page boarding text:"+ text);
        return text;

    } catch (Exception e) {
        Log.ReportEvent("FAIL", "Failed to get booking boarding point text: " + e.getMessage());
        ScreenShots.takeScreenShot1();
        return null;
    }
}

//Method to get dropping point text from booking page 

public String getBookingDroppingPointText(WebDriver driver, Log Log, ScreenShots ScreenShots) {
  try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

      WebElement bookingTextElement = wait.until(ExpectedConditions.presenceOfElementLocated(
          By.xpath("//*[contains(@class,'tg-bsbk-droppingpoint')]")
      ));

      String text = bookingTextElement.getText().trim();
      Log.ReportEvent("INFO", "Extracted booking dropping point text: " + text);
      System.out.println("Booking page dropping text:"+ text);

      return text;

  } catch (Exception e) {
      Log.ReportEvent("FAIL", "Failed to get booking dropping point text: " + e.getMessage());
      ScreenShots.takeScreenShot1();
      return null;
  }
}

//Method to validate boarding text 

public void validateBoardingPointsMatch(String selectedBoardingText, String bookingPageText, Log Log, ScreenShots ScreenShots) {
    if (selectedBoardingText == null || selectedBoardingText.trim().isEmpty()) {
        Log.ReportEvent("FAIL", "Selected boarding point text is null or empty.");
        ScreenShots.takeScreenShot1();
        Assert.fail("Selected boarding point text is null or empty.");
        return;
    }

    if (bookingPageText == null || bookingPageText.trim().isEmpty()) {
        Log.ReportEvent("FAIL", "Booking page boarding point text is null or empty.");
        ScreenShots.takeScreenShot1();
        Assert.fail("Booking page boarding point text is null or empty.");
        return;
    }

    if (selectedBoardingText.trim().equalsIgnoreCase(bookingPageText.trim())) {
        Log.ReportEvent("PASS", "Boarding points match: " + selectedBoardingText);
        ScreenShots.takeScreenShot1();
    } else {
        Log.ReportEvent("FAIL", "Boarding points mismatch. Selected: " + selectedBoardingText + ", Booking Page: " + bookingPageText);
        ScreenShots.takeScreenShot1();
        Assert.fail("Boarding points mismatch.");
    }
}

public void validateBoardingPointTextWithBookingPage(Log Log, ScreenShots ScreenShots) throws InterruptedException {
    // Get selected boarding point text
    String selectedBoardingText = selectBoardingPoints(Log, ScreenShots);

    // Get booking page boarding point text
    String bookingPageText = getBookingBoardingPointText(driver, Log, ScreenShots);

    // Validate both texts
    validateBoardingPointsMatch(selectedBoardingText, bookingPageText, Log, ScreenShots);
}


//----------------------------------------------------------------------------------------------

public void validateDroppingPointTextWithBookingPage(Log Log, ScreenShots ScreenShots) throws InterruptedException {
    // Get selected boarding point text
    String selectedDroppingText = selectDroppingPoint(Log, ScreenShots);

    // Get booking page boarding point text
    String bookingPageDroppingText = getBookingDroppingPointText(driver, Log, ScreenShots);

    // Validate both texts
    validateDroppingPointsMatch(selectedDroppingText, bookingPageDroppingText, Log, ScreenShots);
}


public void validateDroppingPointsMatch(String selectedDroppingText, String bookingPageDroppingText, Log Log, ScreenShots ScreenShots) {
    if (selectedDroppingText == null || selectedDroppingText.trim().isEmpty()) {
        Log.ReportEvent("FAIL", "Selected Dropping point text is null or empty.");
        ScreenShots.takeScreenShot1();
        Assert.fail("Selected Dropping point text is null or empty.");
        return;
    }

    if (bookingPageDroppingText == null || bookingPageDroppingText.trim().isEmpty()) {
        Log.ReportEvent("FAIL", "Booking page Dropping point text is null or empty.");
        ScreenShots.takeScreenShot1();
        Assert.fail("Booking page dropping point text is null or empty.");
        return;
    }

    if (selectedDroppingText.trim().equalsIgnoreCase(bookingPageDroppingText.trim())) {
        Log.ReportEvent("PASS", "Dropping points match: " + selectedDroppingText);
        ScreenShots.takeScreenShot1();
    } else {
        Log.ReportEvent("FAIL", "Dropping points mismatch. Selected: " + selectedDroppingText + ", Booking Page: " + bookingPageDroppingText);
        ScreenShots.takeScreenShot1();
        Assert.fail("Dropping points mismatch.");
    }
}



//------------------------------------------------------------------------------------------------------------


//Method to validate selected bus name is matching with booking page bus name or not

public void validateBusNameMatch(String selectedBusName,String bookingPageBusName, Log Log,ScreenShots ScreenShots) {
	    
	    try {
	        if (selectedBusName == null || selectedBusName.trim().isEmpty()) {
	            Log.ReportEvent("FAIL", "Selected bus name is null or empty.");
	            ScreenShots.takeScreenShot1();
	            Assert.fail("Selected bus name is null or empty.");
	            return;
	        }

	        if (bookingPageBusName == null || bookingPageBusName.trim().isEmpty()) {
	            Log.ReportEvent("FAIL", "Booking page bus name is null or empty.");
	            ScreenShots.takeScreenShot1();
	            Assert.fail("Booking page bus name is null or empty.");
	            return;
	        }

	        if (selectedBusName.trim().equalsIgnoreCase(bookingPageBusName.trim())) {
	            Log.ReportEvent("PASS", "Bus names match: " + selectedBusName);
	            ScreenShots.takeScreenShot1();
	        } else {
	            Log.ReportEvent("FAIL", "Bus names mismatch. Selected: " + selectedBusName + ", Booking Page: " + bookingPageBusName);
	            ScreenShots.takeScreenShot1();
	            Assert.fail("Bus name mismatch.");
	        }
	    } catch (Exception e) {
	        Log.ReportEvent("FAIL", "Exception during bus name validation: " + e.getMessage());
	        ScreenShots.takeScreenShot1();
	        Assert.fail("Exception in validateBusNameMatch().");
	    }
	}


public String getBusNameFromBookingPage(WebDriver driver, Log Log, ScreenShots ScreenShots) {
    try {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        WebElement bookingPageBusName = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[contains(@class,'mb-16')]//h6)[1]"))
        );

        String busName = bookingPageBusName.getText().trim();
        Log.ReportEvent("INFO", "Booking page bus name retrieved: " + busName);
        return busName;

    } catch (Exception e) {
        Log.ReportEvent("FAIL", "Failed to get bus name from booking page: " + e.getMessage());
        ScreenShots.takeScreenShot1();
        return null;
    }
}




















		 
}
