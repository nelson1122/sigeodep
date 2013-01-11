/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import beans.util.RowDataTable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.ActivitiesFacade;
import model.dao.UsersConfigurationFacade;
import model.dao.UsersFacade;
import model.pojo.Activities;
import model.pojo.Users;
import model.pojo.UsersConfiguration;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "usersMB")
@SessionScoped
public class UsersMB {

    /**
     * ACTIVIDADES DURANTE EL HECHO
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    UsersFacade usersFacade;
    @EJB
    UsersConfigurationFacade usersConfigurationFacade;
    private List<Users> usersList;
    private Users currentUser;
    private String name = "";
    private String newName = "";
    private String job = "";
    private String newJob = "";
    private String institution = "";
    private String newInstitution = "";
    private String telephone = "";
    private String newtelephone = "";
    private String email = "";
    private String newEmail = "";
    private String password = "";
    private String newPasword = "";
    private String address = "";
    private String newAddress = "";
    private String login = "";
    private String newLogin = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private boolean permission1 = true;
    private boolean permission2 = true;
    private boolean permission3 = true;
    private boolean permission4 = true;
    private boolean permission5 = true;
    private boolean newPermission1 = true;
    private boolean newPermission2 = true;
    private boolean newPermission3 = true;
    private boolean newPermission4 = true;
    private boolean newPermission5 = true;

    /**
     * Creates a new instance of UsersMB
     */
    public UsersMB() {
        /*
         * edicion creacion y modificacion de usuarios del sistema
         *
         */
    }

