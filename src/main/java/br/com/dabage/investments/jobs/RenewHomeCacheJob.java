package br.com.dabage.investments.jobs;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.home.HomeService;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class RenewHomeCacheJob {
	private Logger log = Logger.getLogger(RenewHomeCacheJob.class);

	@Resource
	public HomeService homeService;

	@Scheduled(cron="0 1 * * * *")
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)) {
			log.info("Executing " + RenewHomeCacheJob.class.getSimpleName() + " on " + new Date());
			homeService.loadHomeCache();
		} else {
			log.info("Job " + RenewHomeCacheJob.class.getSimpleName() + " is out of date.");
		}
	}
}
