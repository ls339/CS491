Interface: End-to-end interface
	endToEndInterface() {
      	DOFinterface EndToEnd
      	DOFinterface.ID <- DOFinterfaceID.create()
      	EndToEnd.builder(IID)
	// Do some secure things in here
  }
	
Input: DOF interface is the default interface, end2endsec is the interface type  
Output: A session object providing a secure end-to-end connection
Function: Sets up an end-to-end secure session 
  	beginSession() {
  		// Assume begin provide on the other end has 
  		Previously agreed upon primeNumber & primeModulo
  		primeNumber ^ [privateKey] mod primeModulo = PublicKey(s)
  		PublicKey ^ [privateKey] mod primeModulo	= SharedKey			
  		ProviderGenerateKeyPair()	//Provider generates public and private key
  		DOFSendPubKeyToProvider() 
  				/* Provider sends public key to Requester
  				 * DOF takes object with keys
  				 */
  				//Requester receives public key				//DOF sends object with keys
  		RequesterGenerateKeyPair	//Requester generates private key
  				//Requester sends public key to Provider		//DOF sends object with key
 				//Both parties generate shared secret			//Diffie-Hellman achieved
  											//Parties begin Encryption
 				//Data Transformation occurs for all sent and received data
 				//Shared secret is used to transform future data	//Encryption and Decryption
 
 				//Requester ends the session
  }
  
Input: End2EndSessionOject
Output: void
Function: Destroys an end-to-end connection

    killSession() {
        destroy(End2EndSessionOject)
    }

/* Test app using our interface */
Input: integer n which represents time, DOFObject
Output: integer r ; 0 = success, 1 = failure
setAlarmTime() {
	DOFObject.beginSession()
	DOFObject.SendSetRequest(n)
		if exception
			throws exception
	not throw exception
}

//--------------------------------------------------------------------------------------------------------------------------

//Requestor creates an object for his eventual request
private void init()
{
	providerObject = mySystem.createObject(providerObjectID);	
}

//Provider sets up a listener for any requestors, as well as his object
private void init()
{
	myObject = mySystem.createObject(myObjectID);
	myObject.beginProvide(EndToEndSecurityInterface.DEF, new ProviderListener());  
}

//REQUESTOR sends a set request to set EndToEndSecurity as TRUE
public void sendSetRequest(boolean _security) {
	try{
	DOFBoolean value = new DOFBoolean(_security);
	providerObject.set(EndToEndSecurityInterface.PROPERTY_SECURITY_ACTIVE, booleanValue, TIMEOUT); //REQUESTOR's object, passes TRUE
	return true;
	catch{
		return false;
	}	
	}
}

//Provider receives the set request
public void setSecurity(boolean _security) {
	securityValue = _security;
}

//Once provider receives the TRUE value for EndToEndSecurity, start a Diffie Hellman Exchange
