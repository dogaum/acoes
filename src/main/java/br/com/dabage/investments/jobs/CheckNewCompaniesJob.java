package br.com.dabage.investments.jobs;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.company.InsertFIITickers;
import br.com.dabage.investments.company.InsertTickers;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class CheckNewCompaniesJob {

	private Logger log = Logger.getLogger(CheckNewCompaniesJob.class);

	@Autowired
	public InsertFIITickers insertFIITickers;
	
	@Autowired
	public InsertTickers insertTickers;

	@Scheduled(fixedDelay=86400000, initialDelay=3000)
	public void execute() {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal)) {
			log.info("Executing " + CheckNewCompaniesJob.class.getSimpleName() + " on " + new Date());
			//insertFIITickers.run();
			//insertTickers.run();			
		} else {
			log.info("Job " + CheckNewCompaniesJob.class.getSimpleName() + " is out of date.");
		}

	}
}
