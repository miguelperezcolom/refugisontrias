package io.mateu.refugisontrias.shared;

import io.mateu.ui.core.communication.Service;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;

/**
 * Created by miguel on 30/5/17.
 */
@Service(url = "servicio")
public interface Servicio {

    public String importarExcelBooking(UserData usuario, Data data) throws Throwable;


    public void exportarACalendario() throws Throwable;

}
