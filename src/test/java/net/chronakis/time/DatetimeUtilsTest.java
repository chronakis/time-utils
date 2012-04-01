package net.chronakis.time;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import net.chronakis.time.DatetimeUtils.IntervalUnit;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

import static org.junit.Assert.*;
import org.junit.Test;


/**
 * Tests for the {@link DatetimeUtils}
 * 
 * @author Ioannis Chronakis
 */
public class DatetimeUtilsTest {
	static SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd");
	static DateFormat SDFOUT  = new SimpleDateFormat("yyyy/MM/dd");
	
	
	@Test
	public void testDateToGregorian() throws Exception {
		Date now = new Date();
		Calendar cal = DatetimeUtils.dateToGregorianCalendar(now);
		assertEquals("dateToGregorian faled to create a calendar with the same date",
				now.getTime(), cal.getTimeInMillis());
	}

	@Test
	public void testDateToGregorianTimeZone() throws Exception {
		Date now = new Date();
		Calendar cal = DatetimeUtils.dateToGregorianCalendar(now, TimeZone.getTimeZone("EST"));
		assertEquals("dateToGregorian with timezone faled to create a calendar with the same date",
				now.getTime(), cal.getTimeInMillis());
	}

	@Test
	public void testBefore() throws Exception {
		Date now = new Date();
		Date before = new Date(now.getTime() - 10000);
		Date after = new Date(now.getTime() + 10000);
		assertTrue("Before was false for a before date", DatetimeUtils.before(before, now));
		assertFalse("Before was true for an after date", DatetimeUtils.before(after, now));
		assertFalse("Before was true for a same date", DatetimeUtils.before(now, now));
	}
	
	@Test
	public void testAfter() throws Exception {
		Date now = new Date();
		Date before = new Date(now.getTime() - 10000);
		Date after = new Date(now.getTime() + 10000);
		assertTrue("After was false for an aftere date", DatetimeUtils.after(after, now));
		assertFalse("After was true for a before date", DatetimeUtils.after(before, now));
		assertFalse("After was true for a same date", DatetimeUtils.after(now, now));
	}
	
	@Test
	public void testCompareTo() throws Exception {
		Date now = new Date();
		Date before = new Date(now.getTime() - 10000);
		Date after = new Date(now.getTime() + 10000);
		assertEquals("Compare returned non zero for equals", 0, DatetimeUtils.compare(now, now));
		assertEquals("Compare returned non negative for less", -1, DatetimeUtils.compare(before, now));
		assertEquals("Compare returned non positive for more", 1, DatetimeUtils.compare(after, now));
	}
	

	@Test
	public void testTruncateTimeCalendar() throws Exception {
		Calendar now = new GregorianCalendar();
		Calendar expectedNow = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
		DatetimeUtils.truncateTime(now);
		assertEquals("Trunctate failed", expectedNow, now);
	}
	
	@Test
	public void testTruncateTimeDatetime() throws Exception {
		Calendar now = new GregorianCalendar();
		Date actualNow = new Date(now.getTimeInMillis());
		Date expectedNow = new Date (new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).getTimeInMillis());
		DatetimeUtils.truncateTime(actualNow);
		assertEquals("Trunctate failed", expectedNow, actualNow);
	}


	/**
	 * Run the test against joda-time
	 */   
	@Test
	public void testTimeBetween() throws Exception {
		Calendar cal1 = GregorianCalendar.getInstance();
		Calendar cal2 = GregorianCalendar.getInstance();
		
		cal1.setTime(SDF.parse("1995/01/01"));
		cal2.setTime(SDF.parse("2000/10/05"));

		for (int i=1 ; i < 3650 ; i++) {
			cal1.add(Calendar.DAY_OF_MONTH, 1);
			long myDiff;
			long otherDiff;
			
			myDiff = DatetimeUtils.timeBetween(cal1, cal2, IntervalUnit.YEARS);
			otherDiff = Years.yearsBetween(new org.joda.time.DateTime(cal1), new org.joda.time.DateTime(cal2)).getYears();
			assertEquals("Years Failed: " + myDiff + " (" + otherDiff + ") " + SDFOUT.format(cal1.getTime()) + " - " +  SDF.format(cal2.getTime()),
					otherDiff, myDiff);

			myDiff = DatetimeUtils.timeBetween(cal1, cal2, IntervalUnit.MONTHS);
			otherDiff = Months.monthsBetween(new org.joda.time.DateTime(cal1), new org.joda.time.DateTime(cal2)).getMonths(); 
			assertEquals("Months Failed: " + myDiff + " (" + otherDiff + ") " + SDFOUT.format(cal1.getTime()) + " - " +  SDF.format(cal2.getTime()),
					otherDiff, myDiff);
			
			myDiff = DatetimeUtils.timeBetween(cal1, cal2, IntervalUnit.WEEKS);
			otherDiff = Weeks.weeksBetween(new org.joda.time.DateTime(cal1), new org.joda.time.DateTime(cal2)).getWeeks(); 
			assertEquals("Weeks Failed: " + myDiff + " (" + otherDiff + ") " + SDFOUT.format(cal1.getTime()) + " - " +  SDF.format(cal2.getTime()),
					otherDiff, myDiff);

			myDiff = DatetimeUtils.timeBetween(cal1, cal2, IntervalUnit.DAYS);
			otherDiff = Days.daysBetween(new org.joda.time.DateTime(cal1), new org.joda.time.DateTime(cal2)).getDays(); 
			assertEquals("Days Failed: " + myDiff + " (" + otherDiff + ") " + SDFOUT.format(cal1.getTime()) + " - " +  SDF.format(cal2.getTime()),
					otherDiff, myDiff);
			
			myDiff = DatetimeUtils.timeBetween(cal1, cal2, IntervalUnit.HOURS);
			otherDiff = Hours.hoursBetween(new org.joda.time.DateTime(cal1), new org.joda.time.DateTime(cal2)).getHours(); 
			assertEquals("Hours Failed: " + myDiff + " (" + otherDiff + ") " + SDFOUT.format(cal1.getTime()) + " - " +  SDF.format(cal2.getTime()),
					otherDiff, myDiff);

			myDiff = DatetimeUtils.timeBetween(cal1, cal2, IntervalUnit.MINUTES);
			otherDiff = Minutes.minutesBetween(new org.joda.time.DateTime(cal1), new org.joda.time.DateTime(cal2)).getMinutes(); 
			assertEquals("Minutes Failed: " + myDiff + " (" + otherDiff + ") " + SDFOUT.format(cal1.getTime()) + " - " +  SDF.format(cal2.getTime()),
					otherDiff, myDiff);
			
			myDiff = DatetimeUtils.timeBetween(cal1, cal2, IntervalUnit.SECONDS);
			otherDiff = Seconds.secondsBetween(new org.joda.time.DateTime(cal1), new org.joda.time.DateTime(cal2)).getSeconds(); 
			assertEquals("Seconds Failed: " + myDiff + " (" + otherDiff + ") " + SDFOUT.format(cal1.getTime()) + " - " +  SDF.format(cal2.getTime()),
					otherDiff, myDiff);
		}
	}
}
