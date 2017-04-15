package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.annotations.UseIdToSelect;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Created by miguel on 3/4/17.
 */
@Entity
@Getter
@Setter
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime cuando = LocalDateTime.now();

    private String texto;

    public Log() {}
    public Log(String texto) {
        this.texto = texto;
    }
}
