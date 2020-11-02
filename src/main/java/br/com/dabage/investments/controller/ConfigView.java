package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.company.CompanyService;
import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.config.ConfigService;
import br.com.dabage.investments.config.ConfigTO;
import br.com.dabage.investments.config.StockTypeTO;
import br.com.dabage.investments.home.HomeService;
import br.com.dabage.investments.news.CheckNewsB3;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.ConfigRepository;
import br.com.dabage.investments.repositories.RoleRepository;
import br.com.dabage.investments.repositories.StockTypeRepository;
import br.com.dabage.investments.user.RoleTO;
import br.com.dabage.investments.user.UserTO;

@Controller(value="configView")
@RequestScoped
public class ConfigView extends BasicView implements Serializable {

	/** */
	private static final long serialVersionUID = -2524943863550149439L;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	GetQuotation getQuotation;

	@Autowired
	StockTypeRepository stockTypeRepository;

	@Autowired
	MongoTemplate template;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	HomeService homeService;

	@Autowired
	ConfigService configService;

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	CompanyService companyService;

	@Autowired
	public CheckNewsB3 checkNewsB3;

	private List<StockTypeTO> stockTypes = new ArrayList<StockTypeTO>();

	private StockTypeTO stockType;

	private UserTO user;

	private List<UserTO> users = new ArrayList<UserTO>();

	private DualListModel<RoleTO> roles;

	private List<CompanyTO> companies;

	private List<ConfigTO> parameters;

	private String oldTicker;

	private String newTicker;

	private Date newsDate;

	private CompanyTO company;
	
	public String init() {
		// Stock Types
		stockTypes = stockTypeRepository.findByRemoveDateNull();

		// All Users
		users = userRepository.findAll();

		// All Companies
		companies = companyRepository.findAll();

		// All Parameters
		parameters = configRepository.findAll();

		//
		oldTicker = "";
		newTicker = "";

		company = new CompanyTO();

		prepareRoles();
        return "config";
    }

	/**
	 * Prepare to add a new StockType
	 * @param event
	 */
	public void prepareStockType(ActionEvent event) {
		stockType = new StockTypeTO();
		stockType.setAddDate(new Date());
	}

	/**
	 * Add a new StockType (Ie: FII, Stock, Stock Option, etc...)
	 * @param event
	 */
	public void addStockType(ActionEvent event) {
		if (!checkStockType(stockType)) return;
		stockTypeRepository.save(stockType);
		stockTypes = stockTypeRepository.findByRemoveDateNull();
	}

	/**
	 * Edit a StockType
	 * @param event
	 */
	public void editStockType(RowEditEvent event) {
		StockTypeTO toEdit = (StockTypeTO) event.getObject();
		if (!checkStockType(toEdit)) return;
		stockTypeRepository.save(toEdit);
	}

	public void deleteStockType() {
		stockType.setRemoveDate(new Date());
		stockTypeRepository.save(stockType);
		stockTypes = stockTypeRepository.findByRemoveDateNull();
	}

	/**
	 * Validates a StockType
	 * @param stock
	 * @return
	 */
	private boolean checkStockType(StockTypeTO stock) {
		if (stock == null) {
			addWarnMessage("Valores invalidos.");
			return false;
		}

		if (stock.getName() == null || stock.getName().isEmpty()) {
			addWarnMessage("Informe um nome.");
			return false;
		}
		stock.setName(stock.getName().toUpperCase());

		if (stock.getIrNormal() == null) {
			addWarnMessage("Informe a % de IR Normal.");
			return false;
		}

		if (stock.getIrDayTrade() == null) {
			addWarnMessage("Informe a % de IR Daytrade.");
			return false;
		}

		return true;
	}

	/** Prepare Roles to add */
	private void prepareRoles() {
        // Roles
        List<RoleTO> rolesSource = roleRepository.findAll();
        List<RoleTO> rolesTarget = new ArrayList<RoleTO>();
        roles = new DualListModel<RoleTO>(rolesSource, rolesTarget);		
	}

