package io.mateu.refugisontrias.model;

import com.google.common.base.Strings;
import io.mateu.refugisontrias.model.util.Helper;
import io.mateu.refugisontrias.model.util.JPATransaction;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 12/2/17.
 */
@Entity
@Getter
@Setter
@UseIdToSelect(ql = "select x.id, concat(x.apellidos, ', ', x.nombre, ' - ', x.entrada, ' - ', x.id) from Reserva x where x.id = xxxx")
public class Reserva implements WithTriggers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    private long id;

    @Embedded
    @Ignored
    @SearchFilter(field = "created")
    private Auditoria auditoria;

    @StartsLine
    @ManyToOne
    @Required
    private Albergue albergue;

    @ListColumn
    @CellStyleGenerator(EstadoReservaCellStyleGenerator.class)
    @Required
    private EstadoReserva estado;

    @Ignored
    private String localizador;

    @StartsLine
    private boolean sobreescribiendoImporte;

    private double importeSobreescrito;

    @StartsLine
    @ListColumn(order = true)
    @Required
    @SearchFilter
    private LocalDate entrada;
    @ListColumn
    @Required
    @SearchFilter
    private LocalDate salida;
    @Output
    @ListColumn
    private int noches;

    @StartsLine
    @Required
    @SearchFilter
    private String nombre;
    @Required
    @SearchFilter
    private String apellidos;
    @SearchFilter
    private String dni;
    private String pais;
    private String comentarios;
    @SearchFilter
    private String telefono;
    @SearchFilter
    private String email;

    @StartsLine
    @ListColumn
    @Required
    private int camas;
    @ListColumn
    @Required
    private int campings;
    private int tiendas;
    private int cocinas;
    private int ropasCama;
    private int toallas;

    @StartsLine
    @Output
    private double totalEstancia;
    @Output
    private double precioCama;
    @Output
    private double precioCamping;
    @Output
    private double precioTienda;
    @Output
    private double precioCocina;
    @Output
    private double precioRopaCama;
    @Output
    private double precioToalla;
    @Output
    private double totalExtras;

    @StartsLine
    @Output
    private double saldo;
    @Output
    @ListColumn
    private double total;
    @Output
    @ListColumn
    private double pagado;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL)
    @Ignored
    private List<Pago> pagos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @Ignored
    private List<Log> log;


    @StartsLine
    @Output
    private String refEnBooking;

    @Output
    private String reservadoPorEnBooking;

    @Output
    private String nombreClienteEnBooking;

    @Output
    private String entradaEnBooking;

    @Output
    private String salidaEnBooking;

    @Output
    private String bookingDateEnBooking;

    @Output
    private String estadoEnBooking;

    @Output
    private String habitacionesEnBooking;

    @Output
    private String personasEnBooking;

    @Output
    private String precioEnBooking;

    @Output
    private String porcentajeComisionEnBooking;

    @Output
    private String importeComisionEnBooking;

    @Output
    private String comentariosEnBooking;



    @Override
    public String toString() {
        return "" + getApellidos() + ", " + getNombre() + " - " + getEntrada() + " - " + getLocalizador();
    }

    public double getImportePrepago() {
        return (getTotalEstancia() > 100)?100:getTotalEstancia();
    }

    public void totalizar() {
        int noches = 0;
        if (getEntrada() != null && getSalida() != null) noches = new Long(DAYS.between(entrada, salida)).intValue();
        setNoches(noches);

        setTotalEstancia(getNoches() * (getCamas() * getPrecioCama() + getCampings() * getPrecioCamping()));
        setTotalExtras(getNoches() * (getTiendas() * getPrecioTienda() + getCocinas() * getPrecioCocina() + getRopasCama() * getPrecioRopaCama() + getToallas() * getPrecioToalla()));
        setTotal(getTotalEstancia() + getTotalExtras());

        if (isSobreescribiendoImporte()) setTotal(getImporteSobreescrito());

        double pagado = 0;
        for (Pago p : getPagos()) {
            pagado += p.getImporte();
        }
        setPagado(pagado);

        setSaldo(getTotal() - getPagado());

        if (getPagado() > 0 && EstadoReserva.PENDIENTE.equals(getEstado())) {
            setEstado(EstadoReserva.OK);
            if (getAlbergue() != null) getAlbergue().actualizarDisponibilidad();
        }

        setLocalizador("R" + ((getCamas() > 0)?"H":"K") + getId() + ((getTiendas() > 0)?"T" + getTiendas():"") + ((getCocinas() > 0)?"C" + getCocinas():"") + ((getRopasCama() > 0)?"LL" + getRopasCama():"") + ((getToallas() > 0)?"TV" + getToallas():"") + "PAX" + (getCamas() + getCampings()));
    }



    @Override
    public void beforeSet(EntityManager em, boolean isNew) {
    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) {
        if (getAlbergue() != null) {
            if (isNew) {
                setPrecioCama(getAlbergue().getPrecioCama());
                setPrecioCamping(getAlbergue().getPrecioCamping());
                setPrecioTienda(getAlbergue().getPrecioTienda());
                setPrecioCocina(getAlbergue().getPrecioCocina());
                setPrecioRopaCama(getAlbergue().getPrecioRopaCama());
                setPrecioToalla(getAlbergue().getPrecioToalla());
            }
            getAlbergue().actualizarDisponibilidad();
        }
        totalizar();
        if (getAuditoria() == null) setAuditoria(new Auditoria());
    }

    @Override
    public void beforeDelete(EntityManager em) {
        setEstado(EstadoReserva.CANCELADA);
    }

    @Override
    public void afterDelete(EntityManager em) {
        if (getAlbergue() != null) getAlbergue().actualizarDisponibilidad();
    }


    public void sendPaymentEmail() {

    }

    public void sendDoneEmail() {

    }

    public void sendCancelledEmail() {

    }

    public void sendExpiringEmail() {

    }

    public void sendExpiredEmail() {

    }

    public void sendConfirmedEmail() {

    }



    public static void main(String... args) throws Exception {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {



            }
        });

    }

    public TPVTransaction getTransaccionTPV(EntityManager em) {
        if (getAlbergue().getTpv() != null) {
            TPVTransaction t = new TPVTransaction();
            t.setBooking(this);
            t.setTpv(getAlbergue().getTpv());
            t.setAmount((getTotalEstancia() > 100)?100:getTotalEstancia());
            t.setCurrency("EUR");
            t.setLanguage("es");
            t.setSubject("PAGO INICIAL RESERVA");
            em.persist(t);
            em.flush();
            return t;
        } else return null;
    }


    @Action(name = "Enviar Email")
    public void enviarEmail() throws Exception {
        Email email = new HtmlEmail();
        email.setHostName(getAlbergue().getEmailHost());
        email.setSmtpPort(getAlbergue().getEmailPort());
        email.setAuthenticator(new DefaultAuthenticator(getAlbergue().getEmailUsuario(), getAlbergue().getEmailPassword()));
        //email.setSSLOnConnect(true);
        email.setFrom(getAlbergue().getEmailFrom());
        if (!Strings.isNullOrEmpty(getAlbergue().getEmailCC())) email.getCcAddresses().add(new InternetAddress(getAlbergue().getEmailCC()));

        String asunto = "Booking done";
        String template = getAlbergue().getBookingDoneEmailTemplate();

        switch (getEstado()) {
            case PENDIENTE:
                if (getAuditoria() != null && DAYS.between(getAuditoria().getCreated(), LocalDateTime.now()) > 2) {
                    asunto = "Booking expiring";
                    template = getAlbergue().getBookingExpriringEmailTemplate();
                } else {
                    asunto = "Booking payment pending";
                    template = getAlbergue().getBookingDoneEmailTemplate();
                }
                break;
            case OK:
                asunto = "Booking confirmed";
                template = getAlbergue().getBookingConfirmedEmailTemplate();
                break;
            case CANCELADA:
                asunto = "Booking cancelled";
                template = getAlbergue().getBookingCancelledEmailTemplate();
                break;
            case CADUCADA:
                asunto = "Booking expired";
                template = getAlbergue().getBookingExpiredEmailTemplate();
                break;
        }

        email.setSubject("Booking done");
        email.setMsg(Helper.freemark(getAlbergue().getBookingDoneEmailTemplate(), getData()));
        email.addTo(getEmail());
        email.send();
    }


    private Map<String,Object> getData() {
        Map<String, Object> m = new HashedMap();

        m.put("id", getId());
        m.put("localizador", getLocalizador());
        if (getAuditoria() != null) m.put("creada", getAuditoria().getCreated());
        m.put("albergue", getAlbergue().getNombre());
        m.put("estado", getEstado());
        m.put("entrada", getEntrada());
        m.put("salida", getSalida());
        m.put("noches", getNoches());
        m.put("nombre", getNombre());
        m.put("apellidos", getApellidos());
        m.put("dni", getDni());
        m.put("pais", getPais());
        m.put("comentarios", getComentarios());
        m.put("telefono", getTelefono());
        m.put("email", getEmail());
        m.put("camas", getCamas());
        m.put("campings", getCampings());
        m.put("tiendas", getTiendas());
        m.put("cocinas", getCocinas());
        m.put("ropasCama", getRopasCama());
        m.put("toallas", getToallas());
        m.put("totalEstancia", getTotalEstancia());
        m.put("precioCama", getPrecioCama());
        m.put("precioCamping", getPrecioCamping());
        m.put("precioTienda", getPrecioTienda());
        m.put("precioCocina", getPrecioCocina());
        m.put("precioRopaCama", getPrecioRopaCama());
        m.put("precioToalla", getPrecioToalla());
        m.put("totalExtras", getTotalExtras());
        m.put("total", getTotal());
        m.put("pagado", getPagado());

        return m;
    }


    @Subtitle
    public String getSubtitulo() {
        return "" + (getAuditoria() != null?getAuditoria().toString():"") + " " + getLocalizador();
    }
}
