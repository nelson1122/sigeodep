/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.VariablesEnum;
import beans.util.Variable;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.joda.time.Years;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsPercentageVariationMB")
@SessionScoped
public class IndicatorsPercentageVariationMB {

    /**
     * Creates a new instance of IndicatorsMB
     */
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
    //private OutputPanel dynamicDataTableGroup; // Placeholder.
    private FacesMessage message = null;
    private ConnectionJdbcMB connectionJdbcMB;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    private String currentVariableGraph;
    private String currentValueGraph;
    private String currentValue;
    private String firstVariablesCrossSelected = null;
    private String initialValue = "";
    private String endValue = "";
    private String dataTableHtmlA;
    private String dataTableHtmlB;
    private String dataTableHtmlDiference;
    private LoginMB loginMB;
    private String sql = "";
    private String[] headers2;//CABECERA 2 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private String[][] matrixResultA;//MATRIZ DE RESULTADOS
    private String[][] matrixResultB;//MATRIZ DE RESULTADOS
    private boolean showItems = true;
    private String currentVariableGraph1;
    private String currentVariableGraph2;
    private String currentValueGraph1;
    private String currentValueGraph2;
    private List<String> valuesGraph1 = new ArrayList<String>();
    private List<String> valuesGraph2 = new ArrayList<String>();
    private Date initialDateA = new Date();
    private Date endDateA = new Date();
    private Date initialDateB = new Date();
    private Date endDateB = new Date();
    private Calendar c1A = Calendar.getInstance();
    private Calendar c2A = Calendar.getInstance();
    private Calendar c1B = Calendar.getInstance();
    private Calendar c2B = Calendar.getInstance();
    private String initialDateStrA = "";
    private String endDateStrA = "";
    private String initialDateStrB = "";
    private String endDateStrB = "";
    private String currentTemporalDisaggregation;
    private String pivotTableName;
    private String prepivotTableName;
    private List<String> temporalDisaggregationTypes = new ArrayList<String>();
    private List<String> variablesGraph = new ArrayList<String>();
    private List<String> valuesGraph = new ArrayList<String>();
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
    //private ArrayList<SpanColumns> dynamicHeaders1;
    //private ArrayList<TableGroup> listaDeGrupos;
    private ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> columNamesFinal;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> rowNames;//NOMBRES DE LAS FILAS    
    private ArrayList<String> totalsHorizontalA = new ArrayList<String>();
    private ArrayList<String> totalsVerticalA = new ArrayList<String>();
    private ArrayList<String> totalsHorizontalB = new ArrayList<String>();
    private ArrayList<String> totalsVerticalB = new ArrayList<String>();
    private Variable currentVariableConfiguring;
    private int numberCross = 2;//maximo numero de variables a cruzar
    private int currentYear = 0;
    private int grandTotalA = 0;//total general de la matriz
    private int grandTotalB = 0;//total general de la matriz
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    private boolean renderedDynamicDataTable = true;
    private boolean showCount = false;//mostrar recuento
    private boolean showRowPercentage = true;//mostrar porcentaje por fila
    private boolean graphType1 = true;
    private boolean graphType2 = false;
    private boolean showCalculation = false;//mostrar la resta
    private boolean colorType = true;
    DecimalFormat formateador = new DecimalFormat("0.00");
    private CopyManager cpManager;
    private StringBuilder sb;
    private int tuplesProcessed;

    public IndicatorsPercentageVariationMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        //-----------------------------------------------
        initialDateA.setDate(1);
        initialDateA.setMonth(0);
        initialDateA.setYear(2003 - 1900);
        endDateA.setDate(c.get(Calendar.DATE));
        endDateA.setMonth(c.get(Calendar.MONTH));
        endDateA.setYear(c.get(Calendar.YEAR) - 1900);
        //-----------------------------------------------
        initialDateB.setDate(1);
        initialDateB.setMonth(0);
        initialDateB.setYear(2003 - 1900);
        endDateB.setDate(c.get(Calendar.DATE));
        endDateB.setMonth(c.get(Calendar.MONTH));
        endDateB.setYear(c.get(Calendar.YEAR) - 1900);
        //-----------------------------------------------
        temporalDisaggregationTypes = new ArrayList<String>();
        temporalDisaggregationTypes.add("Anual");
        temporalDisaggregationTypes.add("Mensual");
        temporalDisaggregationTypes.add("Diaria");
    }

    public void changeDateB() {
        int diferenceMonths = getDateDifference(initialDateA, endDateA, "mensual");
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(initialDateB);
        cal1.add(Calendar.MONTH, diferenceMonths);
        endDateB = cal1.getTime();
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

    private boolean validateDateRange() {

        c1A.setTime(initialDateA);
        c2A.setTime(endDateA);
        c1B.setTime(initialDateB);
        c2B.setTime(endDateB);

        //fecha no puede ser menor a 2002 ni mayor al año del sistema
        if (c1A.get(Calendar.YEAR) < 2002 || c1A.get(Calendar.YEAR) > currentYear) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial del rango A debe estar entre el año 2002 y " + currentYear);
            return false;
        }
        if (c2A.get(Calendar.YEAR) < 2002 || c2A.get(Calendar.YEAR) > currentYear) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha final del rango A debe estar entre el año 2002 y " + currentYear);
            return false;
        }
        if (c1B.get(Calendar.YEAR) < 2002 || c1B.get(Calendar.YEAR) > currentYear) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial del rango B debe estar entre el año 2002 y " + currentYear);
            return false;
        }
        if (c2B.get(Calendar.YEAR) < 2002 || c2B.get(Calendar.YEAR) > currentYear) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha final del rango B debe estar entre el año 2002 y " + currentYear);
            return false;
        }

        //fecha inicial no sea superior a fecha final
        if (c1A.compareTo(c2A) > 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango B la fecha inicial debe ser inferior o igual a la fecha final");
            return false;
        } else {
            initialDateStrA = formato.format(initialDateA);
            endDateStrA = formato.format(endDateA);
        }
        if (c1B.compareTo(c2B) > 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango B la fecha inicial debe ser inferior o igual a la fecha final");
            return false;
        } else {
            initialDateStrB = formato.format(initialDateB);
            endDateStrB = formato.format(endDateB);
        }
        //se cumpla con la desagrgacion temporal
        if (currentTemporalDisaggregation.compareTo("Diaria") == 0) {
            if (getDateDifference(initialDateA, endDateA, "diaria") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango A debe haber como mínimo 1 dia");
                return false;
            }
        }
