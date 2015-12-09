package org.opendof.core.oal.endtoend;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// ETE
import javax.crypto.spec.*;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.KeyPair;
import javax.crypto.KeyAgreement;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyPairGenerator;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.opendof.core.oal.endtoend.TBAInterface;
import org.opendof.core.oal.endtoend.TrainingUI;
import org.opendof.core.oal.DOFErrorException;
import org.opendof.core.oal.DOFException;
import org.opendof.core.oal.DOFInterestLevel;
import org.opendof.core.oal.DOFInterfaceID;
import org.opendof.core.oal.DOFObject;
import org.opendof.core.oal.DOFObjectID;
import org.opendof.core.oal.DOFOperation;
import org.opendof.core.oal.DOFProviderException;
import org.opendof.core.oal.DOFProviderInfo;
import org.opendof.core.oal.DOFQuery;
import org.opendof.core.oal.DOFResult;
import org.opendof.core.oal.DOFSystem;
import org.opendof.core.oal.DOFType;
import org.opendof.core.oal.DOFValue;
import org.opendof.core.oal.DOFOperation.Query;
import org.opendof.core.oal.security.DOFSecurityException;
import org.opendof.core.oal.value.DOFBlob;
import org.opendof.core.oal.value.DOFBoolean;
import org.opendof.core.oal.value.DOFDateTime;

public class Requestor {

    TrainingUI parent; // <-- comment this out when turning off the gui
    DOFSystem mySystem;
    Map<String, DOFObject> objectMap = new HashMap<String, DOFObject>(2);
    DOFObject broadcastObject = null;
    DOFQuery query;
    DOFObject currentProvider = null;
    // For end-to-end
    DOFOperation.Session SessionObject = null; 
    //DOFObject.SessionOperationListener operationListener;
    SessionListener sessionListener;
    //GetListener getOpListener;
    
    DOFOperation.Get activeGetOperation = null;
    DOFOperation.Set activeSetOperation = null;
    DOFOperation.Invoke activeInvokeOperation = null;
    
    
    int TIMEOUT = 5000;
    
    public Requestor(DOFSystem _system, TrainingUI _parent){ // <-- comment this out when turning off the gui
    //public Requestor(DOFSystem _system){
        mySystem = _system;
        this.parent = _parent; // <-- comment this out when turning off the gui
        init();
    }
    
    private void init(){
        broadcastObject = mySystem.createObject(DOFObjectID.BROADCAST); 
        mySystem.beginInterest(TBAInterface.IID, DOFInterestLevel.WATCH);
        query = new DOFQuery.Builder()
            .addFilter(TBAInterface.IID)
            .build();
        mySystem.beginQuery(query, new QueryListener());
        SessionObject = broadcastObject.beginSession(TBAInterface.DEF, ETEInterface.IID, sessionListener);
    }
    
    public void setCurrentRequestor(String _oidString){
        currentProvider = objectMap.get(_oidString);  
    }
    
    public boolean sendSetRequest(boolean _active){
        try{
            DOFBoolean setValue = new DOFBoolean(_active);
            
            if(currentProvider != null)
            {
                currentProvider.set(TBAInterface.PROPERTY_ALARM_ACTIVE, setValue, TIMEOUT);
                return true;
            }
            return false;
        } catch (DOFProviderException e) {
            return false;
        } catch (DOFErrorException e) {
            return false;
        } catch (DOFException e) {
            return false;
        }
    }
    
    public Boolean sendGetRequest() {
        /* 
         * Begin Secure end-to-end session
    	 * SessionObject = currentProvider.beginSession(iface, sessionType) 
         * public DOFOperation.Session beginSession(DOFInterface iface, DOFInterfaceID sessionType, int timeout, SessionOperationListener operationListener)
         * { return oalObject.beginSession(iface, sessionType, timeout, operationListener); }
         */
        try{
            DOFResult<DOFValue> myResult;
            //DOFResult<DOFValue> otherResult;
            if(currentProvider != null)
            {
            	// end-to-end
            	//SessionObject = currentProvider.beginSession(TBAInterface.DEF, ETEInterface.IID, operationListener);
            	myResult = currentProvider.get(TBAInterface.PROPERTY_ALARM_ACTIVE, TIMEOUT);
                return myResult.asBoolean();
            }
            return null;
        } catch(DOFProviderException e){
            return null;
        } catch (DOFErrorException e) {
            return null;
        } catch (DOFException e) {
            return null;
        }
    }
    
