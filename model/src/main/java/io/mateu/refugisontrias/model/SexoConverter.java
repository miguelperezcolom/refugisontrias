package io.mateu.refugisontrias.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by miguel on 25/5/17.
 */
@Converter(autoApply = true)
public class SexoConverter implements AttributeConverter<Sexo, String> {
    @Override
    public String convertToDatabaseColumn(Sexo sexo) {
        switch (sexo) {
            case Masculino:
                return "M";
            case Femenino:
                return "F";
            default:
                throw new IllegalArgumentException("Unknown " + sexo);
        }
    }

    @Override
    public Sexo convertToEntityAttribute(String s) {
        switch (s) {
            case "M":
                return Sexo.Masculino;
            case "F":
                return Sexo.Femenino;
            default:
                throw new IllegalArgumentException("Unknown " + s);
        }
    }
}
