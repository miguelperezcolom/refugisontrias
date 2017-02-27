package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.QLForCombo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by miguel on 12/2/17.
 */
@Entity
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.nombre from Usuario x order by x.nombre")
public class Usuario {

    @Id
    private String login;

    private String nombre;
    private String email;
    @Ignored
    private String password;
    private EstadoUsuario estado;

}
