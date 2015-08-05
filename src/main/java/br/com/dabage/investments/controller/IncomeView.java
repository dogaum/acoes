package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.carteira.IncomeTO;
import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.company.IncomeLabel;
import br.com.dabage.investments.company.IncomeTotal;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.utils.DateUtils;

@Controller(value="incomeView")
@RequestScoped
public class IncomeView extends BasicView implements Serializable {


	/** */
	private static final long serialVersionUID = 151099289434423975L;

	private static final DateFormat formatMonthYear = new SimpleDateFormat("MM/yyyy");

	private List<IncomeTotal> incomes;

	private IncomeLabel incomeLabel;

	private IncomeCompanyTO incomeCompany;

    @Resource
    IncomeCompanyRepository incomeCompanyRepository;

    @Resource
    CompanyRepository companyRepository;

	@Autowired
	GetQuotation getQuotation;

	/**
	 * Initialize this View
	 * @return
	 */
	public String init() {
		calcResults();

		return "rendimentos";
	}

	/**
	 * Calculate results
	 */
	private void calcResults() {
		incomes = new ArrayList<IncomeTotal>();
		List<IncomeCompanyTO> incomesCompany = null;
		incomeLabel = new IncomeLabel();

		Calendar calNow = Calendar.getInstance();
		int fieldNumber = 1;
		do {
			Integer yearMonth = DateUtils.getYearMonth(calNow.getTime());
			setLabel(incomeLabel, fieldNumber, calNow.getTime());
			incomesCompany = incomeCompanyRepository.findByYearMonth(yearMonth);			
			for (IncomeCompanyTO income : incomesCompany) {
				IncomeTotal result = new IncomeTotal(income.getStock());
				if (incomes.contains(result)) {
					int index = incomes.indexOf(result);
					result = incomes.get(index);
				} else {
					incomes.add(result);
				}
				result.addIncome(income);
				setValue(result, fieldNumber, income.getValue());
			}

			// Regrets a month per once
			calNow.add(Calendar.MONTH, -1);
			fieldNumber++;
		} while (incomesCompany != null && !incomesCompany.isEmpty());

		for (IncomeTotal inc : incomes) {
			List<IncomeCompanyTO> incCompanies = inc.getIncomes();
			Collections.sort(incCompanies, IncomeCompanyTO.IncomeDateDesc);

			int count = 0;
			Double avg12 = 0D;
			Double avg24 = 0D;
			for (IncomeCompanyTO incomeCompanyTO : incCompanies) {
				if (incomeCompanyTO.getValue().equals(0D)) {
					continue;
				}
				count++;
				if (count < 13) {
					avg12 += incomeCompanyTO.getValue();
					avg24 += incomeCompanyTO.getValue();
				} else if (count > 12 && count < 25) {
					avg24 += incomeCompanyTO.getValue();
				}
			}

			if (count >= 12) {
				inc.setAvg12(avg12 / 12);
			} else {
				inc.setAvg12(avg12 / count);
			}

			if (count >= 24) {
				inc.setAvg24(avg24 / 24);
			} else {
				inc.setAvg24(avg24 / count);
			}

			inc.setLastQuote(getQuotation.getLastQuoteCache(inc.getStock()));
			Double value = 0D;
			if (inc.getValue1() != null) {
				value = inc.getValue1();
			} else if (inc.getValue2() != null) {
				value = inc.getValue2();
			} else if (inc.getValue3() != null) {
				value = inc.getValue3();
			} else if (inc.getValue4() != null) {
				value = inc.getValue4();
			}

			if (value != 0D) {
				Double lastPercent = (value / inc.getLastQuote());
				inc.setLastPercent(lastPercent);				
			}

		}

		Collections.sort(incomes);
	}

	private void setValue(IncomeTotal result, int fieldNumber, Double value) {
		Class<?> clazz = IncomeTotal.class;
		try {
			Field fieldValue = clazz.getField("value" + fieldNumber);
			fieldValue.setAccessible(true);
			fieldValue.set(result, value);
		} catch (Exception e) {
		}
	}

	private void setLabel(IncomeLabel incomeLabel, int fieldNumber, Date monthYear) {
		Class<?> clazz = IncomeLabel.class;
		try {
			Field fieldLabel = clazz.getField("lbl" + fieldNumber);
			fieldLabel.setAccessible(true);
			String label = formatMonthYear.format(monthYear);
			fieldLabel.set(incomeLabel, label);
		} catch (Exception e) {
		}
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

		if (incomeCompanyRepository.findByStockAndYearMonth(incomeCompany.getStock(), incomeCompany.getYearMonth()) == null) {
			incomeCompanyRepository.save(incomeCompany);	
		}

		incomeCompany = new IncomeCompanyTO();
		calcResults();
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