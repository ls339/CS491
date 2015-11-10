package org.opendof.core.oal.endtoend;

public class ProviderSnippet {
	
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
		
	 // Steps Provider does to generate, encode and send public key to one another, to be done by saad
	// 11/10/2015 Begining of Diffie helman Implementation by Saad
	
	
	 // Phase 0: DH parameter creation
	 // Provider gets and encoded public key from which it extracts the DH paramters used
	 // Now provider must create his keys using these extracted DH parameters
	 
	  //Set up for DH Parameter extraction 
	  //requestorPubKeyEnd = recieved.requestor; Assuming the encoded key recieved here
	  DHParameterSpec dhSkipParamSpec;
          KeyFactory providerKeyFac = KeyFactory.getInstance("DH");  //Get Key specifications from key
          X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(requestorPubKeyEnc); //Create Key
          PublicKey requestorPubKey = providerKeyFac.generatePublic(x509KeySpec); //Get public key
		
          DHParameterSpec dhParamSpec = ((DHPublicKey) requestorPubKey).getParams(); //Get DH Param from public key
		
	 
	 // Phase 1: Key Pair Generation, Key-Agree intialization and encoding public keys to be sent
         // Now DH Param have been obtained, generate keys
         
        
           System.out.println("provider: Generate DH keypair ..."); //remove or not ?
           KeyPairGenerator providerKpairGen = KeyPairGenerator.getInstance("DH");  //Generate a pair of key(i.e private-public pair) of the specified algorithm
           providerKpairGen.initialize(dhParamSpec); //Initialize the keypair to the DH parameter generated before
           KeyPair providerKpair = providerKpairGen.generateKeyPair(); //Create a key and assign it to the generator above
         
           //Key-Pair Agreement Intialization 
           KeyAgreement providerKeyAgree = KeyAgreement.getInstance("DH"); //Create a key exchange Agreement of the "DH" parameter
           providerKeyAgree.init(providerKpair.getPrivate()); //Initialize this key Agreement to the private part of requestor's keypair
		
	    // Provider encodes his public key, and sends it over to requestor.	
            byte[] providerPubKeyEnc = providerKpair.getPublic().getEncoded(); //encode the public part of provider's key as a byte stream
		
		
		
		
		
		
		//Steps bob does to generate, encode and send public key to one another, to be done by saad

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

}
