
package com.tripgain.busestestscripts;

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
public class TCB_02_VerifySearchFunctionalityForBusesForFiveDays extends BaseClass {

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
			BusesPage Buses_Page=new BusesPage(driver);
			
			
			tripgainLogin.enterUserName(userName1);
			tripgainLogin.enterPasswordName(password1);
			tripgainLogin.clickButton();
			Thread.sleep(2000);
			
			tripgainLogin.verifyHomePageIsDisplayed(Log,screenShots);

			//Functions to Search flights on Home Page
			Tripgain_homepage tripgainhomepage = new Tripgain_homepage(driver);
		


			
			
Thread.sleep(2000);
//Functions to Search buses on Home Page
tripgainhomepage.clickOnTravelDropDown();
tripgainhomepage.selectTravelOptionsOnHomeScreen(travelOption);
Buses_Page.validateHomePgaeIsDisplayed(Log, screenShots);


Buses_Page.enterfrom(origin);
Buses_Page.enterTo(destination);
Hotels_Page.selectDate(fromDate, fromMonthYear);
Buses_Page.clicksearchButton();

      long startTime = System.currentTimeMillis();

   long endTime = System.currentTimeMillis();
    long loadTimeInSeconds = (endTime - startTime) / 1000;
    Thread.sleep(3000);

    //Method for to get all operator names
   	Buses_Page.getAllOperatorNames(Log, screenShots);
   	Thread.sleep(3000);
   	
   	//Method for to click view seats based on index
   	String selectedBusName = Buses_Page.clickViewSeatsBasedOnIndex(2, Log, screenShots);
   	
   	//Method for to select boarding points locations
   	String selectedBoarding = Buses_Page.selectBoardingPoints(Log, screenShots);
   	
   	//Method for to select dropping points locations
   	String 	selectDropping = Buses_Page.selectDroppingPoint(Log, screenShots);
   	

   	//Method for pick seat
   	Buses_Page.selectSeat();
   	
   	//Method for to click confirm seat
   	Buses_Page.confirmseatbutton();
   	
   	//Method for to select reason for select popup
   	Buses_Page.reasonForSelectionPopUp();
   	
   	Thread.sleep(5000);
   	
   	//Method for to validate booking page is displayed or not
   	Buses_Page.validateBookingScreenIsDisplayed(Log, screenShots);
   	
   	// Get booking page boarding point text
   	String bookingPageBoardingText = Buses_Page.getBookingBoardingPointText(driver, Log, screenShots);

   	// Validate boarding points match
   	Buses_Page.validateBoardingPointsMatch(selectedBoarding, bookingPageBoardingText, Log, screenShots);	


   	// Get booking page dropping point text
   	String droppingPageText = Buses_Page.getBookingDroppingPointText(driver, Log, screenShots);

   	// Validate dropping points match
   	Buses_Page.validateDroppingPointsMatch(selectDropping, droppingPageText, Log, screenShots);	

   //Method to get bus name text 
   	String bookingPageBusName = Buses_Page.getBusNameFromBookingPage(driver, Log, screenShots);

   	// validate bus names
   	Buses_Page.validateBusNameMatch(selectedBusName, bookingPageBusName, Log, screenShots);	
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
