package io.mateu.refugisontrias.model;

import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 13/4/17.
 */
public class CupoCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        if (o != null) {
            try {
                int cupo = ((Integer)o).intValue();
                if (cupo > 5) return "success";
                else if (cupo > 2) return "warning";
                else if (cupo <= 2) return "danger";
            } catch (Exception e) {

            }
        }
        return null;
    }
}
