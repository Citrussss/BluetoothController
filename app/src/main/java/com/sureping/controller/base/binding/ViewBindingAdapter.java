package com.sureping.controller.base.binding;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.sureping.controller.R;

/**
 * @author sureping
 * @create 19-4-23.
 */
public class ViewBindingAdapter {

    @BindingAdapter("onTouch")
    public static void onTouch(View view, View.OnTouchListener listener) {
        view.setOnTouchListener(listener);
    }
    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (Patterns.WEB_URL.matcher(path).matches()) {
            Glide.with(view.getContext()).load(path)
                    .into(view);
        } else {
            RequestOptions options = new RequestOptions()
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            Glide.with(view.getContext())
                    .load(path)
                    .apply(options)
                    .into(view);
        }
    }
}
