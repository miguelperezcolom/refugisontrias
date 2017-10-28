package io.mateu.refugisontrias.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by miguel on 25/5/17.
 */
@Converter(autoApply = true)
public class TipoDocumentoConverter implements AttributeConverter<TipoDocumento, String> {
    @Override
    public String convertToDatabaseColumn(TipoDocumento tipoDocumento) {
        switch (tipoDocumento) {
            case DNI:
                return "D";
            case PASAPORTE:
                return "P";
            case PERMISO_CONDUCIR:
                return "C";
            default:
                throw new IllegalArgumentException("Unknown " + tipoDocumento);
        }
    }

    @Override
    public TipoDocumento convertToEntityAttribute(String s) {
        switch (s) {
            case "D":
                return TipoDocumento.DNI;
            case "P":
                return TipoDocumento.PASAPORTE;
            case "C":
                return TipoDocumento.PERMISO_CONDUCIR;
            default:
                throw new IllegalArgumentException("Unknown " + s);
        }
    }
}
