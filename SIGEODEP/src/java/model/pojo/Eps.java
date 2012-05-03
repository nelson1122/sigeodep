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
@Table(name = "eps", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"eps_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Eps.findAll", query = "SELECT e FROM Eps e"),
    @NamedQuery(name = "Eps.findByEpsId", query = "SELECT e FROM Eps e WHERE e.epsId = :epsId"),
    @NamedQuery(name = "Eps.findByEpsName", query = "SELECT e FROM Eps e WHERE e.epsName = :epsName")})
public class Eps implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "eps_id", nullable = false)
    private Short epsId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "eps_name", nullable = false, length = 50)
    private String epsName;
    @OneToMany(mappedBy = "epsId")
    private List<Victims> victimsList;

    public Eps() {
    }

    public Eps(Short epsId) {
        this.epsId = epsId;
    }

    public Eps(Short epsId, String epsName) {
        this.epsId = epsId;
        this.epsName = epsName;
    }

    public Short getEpsId() {
        return epsId;
    }

    public void setEpsId(Short epsId) {
        this.epsId = epsId;
    }

    public String getEpsName() {
        return epsName;
    }

    public void setEpsName(String epsName) {
        this.epsName = epsName;
    }

    @XmlTransient
    public List<Victims> getVictimsList() {
        return victimsList;
    }

    public void setVictimsList(List<Victims> victimsList) {
        this.victimsList = victimsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (epsId != null ? epsId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Eps)) {
            return false;
        }
        Eps other = (Eps) object;
        if ((this.epsId == null && other.epsId != null) || (this.epsId != null && !this.epsId.equals(other.epsId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Eps[ epsId=" + epsId + " ]";
    }
    
}
