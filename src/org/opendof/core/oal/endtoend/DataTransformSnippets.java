//Cipher input stream, cipher output streams instead of byte[]

/*
-specific data transform interface
Cipher aesEncryptCipher = Cipher.getInstance("AES");
Cipher aesDecryptCipher = Cipher.getInstance("AES"); 
aesEncryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
aesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret); //TODO find out if this is removable
*/

//@Override | TODO Pass in aesDecryptCipher? | TODO pass as input shared secret? or no?
public CipherInputStream(InputStream in, Cipher aesDecryptCipher)
{
	sharedSecret = receiverSharedSecret;
	aesDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //MUST specify an IV and distribute to both sides
	aesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret);
	
	CipherInputStream cis = new CipherInputStream(in, aesDecryptCipher);
	
	return cis; //TODO what do we return?
} //TODO how to close stream?

//@Override | TODO what do we pass in?
public CipherOutputStream(OutputStream os, Cipher aesDecryptCipher)
{
	sharedSecret = receiverSharedSecret;
	aesDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	aesDecryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
	
	CipherOutputStream cos = new CipherOutputStream(in, aesDecryptCipher);
	
	return cos; //TODO what do we return?
} //TODO how to close stream?

@Override
public transformSendData(DOFInterfaceID interfaceID, byte[] data) //Data transform encapsulates the cipher streams
{
	//receiverSharedSecret generated outside this method
	SecretKey sharedSecret = receiverSharedSecret; //change to provider if provider
	
	//byte[] byteData = data.getBytes();
	byte[] byteCipherData = aesEncryptCipheresCipher.doFinal(data);
	
	//now send the cipher text across the session (This occurs outside this method)
	return byteCipherData;
}

@Override
public transformReceiveData(DOFInterfaceID interfaceID, byte[] data) 
{
	//receiverSharedSecret generated outside this method
	SecretKey sharedSecret = receiverSharedSecret; //change to provider if provider
	
	//byte[] byteCipherData = data.getBytes();
	byte[] bytePlainData = aesDecryptCipher.doFinal(data);
	
	//now send the decrypted data back to application (find out where this occurs)
	return bytePlainData;
}
