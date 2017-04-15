package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 12/2/17.
 */
@Entity
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.nombre from Albergue x order by x.nombre")
public class Albergue {

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
