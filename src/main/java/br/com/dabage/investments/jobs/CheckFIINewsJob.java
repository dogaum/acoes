package br.com.dabage.investments.jobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.news.CheckNewsB3;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class CheckFIINewsJob {

	@Autowired
	public CheckNewsB3 checkNewsB3;

	private Logger log = LogManager.getLogger(CheckFIINewsJob.class);

	static DateFormat dateFormatSearch = new SimpleDateFormat("yyyy-MM-dd");

	@Scheduled(fixedDelay=90000)
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)
				&& (cal.get(Calendar.HOUR_OF_DAY) >= 8 && cal.get(Calendar.HOUR_OF_DAY) <= 22)) {
			log.info("Executing " + CheckFIINewsJob.class.getSimpleName() + " on " + new Date());

			String query = "fii";
			String date = dateFormatSearch.format(cal.getTime());
			int qtyNews = checkNewsB3.run(query, date, date);
			log.info(qtyNews + " news found on " + new Date());			
		} else {
			log.info("Job " + CheckFIINewsJob.class.getSimpleName() + " is out of date.");
		}
	}
}
