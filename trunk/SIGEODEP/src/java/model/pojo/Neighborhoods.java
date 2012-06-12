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
@Table(name = "neighborhoods", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Neighborhoods.findAll", query = "SELECT n FROM Neighborhoods n"),
    @NamedQuery(name = "Neighborhoods.findByNeighborhoodName", query = "SELECT n FROM Neighborhoods n WHERE n.neighborhoodName = :neighborhoodName"),
    @NamedQuery(name = "Neighborhoods.findByNeighborhoodId", query = "SELECT n FROM Neighborhoods n WHERE n.neighborhoodId = :neighborhoodId"),
    @NamedQuery(name = "Neighborhoods.findBySuburbId", query = "SELECT n FROM Neighborhoods n WHERE n.suburbId = :suburbId"),
    @NamedQuery(name = "Neighborhoods.findByNeighborhoodLevel", query = "SELECT n FROM Neighborhoods n WHERE n.neighborhoodLevel = :neighborhoodLevel"),
    @NamedQuery(name = "Neighborhoods.findByNeighborhoodType", query = "SELECT n FROM Neighborhoods n WHERE n.neighborhoodType = :neighborhoodType")})
public class Neighborhoods implements Serializable {
    private static final long serialVersionUID = 1L;
    @Size(max = 75)
    @Column(name = "neighborhood_name", length = 75)
    private String neighborhoodName;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "neighborhood_id", nullable = false)
    private Integer neighborhoodId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "suburb_id", nullable = false)
    private int suburbId;
    @Column(name = "neighborhood_level")
    private Character neighborhoodLevel;
    @Column(name = "neighborhood_type")
    private Character neighborhoodType;
    @OneToMany(mappedBy = "victimNeighborhoodId")
    private List<Victims> victimsList;
    @OneToMany(mappedBy = "injuryNeighborhoodId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public Neighborhoods() {
    }

    public Neighborhoods(Integer neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public Neighborhoods(Integer neighborhoodId, int suburbId) {
        this.neighborhoodId = neighborhoodId;
        this.suburbId = suburbId;
    }

    public String getNeighborhoodName() {
        return neighborhoodName;
    }

    public void setNeighborhoodName(String neighborhoodName) {
        this.neighborhoodName = neighborhoodName;
    }

    public Integer getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(Integer neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public int getSuburbId() {
        return suburbId;
    }

    public void setSuburbId(int suburbId) {
        this.suburbId = suburbId;
    }

    public Character getNeighborhoodLevel() {
        return neighborhoodLevel;
    }

    public void setNeighborhoodLevel(Character neighborhoodLevel) {
        this.neighborhoodLevel = neighborhoodLevel;
    }

    public Character getNeighborhoodType() {
        return neighborhoodType;
    }

    public void setNeighborhoodType(Character neighborhoodType) {
        this.neighborhoodType = neighborhoodType;
    }

    @XmlTransient
    public List<Victims> getVictimsList() {
        return victimsList;
    }

    public void setVictimsList(List<Victims> victimsList) {
        this.victimsList = victimsList;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
        return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
        this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (neighborhoodId != null ? neighborhoodId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Neighborhoods)) {
            return false;
        }
        Neighborhoods other = (Neighborhoods) object;
        if ((this.neighborhoodId == null && other.neighborhoodId != null) || (this.neighborhoodId != null && !this.neighborhoodId.equals(other.neighborhoodId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "j.Neighborhoods[ neighborhoodId=" + neighborhoodId + " ]";
    }
    
}
