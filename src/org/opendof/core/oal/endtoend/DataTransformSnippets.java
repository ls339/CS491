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

public CipherInputStream useCipherInputStream(ByteArrayInputStream in, Cipher aesDecryptCipher, SecretKey sharedSecret, IvParameterSpec iv)
{
	try {
		//sharedSecret = receiverSharedSecret;
		aesDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //MUST specify an IV and distribute to both sides
		aesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret, iv); //iv is the saved IV from encoded public key method
		
		CipherInputStream cis = new CipherInputStream(in, aesDecryptCipher);
		
		return cis; //TODO what do we return?
	}
	catch(Exception e) {
		return null;
	}
}

//create ciphers in initialized method or constructor - class level private variables
public CipherOutputStream useCipherOutputStream(ByteArrayOutputStream os, Cipher aesEncryptCipher, SecretKey sharedSecret, IvParameterSpec iv)
{
	try {
		//sharedSecret = receiverSharedSecret;
		aesEncryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesEncryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret, iv); //iv is the saved IV from encoded public key method
		
		CipherOutputStream cos = new CipherOutputStream(os, aesEncryptCipher);
		
		return cos; //TODO what do we return?
	}
	catch(Exception e) {
		return null;
	}
} //TODO how to close stream?

//Data transform encapsulates the cipher streams
@Override
public byte[] transformSendData(DOFInterfaceID interfaceID, byte[] data)
{
	//receiverSharedSecret saved outside this method
	SecretKey sharedSecret = receiverSharedSecret;
	IvParameterSpec iv = savedIvSpec;
	ByteArrayOutputStream os;
	Cipher aesEncryptCipher;
	CipherOutputStream cos;

	cos = useCipherOutputStream(os, aesEncryptCipher, sharedSecret, iv);
	
	byte[] byteCipherData = data;
	cos.write(byteCipherData);
	cos.close(); //this calls doFinal()
	return byteCipherData;
}

@Override
public byte[] transformReceiveData(DOFInterfaceID interfaceID, byte[] data)
{
	//receiverSharedSecret saved outside this method
	SecretKey sharedSecret = receiverSharedSecret;
	IvParameterSpec iv = savedIvSpec;
	ByteArrayInputStream is;
	Cipher aesDecryptCipher;
	CipherInputStream cis;

	cis = useCipherInputStream(is, aesDecryptCipher, sharedSecret, iv);

	//TODO - doFinal not called with stream input Cipher??
	byte[] bytePlainData;
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	while(cis.read(data) != -1)
	{
		outputStream.write(cis.read(data));
	}
	bytePlainData = outputStream.toByteArray();
	return bytePlainData;
}
