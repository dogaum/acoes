package br.com.dabage.investments.jobs;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.news.CheckNews;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class CheckIncomesJob {

	@Autowired
	public CheckNews checkNews;

	@Scheduled(fixedDelay=3600000)
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)
				&& (cal.get(Calendar.HOUR_OF_DAY) > 8 && cal.get(Calendar.HOUR_OF_DAY) < 19)) {
			System.out.println("Executing " + CheckIncomesJob.class.getSimpleName() + " on " + new Date());

			checkNews.checkIncomes();
		}
	}
}
