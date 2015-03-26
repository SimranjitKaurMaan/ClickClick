package com.purpleberry.clickclick;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends ArrayAdapter<LoadedImage>
{

	private Context mContext;

	private List<LoadedImage> photos;

	public ImageAdapter(Context context, List<LoadedImage> photos1)
	{
		super(context, R.layout.gridview_item, photos1);
		mContext = context;
		photos = photos1;
	}

	public void addPhoto(LoadedImage photo)
	{
		photos.add(photo);
	}

	public int getCount()
	{
		return photos.size();
	}

	public LoadedImage getItem(int position)
	{
		return photos.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, null);
			holder = new ViewHolder();
			holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
			holder.image = (ImageView) convertView.findViewById(R.id.imageView);
			convertView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nameLabel.setText(photos.get(position).getName());
		holder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		holder.image.setPadding(8, 8, 8, 8);
		holder.image.setImageBitmap(photos.get(position).getBitmap());
		return convertView;

	}

	public static class ViewHolder
	{
		ImageView image;

		TextView nameLabel;

	}

}
