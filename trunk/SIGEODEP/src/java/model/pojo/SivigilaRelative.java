/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "sivigila_relative", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sivigila_relative_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaRelative.findAll", query = "SELECT s FROM SivigilaRelative s"),
    @NamedQuery(name = "SivigilaRelative.findBySivigilaRelativeId", query = "SELECT s FROM SivigilaRelative s WHERE s.sivigilaRelativeId = :sivigilaRelativeId"),
    @NamedQuery(name = "SivigilaRelative.findBySivigilaRelativeName", query = "SELECT s FROM SivigilaRelative s WHERE s.sivigilaRelativeName = :sivigilaRelativeName")})
public class SivigilaRelative implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_relative_id", nullable = false)
    private Short sivigilaRelativeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "sivigila_relative_name", nullable = false, length = 2147483647)
    private String sivigilaRelativeName;
    @OneToMany(mappedBy = "relativeId")
    private List<SivigilaAggresor> sivigilaAggresorList;

    public SivigilaRelative() {
    }

    public SivigilaRelative(Short sivigilaRelativeId) {
        this.sivigilaRelativeId = sivigilaRelativeId;
    }

    public SivigilaRelative(Short sivigilaRelativeId, String sivigilaRelativeName) {
        this.sivigilaRelativeId = sivigilaRelativeId;
        this.sivigilaRelativeName = sivigilaRelativeName;
    }

    public Short getSivigilaRelativeId() {
        return sivigilaRelativeId;
    }

    public void setSivigilaRelativeId(Short sivigilaRelativeId) {
        this.sivigilaRelativeId = sivigilaRelativeId;
    }

    public String getSivigilaRelativeName() {
        return sivigilaRelativeName;
    }

    public void setSivigilaRelativeName(String sivigilaRelativeName) {
        this.sivigilaRelativeName = sivigilaRelativeName;
    }

    @XmlTransient
    public List<SivigilaAggresor> getSivigilaAggresorList() {
        return sivigilaAggresorList;
    }

    public void setSivigilaAggresorList(List<SivigilaAggresor> sivigilaAggresorList) {
        this.sivigilaAggresorList = sivigilaAggresorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sivigilaRelativeId != null ? sivigilaRelativeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaRelative)) {
            return false;
        }
        SivigilaRelative other = (SivigilaRelative) object;
        if ((this.sivigilaRelativeId == null && other.sivigilaRelativeId != null) || (this.sivigilaRelativeId != null && !this.sivigilaRelativeId.equals(other.sivigilaRelativeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.SivigilaRelative[ sivigilaRelativeId=" + sivigilaRelativeId + " ]";
    }
    
}
