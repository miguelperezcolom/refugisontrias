package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private long id;

    @Embedded
    private Auditoria auditoria;

    @StartsLine
    @ManyToOne
    @Required
    private Albergue albergue;

    private EstadoReserva estado;

    @StartsLine
    private LocalDate entrada;
    private LocalDate salida;
    @Output
    private int noches;

    @StartsLine
    private String nombre;
    private String apellidos;
    private String dni;
    private String pais;
    private String comentarios;
    private String telefono;
    private String email;

    @StartsLine
    private int camas;
    private int campings;
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
    private double total;
    @Output
    private double pagado;

    @OneToMany(mappedBy = "reserva")
    @Ignored
    private List<Pago> pagos = new ArrayList<>();


    @Override
    public String toString() {
        return "" + getApellidos() + ", " + getNombre() + " - " + getEntrada() + " - " + getId();
    }

    public void totalizar() {
        int noches = 0;
        if (getEntrada() != null && getSalida() != null) noches = new Long(DAYS.between(entrada, salida)).intValue() - 1;
        setNoches(noches);

        setTotalEstancia(getNoches() * (getCamas() * getPrecioCama() + getCampings() * getPrecioCamping()));
        setTotalExtras(getNoches() * (getCocinas() * getPrecioCocina() + getRopasCama() * getPrecioRopaCama() + getToallas() * getPrecioToalla()));
        setTotal(total = getTotalEstancia() + getTotalExtras());
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

    }



    @Override
    public void beforeSet(boolean isNew) {
    }

    @Override
    public void afterSet(boolean isNew) {
        if (getAlbergue() != null) {
            if (isNew) {
                setPrecioCama(getAlbergue().getPrecioCama());
                setPrecioCamping(getAlbergue().getPrecioCamping());
                setPrecioCocina(getAlbergue().getPrecioCocina());
                setPrecioRopaCama(getAlbergue().getPrecioRopaCama());
                setPrecioToalla(getAlbergue().getPrecioToalla());
            }
            getAlbergue().actualizarDisponibilidad();
        }
        totalizar();
    }

    @Override
    public void beforeDelete() {
        setEstado(EstadoReserva.CANCELLADA);
    }

    @Override
    public void afterDelete() {
        if (getAlbergue() != null) getAlbergue().actualizarDisponibilidad();
    }
}
