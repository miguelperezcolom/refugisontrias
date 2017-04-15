package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.Required;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by miguel on 31/3/17.
 */
@Entity
@Getter
@Setter
public class Moneda {

    @Id
    @Required
    private String isoCode;

    private String iso4217Code;

    @Required
    private String nombre;

    private int decimales;
}
