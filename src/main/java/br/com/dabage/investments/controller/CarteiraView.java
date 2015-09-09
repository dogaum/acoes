package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.carteira.CarteiraItemTO;
import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.IncomeTO;
import br.com.dabage.investments.carteira.IncomeTypes;
import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.NegotiationType;
import br.com.dabage.investments.carteira.PortfolioService;
import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.repositories.IncomeRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;
import br.com.dabage.investments.user.UserTO;

@Controller(value="carteiraView")
@RequestScoped
public class CarteiraView extends BasicView implements Serializable {

	/** */
	private static final long serialVersionUID = 6601802404818265880L;

	private List<CarteiraTO> carteiras;

	private CarteiraTO selectedCarteira;
	private List<CarteiraItemTO> carteiraItens;

	private CarteiraItemTO selectedCarteiraItem;
	private Double selectedCarteiraItemTotalValue;

	private String newName;

	private NegotiationTO negotiation;
	private List<SelectItem> negotiationTypes;

	private IncomeTO income;
	private List<SelectItem> incomeTypes;

	private int firstCarteiraRing;

	private boolean emptyPosition;

	private String selectedPoint;
	
	@Autowired
    CarteiraRepository carteiraRepository;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	NegotiationRepository negotiationRepository;

	@Autowired
	IncomeRepository incomeRepository;

	@Autowired
	IncomeCompanyRepository incomeCompanyRepository;
	
	@Autowired
	GetQuotation getQuotation;

	@Autowired
	PortfolioService portfolioService;

	@PostConstruct
	public void prepare() {
		incomeTypes = IncomeTypes.incomeTypes();
		negotiationTypes = new ArrayList<SelectItem>();
		for (int i = 0; i < NegotiationType.values().length; i++) {
			SelectItem item = new SelectItem(NegotiationType.values()[i], NegotiationType.values()[i].name());
			negotiationTypes.add(item);
		}
	}

	public String init() {
		UserTO user = getUserLoggedIn();
		carteiras = carteiraRepository.findByUser(user);
		emptyPosition = false;
		selectedCarteiraItem = null;
		if (carteiras != null && !carteiras.isEmpty()) {

			Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String carteiraName = params.get("carteiraName");
			if (carteiraName != null) {
				selectedCarteira = carteiraRepository.findByUserAndName(user, carteiraName);	
			} else {
				firstCarteiraRing = 0;
				selectedCarteira = carteiras.get(firstCarteiraRing);				
			}

			selectCarteira();
			selectedCarteiraItemTotalValue = 0D;
			showEmptyPosition();
		}
		return "carteiras";
	}

	/**
	 * Criar uma nova Carteira
	 */
	public void create(ActionEvent event) {
		CarteiraTO carteira = new CarteiraTO(newName);
		carteira.setUser(getUserLoggedIn());

		carteiras.add(carteira);
		carteiraRepository.save(carteira);
		newName = "";
	}

	/**
	 * Show or not empty positions
	 */
	public void showEmptyPosition() {
		if (carteiraItens != null) {
			if (emptyPosition) {
				selectCarteira();
				emptyPosition = false;
			} else {
				hideEmptyPosition();
				emptyPosition = true;
			}
		}
	}

	/**
	 * Hid empty positions
	 */
	public void hideEmptyPosition() {
		if (carteiraItens != null && !emptyPosition) {
			Iterator<CarteiraItemTO> iteItens = carteiraItens.iterator();
			while (iteItens.hasNext()) {
				CarteiraItemTO item = iteItens.next();
				if (item.getQuantity().equals(0L)) {
					iteItens.remove();
				}
			}
		}
	}
	
	/**
	 * Apresentar detalhes de uma carteira
	 */
	public void selectCarteira() {
		if (selectedCarteira == null) {
			addWarnMessage("Selecione uma carteira.");
		} else {

			// Seta qual o Ring vai ficar selecionado no refresh da tela.
			firstCarteiraRing = carteiras.indexOf(selectedCarteira);

			portfolioService.calculatePortfolio(selectedCarteira);
			carteiraItens = selectedCarteira.getItens();
			hideEmptyPosition();
		}
	}

