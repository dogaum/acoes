package br.com.dabage.investments.carteira;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.repositories.AbstractDocument;
import br.com.dabage.investments.user.UserTO;

@Document(collection="carteiras")
public class CarteiraTO extends AbstractDocument {

	public CarteiraTO(String name) {
		this.name = name;
		this.createDate = new Date();
		this.status = CarteiraStatus.Ativa;
	}

	@Indexed
	@DBRef
	private UserTO user;

	private Date createDate;

	private String name;

	@DBRef
	private List<NegotiationTO> negotiations;

	@DBRef
	private List<IncomeTO> incomes;

	private Date deleteDate;

	private CarteiraStatus status;

	/**
	 * Totals
	 * @return
	 */
	@Transient
	private Double totalPortfolio;
	@Transient
	private Double totalPortfolioActual;
	@Transient
	private Double totalPortfolioIncome;
	@Transient
	private Double totalCalculateResult;
	@Transient
	private Double totalPortfolioActualPlusIncome;
	@Transient
	private Double percentTotalActual;
	@Transient
	private Double percentTotalPos;
	@Transient
	private NegotiationTO lastNegotiation;
	@Transient
	private IncomeTO lastIncome;

	public UserTO getUser() {
		return user;
	}

	public void setUser(UserTO user) {
		this.user = user;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NegotiationTO> getNegotiations() {
		return negotiations;
	}

	public void setNegotiations(List<NegotiationTO> negotiations) {
		this.negotiations = negotiations;
	}

	public Date getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	public CarteiraStatus getStatus() {
		return status;
	}

	public void setStatus(CarteiraStatus status) {
		this.status = status;
	}

	public List<IncomeTO> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<IncomeTO> incomes) {
		this.incomes = incomes;
	}

	public Double getTotalPortfolio() {
		return totalPortfolio;
	}

	public void setTotalPortfolio(Double totalPortfolio) {
		this.totalPortfolio = totalPortfolio;
	}

	public Double getTotalPortfolioActual() {
		return totalPortfolioActual;
	}

	public void setTotalPortfolioActual(Double totalPortfolioActual) {
		this.totalPortfolioActual = totalPortfolioActual;
	}

	public Double getTotalPortfolioIncome() {
		return totalPortfolioIncome;
	}

	public void setTotalPortfolioIncome(Double totalPortfolioIncome) {
		this.totalPortfolioIncome = totalPortfolioIncome;
	}

	public Double getTotalCalculateResult() {
		return totalCalculateResult;
	}

	public void setTotalCalculateResult(Double totalCalculateResult) {
		this.totalCalculateResult = totalCalculateResult;
	}

	public Double getTotalPortfolioActualPlusIncome() {
		return totalPortfolioActualPlusIncome;
	}

	public void setTotalPortfolioActualPlusIncome(
			Double totalPortfolioActualPlusIncome) {
		this.totalPortfolioActualPlusIncome = totalPortfolioActualPlusIncome;
	}

	public Double getPercentTotalActual() {
		return percentTotalActual;
	}

	public void setPercentTotalActual(Double percentTotalActual) {
		this.percentTotalActual = percentTotalActual;
	}

	public Double getPercentTotalPos() {
		return percentTotalPos;
	}

	public void setPercentTotalPos(Double percentTotalPos) {
		this.percentTotalPos = percentTotalPos;
	}

	public NegotiationTO getLastNegotiation() {
		return lastNegotiation;
	}

	public void setLastNegotiation(NegotiationTO lastNegotiation) {
		this.lastNegotiation = lastNegotiation;
	}

	public IncomeTO getLastIncome() {
		return lastIncome;
	}

	public void setLastIncome(IncomeTO lastIncome) {
		this.lastIncome = lastIncome;
	}

}
