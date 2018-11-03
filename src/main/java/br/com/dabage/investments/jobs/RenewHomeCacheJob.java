package br.com.dabage.investments.jobs;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.home.HomeService;
import br.com.dabage.investments.news.CheckNews;
import br.com.dabage.investments.news.NewsFilterType;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class RenewHomeCacheJob {
	private Logger log = Logger.getLogger(RenewHomeCacheJob.class);

	@Autowired
	public HomeService homeService;

	@Autowired
	public CheckNews checkNews;

	@Scheduled(cron="0 1 * * * *")
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)) {
			log.info("Executing " + RenewHomeCacheJob.class.getSimpleName() + " on " + new Date());
			homeService.loadHomeCache();

			log.info("Executing check news hourly. " + new Date());
			String query = "fii";
			int qtyNews = checkNews.run(query, NewsFilterType.DAY, null, null, 10);
			log.info(qtyNews + " news found on " + new Date());
		} else {
			log.info("Job " + RenewHomeCacheJob.class.getSimpleName() + " is out of date.");
		}
	}
}
