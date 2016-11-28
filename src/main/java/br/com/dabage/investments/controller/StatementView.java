package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.carteira.PortfolioService;
import br.com.dabage.investments.carteira.StatementFilter;
import br.com.dabage.investments.carteira.StatementType;
import br.com.dabage.investments.carteira.StatementVO;

@Controller(value="statementView")
@RequestScoped
public class StatementView extends BasicView implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	@Autowired
	PortfolioService portfolioService;

	/**
	 * Filters
	 */
	private StatementFilter filter;
	
	private List<SelectItem> types;

	List<StatementVO> statements;

	@PostConstruct
	public void prepare() {
		statements = new ArrayList<StatementVO>();
		clearFilter(null);
	}

	public void clearFilter(ActionEvent event) {
		filter = new StatementFilter();
		filter.setType(StatementType.Negotiation);

		types = new ArrayList<SelectItem>();
		SelectItem neg = new SelectItem(StatementType.Negotiation, super.getMessage("app.statement.lbl.filterType.negotiation"));
		types.add(neg);
		SelectItem inc = new SelectItem(StatementType.Income, super.getMessage("app.statement.lbl.filterType.income"));
		types.add(inc);
	}

	public void apply(ActionEvent event) {
		statements = portfolioService.findStatementByFilter(filter);
	}

	public List<StatementVO> getStatements() {
		return statements;
	}

	public void setStatements(List<StatementVO> statements) {
		this.statements = statements;
	}

	public List<SelectItem> getTypes() {
		return types;
	}

	public void setTypes(List<SelectItem> types) {
		this.types = types;
	}

	public StatementFilter getFilter() {
		return filter;
	}

	public void setFilter(StatementFilter filter) {
		this.filter = filter;
	}

}