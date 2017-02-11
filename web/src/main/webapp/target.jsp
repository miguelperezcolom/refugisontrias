<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Enumeration" %><%--
  Created by IntelliJ IDEA.
  User: miguel
  Date: 8/2/17
  Time: 18:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/json;charset=UTF-8" language="java" %>
<%

    for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
        String n = e.nextElement();
        System.out.println(n + ":" + request.getParameter(n));
    }

%>
{"a":"sccecewc", "q":"polymer", "key": "ohdoedoed", "type": "uhuc"}