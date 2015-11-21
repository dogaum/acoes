package br.com.dabage.investments.company;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.home.IncomeVO;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.utils.DateUtils;

@Service
public class CompanyService {

	@Autowired
	CarteiraRepository carteiraRepository;
	
	@Autowired
	IncomeCompanyRepository incomeCompanyRepository;

	@Autowired
	GetQuotation getQuotation;

	private static final DateFormat formatMonthYear = new SimpleDateFormat("MM/yyyy");

	public List<IncomeVO> getLastYearIncomesByStock(String stock) {

		List<IncomeVO> result = new ArrayList<IncomeVO>();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < 13; i++) {
			IncomeVO income = new IncomeVO(DateUtils.getYearMonth(cal.getTime()) + "");
			income.add(0D);
			result.add(income);
			cal.add(Calendar.MONTH, -1);
		}

		LinkedList<IncomeCompanyTO> incomesCompany = incomeCompanyRepository.findByStockOrderByIncomeDateDesc(stock);

		if (incomesCompany != null) {
			for (IncomeCompanyTO incomeTO : incomesCompany) {
				IncomeVO income = new IncomeVO(incomeTO.getYearMonth() + "");
				if (result.contains(income)) {
					int index = result.indexOf(income);
					income = result.get(index);
				} else {
					result.add(income);
				}
				income.add(incomeTO.getValue());
			}

		}
		
		Collections.sort(result);
		int total = result.size();
		if (total > 13) {
			int diff = (total - 13);
			for (int i = 0; i < diff; i++) {
				result.remove(0);
			}
		}

		return result;
	}

	private List<IncomeTotal> getLasIncomes(List<IncomeCompanyTO> incomesCompany) {
		List<IncomeTotal> incomes = new ArrayList<IncomeTotal>();
		IncomeLabel incomeLabel = new IncomeLabel();

		Calendar calNow = Calendar.getInstance();
		int fieldNumber = 1;
		do {

			setLabel(incomeLabel, fieldNumber, calNow.getTime());

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
		} while (fieldNumber <= 24);

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

		return incomes;
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
}
