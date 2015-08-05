package br.com.dabage.investments.controller;


import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.springframework.stereotype.Controller;

@Controller(value="templateView")
@SessionScoped
public class TemplateView extends BasicView implements Serializable {

	/** */
	private static final long serialVersionUID = -4917105272020943642L;
	private String menuItemSelected;

	@PostConstruct
	public void init() {
		menuItemSelected = "graph";
	}

	/**
	 * Apresentar detalhes de uma carteira
	 */
	public void selectMenuItem(AjaxBehaviorEvent event) {
		menuItemSelected = (String) event.getComponent().getAttributes().get("action");
	}

	public String getMenuItemSelected() {
		return menuItemSelected;
	}

	public void setMenuItemSelected(String menuItemSelected) {
		this.menuItemSelected = menuItemSelected;
	}

}