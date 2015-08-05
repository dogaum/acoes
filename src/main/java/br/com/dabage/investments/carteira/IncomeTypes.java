package br.com.dabage.investments.carteira;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public final class IncomeTypes {
	public static final String DIVIDEND = "Dividendo";
	
	public static final String JCP = "JCP";

	public static final String INCOME = "Renda";

	public static final String AMORTIZATION = "Amortizacao";

	public static final List<SelectItem> incomeTypes() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(AMORTIZATION, AMORTIZATION));
		result.add(new SelectItem(DIVIDEND, DIVIDEND));
		result.add(new SelectItem(JCP, JCP));
		result.add(new SelectItem(INCOME, INCOME));

		return result;
	}
}
