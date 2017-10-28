package io.mateu.refugisontrias.model.googlecalendar;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

import java.io.IOException;

/**
 * Created by miguel on 30/5/17.
 */
public abstract class MyAuthorizationCodeApp extends AuthorizationCodeInstalledApp {
    public MyAuthorizationCodeApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
        super(flow, receiver);
    }

    @Override
    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        setAuthorizationUrl(authorizationUrl);
    }

    public abstract void setAuthorizationUrl(AuthorizationCodeRequestUrl authorizationUrl);
}