//        if (currentTemporalDisaggregation.compareTo("Semanal") == 0) {
//            if (getDateDifference(initialDateA, endDateA, "semanal") < 1) {
//                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango A debe haber como mínimo 1 semana");
//                return false;
//            }
//        }
        if (currentTemporalDisaggregation.compareTo("Mensual") == 0) {
            if (getDateDifference(initialDateA, endDateA, "mensual") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango A debe haber como mínimo 1 mes");
                return false;
            }
        }
        if (currentTemporalDisaggregation.compareTo("Anual") == 0) {
            if (getDateDifference(initialDateA, endDateA, "anual") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango A debe haber como mínimo 1 año ");
                return false;
            }
        }
        return true;
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
                + "       indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " OR \n\r" //datos ordenados completos(los que tienen y no tienen conteo )
                + "       indicator_id = " + (currentIndicator.getIndicatorId() + 200) + " \n\r" //ocurrencias
                + "    ) \n\r";
        //System.out.println("ELIMINACIONES \n " + sql);
        connectionJdbcMB.non_query(sql);
    }

    public void process() {
        message = null;
        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";
        boolean continueProcess = validateDateRange();//VALIDACION DE FECHAS
        if (continueProcess) {//ELIMINO DATOS DE UN PROCESO ANTERIOR
            removeIndicatorRecords();
        }

        if (variablesCrossList.size() <= numberCross) {//NUMERO DE VARIABLES A CRUZAR SEA MENOR O IGUAL AL LIMITE ESTABLECIDO
            continueProcess = true;
        } else {
            continueProcess = false;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber " + numberCross + " o menos variables");
        }

        //------------------------------------------------------
        //---------------- RANGO A -----------------------------
        //------------------------------------------------------
        if (continueProcess) {//AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
            variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar            
            variablesCrossData.add(createTemporalDisaggregationVariable(initialDateA, endDateA));//variable de desagregacion temporal
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
        if (continueProcess) {//ALMACENO EN BASE DE DATOS LOS REGISTROS DEL RANGO A
            saveIndicatorRecords(createIndicatorConsult(initialDateStrA, endDateStrA), 200);
        }
        if (continueProcess) {//CREO TODAS LAS POSIBLES COMBINACIONES DEL RANGO A
            createCombinations(200, 0);//se determina si las 200 tienen "sin dato" y genera cobinaciones en 100
        }
        if (continueProcess) {//AGRUPO LOS VALORES DEL RANGO A
            groupingOfValues(200, 0);//agrupa los datos de 200; actuliza los datos de 100;elimina los datos de 200
        }
        //------------------------------------------------------
        //---------------- RANGO B -----------------------------
        //------------------------------------------------------
        if (continueProcess) {//AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
            variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar            
            variablesCrossData.add(createTemporalDisaggregationVariable(initialDateB, endDateB));//variable de desagregacion temporal
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
        if (continueProcess) {//ALMACENO EN BASE DE DATOS LOS REGISTROS DEL RANGO B
            saveIndicatorRecords(createIndicatorConsult(initialDateStrB, endDateStrB), 200);
        }
        if (continueProcess) {//CREO TODAS LAS POSIBLES COMBINACIONES DEL RANGO B
            createCombinations(200, 100);//se determina si las 200 tienen "sin dato" y genera cobinaciones en 0
        }
        if (continueProcess) {//AGRUPO LOS VALORES DEL RANGO B
            groupingOfValues(200, 100);//agrupa los datos de 200; actuliza los datos de 100;elimina los datos de 0
        }
        if (continueProcess) {//CONSTRUYO LA MATRIZ RESULTANTE PARA B
            createMatrixDifference();//matriz de resultados diferencia
        }
        if (continueProcess) {
            dataTableHtmlA = createDataTableResult();
            loadValuesGraph();//creo el grafico
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze de variacion realizado");
        }
    }

    private void createCombinations(int incrementTarget, int incrementSource) {
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
                            + "    indicator_id = " + (currentIndicator.getIndicatorId() + incrementTarget) + " \n\r";
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
                            + "    indicator_id = " + (currentIndicator.getIndicatorId() + incrementTarget) + " \n\r";
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
                            + "    indicator_id = " + (currentIndicator.getIndicatorId() + incrementTarget) + " \n\r";
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
                            append(currentIndicator.getIndicatorId() + incrementSource).append("\t").
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
                                append(currentIndicator.getIndicatorId() + incrementSource).append("\t").
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
                                    append(currentIndicator.getIndicatorId() + incrementSource).append("\t").
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

    private void saveIndicatorRecords(String sqlConsult, int increment) {
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
                            append(currentIndicator.getIndicatorId() + increment).append("\t").
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

    private void groupingOfValues(int incrementTarget, int incrementSource) {
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
                + "     indicator_id = " + (currentIndicator.getIndicatorId() + incrementTarget) + " \n\r"
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
                        + "    indicator_id = " + (currentIndicator.getIndicatorId() + incrementSource) + " AND \n\r"
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
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + incrementTarget) + " \n\r";
            connectionJdbcMB.non_query(sql);//elimino los valores del indicador incrementTarget
        } catch (Exception e) {
        }
    }

    private String createIndicatorConsult(String initialDateStr, String endDateStr) {
        String sqlReturn;
        sqlReturn = " SELECT  \n\r";
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
            }
            if (i == variablesCrossData.size() - 1) {//si es la ultima instruccion se agrega salto de linea
                sqlReturn = sqlReturn + " \n\r";
            } else {//si no es la ultima instruccion se agrega coma y salto de linea
                sqlReturn = sqlReturn + ", \n\r";
            }
        }
        sqlReturn = sqlReturn + ""
                + "   FROM  \n\r"
                + "       " + currentIndicator.getInjuryType() + ", victims \n\r"
                + "   WHERE  \n\r"
                + "       " + currentIndicator.getInjuryType() + ".victim_id = victims.victim_id AND \n\r";
        if (currentIndicator.getIndicatorId() > 4) { //si no es general se filtra por tipo de lesion
            sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_id = " + currentIndicator.getInjuryId().toString() + " AND \n\r";
        }
        sqlReturn = sqlReturn + ""
                + "       " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n\r"
                + "       " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy'); ";
        //System.out.println("TODOS LOS DATOS \n " + sqlReturn);

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
                        //variablesListData.get(i).filterYear(initialDate, endDate);
                    }
                    currentCategoricalValuesList = variablesListData.get(i).getValuesConfigured();
                    currentVariableConfiguring = variablesListData.get(i);
                    btnAddCategoricalValueDisabled = !currentVariableConfiguring.isConfigurable();
                    break;
                }
            }
        }

    }

    public void changeVariableCross() {
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
        try {
            JFreeChart chart = null;
            //if (graphType1) {
            //    chart = createAreaChart();
            //} else {
            chart = createBarChart();
            //}
            File chartFile = new File("dynamichart");
            ChartUtilities.saveChartAsPNG(chartFile, chart, 700, 500);
            chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
        } catch (Exception e) {
        }
    }

    public void reset() {
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
        //graphTypes = getGraphTypes();
        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";
        numberCross = currentIndicator.getNumberCross();
        variablesList = new ArrayList<String>();//SelectItem[variablesListData.size()];
        for (int i = 0; i < variablesListData.size(); i++) {
            variablesList.add(variablesListData.get(i).getName());
        }
        //dynamicDataTableGroup = new OutputPanel();//creo el panel grid
        dataTableHtmlA = "";
        dataTableHtmlB = "";
        dataTableHtmlDiference = "";
        chartImage = null;
        variablesCrossList = new ArrayList<String>();//SelectItem[variablesListData.size()];
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        currentVariablesSelected = null;
        currentVariablesCrossSelected = null;
    }

    private Variable createTemporalDisaggregationVariable(Date initialDate, Date endDate) {
        Variable newVariable = new Variable("Desagregación temporal", "temporalDisaggregation", false);
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

    private Variable createVariable(String name, String generic_table, boolean conf) {
        //conf me indica si es permitida la configuracion de esta variable
        Variable newVariable = new Variable(name, generic_table, conf);
        //cargo la lista de valores posibles
        ArrayList<String> valuesName = new ArrayList<String>();//NOMBRE DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(NOMBRE EN LA CATEGORIA)
        ArrayList<String> valuesId = new ArrayList<String>();  //IDENTIFICADORES DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(ID EN LA CATEGORIA)
        ArrayList<String> valuesConf = new ArrayList<String>();//NOMBRE DE LOS VALORES CONFIGURADOS POR EL USUARIO QUE PUEDE TOMAR LA VARIABLE        
        int infInt;
        int supInt;
        String infStr;
        String supStr;
        switch (VariablesEnum.convert(generic_table)) {//
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
            case neighborhoods://barrio
            case communes://comuna
            case corridors://corredor
            case areas://zona
            case genders://genero
            case days://dia semana
            case quadrants://cuadrante
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
                    currentIndicator.getIndicatorsVariablesList().get(i).getAddValues()));
        }
        return arrayReturn;
    }

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

    private String getMatrixValueA(String type, int col, int row) {
        String strReturn = "-1";

        try {
            if (type.compareTo("countXY") == 0) {//valor en la matriz            
                return matrixResultA[col][row];
            }
            if (type.compareTo("rowTotal") == 0) {//total por fila
                return totalsVerticalA.get(row);
            }
            if (type.compareTo("columnTotal") == 0) {//total columna
                return totalsHorizontalA.get(col);
            }
            if (type.compareTo("columnPercentageXY") == 0) {//porcentaje segun columna para una posicion de la matriz
                if (Double.parseDouble(totalsHorizontalA.get(col)) == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResultA[col][row]) * 100) / Double.parseDouble(totalsHorizontalA.get(col))).replace(",", ".");
                }
            }

            if (type.compareTo("rowPercentageXY") == 0) {//porcentaje segun fila para una posicion de la matriz
                if (Double.parseDouble(totalsVerticalA.get(row)) == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResultA[col][row]) * 100) / Double.parseDouble(totalsVerticalA.get(row))).replace(",", ".");
                }
            }
