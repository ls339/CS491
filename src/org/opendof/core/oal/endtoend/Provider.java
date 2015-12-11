package org.opendof.core.oal.endtoend;

import javax.crypto.interfaces.DHPublicKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;

import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.spec.X509EncodedKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.util.Date;
import java.util.List;

import org.opendof.core.oal.DOFInterface;
import org.opendof.core.oal.DOFInterfaceID;
import org.opendof.core.oal.DOFNotSupportedException;
import org.opendof.core.oal.DOFObject;
import org.opendof.core.oal.DOFObjectID;
import org.opendof.core.oal.DOFOperation;
import org.opendof.core.oal.DOFSystem;
import org.opendof.core.oal.DOFType;
import org.opendof.core.oal.DOFValue;
import org.opendof.core.oal.DOFInterface.Method;
import org.opendof.core.oal.DOFInterface.Property;
import org.opendof.core.oal.DOFOperation.Provide;
import org.opendof.core.oal.value.DOFBlob;
import org.opendof.core.oal.value.DOFBoolean;
import org.opendof.core.oal.DOFRequest;

public class Provider {

    DOFSystem mySystem = null;
    DOFObject myObject = null;
    DOFObjectID myOID = null;
    String lastOp = "undefined";
    int delay = 0;
    byte[] sharedSecret;
    
    boolean isActive = false;
    Date alarmTime;
    
    // This cannot be defined here. DataTransform is not yet defined. -ls339
    //private DataTransform dataTransform = ETE_DATA_TRANSFORM; //data transform vars
    private SecretKey savedSecretKey;
    private IvParameterSpec savedIVSpec;
    private Cipher savedEncryptCipher;
    private Cipher savedDecryptCipher; //end data transform var
    
    public Provider(DOFSystem _system, String oidString){
        mySystem = _system;
        init(oidString);
    }
    
    private void init(String _oidString){
        myOID = DOFObjectID.create(_oidString);
        myObject = mySystem.createObject(myOID);
        myObject.beginProvide(TBAInterface.DEF, new ProviderListener()); 
    }
    
    public boolean getActive(){
        return isActive;
    }
    
    public void setActive(boolean _active){
        isActive = _active;
    }
    
    public Date getAlarmTime(){
        return alarmTime;
    }
    
    public void setAlarmTime(Date _alarmTime){
        alarmTime = _alarmTime;
    }
    
    public String getLastOperation(){
        if(lastOp!=null)
            return lastOp;
        else
            return "undefined";
    }
    
    public void setDelay(int _delay){
        delay = _delay;
    }
    
    /**
     * Decodes an encoded PublicKey.
     * @param  encPubKey An encoded PublicKey
     * @throws NoSuchAlgorithmException the cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException invalid key specifications
     * @return pubKey a simple PublicKey
     */
    public PublicKey decodePublicKey(byte[] encPubKey) 
    		throws NoSuchAlgorithmException, InvalidKeySpecException {
    	try {
    	KeyFactory myKeyFactory = KeyFactory.getInstance("DH");
    	X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(encPubKey);
    	PublicKey pubKey = myKeyFactory.generatePublic(x509KeySpec);
    	return pubKey;
    	} catch(NoSuchAlgorithmException e) {
    		return null;
    	} catch(InvalidKeySpecException e) {
    		return null;
    	}
    }
       
    /**
     * To generate a shared secret after performing do phase of the KeyAgreement on a PublicKey.
     * @param  myKeyAgreement A KeyAgreement parameter that has yet to undergo the do-phase.
     * @param  pubKey A PublicKey on which to perform the KeyAgreement.
     * @throws Exception invalid PublickKey 
     * @return a byte array containing the shared secret.
     */
    public byte[] genSharedSecret(KeyAgreement myKeyAgreement, PublicKey pubKey) 
    		throws InvalidKeyException {   
    	myKeyAgreement.doPhase(pubKey, true);
        byte[] sharedSecret = myKeyAgreement.generateSecret();
        return sharedSecret; 
   }
    
