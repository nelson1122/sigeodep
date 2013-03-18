/*
 * http://quebrandoparadigmas.com/?p=751
 */
package beans.util;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author santos
 */

/*
 * ingresa a esta clase cada que hay un cambio de fase
 * (fase= solicitud al servidor)
 */


public class DefaultPhaseListener implements PhaseListener {

    @Override
    public void afterPhase(PhaseEvent event) {
        //System.out.println("-");
        FacesContext facesContext = event.getFacesContext();
        ExternalContext ext = facesContext.getExternalContext();
        String ctxPath = ((ServletContext) ext.getContext()).getContextPath();
        String currentPage = facesContext.getViewRoot().getViewId();
        boolean isLoginPage = (currentPage.lastIndexOf("index.xhtml") > -1);
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);        
        if (!isLoginPage) {
            if(currentPage.indexOf("html") == -1){
                isLoginPage=true;
            }
        }
        
        //System.out.println(currentPage+ " - " + ctxPath+ " - " + session+ " - " + isLoginPage);
        if (!isLoginPage) {
            //System.out.println("A");
            if (session == null) {
                //System.out.println("B");
                try {
                    System.out.println("salida del programa por que sesion es null" + ctxPath);
                    ext.redirect(ctxPath + "/index.html?v=timeout");
                    System.out.println("enviado a: " + ctxPath + "/index.html?v=timeout");
                    //System.out.println("D");
                } catch (Throwable t) {
                    //System.out.println("E");
                    System.out.println("Fallo al expirar sesion " + t.toString());
                    throw new FacesException("Session timed out", t);
                }
            } else {
                //System.out.println("C");
                Object currentUser = session.getAttribute("username");
                if (!isLoginPage && (currentUser == null || currentUser == "")) {
                    try {
                        //System.out.println("F");
                        System.out.println("salida del programa por que no hay usuario registrado" + ctxPath);
                        ext.redirect(ctxPath + "/index.html?v=timeout");
                    } catch (Throwable t) {
                        //System.out.println("G");
                        System.out.println("Fallo al expirar sesion " + t.toString());
                        throw new FacesException("Session timed out", t);
                    }
                }
            }
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        //System.out.println("-");
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
