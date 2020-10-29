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
public class CheckFIINewsDayJob {

	@Autowired
	public CheckNewsB3 checkNewsB3;

	private Logger log = LogManager.getLogger(CheckFIINewsDayJob.class);

	static DateFormat dateFormatSearch = new SimpleDateFormat("yyyy-MM-dd");

	@Scheduled(cron="0 0 22 * * *")
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)) {
			log.info("Executing " + CheckFIINewsDayJob.class.getSimpleName() + " on " + new Date());

			String query = "fii";
			String date = dateFormatSearch.format(new Date());
			int qtyNews = checkNewsB3.run(query, date, date);
			log.info(qtyNews + " news found on " + new Date());			
		}
		else {
			log.info("Job " + CheckFIINewsDayJob.class.getSimpleName() + " is out of date.");
		}
	}
}
