/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.registerData;

/**
 *
 * @author SANTOS
 */
public enum SCC_F_031Enum {

    field1,
    clave,
    ficha,
    departamen,
    codigodepa,
    municipio,
    codigomuni,
    codigo,
    dia,
    mes,
    ao,
    fechah,
    horas,
    minutos,
    ampm,
    horamil,
    hrshorasin,
    direccion,
    barrio,
    codbar,
    area,
    lugarhecho,
    diasem,
    victimas,
    lesionados,
    nombres,
    apellidos,
    sexo,
    tipoedad,
    edad,
    ocupacion,
    munres,
    residencia,
    tipoid,
    nroid,
    procedenci,
    tipoarma,
    narrac,
    narrac1,
    narrac2,
    n24nivelal,
    mgsindato,
    ns,
    pendiente,
    negativa,
    NOVALUE;

    public static SCC_F_031Enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
