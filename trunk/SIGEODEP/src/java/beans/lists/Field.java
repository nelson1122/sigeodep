/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.lists;

import java.util.ArrayList;

/**
 *
 * @author santos
 */
public class Field {
    
    private String formId;
    private String fielName;
    private String fieldOrder;
    private String fieldDescription;
    private String fieldType;//me dice el tipo de dato o tabla categorica
    private boolean optional;
    //public  ArrayList<Category> categoricalList=new  ArrayList<Category>();
    public  ArrayList<String> categoricalNamesList=new  ArrayList<String>();
    public  ArrayList<String> categoricalCodeList=new  ArrayList<String>();

    public Field(String formId, String fielName, String fieldOrder, String fieldDescription, String fieldType, boolean optional) {
        this.formId = formId;
        this.fielName = fielName;
        this.fieldOrder = fieldOrder;
        this.fieldDescription = fieldDescription;
        this.fieldType = fieldType;
        this.optional = optional;
    }

    public String getFieldName() {
        return fielName;
    }

    public void setFieldName(String fielName) {
        this.fielName = fielName;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public String getFieldOrder() {
        return fieldOrder;
    }

    public void setFieldOrder(String fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    

    
    
    
}
