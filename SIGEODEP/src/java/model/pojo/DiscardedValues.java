/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "discarded_values", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DiscardedValues.findAll", query = "SELECT d FROM DiscardedValues d"),
    @NamedQuery(name = "DiscardedValues.findByIdDiscardedValues", query = "SELECT d FROM DiscardedValues d WHERE d.idDiscardedValues = :idDiscardedValues"),
    @NamedQuery(name = "DiscardedValues.findByIdRelationVariables", query = "SELECT d FROM DiscardedValues d WHERE d.idRelationVariables = :idRelationVariables"),
    @NamedQuery(name = "DiscardedValues.findByValue", query = "SELECT d FROM DiscardedValues d WHERE d.value = :value")})
public class DiscardedValues implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_discarded_values", nullable = false)
    private Integer idDiscardedValues;
    @Column(name = "id_relation_variables")
    private Integer idRelationVariables;
    @Size(max = 200)
    @Column(name = "value", length = 200)
    private String value;

    public DiscardedValues() {
    }

    public DiscardedValues(Integer idDiscardedValues) {
	this.idDiscardedValues = idDiscardedValues;
    }

    public Integer getIdDiscardedValues() {
	return idDiscardedValues;
    }

    public void setIdDiscardedValues(Integer idDiscardedValues) {
	this.idDiscardedValues = idDiscardedValues;
    }

    public Integer getIdRelationVariables() {
	return idRelationVariables;
    }

    public void setIdRelationVariables(Integer idRelationVariables) {
	this.idRelationVariables = idRelationVariables;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (idDiscardedValues != null ? idDiscardedValues.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof DiscardedValues)) {
	    return false;
	}
	DiscardedValues other = (DiscardedValues) object;
	if ((this.idDiscardedValues == null && other.idDiscardedValues != null) || (this.idDiscardedValues != null && !this.idDiscardedValues.equals(other.idDiscardedValues))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.DiscardedValues[ idDiscardedValues=" + idDiscardedValues + " ]";
    }
    
}
