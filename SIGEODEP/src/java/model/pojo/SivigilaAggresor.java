/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "sivigila_aggresor", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaAggresor.findAll", query = "SELECT s FROM SivigilaAggresor s"),
    @NamedQuery(name = "SivigilaAggresor.findBySivigilaAgresorId", query = "SELECT s FROM SivigilaAggresor s WHERE s.sivigilaAgresorId = :sivigilaAgresorId"),
    @NamedQuery(name = "SivigilaAggresor.findByAge", query = "SELECT s FROM SivigilaAggresor s WHERE s.age = :age"),
    @NamedQuery(name = "SivigilaAggresor.findByGender", query = "SELECT s FROM SivigilaAggresor s WHERE s.gender = :gender"),
    @NamedQuery(name = "SivigilaAggresor.findByOccupation", query = "SELECT s FROM SivigilaAggresor s WHERE s.occupation = :occupation"),
    @NamedQuery(name = "SivigilaAggresor.findByOtherRelative", query = "SELECT s FROM SivigilaAggresor s WHERE s.otherRelative = :otherRelative"),
    @NamedQuery(name = "SivigilaAggresor.findByOtherNoRelative", query = "SELECT s FROM SivigilaAggresor s WHERE s.otherNoRelative = :otherNoRelative"),
    @NamedQuery(name = "SivigilaAggresor.findByLiveTogether", query = "SELECT s FROM SivigilaAggresor s WHERE s.liveTogether = :liveTogether"),
    @NamedQuery(name = "SivigilaAggresor.findByOtherGroup", query = "SELECT s FROM SivigilaAggresor s WHERE s.otherGroup = :otherGroup"),
    @NamedQuery(name = "SivigilaAggresor.findByAlcoholOrDrugs", query = "SELECT s FROM SivigilaAggresor s WHERE s.alcoholOrDrugs = :alcoholOrDrugs")})
public class SivigilaAggresor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_agresor_id", nullable = false)
    private Integer sivigilaAgresorId;
    @Column(name = "age")
    private Integer age;
    @Column(name = "gender")
    private Short gender;
    @Column(name = "occupation")
    private Integer occupation;
    @Size(max = 2147483647)
    @Column(name = "other_relative", length = 2147483647)
    private String otherRelative;
    @Size(max = 2147483647)
    @Column(name = "other_no_relative", length = 2147483647)
    private String otherNoRelative;
    @Column(name = "live_together")
    private Boolean liveTogether;
    @Size(max = 2147483647)
    @Column(name = "other_group", length = 2147483647)
    private String otherGroup;
    @Column(name = "alcohol_or_drugs")
    private Boolean alcoholOrDrugs;
    @JoinColumn(name = "relative_id", referencedColumnName = "sivigila_relative_id")
    @ManyToOne
    private SivigilaRelative relativeId;
    @JoinColumn(name = "no_relative_id", referencedColumnName = "sivigila_no_relative_id")
    @ManyToOne
    private SivigilaNoRelative noRelativeId;
    @JoinColumn(name = "group_id", referencedColumnName = "sivigila_group_id")
    @ManyToOne
    private SivigilaGroup groupId;
    @JoinColumn(name = "educational_level_id", referencedColumnName = "sivigila_educational_level_id")
    @ManyToOne
    private SivigilaEducationalLevel educationalLevelId;

    public SivigilaAggresor() {
    }

    public SivigilaAggresor(Integer sivigilaAgresorId) {
        this.sivigilaAgresorId = sivigilaAgresorId;
    }

    public Integer getSivigilaAgresorId() {
        return sivigilaAgresorId;
    }

    public void setSivigilaAgresorId(Integer sivigilaAgresorId) {
        this.sivigilaAgresorId = sivigilaAgresorId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Short getGender() {
        return gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public Integer getOccupation() {
        return occupation;
    }

    public void setOccupation(Integer occupation) {
        this.occupation = occupation;
    }

    public String getOtherRelative() {
        return otherRelative;
    }

    public void setOtherRelative(String otherRelative) {
        this.otherRelative = otherRelative;
    }

    public String getOtherNoRelative() {
        return otherNoRelative;
    }

    public void setOtherNoRelative(String otherNoRelative) {
        this.otherNoRelative = otherNoRelative;
    }

    public Boolean getLiveTogether() {
        return liveTogether;
    }

    public void setLiveTogether(Boolean liveTogether) {
        this.liveTogether = liveTogether;
    }

    public String getOtherGroup() {
        return otherGroup;
    }

    public void setOtherGroup(String otherGroup) {
        this.otherGroup = otherGroup;
    }

    public Boolean getAlcoholOrDrugs() {
        return alcoholOrDrugs;
    }

    public void setAlcoholOrDrugs(Boolean alcoholOrDrugs) {
        this.alcoholOrDrugs = alcoholOrDrugs;
    }

    public SivigilaRelative getRelativeId() {
        return relativeId;
    }

    public void setRelativeId(SivigilaRelative relativeId) {
        this.relativeId = relativeId;
    }

    public SivigilaNoRelative getNoRelativeId() {
        return noRelativeId;
    }

    public void setNoRelativeId(SivigilaNoRelative noRelativeId) {
        this.noRelativeId = noRelativeId;
    }

    public SivigilaGroup getGroupId() {
        return groupId;
    }

    public void setGroupId(SivigilaGroup groupId) {
        this.groupId = groupId;
    }

    public SivigilaEducationalLevel getEducationalLevelId() {
        return educationalLevelId;
    }

    public void setEducationalLevelId(SivigilaEducationalLevel educationalLevelId) {
        this.educationalLevelId = educationalLevelId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sivigilaAgresorId != null ? sivigilaAgresorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaAggresor)) {
            return false;
        }
        SivigilaAggresor other = (SivigilaAggresor) object;
        if ((this.sivigilaAgresorId == null && other.sivigilaAgresorId != null) || (this.sivigilaAgresorId != null && !this.sivigilaAgresorId.equals(other.sivigilaAgresorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.SivigilaAggresor[ sivigilaAgresorId=" + sivigilaAgresorId + " ]";
    }
    
}
