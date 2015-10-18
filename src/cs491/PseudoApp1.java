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
Output: integer, 0 = success, 1 = failure
setAlarmTime() {
	DOFObject.beginSession()
	DOFObject.SendSetRequest(n)
		if exception
			throws exception
	return 0
}

//--------------------------------------------------------------------------------------------------------------------------

/* 1
 * This is the initialization of our end to end security interface by a PROVIDER and a REQUESTER
 */

//REQUESTER creates an object for his eventual request
private void init()
{
	providerObject = mySystem.createObject(providerObjectID);	
}

//PROVIDER sets up a listener for any requestors, as well as his object
private void init()
{
	myObject = mySystem.createObject(myObjectID);
	myObject.beginProvide(EndToEndSecurityInterface.DEF, new ProviderListener());  
}

//REQUESTER sends a set request to set EndToEndSecurity as TRUE
public void sendSetRequest(boolean _security) {
	try{
	DOFBoolean value = new DOFBoolean(_security);
	//REQUESTOR's object, passes TRUE
	providerObject.set(EndToEndSecurityInterface.PROPERTY_SECURITY_ACTIVE, booleanValue, TIMEOUT); 
	return true;
	}
	catch{
		return false;
	}	
}

//PROVIDER receives the set request
public void setSecurity(boolean _security) {
	securityValue = _security;
}

//Once PROVIDER receives the TRUE value for EndToEndSecurity, start a Diffie Hellman exchange
//TODO - transition from listening for gettings and setters to exchanging keys for Diffie Hellman

/* 2
 * This is the start of a Diffie-Hellman exchange by a PROVIDER and a REQUESTER
 */ 
 
//TODO - write pseudocode for exchaning keys via Diffie Hellman
 
	//Provider sends public key to Requester		//DOF takes object with keys
	//Requester receives public key				//DOF sends object with keys
  	//Requester generates private key
  	//Requester sends public key to Provider		//DOF sends object with key
 	//Both parties generate shared secret			//Diffie-Hellman achieved
  								//Parties begin Encryption
 	//Data Transformation occurs for all sent and received data
 	//Shared secret is used to transform future data	//Encryption and Decryption
 
/* 3
 * This is the start of the data transformation by a PROVIDER and a REQUESTER
 */ 

//TODO - write the data transform method
