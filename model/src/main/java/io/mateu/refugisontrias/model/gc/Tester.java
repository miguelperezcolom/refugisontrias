package io.mateu.refugisontrias.model.gc;

import io.mateu.refugisontrias.model.Huesped;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 23/5/17.
 */
public class Tester {

    public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    public static void main(String... args) throws NoSuchAlgorithmException, KeyManagementException {

        enableSSLSocket();

        send(new ArrayList<>());

    }


    public static void send(List<Huesped> huespedes) {

        if (huespedes.size() > 0) {

            try {
                Document doc = Jsoup.connect("https://hospederias.guardiacivil.es/").get();
                System.out.println(doc.title());
                //System.out.println(doc.toString());
                doc = Jsoup.connect("https://hospederias.guardiacivil.es/hospederias/login.do").data("usuario", "07301AAX01", "pswd", "00D1021V23VD").post();
                System.out.println(doc.toString());

                doc = Jsoup.connect("https://hospederias.guardiacivil.es/hospederias/cargaFichero.do")
                        .data("fichero", "datos.txt", new ByteArrayInputStream(toString(huespedes).getBytes(StandardCharsets.UTF_8))).data("confirmacion", "true").data("autoSeq", "on")
                        .post();
                System.out.println(doc.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private static String toString(List<Huesped> huespedes) {
        StringWriter sw;
        PrintWriter pw = new PrintWriter(sw = new StringWriter());

        for (Huesped h : huespedes) {
            pw.println("" + "2" + "|" + h.getNumeroDocumento() + "|" + h.getTipoDocumento()
                    + "|" + ((h.getExpedicion() != null)?h.getExpedicion().format(DateTimeFormatter.ofPattern("yyyyMMdd")):"00000000")
                    + "|" + h.getApellido1()
                    + "|" + ((h.getApellido2() != null)?h.getApellido2():"")
                    + "|" + h.getNombre()
                    + "|" + h.getSexo()
                    + "|" + h.getNacimiento().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + "|" + h.getNacionalidad()
                    + "|" + h.getReserva().getEntrada().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            );
        }


        return sw.toString();
    }

}