	/**
	 * Prepara insercao de negociacao
	 * @param event
	 */
	public void prepareNegotiation(ActionEvent event) {
		negotiation = new NegotiationTO();
		negotiation.setAddDate(new Date());
	}

	public void addNegotiation(ActionEvent event) {
		if (!checkNegotiation(negotiation)) return; 

		negotiation.setIdCarteira(selectedCarteira.getId());
		negotiation.setCalculated(Boolean.FALSE);

		CarteiraItemTO carteiraItem = new CarteiraItemTO(negotiation.getStock());
		if (carteiraItens.contains(carteiraItem)) {
			int idx = carteiraItens.indexOf(carteiraItem);
			carteiraItem = carteiraItens.get(idx);			
		}

		// If NegotiationType is Venda, calculate tax and avg value
		if (negotiation.getNegotiationType().equals(NegotiationType.Venda)) {
			negotiation.setAvgBuyValue(carteiraItem.getAvgValue());

			// If the qty is equals, put the information into a Tax
			if (carteiraItem.getQuantity().equals(negotiation.getQuantity())) {
				// Cancel bought negotiations
				for (NegotiationTO oldNeg : carteiraItem.getNegotiations()) {
					if (!oldNeg.getCalculated()) {
						oldNeg.setCalculated(Boolean.TRUE);
						oldNeg.setCalculateDate(new Date());
						negotiationRepository.save(oldNeg);
					}
				}
			}

			// Put the result into the negotiation
			Double avgCost = (carteiraItem.getAvgTotalCost() / carteiraItem.getAvgQuantity()) * negotiation.getQuantity();
			Double buyTotalValue = (negotiation.getQuantity() * negotiation.getAvgBuyValue()) + avgCost;
			Double sellTotalValue = (negotiation.getValue() * negotiation.getQuantity()) - negotiation.getCosts();
			Double resultValue = sellTotalValue - buyTotalValue;
			negotiation.setCalculateValue(resultValue);
			negotiation.setCalculated(Boolean.TRUE);
			negotiation.setCalculateDate(new Date());

		} else {
			// If is Compra
			carteiraItem.addNegotiation(negotiation);
			for (NegotiationTO negCompra : carteiraItem.getNegotiations()) {
				if (negCompra.getNegotiationType().equals(NegotiationType.Compra)
						&& !negCompra.getCalculated()) {
					negCompra.setAvgBuyValue(carteiraItem.getAvgValue());
					negotiationRepository.save(negCompra);
				}
			}
		}

		negotiationRepository.save(negotiation);
		selectedCarteira.getNegotiations().add(negotiation);
		carteiraRepository.save(selectedCarteira);

		negotiation = new NegotiationTO();
		this.selectCarteira();

		RequestContext rc = RequestContext.getCurrentInstance();
	    rc.execute("PF('addNegotiationDlg').hide()");
	}

	public void editNegotiation(RowEditEvent event) {

		NegotiationTO neg = (NegotiationTO) event.getObject();
		if (!checkNegotiation(neg)) return;

		negotiationRepository.save(neg);
		this.selectCarteira();
	}

	public void editIncome(RowEditEvent event) {

		IncomeTO inc = (IncomeTO) event.getObject();

		incomeRepository.save(inc);
		this.selectCarteira();
	}
	
	/**
	 * Validates a Negotiation insert/update
	 * @param neg
	 * @return
	 */
	private boolean checkNegotiation(NegotiationTO neg) {
		if (neg == null) {
			addWarnMessage("Valores invalidos.");
			return false;
		}

		if (neg.getDtNegotiation() == null) {
			addWarnMessage("Informe uma data.");
			return false;
		}
		
		if (neg.getQuantity() == 0) {
			addWarnMessage("Quantidade deve ser maior de 0 (zero)");
			return false;
		}
		neg.setStock(neg.getStock().toUpperCase());

		if (neg.getStock().length() < 5) {
			addWarnMessage("Codigo invalido: " + neg.getStock());
			return false;
		} else {
			CompanyTO company = companyRepository.findByTicker(neg.getStock());
			if (company == null) {
				addWarnMessage("Codigo invalido: " + neg.getStock());
				return false;
			}
			neg.setStockType(company.getStockType());
		}
	
		return true;
	}

