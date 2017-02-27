package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.QLForCombo;
import io.mateu.ui.mdd.server.annotations.UseIdToSelect;
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
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Auditoria auditoria;

    private EstadoReserva estado;

    private LocalDate entrada;
    private LocalDate salida;
    @Output
    private int noches;

    private String nombre;
    private String apellidos;
    private String dni;
    private String pais;
    private String comentarios;
    private String telefono;
    private String email;

    private int camas;
    private int campings;
    private int cocinas;
    private int ropasCama;
    private int toallas;

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


    @PreUpdate@PrePersist
    public void preStore() {
        totalEstancia = camas * precioCama + campings * precioCamping;
        totalExtras = cocinas * precioCocina + ropasCama * precioRopaCama + toallas * precioToalla;
        total = totalEstancia + totalExtras;
        double pagado = 0;
        for (Pago p : pagos) {
            pagado += p.getImporte();
        }
        saldo = total - pagado;

        System.out.println("setPagado(" + pagado + ")");


        setPagado(pagado);

        noches = 0;
        if (entrada != null && salida != null) noches = new Long(DAYS.between(entrada, salida)).intValue();

    }


}
