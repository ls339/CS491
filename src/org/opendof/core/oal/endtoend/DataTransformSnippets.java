//These three must be created before the key exchange
private DataTransform dataTransform = ETE_DATA_TRANSFORM;
private SecretKey secKey;
private IvParameterSpec initializationVector;
//These three must be created before the key exchange

public static DefaultDataTransform ETE_DATA_TRANSFORM = new Requestor.DefaultDataTransform();
public static final class DefaultDataTransform implements DataTransform 
{
    public static Cipher createDecryptCipher(SecretKey sharedSecret, IvParameterSpec iv)
    {
    	try {
    		Cipher aesDecryptCipher;
    		aesDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //MUST specify an IV and distribute to both sides
    		aesDecryptCipher.init(Cipher.DECRYPT_MODE, sharedSecret, iv); //iv is the saved IV from encoded public key method

    		return aesDecryptCipher;
    	}
    	catch(Exception e) {
    		return null;
    	}
    }
    //create ciphers in initialized method or constructor - class level private variables
    public static Cipher createEncryptCipher(SecretKey sharedSecret, IvParameterSpec iv)
    {
    	try {
    		Cipher aesEncryptCipher;
    		aesEncryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    		aesEncryptCipher.init(Cipher.ENCRYPT_MODE, sharedSecret, iv); //iv is the saved IV from encoded public key method
    
    		return aesEncryptCipher;
    	}
    	catch(Exception e) {
    		return null;
    	}
    }
    
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
} 
private Cipher savedEncryptCipher = DefaultDataTransform.createEncryptCipher(secKey, initializationVector);
private Cipher savedDecryptCipher = DefaultDataTransform.createDecryptCipher(secKey, initializationVector);
//Now call transformSendData and transformReceiveData - somehow
