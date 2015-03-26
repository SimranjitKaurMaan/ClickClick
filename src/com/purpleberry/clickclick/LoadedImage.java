package com.purpleberry.clickclick;

import android.graphics.Bitmap;

public class LoadedImage
{

	Bitmap mBitmap;

	String mName;

	LoadedImage(Bitmap bitmap, String name)
	{
		mBitmap = bitmap;
		mName = name;
	}

	public Bitmap getBitmap()
	{
		return mBitmap;
	}

	public String getName()
	{
		return mName;
	}

}
