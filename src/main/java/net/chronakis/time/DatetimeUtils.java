package net.chronakis.time;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utility class to allow date and calendar operations
 * 
 * @author Ioannis Chronakis
 */
public class DatetimeUtils {
	/**
	 * Enumeration for choosing interval units for time difference functions
	 */
	public enum IntervalUnit {
		YEARS, MONTHS, WEEKS, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS
	}
	

	/**
	 * Get the difference from date1 to date2 in various intervals.
	 * <p>
	 * The math notation would be date2 minus date1
	 * <p>
	 * See {@link DatetimeUtils#timeBetween(Calendar, Calendar, IntervalUnit)} for details
	 * 
	 * @param date1 The start date
	 * @param date2 The end date
	 * @param intervalUnit The unit that the return value will be
	 * @param format A formating string following {@link java.text.SimpleDateFormat} formating rules
	 *  
	 * @throws ParseException When the dates cannot be parsed 
	 */
	public static long timeBetween(String date1, String date2, IntervalUnit intervalUnit, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		return timeBetween(sdf.parse(date1), sdf.parse(date2), intervalUnit);
	}
	
	
	/**
	 * Get the difference from date1 to date2 in various intervals.
	 * <p>
	 * The math notation would be date2 minus date1
	 * <p>
	 * See {@link DatetimeUtils#timeBetween(Calendar, Calendar, IntervalUnit)} for details
	 * 
	 * @param date1 The start date
	 * @param date2 The end date
	 * @param intervalUnit The unit that the return value will be
	 * 
	 * @return 
	 */
	public static long timeBetween(Date date1, Date date2, IntervalUnit intervalUnit) {
		Calendar cal1 = dateToGregorianCalendar(date1);
		Calendar cal2 = dateToGregorianCalendar(date2);
		
		return timeBetween(cal1, cal2, intervalUnit);
	}
	
	
	/**
	 * Get the difference from date1 to date2 in various intervals.
	 * <p>
	 * The math notation would be date2 minus date1
	 * <p>
	 * All return values are rounded using floor.
	 * i.e. when the interval returned is not a complete integral,
	 * the excessive amount is truncated
	 * <p>
	 * Years, months, weeks and days are not affected by daylight savings.
	 * i.e. they are not timeline differences but everyday differences.
	 * In a timeline approach from the midnight of winter time day to the
	 * midnight of a summer time day the diffeerence is not integral,
	 * there is an hour more or less because of the DST differences.
	 * In this approach, the days returned are ignoring this hour difference.<br/>
	 * This choice was made to conform with the rest of java date/time libraries
	 * <p>
	 * The methods have been tested against joda library for a span of 50 years
	 * before and after the current date and the results are identical.
	 *  
	 * @param date1
	 * @param date2
	 * @param intervalUnit
	 * @return
	 */
	public static long timeBetween(Calendar date1, Calendar date2, IntervalUnit intervalUnit) {
		// To follow joda's convention years, months, weeks & days don't follow daylight savings
		// however hours, minutes and seconds do follow them 
		Calendar uDate1; 
		Calendar uDate2;
		if(intervalUnit == IntervalUnit.HOURS || intervalUnit == IntervalUnit.MINUTES || intervalUnit == IntervalUnit.SECONDS) {
			uDate1 = date1;
			uDate2 = date2;
		}
		else {
			uDate1 = adjustDST(date1);
			uDate2 = adjustDST(date2);
		}
			
		
		if(intervalUnit == IntervalUnit.YEARS || intervalUnit == IntervalUnit.MONTHS) {
			// Calendar operation
			Calendar virtCal = (Calendar) uDate1.clone(); // Documentation says clone works
			
			// Do the year
			int rawYearDiff = uDate2.get(Calendar.YEAR) - uDate1.get(Calendar.YEAR);  
			int yearDiff = rawYearDiff;  
			virtCal.add(Calendar.YEAR, yearDiff); // Rounding
			if (yearDiff > 0 && virtCal.after(uDate2))
				yearDiff--;
			else if (yearDiff < 0 && virtCal.before(uDate2))
				yearDiff++;
			if (intervalUnit == IntervalUnit.YEARS)
				return yearDiff;
			
			// Continue to the month. Process the virtCal (the one with the same year field)
			virtCal = (Calendar) uDate2.clone(); // Rounding
			virtCal.set(Calendar.DAY_OF_MONTH, uDate1.get(Calendar.DAY_OF_MONTH));

			int monthDiff = 12 * rawYearDiff + uDate2.get(Calendar.MONTH) - uDate1.get(Calendar.MONTH);

			if(virtCal.after(uDate2) && monthDiff > 0) {
				monthDiff--;
			} else if (virtCal.before(uDate2) && monthDiff < 0) {
				monthDiff++;
			}
			return monthDiff;
		}
		else {
			// Temporal operation
			long diff = uDate2.getTime().getTime() - uDate1.getTime().getTime();
			switch (intervalUnit) {
			case WEEKS:
				diff /=7;
			case DAYS:
				diff /= 24;  
			case HOURS:
				diff /= 60;  
			case MINUTES:
				diff /= 60;  
			case SECONDS:
				diff /= 1000;  
			case MILLISECONDS:
			}
			return diff;
		}
		
	}

