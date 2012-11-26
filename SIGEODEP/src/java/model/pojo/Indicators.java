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
@Table(name = "indicators", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Indicators.findAll", query = "SELECT i FROM Indicators i"),
    @NamedQuery(name = "Indicators.findByIndicatorId", query = "SELECT i FROM Indicators i WHERE i.indicatorId = :indicatorId"),
    @NamedQuery(name = "Indicators.findByIndicatorName", query = "SELECT i FROM Indicators i WHERE i.indicatorName = :indicatorName"),
    @NamedQuery(name = "Indicators.findByIndicatorGroup", query = "SELECT i FROM Indicators i WHERE i.indicatorGroup = :indicatorGroup"),
    @NamedQuery(name = "Indicators.findByGraphType", query = "SELECT i FROM Indicators i WHERE i.graphType = :graphType")})
public class Indicators implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "indicator_id", nullable = false)
    private Integer indicatorId;
    @Size(max = 2147483647)
    @Column(name = "indicator_name", length = 2147483647)
    private String indicatorName;
    @Size(max = 2147483647)
    @Column(name = "indicator_group", length = 2147483647)
    private String indicatorGroup;
    @Size(max = 2147483647)
    @Column(name = "graph_type", length = 2147483647)
    private String graphType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "indicators")
    private List<IndicatorsVariables> indicatorsVariablesList;

    public Indicators() {
    }

    public Indicators(Integer indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Integer getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Integer indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getIndicatorGroup() {
        return indicatorGroup;
    }

    public void setIndicatorGroup(String indicatorGroup) {
        this.indicatorGroup = indicatorGroup;
    }

    public String getGraphType() {
        return graphType;
    }

    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

    @XmlTransient
    public List<IndicatorsVariables> getIndicatorsVariablesList() {
        return indicatorsVariablesList;
    }

    public void setIndicatorsVariablesList(List<IndicatorsVariables> indicatorsVariablesList) {
        this.indicatorsVariablesList = indicatorsVariablesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (indicatorId != null ? indicatorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Indicators)) {
            return false;
        }
        Indicators other = (Indicators) object;
        if ((this.indicatorId == null && other.indicatorId != null) || (this.indicatorId != null && !this.indicatorId.equals(other.indicatorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Indicators[ indicatorId=" + indicatorId + " ]";
    }
    
}