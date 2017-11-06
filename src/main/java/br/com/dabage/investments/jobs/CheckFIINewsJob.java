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
public class CheckFIINewsJob {

	@Autowired
	public CheckNews checkNews;

	private Logger log = Logger.getLogger(CheckFIINewsJob.class);

	@Scheduled(fixedDelay=60000)
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)
				&& (cal.get(Calendar.HOUR_OF_DAY) > 8 && cal.get(Calendar.HOUR_OF_DAY) < 22)) {
			log.info("Executing " + CheckFIINewsJob.class.getSimpleName() + " on " + new Date());

			String query = "fii";
			int qtyNews = checkNews.run(query, NewsFilterType.DAY, null, null);
			log.info(qtyNews + " news found on " + new Date());			
		}
		else {
			log.info("Job " + CheckFIINewsJob.class.getSimpleName() + " is out of date.");
		}
	}
}
