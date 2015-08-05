package br.com.dabage.investments.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtils {

	private static final DateFormat formatYearMonth = new SimpleDateFormat("yyyyMM");

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
}
