package ec;

import java.math.BigInteger;
import java.util.HashMap;

import kmac.Sha3;

public class ECKeyPair {
	
	// Generating a (Schnorr/ECDHIES) key pair from passphrase pw
		public static HashMap<BigInteger, ECPoint> get_keypair(String passphrase) {

			byte[] data = "".getBytes();
			int length = 512;
			String s = "K";
			byte[] outval = Sha3.KMACXOF256(passphrase.getBytes(), data, length, s);
			// s <- KMACXOF256(pw, "", 512, "K"); s <- 4s. (s) in this case is outvalKey
			BigInteger outvalKey = (new BigInteger(outval)).multiply(BigInteger.valueOf(4));
			// Get point y of base point g, (+ or -)sqrt((1-x^2)/(1 + 376014x^2)) mod p
			// Where nominator is (1-x^2) and denominator is (1 + 376014x^2)
			BigInteger x = BigInteger.valueOf(4);
			BigInteger xSquare = x.modPow(BigInteger.valueOf(2),  ECPoint.P);
			BigInteger radicandNominator = BigInteger.ONE.subtract(xSquare); // (1-x^2)
			BigInteger radicandDenominator = BigInteger.ONE.add(BigInteger.valueOf(-1 * ECPoint.D.intValue()).multiply(xSquare).mod(ECPoint.P)); // (1 + 376014x^2)
			BigInteger y = ECPoint.sqrt(radicandNominator.multiply(radicandDenominator.modInverse(ECPoint.P)),ECPoint.P, false); // Final y

			// The curve has a special point G(x_0,y_0) called its public generator, with x_0 = 4 and y_0 a certain unique even number.
			ECPoint g = new ECPoint(x, y);
			// V <- s*G
			ECPoint v = g.multiply(outvalKey);
			HashMap<BigInteger, ECPoint> keypair = new HashMap<>();
			// key pair: (s, V)
			keypair.put(outvalKey, v);
			return keypair;
		}


}
