package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.Unmodifiable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by miguel on 25/5/17.
 */
@Entity
@Getter
@Setter
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @Unmodifiable
    @SearchFilter(field = "id")
    @ListColumn(field = "id")
    @Required
    private Reserva reserva;


    @Required
    private String nombre;
    @Required
    private String apellido1;
    private String apellido2;
    @Enumerated(EnumType.STRING)
    @Required
    private TipoDocumento tipoDocumento;
    @Required
    private String numeroDocumento;
    private LocalDate expedicion;
    @Enumerated(EnumType.STRING)
    @Required
    private Sexo sexo;
    @Required
    private LocalDate nacimiento;
    @Enumerated(EnumType.STRING)
    @Required
    private Nacionalidad nacionalidad;


}
