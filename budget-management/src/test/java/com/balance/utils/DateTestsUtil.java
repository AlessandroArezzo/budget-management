package com.balance.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

public class DateTestsUtil {
	
	private DateTestsUtil() { }
	
	public static Date getFirstDayOfYear(int year) {
		return getDate(1,1,year);
	}
	
	public static Date getLastDayOfYear(int year) {
		return getDate(31,12,year);
	}
	
	public static Date getDateFromYear(int year) {
		Random random=new Random();
		LocalDate localDate = LocalDate.ofYearDay(year, random.nextInt(365)+1);
		return Date.from(localDate.atStartOfDay(ZoneId.of("Z")).toInstant());
	}
	
	public static Date getDate(int day, int month, int year) {
		LocalDate localDate = LocalDate.of( year, month, day);
		return Date.from(localDate.atStartOfDay(ZoneId.of("Z")).toInstant());
	}
	
}
