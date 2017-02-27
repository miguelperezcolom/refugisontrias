package io.mateu.refugisontrias.client;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 19/2/17.
 */
public class BookingArea extends AbstractArea {

    public BookingArea(String name) {
        super(name);
    }

    @Override
    public List<AbstractModule> getModules() {
        return Arrays.asList(new BookingModule());
    }
}
