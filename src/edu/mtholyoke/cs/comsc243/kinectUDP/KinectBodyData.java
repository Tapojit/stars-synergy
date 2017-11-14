package edu.mtholyoke.cs.comsc243.kinectUDP;

import processing.data.JSONArray;

public class KinectBodyData {
	JSONArray jarray;
	

	public int getPersonCount() {
		try {
			return jarray.size();			
		} catch (NullPointerException npe) {
			System.out.println("*** Warning malformed jarray***");
			return 0;
		}
	}

	/**
	 * returns null if the body number is out of range
	 * 
	 * @param i
	 * @return
	 */
	public Body getPerson(int i) {
		try {
			return new Body(jarray.getJSONObject(i));
		} catch (Exception e) {
			return null;
		}
	}
	

	String tmpJsonStr;
	public KinectBodyData(String jsonStr) {
		this.tmpJsonStr = jsonStr;
		jarray = JSONArray.parse(jsonStr);
	}


}
