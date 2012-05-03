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
@Table(name = "sources", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"source_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sources.findAll", query = "SELECT s FROM Sources s"),
    @NamedQuery(name = "Sources.findBySourceId", query = "SELECT s FROM Sources s WHERE s.sourceId = :sourceId"),
    @NamedQuery(name = "Sources.findBySourceName", query = "SELECT s FROM Sources s WHERE s.sourceName = :sourceName")})
public class Sources implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "source_id", nullable = false)
    private Integer sourceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "source_name", nullable = false, length = 50)
    private String sourceName;
    @ManyToMany(mappedBy = "sourcesList")
    private List<Forms> formsList;

    public Sources() {
    }

    public Sources(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Sources(Integer sourceId, String sourceName) {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @XmlTransient
    public List<Forms> getFormsList() {
        return formsList;
    }

    public void setFormsList(List<Forms> formsList) {
        this.formsList = formsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sourceId != null ? sourceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sources)) {
            return false;
        }
        Sources other = (Sources) object;
        if ((this.sourceId == null && other.sourceId != null) || (this.sourceId != null && !this.sourceId.equals(other.sourceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return sourceName;
    }
    
}
