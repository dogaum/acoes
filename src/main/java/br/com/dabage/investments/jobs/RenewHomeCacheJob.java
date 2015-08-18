package br.com.dabage.investments.jobs;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.home.HomeService;

@Component
public class RenewHomeCacheJob {

	@Resource
	public HomeService homeService;

	@Scheduled(cron="0 1 * * * *")
	public void execute() {
		System.out.println("Executing " + RenewHomeCacheJob.class.getSimpleName() + " on " + new Date());
		homeService.loadHomeCache();
	}
}
