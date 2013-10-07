/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.util;

/**
 *
 * @author santos
 */
public class SpanColumns 
{
    private String label;
    private int columns;

    public SpanColumns() {
        label="";
        columns=0;
    }
    
    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
}