	public void deleteNegotiation() {
		negotiation.setRemoveDate(new Date());
		negotiationRepository.save(negotiation);
		carteiraRepository.save(selectedCarteira);
		selectCarteira();
	}

	public void deleteIncome() {
		incomeRepository.delete(income);
		selectedCarteira.getIncomes().remove(income);
		carteiraRepository.save(selectedCarteira);
		selectCarteira();
	}
	
	/**
	 * Prepare a Income to be add
	 * @param event
	 */
	public void prepareIncome(ActionEvent event) {
		income = new IncomeTO();
	}

	public void addIncome(ActionEvent event) {
		if (income.getStock().isEmpty()) {
			addWarnMessage("Infome o codigo da Acao/Fundo.");
			return;
		}

		if (income.getType().isEmpty()) {
			addWarnMessage("Infome o tipo de provento.");
			return;
		}

		if (income.getValue() == null) {
			addWarnMessage("Infome o valor do provento.");
			return;
		}
		
		income.setAddDate(new Date());
		income.setIdCarteira(selectedCarteira.getId());
		incomeRepository.save(income);

		selectedCarteira.getIncomes().add(income);
		carteiraRepository.save(selectedCarteira);

		//TODO se for amortization, subtrair do valor medio de aquisicao
		
		income = new IncomeTO();
		this.selectCarteira();

		RequestContext rc = RequestContext.getCurrentInstance();
	    rc.execute("PF('addIncomeDlg').hide()");
	}

	public void clicked() {
    }

	public List<CarteiraTO> getCarteiras() {
		return carteiras;
	}

	public void setCarteiras(List<CarteiraTO> carteiras) {
		this.carteiras = carteiras;
	}

	public CarteiraTO getSelectedCarteira() {
		return selectedCarteira;
	}

	public void setSelectedCarteira(CarteiraTO selectedCarteira) {
		this.selectedCarteira = selectedCarteira;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public List<CarteiraItemTO> getCarteiraItens() {
		return carteiraItens;
	}

	public void setCarteiraItens(List<CarteiraItemTO> carteiraItens) {
		this.carteiraItens = carteiraItens;
	}

	public NegotiationTO getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(NegotiationTO negotiation) {
		this.negotiation = negotiation;
	}

	public List<SelectItem> getNegotiationTypes() {
		return negotiationTypes;
	}

	public void setNegotiationTypes(List<SelectItem> negotiationTypes) {
		this.negotiationTypes = negotiationTypes;
	}

	public int getFirstCarteiraRing() {
		return firstCarteiraRing;
	}

	public void setFirstCarteiraRing(int firstCarteiraRing) {
		this.firstCarteiraRing = firstCarteiraRing;
	}

	public IncomeTO getIncome() {
		return income;
	}

	public void setIncome(IncomeTO income) {
		this.income = income;
	}

	public List<SelectItem> getIncomeTypes() {
		return incomeTypes;
	}

	public void setIncomeTypes(List<SelectItem> incomeTypes) {
		this.incomeTypes = incomeTypes;
	}

	public boolean getEmptyPosition() {
		return emptyPosition;
	}

	public void setEmptyPosition(boolean emptyPosition) {
		this.emptyPosition = emptyPosition;
	}

	public CarteiraItemTO getSelectedCarteiraItem() {
		return selectedCarteiraItem;
	}

	public void setSelectedCarteiraItem(CarteiraItemTO selectedCarteiraItem) {
		if (selectedCarteiraItem != null && selectedCarteiraItem.getIncomes() != null) {
			selectedCarteiraItemTotalValue = 0D;
			for (IncomeTO income : selectedCarteiraItem.getIncomes()) {
				selectedCarteiraItemTotalValue += income.getValue();
			}
		}
		this.selectedCarteiraItem = selectedCarteiraItem;
	}

	public Double getSelectedCarteiraItemTotalValue() {
		return selectedCarteiraItemTotalValue;
	}

	public void setSelectedCarteiraItemTotalValue(
			Double selectedCarteiraItemTotalValue) {
		this.selectedCarteiraItemTotalValue = selectedCarteiraItemTotalValue;
	}

	public String getSelectedPoint() {
		return selectedPoint;
	}

	public void setSelectedPoint(String selectedPoint) {
		this.selectedPoint = selectedPoint;
	}

}