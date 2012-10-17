/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 *
 * @author SANTOS
 */
public enum DataTypeEnum {

    text,
    day,
    month,
    year,
    minute,
    hour,
    integer,
    date,
    age,
    military,
    percentage,
    level,
    error,
    NOVALUE;

    public static DataTypeEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
