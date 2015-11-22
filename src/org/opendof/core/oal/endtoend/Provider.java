package org.opendof.core.oal.endtoend;

import javax.crypto.interfaces.DHPublicKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.KeyAgreement;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.spec.X509EncodedKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.util.Date;
import java.util.List;

import org.opendof.core.oal.DOFNotSupportedException;
import org.opendof.core.oal.DOFObject;
import org.opendof.core.oal.DOFObjectID;
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
    
    public byte[] genSharedSecret(KeyAgreement myKeyAgreement, PublicKey pubKey) 
    		throws InvalidKeyException {   
    	myKeyAgreement.doPhase(pubKey, true);
        byte[] sharedSecret = myKeyAgreement.generateSecret();
        return sharedSecret; 
   }
    
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
        	
        	byte[] initVector = DOFType.asBytes(parameters.get(0));
        	byte[] encPubKey = DOFType.asBytes(parameters.get(1));
        	
            // Do app logic
        	//byte[] encodedPubKey = DOFType.asBytes(myValueList.get(0));
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
        		
        		byte[] sharedSecret = genSharedSecret(myKeyAgree,pubKey);
        		
        		// Data Transform at this point
        		
        	} catch(NoSuchAlgorithmException e) {
        		// Need to handle exception
        	} catch(InvalidKeySpecException e) {
        		// Need to handle exception
        	} catch(InvalidAlgorithmParameterException e) {
        		// Need to handle exception
        	} catch(InvalidKeyException e) {
        		// Need to handle exception
        	}
        	
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
            
            //request.respond(myDOFBoolean); // respond with another blob
            //request.respond(BlobPubKey);
            //lastOp = "invoke";
        }
    }
}
