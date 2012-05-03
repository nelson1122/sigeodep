/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "fatal_injury_suicide", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FatalInjurySuicide.findAll", query = "SELECT f FROM FatalInjurySuicide f"),
    @NamedQuery(name = "FatalInjurySuicide.findByPreviousAttempt", query = "SELECT f FROM FatalInjurySuicide f WHERE f.previousAttempt = :previousAttempt"),
    @NamedQuery(name = "FatalInjurySuicide.findByMentalAntecedent", query = "SELECT f FROM FatalInjurySuicide f WHERE f.mentalAntecedent = :mentalAntecedent"),
    @NamedQuery(name = "FatalInjurySuicide.findByFatalInjuryId", query = "SELECT f FROM FatalInjurySuicide f WHERE f.fatalInjuryId = :fatalInjuryId")})
public class FatalInjurySuicide implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "previous_attempt")
    private Boolean previousAttempt;
    @Column(name = "mental_antecedent")
    private Boolean mentalAntecedent;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "fatal_injury_id", nullable = false)
    private Integer fatalInjuryId;
    @JoinColumn(name = "suicide_death_mechanism_id", referencedColumnName = "suicide_mechanism_id")
    @ManyToOne
    private SuicideMechanisms suicideDeathMechanismId;
    @JoinColumn(name = "related_event_id", referencedColumnName = "related_event_id")
    @ManyToOne
    private RelatedEvents relatedEventId;
    @JoinColumn(name = "fatal_injury_id", referencedColumnName = "fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private FatalInjuries fatalInjuries;
    @JoinColumn(name = "area_id", referencedColumnName = "area_id")
    @ManyToOne
    private Areas areaId;

    public FatalInjurySuicide() {
    }

    public FatalInjurySuicide(Integer fatalInjuryId) {
        this.fatalInjuryId = fatalInjuryId;
    }

    public Boolean getPreviousAttempt() {
        return previousAttempt;
    }

    public void setPreviousAttempt(Boolean previousAttempt) {
        this.previousAttempt = previousAttempt;
    }

    public Boolean getMentalAntecedent() {
        return mentalAntecedent;
    }

    public void setMentalAntecedent(Boolean mentalAntecedent) {
        this.mentalAntecedent = mentalAntecedent;
    }

    public Integer getFatalInjuryId() {
        return fatalInjuryId;
    }

    public void setFatalInjuryId(Integer fatalInjuryId) {
        this.fatalInjuryId = fatalInjuryId;
    }

    public SuicideMechanisms getSuicideDeathMechanismId() {
        return suicideDeathMechanismId;
    }

    public void setSuicideDeathMechanismId(SuicideMechanisms suicideDeathMechanismId) {
        this.suicideDeathMechanismId = suicideDeathMechanismId;
    }

    public RelatedEvents getRelatedEventId() {
        return relatedEventId;
    }

    public void setRelatedEventId(RelatedEvents relatedEventId) {
        this.relatedEventId = relatedEventId;
    }

    public FatalInjuries getFatalInjuries() {
        return fatalInjuries;
    }

    public void setFatalInjuries(FatalInjuries fatalInjuries) {
        this.fatalInjuries = fatalInjuries;
    }

    public Areas getAreaId() {
        return areaId;
    }

    public void setAreaId(Areas areaId) {
        this.areaId = areaId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fatalInjuryId != null ? fatalInjuryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FatalInjurySuicide)) {
            return false;
        }
        FatalInjurySuicide other = (FatalInjurySuicide) object;
        if ((this.fatalInjuryId == null && other.fatalInjuryId != null) || (this.fatalInjuryId != null && !this.fatalInjuryId.equals(other.fatalInjuryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.FatalInjurySuicide[ fatalInjuryId=" + fatalInjuryId + " ]";
    }
    
}
