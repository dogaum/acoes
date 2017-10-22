package br.com.dabage.investments.jobs;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.company.InsertFIITickers;
import br.com.dabage.investments.company.InsertTickers;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class CheckNewCompaniesJob {

	@Autowired
	public InsertFIITickers insertFIITickers;
	
	@Autowired
	public InsertTickers insertTickers;

	@Scheduled(fixedDelay=86400000, initialDelay=3000000)
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)) {
			System.out.println("Executing " + CheckNewCompaniesJob.class.getSimpleName() + " on " + new Date());
			insertFIITickers.run();
			insertTickers.run();			
		}

	}
}
