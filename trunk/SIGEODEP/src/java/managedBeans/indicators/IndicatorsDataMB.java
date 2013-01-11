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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import managedBeans.reports.SpanColumns;
import managedBeans.reports.TableGroup;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;
import model.pojo.IndicatorsVariables;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.primefaces.component.column.Column;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.row.Row;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsDataMB")
@SessionScoped
public class IndicatorsDataMB {

    /**
     * Creates a new instance of IndicatorsMB
     */
    @EJB
    IndicatorsFacade indicatorsFacade;
    Indicators currentIndicator;
    private ConnectionJdbcMB connectionJdbcMB;
    private int currentYear = 0;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    ArrayList<String> rowNames;//NOMBRES DE LAS FILAS
    String[][] matrixResult;//MATRIZ DE RESULTADOS
    ArrayList<SpanColumns> headers1;//CABECERA 1 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    String[] headers2;//CABECERA 2 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    ArrayList<Integer> totalsHorizontal = new ArrayList<Integer>();
    ArrayList<Integer> totalsVertical = new ArrayList<Integer>();
    String pivotTableName;
    String prepivotTableName;
    ArrayList<Variable> variablesCrossData;
    boolean showAll;
    boolean showTotals;
    String initialDateStr;
    String endDateStr;

    public IndicatorsDataMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
    }

    public Variable createVariable(String name, String generic_table, boolean conf) {
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
                for (int i = 0; i < 23; i++) {
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
                System.out.println("AÑO ACTUAL" + currentYear);
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
            case NOVALUE://es una tabla categorica
                try {
                    ResultSet rs = connectionJdbcMB.consult("Select * from " + generic_table);
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

    /*
     * retorna un listado de las variables que se pueden cruzar en determinado
     * indicador
     */
    public ArrayList<Variable> getVariablesIndicator(int indicatorId) {
        ArrayList<Variable> arrayReturn = new ArrayList<Variable>();

        currentIndicator = indicatorsFacade.find(indicatorId);
        List<IndicatorsVariables> variablesList = currentIndicator.getIndicatorsVariablesList();
        for (int i = 0; i < variablesList.size(); i++) {
            //se crea un ArrayList<Variable> y no se trabaja con l estructura de la base de daros(pojo)
            //por que el usuario realiza configuraciones y ediciones de este
            //
            arrayReturn.add(
                    createVariable(
                    variablesList.get(i).getIndicatorsVariablesPK().getVariableName(),
                    variablesList.get(i).getCategory(),
                    variablesList.get(i).getAddValues()));
        }
        return arrayReturn;
    }

    public List<String> getGraphTypes(int n) {
        ArrayList<String> listReturn = new ArrayList<String>();
        try {
            ResultSet rs = connectionJdbcMB.consult("Select graph_type from indicators where indicator_id=" + String.valueOf(n));
            if (rs.next()) {
                String[] splitGraphType = rs.getString(1).split(",");
                listReturn.addAll(Arrays.asList(splitGraphType));
            }
        } catch (Exception e) {
        }
        return listReturn;
    }

    public PanelGrid createDataTableResult() {
        PanelGrid panelGrid = new PanelGrid();
        headers1 = new ArrayList<SpanColumns>();
        headers2 = new String[columNames.size()];
        totalsHorizontal = new ArrayList<Integer>();
        totalsVertical = new ArrayList<Integer>();

        Row row1;
        //System.out.println("SIZE COLUM NAMES" + String.valueOf(columNames.size()));
        //System.out.println("SIZE ROW NAMES" + String.valueOf(rowNames.size()));
        //System.out.println("SIZE MATRIZ COLUMNAS" + String.valueOf(matrixResult.length));
        //System.out.println("SIZE MATRIZ FILAS" + String.valueOf(matrixResult[0].length));

        //AGREGO LAS CABECERAS
        if (variablesCrossData.size() == 2) {//CABECERA SIMPLE
            row1 = new Row();
            addColumToRow(row1, " ", "ui-widget-header", 1, 1);//COLUMNA VACIA
            for (int i = 0; i < columNames.size(); i++) {
                addColumToRow(row1, columNames.get(i), "ui-widget-header", 1, 1);
            }
            if (showTotals) {
                addColumToRow(row1, "Total", "ui-widget-header", 1, 1);
            }
            panelGrid.getChildren().add(row1);
        }
        if (variablesCrossData.size() == 3) {//CABECERA COMPUESTA            
            //listaDeGrupos = new ArrayList<TableGroup>();
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
            row1 = new Row();
            addColumToRow(row1, " ", "ui-widget-header", 1, 2);//COLUMNA VACIA AQUI TIENE ROWSPAN=2
            for (int i = 0; i < headers1.size(); i++) {
                addColumToRow(row1, headers1.get(i).getLabel(), "ui-widget-header", headers1.get(i).getColumns(), 1);
            }
            if (showTotals) {
                addColumToRow(row1, "Total", "ui-widget-header", 1, 2);
            }
            panelGrid.getChildren().add(row1);
            //AGREGO LA CABECERA 2 A El PANEL_GRID
            row1 = new Row();
            //addColumToRow(row1, " ", "ui-widget-header",1,2);//LA COLUMNA VACIA VIENE DEL ANTERIOR FILA (ROWSPAN=2)
            for (int i = 0; i < headers2.length; i++) {
                addColumToRow(row1, headers2[i], "ui-widget-header ui-state-focus", 1, 1);
            }
            panelGrid.getChildren().add(row1);
        }
        //CREO UN VECTOR PARA LOS TOTALES
        totalsHorizontal = new ArrayList<Integer>();
        totalsVertical = new ArrayList<Integer>();
        for (int i = 0; i < columNames.size(); i++) {
            totalsHorizontal.add(0);
        }
        //INGRESO FILAS
        for (int j = 0; j < rowNames.size(); j++) {
            row1 = new Row();
            int total = 0;
            //AGREGO EL NOMBRE DE CADA FILA
            addColumToRow(row1, rowNames.get(j), "ui-widget-header ui-state-focus", 1, 1);
            //AGREGO LOS DATOS DE LA FILA
            for (int i = 0; i < columNames.size(); i++) {
                addColumToRow(row1, matrixResult[i][j], " ", 1, 1);
                totalsHorizontal.set(i, totalsHorizontal.get(i) + Integer.parseInt(matrixResult[i][j]));
                total = total + Integer.parseInt(matrixResult[i][j]);
            }
            totalsVertical.add(total);
            if (showTotals) {
                addColumToRow(row1, String.valueOf(total), "ui-widget-header ui-state-focus", 1, 1);
            }
            panelGrid.getChildren().add(row1);
        }
        //AGREGO EL TOTAL

        if (showTotals) {
            row1 = new Row();
            int total = 0;
            addColumToRow(row1, "Totales", "ui-widget-header ui-state-focus", 1, 1);
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                addColumToRow(row1, String.valueOf(totalsHorizontal.get(i)), "ui-widget-header ui-state-focus", 1, 1);
                total = total + totalsHorizontal.get(i);
            }
            addColumToRow(row1, String.valueOf(total), "ui-widget-header ui-state-focus", 1, 1);
            panelGrid.getChildren().add(row1);
        }
        return panelGrid;
    }

    public PieDataset createPieDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        dataset.setValue("Dato A", new Double(45.0));
        dataset.setValue("Dato B", new Double(15.0));
        dataset.setValue("Dato C", new Double(25.2));
        dataset.setValue("Dato E", new Double(14.8));
        return dataset;
    }

    public JFreeChart createChart(ArrayList<Variable> variablesCrossData, String currentVariableGraph, String currentValueGraph, Indicators currentIndicator) {
        //variablesCrossData   : lista de variables a cruzar
        //currentVariableGraph : variable por la que se va a filtrar el grafico(cuando son tres variables)
        //currentValueGraph    : valor por el que se va a filtrar el grafico(cuando son tres variables)
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String indicatorName = currentIndicator.getIndicatorName();
        String categoryAxixLabel = "";
        try {

            int pos = 0;
            String sql = "";
            sql = sql + "SELECT * FROM " + pivotTableName;
            ResultSet rs;// = connectionJdbcMB.consult("SELECT * FROM " + pivotTableName);            
            if (currentValueGraph.length() != 0 && currentVariableGraph.length() != 0) {//SON TRES VARIABLES
                //determino los nombres de las columnas
                rs = connectionJdbcMB.consult(sql);
                int numberColumns = rs.getMetaData().getColumnCount();
                ArrayList<String> nameColumns = new ArrayList<String>();
                for (int i = 1; i <= numberColumns; i++) {//metadata cuenta desde 1
                    nameColumns.add(rs.getMetaData().getColumnName(i));
                }
                //determino el numero de columna a filtrar (variable en variableCrossData                
                for (int i = 0; i < variablesCrossData.size(); i++) {
                    if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                        pos = i;
                        break;
                    }
                }
                //adicino la instruccion WHERE a la consulta
                sql = sql + " Where " + nameColumns.get(pos) + " LIKE '" + currentValueGraph + "' ";
                rs = connectionJdbcMB.consult(sql);

                //consulta para generar el grafico                
                System.out.println("CONSULTA PARA GENERAR EL GRAFICO #######################");
                System.out.println(sql);

                System.out.println("REGISTROS DEL DATASET #######################");
                if (pos == 0) {
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), rs.getString(2), rs.getString(3));
                        System.out.println(rs.getLong("count") + "\t\t\t  " + rs.getString(1) + "\t\t\t  " + rs.getString(2));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 1) {
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), rs.getString(1), rs.getString(3));
                        System.out.println(rs.getLong("count") + "\t\t\t  " + rs.getString(1) + "\t\t\t  " + rs.getString(2));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 2) {
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), rs.getString(1), rs.getString(2));
                        System.out.println(rs.getLong("count") + "\t\t\t  " + rs.getString(1) + "\t\t\t  " + rs.getString(2));
                    }
                    categoryAxixLabel = variablesCrossData.get(1).getName();
                }
                indicatorName = currentIndicator.getIndicatorName() + "\n(" + currentVariableGraph + " es " + currentValueGraph + ")";
            } else {// SON DOS VARIABLES
                System.out.println("CONSULTA PARA GENERAR EL GRAFICO #######################");
                System.out.println(sql);

                System.out.println("REGISTROS DEL DATASET #######################");
                rs = connectionJdbcMB.consult(sql);
                //System.out.println("AQUI 2--------------------------");            
                while (rs.next()) {
                    dataset.setValue(rs.getLong("count"), rs.getString(1), rs.getString(2));
                    System.out.println(rs.getLong("count") + "\t\t\t  " + rs.getString(1) + "\t\t\t  " + rs.getString(2));
                }
            }
            System.out.println("FIN REGISTROS DEL DATASET #######################");

        } catch (SQLException ex) {
            Logger.getLogger(IndicatorsDataMB.class.getName()).log(Level.SEVERE, null, ex);
        }

        JFreeChart chartReturn = ChartFactory.createBarChart(indicatorName, categoryAxixLabel, "Conteo", dataset, PlotOrientation.VERTICAL, true, true, false);

        chartReturn.setBackgroundPaint(new Color(200, 200, 200));
        chartReturn.getTitle().setPaint(new Color(50, 50, 50));
        chartReturn.getTitle().setFont(new Font("SanSerif", Font.BOLD, 15));


        CategoryPlot plot = (CategoryPlot) chartReturn.getPlot();
        CategoryAxis xAxis = (CategoryAxis) plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        CategoryPlot p = chartReturn.getCategoryPlot();
        p.setRangeGridlinePaint(Color.white);
        return chartReturn;
    }

    public void createPivotTable() {
        //show all me muestra todas las combinaciones
        columNames = new ArrayList<String>();
        rowNames = new ArrayList<String>();
        String sql;

        sql = "\n\r DROP TABLE IF EXISTS " + pivotTableName + ";\n\r";
        sql = sql + " CREATE TABLE  \n\r";
        sql = sql + "	" + pivotTableName + "  \n\r";
        sql = sql + " AS  \n\r";
        sql = sql + " SELECT * from " + prepivotTableName + " \n\r";
        connectionJdbcMB.non_query(sql);


        //HAY QUE ARMAR LAS POSIBLES COMBINACIONES PARA QUE LOS DATOS QUEDEN 
        //ORDENADOS SEGUN COMO SE ENCUENTRE LA CONFIGURACION

        try {
            ArrayList<String> fieldsNames = new ArrayList<String>();
            connectionJdbcMB.non_query("DELETE FROM " + pivotTableName);//elimino registros                 
            //System.out.println("AQUI 1--------------------------");
            //DETEMINO LOS NOMBRES DE LAS COLUMNAS
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName + " LIMIT 1;");
            //System.out.println("AQUI 2--------------------------");
            int ncol = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= ncol; i++) {
                fieldsNames.add(rs.getMetaData().getColumnName(i));
            }
            //CREO NUEVOS VECTORES DE VALORES POR QUE PUEDE SER QUE HAYA QUE
            //AGREGAR EL VALOR 'SIN DATO' QUE NO VIENE POR DEFECTO EN LOS VALORES
            //---------------------------------------------------------
            ArrayList<String> values1 = new ArrayList<String>();
            for (int i = 0; i < variablesCrossData.get(0).getValuesConfigured().size(); i++) {
                values1.add(variablesCrossData.get(0).getValuesConfigured().get(i));
            }
            rs = connectionJdbcMB.consult(
                    "SELECT * FROM " + prepivotTableName
                    + " WHERE " + fieldsNames.get(0) + " like 'SIN DATO'");
            if (rs.next()) {
                values1.add("SIN DATO");
                //System.out.println("APARECIO 'SIN DATO' EN " + fieldsNames.get(0));
            } else {
                //System.out.println("NO APARECIO 'SIN DATO' EN " + fieldsNames.get(0));
            }
            //---------------------------------------------------------

            ArrayList<String> values2 = new ArrayList<String>();
            for (int i = 0; i < variablesCrossData.get(1).getValuesConfigured().size(); i++) {
                values2.add(variablesCrossData.get(1).getValuesConfigured().get(i));
            }
            rs = connectionJdbcMB.consult(
                    "SELECT * FROM " + prepivotTableName
                    + " WHERE " + fieldsNames.get(1) + " like 'SIN DATO'");
            if (rs.next()) {
                values2.add("SIN DATO");
                //System.out.println("APARECIO 'SIN DATO' EN " + fieldsNames.get(0));
            } else {
                //System.out.println("NO APARECIO 'SIN DATO' EN " + fieldsNames.get(0));
            }
            //---------------------------------------------------------
            ArrayList<String> values3 = new ArrayList<String>();
            if (variablesCrossData.size() == 3) {
                for (int i = 0; i < variablesCrossData.get(2).getValuesConfigured().size(); i++) {
                    values3.add(variablesCrossData.get(2).getValuesConfigured().get(i));
                }
                rs = connectionJdbcMB.consult(
                        "SELECT * FROM " + prepivotTableName
                        + " WHERE " + fieldsNames.get(2) + " like 'SIN DATO'");
                if (rs.next()) {
                    values3.add("SIN DATO");
                    //System.out.println("APARECIO 'SIN DATO' EN " + fieldsNames.get(0));
                } else {
                    //System.out.println("NO APARECIO 'SIN DATO' EN " + fieldsNames.get(0));
                }
            }
            //REALIZO LAS POSIBLES COMBINACIONES
            if (variablesCrossData.size() == 2) {
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    for (int j = 0; j < values2.size(); j++) {
                        if (i == 0) {
                            rowNames.add(values2.get(j));
                        }
                        sql = "INSERT INTO " + pivotTableName + " VALUES (";
                        sql = sql + "'" + values1.get(i) + "',";
                        sql = sql + "'" + values2.get(j) + "',";
                        sql = sql + "'0')";
                        //System.out.println("INSERTANDO----------------------------" + sql);
                        connectionJdbcMB.non_query(sql);
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
                            sql = "INSERT INTO " + pivotTableName + " VALUES (";
                            sql = sql + "'" + values1.get(i) + "',";
                            sql = sql + "'" + values2.get(j) + "',";
                            sql = sql + "'" + values3.get(k) + "',";
                            sql = sql + "'0')";
                            connectionJdbcMB.non_query(sql);
                            //System.out.println("INSERTANDO----------------------------" + sql);
                        }
                    }
                }
            }
            //System.out.println("AQUI 2--------------------------");
            //ACTUALZO LOS COUNT DE PIVOT CON LOS COUNT DE PREPIVOT
            sql = " UPDATE " + pivotTableName + " SET count = " + prepivotTableName + ".count \n\r"
                    + " FROM " + prepivotTableName + " \n\r"
                    + " WHERE \n\r";
            for (int i = 0; i < fieldsNames.size() - 1; i++) {//-1 por que la ultima column es count
                sql = sql + " " + pivotTableName + "." + fieldsNames.get(i) + " like " + prepivotTableName + "." + fieldsNames.get(i);
                if (i != fieldsNames.size() - 2) {//menos 2 para saber que es el ultima posicion
                    sql = sql + " AND \n\r";
                }
            }//System.out.println("AQUI 2--------------------------" + sql);
            connectionJdbcMB.non_query(sql);

            //System.out.println("NOMBRES DE COLUMNAS INICIAL----------------\n\r" + columNames.toString());
            //System.out.println("NOMBRES DE FILAS INICIAL----------------\n\r" + rowNames.toString());
            ArrayList<String> columNamesAux;
            ArrayList<String> rowNamesAux;
            if (!showAll) {//si solicita no mostrar todos elimino los que tengan cero y tambien del vector de columnas y filas
                columNamesAux = new ArrayList<String>();
                rowNamesAux = new ArrayList<String>();
                //elimino los que tengan resultado=0
                sql = "DELETE FROM " + pivotTableName + " WHERE count = 0";
                //System.out.println("SQL1----------------\n\r" + sql);
                connectionJdbcMB.non_query(sql);//elimino registros                 
                //elimino nombres de filas y de columnas que no aparezcan
                boolean esta;
                if (variablesCrossData.size() == 2) {
                    sql = "SELECT DISTINCT " + fieldsNames.get(0) + " FROM " + pivotTableName;
                    //System.out.println("SQL2----------------\n\r" + sql);
                    rs = connectionJdbcMB.consult(sql);
                }
                if (variablesCrossData.size() == 3) {
                    sql = "SELECT DISTINCT (" + fieldsNames.get(0) + "||'}'||" + fieldsNames.get(1) + ") FROM " + pivotTableName;
                    //System.out.println("SQL3----------------\n\r" + sql);
                    rs = connectionJdbcMB.consult(sql);
                }

                while (rs.next()) {
                    columNamesAux.add(rs.getString(1));
                }
                //comparo cuales nombres de columnas estan
                for (int i = 0; i < columNames.size(); i++) {
                    esta = false;
                    for (int j = 0; j < columNamesAux.size(); j++) {
                        //System.out.println("COMPARA: " + columNames.get(i) + " CON " + columNames2.get(j));
                        if (columNames.get(i).compareTo(columNamesAux.get(j)) == 0) {
                            //System.out.println("SI ESTA: " + columNames.get(i));
                            esta = true;
                            break;
                        }
                    }
                    if (!esta) {
                        //System.out.println("NO ESTA: " + columNames.get(i));
                        columNames.remove(i);
                        i = -1;
                    }
                }
                if (variablesCrossData.size() == 2) {
                    sql = "SELECT DISTINCT " + fieldsNames.get(1) + " FROM " + pivotTableName;
                    //System.out.println("SQL5----------------\n\r" + sql);
                    rs = connectionJdbcMB.consult(sql);
                }
                if (variablesCrossData.size() == 3) {
                    sql = "SELECT DISTINCT " + fieldsNames.get(2) + " FROM " + pivotTableName;
                    //System.out.println("SQL6----------------\n\r" + sql);
                    rs = connectionJdbcMB.consult(sql);
                }
                while (rs.next()) {
                    rowNamesAux.add(rs.getString(1));
                }
                //comparo cuales nombres de filas no estan                    
                for (int i = 0; i < rowNames.size(); i++) {
                    esta = false;
                    for (int j = 0; j < rowNamesAux.size(); j++) {
                        if (rowNames.get(i).compareTo(rowNamesAux.get(j)) == 0) {
                            esta = true;
                            break;
                        }
                    }
                    if (!esta) {
                        rowNames.remove(i);
                        i = -1;
                    }
                }
                //System.out.println("NOMBRES DE COLUMNAS FINAL----------------\n\r" + columNames.toString());
                //System.out.println("NOMBRES DE FILAS FINAL----------------\n\r" + rowNames.toString());
            }
            //se crea la matrix e inicia matriz con valores en: "0"
            matrixResult = new String[columNames.size()][rowNames.size()];
            for (int i = 0; i < columNames.size(); i++) {
                for (int j = 0; j < rowNames.size(); j++) {
                    matrixResult[i][j] = "0";
                }
            }
            sql = "SELECT * FROM " + pivotTableName;
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                boolean find = false;
                for (int i = 0; i < columNames.size(); i++) {
                    for (int j = 0; j < rowNames.size(); j++) {
                        if (variablesCrossData.size() == 2) {
                            if (rs.getString(1).compareTo(columNames.get(i)) == 0
                                    && rs.getString(2).compareTo(rowNames.get(j)) == 0) {
                                matrixResult[i][j] = rs.getString(3);
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 3) {
                            if (columNames.get(i).compareTo(rs.getString(1) + "}" + rs.getString(2)) == 0
                                    && rs.getString(3).compareTo(rowNames.get(j)) == 0) {
                                matrixResult[i][j] = rs.getString(4);
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
            System.out.println("INICIA MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");


            for (int i = 0; i < columNames.size(); i++) {
                System.out.print(columNames.get(i) + "\t");
            }
            System.out.print("\n\r");
            String salida = "";
            for (int j = 0; j < rowNames.size(); j++) {
                salida = "";
                for (int i = 0; i < columNames.size(); i++) {
                    salida = salida + matrixResult[i][j] + "\t";
                }
                System.out.println(salida + "\n\r");
            }
            System.out.println("FINALIZA MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            //System.out.println("SQL5----------------\n\r" + sql);

        } catch (Exception e) {
            System.out.println("EXCEPTION--------------------------" + e.toString());
        }
    }

    public void createPrepivotTable() {

        String prepivotTableName_2 = prepivotTableName + "_2";
        String sql = "\n\r DROP TABLE IF EXISTS " + prepivotTableName_2 + ";\n\r";
        sql = sql + " CREATE TABLE  \n\r";
        sql = sql + "	" + prepivotTableName_2 + "  \n\r";
        sql = sql + " AS  \n\r";
        sql = sql + " SELECT  \n\r";

        System.out.println("ORDEN DE LAS VARIABLES A CRUZAR");
        for (int i = 0; i < variablesCrossData.size(); i++) {
            System.out.println(variablesCrossData.get(i).getName());
        }
        for (int i = 0; i < variablesCrossData.size(); i++) {
            //System.out.println("SWITCH CON (generic table) " + variablesCrossData.get(i).getGeneric_table());
            switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable 
                case injuries_fatal://TIPO DE LESION -----------------------
                    sql = sql + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sql = sql + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sql = sql + "   END AS tipo_lesion";
                    //sql = sql + "   (SELECT injury_name FROM injuries WHERE injury_id = " + currentIndicator.getInjuryType() + ".injury_id) as tipo_lesion, \n\r";

                    break;
                case age://DETERMINAR EDAD -----------------------                   
                    sql = sql + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                        sql = sql + "       WHEN (( \n\r";
                        sql = sql + "           CASE \n\r";
                        sql = sql + "               WHEN (victims.age_type_id = 2 or victims.age_type_id = 3) THEN 1 \n\r";
                        sql = sql + "               WHEN (victims.age_type_id = 1) THEN victims.victim_age \n\r";
                        sql = sql + "           END \n\r";
                        sql = sql + "       ) between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                    }
                    sql = sql + "   END AS edad";
                    break;
                case hour://HORA -----------------------
                    sql = sql + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
//                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
//                        
//                        sql = sql + "       WHEN (extract(hour from fatal_injuries.injury_time) \n\r";
//                        sql = sql + "       between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                        String[] splitAge2 = splitAge[0].split(":");
                        String[] splitAge3 = splitAge[1].split(":");
                        sql = sql + "       WHEN (extract(hour from fatal_injuries.injury_time) \n\r";
                        sql = sql + "       between " + splitAge2[0] + " and " + splitAge3[0] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                    }
                    sql = sql + "   END AS hora";
                    break;
                case neighborhoods://NOMBRE DEL BARRIO -----------------------
                    sql = sql + "   (SELECT neighborhood_name FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as barrio";
                    break;
                case communes://COMUNA -----------------------
                    sql = sql + "   CAST((SELECT suburb_id FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as text) as comuna";
                    break;
                case corridors://CORREDOR -----------------------
                    sql = sql + "   CASE (SELECT neighborhood_corridor FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) \n\r";
                    sql = sql + "       WHEN '1' THEN 'CENTRAL'  \n\r";
                    sql = sql + "       WHEN '2' THEN 'OCCIDENTAL' \n\r";
                    sql = sql + "       WHEN '3' THEN 'ORIENTAL' \n\r";
                    sql = sql + "       WHEN '4' THEN 'SURORIENTAL' \n\r";
                    sql = sql + "   END AS corredor";
                    break;
                case areas://ZONA -----------------------        
                    sql = sql + "   CASE (SELECT neighborhood_type FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)  \n\r";
                    sql = sql + "       WHEN '1' THEN 'ZONA URBANA'  \n\r";
                    sql = sql + "       WHEN '2' THEN 'ZONA RURAL' \n\r";
                    sql = sql + "   END AS zona";
                    break;
                case genders://GENERO  ----------------------
                    sql = sql + "   CASE (victims.gender_id) \n\r";
                    sql = sql + "       WHEN 1 THEN 'MASCULINO'  \n\r";
                    sql = sql + "       WHEN 2 THEN 'FEMENINO' \n\r";
                    sql = sql + "   END AS genero";
                    break;
                case days://DIA SEMANA ----------------------
                    sql = sql + "   " + currentIndicator.getInjuryType() + ".injury_day_of_week as dia_semana";
                    break;
                case year://AÑO -----------------------
                    sql = sql + "   CAST(extract(year from " + currentIndicator.getInjuryType() + ".injury_date)::int as text) as anyo";
                    break;
                case month://MES -----------------------
                    sql = sql + "   CASE (extract(month from " + currentIndicator.getInjuryType() + ".injury_date)::int) \n\r";
                    sql = sql + "       WHEN 1  THEN 'ENERO' \n\r";
                    sql = sql + "       WHEN 2  THEN 'FEBRERO' \n\r";
                    sql = sql + "       WHEN 3  THEN 'MARZO' \n\r";
                    sql = sql + "       WHEN 4  THEN 'ABRIL' \n\r";
                    sql = sql + "       WHEN 5  THEN 'MAYO' \n\r";
                    sql = sql + "       WHEN 6  THEN 'JUNIO' \n\r";
                    sql = sql + "       WHEN 7  THEN 'JULIO' \n\r";
                    sql = sql + "       WHEN 8  THEN 'AGOSTO' \n\r";
                    sql = sql + "       WHEN 9  THEN 'SEPTIEMBRE' \n\r";
                    sql = sql + "       WHEN 10 THEN 'OCTUBRE' \n\r";
                    sql = sql + "       WHEN 11 THEN 'NOVIEMBRE' \n\r";
                    sql = sql + "       WHEN 12 THEN 'DICIEMBRE' \n\r";
                    sql = sql + "   END AS mes";
                    break;
            }
            if (i == variablesCrossData.size() - 1) {//si es la ultima instruccion se agrega salto de linea
                sql = sql + " \n\r";
            } else {//si no es la ultima instruccion se agrega coma y salto de linea
                sql = sql + ", \n\r";
            }
        }

        //-----------------------
        sql = sql + "   FROM  \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ", victims \n\r";
        sql = sql + "   WHERE  \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ".victim_id = victims.victim_id AND \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy'); ";

        System.out.println("CREAR TABLA:-------------------------------------------- \n\r" + sql);
        connectionJdbcMB.non_query(sql);//CREO LA TABLA PREPIVOT

        //----------------------
        //QUITAMOS LOS VALORES ELIMINADOS DE CADA CATEGORIA
        sql = "";
        List<String> valuesList;//todos los valores (puede y no puede tomar)
        List<String> valuesConfiguredList;//valores configurados (solo los que puede tomar, unos eliminados por el usuario)
        List<String> valuesDiscardedList;//valores descartados de la categoria(los valores que el usuario elimino de la categoria)
        for (int i = 0; i < variablesCrossData.size(); i++) {
            //DETERMINO SI SE HA REALIZADO UNA CONFIGURACION
            valuesDiscardedList = new ArrayList<String>();
            if (variablesCrossData.get(i).getValues().size() != variablesCrossData.get(i).getValuesConfigured().size()) {
                valuesList = variablesCrossData.get(i).getValues();
                valuesConfiguredList = variablesCrossData.get(i).getValuesConfigured();
                for (int j = 0; j < valuesList.size(); j++) {
                    boolean find = false;
                    for (int k = 0; k < valuesConfiguredList.size(); k++) {
                        if (valuesList.get(j).compareTo(valuesConfiguredList.get(k)) == 0) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {//si el valor no se encuentra es por que se descarto
                        valuesDiscardedList.add(valuesList.get(j));
                    }
                }
            }
            if (!valuesDiscardedList.isEmpty()) {
                //DETERMINO CUALES NO VAN        
                switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable                
                    case age://ELIMINAR SEGUN EDAD -----------------------                                       
                        break;
                    case hour://ELIMINAR SEGUN HORA -----------------------                    
                        break;
                    case neighborhoods://ELIMINAR SEGUN NOMBRE DEL BARRIO -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR barrio LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case communes://ELIMINAR SEGUN COMUNA -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR comuna LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case corridors://ELIMINAR SEGUN CORREDOR --------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR corredor LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case areas://ELIMINAR SEGUN ZONA -----------------------     
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR zona LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case genders://ELIMINAR SEGUN GENERO  -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR genero LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case days://ELIMINAR SEGUN DIA SEMANA -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR dia_semana LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case year://ELIMINAR SEGUN AÑO -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR anyo LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case month://ELIMINAR SEGUN MES -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR mes LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                }
            }
        }
        //quitar registros por los valores eliminados en la configuracion
        if (sql.trim().length() != 0) {
            sql = sql.substring(2, sql.length());//elimino primer "OR"                    
            sql = "\n\r DELETE FROM " + prepivotTableName_2 + " WHERE " + sql;
            System.out.println("ELIMINAR VALORES DESCARTADOS EN CONFIGURACION :-------------- \n\r" + sql);
            connectionJdbcMB.non_query(sql);//REALIZO LAS ElIMINACIONES DE LA TABLA PIVOT
        }

        ArrayList<String> columnNames = new ArrayList<String>();
        //no permitir valores nulos
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName_2);
            int ncol = rs.getMetaData().getColumnCount();
            //System.out.println("COLUMNAS DE "+prepivotTableName_2+":-------------------------------------------- \n\r"+String.valueOf(ncol));
            for (int i = 1; i < ncol + 1; i++) {
                //System.out.println("AQUI:-------------------------------------------- \n\r");
                sql = "UPDATE " + prepivotTableName_2 + " SET " + rs.getMetaData().getColumnName(i) + " = 'SIN DATO' "
                        + " WHERE " + rs.getMetaData().getColumnName(i) + " is null;";
                //System.out.println("QUITAR VALORES NULOS:-------------------------------------------- \n\r"+sql);
                columnNames.add(rs.getMetaData().getColumnName(i));

                connectionJdbcMB.non_query(sql);
            }
        } catch (Exception e) {
            System.out.println("ERROR POR EXCEPTION:---" + e.toString());
        }

        //nombres de las columnas
        String columns = "";
        for (int i = 0; i < columnNames.size(); i++) {
            if (i == 0) {
                columns = columnNames.get(i);
            } else {
                columns = columns + ", " + columnNames.get(i);
            }
        }

        //creo una tabla de valores agrupados
        sql = "\n\r DROP TABLE IF EXISTS " + prepivotTableName + ";\n\r"
                + " CREATE TABLE  \n\r"
                + "	" + prepivotTableName + "  \n\r"
                + " AS  \n\r"
                + "select  \n\r"
                + "	" + columns + ",count(*)  \n\r"
                + "from  \n\r"
                + "	" + prepivotTableName_2 + "  \n\r"
                + "group by  \n\r"
                + "	" + columns + "  \n\r"
                + "order by  \n\r"
                + "	" + columns + "; \n\r";

        System.out.println("TABLA FINAL:-------------------------------------------- \n\r" + sql);
        connectionJdbcMB.non_query(sql);//CREO LA TABLA PREPIVOT FINAL        
        System.out.println("ELIMINO TABLA:-------------------------------------------- \n\r" + prepivotTableName_2);
        sql = "\n\r DROP TABLE IF EXISTS " + prepivotTableName_2 + ";\n\r";
        connectionJdbcMB.non_query(sql);//ELIMINO TABLA PREPIVOT INICIAL

    }

    private void addColumToRow(Row row1, String get, String style, int colSpan, int rowSpan) {
        Column column = new Column();
        HtmlOutputText text = new HtmlOutputText();
        column.setStyleClass(style);
        text.setValue(get);
        column.getChildren().add(text);
        column.setStyle("width: 200px;");
        column.setRowspan(rowSpan);
        column.setColspan(colSpan);
        row1.getChildren().add(column);
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public String getInitialDateStr() {
        return initialDateStr;
    }

    public void setInitialDateStr(String initialDateStr) {
        this.initialDateStr = initialDateStr;
    }

    public String getPivotTableName() {
        return pivotTableName;
    }

    public void setPivotTableName(String pivotTableName) {
        this.pivotTableName = pivotTableName;
    }

    public String getPrepivotTableName() {
        return prepivotTableName;
    }

    public void setPrepivotTableName(String prepivotTableName) {
        this.prepivotTableName = prepivotTableName;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public boolean isShowTotals() {
        return showTotals;
    }

    public void setShowTotals(boolean showTotals) {
        this.showTotals = showTotals;
    }

    public ArrayList<Variable> getVariablesCrossData() {
        return variablesCrossData;
    }

    public void setVariablesCrossData(ArrayList<Variable> variablesCrossData) {
        this.variablesCrossData = variablesCrossData;
    }

    public Indicators getCurrentIndicator() {
        return currentIndicator;
    }

    public void setCurrentIndicator(Indicators currentIndicator) {
        this.currentIndicator = currentIndicator;
    }
}
