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
@Table(name = "non_fatal_self-inflicted", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NonFatalSelfInflicted.findAll", query = "SELECT n FROM NonFatalSelfInflicted n"),
    @NamedQuery(name = "NonFatalSelfInflicted.findByPreviousAttempt", query = "SELECT n FROM NonFatalSelfInflicted n WHERE n.previousAttempt = :previousAttempt"),
    @NamedQuery(name = "NonFatalSelfInflicted.findByMentalAntecedent", query = "SELECT n FROM NonFatalSelfInflicted n WHERE n.mentalAntecedent = :mentalAntecedent"),
    @NamedQuery(name = "NonFatalSelfInflicted.findByNonFatalInjuryId", query = "SELECT n FROM NonFatalSelfInflicted n WHERE n.nonFatalInjuryId = :nonFatalInjuryId")})
public class NonFatalSelfInflicted implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "previous_attempt")
    private Boolean previousAttempt;
    @Column(name = "mental_antecedent")
    private Boolean mentalAntecedent;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_injury_id", nullable = false)
    private Integer nonFatalInjuryId;
    @JoinColumn(name = "precipitating_factor_id", referencedColumnName = "precipitating_factor_id")
    @ManyToOne
    private PrecipitatingFactors precipitatingFactorId;
    @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private NonFatalInjuries nonFatalInjuries;

    public NonFatalSelfInflicted() {
    }

    public NonFatalSelfInflicted(Integer nonFatalInjuryId) {
        this.nonFatalInjuryId = nonFatalInjuryId;
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

    public Integer getNonFatalInjuryId() {
        return nonFatalInjuryId;
    }

    public void setNonFatalInjuryId(Integer nonFatalInjuryId) {
        this.nonFatalInjuryId = nonFatalInjuryId;
    }

    public PrecipitatingFactors getPrecipitatingFactorId() {
        return precipitatingFactorId;
    }

    public void setPrecipitatingFactorId(PrecipitatingFactors precipitatingFactorId) {
        this.precipitatingFactorId = precipitatingFactorId;
    }

    public NonFatalInjuries getNonFatalInjuries() {
        return nonFatalInjuries;
    }

    public void setNonFatalInjuries(NonFatalInjuries nonFatalInjuries) {
        this.nonFatalInjuries = nonFatalInjuries;
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
        if (!(object instanceof NonFatalSelfInflicted)) {
            return false;
        }
        NonFatalSelfInflicted other = (NonFatalSelfInflicted) object;
        if ((this.nonFatalInjuryId == null && other.nonFatalInjuryId != null) || (this.nonFatalInjuryId != null && !this.nonFatalInjuryId.equals(other.nonFatalInjuryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.NonFatalSelfInflicted[ nonFatalInjuryId=" + nonFatalInjuryId + " ]";
    }
    
}