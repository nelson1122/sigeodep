package managedBeans.closures;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import beans.connection.ConnectionJdbcMB;
import beans.enumerators.ClosuresEnum;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.joda.time.DateTime;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSink;

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
    private SelectItem[] injuriesList;
    private short currentInjury;
    private String currentInjuryName;
    private ConnectionJdbcMB connectionJdbcMB;
    private String currentDateClosure = "";
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private String startDate;//fecha inicial de cierre
    private String nextDateClosure;//fecha del siguiente de cierre
    private String yearBeforeDate;//un año antes de la fecha inical de cierre (para borrar cache)
    private String endDate;//fecha final de cierre
    private String outputTextAnalysis = "";
    private String outputTextImputation = "";
    private String outputTextStoreData = "";
    private int currentVariableData = 0;
    private SelectItem[] variablesData;
    private boolean renderedBtnAnalysis = true;
    private boolean renderedBtnImputation = false;
    private boolean renderedBtnReset = false;
    private boolean renderedBtnStoreData = false;
    private boolean renderedAnalysisResult = false;
    private boolean renderedImputationResult = false;
    private boolean renderedStoreDataResult = false;
    private DecimalFormat formatD = new DecimalFormat("0.00");
    private DecimalFormat formatI = new DecimalFormat("0");
    private Date currentDate = new Date();
    private SimpleDateFormat format_postgres = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList<AnalysisColumn> analyzedColumns;
    private String realPath = "";

    public ClosuresMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        realPath = (String) servletContext.getRealPath("/");
    }

    public void reset() {
        renderedBtnAnalysis = true;
        renderedBtnImputation = false;
        renderedBtnReset = false;
        renderedBtnStoreData = false;
        renderedAnalysisResult = false;
        renderedImputationResult = false;
        renderedStoreDataResult = false;

        outputTextAnalysis = "";
        injuriesList = new SelectItem[10];
        variablesData = null;
        currentVariableData = 0;
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM injuries WHERE injury_id != 55");
            for (int i = 0; i < 10; i++) {
                if (rs.next()) {
                    injuriesList[i] = new SelectItem(rs.getShort(1), rs.getString(2));
                    if (rs.getString(2).compareTo("NO INTENCIONAL") == 0) {
                        currentInjury = rs.getShort(1);
                        Date closureDate = rs.getDate("closure_date");
                        currentDateClosure = formato.format(closureDate);

                        DateTime closureTimeDT = new DateTime(closureDate).dayOfMonth().withMinimumValue();
                        startDate = formato.format(closureTimeDT.toDate());//fecha con primer dia del mes

                        closureTimeDT = new DateTime(closureDate).dayOfMonth().withMaximumValue();
                        endDate = formato.format(closureTimeDT.toDate());//fecha con ultimo dia del mes                        

                        closureTimeDT = new DateTime(closureDate).dayOfMonth().withMinimumValue().plusMonths(1);
                        nextDateClosure = formato.format(closureTimeDT.toDate());//fecha con primer dia del mes y un mes aumentado

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

                closureTimeDT = new DateTime(closureDate).dayOfMonth().withMinimumValue().plusMonths(1);
                nextDateClosure = formato.format(closureTimeDT.toDate());//fecha con primer dia del mes y un mes aumentado

                yearBeforeDate = endDate.split("/")[0] + "/" + endDate.split("/")[1] + "/" + String.valueOf(Integer.parseInt(endDate.split("/")[2]) - 1);
                currentInjuryName = rs.getString(2);
            }
        } catch (SQLException | NumberFormatException e) {
        }
    }

    public void startImputation() {
//        outputTextImputation = "";
//        for (int i = 0; i < actionsToImputation.size(); i++) {
//            connectionJdbcMB.non_query(sqlToImputation.get(i));
//            outputTextImputation = outputTextImputation + "</br>" + actionsToImputation.get(i);
//        }
//
//        renderedBtnAnalysis = false;
//        renderedBtnImputation = false;
//        renderedBtnReset = true;
//        renderedBtnStoreData = true;
//        renderedAnalysisResult = true;
//        renderedImputationResult = true;
//        renderedStoreDataResult = false;
    }

    public void startStoreData() {
        try {
            //SE VERIFICA QUE EXISTA UNA COPIA DE SEGURIDAD DE LA BASE DE DATOS -------------------------------------            
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM backups WHERE date(date_backup) >= '" + format_postgres.format(currentDate) + "'");
            if (rs.next()) {

                outputTextStoreData = ""
                        + "Se ha realizado el cierre de <font color=\"blue\"><b>'"
                        + currentInjuryName
                        + "'</b></font> </br>comprendido desde <font color=\"blue\"><b>"
                        + startDate
                        + "</font></b> hasta <font color=\"blue\"><b>"
                        + endDate
                        + "</b></font></br>se almacenaron <font color=\"blue\"><b>"
                        //+ numInserts
                        + "</b></font> registros";
                //actualizo la fecha que se realizo el ultimo cierre en injuries
                connectionJdbcMB.non_query(""
                        + " UPDATE injuries "
                        + " SET closure_date = to_date('" + nextDateClosure + "','dd/MM/yyyy') "
                        + " WHERE injury_id = " + currentInjury);

                renderedBtnAnalysis = false;
                renderedBtnImputation = false;
                renderedBtnReset = true;
                renderedBtnStoreData = false;
                renderedAnalysisResult = true;
                renderedImputationResult = true;
                renderedStoreDataResult = true;
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Para realizar un cierre primero debe existir una copia de seguridad para este dia"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClosuresMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String createStaConsultForCsv(String table, String column, boolean takeNulls) {
        String strReturn = "";
        /*
         * dependiendo de la tabla genera una consulta sql  cuyos registros
         * iran a un archivo csv para la contruccion de el modelo
         */
        switch (ClosuresEnum.convert(table)) {
            case fatal_injury_murder_sta:
                strReturn = ""
                        + " SELECT "
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  numero_victimas_fatales_mismo_hecho,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora,"
                        //+ "  hora_evento,\n"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  clase_lugar_hecho,\n"
                        + "  nivel_alcohol,\n"
                        + "  tipo_arma,\n"
                        + "  contexto_homicidio \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";

                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL ";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL ";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
            case fatal_injury_traffic_sta:
                strReturn = ""
                        + " SELECT \n"
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  numero_victimas_fatales_mismo_hecho,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora,"
                        //+ "  hora_evento,\n"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  clase_lugar_hecho,\n"
                        + "  nivel_alcohol,\n"
                        + "  nivel_alcohol_contraparte,\n"
                        + "  tipo_via_hecho,\n"
                        + "  clase_accidente,\n"
                        + "  caracteristicas_victima,\n"
                        + "  medidas_proteccion,\n"
                        + "  vehiculo_involucrado_victima,\n"
                        + "  vehiculo_involucrado_contraparte,\n"
                        + "  tipo_servicio_vehiculo_victima,\n"
                        + "  tipo_servicio_vehiculo_contraparte \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";
                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL ";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL ";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
            case fatal_injury_suicide_sta:
                strReturn = ""
                        + " SELECT "
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  numero_victimas_fatales_mismo_hecho,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora,"
                        //+ "  hora_evento,\n"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  clase_lugar_hecho,\n"
                        + "  nivel_alcohol,\n"
                        + "  mecanismo_suicidio,\n"
                        + "  intento_previo_suicidio,\n"
                        + "  antecedentes_salud_mental,\n"
                        + "  eventos_relacionados_con_hecho \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";
                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL ";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL ";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
            case fatal_injury_accident_sta:
                strReturn = ""
                        + " SELECT "
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  numero_victimas_fatales_mismo_hecho,\n"
                        + "  numero_victimas_no_fatales_mismo_hecho,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora,"
                        //+ "  hora_evento,\n"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  clase_lugar_hecho,\n"
                        + "  nivel_alcohol,\n"
                        + "  mecanismo_muerte \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";
                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL ";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL ";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
            case non_fatal_interpersonal_sta:
                strReturn = ""
                        + " SELECT "
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  grado_quemadura,\n"
                        + "  porcentaje_quemadura,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        + "  grupo_etnico,\n"
                        + "  aseguradora,\n"
                        + "  grupo_vulnerable,\n"
                        //+ "  fecha_consulta,\n"
                        + "  EXTRACT(MONTH FROM fecha_consulta) AS mes_consulta,\n"
                        + "  EXTRACT(YEAR FROM fecha_consulta) AS anio_consulta,\n"
                        + "  EXTRACT(DOW FROM fecha_consulta) AS dia_semana_consulta,\n"
                        //+ "  hora_consulta,\n"
                        + "  EXTRACT(HOUR FROM hora_consulta) AS hora_consulta,"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes_evento,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio_evento,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana_evento,\n"
                        //+ "  hora_evento,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora_evento,"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  fuente_no_fatal,\n"
                        + "  actividad,\n"
                        + "  mecanismo, \n"
                        + "  lugar_del_hecho, \n"
                        + "  uso_de_alcohol, \n"
                        + "  uso_de_drogas, \n"
                        + "  destino_paciente, \n"
                        + "  diagnostico_1, \n"
                        + "  sitio_anatomico, \n"
                        + "  naturaleza_lesion, \n"
                        + "  antecedentes_previos, \n"
                        + "  relacion_agresor_victima, \n"
                        + "  contexto, \n"
                        + "  genero_agresor \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";
                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL \n";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL \n";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
            case non_fatal_transport_sta:
                strReturn = ""
                        + " SELECT "
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  grado_quemadura,\n"
                        + "  porcentaje_quemadura,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        + "  grupo_etnico,\n"
                        + "  aseguradora,\n"
                        + "  grupo_vulnerable,\n"
                        //+ "  fecha_consulta,\n"
                        + "  EXTRACT(MONTH FROM fecha_consulta) AS mes_consulta,\n"
                        + "  EXTRACT(YEAR FROM fecha_consulta) AS anio_consulta,\n"
                        + "  EXTRACT(DOW FROM fecha_consulta) AS dia_semana_consulta,\n"
                        //+ "  hora_consulta,\n"
                        + "  EXTRACT(HOUR FROM hora_consulta) AS hora_consulta,"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes_evento,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio_evento,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana_evento,\n"
                        //+ "  hora_evento,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora_evento,"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  fuente_no_fatal,\n"
                        + "  actividad,\n"
                        + "  mecanismo, \n"
                        + "  lugar_del_hecho, \n"
                        + "  uso_de_alcohol, \n"
                        + "  uso_de_drogas, \n"
                        + "  destino_paciente, \n"
                        + "  diagnostico_1, \n"
                        + "  sitio_anatomico, \n"
                        + "  naturaleza_lesion, \n"
                        + "  tipo_transporte_lesionado, \n"
                        + "  tipo_transporte_contraparte, \n"
                        + "  tipo_de_usuario, \n"
                        + "  elementos_de_seguridad \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";
                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL \n";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL \n";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
            case non_fatal_self_inflicted_sta:
                strReturn = ""
                        + " SELECT "
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  grado_quemadura,\n"
                        + "  porcentaje_quemadura,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        + "  grupo_etnico,\n"
                        + "  aseguradora,\n"
                        + "  grupo_vulnerable,\n"
                        //+ "  fecha_consulta,\n"
                        + "  EXTRACT(MONTH FROM fecha_consulta) AS mes_consulta,\n"
                        + "  EXTRACT(YEAR FROM fecha_consulta) AS anio_consulta,\n"
                        + "  EXTRACT(DOW FROM fecha_consulta) AS dia_semana_consulta,\n"
                        //+ "  hora_consulta,\n"
                        + "  EXTRACT(HOUR FROM hora_consulta) AS hora_consulta,"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes_evento,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio_evento,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana_evento,\n"
                        //+ "  hora_evento,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora_evento,"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  fuente_no_fatal,\n"
                        + "  actividad,\n"
                        + "  mecanismo, \n"
                        + "  lugar_del_hecho, \n"
                        + "  uso_de_alcohol, \n"
                        + "  uso_de_drogas, \n"
                        + "  destino_paciente, \n"
                        + "  diagnostico_1, \n"
                        + "  sitio_anatomico, \n"
                        + "  naturaleza_lesion, \n"
                        + "  intento_previo, \n"
                        + "  antecedentes_mentales, \n"
                        + "  factores_precipitantes \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";
                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL \n";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL \n";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
            case non_fatal_domestic_violence_sta:
                strReturn = ""
                        + " SELECT "
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  grado_quemadura,\n"
                        + "  porcentaje_quemadura,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        + "  grupo_etnico,\n"
                        + "  aseguradora,\n"
                        + "  grupo_vulnerable,\n"
                        //+ "  fecha_consulta,\n"
                        + "  EXTRACT(MONTH FROM fecha_consulta) AS mes_consulta,\n"
                        + "  EXTRACT(YEAR FROM fecha_consulta) AS anio_consulta,\n"
                        + "  EXTRACT(DOW FROM fecha_consulta) AS dia_semana_consulta,\n"
                        //+ "  hora_consulta,\n"
                        + "  EXTRACT(HOUR FROM hora_consulta) AS hora_consulta,"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes_evento,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio_evento,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana_evento,\n"
                        //+ "  hora_evento,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora_evento,"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  fuente_no_fatal,\n"
                        + "  actividad,\n"
                        + "  mecanismo, \n"
                        + "  lugar_del_hecho, \n"
                        + "  uso_de_alcohol, \n"
                        + "  uso_de_drogas, \n"
                        + "  destino_paciente, \n"
                        + "  diagnostico_1, \n"
                        + "  sitio_anatomico, \n"
                        + "  naturaleza_lesion, \n"
                        + "  tipo_maltrato, \n"
                        + "  tipo_agresor, \n"
                        + "  accion_a_realizar \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";
                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL \n";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL \n";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
            case non_fatal_non_intentional_sta:
                strReturn = ""
                        + " SELECT "
                        + "  edad_victima,\n"
                        + "  mayor_edad,\n"
                        + "  grado_quemadura,\n"
                        + "  porcentaje_quemadura,\n"
                        + "  genero,\n"
                        + "  ocupacion,\n"
                        + "  barrio_residencia,\n"
                        + "  grupo_etnico,\n"
                        + "  aseguradora,\n"
                        + "  grupo_vulnerable,\n"
                        //+ "  fecha_consulta,\n"
                        + "  EXTRACT(MONTH FROM fecha_consulta) AS mes_consulta,\n"
                        + "  EXTRACT(YEAR FROM fecha_consulta) AS anio_consulta,\n"
                        + "  EXTRACT(DOW FROM fecha_consulta) AS dia_semana_consulta,\n"
                        //+ "  hora_consulta,\n"
                        + "  EXTRACT(HOUR FROM hora_consulta) AS hora_consulta,"
                        //+ "  fecha_evento,\n"
                        + "  EXTRACT(MONTH FROM fecha_evento) AS mes_evento,\n"
                        + "  EXTRACT(YEAR FROM fecha_evento) AS anio_evento,\n"
                        + "  EXTRACT(DOW FROM fecha_evento) AS dia_semana_evento,\n"
                        //+ "  hora_evento,\n"
                        + "  EXTRACT(HOUR FROM hora_evento) AS hora_evento,"
                        + "  barrio_evento,\n"
                        + "  cuadrante,\n"
                        + "  fuente_no_fatal,\n"
                        + "  actividad,\n"
                        + "  mecanismo, \n"
                        + "  lugar_del_hecho, \n"
                        + "  uso_de_alcohol, \n"
                        + "  uso_de_drogas, \n"
                        + "  destino_paciente, \n"
                        + "  diagnostico_1, \n"
                        + "  sitio_anatomico, \n"
                        + "  naturaleza_lesion \n"
                        + " FROM \n"
                        + "  " + table + " \n"
                        + " WHERE \n"
                        + "  estado = 3 \n";
                if (!takeNulls) {//se toman los datos donde la columna no sea "null"
                    strReturn = strReturn + " AND " + column + " IS NOT NULL \n";
                } else {//se toman los datos donde la columna sea "null"
                    strReturn = strReturn + " AND " + column + " IS NULL \n";
                }
                strReturn = strReturn + " ORDER BY \n  id_lesion ASC \n";
                break;
        }

        return strReturn;
    }

    private void createCsvFile(String variableName, String sql, String nameFile) {

        //!IMPORTANTE! el cvs debe ser organizado de manera tal que las 
        //columnas numericas queden al inicio para poder aplicar un filtro
        //de valores numericos al modelo

        //intReturn[0] retorna la cantidad de registros adicionados al archivo
        //intReturn[1] retorna el indice de la columna que se esta trabajando;

        ResultSet rs;
        //int[] intReturn = new int[2];
        //intReturn[0] = 0;
        //intReturn[1] = 0;
        //String nameFile = "closures.csv";  // Aqui se le asigna el nombre y la extension al archivo 
        java.io.File folder = new java.io.File(realPath + "web/configurations/maps");
        if (folder.exists()) {
            try {
                //verificar que el directorio exista
                String nameAndPathFile = realPath + "web/configurations/maps/" + nameFile;
                java.io.File ficherofile = new java.io.File(nameAndPathFile);
                if (ficherofile.exists()) {//Lo Borramos
                    ficherofile.delete();//Lo Borramos
                }
                FileWriter fw = new FileWriter(nameAndPathFile);
                BufferedWriter bw = new BufferedWriter(fw);
                try (PrintWriter salArch = new PrintWriter(bw)) {
                    rs = connectionJdbcMB.consult(sql);
                    int columCount = rs.getMetaData().getColumnCount();
                    String line = "";
                    for (int i = 0; i < columCount; i++) {
                        line = line + "\"" + rs.getMetaData().getColumnName(i + 1) + "\",";
                        //if (rs.getMetaData().getColumnName(i + 1).compareTo(variableName) == 0) {
                        //    intReturn[1] = i + 1;//posicion usada en data.setClassIndex() del modelo
                        //}
                    }
                    line = line.substring(0, line.length() - 1);
                    salArch.println(line);//escribir cabecera                    
                    while (rs.next()) {
                        line = "";
                        //intReturn[0]++;
                        for (int i = 0; i < columCount; i++) {
                            if (rs.getString(i + 1) != null) {
                                line = line + "\"" + rs.getString(i + 1) + "\",";
                            } else {
                                line = line + "\"\",";
                            }
                        }
                        line = line.substring(0, line.length() - 1);
                        salArch.println(line);//escribir registro
                    }
                    salArch.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ClosuresMB.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(ClosuresMB.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("No se encuentra la carpeta");
        }
        //return intReturn;
    }

    public void startClosure() {
        /*
         * iniciar con el cierre del periodo
         */
        outputTextAnalysis = "";
        analyzedColumns = new ArrayList<>();
        switch (currentInjury) {
            case 10://;"HOMICIDIO"
                insertFatalInjuryMurderSta();//se ingresa los registros en el rango de fechas
                preProcessCategoryToNull("fatal_injury_murder_sta");//reglas iniciales y conversion a nulos                
                analyzeColumnsOfTableSta("fatal_injury_murder_sta");
                imputeForModeAndAverage("fatal_injury_murder_sta");//imputacion(moda y promedio) 
                imputeForModel("fatal_injury_murder_sta");//imputacion por modelo
                printResultOfImputation();
                copyToDataWarehouse();//se transfiere datos a la bodega
                updateClosureDateInjury();//se acutualiza la fecha del ultimo cierre en la tabla injuries
                deleteDataFromBeforeYear("fatal_injury_murder_sta");//se eliminan los datos de un año antes
                break;
            case 11://;"MUERTE EN ACCIDENTE DE TRANSITO"
                insertFatalInjuryTrafficSta();
                preProcessCategoryToNull("fatal_injury_traffic_sta");//reglas iniciales y conversion a nulos
                analyzeColumnsOfTableSta("fatal_injury_traffic_sta");
                deleteDataFromBeforeYear("fatal_injury_traffic_sta");//se eliminan los datos de un año antes
                break;
            case 12://;"SUICIDIO"
                insertFatalInjurySuicideSta();
                preProcessCategoryToNull("fatal_injury_suicide_sta");//reglas iniciales y conversion a nulos
                analyzeColumnsOfTableSta("fatal_injury_suicide_sta");
                deleteDataFromBeforeYear("fatal_injury_suicide_sta");//se eliminan los datos de un año antes
                break;
            case 13://;"MUERTE ACCIDENTAL"
                insertFatalInjuryAccidentSta();
                preProcessCategoryToNull("fatal_injury_accident_sta");//reglas iniciales y conversion a nulos
                analyzeColumnsOfTableSta("fatal_injury_accident_sta");
                deleteDataFromBeforeYear("fatal_injury_accident_sta");//se eliminan los datos de un año antes
                break;
            case 50://;"VIOLENCIA INTERPERSONAL"
                insertNonFatalInterpersonalSta();
                preProcessCategoryToNull("non_fatal_interpersonal_sta");//reglas iniciales y conversion a nulos
                analyzeColumnsOfTableSta("non_fatal_interpersonal_sta");
                deleteDataFromBeforeYear("non_fatal_interpersonal_sta");//se eliminan los datos de un año antes
                break;
            case 51://;"LESION EN ACCIDENTE DE TRANSITO"
                insertNonFatalTraficcSta();
                preProcessCategoryToNull("non_fatal_transport_sta");//reglas iniciales y conversion a nulos
                analyzeColumnsOfTableSta("non_fatal_transport_sta");
                deleteDataFromBeforeYear("non_fatal_transport_sta");//se eliminan los datos de un año antes                
                break;
            case 52://;"INTENCIONAL AUTOINFLINGIDA"
                insertNonFatalSelfInflictedSta();
                preProcessCategoryToNull("non_fatal_self_inflicted_sta");//reglas iniciales y conversion a nulos
                analyzeColumnsOfTableSta("non_fatal_self_inflicted_sta");
                deleteDataFromBeforeYear("non_fatal_self_inflicted_sta");//se eliminan los datos de un año antes
                break;
            case 53://;"VIOLENCIA INTRAFAMILIAR"                
                insertNonFatalDomesticViolenceSta();
                preProcessCategoryToNull("non_fatal_domestic_violence_sta");//reglas iniciales y conversion a nulos
                analyzeColumnsOfTableSta("non_fatal_domestic_violence_sta");
                deleteDataFromBeforeYear("non_fatal_domestic_violence_sta");//se eliminan los datos de un año antes
                break;
            case 54://;"NO INTENCIONAL"
                insertNonFatalNonIntentionalSta();
                preProcessCategoryToNull("non_fatal_non_intentional_sta");//reglas iniciales y conversion a nulos
                analyzeColumnsOfTableSta("non_fatal_non_intentional_sta");
                deleteDataFromBeforeYear("non_fatal_non_intentional_sta");//se eliminan los datos de un año antes
                break;
            case 56://;"SIVIGILA-VIF"                
                //insertSivigilaSta();
                //preProcessCategoryToNull("sivigila_sta");//reglas iniciales y conversion a nulos
                //imputeTableSta("sivigila_sta");
                //deleteDataFromBeforeYear("sivigila_sta");//se eliminan los datos de un año antes
                break;
        }
        renderedBtnAnalysis = false;
        renderedBtnImputation = true;
        renderedBtnReset = true;
        renderedBtnStoreData = false;
        renderedAnalysisResult = true;
        renderedImputationResult = false;
        renderedStoreDataResult = false;
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Empieza proceso de cierre"));
    }

    private void imputeForModeAndAverage(String table) {
        //imputacion(moda y promedio)         
        for (int i = 0; i < analyzedColumns.size(); i++) {
            if ((int) analyzedColumns.get(i).getNullPercentagePerColumn() > 0 && (int) analyzedColumns.get(i).getNullPercentagePerColumn() <= 10) {
                try {
                    connectionJdbcMB.non_query("\n"
                            + " UPDATE \n"
                            + "  " + table + " \n"
                            + " SET \n"
                            + "  " + analyzedColumns.get(i).getColumnName() + " = '" + analyzedColumns.get(i).getModePerColumn() + "'"
                            + " WHERE \n"
                            + "  " + analyzedColumns.get(i).getColumnName() + " IS NULL ");
                } catch (Exception e) {
                }
            }
        }

    }

    private ArrayList<String> determineIdFromNullData(String table, String column) {
        ArrayList<String> arrayReturn = new ArrayList<>();
        try {
            ResultSet rs = connectionJdbcMB.consult(" "
                    + " SELECT \n"
                    + "   id_lesion \n"
                    + " FROM \n"
                    + "   " + table + "\n"
                    + " WHERE \n"
                    + "   estado = 3 AND " + column + " IS NULL \n"
                    + " ORDER BY \n"
                    + "   id_lesion ASC \n");

            while (rs.next()) {
                arrayReturn.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClosuresMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arrayReturn;
    }

    private void imputeForModel(String table) {
        //-------------------------------------------
        //--- imputacion por modelo -----------------
        //-------------------------------------------
        Imputation imputation;
        ArrayList<Imputed> J48Prediction;
        ArrayList<Imputed> KNNPrediction;
        ArrayList<String> idLesionArray;

        try {
            for (int i = 0; i < analyzedColumns.size(); i++) {
                if ((int) analyzedColumns.get(i).getNullPercentagePerColumn() > 10 && (int) analyzedColumns.get(i).getNullPercentagePerColumn() <= 33) {
                    createCsvFile("", createStaConsultForCsv(table, analyzedColumns.get(i).getColumnName(), true), "null_data.csv");
                    createCsvFile(analyzedColumns.get(i).getColumnName(), createStaConsultForCsv(table, analyzedColumns.get(i).getColumnName(), false), "not_null_data.csv");
                    imputation = new Imputation();
                    imputation.setTrainingSet(realPath + "web/configurations/maps/not_null_data.csv");
                    imputation.setClassIndex(analyzedColumns.get(i).getColumnName());
                    imputation.setPredictionSet(realPath + "web/configurations/maps/null_data.csv");
                    J48Prediction = imputation.runJ48Prediction();
                    KNNPrediction = imputation.runKNNPrediction();
                    idLesionArray = determineIdFromNullData(table, analyzedColumns.get(i).getColumnName());
                    for (int k = 0; k < J48Prediction.size(); k++) {
                        if (J48Prediction.get(k).getConfidence() >= KNNPrediction.get(k).getConfidence()) {
                            connectionJdbcMB.non_query("\n"
                                    + " UPDATE \n"
                                    + "   " + table + "\n"
                                    + " SET \n"
                                    + "   " + analyzedColumns.get(i).getColumnName() + " = '" + J48Prediction.get(k).getPredicted() + "' \n"
                                    + " WHERE \n"
                                    + "   id_lesion LIKE '" + idLesionArray.get(k) + "' \n");
                        } else {
                            connectionJdbcMB.non_query("\n"
                                    + " UPDATE \n"
                                    + "   " + table + "\n"
                                    + " SET \n"
                                    + "   " + analyzedColumns.get(i).getColumnName() + " = '" + KNNPrediction.get(k).getPredicted() + "' \n"
                                    + " WHERE \n"
                                    + "   id_lesion LIKE '" + idLesionArray.get(k) + "'\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void printResultOfImputation() {


        //------------------------------------------------------------------
        //--- IMPRIMO RESULTADOS  ------------------------------------------
        //------------------------------------------------------------------
        outputTextAnalysis = "";
        for (int i = 0; i < analyzedColumns.size(); i++) {
            //NOMBRE DE VARIABLE--------------------------------------------
            outputTextAnalysis = outputTextAnalysis + "<br/> <b>" + analyzedColumns.get(i).getVariableName() + "</b>";
            //PORCENTAJE NULOS----------------------------------------------
            if (analyzedColumns.get(i).getNullPercentagePerColumn() == -1) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"red\"> nulos por columna no determinado </font>";
            } else if ((int) analyzedColumns.get(i).getNullPercentagePerColumn() > -1
                    && (int) analyzedColumns.get(i).getNullPercentagePerColumn() <= 10) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Nulos:</font> <font color=\"green\"><b>" + formatD.format(analyzedColumns.get(i).getNullPercentagePerColumn()) + "%</b> </font>";
            } else if ((int) analyzedColumns.get(i).getNullPercentagePerColumn() > 10
                    && (int) analyzedColumns.get(i).getNullPercentagePerColumn() <= 33) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Nulos:</font> <font color=\"orange\"><b>" + formatD.format(analyzedColumns.get(i).getNullPercentagePerColumn()) + "%</b> </font>";
            } else {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Nulos:</font> <font color=\"red\"><b>" + formatD.format(analyzedColumns.get(i).getNullPercentagePerColumn()) + "%</b> </font>";
            }
            //CANTIDAD DE NULOS---------------------------------------------
            if (analyzedColumns.get(i).getNullCountPerColumn() != -1) {
                outputTextAnalysis = outputTextAnalysis + "<font color=\"gray\"> (" + analyzedColumns.get(i).getNullCountPerColumn() + " veces) </font>";
            }
            //PROMEDIO DE COLUMNA---------------------------------------------
            if (analyzedColumns.get(i).getAveragePerColumn() != -1) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Promedio:  </font> " + formatD.format(analyzedColumns.get(i).getAveragePerColumn());
            }
            //FECHA MINIMA---------------------------------------------
            if (analyzedColumns.get(i).getMinDatePerColumn() != null) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Fecha mínima:  </font> " + analyzedColumns.get(i).getMinDatePerColumn();
            }
            //FECHA MAXIMA---------------------------------------------
            if (analyzedColumns.get(i).getMaxDatePerColumn() != null) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Fecha máxima:  </font> " + analyzedColumns.get(i).getMaxDatePerColumn();
            }
            //NUMERO MINIMO---------------------------------------------
            if (analyzedColumns.get(i).getMinNumberPerColumn() != null) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Número Mínimo:  </font> " + analyzedColumns.get(i).getMinNumberPerColumn();
            }
            //NUMERO MAXIMO---------------------------------------------
            if (analyzedColumns.get(i).getMaxNumberPerColumn() != null) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Número Máximo:  </font> " + analyzedColumns.get(i).getMaxNumberPerColumn();
            }
            //MODA---------------------------------------------
//                if (analyzedColumns.get(i).getModePerColumn() != null) {
//                    outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Moda:  </font> " + analyzedColumns.get(i).getModePerColumn();
//                }
            //CINCO FRECUENTES---------------------------------------------
            if (analyzedColumns.get(i).getFiveFrecuents() != null) {
                outputTextAnalysis = outputTextAnalysis + "<br/> <font color=\"blue\">Frecuentes:  </font> " + analyzedColumns.get(i).getFiveFrecuents();
            }
            outputTextAnalysis = outputTextAnalysis + "<br/>";
        }
    }

    private void copyToDataWarehouse() {
        //se transfiere datos a la bodega
    }

    private void updateClosureDateInjury() {
        //se acutualiza la fecha del ultimo cierre en la tabla injuries
    }

    private void deleteDataFromBeforeYear(String table) {
//            connectionJdbcMB.non_query(""
//                    + " DELETE FROM " + table 
//                    + " WHERE "+ table + ".fecha_evento <= to_date('" + yearBeforeDate + "','dd/MM/yyyy')");
    }

    private void preProcessCategoryToNull(String table) {
        //1. SE CONVIERTE EN NULO AQUELLAS CATEGORIAS: sin dato,sin dato urbano,sin dato rural,ninguno,no se sabe,desconocido
        //2. SE APLICAN REGLAS INICIALES PARA CATEGORIAS DE MULTIPLE OPCION
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + table + " LIMIT 1");
            ResultSet rs2;
            int numColumns = rs.getMetaData().getColumnCount();//determino cantidad de columnas
            //while (rs.next()) {//recorro registros que fueron cargados
            for (int i = 2; i <= numColumns; i++) {//recorro cada columna y realizo el analisis correspondiente
                switch (ClosuresEnum.convert(rs.getMetaData().getColumnName(i))) {//nombre de variable                                                             

                    //----------------------------------------------------------------------
                    //------- COLUMNAS SIN REGLAS INICIALES, Y NO SE TRANSFORMAN EN NULOS --
                    //----------------------------------------------------------------------
                    case id_lesion:
                        break;
                    case estado:
                        break;
                    case tipo_via_hecho://road_types
                        break;
                    case mecanismo_muerte://accident_mechanisms
                        break;
                    case fuente_no_fatal://non_fatal_data_sources
                        break;
                    case destino_paciente://destinations_of_patient
                        break;
                    case diagnostico_1://diagnoses                        
                    case diagnostico_2://no se realiza la imputacion de diagnostco 2 por que se acepta nulo
                        break;
                    case genero:
                        break;
                    case tipo_arma://weapon_types
                        break;
                    case fecha_consulta:
                        break;
                    case fecha_evento:
                        break;
                    case edad_victima://se categorizo a quinquenal
                        break;
                    case mayor_edad:
                        break;
                    case hora_consulta://no construir modelo solo moda
                        break;
                    case hora_evento://no construir modelo solo moda
                        break;
                    case grupo_etnico://ethnic_groups                        
                        break;
                    case numero_victimas_no_fatales_mismo_hecho://nulos se reemplazan por 0, se categoriza a: '0','1','2','3','4 o más'
                        break;
                    case numero_victimas_fatales_mismo_hecho://nulos se reemplazan por 1, se categoriza a: '1','2','3','4 o más', si es menor a 1 se vuelve 1
                        break;
                    //----------------------------------------------------------
                    //---------- REEMPLAZO POR NO APLICA -----------------------
                    //----------------------------------------------------------
                    case grado_quemadura:
                        //GRADO QUEMADURA ES TEXTO NO SE LE APLICA EL FILTRO DE CATEGORIA NUMERICA EN MODELO                        
                        //10. fuego llama humo            //11. objeto liquido caliente
                        //12. polvora                     //19. hidrocarburos
                        //21. minas municion sin explotar //22. otro artefacto explosivo
                        //25. electricidad
                        //si mecanismo no es ninguno de los anteriores: el campo 'grado quemadura' se convierte en NA (no aplica)
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = 'NA' "
                                + " WHERE "
                                + "  mecanismo NOT LIKE '10' AND "
                                + "  mecanismo NOT LIKE '11' AND "
                                + "  mecanismo NOT LIKE '12' AND "
                                + "  mecanismo NOT LIKE '19' AND "
                                + "  mecanismo NOT LIKE '21' AND "
                                + "  mecanismo NOT LIKE '22' AND "
                                + "  mecanismo NOT LIKE '25' AND "
                                + "  estado = 2 ");
                        //1. para imputacion al csv mandamos los que no sean nulos y los que no sean NA
                        //2. porcentaje de nulos se alcula quitando registros que sean NA
                        //3. promedio o moda se determina con los valores que no sean NA
                        break;
                    case porcentaje_quemadura:
                        //1. PRIMERO SE REALIZA LAS REGLAS INICIALES DE grado_quemadura:
                        //2. si grado_quemadura=NA porcentaje_quemadura = -1 (no aplica)
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = -1 "
                                + " WHERE "
                                + " grado_quemadura LIKE 'NA' "
                                + " AND estado = 2 ");
                        //1. para imputacion al csv mandamos los que no sean nulos y los que no sean -1
                        //2. porcentaje de nulos se alcula quitando registros que sean -1
                        //3. promedio o moda se determina con los valores que no sean -1
                        break;
                    //----------------------------------------------------------
                    //---------- RELACIONES DE DE UNO A MUCHOS -----------------
                    //----------------------------------------------------------
                    case grupo_vulnerable://vulnerable_groups ------------------
                        //es valido que venga todos N
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = 'N,N,N,N,N,N,N,N,N,N' "
                                + " WHERE " + rs.getMetaData().getColumnName(i) + " IS NULL AND estado = 2");
                        break;
                    case sitio_anatomico://-------------------------------------                        
                        connectionJdbcMB.non_query(""//volver nulos si todos son N o si solo esta marcado sitio_anatomico_sin_dato = SI
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE "
                                + "  (" + rs.getMetaData().getColumnName(i) + "  LIKE 'N,N,N,N,N,N,N,N,N,N,N,N,N' "
                                + "  OR " + rs.getMetaData().getColumnName(i) + " LIKE 'N,N,N,N,N,N,N,N,N,N,N,N,S' )"
                                + " AND estado = 2");
                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT " + rs.getMetaData().getColumnName(i) + ", id_lesion "
                                + " FROM " + table
                                + " WHERE " + rs.getMetaData().getColumnName(i) + " IS NOT NULL AND estado = 2");
                        try {
                            String[] splitValue;
                            String finalValue;
                            boolean existsYes;
                            while (rs2.next()) {
                                splitValue = rs2.getString(1).split(",");
                                if (splitValue[12].compareTo("S") == 0) {
                                    finalValue = "";
                                    for (int j = 0; j < 12; j++) {
                                        finalValue = finalValue + splitValue[j] + ",";
                                    }
                                    finalValue = finalValue + "N";
                                    connectionJdbcMB.non_query(""//cuando sitio_anatomico_sin_dato = SI  y existe otro sitio_anatomico se cambia sitio_anatomico_sin_dato = NO
                                            + " UPDATE " + table
                                            + " SET " + rs.getMetaData().getColumnName(i) + " = '" + finalValue + "'"
                                            + " WHERE id_lesion LIKE '" + rs2.getString(2) + "'");
                                }
                                if (splitValue[0].compareTo("S") == 0) {
                                    existsYes = false;
                                    for (int j = 1; j < 13; j++) {
                                        if (splitValue[j].compareTo("S") == 0) {
                                            existsYes = true;
                                            break;
                                        }
                                    }
                                    if (existsYes) {
                                        connectionJdbcMB.non_query(""//cuando sitio_anatomico_sistemico = SI  los demas quedan como no
                                                + " UPDATE " + table
                                                + " SET " + rs.getMetaData().getColumnName(i) + " = 'S,N,N,N,N,N,N,N,N,N,N,N,N' "
                                                + " WHERE id_lesion LIKE '" + rs2.getString(2) + "'");

                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error reglas iniciales sitio anatomico " + e.getMessage());
                        }
                        break;
                    case naturaleza_lesion:
                        connectionJdbcMB.non_query(""//si todos son N o solo naturaleza_no_se_sabe=S se vuelve null
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE "
                                + "  (" + rs.getMetaData().getColumnName(i) + "  LIKE 'N,N,N,N,N,N,N,N,N,N,N' "
                                + "  OR " + rs.getMetaData().getColumnName(i) + " LIKE 'N,N,N,N,N,N,N,N,N,N,S' )"
                                + " AND estado = 2");

                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT " + rs.getMetaData().getColumnName(i) + ", id_lesion "
                                + " FROM " + table
                                + " WHERE " + rs.getMetaData().getColumnName(i) + " IS NOT NULL AND estado = 2");
                        try {
                            String[] splitValue;
                            String finalValue;
                            boolean existsYes;
                            while (rs2.next()) {
                                splitValue = rs2.getString(1).split(",");
                                if (splitValue[10].compareTo("S") == 0) {
                                    existsYes = false;
                                    for (int j = 1; j < 10; j++) {
                                        if (splitValue[j].compareTo("S") == 0) {
                                            existsYes = true;
                                            break;
                                        }
                                    }
                                    if (existsYes) {
                                        finalValue = "";
                                        for (int j = 0; j < 10; j++) {
                                            finalValue = finalValue + splitValue[j] + ",";
                                        }
                                        finalValue = finalValue + "N";
                                        connectionJdbcMB.non_query(""//si naturaleza_no_se_sabe=SI y llega otra naturaleza se modifica naturaleza_no_se_sabe=NO
                                                + " UPDATE " + table
                                                + " SET " + rs.getMetaData().getColumnName(i) + " = '" + finalValue + "' "
                                                + " WHERE id_lesion LIKE '" + rs2.getString(2) + "'");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error reglas iniciales naturaleza lesion" + e.getMessage());
                        }
                        break;

                    case tipo_servicio_vehiculo_contraparte:
                    case vehiculo_involucrado_contraparte:
                        //1. primero marco con cero(9) donde no venga ningun "vehiculos involucadros (contraparte)"
                        //2. si todos vienen como 5,5,5 EN "vehiculos involucadros en accidente(contraparte)" queda (9)
                        //3. si viene nulo se vuelve 9
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = '9' "
                                + " WHERE "
                                + " (" + rs.getMetaData().getColumnName(i) + " IS NULL "
                                + " OR " + rs.getMetaData().getColumnName(i) + " LIKE '5'"
                                + " OR " + rs.getMetaData().getColumnName(i) + " LIKE '5,5' "
                                + " OR " + rs.getMetaData().getColumnName(i) + " LIKE '5,5,5'"
                                + " ) AND estado = 2");
                        //si clase de accidente es: '1. colision con objeto movil','5. peaton atropellado,'6. ciclista atropellado'
                        //y viene (9) en "vehiculos involucadros en accidente(contraparte)"(hacer nulo - para poder imputar)
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE "
                                + " ( "
                                + "   clase_accidente LIKE '1' OR "
                                + "   clase_accidente LIKE '5' OR "
                                + "   clase_accidente LIKE '6' "
                                + " ) "
                                + " AND " + rs.getMetaData().getColumnName(i) + " LIKE '9' "
                                + " AND estado = 2");
                        break;
                    case elementos_de_seguridad://non_fatal_transport_security_element                                                
                        connectionJdbcMB.non_query(""//si todos son N o solo elementos_de_seguridad_no_se_sabe = S se vuelve null
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE "
                                + "  (" + rs.getMetaData().getColumnName(i) + "  LIKE 'N,N,N,N,N,N,N' "
                                + "  OR " + rs.getMetaData().getColumnName(i) + " LIKE 'N,N,N,N,N,N,S' )"
                                + " AND estado = 2");
                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT " + rs.getMetaData().getColumnName(i) + ", id_lesion "
                                + " FROM " + table
                                + " WHERE " + rs.getMetaData().getColumnName(i) + " IS NOT NULL AND estado = 2");
                        try {
                            String[] splitValue;
                            String finalValue;
                            boolean existsYes;
                            while (rs2.next()) {
                                splitValue = rs2.getString(1).split(",");
                                if (splitValue[6].compareTo("S") == 0) {
                                    existsYes = false;
                                    for (int j = 0; j < 6; j++) {
                                        if (splitValue[j].compareTo("S") == 0) {
                                            existsYes = true;
                                            break;
                                        }
                                    }
                                    if (existsYes) {
                                        finalValue = "";
                                        for (int j = 0; j < 6; j++) {
                                            finalValue = finalValue + splitValue[j] + ",";
                                        }
                                        finalValue = finalValue + "N";
                                        connectionJdbcMB.non_query(""//si marcan elemento de seguridad "no se uso" y hay otro que sea valido "entonces a no se uso se lo marca como 'no'"
                                                + " UPDATE " + table
                                                + " SET " + rs.getMetaData().getColumnName(i) + " = '" + finalValue + "' "
                                                + " WHERE id_lesion LIKE '" + rs2.getString(2) + "'");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error reglas iniciales elementos seguridad" + e.getMessage());
                        }
                        break;
                    case tipo_maltrato://domestic_violence_abuse_type                                                
                        connectionJdbcMB.non_query(""//si todos son N o solo abuse_type_no_se_sabe=S se vuelve null
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE "
                                + "  (" + rs.getMetaData().getColumnName(i) + "  LIKE 'N,N,N,N,N,N,N,N' "
                                + "  OR " + rs.getMetaData().getColumnName(i) + " LIKE 'N,N,N,N,N,N,S,N' )"
                                + " AND estado = 2");
                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT " + rs.getMetaData().getColumnName(i) + ", id_lesion "
                                + " FROM " + table
                                + " WHERE " + rs.getMetaData().getColumnName(i) + " IS NOT NULL AND estado = 2");
                        try {
                            String[] splitValue;
                            String finalValue;
                            boolean existsYes;
                            while (rs2.next()) {
                                splitValue = rs2.getString(1).split(",");
                                if (splitValue[6].compareTo("S") == 0) {
                                    existsYes = false;
                                    for (int j = 0; j < 7; j++) {
                                        if (j != 6 && splitValue[j].compareTo("S") == 0) {
                                            existsYes = true;
                                            break;
                                        }
                                    }
                                    if (existsYes) {
                                        finalValue = "";
                                        for (int j = 0; j < 7; j++) {
                                            if (j == 6) {
                                                finalValue = finalValue + "N,";
                                            } else {
                                                finalValue = finalValue + splitValue[j] + ",";
                                            }
                                        }
                                        finalValue = finalValue.substring(0, finalValue.length() - 1);
                                        connectionJdbcMB.non_query(""//si marcan tipo_maltrato_sin_dato=S y hay otro que sea valido se cambia tipo_maltrato_sin_dato=N"
                                                + " UPDATE " + table
                                                + " SET " + rs.getMetaData().getColumnName(i) + " = '" + finalValue + "' "
                                                + " WHERE id_lesion LIKE '" + rs2.getString(2) + "'");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error reglas iniciales tipo de maltrato" + e.getMessage());
                        }
                        break;
                    case tipo_agresor://domestic_violence_aggressor_type                        
                        connectionJdbcMB.non_query(""//si todos son N o solo tipo_agresor_no_se_sabe=S se vuelve null
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE "
                                + "  (" + rs.getMetaData().getColumnName(i) + "  LIKE 'N,N,N,N,N,N,N,N,N,N,N,N,N,N,N,N,N,N,N,N,N' "
                                + "  OR " + rs.getMetaData().getColumnName(i) + " LIKE 'N,N,N,N,N,N,N,N,S,N,N,N,N,N,N,N,N,N,N,N,N' )"
                                + " AND estado = 2");
                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT " + rs.getMetaData().getColumnName(i) + ", id_lesion "
                                + " FROM " + table
                                + " WHERE " + rs.getMetaData().getColumnName(i) + " IS NOT NULL AND estado = 2");
                        try {
                            String[] splitValue;
                            String finalValue;
                            boolean existsYes;
                            while (rs2.next()) {
                                splitValue = rs2.getString(1).split(",");
                                if (splitValue[8].compareTo("S") == 0) {
                                    existsYes = false;
                                    for (int j = 0; j < 21; j++) {
                                        if (j != 8 && splitValue[j].compareTo("S") == 0) {
                                            existsYes = true;
                                            break;
                                        }
                                    }
                                    if (existsYes) {
                                        finalValue = "";
                                        for (int j = 0; j < 21; j++) {
                                            if (j == 8) {
                                                finalValue = finalValue + "N,";
                                            } else {
                                                finalValue = finalValue + splitValue[j] + ",";
                                            }
                                        }
                                        finalValue = finalValue.substring(0, finalValue.length() - 1);
                                        connectionJdbcMB.non_query(""//si marcan tipo_maltrato_sin_dato=S y hay otro que sea valido se cambia tipo_maltrato_sin_dato=N"
                                                + " UPDATE " + table
                                                + " SET " + rs.getMetaData().getColumnName(i) + " = '" + finalValue + "' "
                                                + " WHERE id_lesion LIKE '" + rs2.getString(2) + "'");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error reglas iniciales tipo de maltrato" + e.getMessage());
                        }
                        break;
                    case accion_a_realizar://domestic_violence_action_to_take                        
                        connectionJdbcMB.non_query(""//si todos son N o solo accion_a_realizar_sin_dato=S se vuelve null
                                + " UPDATE " + table
                                + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE "
                                + "  (" + rs.getMetaData().getColumnName(i) + "  LIKE 'N,N,N,N,N,N,N,N,N,N,N,N,N' "
                                + "  OR " + rs.getMetaData().getColumnName(i) + " LIKE 'N,N,N,N,N,N,N,N,N,N,N,N,S' )"
                                + " AND estado = 2");
                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT " + rs.getMetaData().getColumnName(i) + ", id_lesion "
                                + " FROM " + table
                                + " WHERE " + rs.getMetaData().getColumnName(i) + " IS NOT NULL AND estado = 2");
                        try {
                            String[] splitValue;
                            String finalValue;
                            boolean existsYes;
                            while (rs2.next()) {
                                splitValue = rs2.getString(1).split(",");
                                if (splitValue[12].compareTo("S") == 0) {
                                    existsYes = false;
                                    for (int j = 0; j < 21; j++) {
                                        if (j != 8 && splitValue[j].compareTo("S") == 0) {
                                            existsYes = true;
                                            break;
                                        }
                                    }
                                    if (existsYes) {
                                        finalValue = "";
                                        for (int j = 0; j < 11; j++) {
                                            finalValue = finalValue + splitValue[j] + ",";
                                        }
                                        finalValue = finalValue + "N";
                                        connectionJdbcMB.non_query(""//si marcan accion_realizar_sin_dato=S y hay otro que sea valido se cambia accion_realizar_sin_dato=N"
                                                + " UPDATE " + table
                                                + " SET " + rs.getMetaData().getColumnName(i) + " = '" + finalValue + "' "
                                                + " WHERE id_lesion LIKE '" + rs2.getString(2) + "'");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error reglas iniciales tipo de maltrato" + e.getMessage());
                        }
                        break;
                    //----------------------------------------------------------
                    //---------- RELACIONES CATEGORICAS CON REMPLAZO -----------
                    //----------------------------------------------------------
                    case uso_de_alcohol://NO SE SABE(9)
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '9' ) AND estado = 2");
                        break;
                    case uso_de_drogas://NO SE SABE(9) A IMPUTAR CONVIERTO EN NULO
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '9' ) AND estado = 2");
                        break;
                    case nivel_alcohol_contraparte://'2. SIN DATO'
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '2' ) AND estado = 2");
                        break;
                    case nivel_alcohol://'2. SIN DATO'
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '2' ) AND estado = 2");
                        break;
                    case ocupacion://jobs //'0. SIN DATO'
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '0' ) AND estado = 2");
                        break;
                    case barrio_evento://neighborhoods
                    case barrio_residencia://neighborhoods //52002 SIN DATO RURAL //52001 SIN DATO URBANO                        
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '52000' "
                                + "       OR " + rs.getMetaData().getColumnName(i) + " LIKE '52001') AND estado = 2");
                        break;
                    case aseguradora://insurance //000001. SIN DATO                        
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE " + rs.getMetaData().getColumnName(i) + " LIKE '000001' AND estado = 2");
                        break;
                    case cuadrante://quadrants //0. SIN DATO
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '0' ) AND estado = 2");
                        break;
                    case actividad://activities //99. NO SE SABE                        
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '99' ) AND estado = 2");
                        break;
                    case mecanismo://mechanisms //28. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '28' ) AND estado = 2");
                        break;
                    case lugar_del_hecho://non_fatal_places //9. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '9' ) AND estado = 2");
                        break;
                    case clase_lugar_hecho://places //9. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '9' ) AND estado = 2");
                        break;
                    case contexto_homicidio://murder_contexts   //'1. DESCONOCIDO'
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '1' ) AND estado = 2");
                        break;
                    case mecanismo_suicidio://suicide_mechanisms //7. SIN DATO                        
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '7' ) AND estado = 2");
                        break;
                    case intento_previo_suicidio://boolean3 //3. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '3' ) AND estado = 2");
                        break;
                    case antecedentes_salud_mental://boolean3 //3. NO SE SABE                        
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '3' ) AND estado = 2");
                        break;
                    case eventos_relacionados_con_hecho://related_events //6. SIN DATO
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '6' ) AND estado = 2");
                        break;
                    case clase_accidente://accident_classes //8. SIN DATO
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '8' ) AND estado = 2");
                        break;
                    case caracteristicas_victima://victim_characteristics //8. SIN DATO
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '8' ) AND estado = 2");
                        break;
                    case medidas_proteccion://protective_measures //7. SIN DATO
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '7' ) AND estado = 2");
                        break;
                    case vehiculo_involucrado_victima://involved_vehicles //5. SIN DATO
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '5' ) AND estado = 2");
                        break;
                    case tipo_servicio_vehiculo_victima://service_types //5. SIN DATO
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '5' ) AND estado = 2");
                        break;
                    case tipo_transporte_lesionado://transport_types //11. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '11' ) AND estado = 2");
                        break;
                    case tipo_transporte_contraparte://transport_counterparts //13. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '13' ) AND estado = 2");
                        break;
                    case tipo_de_usuario://transport_users //9. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '9' ) AND estado = 2");
                        break;
                    case antecedentes_previos://boolean3 //3. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '3' ) AND estado = 2");
                        break;
                    case relacion_agresor_victima://relationships_to_victim //4. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '4' ) AND estado = 2");
                        break;
                    case contexto://contexts //6. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '6' ) AND estado = 2");
                        break;
                    case genero_agresor://aggressor_genders //9. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '9' ) AND estado = 2");
                        break;
                    case intento_previo://boolean3 //3. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '3' ) AND estado = 2");
                        break;
                    case antecedentes_mentales://boolean3 //3. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '3' ) AND estado = 2");
                        break;
                    case factores_precipitantes://precipitating_factors //99. NO SE SABE
                        connectionJdbcMB.non_query(""
                                + " UPDATE " + table + " SET " + rs.getMetaData().getColumnName(i) + " = null "
                                + " WHERE (" + rs.getMetaData().getColumnName(i) + " LIKE '99' ) AND estado = 2");
                        break;
                    case NOVALUE:
                        System.out.println("El nombre de columna (" + rs.getMetaData().getColumnName(i) + ") no se encontro en la enumeración");
                        break;
                    default:
                        System.out.println("El nombre de columna (" + rs.getMetaData().getColumnName(i) + ") no se procesara");
                }
            }
            //se actualiza al estado 3 (se aplico conversion de categorias a nulos Y REGLAS INICIALES)
            connectionJdbcMB.non_query(" UPDATE " + table + " SET estado = 3 WHERE estado = 2");
        } catch (Exception e) {
        }
    }

    private void analyzeColumnsOfTableSta(String table) {
        try {
            //--------------------------------------------------------------------
            //--- REALIZO EL ANALISIS DE CADA COLUMNA
            //--------------------------------------------------------------------            
            AnalysisColumn newAnalysisColumn;
            int numColumns;//numero de columnas de la tabla
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + table + " LIMIT 1");
            numColumns = rs.getMetaData().getColumnCount();//determino cantidad de columnas
            for (int i = 1; i <= numColumns; i++) {//recorro columnas y realizo analisis
                switch (ClosuresEnum.convert(rs.getMetaData().getColumnName(i))) {//nombre de variable                                                             
                    //---------NO SE LES REALIZA ANALISIS------------------------
                    case estado:
                    case id_lesion:
                        break;
                    case fecha_consulta:
                    case fecha_evento:
                        newAnalysisColumn = new AnalysisColumn();
                        newAnalysisColumn.setColumnName(rs.getMetaData().getColumnName(i));
                        newAnalysisColumn.setVariableName(rs.getMetaData().getColumnName(i).replace("_", " "));
                        newAnalysisColumn.setNullPercentagePerColumn(determineNullsPercentagePerColumn(table, rs.getMetaData().getColumnName(i), ""));
                        newAnalysisColumn.setNullCountPerColumn(determineNullsCountPerColumn(table, rs.getMetaData().getColumnName(i), ""));
                        newAnalysisColumn.setMinDatePerColumn(determineMinDateColumn(table, rs.getMetaData().getColumnName(i), ""));
                        newAnalysisColumn.setMaxDatePerColumn(determineMaxDateColumn(table, rs.getMetaData().getColumnName(i), ""));
                        newAnalysisColumn.setFiveFrecuents(determineFiveFrecuents(table, rs.getMetaData().getColumnName(i), ""));
                        analyzedColumns.add(newAnalysisColumn);
                        break;
                    case grado_quemadura:
                        newAnalysisColumn = new AnalysisColumn();
                        newAnalysisColumn.setColumnName(rs.getMetaData().getColumnName(i));
                        newAnalysisColumn.setVariableName(rs.getMetaData().getColumnName(i).replace("_", " "));
                        newAnalysisColumn.setModePerColumn(determineModeColumn(
                                table,
                                rs.getMetaData().getColumnName(i),
                                " AND "
                                + "("
                                + " " + rs.getMetaData().getColumnName(i) + " NOT LIKE 'NA' OR"
                                + " " + rs.getMetaData().getColumnName(i) + " is null "
                                + ") "));
                        newAnalysisColumn.setNullPercentagePerColumn(determineNullsPercentagePerColumn(
                                table,
                                rs.getMetaData().getColumnName(i),
                                " AND "
                                + "("
                                + " " + rs.getMetaData().getColumnName(i) + " NOT LIKE 'NA' OR"
                                + " " + rs.getMetaData().getColumnName(i) + " is null "
                                + ") "));
                        newAnalysisColumn.setNullCountPerColumn(determineNullsCountPerColumn(
                                table,
                                rs.getMetaData().getColumnName(i),
                                " AND "
                                + "("
                                + " " + rs.getMetaData().getColumnName(i) + " NOT LIKE 'NA' OR"
                                + " " + rs.getMetaData().getColumnName(i) + " is null "
                                + ") "));
                        newAnalysisColumn.setFiveFrecuents(determineFiveFrecuents(
                                table,
                                rs.getMetaData().getColumnName(i),
                                " AND "
                                + "("
                                + " " + rs.getMetaData().getColumnName(i) + " NOT LIKE 'NA' OR"
                                + " " + rs.getMetaData().getColumnName(i) + " is null "
                                + ") "));
                        analyzedColumns.add(newAnalysisColumn);
                        break;
                    case porcentaje_quemadura:
                        newAnalysisColumn = new AnalysisColumn();
                        newAnalysisColumn.setColumnName(rs.getMetaData().getColumnName(i));
                        newAnalysisColumn.setVariableName(rs.getMetaData().getColumnName(i).replace("_", " "));
                        newAnalysisColumn.setModePerColumn(determineModeColumn(
                                table,
                                rs.getMetaData().getColumnName(i),
                                " AND "
                                + "("
                                + " " + rs.getMetaData().getColumnName(i) + "::text NOT LIKE '-1' OR"
                                + " " + rs.getMetaData().getColumnName(i) + " is null "
                                + ") "));
                        newAnalysisColumn.setNullPercentagePerColumn(determineNullsPercentagePerColumn(
                                table,
                                rs.getMetaData().getColumnName(i),
                                " AND "
                                + "("
                                + " " + rs.getMetaData().getColumnName(i) + "::text NOT LIKE '-1' OR"
                                + " " + rs.getMetaData().getColumnName(i) + " is null "
                                + ") "));
                        newAnalysisColumn.setNullCountPerColumn(determineNullsCountPerColumn(
                                table,
                                rs.getMetaData().getColumnName(i),
                                " AND "
                                + "("
                                + " " + rs.getMetaData().getColumnName(i) + "::text NOT LIKE '-1' OR"
                                + " " + rs.getMetaData().getColumnName(i) + " is null "
                                + ") "));
                        newAnalysisColumn.setFiveFrecuents(determineFiveFrecuents(
                                table,
                                rs.getMetaData().getColumnName(i),
                                " AND "
                                + "("
                                + " " + rs.getMetaData().getColumnName(i) + "::text NOT LIKE '-1' OR"
                                + " " + rs.getMetaData().getColumnName(i) + " is null "
                                + ") "));
                        analyzedColumns.add(newAnalysisColumn);
                        break;
                    case edad_victima:
                    case mayor_edad:
                    case genero:
                    case ocupacion:
                    case barrio_residencia:
                    case hora_evento:
                    case barrio_evento:
                    case cuadrante:
                    case clase_lugar_hecho:
                    case nivel_alcohol:
                    case numero_victimas_fatales_mismo_hecho:
                    case tipo_arma:
                    case contexto_homicidio:
                    case numero_victimas_no_fatales_mismo_hecho:
                    case mecanismo_muerte:
                    case mecanismo_suicidio:
                    case intento_previo_suicidio:
                    case antecedentes_salud_mental:
                    case eventos_relacionados_con_hecho:
                    case tipo_via_hecho:
                    case clase_accidente:
                    case caracteristicas_victima:
                    case medidas_proteccion:
                    case nivel_alcohol_contraparte:
                    case vehiculo_involucrado_victima:
                    case vehiculo_involucrado_contraparte:
                    case tipo_servicio_vehiculo_victima:
                    case tipo_servicio_vehiculo_contraparte:
                    case grupo_etnico:
                    case aseguradora:
                    case grupo_vulnerable:
                    case hora_consulta:
                    case fuente_no_fatal:
                    case actividad:
                    case mecanismo:
                    case lugar_del_hecho:
                    case uso_de_alcohol:
                    case uso_de_drogas:
                    case destino_paciente:
                    case diagnostico_1:
                    case diagnostico_2:
                    case sitio_anatomico:
                    case naturaleza_lesion:
                    case tipo_transporte_lesionado:
                    case tipo_transporte_contraparte:
                    case tipo_de_usuario:
                    case elementos_de_seguridad:
                    case tipo_maltrato:
                    case tipo_agresor:
                    case accion_a_realizar:
                    case antecedentes_previos:
                    case relacion_agresor_victima:
                    case contexto:
                    case genero_agresor:
                    case intento_previo:
                    case antecedentes_mentales:
                    case factores_precipitantes:
                        newAnalysisColumn = new AnalysisColumn();
                        newAnalysisColumn.setColumnName(rs.getMetaData().getColumnName(i));
                        newAnalysisColumn.setVariableName(rs.getMetaData().getColumnName(i).replace("_", " "));
                        newAnalysisColumn.setModePerColumn(determineModeColumn(table, rs.getMetaData().getColumnName(i), ""));
                        newAnalysisColumn.setNullPercentagePerColumn(determineNullsPercentagePerColumn(table, rs.getMetaData().getColumnName(i), ""));
                        newAnalysisColumn.setNullCountPerColumn(determineNullsCountPerColumn(table, rs.getMetaData().getColumnName(i), ""));
                        newAnalysisColumn.setFiveFrecuents(determineFiveFrecuents(table, rs.getMetaData().getColumnName(i), ""));
                        analyzedColumns.add(newAnalysisColumn);
                        break;
                    case NOVALUE:
                        System.out.println("El nombre de columna (" + rs.getMetaData().getColumnName(i) + ") no se encontro en la enumeración");
                        break;
                    default:
                        System.out.println("El nombre de columna (" + rs.getMetaData().getColumnName(i) + ") no se procesara");
                }
            }

        } catch (Exception e) {
        }
    }

    private void insertNonFatalNonIntentionalSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'NO FATALES NO INTENCIONALES'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * REGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {
            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM non_fatal_non_intentional_sta WHERE estado != 1");
            String sql = ""
                    + " INSERT INTO non_fatal_non_intentional_sta \n"
                    + "SELECT \n"
                    + "	non_fatal_injuries.non_fatal_injury_id,\n"
                    + "	CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + "END AS mayor_edad,"
                    + "	burn_injury_degree,\n"
                    + "	burn_injury_percentage,\n"
                    + "	victims.gender_id,\n"
                    + "	victims.job_id,\n"
                    + "	victims.victim_neighborhood_id,\n"
                    + "	victims.ethnic_group_id,\n"
                    + "	victims.insurance_id,	\n"
                    + "	--(SELECT cast(array_agg(vulnerable_group_id) as text ) FROM victim_vulnerable_group WHERE victim_vulnerable_group.victim_id=victims.victim_id ) as grupo_vulnerable2 ,	\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 1) \n"
                    + "	       WHEN '1' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 2) \n"
                    + "	       WHEN '2' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 3) \n"
                    + "	       WHEN '3' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 4) \n"
                    + "	       WHEN '4' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 5) \n"
                    + "	       WHEN '5' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 6) \n"
                    + "	       WHEN '6' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 13) \n"
                    + "	       WHEN '13' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 14) \n"
                    + "	       WHEN '14' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 16) \n"
                    + "	       WHEN '16' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 98) \n"
                    + "	       WHEN '98' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END AS grupo_vulnerable,	  \n"
                    + "	 checkup_date,\n"
                    + "	 checkup_time,\n"
                    + "	 injury_date,\n"
                    + "	 injury_time,\n"
                    + "	 non_fatal_injuries.injury_neighborhood_id,\n"
                    + "	 non_fatal_injuries.quadrant_id,\n"
                    + "	 non_fatal_injuries.non_fatal_data_source_id,\n"
                    + "	 non_fatal_injuries.activity_id,\n"
                    + "	 non_fatal_injuries.mechanism_id,\n"
                    + "	 non_fatal_injuries.injury_place_id,\n"
                    + "	 non_fatal_injuries.use_alcohol_id,	  \n"
                    + "	 non_fatal_injuries.use_drugs_id,\n"
                    + "	 non_fatal_injuries.destination_patient_id,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1) AS cie_1,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1 OFFSET 1) AS cie_2,\n"
                    + "	 --(SELECT cast(array_agg(anatomical_location_id) as text ) FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id ) as sitio_anatomico,\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "         CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=10) \n"
                    + "	    WHEN '10' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=11) \n"
                    + "	    WHEN '11' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location	 \n"
                    + "	WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS sitio_anatomico,\n"
                    + "	 --(SELECT cast(array_agg(kind_injury_id) as text ) FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id ) as naturaleza_lesion,\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS naturaleza_lesion,\n"
                    + "	 \n"
                    + "	 '2'::int as estado\n"
                    + "   FROM \n"
                    + "       non_fatal_injuries, victims \n"
                    + "   WHERE  \n"
                    + "       non_fatal_injuries.injury_id = 54 AND \n"
                    + "       non_fatal_injuries.victim_id = victims.victim_id AND \n"
                    + "   non_fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "   non_fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "       non_fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "       non_fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }
    }

    private void insertNonFatalInterpersonalSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'NO FATALES INTERPERSONAL'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * rEGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {
            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM non_fatal_interpersonal_sta WHERE estado != 1");
            String sql = ""
                    + "  INSERT INTO non_fatal_interpersonal_sta \n"
                    + "  SELECT \n"
                    + "	 non_fatal_injuries.non_fatal_injury_id,\n"
                    + "	 CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + "  END AS mayor_edad,"
                    + "	 burn_injury_degree,\n"
                    + "	 burn_injury_percentage,\n"
                    + "	 victims.gender_id,\n"
                    + "	 victims.job_id,\n"
                    + "	 victims.victim_neighborhood_id,\n"
                    + "	 victims.ethnic_group_id,\n"
                    + "	 victims.insurance_id,	\n"
                    + "	 --(SELECT cast(array_agg(vulnerable_group_id) as text ) FROM victim_vulnerable_group WHERE victim_vulnerable_group.victim_id=victims.victim_id ) as grupo_vulnerable2 ,	\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 1) \n"
                    + "	       WHEN '1' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 2) \n"
                    + "	       WHEN '2' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 3) \n"
                    + "	       WHEN '3' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 4) \n"
                    + "	       WHEN '4' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 5) \n"
                    + "	       WHEN '5' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 6) \n"
                    + "	       WHEN '6' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 13) \n"
                    + "	       WHEN '13' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 14) \n"
                    + "	       WHEN '14' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 16) \n"
                    + "	       WHEN '16' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 98) \n"
                    + "	       WHEN '98' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END AS grupo_vulnerable,	  \n"
                    + "	 checkup_date,\n"
                    + "	 checkup_time,\n"
                    + "	 injury_date,\n"
                    + "	 injury_time,\n"
                    + "	 non_fatal_injuries.injury_neighborhood_id,\n"
                    + "	 non_fatal_injuries.quadrant_id,\n"
                    + "	 non_fatal_injuries.non_fatal_data_source_id,\n"
                    + "	 non_fatal_injuries.activity_id,\n"
                    + "	 non_fatal_injuries.mechanism_id,\n"
                    + "	 non_fatal_injuries.injury_place_id,\n"
                    + "	 non_fatal_injuries.use_alcohol_id,	  \n"
                    + "	 non_fatal_injuries.use_drugs_id,\n"
                    + "	 non_fatal_injuries.destination_patient_id,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1) AS cie_1,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1 OFFSET 1) AS cie_2,\n"
                    + "	 --(SELECT cast(array_agg(anatomical_location_id) as text ) FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id ) as sitio_anatomico,\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "         CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=10) \n"
                    + "	    WHEN '10' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=11) \n"
                    + "	    WHEN '11' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location	 \n"
                    + "	WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS sitio_anatomico,\n"
                    + "	 --(SELECT cast(array_agg(kind_injury_id) as text ) FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id ) as naturaleza_lesion,\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS naturaleza_lesion,\n"
                    + "	 non_fatal_interpersonal.previous_antecedent,\n"
                    + "	 non_fatal_interpersonal.relationship_victim_id,\n"
                    + "	 non_fatal_interpersonal.context_id,\n"
                    + "	 non_fatal_interpersonal.aggressor_gender_id,\n"
                    + "	 '2'::int as estado\n"
                    + " FROM \n"
                    + "    non_fatal_injuries\n"
                    + "    LEFT JOIN non_fatal_interpersonal USING (non_fatal_injury_id)\n"
                    + "    JOIN victims USING (victim_id)"
                    + " WHERE\n"
                    + "   non_fatal_injuries.injury_id = 50 AND \n"
                    + "   non_fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "   non_fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "   non_fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "   non_fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }
    }

    private void insertNonFatalSelfInflictedSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'NO FATALES AUTOINFLINGIDA'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * REGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {
            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM non_fatal_self_inflicted_sta WHERE estado != 1 ");
            String sql = ""
                    + "  INSERT INTO non_fatal_self_inflicted_sta \n"
                    + "  SELECT \n"
                    + "	 non_fatal_injuries.non_fatal_injury_id,\n"
                    + "	 CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + "  END AS mayor_edad,"
                    + "	 burn_injury_degree,\n"
                    + "	 burn_injury_percentage,\n"
                    + "	 victims.gender_id,\n"
                    + "	 victims.job_id,\n"
                    + "	 victims.victim_neighborhood_id,\n"
                    + "	 victims.ethnic_group_id,\n"
                    + "	 victims.insurance_id,	\n"
                    + "	 --(SELECT cast(array_agg(vulnerable_group_id) as text ) FROM victim_vulnerable_group WHERE victim_vulnerable_group.victim_id=victims.victim_id ) as grupo_vulnerable2 ,	\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 1) \n"
                    + "	       WHEN '1' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 2) \n"
                    + "	       WHEN '2' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 3) \n"
                    + "	       WHEN '3' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 4) \n"
                    + "	       WHEN '4' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 5) \n"
                    + "	       WHEN '5' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 6) \n"
                    + "	       WHEN '6' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 13) \n"
                    + "	       WHEN '13' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 14) \n"
                    + "	       WHEN '14' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 16) \n"
                    + "	       WHEN '16' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 98) \n"
                    + "	       WHEN '98' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END AS grupo_vulnerable,	  \n"
                    + "	 checkup_date,\n"
                    + "	 checkup_time,\n"
                    + "	 injury_date,\n"
                    + "	 injury_time,\n"
                    + "	 non_fatal_injuries.injury_neighborhood_id,\n"
                    + "	 non_fatal_injuries.quadrant_id,\n"
                    + "	 non_fatal_injuries.non_fatal_data_source_id,\n"
                    + "	 non_fatal_injuries.activity_id,\n"
                    + "	 non_fatal_injuries.mechanism_id,\n"
                    + "	 non_fatal_injuries.injury_place_id,\n"
                    + "	 non_fatal_injuries.use_alcohol_id,	  \n"
                    + "	 non_fatal_injuries.use_drugs_id,\n"
                    + "	 non_fatal_injuries.destination_patient_id,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1) AS cie_1,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1 OFFSET 1) AS cie_2,\n"
                    + "	 --(SELECT cast(array_agg(anatomical_location_id) as text ) FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id ) as sitio_anatomico,\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "         CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=10) \n"
                    + "	    WHEN '10' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=11) \n"
                    + "	    WHEN '11' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location	 \n"
                    + "	WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS sitio_anatomico,\n"
                    + "	 --(SELECT cast(array_agg(kind_injury_id) as text ) FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id ) as naturaleza_lesion,\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS naturaleza_lesion,\n"
                    + "	 non_fatal_self_inflicted.previous_attempt,\n"
                    + "	 non_fatal_self_inflicted.mental_antecedent,\n"
                    + "	 non_fatal_self_inflicted.precipitating_factor_id,\n"
                    + "	 '2'::int as estado\n"
                    + " FROM \n"
                    + "    non_fatal_injuries\n"
                    + "    LEFT JOIN non_fatal_self_inflicted USING (non_fatal_injury_id)\n"
                    + "    JOIN victims USING (victim_id)"
                    + " WHERE\n"
                    + "   non_fatal_injuries.injury_id = 52 AND \n"
                    + "   non_fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "   non_fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "   non_fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "   non_fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }
    }

    private void insertNonFatalTraficcSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'NO FATALES EN TRANSITO'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * REGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {
            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM non_fatal_transport_sta WHERE estado != 1 ");
            String sql = ""
                    + " INSERT INTO non_fatal_transport_sta \n"
                    + " SELECT \n"
                    + "	 non_fatal_injuries.non_fatal_injury_id,\n"
                    + "	 CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + "  END AS mayor_edad,"
                    + "	 burn_injury_degree,\n"
                    + "	 burn_injury_percentage,\n"
                    + "	 victims.gender_id,\n"
                    + "	 victims.job_id,\n"
                    + "	 victims.victim_neighborhood_id,\n"
                    + "	 victims.ethnic_group_id,\n"
                    + "	 victims.insurance_id,	\n"
                    + "	 --(SELECT cast(array_agg(vulnerable_group_id) as text ) FROM victim_vulnerable_group WHERE victim_vulnerable_group.victim_id=victims.victim_id ) as grupo_vulnerable2 ,	\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 1) \n"
                    + "	       WHEN '1' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 2) \n"
                    + "	       WHEN '2' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 3) \n"
                    + "	       WHEN '3' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 4) \n"
                    + "	       WHEN '4' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 5) \n"
                    + "	       WHEN '5' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 6) \n"
                    + "	       WHEN '6' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 13) \n"
                    + "	       WHEN '13' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 14) \n"
                    + "	       WHEN '14' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 16) \n"
                    + "	       WHEN '16' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 98) \n"
                    + "	       WHEN '98' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END AS grupo_vulnerable,	  \n"
                    + "	 checkup_date,\n"
                    + "	 checkup_time,\n"
                    + "	 injury_date,\n"
                    + "	 injury_time,\n"
                    + "	 non_fatal_injuries.injury_neighborhood_id,\n"
                    + "	 non_fatal_injuries.quadrant_id,\n"
                    + "	 non_fatal_injuries.non_fatal_data_source_id,\n"
                    + "	 non_fatal_injuries.activity_id,\n"
                    + "	 non_fatal_injuries.mechanism_id,\n"
                    + "	 non_fatal_injuries.injury_place_id,\n"
                    + "	 non_fatal_injuries.use_alcohol_id,	  \n"
                    + "	 non_fatal_injuries.use_drugs_id,\n"
                    + "	 non_fatal_injuries.destination_patient_id,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1) AS cie_1,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1 OFFSET 1) AS cie_2,\n"
                    + "	 --(SELECT cast(array_agg(anatomical_location_id) as text ) FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id ) as sitio_anatomico,\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "         CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=10) \n"
                    + "	    WHEN '10' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=11) \n"
                    + "	    WHEN '11' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location	 \n"
                    + "	 WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS sitio_anatomico,\n"
                    + "	 --(SELECT cast(array_agg(kind_injury_id) as text ) FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id ) as naturaleza_lesion,\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS naturaleza_lesion,\n"
                    + "	 non_fatal_transport.transport_type_id,\n"
                    + "	 non_fatal_transport.transport_counterpart_id,\n"
                    + "	 non_fatal_transport.transport_user_id,\n"
                    + "	 --(SELECT cast(array_agg(security_element_id) as text ) FROM non_fatal_transport_security_element WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_transport_security_element.non_fatal_injury_id ) as naturaleza_lesion,\n"
                    + "	 CASE (SELECT security_element_id FROM non_fatal_transport_security_element WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_transport_security_element.non_fatal_injury_id AND security_element_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT security_element_id FROM non_fatal_transport_security_element WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_transport_security_element.non_fatal_injury_id AND security_element_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT security_element_id FROM non_fatal_transport_security_element WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_transport_security_element.non_fatal_injury_id AND security_element_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT security_element_id FROM non_fatal_transport_security_element WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_transport_security_element.non_fatal_injury_id AND security_element_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT security_element_id FROM non_fatal_transport_security_element WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_transport_security_element.non_fatal_injury_id AND security_element_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT security_element_id FROM non_fatal_transport_security_element WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_transport_security_element.non_fatal_injury_id AND security_element_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT security_element_id FROM non_fatal_transport_security_element WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_transport_security_element.non_fatal_injury_id AND security_element_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS elementos_seguridad,\n"
                    + "	 '2'::int as estado\n"
                    + " FROM \n"
                    + "    non_fatal_injuries\n"
                    + "    LEFT JOIN non_fatal_transport USING (non_fatal_injury_id)\n"
                    + "    JOIN victims USING (victim_id)"
                    + " WHERE\n"
                    + "   non_fatal_injuries.injury_id = 51 AND \n"
                    + "   non_fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "   non_fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "   non_fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "   non_fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }
    }

    private void insertNonFatalDomesticViolenceSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'NO FATALES VIOLENCIA INTRAFAMILIAR'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * REGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {
            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM non_fatal_domestic_violence_sta WHERE estado != 1");
            String sql = ""
                    + " INSERT INTO non_fatal_domestic_violence_sta \n"
                    + "SELECT \n"
                    + "	 non_fatal_injuries.non_fatal_injury_id,\n"
                    + "	 CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + "  END AS mayor_edad,"
                    + "	 burn_injury_degree,\n"
                    + "  burn_injury_percentage,\n"
                    + "	 victims.gender_id,\n"
                    + "	 victims.job_id,\n"
                    + "	 victims.victim_neighborhood_id,\n"
                    + "	 victims.ethnic_group_id,\n"
                    + "	 victims.insurance_id,	\n"
                    + "	 --(SELECT cast(array_agg(vulnerable_group_id) as text ) FROM victim_vulnerable_group WHERE victim_vulnerable_group.victim_id=victims.victim_id ) as grupo_vulnerable2 ,	\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 1) \n"
                    + "	       WHEN '1' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 2) \n"
                    + "	       WHEN '2' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 3) \n"
                    + "	       WHEN '3' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 4) \n"
                    + "	       WHEN '4' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 5) \n"
                    + "	       WHEN '5' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 6) \n"
                    + "	       WHEN '6' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 13) \n"
                    + "	       WHEN '13' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 14) \n"
                    + "	       WHEN '14' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 16) \n"
                    + "	       WHEN '16' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT vulnerable_group_id FROM victim_vulnerable_group WHERE victims.victim_id = victim_vulnerable_group.victim_id AND vulnerable_group_id = 98) \n"
                    + "	       WHEN '98' THEN 'S'  \n"
                    + "	       ELSE 'N'\n"
                    + "	 END AS grupo_vulnerable,	  \n"
                    + "	 checkup_date,\n"
                    + "	 checkup_time,\n"
                    + "	 injury_date,\n"
                    + "	 injury_time,\n"
                    + "	 non_fatal_injuries.injury_neighborhood_id,\n"
                    + "	 non_fatal_injuries.quadrant_id,\n"
                    + "	 non_fatal_injuries.non_fatal_data_source_id,\n"
                    + "	 non_fatal_injuries.activity_id,\n"
                    + "	 non_fatal_injuries.mechanism_id,\n"
                    + "	 non_fatal_injuries.injury_place_id,\n"
                    + "	 non_fatal_injuries.use_alcohol_id,	  \n"
                    + "	 non_fatal_injuries.use_drugs_id,\n"
                    + "	 non_fatal_injuries.destination_patient_id,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1) AS cie_1,	  	 \n"
                    + "	 (SELECT diagnosis_id FROM non_fatal_diagnosis WHERE non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id LIMIT 1 OFFSET 1) AS cie_2,\n"
                    + "	 --(SELECT cast(array_agg(anatomical_location_id) as text ) FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id ) as sitio_anatomico,\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "         CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=10) \n"
                    + "	    WHEN '10' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=11) \n"
                    + "	    WHEN '11' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT anatomical_location_id FROM non_fatal_anatomical_location	 \n"
                    + "	 WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_anatomical_location.non_fatal_injury_id AND anatomical_location_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS sitio_anatomico,\n"
                    + "	 --(SELECT cast(array_agg(kind_injury_id) as text ) FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id ) as naturaleza_lesion,\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=98) \n"
                    + "	    WHEN '98' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT kind_injury_id FROM non_fatal_kind_of_injury WHERE non_fatal_injuries.non_fatal_injury_id = non_fatal_kind_of_injury.non_fatal_injury_id AND kind_injury_id=99) \n"
                    + "	    WHEN '99' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS naturaleza_lesion,\n"
                    + "	 --(SELECT cast(array_agg(abuse_type_id) as text ) FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id ) as tipo_abuso2,\n"
                    + "	 CASE (SELECT abuse_type_id FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id AND abuse_type_id = 1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT abuse_type_id FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id AND abuse_type_id = 2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT abuse_type_id FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id AND abuse_type_id = 3) \n"
                    + "	    WHEN '3'  THEN 'S' \n"
                    + "	    WHEN '9'  THEN 'S' \n"
                    + "	    WHEN '10' THEN 'S' \n"
                    + "	    WHEN '11' THEN 'S' \n"
                    + "	    WHEN '12' THEN 'S' \n"
                    + "	    WHEN '13' THEN 'S' \n"
                    + "	    WHEN '14' THEN 'S' \n"
                    + "	    WHEN '15' THEN 'S' \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT abuse_type_id FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id AND abuse_type_id = 4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT abuse_type_id FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id AND abuse_type_id = 5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT abuse_type_id FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id AND abuse_type_id = 6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT abuse_type_id FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id AND abuse_type_id = 7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT abuse_type_id FROM domestic_violence_abuse_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_abuse_type.non_fatal_injury_id AND abuse_type_id = 8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS tipo_maltrato,\n"
                    + "	 --(SELECT cast(array_agg(aggressor_type_id) as text ) FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id ) as tipo_agresor2,\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 3) \n"
                    + "	    WHEN '3' THEN 'S' \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 10) \n"
                    + "	    WHEN '10' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 11) \n"
                    + "	    WHEN '11' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 13) \n"
                    + "	    WHEN '13' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 14) \n"
                    + "	    WHEN '14' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 15) \n"
                    + "	    WHEN '15' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 16) \n"
                    + "	    WHEN '16' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 17) \n"
                    + "	    WHEN '17' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 18) \n"
                    + "	    WHEN '18' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 19) \n"
                    + "	    WHEN '19' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 20) \n"
                    + "	    WHEN '20' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 21) \n"
                    + "	    WHEN '21' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT aggressor_type_id FROM domestic_violence_aggressor_type WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_aggressor_type.non_fatal_injury_id AND aggressor_type_id = 22) \n"
                    + "	    WHEN '22' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS tipo_agresor,\n"
                    ////----------------
                    + "	 --(SELECT cast(array_agg(action_id) as text ) FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id ) as accion_a_realizar2,\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 1) \n"
                    + "	    WHEN '1' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 2) \n"
                    + "	    WHEN '2' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 3) \n"
                    + "	    WHEN '3' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 4) \n"
                    + "	    WHEN '4' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 5) \n"
                    + "	    WHEN '5' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 6) \n"
                    + "	    WHEN '6' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 7) \n"
                    + "	    WHEN '7' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 8) \n"
                    + "	    WHEN '8' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 9) \n"
                    + "	    WHEN '9' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 10) \n"
                    + "	    WHEN '10' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 11) \n"
                    + "	    WHEN '11' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 12) \n"
                    + "	    WHEN '12' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END ||','||\n"
                    + "	 CASE (SELECT action_id FROM domestic_violence_action_to_take WHERE non_fatal_injuries.non_fatal_injury_id = domestic_violence_action_to_take.non_fatal_injury_id AND action_id = 13) \n"
                    + "	    WHEN '13' THEN 'S'  \n"
                    + "	    ELSE 'N'\n"
                    + "	 END AS accion_a_realizar,\n"
                    ///-------------
                    + "	 '2'::int as estado\n"
                    + " FROM \n"
                    + "    non_fatal_injuries\n"
                    + "    LEFT JOIN non_fatal_domestic_violence USING (non_fatal_injury_id)\n"
                    + "    JOIN victims USING (victim_id)"
                    + " WHERE\n"
                    + "   (injury_id = 53 OR injury_id = 55)   AND \n"
                    + "   non_fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "   non_fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "   non_fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "   non_fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }
    }

    private void insertFatalInjuryMurderSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'FATALES HOMICIDIO'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * REGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {
            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM fatal_injury_murder_sta WHERE estado != 1");
            String sql = ""
                    + " INSERT INTO fatal_injury_murder_sta\n"
                    + " SELECT \n"
                    + "	fatal_injuries.fatal_injury_id,\n"
                    + "	CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + " END AS mayor_edad,"
                    + "	--victim_number,	\n"
                    + " CASE\n"
                    + "	   WHEN ( victim_number IS NULL) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 0) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 1) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 2) THEN '2'  \n"
                    + "	   WHEN ( victim_number = 3) THEN '3'  \n"
                    + "	   WHEN ( victim_number > 3) THEN '4 o más'  \n"
                    + " END AS numero_victimas_fatales_mismo_hecho,"
                    + "	victims.gender_id,\n"
                    + "	victims.job_id,\n"
                    + "	victims.victim_neighborhood_id,\n"
                    + "	injury_date,\n"
                    + "	injury_time,\n"
                    + "	fatal_injuries.injury_neighborhood_id,\n"
                    + "	fatal_injuries.quadrant_id,\n"
                    + "	fatal_injuries.injury_place_id, \n"
                    + "	fatal_injuries.alcohol_level_victim_id,\n"
                    + "	fatal_injury_murder.weapon_type_id,\n"
                    + "	fatal_injury_murder.murder_context_id,\n"
                    + "	 '2'::int as estado\n"
                    + "  FROM \n"
                    + "    fatal_injuries\n"
                    + "    LEFT JOIN fatal_injury_murder USING (fatal_injury_id)\n"
                    + "    JOIN victims USING (victim_id)"
                    + "  WHERE\n"
                    + "       fatal_injuries.injury_id = 10 AND \n"
                    + "       fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "       fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "       fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "       fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }
    }

    private void insertFatalInjurySuicideSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'FATALES SUICIDIO'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * REGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {
            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM fatal_injury_suicide_sta WHERE estado != 1");
            String sql = ""
                    + " INSERT INTO fatal_injury_suicide_sta\n"
                    + " SELECT \n"
                    + "	fatal_injuries.fatal_injury_id,\n"
                    + "	CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + " END AS mayor_edad,"
                    + "	--victim_number,	\n"
                    + " CASE\n"
                    + "	   WHEN ( victim_number IS NULL) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 0) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 1) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 2) THEN '2'  \n"
                    + "	   WHEN ( victim_number = 3) THEN '3'  \n"
                    + "	   WHEN ( victim_number > 3) THEN '4 o más'  \n"
                    + " END AS numero_victimas_fatales_mismo_hecho,"
                    + "	victims.gender_id,\n"
                    + "	victims.job_id,\n"
                    + "	victims.victim_neighborhood_id,\n"
                    + "	injury_date,\n"
                    + "	injury_time,\n"
                    + "	fatal_injuries.injury_neighborhood_id,\n"
                    + "	fatal_injuries.quadrant_id,\n"
                    + "	fatal_injuries.injury_place_id, \n"
                    + "	fatal_injuries.alcohol_level_victim_id,\n"
                    + "	fatal_injury_suicide.suicide_death_mechanism_id,\n"
                    + "	fatal_injury_suicide.previous_attempt,\n"
                    + "	fatal_injury_suicide.mental_antecedent,\n"
                    + "	fatal_injury_suicide.related_event_id,\n"
                    + "	 '2'::int as estado\n"
                    + "  FROM \n"
                    + "    fatal_injuries\n"
                    + "    LEFT JOIN fatal_injury_suicide USING (fatal_injury_id)\n"
                    + "    JOIN victims USING (victim_id)"
                    + "  WHERE\n"
                    + "    fatal_injuries.injury_id = 12 AND \n"
                    + "    fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "    fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "    fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "    fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }
    }

    private void insertFatalInjuryAccidentSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'FATALES ACCIDENTALES'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * REGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {

            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM fatal_injury_accident_sta WHERE estado != 1");
            String sql = ""
                    + " INSERT INTO fatal_injury_accident_sta\n"
                    + " SELECT \n"
                    + "	fatal_injuries.fatal_injury_id,\n"
                    + "	CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + " END AS mayor_edad,"
                    + "	--victim_number,	\n"
                    + " CASE\n"
                    + "	   WHEN ( victim_number IS NULL) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 0) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 1) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 2) THEN '2'  \n"
                    + "	   WHEN ( victim_number = 3) THEN '3'  \n"
                    + "	   WHEN ( victim_number > 3) THEN '4 o más'  \n"
                    + " END AS numero_victimas_fatales_mismo_hecho,"
                    + "	--fatal_injury_accident.number_non_fatal_victims, \n"
                    + " CASE\n"
                    + "	   WHEN ( fatal_injury_accident.number_non_fatal_victims IS NULL) THEN '0'  \n"
                    + "	   WHEN ( fatal_injury_accident.number_non_fatal_victims = 0) THEN '0'  \n"
                    + "	   WHEN ( fatal_injury_accident.number_non_fatal_victims = 1) THEN '1'  \n"
                    + "	   WHEN ( fatal_injury_accident.number_non_fatal_victims = 2) THEN '2'  \n"
                    + "	   WHEN ( fatal_injury_accident.number_non_fatal_victims = 3) THEN '3'  \n"
                    + "	   WHEN ( fatal_injury_accident.number_non_fatal_victims > 3) THEN '4 o más'  \n"
                    + " END AS numero_victimas_no_fatales_mismo_hecho,"
                    + "	victims.gender_id,\n"
                    + "	victims.job_id,\n"
                    + "	victims.victim_neighborhood_id,\n"
                    + "	injury_date,\n"
                    + "	injury_time,\n"
                    + "	fatal_injuries.injury_neighborhood_id,\n"
                    + "	fatal_injuries.quadrant_id,\n"
                    + "	fatal_injuries.injury_place_id, \n"
                    + "	fatal_injuries.alcohol_level_victim_id,\n"
                    + "	fatal_injury_accident.death_mechanism_id,\n"
                    + "	 '2'::int as estado\n"
                    + "  FROM \n"
                    + "    fatal_injuries\n"
                    + "    LEFT JOIN fatal_injury_accident USING (fatal_injury_id)\n"
                    + "    JOIN victims USING (victim_id)"
                    + "  WHERE\n"
                    + "    fatal_injuries.injury_id = 13 AND \n"
                    + "    fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "    fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "    fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "    fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }

    }

    private void insertFatalInjuryTrafficSta() {
        /*
         * SE REGISTRAN LOS DATOS PERTENECIENTES A 'FATALES TRANSITO'
         * QUE ESTEN EN EL RANGO DE FECHAS DADO A LA TABLA STA, EL ESTADO DEL 
         * REGISTRO QUEDA * COMO 2 OSEA FUE CARGADO A TABLA STA
         */
        try {
            connectionJdbcMB.non_query(""//elimino los registros no cargados a la bodega(estado diferente de 1)
                    + " DELETE FROM fatal_injury_traffic_sta WHERE estado != 1");
            String sql = ""
                    + " INSERT INTO fatal_injury_traffic_sta\n"
                    + " SELECT \n"
                    + "	fatal_injuries.fatal_injury_id,\n"
                    + "	CASE\n"
                    + "       WHEN ( victim_age between 0 and 4) THEN '0-4'  \n"
                    + "       WHEN ( victim_age between 5 and 9) THEN '5-9'  \n"
                    + "       WHEN ( victim_age between 10 and 14) THEN '10-14'  \n"
                    + "       WHEN ( victim_age between 15 and 19) THEN '15-19'  \n"
                    + "       WHEN ( victim_age between 20 and 24) THEN '20-24'  \n"
                    + "       WHEN ( victim_age between 25 and 29) THEN '25-29'  \n"
                    + "       WHEN ( victim_age between 30 and 34) THEN '30-34'  \n"
                    + "       WHEN ( victim_age between 35 and 39) THEN '35-39'  \n"
                    + "       WHEN ( victim_age between 40 and 44) THEN '40-44'  \n"
                    + "       WHEN ( victim_age between 45 and 49) THEN '45-49'  \n"
                    + "       WHEN ( victim_age between 50 and 54) THEN '50-54'  \n"
                    + "       WHEN ( victim_age between 55 and 59) THEN '55-59'  \n"
                    + "       WHEN ( victim_age between 60 and 64) THEN '60-64'  \n"
                    + "       WHEN ( victim_age >= 65) THEN '65+'  \n"
                    + "  END AS edad_victima,\n"
                    + "  CASE\n"
                    + "       WHEN ( victim_age <= 17) THEN 'MENOR'  \n"
                    + "       WHEN ( victim_age >= 18) THEN 'MAYOR'  \n"
                    + " END AS mayor_edad,"
                    + "	--victim_number,	\n"
                    + " CASE\n"
                    + "	   WHEN ( victim_number IS NULL) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 0) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 1) THEN '1'  \n"
                    + "	   WHEN ( victim_number = 2) THEN '2'  \n"
                    + "	   WHEN ( victim_number = 3) THEN '3'  \n"
                    + "	   WHEN ( victim_number > 3) THEN '4 o más'  \n"
                    + " END AS numero_victimas_fatales_mismo_hecho,"
                    + "	victims.gender_id,\n"
                    + "	victims.job_id,\n"
                    + "	victims.victim_neighborhood_id,\n"
                    + "	injury_date,\n"
                    + "	injury_time,\n"
                    + "	fatal_injuries.injury_neighborhood_id,\n"
                    + "	fatal_injuries.quadrant_id,\n"
                    + "	fatal_injuries.injury_place_id, \n"
                    + "	fatal_injuries.alcohol_level_victim_id,\n"
                    + "	fatal_injury_traffic.alcohol_level_counterpart_id,\n"
                    + "	fatal_injury_traffic.road_type_id,\n"
                    + "	fatal_injury_traffic.accident_class_id,\n"
                    + "	fatal_injury_traffic.victim_characteristic_id,\n"
                    + "	fatal_injury_traffic.protection_measure_id,\n"
                    + "	fatal_injury_traffic.involved_vehicle_id,\n"
                    + "	( SELECT \n"
                    + "	  REPLACE(REPLACE(cast(array_agg(tmp.involved_vehicle_id) as text ),'{',''),'}','')\n"
                    + "	  FROM (SELECT * FROM counterpart_involved_vehicle ORDER BY involved_vehicle_id) as tmp\n"
                    + "	  WHERE \n"
                    + "	     fatal_injuries.fatal_injury_id = tmp.fatal_injury_id \n"
                    + "	) as vehiculo_involucrado_contraparte,"
                    + "	fatal_injury_traffic.service_type_id,\n"
                    + "	( SELECT \n"
                    + "	  REPLACE(REPLACE(cast(array_agg(tmp.service_type_id) as text ),'{',''),'}','') \n"
                    + "	  FROM (SELECT * FROM counterpart_service_type ORDER BY service_type_id) as tmp	  \n"
                    + "	  WHERE fatal_injuries.fatal_injury_id = tmp.fatal_injury_id \n"
                    + "	) as tipo_servicio_contraparte,"
                    + "	'2'::int as estado\n"
                    + " FROM \n"
                    + "    fatal_injuries\n"
                    + "    LEFT JOIN fatal_injury_traffic USING (fatal_injury_id)\n"
                    + "    JOIN victims USING (victim_id)"
                    + " WHERE\n"
                    + "   fatal_injuries.injury_id = 11 AND \n"
                    + "   fatal_injuries.injury_date >= to_date('01/01/2002','dd/MM/yyyy') AND \n"
                    + "   fatal_injuries.injury_date <= to_date('01/01/2014','dd/MM/yyyy') ";
            //+ "   fatal_injuries.injury_date >= to_date('" + startDate + "','dd/MM/yyyy') AND \n"
            //+ "   fatal_injuries.injury_date <= to_date('" + endDate + "','dd/MM/yyyy') ";
            //System.out.println("\nCONSULTA INSERT\n" + sql + "\n");
            connectionJdbcMB.non_query(sql);//se pasa la información a la tabla           
        } catch (Exception e) {
        }
    }

    private void insertSivigilaSta() {
    }

    public void changeVariableData() {
        outputTextAnalysis = variablesData[currentVariableData].getDescription();
    }

    private double determineAverageColumn(String table, String column, String filter) {
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT AVG(" + column + ") FROM " + table + " WHERE estado = 3 " + filter);
            if (rs.next()) {
                return rs.getDouble(1);
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private String determineMinDateColumn(String table, String column, String filter) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select MIN(" + column + ") from " + table + " WHERE estado = 3 " + filter);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineMaxDateColumn(String table, String column, String filter) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select MAX(" + column + ") from " + table + " WHERE estado = 3 " + filter);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineMinNumberColumn(String table, String column, String filter) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select MIN(" + column + ") from " + table + " WHERE estado = 3 " + filter);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineMaxNumberColumn(String table, String column, String filter) {
        try {
            ResultSet rs = connectionJdbcMB.consult("select MAX(" + column + ") from " + table + " WHERE estado = 3 " + filter);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "sin determinar";
            }
        } catch (Exception e) {
            return "sin determinar";
        }
    }

    private String determineModeColumn(String table, String column, String filter) {
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT " + column + ", COUNT(" + column + ") AS veces\n"
                    + " FROM " + table + "\n"
                    + " WHERE estado = 3 " + filter + "\n"
                    + " GROUP BY " + column + "\n"
                    + " ORDER BY COUNT(" + column + ") DESC\n"
                    + " LIMIT 1");
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
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + table + " WHERE estado = 3 ");
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
                outputTextAnalysis = outputTextAnalysis + "<br/>La fila " + numOfRow + " tiene " + formatD.format(percentageOfNulls) + "% de nulos";
            }
        } catch (Exception e) {
        }
    }

    private double determineNullsPercentagePerColumn(String table, String column, String filter) {
        //table: tabla que se usara
        //column: columna a la que se le realizara el analisis
        //filter: sql para limitar la consulta
        try {
            //determino numero de registros
            ResultSet rs = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + table + " WHERE estado = 3 " + filter);
            rs.next();
            double numOfRows = rs.getDouble(1);
            double percentageOfNulls;
            rs = connectionJdbcMB.consult("select count(*) from " + table + " where " + column + " IS null AND estado = 3 " + filter);
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

    private int determineNullsCountPerColumn(String table, String column, String filter) {
        try {
            //determino numero de registros            
            ResultSet rs = connectionJdbcMB.consult("select count(*) from " + table + " where " + column + " is null AND estado = 3 " + filter);
            rs.next();
            if (rs.getInt(1) == 0) {
                return 0;
            } else {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            return -1;
        }
    }

    private String determineFiveFrecuents(String table, String column, String filter) {
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + table + " WHERE estado = 3 " + filter);
            rs.next();
            double numOfRows = rs.getDouble(1);
            String out = "";
            rs = connectionJdbcMB.consult(""
                    + " SELECT " + column + ", COUNT(" + column + ") AS veces\n"
                    + " FROM " + table + "\n"
                    + " WHERE estado = 3 " + filter + "\n"
                    + " GROUP BY " + column + "\n"
                    + " ORDER BY COUNT(" + column + ") DESC\n"
                    + " LIMIT 5");
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

    public String getOutputTextAnalysis() {
        return outputTextAnalysis;
    }

    public void setOutputTextAnalysis(String outputTextAnalysis) {
        this.outputTextAnalysis = outputTextAnalysis;
    }

    public String getOutputTextImputation() {
        return outputTextImputation;
    }

    public void setOutputTextImputation(String outputTextImputation) {
        this.outputTextImputation = outputTextImputation;
    }

    public String getOutputTextStoreData() {
        return outputTextStoreData;
    }

    public void setOutputTextStoreData(String outputTextStoreData) {
        this.outputTextStoreData = outputTextStoreData;
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

    public boolean isRenderedBtnAnalysis() {
        return renderedBtnAnalysis;
    }

    public void setRenderedBtnAnalysis(boolean renderedBtnAnalysis) {
        this.renderedBtnAnalysis = renderedBtnAnalysis;
    }

    public boolean isRenderedBtnImputation() {
        return renderedBtnImputation;
    }

    public void setRenderedBtnImputation(boolean renderedBtnImputation) {
        this.renderedBtnImputation = renderedBtnImputation;
    }

    public boolean isRenderedBtnReset() {
        return renderedBtnReset;
    }

    public void setRenderedBtnReset(boolean renderedBtnReset) {
        this.renderedBtnReset = renderedBtnReset;
    }

    public boolean isRenderedBtnStoreData() {
        return renderedBtnStoreData;
    }

    public void setRenderedBtnStoreData(boolean renderedBtnStoreData) {
        this.renderedBtnStoreData = renderedBtnStoreData;
    }

    public boolean isRenderedAnalysisResult() {
        return renderedAnalysisResult;
    }

    public void setRenderedAnalysisResult(boolean renderedAnalysisResult) {
        this.renderedAnalysisResult = renderedAnalysisResult;
    }

    public boolean isRenderedImputationResult() {
        return renderedImputationResult;
    }

    public void setRenderedImputationResult(boolean renderedImputationResult) {
        this.renderedImputationResult = renderedImputationResult;
    }

    public boolean isRenderedStoreDataResult() {
        return renderedStoreDataResult;
    }

    public void setRenderedStoreDataResult(boolean renderedStoreDataResult) {
        this.renderedStoreDataResult = renderedStoreDataResult;
    }
}