    public void load() {
        currentUser = null;
        if (selectedRowDataTable != null) {
            currentUser = usersFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
        if (currentUser != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;

            if (currentUser.getUserName() != null) {
                name = currentUser.getUserName();
            } else {
                name = "";
            }
            if (currentUser.getUserJob() != null) {
                job = currentUser.getUserJob();
            } else {
                job = "";
            }
            if (currentUser.getUserInstitution() != null) {
                institution = currentUser.getUserInstitution();
            } else {
                institution = "";
            }
            if (currentUser.getUserTelephone() != null) {
                telephone = currentUser.getUserTelephone();
            } else {
                telephone = "";
            }
            if (currentUser.getUserEmail() != null) {
                email = currentUser.getUserEmail();
            } else {
                email = "";
            }
            if (currentUser.getUserPassword() != null) {
                password = currentUser.getUserPassword();
            } else {
                password = "";
            }
            if (currentUser.getUserAddress() != null) {
                address = currentUser.getUserAddress();
            } else {
                address = "";
            }
            if (currentUser.getUserLogin() != null) {
                login = currentUser.getUserLogin();
            } else {
                login = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentUser != null) {
            try {
                usersFacade.remove(currentUser);
                currentUser = null;
                selectedRowDataTable = null;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Exception e) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "NO REALIZADO", "El registro no puede ser eliminado por que su informacion esta siendo utilizada");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        }
        createDynamicTable();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentUser != null) {
            if (name.trim().length() == 0 || password.trim().length() == 0 || login.trim().length() == 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Faltan datos", "los campos: NOMBRE, PASWORD y LOGIN son obligatorios");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                Users u = usersFacade.findByLogin(login);
                if (currentUser.getUserLogin().compareTo(u.getUserLogin()) == 0) {
                    currentUser.setUserName(name);
                    currentUser.setUserLogin(login);
                    currentUser.setUserName(name);
                    currentUser.setUserJob(job);
                    currentUser.setUserInstitution(institution);
                    currentUser.setUserTelephone(telephone);
                    currentUser.setUserEmail(email);
                    currentUser.setUserAddress(address);
                    currentUser.setUserPassword(password);
                    usersFacade.edit(currentUser);
                    name = "";
                    currentUser = null;
                    selectedRowDataTable = null;
                    createDynamicTable();
                    btnEditDisabled = true;
                    btnRemoveDisabled = true;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El login digitado ya esta en uso");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
        }
    }

    public void saveRegistry() {
        if (newName.trim().length() == 0 || newPasword.trim().length() == 0 || newLogin.trim().length() == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Faltan datos", "los campos: NOMBRE, PASWORD y LOGIN son obligatorios");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            Users u = usersFacade.findByLogin(newLogin);
            if (u == null) {
                newName = newName.toUpperCase();
                Users newRegistry = new Users();
                newRegistry.setUserId(usersFacade.findMax() + 1);
                newRegistry.setUserLogin(newLogin);
                newRegistry.setUserName(newName);
                newRegistry.setUserJob(newJob);
                newRegistry.setUserInstitution(newInstitution);
                newRegistry.setUserTelephone(newtelephone);
                newRegistry.setUserEmail(newEmail);
                newRegistry.setUserAddress(newAddress);
                newRegistry.setUserPassword(newPasword);
                UsersConfiguration usersConfiguration = new UsersConfiguration(newRegistry.getUserId());
                newRegistry.setUsersConfiguration(usersConfiguration);                
                //usersFacade.edit(currentUser);
                usersFacade.create(newRegistry);
                newRegistry();
                currentUser = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El login digitado ya esta en uso");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void newRegistry() {
        name = "";
        newName = "";
        job = "";
        newJob = "";
        institution = "";
        newInstitution = "";
        telephone = "";
        newtelephone = "";
        email = "";
        newEmail = "";
        password = "";
        newPasword = "";
        address = "";
        newAddress = "";
        login = "";
        newLogin = "";
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            usersList = usersFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (usersList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < usersList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        usersList.get(i).getUserId().toString(),
                        usersList.get(i).getUserLogin(),
                        usersList.get(i).getUserName(),
                        usersList.get(i).getUserJob(),
                        usersList.get(i).getUserInstitution(),
                        usersList.get(i).getUserTelephone(),
                        usersList.get(i).getUserEmail(),
                        usersList.get(i).getUserAddress()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        usersList = usersFacade.findAll();
        for (int i = 0; i < usersList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    usersList.get(i).getUserId().toString(),
                    usersList.get(i).getUserLogin(),
                    usersList.get(i).getUserName(),
                    usersList.get(i).getUserJob(),
                    usersList.get(i).getUserInstitution(),
                    usersList.get(i).getUserTelephone(),
                    usersList.get(i).getUserEmail(),
                    usersList.get(i).getUserAddress()));
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
    }

    public int getCurrentSearchCriteria() {
        return currentSearchCriteria;
    }

    public void setCurrentSearchCriteria(int currentSearchCriteria) {
        this.currentSearchCriteria = currentSearchCriteria;
    }

    public String getCurrentSearchValue() {
        return currentSearchValue;
    }

    public void setCurrentSearchValue(String currentSearchValue) {
        this.currentSearchValue = currentSearchValue;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentActivitiy(Users currentUser) {
        this.currentUser = currentUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(String newInstitution) {
        this.newInstitution = newInstitution;
    }

    public String getNewJob() {
        return newJob;
    }

    public void setNewJob(String newJob) {
        this.newJob = newJob;
    }

    public String getNewPasword() {
        return newPasword;
    }

    public void setNewPasword(String newPasword) {
        this.newPasword = newPasword;
    }

    public String getNewtelephone() {
        return newtelephone;
    }

    public void setNewtelephone(String newtelephone) {
        this.newtelephone = newtelephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isBtnEditDisabled() {
        return btnEditDisabled;
    }

    public void setBtnEditDisabled(boolean btnEditDisabled) {
        this.btnEditDisabled = btnEditDisabled;
    }

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(String newLogin) {
        this.newLogin = newLogin;
    }

    public boolean isPermission1() {
        return permission1;
    }

    public void setPermission1(boolean permission1) {
        this.permission1 = permission1;
    }

    public boolean isPermission2() {
        return permission2;
    }

    public void setPermission2(boolean permission2) {
        this.permission2 = permission2;
    }

    public boolean isPermission3() {
        return permission3;
    }

    public void setPermission3(boolean permission3) {
        this.permission3 = permission3;
    }

    public boolean isPermission4() {
        return permission4;
    }

    public void setPermission4(boolean permission4) {
        this.permission4 = permission4;
    }

    public boolean isPermission5() {
        return permission5;
    }

    public void setPermission5(boolean permission5) {
        this.permission5 = permission5;
    }

    public boolean isNewPermission1() {
        return newPermission1;
    }

    public void setNewPermission1(boolean newPermission1) {
        this.newPermission1 = newPermission1;
    }

    public boolean isNewPermission2() {
        return newPermission2;
    }

    public void setNewPermission2(boolean newPermission2) {
        this.newPermission2 = newPermission2;
    }

    public boolean isNewPermission3() {
        return newPermission3;
    }

    public void setNewPermission3(boolean newPermission3) {
        this.newPermission3 = newPermission3;
    }

    public boolean isNewPermission4() {
        return newPermission4;
    }

    public void setNewPermission4(boolean newPermission4) {
        this.newPermission4 = newPermission4;
    }

    public boolean isNewPermission5() {
        return newPermission5;
    }

    public void setNewPermission5(boolean newPermission5) {
        this.newPermission5 = newPermission5;
    }
}
