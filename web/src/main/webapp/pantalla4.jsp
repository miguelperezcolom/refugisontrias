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
            <div main-title style="text-align: center;  ">Book</div>
        </app-toolbar>
    </app-header>
    <div>
        <div style="tdisplay: table; margin: 0 auto;  width: 300px; ">


        <form is="iron-form" method="get" action="target.jsp" id="basic">
            <paper-input name="name" label="Name" requiredx></paper-input>
            <paper-input name="email" label="Email" requiredx></paper-input>
            <paper-input name="phone" label="Phone" requiredx></paper-input>
            <paper-dropdown-menu name="beds" label="Beds" requiredx>
                <paper-listbox class="dropdown-content">
                    <% for (int i = 1; i <= 16; i++) {%>
                    <paper-item><%=i%>-<%=i * 14%>&euro;</paper-item>
                    <% } %>
                </paper-listbox>
            </paper-dropdown-menu>
            <vaadin-date-picker name="checkin" label="Checkin"  min="2017-02-01" max="2017-12-31" initial-position="2017-02-01" requiredx>
            </vaadin-date-picker>
            <vaadin-date-picker name="checkout" label="Checkout"  min="2017-02-01" max="2017-12-31" initial-position="2017-02-01" requiredx>
            </vaadin-date-picker>
            <paper-dropdown-menu name="bedsheets" label="Bed sheets">
                <paper-listbox class="dropdown-content">
                    <paper-item>Bring sleeping bag</paper-item>
                    <paper-listbox class="dropdown-content">
                        <% for (int i = 1; i <= 16; i++) {%>
                        <paper-item><%=i%>-<%=i * 3%>&euro;</paper-item>
                        <% } %>
                    </paper-listbox>
                </paper-listbox>
            </paper-dropdown-menu>
            <br/>
            <paper-dropdown-menu name="usekitchen" label="Use kitchen">
                <paper-listbox class="dropdown-content">
                    <paper-item>No use kitchen</paper-item>
                    <paper-listbox class="dropdown-content">
                        <% for (int i = 1; i <= 16; i++) {%>
                        <paper-item><%=i%>-<%=i * 2%>&euro;</paper-item>
                        <% } %>
                    </paper-listbox>
                </paper-listbox>
            </paper-dropdown-menu>
            <br/>
            <paper-textarea label="Estimated arrival time / others" name="comments"></paper-textarea>


            <paper-button raised onclick="_submit(event)">Submit</paper-button>
            <paper-button raised onclick="_reset(event)">Reset</paper-button>
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
    function _submit(event) {
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

</script>
</body>
</html>