package me.drex.votifier.rsa;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * An RSA key pair generator.
 *
 * @author Blake Beaupain
 */

public class RSAKeygen {

    private RSAKeygen() {
    }

    /**
     * Generates an RSA key pair.
     *
     * @param bits The amount of bits
     * @return The key pair
     */

    public static KeyPair generate(int bits) throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(bits, RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);
        return keygen.generateKeyPair();
    }
}