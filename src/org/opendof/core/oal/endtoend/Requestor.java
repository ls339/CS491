package org.opendof.core.oal.endtoend;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    DOFObject.SessionOperationListener operationListener;
    
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
            DOFResult<DOFValue> otherResult;
            if(currentProvider != null)
            {
            	// end-to-end
            	SessionObject = currentProvider.beginSession(TBAInterface.DEF, ETEInterface.IID, operationListener);

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
    public void SEND_ENCODED_PUB_KEY(KeyAgreement myKeyAgreement) 
    		throws NoSuchAlgorithmException,InvalidParameterSpecException, 
    		InvalidAlgorithmParameterException, InvalidKeyException {
        try{
        	DHParameterSpec dhSkipParamSpec;
        	AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
        	paramGen.init(2048); 
        	AlgorithmParameters params = paramGen.generateParameters(); 
        	dhSkipParamSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class); 
        	KeyPairGenerator requestorKpairGen = KeyPairGenerator.getInstance("DH");
        	requestorKpairGen.initialize(dhSkipParamSpec);
        	KeyPair requestorKpair = requestorKpairGen.generateKeyPair();
        	myKeyAgreement.init(requestorKpair.getPrivate());
        	
        	/* Create the 16 byte IV 
        	    byte[] iv = new byte[16];
                SecureRandom random = new SecureRandom();
                random.nextBytes(iv);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv); //IV needs to be initialized outside of this method - private variable maybe?
        	*/
        	
        	DOFBlob BlobPubKey = new DOFBlob(requestorKpair.getPublic().getEncoded()); //this creates a 256 byte array - find out the exact size if not 256
        	DOFBlob InitVector = new DOFBlob(requestorKpair.getPublic().getEncoded()); // Placeholder

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
    // By Saad,
    // Once you reciev a response Blob from provider, we need to extract the public key from it for keyagreement
    //Input: Provider's EncodedPubkey as a byte[]
    //Output: provider's PubKey
    //Calrify how we are going to save/return the providerPubKey
    public PublicKey getproviderPubKey(byte[] providerPubKeyEnc)
    {
        
          KeyFactory requestorKeyFac = KeyFactory.getInstance("DH"); //Get Key specifications from key
          X509EncodedKeySpec x509KeySpec1 = new X509EncodedKeySpec(providerPubKeyEnc); //Create Key
          PublicKey providerPubKey = requestorKeyFac.generatePublic(x509KeySpec1); //Get public key
        
    }
    
    // Seperating Keyagreement logic and sending keys as discussed with louis
    //Clarify if this should be part of some driving program
    KeyAgreement requestorKeyAgree = KeyAgreement.getInstance("DH"); //Create a key exchange Agreement of the "DH" parameter
    SEND_ENCODED_PUB_KEY(requestorKeyAgree); // At this point requestorKeyAgree is populated
    PublicKey providerPubKey= getproviderPubKey(ProviderPubKeyBlob); // Pass the ProviderPubKeyblod to the the function that extracts public key from it
    requestorKeyAgree.doPhase(providerPubKey, true); //Pass the providerPubKey to the KeyAgreement
     
    //Input: a RequesterKeyagree parameter that has successfully intitated the do-phase of agreement
    //Output: a byte array containing the shared key
    public byte[] gen_shared_secret(byte[] requestorKeyAgree) {
        
         byte[] requestorSharedSecret = requestorKeyAgree.generateSecret();
         return requesterSharedSecret; 
    }
    
    /*public byte[] gen_shared_secret(byte[] encPubKey) {
    	// Decode pub key
    	// Key agreement do phase
    	// generate shared secret
    	// return shared secret
    	return new byte[0]; // Place holder
    } */
    
    public void init_data_transform() {
    	
    	
    }
    public void sendBeginGetRequest() {
    	activeGetOperation = broadcastObject.beginGet(TBAInterface.PROPERTY_ALARM_ACTIVE, TIMEOUT, new GetListener());
    }
    
    public void sendBeginSetRequest(boolean _active) {
    	DOFBoolean setValue = new DOFBoolean(_active);
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
