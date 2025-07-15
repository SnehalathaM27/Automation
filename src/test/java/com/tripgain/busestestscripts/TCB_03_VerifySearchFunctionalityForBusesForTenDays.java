
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
public class TCB_03_VerifySearchFunctionalityForBusesForTenDays extends BaseClass {

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
//		String[] dates=GenerateDates.GenerateDatesToSelectFlights();
//			String fromDate=dates[0];
//			String returnDate=dates[1];
//			String fromMonthYear=dates[2];
//			String returnMonthYear=dates[3];
			number++;
			
			String origin = excelData.get("Origin");
			String destination = excelData.get("Destination");
			String travelOption = excelData.get("TravelOption");

			//Functions to Login TripGain Application
			Tripgain_Login tripgainLogin= new Tripgain_Login(driver);
			HotelsPage Hotels_Page=new HotelsPage(driver);
			
			tripgainLogin.enterUserName(userName1);
			tripgainLogin.enterPasswordName(password1);
			tripgainLogin.clickButton();
			Thread.sleep(2000);
			
			tripgainLogin.verifyHomePageIsDisplayed(Log,screenShots);

			//Functions to Search flights on Home Page
			Tripgain_homepage tripgainhomepage = new Tripgain_homepage(driver);
			BusesPage Buses_Page=new BusesPage(driver);
			Thread.sleep(2000);
			//Functions to Search buses on Home Page
			tripgainhomepage.clickOnTravelDropDown();
			tripgainhomepage.selectTravelOptionsOnHomeScreen(travelOption);
			Buses_Page.validateHomePgaeIsDisplayed(Log, screenShots);

Thread.sleep(2000);
Tripgain_FutureDates futureDates = new Tripgain_FutureDates(driver);
Map<String, Tripgain_FutureDates.DateResult> dateResults = futureDates.furtherDate();
Tripgain_FutureDates.DateResult date10 = dateResults.get("datePlus10");
String fromMonthYear = date10.month + " " + date10.year;
Tripgain_FutureDates.DateResult date20 = dateResults.get("datePlus20");
String returnMonthYear = date20.month + " " + date20.year;


Buses_Page.enterfrom(origin);
Buses_Page.enterTo(destination);

System.out.println(date10.day);
System.out.println(fromMonthYear);
System.out.println(date20.day);
Hotels_Page.selectDate(date10.day, fromMonthYear);


      long startTime = System.currentTimeMillis();

  Hotels_Page.clickOnSearch();

  // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    //   wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='MuiGrid2-root MuiGrid2-direction-xs-row MuiGrid2-grid-lg-9 MuiGrid2-grid-md-8 MuiGrid2-grid-sm-12 MuiGrid2-grid-xs-12 css-lgz2hl']")));

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
