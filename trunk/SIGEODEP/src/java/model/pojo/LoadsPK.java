/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author santos
 */
@Embeddable
public class LoadsPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "tag_id", nullable = false)
    private int tagId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "record_id", nullable = false)
    private int recordId;

    public LoadsPK() {
    }

    public LoadsPK(int tagId, int recordId) {
        this.tagId = tagId;
        this.recordId = recordId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) tagId;
        hash += (int) recordId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LoadsPK)) {
            return false;
        }
        LoadsPK other = (LoadsPK) object;
        if (this.tagId != other.tagId) {
            return false;
        }
        if (this.recordId != other.recordId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.LoadsPK[ tagId=" + tagId + ", recordId=" + recordId + " ]";
    }
    
}
