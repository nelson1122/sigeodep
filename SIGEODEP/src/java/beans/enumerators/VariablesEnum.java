/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 *
 * @author SANTOS
 */
public enum VariablesEnum {
    day,//dia,
    age,//edad
    neighborhoods,//barrio,
    communes,//comuna,
    corridors,//corredor,
    areas,//zona,
    genders,//genero,
    days,//dia_semana,
    year,//año
    month,//mes
    hour,//hora
    injuries_fatal,
    injuries_non_fatal,
    NOVALUE;
    public static VariablesEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
