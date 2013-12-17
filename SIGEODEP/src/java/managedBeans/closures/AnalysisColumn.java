/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.closures;

/**
 *
 * @author santos
 */
public class AnalysisColumn {
    /*
     * clase que representa el analisis realizado a una columna a imputar
     * no todos los valores seran usados, depende de la columna que se trabaje
     */    
    private String sqlForImputation = null;//sql que permite realizar la imputacion de esta columna
    private String description = null;//descripcion de imputacion(de haber sido realizada)
    private String columnName = null;//nombre de la columna en base de datos
    private String variableName = null;//nombre de la variable que representa la columna
    private double nullPercentagePerColumn = -1;//porcentage de nulos
    private int nullCountPerColumn = -1;//porcentage de nulos
    private double averagePerColumn = -1;//promedio por columna
    private String minDatePerColumn = null;//fecha minima en columna
    private String maxDatePerColumn = null;//fecha maxima en columna
    private String minNumberPerColumn = null;//numero minimo en columna
    private String maxNumberPerColumn = null;//numero maximo en columna
    private String modePerColumn = null;//moda que tiene la columna
    private String fiveFrecuents = null;//frecuentes (maximo 5)

    public AnalysisColumn() {
    }

    public String getSqlForImputation() {
        return sqlForImputation;
    }

    public void setSqlForImputation(String sqlForImputation) {
        this.sqlForImputation = sqlForImputation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public double getNullPercentagePerColumn() {
        return nullPercentagePerColumn;
    }

    public void setNullPercentagePerColumn(double nullPercentagePerColumn) {
        this.nullPercentagePerColumn = nullPercentagePerColumn;
    }

    public int getNullCountPerColumn() {
        return nullCountPerColumn;
    }

    public void setNullCountPerColumn(int nullCountPerColumn) {
        this.nullCountPerColumn = nullCountPerColumn;
    }

    public double getAveragePerColumn() {
        return averagePerColumn;
    }

    public void setAveragePerColumn(double averagePerColumn) {
        this.averagePerColumn = averagePerColumn;
    }

    public String getMinDatePerColumn() {
        return minDatePerColumn;
    }

    public void setMinDatePerColumn(String minDatePerColumn) {
        this.minDatePerColumn = minDatePerColumn;
    }

    public String getMaxDatePerColumn() {
        return maxDatePerColumn;
    }

    public void setMaxDatePerColumn(String maxDatePerColumn) {
        this.maxDatePerColumn = maxDatePerColumn;
    }

    public String getMinNumberPerColumn() {
        return minNumberPerColumn;
    }

    public void setMinNumberPerColumn(String minNumberPerColumn) {
        this.minNumberPerColumn = minNumberPerColumn;
    }

    public String getMaxNumberPerColumn() {
        return maxNumberPerColumn;
    }

    public void setMaxNumberPerColumn(String maxNumberPerColumn) {
        this.maxNumberPerColumn = maxNumberPerColumn;
    }

    public String getModePerColumn() {
        return modePerColumn;
    }

    public void setModePerColumn(String modePerColumn) {
        this.modePerColumn = modePerColumn;
    }

    public String getFiveFrecuents() {
        return fiveFrecuents;
    }

    public void setFiveFrecuents(String fiveFrecuents) {
        this.fiveFrecuents = fiveFrecuents;
    }
}
