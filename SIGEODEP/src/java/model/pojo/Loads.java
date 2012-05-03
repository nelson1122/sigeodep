/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "loads", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Loads.findAll", query = "SELECT l FROM Loads l"),
    @NamedQuery(name = "Loads.findByTagId", query = "SELECT l FROM Loads l WHERE l.loadsPK.tagId = :tagId"),
    @NamedQuery(name = "Loads.findByIsFatal", query = "SELECT l FROM Loads l WHERE l.isFatal = :isFatal"),
    @NamedQuery(name = "Loads.findByRecordId", query = "SELECT l FROM Loads l WHERE l.loadsPK.recordId = :recordId")})
public class Loads implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected LoadsPK loadsPK;
    @Column(name = "is_fatal")
    private Boolean isFatal;

    public Loads() {
    }

    public Loads(LoadsPK loadsPK) {
        this.loadsPK = loadsPK;
    }

    public Loads(int tagId, int recordId) {
        this.loadsPK = new LoadsPK(tagId, recordId);
    }

    public LoadsPK getLoadsPK() {
        return loadsPK;
    }

    public void setLoadsPK(LoadsPK loadsPK) {
        this.loadsPK = loadsPK;
    }

    public Boolean getIsFatal() {
        return isFatal;
    }

    public void setIsFatal(Boolean isFatal) {
        this.isFatal = isFatal;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (loadsPK != null ? loadsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Loads)) {
            return false;
        }
        Loads other = (Loads) object;
        if ((this.loadsPK == null && other.loadsPK != null) || (this.loadsPK != null && !this.loadsPK.equals(other.loadsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Loads[ loadsPK=" + loadsPK + " ]";
    }
    
}
