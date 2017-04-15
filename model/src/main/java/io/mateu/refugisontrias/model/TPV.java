package io.mateu.refugisontrias.model;


import com.google.common.base.Strings;
import io.mateu.refugisontrias.model.util.Helper;
import io.mateu.refugisontrias.model.util.JPATransaction;
import io.mateu.refugisontrias.model.webpay.Digest;
import io.mateu.ui.core.server.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.text.DecimalFormat;

import static org.apache.fop.fonts.type1.AdobeStandardEncoding.t;

/**
 * Created by miguel on 29/3/17.
 */
@Entity@Getter@Setter
public class TPV {


    /*
    public static final String TEST_CLAVE = "sq7HjrUOBfKmC576ILgskD5srU870gJ7";
    public static final String TEST_CODIGO = "22052526";
    public static final String TEST_NOMBRE = "VIAJES URBIS, S.A.";
    public static final String TEST_URL = "http://www.viajesurbis.com";
*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private TPVTYPE type;

    private String name;
    private String paypalEmail;
    private boolean xml;
    private String merchantCode;
    private String merchantName;
    private String merchantSecret;
    private String privateKey;
    private String merchantTerminal;
    private String actionUrl;
    private String notificationUrl;
    private String okUrl;
    private String koUrl;


}
