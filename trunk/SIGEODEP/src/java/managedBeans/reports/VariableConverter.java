/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.reports;

/**
 *
 * @author and
 */
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "variableConverter")
public class VariableConverter implements Converter {

    ReportConnection connection = new ReportConnection();

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                connection.connect();
                int id = Integer.parseInt(submittedValue);
                Object variable = connection.getVariableById(id);
                connection.disconnect();
                return variable;

            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Variable no valida."));
            }
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return ((Integer) value).toString();
        }
    }
}