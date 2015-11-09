//This is the implementation of the Provider snippet
DOFObject baseObject = system.createObject(baseID);
baseProvide = sessionObject.beginProvide(KnownSessionType.DEF, DOF.TIMEOUT_NEVER, new SessionProvider(), null);

private class SessionProvider extends DOFObject.DefaultProvider{       
	@Override
	public void session(DOFOperation.Provide operation, DOFRequest.Session request, DOFObject object, DOFInterfaceID interfaceID, DOFObjectID sessionID, DOFInterfaceID sessionType){
		if(sessionType.equals(KnownSessionType.IID)){
			synchronized(this){
				if(sessionProvide == null){
					DOFObject sessionObject = system.createObject(sessionID);
					sessionProvide = sessionObject.beginProvide(KnownSessionType.DEF, DOF.TIMEOUT_NEVER, knownSessionProviderImplementation, null);
					
					//You would need to save the sessionObject to some kind of map or list to reference later. 
					
					request.respond();
				} else {
					DOFErrorException ex = new DOFErrorException(DOFErrorException.TOO_MANY);
					request.respond(ex);
				}
			}
		} else {
			DOFErrorException ex = new DOFErrorException(DOFErrorException.NOT_SUPPORTED);
			request.respond(ex);
		}
	}
	
	//new addition - Alex Xi - 11/8/15
	keyPairObject kpo = null; //key pair for provider
	byte[] requesterKey = null; //encoded key from requester
	byte[] providerSharedSecret = null;
	KeyAgreement providerKeyAgree = KeyAgreement.getInstance("DH");
	
	public void generateEncodedPublicKey() //params required?
	{
		DOFResult<List<DOFValue>> myResults = sessionObject.invoke(ETEInterface.METHOD_SEND_ENCODED_PUB_KEY_ID); //save provider's keys to an object - 1
		List<DOFValue> myValueList = myResults.get();
		kpo = myValueList.get(0); // should only be 1 result, the key pair object
		byte[] encodedKey = kpo.getPublic().getEncoded(); //get only the providers public key
		
		DOFObject returnObject = new DOFObject(); //send a DOFObject with key pair to interface for requester to get
		returnObject = kpo; //possibly redundant, DOF might be able to send non-DOFObjects
		sessionObject.set(ETEInterface.PROPERTY_PROVIDER_ENC_PUB_KEY, returnObject);
		
		providerKeyAgree.init(kpo.getPrivate());
		
		request.respond(); //response if necessary
	}
	
	/*
	 * I have the generation of the shared secret here because the way I passed the encoded public keys was not
	 * direct. I instead passed it to the interface as a property which the other party may then "pick up" when
	 * they wanted to use it. Also our previous method of using the interface to do the D-H exchange would have
	 * had us passing our private keys to the interface which may or may not be safe. It can always be changed.
	 */
	
	public generateSharedKey()
	{
		requesterEncodedPublicKey = sessionObject.get(ETEInterface.PROPERTY_REQUESTER_ENC_PUB_KEY); //get the encoded public key of requester
		
		KeyFactory providerKeyFac = KeyFactory.getInstance("DH");
		x509KeySpec = new X509EncodedKeySpec(requesterEncodedPublicKey); 
		
		PublicKey requesterPubKey = providerKeyFac.generatePublic(x509KeySpec); //decrypt the encoded public key
		
		providerKeyAgree.doPhase(requesterPubKey, true);
		
		providerSharedSecret = providerKeyAgree.generateSecret(); //End of DH, start of encrypting and decrypting
		
		request.respond(); //response if necessary
	} //end additions - Alex Xi - 11/8/15
	
	@Override
	public void sessionComplete(DOFOperation.Provide operation, DOFRequest.Session request, DOFObject object, DOFInterfaceID interfaceID, DOFObjectID sessionID, DOFInterfaceID sessionType){
		//You would need to retrieve the session Object create before
		sessionObject.destroy();
		sessionObject = null;
		sessionProvide = null;
	}
}

//This is the Requestor snippet
sessionOp = requestor.beginSession(BaseInterface.DEF, KnownSessionType.IID, sessionTimeout, new CustomSessionOperationListener(), null);

public class CustomSessionOperationListener implements SessionOperationListener
{
    @Override
    public void sessionOpen(Session operation, DOFProviderInfo providerInfo, DOFObject session, DOFException exception) {
			sessionObject = session;
			//Save the session Object 
			//Once this is called the normal 
        }
    }
	
	//new addition - Alex Xi - 11/8/15
	keyPairObject kpo = null; //requester's keys
	byte[] providerEncodedPublicKey = null;
	byte[] requesterSharedSecret = null; //the shared secret, name for reference only
	KeyAgreement requesterKeyAgree = KeyAgreement.getInstance("DH");
	
	public void generateEncodedPublicKey() //params required?
	{
		DOFResult<List<DOFValue>> myResults = sessionObject.invoke(ETEInterface.METHOD_SEND_ENCODED_PUB_KEY_ID); //save requester's keys to an object - 1
		List<DOFValue> myValueList = myResults.get();
		kpo = myValueList.get(0); // should only be 1 result, the key pair object
		byte[] encodedKey = kpo.getPublic().getEncoded(); //extract requester's encoded public key
		
		DOFObject returnObject = new DOFObject(); //send a DOFObject with key pair to interface for provider to get
		returnObject = kpo; //possibly redundant, DOF might be able to send non-DOFObjects
		sessionObject.set(ETEInterface.PROPERTY_REQUESTER_ENC_PUB_KEY, returnObject);
		
		requesterKeyAgree.init(kpo.getPrivate());
	}
	
	/*
	 * I have the generation of the shared secret here because the way I passed the encoded public keys was not
	 * direct. I instead passed it to the interface as a property which the other party may then "pick up" when
	 * they wanted to use it. Also our previous method of using the interface to do the D-H exchange would have
	 * had us passing our private keys to the interface which may or may not be safe. It can always be changed.
	 */
	
	public generateSharedKey()
	{
		providerEncodedPublicKey = sessionObject.get(ETEInterface.PROPERTY_PROVIDER_ENC_PUB_KEY); //get the encoded public key of provider
		
		KeyFactory requesterKeyFac = KeyFactory.getInstance("DH");
		x509KeySpec = new X509EncodedKeySpec(providerEncodedPublicKey); 
		
		PublicKey providerPubKey = requesterKeyFac.generatePublic(x509KeySpec); //decrypt the encoded public key
		
		requesterKeyAgree.doPhase(providerPubKey, true);
		
		requesterSharedSecret = requesterKeyAgree.generateSecret(); //End of DH, start of encrypting and decrypting
		
	} //end additions - Alex Xi - 11/8/15
	
    @Override
    public void complete(DOFOperation operation, DOFException exception) {
		if( operation.equals(sessionOp) ) {
            if( exception != null ) {
				//The sessionObject is not available any more.
                sessionObject = null;
            }
        }
    }
}