/*
 *la funcion de esta clase es de cargar los datos de las fichas existentes
 * los campos para cada ficha y los valores categoricos, se realiza esta precarga
 * por que se tiene que estar accediendo a la base de datos constantemente por lo
 * que el usuario notaria lentitud en el proceso de asociacion de variables y valores
 */
package managedBeans.preload;

import beans.connection.ConnectionJDBC;
import beans.enumerators.DataTypeEnum;
import beans.lists.Category;
import beans.lists.Field;
import beans.lists.Form;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author santos
 */
@ManagedBean(name = "formsAndFieldsDataMB")
@SessionScoped
public class FormsAndFieldsDataMB implements Serializable {

    private ResultSet rsForms;
    private ResultSet rsCategorical;
    private ResultSet rsFields;
    private ArrayList<Form> forms = new ArrayList<Form>();
    private boolean loaded = false;
    private String nameForm;

    public FormsAndFieldsDataMB() {
        /**
         * Creates a new instance of FormsAndFieldsDataMB
         */
    }

    public void reset() {
    }

    public void loadFormsData() {
        /**
         * creacion de una lista de todas las variables de los formularios asi
         * como de sus valores esperados
         */
        int amount = 0;//cantidad de consultas necesarias(para calcular porcentaje de progreso)
        if (!loaded)//no se han cargado aun los datos de los formularios
        {
            try {
                ConnectionJDBC conx = new ConnectionJDBC();
                conx.connect();
                rsForms = conx.consult("SELECT * FROM forms;");
                //**********************************************RECORRER FORMULARIOS
                while (rsForms.next()) {
                    Form newForm = new Form(rsForms.getString("form_name"), rsForms.getString("form_id"));
                    rsFields = conx.consult("SELECT * FROM fields WHERE form_id like '" + rsForms.getString("form_id") + "'");
                    //**********************RECORRER CAMPOS
                    while (rsFields.next()) {
                        Field newField = new Field(
                                rsFields.getString("form_id"),
                                rsFields.getString("field_name"),
                                rsFields.getString("field_order"),
                                rsFields.getString("field_description"),
                                rsFields.getString("field_type"),
                                rsFields.getBoolean("field_optional"));
//                        if (  newField.getFieldType().compareTo("integer") != 0 && 
//                              newField.getFieldType().compareTo("date") != 0 && 
//                              newField.getFieldType().compareTo("age") != 0 &&
//                              newField.getFieldType().compareTo("military") != 0 &&
//                              newField.getFieldType().compareTo("text") != 0) {
                        switch (DataTypeEnum.convert(newField.getFieldType())) {//tipo de relacion
                            case NOVALUE:
                                rsCategorical = conx.consult("SELECT * FROM " + newField.getFieldType());
                                while (rsCategorical.next()) {
                                    //Category newCategory = new Category(rsCategorical.getString(1), rsCategorical.getString(2));
                                    //newField.categoricalList.add(newCategory);
                                    newField.categoricalNamesList.add(rsCategorical.getString(2));
                                    newField.categoricalCodeList.add(rsCategorical.getString(1));
                                    amount++;
                                }
                                break;
                        }
//                        }
                        newForm.fieldsList.add(newField);
                    }
                    forms.add(newForm);
                }
                loaded = true;
                System.out.println("LA CANTIDAD DE PROCESOS NECESARIOS FUE DE " + String.valueOf(amount));
                conx.disconnect();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    public Field searchField(String nomVarExpected) {
        ArrayList<Field> fieldsList;
        for (int i = 0; i < forms.size(); i++) {
            if (forms.get(i).getCode().compareTo(nameForm) == 0) {
                fieldsList = forms.get(i).fieldsList;
                for (int j = 0; j < fieldsList.size(); j++) {
                    if (fieldsList.get(j).getFieldName().compareTo(nomVarExpected) == 0) {
                        return fieldsList.get(j);
                    }
                }
            }
        }
        return null;
    }

    public String variableDescription(String nameVariable) {
        ArrayList<Field> fieldsList;
        ArrayList<String> returnList = new ArrayList<String>();
        for (int i = 0; i < forms.size(); i++) {
            if (forms.get(i).getCode().compareTo(nameForm) == 0) {
                fieldsList = forms.get(i).fieldsList;
                for (int j = 0; j < fieldsList.size(); j++) {
                    if (fieldsList.get(j).getFieldName().compareTo(nameVariable) == 0) {
                        return fieldsList.get(j).getFieldDescription();
                    }
                }
            }
        }
        return "";
    }

    public ArrayList<String> categoricalNameList(String typeVarExepted, int amount) {
        /*
         * retorna una lista con los nombres pertenecientes a una categoria
         * amount me idica cuantos registros tendra la lista, si amount es 0
         * indica que se la lista contendra todos los que existan.
         */
        ArrayList<Field> fieldsList;
        ArrayList<String> returnList = new ArrayList<String>();
        for (int i = 0; i < forms.size(); i++) {
            if (forms.get(i).getCode().compareTo(nameForm) == 0) {
                fieldsList = forms.get(i).fieldsList;
                for (int j = 0; j < fieldsList.size(); j++) {
                    if (fieldsList.get(j).getFieldName().compareTo(typeVarExepted) == 0) {
                        if (amount != 0) {
                            for (int k = 0; k < amount; k++) {
                                if (fieldsList.get(j).categoricalNamesList.size() > k) {
                                    returnList.add(fieldsList.get(j).categoricalNamesList.get(k));
                                }
                            }
                            return returnList;
                        } else {
                            return fieldsList.get(j).categoricalNamesList;
                        }
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<String> categoricalCodeList(String typeVarExepted, int amount) {
        /*
         * retorna una lista con los codigos pertenecientes a una categoria
         * amount me idica cuantos registros tendra la lista, si amount es 0
         * indica que se la lista contendra todos los que existan.
         */
        ArrayList<Field> fieldsList;
        ArrayList<String> returnList = new ArrayList<String>();
        for (int i = 0; i < forms.size(); i++) {
            if (forms.get(i).getCode().compareTo(nameForm) == 0) {
                fieldsList = forms.get(i).fieldsList;
                for (int j = 0; j < fieldsList.size(); j++) {
                    if (fieldsList.get(j).getFieldName().compareTo(typeVarExepted) == 0) {
                        if (amount != 0) {
                            for (int k = 0; k < amount; k++) {
                                if (fieldsList.get(j).categoricalCodeList.size() > k) {
                                    returnList.add(fieldsList.get(j).categoricalCodeList.get(k));
                                }
                            }
                            return returnList;
                        } else {
                            return fieldsList.get(j).categoricalCodeList;
                        }
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<Form> getForms() {
        return forms;
    }

    public void setForms(ArrayList<Form> forms) {
        this.forms = forms;
    }

    public String getNameForm() {
        return nameForm;
    }

    public void setNameForm(String nameForm) {
        this.nameForm = nameForm;
    }
}
