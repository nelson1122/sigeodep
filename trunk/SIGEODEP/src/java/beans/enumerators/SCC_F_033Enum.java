/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 *
 * @author SANTOS
 */
public enum SCC_F_033Enum {

    registro,
    clave,
    instituc,
    apellido,
    apellido1,
    nombre,
    nombre1,
    tipo,
    numero,
    medida,
    edad,
    sexo,
    ocupacio,
    asegurad,
    departam,
    codigo,
    municipi,
    codigo1,
    barrio,
    codigo2,
    direccio,
    telefono,
    departam1,
    codigo3,
    municip,
    codigo4,
    barrio1,
    codigo5,
    direccio1,
    fecha,
    mes,
    ao,
    fecha1,
    dia,
    hora,
    minutos,
    ampm,
    hora1,
    fecha2,
    mes1,
    ao1,
    fecha3,
    dia1,
    hora2,
    minutos1,
    ampm1,
    hora3,
    remitido,
    de,
    lugar,
    cual,
    activida,
    cual1,
    mecanism,
    altura,
    cual2,
    cual3,
    otro,
    uso,
    uso1,
    grado,
    porcente,
    grupo,
    otro1,
    grupo1,
    otro2,
    ma1,
    field1,
    ma2,
    field2,
    ma3,
    field3,
    ma4,
    field4,
    ma5,
    field5,
    ma6,
    field6,
    ma7,
    field7,
    ma8,
    field8,
    cual4,
    field9,
    cual5,
    field10,
    concilia,
    caucion,
    dictamen,
    remision,
    remision1,
    remision2,
    remision3,
    medidas,
    remision4,
    atencion,
    restable,
    otra,
    cual6,
    sindat,
    responsab,
    NOVALUE;

    public static SCC_F_033Enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
