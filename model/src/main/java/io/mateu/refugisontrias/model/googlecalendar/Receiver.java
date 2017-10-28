package io.mateu.refugisontrias.model.googlecalendar;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by miguel on 30/5/17.
 */
public class Receiver implements VerificationCodeReceiver {

    public final Lock lock;
    public final Condition gotAuthorizationResponse;
    private final String callbackUrl;

    public String code;
    public String error;

    public Receiver(String callbackUrl) {
        this.lock = new ReentrantLock();
        this.gotAuthorizationResponse = this.lock.newCondition();
        this.callbackUrl = callbackUrl;
    }


    @Override
    public String getRedirectUri() throws IOException {
        return callbackUrl;
    }

    @Override
    public String waitForCode() throws IOException {
        System.out.println("OAUTH2: waitForCode()");

        String var1;
            System.out.println("OAUTH2: awaiting");

            while(this.code == null && this.error == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("OAUTH2: continue. code = " + code + ", error = " + error);


            if(this.error != null) {
                throw new IOException("User authorization failed (" + this.error + ")");
            }

            var1 = this.code;

        System.out.println("OAUTH2: code = " + var1);
        return var1;
    }

    @Override
    public void stop() throws IOException {

    }
}
