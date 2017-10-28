package io.mateu.refugisontrias.client;

import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 19/2/17.
 */
public class RSTApp extends AbstractApplication {
    @Override
    public String getName() {
        return "Refugi Son Trias";
    }

    @Override
    public List<AbstractArea> getAreas() {
        return Arrays.asList(new UniqueArea("All"));
    }
}
