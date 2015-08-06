package br.com.dabage.investments.jobs;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.company.InsertFIITickers;
import br.com.dabage.investments.company.InsertTickers;

@Component
public class CheckNewCompaniesJob {

	@Autowired
	public InsertFIITickers insertFIITickers;
	
	@Autowired
	public InsertTickers insertTickers;

	@Scheduled(fixedDelay=86400000)
	public void execute() {
		System.out.println("Executing " + CheckNewCompaniesJob.class.getSimpleName() + " on " + new Date());
		insertFIITickers.run();
		insertTickers.run();
	}
}
