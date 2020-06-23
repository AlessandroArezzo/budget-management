package com.balance.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTestsUtil {
	private DateTestsUtil() { }
	
	private static final DateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy"); 

	public static Date getFirstDayOfYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_YEAR, 1); 
		return parseDate(cal.getTime());
	}
	
	public static Date getLastDayOfYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, 11); 
		cal.set(Calendar.DAY_OF_MONTH, 31);
		return parseDate(cal.getTime());
	}
	
	public static Date getDateFromYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		return parseDate(cal.getTime());
	}
	
	private static Date parseDate(Date date) {
		try {
			date=FORMATTER.parse(FORMATTER.format(date));
		} catch (ParseException e) {}
		return date;
	}
}
