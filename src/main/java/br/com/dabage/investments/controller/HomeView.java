package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.util.List;

import javax.faces.bean.RequestScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.PortfolioService;
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

	private List<CarteiraTO> carteiras;

	private String credits;

	private Double totalCarteiras;
	private Double totalActualCarteiras;

	public void init() {
		UserTO user = getUserLoggedIn();
		carteiras = carteiraRepository.findByUser(user);
		totalCarteiras = 0D;
		totalActualCarteiras = 0D;
		for (CarteiraTO carteira : carteiras) {
			portfolioService.calculatePortfolio(carteira);
			totalCarteiras += carteira.getTotalPortfolio();
			totalActualCarteiras += carteira.getTotalPortfolioActual();
		}

		credits = getMessage("app.home.credits");
	}

	public List<CarteiraTO> getCarteiras() {
		return carteiras;
	}

	public void setCarteiras(List<CarteiraTO> carteiras) {
		this.carteiras = carteiras;
	}

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public Double getTotalCarteiras() {
		return totalCarteiras;
	}

	public void setTotalCarteiras(Double totalCarteiras) {
		this.totalCarteiras = totalCarteiras;
	}

	public Double getTotalActualCarteiras() {
		return totalActualCarteiras;
	}

	public void setTotalActualCarteiras(Double totalActualCarteiras) {
		this.totalActualCarteiras = totalActualCarteiras;
	}

}