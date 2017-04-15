package io.mateu.refugisontrias.model;

import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 13/4/17.
 */
public class EstadoReservaCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        if (EstadoReserva.PENDIENTE.equals(o)) return "pending";
        else if (EstadoReserva.OK.equals(o)) return "done";
        else if (EstadoReserva.CANCELADA.equals(o)) return "cancelled";
        else if (EstadoReserva.CADUCADA.equals(o)) return "expired";
        return null;
    }
}
