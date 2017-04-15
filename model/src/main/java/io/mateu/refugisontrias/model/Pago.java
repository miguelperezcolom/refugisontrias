package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.*;
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
@Indelible
public class Pago implements WithTriggers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @ListColumn(field = "created")
    @SearchFilter(field = "created")
    private Auditoria auditoria;


    @ManyToOne
    @Unmodifiable
    @SearchFilter(field = "id")
    @ListColumn(field = "id")
    private Reserva reserva;

    @Unmodifiable
    @SearchFilter
    @ListColumn
    private TipoPago tipo;


    @Unmodifiable
    @ListColumn
    private double importe;

    @TextArea
    @ListColumn
    @Unmodifiable
    private String concepto;


    @Override
    public void beforeSet(EntityManager em, boolean isNew) {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) {
        if (getReserva() != null) getReserva().totalizar();
    }

    @Override
    public void beforeDelete(EntityManager em) {

    }

    @Override
    public void afterDelete(EntityManager em) {
        if (getReserva() != null) getReserva().totalizar();
    }
}
