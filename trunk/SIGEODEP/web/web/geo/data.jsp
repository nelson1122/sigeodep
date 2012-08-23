<%-- 
    Document   : data
    Created on : Jun 6, 2012, 6:41:46 PM
    Author     : and
--%>
<%@page import="managedBeans.geo.MyFeatureCollection"%>
<%-- Set the content type header with the JSP directive --%>
<%@ page contentType="application/json" %>

<%-- Set the content disposition header --%>
<%
    String variable = request.getParameter("variable");
    System.out.println(variable);
    String value = request.getParameter("value");
    System.out.println(value);
    String option = request.getParameter("option");
    System.out.println(option);
    if(option == null){
        option = "1";
    }
    MyFeatureCollection f = new MyFeatureCollection(variable, value, option);
    String toJson = f.getGeoJSON().toString();
    response.setContentType("application/json");
    response.setHeader("Content-Disposition", "inline");
    response.getWriter().write(toJson);
%>




