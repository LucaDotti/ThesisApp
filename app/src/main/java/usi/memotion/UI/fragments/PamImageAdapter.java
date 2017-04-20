package usi.memotion.UI.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by usi on 08/02/17.
 */

public class PamImageAdapter extends BaseAdapter {
    private int[] images;
    private Context context;

    public PamImageAdapter(int[] images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
//            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(images[position]);
        return imageView;

    }
}
