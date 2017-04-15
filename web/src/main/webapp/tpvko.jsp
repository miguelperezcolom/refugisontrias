<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<html>
<head>
    <style>
        .container {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translateX(-50%) translateY(-50%);
        }
        .aviso {
            text-align: center;
            font-family: sans-serif;
            font-size: 30px;
        }
    </style>
</head>
<body>
<div class="container aviso">
    <img src="images/ko.png">
    <p>We are sorry, but something went wrong. <%=(request.getParameter("msg") != null)?request.getParameter("msg"):""%></p>
</div>
</body>
</html>