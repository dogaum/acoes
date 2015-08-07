package br.com.dabage.investments.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.PortfolioService;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.UserRepository;
import br.com.dabage.investments.user.UserTO;

@Service
public class HomeService {

	@Autowired
	CarteiraRepository carteiraRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PortfolioService portfolioService;
	
	static Map<UserTO, HomeVO> homeCache;

	/**
	 * Initialize all home
	 */
	public void loadHomeCache() {
		homeCache = new HashMap<UserTO, HomeVO>();
		List<UserTO> users = userRepository.findAll();
		for (UserTO user : users) {
			HomeVO home = loadHomeByUser(user);
			homeCache.put(user, home);
		}
	}

	public HomeVO loadHomeByUser(UserTO user) {
		HomeVO home = new HomeVO();
		home.setUser(user);
		home.setLoadTime(new Date());
		home.setCarteiras(new ArrayList<CarteiraVO>());

		Double totalCarteiras = 0D;
		Double totalActualCarteiras = 0D;

		List<CarteiraTO> carteiras = carteiraRepository.findByUser(user);
		for (CarteiraTO carteira : carteiras) {
			portfolioService.calculatePortfolio(carteira);
			CarteiraVO carteiraVO = new CarteiraVO();
			carteiraVO.setItens(carteira.getItens());
			carteiraVO.setLastIncome(carteira.getLastIncome());
			carteiraVO.setLastNegotiation(carteira.getLastNegotiation());
			carteiraVO.setName(carteira.getName());
			carteiraVO.setPercentTotalActual(carteira.getPercentTotalActual());
			carteiraVO.setPercentTotalPos(carteira.getPercentTotalPos());
			carteiraVO.setTotalCalculateResult(carteira.getTotalCalculateResult());
			carteiraVO.setTotalPortfolio(carteira.getTotalPortfolio());
			carteiraVO.setTotalPortfolioActual(carteira.getTotalPortfolioActual());
			carteiraVO.setTotalPortfolioActualPlusIncome(carteira.getTotalPortfolioActualPlusIncome());
			carteiraVO.setTotalPortfolioIncome(carteira.getTotalPortfolioIncome());

			totalCarteiras += carteiraVO.getTotalPortfolio();
			totalActualCarteiras += carteiraVO.getTotalPortfolioActual();

			home.getCarteiras().add(carteiraVO);
		}
		home.setTotalCarteiras(totalCarteiras);
		home.setTotalActualCarteiras(totalActualCarteiras);

		return home;
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public HomeVO getHomeFromUser(UserTO user) {
		if (homeCache == null) {
			loadHomeCache();
		}

		HomeVO home = homeCache.get(user);
		if (home == null) {
			home = loadHomeByUser(user);
			homeCache.put(user, home);
		}

		return home;
	}

}
