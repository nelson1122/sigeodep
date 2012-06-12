/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.registerData;

/**
 *
 * @author SANTOS
 */
public enum SCC_F_028Enum {

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
    contexto,
    narrac,
    narrac1,
    narrac2,
    n24nivelde,
    mgsindato,
    pendiente,
    ns,
    negativo,
    NOVALUE;

    public static SCC_F_028Enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