//            if (type.compareTo("rowPercentageTotal") == 0) {//porcentaje segun fila para un total horizontal
//                if (Double.parseDouble(totalsHorizontal.get(col)) == 0) {
//                    return "0";
//                } else {
//                    return formateador.format((Double.parseDouble(totalsHorizontal.get(col)) * 100) / (double) grandTotal);
//                }
//            }
            if (type.compareTo("totalPercentageXY") == 0) {//porcentaje de una posicion de la matriz segun el total general 
                if (grandTotalA == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResultA[col][row]) * 100) / (double) grandTotalA).replace(",", ".");
                }
            }
            if (type.compareTo("percentageOfTotalRowAccordingTotalRow") == 0) {//porcentaje del total de una fila segun el total de la fila                
                if (Double.parseDouble(totalsVerticalA.get(row)) == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
            if (type.compareTo("percentageOfTotalColumnAccordingTotalColumn") == 0) {//porcentaje del total de una columna segun el total de la columna                
                if (Double.parseDouble(totalsHorizontalA.get(col)) == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
            if (type.compareTo("percentageOfTotalRowAccordingGrandTotal") == 0) {//porcentaje del total de una fila segun el total general            
                if (grandTotalA == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(totalsVerticalA.get(row)) * 100) / (double) grandTotalA).replace(",", ".");
                }
            }
            if (type.compareTo("percentageOfTotalColumnAccordingGrandTotal") == 0) {//porcentaje del total de una columna segun el total general
                if (grandTotalA == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(totalsHorizontalA.get(col)) * 100) / (double) grandTotalA).replace(",", ".");
                }
            }
            if (type.compareTo("percentageOfGrandTotalAccordingGrandTotal") == 0) {//porcentaje del gran total en base al gran total
                if (grandTotalA == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
        } catch (Exception e) {
            System.out.println("Error sacando valores de la matriz: " + e.toString());
        }
        return strReturn;
    }

    private String getMatrixValueB(String type, int col, int row) {
        String strReturn = "-1";

        //DecimalFormat formateador = new DecimalFormat("0.00");
        try {
            if (type.compareTo("countXY") == 0) {//valor en la matriz            
                return matrixResultB[col][row];
            }
            if (type.compareTo("rowTotal") == 0) {//total por fila
                return totalsVerticalB.get(row);
            }
            if (type.compareTo("columnTotal") == 0) {//total columna
                return totalsHorizontalB.get(col);
            }
            if (type.compareTo("columnPercentageXY") == 0) {//porcentaje segun columna para una posicion de la matriz
                if (Double.parseDouble(totalsHorizontalB.get(col)) == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResultB[col][row]) * 100) / Double.parseDouble(totalsHorizontalB.get(col))).replace(",", ".");
                }
            }

            if (type.compareTo("rowPercentageXY") == 0) {//porcentaje segun fila para una posicion de la matriz
                if (Double.parseDouble(totalsVerticalB.get(row)) == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResultB[col][row]) * 100) / Double.parseDouble(totalsVerticalB.get(row))).replace(",", ".");
                }
            }
