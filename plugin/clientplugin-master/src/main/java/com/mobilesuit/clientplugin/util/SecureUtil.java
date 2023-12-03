package com.mobilesuit.clientplugin.util;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.mobilesuit.clientplugin.singleton.DataContainer;

public class SecureUtil {
    public static CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("github-token", key)
        );
    }

    public static String getToken(){
        String key = null; // e.g. serverURL, accountID
        CredentialAttributes attributes = createCredentialAttributes("GUNDAM_Plugin");
        PasswordSafe passwordSafe = PasswordSafe.getInstance();

        Credentials credentials = passwordSafe.get(attributes);
        if (credentials != null) {
            String password = credentials.getPasswordAsString();
            return password;
        }
        return null;
    }

    public static void deleteToken(){
        CredentialAttributes attributes = createCredentialAttributes("GUNDAM_Plugin");

        PasswordSafe.getInstance().set(attributes, null);
    }

}
