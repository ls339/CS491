package org.opendof.core.oal.endtoend;

public class RequestorSnippet {
	// Steps Alice does to generate, encode and send public key to one another, to be done by saad

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
		private static final byte skip1024ModulusBytes[] = {
	        (byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	        (byte)0x4E, (byte)0x49, (byte)0xDB, (byte)0xCD,
	        (byte)0x20, (byte)0xB4, (byte)0x9D, (byte)0xE4,
	        (byte)0x91, (byte)0x07, (byte)0x36, (byte)0x6B,
	        (byte)0x33, (byte)0x6C, (byte)0x38, (byte)0x0D,
	        (byte)0x45, (byte)0x1D, (byte)0x0F, (byte)0x7C,
	        (byte)0x88, (byte)0xB3, (byte)0x1C, (byte)0x7C,
	        (byte)0x5B, (byte)0x2D, (byte)0x8E, (byte)0xF6,
	        (byte)0xF3, (byte)0xC9, (byte)0x23, (byte)0xC0,
	        (byte)0x43, (byte)0xF0, (byte)0xA5, (byte)0x5B,
	        (byte)0x18, (byte)0x8D, (byte)0x8E, (byte)0xBB,
	        (byte)0x55, (byte)0x8C, (byte)0xB8, (byte)0x5D,
	        (byte)0x38, (byte)0xD3, (byte)0x34, (byte)0xFD,
	        (byte)0x7C, (byte)0x17, (byte)0x57, (byte)0x43,
	        (byte)0xA3, (byte)0x1D, (byte)0x18, (byte)0x6C,
	        (byte)0xDE, (byte)0x33, (byte)0x21, (byte)0x2C,
	        (byte)0xB5, (byte)0x2A, (byte)0xFF, (byte)0x3C,
	        (byte)0xE1, (byte)0xB1, (byte)0x29, (byte)0x40,
	        (byte)0x18, (byte)0x11, (byte)0x8D, (byte)0x7C,
	        (byte)0x84, (byte)0xA7, (byte)0x0A, (byte)0x72,
	        (byte)0xD6, (byte)0x86, (byte)0xC4, (byte)0x03,
	        (byte)0x19, (byte)0xC8, (byte)0x07, (byte)0x29,
	        (byte)0x7A, (byte)0xCA, (byte)0x95, (byte)0x0C,
	        (byte)0xD9, (byte)0x96, (byte)0x9F, (byte)0xAB,
	        (byte)0xD0, (byte)0x0A, (byte)0x50, (byte)0x9B,
	        (byte)0x02, (byte)0x46, (byte)0xD3, (byte)0x08,
	        (byte)0x3D, (byte)0x66, (byte)0xA4, (byte)0x5D,
	        (byte)0x41, (byte)0x9F, (byte)0x9C, (byte)0x7C,
	        (byte)0xBD, (byte)0x89, (byte)0x4B, (byte)0x22,
	        (byte)0x19, (byte)0x26, (byte)0xBA, (byte)0xAB,
	        (byte)0xA2, (byte)0x5E, (byte)0xC3, (byte)0x55,
	        (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7
	    };

	    // The SKIP 1024 bit modulus
	    private static final BigInteger skip1024Modulus = new BigInteger(1, skip1024ModulusBytes);

	    // The base used with the SKIP 1024 bit modulus
	    private static final BigInteger skip1024Base = BigInteger.valueOf(2);
	    // ls
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

}
