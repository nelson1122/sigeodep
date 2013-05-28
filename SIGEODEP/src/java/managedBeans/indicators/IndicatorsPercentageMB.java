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
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import managedBeans.reports.SpanColumns;
import managedBeans.reports.TableGroup;
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
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.primefaces.component.column.Column;
import org.primefaces.component.row.Row;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsPercentageMB")
@SessionScoped
public class IndicatorsPercentageMB {

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
    private FacesMessage message = null;
    private ConnectionJdbcMB connectionJdbcMB;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    private String currentVariableGraph;
    private String currentValueGraph;
    private String dataTableHtml;
    private String firstVariablesCrossSelected = null;
    private String initialValue = "";
    private String endValue = "";
    private String[] headers2;//CABECERA 2 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private String[][] matrixResult;//MATRIZ DE RESULTADOS
    private Date initialDate = new Date();
    private Date endDate = new Date();
    private String initialDateStr;
    private String endDateStr;
    private LoginMB loginMB;
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
    private ArrayList<SpanColumns> dynamicHeaders1;
    private ArrayList<TableGroup> listaDeGrupos;
    private ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> rowNames;//NOMBRES DE LAS FILAS    
    private ArrayList<String> totalsHorizontal = new ArrayList<String>();
    private ArrayList<String> totalsVertical = new ArrayList<String>();
    private Variable currentVariableConfiguring;
    private int grandTotal = 0;//total general de la matriz
    private int numberCross = 2;//maximo numero de variables a cruzar
    private int currentYear = 0;
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    private boolean showItems = true;
    private String sql;//mostrar filas y columnas vacias
    private boolean showCount = true;//mostrar recuento
    private boolean showRowPercentage = true;//mostrar porcentaje por fila
    private boolean showColumnPercentage = true;//mostrar porcentaje por columna
    private boolean showTotalPercentage = true;//mostrar porcentaje del total
    private boolean btnExportDisabled = true;
    private boolean showEmpty = false;
    private boolean graphType1 = true;
    private boolean graphType2 = false;
    boolean colorType = true;
    private Integer tuplesProcessed = 0;
    private StringBuilder sb;
    private CopyManager cpManager;

