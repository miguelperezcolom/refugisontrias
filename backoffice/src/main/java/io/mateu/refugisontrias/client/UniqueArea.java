package io.mateu.refugisontrias.client;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 19/2/17.
 */
public class UniqueArea extends AbstractArea {

    public UniqueArea(String name) {
        super(name);
    }

    @Override
    public List<AbstractModule> getModules() {
        return Arrays.asList(new AdminModule(), new BookingModule());
    }
}
