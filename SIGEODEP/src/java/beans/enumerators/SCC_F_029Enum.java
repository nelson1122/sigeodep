/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 *
 * @author SANTOS
 */
public enum SCC_F_029Enum {

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
    tipovia,
    area,
    claseacci,
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
    caracvict,
    proteccion,
    vehic1,
    vehic2,
    vehic3,
    vehic4,
    tiposerv1,
    tiposerv2,
    tiposerv3,
    tiposerv4,
    narrac,
    narrac1,
    narrac2,
    nivelvict,
    alcvict,
    nivelculp,
    alcculp,
    NOVALUE;

    public static SCC_F_029Enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
