package io.mateu.refugisontrias.model;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
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
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import io.mateu.refugisontrias.App;
import io.mateu.refugisontrias.model.googlecalendar.MyAuthorizationCodeApp;
import io.mateu.refugisontrias.model.googlecalendar.Receiver;
import io.mateu.refugisontrias.model.googlecalendar.Tester;
import io.mateu.refugisontrias.servlet.OAuthCallbackServlet;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;


/**
 * Created by miguel on 12/2/17.
 */
@Entity
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.nombre from Albergue x order by x.nombre")
public class Albergue {

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ListColumn("Nombre")
    private String nombre;
    private String url;

    @StartsLine
    private String emailHost;
    private int emailPort;
    private String emailUsuario;
    private String emailPassword;
    private String emailFrom;
    private String emailCC;

    @StartsLine
    @ManyToOne
    private TPV tpv;

    @StartsLine
    private double precioCama;
    private double precioCamping;
    private double precioTienda;
    private double precioCocina;
    private double precioRopaCama;
    private double precioToalla;

    @StartsLine
    @TextArea
    private String paymentEmailTemplate;
    @TextArea
    private String bookingDoneEmailTemplate;
    @TextArea
    private String bookingCancelledEmailTemplate;
    @TextArea
    private String bookingExpriringEmailTemplate;
    @TextArea
    private String bookingExpiredEmailTemplate;
    @TextArea
    private String bookingConfirmedEmailTemplate;



    @OneToMany(mappedBy="albergue", cascade = CascadeType.ALL)
    @MapKey(name="fecha")
    private Map<LocalDate, CupoDia> cupoPorDia = new HashMap<>();

    @OneToMany(mappedBy = "albergue", cascade = CascadeType.PERSIST)
    @Ignored
    private List<Reserva> reservas = new ArrayList<>();

    @Override
    public String toString() {
        return getNombre();
    }


    @Action(name = "Autorizar Google Calendar")
    public URL autorizarGoogleCalendar() throws Throwable {

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

            Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

            importarCalendarios(credential);

//        } else if ("vaadin".equals(MateuUI.getApp().getPort())) {
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
                        Credential credential = app.authorize("user");
                        synchronized (x) {
                            x.notify();
                        }

                        importarCalendarios(credential);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            synchronized (x) {
                x.wait();
            }

            System.out.println("returns " + u[0]);

            //return app.getFlow().newAuthorizationUrl().setRedirectUri("http://viajesibiza.mateu.io/oauth2callback").toURL();
            //return app.getFlow().newAuthorizationUrl().setRedirectUri(MateuUI.getApp().getBaseUrl() + "oauth2callback").toURL();
            return u[0];
        }

        return null;
    }

    private void importarCalendarios(Credential credential) throws Exception {

        com.google.api.services.calendar.Calendar service = getCalendarService(credential);

        io.mateu.refugisontrias.model.util.Helper.transact(new io.mateu.refugisontrias.model.util.JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                // List the next 10 events from the primary calendar.
                DateTime now = new DateTime(System.currentTimeMillis());
                CalendarList l = service.calendarList().list().execute();
                for (CalendarListEntry k : l.getItems()) {
                    System.out.println("**********");
                    System.out.println(k.getId());
                    System.out.println(k.getEtag());
                    System.out.println(k.getDescription());
                    System.out.println(k.getForegroundColor());
                    System.out.println(k.getBackgroundColor());
                    System.out.println(k.getColorId());
                    System.out.println(k.getSummary());
                    System.out.println("**********");

                    Calendario cal = em.find(Calendario.class, k.getId());
                    if (cal == null) {
                        cal = new Calendario();
                        cal.setId(k.getId());
                        cal.setNombre(k.getSummary());
                        em.persist(cal);
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

    public void actualizarDisponibilidad() {
        for (CupoDia c : getCupoPorDia().values()) {
            c.setReservadoCamas(0);
            c.setReservadoCamping(0);
            c.totalizar();
        }
        for (Reserva r : getReservas()) if (EstadoReserva.PENDIENTE.equals(r.getEstado()) || EstadoReserva.OK.equals(r.getEstado())) {
            for (LocalDate d = r.getEntrada(); d.isBefore(r.getSalida()); d = d.plusDays(1)) {
                CupoDia c = getCupoPorDia().get(d);
                if (c == null) {
                    c = new CupoDia();
                    c.setFecha(d);
                    c.setAlbergue(this);
                    getCupoPorDia().put(d, c);
                }
                c.setReservadoCamas(c.getReservadoCamas() + r.getCamas());
                c.setReservadoCamping(c.getReservadoCamping() + r.getCampings());
                c.totalizar();
            }
        }
    }
}
