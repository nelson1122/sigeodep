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
 * @author santos
 */
@Entity
@Table(name = "domestic_violence_data_sources", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"domestic_violence_data_sources_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DomesticViolenceDataSources.findAll", query = "SELECT d FROM DomesticViolenceDataSources d"),
    @NamedQuery(name = "DomesticViolenceDataSources.findByDomesticViolenceDataSourcesId", query = "SELECT d FROM DomesticViolenceDataSources d WHERE d.domesticViolenceDataSourcesId = :domesticViolenceDataSourcesId"),
    @NamedQuery(name = "DomesticViolenceDataSources.findByDomesticViolenceDataSourcesName", query = "SELECT d FROM DomesticViolenceDataSources d WHERE d.domesticViolenceDataSourcesName = :domesticViolenceDataSourcesName")})
public class DomesticViolenceDataSources implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "domestic_violence_data_sources_id", nullable = false)
    private Short domesticViolenceDataSourcesId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "domestic_violence_data_sources_name", nullable = false, length = 50)
    private String domesticViolenceDataSourcesName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "domesticViolenceDataSourceId")
    private List<NonFatalDomesticViolence> nonFatalDomesticViolenceList;

    public DomesticViolenceDataSources() {
    }

    public DomesticViolenceDataSources(Short domesticViolenceDataSourcesId) {
        this.domesticViolenceDataSourcesId = domesticViolenceDataSourcesId;
    }

    public DomesticViolenceDataSources(Short domesticViolenceDataSourcesId, String domesticViolenceDataSourcesName) {
        this.domesticViolenceDataSourcesId = domesticViolenceDataSourcesId;
        this.domesticViolenceDataSourcesName = domesticViolenceDataSourcesName;
    }

    public Short getDomesticViolenceDataSourcesId() {
        return domesticViolenceDataSourcesId;
    }

    public void setDomesticViolenceDataSourcesId(Short domesticViolenceDataSourcesId) {
        this.domesticViolenceDataSourcesId = domesticViolenceDataSourcesId;
    }

    public String getDomesticViolenceDataSourcesName() {
        return domesticViolenceDataSourcesName;
    }

    public void setDomesticViolenceDataSourcesName(String domesticViolenceDataSourcesName) {
        this.domesticViolenceDataSourcesName = domesticViolenceDataSourcesName;
    }

    @XmlTransient
    public List<NonFatalDomesticViolence> getNonFatalDomesticViolenceList() {
        return nonFatalDomesticViolenceList;
    }

    public void setNonFatalDomesticViolenceList(List<NonFatalDomesticViolence> nonFatalDomesticViolenceList) {
        this.nonFatalDomesticViolenceList = nonFatalDomesticViolenceList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (domesticViolenceDataSourcesId != null ? domesticViolenceDataSourcesId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DomesticViolenceDataSources)) {
            return false;
        }
        DomesticViolenceDataSources other = (DomesticViolenceDataSources) object;
        if ((this.domesticViolenceDataSourcesId == null && other.domesticViolenceDataSourcesId != null) || (this.domesticViolenceDataSourcesId != null && !this.domesticViolenceDataSourcesId.equals(other.domesticViolenceDataSourcesId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.DomesticViolenceDataSources[ domesticViolenceDataSourcesId=" + domesticViolenceDataSourcesId + " ]";
    }
    
}