	/**
	 * Prepare to add a new User
	 * @param event
	 */
	public void prepareUser(ActionEvent event) {
		prepareRoles();

		user = new UserTO();
		user.setAddDate(new Date());
		user.setActivated(Boolean.TRUE);
		user.setActivateDate(new Date());
	}

	
	/**
	 * Prepare to edit a User
	 * @param event
	 */
	public void prepareEditUser(ActionEvent event) {
		prepareRoles();
		roles.getSource().removeAll(user.getRoles());
		roles.getTarget().addAll(user.getRoles());
		RequestContext rc = RequestContext.getCurrentInstance();
	    rc.execute("PF('addUserDlg').show()");
	}

	/**
	 * Add a new User
	 * @param event
	 */
	public void addUser(ActionEvent event) {
		if (!checkUser(user)) return;
		Object[] array = roles.getTarget().toArray();
		List<RoleTO> rolesSelected = new ArrayList<RoleTO>();
		for (int i = 0; i < array.length; i++) {
			RoleTO role = roleRepository.findById(new BigInteger((String)array[i])).get();
			rolesSelected.add(role);
		}
		user.setRoles(rolesSelected);
		userRepository.save(user);
		users = userRepository.findAll();
		RequestContext rc = RequestContext.getCurrentInstance();
	    rc.execute("PF('addUserDlg').hide()");
	}

	/**
	 * Edit a User
	 * @param event
	 */
	public void editUser(ActionEvent event) {
		if (!checkUser(user)) return;
		user.setRoles(roles.getTarget());
		userRepository.save(user);
	}

	public void deleteUser() {
		user.setRemoveDate(new Date());
		user.setActivated(Boolean.FALSE);
		userRepository.save(user);
		users = userRepository.findAll();
	}

	public void activateUser() {
		user.setRemoveDate(null);
		user.setActivated(Boolean.TRUE);
		userRepository.save(user);
		users = userRepository.findAll();
	}

	public void calcPortfolioItems() {
		configService.calcPortfolioItem();
	}
	
	/**
	 * Validates a new User
	 * @param user
	 * @return
	 */
	private boolean checkUser(UserTO user) {
		if (user == null) {
			addWarnMessage("Valores invalidos.");
			return false;
		}

		if (user.getName() == null || user.getName().isEmpty()) {
			addWarnMessage("Informe um nome.");
			return false;
		}
		user.setName(user.getName().toUpperCase());

		if (user.getSurname() == null || user.getSurname().isEmpty()) {
			addWarnMessage("Informe um sobrenome.");
			return false;
		}

		if (user.getEmail() == null || user.getEmail().isEmpty()) {
			addWarnMessage("Informe um email valido.");
			return false;
		}

		if (user.getPassword() == null || user.getPassword().isEmpty()) {
			addWarnMessage("Informe uma senha valida.");
			return false;
		}

		if (user.getRetypePassword() == null || user.getRetypePassword().isEmpty()) {
			addWarnMessage("Informe novamente a senha.");
			return false;
		}

		if (!user.getPassword().equals(user.getRetypePassword())) {
			addWarnMessage("As senhas informadas nao conferem.");
			return false;
		}
		
		if (roles.getTarget().isEmpty()) {
			addWarnMessage("Adicione um perfil de acesso ao novo usuario.");
			return false;
		}
		
		return true;
	}

	/**
	 * Refresh Quote and Home cache
	 */
	public void refreshQuoteCache() {
		getQuotation.cleanQuoteCache();
		homeService.loadHomeCache();
	}

	/**
	 * Save the Company edition
	 * @param event
	 */
	public void editCompany(RowEditEvent event) {

		CompanyTO company = (CompanyTO) event.getObject();

		companyRepository.save(company);
	}

	/**
	 * Save the Config/Parameter edition
	 * @param event
	 */
	public void editParameter(RowEditEvent event) {

		ConfigTO parameter = (ConfigTO) event.getObject();

		configRepository.save(parameter);
	}

