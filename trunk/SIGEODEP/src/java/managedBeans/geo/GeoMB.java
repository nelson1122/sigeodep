/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo;

import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author and
 */
@ManagedBean
@SessionScoped
public class GeoMB {
    Map<String, String> variables;
    String selectedVariable;
    Map<String, Integer> values;
    String selectedValue;

    /**
     * Creates a new instance of GeoMB
     */
    public GeoMB() {
        GeoDBConnection connection = new GeoDBConnection();
        connection.connect();
        variables = connection.getVariables();
        connection.disconnect();
    }
    
    public void handleVariableChange(){
        GeoDBConnection connection = new GeoDBConnection();
        connection.connect();
        values = connection.getValues("SCC-F-032", selectedVariable);
        connection.disconnect();
    }
    
    /*
     * Setters and Getters
     */

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    public String getSelectedVariable() {
        return selectedVariable;
    }

    public void setSelectedVariable(String selectedVariable) {
        this.selectedVariable = selectedVariable;
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    public void setValues(Map<String, Integer> values) {
        this.values = values;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
    
}
