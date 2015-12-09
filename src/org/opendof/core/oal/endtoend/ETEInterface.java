package org.opendof.core.oal.endtoend;

import org.opendof.core.oal.DOFInterface;
import org.opendof.core.oal.DOFInterfaceID;
import org.opendof.core.oal.DOFObject;
import org.opendof.core.oal.DOFType;
import org.opendof.core.oal.value.DOFBlob;
import org.opendof.core.oal.value.DOFBoolean;
import org.opendof.core.oal.value.DOFDateTime;
import java.math.BigInteger;
import java.security.KeyPair;

import javax.crypto.spec.*;

/**
 * This interface is used to transmit an encoded public key for Diffie-Hellman key exchange.
 *
 */
public class ETEInterface {

	public static final DOFBlob.Type BLOB_KEY = new DOFBlob.Type(256, 256);
	public static final DOFBlob.Type INIT_VECTOR = new DOFBlob.Type(256, 256);
	
	public static final DOFInterface DEF;
	public static final DOFInterfaceID IID = DOFInterfaceID.create("[63:{53551070}]");

    public static final int PROPERTY_BLOB_KEY_ID = 1;
    public static final int METHOD_SEND_ENCODED_PUB_KEY_ID = 2;

/**
 * Used by a requestor to transmit an encoded public key to a provider.
 * 
 * @param INIT_VECTOR initialization vector.
 * @param BLOB_KEY the requestor encoded public key.
 * @return BLOB_KEY the provider encoded public key.
 */
    public static final DOFInterface.Method SEND_ENCODED_PUB_KEY; 
	
	static {
    	DEF = new DOFInterface.Builder(IID)
    			.addProperty(1, true, true, BLOB_KEY)
    			.addMethod(2, new DOFType[] { INIT_VECTOR, BLOB_KEY },new DOFType[] { BLOB_KEY }).build();

    	SEND_ENCODED_PUB_KEY = DEF.getMethod(METHOD_SEND_ENCODED_PUB_KEY_ID);

	}
}
