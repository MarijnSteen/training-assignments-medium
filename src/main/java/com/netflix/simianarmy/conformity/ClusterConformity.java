package com.netflix.simianarmy.conformity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ClusterConformity {

	private boolean isConforming;
    private boolean isOptOutOfConformity;
    
    public static final String IS_CONFORMING = "isConforming";
    public static final String CONFORMITY_RULES = "conformityRules";
    public static final String IS_OPTEDOUT = "isOptedOut";

	private final Map<String, Conformity> conformities = Maps.newHashMap();

	public Map<String, Conformity> getConformities() {
		return conformities;
	}

	public Collection<Conformity> getConformties() {
		return conformities.values();
	}

	public Conformity getConformity(ConformityRule rule) {
		Validate.notNull(rule);
		return conformities.get(rule.getName());
	}

	public ClusterConformity updateConformity(Conformity conformity) {
		Validate.notNull(conformity);
		conformities.put(conformity.getRuleId(), conformity);
		return this;
	}

	public void clearConformities() {
		conformities.clear();
	}

	public boolean isConforming() {
		return isConforming;
	}

	public void setConforming(boolean conforming) {
		isConforming = conforming;
	}
	
    public boolean isOptOutOfConformity() {
        return isOptOutOfConformity;
    }
    
    public void setOptOutOfConformity(boolean optOutOfConformity) {
        isOptOutOfConformity = optOutOfConformity;
    }
    
    public Map<String, String> getFieldToValueMap(){
    	Map<String, String> map = Maps.newHashMap();
    	putToMapIfNotNull(map, IS_CONFORMING, String.valueOf(isConforming));
        putToMapIfNotNull(map, IS_OPTEDOUT, String.valueOf(isOptOutOfConformity));
        List<String> ruleIds = Lists.newArrayList();
        for (Conformity conformity : getConformities().values()) {
            map.put(conformity.getRuleId(), StringUtils.join(conformity.getFailedComponents(), ","));
            ruleIds.add(conformity.getRuleId());
        }
        putToMapIfNotNull(map, CONFORMITY_RULES, StringUtils.join(ruleIds, ","));
    	return map;
    }
    
    public static ClusterConformity parseFieldToValueMap(Map<String, String> fieldToValue){
    	Validate.notNull(fieldToValue);
    	ClusterConformity clusterConformity = new ClusterConformity();
    	clusterConformity.setConforming(Boolean.parseBoolean(fieldToValue.get(IS_CONFORMING)));
    	clusterConformity.setOptOutOfConformity(Boolean.parseBoolean(fieldToValue.get(IS_OPTEDOUT)));
    	for (String ruleId : StringUtils.split(fieldToValue.get(CONFORMITY_RULES), ",")) {
            clusterConformity.updateConformity(new Conformity(ruleId,
                    Lists.newArrayList(StringUtils.split(fieldToValue.get(ruleId), ","))));
        }
    	return clusterConformity;
    }
    
    private static void putToMapIfNotNull(Map<String, String> map, String key, String value) {
        Validate.notNull(map);
        Validate.notNull(key);
        if (value != null) {
            map.put(key, value);
        }
    }

}
