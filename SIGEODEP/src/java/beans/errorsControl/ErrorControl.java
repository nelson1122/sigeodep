/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.errorsControl;

import beans.relations.RelationVar;

/**
 *
 * @author SANTOS
 */

public class ErrorControl {
    private String varFoundName;//nombre de la variable encontrada
    private String varExeptedName;//nombre de la variable esperada    
    private String value;//valor que esta presentando el conflicto
    private String newValue;//valor que soluciona el conflicto
    private String rowId;//numero de linea del archivo donde esta el error
    private String typeExepted;//me dice el tipo de dato que se esperaba
    private String errorDescription;//me describe el error probocado
    private String errorSubject;//me describe el error probocado(largo)
    private String errorSolution;//me describe la solucion al error
    private String relationDescription;//me describe la solucion al error
    private boolean typeComparisonForCode;
    

    public ErrorControl(RelationVar relationVar,String value, String rowId,String relationDescription) {        
        this.varFoundName = relationVar.getNameFound();
        this.value = value;
        this.varExeptedName = relationVar.getNameExpected();
        this.rowId = rowId;
        this.typeExepted =relationVar.getFieldType();
	this.relationDescription=relationDescription;
        this.typeComparisonForCode=relationVar.getTypeComparisonForCode();
        createDescriptions();
    }
    
    private void createDescriptions()
    {
        
        errorDescription="En la Linea (" + rowId + ")"
                        + ", Columna (" + varFoundName +")"
                        + " el valor (" + value+")"
                        + " No es de tipo: "+varExeptedName;
        errorSubject="Este error fue provocado por que se esperaba encontrar un valor de tipo "+typeExepted;        
	
	if(typeExepted.compareTo("integer")==0)
	{
	    errorSolution="Digite un en la casilla 'nuevo valor' un número entero  y presione resolver";
	}
	else if(typeExepted.compareTo("date")==0)
	{
	    errorSolution="Digite una fecha válida según el formato especificado en la casilla 'nuevo valor' y presione resolver";
	}
	else 
	{ //se esperaba una valor categorico
	    errorSolution="El valor esperado debe ser: ("+this.relationDescription+") seleccione un valor de la lista de valores aceptados o digite un valor válido en la casilla: (nuevo valor) y presione resolver";
	}
        
    }
    
    public String getValue() {
        return value;
    }
    public String getVarExeptedName() {
        return varExeptedName;
    }

    public String getVarFoundName() {
        return varFoundName;
    }

    public String getTypeExepted() {
        return typeExepted;
    }

    public String getRowId() {
        return rowId;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorSolution() {
        return errorSolution;
    }

    public String getErrorSubject() {
        return errorSubject;
    }

    public boolean isTypeComparisonForCode() {
	return typeComparisonForCode;
    }

    public void setTypeComparisonForCode(boolean typeComparisonForCode) {
	this.typeComparisonForCode = typeComparisonForCode;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