	/**
	 * Change Negotiation Code
	 * @param event
	 */
	public void changeNegotiationCode(ActionEvent event) {
		if (oldTicker.isEmpty()) {
			addWarnMessage("Infome o Codigo Antigo da Acao/Fundo.");
			return;
		}

		if (newTicker.isEmpty()) {
			addWarnMessage("Infome o Codigo Antigo da Acao/Fundo.");
			return;
		}

		companyService.changeTicker(oldTicker, newTicker);

		oldTicker = "";
		newTicker = "";

		addInfoMessage("Codigo alterado com sucesso.");
	};

	/**
	 * Add Negotiation Code
	 * @param event
	 */
	public void addNegotiationCode(ActionEvent event) {
		if (company.getTicker() == null || company.getTicker().isEmpty()) {
			addWarnMessage("Infome o Codigo do Fundo.");
			return;
		}

		if (company.getName() == null || company.getName().isEmpty()) {
			addWarnMessage("Infome o Nome do Fundo.");
			return;
		}

		if (company.getFullName() == null || company.getFullName().isEmpty()) {
			addWarnMessage("Infome o Nome completo do Fundo.");
			return;
		}

		if (company.getSetor() == null || company.getSetor().isEmpty()) {
			addWarnMessage("Infome o Setor do Fundo.");
			return;
		}

		String category = "FII";
		StockTypeTO stockType = stockTypeRepository.findByName(category);
		company.setStockType(stockType);
		company.setCategory(category);
		company.setTicker(company.getTicker().trim().toUpperCase());
		company.setName(company.getName().trim().toUpperCase());
		company.setFullName(company.getFullName().trim().toUpperCase());
		company.setSetor(company.getSetor().trim());

		// save object
		companyRepository.save(company);

		//add sucess message
		addInfoMessage(String.format("Codigo %s inserido com sucesso.", company.getTicker()));

		// reset object
		company = new CompanyTO();
	}

	/**
	 * Update news from B3
	 * @param event
	 */
	public void runNews(ActionEvent event) {
		if (newsDate == null) {
			addWarnMessage("Infome a Data.");
			return;
		}

		DateFormat dateFormatSearch = new SimpleDateFormat("yyyy-MM-dd");
		String query = "fii";
		String date = dateFormatSearch.format(newsDate);
		int qtyNews = checkNewsB3.run(query, date, date);

		newsDate = null;

		addInfoMessage("Noticias atualizado com sucesso. Quantidade: " + qtyNews);
	}

	public List<StockTypeTO> getStockTypes() {
		return stockTypes;
	}

	public void setStockTypes(List<StockTypeTO> stockTypes) {
		this.stockTypes = stockTypes;
	}

	public StockTypeTO getStockType() {
		return stockType;
	}

	public void setStockType(StockTypeTO stockType) {
		this.stockType = stockType;
	}

	public UserTO getUser() {
		return user;
	}

	public void setUser(UserTO user) {
		this.user = user;
	}

	public List<UserTO> getUsers() {
		return users;
	}

	public void setUsers(List<UserTO> users) {
		this.users = users;
	}

	public DualListModel<RoleTO> getRoles() {
		return roles;
	}

	public void setRoles(DualListModel<RoleTO> roles) {
		this.roles = roles;
	}

	public List<CompanyTO> getCompanies() {
		return companies;
	}

	public void setCompanies(List<CompanyTO> companies) {
		this.companies = companies;
	}

	public List<ConfigTO> getParameters() {
		return parameters;
	}

	public void setParameters(List<ConfigTO> parameters) {
		this.parameters = parameters;
	}

	public String getOldTicker() {
		return oldTicker;
	}

	public void setOldTicker(String oldTicker) {
		this.oldTicker = oldTicker;
	}

	public String getNewTicker() {
		return newTicker;
	}

	public void setNewTicker(String newTicker) {
		this.newTicker = newTicker;
	}

	public Date getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(Date newsDate) {
		this.newsDate = newsDate;
	}

	public CompanyTO getCompany() {
		return company;
	}

	public void setCompany(CompanyTO company) {
		this.company = company;
	}

}