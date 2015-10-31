package org.opendof.core.oal.endtoend;

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
    }
}
