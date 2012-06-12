/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.registerData;

/**
 *
 * @author SANTOS
 */
public enum SCC_F_030Enum {

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
    eventos,
    intentos,
    saludmenta,
    narrac,
    narrac1,
    narrac2,
    n26nivelde,
    mgsindato,
    pendiente,
    ns,
    negativo,
    NOVALUE;

    public static SCC_F_030Enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
