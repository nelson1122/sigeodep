/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "non_fatal_domestic_violence", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NonFatalDomesticViolence.findAll", query = "SELECT n FROM NonFatalDomesticViolence n"),
    @NamedQuery(name = "NonFatalDomesticViolence.findByNonFatalInjuryId", query = "SELECT n FROM NonFatalDomesticViolence n WHERE n.nonFatalInjuryId = :nonFatalInjuryId")})
public class NonFatalDomesticViolence implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_injury_id", nullable = false)
    private Integer nonFatalInjuryId;
    @ManyToMany(mappedBy = "nonFatalDomesticViolenceList")
    private List<AggressorTypes> aggressorTypesList;
    @ManyToMany(mappedBy = "nonFatalDomesticViolenceList")
    private List<ActionsToTake> actionsToTakeList;
    @ManyToMany(mappedBy = "nonFatalDomesticViolenceList")
    private List<AbuseTypes> abuseTypesList;
    @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private NonFatalInjuries nonFatalInjuries;
    @JoinColumn(name = "domestic_violence_data_source_id", referencedColumnName = "domestic_violence_data_sources_id", nullable = false)
    @ManyToOne(optional = false)
    private DomesticViolenceDataSources domesticViolenceDataSourceId;

    public NonFatalDomesticViolence() {
    }

    public NonFatalDomesticViolence(Integer nonFatalInjuryId) {
        this.nonFatalInjuryId = nonFatalInjuryId;
    }

    public Integer getNonFatalInjuryId() {
        return nonFatalInjuryId;
    }

    public void setNonFatalInjuryId(Integer nonFatalInjuryId) {
        this.nonFatalInjuryId = nonFatalInjuryId;
    }

    @XmlTransient
    public List<AggressorTypes> getAggressorTypesList() {
        return aggressorTypesList;
    }

    public void setAggressorTypesList(List<AggressorTypes> aggressorTypesList) {
        this.aggressorTypesList = aggressorTypesList;
    }

    @XmlTransient
    public List<ActionsToTake> getActionsToTakeList() {
        return actionsToTakeList;
    }

    public void setActionsToTakeList(List<ActionsToTake> actionsToTakeList) {
        this.actionsToTakeList = actionsToTakeList;
    }

    @XmlTransient
    public List<AbuseTypes> getAbuseTypesList() {
        return abuseTypesList;
    }

    public void setAbuseTypesList(List<AbuseTypes> abuseTypesList) {
        this.abuseTypesList = abuseTypesList;
    }

    public NonFatalInjuries getNonFatalInjuries() {
        return nonFatalInjuries;
    }

    public void setNonFatalInjuries(NonFatalInjuries nonFatalInjuries) {
        this.nonFatalInjuries = nonFatalInjuries;
    }

    public DomesticViolenceDataSources getDomesticViolenceDataSourceId() {
        return domesticViolenceDataSourceId;
    }

    public void setDomesticViolenceDataSourceId(DomesticViolenceDataSources domesticViolenceDataSourceId) {
        this.domesticViolenceDataSourceId = domesticViolenceDataSourceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nonFatalInjuryId != null ? nonFatalInjuryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NonFatalDomesticViolence)) {
            return false;
        }
        NonFatalDomesticViolence other = (NonFatalDomesticViolence) object;
        if ((this.nonFatalInjuryId == null && other.nonFatalInjuryId != null) || (this.nonFatalInjuryId != null && !this.nonFatalInjuryId.equals(other.nonFatalInjuryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.NonFatalDomesticViolence[ nonFatalInjuryId=" + nonFatalInjuryId + " ]";
    }
    
}
