
package com.tripgain.hotaltestscripts;

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
public class TCH_06_VerifyHotelsPriceHighToLow extends BaseClass {

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
			String returnDate=dates[1];
			String fromMonthYear=dates[2];
			String returnMonthYear=dates[3];
			number++;
			
						String origin = excelData.get("Origin");
						int roomcount = Integer.parseInt(excelData.get("Roomcount"));
						int adtcount = Integer.parseInt(excelData.get("AdtCount"));
						int chdcount = Integer.parseInt(excelData.get("ChdCount")); 
						

						


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
		


			
			
Thread.sleep(2000);
Hotels_Page.hotelClick();
Hotels_Page.validateHomePgaeIsDisplayed(Log, screenShots);
//Hotels_Page.enterCityOrHotelName(origin);
Hotels_Page.enterCityOrHotelName(origin);

Hotels_Page.selectDate(fromDate, fromMonthYear);
Hotels_Page.selectReturnDate(returnDate, returnMonthYear);
            
Hotels_Page.addRoom(roomcount,adtcount,chdcount); 
      long startTime = System.currentTimeMillis();

  Hotels_Page.clickOnSearch();

  // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    //   wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='MuiGrid2-root MuiGrid2-direction-xs-row MuiGrid2-grid-lg-9 MuiGrid2-grid-md-8 MuiGrid2-grid-sm-12 MuiGrid2-grid-xs-12 css-lgz2hl']")));

   long endTime = System.currentTimeMillis();
    long loadTimeInSeconds = (endTime - startTime) / 1000;
    Thread.sleep(3000);
   Hotels_Page.getFlightsCount(Log, screenShots);
    Hotels_Page.validateResultScreen(Log, screenShots);
	Thread.sleep(2000);
	
	
	//Method for to validate result page hotel page is displaying or not 
		Hotels_Page.waitForResultPageHotelGrid(Log, screenShots);
		Hotels_Page.ClickHotelSortDropdown();
		Hotels_Page.ClickHotelsPriceHighToLow();
		Hotels_Page.validateHotelsPriceHighToLow(Log, screenShots);
		   
		   
				
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
