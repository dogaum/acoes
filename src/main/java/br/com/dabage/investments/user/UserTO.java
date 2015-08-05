package br.com.dabage.investments.user;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.repositories.AbstractDocument;


@Document(collection="user")
public class UserTO extends AbstractDocument {
	
	private String name;
	private String surname;
	private String age;
	
	@Indexed
	private String username;
	private String password;

	@Transient
	private String retypePassword;

	@Indexed
	private String email;

	@DBRef
	private List<RoleTO> roles;

	/** Creation date */
	private Date addDate;

	/** Deleted date */
	private Date removeDate;

	/** IF it was actvated .*/
	private Boolean activated;

	/** Date of activation */
	private Date activateDate;

	public UserTO() {
		super();		
	}

	public UserTO(String name, String surname, String age, String username,
			String password, String email, List<RoleTO> roles) {
		super();
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.username = username;
		this.password = password;
		this.email = email;
		this.roles = roles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRetypePassword() {
		return retypePassword;
	}

	public void setRetypePassword(String retypePassword) {
		this.retypePassword = retypePassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<RoleTO> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleTO> roles) {
		this.roles = roles;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getRemoveDate() {
		return removeDate;
	}

	public void setRemoveDate(Date removeDate) {
		this.removeDate = removeDate;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public Date getActivateDate() {
		return activateDate;
	}

	public void setActivateDate(Date activateDate) {
		this.activateDate = activateDate;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", surname=" + surname + ", age=" + age
				+ ", username=" + username + ", password=" + password + ", email=" + email
				+ ", roles=" + roles + "]";
	}

}
