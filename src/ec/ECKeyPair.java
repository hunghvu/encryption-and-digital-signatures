/**
 * This represents a key pair
 * @author Phong Le
 */
package ec;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import kmac.KCrypt;
import kmac.Sha3;
import util.DecryptionData;
import util.UtilMethods;

public class ECKeyPair {

    /** The static point of the public key. */
    public static final ECPoint G = new ECPoint(BigInteger.valueOf(4L), false);
    /** The public key, generated based on the provided passphrase. */
    private final ECPoint V;
    /** The secret key. */
    private final BigInteger s;

    /**
     * Generating a (Schnorr/ECDHIES) key pair from passphrase pw
     * 
     * @param passphrase the provided passphrase
     */
    public ECKeyPair(String passphrase) {
        byte[] outvalkey = Sha3.KMACXOF256(passphrase.getBytes(), new byte[] {}, 512, "K");
        // s <- KMACXOF256(pw, "", 512, "K"); s <- 4s. (s) in this case is sKey
        s = (new BigInteger(outvalkey)).multiply(BigInteger.valueOf(4));
        // V <- s*G
        V = G.multiply(s);
    }

    /**
     * Constructs a new key pair with the provided private key.
     * 
     * @param s the private key
     */
    public ECKeyPair(BigInteger s) {
        this.s = s;
        V = G.multiply(s);
    }

    /**
     * Get the public key.
     */
    public ECPoint getPublicKey() {
        return V;
    }

    /**
     * Get the private key.
     */
    public BigInteger getPrivateKey() {
        return s;
    }

    /**
     * Encrypts the private key under the provided password, then writes it to the
     * path provided.
     * 
     * @param pass passphrase
     * @param path output file path
     */
    public String writePrvToFile(String pass, String path) {
        path += File.separator + "PrivateKey";
        try {
            UtilMethods.writeBytesToFile(KCrypt.encrypt(s.toByteArray(), pass.getBytes()), path);
            return "Encrypted data has been written to " + path;
        } catch (IOException e) {
            return "Error occurred while writing output to file.";
        }
    }

    /**
     * Reads the specified private key file and returns a new ECKeyPair object
     * 
     * @param path the path to the file containing the serialized private key
     * @param pwd  the password used to write the secret key to the file
     * @return a new ECKeyPair object containing the private key of the file
     */
    public static ECKeyPair readPrivateKeyFile(String path, String pwd) {
        byte[] enc = UtilMethods.readFileBytes(path);
        if (enc == null) {
            return null;
        }
        DecryptionData prvBytes = KCrypt.decrypt(enc, pwd.getBytes());
        if (!prvBytes.isValid()) {
            return null;
        }
        return new ECKeyPair(new BigInteger(prvBytes.getData()));
    }

    /**
     * Retrieve a curve point from a file
     * 
     * @param inPath the path to the public key file
     */
    public static ECPoint readPubKeyFile(String inPath) {
        return ECPoint.toECPoint(UtilMethods.readFileBytes(inPath));
    }

    /**
     * Write public key to a file
     * 
     * @param outPath url of the output path
     */
    public String writePubToFile(String outPath) {
        outPath += File.separator + "PublicKey";
        String result = UtilMethods.writeBytesToFile(V.toByteArray(), outPath);
        if (result.equals("")) {
            return "Public key file has been written to " + outPath;
        } else {
            return result;
        }
    }

}
