package io.mateu.refugisontrias.web;

import io.mateu.refugisontrias.model.util.Helper;
import io.mateu.refugisontrias.model.util.JPATransaction;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 1/3/17.
 */
public class Logic {

    public static String checkDisponibilidad(Map<String, Object> m) throws Exception {
        //LocalDate entrada, LocalDate salida, int camas, int campings;
        LocalDate entrada = LocalDate.parse((String) m.get("entrada"), DateTimeFormatter.ISO_DATE);
        LocalDate salida = LocalDate.parse((String) m.get("salida"), DateTimeFormatter.ISO_DATE);
        int uds = Integer.parseInt((String) m.get("pax"));
        int camas = ("bedroom".equals(m.get("que")))?uds:0;
        int campings = ("camping".equals(m.get("que")))?uds:0;

        Map<String, Object> r = new HashMap<>();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                long noches = DAYS.between(entrada, salida);

                r.put("ok", "bedroom".equals(m.get("que")));
                r.put("resumen", "Data IN " + entrada + " Data OUT " + salida + " PAX " + uds + " NITS " + noches + " " + m.get("que") + " SI<br/>" +
                        "Preu PAX/NIT ALLOTJAMENT = 14,00€<br/>" +
                        "Preu TOTAL allotjament = 140,00€");

            }
        });

        return Helper.toJson(r);
    }

    public static String getEntradasAlternativas(Map<String, Object> m) throws Exception {
        LocalDate entrada = LocalDate.parse((String) m.get("entrada"), DateTimeFormatter.ISO_DATE);
        LocalDate salida = LocalDate.parse((String) m.get("salida"), DateTimeFormatter.ISO_DATE);
        int uds = Integer.parseInt((String) m.get("pax"));
        int camas = ("bedroom".equals(m.get("que")))?uds:0;
        int campings = ("camping".equals(m.get("que")))?uds:0;

        Map<String, Object> r = new HashMap<>();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                long noches = DAYS.between(entrada, salida);

                r.put("ok", "bedroom".equals(m.get("que")));
                r.put("resumen", "Data IN " + entrada + " Data OUT " + salida + " PAX " + uds + " NITS " + noches + " " + m.get("que") + " SI<br/>" +
                        "Preu PAX/NIT ALLOTJAMENT = 14,00€<br/>" +
                        "Preu TOTAL allotjament = 140,00€");

            }
        });


        return Helper.toJson(r);
    }

    public static String confirmar(Map<String, Object> m) throws IOException {
        LocalDate entrada = LocalDate.parse((String) m.get("entrada"), DateTimeFormatter.ISO_DATE);
        LocalDate salida = LocalDate.parse((String) m.get("salida"), DateTimeFormatter.ISO_DATE);
        int uds = Integer.parseInt((String) m.get("pax"));
        int camas = ("bedroom".equals(m.get("que")))?uds:0;
        int campings = ("camping".equals(m.get("que")))?uds:0;

        Map<String, Object> r = new HashMap<>();
        return Helper.toJson(r);
    }

    public static String comboPax(Map<String, Object> m) throws IOException {
        List<Map<String, Object>> valores = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            valores.add(Helper.hashmap("value", i, "text", i));
        }
        return Helper.toJson(valores);
    }
}
