package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.Unmodifiable;
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
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Auditoria auditoria;


    @ManyToOne
    @Unmodifiable
    private Reserva reserva;

    private double importe;


    //@PostUpdate
    private void post() throws Exception {
        if (reserva != null) {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager entityManager) throws Exception {
                    entityManager.find(Reserva.class, reserva.getId()).preStore();
                }
            });
        }
    }

}
