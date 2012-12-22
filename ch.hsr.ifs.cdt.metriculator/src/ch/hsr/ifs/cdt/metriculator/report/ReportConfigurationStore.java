package ch.hsr.ifs.cdt.metriculator.report;

import java.util.HashMap;

public class ReportConfigurationStore {

	HashMap<String, Object> store = new HashMap<String, Object>();
	
	public void set(Class<?> scope, String key, Object value){
		store.put(scope.getClass().getCanonicalName() + "." + key, value);
	}
	public Object get(Class<?> scope, String key, Object defaultValue){
		Object value = store.get(scope.getClass().getCanonicalName() + "." + key);
		return value == null ? defaultValue : value;
	}
	
}
