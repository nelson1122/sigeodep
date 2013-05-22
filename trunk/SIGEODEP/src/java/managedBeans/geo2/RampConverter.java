/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

import java.awt.Color;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 *
 * @author and
 */
@ManagedBean
@SessionScoped
public class RampConverter implements Converter {
    public static ArrayList<Ramp> rampDB;

    static {
        rampDB = new ArrayList<>();
        Ramp ramp1 = new Ramp(1, "Semaforo", "../geo2/lib/img/down_16.png");
        ramp1.setColors(Color.GREEN, Color.yellow, Color.RED);
        rampDB.add(ramp1);
        Ramp ramp2 = new Ramp(2, "Amarillo-Rojo", "../geo2/lib/img/up_16.png");
        ramp2.setColors(Color.yellow, Color.RED);
        rampDB.add(ramp2);
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                for (Ramp r : rampDB) {
                    if (r.getName().equals(submittedValue)) {
                        return r;
                    }
                }
            } catch(NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid Ramp"));
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return value.toString();
        }
    }
}
   
