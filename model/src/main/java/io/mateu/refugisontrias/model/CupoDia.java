package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by miguel on 15/2/17.
 */
@Entity
@Getter
@Setter
public class CupoDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @Unmodifiable
    private Albergue albergue;

    @Unmodifiable
    private LocalDate fecha;

    private int cupoCamas;
    private int cupoCamping;

    @Output
    private int reservadoCamas;
    @Output
    private int reservadoCamping;
    @Output
    private int disponibleCamas;
    @Output
    private int disponibleCamping;


    @Action(name = "Fijar cupo camas")
    public static void setCupoCamas(@Required@Caption("Del") LocalDate del, @Required@Caption("Al") LocalDate al, @Required@Caption("Cupo") int cupo) throws Exception {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Albergue a = em.find(Albergue.class, 1l);
                for (LocalDate i = del; i.isBefore(al) || i.equals(al); i = i.plusDays(1)) {
                    CupoDia c = a.getCupoPorDia().get(i);
                    if (c == null) {
                        a.getCupoPorDia().put(i, c = new CupoDia());
                        c.setAlbergue(a);
                        c.setFecha(i);
                    }
                    c.setCupoCamas(cupo);
                }
            }
        });
    }

}
