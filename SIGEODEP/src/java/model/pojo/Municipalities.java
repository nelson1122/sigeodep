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
 * @author santos
 */
@Entity
@Table(name = "municipalities", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Municipalities.findAll", query = "SELECT m FROM Municipalities m"),
    @NamedQuery(name = "Municipalities.findByMunicipalityId", query = "SELECT m FROM Municipalities m WHERE m.municipalityId = :municipalityId"),
    @NamedQuery(name = "Municipalities.findByMunicipalityName", query = "SELECT m FROM Municipalities m WHERE m.municipalityName = :municipalityName")})
public class Municipalities implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "municipality_id", nullable = false)
    private Short municipalityId;
    @Size(max = 50)
    @Column(name = "municipality_name", length = 50)
    private String municipalityName;

    public Municipalities() {
    }

    public Municipalities(Short municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Short getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Short municipalityId) {
        this.municipalityId = municipalityId;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (municipalityId != null ? municipalityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Municipalities)) {
            return false;
        }
        Municipalities other = (Municipalities) object;
        if ((this.municipalityId == null && other.municipalityId != null) || (this.municipalityId != null && !this.municipalityId.equals(other.municipalityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Municipalities[ municipalityId=" + municipalityId + " ]";
    }
    
}
