//Cipher input stream, cipher output streams instead of byte[]

/*
	TODO --------> implement data transform interface: dofoperation.session.datatransform
	Cipher aesEncryptCipher = Cipher.getInstance("AES");
	Cipher aesDecryptCipher = Cipher.getInstance("AES"); 
	aesEncryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
	aesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret);
	
	workflow: est session - key neg - construct obj to gen ciphers - attach on both sides of session
*/

// requester - session operation returned, dofoperation.session returned when session created
// provider - dofrequest.session created when session request received. call setdatatransform here

public Cipher getDecryptCipher(Cipher inCipher, SecretKey sharedSecret, IvParameterSpec iv)
{
	try {
		Cipher aesDecryptCipher = inCipher;
		aesDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //MUST specify an IV and distribute to both sides
		aesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret, iv); //iv is the saved IV from encoded public key method

		return aesDecryptCipher;
	}
	catch(Exception e) {
		return null;
	}
}

//create ciphers in initialized method or constructor - class level private variables
public Cipher getEncryptCipher(Cipher inCipher, SecretKey sharedSecret, IvParameterSpec iv)
{
	try {
		Cipher aesEncryptCipher = inCipher;
		aesEncryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesEncryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret, iv); //iv is the saved IV from encoded public key method

		return aesEncryptCipher;
	}
	catch(Exception e) {
		return null;
	}
}

@Override
public byte[] transformSendData(DOFInterfaceID interfaceID, byte[] data)
{
	Cipher aesEncryptCipher = savedEncryptCipher;
	byte[] byteCipherData = aesEncryptCipher.doFinal(data);
	return byteCipherData;
}

@Override
public byte[] transformReceiveData(DOFInterfaceID interfaceID, byte[] data)
{
	Cipher aesDecryptCipher = savedDecryptCipher;
	byte[] bytePlainData = aesDecryptCipher.doFinal(data);
	return bytePlainData;
}