//            if (type.compareTo("rowPercentageTotal") == 0) {//porcentaje segun fila para un total horizontal
//                if (Double.parseDouble(totalsHorizontal.get(col)) == 0) {
//                    return "0";
//                } else {
//                    return formateador.format((Double.parseDouble(totalsHorizontal.get(col)) * 100) / (double) grandTotal);
//                }
//            }
            if (type.compareTo("totalPercentageXY") == 0) {//porcentaje de una posicion de la matriz segun el total general 
                if (grandTotalB == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResultB[col][row]) * 100) / (double) grandTotalB).replace(",", ".");
                }
            }
            if (type.compareTo("percentageOfTotalRowAccordingTotalRow") == 0) {//porcentaje del total de una fila segun el total de la fila                
                if (Double.parseDouble(totalsVerticalB.get(row)) == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
            if (type.compareTo("percentageOfTotalColumnAccordingTotalColumn") == 0) {//porcentaje del total de una columna segun el total de la columna                
                if (Double.parseDouble(totalsHorizontalB.get(col)) == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
            if (type.compareTo("percentageOfTotalRowAccordingGrandTotal") == 0) {//porcentaje del total de una fila segun el total general            
                if (grandTotalB == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(totalsVerticalB.get(row)) * 100) / (double) grandTotalB).replace(",", ".");
                }
            }
            if (type.compareTo("percentageOfTotalColumnAccordingGrandTotal") == 0) {//porcentaje del total de una columna segun el total general
                if (grandTotalB == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(totalsHorizontalB.get(col)) * 100) / (double) grandTotalB).replace(",", ".");
                }
            }
            if (type.compareTo("percentageOfGrandTotalAccordingGrandTotal") == 0) {//porcentaje del gran total en base al gran total
                if (grandTotalB == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
        } catch (Exception e) {
            System.out.println("Error sacando valores de la matriz: " + e.toString());
        }
        return strReturn;
    }

    private String determineHeader(String value) {
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
        headers2 = new String[columNamesFinal.size()];
        int posRow = 0;
        int posF;
        int posI;
        String strReturn = " ";
        String value;
        double totalA;
        double totalB;
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        //-------------------------------------------------------------------                
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja            
            posRow++;
            for (int i = 0; i < columNamesFinal.size(); i++) {
                celda = fila.createCell((short) i + 2);// +2 por que faltal las filas               
                texto = new HSSFRichTextString(determineHeader(columNamesFinal.get(i)));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNamesFinal.size(); i++) {
                splitVars = columNamesFinal.get(i).split("\\}");//separo las dos variables
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
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja
            posRow++;
            posF = 1;
            posI = 1;
            for (int j = 0; j < headers1.size(); j++) {
                posI = posF + 1;
                for (int i = 0; i < headers1.get(j).getColumns(); i++) {
                    posF++;
                }
                sheet.addMergedRegion(new CellRangeAddress(0, 0, posI, posF));
                //posF++;
            }
            short posColumn = 2;// +2 por que faltal las filas               
            for (int i = 0; i < headers1.size(); i++) {
                celda = fila.createCell(posColumn);
                posColumn = (short) (posColumn + headers1.get(i).getColumns());
                texto = new HSSFRichTextString(determineHeader(headers1.get(i).getLabel()));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja
            posRow++;
            for (int i = 0; i < headers2.length; i++) {
                celda = fila.createCell((short) i + 2);// +2 por que faltal las filas               
                texto = new HSSFRichTextString(determineHeader(headers2[i]));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
        }

        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------      
        int rowsForRecord = 0;//filas a crear por registro(inicia en 1 por el rowspan cuenta desde 1)
        if (showRowPercentage) {
            rowsForRecord++;
        }
        if (showCount) {
            rowsForRecord++;
        }
        posI = posRow;
        posF = posRow + 1;

        for (int j = 0; j < rowNames.size(); j++) {
            if (rowsForRecord == 2) {
                sheet.addMergedRegion(new CellRangeAddress(posI, posF, 0, 0));
                posI = posI + 2;
                posF = posF + 2;
                fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja            
                posRow++;
                celda = fila.createCell(0);
                celda.setCellValue(determineHeader(rowNames.get(j)));

                celda = fila.createCell(1);
                celda.setCellValue("Recuento");
                for (int i = 0; i < columNamesFinal.size(); i++) {
                    totalA = Double.parseDouble(getMatrixValueA("countXY", i, j));
                    totalB = Double.parseDouble(getMatrixValueB("countXY", i, j));
                    if (showCalculation) {
                        value = formateador.format(totalA - totalB) + "(" + formateador.format(totalA) + "-" + formateador.format(totalB) + ")";
                    } else {
                        value = formateador.format(totalA - totalB);
                    }
                    celda = fila.createCell((short) i + 2);// +2 por que faltal nombres de filas
                    celda.setCellValue(new HSSFRichTextString(value));
                }

                fila = sheet.createRow(posRow);
                posRow++;
                celda = fila.createCell(1);
                celda.setCellValue(new HSSFRichTextString("% por fila"));

                for (int i = 0; i < columNamesFinal.size(); i++) {
                    //value;
                    totalA = Double.parseDouble(getMatrixValueA("rowPercentageXY", i, j));
                    totalB = Double.parseDouble(getMatrixValueB("rowPercentageXY", i, j));
                    if (showCalculation) {
                        value = formateador.format(totalA - totalB) + " (" + formateador.format(totalA) + "-" + formateador.format(totalB) + ")";
                    } else {
                        value = formateador.format(totalA - totalB);
                    }
                    celda = fila.createCell((short) i + 2);// +2 por que faltal nombres de filas                            
                    celda.setCellValue(new HSSFRichTextString(value));
                }

            } else {
                fila = sheet.createRow(posRow);
                posRow++;

                celda = fila.createCell(0);
                celda.setCellValue(new HSSFRichTextString(determineHeader(rowNames.get(j))));

                celda = fila.createCell(1);
                celda.setCellValue(new HSSFRichTextString("% por fila"));

                for (int i = 0; i < columNamesFinal.size(); i++) {
                    //value;
                    totalA = Double.parseDouble(getMatrixValueA("rowPercentageXY", i, j));
                    totalB = Double.parseDouble(getMatrixValueB("rowPercentageXY", i, j));
                    if (showCalculation) {
                        value = formateador.format(totalA - totalB) + " (" + formateador.format(totalA) + "-" + formateador.format(totalB) + ")";
                    } else {
                        value = formateador.format(totalA - totalB);
                    }
                    celda = fila.createCell((short) i + 2);// +2 por que faltal nombres de columnas               
                    celda.setCellValue(new HSSFRichTextString(value));
                }
            }
        }
    }

    private String createDataTableResult() {

        headers1 = new ArrayList<SpanColumns>();
        headers2 = new String[columNamesFinal.size()];

        String strReturn = " ";
        strReturn = strReturn + "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td id=\"firstTd\" >\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td class=\"ui-widget-header\">\r\n";
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        //-------------------------------------------------------------------        
        strReturn = strReturn + "                    <div id=\"divHeader\" style=\"overflow:hidden;width:434px;\">\r\n";//484 TAMAÑO DIV HEADER SUPERIOR(16=heigth del scroll)
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            //-------------------------------------------------------------------
            //CABECERA SIMPLE
            strReturn = strReturn + "                            <tr>\r\n";
            for (int i = 0; i < columNamesFinal.size(); i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:150px;\">" + determineHeader(columNamesFinal.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
//            strReturn = strReturn + "                                <td>\r\n";
//            strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:150px;\">Total</div>\r\n";
//            strReturn = strReturn + "                                </td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNamesFinal.size(); i++) {
                splitVars = columNamesFinal.get(i).split("\\}");//separo las dos variables
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
//            strReturn = strReturn + "                                <td >\r\n";
//            strReturn = strReturn + "                                    <div >-</div>\r\n";
//            strReturn = strReturn + "                                </td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";

            strReturn = strReturn + "                            <tr>\r\n";
            //AGREGO LA CABECERA 2 A El PANEL_GRID
            for (int i = 0; i < headers2.length; i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:150px;\">" + determineHeader(headers2[i]) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
//            strReturn = strReturn + "                                <td >\r\n";
//            strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:150px;\">Total</div>\r\n";
//            strReturn = strReturn + "                                </td>\r\n";
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
        //-------------------------------------------------------------------        
        int rowsForRecord = 0;//filas a crear por registro(inicia en 1 por el rowspan cuenta desde 1)
//        if (showColumnPercentage) {
//            rowsForRecord++;
//        }
        if (showRowPercentage) {
            rowsForRecord++;
        }
        if (showCount) {
            rowsForRecord++;
        }
//        if (showTotalPercentage) {
//            rowsForRecord++;
//        }

        strReturn = strReturn + "                    <div id=\"firstcol\" style=\"overflow: hidden;height:280px\">\r\n";//tamaño del div izquierdo
        strReturn = strReturn + "                        <table width=\"200px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";

        //rowNames.add("Totales");
        for (int j = 0; j < rowNames.size(); j++) {
            //----------------------------------------------------------------------
            //NOMBRE PARA CADA FILA            
            boolean showCountAdd = false;
            boolean showRowPercentageAdd = false;
            boolean showColumnPercentageAdd = false;
            boolean showTotalPercentageAdd = false;
            strReturn = strReturn + "                            <tr>\r\n";
            strReturn = strReturn + "                                <td rowspan=\"" + rowsForRecord + "\">" + determineHeader(rowNames.get(j)) + "</td>\r\n";
            if (showCount && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">Recuento</td>\r\n";
                showCountAdd = true;
            }
            if (showRowPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% por fila</td>\r\n";
                showRowPercentageAdd = true;
            }
//            if (showColumnPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
//                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% por columna</td>\r\n";
//                showColumnPercentageAdd = true;
//            }
//            if (showTotalPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
//                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% del total</td>\r\n";
//                showTotalPercentageAdd = true;
//            }
            strReturn = strReturn + "                            </tr>\r\n";
            //
            if (showCount && !showCountAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">recuento</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showRowPercentage && !showRowPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% por fila</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
//            if (showColumnPercentage && !showColumnPercentageAdd) {
//                strReturn = strReturn + "                            <tr>\r\n";
//                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% por columna</td>\r\n";
//                strReturn = strReturn + "                            </tr>\r\n";
//            }
//            if (showTotalPercentage && !showTotalPercentageAdd) {
//                strReturn = strReturn + "                            <tr>\r\n";
//                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% del total</td>\r\n";
//                strReturn = strReturn + "                            </tr>\r\n";
//            }
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td valign=\"top\">\r\n";

        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------      
        strReturn = strReturn + "                    <div id=\"table_div\" style=\"overflow: scroll;width:450px;height:300px;position:relative\" onscroll=\"fnScroll()\" >\r\n";//div que maneja la tabla
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";
        //----------------------------------------------------------------------
        String value;
        double totalA;
        double totalB;
        boolean firstTrAdd = false;

        //AGREGO LOS REGISTROS DE LA MATRIZ        
        for (int j = 0; j < rowNames.size(); j++) {//-1 por que le agrege "TOTALES"
            if (showCount) {
                if (j == 0 && !firstTrAdd) {
                    strReturn = strReturn + "                            <tr " + getColorType() + " id='firstTr'>\r\n";
                    firstTrAdd = true;
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNamesFinal.size(); i++) {
                    totalA = Double.parseDouble(getMatrixValueA("countXY", i, j));
                    totalB = Double.parseDouble(getMatrixValueB("countXY", i, j));
                    if (showCalculation) {
                        value = "<b>" + formateador.format(totalA - totalB) + "</b><br/>(" + formateador.format(totalA) + "-" + formateador.format(totalB) + ")";
                    } else {
                        value = formateador.format(totalA - totalB);
                    }
                    strReturn = strReturn + "                                <td><div style=\"width:150px;\">" + value + "</div></td>\r\n";
                }
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showRowPercentage) {
                if (j == 0 && !firstTrAdd) {
                    strReturn = strReturn + "                            <tr " + getColorType() + " id='firstTr'>\r\n";
                    firstTrAdd = true;
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNamesFinal.size(); i++) {
                    //value;
                    totalA = Double.parseDouble(getMatrixValueA("rowPercentageXY", i, j));
                    totalB = Double.parseDouble(getMatrixValueB("rowPercentageXY", i, j));
                    if (showCalculation) {
                        value = "<b>" + formateador.format(totalA - totalB) + "</b><br/>(" + formateador.format(totalA) + "-" + formateador.format(totalB) + ")";
                    } else {
                        value = formateador.format(totalA - totalB);
                    }
                    strReturn = strReturn + "                                <td><div style=\"width:150px;\">" + value + "</div></td>\r\n";
                }
                strReturn = strReturn + "                            </tr>\r\n";
            }
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

    private JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String serieName = "";
        ResultSet rs;
        ResultSet rs2;
        ResultSet rs3;
        ResultSet rs4;
        String sql1;
        String sql2;
        String sql3;
        String sql4;
        double increment = 0;
        try {
            sql1 = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql1 = sql1 + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                serieName = "\n" + currentVariableGraph1 + " es " + currentValueGraph1;
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql1 = sql1 + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                serieName = serieName + " - " + currentVariableGraph2 + " es " + currentValueGraph2;
            }
            sql3 = ""
                    + " SELECT \n"
                    + "    SUM(count) \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql3 = sql3 + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                serieName = "\n" + currentVariableGraph1 + " es " + currentValueGraph1;
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql3 = sql3 + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                serieName = serieName + " - " + currentVariableGraph2 + " es " + currentValueGraph2;
            }

            sql2 = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql2 = sql2 + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                serieName = "\n" + currentVariableGraph1 + " es " + currentValueGraph1;
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql2 = sql2 + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                serieName = serieName + " - " + currentVariableGraph2 + " es " + currentValueGraph2;
            }

            sql4 = ""
                    + " SELECT \n"
                    + "    SUM(count) \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql4 = sql4 + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                serieName = "\n" + currentVariableGraph1 + " es " + currentValueGraph1;
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql4 = sql4 + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                serieName = serieName + " - " + currentVariableGraph2 + " es " + currentValueGraph2;
            }
//            if (serieName.length() == 0) {
//                serieName = "Casos";
//            }
            rs = connectionJdbcMB.consult(sql1 + " ORDER BY record_id");
            rs2 = connectionJdbcMB.consult(sql2 + " ORDER BY record_id");
            rs3 = connectionJdbcMB.consult(sql3);
            rs4 = connectionJdbcMB.consult(sql4);

            rs3.next();
            rs4.next();

            String strDateName;
            double valor;
            double valor2;
            int totalA = rs3.getInt(1);
            int totalB = rs4.getInt(1);

            while (rs.next()) {
                rs2.next();
                //if (rs != null && rs2 != null) {
                valor = (double) (rs.getInt("count") * 100) / (double) totalA;
                valor2 = (double) (rs2.getInt("count") * 100) / (double) totalB;

                valor = valor - valor2;
                if (increment < Math.sqrt(valor * valor)) {
                    increment = Math.sqrt(valor * valor);
                }
                strDateName = rs.getString("column_1") + " - " + rs2.getString("column_1");
                dataset.setValue(valor, "-", strDateName);
                //}
                //dataset.setValue(rs2.getLong("count"), "Rango B", strDateName);
            }
            increment = increment * 0.005;//grosor linea

        } catch (SQLException ex) {
            //System.out.println("Error: " + ex.toString());
            increment = increment * 0.005;//grosor linea
        }

        final JFreeChart chart = ChartFactory.createBarChart(
                "Variación porcentual de casos", // chart title
                "Fecha", // domain axis label
                "Value", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                false, // include legend
                true, // tooltips
                false // urls
                );

        chart.setBackgroundPaint(Color.white);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        final TextTitle subtitle = new TextTitle(
                "Rango A: (" + sdf.format(initialDateA) + " - " + sdf.format(endDateA) + ")\t\n"
                + "Rango B: (" + sdf.format(initialDateB) + " - " + sdf.format(endDateB) + ")" + serieName);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setForegroundAlpha(0.5f);
        ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());//quitar gradiente

        IntervalMarker intervalmarker = new IntervalMarker(-1 * increment, increment, Color.yellow);
        plot.addRangeMarker(intervalmarker);

        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        if (showItems) {
            CategoryItemRenderer renderer = plot.getRenderer();
            CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00"));//DecimalFormat("0.00"));
            //CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{3}", NumberFormat.getIntegerInstance(), new DecimalFormat("0.00%"));

            renderer.setItemLabelGenerator(generator);
            renderer.setItemLabelsVisible(true);
        }

        return chart;
    }

    private JFreeChart createAreaChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String serieName = "";
        ResultSet rs;
        ResultSet rs2;
        ResultSet rs3;
        ResultSet rs4;
        String sql1;
        String sql2;
        String sql3;
        String sql4;
        double increment = 0;
        try {
            sql1 = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql1 = sql1 + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                serieName = "\n" + currentVariableGraph1 + " es " + currentValueGraph1;
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql1 = sql1 + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                serieName = serieName + " - " + currentVariableGraph2 + " es " + currentValueGraph2;
            }

            sql3 = ""
                    + " SELECT \n"
                    + "    SUM(count) \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql1 = sql1 + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                serieName = "\n" + currentVariableGraph1 + " es " + currentValueGraph1;
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql1 = sql1 + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                serieName = serieName + " - " + currentVariableGraph2 + " es " + currentValueGraph2;
            }


            sql2 = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql2 = sql2 + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                serieName = "\n" + currentVariableGraph1 + " es " + currentValueGraph1;
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql2 = sql2 + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                serieName = serieName + " - " + currentVariableGraph2 + " es " + currentValueGraph2;
            }

            sql4 = ""
                    + " SELECT \n"
                    + "    SUM(count) \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql2 = sql2 + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                serieName = "\n" + currentVariableGraph1 + " es " + currentValueGraph1;
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql2 = sql2 + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                serieName = serieName + " - " + currentVariableGraph2 + " es " + currentValueGraph2;
            }
