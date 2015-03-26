package com.purpleberry.clickclick;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.support.v7.app.ActionBarActivity;
// import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
// import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
// import android.graphics.Point;
// import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;

public class MainActivity extends ActionBarActivity
{

	public static final int TAKE_PHOTO_REQUEST = 0;

	public static final int MEDIA_TYPE_IMAGE = 1;

	protected Uri mMediaUri;

	private File mediaFile;

	private GridView mGridView;

	private TextView welcomeText;

	private ImageAdapter adapter;

	private File mediaStorageDir;

	private String appName;

	public List<LoadedImage> list = new ArrayList<LoadedImage>();

	private File[] imageList;

	private Bitmap bitmap;

	private Bitmap newBitmap;

	private String nameFile;

	private int Count = 0;

	private boolean flag_loading = true;

	private int screenWidth;

	private int screenHeight;

	private LoadedImage Img;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		initialiseViews();
		getScreenResolution();
		loadImages();

	}

	private void getScreenResolution()
	{
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenWidth = displaymetrics.widthPixels;
		screenHeight = displaymetrics.heightPixels;

	}

	private void initialiseViews()
	{
		appName = this.getString(R.string.app_name);
		welcomeText = (TextView) findViewById(R.id.empty);
		mGridView = (GridView) findViewById(R.id.grid_view);
		mGridView.setEmptyView(welcomeText);
		mGridView.setOnScrollListener(onEndlessScrollListener);

	}

	private EndlessScrollListener onEndlessScrollListener = new EndlessScrollListener()
	{

		@Override
		public void onLoadMore(int page, int totalItemsCount)
		{
			// Triggered only when new data needs to be appended to the list
			// Add whatever code is needed to append new items to your AdapterView

			customLoadMoreDataFromSD(totalItemsCount);

		}

		private void customLoadMoreDataFromSD(int totalItemsCount)
		{
			Log.d("ClickClick", "LoadMore");

			if (flag_loading == true)
			{
				loadImages();
			}

		}

	};

	private void setUpGridViews()
	{
		Collections.reverse(list);
		Log.d("click", "inside setupgridviews");
		adapter = new ImageAdapter(this, list);
		mGridView.setAdapter(adapter);

	}

	private void loadImages()
	{
		Log.d(appName, "inside loadImages");
		File image;
		mediaStorageDir = new File(Environment.getExternalStorageDirectory(), appName);
		Log.d("click", "load" + mediaStorageDir);
		imageList = mediaStorageDir.listFiles();
		if (imageList != null)
		{
			int limitCount = Count + 5;
			if (limitCount > imageList.length)
				limitCount = imageList.length;
			for (int i = Count; i < limitCount; i++)
			{
				try
				{
					image = imageList[i];
					Count++;
					Log.d("click", "inside loadIMages");
					bitmap = BitmapFactory.decodeStream(image.toURL().openStream());
					if (bitmap != null)
					{
						newBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true);
						Log.e("ClickClick", "Image" + Count + newBitmap);
						list.add(new LoadedImage(newBitmap, image.getName()));
						bitmap.recycle();

					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			setUpGridViews();
		}
	}

	/**
	 * Add image(s) to the grid view adapter.
	 * 
	 * @param list2
	 *            Array of LoadedImages List
	 */
	private void addImage(List<LoadedImage> list2)
	{
		Collections.reverse(list2);
		adapter = new ImageAdapter(this, list2);
		Log.e("ClickClick", "adapter notified");
		mGridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		flag_loading = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Take appropriate action for each action item click
		switch (item.getItemId())
		{
			case R.id.action_camera:
				clickPhoto();// Take Photo
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			// adding new image to the sdcard folder
			Bitmap mBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
			if (mBitmap != null)
			{
				newBitmap = Bitmap.createScaledBitmap(mBitmap, screenWidth, screenHeight, true);
				Log.e("ClickClick", "Image" + Count + newBitmap);
				Img = new LoadedImage(newBitmap, nameFile);
				list.add(Img);
				mBitmap.recycle();

			}
			else if (resultCode == RESULT_CANCELED)
			{
				Toast.makeText(this, "Sorry..there was an error!", Toast.LENGTH_LONG).show();
			}

			addImage(list);

		}
	}

	private void clickPhoto()
	{
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mMediaUri = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		if (mMediaUri == null)
		{
			// display an error
			Toast.makeText(MainActivity.this, "There was a problem accessing your device's external storage", Toast.LENGTH_LONG).show();
		}
		takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
		startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
	}

	private Uri getOutputMediaFile(int mediaType)
	{

		if (isExternalStorageAvailable())
		{

			mediaStorageDir = new File(Environment.getExternalStorageDirectory(), appName);

			if (!mediaStorageDir.exists())
			{
				if (!mediaStorageDir.mkdirs())
				{
					Log.d("ClickClick", "Failed to create directory");
					return null;
				}
			}

			Date now = new Date();
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

			String path = mediaStorageDir.getPath() + File.separator;
			if (mediaType == MEDIA_TYPE_IMAGE)
			{
				mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");

				nameFile = "IMG_" + timeStamp + ".jpg";
			}
			else
			{
				return null;
			}

			Log.d("ClickClick", "File :" + Uri.fromFile(mediaFile));
			return Uri.fromFile(mediaFile);
		}
		else
		{

			return null;
		}
	}

	// checking if external storage is available
	private boolean isExternalStorageAvailable()
	{
		String state = Environment.getExternalStorageState();

		if (state.equals(Environment.MEDIA_MOUNTED))
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	protected void onDestroy()
	{
		super.onDestroy();
		Log.d("inside", "onDestroy");

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}

}
