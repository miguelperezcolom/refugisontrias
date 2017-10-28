package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.QLForCombo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by miguel on 31/5/17.
 */
@Entity
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.nombre from Calendario x order by x.nombre")
public class Calendario {

    @Id
    private String id;

    private String nombre;

    private boolean exportarAqui;

}
