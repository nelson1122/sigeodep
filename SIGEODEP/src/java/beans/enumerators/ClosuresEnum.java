/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 *
 * @author SANTOS
 */
public enum ClosuresEnum {

    
    id_lesion,
    edad_victima,
    genero,
    ocupacion,
    barrio_residencia,
    grupo_etnico,
    aseguradora,
    gv_discapacitado,
    gv_desplazado,
    gv_desmovilizado,
    gv_refugiado,
    gv_campesino,
    gv_lgtbi,
    gv_migrantes,
    gv_carcelarios,
    gv_gestantes,
    gv_otro,
    fecha_consulta,
    hora_consulta,
    fecha_evento,
    hora_evento,
    barrio_evento,
    cuadrante,
    fuente_no_fatal,
    actividad,
    mecanismo,
    lugar_del_hecho,
    uso_de_alcohol,
    uso_de_drogas,
    grado_quemadura,
    porcentaje_quemadura,
    destino_paciente,
    diagnostico_1,
    diagnostico_2,
    sa_sistemico,
    sa_craneo,
    sa_ojos,
    sa_maxilofacial,
    sa_cuello,
    sa_torax,
    sa_abdomen,
    sa_columna,
    sa_pelvis_genitales,
    sa_miembros_superiores,
    sa_miembros_inferiores,
    sa_otro,
    sa_sin_dato,
    nl_laceracion,
    nl_cortada,
    nl_lesion_profunda,
    nl_esguince,
    nl_fractura,
    nl_quemadura,
    nl_contusion,
    nl_lesion_organica,
    nl_trauma_creneoencefalico,
    nl_otro,
    nl_no_se_sabe,
    NOVALUE;

    public static ClosuresEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
