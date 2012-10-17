/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "users", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByUserId", query = "SELECT u FROM Users u WHERE u.userId = :userId"),
    @NamedQuery(name = "Users.findByUserName", query = "SELECT u FROM Users u WHERE u.userName = :userName"),
    @NamedQuery(name = "Users.findByUserJob", query = "SELECT u FROM Users u WHERE u.userJob = :userJob"),
    @NamedQuery(name = "Users.findByUserInstitution", query = "SELECT u FROM Users u WHERE u.userInstitution = :userInstitution"),
    @NamedQuery(name = "Users.findByUserTelephone", query = "SELECT u FROM Users u WHERE u.userTelephone = :userTelephone"),
    @NamedQuery(name = "Users.findByUserEmail", query = "SELECT u FROM Users u WHERE u.userEmail = :userEmail"),
    @NamedQuery(name = "Users.findByUserPassword", query = "SELECT u FROM Users u WHERE u.userPassword = :userPassword"),
    @NamedQuery(name = "Users.findByUserAddress", query = "SELECT u FROM Users u WHERE u.userAddress = :userAddress")})
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "user_name", nullable = false, length = 2147483647)
    private String userName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "user_job", nullable = false, length = 100)
    private String userJob;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "user_institution", nullable = false, length = 2147483647)
    private String userInstitution;
    @Size(max = 25)
    @Column(name = "user_telephone", length = 25)
    private String userTelephone;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "user_email", nullable = false, length = 2147483647)
    private String userEmail;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "user_password", nullable = false, length = 2147483647)
    private String userPassword;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "user_address", nullable = false, length = 2147483647)
    private String userAddress;
    @OneToMany(mappedBy = "userId")
    private List<NonFatalInjuries> nonFatalInjuriesList;
    @OneToMany(mappedBy = "userId")
    private List<FatalInjuries> fatalInjuriesList;
    @Size(max = 2147483647)
    @Column(name = "user_login", length = 2147483647)
    private String userLogin;

    public Users() {
    }

    public Users(Integer userId) {
	this.userId = userId;
    }

    public Users(Integer userId, String userName, String userJob, String userInstitution, String userEmail, String userPassword, String userAddress) {
	this.userId = userId;
	this.userName = userName;
	this.userJob = userJob;
	this.userInstitution = userInstitution;
	this.userEmail = userEmail;
	this.userPassword = userPassword;
	this.userAddress = userAddress;
    }

    public Integer getUserId() {
	return userId;
    }

    public void setUserId(Integer userId) {
	this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserJob() {
	return userJob;
    }

    public void setUserJob(String userJob) {
	this.userJob = userJob;
    }

    public String getUserInstitution() {
	return userInstitution;
    }

    public void setUserInstitution(String userInstitution) {
	this.userInstitution = userInstitution;
    }

    public String getUserTelephone() {
	return userTelephone;
    }

    public void setUserTelephone(String userTelephone) {
	this.userTelephone = userTelephone;
    }

    public String getUserEmail() {
	return userEmail;
    }

    public void setUserEmail(String userEmail) {
	this.userEmail = userEmail;
    }

    public String getUserPassword() {
	return userPassword;
    }

    public void setUserPassword(String userPassword) {
	this.userPassword = userPassword;
    }

    public String getUserAddress() {
	return userAddress;
    }

    public void setUserAddress(String userAddress) {
	this.userAddress = userAddress;
    }
    
    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
	return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
	this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @XmlTransient
    public List<FatalInjuries> getFatalInjuriesList() {
	return fatalInjuriesList;
    }

    public void setFatalInjuriesList(List<FatalInjuries> fatalInjuriesList) {
	this.fatalInjuriesList = fatalInjuriesList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (userId != null ? userId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Users)) {
	    return false;
	}
	Users other = (Users) object;
	if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Users[ userId=" + userId + " ]";
    }
    
}
