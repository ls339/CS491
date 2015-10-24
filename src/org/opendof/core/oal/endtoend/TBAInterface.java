package org.opendof.core.oal.endtoend;

import org.opendof.core.oal.DOFInterface;
import org.opendof.core.oal.DOFInterfaceID;
import org.opendof.core.oal.DOFType;
import org.opendof.core.oal.value.DOFBoolean;
import org.opendof.core.oal.value.DOFDateTime;

public class TBAInterface {

	public static final DOFType IS_ACTIVE = DOFBoolean.TYPE;
	public static final DOFType ALARM_TIME = DOFDateTime.TYPE;
	
	public static final DOFInterface DEF;
	public static final DOFInterfaceID IID = DOFInterfaceID.create("[1:{01000034}]");

	public static final int PROPERTY_ALARM_ACTIVE_ID = 1;
	public static final int PROPERTY_ALARM_TIME_VALUE_ID = 2;
	public static final int METHOD_SET_NEW_TIME_ID = 3;
	public static final int EVENT_ALARM_TRIGGERED_ID = 4;
	public static final int EXCEPTION_BAD_TIME_VALUE_ID = 5;

	public static final DOFInterface.Property PROPERTY_ALARM_ACTIVE;
	public static final DOFInterface.Property PROPERTY_ALARM_TIME_VALUE;
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
    	METHOD_SET_NEW_TIME = DEF.getMethod(METHOD_SET_NEW_TIME_ID);
    	EVENT_ALARM_TRIGGERED = DEF.getEvent(EVENT_ALARM_TRIGGERED_ID);
        EXCEPTION_BAD_TIME_VALUE = DEF.getException(EXCEPTION_BAD_TIME_VALUE_ID);
	}
}
