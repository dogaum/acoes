package br.com.dabage.investments.jobs;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.news.CheckNews;

@Component
public class CheckFIINewsJob {

	@Autowired
	public CheckNews checkNews;

	@Scheduled(fixedDelay=60000)
	public void execute() {
		System.out.println("Executing " + CheckFIINewsJob.class.getSimpleName() + " on " + new Date());
		String query = "fii";
		int qtyNews = checkNews.run(query);
		//checkNews.run2();
		System.out.println(qtyNews + " news found on " + new Date());
	}
}
