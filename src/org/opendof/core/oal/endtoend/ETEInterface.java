package org.opendof.core.oal.endtoend;
// import open dof standard library & data transform interface
import org.opendof.core.oal.DOFInterface;
import org.opendof.core.oal.DOFInterfaceID;
import org.opendof.core.oal.DOFType;
import org.opendof.core.oal.value.DOFBlob;
import org.opendof.core.oal.value.DOFBoolean;
import org.opendof.core.oal.value.DOFDateTime;
import java.math.BigInteger;
import java.security.KeyPair;

import javax.crypto.spec.*;

public class ETEInterface {

	public static final DOFBlob.Type BLOB_KEY = new DOFBlob.Type(32, 32);
	
	public static final DOFInterface DEF;
	public static final DOFInterfaceID IID = DOFInterfaceID.create("[63:{53551070}]");

    public static final int PROPERTY_BLOB_KEY_ID = 1;
    public static final int METHOD_SEND_ENCODED_PUB_KEY_ID = 2;
    //public static final int METHOD_GEN_SHARED_SECRET_ID = 3; // Not needed
    //public static final int METHOD_DATA_TRANSFORM_ID = 4; // Not needed
    
    /*
	public static final DOFInterface.Property PROPERTY_ALARM_ACTIVE;
	public static final DOFInterface.Property PROPERTY_ALARM_TIME_VALUE;
	*/
	/*
	 * split this into two methods
	 * (Method 1)
	 * public static final DOFInterface.Method METHOD_KEY_PAIR_GEN; // end-to-end should return a keypair object
	 * public static final DOFInterface.Method INIT_KEY_AGREEMENT;
	 * public static final DOFInterface.Method ENCODE_PUB_KEY; // Send key up to encode
	 * (Method 2)
	 * public static final DOFInterface.Method DECODE_PUB_KEY; // return public key
	 * public static final DOFInterface.Method GET_DH_PARAM_SPECS;
	 * public static final DOFInterface.Method KEY_AGREEMENT_DO_PHASE;
	 * public static final DOFInterface.Method GEN_SHARED_SECRET;
	 */

	public static final DOFInterface.Method SEND_ENCODED_PUB_KEY; // Method 1
	//public static final DOFInterface.Method GEN_SHARED_SECRET; // Method 2
	//public static final DOFInterface.Method DATA_TRANSFORM; 
	
	static {
    	DEF = new DOFInterface.Builder(IID)
    			.addProperty(1, true, true, BLOB_KEY)
    			.addMethod(2, new DOFType[] { BLOB_KEY },new DOFType[] { BLOB_KEY }).build(); //one type for IV, one for shared Key
    			//.addMethod(3, null, null)
    			//.addMethod(4, new DOFType[] { BLOB_KEY }, null).build();

    	SEND_ENCODED_PUB_KEY = DEF.getMethod(METHOD_SEND_ENCODED_PUB_KEY_ID);
    	//GEN_SHARED_SECRET = DEF.getMethod(METHOD_GEN_SHARED_SECRET_ID);
    	//DATA_TRANSFORM = DEF.getMethod(METHOD_DATA_TRANSFORM_ID);
	}
}
