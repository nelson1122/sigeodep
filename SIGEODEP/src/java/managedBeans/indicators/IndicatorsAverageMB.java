/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.VariablesEnum;
import static beans.enumerators.VariablesEnum.accident_classes;
import static beans.enumerators.VariablesEnum.accident_mechanisms;
import static beans.enumerators.VariablesEnum.activities;
import static beans.enumerators.VariablesEnum.aggressor_genders;
import static beans.enumerators.VariablesEnum.alcohol_levels;
import static beans.enumerators.VariablesEnum.alcohol_levels_counterparts;
import static beans.enumerators.VariablesEnum.alcohol_levels_victim;
import static beans.enumerators.VariablesEnum.boolean3;
import static beans.enumerators.VariablesEnum.contexts;
import static beans.enumerators.VariablesEnum.counterpart_service_type;
import static beans.enumerators.VariablesEnum.destinations_of_patient;
import static beans.enumerators.VariablesEnum.involved_vehicles;
import static beans.enumerators.VariablesEnum.mechanisms;
import static beans.enumerators.VariablesEnum.murder_contexts;
import static beans.enumerators.VariablesEnum.non_fatal_places;
import static beans.enumerators.VariablesEnum.places;
import static beans.enumerators.VariablesEnum.precipitating_factors;
import static beans.enumerators.VariablesEnum.protective_measures;
import static beans.enumerators.VariablesEnum.related_events;
import static beans.enumerators.VariablesEnum.relationships_to_victim;
import static beans.enumerators.VariablesEnum.road_types;
import static beans.enumerators.VariablesEnum.service_types;
import static beans.enumerators.VariablesEnum.suicide_mechanisms;
import static beans.enumerators.VariablesEnum.transport_counterparts;
import static beans.enumerators.VariablesEnum.transport_types;
import static beans.enumerators.VariablesEnum.transport_users;
import static beans.enumerators.VariablesEnum.use_alcohol_drugs;
import static beans.enumerators.VariablesEnum.victim_characteristics;
import static beans.enumerators.VariablesEnum.weapon_types;
import beans.util.Variable;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import managedBeans.reports.SpanColumns;
import model.dao.IndicatorsConfigurationsFacade;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;
import model.pojo.IndicatorsConfigurations;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.joda.time.Years;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsAverageMB")
@SessionScoped
public class IndicatorsAverageMB {

    @EJB
    IndicatorsFacade indicatorsFacade;
    @EJB
    IndicatorsConfigurationsFacade indicatorsConfigurationsFacade;
    private String currentConfigurationSelected = "";
    private List<String> configurationsList = new ArrayList<String>();
    private String newConfigurationName = "";
    private Indicators currentIndicator;
    private StreamedContent chartImage;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private OutputPanel dynamicDataTableGroup; // Placeholder.
    private FacesMessage message = null;
    private ConnectionJdbcMB connectionJdbcMB;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    private String sql = "";
    //private String currentValueGraph;
    private String currentTemporalDisaggregation;
    private String firstVariablesCrossSelected = null;
    private String initialValue = "";
    private String endValue = "";
    private String dataTableHtml;
    private String[] headers2;//CABECERA 2 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private String[][] matrixResult;//MATRIZ DE RESULTADOS
    private Date initialDate = new Date();
    private Calendar c1 = Calendar.getInstance();
    private Calendar c2 = Calendar.getInstance();
    private Date endDate = new Date();
    private String initialDateStr;
    private String endDateStr;
    private boolean showItems = true;
    private String currentVariableGraph1;
    private String currentVariableGraph2;
    private String currentValueGraph1;
    private String currentValueGraph2;
    private List<String> valuesGraph1 = new ArrayList<String>();
    private List<String> valuesGraph2 = new ArrayList<String>();
    private List<String> temporalDisaggregationTypes = new ArrayList<String>();
    private List<String> variablesList = new ArrayList<String>();//lista de nombres de variables disponibles que sepueden cruzar(se visualizan en pagina)
    private List<String> variablesCrossList = new ArrayList<String>();//ista de nombres de variables que se van a cruzar(se visualizan en pagina)
    private List<String> currentVariablesSelected = new ArrayList<String>();//lista de nombres seleccionados en la lista de variables disponibles
    private List<String> currentVariablesCrossSelected = new ArrayList<String>();//lista de nombres seleccionados en la lista de variables a cruzar    
    private List<String> currentCategoricalValuesList = new ArrayList<String>();
    private List<String> currentCategoricalValuesSelected;
    private ArrayList<SpanColumns> headers1;//CABECERA 1 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private ArrayList<Variable> variablesListData;//lista de variables que tiene el indicador
    private ArrayList<Variable> variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar
    private ArrayList<String> valuesCategoryList;//lista de valores para una categoria
    private ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> rowNames;//NOMBRES DE LAS FILAS    
    DecimalFormat formateador = new DecimalFormat("0.00");
    ArrayList<String> countAppearsStr = new ArrayList<String>();//arreglo para cada dia
    private LoginMB loginMB;
    private Variable currentVariableConfiguring;
    private int numberCross = 2;//maximo numero de variables a cruzar
    private int grandTotal = 0;//total general de la matriz
    private int currentYear = 0;
    private boolean btnExportDisabled = true;
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    private boolean renderedDynamicDataTable = true;
    private boolean colorType = true;
    private boolean showEmpty = false;
    private CopyManager cpManager;
    private StringBuilder sb;
    private int tuplesProcessed;

    public IndicatorsAverageMB() {
        //-------------------------------------------------
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);

        initialDate.setDate(c.get(Calendar.DATE));
        initialDate.setMonth(c.get(Calendar.MONTH));
        initialDate.setYear(2012 - 1900);

        endDate.setDate(c.get(Calendar.DATE));
        endDate.setMonth(c.get(Calendar.MONTH));
        endDate.setYear(c.get(Calendar.YEAR) - 1900);

