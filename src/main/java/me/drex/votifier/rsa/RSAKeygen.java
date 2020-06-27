package me.drex.votifier.rsa;

import me.drex.votifier.Votifier;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.logging.Logger;

public class RSAKeygen {

    //Generate an RSA key
    public static KeyPair generate(int bits) throws Exception {
        Votifier.getLogger().info("Votifier is generating an RSA key pair...");
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(bits,
                RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);
        return keygen.generateKeyPair();
    }

}
