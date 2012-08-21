/*
 * Clase que maneja la informacion de cada una de
 * las relaciones entre variables
 */
package beans.relations;

import java.util.ArrayList;

/**
 *
 * @author santos
 */
public class RelationVar {

    private String nameExpected;//nombre esperado
    private boolean comparisonForcode;//tipo de comparacion puede ser por valor o por codigo
    private String fieldType;//nombre de la tabla(cuando es categorica) o tipo de dato(integer,date,text)
    private String nameFound;//nombre encontrado
    private String dateFormat;//si el campo es de tipo fecha saber que formato tiene
    private ArrayList<RelationValue> relationValueList;//conjunto de relaciones entre valores
    private ArrayList<String> discardedValues;

    public RelationVar(String nameExpected, String nameFound, String fieldType, boolean comparisonForcode,String dateFormat) {
        this.nameExpected = nameExpected;
        this.comparisonForcode = comparisonForcode;
        this.fieldType = fieldType;
        this.nameFound = nameFound;
        this.dateFormat=dateFormat;
        this.relationValueList = new ArrayList<RelationValue>();
        this.discardedValues=new ArrayList<String>();
        
    }
    
    public boolean compareNames(String e, String f) {
        if (e.compareTo(nameExpected) == 0 && f.compareTo(nameFound) == 0) {
            return true;
        } else {
            return false;
        }
    }
    public boolean compareNames(String e) {
        if (e.compareToIgnoreCase(nameFound) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addDiscartedValue(String n) {        
        discardedValues.add(n);
    }
    
    public void removeDiscartedValue(String n) {
        for (int i = 0; i < discardedValues.size(); i++) {
            if(discardedValues.get(i).compareTo(n)==0)
            {
                discardedValues.remove(i);
                break;
            }
        }
    }
    
    //valor esperada(Expected), variable encontrada(Found)
    public void addRelationValue(String e, String f) {
        RelationValue newRelationValue=new RelationValue(e,f);
        relationValueList.add(newRelationValue);
    }
    
    public void removeRelationValue(String e, String f) {
        for (int i = 0; i < relationValueList.size(); i++) {
            if (relationValueList.get(i).compareNames(e, f)) {
                relationValueList.remove(i);
                break;
            }
        }
    }

    public ArrayList<RelationValue> getRelationValueList() {
        return relationValueList;
    }

    public void setRelationValueList(ArrayList<RelationValue> relationValueList) {
        this.relationValueList = relationValueList;
    }

    public String getNameExpected() {
        return nameExpected;
    }

    public void setNameExpected(String nameExpected) {
        this.nameExpected = nameExpected;
    }

    public String getNameFound() {
        return nameFound;
    }

    public void setNameFound(String nameFound) {
        this.nameFound = nameFound;
    }

    public boolean getTypeComparisonForCode() {
        return comparisonForcode;
    }

    public void setTypeComparisonForCode(boolean typeComparison) {
        this.comparisonForcode = typeComparison;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public ArrayList<String> getDiscardedValues() {
        return discardedValues;
    }

    public void setDiscardedValues(ArrayList<String> discardedValues) {
        this.discardedValues = discardedValues;
    }
    
    
    
}
