/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 *
 * @author SANTOS
 */
public enum PopulationsEnum {
    loadCapitalMunicipality,//cabecera municipal
    loadTownCenter,//centro poblado
    loadDispersedRural,//rural disperso
    NOVALUE;
    public static PopulationsEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
