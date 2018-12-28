package com.example.pleasestop.vkonkurse.Utils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.pleasestop.vkonkurse.R;
import com.example.pleasestop.vkonkurse.model.Competition;

import static com.example.pleasestop.vkonkurse.MyApp.getContext;

public class CustomBind {



    @BindingAdapter("bind:spannableText")
    public static void setSpannableText(final TextView textView, final Competition item) {
        textView.setText(item.getSpanText());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @BindingAdapter(value = {"bind:setImage", "bind:progress"}, requireAll = false)
    public static void setIcon (final ImageView imageView, String link, final ProgressBar progressBar) {
        if(link != null)
            Glide.with(getContext())
                    .load(link)
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_ab_app)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            imageView.setImageDrawable(resource);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
    }
}