    public IndicatorsPercentageMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        initialDate.setDate(1);
        initialDate.setMonth(0);
        initialDate.setYear(2003 - 1900);
        endDate.setDate(c.get(Calendar.DATE));
        endDate.setMonth(c.get(Calendar.MONTH));
        endDate.setYear(c.get(Calendar.YEAR) - 1900);
    }

    public void showMessage() {
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void loadValuesGraph() {
        valuesGraph = new ArrayList<String>();
        for (int i = 0; i < variablesCrossData.size(); i++) {
            if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {//cargo el combo de valores
                    valuesGraph.add(variablesCrossData.get(i).getValuesConfigured().get(j));
                    currentValueGraph = valuesGraph.get(0);
                }
                break;
            }
        }
        createImage();
    }

    public void createMatrixResult() {

        try {//System.out.println("INICIA CREAR MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            //ArrayList<String> columnNamesPivot = new ArrayList<String>();
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
            //---------------------------------------------------------            
            //INSERTAMOS DATOS EN MATRIZ
            //---------------------------------------------------------
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
            //---------------------------------------------------------            
            //DETERMINO LOS VECTORES TOTALES DE FILAS Y TOTALES DE COLUMNAS
            //---------------------------------------------------------            
            //System.out.println("INICIA DETERMINO LOS VECTORES TOTALES DE FILAS Y TOTALES DE COLUMNAS xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            totalsHorizontal = new ArrayList<String>();
            totalsVertical = new ArrayList<String>();
            for (int i = 0; i < columNames.size(); i++) {
                totalsHorizontal.add("0");
            }
            int total;
            for (int j = 0; j < rowNames.size(); j++) {
                //AGREGO LOS DATOS DE LA FILA
                total = 0;
                for (int i = 0; i < columNames.size(); i++) {
                    totalsHorizontal.set(i, String.valueOf(Integer.parseInt(totalsHorizontal.get(i)) + Integer.parseInt(matrixResult[i][j])));
                    total = total + Integer.parseInt(matrixResult[i][j]);
                }
                totalsVertical.add(String.valueOf(total));
            }
            //determino general total
            grandTotal = 0;
            for (int i = 0; i < totalsVertical.size(); i++) {
                grandTotal = grandTotal + Integer.parseInt(totalsVertical.get(i));
            }
        } catch (Exception e) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
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
        boolean continueProcess = true;
        message = null;
        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";

        if (continueProcess) {//VALIDACION DE FECHAS
            initialDateStr = formato.format(initialDate);
            endDateStr = formato.format(endDate);
        }
        if (continueProcess) {//NUMERO DE VARIABLES A CRUZAR SEA MENOR O IGUAL AL LIMITE ESTABLECIDO
            if (currentIndicator.getIndicatorId() < 5) {//es un indicador general
                if (variablesCrossList.size() <= numberCross) {
                    continueProcess = true;
                } else {
                    continueProcess = false;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber " + numberCross + " o menos variables");
                }
            } else {
                if (variablesCrossList.size() < 4 && variablesCrossList.size() > 0) {
                    continueProcess = true;
                } else {
                    continueProcess = false;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber minimo 1 y maximo 3 variables");
                }
            }
        }
        if (continueProcess) {//SI ES INDICADOR GENERAL AGREGO UNA NUEVA VARIABLE A CRUZAR(tipo lesion)
            if (currentIndicator.getIndicatorId() == 1 || currentIndicator.getIndicatorId() == 2) {
                //agrego a la lista de variables a cruzar "tipo de lesion fatal"
                Variable newVariable = createVariable("Tipo Lesion", "injuries_fatal", false, "");
                variablesCrossData.add(newVariable);
            }
            if (currentIndicator.getIndicatorId() == 3 || currentIndicator.getIndicatorId() == 4) {
                //agrego a la lista de variables a cruzar "tipo de lesion fatal"
                Variable newVariable = createVariable("Tipo Lesion", "injuries_non_fatal", false, "");
                variablesCrossData.add(newVariable);
            }
        }
        if (continueProcess) {//AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
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
        if (continueProcess) {//CARGO LOS COMBOS PARA EL GRAFICO
            if (variablesCrossData.size() == 3) {
                for (int i = 0; i < variablesCrossData.size(); i++) {
                    if (i == 0) {
                        variablesGraph.add(variablesCrossData.get(i).getName());
                        currentVariableGraph = variablesGraph.get(0);
                        //cargo el combo de valores
                        for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                            valuesGraph.add(variablesCrossData.get(i).getValuesConfigured().get(j));
                            currentValueGraph = valuesGraph.get(0);
                        }
                    } else {
                        variablesGraph.add(variablesCrossData.get(i).getName());
                    }
                }
            }
        }

        if (continueProcess) {
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
        if (continueProcess) {//GENERO TABLA E IMAGEN
            dataTableHtml = createDataTableResult();
            createImage();//creo el grafico
            btnExportDisabled = false;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado");
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
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
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
            //CREO NUEVOS VECTORES DE VALORES POR QUE PUEDE SER QUE HAYA QUE AGREGAR EL VALOR 'SIN DATO' QUE NO VIENE POR DEFECTO EN LA CATEGORIA
            //---------------------------------------------------------
            ArrayList<String> values1 = new ArrayList<String>();
            ResultSet rs;
            sql = "";
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
                //columnTypeName.add(variablesCrossData.get(0).getName());//searchTypeName(columnNamesPivot.get(0)));
                //columnTypeName.add("");
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
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

//    private void removeValuesConfigured() {
//        /*
//         * elimina los registros que no sean necesarios por que se ha configurado una 
//         * variable quitando valores
//         */
//        ArrayList<String> valuesDiscardedList;//valores descartados de la categoria(los valores que el usuario elimino de la categoria)
//        List<String> valuesList;//todos los valores (puede y no puede tomar)
//        List<String> valuesConfiguredList;//valores configurados (solo los que puede tomar, unos eliminados por el usuario)
//        sql = "";
//        for (int i = 0; i < variablesCrossData.size(); i++) {
//            //DETERMINO SI SE HA REALIZADO UNA CONFIGURACION
//            valuesDiscardedList = new ArrayList<String>();
//            if (variablesCrossData.get(i).getValues().size() != variablesCrossData.get(i).getValuesConfigured().size()) {
//                valuesList = variablesCrossData.get(i).getValues();
//                valuesConfiguredList = variablesCrossData.get(i).getValuesConfigured();
//                for (int j = 0; j < valuesList.size(); j++) {
//                    boolean find = false;
//                    for (int k = 0; k < valuesConfiguredList.size(); k++) {
//                        if (valuesList.get(j).compareTo(valuesConfiguredList.get(k)) == 0) {
//                            find = true;
//                            break;
//                        }
//                    }
//                    if (!find) {//si el valor no se encuentra es por que se descarto
//                        valuesDiscardedList.add(valuesList.get(j));
//                    }
//                }
//            }
//            if (!valuesDiscardedList.isEmpty()) {//adicionamos usuario e indicador                                
//                sql = "";
//                for (int j = 0; j < valuesDiscardedList.size(); j++) {
//                    sql = sql + "OR column_" + (i + 1) + " LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
//                }
//            }
//        }
//        if (sql.trim().length() != 0) {
//            sql = sql.substring(2, sql.length());//elimino primer "OR"                    
//            sql = ""
//                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
//                    + "    indicator_id = " + currentIndicator.getIndicatorId() + " AND \n\r"
//                    + " ( \n\r"
//                    + sql
//                    + " ) \n\r";
//            sql = "\n\r DELETE FROM indicators_records WHERE " + sql;
//            connectionJdbcMB.non_query(sql);//
//            //System.out.println("REALIZO LAS ElIMINACIONES DE LA TABLA PIVOT \n " + sql);
//        } else {
//            //System.out.println("NO HAY ELIMINACIONES DE LA TABLA PIVOT \n " + sql);
//        }
//    }
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
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private String createIndicatorConsult() {
        String sqlReturn = " SELECT  \n\r";
        String sourceTable = "";//tabla adicional que se usara en la seccion "FROM" de la consulta sql
        String filterSourceTable = "";//filtro adicional usado en la "WHERE" de la consulta sql
        for (int i = 0; i < variablesCrossData.size(); i++) {
            switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable 
                case injuries_fatal://TIPO DE LESION -----------------------
                    sqlReturn = sqlReturn + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS tipo_lesion";
                    break;
                case injuries_non_fatal://TIPO DE LESION -----------------------
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
        sqlReturn = sqlReturn + "   FROM  \n\r";
        sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ", victims" + sourceTable + " \n\r";
        sqlReturn = sqlReturn + "   WHERE  \n\r";
        sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".victim_id = victims.victim_id AND \n\r";
        sqlReturn = sqlReturn + "       " + filterSourceTable;
        if (currentIndicator.getIndicatorId() != 1 && currentIndicator.getIndicatorId() != 2
                && currentIndicator.getIndicatorId() != 3 && currentIndicator.getIndicatorId() != 4) {
            //si no es general se filtra por tipo de lesion
            sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_id = " + currentIndicator.getInjuryId().toString() + " AND \n\r";
        }
        sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n\r";
        sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy'); ";
        System.out.println("CONSULTA (indicators percentage) \n " + sqlReturn);
        return sqlReturn;
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
                    if (variablesList.get(i).compareTo(currentVariablesSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
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
        try {//JFreeChart 
            JFreeChart chart = null;
            if (graphType1) {
                chart = createStackedBarChart();
            } else {
                chart = createPieChart();
            }
            File chartFile = new File("dynamichart");
            ChartUtilities.saveChartAsPNG(chartFile, chart, 700, 500);
            chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
        } catch (Exception e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
        }
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
                    System.out.println("Error 6 en " + this.getClass().getName() + ":" + e.toString());
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

    private String getMatrixValue(String type, int col, int row) {
        String strReturn = "-1";

        DecimalFormat formateador = new DecimalFormat("0.00");
        try {
            if (type.compareTo("countXY") == 0) {//valor en la matriz            
                return matrixResult[col][row];
            }
            if (type.compareTo("rowTotal") == 0) {//total por fila
                return totalsVertical.get(row);
            }
            if (type.compareTo("columnTotal") == 0) {//total columna
                return totalsHorizontal.get(col);
            }
            if (type.compareTo("columnPercentageXY") == 0) {//porcentaje segun columna para una posicion de la matriz
                if (Double.parseDouble(totalsHorizontal.get(col)) == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResult[col][row]) * 100) / Double.parseDouble(totalsHorizontal.get(col)));
                }
            }

            if (type.compareTo("rowPercentageXY") == 0) {//porcentaje segun fila para una posicion de la matriz
                if (Double.parseDouble(totalsVertical.get(row)) == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResult[col][row]) * 100) / Double.parseDouble(totalsVertical.get(row)));
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
                if (grandTotal == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResult[col][row]) * 100) / (double) grandTotal);
                }
            }
            if (type.compareTo("percentageOfTotalRowAccordingTotalRow") == 0) {//porcentaje del total de una fila segun el total de la fila                
                if (Double.parseDouble(totalsVertical.get(row)) == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
            if (type.compareTo("percentageOfTotalColumnAccordingTotalColumn") == 0) {//porcentaje del total de una columna segun el total de la columna                
                if (Double.parseDouble(totalsHorizontal.get(col)) == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
            if (type.compareTo("percentageOfTotalRowAccordingGrandTotal") == 0) {//porcentaje del total de una fila segun el total general            
                if (grandTotal == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(totalsVertical.get(row)) * 100) / (double) grandTotal);
                }
            }
            if (type.compareTo("percentageOfTotalColumnAccordingGrandTotal") == 0) {//porcentaje del total de una columna segun el total general
                if (grandTotal == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(totalsHorizontal.get(col)) * 100) / (double) grandTotal);
                }
            }
            if (type.compareTo("percentageOfGrandTotalAccordingGrandTotal") == 0) {//porcentaje del gran total en base al gran total
                if (grandTotal == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
        } catch (Exception e) {
            System.out.println("Error 7 en " + this.getClass().getName() + ":" + e.toString());
        }
        return strReturn;
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
        int posCol;
        String strReturn = " ";
        String value;
        double totalA;
        double totalB;
        int rowsForRecord = 0;//filas a crear por registro(inicia en 1 por el rowspan cuenta desde 1)        
        boolean nameColumnAdd;
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        //-------------------------------------------------------------------                
        posCol = 2;// +2 por que faltal nombre de filas               
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            for (int i = 0; i < columNames.size(); i++) {
                celda = fila.createCell(posCol++);
                texto = new HSSFRichTextString(determineHeader(columNames.get(i)));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            celda = fila.createCell(posCol);
            celda.setCellValue("Total");
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
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja
            posF = 1;
            //posI = 1;
            for (int j = 0; j < headers1.size(); j++) {
                posI = posF + 1;
                for (int i = 0; i < headers1.get(j).getColumns(); i++) {
                    posF++;
                }
                sheet.addMergedRegion(new CellRangeAddress(0, 0, posI, posF));
                //posF++;
            }
            posCol = 2;// +2 por que faltal las filas               
            for (int i = 0; i < headers1.size(); i++) {
                celda = fila.createCell(posCol);
                posCol = posCol + headers1.get(i).getColumns();
                texto = new HSSFRichTextString(determineHeader(headers1.get(i).getLabel()));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja
            posRow++;
            posCol = 2;// +2 por que faltal las filas               
            for (int i = 0; i < headers2.length; i++) {
                celda = fila.createCell(posCol++);
                texto = new HSSFRichTextString(determineHeader(headers2[i]));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            celda = fila.createCell(posCol);
            celda.setCellValue("Total");
        }

        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------      

        if (showColumnPercentage) {
            rowsForRecord++;
        }
        if (showRowPercentage) {
            rowsForRecord++;
        }
        if (showCount) {
            rowsForRecord++;
        }
        if (showTotalPercentage) {
            rowsForRecord++;
        }

        posI = posRow;
        if (rowsForRecord > 0) {
            posF = posRow + rowsForRecord - 1;
        } else {
            posF = posRow + rowsForRecord;
        }

        for (int j = 0; j < rowNames.size() - 1; j++) {
            nameColumnAdd = false;
            sheet.addMergedRegion(new CellRangeAddress(posI, posF, 0, 0));

            posI = posI + rowsForRecord;
            posF = posF + rowsForRecord;

            if (showCount) {
                fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
                if (!nameColumnAdd) {
                    celda = fila.createCell(0);
                    celda.setCellValue(determineHeader(rowNames.get(j)));
                    nameColumnAdd = true;
                }
                posCol = 1;
                celda = fila.createCell(posCol++);
                celda.setCellValue("Recuento");
                for (int i = 0; i < columNames.size(); i++) {
                    celda = fila.createCell(posCol++);
                    celda.setCellValue(getMatrixValue("countXY", i, j));
                }
                celda = fila.createCell(posCol++);
                celda.setCellValue(getMatrixValue("rowTotal", -1, j));
            }
            if (showRowPercentage) {
                fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
                if (!nameColumnAdd) {
                    celda = fila.createCell(0);
                    celda.setCellValue(determineHeader(rowNames.get(j)));
                    nameColumnAdd = true;
                }
                posCol = 1;
                celda = fila.createCell(posCol++);
                celda.setCellValue("% por fila");
                for (int i = 0; i < columNames.size(); i++) {
                    celda = fila.createCell(posCol++);
                    celda.setCellValue(getMatrixValue("rowPercentageXY", i, j));
                }
                celda = fila.createCell(posCol++);
                celda.setCellValue(getMatrixValue("percentageOfTotalRowAccordingTotalRow", -1, j));
            }
            if (showColumnPercentage) {
                fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
                if (!nameColumnAdd) {
                    celda = fila.createCell(0);
                    celda.setCellValue(determineHeader(rowNames.get(j)));
                    nameColumnAdd = true;
                }
                posCol = 1;
                celda = fila.createCell(posCol++);
                celda.setCellValue("% por columna");

                for (int i = 0; i < columNames.size(); i++) {
                    celda = fila.createCell(posCol++);
                    celda.setCellValue(getMatrixValue("columnPercentageXY", i, j));
                }
                celda = fila.createCell(posCol++);
                celda.setCellValue(getMatrixValue("percentageOfTotalRowAccordingGrandTotal", -1, j));
            }
            if (showTotalPercentage) {
                fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
                if (!nameColumnAdd) {
                    celda = fila.createCell(0);
                    celda.setCellValue(determineHeader(rowNames.get(j)));
                    //nameColumnAdd = true;
                }
                posCol = 1;
                celda = fila.createCell(posCol++);
                celda.setCellValue("% del total");
                for (int i = 0; i < columNames.size(); i++) {
                    celda = fila.createCell(posCol++);
                    celda.setCellValue(getMatrixValue("totalPercentageXY", i, j));
                }
                celda = fila.createCell(posCol++);
                celda.setCellValue(getMatrixValue("percentageOfTotalRowAccordingGrandTotal", 0, j));
            }
        }
        //---------------------------------------------------------------
        //fila final con los totales
        //---------------------------------------------------------------
        sheet.addMergedRegion(new CellRangeAddress(posI, posF, 0, 0));
        nameColumnAdd = false;
        if (showCount) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            if (!nameColumnAdd) {
                celda = fila.createCell(0);
                celda.setCellValue("Totales");
                nameColumnAdd = true;
            }
            posI = 1;
            celda = fila.createCell(posI++);
            celda.setCellValue("Recuento");
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                celda = fila.createCell(posI++);
                celda.setCellValue(getMatrixValue("columnTotal", i, 0));
            }
            celda = fila.createCell(posI++);
            celda.setCellValue(String.valueOf(grandTotal));
        }
        if (showRowPercentage) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            if (!nameColumnAdd) {
                celda = fila.createCell(0);
                celda.setCellValue("Totales");
                nameColumnAdd = true;
            }
            posI = 1;
            celda = fila.createCell(posI++);
            celda.setCellValue("% por fila ");
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                celda = fila.createCell(posI++);
                celda.setCellValue(getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0));
            }
            celda = fila.createCell(posI++);
            celda.setCellValue(getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));
        }
        if (showColumnPercentage) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            if (!nameColumnAdd) {
                celda = fila.createCell(0);
                celda.setCellValue("Totales");
                nameColumnAdd = true;
            }
            posI = 1;
            celda = fila.createCell(posI++);
            celda.setCellValue("% por columna");
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                celda = fila.createCell(posI++);
                celda.setCellValue(getMatrixValue("percentageOfTotalColumnAccordingTotalColumn", i, 0));
            }
            celda = fila.createCell(posI++);
            celda.setCellValue(getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));
        }
        if (showTotalPercentage) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            if (!nameColumnAdd) {
                celda = fila.createCell(0);
                celda.setCellValue("Totales");
                //nameColumnAdd = true;
            }
            posI = 1;
            celda = fila.createCell(posI++);
            celda.setCellValue("% del total");
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                celda = fila.createCell(posI++);
                celda.setCellValue(getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0));
            }
            celda = fila.createCell(posI++);
            celda.setCellValue(getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));
        }
    }

    private String createDataTableResult() {
        headers1 = new ArrayList<SpanColumns>();
        headers2 = new String[columNames.size()];        

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
            for (int i = 0; i < columNames.size(); i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:160px;\">" + determineHeader(columNames.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                                <td>\r\n";
            strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:160px; \">Total</div>\r\n";
            strReturn = strReturn + "                                </td>\r\n";
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
            strReturn = strReturn + "                                <td >\r\n";
            strReturn = strReturn + "                                    <div >-</div>\r\n";
            strReturn = strReturn + "                                </td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";

            strReturn = strReturn + "                            <tr>\r\n";
            //AGREGO LA CABECERA 2 A El PANEL_GRID
            for (int i = 0; i < headers2.length; i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:160px; height:20px;\">" + determineHeader(headers2[i]) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                                <td >\r\n";
            strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:160px; height:20px;\">Total</div>\r\n";
            strReturn = strReturn + "                                </td>\r\n";
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
        if (showColumnPercentage) {
            rowsForRecord++;
        }
        if (showRowPercentage) {
            rowsForRecord++;
        }
        if (showCount) {
            rowsForRecord++;
        }
        if (showTotalPercentage) {
            rowsForRecord++;
        }

        strReturn = strReturn + "                    <div id=\"firstcol\" style=\"overflow: hidden;height:280px\">\r\n";//tamaño del div izquierdo
        strReturn = strReturn + "                        <table width=\"200px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";

        rowNames.add("Totales");
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
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">Recuento</div></td>\r\n";
                showCountAdd = true;
            }
            if (showRowPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% por fila</div></td>\r\n";
                showRowPercentageAdd = true;
            }

            if (showColumnPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% por columna</div></td>\r\n";
                showColumnPercentageAdd = true;
            }
            if (showTotalPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% del total</div></td>\r\n";
                showTotalPercentageAdd = true;
            }
            strReturn = strReturn + "                            </tr>\r\n";

            if (showCount && !showCountAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">recuento</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showRowPercentage && !showRowPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% por fila</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showColumnPercentage && !showColumnPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% por columna</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showTotalPercentage && !showTotalPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% del total</div></td>\r\n";

                strReturn = strReturn + "                            </tr>\r\n";
            }
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td valign=\"top\">\r\n";


        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------      
        //int sizeTableMatrix = columNames.size() * 150;//que cada columna tenga 100px
        //sizeTableMatrix = sizeTableMatrix + 100;//de los totales
        strReturn = strReturn + "                    <div id=\"table_div\" style=\"overflow: scroll;width:450px;height:300px;position:relative\" onscroll=\"fnScroll()\" >\r\n";//div que maneja la tabla
        //strReturn = strReturn + "                        <table width=\"" + sizeTableMatrix + "px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";//
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";//
        //----------------------------------------------------------------------
        //AGREGO LOS REGISTROS DE LA MATRIZ        
        for (int j = 0; j < rowNames.size() - 1; j++) {//-1 por que le agrege "TOTALES"
            if (showCount) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr " + getColorType() + " id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("countXY", i, j) + "</div></td>\r\n";
                }
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("rowTotal", -1, j) + "</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showRowPercentage) {
                if (j == 0) {//si es la primera fila tiene elidentificador 'firstTr'
                    strReturn = strReturn + "                            <tr " + getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("rowPercentageXY", i, j) + "</div></td>\r\n";
                }
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalRowAccordingTotalRow", -1, j) + "</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            //total = 0;
            if (showColumnPercentage) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr " + getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("columnPercentageXY", i, j) + "</div></td>\r\n";
                }
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalRowAccordingGrandTotal", -1, j) + "</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            //total = 0;
            if (showTotalPercentage) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr " + getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("totalPercentageXY", i, j) + "</div></td>\r\n";
                }
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalRowAccordingGrandTotal", 0, j) + "</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            changeColorType();//cambiar de color las filas de blanco a azul
        }

        //----------------------------------------------------------------------
        //AGREGO LA ULTIMA FILA CORRESPONDIENTE A LOS TOTALES
        //----------------------------------------------------------------------

        if (showCount) {
            strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("columnTotal", i, 0) + "</div></td>\r\n";
            }
            strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + String.valueOf(grandTotal) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }

        if (showRowPercentage) {
            strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</div></td>\r\n";
            }
            strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (showColumnPercentage) {
            strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalColumnAccordingTotalColumn", i, 0) + "</div></td>\r\n";
            }
            strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (showTotalPercentage) {
            strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                //strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</td>\r\n";
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</div></td>\r\n";
            }
            strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
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

    public JFreeChart createStackedBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String indicatorName = currentIndicator.getIndicatorName()+" - Municipo de Pasto.\n";
        String categoryAxixLabel = "";
        String variablesName = "";
        int pos = 0;
        try {
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            ResultSet rs;
            if (variablesCrossData.size() == 1) {
                variablesName = "Desagregado por: " + variablesCrossData.get(0).getName();
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                while (rs.next()) {
                    dataset.setValue(rs.getLong("count"), determineHeader(rs.getString("column_1")), "-");
                }
            }
            if (variablesCrossData.size() == 2) {
                variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(1).getName();
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                while (rs.next()) {
                    dataset.setValue(rs.getLong("count"), determineHeader(rs.getString("column_1")), determineHeader(rs.getString("column_2")));
                }
            }
            if (variablesCrossData.size() == 3) {
                //determino el numero de columna a filtrar (variable en variableCrossData                
                for (int i = 0; i < variablesCrossData.size(); i++) {
                    if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                        pos = i;
                        break;
                    }
                }
                //adiciono la instruccion WHERE a la consulta
                sql = sql + " AND column_" + String.valueOf(pos + 1) + " LIKE '" + currentValueGraph + "' ";
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                if (pos == 0) {
                    variablesName = "Desagregado por: " + variablesCrossData.get(1).getName() + ", " + variablesCrossData.get(2).getName() + ", " + variablesCrossData.get(0).getName() + " = " + determineHeader(currentValueGraph);
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), determineHeader(rs.getString("column_2")), determineHeader(rs.getString("column_3")));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 1) {
                    variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(2).getName() + ", " + variablesCrossData.get(1).getName() + " = " + determineHeader(currentValueGraph);
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), determineHeader(rs.getString("column_1")), determineHeader(rs.getString("column_3")));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 2) {
                    variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(1).getName() + ", " + variablesCrossData.get(2).getName() + " = " + determineHeader(currentValueGraph);
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), determineHeader(rs.getString("column_1")), determineHeader(rs.getString("column_2")));
                    }
                    categoryAxixLabel = variablesCrossData.get(1).getName();
                }                
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.toString());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        indicatorName = indicatorName + variablesName + "\nPeriodo " + sdf.format(initialDate) + " a " + sdf.format(endDate);
        JFreeChart chartReturn = ChartFactory.createStackedBarChart(
                indicatorName,
                categoryAxixLabel,
                "porcentaje", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // the plot orientation
                true, // legend
                true, // tooltips
                false // urls
                );

        chartReturn.setBackgroundPaint(Color.white);
        chartReturn.getTitle().setPaint(new Color(50, 50, 50));
        chartReturn.getTitle().setFont(new Font("SanSerif", Font.BOLD, 15));

        CategoryPlot plot = (CategoryPlot) chartReturn.getPlot();

        ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());//quitar gradiente
        CategoryAxis xAxis = (CategoryAxis) plot.getDomainAxis();//rotacion a 45 grados        
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setRenderAsPercentages(true);
        if (showItems) {
            renderer.setDrawBarOutline(false);
            renderer.setBaseItemLabelsVisible(true);
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{3}", NumberFormat.getIntegerInstance(), new DecimalFormat("0.00%")));
        }

        return chartReturn;
    }

    public JFreeChart createPieChart() {
        //DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultPieDataset dataset = new DefaultPieDataset();
        String indicatorName = currentIndicator.getIndicatorName() + " - Municipo de Pasto.\n";
        String categoryAxixLabel = "";
        String variablesName = "";
        int pos = 0;
        try {
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            ResultSet rs;
            if (variablesCrossData.size() == 1) {
                variablesName = "Desagregado por: " + variablesCrossData.get(0).getName();
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                while (rs.next()) {
                    dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
                }
            }
            if (variablesCrossData.size() == 2) {
                variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(1).getName();
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                while (rs.next()) {
                    dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
                }
            }
            if (variablesCrossData.size() == 3) {
                //determino el numero de columna a filtrar (variable en variableCrossData                
                for (int i = 0; i < variablesCrossData.size(); i++) {
                    if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                        pos = i;
                        break;
                    }
                }
                //adiciono la instruccion WHERE a la consulta
                sql = sql + " AND column_" + String.valueOf(pos + 1) + " LIKE '" + currentValueGraph + "' ";
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                if (pos == 0) {
                    variablesName = "Desagregado por: " + variablesCrossData.get(1).getName() + ", " + variablesCrossData.get(2).getName() + ", " + variablesCrossData.get(0).getName() + " = " + determineHeader(currentValueGraph);
                    while (rs.next()) {
                        dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 1) {
                    variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(2).getName() + ", " + variablesCrossData.get(1).getName() + " = " + determineHeader(currentValueGraph);
                    while (rs.next()) {
                        dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 2) {
                    variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(1).getName() + ", " + variablesCrossData.get(2).getName() + " = " + determineHeader(currentValueGraph);
                    while (rs.next()) {
                        dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
                    }
                    categoryAxixLabel = variablesCrossData.get(1).getName();
                }                
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.toString());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        indicatorName = indicatorName + variablesName + "\nPeriodo " + sdf.format(initialDate) + " a " + sdf.format(endDate);


        JFreeChart chartReturn = ChartFactory.createPieChart(
                indicatorName,
                dataset, // data
                true, // legend
                true, // tooltips
                false // urls
                );

        chartReturn.setBackgroundPaint(Color.white);
        chartReturn.getTitle().setPaint(new Color(50, 50, 50));
        chartReturn.getTitle().setFont(new Font("SanSerif", Font.BOLD, 15));

        if (showItems) {
            PiePlot plot = (PiePlot) chartReturn.getPlot();
            plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}={2}", NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
            plot.setNoDataMessage("No data available");
            plot.setCircular(false);
            plot.setLabelGap(0.02);
        }
        return chartReturn;
    }

    private void addColumToRow(Row row1, String get, String styleClass, String styleCSS, int colSpan, int rowSpan) {
        Column column = new Column();
        HtmlOutputText text = new HtmlOutputText();
        column.setStyleClass(styleClass);
        text.setValue(get);
        column.getChildren().add(text);
        column.setStyle(styleCSS);
        column.setRowspan(rowSpan);
        column.setColspan(colSpan);
        row1.getChildren().add(column);
    }

    private void loadIndicator(int n) {
        currentIndicator = indicatorsFacade.find(n);
        reset();
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
    public void loadIndicator2() {
        loadIndicator(2);
    }

    public void loadIndicator4() {
        loadIndicator(4);
    }

    public void loadIndicator11() {
        loadIndicator(11);
    }

    public void loadIndicator18() {
        loadIndicator(18);
    }

    public void loadIndicator25() {
        loadIndicator(25);
    }

    public void loadIndicator32() {
        loadIndicator(32);
    }

    public void loadIndicator39() {
        loadIndicator(39);
    }

    public void loadIndicator46() {
        loadIndicator(46);
    }

    public void loadIndicator53() {
        loadIndicator(53);
    }

    public void loadIndicator60() {
        loadIndicator(60);
    }

    public void loadIndicator67() {
        loadIndicator(67);
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

    public boolean isShowColumnPercentage() {
        return showColumnPercentage;
    }

    public void setShowColumnPercentage(boolean showColumnPercentage) {
        this.showColumnPercentage = showColumnPercentage;
    }

    public boolean isShowTotalPercentage() {
        return showTotalPercentage;
    }

    public void setShowTotalPercentage(boolean showTotalPercentage) {
        this.showTotalPercentage = showTotalPercentage;
    }

    public String getDataTableHtml() {
        return dataTableHtml;
    }

    public void setDataTableHtml(String dataTableHtml) {
        this.dataTableHtml = dataTableHtml;
    }

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