/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.relations;

/**
 *
 * @author santos
 */
public class RelationValue 
{
    private String nameExpected;//nombre esperado    
    private String nameFound;//nombre encontrado
    
    
    public RelationValue(String nameExpected, String nameFound) {
        this.nameExpected = nameExpected;
        this.nameFound = nameFound;
    }
    
    public boolean compareNames(String e,String f)
    {
        if(e.compareTo(nameExpected)==0 && f.compareTo(nameFound)==0) return true;
        else return false;
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
}
