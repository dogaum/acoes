package br.com.dabage.investments.controller;


import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.carteira.PortfolioService;
import br.com.dabage.investments.home.HomeService;
import br.com.dabage.investments.home.HomeVO;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.user.UserTO;

@Controller(value="homeView")
@RequestScoped
public class HomeView extends BasicView implements Serializable {

	/** */
	private static final long serialVersionUID = -955941018296280391L;

	@Autowired
	CarteiraRepository carteiraRepository;

	@Autowired
	PortfolioService portfolioService;

	@Autowired
	HomeService homeService;

	private HomeVO home;

	private String credits;

	@PostConstruct
	public void initHome() {
		credits = getMessage("app.home.credits");
		//homeService.loadHomeCache();
	}

	public void init() {
		UserTO user = getUserLoggedIn();
		home = homeService.getHomeFromUser(user);
	}

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public HomeVO getHome() {
		return home;
	}

	public void setHome(HomeVO home) {
		this.home = home;
	}

}