/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 *
 * @author SANTOS
 */
public enum FormsEnum {
    SCC_F_028,
    SCC_F_029,
    SCC_F_030,
    SCC_F_031,
    SCC_F_032,
    SCC_F_033,
    NOVALUE;
    public static FormsEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
