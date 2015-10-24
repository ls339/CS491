package org.opendof.core.oal.endtoend;

import java.util.Collection;
import java.util.HashMap;

import org.opendof.core.oal.DOF;
import org.opendof.core.oal.DOFSystem;

public class DOFAbstraction {

    DOF dof;
    HashMap<String, DOFSystem> systemMap = new HashMap<String, DOFSystem>(3);
    public DOFAbstraction(){
        dof = new DOF(new DOF.Config.Builder().build());
    }
    
    public DOFSystem createSystem(String _name){
        DOFSystem returnSystem;
        if(dof != null){
            returnSystem = dof.createSystem();
            systemMap.put(_name, returnSystem);
            return returnSystem;
        }
        return null;
    }
    
    public DOFSystem getSystem(String _name){
        return systemMap.get(_name);
    }
    
    public void destroy(){
        Collection<DOFSystem> systemList = systemMap.values(); 
        
        for(DOFSystem system: systemList){
            system.destroy();
        }
        dof.setNodeDown();
        dof.destroy();
    }
}
