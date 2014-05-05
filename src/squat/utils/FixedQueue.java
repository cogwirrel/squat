package squat.utils;

import java.util.ArrayList;
import java.util.List;

public class FixedQueue<T> {
	List<T> list;
	int maxSize;
	
	public FixedQueue(int maxSize) {
		this.list = new ArrayList<T>();
		this.maxSize = maxSize;
	}
	
	public void add(T item) {
		list.add(item);
		
		if(list.size() > maxSize) {
			list.remove(0);
		}
	}
	
	public T get(int index) {
		return list.get(index);
	}
	
	public int size() {
		return list.size();
	}
	
	public List<T> getList() {
		return list;
	}
}
