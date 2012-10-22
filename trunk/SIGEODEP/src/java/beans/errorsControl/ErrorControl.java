/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.errorsControl;

import beans.enumerators.DataTypeEnum;
import beans.relations.RelationVar;

/**
 *
 * @author SANTOS
 */
public class ErrorControl {

    //private String varFoundName;//nombre de la variable encontrada
    //p/rivate String varExeptedName;//nombre de la variable esperada    
    private String value="";//valor que esta presentando el conflicto
    private String newValue="";//valor que soluciona el conflicto
    private String rowId="";//numero de linea del archivo donde esta el error
    private String errorDescription="";//me describe el error probocado
    private String errorSubject="";//me describe el error probocado(largo)
    private String errorSolution="";//me describe la solucion al error
    private String relationDescription="";//me describe la solucion al error
    RelationVar relationVar;

    public ErrorControl(RelationVar relationVar, String value, String rowId, String relationDescription) {
        this.relationVar = relationVar;
        this.value = value;
        this.rowId = rowId;
        this.relationDescription = relationDescription;
        createDescriptions();
    }

    private void createDescriptions() {

        if (value.compareTo("REQUIRED VALIDATION") == 0) {            
            errorSolution = relationDescription;
            errorDescription=rowId;
            errorSubject ="";
        } else {
            
            if(relationVar==null)
            {
                System.out.println("MAS ERRORES");
                        
            }else{
            errorDescription = "En la Linea (" + rowId + ")"
                    + ", Columna (" + relationVar.getNameFound() + ")"
                    + " el valor (" + value + ")"
                    + " No es de tipo: " + relationVar.getNameExpected();
            errorSubject = "Este error fue provocado por que se esperaba encontrar un valor de tipo " + relationVar.getTypeComparisonForCode();
            switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                case text:
                    break;
                case integer:
                    errorSolution = "Digite en la casilla 'nuevo valor' un número entero  y presione resolver";
                    break;
                case age:
                    errorSolution = "Digite en la casilla 'nuevo valor' una edad válida  y presione resolver. La edad debe ser un número entero o ser especificada en años y meses ejemplo: 3 años 4 meses";
                    break;
                case military:
                    errorSolution = "Una hora válida es: \r\n - numero de 4 cifras de 0000 a 2400 \r\n - horas:minutos (separados por +) \r\n - horas+minutos (separados por :) \r\n - horas:minutos:segundos:centesimas.milesimas ejemplo 20:23:12.200";
                    break;
                case date:
                    errorSolution = "El valor(" + value + ") no cumple con el formato(" + relationVar.getDateFormat() + ") valido para fecha";
                    break;
                case day:
                    errorSolution = "Digite en la casilla 'nuevo valor' un dia válido y presione resolver. El dia debe ser un número entero de 1 a 31";
                    break;
                case month:
                    errorSolution = "Digite en la casilla 'nuevo valor' un mes válido y presione resolver. El mes es un número entero de 1 a 12";
                    break;
                case year:
                    errorSolution = "Digite en la casilla 'nuevo valor' un año válido y presione resolver. El año es un número de 2 o cuatro cifras";
                    break;
                case minute:
                    errorSolution = "Digite en la casilla 'nuevo valor' un minuto válido y presione resolver. El minuto es un número de 0 59";
                    break;
                case hour:
                    errorSolution = "Digite en la casilla 'nuevo valor' una hora válida y presione resolver. La hora es un número de 0 a 24";
                    break;
                case percentage:
                    errorSolution = "Digite en la casilla 'nuevo valor' un porcentaje válido y presione resolver. El porcentaje es un número de 1 a 100";
                    break;
                //            case degree:
                //                errorSolution = "Digite en la casilla 'nuevo valor' una grado válido y presione resolver. El grado puede ser: 1, 2, 3";
                //                break;
                case NOVALUE:
                    errorSolution = "El valor esperado debe ser: (" + this.relationDescription + ") seleccione un valor de la lista de valores aceptados o digite un valor válido en la casilla (nuevo valor) y presione resolver";
                    break;
            }
            }
        }
    }

    public RelationVar getRelationVar() {
        return relationVar;
    }

    public String getValue() {
        return value;
    }

//    public String getVarExeptedName() {
//        return varExeptedName;
//    }
//
//    public String getVarFoundName() {
//        return varFoundName;
//    }
//
//    public String getTypeExepted() {
//        return typeExepted;
//    }
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

//    public boolean isTypeComparisonForCode() {
//        return typeComparisonForCode;
//    }
//
//    public void setTypeComparisonForCode(boolean typeComparisonForCode) {
//        this.typeComparisonForCode = typeComparisonForCode;
//    }
    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
