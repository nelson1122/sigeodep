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
public class Form {

    private String name;
    private String code;
    public ArrayList<Field> fieldsList = new ArrayList<Field>();

    public Form(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
