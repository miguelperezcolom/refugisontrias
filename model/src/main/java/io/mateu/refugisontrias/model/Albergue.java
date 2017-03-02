package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.QLForCombo;
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

    private String emailHost;
    private String emailUsuario;
    private String emailPassword;


    private double precioCama;
    private double precioCamping;
    private double precioCocina;
    private double precioRopaCama;
    private double precioToalla;

    @OneToMany(mappedBy="albergue", cascade = CascadeType.ALL)
    @MapKey(name="fecha")
    private Map<LocalDate, CupoDia> cupoPorDia = new HashMap<>();

    @OneToMany(mappedBy = "albergue", cascade = CascadeType.PERSIST)
    private List<Reserva> reservas = new ArrayList<>();

    @Override
    public String toString() {
        return getNombre();
    }


    public void actualizarDisponibilidad() {
        for (CupoDia c : getCupoPorDia().values()) {
            c.setDisponibleCamas(c.getCupoCamas());
            c.setDisponibleCamping(c.getCupoCamping());
            c.setReservadoCamas(0);
            c.setReservadoCamping(0);
        }
        for (Reserva r : getReservas()) if (EstadoReserva.OK.equals(r.getEstado())) {
            for (LocalDate d = r.getEntrada(); d.isBefore(r.getSalida()); d = d.plusDays(1)) {
                CupoDia c = getCupoPorDia().get(d);
                if (c == null) {
                    c = new CupoDia();
                    c.setFecha(d);
                    c.setAlbergue(this);
                    getCupoPorDia().put(d, c);
                }
                c.setReservadoCamas(c.getReservadoCamas() + r.getCamas());
                c.setDisponibleCamas(c.getCupoCamas() - c.getReservadoCamas());
                c.setReservadoCamping(c.getReservadoCamping() + r.getCampings());
                c.setDisponibleCamping(c.getCupoCamping() - c.getReservadoCamping());
            }
        }
    }
}
