package com.ibm.hrl.scenoptic.utils;

public class ScenopticUtils {
	public static int getOptionalInt(Integer value) {
		if (value != null)
			return value;
		return 0;
	}

	public static long getOptionalLong(Long value) {
		if (value != null)
			return value;
		return 0;
	}

	public static float getOptionalFloat(Float value) {
		if (value != null)
			return value;
		return 0;
	}

	public static double getOptionalDouble(Double value) {
		if (value != null)
			return value;
		return 0;
	}

	public static String getOptionalString(String value) {
		if (value != null)
			return value;
		return "";
	}

	public static boolean getOptionalBoolean(Boolean value) {
		if (value != null)
			return value;
		return false;
	}
}
