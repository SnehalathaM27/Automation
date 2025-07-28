
package com.tripgain.trainScripts;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.tripgain.collectionofpages.*;
import com.tripgain.common.*;
import com.tripgain.testscripts.BaseClass;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;

@Listeners(TestListener.class)
public class TCT_08_VerifySearchFunctionalityForTrains extends BaseClass {

	WebDriver driver;    
	ExtentReports extent;
	ExtentTest test;
	String className = "";
	Log Log;  // Declare Log object
	ScreenShots screenShots;  // Declare Log object
	ExtantManager extantManager;
	// ThreadLocal to store Excel data per test thread
	static ThreadLocal<Map<String, String>> excelDataThread = new ThreadLocal<>();

	int number=1;

	@Test(dataProvider = "sheetBasedData", dataProviderClass = DataProviderUtils.class)
	public void myTest(Map<String, String> excelData) throws InterruptedException, IOException {
		System.out.println("Running test with: " + excelData);
		//To get Data from Excel
		try{
			String userName1 = excelData.get("UserName");
			String password1 = excelData.get("Password");
		String[] dates=GenerateDates.GenerateDatesToSelectFlights();
		String fromDate=dates[0];
		String fromMonthYear=dates[2];
		
			number++;
			
						String origin = excelData.get("Origin");
						String destination = excelData.get("Destination");
						String travelOption = excelData.get("TravelOption");


						


			//Functions to Login TripGain Application
			Tripgain_Login tripgainLogin= new Tripgain_Login(driver);
			HotelsPage Hotels_Page=new HotelsPage(driver);
			TrainsPage Trains_Page=new TrainsPage(driver);
			
			tripgainLogin.enterUserName(userName1);
			tripgainLogin.enterPasswordName(password1);
			tripgainLogin.clickButton();
			Thread.sleep(2000);
			
			tripgainLogin.verifyHomePageIsDisplayed(Log,screenShots);

			//Functions to Search flights on Home Page
			Tripgain_homepage tripgainhomepage = new Tripgain_homepage(driver);
		
			tripgainhomepage.clickOnTravelDropDown();
			tripgainhomepage.selectTravelOptionsOnHomeScreen(travelOption);

			Thread.sleep(3000);
			Trains_Page.validateHomePgaeIsDisplayed(Log, screenShots);
//Hotels_Page.enterCityOrHotelName(origin);
Trains_Page.enterFromLocation(origin);
Trains_Page.enterToLocation(destination);
Trains_Page.selectDate(fromDate, fromMonthYear);
Trains_Page.clickQuotaDropdown();
Trains_Page.enetrDropdownValues("General Quota");
Trains_Page.clickOnSearch();
  
      long startTime = System.currentTimeMillis();



   long endTime = System.currentTimeMillis();
    long loadTimeInSeconds = (endTime - startTime) / 1000;
    Thread.sleep(5000);
   
    Trains_Page.QuitWhenTrainsNotFoundMsgIsDisplayed(Log, screenShots);
    Thread.sleep(3000);
    Trains_Page.validateTrainsGrid(Log, screenShots);
    Thread.sleep(2000);
    Trains_Page.getTrainNames();
    System.out.println("TRAINS NAMES DONE");
    Thread.sleep(2000);
  
    //Method to click available classes
    Trains_Page.clickAvailableClasses(Log, screenShots, "Sleeper (SL)");
    Thread.sleep(2000);
    Trains_Page.clcikDepartTime(Log, screenShots, "12 PM to 6 PM");
    Thread.sleep(2000);
    Trains_Page.validateDepartTime12PMTo6PM(Log, screenShots);
    Thread.sleep(2000);
    Trains_Page.clcikArrivalTime(Log, screenShots, "6 AM to 12 PM");
    Thread.sleep(2000);
    Trains_Page.validateArrivalTime6AMTo12PM(Log, screenShots);
    Trains_Page.clickTrainNameFilterAscending();
    Thread.sleep(3000);
    Trains_Page.validateTrainNameFilterAscending(Log, screenShots);
    Thread.sleep(4000);
    
    Trains_Page.clickTrainNameFilterDescending();
    Thread.sleep(3000);
    Trains_Page.validateTrainNameFilterDescending(Log, screenShots);
    Thread.sleep(3000);
    Trains_Page.clickDepartureFilterAscending();
    Thread.sleep(3000);
    Trains_Page.validateDepatureAscendingFIlter(Log, screenShots);
    Thread.sleep(3000);
    Trains_Page.clickdepatureFilterDescending();
    Thread.sleep(3000);
    Trains_Page.validateDepatureDescendingFilter(Log, screenShots);
    Thread.sleep(3000);
    Trains_Page.clickArrivalFilterAscending();
    Thread.sleep(3000);
    Trains_Page.validateArrivalAscendingFIlter(Log, screenShots);
    Thread.sleep(3000);
    Trains_Page.clickArrivalFilterDescending();
    Thread.sleep(3000);
    Trains_Page.validateArrivalDescendingFilter(Log, screenShots);
    Thread.sleep(3000);  
    
  String[] resultCardDetails = Trains_Page.clickOnCheckAvailabilityBasedOnIndex(1);  // pass desired index

  Thread.sleep(2000);
  String[] selectedTierDetails =Trains_Page.selectTierAndGetPrice("Sleeper (SL)", Log, screenShots);
  System.out.println("selectTier And GetPrice done");
    Thread.sleep(2000);
    String[] result = Trains_Page.clickBookNowButtonInsideContainer(Log, screenShots);
    System.out.println("BOOK NOW DONE");
    Thread.sleep(2000);
//    Trains_Page.validateAvailabilityStatus(Log, screenShots, result);
  //  System.out.println("VALIDATE AVAILABILITY TEXT DONE");
    //Thread.sleep(2000);
   //Trains_Page.validateTravellingDateStatus(Log, screenShots, result);
   // System.out.println("VALIDATE TRAVELLING DATE STATUS DONE");
    //Thread.sleep(2000);
    Trains_Page.compareBookingPageDetailsWithResultCard(resultCardDetails, Log, screenShots);   
    System.out.println("BOOKING PAGE COMPARISION WTH RESULT PAGE DONE");
Thread.sleep(3000);

    Trains_Page.compareTierAndPriceWithBookingPage(selectedTierDetails, Log, screenShots);
    System.out.println("COMPARISION OF TIER NAME AND PRICE DONE");
    Thread.sleep(3000);
    Trains_Page.clickBoardingPointBookingpg(Log, screenShots);
    System.out.println("SELECT BOARDING POINT DONE");
    
    Trains_Page.clickAddTravellers(2, Log);
    Thread.sleep(3000);
    System.out.println("ADDING TRAVELLERS DONE");
    
  /*  Trains_Page.enterTravellerDetails();
    Thread.sleep(3000);
    System.out.println("Entering TRAVELLERS DONE");
    
    Trains_Page.clickSendApprovalButton();
System.out.println("SEND FOR APPROVAL DONE");

Thread.sleep(3000);
Trains_Page.validateApprovalPageIsDisplayingOrNot();
System.out.println("APPROVAL PAGE VALIDATION DONE");*/

    driver.quit();
		}catch (Exception e)
		{
			Log.ReportEvent("FAIL", "Occurred Exception"+ e.getMessage());
			screenShots.takeScreenShot1();
			e.printStackTrace();
			Assert.fail();
		}

	}

	@BeforeMethod(alwaysRun = true)
	@Parameters("browser")
	public void launchApplication(String browser, Method method, Object[] testDataObjects) {
		// Get test data passed from DataProvider
		@SuppressWarnings("unchecked")
		Map<String, String> testData = (Map<String, String>) testDataObjects[0];
		excelDataThread.set(testData);  // Set it early!

		String url = (testData != null && testData.get("URL") != null) ? testData.get("URL") : "https://defaulturl.com";

		extantManager = new ExtantManager();
		extantManager.setUpExtentReporter(browser);
		className = this.getClass().getSimpleName();
		String testName = className + "_" + number;
		extantManager.createTest(testName);
		test = ExtantManager.getTest();
		extent = extantManager.getReport();
		test.log(Status.INFO, "Execution Started Successfully");

		driver = launchBrowser(browser, url);
		Log = new Log(driver, test);
		screenShots = new ScreenShots(driver, test);
	}

	@AfterMethod
	public void tearDown() {
		if (driver != null) {
			driver.quit();
			extantManager.flushReport();
		}
	}



}