	//
	// Wrappers and factories
	//
	
	/**
	 * Factory function to construct a GregorianCalendar from a date in the default time zone 
	 */
	public static Calendar dateToGregorianCalendar(Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		
		return cal;
	}
	
	/**
	 * Factory function to construct a GregorianCalendar from a date in the supplied time zone 
	 */
	public static Calendar dateToGregorianCalendar(Date date, TimeZone timeZone) {
		Calendar cal = GregorianCalendar.getInstance(timeZone);
		cal.setTime(date);
		
		return cal;
	}

	/**
	 * Returns true if date1 is before date2. System time zone is used
	 * <p>
	 * {@link java.util.Calendar#before(Object))}
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean before (Date date1, Date date2) {
		return dateToGregorianCalendar(date1).before(dateToGregorianCalendar(date2));
	}
	
	/**
	 * Returns true if date1 is after date2. System time zone is used
	 * <p>
	 * {@link java.util.Calendar#after(Object))}
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean after (Date date1, Date date2) {
		return dateToGregorianCalendar(date1).after(dateToGregorianCalendar(date2));
	}
	
	/**
	 * compareTo version with dates. System time zone is used
	 * <p>
	 * {@link java.util.Calendar#compareTo(Calendar))}
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compare (Date date1, Date date2) {
		return dateToGregorianCalendar(date1).compareTo(dateToGregorianCalendar(date2));
	}
	
	/**
	 * Truncates the time part of a date
	 * <p>
	 * This function alters the supplied date and returns it
	 * <p>
	 * If you want to leave it unaltered call it like
	 *   {@code result = truncateTime(datetime.clone())}
	 *   
	 * @param datetime
	 * @return
	 */
	public static Date truncateTime(Date datetime) {
		Calendar cal = truncateTime(dateToGregorianCalendar(datetime));
		datetime.setTime(cal.getTimeInMillis());
		return datetime;
	}
	
	
	/**
	 * Truncates the time part of a calendar and returns a new calendar
	 * <p>
	 * This function alters the supplied calendar and returns it
	 * <p>
	 * If you want to leave it unaltered call it like
	 *   {@code result = truncateTime(calendar.clone())}
	 * 
	 * @param calendar
	 * @return
	 */
	public static Calendar truncateTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	//
	// Helpers
	//
	
	/**
	 * Helper function that returns a clone of the supplied calendar with DST stripped
	 *  
	 * @param cal The supplied calendar
	 * 
	 * @return A calendar with the same date/time but without DST
	 */
	public static Calendar adjustDST(Calendar cal) {
		TimeZone tz = cal.getTimeZone();
		Calendar adjusted = GregorianCalendar.getInstance();
		long sav = tz.inDaylightTime(cal.getTime()) ? tz.getDSTSavings() : 0;
		adjusted.setTimeInMillis(cal.getTimeInMillis() + sav);
		
		return adjusted;
	}
}
