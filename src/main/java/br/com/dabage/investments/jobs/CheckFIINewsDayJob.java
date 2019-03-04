package br.com.dabage.investments.jobs;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.news.CheckNews;
import br.com.dabage.investments.news.NewsFilterType;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class CheckFIINewsDayJob {

	@Autowired
	public CheckNews checkNews;

	private Logger log = Logger.getLogger(CheckFIINewsDayJob.class);

	@Scheduled(cron="0 0 22 * * *")
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)) {
			log.info("Executing " + CheckFIINewsDayJob.class.getSimpleName() + " on " + new Date());

			String query = "fii";
			int qtyNews = checkNews.run(query, NewsFilterType.DAY, null, null, 20);
			log.info(qtyNews + " news found on " + new Date());			
		}
		else {
			log.info("Job " + CheckFIINewsDayJob.class.getSimpleName() + " is out of date.");
		}
	}
}