//            if (serieName.length() == 0) {
//                serieName = "Casos";
//            }
            rs = connectionJdbcMB.consult(sql1 + " ORDER BY record_id");
            rs2 = connectionJdbcMB.consult(sql2 + " ORDER BY record_id");
            rs3 = connectionJdbcMB.consult(sql3);
            rs4 = connectionJdbcMB.consult(sql4);

            rs3.next();
            rs4.next();

            String strDateName;
            double valor;
            double valor2;
            int totalA = rs3.getInt(1);
            int totalB = rs4.getInt(1);

            while (rs.next()) {
                rs2.next();
                //if (rs != null && rs2 != null) {
                valor = (double) (rs.getInt("count") * 100) / (double) totalA;
                valor2 = (double) (rs2.getInt("count") * 100) / (double) totalB;

                valor = valor - valor2;
                if (increment < Math.sqrt(valor * valor)) {
                    increment = Math.sqrt(valor * valor);
                }
                strDateName = rs.getString("column_1") + " - " + rs2.getString("column_1");
                dataset.setValue(valor, "-", strDateName);
                //}
                //dataset.setValue(rs2.getLong("count"), "Rango B", strDateName);
            }
            increment = increment * 0.005;//grosor linea

        } catch (SQLException ex) {
            //System.out.println("Error: " + ex.toString());
            increment = increment * 0.005;//grosor linea
        }

        final JFreeChart chart = ChartFactory.createAreaChart(
                "Variacion de casos", // chart title
                "Fecha", // domain axis label
                "Value", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );

        chart.setBackgroundPaint(Color.white);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        final TextTitle subtitle = new TextTitle(
                "Rango A: (" + sdf.format(initialDateA) + " - " + sdf.format(endDateA) + ")\t\n"
                + "Rango B: (" + sdf.format(initialDateB) + " - " + sdf.format(endDateB) + ")" + serieName);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setForegroundAlpha(0.5f);

        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.addCategoryLabelToolTip("Type 1", "The first type.");
        domainAxis.addCategoryLabelToolTip("Type 2", "The second type.");
        domainAxis.addCategoryLabelToolTip("Type 3", "The third type.");

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLabelAngle(0 * Math.PI / 2.0);

