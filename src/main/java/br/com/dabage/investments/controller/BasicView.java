package br.com.dabage.investments.controller;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.dabage.investments.repositories.UserRepository;
import br.com.dabage.investments.user.UserTO;

public class BasicView implements Serializable {

	@Resource
	UserRepository userRepository;
	
	/** */
	private static final long serialVersionUID = 8359936032778514688L;

	public void addInfoMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public void addWarnMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public void addErrorMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public String getUserName() {
		UserTO user = getUserLoggedIn();
		return user.getName() + " " + user.getSurname();
	}

	public UserTO getUserLoggedIn() {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(userDetails.getUsername());
	}

	public String getMessage(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context == null) return "";
		Locale locale = context.getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle bundle = ResourceBundle.getBundle("messages", locale, loader);
		String msg = bundle.getString(key);

		return msg;
	}
}
