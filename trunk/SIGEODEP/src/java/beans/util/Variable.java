/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author and
 */
public class Variable {

    private int id;
    private String name;//Nombre para la variable
    private String table;//tabla donde estan los datos
    private String field;//columna de la tabla donde estan los datos
    private String generic_table;//tabla donde se encuentra la categoria
    private String source_table;//tabla donde se debe buscar el valor
    private List<String> values;//valores que puede tomar en la categoria
    private List<String> valuesId;//identificadores de los valores que puede tomar en la categoria
    private List<String> valuesConfigured;//valores que se configuraron(los mismos o menos valores que valuesId)
    private boolean configurable = false;//me dice si esta variable acepta o no realizarle configuracion (adicionar categoria)

    public Variable(String name, String table, String field, String generic_table, boolean conf, String source_table) {
        this.name = name;
        this.table = table;
        this.field = field;
        this.generic_table = generic_table;
        this.values = new ArrayList<String>();
        this.valuesId = new ArrayList<String>();
        this.valuesConfigured = new ArrayList<String>();
        this.configurable = conf;
        this.source_table = source_table;
    }

    public Variable(String name, String generic_table, boolean conf, String source_table) {
        this.name = name;
        this.generic_table = generic_table;
        this.values = new ArrayList<String>();
        this.valuesId = new ArrayList<String>();
        this.valuesConfigured = new ArrayList<String>();
        this.configurable = conf;
        this.source_table = source_table;
    }

    public Variable() {
        this.values = new ArrayList<String>();
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getGeneric_table() {
        return generic_table;
    }

    public void setGeneric_table(String generic_table) {
        this.generic_table = generic_table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public List<String> getValuesConfigured() {
        return valuesConfigured;
    }

    public void setValuesConfigured(List<String> valuesConfigured) {
        this.valuesConfigured = valuesConfigured;
    }

    public List<String> getValuesId() {
        return valuesId;
    }

    public void setValuesId(List<String> valuesId) {
        this.valuesId = valuesId;
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    public void filterYear(Date initialDate, Date endDate) {
        //crear la lista de años que se encuentran entre dos fechas
        int initialYear = initialDate.getYear() + 1900;
        int endYear = endDate.getYear() + 1900;
        values = new ArrayList<String>();
        valuesId = new ArrayList<String>();
        valuesConfigured = new ArrayList<String>();
        for (int i = initialYear; i <= endYear; i++) {
            values.add(String.valueOf(i));
            valuesId.add(String.valueOf(i));
            valuesConfigured.add(String.valueOf(i));
        }

    }

    public String getSource_table() {
        return source_table;
    }

    public void setSource_table(String source_table) {
        this.source_table = source_table;
    }   
    
    
}