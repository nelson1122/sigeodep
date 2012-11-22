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
@Table(name = "variables", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Variables.findAll", query = "SELECT v FROM Variables v"),
    @NamedQuery(name = "Variables.findByVariableId", query = "SELECT v FROM Variables v WHERE v.variableId = :variableId"),
    @NamedQuery(name = "Variables.findByVariableName", query = "SELECT v FROM Variables v WHERE v.variableName = :variableName"),
    @NamedQuery(name = "Variables.findByDisaggregation", query = "SELECT v FROM Variables v WHERE v.disaggregation = :disaggregation"),
    @NamedQuery(name = "Variables.findByTableName", query = "SELECT v FROM Variables v WHERE v.tableName = :tableName"),
    @NamedQuery(name = "Variables.findByField", query = "SELECT v FROM Variables v WHERE v.field = :field"),
    @NamedQuery(name = "Variables.findByCategory", query = "SELECT v FROM Variables v WHERE v.category = :category")})
public class Variables implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "variable_id", nullable = false)
    private Integer variableId;
    @Size(max = 2147483647)
    @Column(name = "variable_name", length = 2147483647)
    private String variableName;
    @Column(name = "disaggregation")
    private Boolean disaggregation;
    @Size(max = 2147483647)
    @Column(name = "table_name", length = 2147483647)
    private String tableName;
    @Size(max = 2147483647)
    @Column(name = "field", length = 2147483647)
    private String field;
    @Size(max = 2147483647)
    @Column(name = "category", length = 2147483647)
    private String category;
    @ManyToMany(mappedBy = "variablesList")
    private List<Indicators> indicatorsList;

    public Variables() {
    }

    public Variables(Integer variableId) {
        this.variableId = variableId;
    }

    public Integer getVariableId() {
        return variableId;
    }

    public void setVariableId(Integer variableId) {
        this.variableId = variableId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public Boolean getDisaggregation() {
        return disaggregation;
    }

    public void setDisaggregation(Boolean disaggregation) {
        this.disaggregation = disaggregation;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlTransient
    public List<Indicators> getIndicatorsList() {
        return indicatorsList;
    }

    public void setIndicatorsList(List<Indicators> indicatorsList) {
        this.indicatorsList = indicatorsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (variableId != null ? variableId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Variables)) {
            return false;
        }
        Variables other = (Variables) object;
        if ((this.variableId == null && other.variableId != null) || (this.variableId != null && !this.variableId.equals(other.variableId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Variables[ variableId=" + variableId + " ]";
    }
    
}
