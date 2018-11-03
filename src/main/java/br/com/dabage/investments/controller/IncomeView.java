package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.company.IncomeLabel;
import br.com.dabage.investments.company.IncomeTotal;
import br.com.dabage.investments.config.IncomeService;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;

@Controller(value="incomeView")
@RequestScoped
public class IncomeView extends BasicView implements Serializable {


	/** */
	private static final long serialVersionUID = 151099289434423975L;

	

	private List<IncomeTotal> incomes;

	private IncomeLabel incomeLabel;

	private IncomeCompanyTO incomeCompany;

    @Resource
    IncomeCompanyRepository incomeCompanyRepository;

    @Resource
    CompanyRepository companyRepository;

	@Autowired
	GetQuotation getQuotation;

	@Autowired
	IncomeService incomeService;

	/**
	 * Initialize this View
	 * @return
	 */
	public String init() {
		incomes = incomeService.getIncomesHistory();
		incomeLabel = incomeService.getIncomeLabels();

		return "rendimentos";
	}

	/**
	 * Reset income history
	 */
	public void resetIncomes() {
		incomeService.calcIncomesHistory();
		incomes = incomeService.getIncomesHistory();
		incomeLabel = incomeService.getIncomeLabels();
	}

	/**
	 * Prepare a Income to be add
	 * @param event
	 */
	public void prepareIncome(ActionEvent event) {
		incomeCompany = new IncomeCompanyTO();
	}

	/**
	 * Add a new IncomeCompanyTO
	 * @param event
	 */
	public void addIncome(ActionEvent event) {
		if (incomeCompany.getStock().isEmpty()) {
			addWarnMessage("Infome o codigo da Acao/Fundo.");
			return;
		}

		if (incomeCompany.getIncomeDate() == null) {
			addWarnMessage("Infome a data de divulgacao.");
			return;
		}

		if (incomeCompany.getYearMonthDate() == null) {
			addWarnMessage("Infome o mes de referencia.");
			return;
		}

		if (incomeCompany.getValue() == null) {
			addWarnMessage("Infome o valor do provento.");
			return;
		}

		CompanyTO company = companyRepository.findByTicker(incomeCompany.getStock());
		if (company == null) {
			addWarnMessage("Empresa nao encontrada.");
			return;
		}
		incomeCompany.setIdCompany(company.getId());

		IncomeCompanyTO old = incomeCompanyRepository.findByStockAndYearMonth(incomeCompany.getStock(), incomeCompany.getYearMonth());
		if (old == null) {
			incomeCompanyRepository.save(incomeCompany);		
		} else {
			old.setValue(incomeCompany.getValue());
			old.setIncomeDate(incomeCompany.getIncomeDate());
			incomeCompanyRepository.save(old);
		}

		incomeCompany = new IncomeCompanyTO();

		incomeService.calcIncomesHistory();
		incomes = incomeService.getIncomesHistory();
		incomeLabel = incomeService.getIncomeLabels();
		
		RequestContext rc = RequestContext.getCurrentInstance();
	    rc.execute("PF('addIncomeReDlg').hide()");
	}

	public List<IncomeTotal> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<IncomeTotal> incomes) {
		this.incomes = incomes;
	}

	public IncomeLabel getIncomeLabel() {
		return incomeLabel;
	}

	public void setIncomeLabel(IncomeLabel incomeLabel) {
		this.incomeLabel = incomeLabel;
	}

	public IncomeCompanyTO getIncomeCompany() {
		return incomeCompany;
	}

	public void setIncomeCompany(IncomeCompanyTO incomeCompany) {
		this.incomeCompany = incomeCompany;
	}

}