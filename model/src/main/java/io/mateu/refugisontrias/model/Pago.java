package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.Unmodifiable;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by miguel on 12/2/17.
 */
@Entity
@Getter
@Setter
public class Pago implements WithTriggers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Auditoria auditoria;


    @ManyToOne
    @Unmodifiable
    private Reserva reserva;

    @Unmodifiable
    private double importe;


    @Override
    public void beforeSet(boolean isNew) {

    }

    @Override
    public void afterSet(boolean isNew) {
        if (getReserva() != null) getReserva().totalizar();
    }

    @Override
    public void beforeDelete() {

    }

    @Override
    public void afterDelete() {
        if (getReserva() != null) getReserva().totalizar();
    }
}
