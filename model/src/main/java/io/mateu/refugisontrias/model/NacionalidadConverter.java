package io.mateu.refugisontrias.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguel on 25/5/17.
 */
@Converter(autoApply = true)
public class NacionalidadConverter implements AttributeConverter<Nacionalidad, String> {

    @Override
    public String convertToDatabaseColumn(Nacionalidad nacionalidad) {
        return "" + nacionalidad;
    }

    @Override
    public Nacionalidad convertToEntityAttribute(String s) {
        try {
            return (Nacionalidad) Class.forName("io.mateu.refugisontrias.model.Nacionalidad." + s).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown " + s);
        }
    }
}
