//Cipher input stream, cipher output streams instead of byte[]

/*
TODO --------> implement data transform interface: dofoperation.session.datatransform
Cipher aesEncryptCipher = Cipher.getInstance("AES");
Cipher aesDecryptCipher = Cipher.getInstance("AES"); 
aesEncryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
aesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret); //TODO find out if this is removable

workflow: est session - key neg - construct obj to gen ciphers - attach on both sides of session
*/

// requester - session operation returned, dofoperation.session returned when session created
// provider - dofrequest.session created when session request received. call setdatatransform here

//@Override | TODO Pass in aesDecryptCipher? | TODO pass as input shared secret? or no?
public useCipherInputStream(InputStream in, Cipher aesDecryptCipher)
{
	sharedSecret = receiverSharedSecret;
	aesDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //MUST specify an IV and distribute to both sides
	aesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret, iv); //iv is the saved IV from encoded public key method
	
	CipherInputStream cis = new CipherInputStream(in, aesDecryptCipher);
	
	return cis; //TODO what do we return?
} //TODO how to close stream?

//create ciphers in initialized method or constructor - class level private variables
//@Override | TODO what do we pass in?
public useCipherOutputStream(OutputStream os, Cipher aesEncryptCipher)
{
	sharedSecret = receiverSharedSecret;
	aesDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	aesDecryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret, iv); //iv is the saved IV from encoded public key method
	
	CipherOutputStream cos = new CipherOutputStream(os, aesEncryptCipher);
	
	return cos; //TODO what do we return?
} //TODO how to close stream?

@Override
public transformSendData(DOFInterfaceID interfaceID, byte[] data) //Data transform encapsulates the cipher streams
{
	//receiverSharedSecret generated outside this method
	//SecretKey sharedSecret = receiverSharedSecret;
	//------------------------------------------------------------------
	//use the cipher method to create the cipher and init AES encryption
	CipherOutputStream cos = useCipherOutputStream(OutputStream os, Cipher aesEncryptCipher);
	//TODO - doFinal not called with stream cipher?
	//byte[] byteCipherData = aesEncryptCipher.doFinal(data); //convert to cipher data
	//cos.write(byteCipherData); //write the cipher data to the cipher stream
	//------------------------------------------------------------------
	//now send the cipher text across the session (This occurs outside this method)
	//return byteCipherData; //what do we return?
	byte[] byteCipherData = cos.write(data);
	return byteCipherData;
}

@Override
public transformReceiveData(DOFInterfaceID interfaceID, byte[] data) 
{
	//receiverSharedSecret generated outside this method
	//SecretKey sharedSecret = receiverSharedSecret;
	//------------------------------------------------------------------
	//use the cipher method to create the cipher and init AES encryption
	CipherInputStream cis = useCipherInputStream(InputStream os, Cipher aesDecryptCipher);
	//TODO - doFinal not called with stream Cipher??
	//byte[] bytePlainData = aesDecryptCipher.doFinal(data); //convert cipher data to plain data
	//cis.read(bytePlainData); //use the cipher stream to read the data
	//------------------------------------------------------------------
	//now send the decrypted data back to application (find out where this occurs)
	//return bytePlainData;
	byte[] bytePlainData = cis.read(data);
	return bytePlainData;
}
