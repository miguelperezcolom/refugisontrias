<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.stream.Collectors" %><%--
  Created by IntelliJ IDEA.
  User: miguel
  Date: 8/2/17
  Time: 18:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/json;charset=UTF-8" language="java" %><%

    if ("POST".equalsIgnoreCase(request.getMethod()))
    {
        String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println("received:" + test);
    }

    for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
        String n = e.nextElement();
        System.out.println(n + ":" + request.getParameter(n));
    }

%>
{"a":"sccecewc", "q":"polymer", "key": "ohdoedoed", "type": "uhuc"}