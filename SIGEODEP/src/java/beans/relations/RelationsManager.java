/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.relations;

import java.util.ArrayList;

/**
 *
 * @author santos
 */
public class RelationsManager 
{
    private ArrayList<RelationsGroup> relationsGroupList;

    public RelationsManager() 
    {
        relationsGroupList=new ArrayList<RelationsGroup>();
    }
    public void addRelationsGroup(RelationsGroup rg)
    {
        relationsGroupList.add(rg);
    }
    public void removeRelationsGroup(String name)
    {
        for (int i = 0; i < relationsGroupList.size(); i++) {
            if(relationsGroupList.get(i).getName().compareTo(name)==0)
            {
                relationsGroupList.remove(i);
                break;
            }
        }
    }
    public RelationsGroup findRelationsGroup(String name)
    {
        for (int i = 0; i < relationsGroupList.size(); i++) {
            if(relationsGroupList.get(i).getName().compareTo(name)==0)
            {
                return relationsGroupList.get(i);
            }
        }
        return null;
    }

    public ArrayList<RelationsGroup> getRelationsGroupList() {
        return relationsGroupList;
    }

    public void setRelationsGroupList(ArrayList<RelationsGroup> relationsGroupList) {
        this.relationsGroupList = relationsGroupList;
    }
    
}
