package io.mateu.refugisontrias.client;

import io.mateu.refugisontrias.shared.Servicio;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 19/2/17.
 */
public class BookingModule extends AbstractModule {


    @Override
    public String getName() {
        return "Reservas";
    }

    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Reservas") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.Reserva", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Huéspedes") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.Huesped", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Pagos") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.Pago", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Transacciones TPV") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.TPVTransaction", new MDDCallback());
            }
        });


        m.add(new AbstractAction("Importar excel reservas booking.com") {
            @Override
            public void run() {
                MateuUI.openView(new ImportarExcelBookingView());
            }
        });

        m.add(new AbstractAction("Exportar reservas a calendarios") {
            @Override
            public void run() {
                ((ServicioAsync) MateuUI.create(Servicio.class)).exportarACalendario(new Callback<>());
            }
        });

        return m;
    }
}
