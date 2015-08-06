package br.com.dabage.investments.jobs;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.quote.GetQuotation;

@Component
public class RenewCacheJob {

	@Resource
	public GetQuotation getQuotation;

	@Scheduled(fixedDelay=90000)
	public void execute() {
		System.out.println("Executing " + RenewCacheJob.class.getSimpleName() + " on " + new Date());
		getQuotation.renewCache();
	}
}
