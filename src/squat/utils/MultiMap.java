package squat.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class MultiMap<K,V> {
	private Map<K,List<V>> map;
	
	public MultiMap() {
		map = new HashMap<K, List<V>>();
	}
	
	public void put(K key, V value) {
		List<V> values = map.get(key);
		if(values == null) {
			values = new ArrayList<V>();
		}
		values.add(value);
		map.put(key, values);
	}
	
	public List<V> get(K key) {
		return map.get(key);
	}
	
	public void remove(K key, V value) {
		List<V> values = map.get(key);
		for(Iterator<V> it = values.iterator(); it.hasNext();) {
			if(it.next() == value) {
				it.remove();
			}
		}
	}
}
