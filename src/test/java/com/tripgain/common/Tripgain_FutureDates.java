package com.tripgain.common;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class Tripgain_FutureDates {
	 WebDriver driver;

	    // Constructor to initialize WebDriver
	    public Tripgain_FutureDates(WebDriver driver) {
	        this.driver = driver;
	    }

	    public static class DateResult {
	        public String day;
	        public String month;
	        public String year;

	        public DateResult(String day, String month, String year) {
	            this.day = day;
	            this.month = month;
	            this.year = year;
	        }

	        @Override
	        public String toString() {
	            return day + "-" + month + "-" + year;
	        }
	    }

	    public Map<String, DateResult> furtherDate() {
	        WebElement dateElement = driver.findElement(By.xpath("//div[@class='react-datepicker__input-container']//input"));
	        String currentDateStr = dateElement.getAttribute("value"); // e.g., "30th-May-2025"

	        // Clean suffixes: "th", "st", "nd", "rd"
	        String cleanedDateStr = currentDateStr.replaceAll("(st|nd|rd|th)", "");
	        String[] dateParts = cleanedDateStr.split("-");

	        int day = Integer.parseInt(dateParts[0]);       // e.g., 30
	        String monthStr = dateParts[1];                 // e.g., May
	        int year = Integer.parseInt(dateParts[2]);      // e.g., 2025

	        // Convert to LocalDate
	        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);
	        Month month = Month.from(monthFormatter.parse(monthStr));
	        LocalDate currentDate = LocalDate.of(year, month, day);

	        // Add 5 and 15 days
	        LocalDate datePlus2 = currentDate.plusDays(2);
	        LocalDate datePlus4 = currentDate.plusDays(4);
	        LocalDate datePlus5 = currentDate.plusDays(5);
	        LocalDate datePlus10 = currentDate.plusDays(10);
	        LocalDate datePlus15 = currentDate.plusDays(15);
	        LocalDate datePlus1 = currentDate.plusDays(1);
	        LocalDate datePlus8 = currentDate.plusDays(8);
	        LocalDate datePlus12 = currentDate.plusDays(12);
	        LocalDate datePlus20 = currentDate.plusDays(20);

	       
	        

	        // Prepare results as strings
	        
	        DateResult result1 = new DateResult(
	                String.valueOf(datePlus1.getDayOfMonth()),
	                datePlus1.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	                String.valueOf(datePlus1.getYear())
	            );
	        
	        DateResult result2 = new DateResult(
	                String.valueOf(datePlus2.getDayOfMonth()),
	                datePlus2.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	                String.valueOf(datePlus2.getYear())
	            );
	        
	        DateResult result4 = new DateResult(
	                String.valueOf(datePlus4.getDayOfMonth()),
	                datePlus4.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	                String.valueOf(datePlus4.getYear())
	            );

	        
	        
	        DateResult result5 = new DateResult(
	            String.valueOf(datePlus5.getDayOfMonth()),
	            datePlus5.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	            String.valueOf(datePlus5.getYear())
	        );
	        
	        DateResult result8 = new DateResult(
	                String.valueOf(datePlus8.getDayOfMonth()),
	                datePlus8.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	                String.valueOf(datePlus8.getYear())
	            );
	        DateResult result10 = new DateResult(
	                String.valueOf(datePlus10.getDayOfMonth()),
	                datePlus10.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	                String.valueOf(datePlus10.getYear())
	            );
	        DateResult result12 = new DateResult(
	                String.valueOf(datePlus12.getDayOfMonth()),
	                datePlus12.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	                String.valueOf(datePlus12.getYear())
	            );

	        DateResult result15 = new DateResult(
	            String.valueOf(datePlus15.getDayOfMonth()),
	            datePlus15.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	            String.valueOf(datePlus15.getYear())
	        );
	        
	        DateResult result20 = new DateResult(
	                String.valueOf(datePlus20.getDayOfMonth()),
	                datePlus20.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	                String.valueOf(datePlus20.getYear())
	            );
	        

	        Map<String, DateResult> resultMap = new HashMap<>();
	        resultMap.put("datePlus1", result1);
	        resultMap.put("datePlus2", result2);
	        resultMap.put("datePlus4", result4);
	        resultMap.put("datePlus5", result5);
	        resultMap.put("datePlus8", result8);
	        resultMap.put("datePlus10", result10);
	        resultMap.put("datePlus12", result12);
	        resultMap.put("datePlus15", result15);
	        resultMap.put("datePlus20", result20);


	        return resultMap;
	    }
	

}
