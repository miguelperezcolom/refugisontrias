<%@ page import="java.util.Enumeration" %>
<html>
<head>
    <!-- 1. Load webcomponents-lite.min.js for polyfill support. -->
    <script src="bower_components/webcomponentsjs/webcomponents-lite.min.js">
    </script>

    <!-- 2. Use an HTML Import to bring in some elements. -->
    <link rel="import" href="bower_components/paper-input/paper-input.html">
    <link rel="import" href="bower_components/polymer/polymer.html">
    <link rel="import" href="bower_components/iron-form/iron-form.html">
    <link rel="import" href="bower_components/paper-checkbox/paper-checkbox.html">
    <link rel="import" href="bower_components/paper-button/paper-button.html">
    <link rel="import" href="bower_components/paper-input/paper-input.html">
    <link rel="import" href="bower_components/paper-dropdown-menu/paper-dropdown-menu.html">
    <link rel="import" href="bower_components/paper-listbox/paper-listbox.html">
    <link rel="import" href="bower_components/paper-item/paper-item.html">
    <link rel="import" href="bower_components/paper-input/paper-textarea.html">

    <link rel="import" href="bower_components/app-layout/app-header-layout/app-header-layout.html">
    <link rel="import" href="bower_components/app-layout/app-header/app-header.html">
    <link rel="import" href="bower_components/app-layout/app-toolbar/app-toolbar.html">
    <link rel="import" href="bower_components/app-layout/app-scroll-effects/effects/waterfall.html">

    <link rel="import" href="bower_components/vaadin-date-picker/vaadin-date-picker.html">
    <link rel="import" href="bower_components/vaadin-grid/vaadin-grid.html">

    <link rel="import" href="bower_components/paper-toast/paper-toast.html">

    <link rel="import" href="bower_components/iron-icons/iron-icons.html">
    <link rel="import" href="bower_components/paper-alert-dialog/paper-alert-dialog.html">
    <link rel="import" href="bower_components/paper-alert-dialog/paper-alert-dialog-icon-header.html">

    <link rel="import" href="bower_components/paper-dialog/paper-dialog.html">

    <style>
        body {
            margin: 0;
            font-family: sans-serif;
            background-color: white;
        }

        app-header {
            background-color: #00897B;
            color: #fff;
        }
        app-header paper-icon-button {
            --paper-icon-button-ink-color: white;
        }
        
        .donex {
            color: green;
            width: 70px;
            height: 70px;
        }

        .donexx {
            text-align: center;
        }
    </style>

</head>
<body>

<app-header-layout has-scrolling-region >
    <app-header fixed condenses effects="waterfall">
        <app-toolbar>
            <div main-title style="text-align: center;  ">Not enough rooms. Change check-in day</div>
        </app-toolbar>
    </app-header>
    <div>
        <div style="tdisplay: table; margin: 0 auto;  width: 300px; ">


        <form is="iron-form" method="get" action="target.jsp" id="basic">

            <%

                for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
                    String n = e.nextElement();
                    %><input type="hidden" name="<%=n%>" value="<%=request.getParameter(n)%>"/><%
                }

            %>

            <input type="hidden" name="newcheckin" value=""/>

            <vaadin-grid selection-mode="single" id="g">
                <table>
                    <!-- Define the columns and their mapping to data properties. -->
                    <col name="day">
                    <col name="availability">
                    <col name="enough">

                    <!-- Define the column headings. -->
                    <thead>
                    <tr>
                        <th>Day</th>
                        <th>Available</th>
                        <th></th>
                    </tr>
                    </thead>
                </table>
            </vaadin-grid>
            <div class="output"></div>
        </form>

        <style is="custom-style">
            #toast2 {
                --paper-toast-background-color: red;
                --paper-toast-color: white;
            }
        </style>

        <paper-toast id="toast2" class="fit-bottom" text="This toast is red and fits bottom!"></paper-toast>


        <paper-dialog id="d">

            <div class="donexx"><iron-icon icon="icons:done" class="donex"></iron-icon></div>

            <h1>Thanks!</h1>

            <h2>Your booking has been confirmed</h2>
            <paper-dialog-scrollable>
                You will receive an email with your booking data
            </paper-dialog-scrollable>
            <div class="buttons">
                <!--<paper-button dialog-dismiss>Cancel</paper-button>-->
                <paper-button dialog-confirm autofocus>Go to home</paper-button>
            </div>
        </paper-dialog>

        <paper-alert-dialog id="okd" confirm-button="Got it">
            <paper-alert-dialog-icon-header icon="icons:done">
                Well done!
            </paper-alert-dialog-icon-header>
            Excellent, you can move on.
        </paper-alert-dialog>
    </div>
    </div>
</app-header-layout>



<!--
<iron-request id="xhr"></iron-request>
-->
<script>

    g.addEventListener('selected-items-changed', function() {
        console.log('Selected: ' + g.selection.selected());
        if (g.selection.size > 0) {
            basic.newcheckin.value = g.items[g.selection.selected()].day;
            console.log('Selected: ' + g.items[g.selection.selected()].day);
            console.log('Selected: ' + g.items[g.selection.selected()].availability);
            Polymer.dom(event).localTarget.parentElement.submit();
        }
    });

    function _submit(event) {
        console.log(g.selection.selected());
        Polymer.dom(event).localTarget.parentElement.submit();
    }
    function _reset(event) {
        var form = Polymer.dom(event).localTarget.parentElement
        form.reset();
        form.querySelector('.output').innerHTML = '';
    }
    /*
    basic.addEventListener('iron-form-submit', function(event) {
        this.querySelector('.output').innerHTML = JSON.stringify(event.detail);
    });
    */
    basic.addEventListener('iron-form-response', function(event) {
        //toast2.text = JSON.stringify(event.detail.response);
        //toast2.open();
        d.open();
        //okd.open();
        //this.querySelector('.output').innerHTML = JSON.stringify(event.detail.response);
    });
    okd.addEventListener('confirm', function(event) {
        alert("Confirmed!");
    });
    okd.addEventListener('dismiss', function(event) {
        alert("Dismissed!");
    });


    // The Web Components polyfill introduces a custom event we can
    // use to determine when the custom elements are ready to be used.
    document.addEventListener("WebComponentsReady", function () {

        // Reference to the grid element.
        var grid = document.querySelector("vaadin-grid");

        // Add some example data as an array.
        grid.items = [
            { "day": "2017-03-17", "availability": 5, "enough": false},
            { "day": "2017-03-17", "availability": 3, "enough": false},
            { "day": "2017-03-17", "availability": 4, "enough": false},
            { "day": "2017-03-17", "availability": 10, "enough": true},
            { "day": "2017-03-17", "availability": 16, "enough": true},
            { "day": "2017-03-17", "availability": 16, "enough": true},
            { "day": "2017-03-17", "availability": 10, "enough": true},
            { "day": "2017-03-17", "availability": 5, "enough": false}
        ];

        var progressRenderer = function(cell) {
            cell.element.innerHTML = (cell.data)?'<iron-icon icon="icons:done"></iron-icon>':'';
        };

        grid.columns[2].renderer = progressRenderer;

    });


</script>
</body>
</html>