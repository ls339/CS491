//Cipher input stream, cipher output streams instead of byte[]

public transformSendData(DOFInterfaceID interfaceID, byte[] data) 
{
	//receiverSharedSecret generated outside this method
	SecretKey sharedSecret = receiverSharedSecret; //change to provider if provider
	
	Cipher AesCipher = Cipher.getInstance("AES");
	
	byte[] byteData = data.getBytes();
	
	AesCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
	byte[] byteCipherData = AesCipher.doFinal(byteData);
	
	//now send the cipher text across the session (This occurs outside this method)
	return byteCipherData;
}

public transformReceiveData(DOFInterfaceID interfaceID, byte[] data) 
{
	//receiverSharedSecret generated outside this method
	SecretKey sharedSecret = receiverSharedSecret; //change to provider if provider
	
	Cipher AesCipher = Cipher.getInstance("AES");
	
	byte[] byteCipherData = data.getBytes();
	
	AesCipher.init(Cipher.DECRYPT_MODE, sharedSecret);
	byte[] bytePlainData = AesCipher.doFinal(byteCipherData);
	
	//now send the decrypted data back to application
	return bytePlainData;
}
