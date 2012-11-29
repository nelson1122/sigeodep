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
 * @author SANTOS
 */
@Entity
@Table(name = "users_configuration", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UsersConfiguration.findAll", query = "SELECT u FROM UsersConfiguration u"),
    @NamedQuery(name = "UsersConfiguration.findByUserId", query = "SELECT u FROM UsersConfiguration u WHERE u.userId = :userId"),
    @NamedQuery(name = "UsersConfiguration.findByTagName", query = "SELECT u FROM UsersConfiguration u WHERE u.tagName = :tagName"),
    @NamedQuery(name = "UsersConfiguration.findByFormId", query = "SELECT u FROM UsersConfiguration u WHERE u.formId = :formId"),
    @NamedQuery(name = "UsersConfiguration.findBySourceId", query = "SELECT u FROM UsersConfiguration u WHERE u.sourceId = :sourceId"),
    @NamedQuery(name = "UsersConfiguration.findByDelimiterFile", query = "SELECT u FROM UsersConfiguration u WHERE u.delimiterFile = :delimiterFile"),
    @NamedQuery(name = "UsersConfiguration.findByFileName", query = "SELECT u FROM UsersConfiguration u WHERE u.fileName = :fileName")})
public class UsersConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Size(max = 2147483647)
    @Column(name = "tag_name", length = 2147483647)
    private String tagName;
    @Size(max = 12)
    @Column(name = "form_id", length = 12)
    private String formId;
    @Column(name = "source_id")
    private Integer sourceId;
    @Size(max = 5)
    @Column(name = "delimiter_file", length = 5)
    private String delimiterFile;
    @Size(max = 2147483647)
    @Column(name = "file_name", length = 2147483647)
    private String fileName;
    @Column(name = "relation_group_name", length = 2147483647)
    private String relationGroupName;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Users users;

    public UsersConfiguration() {
    }

    public UsersConfiguration(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getDelimiterFile() {
        return delimiterFile;
    }

    public void setDelimiterFile(String delimiterFile) {
        this.delimiterFile = delimiterFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getRelationGroupName() {
        return relationGroupName;
    }

    public void setRelationGroupName(String relationGroupName) {
        this.relationGroupName = relationGroupName;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsersConfiguration)) {
            return false;
        }
        UsersConfiguration other = (UsersConfiguration) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.UsersConfiguration[ userId=" + userId + " ]";
    }
    
}
