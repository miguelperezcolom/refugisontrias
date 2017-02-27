package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.QLForCombo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
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


    @Override
    public String toString() {
        return getNombre();
    }
}
