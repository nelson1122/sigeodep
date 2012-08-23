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
@Table(name = "boolean", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BooleanPojo.findAll", query = "SELECT b FROM BooleanPojo b"),
    @NamedQuery(name = "BooleanPojo.findByBooleanId", query = "SELECT b FROM BooleanPojo b WHERE b.booleanId = :booleanId"),
    @NamedQuery(name = "BooleanPojo.findByBooleanName", query = "SELECT b FROM BooleanPojo b WHERE b.booleanName = :booleanName")})
public class BooleanPojo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "boolean_id", nullable = false)
    private Short booleanId;
    @Size(max = 2147483647)
    @Column(name = "boolean_name", length = 2147483647)
    private String booleanName;
    @OneToMany(mappedBy = "previousAttempt")
    private List<NonFatalSelfInflicted> nonFatalSelfInflictedList;
    @OneToMany(mappedBy = "mentalAntecedent")
    private List<NonFatalSelfInflicted> nonFatalSelfInflictedList1;
    @OneToMany(mappedBy = "previousAttempt")
    private List<FatalInjurySuicide> fatalInjurySuicideList;
    @OneToMany(mappedBy = "mentalAntecedent")
    private List<FatalInjurySuicide> fatalInjurySuicideList1;
    @OneToMany(mappedBy = "previousAntecedent")
    private List<NonFatalInterpersonal> nonFatalInterpersonalList;
    

    public BooleanPojo() {
    }

    public BooleanPojo(Short booleanId) {
	this.booleanId = booleanId;
    }

    public Short getBooleanId() {
	return booleanId;
    }

    public void setBooleanId(Short booleanId) {
	this.booleanId = booleanId;
    }

    public String getBooleanName() {
	return booleanName;
    }

    public void setBooleanName(String booleanName) {
	this.booleanName = booleanName;
    }

    @XmlTransient
    public List<NonFatalSelfInflicted> getNonFatalSelfInflictedList() {
	return nonFatalSelfInflictedList;
    }

    public void setNonFatalSelfInflictedList(List<NonFatalSelfInflicted> nonFatalSelfInflictedList) {
	this.nonFatalSelfInflictedList = nonFatalSelfInflictedList;
    }

    @XmlTransient
    public List<NonFatalSelfInflicted> getNonFatalSelfInflictedList1() {
	return nonFatalSelfInflictedList1;
    }

    public void setNonFatalSelfInflictedList1(List<NonFatalSelfInflicted> nonFatalSelfInflictedList1) {
	this.nonFatalSelfInflictedList1 = nonFatalSelfInflictedList1;
    }

    @XmlTransient
    public List<FatalInjurySuicide> getFatalInjurySuicideList() {
	return fatalInjurySuicideList;
    }

    public void setFatalInjurySuicideList(List<FatalInjurySuicide> fatalInjurySuicideList) {
	this.fatalInjurySuicideList = fatalInjurySuicideList;
    }

    @XmlTransient
    public List<FatalInjurySuicide> getFatalInjurySuicideList1() {
	return fatalInjurySuicideList1;
    }

    public void setFatalInjurySuicideList1(List<FatalInjurySuicide> fatalInjurySuicideList1) {
	this.fatalInjurySuicideList1 = fatalInjurySuicideList1;
    }
    
    @XmlTransient
    public List<NonFatalInterpersonal> getNonFatalInterpersonalList() {
        return nonFatalInterpersonalList;
    }

    public void setNonFatalInterpersonalList(List<NonFatalInterpersonal> nonFatalInterpersonalList) {
        this.nonFatalInterpersonalList = nonFatalInterpersonalList;
    }
    
    @Override
    public int hashCode() {
	int hash = 0;
	hash += (booleanId != null ? booleanId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof BooleanPojo)) {
	    return false;
	}
	BooleanPojo other = (BooleanPojo) object;
	if ((this.booleanId == null && other.booleanId != null) || (this.booleanId != null && !this.booleanId.equals(other.booleanId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.BooleanPojo[ booleanId=" + booleanId + " ]";
    }
    
}