    // We cannot call this here, its being called before the class is defined - ls339
    //public eteDataTransform ETE_DATA_TRANSFORM = new Requestor.eteDataTransform();
    public final class eteDataTransform implements DOFOperation.Session.DataTransform 
    {
    	/**
    	 * Decrypts cipher.
    	 * @param sharedSecret generated shared secret.
    	 * @param iv initialization vector.
    	 * @return aesDecryptCiper decrypted cipher.
    	 */
        public Cipher createDecryptCipher(SecretKey sharedSecret, IvParameterSpec iv)
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
        /**
         * Encrypts cipher.
         * @param sharedSecret generated shared secret.
         * @param iv initialization vector.
         * @return aesEncryptCipher decrypted cipher.
         */
        public Cipher createEncryptCipher(SecretKey sharedSecret, IvParameterSpec iv)
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
        	try 
        	{ 
        		byte[] byteCipherData = aesEncryptCipher.doFinal(data);
        		return byteCipherData; 		
        	} catch (BadPaddingException e) {
        		return null;
        	} catch(IllegalBlockSizeException e) {
        		return null;
        	}
        	
        }
        
        @Override
        public byte[] transformReceiveData(DOFInterfaceID interfaceID, byte[] data)
        {
        	Cipher aesDecryptCipher = savedDecryptCipher;
        	try {
        		byte[] bytePlainData = aesDecryptCipher.doFinal(data);
            	return bytePlainData;
        	} catch(BadPaddingException e) {
        		return null;
        	} catch(IllegalBlockSizeException e) {
        		return null;
        	}	
        }
    } 
  
    private class ProviderListener extends DOFObject.DefaultProvider {
   
        @Override
        public void get(Provide operation, DOFRequest.Get request, Property property) {
            DOFBoolean myDOFBoolean = new DOFBoolean(isActive);
            
            if(delay > 0){
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                	request.respond(new DOFNotSupportedException("The specified item id is not supported by the specified interface."));
                    //Specific exceptions are now available and the constructor below is deprecated.
                    //request.respond(new DOFErrorException(DOFErrorException.APPLICATION_ERROR));
                }
            }
            
            request.respond(myDOFBoolean);
            lastOp = "get";
        }
        
        @Override
        public void set(Provide operation, DOFRequest.Set request, Property property, DOFValue value) {
            isActive = DOFType.asBoolean(value);
            
            if(delay > 0){
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                	request.respond(new DOFNotSupportedException("The specified item id is not supported by the specified interface."));
                    //Specific exceptions are now available and the constructor below is deprecated.
                    //request.respond(new DOFErrorException(DOFErrorException.APPLICATION_ERROR));
                }
            }
            
            request.respond();
            lastOp = "set";
        }
        
        @Override
        public void invoke(Provide operation, DOFRequest.Invoke request, Method method, List<DOFValue> parameters) {
            
        	if(method.getInterface().getInterfaceID() == ETEInterface.IID){
            	byte[] initVector = DOFType.asBytes(parameters.get(0));
            	byte[] encPubKey = DOFType.asBytes(parameters.get(1));
    
            	try {
            		KeyAgreement myKeyAgree = KeyAgreement.getInstance("DH");
            		PublicKey pubKey = decodePublicKey(encPubKey);
            		DHParameterSpec dhParamSpec = ((DHPublicKey)pubKey).getParams();
            		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DH");
            		keyPairGen.initialize(dhParamSpec);
            		KeyPair keyPair = keyPairGen.generateKeyPair();
            		myKeyAgree.init(keyPair.getPrivate());
            		DOFBlob BlobPubKey = new DOFBlob(keyPair.getPublic().getEncoded());
            		
            		request.respond(BlobPubKey);
            		
            		// Generate our shared secret.
            		sharedSecret = genSharedSecret(myKeyAgree,pubKey); 		
            		
            	} catch(NoSuchAlgorithmException e) {
            		// Need to handle exception
            	} catch(InvalidKeySpecException e) {
            		// Need to handle exception
            	} catch(InvalidAlgorithmParameterException e) {
            		// Need to handle exception
            	} catch(InvalidKeyException e) {
            		// Need to handle exception
            	}
        	}

        	alarmTime = DOFType.asDate(parameters.get(0));
            DOFBoolean myDOFBoolean = new DOFBoolean(isActive);
            
            if(delay > 0){
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                	request.respond(new DOFNotSupportedException("The specified item id is not supported by the specified interface."));
                    //Specific exceptions are now available and the constructor below is deprecated.
                    //request.respond(new DOFErrorException(DOFErrorException.APPLICATION_ERROR));
                }
            }
            
            request.respond(myDOFBoolean); 
            lastOp = "invoke";
        }
    }
}
