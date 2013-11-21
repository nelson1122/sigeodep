/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package closures;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.ClosuresEnum;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.joda.time.DateTime;

/**
 *
 * @author santos
 */
@ManagedBean(name = "closuresMB")
@SessionScoped
public class ClosuresMB {

    /**
     * Creates a new instance of ClosuresMB
     */
//    private List<String> years;
//    private String currentYear;
//    private SelectItem[] months;
//    private short currentMonth;
    private SelectItem[] injuriesList;
    private short currentInjury;
    private String currentInjuryName;
    private ConnectionJdbcMB connectionJdbcMB;
    private String currentDateClosure = "";
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private String startDate;//fecha inicial de cierre
    private String yearBeforeDate;//un año antes de la fecha inical de cierre (para borrar cache)
    private String endDate;//fecha final de cierre
    private String outputText = "";
    private int currentVariableData;
    private SelectItem[] variablesData;
    DecimalFormat formatD = new DecimalFormat("0.00");

    public ClosuresMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void reset() {
        outputText = "";
        injuriesList = new SelectItem[10];
        variablesData = null;
        currentVariableData = 0;
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM injuries WHERE injury_id != 55");
            for (int i = 0; i < 10; i++) {
                if (rs.next()) {
                    injuriesList[i] = new SelectItem(rs.getShort(1), rs.getString(2));
                    if (i == 0) {
                        currentInjury = rs.getShort(1);
                        Date closureDate = rs.getDate("closure_date");
                        currentDateClosure = formato.format(closureDate);
                        DateTime closureTimeDT = new DateTime(closureDate).dayOfMonth().withMinimumValue();
                        startDate = formato.format(closureTimeDT.toDate());//fecha con primer dia del mes
                        closureTimeDT = new DateTime(closureDate).dayOfMonth().withMaximumValue();
                        endDate = formato.format(closureTimeDT.toDate());//fecha con ultimo dia del mes                        
                        yearBeforeDate = endDate.split("/")[0] + "/" + endDate.split("/")[1] + "/" + String.valueOf(Integer.parseInt(endDate.split("/")[2]) - 1);
                        currentInjuryName = rs.getString(2);
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
        }
    }

    public void changeInjury() {
        /*
         * se selecciona un tipo de lesion, se debe cargar la fecha del ultimo cierre
         */
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM injuries WHERE injury_id = " + currentInjury);
            if (rs.next()) {
                Date closureDate = rs.getDate("closure_date");
                currentDateClosure = formato.format(closureDate);
                DateTime closureTimeDT = new DateTime(closureDate).dayOfMonth().withMinimumValue();
                startDate = formato.format(closureTimeDT.toDate());//fecha con primer dia del mes
                closureTimeDT = new DateTime(closureDate).dayOfMonth().withMaximumValue();
                endDate = formato.format(closureTimeDT.toDate());//fecha con ultimo dia del mes                        
                yearBeforeDate = endDate.split("/")[0] + "/" + endDate.split("/")[1] + "/" + String.valueOf(Integer.parseInt(endDate.split("/")[2]) - 1);
                currentInjuryName = rs.getString(2);
            }
        } catch (SQLException | NumberFormatException e) {
        }
    }

    public void startClosure() {
        /*
         * iniciar con el cierre del periodo
         */
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La fecha va desde " + startDate.toString() + " hasta " + endDate.toString()));
        switch (currentInjury) {
            case 10://;"HOMICIDIO"
                break;
            case 11://;"MUERTE EN ACCIDENTE DE TRANSITO"
                break;
            case 12://;"SUICIDIO"
                break;
            case 13://;"MUERTE ACCIDENTAL"
                break;
            case 50://;"VIOLENCIA INTERPERSONAL"
                break;
            case 51://;"LESION EN ACCIDENTE DE TRANSITO"
                break;
            case 52://;"INTENCIONAL AUTOINFLINGIDA"
                break;
            case 53://;"VIOLENCIA INTRAFAMILIAR"
                break;
            case 54://;"NO INTENCIONAL"
                nonIntentional();
                break;
            case 55://;"VIOLENCIA INTRAFAMILIAR LCENF"
                break;
            case 56://;"SIVIGILA-VIF"                
                break;
        }
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Empieza proceso de cierre"));
    }

    private void nonIntentional() {
        /*
         * realizar el cierre de las no intencionales
         */
        try {
            String sql = ""
                    + " INSERT INTO non_fatal_non_intentional_sta \n"
                    + " SELECT \n"
                    + "	non_fatal_injury_id,\n"
                    + "	victim_age,\n"
                    //---------------------------------------------------------
                    + "	(select gender_name from genders where gender_id = victims.gender_id) as gender,\n"
                    //---------------------------------------------------------
                    + "	(select job_name from jobs where job_id = victims.job_id) as job,\n"
                    //---------------------------------------------------------
                    + "	(select neighborhood_name from neighborhoods where neighborhood_id = victims.victim_neighborhood_id) as victim_neighborhood,\n"
                    //---------------------------------------------------------
                    + "	(select ethnic_group_name from ethnic_groups where ethnic_group_id = victims.ethnic_group_id) as ethnic_group,\n"
                    //---------------------------------------------------------
                    + "	(select insurance_name from insurance where insurance_id = victims.insurance_id) as insurance,\n"
                    //---------------------------------------------------------
                    + "	--(SELECT cast(array_agg(vulnerable_group_id) as text ) FROM victim_vulnerable_group WHERE victim_vulnerable_group.victim_id=victims.victim_id ) as grupo_vulnerable ,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 1) \n"
                    + "	       WHEN '1' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_discapacitado,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 2) \n"
                    + "	       WHEN '2' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_desplazado,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 3) \n"
                    + "	       WHEN '3' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_desmovilizado,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 4) \n"
                    + "	       WHEN '4' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_refugiado,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 5) \n"
                    + "	       WHEN '5' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_campesino,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 6) \n"
                    + "	       WHEN '6' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_lgtbi,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 13) \n"
                    + "	       WHEN '13' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_migrantes,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 14) \n"
                    + "	       WHEN '14' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_carcelarios,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 16) \n"
                    + "	       WHEN '16' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_gestantes,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 98) \n"
                    + "	       WHEN '98' THEN 'SI'  \n"
                    + "	       ELSE 'NO'\n"
                    + "	 END AS gv_otro,	  \n"
                    //---------------------------------------------------------
                    + "	 checkup_date,\n"
                    //---------------------------------------------------------
                    + "	 checkup_time,\n"
                    //---------------------------------------------------------
                    + "	 injury_date,\n"
                    //---------------------------------------------------------
                    + "	 injury_time,\n"
                    //---------------------------------------------------------
                    + "	 (select neighborhood_name from neighborhoods where neighborhood_id = non_fatal_injuries.injury_neighborhood_id) as injury_neighborhood,\n"
                    //---------------------------------------------------------
                    + "	 (select quadrant_name from quadrants where quadrant_id = non_fatal_injuries.quadrant_id) as quadrant,\n"
                    //---------------------------------------------------------
                    + "	 (select non_fatal_data_source_name from non_fatal_data_sources where non_fatal_data_source_id = non_fatal_injuries.non_fatal_data_source_id) as non_fatal_data_source,\n"
                    //---------------------------------------------------------
                    + "	 (select activity_name from activities where activity_id = non_fatal_injuries.activity_id) as activity,\n"
                    //---------------------------------------------------------
                    + "	 (select mechanism_name from mechanisms where mechanism_id = non_fatal_injuries.mechanism_id) as mechanism,\n"
                    //---------------------------------------------------------
                    + "	 (select non_fatal_place_name from non_fatal_places where non_fatal_place_id = non_fatal_injuries.injury_place_id) as injury_place,\n"
                    //---------------------------------------------------------
                    + "	 (select use_alcohol_drugs_name from use_alcohol_drugs where use_alcohol_drugs_id = non_fatal_injuries.use_alcohol_id) as use_alcohol,	  \n"
                    //---------------------------------------------------------
                    + "	 (select use_alcohol_drugs_name from use_alcohol_drugs where use_alcohol_drugs_id = non_fatal_injuries.use_drugs_id) as use_drugs,\n"
                    //---------------------------------------------------------
                    + "	 burn_injury_degree,\n"
                    //---------------------------------------------------------
                    + "	 burn_injury_percentage,\n"
                    //---------------------------------------------------------
                    + "	 (select destination_patient_name from destinations_of_patient where destination_patient_id = non_fatal_injuries.destination_patient_id) as destination_patient,	  \n"
                    //---------------------------------------------------------
                    + "	 (SELECT diagnosis_name FROM diagnoses WHERE diagnosis_id IN\n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1)) AS cie_1,	  \n"
                    //---------------------------------------------------------
                    + "	 (SELECT diagnosis_name FROM diagnoses WHERE diagnosis_id IN\n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1 OFFSET 1)) AS cie_2,\n"
                    //---------------------------------------------------------
                    + "	 --(SELECT cast(array_agg(anatomical_location_id) as text ) FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id ) as sitio_anatomico,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=1) \n"
                    + "	    WHEN '1' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_sistemico,	  \n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=2) \n"
                    + "	    WHEN '2' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_craneo,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=3) \n"
                    + "	    WHEN '3' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_ojos,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=4) \n"
                    + "	    WHEN '4' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_maxilofacial,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=5) \n"
                    + "	    WHEN '5' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_cuello,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=6) \n"
                    + "	    WHEN '6' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_torax,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=7) \n"
                    + "	    WHEN '7' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_abdomen,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=8) \n"
                    + "	    WHEN '8' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_columna,\n"
                    //---------------------------------------------------------
                    + "         CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=9) \n"
                    + "	    WHEN '9' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_pelvis_genitales,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=10) \n"
                    + "	    WHEN '10' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_miembros_superiores,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=11) \n"
                    + "	    WHEN '11' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_miembros_inferiores,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=98) \n"
                    + "	    WHEN '98' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_otro,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=99) \n"
                    + "	    WHEN '99' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS sa_sin_dato,\n"
                    //---------------------------------------------------------
                    + "	 --(SELECT cast(array_agg(kind_injury_id) as text ) FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id ) as naturaleza_lesion,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=1) \n"
                    + "	    WHEN '1' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_laceracion,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=2) \n"
                    + "	    WHEN '2' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_cortada,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=3) \n"
                    + "	    WHEN '3' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_lesion_profunda,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=4) \n"
                    + "	    WHEN '4' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_esguince,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=5) \n"
                    + "	    WHEN '5' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_fractura,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=6) \n"
                    + "	    WHEN '6' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_quemadura,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=7) \n"
                    + "	    WHEN '7' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_contusion,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=8) \n"
                    + "	    WHEN '8' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_lesion_organica,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=9) \n"
                    + "	    WHEN '9' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_trauma_creneoencefalico,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=98) \n"
                    + "	    WHEN '98' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_otro,\n"
                    //---------------------------------------------------------
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=99) \n"
                    + "	    WHEN '99' THEN 'SI'  \n"
                    + "	    ELSE 'NO'\n"
                    + "	 END AS nl_no_se_sabe\n"
                    //---------------------------------------------------------
                    + "   FROM \n"
                    + "       non_fatal_injuries, victims \n"
                    + "   WHERE  \n"
                    + "       non_fatal_injuries.non_fatal_injury_id NOT IN( SELECT id_lesion::int FROM non_fatal_non_intentional_sta) AND \n"
                    + "       non_fatal_injuries.victim_id = victims.victim_id AND \n"
                    //+ "       non_fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
                    //+ "       non_fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') "
                    + "       non_fatal_injuries.injury_date >= to_date('01/09/2012','dd/MM/yyyy') AND \n"
                    + "       non_fatal_injuries.injury_date <= to_date('01/10/2012','dd/MM/yyyy') ";
            //System.out.println("\n" + sql + "\n");

            //se pasa la información a la tabla 
            connectionJdbcMB.non_query(sql);
            //se elimina los registrod de un año antes            
            /*connectionJdbcMB.non_query(""
             + " DELETE FROM "
             + "     non_fatal_non_intentional_sta "
             + " WHERE  \n"
             + "     non_fatal_injuries.injury_date < to_date('" + yearBeforeDate + "','dd/MM/yyyy')");
             */
            outputText = "";

            determineDataOutput("non_fatal_non_intentional_sta");

            //se realiza la imputacion 

            //se transfiere datos a la bodega

            //se acutualiza la fecha del ultimo cierre en la tabla injuries

        } catch (Exception e) {
        }
    }

