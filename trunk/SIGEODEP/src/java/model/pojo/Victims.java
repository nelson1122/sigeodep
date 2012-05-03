/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "victims", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Victims.findAll", query = "SELECT v FROM Victims v"),
    @NamedQuery(name = "Victims.findByVictimNid", query = "SELECT v FROM Victims v WHERE v.victimNid = :victimNid"),
    @NamedQuery(name = "Victims.findByVictimFirstname", query = "SELECT v FROM Victims v WHERE v.victimFirstname = :victimFirstname"),
    @NamedQuery(name = "Victims.findByVictimLastname", query = "SELECT v FROM Victims v WHERE v.victimLastname = :victimLastname"),
    @NamedQuery(name = "Victims.findByVictimAge", query = "SELECT v FROM Victims v WHERE v.victimAge = :victimAge"),
    @NamedQuery(name = "Victims.findByAgeTypeId", query = "SELECT v FROM Victims v WHERE v.ageTypeId = :ageTypeId"),
    @NamedQuery(name = "Victims.findByVictimTelephone", query = "SELECT v FROM Victims v WHERE v.victimTelephone = :victimTelephone"),
    @NamedQuery(name = "Victims.findByVictimAddress", query = "SELECT v FROM Victims v WHERE v.victimAddress = :victimAddress"),
    @NamedQuery(name = "Victims.findByVictimNeighborhoodId", query = "SELECT v FROM Victims v WHERE v.victimNeighborhoodId = :victimNeighborhoodId"),
    @NamedQuery(name = "Victims.findByVictimDateOfBirth", query = "SELECT v FROM Victims v WHERE v.victimDateOfBirth = :victimDateOfBirth"),
    @NamedQuery(name = "Victims.findByVictimClass", query = "SELECT v FROM Victims v WHERE v.victimClass = :victimClass"),
    @NamedQuery(name = "Victims.findByVictimId", query = "SELECT v FROM Victims v WHERE v.victimId = :victimId")})
