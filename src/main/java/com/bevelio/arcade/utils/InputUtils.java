package com.bevelio.arcade.utils;

public class InputUtils 
{
	public static Double getDouble(String str)
	{
		if(isDouble(str))
		{
			return Double.parseDouble(str);
		}
		return null;
	}
	
	public static boolean isDouble(String str)
	{
		try 
        {
           Double.parseDouble(str);
        } 
        catch (NumberFormatException nfe) 
        {
           return false;
        }
		return true;
	}

	public static double getDouble(Object object)
	{
		if(object instanceof String)
			return InputUtils.getDouble((String) object);
		else if(object instanceof Double)
			return (Double) object;
		return 0.0;
	}
}
