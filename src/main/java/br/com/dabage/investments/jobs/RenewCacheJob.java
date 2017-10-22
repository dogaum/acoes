package br.com.dabage.investments.jobs;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class RenewCacheJob {

	@Resource
	public GetQuotation getQuotation;

	@Scheduled(fixedDelay=90000)
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)
				&& (cal.get(Calendar.HOUR_OF_DAY) > 9 && cal.get(Calendar.HOUR_OF_DAY) < 19)) {
			System.out.println("Executing " + RenewCacheJob.class.getSimpleName() + " on " + new Date());
			getQuotation.renewCache();			
		} else {
			System.out.println("Job " + RenewCacheJob.class.getSimpleName() + " out of date: " + new Date());
		}

	}
}