public class Victims implements Serializable {
    private static final long serialVersionUID = 1L;
    @Size(max = 20)
    @Column(name = "victim_nid", length = 20)
    private String victimNid;
    @Size(max = 2147483647)
    @Column(name = "victim_firstname", length = 2147483647)
    private String victimFirstname;
    @Size(max = 2147483647)
    @Column(name = "victim_lastname", length = 2147483647)
    private String victimLastname;
    @Column(name = "victim_age")
    private Short victimAge;
    @Column(name = "age_type_id")
    private Short ageTypeId;
    @Size(max = 20)
    @Column(name = "victim_telephone", length = 20)
    private String victimTelephone;
    @Size(max = 2147483647)
    @Column(name = "victim_address", length = 2147483647)
    private String victimAddress;
    @Column(name = "victim_neighborhood_id")
    private Integer victimNeighborhoodId;
    @Column(name = "victim_date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date victimDateOfBirth;
    @Column(name = "victim_class")
    private Short victimClass;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "victim_id", nullable = false)
    private Integer victimId;
    @JoinTable(name = "victim_vulnerable_group", joinColumns = {
        @JoinColumn(name = "victim_id", referencedColumnName = "victim_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "vulnerable_group_id", referencedColumnName = "vulnerable_group_id", nullable = false)})
    @ManyToMany
    private List<VulnerableGroups> vulnerableGroupsList;
    @JoinColumn(name = "vulnerable_group_id", referencedColumnName = "vulnerable_group_id")
    @ManyToOne
    private VulnerableGroups vulnerableGroupId;
    @JoinColumn(name = "job_id", referencedColumnName = "job_id")
    @ManyToOne
    private Jobs jobId;
    @JoinColumn(name = "type_id", referencedColumnName = "type_id")
    @ManyToOne
    private IdTypes typeId;
    @JoinColumn(name = "gender_id", referencedColumnName = "gender_id")
    @ManyToOne
    private Genders genderId;
    @JoinColumn(name = "ethnic_group_id", referencedColumnName = "ethnic_group_id")
    @ManyToOne
    private EthnicGroups ethnicGroupId;
    @JoinColumn(name = "eps_id", referencedColumnName = "eps_id")
    @ManyToOne
    private Eps epsId;
    @OneToMany(mappedBy = "victimId")
    private List<NonFatalInjuries> nonFatalInjuriesList;
    @OneToMany(mappedBy = "victimId")
    private List<FatalInjuries> fatalInjuriesList;

    public Victims() {
    }

    public Victims(Integer victimId) {
        this.victimId = victimId;
    }

    public String getVictimNid() {
        return victimNid;
    }

    public void setVictimNid(String victimNid) {
        this.victimNid = victimNid;
    }

    public String getVictimFirstname() {
        return victimFirstname;
    }

    public void setVictimFirstname(String victimFirstname) {
        this.victimFirstname = victimFirstname;
    }

    public String getVictimLastname() {
        return victimLastname;
    }

    public void setVictimLastname(String victimLastname) {
        this.victimLastname = victimLastname;
    }

    public Short getVictimAge() {
        return victimAge;
    }

    public void setVictimAge(Short victimAge) {
        this.victimAge = victimAge;
    }

    public Short getAgeTypeId() {
        return ageTypeId;
    }

    public void setAgeTypeId(Short ageTypeId) {
        this.ageTypeId = ageTypeId;
    }

    public String getVictimTelephone() {
        return victimTelephone;
    }

    public void setVictimTelephone(String victimTelephone) {
        this.victimTelephone = victimTelephone;
    }

    public String getVictimAddress() {
        return victimAddress;
    }

    public void setVictimAddress(String victimAddress) {
        this.victimAddress = victimAddress;
    }

    public Integer getVictimNeighborhoodId() {
        return victimNeighborhoodId;
    }

    public void setVictimNeighborhoodId(Integer victimNeighborhoodId) {
        this.victimNeighborhoodId = victimNeighborhoodId;
    }

    public Date getVictimDateOfBirth() {
        return victimDateOfBirth;
    }

    public void setVictimDateOfBirth(Date victimDateOfBirth) {
        this.victimDateOfBirth = victimDateOfBirth;
    }

    public Short getVictimClass() {
        return victimClass;
    }

    public void setVictimClass(Short victimClass) {
        this.victimClass = victimClass;
    }

    public Integer getVictimId() {
        return victimId;
    }

    public void setVictimId(Integer victimId) {
        this.victimId = victimId;
    }

    @XmlTransient
    public List<VulnerableGroups> getVulnerableGroupsList() {
        return vulnerableGroupsList;
    }

    public void setVulnerableGroupsList(List<VulnerableGroups> vulnerableGroupsList) {
        this.vulnerableGroupsList = vulnerableGroupsList;
    }

    public VulnerableGroups getVulnerableGroupId() {
        return vulnerableGroupId;
    }

    public void setVulnerableGroupId(VulnerableGroups vulnerableGroupId) {
        this.vulnerableGroupId = vulnerableGroupId;
    }

    public Jobs getJobId() {
        return jobId;
    }

    public void setJobId(Jobs jobId) {
        this.jobId = jobId;
    }

    public IdTypes getTypeId() {
        return typeId;
    }

    public void setTypeId(IdTypes typeId) {
        this.typeId = typeId;
    }

    public Genders getGenderId() {
        return genderId;
    }

    public void setGenderId(Genders genderId) {
        this.genderId = genderId;
    }

    public EthnicGroups getEthnicGroupId() {
        return ethnicGroupId;
    }

    public void setEthnicGroupId(EthnicGroups ethnicGroupId) {
        this.ethnicGroupId = ethnicGroupId;
    }

    public Eps getEpsId() {
        return epsId;
    }

    public void setEpsId(Eps epsId) {
        this.epsId = epsId;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
        return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
        this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @XmlTransient
    public List<FatalInjuries> getFatalInjuriesList() {
        return fatalInjuriesList;
    }

    public void setFatalInjuriesList(List<FatalInjuries> fatalInjuriesList) {
        this.fatalInjuriesList = fatalInjuriesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (victimId != null ? victimId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Victims)) {
            return false;
        }
        Victims other = (Victims) object;
        if ((this.victimId == null && other.victimId != null) || (this.victimId != null && !this.victimId.equals(other.victimId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Victims[ victimId=" + victimId + " ]";
    }
    
}
