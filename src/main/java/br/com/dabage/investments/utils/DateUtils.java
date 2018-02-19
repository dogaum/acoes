package br.com.dabage.investments.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtils {

	private static final DateFormat formatYearMonth = new SimpleDateFormat("yyyyMM");

	private static final DateFormat formatMonthYear = new SimpleDateFormat("MM/yyyy");
	
	private static final DateFormat formatMonth = new SimpleDateFormat("MM");

	private static final DateFormat formatDayMonthYear = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Get YearMonth in a Date
	 * @param date
	 * @return
	 */
	public static Integer getYearMonth(Date date) {
		return Integer.parseInt(formatYearMonth.format(date));
	}

	/**
	 * Get a date from a YearMonth
	 * @param yearMonth
	 * @return
	 */
	public static Date getDateFromYearMonth(Integer yearMonth) {
		Date result = null;
		String yearMonthStr = yearMonth + "";
		try {
			result = formatYearMonth.parse(yearMonthStr);
		} catch (ParseException e) {
		}

		return result;
	}

	/**
	 * 
	 * @param cal
	 * @return
	 */
	public static boolean isWorkingDay(Calendar cal) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
            return false;
        return true;
    }

	public static String getStringMonth(Date date) {
		return formatMonth.format(date);
	}

	public static String formatToMonthYear(Integer yearMonth) {
		String result = "";
		if (yearMonth != null) {
			try {
				Date date = formatYearMonth.parse(yearMonth + "");
				result = formatMonthYear.format(date);
			} catch (ParseException e) {
			}
		}

		return result;
	}

	public static Date parseStrToDate(String str) {
		Date ret = null;

		try {
			ret = formatDayMonthYear.parse(str);
		} catch (ParseException e) {
		}

		return ret;
	}

	public static String formatDateToStr(Date date) {
		String ret = null;

		ret = formatDayMonthYear.format(date);

		return ret;
	}
}
