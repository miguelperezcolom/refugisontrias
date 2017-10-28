package io.mateu.refugisontrias.server;

import io.mateu.refugisontrias.model.Albergue;
import io.mateu.refugisontrias.model.Importer;
import io.mateu.refugisontrias.shared.Servicio;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by miguel on 30/5/17.
 */
public class ServicioImpl implements Servicio {
    @Override
    public String importarExcelBooking(UserData usuario, Data data) throws Throwable {
        System.out.println("" + data);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (data.isEmpty("file")) throw new Throwable("You must first upload an excel file");
        else {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {
                    Object[][] l = Helper.parseExcel(new File(((FileLocator) data.get("file")).getTmpPath()))[0];
                    Importer.importBookingExcel(em, usuario, l, pw, em.find(Albergue.class, 1l));
                }
            });
        }
        return sw.toString();
    }

    @Override
    public void exportarACalendario() throws Throwable {
        Importer.exportarACalendario();
    }
}
