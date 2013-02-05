/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 *
 * @author SANTOS
 */
public enum IndicatorTypeEnum {

    count,
    percentage,
    variation,
    percentage_variation,
    average,
    rate,
    specified_rate,
    specified_percentage,
    NOVALUE;

    public static IndicatorTypeEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
