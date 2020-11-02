package br.com.dabage.investments.config;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.company.IncomeLabel;
import br.com.dabage.investments.company.IncomeTotal;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.utils.DateUtils;

@Service
public class IncomeService {

	private Logger log = LogManager.getLogger(IncomeService.class);

	@Autowired
    IncomeCompanyRepository incomeCompanyRepository;

	@Autowired
    CompanyRepository companyRepository;

	@Autowired
	GetQuotation getQuotation;

	private static final DateFormat formatMonthYear = new SimpleDateFormat("MM/yyyy");

	/**
	 * Income cache
	 */
	private static List<IncomeTotal> incomes = null;

	/**
	 * Income Labels
	 */
	private static IncomeLabel incomeLabel = null;

	/**
	 * Calculate results
	 */
	public void calcIncomesHistory() {
		log.info("Calculating incomes history - start");
		incomes = new ArrayList<IncomeTotal>();
		List<IncomeTotal> temp = new ArrayList<IncomeTotal>();
		incomeLabel= new IncomeLabel();
		List<IncomeCompanyTO> incomesCompany = null;
		Calendar calNow = Calendar.getInstance();
		int fieldNumber = 1;
		do {
			Integer yearMonth = DateUtils.getYearMonth(calNow.getTime());
			setLabel(incomeLabel, fieldNumber, calNow.getTime());
			incomesCompany = incomeCompanyRepository.findByYearMonth(yearMonth);			
			for (IncomeCompanyTO income : incomesCompany) {
				IncomeTotal result = new IncomeTotal(income.getStock());
				if (temp.contains(result)) {
					int index = temp.indexOf(result);
					result = temp.get(index);
				} else {
					temp.add(result);
				}
				result.addIncome(income);
				setValue(result, fieldNumber, income.getValue());
			}

			// Regrets a month per once
			calNow.add(Calendar.MONTH, -1);
			fieldNumber++;
		} while (fieldNumber < 25);

		for (IncomeTotal inc : temp) {
			CompanyTO company = companyRepository.findByTicker(inc.getStock());
			if (company.getCategory() == null || !company.getCategory().equals("FII")) {
				continue;
			}

			inc.setCompany(company);
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

			inc.setLastQuote(getQuotation.getLastQuoteCache(inc.getStock(), false));
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

			if (inc.getAvg12() != null && inc.getLastQuote() != null) {
				Double percent12 = (inc.getAvg12() / inc.getLastQuote());
				inc.setPercentAvg12(percent12);				
			} else {
				inc.setPercentAvg12(0D);
			}

			if (inc.getAvg24() != null && inc.getLastQuote() != null) {
				Double percent24 = (inc.getAvg24() / inc.getLastQuote());
				inc.setPercentAvg24(percent24);				
			} else {
				inc.setPercentAvg24(0D);
			}
			incomes.add(inc);
		}
		Collections.sort(incomes);
		log.info("Calculating incomes history - end");
	}

	/**
	 * Return Get Income History
	 * @return
	 */
	public List<IncomeTotal> getIncomesHistory() {
		if (incomes == null || incomes.isEmpty()) {
			this.calcIncomesHistory();
		}
		return incomes;
	}

	public IncomeLabel getIncomeLabels() {
		return incomeLabel;
	}

	/**
	 * Get stock income history
	 * @param stock
	 * @return
	 */
	public IncomeTotal getIncomeHistory(String stock) {
		IncomeTotal result = new IncomeTotal(stock);
		if (incomes == null || incomes.isEmpty()) {
			this.calcIncomesHistory();
		}

		if (incomes.contains(result)) {
			int idx = incomes.indexOf(result);
			result = incomes.get(idx);
		}

		return result;
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

	private void setValue(IncomeTotal result, int fieldNumber, Double value) {
		Class<?> clazz = IncomeTotal.class;
		try {
			Field fieldValue = clazz.getField("value" + fieldNumber);
			fieldValue.setAccessible(true);
			fieldValue.set(result, value);
		} catch (Exception e) {
		}
	}

}
