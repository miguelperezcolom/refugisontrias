package io.mateu.refugisontrias.model.booking;

import io.mateu.refugisontrias.model.util.Helper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by miguel on 25/5/17.
 */
public class Tester {

    public static void main(String... args) {

        test();

    }

    private static void test() {

        try {
            int pos = 0;
            Document doc = Jsoup.connect("https://admin.booking.com/").get();
            System.out.println(doc.title());
            System.out.println(doc.toString());

            Helper.writeTemp("booking_index.html", doc.toString());

            //post a https://admin.booking.com/?lang=es
//            lang:es
//            loginname:test
//            password:aaa
//            lang:es
//            csrf_token:empty-token

            //<div class="help-block alert alert-warning text-center">Tu cuenta ha sido desactivada. Ponte en contacto con nosotros por teléfono llamando al . Tu cuenta ha sido desactivada. Ponte en contacto con nosotros por teléfono llamando al .</div>

//            <input id="loginname" class="form-control" type="text"
//            placeholder="Introduce el nombre de usuario"
//            name="loginname"
//            value="test"
//            maxlength="40"
//            tabindex=1
//            autofocus="autofocus"
//            data-track-ga="login page, login_name, click">


            // Response header
            // Location:https://admin.booking.com/hotel/hoteladmin/index-hotel.html?perform_routing=1&hotel_id=1833383&ses=c138914b1fb0bb04647cd2c2020dc27c&lang=en



            doc = Jsoup.connect("https://admin.booking.com/")
                    .data("lang", "es").data("loginname", "1833383").data("password", "sontrias041015").data("csrf_token", "empty-token")
                    .post();

            Connection.Response response = Jsoup
                    .connect("https://admin.booking.com/")
                    .data("lang", "es").data("loginname", "1833383").data("password", "sontrias041015").data("csrf_token", "empty-token")
                    .method(Connection.Method.POST)
                    .followRedirects(false)
                    .execute();

            response.header("Location");


            System.out.println(doc.toString());


            //https://admin.booking.com/hotel/hoteladmin/extranet_ng/manage/upcoming_reservations.html?hotel_id=1833383&ses=163c8ed91113fec6068ff67277a844c3&lang=en
            //302: Location:https://admin.booking.com/hotel/hoteladmin/new_location/?dest=%2Fhotel%2Fhoteladmin%2Fextranet_ng%2Fmanage%2Fupcoming_reservations.html%3Fhotel_id%3D1833383%26lang%3Den&ses=163c8ed91113fec6068ff67277a844c3&hotel_id=1833383
            //https://admin.booking.com/hotel/hoteladmin/new_location/?dest=%2Fhotel%2Fhoteladmin%2Fextranet_ng%2Fmanage%2Fupcoming_reservations.html%3Fhotel_id%3D1833383%26lang%3Den&ses=163c8ed91113fec6068ff67277a844c3&hotel_id=1833383



//<div class="auth-wrapper">
//
//    <!-- Step 1: New location detected, ask for verification and provide limited access to reservations -->
//
//    <div class="content-block " id="verify_by_pin">
//        <h2 class="title_select_phone_number">Choose a verification method
//                    </h2>
//        <p>Please choose a verification method for this device and you will immediately receive a 6-digit code.
//                    To verify this device via <b>Pulse code</b>, you will need to retrieve your code within the app’s settings.
//        </p>
//
//        <form id="select_phone_number" class="form-horizontal one-time-submittable" action="verify.html?hotel_id=1833383&ses=163c8ed91113fec6068ff67277a844c3" method="POST">
//            <input type="hidden" name="dest" value="%2Fhotel%2Fhoteladmin%2Fextranet_ng%2Fmanage%2Fupcoming_reservations.html%3Fhotel_id%3D1833383%26lang%3Den" />
//            <input type="hidden" name="check_pin_auth" value="" />
//            <input type="hidden" name="phone_id" />
//
//            <div class="form-group">
//                <div class="delivery-method-options">
//                    <label for="voice_call_option" class="radio inline"><input type="radio" id="voice_call_option" name="message_type" value="call"  />
//                    Phone call</label>
//                    <label class="radio inline" for="text_option"><input type="radio" id="text_option" name="message_type" value="sms"  />
//                    SMS</label>
//                    <label class="radio inline pulse_code_option" for="pulse_code_option"><input type="radio" id="pulse_code_option" name="message_type" value="pulse"  />
//                    Pulse code</label>
//                </div>
//
//                <div class="delivery-method-dependant-options">
//                    <label class="control-label  label-call">Select phone number</label>
//                    <div class="controls channel-select-box call-select-box">
//                            <select class="form-control" name="phone_id_call">
//
//                                <option value="e4a7b0d6d0f1eb5a3ecea2ea5d6eba23" >
//                    +34*****2363
//
//                    </option>
//                           </select>
//                        </div>
//                    <label class="control-label  label-sms">Select phone number</label>
//                    <div class="controls channel-select-box sms-select-box">
//                            <select class="form-control" name="phone_id_sms">
//
//                                <option value="e4a7b0d6d0f1eb5a3ecea2ea5d6eba23" >
//                    +34*****2363
//
//                    </option>
//                           </select>
//                        </div>
//
//                    <div class="controls channel-select-box pulse-select-box">
//                        <input type="text" name="ask_pin" /> <input type="submit" class="btn btn-primary send-me-pin" data-track-ga="Two Factor Auth,Send Pin button" value="" />
//                        <p class="pulse-image-label">Get your Pulse code from the app by following the steps below.</p>
//                        <span class="ios">
//                            <img src="https://r.bstatic.com/backend_static/common/img/classic_extranet/pulse_token_ios_2/a3bfbbb1b3108aebc29eca12950566817bb253f1.png" />
//                        </span>
//                    </div>
//                    <div class="ctas cta-phone">
//                        <input type="submit" class="btn btn-primary send-me-pin" data-track-ga="Two Factor Auth,Send Pin button" value="" />
//                    </div>
//                </div>
//            </div>
//            <div class="cta-alt delivery-method-dependant-options">
//            <a href="?hotel_id=1833383;ses=163c8ed91113fec6068ff67277a844c3;phone_not_present=1;dest=%2Fhotel%2Fhoteladmin%2Fextranet_ng%2Fmanage%2Fupcoming_reservations.html%3Fhotel_id%3D1833383%26lang%3Den" data-track-ga="Two Factor Auth,Number not shown link" class="link">My number is not shown</a>
//            </div>
//        </form>
//    </div><!-- / verify via phone -->
//
//
//    <!-- / Step 1 -->
//
//    <div class="footer-links">
//        <a href="/hotel/hoteladmin/privacy.html?lang=en" class="privacy-link btn btn-link" target="_blank">Privacy and Cookies</a></li></a>
//    </div>
//</div>




            // post a https://admin.booking.com/hotel/hoteladmin/new_location/verify.html?hotel_id=1833383&ses=163c8ed91113fec6068ff67277a844c3
//            dest:%2Fhotel%2Fhoteladmin%2Fextranet_ng%2Fmanage%2Fupcoming_reservations.html%3Fhotel_id%3D1833383%26lang%3Den
//            check_pin_auth:
//            phone_id:e4a7b0d6d0f1eb5a3ecea2ea5d6eba23
//            message_type:sms
//            phone_id_call:e4a7b0d6d0f1eb5a3ecea2ea5d6eba23
//            phone_id_sms:e4a7b0d6d0f1eb5a3ecea2ea5d6eba23
//            ask_pin:
//            csrf_token:empty-token


            // recibimos

//            <div class="enter-pin-div" style="">
//            <div class="content-block" id="verify_by_phone" style="">
//                <h2 class="title_select_phone_number">Enter the PIN provided to you by Booking.com</h2>
//                <form id="enter_security_pin" class="form-horizontal pinform one-time-submittable" action="confirm.html" method="POST">
//                    <div class="form-group">
//                        <div class="pin-controls">
//                            <label>Enter new PIN number:</label>
//                            <input type="number" name="ask_pin"/>
//                        </div>
//                        <div class="ctas">
//                            <input type="submit" class="btn btn-primary text-me" value="Verify now"  data-track-ga="Two Factor Auth,Verify Now"/>
//                            <input type="hidden" name="hotel_id" value="1833383" />
//                            <input type="hidden" name="ses" value="163c8ed91113fec6068ff67277a844c3" />
//                            <input type="hidden" name="account_id" value="1527115">
//                            <input type="hidden" name="dest" value="%2Fhotel%2Fhoteladmin%2Fextranet_ng%2Fmanage%2Fupcoming_reservations.html%3Fhotel_id%3D1833383%26lang%3Den" />
//                            <input type="hidden" name="check_pin_auth" value="" />
//                        </div>
//                    </div>
//                </form>
//            </div>
//            <p class="use-phone call-again-selector ">If you didn't receive a call or entered an incorrect PIN, <a href="#" data-track-ga="Two Factor Auth,Start again link" >click here</a>.</p>
//                    </div>





            // post a Request URL:https://admin.booking.com/hotel/hoteladmin/new_location/confirm.html

//            ask_pin:165314
//            hotel_id:1833383
//            ses:163c8ed91113fec6068ff67277a844c3
//            account_id:1527115
//            dest:%2Fhotel%2Fhoteladmin%2Fextranet_ng%2Fmanage%2Fupcoming_reservations.html%3Fhotel_id%3D1833383%26lang%3Den
//            check_pin_auth:
//            csrf_token:empty-token


            // recibimos


//<div class="content-block" id="verify_by_pin">
//
//    <p class="alert-block">The PIN you entered is incorrect. <a href="verify.html?hotel_id=1833383&ses=163c8ed91113fec6068ff67277a844c3&phone_id=0&dest=%2Fhotel%2Fhoteladmin%2Fextranet_ng%2Fmanage%2Fupcoming_reservations.html%3Fhotel_id%3D1833383%26lang%3Den&check_pin_auth=">Please try again here</a>.</p>
//
//    <div class="footer-links">
//        <a href="/hotel/hoteladmin/privacy.html?lang=xu" class="privacy-link btn btn-link" target="_blank">Privacy and Cookies</a></li></a>
//    </div>
//</div>


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
