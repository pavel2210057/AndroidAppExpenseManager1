package com.crazytrends.expensemanager.appBase.utils;


import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class CustomBindingAdapter {
    @BindingAdapter(requireAll = false, value = {"imageUrl", "placeholder"})
    public static void setImageUrl(ImageView imageView, String str, Drawable drawable) {
        if (str == null) {
            imageView.setImageDrawable(drawable);
        } else {
            Glide.with(imageView.getContext()).load(str).apply(new RequestOptions().fitCenter().centerCrop().error(drawable)).into(imageView);
        }
    }

    @BindingAdapter(requireAll = false, value = {"imageRes"})
    public static void setImage(ImageView imageView, int i) {
        imageView.setImageResource(i);
    }
}
