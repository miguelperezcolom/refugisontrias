package io.mateu.refugisontrias.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 19/2/17.
 */
public class AdminModule extends AbstractModule {
    @Override
    public String getName() {
        return "Admin";
    }

    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        if (MateuUI.hasPermission(1)) {

            m.add(new AbstractAction("Usuarios") {
                @Override
                public void run() {
                    ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.Usuario", new MDDCallback());
                }
            });

            m.add(new AbstractAction("Configuración") {
                @Override
                public void run() {
                    ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.Albergue", new MDDCallback());
                }
            });

            m.add(new AbstractAction("Monedas") {
                @Override
                public void run() {
                    ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.Moneda", new MDDCallback());
                }
            });

            m.add(new AbstractAction("TPVs") {
                @Override
                public void run() {
                    ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.TPV", new MDDCallback());
                }
            });

            m.add(new AbstractAction("Calendarios") {
                @Override
                public void run() {
                    ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.Calendario", new MDDCallback());
                }
            });

        }

        m.add(new AbstractAction("Cupo") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.refugisontrias.model.CupoDia", new MDDCallback());
            }
        });

        return m;
    }
}
