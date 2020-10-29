package br.com.dabage.investments.home;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.com.dabage.investments.user.UserTO;

public class HomeVO implements Serializable {

	/** */
	private static final long serialVersionUID = -2088648647200256827L;

	private UserTO user;

	private List<CarteiraVO> carteiras;

	private Date loadTime;

	private Double totalCarteiras;

	private Double totalActualCarteiras;

	private Double totalPercentCarteiras;

	public UserTO getUser() {
		return user;
	}

	public void setUser(UserTO user) {
		this.user = user;
	}

	public List<CarteiraVO> getCarteiras() {
		return carteiras;
	}

	public void setCarteiras(List<CarteiraVO> carteiras) {
		this.carteiras = carteiras;
	}

	public Date getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(Date loadTime) {
		this.loadTime = loadTime;
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

	public Double getTotalPercentCarteiras() {
		return totalPercentCarteiras;
	}

	public void setTotalPercentCarteiras(Double totalPercentCarteiras) {
		this.totalPercentCarteiras = totalPercentCarteiras;
	}

}
