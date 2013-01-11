/*
 * http://quebrandoparadigmas.com/?p=751
 */
package beans.util;

import beans.connection.ConnectionJdbcMB;
import java.sql.ResultSet;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;
import managedBeans.login.LoginMB;

/**
 *
 * @author Desarrollo
 */
public class DefaultPhaseListener implements PhaseListener {

    private static final long serialVersionUID = -1065005858605121693L;
    //private static final String indexPage = "index.xhtml";
    ConnectionJdbcMB connectionJdbcMB;
    LoginMB loginMB;
    //@EJB
    //private LoginMB loginMB;

    @Override
    public void afterPhase(PhaseEvent event) {
        // Antes de iniciar la fase, no hago nada
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        FacesContext context2 = FacesContext.getCurrentInstance();
        ExternalContext ext = context.getExternalContext();
        HttpSession session = (HttpSession) ext.getSession(false);
        boolean newSession = (session == null) || (session.isNew());
        boolean postback = !ext.getRequestParameterMap().isEmpty();
        boolean timedout = postback && newSession;
        if (timedout) {
            //Application app = context.getApplication();
            //ViewHandler viewHandler = app.getViewHandler();
            //UIViewRoot view = viewHandler.createView(context, "/" + indexPage);
            //context.setViewRoot(view);
            //context.renderResponse();
            connectionJdbcMB = (ConnectionJdbcMB) context2.getApplication().evaluateExpressionGet(context2, "#{connectionJdbcMB}", ConnectionJdbcMB.class);
            loginMB = (LoginMB) context2.getApplication().evaluateExpressionGet(context2, "#{loginMB}", LoginMB.class);
            
            
            try {
                loginMB.logout1();
                //if (rs.next()) {
                //    System.out.println("AQUI SE MODIFICA DB POR SALIDA DE SESION");
                //} else {
                //    System.out.println("NO SE PUDO RESTAURAR LA BANDERA DE SESION");
                //}
                //viewHandler.renderView(context, view);
                //	context.responseComplete();
                //if (loginMB != null) {
                //}
            } catch (Throwable t) {
                throw new FacesException("Sesión ha expirado!!!", t);
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