    public void changeVariableData() {
        outputText = variablesData[currentVariableData].getDescription();
    }

    private void determineDataOutput(String table) {

        variablesData = new SelectItem[57];
        try {
            //determino cantidad de columnas
            String outText = "";
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + table);
            int numColumns = rs.getMetaData().getColumnCount();
            //recorro cada columna y realizo el analisis correspondiente
            double nullColumn = 0;
            for (int i = 2; i <= numColumns; i++) {
                //outputText = outputText + "<br/><hr width=\"50%\" align=\"center\"/>";
                switch (ClosuresEnum.convert(rs.getMetaData().getColumnName(i))) {//nombre de variable                                         
                    //case id_lesion:
                    case fecha_consulta:
                    case fecha_evento:
                        outText = ""
                                + "<br/> <b>" + rs.getMetaData().getColumnName(i).replace("_", " ") + "</b>"
                                + "<br/> <font color=\"blue\">Mínima:    </font> " + determineMinDateColumn("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i))
                                + "<br/> <font color=\"blue\">Maxima:    </font> " + determineMaxDateColumn("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i))
                                + "<br/> <font color=\"blue\">Frecuentes:</font> " + determineFiveFrecuents("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i));

                        break;
                    case edad_victima:
                        outText = ""
                                + "<br/> <b>" + rs.getMetaData().getColumnName(i).replace("_", " ") + "</b>"
                                + "<br/> <font color=\"blue\">Promedio:  </font> " + determineAverageColumn("non_fatal_non_intentional_sta", "edad_victima")
                                + "<br/> <font color=\"blue\">Mínimo:    </font> " + determineMinNumberColumn("non_fatal_non_intentional_sta", "edad_victima")
                                + "<br/> <font color=\"blue\">Maximo:    </font> " + determineMaxNumberColumn("non_fatal_non_intentional_sta", "edad_victima")
                                + "<br/> <font color=\"blue\">Frecuentes:</font> " + determineFiveFrecuents("non_fatal_non_intentional_sta", "edad_victima");
                        break;
                    case genero:
                    case ocupacion:
                    case barrio_residencia:
                    case grupo_etnico:
                    case aseguradora:
                    case hora_consulta:
                    case hora_evento:
                    case barrio_evento:
                    case cuadrante:
                    case fuente_no_fatal:
                    case actividad:
                    case mecanismo:
                    case lugar_del_hecho:
                    case uso_de_alcohol:
                    case uso_de_drogas:
                    case grado_quemadura:
                    case porcentaje_quemadura:
                    case destino_paciente:
                    case diagnostico_1:
                    case diagnostico_2:
                        outText = ""
                                + "<br/> <b>" + rs.getMetaData().getColumnName(i).replace("_", " ") + "</b>"
                                //+ "<br/> <font color=\"blue\">Moda:</font>       " + determineModeColumn("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i))
                                + "<br/> <font color=\"blue\">Frecuentes: </font>" + determineFiveFrecuents("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i));
                        break;
                    case gv_discapacitado:
                    case gv_desplazado:
                    case gv_desmovilizado:
                    case gv_refugiado:
                    case gv_campesino:
                    case gv_lgtbi:
                    case gv_migrantes:
                    case gv_carcelarios:
                    case gv_gestantes:
                    case gv_otro:
                    case sa_sistemico:
                    case sa_craneo:
                    case sa_ojos:
                    case sa_maxilofacial:
                    case sa_cuello:
                    case sa_torax:
                    case sa_abdomen:
                    case sa_columna:
                    case sa_pelvis_genitales:
                    case sa_miembros_superiores:
                    case sa_miembros_inferiores:
                    case sa_otro:
                    case sa_sin_dato:
                    case nl_laceracion:
                    case nl_cortada:
                    case nl_lesion_profunda:
                    case nl_esguince:
                    case nl_fractura:
                    case nl_quemadura:
                    case nl_contusion:
                    case nl_lesion_organica:
                    case nl_trauma_creneoencefalico:
                    case nl_otro:
                    case nl_no_se_sabe:
                        outText = ""
                                + "<br/> <b>" + rs.getMetaData().getColumnName(i).replace("_", " ") + "</b>"
                                //+ "<br/> <font color=\"blue\">Moda:</font> " + determineModeColumn("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i))
                                + "<br/> <font color=\"blue\">SI:   </font>" + determineYesPerColumn("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i))
                                + "<br/> <font color=\"blue\">NO:   </font>" + determineNoPerColumn("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i));
                        break;
                    case NOVALUE:
                        System.out.println("El nombre de columna (" + rs.getMetaData().getColumnName(i) + ") no se encontro en la enumeración");
                        break;
                }
                nullColumn = determineNullsPerColumn("non_fatal_non_intentional_sta", rs.getMetaData().getColumnName(i));
                if ((int) nullColumn == -1) {
                    outText = outText + "<br/> <font color=\"red\"> nulos por columna no determinado </font>";
                } else if ((int) nullColumn > -1 && (int) nullColumn <= 10) {
                    outText = outText + "<br/> <font color=\"blue\">Nulos:</font> <font color=\"green\"><b>" + formatD.format(nullColumn) + "%</b> </font>";
                } else if ((int) nullColumn > 10 && (int) nullColumn <= 30) {
                    outText = outText + "<br/> <font color=\"blue\">Nulos:</font> <font color=\"yellow\"><b>" + formatD.format(nullColumn) + "%</b> </font>";
                } else {
                    outText = outText + "<br/> <font color=\"blue\">Nulos:</font> <font color=\"red\"><b>" + formatD.format(nullColumn) + "%</b> </font>";
                }
                variablesData[i - 2] = new SelectItem(i - 2, rs.getMetaData().getColumnName(i).replace("_", " "), outText);
            }
            currentVariableData = 0;
            outputText = variablesData[currentVariableData].getDescription();
        } catch (Exception e) {
        }

    }

    private String determineAverageColumn(String table, String column) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select AVG(" + column + ") from " + table);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineMinDateColumn(String table, String column) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select MIN(" + column + ") from " + table);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineMaxDateColumn(String table, String column) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select MAX(" + column + ") from " + table);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineMinNumberColumn(String table, String column) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select MIN(" + column + ") from " + table);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineMaxNumberColumn(String table, String column) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select MAX(" + column + ") from " + table);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineModeColumn(String table, String column) {
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT " + column + ", COUNT(" + column + ") AS veces\n"
                    + "FROM " + table + "\n"
                    + "GROUP BY " + column + "\n"
                    + "ORDER BY COUNT(" + column + ") DESC\n"
                    + "LIMIT 1");
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "Sin determinar";
            }

        } catch (Exception e) {
            return "Sin determinar";
        }
    }

    private void determineNullsPerRow(String table) {
        try {

            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + table);
            int numColumns = rs.getMetaData().getColumnCount();
            int numOfNulls;
            int numOfRow = 0;
            double percentageOfNulls;
            while (rs.next()) {
                numOfNulls = 0;
                for (int i = 1; i <= numColumns; i++) {
                    if (rs.getString(i) == null || rs.getString(i).length() == 0) {
                        numOfNulls++;
                    }
                }
                numOfRow++;
                percentageOfNulls = (numOfNulls * 100) / numColumns;
                outputText = outputText + "<br/>La fila " + numOfRow + " tiene " + formatD.format(percentageOfNulls) + "% de nulos";
            }
        } catch (Exception e) {
        }
    }

    private double determineNullsPerColumn(String table, String column) {
        try {

            //determino numero de registros
            ResultSet rs = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + table);
            rs.next();
            double numOfRows = rs.getDouble(1);
            double percentageOfNulls;
            rs = connectionJdbcMB.consult("select count(*) from " + table + " where " + column + " is null");
            rs.next();
            if (rs.getInt(1) == 0) {
                return 0;
            } else {
                percentageOfNulls = (rs.getDouble(1) * 100) / numOfRows;
                return percentageOfNulls;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    private String determineYesPerColumn(String table, String column) {
        try {
            //determino numero de registros
            ResultSet rs = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + table);
            rs.next();
            double numOfRows = rs.getDouble(1);
            double percentageOfYes;
            rs = connectionJdbcMB.consult(""
                    + " select count(*) from " + table + " "
                    + " where " + column + " like 'SI'");
            rs.next();
            if (rs.getInt(1) == 0) {
                return "0%";
            } else {
                percentageOfYes = (rs.getDouble(1) * 100) / numOfRows;
                return formatD.format(percentageOfYes) + "%";
            }
        } catch (Exception e) {
            return "No determinado";
        }
    }

    private String determineNoPerColumn(String table, String column) {
        try {
            //determino numero de registros
            ResultSet rs = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + table);
            rs.next();
            double numOfRows = rs.getDouble(1);
            double percentageOfNo;
            rs = connectionJdbcMB.consult(""
                    + " select count(*) from " + table + " "
                    + " where " + column + " like 'NO'");
            rs.next();
            if (rs.getInt(1) == 0) {
                return "0%";
            } else {
                percentageOfNo = (rs.getDouble(1) * 100) / numOfRows;
                return formatD.format(percentageOfNo) + "%";
            }
        } catch (Exception e) {
            return "No determinado";
        }
    }

    private String determineFiveFrecuents(String table, String column) {
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + table);
            rs.next();
            double numOfRows = rs.getDouble(1);
            String out = "";
            rs = connectionJdbcMB.consult(""
                    + "SELECT " + column + ", COUNT(" + column + ") AS veces\n"
                    + "FROM " + table + "\n"
                    + "GROUP BY " + column + "\n"
                    + "ORDER BY COUNT(" + column + ") DESC\n"
                    + "LIMIT 5");
            int c = 1;
            while (rs.next()) {

                //darkgray
                if (rs.getInt(2) == 0) {
                    out = out + "<br/>" + rs.getString(1) + "<font color=\"gray\"> (" + rs.getString(2) + " veces) </font><font color=\"darkgray\"> (0%) </font>";
                } else {
                    out = out + "<br/>" + rs.getString(1) + "<font color=\"gray\"> (" + rs.getString(2) + " veces) </font><font color=\"darkgray\"> (" + String.valueOf(formatD.format((rs.getDouble(2) * 100) / numOfRows)) + "%) </font>";
                }
                c++;
            }
            return out;
        } catch (Exception e) {
            return "Sin determinar";
        }
    }

    public String getCurrentDateClosure() {
        return currentDateClosure;
    }

    public void setCurrentDateClosure(String currentDateClosure) {
        this.currentDateClosure = currentDateClosure;
    }

    public SelectItem[] getInjuriesList() {
        return injuriesList;
    }

    public void setInjuriesList(SelectItem[] injuriesList) {
        this.injuriesList = injuriesList;
    }

    public short getCurrentInjury() {
        return currentInjury;
    }

    public void setCurrentInjury(short currentInjury) {
        this.currentInjury = currentInjury;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCurrentInjuryName() {
        return currentInjuryName;
    }

    public void setCurrentInjuryName(String currentInjuryName) {
        this.currentInjuryName = currentInjuryName;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public SelectItem[] getVariablesData() {
        return variablesData;
    }

    public void setVariablesData(SelectItem[] variablesData) {
        this.variablesData = variablesData;
    }

    public int getCurrentVariableData() {
        return currentVariableData;
    }

    public void setCurrentVariableData(int currentVariableData) {
        this.currentVariableData = currentVariableData;
    }
}
