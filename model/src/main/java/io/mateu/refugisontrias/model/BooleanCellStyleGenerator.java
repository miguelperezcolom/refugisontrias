package io.mateu.refugisontrias.model;

import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 14/4/17.
 */
public class BooleanCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        return (o != null && ((Boolean)o))?"done":"cancelled";
    }
}
