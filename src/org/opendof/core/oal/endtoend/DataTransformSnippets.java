//Cipher input stream, cipher output streams instead of byte[]

Cipher aesEncryptCipher = Cipher.getInstance("AES");
Cipher aesDecryptCipher = Cipher.getInstance("AES"); 
AesEncryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
AesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret); //TODO find out if ant of this can be merged

@Override
public transformSendData(DOFInterfaceID interfaceID, byte[] data) 
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
