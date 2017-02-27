package io.mateu.refugisontrias.client;

import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 19/2/17.
 */
@App
public class RSTApp extends AbstractApplication {
    @Override
    public String getName() {
        return "Refugi Son Trias";
    }

    @Override
    public List<AbstractArea> getAreas() {
        return Arrays.asList(new AdminArea("Admin"), new BookingArea("Booking"));
    }
}
