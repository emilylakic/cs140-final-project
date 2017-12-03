package project;

import java.util.Arrays;

public class Memory {
	public static final int DATA_SIZE = 512;
	private int[] data = new int[DATA_SIZE];
	
	int[] getData() {
		return data;
	}
	
	int getData(int index) {
		return data[index];
	}
	
	void setData(int index, int value) {
		data[index] = value;
	}
	
	void clearData() {
		for(int i = 0; i < data.length; i++) data[i] = 0;
	}
	
	int[] getData(int min, int max) {
		return Arrays.copyOfRange(data, min, max);
	}
}
