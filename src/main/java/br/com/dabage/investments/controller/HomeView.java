package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.RequestScoped;

import org.springframework.stereotype.Controller;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.NegotiationType;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.user.UserTO;

@Controller(value="homeView")
@RequestScoped
public class HomeView extends BasicView implements Serializable {

	/** */
	private static final long serialVersionUID = -955941018296280391L;

	@Resource
	CarteiraRepository carteiraRepository;

	private List<CarteiraTO> carteiras;

	private String credits;
	
	public void init() {
		UserTO user = getUserLoggedIn();
		carteiras = carteiraRepository.findByUser(user);
		for (CarteiraTO carteira : carteiras) {
			Double totalPortfolio = 0D;
			for (NegotiationTO neg : carteira.getNegotiations()) {
				if (neg.getNegotiationType().equals(NegotiationType.Compra)) {
					totalPortfolio += (neg.getQuantity() * neg.getValue());
				} else {
					totalPortfolio -= (neg.getQuantity() * neg.getValue());
				}
			}
			carteira.setTotalPortfolio(totalPortfolio);
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

}