        temporalDisaggregationTypes = new ArrayList<String>();
        temporalDisaggregationTypes.add("Anual");
        temporalDisaggregationTypes.add("Mensual");
        //temporalDisaggregationTypes.add("Semanal");
        temporalDisaggregationTypes.add("Diaria");
    }

    public void showMessage() {
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void loadValuesGraph() {
        valuesGraph1 = new ArrayList<String>();
        valuesGraph2 = new ArrayList<String>();
        currentVariableGraph1 = "";
        currentVariableGraph2 = "";
        currentValueGraph1 = null;
        currentValueGraph2 = null;

        if (variablesCrossData.size() > 1) {
            currentVariableGraph1 = variablesCrossData.get(1).getName();
            for (int j = 0; j < variablesCrossData.get(1).getValuesConfigured().size(); j++) {
                valuesGraph1.add(variablesCrossData.get(1).getValuesConfigured().get(j));
                currentValueGraph1 = variablesCrossData.get(1).getValuesConfigured().get(j);
            }
        }
        if (variablesCrossData.size() > 2) {
            currentVariableGraph2 = variablesCrossData.get(2).getName();
            for (int j = 0; j < variablesCrossData.get(2).getValuesConfigured().size(); j++) {
                valuesGraph2.add(variablesCrossData.get(2).getValuesConfigured().get(j));
                currentValueGraph2 = variablesCrossData.get(2).getValuesConfigured().get(j);
            }
        }
        createImage();
    }

    private int getDateDifference(Date date1, Date date2, String typeDifference) {
        Interval interval = new Interval(new DateTime(date1), new DateTime(date2));
        if (typeDifference.compareTo("anual") == 0) {
            Years years34 = Years.yearsIn(interval);
            System.out.println("Años" + years34.getYears());
            return years34.getYears();
        } else if (typeDifference.compareTo("mensual") == 0) {
            Months months11 = Months.monthsIn(interval);
            System.out.println("Meses" + months11.getMonths());
            return months11.getMonths();
        } else if (typeDifference.compareTo("diaria") == 0) {
            Days days15 = Days.daysIn(interval);
            System.out.println("Dias" + days15.getDays());
            return days15.getDays();
        }
        return 0;
    }

    public void createMatrixResult() {
        try {//System.out.println("INICIA CREAR MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");     
            columNames = new ArrayList<String>();
            rowNames = new ArrayList<String>();
            ResultSet rs = null;
            //---------------------------------------------------------            
            //DETERMINO NOMBRES DE COLUMNAS PARA MATIRZ SALIDA
            //---------------------------------------------------------            
            if (variablesCrossData.size() < 3) { //una o dos variables
                sql = ""
                        + " SELECT \n"
                        + "    column_1 \n"
                        + " FROM \n"
                        + "    indicators_records \n"
                        + " WHERE \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                        + " GROUP BY \n"
                        + "    column_1 \n"
                        + " ORDER BY \n"
                        + "    MIN(record_id) \n";
                rs = connectionJdbcMB.consult(sql);
            }
            if (variablesCrossData.size() == 3) {
                sql = ""
                        + " SELECT "
                        + "    column_1 ||'}'|| column_2 "
                        + " FROM \n"
                        + "    indicators_records \n"
                        + " WHERE \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                        + " GROUP BY \n"
                        + "    column_1 ||'}'|| column_2 "
                        + " ORDER BY \n"
                        + "    MIN(record_id) \n";
                rs = connectionJdbcMB.consult(sql);
            }
            while (rs.next()) {
                columNames.add(rs.getString(1));
            }
            //---------------------------------------------------------            
            //DETERMINO NOMBRES DE FILAS PARA MATIRZ SALIDA
            //---------------------------------------------------------            
            if (variablesCrossData.size() == 1) {
                rowNames.add("Valor");
            }
            if (variablesCrossData.size() == 2) {
                sql = ""
                        + " SELECT \n"
                        + "    column_2 \n"
                        + " FROM \n"
                        + "    indicators_records \n"
                        + " WHERE \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                        + " GROUP BY \n"
                        + "    column_2 \n"
                        + " ORDER BY \n"
                        + "    MIN(record_id) \n";
                rs = connectionJdbcMB.consult(sql);
            }
            if (variablesCrossData.size() == 3) {
                sql = ""
                        + " SELECT \n"
                        + "    column_3 \n"
                        + " FROM \n"
                        + "    indicators_records \n"
                        + " WHERE \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                        + " GROUP BY \n"
                        + "    column_3 \n"
                        + " ORDER BY \n"
                        + "    MIN(record_id) \n";
                rs = connectionJdbcMB.consult(sql);
            }
            while (rs.next()) {
                rowNames.add(rs.getString(1));
            }
            //---------------------------------------------------------            
            //SE CREA LA MATRIZ DE RESULTADOS (iniciada en 0 )
            //---------------------------------------------------------
            matrixResult = new String[columNames.size()][rowNames.size()];
            for (int i = 0; i < columNames.size(); i++) {
                for (int j = 0; j < rowNames.size(); j++) {
                    matrixResult[i][j] = "0";
                }
            }
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                boolean find = false;
                for (int i = 0; i < columNames.size(); i++) {
                    for (int j = 0; j < rowNames.size(); j++) {
                        if (variablesCrossData.size() == 1) {//ES UNA VARIABLE                            
                            if (rs.getString("column_1").compareTo(columNames.get(i)) == 0) {
                                matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 2) {//SON DOS VARIABLES                            
                            if (rs.getString("column_1").compareTo(columNames.get(i)) == 0 && rs.getString("column_2").compareTo(rowNames.get(j)) == 0) {
                                matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 3) {//SON TRES VARIABLES                            
                            if (columNames.get(i).compareTo(rs.getString("column_1") + "}" + rs.getString("column_2")) == 0 && rs.getString("column_3").compareTo(rowNames.get(j)) == 0) {
                                matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (find) {
                            break;
                        }
                    }
                    if (find) {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }


    private Variable createTemporalDisaggregationVariable(Date initialDate, Date endDate) {
        Variable newVariable = new Variable("Desagregación temporal", "temporalDisaggregation", false, "");
        int diferenceRank;
        int daysMax;
        Calendar cal1 = Calendar.getInstance();
        ArrayList<String> valuesName = new ArrayList<String>();//NOMBRE DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(NOMBRE EN LA CATEGORIA)
        ArrayList<String> valuesId = new ArrayList<String>();  //IDENTIFICADORES DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(ID EN LA CATEGORIA)
        ArrayList<String> valuesConf = new ArrayList<String>();//NOMBRE DE LOS VALORES CONFIGURADOS POR EL USUARIO QUE PUEDE TOMAR LA VARIABLE        
        String iniDateString;
        String endDateString;
        SimpleDateFormat sdf;

        if (currentTemporalDisaggregation.compareTo("Diaria") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, "diaria");
            //System.out.println("(DIFERENCIA EN DIAS) :" + diferenceRank);
            sdf = new SimpleDateFormat("dd MMM yyyy");
            for (int i = 0; i < diferenceRank; i++) {//+1 por que la difrencia no toma el ultimo dia
                cal1.setTime(initialDate);
                cal1.add(Calendar.DATE, i);
                iniDateString = formato.format(cal1.getTime());
                valuesName.add(sdf.format(cal1.getTime()));//agrego el dia en formato: 14 Junio 2013
                valuesId.add(iniDateString + "}" + iniDateString);
                valuesConf.add(sdf.format(cal1.getTime()));
            }
        }
//        if (currentTemporalDisaggregation.compareTo("Semanal") == 0) {
//            diferenceRank = getDateDifference(initialDate, endDate, "semanal");
//            //System.out.println("(SEMANAS EN DIAS) :" + diferenceRank);
//            //sdf = new SimpleDateFormat("dd MMM yyyy");
//            for (int i = 0; i < diferenceRank; i++) {//+1 por que la difrencia no toma el ultimo dia
//                cal1.setTime(initialDate);
//                cal1.add(Calendar.DATE, i * 7);//se aumentan i*7 dias (una semana)
//                iniDateString = formato.format(cal1.getTime());
//                cal1.add(Calendar.DATE, 6);
//                endDateString = formato.format(cal1.getTime());
//                valuesName.add(iniDateString + " a " + endDateString);//agrego el dia en formato: 14 Junio 2013
//                valuesId.add(iniDateString + "}" + endDateString);
//                valuesConf.add(iniDateString + " a " + endDateString);
//            }
//        }
        if (currentTemporalDisaggregation.compareTo("Mensual") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, "mensual");
            //System.out.println("(DIFERENCIA EN MESES) :" + diferenceRank);
            sdf = new SimpleDateFormat("MMM yyyy");
            for (int i = 0; i < diferenceRank; i++) {//+1 por que la difrencia no toma el ultimo mes
                cal1.setTime(initialDate);
                cal1.set(Calendar.DATE, 1);//coloco el dia en 1
                cal1.add(Calendar.MONTH, i);//fecha inicial se la aumenta i meses                
                iniDateString = formato.format(cal1.getTime());
                daysMax = cal1.getActualMaximum(Calendar.DAY_OF_MONTH); // numero maximo de dias del mes
                cal1.set(Calendar.DATE, daysMax);//coloco el dia en el maximo para el mes                
                endDateString = formato.format(cal1.getTime());
                valuesName.add(sdf.format(cal1.getTime()));//agrego el dia en formato: Junio 2013
                valuesId.add(iniDateString + "}" + endDateString);
                valuesConf.add(sdf.format(cal1.getTime()));
            }
        }
        if (currentTemporalDisaggregation.compareTo("Anual") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, "anual");
            //System.out.println("(DIFERENCIA EN AÑOS) :" + diferenceRank);
            sdf = new SimpleDateFormat("yyyy");
            for (int i = 0; i < diferenceRank; i++) {//+1 por que la difrencia no toma el ultimo dia
                cal1.setTime(initialDate);
                cal1.set(Calendar.DATE, 1);//coloco el dia en 1
                cal1.set(Calendar.MONTH, 0);//coloco el mes en enero(0)
                cal1.add(Calendar.YEAR, i);//fecha inicial se la aumenta i años                
                iniDateString = formato.format(cal1.getTime());
                cal1.set(Calendar.DATE, 31);//coloco el dia en 31
                cal1.set(Calendar.MONTH, 11);//coloco el mes en diciembre(11)
                endDateString = formato.format(cal1.getTime());
                valuesName.add(sdf.format(cal1.getTime()));//agrego el dia en formato: Junio 2013
                valuesId.add(iniDateString + "}" + endDateString);
                valuesConf.add(sdf.format(cal1.getTime()));
            }
        }
        newVariable.setValues(valuesName);
        newVariable.setValuesId(valuesId);
        newVariable.setValuesConfigured(valuesConf);
//        for (int i = 0; i < newVariable.getValues().size(); i++) {
//            System.out.println(newVariable.getValues().get(i)+"\t:\t"+newVariable.getValuesId().get(i));
//        }
        return newVariable;
    }

    private boolean validateDateRange() {

        c1.setTime(initialDate);
        c2.setTime(endDate);

        //fecha no puede ser menor a 2002 ni mayor al año del sistema
        if (c1.get(Calendar.YEAR) < 2002 || c1.get(Calendar.YEAR) > currentYear + 1) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial del rango A debe estar entre el año 2002 y " + (currentYear + 1));
            return false;
        }
        if (c2.get(Calendar.YEAR) < 2002 || c2.get(Calendar.YEAR) > currentYear + 1) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha final del rango A debe estar entre el año 2002 y " + (currentYear + 1));
            return false;
        }


        //fecha inicial no sea superior a fecha final
        if (c1.compareTo(c2) > 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial debe ser inferior o igual a la fecha final");
            return false;
        } else {
            initialDateStr = formato.format(initialDate);
            endDateStr = formato.format(endDate);
        }
        //se cumpla con la desagrgacion temporal
        if (currentTemporalDisaggregation.compareTo("Diaria") == 0) {
            if (getDateDifference(initialDate, endDate, "diaria") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango seleccionado deben haber como mínimo 1 dia");
                return false;
            }
        }
//        if (currentTemporalDisaggregation.compareTo("Semanal") == 0) {
//            if (getDateDifference(initialDate, endDate, "semanal") < 1) {
//                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango seleccionado deben haber como mínimo 1 semana");
//                return false;
//            }
//        }
        if (currentTemporalDisaggregation.compareTo("Mensual") == 0) {
            if (getDateDifference(initialDate, endDate, "mensual") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango seleccionado deben haber como mínimo 1 mes");
                return false;
            }
        }
        if (currentTemporalDisaggregation.compareTo("Anual") == 0) {
            if (getDateDifference(initialDate, endDate, "anual") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango seleccionado deben haber como mínimo 1 año ");
                return false;
            }
        }
        return true;
    }

    private void removeEmpty() {
        //------------------------------------------------------------------
        //SE ELIMINAN LOS VALORES VACIOS
        //------------------------------------------------------------------
        sql = ""
                + " DELETE FROM \n\r"
                + "    indicators_records \n\r"
                + " WHERE \n\r"
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "    indicator_id = " + currentIndicator.getIndicatorId() + " AND \n\r"
                + "    count = 0 ";
        connectionJdbcMB.non_query(sql);
    }

    public void process() {
        btnExportDisabled = true;
        variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar            
        message = null;

        boolean continueProcess = validateDateRange();//VALIDACION DE FECHAS        

        if (continueProcess) {
            if (variablesCrossList.size() <= 2) {//NUMERO DE VARIABLES A CRUZAR SEA MENOR O IGUAL AL LIMITE ESTABLECIDO
                continueProcess = true;
            } else {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber " + numberCross + " o menos variables");
            }
        }
        if (continueProcess) {//AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
            variablesCrossData.add(createTemporalDisaggregationVariable(initialDate, endDate));//variable de desagregacion temporal
            for (int j = 0; j < variablesCrossList.size(); j++) {
                for (int i = 0; i < variablesListData.size(); i++) {
                    if (variablesListData.get(i).getName().compareTo(variablesCrossList.get(j)) == 0) {
                        variablesCrossData.add(variablesListData.get(i));
                    }
                }
            }
        }

        if (continueProcess) {//CADA VARIABLE A CRUZAR TENGA VALORES CONFIGURADOS
            for (int i = 0; i < variablesCrossData.size(); i++) {
                if (variablesCrossData.get(i).getValuesConfigured().isEmpty()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La variable " + variablesListData.get(i).getName() + " no tiene valores configurados, para continuar debe ser configurada.");
                    continueProcess = false;
                }
            }
        }

        if (continueProcess) {//ELIMINO DATOS DE UN PROCESO ANTERIOR
            removeIndicatorRecords();
        }
        if (continueProcess) {//ALMACENO EN BASE DE DATOS LOS REGISTROS DE ESTE CRUCE
            saveIndicatorRecords(createIndicatorConsult());
        }
//        if (continueProcess) {//ELIMINO LOS VALORES QUE SEAN CONFIGURADOS POR EL USUARIO
//            removeValuesConfigured();
//        }
        if (continueProcess) {//CREO TODAS LAS POSIBLES COMBINACIONES
            createCombinations();
        }
        if (continueProcess) {//AGRUPO LOS VALORES
            groupingOfValues();
        }
        if (!showEmpty) {
            removeEmpty();
        }
        if (continueProcess) {//MATRIZ DE RESULTADOS
            createMatrixResult();
        }
        if (continueProcess) {//CREO LA TABLA DE RESULTADOS Y EL GRAFICO
            dataTableHtml = createDataTableResult();
            loadValuesGraph();//creo el grafico
            btnExportDisabled = false;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado");
        }
    }

    private void createCombinations() {
        //---------------------------------------------------------
        //FORMAR POSIBLES COMBINACIONES PARA QUE LOS DATOS QUEDEN ORDENADOS SEGUN COMO SE ENCUENTRE LA CONFIGURACION
        //---------------------------------------------------------
        columNames = new ArrayList<String>();
        rowNames = new ArrayList<String>();
        //columnTypeName = new ArrayList<String>();
        try {
            //---------------------------------------------------------
            //CREO NUEVOS VECTORES DE VALORES POR QUE PUEDE SER QUE HAYA QUE AGREGAR EL VALOR 'SIN DATO' QUE NO VIENE POR DEFECTO EN LOS VALORES                        
            //---------------------------------------------------------
            ArrayList<String> values1 = new ArrayList<String>();
            ResultSet rs;
            boolean addNoData;
            if (variablesCrossData.size() > 0) {
                addNoData = true;
                for (int i = 0; i < variablesCrossData.get(0).getValuesConfigured().size(); i++) {
                    values1.add(variablesCrossData.get(0).getValuesConfigured().get(i));
                    if (variablesCrossData.get(0).getValuesConfigured().get(i).compareToIgnoreCase("SIN DATO") == 0) {
                        addNoData = false;//la categoria contiene un valor sin dato
                    }
                }
                if (addNoData) {
                    sql = ""
                            + " SELECT "
                            + "    * "
                            + " FROM "
                            + "    indicators_records "
                            + " WHERE "
                            + "    column_1 like 'SIN DATO' AND "
                            + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                            + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r";
                    rs = connectionJdbcMB.consult(sql);
                    if (rs.next()) {
                        values1.add("SIN DATO");
                    }
                }
            }
            ArrayList<String> values2 = new ArrayList<String>();
            if (variablesCrossData.size() > 1) {
                addNoData = true;
                for (int i = 0; i < variablesCrossData.get(1).getValuesConfigured().size(); i++) {
                    values2.add(variablesCrossData.get(1).getValuesConfigured().get(i));
                    if (variablesCrossData.get(1).getValuesConfigured().get(i).compareToIgnoreCase("SIN DATO") == 0) {
                        addNoData = false;//la categoria contiene un valor sin dato
                    }
                }
                if (addNoData) {
                    sql = ""
                            + " SELECT "
                            + "    * "
                            + " FROM "
                            + "    indicators_records "
                            + " WHERE "
                            + "    column_2 like 'SIN DATO' AND "
                            + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                            + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r";
                    rs = connectionJdbcMB.consult(sql);
                    if (rs.next()) {
                        values2.add("SIN DATO");
                    }
                }
            }
            ArrayList<String> values3 = new ArrayList<String>();
            if (variablesCrossData.size() > 2) {
                addNoData = true;
                for (int i = 0; i < variablesCrossData.get(2).getValuesConfigured().size(); i++) {
                    values3.add(variablesCrossData.get(2).getValuesConfigured().get(i));
                    if (variablesCrossData.get(2).getValuesConfigured().get(i).compareToIgnoreCase("SIN DATO") == 0) {
                        addNoData = false;//la categoria contiene un valor sin dato
                    }
                }
                if (addNoData) {
                    sql = ""
                            + " SELECT "
                            + "    * "
                            + " FROM "
                            + "    indicators_records "
                            + " WHERE "
                            + "    column_3 like 'SIN DATO' AND "
                            + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                            + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r";
                    rs = connectionJdbcMB.consult(sql);
                    if (rs.next()) {
                        values3.add("SIN DATO");
                    }
                }
            }
            //---------------------------------------------------------
            //REALIZO LAS POSIBLES COMBINACIONES
            //---------------------------------------------------------            
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            sb = new StringBuilder();
            tuplesProcessed = 0;
            int id = 0;
            if (variablesCrossData.size() == 1) {
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    sb.
                            append(loginMB.getCurrentUser().getUserId()).append("\t").
                            append(currentIndicator.getIndicatorId()).append("\t").
                            append(id).append("\t").
                            append(values1.get(i)).append("\t").
                            append("-").append("\t").
                            append("-").append("\t").
                            append(0).append("\t").
                            append(0).append("\n");
                    id++;
                }
                rowNames.add("Cantidad");
            } else if (variablesCrossData.size() == 2) {
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    for (int j = 0; j < values2.size(); j++) {
                        if (i == 0) {
                            rowNames.add(values2.get(j));
                        }
                        sb.
                                append(loginMB.getCurrentUser().getUserId()).append("\t").
                                append(currentIndicator.getIndicatorId()).append("\t").
                                append(id).append("\t").
                                append(values1.get(i)).append("\t").
                                append(values2.get(j)).append("\t").
                                append("-").append("\t").
                                append(0).append("\t").
                                append(0).append("\n");
                        id++;
                    }
                }
            } else if (variablesCrossData.size() == 3) {
                for (int i = 0; i < values1.size(); i++) {
                    for (int j = 0; j < values2.size(); j++) {
                        columNames.add(values1.get(i) + "}" + values2.get(j));
                        for (int k = 0; k < values3.size(); k++) {
                            if (i == 0 && j == 0) {
                                rowNames.add(values3.get(k));
                            }
                            sb.
                                    append(loginMB.getCurrentUser().getUserId()).append("\t").
                                    append(currentIndicator.getIndicatorId()).append("\t").
                                    append(id).append("\t").
                                    append(values1.get(i)).append("\t").
                                    append(values2.get(j)).append("\t").
                                    append(values3.get(k)).append("\t").
                                    append(0).append("\t").
                                    append(0).append("\n");
                            id++;
                        }
                    }
                }
            }
            //REALIZO LA INSERCION
            cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
            sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
        } catch (Exception e) {
            System.out.println("EXCEPTION--------------------------" + e.toString());
        }
    }

    private void saveIndicatorRecords(String sqlConsult) {
        //------------------------------------------------------------------
        //AGEGAR UNA CONSULTA A LA TABLA indicators_records 
        //------------------------------------------------------------------
        try {
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            ResultSet rs = connectionJdbcMB.consult(sqlConsult);
            sb = new StringBuilder();
            tuplesProcessed = 0;
            int ncol = rs.getMetaData().getColumnCount();
            boolean haveNulls;
            while (rs.next()) {
                haveNulls = false;
                for (int i = 1; i <= ncol; i++) {//agrego solo los que no tengan valores nulos
                    if (rs.getString(i) == null || rs.getString(i).length() == 0 || rs.getString(i).compareToIgnoreCase("null") == 0) {
                        haveNulls = true;
                    }
                }
                if (!haveNulls) {//si no tiene nullos es agregado
                    tuplesProcessed++;
                    sb.
                            append(loginMB.getCurrentUser().getUserId()).append("\t").
                            append(currentIndicator.getIndicatorId() + 100).append("\t").
                            append(tuplesProcessed).append("\t");

                    for (int i = 1; i <= ncol; i++) {//datos del cruce
                        sb.append(rs.getString(i)).append("\t");
                    }
                    for (int i = 0; i < 3 - ncol; i++) {//variables no usadas(vacias)
                        sb.append("-").append("\t");
                    }
                    sb.append(0).append("\t").append(0).append("\n");//count y poblacion quedan como cero
                }
            }
            //REALIZO LA INSERCION
            cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
            sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
        } catch (Exception e) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void groupingOfValues() {
        //------------------------------------------------------------------
        //SE AGRUPAN LOS VALORES Y SE REALIZA EL CONTEO
        //------------------------------------------------------------------                
        sql = ""
                + " SELECT  \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3, \n\r"
                + "     count(*)  \n\r"
                + " FROM \n\r"
                + "	indicators_records \n\r"
                + " WHERE \n\r"
                + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "     indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r"
                + " GROUP BY \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3 \n\r"
                + " ORDER BY \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3 \n\r";
        ResultSet rs = connectionJdbcMB.consult(sql);
        try {//actualizo el valor count de los registros currentIndicator.getIndicatorId() apartir de  currentIndicator.getIndicatorId()+100
            while (rs.next()) {
                sql = " "
                        + " UPDATE \n\r"
                        + "    indicators_records \n\r"
                        + " SET \n\r"
                        + "    count = " + rs.getString("count") + " \n\r"
                        + " WHERE \n\r"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                        + "    indicator_id = " + currentIndicator.getIndicatorId() + " AND \n\r"
                        + "    column_1 like '" + rs.getString("column_1") + "' AND \n\r"
                        + "    column_2 like '" + rs.getString("column_2") + "' AND \n\r"
                        + "    column_3 like '" + rs.getString("column_3") + "' \n\r";
                connectionJdbcMB.non_query(sql);
            }
            sql = ""
                    + " DELETE FROM \n\r"
                    + "    indicators_records \n\r"
                    + " WHERE \n\r"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r";
            connectionJdbcMB.non_query(sql);//elimino los valores del indicador 100
        } catch (Exception e) {
        }
    }

    private void removeIndicatorRecords() {
        //---------------------------------------------------------        
        //elimino los datos de este indicador
        //---------------------------------------------------------
        sql = ""
                + " DELETE FROM \n\r"
                + "    indicators_records \n\r"
                + " WHERE \n\r"
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "    ( \n\r"
                + "       indicator_id = " + currentIndicator.getIndicatorId() + " OR \n\r" //datos ordenados completos(los que tienen y no tienen conteo )
                + "       indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r" //ocurrencias
                + "    ) \n\r";
        //System.out.println("ELIMINACIONES \n " + sql);
        connectionJdbcMB.non_query(sql);
    }

    private String createIndicatorConsult() {
        String sqlReturn;
        sqlReturn = " SELECT  \n\r";
        String sourceTable = "";//tabla adicional que se usara en la seccion "FROM" de la consulta sql
        String filterSourceTable = "";//filtro adicional usado en la "WHERE" de la consulta sql
        for (int i = 0; i < variablesCrossData.size(); i++) {
            switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable 
                case temporalDisaggregation://DETERMINAR LA DESAGREGACION TEMPORAL -----------------------                   
                    sqlReturn = sqlReturn + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesId().size(); j++) {
                        String[] splitDates = variablesCrossData.get(i).getValuesId().get(j).split("}");
                        sqlReturn = sqlReturn + "       WHEN ( \n\r";
                        sqlReturn = sqlReturn + "           " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + splitDates[0] + "','dd/MM/yyyy') AND \n\r";
                        sqlReturn = sqlReturn + "           " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + splitDates[1] + "','dd/MM/yyyy') \n\r";
                        sqlReturn = sqlReturn + "       ) THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS fecha";
                    break;
                case injuries_fatal://TIPO DE LESION FATAL-----------------------
                    sqlReturn = sqlReturn + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS tipo_lesion";
                    break;
                case injuries_non_fatal://TIPO DE LESION NO FATAL-----------------------
                    sqlReturn = sqlReturn + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS tipo_lesion";
                    break;
                case age://DETERMINAR EDAD -----------------------                   
                    sqlReturn = sqlReturn + "   CASE \n\r";
                    sqlReturn = sqlReturn + "       WHEN (victims.victim_age is null)  THEN 'SIN DATO' \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                        if (splitAge[1].compareTo("n") == 0) {
                            splitAge[1] = "200";
                        }
                        sqlReturn = sqlReturn + ""
                                + "       WHEN (( \n\r"
                                + "           CASE \n\r"
                                + "               WHEN (victims.age_type_id = 2 or victims.age_type_id = 3) THEN 1 \n\r"
                                + "               WHEN (victims.age_type_id = 1) THEN victims.victim_age \n\r"
                                + "           END \n\r"
                                + "       ) between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS edad";
                    break;
                case hour://HORA -----------------------
                    sqlReturn = sqlReturn + ""
                            + "   CASE \n\r"
                            + "       WHEN (" + currentIndicator.getInjuryType() + ".injury_time is null)  THEN 'SIN DATO' \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                        String[] splitAge2 = splitAge[0].split(":");
                        String[] splitAge3 = splitAge[1].split(":");
                        sqlReturn = sqlReturn + ""
                                + "       WHEN (extract(hour from " + currentIndicator.getInjuryType() + ".injury_time) \n\r"
                                + "       between " + splitAge2[0] + " and " + splitAge3[0] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS hora";
                    break;
                case neighborhoods://NOMBRE DEL BARRIO -----------------------
                    sqlReturn = sqlReturn + ""
                            + "   CASE \n\r"
                            + "      WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "      ELSE (SELECT neighborhood_name FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) \n\r"
                            + "   END AS barrio";
                    break;
                case communes://COMUNA -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          communes.commune_name \n\r"
                            + "       FROM \n\r"
                            + "          public.communes, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_suburb = communes.commune_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS comuna";
                    break;
                case quadrants://CUADRANTE -----------------------
                    //sqlReturn = sqlReturn + "   CAST((SELECT neighborhood_quadrant FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as text) as cuadrante \n\r";
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          quadrants.quadrant_name \n\r"
                            + "       FROM \n\r"
                            + "          public.quadrants, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_quadrant = quadrants.quadrant_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS cuadrante";
                    break;
                case corridors://CORREDOR -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          corridors.corridor_name \n\r"
                            + "       FROM \n\r"
                            + "          public.corridors, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_corridor = corridors.corridor_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS corredor";
                    break;
                case areas://ZONA -----------------------        
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          areas.area_name \n\r"
                            + "       FROM \n\r"
                            + "          public.areas, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_area = areas.area_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS zona";
                    break;
                case genders://GENERO  ----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN victims.gender_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          genders.gender_name \n\r"
                            + "       FROM \n\r"
                            + "          genders \n\r"
                            + "       WHERE \n\r"
                            + "          victims.gender_id = genders.gender_id \n\r"
                            + "    )"
                            + " END AS genero";
                    break;
                case days://DIA SEMANA ---------------------
                    //sqlReturn = sqlReturn + "   " + currentIndicator.getInjuryType() + ".injury_day_of_week as dia_semana";
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_day_of_week is null THEN 'SIN DATO' \n\r"
                            + "    ELSE ( " + currentIndicator.getInjuryType() + ".injury_day_of_week )"
                            + " END AS dia_semana";
                    break;
                case year://AÑO -----------------------
                    //sqlReturn = sqlReturn + "   CAST(extract(year from " + currentIndicator.getInjuryType() + ".injury_date)::int as text) as anyo";
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_date is null THEN 'SIN DATO' \n\r"
                            + "    ELSE ( CAST(extract(year from " + currentIndicator.getInjuryType() + ".injury_date)::int as text) )"
                            + " END AS anyo";
                    break;
                case month://MES -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_date is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          months.month_name \n\r"
                            + "       FROM \n\r"
                            + "          public.months \n\r"
                            + "       WHERE \n\r"
                            + "          (extract(month from " + currentIndicator.getInjuryType() + ".injury_date)::int) = months.month_id \n\r"
                            + "    )"
                            + " END AS mes";
                    break;














                case contexts://(interpersonale en comunidad)
                    if (sourceTable.indexOf("non_fatal_interpersonal") == -1) {
                        sourceTable = sourceTable + ", non_fatal_interpersonal ";
                        filterSourceTable = filterSourceTable + " non_fatal_interpersonal.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          contexts.context_name \n\r"
                            + "       FROM \n\r"
                            + "          public.contexts \n\r"
                            + "       WHERE \n\r"
                            + "          context_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS contexto";
                    break;
                case aggressor_genders://(interpersonale en comunidad)
                    if (sourceTable.indexOf("non_fatal_interpersonal") == -1) {
                        sourceTable = sourceTable + ", non_fatal_interpersonal ";
                        filterSourceTable = filterSourceTable + " non_fatal_interpersonal.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          aggressor_genders.gender_name \n\r"
                            + "       FROM \n\r"
                            + "          public.aggressor_genders \n\r"
                            + "       WHERE \n\r"
                            + "          gender_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS sexo_agresor";
                    break;
                case relationships_to_victim://(interpersonale en comunidad)
                    if (sourceTable.indexOf("non_fatal_interpersonal") == -1) {
                        sourceTable = sourceTable + ", non_fatal_interpersonal ";
                        filterSourceTable = filterSourceTable + " non_fatal_interpersonal.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          relationships_to_victim.relationship_victim_name \n\r"
                            + "       FROM \n\r"
                            + "          public.relationships_to_victim \n\r"
                            + "       WHERE \n\r"
                            + "          relationship_victim_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS relacion_agresor_victima";
                    break;
                case precipitating_factors://(violencia autoinflingida)
                    if (sourceTable.indexOf("non_fatal_self_inflicted") == -1) {
                        sourceTable = sourceTable + ", non_fatal_self_inflicted ";
                        filterSourceTable = filterSourceTable + " non_fatal_self_inflicted.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          precipitating_factors.precipitating_factor_name \n\r"
                            + "       FROM \n\r"
                            + "          public.precipitating_factors \n\r"
                            + "       WHERE \n\r"
                            + "          precipitating_factor_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS factor_precipitante";
                    break;
                case transport_users://(accidentes transito)
                    if (sourceTable.indexOf("non_fatal_transport") == -1) {
                        sourceTable = sourceTable + ", non_fatal_transport ";
                        filterSourceTable = filterSourceTable + " non_fatal_transport.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          transport_users.transport_user_name \n\r"
                            + "       FROM \n\r"
                            + "          public.transport_users \n\r"
                            + "       WHERE \n\r"
                            + "          transport_user_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS tipo_usuario";
                    break;
                case transport_counterparts://(accidentes transito)
                    if (sourceTable.indexOf("non_fatal_transport") == -1) {
                        sourceTable = sourceTable + ", non_fatal_transport ";
                        filterSourceTable = filterSourceTable + " non_fatal_transport.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          transport_counterparts.transport_counterpart_name \n\r"
                            + "       FROM \n\r"
                            + "          public.transport_counterparts \n\r"
                            + "       WHERE \n\r"
                            + "          transport_counterpart_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS tipo_transporte_contraparte";
                    break;
                case transport_types://(accidentes transito)
                    if (sourceTable.indexOf("non_fatal_transport") == -1) {
                        sourceTable = sourceTable + ", non_fatal_transport ";
                        filterSourceTable = filterSourceTable + " non_fatal_transport.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          transport_types.transport_type_name \n\r"
                            + "       FROM \n\r"
                            + "          public.transport_types \n\r"
                            + "       WHERE \n\r"
                            + "          transport_type_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS tipo_transporte_victima";
                    break;
                case destinations_of_patient://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          destinations_of_patient.destination_patient_name \n\r"
                            + "       FROM \n\r"
                            + "          public.destinations_of_patient \n\r"
                            + "       WHERE \n\r"
                            + "          destination_patient_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS destino_paciente";
                    break;
                case activities://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          activities.activity_name \n\r"
                            + "       FROM \n\r"
                            + "          public.activities \n\r"
                            + "       WHERE \n\r"
                            + "          activity_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS actividad_realizada";
                    break;
                case non_fatal_places://(Violencia Interpersonal en Familia)(lesiones en transporte)(interpersonal en comunidad)
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          non_fatal_places.non_fatal_place_name \n\r"
                            + "       FROM \n\r"
                            + "          public.non_fatal_places \n\r"
                            + "       WHERE \n\r"
                            + "          non_fatal_place_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS lugar_hecho";
                    break;
                case use_alcohol_drugs://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          use_alcohol_drugs.use_alcohol_drugs_name \n\r"
                            + "       FROM \n\r"
                            + "          public.use_alcohol_drugs \n\r"
                            + "       WHERE \n\r"
                            + "          use_alcohol_drugs_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )";
                    if (variablesCrossData.get(i).getSource_table().indexOf("use_alcohol_id") != -1) {
                        sqlReturn = sqlReturn + " END AS uso_alcohol";
                    }
                    if (variablesCrossData.get(i).getSource_table().indexOf("use_drugs_id") != -1) {
                        sqlReturn = sqlReturn + " END AS uso_drogas";
                    }
                    break;
                case mechanisms://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          mechanisms.mechanism_name \n\r"
                            + "       FROM \n\r"
                            + "          public.mechanisms \n\r"
                            + "       WHERE \n\r"
                            + "          mechanism_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS mecanismo";
                    break;
                case protective_measures://(transito)
                    if (sourceTable.indexOf("fatal_injury_traffic") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_traffic ";
                        filterSourceTable = filterSourceTable + " fatal_injury_traffic.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          protective_measures.protective_measures_name \n\r"
                            + "       FROM \n\r"
                            + "          public.protective_measures \n\r"
                            + "       WHERE \n\r"
                            + "          protective_measures_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS medidas_proteccion";
                    break;
                case victim_characteristics://(transito)
                    if (sourceTable.indexOf("fatal_injury_traffic") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_traffic ";
                        filterSourceTable = filterSourceTable + " fatal_injury_traffic.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          victim_characteristics.characteristic_name \n\r"
                            + "       FROM \n\r"
                            + "          public.victim_characteristics \n\r"
                            + "       WHERE \n\r"
                            + "          characteristic_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS caracteristicas_victima";
                    break;
                case road_types://(transito)
                    if (sourceTable.indexOf("fatal_injury_traffic") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_traffic ";
                        filterSourceTable = filterSourceTable + " fatal_injury_traffic.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          road_types.road_type_name \n\r"
                            + "       FROM \n\r"
                            + "          public.road_types \n\r"
                            + "       WHERE \n\r"
                            + "          road_type_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS tipo_via";
                    break;
                case counterpart_service_type://(transito)
                    sqlReturn = sqlReturn + "   CASE (SELECT service_type_id  FROM counterpart_service_type  WHERE fatal_injury_id=" + currentIndicator.getInjuryType() + ".fatal_injury_id LIMIT 1)  \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "       ELSE 'SIN DATO' \n\r END AS servicio_contraparte";
                    break;
                case service_types://(transito)
                    if (sourceTable.indexOf("fatal_injury_traffic") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_traffic ";
                        filterSourceTable = filterSourceTable + " fatal_injury_traffic.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          service_types.service_type_name \n\r"
                            + "       FROM \n\r"
                            + "          public.service_types \n\r"
                            + "       WHERE \n\r"
                            + "          service_type_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS servicio_victima";
                    break;
                case involved_vehicles://(transito)
                    //contraparte
                    if (variablesCrossData.get(i).getSource_table().indexOf("counterpart_involved_vehicle.involved_vehicle_id") != -1) {
                        sqlReturn = sqlReturn + "   CASE (SELECT involved_vehicle_id  FROM counterpart_involved_vehicle  WHERE fatal_injury_id=" + currentIndicator.getInjuryType() + ".fatal_injury_id LIMIT 1)  \n\r";
                        for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                            sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                        }
                        sqlReturn = sqlReturn + "       ELSE 'SIN DATO' \n\r END AS vehiculo_contraparte";
                    }
                    //victima
                    if (variablesCrossData.get(i).getSource_table().indexOf("fatal_injury_traffic.involved_vehicle_id") != -1) {
                        if (sourceTable.indexOf("fatal_injury_traffic") == -1) {
                            sourceTable = sourceTable + ", fatal_injury_traffic ";
                            filterSourceTable = filterSourceTable + " fatal_injury_traffic.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                        }
                        sqlReturn = sqlReturn + ""
                                + " CASE \n\r"
                                + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                                + "    ELSE \n\r"
                                + "    ( \n\r"
                                + "       SELECT \n\r"
                                + "          involved_vehicles.involved_vehicle_name \n\r"
                                + "       FROM \n\r"
                                + "          public.involved_vehicles \n\r"
                                + "       WHERE \n\r"
                                + "          involved_vehicle_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                                + "    )"
                                + " END AS vehiculo_victima";

                    }
                    break;
                case accident_classes://(transito)
                    if (sourceTable.indexOf("fatal_injury_traffic") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_traffic ";
                        filterSourceTable = filterSourceTable + " fatal_injury_traffic.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          accident_classes.accident_class_name \n\r"
                            + "       FROM \n\r"
                            + "          public.accident_classes \n\r"
                            + "       WHERE \n\r"
                            + "          accident_class_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS clase_accidente";
                    break;
                case boolean3:
                    //(suicidio)
                    if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_suicide.previous_attempt") == 0
                            || variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_suicide.mental_antecedent") == 0) {
                        if (sourceTable.indexOf("fatal_injury_suicide") == -1) {
                            sourceTable = sourceTable + ", fatal_injury_suicide ";
                            filterSourceTable = filterSourceTable + " fatal_injury_suicide.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                        }
                        sqlReturn = sqlReturn + ""
                                + " CASE \n\r"
                                + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                                + "    ELSE \n\r"
                                + "    ( \n\r"
                                + "       SELECT \n\r"
                                + "          boolean3.boolean_name \n\r"
                                + "       FROM \n\r"
                                + "          public.boolean3 \n\r"
                                + "       WHERE \n\r"
                                + "          boolean_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                                + "    )";
                        if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_suicide.previous_attempt") == 0) {
                            sqlReturn = sqlReturn + " END AS intento_previo";
                        }
                        if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_suicide.mental_antecedent") == 0) {
                            sqlReturn = sqlReturn + " END AS antecedentes_mentales";
                        }

                    }
                    //(autoinflingida)
                    if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_self_inflicted.previous_attempt") == 0
                            || variablesCrossData.get(i).getSource_table().compareTo("non_fatal_self_inflicted.mental_antecedent") == 0) {
                        if (sourceTable.indexOf("non_fatal_self_inflicted") == -1) {
                            sourceTable = sourceTable + ", non_fatal_self_inflicted ";
                            filterSourceTable = filterSourceTable + " non_fatal_self_inflicted.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                        }
                        sqlReturn = sqlReturn + ""
                                + " CASE \n\r"
                                + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                                + "    ELSE \n\r"
                                + "    ( \n\r"
                                + "       SELECT \n\r"
                                + "          boolean3.boolean_name \n\r"
                                + "       FROM \n\r"
                                + "          public.boolean3 \n\r"
                                + "       WHERE \n\r"
                                + "          boolean_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                                + "    )";
                        if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_self_inflicted.previous_attempt") == 0) {
                            sqlReturn = sqlReturn + " END AS intento_previo";
                        }
                        if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_self_inflicted.mental_antecedent") == 0) {
                            sqlReturn = sqlReturn + " END AS antecedentes_mentales";
                        }
                    }
                    //(interpersonal en comunidad)
                    if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_interpersonal.previous_antecedent") == 0) {
                        if (sourceTable.indexOf("non_fatal_interpersonal") == -1) {
                            sourceTable = sourceTable + ", non_fatal_interpersonal ";
                            filterSourceTable = filterSourceTable + " non_fatal_interpersonal.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
                        }
                        sqlReturn = sqlReturn + ""
                                + " CASE \n\r"
                                + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                                + "    ELSE \n\r"
                                + "    ( \n\r"
                                + "       SELECT \n\r"
                                + "          boolean3.boolean_name \n\r"
                                + "       FROM \n\r"
                                + "          public.boolean3 \n\r"
                                + "       WHERE \n\r"
                                + "          boolean_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                                + "    )"
                                + " END AS antecedente_previo";
                    }
                    break;
                case related_events://EVENTOS RELACIONADOS (suicidio)
                    if (sourceTable.indexOf("fatal_injury_suicide") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_suicide ";
                        filterSourceTable = filterSourceTable + " fatal_injury_suicide.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          related_events.related_event_name \n\r"
                            + "       FROM \n\r"
                            + "          public.related_events \n\r"
                            + "       WHERE \n\r"
                            + "          related_event_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS eventos_relacionados";
                    break;
                case accident_mechanisms://MECANISMO (muerte accidental)                     
                    if (sourceTable.indexOf("fatal_injury_accident") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_accident ";
                        filterSourceTable = filterSourceTable + " fatal_injury_accident.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          accident_mechanisms.accident_mechanism_name \n\r"
                            + "       FROM \n\r"
                            + "          public.accident_mechanisms \n\r"
                            + "       WHERE \n\r"
                            + "          accident_mechanism_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS mecanismo_accidente";
                    break;
                case suicide_mechanisms://MECANISMO (suicidio)
                    if (sourceTable.indexOf("fatal_injury_suicide") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_suicide ";
                        filterSourceTable = filterSourceTable + " fatal_injury_suicide.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          suicide_mechanisms.suicide_mechanism_name \n\r"
                            + "       FROM \n\r"
                            + "          public.suicide_mechanisms \n\r"
                            + "       WHERE \n\r"
                            + "          suicide_mechanism_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS mecanismo_suicidio";

                    break;
                case murder_contexts://CONTEXTO (homicidios)
                    if (sourceTable.indexOf("fatal_injury_murder") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_murder ";
                        filterSourceTable = filterSourceTable + " fatal_injury_murder.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          murder_contexts.murder_context_name \n\r"
                            + "       FROM \n\r"
                            + "          public.murder_contexts \n\r"
                            + "       WHERE \n\r"
                            + "          murder_context_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS contexto";

                    break;
                case alcohol_levels://CONSUMO ALCOHOL                     
                    if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_traffic.alcohol_level_counterpart_id") == 0) {//(transito)
                        if (sourceTable.indexOf("fatal_injury_traffic.alcohol_level_counterpart_id") == -1) {
                            sourceTable = sourceTable + ", fatal_injury_traffic ";
                            filterSourceTable = filterSourceTable + " fatal_injury_traffic.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                        }
                        sqlReturn = sqlReturn + ""
                                + " CASE \n\r"
                                + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                                + "    ELSE \n\r"
                                + "    ( \n\r"
                                + "       SELECT \n\r"
                                + "          alcohol_levels.alcohol_level_name \n\r"
                                + "       FROM \n\r"
                                + "          public.alcohol_levels \n\r"
                                + "       WHERE \n\r"
                                + "          alcohol_level_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                                + "    )"
                                + " END AS consumo_alcohol";

                    } else {//(homicidios)(suicidios)(muerte accidental)
                        sqlReturn = sqlReturn + ""
                                + " CASE \n\r"
                                + "    WHEN " + currentIndicator.getInjuryType() + ".alcohol_level_victim_id is null THEN 'SIN DATO' \n\r"
                                + "    ELSE \n\r"
                                + "    ( \n\r"
                                + "       SELECT \n\r"
                                + "          alcohol_levels.alcohol_level_name \n\r"
                                + "       FROM \n\r"
                                + "          public.alcohol_levels \n\r"
                                + "       WHERE \n\r"
                                + "          alcohol_level_id = " + currentIndicator.getInjuryType() + ".alcohol_level_victim_id \n\r"
                                + "    )"
                                + " END AS consumo_alcohol";
                    }
                    break;
                case places://SITIO EVENTO (homicidios)(muerte accidental)                   
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_place_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          places.place_name \n\r"
                            + "       FROM \n\r"
                            + "          public.places \n\r"
                            + "       WHERE \n\r"
                            + "          place_id = " + currentIndicator.getInjuryType() + ".injury_place_id \n\r"
                            + "    )"
                            + " END AS lugar_hecho";

                    break;
                case weapon_types://TIPO DE ARMA (homicidios)
                    if (sourceTable.indexOf("fatal_injury_murder") == -1) {
                        sourceTable = sourceTable + ", fatal_injury_murder ";
                        filterSourceTable = filterSourceTable + " fatal_injury_murder.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
                    }
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + variablesCrossData.get(i).getSource_table() + " is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          weapon_types.weapon_type_name \n\r"
                            + "       FROM \n\r"
                            + "          public.weapon_types \n\r"
                            + "       WHERE \n\r"
                            + "          weapon_types.weapon_type_id = " + variablesCrossData.get(i).getSource_table() + " \n\r"
                            + "    )"
                            + " END AS tipo_arma";
                    break;
            }
            if (i == variablesCrossData.size() - 1) {//si es la ultima instruccion se agrega salto de linea
                sqlReturn = sqlReturn + " \n\r";
            } else {//si no es la ultima instruccion se agrega coma y salto de linea
                sqlReturn = sqlReturn + ", \n\r";
            }
        }
        sqlReturn = sqlReturn + ""
                + "   FROM  \n\r"
                + "       " + currentIndicator.getInjuryType() + ", victims" + sourceTable + " \n\r"
                + "   WHERE  \n\r"
                + "       " + currentIndicator.getInjuryType() + ".victim_id = victims.victim_id AND \n\r"
                + "       " + filterSourceTable;
        if (currentIndicator.getIndicatorId() > 4) { //si no es general se filtra por tipo de lesion
            sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_id = " + currentIndicator.getInjuryId().toString() + " AND \n\r";
        }
        sqlReturn = sqlReturn + ""
                + "       " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n\r"
                + "       " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy'); ";
        System.out.println("CONSULTA (indicators average) \n " + sqlReturn);

        return sqlReturn;
    }

    public void changeCategoticalList() {
        if (!currentCategoricalValuesSelected.isEmpty()) {
            btnRemoveCategoricalValueDisabled = false;
        }
    }

    public int btnRemoveConfigurationClick() {
        //System.out.println("currentConfigurationSelected es " + currentConfigurationSelected);
        if (currentConfigurationSelected == null || currentConfigurationSelected.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
            return 0;
        }
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            if (indicatorsConfigurationsList.get(i).getConfigurationName().compareTo(currentConfigurationSelected) == 0) {
                indicatorsConfigurationsFacade.remove(indicatorsConfigurationsList.get(i));
                btnLoadConfigurationClick();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La configuración ha sido eliminada"));
                return 0;
            }
        }
        return 0;
    }

    public int btnOpenConfigurationClick() {
        //realizar la carga de la configuracion indicada
        if (currentConfigurationSelected == null || currentConfigurationSelected.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
            return 0;
        }
        currentCategoricalValuesList = new ArrayList<String>();
        IndicatorsConfigurations indicatorsConfigurationsSelected = indicatorsConfigurationsFacade.findByName(currentConfigurationSelected);

        if (firstVariablesCrossSelected.compareTo(indicatorsConfigurationsSelected.getVariableName()) != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La configuracion corresponde a la variable ("
                    + indicatorsConfigurationsSelected.getVariableName() + ") se debe abrir una configuracion para"
                    + " una variable de tipo (" + firstVariablesCrossSelected + ")"));
            return 0;
        }

        String[] splitConfiguration = indicatorsConfigurationsSelected.getConfiguredValues().split("\t");
        currentCategoricalValuesList.addAll(Arrays.asList(splitConfiguration));

        for (int i = 0; i < variablesCrossData.size(); i++) {
            if (variablesCrossData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                variablesCrossData.get(i).setValuesConfigured(Arrays.asList(splitConfiguration));
                variablesCrossData.get(i).setValuesId(Arrays.asList(splitConfiguration));
                variablesCrossData.get(i).setValues(Arrays.asList(splitConfiguration));
                break;
            }
        }
        for (int i = 0; i < variablesListData.size(); i++) {
            if (variablesListData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                variablesListData.get(i).setValuesConfigured(Arrays.asList(splitConfiguration));
                variablesListData.get(i).setValuesId(Arrays.asList(splitConfiguration));
                variablesListData.get(i).setValues(Arrays.asList(splitConfiguration));
                break;
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuración cargada"));
        return 0;
    }

    public void btnLoadConfigurationClick() {
        //recargar las configuraciones existentes
        //System.out.println("inicia carga de configuraciones");
        currentConfigurationSelected = "";
        configurationsList = new ArrayList<String>();
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            if (currentVariablesCrossSelected.get(0).compareTo(indicatorsConfigurationsList.get(i).getVariableName()) == 0) {
                configurationsList.add(indicatorsConfigurationsList.get(i).getConfigurationName());
            }
            //System.out.println("inicia carga de configuraciones");
        }
    }

    public int btnSaveConfigurationClick() {
        if (newConfigurationName.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite el nombre para la nueva configuración"));
            return 0;
        }
        //determino si el nombre ya esta ingresado
        boolean founConfiguration = false;
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            if (indicatorsConfigurationsList.get(i).getConfigurationName().compareTo(newConfigurationName) == 0) {
                founConfiguration = true;
                break;
            }
        }
        if (!founConfiguration) {
            if (!currentCategoricalValuesList.isEmpty()) {
                IndicatorsConfigurations newIndicatorsConfigurations = new IndicatorsConfigurations(indicatorsConfigurationsFacade.findMax() + 1);
                //System.out.println("El valor de id_configuration es : " + newIndicatorsConfigurations.getConfigurationId());
                newIndicatorsConfigurations.setConfigurationName(newConfigurationName);
                newIndicatorsConfigurations.setVariableName(firstVariablesCrossSelected);
                String configuredValues = "";
                for (int i = 0; i < currentCategoricalValuesList.size(); i++) {
                    configuredValues = configuredValues + currentCategoricalValuesList.get(i);
                    if (i != currentCategoricalValuesList.size() - 1) {
                        configuredValues = configuredValues + "\t";
                    }
                }
                newIndicatorsConfigurations.setConfiguredValues(configuredValues);
                indicatorsConfigurationsFacade.create(newIndicatorsConfigurations);
                btnLoadConfigurationClick();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La cofiguración ha sido almacenada"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No existen categorias para almacenar"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre registrado ya fue ingresado, por favor digite uno diferente"));
        }

        return 0;
    }

    public int btnAddCategoricalValueClick() {
        int i;
        int e;
        if (initialValue.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite un valor inicial"));
            return 0;
        }
        if (endValue.trim().length() == 0) {//VALOR FINAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite un valor final"));
            return 0;
        }
        if (endValue.compareToIgnoreCase("n") == 0) {
            endValue = "n";
        }

        if (endValue.compareTo("n") == 0) {
            try {//VALOR INICIAL NUMERICOS
                i = Integer.parseInt(initialValue);
                if (i < 0) {//VALOR INICIAL Y FINAL MAYOR O IGUAL A CERO
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                    return 0;
                }
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser numéricos"));
                return 0;
            }
        } else {
            try {//VALOR INICIAL Y FINAL NUMERICOS
                i = Integer.parseInt(initialValue);
                e = Integer.parseInt(endValue);

                if (i < 0 && e < 0) {//VALOR INICIAL Y FINAL MAYOR O IGUAL A CERO
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                    return 0;
                }
                if (i < 0 && e < 0) {//VALOR INICIAL Y FINAL MAYOR O IGUAL A CERO
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                    return 0;
                }
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser numéricos"));
                return 0;
            }
            if (i > e) {//VALOR INICIAL MAYOR QUE FINAL
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El valor inicial debe ser menor que el valor final"));
                return 0;
            }
        }

        //EL RANGO NO ESTE DENTRO DE OTRO
        if (endValue.compareTo("n") != 0) {
            if (currentVariableConfiguring != null) {
                for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                    String[] splitValues = currentVariableConfiguring.getValuesConfigured().get(j).split("/");
                    int initialValueFoundInteger = Integer.parseInt(splitValues[0]);
                    int endValueFoundInteger = Integer.parseInt(splitValues[1]);
                    int initialValueAddInteger = Integer.parseInt(initialValue);
                    int endValueAddInteger = Integer.parseInt(endValue);
                    for (int k = initialValueFoundInteger; k < endValueFoundInteger; k++) {
                        for (int l = initialValueAddInteger; l < endValueAddInteger; l++) {
                            if (k == l) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dentro del rango ingresado el valor (" + String.valueOf(k) + ") esta contenido en la lista de valores"));
                                return 0;
                            }
                        }
                    }
                }
            }
        }
        //ingreso el nuevo valor a la categoria
        if (currentVariableConfiguring != null) {

            if (initialValue.length() == 1) {
                initialValue = "0" + initialValue;
            }
            if (endValue.length() == 1 && endValue.compareTo("n") != 0) {
                endValue = "0" + endValue;
            }
            currentVariableConfiguring.getValuesConfigured().add(initialValue + "/" + endValue);
            currentCategoricalValuesList = new ArrayList<String>();
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValuesConfigured().get(j));
            }
            initialValue = "";
            endValue = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha adicionado un nuevo valor a la categoría"));
            return 0;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", "No hay categoria seleccionada"));
        return 0;
    }

    public void btnRemoveCategoryValueClick() {
        //btnRemoveCategoricalValueDisabled = false;
        if (currentVariableConfiguring != null) {
            for (int i = 0; i < currentCategoricalValuesSelected.size(); i++) {
                for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                    if (currentVariableConfiguring.getValuesConfigured().get(j).compareTo(currentCategoricalValuesSelected.get(i)) == 0) {
                        currentVariableConfiguring.getValuesConfigured().remove(j);
                        break;
                    }
                }
            }
            //recargo la lista de valores de la categoria
            currentCategoricalValuesList = new ArrayList<String>();
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValuesConfigured().get(j));
            }
        }
    }

    public void btnResetCategoryListClick() {
        currentCategoricalValuesSelected = new ArrayList<String>();
        //btnRemoveCategoricalValueDisabled = false;
        if (currentVariableConfiguring != null) {
            //paso los elementos de la lista: values a valuesConfiguration
            currentVariableConfiguring.setValuesConfigured(new ArrayList<String>());
            for (int j = 0; j < currentVariableConfiguring.getValues().size(); j++) {
                currentVariableConfiguring.getValuesConfigured().add(currentVariableConfiguring.getValues().get(j));
            }
            //recargo la lista de valores de la categoria
            currentCategoricalValuesList = new ArrayList<String>();
            for (int j = 0; j < currentVariableConfiguring.getValues().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValues().get(j));
            }
        }
    }

    public void changeVariable() {
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        if (currentVariablesSelected != null) {
            if (!currentVariablesSelected.isEmpty()) {
                btnAddVariableDisabled = false;
            }
        }
        if (currentVariablesCrossSelected != null) {
            if (!currentVariablesCrossSelected.isEmpty()) {
                btnRemoveVariableDisabled = false;
            }
        }
    }

    public void changeCrossVariable() {
        btnRemoveVariableDisabled = true;
        btnRemoveCategoricalValueDisabled = true;
        currentCategoricalValuesSelected = new ArrayList<String>();
        currentVariableConfiguring = null;
        initialValue = "";
        endValue = "";
        //cargo la lista de valores categoricos para la variable
        if (!currentVariablesCrossSelected.isEmpty()) {
            btnRemoveCategoricalValueDisabled = true;
            btnRemoveVariableDisabled = false;
            firstVariablesCrossSelected = currentVariablesCrossSelected.get(0);
            //filtro los años segun la fecha
            currentCategoricalValuesSelected = new ArrayList<String>();
            for (int i = 0; i < variablesListData.size(); i++) {
                if (variablesListData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                    if (variablesListData.get(i).getGeneric_table().compareTo("year") == 0) {
                        variablesListData.get(i).filterYear(initialDate, endDate);
                    }
                    currentCategoricalValuesList = variablesListData.get(i).getValuesConfigured();
                    currentVariableConfiguring = variablesListData.get(i);
                    btnAddCategoricalValueDisabled = !currentVariableConfiguring.isConfigurable();
                    break;
                }
            }
        }
    }

    public void addVariableClick() {
        String error = "";
        boolean nextStep = true;
        if (currentVariablesSelected == null) {
            error = "Debe seleccionarse una variable";
            nextStep = false;
        }
        if (nextStep) {
            for (int i = 0; i < variablesList.size(); i++) {
                for (int j = 0; j < currentVariablesSelected.size(); j++) {
                    //System.out.println("comparar: " + variablesList.get(i)+ " CON "+currentVariablesSelected.get(j));
                    if (variablesList.get(i).compareTo(currentVariablesSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        //System.out.println("quitar: " + variablesList.get(i));
                        variablesList.remove(i);//la quito de este listado                    
                        variablesCrossList.add(currentVariablesSelected.get(j));//la agrego al otro                        
                        btnAddVariableDisabled = true;
                        i = -1;
                        break;
                    }
                }
            }
            currentVariablesSelected = null;
        }
        if (error.length() != 0) {//hay  errores al relacionar la variables 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
    }

    public void removeVariableClick() {
        String error = "";
        boolean nextStep = true;
        if (currentVariablesCrossSelected == null) {
            error = "Debe seleccionarse una variable";
            nextStep = false;
        }
        if (nextStep) {
            for (int i = 0; i < variablesCrossList.size(); i++) {
                for (int j = 0; j < currentVariablesCrossSelected.size(); j++) {
                    if (variablesCrossList.get(i).compareTo(currentVariablesCrossSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        variablesCrossList.remove(i);//la quito de este listado                    
                        variablesList.add(currentVariablesCrossSelected.get(j));//la agrego al otro                        
                        btnRemoveVariableDisabled = true;
                        i = -1;
                        break;
                    }
                }
            }
            currentVariablesCrossSelected = null;
        }
        if (error.length() != 0) {//hay  errores al relacionar la variables 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
    }

    public void createImage() {
        //if (currentGraphType.compareTo("barras vertical") == 0) {
        try {
            JFreeChart chart = createBarChart();
            //JFreeChart chart = createChart();
            File chartFile = new File("dynamichart");
            ChartUtilities.saveChartAsPNG(chartFile, chart, 700, 500);
            chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
        } catch (Exception e) {
        }
        // }
    }

    public void reset() {
        btnExportDisabled = true;
        dataTableHtml = "";
        chartImage = null;
        currentVariableConfiguring = null;
        variablesCrossList = new ArrayList<String>();
        currentVariablesSelected = new ArrayList<String>();
        currentVariablesCrossSelected = new ArrayList<String>();
        firstVariablesCrossSelected = null;
        currentCategoricalValuesList = new ArrayList<String>();
        currentCategoricalValuesSelected = new ArrayList<String>();
        titlePage = currentIndicator.getIndicatorGroup();
        titleIndicator = currentIndicator.getIndicatorGroup();
        subTitleIndicator = currentIndicator.getIndicatorName();
        variablesListData = getVariablesIndicator();
        currentVariableGraph1 = "";
        currentVariableGraph2 = "";
        valuesGraph1 = new ArrayList<String>();
        valuesGraph2 = new ArrayList<String>();
        currentValueGraph1 = "";
        currentValueGraph2 = "";
        numberCross = currentIndicator.getNumberCross();
        variablesList = new ArrayList<String>();//SelectItem[variablesListData.size()];
        for (int i = 0; i < variablesListData.size(); i++) {
            variablesList.add(variablesListData.get(i).getName());
        }
        dynamicDataTableGroup = new OutputPanel();//creo el panel grid

        variablesCrossList = new ArrayList<String>();//SelectItem[variablesListData.size()];
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        currentVariablesSelected = null;
        currentVariablesCrossSelected = null;
    }

    public Variable createVariable(String name, String generic_table, boolean conf, String source_table) {
        //conf me indica si es permitida la configuracion de esta variable
        Variable newVariable = new Variable(name, generic_table, conf, source_table);
        //cargo la lista de valores posibles
        ArrayList<String> valuesName = new ArrayList<String>();//NOMBRE DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(NOMBRE EN LA CATEGORIA)
        ArrayList<String> valuesId = new ArrayList<String>();  //IDENTIFICADORES DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(ID EN LA CATEGORIA)
        ArrayList<String> valuesConf = new ArrayList<String>();//NOMBRE DE LOS VALORES CONFIGURADOS POR EL USUARIO QUE PUEDE TOMAR LA VARIABLE        
        int infInt;
        int supInt;
        String infStr;
        String supStr;
        switch (VariablesEnum.convert(generic_table)) {//
            case monthly:
                valuesName.add("mensual");
                valuesConf.add("mensual");
                valuesId.add("mensual");
                break;
            case annual:
                valuesName.add("anual");
                valuesConf.add("anual");
                valuesId.add("anual");
                break;
            case daily:
                valuesName.add("diario");
                valuesConf.add("diario");
                valuesId.add("diario");
                break;
            case age:
                infInt = 0;
                supInt = 4;
                for (int i = 0; i < 16; i++) {
                    infStr = String.valueOf(infInt);
                    supStr = String.valueOf(supInt);
                    if (infStr.length() == 1) {
                        infStr = "0" + infStr;
                    }
                    if (supStr.length() == 1) {
                        supStr = "0" + supStr;
                    }
                    valuesName.add(infStr + "/" + supStr);
                    valuesConf.add(infStr + "/" + supStr);
                    valuesId.add(infStr + "/" + supStr);
                    infInt = infInt + 5;
                    supInt = supInt + 5;
                }
                valuesName.add("80/n");
                valuesConf.add("80/n");
                valuesId.add("80/n");
                break;
            case injuries_fatal:
                valuesId.add("10");
                valuesName.add("HOMICIDIO");
                valuesConf.add("HOMICIDIO");
                valuesId.add("11");
                valuesName.add("MUERTE EN ACCIDENTE DE TRANSITO");
                valuesConf.add("MUERTE EN ACCIDENTE DE TRANSITO");
                valuesId.add("12");
                valuesName.add("SUICIDIO");
                valuesConf.add("SUICIDIO");
                valuesId.add("13");
                valuesName.add("MUERTE ACCIDENTAL");
                valuesConf.add("MUERTE ACCIDENTAL");
                break;
            case injuries_non_fatal:
                valuesId.add("50");
                valuesName.add("VIOLENCIA INTERPERSONAL");
                valuesConf.add("VIOLENCIA INTERPERSONAL");
                valuesId.add("51");
                valuesName.add("LESION EN ACCIDENTE DE TRANSITO");
                valuesConf.add("LESION EN ACCIDENTE DE TRANSITO");
                valuesId.add("52");
                valuesName.add("INTENCIONAL AUTOINFLINGIDA");
                valuesConf.add("INTENCIONAL AUTOINFLINGIDA");
                valuesId.add("53");
                valuesName.add("VIOLENCIA INTRAFAMILIAR");
                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
                valuesId.add("54");
                valuesName.add("NO INTENCIONAL");
                valuesConf.add("NO INTENCIONAL");
                valuesId.add("55");
                valuesName.add("VIOLENCIA INTRAFAMILIAR");
                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
                break;
            case hour:
                infInt = 0;
                supInt = 2;
                for (int i = 0; i < 8; i++) {
                    valuesName.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    valuesConf.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    valuesId.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    infInt = supInt + 1;
                    supInt = supInt + 3;
                }
                break;
            case day:
                break;
            case month:
                valuesId.add("1");
                valuesName.add("ENERO");
                valuesConf.add("ENERO");
                valuesId.add("2");
                valuesName.add("FEBRERO");
                valuesConf.add("FEBRERO");
                valuesId.add("3");
                valuesName.add("MARZO");
                valuesConf.add("MARZO");
                valuesId.add("4");
                valuesName.add("ABRIL");
                valuesConf.add("ABRIL");
                valuesId.add("5");
                valuesName.add("MAYO");
                valuesConf.add("MAYO");
                valuesId.add("6");
                valuesName.add("JUNIO");
                valuesConf.add("JUNIO");
                valuesId.add("7");
                valuesName.add("JULIO");
                valuesConf.add("JULIO");
                valuesId.add("8");
                valuesName.add("AGOSTO");
                valuesConf.add("AGOSTO");
                valuesId.add("9");
                valuesName.add("SEPTIEMBRE");
                valuesConf.add("SEPTIEMBRE");
                valuesId.add("10");
                valuesName.add("OCTUBRE");
                valuesConf.add("OCTUBRE");
                valuesId.add("11");
                valuesName.add("NOVIEMBRE");
                valuesConf.add("NOVIEMBRE");
                valuesId.add("12");
                valuesName.add("DICIEMBRE");
                valuesConf.add("DICIEMBRE");
                break;
            case year:
                //System.out.println("AÑO ACTUAL" + currentYear);
                for (int i = 2003; i <= currentYear; i++) {
                    valuesName.add(String.valueOf(i));
                    valuesConf.add(String.valueOf(i));
                    valuesId.add(String.valueOf(i));
                }
                break;
            case neighborhoods://barrio,
            case communes://comuna,
            case corridors://corredor,
            case areas://zona,
            case genders://genero,
            case days://dia semana
            case quadrants://cuadrante

            case activities:
            case boolean3:
            case victim_characteristics:
            case accident_classes:
            case alcohol_levels:
            case use_alcohol_drugs:
            case alcohol_levels_counterparts:
            case alcohol_levels_victim:
            case murder_contexts:
            case contexts:
            case destinations_of_patient:
            case related_events:
            case precipitating_factors:
            case aggressor_genders:
            case places:
            case non_fatal_places:
            case mechanisms:
            case accident_mechanisms:
            case suicide_mechanisms:
            case protective_measures:
            case relationships_to_victim:
            case weapon_types:
            case counterpart_service_type:
            case service_types:
            case transport_counterparts:
            case transport_types:
            case transport_users:
            case involved_vehicles:
            case road_types:
            case NOVALUE://es una tabla categorica
                try {
                    //ResultSet rs = connectionJdbcMB.consult("Select * from " + generic_table);
                    ResultSet rs = connectionJdbcMB.consult("Select * from " + generic_table + " order by 1");
                    while (rs.next()) {
                        valuesName.add(rs.getString(2));
                        valuesConf.add(rs.getString(2));
                        valuesId.add(rs.getString(1));
                    }
                } catch (Exception e) {
                }
                break;
        }
        newVariable.setValues(valuesName);
        newVariable.setValuesId(valuesId);
        newVariable.setValuesConfigured(valuesConf);
        return newVariable;
    }

    public ArrayList<Variable> getVariablesIndicator() {
        ArrayList<Variable> arrayReturn = new ArrayList<Variable>();
        currentIndicator = indicatorsFacade.find(currentIndicator.getIndicatorId());
        for (int i = 0; i < currentIndicator.getIndicatorsVariablesList().size(); i++) {
            arrayReturn.add(
                    createVariable(
                    currentIndicator.getIndicatorsVariablesList().get(i).getIndicatorsVariablesPK().getVariableName(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getCategory(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getAddValues(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getSourceTable()));
        }
        return arrayReturn;
    }

//    public List<String> getGraphTypes() {
//        ArrayList<String> listReturn = new ArrayList<String>();
//        try {
//            ResultSet rs = connectionJdbcMB.consult("Select graph_type from indicators where indicator_id=" + String.valueOf(currentIndicator.getIndicatorId()));
//            if (rs.next()) {
//                String[] splitGraphType = rs.getString(1).split(",");
//                listReturn.addAll(Arrays.asList(splitGraphType));
//            }
//        } catch (Exception e) {
//        }
//        return listReturn;
//    }
    private String getColorType() {
        if (colorType) {
            return "bgcolor=\"#DDDDFF\"";
        } else {
            return "bgcolor=\"#FFFFFF\"";
        }
    }

    private void changeColorType() {
        if (colorType) {
            colorType = false;
        } else {
            colorType = true;
        }
    }

    private String determineHeader(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '0' && value.charAt(i) != '1' && value.charAt(i) != '2'
                    && value.charAt(i) != '3'&& value.charAt(i) != '4'&& value.charAt(i) != '5'
                    && value.charAt(i) != '6'&& value.charAt(i) != '7'&& value.charAt(i) != '8'
                    && value.charAt(i) != '9'&& value.charAt(i) != ' '&& value.charAt(i) != 'n'
                    && value.charAt(i) != '-'&& value.charAt(i) != ':'&& value.charAt(i) != '/') {
                return value;
            }
        }
        if (value.indexOf("SIN DATO") == -1) {
            if (value.indexOf("/") != -1) {
                if (value.indexOf(":") != -1) {
                    String newValue = value.replace("/", " a ");
                    return newValue + " Horas";
                } else {
                    String newValue = value.replace("/", " a ");
                    return newValue + " Años";
                }
            }
        }
        return value;
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow fila;
        HSSFCell celda;
        HSSFRichTextString texto;

        headers1 = new ArrayList<SpanColumns>();
        headers2 = new String[columNames.size()];
        int posRow = 0;
        int posF;
        int posI;
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        //-------------------------------------------------------------------                        

        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja            
            posRow++;
            posI = 1;
            for (int i = 0; i < columNames.size(); i++) {
                celda = fila.createCell((short) posI);// +2 por que faltal las filas               
                posI++;
                texto = new HSSFRichTextString(determineHeader(columNames.get(i)));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNames.size(); i++) {
                splitVars = columNames.get(i).split("\\}");//separo las dos variables
                if (splitVars[0].compareTo(currentVar) == 0) {//ya existe solo le aumento el numero de columnas unidas al ultimo de la lista "headers1"
                    int num = headers1.get(headers1.size() - 1).getColumns();
                    headers1.get(headers1.size() - 1).setColumns(num + 1);
                } else {//no existe la columna la debo crear y adicionar a la lista                    
                    currentVar = splitVars[0];
                    SpanColumns newSpanColumn = new SpanColumns();
                    newSpanColumn.setLabel(splitVars[0]);
                    newSpanColumn.setColumns(1);
                    headers1.add(newSpanColumn);
                }
                headers2[i] = splitVars[1];//a la segunda cabecera le agrego la segunda variable separada
            }
            //AGREGO LA CABECERA 1 
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja
            posRow++;
            posF = 0;
            posI = 1;
            for (int j = 0; j < headers1.size(); j++) {
                posI = posF + 1;
                for (int i = 0; i < headers1.get(j).getColumns(); i++) {
                    posF++;
                }
                sheet.addMergedRegion(new CellRangeAddress(0, 0, posI, posF));
            }
            short posColumn = 1;// +2 por que faltal las filas               
            for (int i = 0; i < headers1.size(); i++) {
                celda = fila.createCell(posColumn);
                posColumn = (short) (posColumn + headers1.get(i).getColumns());
                texto = new HSSFRichTextString(determineHeader(headers1.get(i).getLabel()));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja
            posRow++;
            posI = 1;// +1 por que faltal nombre de filas
            for (int i = 0; i < headers2.length; i++) {
                celda = fila.createCell((short) posI);
                posI++;
                texto = new HSSFRichTextString(determineHeader(headers2[i]));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
        }
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        for (int j = 0; j < rowNames.size(); j++) {
            fila = sheet.createRow(posRow);
            posRow++;
            //nombre fila
            celda = fila.createCell(0);
            celda.setCellValue(new HSSFRichTextString(determineHeader(rowNames.get(j))));

            posI = 1;// 1 por que faltal nombres de fila                               
            for (int i = 0; i < columNames.size(); i++) {
                celda = fila.createCell((short) posI);
                celda.setCellValue(new HSSFRichTextString(matrixResult[i][j]));
                posI++;
            }
        }
    }

    private String createDataTableResult() {
        headers1 = new ArrayList<SpanColumns>();
        headers2 = new String[columNames.size()];
        String height = "height:20px;";
        
        String strReturn = " ";
        strReturn = strReturn + "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td id=\"firstTd\" >\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td class=\"ui-widget-header\">\r\n";
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        strReturn = strReturn + "                    <div id=\"divHeader\" style=\"overflow:hidden;width:434px;\">\r\n";//484 TAMAÑO DIV HEADER SUPERIOR(16=heigth del scroll)
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            //-------------------------------------------------------------------
            //CABECERA SIMPLE
            strReturn = strReturn + "                            <tr>\r\n";
            for (int i = 0; i < columNames.size(); i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:150px;\">" + determineHeader(columNames.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNames.size(); i++) {
                splitVars = columNames.get(i).split("\\}");//separo las dos variables
                if (splitVars[0].compareTo(currentVar) == 0) {//ya existe solo le aumento el numero de columnas unidas al ultimo de la lista "headers1"
                    int num = headers1.get(headers1.size() - 1).getColumns();
                    headers1.get(headers1.size() - 1).setColumns(num + 1);
                } else {//no existe la columna la debo crear y adicionar a la lista                    
                    currentVar = splitVars[0];
                    SpanColumns newSpanColumn = new SpanColumns();
                    newSpanColumn.setLabel(splitVars[0]);
                    newSpanColumn.setColumns(1);
                    headers1.add(newSpanColumn);
                }
                headers2[i] = splitVars[1];//a la segunda cabecera le agrego la segunda variable separada
            }
            //AGREGO LA CABECERA 1 A El PANEL_GRID
            strReturn = strReturn + "                            <tr>\r\n";
            for (int i = 0; i < headers1.size(); i++) {
                strReturn = strReturn + "                                <td colspan=\"" + headers1.get(i).getColumns() + "\">\r\n";
                strReturn = strReturn + "                                    <div >" + determineHeader(headers1.get(i).getLabel()) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";

            strReturn = strReturn + "                            <tr>\r\n";
            //AGREGO LA CABECERA 2 A El PANEL_GRID
            for (int i = 0; i < headers2.length; i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:150px;\">" + determineHeader(headers2[i]) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td valign=\"top\" class=\"ui-widget-header\">\r\n";
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA PRIMER COLUMNA
        strReturn = strReturn + "                    <div id=\"firstcol\" style=\"overflow: hidden;height:280px\">\r\n";//tamaño del div izquierdo
        strReturn = strReturn + "                        <table width=\"200px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";
        for (int j = 0; j < rowNames.size(); j++) {
            //----------------------------------------------------------------------
            //NOMBRE PARA CADA FILA            
            strReturn = strReturn + "                            <tr>\r\n";
            //strReturn = strReturn + "                                <td class=\"tableFirstCol\">" + determineHeader(rowNames.get(j)) + "</td>\r\n";
            strReturn = strReturn + "                                <td height=\"20px\" ><div style=\"overflow:hidden; height:20px; width:200px; \">" + determineHeader(rowNames.get(j)) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td valign=\"top\">\r\n";
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------      
        strReturn = strReturn + "                    <div id=\"table_div\" style=\"overflow: scroll;width:450px;height:300px;position:relative\" onscroll=\"fnScroll()\" >\r\n";//div que maneja la tabla
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";//
        //----------------------------------------------------------------------
        //AGREGO LOS REGISTROS DE LA MATRIZ        
        //for (int j = 0; j < rowNames.size() - 1; j++) {//-1 por que le agrege "TOTALES"
        for (int j = 0; j < rowNames.size(); j++) {
            if (j == 0) {
                strReturn = strReturn + "                            <tr " + getColorType() + " id='firstTr'>\r\n";
            } else {
                strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            }
            for (int i = 0; i < columNames.size(); i++) {
                String value;
                value = matrixResult[i][j];
                strReturn = strReturn + "                                <td> \r\n";//mantenga dimension
//                strReturn = strReturn + "                                <div style=\"width:150px;\">" + value + "</div>\r\n";
                strReturn = strReturn + "                                <div style=\"width:150px; height:20px;\">" + value + "</div>\r\n";
                strReturn = strReturn + "                                </td> \r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";
            changeColorType();//cambiar de color las filas de blanco a azul
        }
        //-------------------------------------------------------------------
        //FINALIZA
        //-------------------------------------------------------------------        
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "        </table>\r\n";
        //System.out.println(strReturn);
        return strReturn;
    }

    public JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String variablesName = "";
        String indicatorName ="";
        String categoryAxixLabel = "";        
        ResultSet rs;
        double average = 0;
        int divisor = 0;
        double increment = 0;
        try {
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql = sql + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                
                variablesName = "Desagregado por: " + currentVariableGraph1 + " = " + determineHeader(currentValueGraph1);
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql = sql + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                variablesName = variablesName+ ", " + currentVariableGraph2 + " = " + determineHeader(currentValueGraph2);
            }

            rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
            while (rs.next()) {
                dataset.setValue(rs.getLong("count"), "-", rs.getString("column_1"));
                average = average + rs.getLong("count");
                if (increment < rs.getLong("count")) {
                    increment = rs.getLong("count");
                }
                divisor++;
            }
            if (divisor != 0) {
                average = average / divisor;
            }
            increment = increment * 0.005;//grosor linea
            indicatorName = currentIndicator.getIndicatorName()+"("+formateador.format(average)+") - Municipo de Pasto.\n";
            //indicatorName = indicatorName + "\n Promedio: " + formateador.format(average);
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.toString());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");        
        indicatorName = indicatorName + variablesName + "\nPeriodo "+sdf.format(initialDate)+" a "+sdf.format(endDate);        
        
        JFreeChart chartReturn = ChartFactory.createBarChart(indicatorName, categoryAxixLabel, "Conteo", dataset, PlotOrientation.VERTICAL, false, true, false);
        //COLORES DE FONDO Y TITULO----------------------------
        chartReturn.setBackgroundPaint(Color.white);
        chartReturn.getTitle().setPaint(new Color(50, 50, 50));
        chartReturn.getTitle().setFont(new Font("SanSerif", Font.BOLD, 15));        
        
        //COLOCAR LABELS A LOS GRAFICOS----------------------------
        CategoryPlot plot = (CategoryPlot) chartReturn.getPlot();
        ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());//quitar gradiente
        plot.setRangeGridlinePaint(Color.BLUE);//malla color blanco
        //adicionar raya
        IntervalMarker intervalmarker = new IntervalMarker(average - increment, average + increment, Color.yellow);
        plot.addRangeMarker(intervalmarker);

        if (showItems) {
            CategoryItemRenderer renderer = plot.getRenderer();
            CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0"));//DecimalFormat("0.00"));
            renderer.setItemLabelGenerator(generator);
            renderer.setItemLabelsVisible(true);
        }
        //ROTAR LASETIQUETAS DEL EJE X-----------------------------
        CategoryAxis xAxis = (CategoryAxis) plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        return chartReturn;
    }

    private void loadIndicator(int n) {
        currentIndicator = indicatorsFacade.find(n);
        reset();
    }

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //FUNCIOES PARA REALIZAR LA CARGA DE UN INDICADOR
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public void loadIndicator8() {
        loadIndicator(8);
    }

    public void loadIndicator15() {
        loadIndicator(15);
    }

    public void loadIndicator22() {
        loadIndicator(22);
    }

    public void loadIndicator29() {
        loadIndicator(29);
    }

    public void loadIndicator36() {
        loadIndicator(36);
    }

    public void loadIndicator43() {
        loadIndicator(43);
    }

    public void loadIndicator50() {
        loadIndicator(50);
    }

    public void loadIndicator57() {
        loadIndicator(57);
    }

    public void loadIndicator64() {
        loadIndicator(64);
    }

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //FUNCIONES GET AND SET
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public String getSubTitleIndicator() {
        return subTitleIndicator;
    }

    public void setSubTitleIndicator(String subTitleIndicator) {
        this.subTitleIndicator = subTitleIndicator;
    }

    public String getTitleIndicator() {
        return titleIndicator;
    }

    public void setTitleIndicator(String titleIndicator) {
        this.titleIndicator = titleIndicator;
    }

    public String getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    public StreamedContent getChartImage() {
        return chartImage;
    }

    public void setChartImage(StreamedContent chartImage) {
        this.chartImage = chartImage;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public List<String> getVariablesCrossList() {
        return variablesCrossList;
    }

    public void setVariablesCrossList(List<String> variablesCrossList) {
        this.variablesCrossList = variablesCrossList;
    }

    public List<String> getVariablesList() {
        return variablesList;
    }

    public void setVariablesList(List<String> variablesList) {
        this.variablesList = variablesList;
    }

    public List<String> getCurrentVariablesCrossSelected() {
        return currentVariablesCrossSelected;
    }

    public void setCurrentVariablesCrossSelected(List<String> currentVariablesCrossSelected) {
        this.currentVariablesCrossSelected = currentVariablesCrossSelected;
    }

    public List<String> getCurrentVariablesSelected() {
        return currentVariablesSelected;
    }

    public void setCurrentVariablesSelected(List<String> currentVariablesSelected) {
        this.currentVariablesSelected = currentVariablesSelected;
    }

    public boolean isBtnAddVariableDisabled() {
        return btnAddVariableDisabled;
    }

    public void setBtnAddVariableDisabled(boolean btnAddVariableDisabled) {
        this.btnAddVariableDisabled = btnAddVariableDisabled;
    }

    public boolean isBtnRemoveVariableDisabled() {
        return btnRemoveVariableDisabled;
    }

    public void setBtnRemoveVariableDisabled(boolean btnRemoveVariableDisabled) {
        this.btnRemoveVariableDisabled = btnRemoveVariableDisabled;
    }

    public ArrayList<String> getValuesCategoryList() {
        return valuesCategoryList;
    }

    public void setValuesCategoryList(ArrayList<String> valuesCategoryList) {
        this.valuesCategoryList = valuesCategoryList;
    }

//    public String getCurrentGraphType() {
//        return currentGraphType;
//    }
//
//    public void setCurrentGraphType(String currentGraphType) {
//        this.currentGraphType = currentGraphType;
//    }
    public List<String> getCurrentCategoricalValuesList() {
        return currentCategoricalValuesList;
    }

    public void setCurrentCategoricalValuesList(List<String> currentCategoricalValuesList) {
        this.currentCategoricalValuesList = currentCategoricalValuesList;
    }

    public List<String> getCurrentCategoricalValuesSelected() {
        return currentCategoricalValuesSelected;
    }

    public void setCurrentCategoricalValuesSelected(List<String> currentCategoricalValuesSelected) {
        this.currentCategoricalValuesSelected = currentCategoricalValuesSelected;
    }

    public String getFirstVariablesCrossSelected() {
        return firstVariablesCrossSelected;
    }

    public void setFirstVariablesCrossSelected(String firstVariablesCrossSelected) {
        this.firstVariablesCrossSelected = firstVariablesCrossSelected;
    }

    public boolean isBtnRemoveCategoricalValueDisabled() {
        return btnRemoveCategoricalValueDisabled;
    }

    public void setBtnRemoveCategoricalValueDisabled(boolean btnRemoveCategoricalValueDisabled) {
        this.btnRemoveCategoricalValueDisabled = btnRemoveCategoricalValueDisabled;
    }

    public boolean isBtnAddCategoricalValueDisabled() {
        return btnAddCategoricalValueDisabled;
    }

    public void setBtnAddCategoricalValueDisabled(boolean btnAddCategoricalValueDisabled) {
        this.btnAddCategoricalValueDisabled = btnAddCategoricalValueDisabled;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public String getDataTableHtml() {
        return dataTableHtml;
    }

    public void setDataTableHtml(String dataTableHtml) {
        this.dataTableHtml = dataTableHtml;
    }
//    public boolean isShowTotals() {
//        return showTotals;
//    }
//
//    public void setShowTotals(boolean showTotals) {
//        this.showTotals = showTotals;
//    }
//    public boolean isShowAll() {
//        return showAll;
//    }
//
//    public void setShowAll(boolean showAll) {
//        this.showAll = showAll;
//    }

    public OutputPanel getDynamicDataTableGroup() {
        return dynamicDataTableGroup;
    }

    public void setDynamicDataTableGroup(OutputPanel dynamicDataTableGroup) {
        this.dynamicDataTableGroup = dynamicDataTableGroup;
    }

    public boolean isRenderedDynamicDataTable() {
        return renderedDynamicDataTable;
    }

    public void setRenderedDynamicDataTable(boolean renderedDynamicDataTable) {
        this.renderedDynamicDataTable = renderedDynamicDataTable;
    }

    public String getCurrentValueGraph1() {
        return currentValueGraph1;
    }

    public void setCurrentValueGraph1(String currentValueGraph1) {
        this.currentValueGraph1 = currentValueGraph1;
    }

    public String getCurrentValueGraph2() {
        return currentValueGraph2;
    }

    public void setCurrentValueGraph2(String currentValueGraph2) {
        this.currentValueGraph2 = currentValueGraph2;
    }

    public String getCurrentVariableGraph1() {
        return currentVariableGraph1;
    }

    public void setCurrentVariableGraph1(String currentVariableGraph1) {
        this.currentVariableGraph1 = currentVariableGraph1;
    }

    public String getCurrentVariableGraph2() {
        return currentVariableGraph2;
    }

    public void setCurrentVariableGraph2(String currentVariableGraph2) {
        this.currentVariableGraph2 = currentVariableGraph2;
    }

    public List<String> getValuesGraph1() {
        return valuesGraph1;
    }

    public void setValuesGraph1(List<String> valuesGraph1) {
        this.valuesGraph1 = valuesGraph1;
    }

    public List<String> getValuesGraph2() {
        return valuesGraph2;
    }

    public void setValuesGraph2(List<String> valuesGraph2) {
        this.valuesGraph2 = valuesGraph2;
    }

    public String getCurrentTemporalDisaggregation() {
        return currentTemporalDisaggregation;
    }

    public void setCurrentTemporalDisaggregation(String currentTemporalDisaggregation) {
        this.currentTemporalDisaggregation = currentTemporalDisaggregation;
    }

    public List<String> getTemporalDisaggregationTypes() {
        return temporalDisaggregationTypes;
    }

    public void setTemporalDisaggregationTypes(List<String> temporalDisaggregationTypes) {
        this.temporalDisaggregationTypes = temporalDisaggregationTypes;
    }

//    public boolean isShowCalculation() {
//        return showCalculation;
//    }
//
//    public void setShowCalculation(boolean showCalculation) {
//        this.showCalculation = showCalculation;
//    }
    public String getNewConfigurationName() {
        return newConfigurationName;
    }

    public void setNewConfigurationName(String newConfigurationName) {
        this.newConfigurationName = newConfigurationName;
    }

    public String getCurrentConfigurationSelected() {
        return currentConfigurationSelected;
    }

    public void setCurrentConfigurationSelected(String currentConfigurationSelected) {
        this.currentConfigurationSelected = currentConfigurationSelected;
    }

    public List<String> getConfigurationsList() {
        return configurationsList;
    }

    public void setConfigurationsList(List<String> configurationsList) {
        this.configurationsList = configurationsList;
    }

    public boolean isShowItems() {
        return showItems;
    }

    public void setShowItems(boolean showItems) {
        this.showItems = showItems;
    }

    public boolean isBtnExportDisabled() {
        return btnExportDisabled;
    }

    public void setBtnExportDisabled(boolean btnExportDisabled) {
        this.btnExportDisabled = btnExportDisabled;
    }

    public boolean isShowEmpty() {
        return showEmpty;
    }

    public void setShowEmpty(boolean showEmpty) {
        this.showEmpty = showEmpty;
    }
}
