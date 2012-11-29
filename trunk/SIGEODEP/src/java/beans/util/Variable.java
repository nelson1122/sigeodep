/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.util;

import java.util.ArrayList;
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
    private List<String> values;//valores que puede tomar en la categoria

    public Variable(String name, String table, String field, String generic_table) {
        this.name = name;
        this.table = table;
        this.field = field;
        this.generic_table = generic_table;
        this.values = new ArrayList<String>();
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
    public String toString(){
        return this.name;
    }
}
