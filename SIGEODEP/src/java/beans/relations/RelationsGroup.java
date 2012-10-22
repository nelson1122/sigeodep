/*
 * esta clase se encargara de administrar los drupos de relaciones entre 
 * variables y valores que exiten dentro del sistema para la 
 * carga de archivos planos y permitir abrir y guardar estas 
 * relaciones dentro de un archivo xml
 * 
 */
package beans.relations;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author santos
 */
public class RelationsGroup {
    private String name;//nombre para el conjunto de relaciones
    private String form;//ficha a la que corresponde 
    private String source;//fuente o proveedor de los datos    
    private ArrayList<RelationVar> relationVarList;//conjunto de relacione entre variables 
    
    public RelationsGroup(String name, String form, String source) {
        this.name = name;
        this.form = form;
        this.source = source;        
        relationVarList=new ArrayList<RelationVar>();        
    }
    
    public void addRelationVar(RelationVar r)
    {
        relationVarList.add(r);
    }
    public void removeRelationVar(String e,String f)//variable esperada(Expected), variable encontrada(Found)
    {
        for (int i = 0; i < relationVarList.size(); i++) 
        {
            if(relationVarList.get(i).compareNames(e,f))
            {
                relationVarList.remove(i);
                break;
            }
        }
    }    
    public RelationVar findRelationVar(String varExcepted,String varFound)
    {
         for (int i = 0; i < relationVarList.size(); i++) {
            if(relationVarList.get(i).compareNames(varExcepted, varFound))
            {
                return relationVarList.get(i);
            }
        }
        return null;
    }
    
    public ArrayList<String> categoricalList(String category) {
	throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public RelationVar findRelationVar(String varFound)
    {
        //retorna una relacion de variables dada una variable encontrada
         for (int i = 0; i < relationVarList.size(); i++) {
            if(relationVarList.get(i).getNameFound().compareTo(varFound)==0)
            {
                return relationVarList.get(i);
            }
        }
        return null;
    }
    public RelationVar findRelationVar2(String varExpected)
    {
        //retorna una relacion de variables dada una variable esperada
         for (int i = 0; i < relationVarList.size(); i++) {
            if(relationVarList.get(i).getNameExpected().compareTo(varExpected)==0)
            {
                return relationVarList.get(i);
            }
        }
        return null;
    }
    public RelationVar addRelationVar(String r)
    {
        return null;
    }
    

    public List<RelationVar> getRelationVarList() {
        return relationVarList;
    }

    public void setRelationVarList(ArrayList<RelationVar> relationVarList) {
        this.relationVarList = relationVarList;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    

    
    
    
}
