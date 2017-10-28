package io.mateu.refugisontrias.client;

import io.mateu.refugisontrias.shared.Servicio;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.FileField;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;

import java.util.List;

/**
 * Created by miguel on 30/5/17.
 */
public class ImportarExcelBookingView  extends AbstractView {

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> l = super.createActions();

        l.add(new AbstractAction("Procesar fichero excel") {
            @Override
            public void run() {
                ((ServicioAsync) MateuUI.create(Servicio.class)).importarExcelBooking(MateuUI.getApp().getUserData(), getForm().getData(), new Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        MateuUI.notifyInfo(result);
                    }
                });
            }
        });

        return l;
    }

    @Override
    public String getTitle() {
        return "Importar excel reservas booking.com";
    }

    @Override
    public void build() {

        add(new FileField("file", "Excel file"));

    }

}