package org.opendof.core.oal.endtoend;

import org.opendof.core.oal.DOFInterface;
import org.opendof.core.oal.DOFInterfaceID;
import org.opendof.core.oal.DOFType;
import org.opendof.core.oal.value.DOFBoolean;
import org.opendof.core.oal.value.DOFDateTime;
import java.math.BigInteger;
import java.security.KeyPair;

import javax.crypto.spec.*;


// This interface needs to become our end to end interface
public class ETEInterface {

	public static final DOFType IS_ACTIVE = DOFBoolean.TYPE;
	public static final DOFType ALARM_TIME = DOFDateTime.TYPE;
	
	public static final DOFInterface DEF;
	public static final DOFInterfaceID IID = DOFInterfaceID.create("[63:{53551070}]");

	public static final int PROPERTY_ALARM_ACTIVE_ID = 1;
	public static final int PROPERTY_ALARM_TIME_VALUE_ID = 2;
	// end-to-end Begin
    // The 1024 bit Diffie-Hellman modulus values used by SKIP
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
    private static final BigInteger skip1024Modulus
    = new BigInteger(1, skip1024ModulusBytes);

    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger skip1024Base = BigInteger.valueOf(2);

    
	public static final DHParameterSpec METHOD_KEY_PAIR_GEN_PARAM_SPEC = new DHParameterSpec(skip1024Modulus,skip1024Base);
    //public static final KeyPair METHOD_INIT_KEY_AGREEMENT_GET_PRIVATE;
	// end-to-end End
	
    public static final int METHOD_SET_NEW_TIME_ID = 3;
	public static final int EVENT_ALARM_TRIGGERED_ID = 4;
	public static final int EXCEPTION_BAD_TIME_VALUE_ID = 5;

	public static final DOFInterface.Property PROPERTY_ALARM_ACTIVE;
	public static final DOFInterface.Property PROPERTY_ALARM_TIME_VALUE;
	
	// end-to-end Begin
	public static final DOFInterface.Method METHOD_KEY_PAIR_GEN; // end-to-end should return a keypair object
	//public static final DOFInterface.Method INIT_KEY_AGREEMENT;
	//public static final DOFInterface.Method ENCODE_PUB_KEY;
	//public static final DOFInterface.Method DECODE_PUB_KEY; // return public key
	// Left of with SAAD here.
	//public static final DOFInterface.Method GET_DH_PARAM_SPECS;
	//public static final DOFInterface.Method KEY_AGREEMENT_DO_PHASE;
	//public static final DOFInterface.Method GEN_SHARED_SECRET;
	// end-to-end End
	public static final DOFInterface.Method METHOD_SET_NEW_TIME;
	public static final DOFInterface.Event EVENT_ALARM_TRIGGERED;
	public static final DOFInterface.Exception EXCEPTION_BAD_TIME_VALUE;
	
	
	static {
    	DEF = new DOFInterface.Builder(IID)
    			.addProperty(1, true, true, IS_ACTIVE)
    			.addProperty(2, false, true, ALARM_TIME)
    			.addMethod(3, new DOFType[] { ALARM_TIME }, new DOFType[] { IS_ACTIVE })
    			.addEvent(4, new DOFType[] {})
    			.addException(5, new DOFType[] {}).build();
    	

    	PROPERTY_ALARM_ACTIVE = DEF.getProperty(PROPERTY_ALARM_ACTIVE_ID);
    	PROPERTY_ALARM_TIME_VALUE = DEF.getProperty(PROPERTY_ALARM_TIME_VALUE_ID);
    	// end-to-end begin
    	METHOD_KEY_PAIR_GEN = DEF.
    	// end to end end
    	METHOD_SET_NEW_TIME = DEF.getMethod(METHOD_SET_NEW_TIME_ID);
    	EVENT_ALARM_TRIGGERED = DEF.getEvent(EVENT_ALARM_TRIGGERED_ID);
        EXCEPTION_BAD_TIME_VALUE = DEF.getException(EXCEPTION_BAD_TIME_VALUE_ID);
	}
}