    public Boolean sentInvokeRequest(Date _alarmTime) {
        try{
            DOFDateTime alarmTimeParameter = new DOFDateTime(_alarmTime); 
            if(currentProvider != null)
            {
                DOFResult<List<DOFValue>> myResults = currentProvider.invoke(TBAInterface.METHOD_SET_NEW_TIME, TIMEOUT, alarmTimeParameter);        
                List<DOFValue> myValueList = myResults.get();
                return DOFType.asBoolean(myValueList.get(0));
            }
            return null;
        } catch(DOFProviderException e){
            return null;
        } catch (DOFErrorException e) {
            return null;
        } catch (DOFException e) {
            return null;
        }
    }
   
    // ETE SEND_ENCODED_PUB_KEY Method
    
    /**
     * To send a PublicKey to the provider and recieve their PublicKey.
     * @param  myKeyAgreement A KeyAgreement parameter that has yet to undergo the do-phase.
     * @throws NoSuchAlgorithmException the cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidParameterSpecException invalid parameter specifications
     * @throws InvalidAlgorithmParameterException invalid or inappropriate algorithm parameters
     * @throws InvalidKeyException invalid PublickKey 
     * @throws InvalidKeySpecException invalid key specifications
     * @return a PublicKey recieved from the provider
     */
    public PublicKey send_key(KeyAgreement myKeyAgreement) 
    		throws NoSuchAlgorithmException,InvalidParameterSpecException, 
    		InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {
        try{
        	DHParameterSpec dhSkipParamSpec;
        	AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
        	paramGen.init(1024);
        	AlgorithmParameters params = paramGen.generateParameters(); 
        	dhSkipParamSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class); 
        	KeyPairGenerator requestorKpairGen = KeyPairGenerator.getInstance("DH");
        	requestorKpairGen.initialize(dhSkipParamSpec);
        	KeyPair requestorKpair = requestorKpairGen.generateKeyPair();
        	myKeyAgreement.init(requestorKpair.getPrivate());
        	//byte[] requestorPubKeyEnc = requestorKpair.getPublic().getEncoded();
        	DOFBlob BlobPubKey = new DOFBlob(requestorKpair.getPublic().getEncoded());
        	DOFBlob InitVector = new DOFBlob(requestorKpair.getPublic().getEncoded()); // Placeholder
        	
        	if(currentProvider != null)
            {
                //DOFResult<List<DOFValue>> myResults = currentProvider.invoke(ETEInterface.SEND_ENCODED_PUB_KEY, TIMEOUT, BlobPubKey); 
                DOFResult<List<DOFValue>> myResults = currentProvider.invoke(ETEInterface.SEND_ENCODED_PUB_KEY, TIMEOUT, InitVector, BlobPubKey);
                List<DOFValue> myValueList = myResults.get();
                // Used to convert from blob back to PublicKey type and decode 
                byte[] encodedPubKey = DOFType.asBytes(myValueList.get(0));
                PublicKey pubKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(encodedPubKey));
                return pubKey;
            }
            //return null;
        } catch(DOFProviderException e){
            //return null;
        } catch (DOFErrorException e) {
            //return null;
        } catch (DOFException e) {
            //return null;
        }
        return null;
    }
    
     /**
     * To decode an encoded PublicKey
     * @param  encPubKey An encoded PublicKey
     * @throws NoSuchAlgorithmException the cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException invalid key specifications
     * @return a simple PublicKey
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
     * @throws Exception invalid PublickKey parameter
     * @return a byte array containing the shared secret.
     */
    public byte[] genSharedSecret(KeyAgreement myKeyAgreement, PublicKey pubKey) 
    		throws InvalidKeyException {   
    	myKeyAgreement.doPhase(pubKey, true);
        byte[] sharedSecret = myKeyAgreement.generateSecret();
        return sharedSecret; 
   }
    
    private Cipher savedEncryptCipher;//= DefaultDataTransform.createEncryptCipher(secKey, initializationVector);
    
    public final class DefaultDataTransform implements DOFOperation.Session.DataTransform 
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
        	// Need to get aesEncryptCipher from the DOFObject
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
        	
        	//return new byte[0]; // Placeholder
        }
        @Override
        public byte[] transformReceiveData(DOFInterfaceID interfaceID, byte[] data)
        {
        	// Need to get aesDecryptCipher from the DOFObject
        	//Cipher aesDecryptCipher = savedDecryptCipher;
        	//byte[] bytePlainData = aesDecryptCipher.doFinal(data);
        	//return bytePlainData;
        	return new byte[0]; // Placeholder
        }
    } 

    /*
    @Override
    public byte[] transformSendData(DOFInterfaceID interfaceID, byte[] data)  {
    	//receiverSharedSecret generated outside this method
    	//SecretKey sharedSecret = receiverSharedSecret;
    	//------------------------------------------------------------------
    	//use the cipher method to create the cipher and init AES encryption
    	CipherOutputStream cos = useCipherOutputStream(ByteArrayOutputStream os, Cipher aesEncryptCipher);
    	//TODO - doFinal not called with stream cipher?
    	//byte[] byteCipherData = aesEncryptCipher.doFinal(data); //convert to cipher data
    	//cos.write(byteCipherData); //write the cipher data to the cipher stream
    	//------------------------------------------------------------------
    	//now send the cipher text across the session (This occurs outside this method)
    	//return byteCipherData; //what do we return?
    	byte[] byteCipherData = cos.write(data);
    	return byteCipherData;
    }

    public void sendBeginGetRequest() {
    	activeGetOperation = broadcastObject.beginGet(TBAInterface.PROPERTY_ALARM_ACTIVE, TIMEOUT, new GetListener());
    }
    @Override
    public transformReceiveData(DOFInterfaceID interfaceID, byte[] data) 
    {
    	//receiverSharedSecret generated outside this method
    	//SecretKey sharedSecret = receiverSharedSecret;
    	//------------------------------------------------------------------
    	//use the cipher method to create the cipher and init AES encryption
    	CipherInputStream cis = useCipherInputStream(ByteArrayInputStream os, Cipher aesDecryptCipher);
    	//TODO - doFinal not called with stream Cipher??
    	//byte[] bytePlainData = aesDecryptCipher.doFinal(data); //convert cipher data to plain data
    	//cis.read(bytePlainData); //use the cipher stream to read the data
    	//------------------------------------------------------------------
    	//now send the decrypted data back to application (find out where this occurs)
    	//return bytePlainData;
    	byte[] bytePlainData = cis.read(data);
    	return bytePlainData;
    }
    */
    
    public void sendBeginSetRequest(boolean _active) {
    	DOFBoolean setValue = new DOFBoolean(_active);
    	//SessionObject = broadcastObject.beginSession(TBAInterface.DEF, ETEInterface.IID, sessionListener);
    	//SessionObject.setDataTransform(new DefaultDataTransform());
    	activeSetOperation = broadcastObject.beginSet(TBAInterface.PROPERTY_ALARM_ACTIVE, setValue, TIMEOUT, new SetListener()); 
            
               
    }
    
    public void sendBeginInvokeRequest(Date _alarmTime) {
    	List<DOFValue> parameters = new ArrayList<DOFValue>();
            DOFDateTime alarmTimeParameter = new DOFDateTime(_alarmTime);
            parameters.add(alarmTimeParameter);
            activeInvokeOperation = broadcastObject.beginInvoke(TBAInterface.METHOD_SET_NEW_TIME, parameters, TIMEOUT, new InvokeListener());
    }
    /*
    public void sendBeginInvokeRequest(Date _alarmTime) {
    	List<DOFValue> parameters = new ArrayList<DOFValue>();
            DOFDateTime alarmTimeParameter = new DOFDateTime(_alarmTime);
            parameters.add(alarmTimeParameter);
            
            activeInvokeOperation = broadcastObject.beginInvoke(TBAInterface.METHOD_SET_NEW_TIME, parameters, TIMEOUT, new InvokeListener());
    }
     */
    
    private class SessionListener implements DOFObject.SessionOperationListener
    {
    	@Override
    	public void complete(DOFOperation operation, DOFException exception) {
    		
    	}
    	@Override
    	public void sessionOpen(DOFOperation.Session operation, DOFProviderInfo providerInfo, DOFObject session, DOFException exception) {
    		
    	}
    }
    
    private class QueryListener implements DOFSystem.QueryOperationListener
    {

        @Override
        public void interfaceAdded(Query operation, DOFObjectID oid, DOFInterfaceID iid) {
            DOFObject providerObject = mySystem.createObject(oid);
            objectMap.put(oid.toStandardString(), providerObject);
        }

        @Override
        public void interfaceRemoved(Query operation, DOFObjectID oid, DOFInterfaceID iid) {
            /* This is called when the provider cancels any provide operation detected by the query. */
        }

        @Override
        public void providerRemoved(Query operation, DOFObjectID oid) {
            /* This is called when, due to the canceling of a provide operation, the provider no longer matches the query. */
            DOFObject providerObject = objectMap.get(oid.toStandardString());            
            if(providerObject != null)
                providerObject.destroy();
            objectMap.remove(oid.toStandardString());
        }

        @Override
        public void complete(DOFOperation operation, DOFException exception) {
        }
    }
        
    private class SetListener implements DOFObject.SetOperationListener {
        @Override
        public void setResult(DOFOperation.Set operation, DOFProviderInfo providerInfo, DOFException exception) {
            if(exception == null) {
                 	DOFObjectID providerID = providerInfo.getProviderID();
                 	String providerIDString = providerID.toStandardString();
                 	parent.displaySetResults(providerIDString); // <-- * 
             } else {
                 	//Handle the error.
             }
            
        }

        @Override
        public void complete(DOFOperation operation, DOFException ex) {
        }
    }

    private class GetListener implements DOFObject.GetOperationListener {

        @Override
        public void getResult(DOFOperation.Get operation, DOFProviderInfo providerInfo, DOFValue result, DOFException exception) {
        	if(exception == null) {
              	DOFObjectID providerID = providerInfo.getProviderID();
                 	String providerIDString = providerID.toStandardString();
                 
                 	Boolean unwrappedResult = DOFType.asBoolean(result); 
                 
                 	parent.displayGetResults(providerIDString, unwrappedResult);// <-- * 
             } else {
                 	//Handle the error.
             }
        };

        @Override
        public void complete(DOFOperation operation, DOFException ex) {
        }
    }

    private class InvokeListener implements DOFObject.InvokeOperationListener {

        @Override
        public void invokeResult(DOFOperation.Invoke operation, DOFProviderInfo providerInfo, List<DOFValue> result, DOFException exception) {
        	if(exception == null) {
              	DOFObjectID providerID = providerInfo.getProviderID();
                 	String providerIDString = providerID.toStandardString();
              
                 	Boolean unwrappedResult = DOFType.asBoolean(result.get(0));
                 	parent.displayInvokeResults(providerIDString, unwrappedResult); // <-- comment this out when turning off the gui
             } else {
                 	if(exception.getClass().equals(DOFProviderException.class)){
                     		DOFProviderException ex = (DOFProviderException) exception;
                     		int itemID = ex.getInterfaceException().getItemID();
                     		System.out.println("Received provider exception: " + itemID);
                 	} else if(exception instanceof DOFSecurityException){
                 
                 	}
                 	else if(exception.getClass().equals(DOFErrorException.class)){
                     		DOFErrorException ex = (DOFErrorException) exception;
                     		int errorCode = ex.getErrorCode();
                     		System.out.println("Received error exception: " + errorCode);
                 	} else {
                     		int errorCode = exception.getErrorCode();
                     		System.out.println("Received exception: " + errorCode);
                 	}
             }
        }

        @Override
        public void complete(DOFOperation operation, DOFException ex) {
        }


    }
}