//        NumberAxis xAxis2 = new NumberAxis("Domain Axis 2");
//        xAxis2.setAutoRangeIncludesZero(false);
//        plot.setSecondaryDomainAxis(0, xAxis2);
//        plot.setSecondaryDomainAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);


        if (showItems) {
            CategoryItemRenderer renderer = plot.getRenderer();
            CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00"));//DecimalFormat("0.00"));
            renderer.setItemLabelGenerator(generator);
            renderer.setItemLabelsVisible(true);
        }

        return chart;
    }

    private void createMatrixDifference() {
        //System.out.println("INICIA CREAR MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        try {
            ArrayList<String> columnNamesPivot = new ArrayList<String>();
            columNames = new ArrayList<String>();
            columNamesFinal = new ArrayList<String>();
            rowNames = new ArrayList<String>();
            int totalA;
            int totalB;
            ResultSet rs = null;
            ResultSet rs2;
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
                columNamesFinal.add(rs.getString(1));
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

            matrixResultA = new String[columNames.size()][rowNames.size()];
            matrixResultB = new String[columNames.size()][rowNames.size()];
            for (int i = 0; i < columNames.size(); i++) {
                for (int j = 0; j < rowNames.size(); j++) {
                    matrixResultA[i][j] = "0";
                    matrixResultB[i][j] = "0";
                }
            }

            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 0) + "  \n"
                    + " ORDER BY \n"
                    + "    record_id \n";
            rs = connectionJdbcMB.consult(sql);
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + "  \n"
                    + " ORDER BY \n"
                    + "    record_id \n";
            rs2 = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                rs2.next();
                boolean find = false;
                for (int i = 0; i < columNames.size(); i++) {
                    for (int j = 0; j < rowNames.size(); j++) {
                        if (variablesCrossData.size() == 1) {//ES UNA VARIABLE                            
                            if (rs.getString("column_1").compareTo(columNames.get(i)) == 0) {
                                totalA = Integer.parseInt(rs.getString("count"));
                                totalB = Integer.parseInt(rs2.getString("count"));
                                matrixResultA[i][j] = rs.getString("count");
                                matrixResultB[i][j] = rs2.getString("count");
                                columNamesFinal.set(i, rs.getString("column_1") + " - " + rs2.getString("column_1"));
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 2) {//SON DOS VARIABLES                            
                            if (rs.getString("column_1").compareTo(columNames.get(i)) == 0 && rs.getString("column_2").compareTo(rowNames.get(j)) == 0) {
                                totalA = Integer.parseInt(rs.getString("count"));
                                totalB = Integer.parseInt(rs2.getString("count"));
                                matrixResultA[i][j] = rs.getString("count");
                                matrixResultB[i][j] = rs2.getString("count");
                                columNamesFinal.set(i, rs.getString("column_1") + " - " + rs2.getString("column_1"));
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 3) {//SON TRES VARIABLES                            
                            if (columNames.get(i).compareTo(rs.getString("column_1") + "}" + rs.getString("column_2")) == 0 && rs.getString("column_3").compareTo(rowNames.get(j)) == 0) {
                                totalA = Integer.parseInt(rs.getString("count"));
                                totalB = Integer.parseInt(rs2.getString("count"));
                                matrixResultA[i][j] = rs.getString("count");
                                matrixResultB[i][j] = rs2.getString("count");
                                columNamesFinal.set(i, rs.getString("column_1") + " - " + rs2.getString("column_1") + "}" + rs.getString("column_2"));
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

        //---------------------------------------------------------            
        //DETERMINO LOS VECTORES TOTALES DE FILAS Y TOTALES DE COLUMNAS
        //---------------------------------------------------------            
        //System.out.println("INICIA DETERMINO LOS VECTORES TOTALES DE FILAS Y TOTALES DE COLUMNAS xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        totalsHorizontalA = new ArrayList<String>();
        totalsVerticalA = new ArrayList<String>();
        totalsHorizontalB = new ArrayList<String>();
        totalsVerticalB = new ArrayList<String>();
        for (int i = 0; i < columNames.size(); i++) {
            totalsHorizontalA.add("0");
            totalsHorizontalB.add("0");
        }
        int totalA;
        int totalB;
        for (int j = 0; j < rowNames.size(); j++) {
            totalA = 0;
            totalB = 0;
            for (int i = 0; i < columNames.size(); i++) {
                String valueA = matrixResultA[i][j];
                String valueB = matrixResultB[i][j];
                totalsHorizontalA.set(
                        i,
                        String.valueOf(
                        Integer.parseInt(totalsHorizontalA.get(i))
                        + Integer.parseInt(valueA)));
                totalsHorizontalB.set(
                        i,
                        String.valueOf(
                        Integer.parseInt(totalsHorizontalB.get(i))
                        + Integer.parseInt(valueB)));

                totalA = totalA + Integer.parseInt(valueA);
                totalB = totalB + Integer.parseInt(valueB);
            }
            totalsVerticalA.add(String.valueOf(totalA));
            totalsVerticalB.add(String.valueOf(totalB));
        }
        //determino totales generales total
        grandTotalA = 0;
        for (int i = 0; i < totalsVerticalA.size(); i++) {
            grandTotalA = grandTotalA + Integer.parseInt(totalsVerticalA.get(i));
        }
        grandTotalB = 0;
        for (int i = 0; i < totalsVerticalB.size(); i++) {
            grandTotalB = grandTotalB + Integer.parseInt(totalsVerticalB.get(i));
        }
    }

    public void changeGraphType1() {
        if (graphType1 == false) {
            graphType2 = true;
        } else {
            graphType2 = false;
        }
        createImage();
    }

    public void changeGraphType2() {
        if (graphType2 == false) {
            graphType1 = true;
        } else {
            graphType1 = false;
        }
        createImage();
    }

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //FUNCIOES PARA REALIZAR LA CARGA DE UN INDICADOR
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    private void loadIndicator(int n) {
        currentIndicator = indicatorsFacade.find(n);
        reset();
    }

    public void loadIndicator7() {
        loadIndicator(7);
    }

    public void loadIndicator14() {
        loadIndicator(14);
    }

    public void loadIndicator21() {
        loadIndicator(21);
    }

    public void loadIndicator28() {
        loadIndicator(28);
    }

    public void loadIndicator35() {
        loadIndicator(35);
    }

    public void loadIndicator42() {
        loadIndicator(42);
    }

    public void loadIndicator49() {
        loadIndicator(49);
    }

    public void loadIndicator56() {
        loadIndicator(56);
    }

    public void loadIndicator63() {
        loadIndicator(63);
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

    public Date getInitialDateA() {
        return initialDateA;
    }

    public void setInitialDateA(Date initialDateA) {
        this.initialDateA = initialDateA;
    }

    public Date getEndDateA() {
        return endDateA;
    }

    public void setEndDateA(Date endDateA) {
        this.endDateA = endDateA;
    }

    public Date getEndDateB() {
        return endDateB;
    }

    public void setEndDateB(Date endDateB) {
        this.endDateB = endDateB;
    }

    public Date getInitialDateB() {
        return initialDateB;
    }

    public void setInitialDateB(Date initialDateB) {
        this.initialDateB = initialDateB;
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
    public boolean isRenderedDynamicDataTable() {
        return renderedDynamicDataTable;
    }

    public void setRenderedDynamicDataTable(boolean renderedDynamicDataTable) {
        this.renderedDynamicDataTable = renderedDynamicDataTable;
    }

    public String getCurrentValueGraph() {
        return currentValueGraph;
    }

    public void setCurrentValueGraph(String currentValueGraph) {
        this.currentValueGraph = currentValueGraph;
    }

    public String getCurrentVariableGraph() {
        return currentVariableGraph;
    }

    public void setCurrentVariableGraph(String currentVariableGraph) {
        this.currentVariableGraph = currentVariableGraph;
    }

    public List<String> getValuesGraph() {
        return valuesGraph;
    }

    public void setValuesGraph(List<String> valuesGraph) {
        this.valuesGraph = valuesGraph;
    }

    public List<String> getVariablesGraph() {
        return variablesGraph;
    }

    public void setVariablesGraph(List<String> variablesGraph) {
        this.variablesGraph = variablesGraph;
    }

    public String getDataTableHtmlA() {
        return dataTableHtmlA;
    }

    public void setDataTableHtmlA(String dataTableHtmlA) {
        this.dataTableHtmlA = dataTableHtmlA;
    }

    public String getDataTableHtmlB() {
        return dataTableHtmlB;
    }

    public void setDataTableHtmlB(String dataTableHtmlB) {
        this.dataTableHtmlB = dataTableHtmlB;
    }

    public String getDataTableHtmlDiference() {
        return dataTableHtmlDiference;
    }

    public void setDataTableHtmlDiference(String dataTableHtmlDiference) {
        this.dataTableHtmlDiference = dataTableHtmlDiference;
    }

    public boolean isShowCalculation() {
        return showCalculation;
    }

    public void setShowCalculation(boolean showCalculation) {
        this.showCalculation = showCalculation;
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

    public boolean isShowCount() {
        return showCount;
    }

    public void setShowCount(boolean showCount) {
        this.showCount = showCount;
    }

    public boolean isShowRowPercentage() {
        return showRowPercentage;
    }

    public void setShowRowPercentage(boolean showRowPercentage) {
        this.showRowPercentage = showRowPercentage;
    }

//    public boolean isShowColumnPercentage() {
//        return showColumnPercentage;
//    }
//
//    public void setShowColumnPercentage(boolean showColumnPercentage) {
//        this.showColumnPercentage = showColumnPercentage;
//    }
//
//    public boolean isShowTotalPercentage() {
//        return showTotalPercentage;
//    }
//
//    public void setShowTotalPercentage(boolean showTotalPercentage) {
//        this.showTotalPercentage = showTotalPercentage;
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

    public boolean isShowItems() {
        return showItems;
    }

    public void setShowItems(boolean showItems) {
        this.showItems = showItems;
    }

    public boolean isGraphType1() {
        return graphType1;
    }

    public void setGraphType1(boolean graphType1) {
        this.graphType1 = graphType1;
    }

    public boolean isGraphType2() {
        return graphType2;
    }

    public void setGraphType2(boolean graphType2) {
        this.graphType2 = graphType2;
    }
}
