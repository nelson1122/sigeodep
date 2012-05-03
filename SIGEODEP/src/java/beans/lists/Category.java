/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.lists;

/**
 *
 * @author santos
 */
public class Category {
    private String categorId;
    private String categorName;

    public Category(String categorId, String categorName) {
        this.categorId = categorId;
        this.categorName = categorName;
    }

    public String getCategorId() {
        return categorId;
    }

    public void setCategorId(String categorId) {
        this.categorId = categorId;
    }

    public String getCategorName() {
        return categorName;
    }

    public void setCategorName(String categorName) {
        this.categorName = categorName;
    }
    
}
