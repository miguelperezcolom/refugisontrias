package io.mateu.refugisontrias.model;

import com.google.common.base.Strings;
import io.mateu.refugisontrias.model.util.Helper;
import io.mateu.refugisontrias.model.util.JPATransaction;
import io.mateu.refugisontrias.model.webpay.Digest;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import sis.redsys.api.ApiMacSha256;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

/**
 * Created by miguel on 5/4/17.
 */
@Entity
@Getter
@Setter
public class TPVTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private TPV tpv;

    private LocalDateTime created;

    private String log;

    private TPVTRANSACTIONSTATUS status = TPVTRANSACTIONSTATUS.PENDING;



    private String language;
    private Reserva booking;
    private double amount;
    private String currency;
    private String subject;




    public static String getForm(final long transactionId) throws Exception {
        final String[] h = {""};

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                TPVTransaction t = em.find(TPVTransaction.class, transactionId);

                h[0] = t.getForm(em, t.getLanguage(), t.getBooking().getId(), t.getId(), t.getAmount(), t.getCurrency(), t.getSubject());

            }
        });

        return h[0];
    }

    public String getForm(EntityManager em, final String language, final long bookingId, final long transactionId, final double amount, final String currency, final String subject) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String Merchant_ConsumerLanguage;
        if ("es".equals(language)) Merchant_ConsumerLanguage = "001";
        else if ("en".equals(language)) Merchant_ConsumerLanguage = "002";
        else if ("fr".equals(language)) Merchant_ConsumerLanguage = "004";
        else if ("de".equals(language)) Merchant_ConsumerLanguage = "005";
        else if ("it".equals(language)) Merchant_ConsumerLanguage = "007";
        else Merchant_ConsumerLanguage = "001";


        StringBuffer h = new StringBuffer();

        if (TPVTYPE.SERMEPA.equals(getTpv().getType())) {


            ApiMacSha256 apiMacSha256 = new ApiMacSha256();

            apiMacSha256.setParameter("DS_MERCHANT_AMOUNT", getMerchantAmount(amount));
            apiMacSha256.setParameter("DS_MERCHANT_ORDER", "" + getId());
            apiMacSha256.setParameter("DS_MERCHANT_MERCHANTCODE", getTpv().getMerchantCode());
            apiMacSha256.setParameter("DS_MERCHANT_CURRENCY", getMerchantCurrency(em, currency));
            apiMacSha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "0");
            apiMacSha256.setParameter("DS_MERCHANT_TERMINAL", getTpv().getMerchantTerminal());
            apiMacSha256.setParameter("DS_MERCHANT_MERCHANTURL", getTpv().getNotificationUrl());
            apiMacSha256.setParameter("DS_MERCHANT_URLOK", getTpv().getOkUrl());
            apiMacSha256.setParameter("DS_MERCHANT_URLKO", getTpv().getKoUrl());

            String params = apiMacSha256.createMerchantParameters();
            String signature = apiMacSha256.createMerchantSignature(getTpv().getMerchantSecret());

            h.append("<form name=f action='" + getTpv().getActionUrl() + "' method='post'>");
            h.append("<input type='hidden' name=Ds_SignatureVersion value='HMAC_SHA256_V1' />");
            h.append("<input type='hidden' name=Ds_MarchantParametersvalue='" + params + "' />");
            h.append("<input type='hidden' name=Ds_Signature value='" + signature + "' />");
            h.append("<input type='submit' value='Realizar Pago' />");
            h.append("</form>");

