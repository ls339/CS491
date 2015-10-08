package cs491;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
import com.sun.crypto.provider.SunJCE;

public class DiffieHellman {
	
	private BigInteger p, g;
	
	// https://docs.oracle.com/javase/7/docs/api/javax/crypto/spec/DHParameterSpec.html
	DHParameterSpec params = new DHParameterSpec(p,g);
	
	// http://docs.oracle.com/javase/7/docs/api/java/security/KeyPairGenerator.html
	KeyPairGenerator requestorGen = KeyPairGenerator.getInstance("DH");
	
	requestorGen.initialize(params, new SecureRandom());
	
	public DiffieHellman() {
		//Testing 
		System.out.println(Security.getProviders());
	}

}

/* seems to be working right. Saad
*/
// Alex
