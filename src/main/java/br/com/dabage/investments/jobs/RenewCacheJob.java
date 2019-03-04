package br.com.dabage.investments.jobs;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class RenewCacheJob {

	private Logger log = Logger.getLogger(RenewCacheJob.class);

	@Resource
	public GetQuotation getQuotation;

	@Scheduled(fixedDelay=90000)
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)
				&& (cal.get(Calendar.HOUR_OF_DAY) > 10 && cal.get(Calendar.HOUR_OF_DAY) < 18)) {
			log.info("Executing " + RenewCacheJob.class.getSimpleName() + " on " + new Date());
			getQuotation.renewCache();			
		} else {
			log.info("Job " + RenewCacheJob.class.getSimpleName() + " is out of date.");
		}
	}
}
