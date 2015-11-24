package org.opendof.core.oal.endtoend;

import java.util.Date;
import java.util.List;


import java.util.ArrayList;
import java.util.HashMap;;
import java.util.Map;
// ETE
import java.math.BigInteger;
import java.security.KeyPair;
import javax.crypto.KeyAgreement;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyPairGenerator;
import javax.crypto.spec.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import org.opendof.core.oal.DOFNotSupportedException;
import org.opendof.core.oal.DOFObject;
import org.opendof.core.oal.DOFObjectID;
import org.opendof.core.oal.DOFSystem;
import org.opendof.core.oal.DOFType;
import org.opendof.core.oal.DOFValue;
import org.opendof.core.oal.DOFInterface.Method;
import org.opendof.core.oal.DOFInterface.Property;
import org.opendof.core.oal.DOFOperation.Provide;
import org.opendof.core.oal.value.DOFBoolean;
import org.opendof.core.oal.DOFRequest;

public class Provider {

    DOFSystem mySystem = null;
    DOFObject myObject = null;
    DOFObjectID myOID = null;
    String lastOp = "undefined";
    int delay = 0;
    
    boolean isActive = false;
    Date alarmTime;
    
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
    //By Saad, 
      public PublicKey decodePubKey(byte[] requestorPubKeyEnc)
    {
          KeyFactory providerKeyFac = KeyFactory.getInstance("DH");  //Get Key specifications from key
          X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(requestorPubKeyEnc); //Create Key
          PublicKey requestorPubKey = providerKeyFac.generatePublic(x509KeySpec); //Get public key
          return requestorPubKey;
        }
     
    
    
      // ETE SEND_ENCODED_PUB_KEY Method
      //Clarify about throws
      //Clarify about stuff after blob and how to return stuff to requestor
      //Passing keyAgreement and requestorPubKey so DH parameters can be intialized
    public void SEND_ENCODED_PUB_KEY(KeyAgreement myKeyAgreement,PublicKey requestorPubKey) 
    		throws NoSuchAlgorithmException,InvalidParameterSpecException, 
    		InvalidAlgorithmParameterException, InvalidKeyException {
        try{
            
            DHParameterSpec dhSkipParamSpec
            DHParameterSpec dhParamSpec = ((DHPublicKey) requestorPubKey).getParams(); //Get DH Param from public key
            KeyPairGenerator providerKpairGen = KeyPairGenerator.getInstance("DH");  //Generate a pair of key(i.e private-public pair) of the specified algorithm
            providerKpairGen.initialize(dhParamSpec); //Initialize the keypair to the DH parameter generated before
            KeyPair providerKpair = providerKpairGen.generateKeyPair(); //Create a key and assign it to the generator above
            myKeyAgree.init(providerKpair.getPrivate());
        	
        	DOFBlob BlobPubKey = new DOFBlob(providerKpair.getPublic().getEncoded()); //this creates a 256 byte array - find out the exact size if not 256

        	if(currentProvider != null)
            {
                DOFResult<List<DOFValue>> myResults = currentProvider.invoke(ETEInterface.SEND_ENCODED_PUB_KEY, TIMEOUT, InitVector, BlobPubKey);
                List<DOFValue> myValueList = myResults.get();
                //return DOFType.asBytes(myValueList.get(0));
                //return DOFType.asBoolean(myValueList.get(0));
                byte[] sharedSecret = gen_shared_secret(DOFType.asBytes(myValueList.get(0)));
            }
            //return null;
        } catch(DOFProviderException e){
            //return null;
        } catch (DOFErrorException e) {
            //return null;
        } catch (DOFException e) {
            //return null;
        }
    }
    
    KeyAgreement providerKeyAgree = KeyAgreement.getInstance("DH"); //Create a key exchange Agreement of the "DH" parameter
    SEND_ENCODED_PUB_KEY(providerKeyAgree,requestorPubKey); // At this point providerKeyAgree is populated
    providerKeyAgree.doPhase(requestorPubKey, true); //Pass the requestorrPubKey to the KeyAgreement
    
    
    //Input: a ProviderKeyagree parameter that has successfully intitated the do-phase of agreement
    //Output: a byte array containing the shared key
      public byte[] gen_shared_secret(byte[] providerKeyAgree) {
        
         byte[] providerSharedSecret = providerKeyAgree.generateSecret();
         return providerSharedSecret; 
    }
    
    //Data Transform begins here
    @Override
    public void setDataTransform(DataTransform dataTransform) {
        operation.setDataTransform(dataTransform);
    }
    
    private DataTransform dataTransform = ETE_DATA_TRANSFORM;
    private SecretKey secKey;
    private IvParameterSpec initializationVector;
    private Cipher savedEncryptCipher;
	private Cipher savedDecryptCipher;
    public static DefaultDataTransform ETE_DATA_TRANSFORM = new Requestor.DefaultDataTransform();
    public static final class DefaultDataTransform implements DataTransform 
    {
    	public Cipher createDecryptCipher(Cipher inCipher, SecretKey sharedSecret, IvParameterSpec iv)
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
    	public Cipher createEncryptCipher(Cipher inCipher, SecretKey sharedSecret, IvParameterSpec iv)
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
    } //End of Data Transform
    
    
    private class ProviderListener extends DOFObject.DefaultProvider {
    	/*
         * Trap BeginSession here
         * Override DefaultProvider to trap session callback
         * Intercept begin provide
         * if(end-to-end security was requested) then
       	 * 		Save original interface DEF requested
         *		Provide end-to-end secure beginProvide
         * 		myObject.beginProvide(end-to-end security);
         *		DH key negotiation
         * 		Return end-to-end secured session object with original interface request.
         * else 
         * Let OpenDOF handle the session 
         * 
         * JS mentioned that we can copy one of the below methods to use for our function.
         * 
         * 
         * Add a session call 
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
         */
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
        /*
        @Override
        public void invoke(Provide operation, DOFRequest.Invoke request, Method method, List<DOFValue> parameters) {
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
        */
        
        @Override
        public void invoke(Provide operation, DOFRequest.Invoke request, Method method, List<DOFValue> parameters) {
            // Figure out what method is being called if we have more than one method.
        	
        	byte[] key = DOFType.asBytes(parameters.get(0));
            
            // Do app logic
            PublicKey requestorPubKey= decodePubKey(BlobPubKey); //assuming we recieve requestorPubKey Blob here
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
            
            request.respond(myDOFBoolean); // respond with another blob
            lastOp = "invoke";
        }
    }
}
