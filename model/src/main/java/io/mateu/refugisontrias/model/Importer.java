package io.mateu.refugisontrias.model;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import io.mateu.refugisontrias.model.googlecalendar.MyAuthorizationCodeApp;
import io.mateu.refugisontrias.model.googlecalendar.Receiver;
import io.mateu.refugisontrias.model.googlecalendar.Tester;
import io.mateu.refugisontrias.model.util.JPATransaction;
import io.mateu.refugisontrias.servlet.OAuthCallbackServlet;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.util.Helper;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 30/5/17.
 */
public class Importer {

    /** Application name. */
    @Ignored
    private static final String APPLICATION_NAME =
            "Google Calendar API Java Quickstart";

    /** Directory to store user credentials for this application. */
    @Ignored
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    @Ignored
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    @Ignored
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    @Ignored
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    @Ignored
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }



    public static void exportarACalendario() throws Throwable {

        final Credential[] credential = new Credential[1];

        if (AbstractApplication.PORT_JAVAFX.equals(MateuUI.getApp().getPort())) {

            // Load client secrets.
            InputStream in = Tester.class.getResourceAsStream("/client_secret_desktop.json");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                            .setDataStoreFactory(DATA_STORE_FACTORY)
                            .setAccessType("offline")
                            .build();

            credential[0] = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        } else if (AbstractApplication.PORT_VAADIN.equals(MateuUI.getApp().getPort())) {

            Object x = new Object();
            URL[] u = new URL[1];

            // Load client secrets.
            InputStream in = Tester.class.getResourceAsStream("/client_secret_web.json");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                            .setDataStoreFactory(DATA_STORE_FACTORY)
                            .setAccessType("offline")
                            .build();

            AuthorizationCodeInstalledApp app = new MyAuthorizationCodeApp(flow, OAuthCallbackServlet.receiver = new Receiver(MateuUI.getApp().getBaseUrl() + "oauth2callback")) {
                @Override
                public void setAuthorizationUrl(AuthorizationCodeRequestUrl authorizationUrl) {
                    System.out.println("setAuthorizationUrl(" + ((authorizationUrl == null)?"null":authorizationUrl.toURL()) + ")");
                    u[0] = authorizationUrl.toURL();
                    synchronized (x) {
                        x.notify();
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        credential[0] = app.authorize("user");
                        synchronized (x) {
                            x.notify();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            synchronized (x) {
                x.wait();
            }

        }

        com.google.api.services.calendar.Calendar service = getCalendarService(credential[0]);

        io.mateu.refugisontrias.model.util.Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                List<Calendario> calendarios = em.createQuery("select x from Calendario x where x.exportarAqui = true").getResultList();

                List<Reserva> reservas = em.createQuery("select x from Reserva x where x.salida >= :hoy").setParameter("hoy", LocalDate.now()).getResultList();

                for (Calendario cal : calendarios) {
                    for (Reserva r : reservas) {

                        {
                            boolean nuevo = false;
                            Event event = null;
                            try {
                                event = service.events().get(cal.getId(), "reserva" + r.getId()).execute();

                                if ("cancelled".equalsIgnoreCase(event.getStatus())) {
                                    event.setStatus("confirmed");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (event == null) {
                                event = new Event();
                                event.setId("reserva" + r.getId());
                                nuevo = true;
                            }


                            if (EstadoReserva.CADUCADA.equals(r.getEstado())) {
                                event.setColorId("5");
                            } else if (EstadoReserva.CANCELADA.equals(r.getEstado())) {
                                event.setColorId("4");
                            } else if (EstadoReserva.OK.equals(r.getEstado())) {
                                event.setColorId("10");
                            } else if (EstadoReserva.PENDIENTE.equals(r.getEstado())) {
                                event.setColorId("1");
                            } else {
                                event.setColorId("3");
                            }


                            event.setSummary(r.getLocalizador() + " " + r.getNombre() + " " + r.getApellidos()+ " x " + (r.getCamas() + r.getCampings()))
                                    //.setLocation("800 Howard St., San Francisco, CA 94103")
                                    .setDescription("Reserva exportada desde el sistema");

                            DateTime startDateTime = new DateTime("" + r.getEntrada().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T12:00:00-01:00");
                            EventDateTime start = new EventDateTime()
                                    .setDateTime(startDateTime)
                                    //.setTimeZone("America/Los_Angeles")
                                    ;
                            event.setStart(start);

                            DateTime endDateTime = new DateTime("" + r.getSalida().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T12:00:00-01:00");
                            EventDateTime end = new EventDateTime()
                                    .setDateTime(endDateTime)
                                    //.setTimeZone("America/Los_Angeles")
                                    ;
                            event.setEnd(end);

                            /*
                            String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
                            event.setRecurrence(Arrays.asList(recurrence));

                            EventAttendee[] attendees = new EventAttendee[] {
                                    new EventAttendee().setEmail("lpage@example.com"),
                                    new EventAttendee().setEmail("sbrin@example.com"),
                            };
                            event.setAttendees(Arrays.asList(attendees));

                            EventReminder[] reminderOverrides = new EventReminder[] {
                                    new EventReminder().setMethod("email").setMinutes(24 * 60),
                                    new EventReminder().setMethod("popup").setMinutes(10),
                            };
                            Event.Reminders reminders = new Event.Reminders()
                                    .setUseDefault(false)
                                    .setOverrides(Arrays.asList(reminderOverrides));
                            event.setReminders(reminders);
*/

                            String calendarId = cal.getId();
                            if (nuevo) {
                                event = service.events().insert(calendarId, event).execute();
                                System.out.printf("Event created: %s\n", event.getHtmlLink());
                            }
                            else {
                                event = service.events().update(calendarId, event.getId(), event).execute();
                                System.out.printf("Event updated: %s\n", event.getHtmlLink());
                            }
                        }

                    }
                }

            }
        });

    }

    private static com.google.api.services.calendar.Calendar getCalendarService(Credential authorizedCredential) throws IOException {
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, authorizedCredential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public static void importBookingExcel(EntityManager em, UserData user, Object[][] l, PrintWriter pw, Albergue albergue) throws Throwable {

        Usuario u = em.find(Usuario.class, user.getLogin());


        int colref = -1;
        int colreservadopor = -1;
        int colnombrecliente = -1;
        int colentrada = -1;
        int colsalida = -1;
        int colfechareserva = -1;
        int colestado = -1;
        int colhabitaciones = -1;
        int colpersonas = -1;
        int colprecio = -1;
        int colporcentajecomision = -1;
        int colimportecomision = -1;
        int colcomentarios = -1;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdh = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (int fila = 0; fila < l.length; fila++) {
            int nofila = fila + 1;
            if (colref < 0) {
                for (int col = 0; col < l[fila].length; col++) {
                    String s = ("" + l[fila][col]).trim();
                    if ("número de reserva".equalsIgnoreCase("" + s)) colref = col;
                    if ("reservat per".equalsIgnoreCase("" + s)) colreservadopor = col;
                    if ("nom del client".equalsIgnoreCase("" + s)) colnombrecliente = col;
                    if ("entrada".equalsIgnoreCase("" + s)) colentrada = col;
                    if ("sortida".equalsIgnoreCase("" + s)) colsalida = col;
                    if ("booking date".equalsIgnoreCase("" + s)) colfechareserva = col;
                    if ("estat".equalsIgnoreCase("" + s)) colestado = col;
                    if ("habitacions".equalsIgnoreCase("" + s)) colhabitaciones = col;
                    if ("persones".equalsIgnoreCase("" + s)) colpersonas = col;
                    if ("preu".equalsIgnoreCase("" + s)) colprecio = col;
                    if ("commission %".equalsIgnoreCase("" + s)) colporcentajecomision = col;
                    if ("Commission amount".equalsIgnoreCase("" + s)) colimportecomision = col;
                    if ("comentaris".equalsIgnoreCase("" + s)) colcomentarios = col;
                }
            } else {
                try {

                    if (l[fila].length < Helper.max(colref, colreservadopor, colnombrecliente, colentrada, colsalida, colfechareserva, colestado, colhabitaciones, colpersonas, colprecio, colporcentajecomision, colimportecomision, colcomentarios)) throw new Exception("Missing columns");

                    String ref = null;
                    if (l[fila][colref] != null) {
                        if (l[fila][colref] instanceof Double) ref = "" + ((Double)l[fila][colref]).longValue();
                        else ref += "" + l[fila][colref];
                    }
                    Date entrada = null;
                    try {
                        entrada = (Date) l[fila][colentrada];
                    } catch (Exception e) {
                        entrada = sdf.parse((String) l[fila][colentrada]);
                    }
                    Date salida = null;
                    try {
                        salida = (Date) l[fila][colsalida];
                    } catch (Exception e) {
                        salida = sdf.parse((String) l[fila][colsalida]);
                    }

                    Date fechareserva = null;
                    try {
                        fechareserva = (Date) l[fila][colfechareserva];
                    } catch (Exception e) {
                        fechareserva = sdh.parse((String) l[fila][colfechareserva]);
                    }

                    String titular = (l[fila][colnombrecliente] != null)?"" + l[fila][colnombrecliente]:null;

                    int pax = getInt(l[fila][colpersonas]);
                    int habs = getInt(l[fila][colpersonas]);

                    String estado = (l[fila][colestado] != null)?"" + l[fila][colestado]:null;


                    if (ref == null) pw.println("<span style='color: red;'>line " + nofila + ": missing ref</span>");
                    else if (entrada == null) pw.println("<span style='color: red;'>line " + nofila + ": missing checkin date</span>");
                    else if (salida == null) pw.println("<span style='color: red;'>line " + nofila + ": missing checkout date</span>");
                    else if (titular == null) pw.println("<span style='color: red;'>line " + nofila + ": missing lead name</span>");
                    else if (fechareserva == null) pw.println("<span style='color: red;'>line " + nofila + ": missing booking date</span>");
                    else if (pax == 0) pw.println("<span style='color: red;'>line " + nofila + ": missing number of pax</span>");
                    else if (estado == null) pw.println("<span style='color: red;'>line " + nofila + ": missing status</span>");
                    else {

                        Reserva r = null;
                        try {
                            r = (Reserva) em.createQuery("select x from Reserva x where x.refEnBooking = :ref").setParameter("ref", ref).getResultList().get(0);
                        } catch (Exception e) {

                        }

                        boolean nueva = false;
                        if (r == null) {
                            r = new Reserva();
                            r.setAuditoria(new Auditoria());
                            r.getAuditoria().touch(u);
                            r.setAlbergue(albergue);
                            albergue.getReservas().add(r);
                            em.persist(r);
                            pw.println("line " + nofila + ": reserva creada");
                            nueva = true;
                        }

                        r.getAuditoria().touch(u);

                        r.setRefEnBooking(ref);
                        if (l[fila][colreservadopor] != null) r.setReservadoPorEnBooking("" + l[fila][colreservadopor]);
                        if (l[fila][colnombrecliente] != null) r.setNombreClienteEnBooking("" + l[fila][colnombrecliente]);
                        if (l[fila][colentrada] != null) r.setEntradaEnBooking("" + l[fila][colentrada]);
                        if (l[fila][colsalida] != null) r.setSalidaEnBooking("" + l[fila][colsalida]);
                        if (l[fila][colfechareserva] != null) r.setBookingDateEnBooking("" + l[fila][colfechareserva]);
                        if (l[fila][colestado] != null) r.setEstadoEnBooking("" + l[fila][colestado]);
                        if (l[fila][colhabitaciones] != null) r.setHabitacionesEnBooking("" + l[fila][colhabitaciones]);
                        if (l[fila][colpersonas] != null) r.setPersonasEnBooking("" + l[fila][colpersonas]);
                        if (l[fila][colprecio] != null) r.setPrecioEnBooking("" + l[fila][colprecio]);
                        if (l[fila][colporcentajecomision] != null) r.setPorcentajeComisionEnBooking("" + l[fila][colporcentajecomision]);
                        if (l[fila][colimportecomision] != null) r.setImporteComisionEnBooking("" + l[fila][colimportecomision]);
                        if (l[fila][colcomentarios] != null) r.setComentariosEnBooking("" + l[fila][colcomentarios]);

                        r.setEntrada(Helper.toLocalDate(entrada));
                        r.setSalida(Helper.toLocalDate(salida));
                        r.setNombre(titular);
                        r.setApellidos(titular);
                        r.setComentarios(r.getComentariosEnBooking());
                        r.setCamas(pax);
                        EstadoReserva s = EstadoReserva.PENDIENTE;
                        if ("cancelled_by_guest".equalsIgnoreCase(estado) || "cancelled_by_hotel".equalsIgnoreCase(estado) || "no_show".equalsIgnoreCase(estado)) s = EstadoReserva.CANCELADA;
                        if ("ok".equalsIgnoreCase(estado)) s = EstadoReserva.OK;
                        r.setEstado(s);
                        r.afterSet(em, nueva);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    pw.println("<span style='color: red;'>line " + nofila + ": ERROR " + e.getClass().getName() + "(" + e.getMessage()+ ")</span>");
                }
                pw.println("<br/>");
            }
        }
        if (colref < 0) throw new Throwable("Missing ref col");
        pw.println("<br/>");
        pw.println("<br/>");
        pw.println("****END OF FILE****");
        pw.println("<br/>");
        pw.println("<br/>");
    }

    private static int getInt(Object o) {
        int v = 0;
        try {
            v = (Integer) o;
        } catch (Exception e) {
            try {
                v = Integer.parseInt("" + o);
            } catch (Exception e1) {
                try {
                    v = new Double("" + o).intValue();
                } catch (Exception e2) {

                }
            }
        }
        return v;
    }
}
