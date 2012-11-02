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
@Table(name = "relation_variables", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RelationVariables.findAll", query = "SELECT r FROM RelationVariables r"),
    @NamedQuery(name = "RelationVariables.findByIdRelationVariables", query = "SELECT r FROM RelationVariables r WHERE r.idRelationVariables = :idRelationVariables"),
    @NamedQuery(name = "RelationVariables.findByNameExpected", query = "SELECT r FROM RelationVariables r WHERE r.nameExpected = :nameExpected"),
    @NamedQuery(name = "RelationVariables.findByNameFound", query = "SELECT r FROM RelationVariables r WHERE r.nameFound = :nameFound"),
    @NamedQuery(name = "RelationVariables.findByDateFormat", query = "SELECT r FROM RelationVariables r WHERE r.dateFormat = :dateFormat"),
    @NamedQuery(name = "RelationVariables.findByFieldType", query = "SELECT r FROM RelationVariables r WHERE r.fieldType = :fieldType"),
    @NamedQuery(name = "RelationVariables.findByComparisonForCode", query = "SELECT r FROM RelationVariables r WHERE r.comparisonForCode = :comparisonForCode")})
public class RelationVariables implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_relation_variables", nullable = false)
    private Integer idRelationVariables;
    @Size(max = 2147483647)
    @Column(name = "name_expected", length = 2147483647)
    private String nameExpected;
    @Size(max = 2147483647)
    @Column(name = "name_found", length = 2147483647)
    private String nameFound;
    @Size(max = 2147483647)
    @Column(name = "date_format", length = 2147483647)
    private String dateFormat;
    @Size(max = 2147483647)
    @Column(name = "field_type", length = 2147483647)
    private String fieldType;
    @Column(name = "comparison_for_code")
    private Boolean comparisonForCode;
    @JoinColumn(name = "id_relation_group", referencedColumnName = "id_relation_group")
    @ManyToOne
    private RelationGroup idRelationGroup;
    @OneToMany(mappedBy = "idRelationVariables")
    private List<RelationValues> relationValuesList;
    @OneToMany(mappedBy = "idRelationVariables")
    private List<RelationsDiscardedValues> relationsDiscardedValuesList;

    public RelationVariables() {
    }

    public RelationVariables(Integer idRelationVariables) {
	this.idRelationVariables = idRelationVariables;
    }

    public Integer getIdRelationVariables() {
	return idRelationVariables;
    }

    public void setIdRelationVariables(Integer idRelationVariables) {
	this.idRelationVariables = idRelationVariables;
    }

    public String getNameExpected() {
	return nameExpected;
    }

    public void setNameExpected(String nameExpected) {
	this.nameExpected = nameExpected;
    }

    public String getNameFound() {
	return nameFound;
    }

    public void setNameFound(String nameFound) {
	this.nameFound = nameFound;
    }

    public String getDateFormat() {
	return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
	this.dateFormat = dateFormat;
    }

    public String getFieldType() {
	return fieldType;
    }

    public void setFieldType(String fieldType) {
	this.fieldType = fieldType;
    }

    public Boolean getComparisonForCode() {
	return comparisonForCode;
    }

    public void setComparisonForCode(Boolean comparisonForCode) {
	this.comparisonForCode = comparisonForCode;
    }

    public RelationGroup getIdRelationGroup() {
	return idRelationGroup;
    }

    public void setIdRelationGroup(RelationGroup idRelationGroup) {
	this.idRelationGroup = idRelationGroup;
    }

    @XmlTransient
    public List<RelationsDiscardedValues> getRelationsDiscardedValuesList() {
        return relationsDiscardedValuesList;
    }

    public void setRelationsDiscardedValuesList(List<RelationsDiscardedValues> relationsDiscardedValuesList) {
        this.relationsDiscardedValuesList = relationsDiscardedValuesList;
    }
    
    @XmlTransient
    public List<RelationValues> getRelationValuesList() {
	return relationValuesList;
    }

    public void setRelationValuesList(List<RelationValues> relationValuesList) {
	this.relationValuesList = relationValuesList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (idRelationVariables != null ? idRelationVariables.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof RelationVariables)) {
	    return false;
	}
	RelationVariables other = (RelationVariables) object;
	if ((this.idRelationVariables == null && other.idRelationVariables != null) || (this.idRelationVariables != null && !this.idRelationVariables.equals(other.idRelationVariables))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.RelationVariables[ idRelationVariables=" + idRelationVariables + " ]";
    }
    
}