/*
            h.append("<form name=f action='" + getTpv().getActionUrl() + "' method='post'>");
            h.append("<input type='hidden' name=Ds_Merchant_MerchantName value='" + getTpv().getMerchantName() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_MerchantCode value='" + getTpv().getMerchantCode() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_ConsumerLanguage value='" + Merchant_ConsumerLanguage + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_Terminal value='" + getTpv().getMerchantTerminal() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_Order value='" + getId() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_Amount value='" + getMerchantAmount(amount) + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_Currency value='" + getMerchantCurrency(em, currency) + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_TransactionType value='" + getMerchantTransactionType() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_MerchantURL value='" + getTpv().getNotificationUrl() + "?idtransaccion=" + getId() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_MerchantSignature value='" + getSignature(em, transactionId, amount, currency, subject) + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_UrlOK value='" + getTpv().getOkUrl() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_UrlKO value='" + getTpv().getKoUrl() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_ProductDescription value='" + subject + "' />");
            h.append("</form>");
            */
        } else if (TPVTYPE.WEBPAY.equals(getTpv().getType())) {
            //MERCHANTNUMBER + | + OPERATION + | + ORDERNUMBER + | + AMOUNT + | + CURRENCY + | + DEPOSITFLAG + | + MERORDERNUM + | + URL + | + DESCRIPTION + | + MD

            h.append("<form name=f action='" + getTpv().getActionUrl() + "' method='post'>");
            h.append("<input type='hidden' name=MERCHANTNUMBER value='" + getTpv().getMerchantCode() + "' />");
            h.append("<input type='hidden' name=OPERATION value='CREATE_ORDER' />");
            h.append("<input type='hidden' name=ORDERNUMBER value='" + getId() + "' />");
            h.append("<input type='hidden' name=AMOUNT value='" + getMerchantAmount(amount) + "' />");
            h.append("<input type='hidden' name=CURRENCY value='" + getMerchantCurrency(em, currency) + "' />");
            h.append("<input type='hidden' name=DEPOSITFLAG value='1' />");
            h.append("<input type='hidden' name=MERORDERNUM value='" + getId() + "' />");
            h.append("<input type='hidden' name=URL value='" + getTpv().getNotificationUrl() + "?idtransaccion=" + getId() + "' />");
            h.append("<input type='hidden' name=DESCRIPTION value='" + subject + "' />");
            h.append("<input type='hidden' name=MD value='' />");
            h.append("<input type='hidden' name=DIGEST value='" + getSignature(em, transactionId, amount, currency, subject) + "' />");
            h.append("<input type='submit'>");
            h.append("</form>");
        } else if (TPVTYPE.PAYPAL.equals(getTpv().getType())) {

            h.append("<form action='https://www.paypal.com/cgi-bin/webscr' method='post'>"
                    + "<!-- Identify your business so that you can collect the payments. -->"
                    + "<input type='hidden' name='business' value='" + getTpv().getPaypalEmail() + "'>"
                    + "<!-- Specify a Buy Now button. -->"
                    + "<input type='hidden' name='cmd' value='_xclick'>"
                    + "<!-- Specify details about the item that buyers will purchase. -->"
                    + "<input type='hidden' name='notify_url' value='" + getTpv().getNotificationUrl() + "'>"
                    + "<input type='hidden' name='item_number' value='" + getId() + "'>"
                    + "<input type='hidden' name='item_name' value='BOOKING NR " + bookingId + "'>"
                    + "<input type='hidden' name='amount' value='" + Helper.roundOffEuros(amount) + "'>"
                    + "<input type='hidden' name='currency_code' value='" + currency + "'>"
                    + "<!-- Display the payment button. -->"
                    + "<input type='image' name='submit' border='0' src='https://www.paypalobjects.com/en_US/i/btn/btn_buynow_LG.gif' alt='PayPal - The safer, easier way to pay online'>"
                    + "<img alt='' border='0' width='1' height='1' src='https://www.paypalobjects.com/en_US/i/scr/pixel.gif' >"
                    + "</form>");

        } else {
            h.append("<h1>UNKNOWN TPV TYPE: " + getTpv().getType() + "</h1>");
        }

        return h.toString();
    }


    private String getMerchantTransactionType() {
        String Merchant_TransactionType = "0";
        return Merchant_TransactionType;
    }


    private String getMerchantCurrency(EntityManager em, String currencyCode) {
        return em.find(Moneda.class, currencyCode).getIso4217Code();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("-->" + new DecimalFormat("####.00").format(0.1).replaceAll(",", "").replaceAll("\\.", ""));


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager entityManager) throws Exception {

                Email email = new HtmlEmail();
                email.setHostName("mateu");
                email.setSmtpPort(25);
                email.setFrom("miguel@mateu.io");
                email.setSubject("TestMail");
                email.setMsg("This is a test mail ... :-)");
                email.addTo("miguelperezcolom@gmail.com");
                email.send();


            }
        });
    }


    private String getMerchantAmount(double amount) {
        return new DecimalFormat("####.00").format(amount).replaceAll(",", "").replaceAll("\\.", "");
    }

    public void sendEmail() throws Exception {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                Albergue a = em.find(Albergue.class, 1);


                Email email = new HtmlEmail();
                email.setHostName("smtp.googlemail.com");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("username", "password"));
                email.setSSLOnConnect(true);
                email.setFrom("user@gmail.com");
                email.setSubject("TestMail");
                email.setMsg("This is a test mail ... :-)");
                email.addTo("foo@bar.com");
                email.send();

            }
        });

    }


    public String getBoton(EntityManager em) throws Exception {
        return getBoton(em, getBooking().getId(), getAmount(), getCurrency());
    }

    public String getBoton(EntityManager em, final long bookingId, final double amount, final String currency) throws Exception {
        if (TPVTYPE.SERMEPA.equals(getTpv().getType()) || TPVTYPE.WEBPAY.equals(getTpv().getType())) {
            ApiMacSha256 apiMacSha256 = new ApiMacSha256();

            apiMacSha256.setParameter("DS_MERCHANT_AMOUNT", getMerchantAmount(amount));
            apiMacSha256.setParameter("DS_MERCHANT_ORDER", "" + getId());
            apiMacSha256.setParameter("DS_MERCHANT_MERCHANTCODE", getTpv().getMerchantCode());
            apiMacSha256.setParameter("DS_MERCHANT_CURRENCY", getMerchantCurrency(em, currency));
            apiMacSha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "0");
            apiMacSha256.setParameter("DS_MERCHANT_TERMINAL", getTpv().getMerchantTerminal());
            apiMacSha256.setParameter("DS_MERCHANT_MERCHANTURL", getTpv().getNotificationUrl());
            apiMacSha256.setParameter("DS_MERCHANT_URLOK", getTpv().getOkUrl());
            apiMacSha256.setParameter("DS_MERCHANT_URLKO", getTpv().getKoUrl());

            String params = apiMacSha256.createMerchantParameters();
            String signature = apiMacSha256.createMerchantSignature(getTpv().getMerchantSecret());

            StringBuffer h = new StringBuffer();

            h.append("<form name=f action='" + getTpv().getActionUrl() + "' method='post'>");
            h.append("<input type='hidden' name=Ds_SignatureVersion value='HMAC_SHA256_V1' />");
            h.append("<input type='hidden' name=Ds_MarchantParameters value='" + params + "' />");
            h.append("<input type='hidden' name=Ds_Signature value='" + signature + "' />");
            h.append("<input type='submit' value='PAY NOW' />");
            h.append("</form>");

            return h.toString();
        } else if (TPVTYPE.PAYPAL.equals(getTpv().getType())) {
            return "<form action='https://www.paypal.com/cgi-bin/webscr' method='post'>"
                    + "<!-- Identify your business so that you can collect the payments. -->"
                    + "<input type='hidden' name='business' value='" + getTpv().getPaypalEmail() + "'>"
                    + "<!-- Specify a Buy Now button. -->"
                    + "<input type='hidden' name='cmd' value='_xclick'>"
                    + "<!-- Specify details about the item that buyers will purchase. -->"
                    + "<input type='hidden' name='notify_url' value='" + getTpv().getNotificationUrl() + "'>"
                    //+ "<input type='hidden' name='return' value='" + getOkUrl() + "'>"
                    + "<input type='hidden' name='item_number' value='" + getId() + "'>"
                    + "<input type='hidden' name='item_name' value='BOOKING NR " + bookingId + "'>"
                    + "<input type='hidden' name='amount' value='" + Helper.roundOffEuros(amount) + "'>"
                    + "<input type='hidden' name='currency_code' value='" + currency + "'>"
                    + "<!-- Display the payment button. -->"
                    + "<input type='image' name='submit' border='0' src='http://www.paypalobjects.com/en_US/i/btn/btn_paynowCC_LG.gif' alt='PayPal - The safer, easier way to pay online'>"
                    + "<img alt='' border='0' width='1' height='1' src='http://www.paypalobjects.com/en_US/i/scr/pixel.gif' >"
                    + "</form>";
        } else {
            return "<h1>UNKNOWN TPV TYPE: " + getTpv().getType() + "</h1>";
        }
    }

    private String getSignature(EntityManager em, final long bookingId, final double amount, final String currency, final String subject) throws NoSuchAlgorithmException {

        if (TPVTYPE.WEBPAY.equals(getTpv().getType())) {

			/*
						h.append("<form name=f action='" + getActionUrl() + "' method='post'>");
			h.append("<input type='hidden' name=MERCHANTNUMBER value='" + getMerchantCode() + "' />");
			h.append("<input type='hidden' name=OPERATION value='CREATE_ORDER' />");
			h.append("<input type='hidden' name=ORDERNUMBER value='" + Helper.getID(this) + "' />");
			h.append("<input type='hidden' name=AMOUNT value='" + getMerchantAmount() + "' />");
			h.append("<input type='hidden' name=CURRENCY value='" + getMerchantCurrency() + "' />");
			h.append("<input type='hidden' name=DEPOSITFLAG value='1' />");
			h.append("<input type='hidden' name=MERORDERNUM value='" + Helper.getID(this) + "' />");
			h.append("<input type='hidden' name=URL value='" + getNotificationUrl() + "?idtransaccion=" + Helper.getID(this) + "' />");
			h.append("<input type='hidden' name=DESCRIPTION value='" + getSubject() + "' />");
			h.append("<input type='hidden' name=DIGEST value='" + getSignature() + "' />");
			h.append("</form>");

			 */

            //MERCHANTNUMBER + | + OPERATION + | + ORDERNUMBER + | + AMOUNT + | + CURRENCY + | + DEPOSITFLAG + | + MERORDERNUM + | + URL + | + DESCRIPTION + | + MD
            String contenido = "" + getTpv().getMerchantCode() + "|" + "CREATE_ORDER" + "|" + getId() + "|" + getMerchantAmount(amount) + "|" + getMerchantCurrency(em, currency) + "|" + "1" + "|" + bookingId + "|" + getTpv().getNotificationUrl() + "?idtransaccion=" + bookingId + "|" + subject + "|" + "";

            System.out.println("contenido=" + contenido);
            System.out.println("clave=" + getTpv().getPrivateKey());

            String firma = null;
            try {
                Security.addProvider(new BouncyCastleProvider());

                PEMReader pemParser = new PEMReader(new StringReader(getTpv().getPrivateKey()));
                Object object = pemParser.readObject();
                System.out.println("leido " + object.getClass().getName());
                KeyPair kp = (KeyPair) object;
                firma = Digest.sign(contenido, kp.getPrivate());

                System.out.println("firma=" + firma);
                System.out.println(Digest.sign(contenido, "/Users/miguel/Downloads/GP_webpay_Implementation_Examples_v2.1_CZ_EN/progs/generate_certificate/generate_certificate_en/estec.ks", "antonia", "gpwebpay"));
                //firma = Digest.sign(contenido, "/Users/miguel/Downloads/GP_webpay_Implementation_Examples_v2.1_CZ_EN/test_keystore_and_certificate/test.ks", "test", "gpwebpay");

                System.out.println("firma=" + firma);

            } catch (KeyException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                //} catch (KeyStoreException e) {
                //	e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            System.out.println("Firma: " + firma);

            return firma;
        } else if (TPVTYPE.SERMEPA.equals(getTpv().getType())) {
            byte bAmount[] = getMerchantAmount(amount).getBytes();
            byte bOrder[] = ("" + getId()).getBytes();
            byte bCode[] = getTpv().getMerchantCode().getBytes();
            byte bCurrency[] = getMerchantCurrency(em, currency).getBytes();
            byte bTransactionType[] = getMerchantTransactionType().getBytes();
            byte bMerchantURL[] = (getTpv().getNotificationUrl() + "?idtransaccion=" + getId()).getBytes();
            byte bMerchantURLOK[] = getTpv().getOkUrl().getBytes();
            byte bMerchantURLKO[] = getTpv().getKoUrl().getBytes();
            byte bPassword[] = getTpv().getMerchantSecret().getBytes();

            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(bAmount);
            sha.update(bOrder);
            sha.update(bCode);
            sha.update(bCurrency);
            sha.update(bTransactionType);
            sha.update(bMerchantURL);
            byte[] hash = sha.digest(bPassword);

            int h = 0;
            String s = new String();

            final int SHA1_DIGEST_LENGTH = 20;

            String Merchant_Signature = "";

            for(int i = 0; i < SHA1_DIGEST_LENGTH; i++) {
                h = (int) hash[i];          // Convertir de byte a int
                if(h < 0) h += 256;  // Si son valores negativos, pueden haber problemas de conversi¢n.
                s = Integer.toHexString(h); // Devuelve el valor hexadecimal como un String
                if (s.length() < 2) Merchant_Signature = Merchant_Signature.concat("0"); // A¤ade un 0 si es necesario
                Merchant_Signature = Merchant_Signature.concat(s); // A¤ade la conversi¢n a la cadena ya existente
            }

            Merchant_Signature = Merchant_Signature.toUpperCase(); // Convierte la cadena generada a Mayusculas.
            System.out.println("Merchant_Signature: " + Merchant_Signature);

            return Merchant_Signature;
        }
        return null;
    }



    public static void procesarPost(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        System.out.println("****TPVNOTIFICACION****");
        for (String n : request.getParameterMap().keySet()) {
            for (String v : request.getParameterValues(n)) {
                System.out.println("" + n + ":" + v);
            }
        }
        System.out.println("****TPVNOTIFICACION****");

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                String idtransaccion = request.getParameter("idtransaccion");
                if (idtransaccion == null && request.getParameter("item_number") != null) {
                    idtransaccion = request.getParameter("item_number");
                }


                TPVTransaction t = em.find(TPVTransaction.class, Long.parseLong(idtransaccion));
                Reserva r = t.getBooking();
                r.getLog().add(new Log(Helper.toJson(request.getParameterMap())));


                boolean ok = false;

                switch (r.getAlbergue().getTpv().getType()) {
                    case SERMEPA:
                        String src = request.getParameter("Ds_Response");
                        ok = !Strings.isNullOrEmpty(src) && Helper.toInt(src) < 101;
                        break;
                    case WEBPAY:
                        String src2 = request.getParameter("PRCODE");
                        ok = !Strings.isNullOrEmpty(src2) && Helper.toInt(src2) == 0;
                        break;
                    case PAYPAL:
                        ok = "Completed".equalsIgnoreCase(request.getParameter("payment_status")) || "Processed".equalsIgnoreCase(request.getParameter("payment_status"));
                        break;
                }



                if (ok) {
                    t.setStatus(TPVTRANSACTIONSTATUS.OK);
                    if (TPVTYPE.WEBPAY.equals(r.getAlbergue().getTpv().getType())) {
                        String msg = "";
                        if (request.getParameter("PRCODE") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "PRCODE=" + request.getParameter("PRCODE");
                        }
                        if (request.getParameter("SRCODE") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "SRCODE=" + request.getParameter("SRCODE");
                        }
                        if (request.getParameter("RESULTTEXT") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "RESULTTEXT=" + request.getParameter("RESULTTEXT");
                        }
                        response.sendRedirect("tpvok.jsp?msg=" + msg);
                    }

                    Pago p;
                    r.getPagos().add(p = new Pago());
                    p.setAuditoria(new Auditoria());
                    p.setTipo(TipoPago.TPV);
                    p.setImporte(t.getAmount());
                    p.setReserva(r);

                } else {
                    t.setStatus(TPVTRANSACTIONSTATUS.ERROR);
                    if (TPVTYPE.WEBPAY.equals(r.getAlbergue().getTpv().getType())) {
                        String msg = "";
                        if (request.getParameter("PRCODE") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "PRCODE=" + request.getParameter("PRCODE");
                        }
                        if (request.getParameter("SRCODE") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "SRCODE=" + request.getParameter("SRCODE");
                        }
                        if (request.getParameter("RESULTTEXT") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "RESULTTEXT=" + request.getParameter("RESULTTEXT");
                        }
                        response.sendRedirect("tpvko.jsp?msg=" + msg);
                    }

                }


            }
        });


    }
}
