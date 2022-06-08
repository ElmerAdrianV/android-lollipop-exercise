package com.codepath.android.lollipopexercise.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.models.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";
    private Contact mContact;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvPhone;
    private View vPalette;
    private FloatingActionButton fabCall;
    private android.transition.Transition.TransitionListener mEnterTransitionListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        vPalette = findViewById(R.id.vPalette);
        fabCall=findViewById(R.id.fabCall);
        // Extract contact from bundle
        mContact = (Contact)getIntent().getExtras().getSerializable(EXTRA_CONTACT);

        // Use Glide to get a callback with a Bitmap which can then
        // be used to extract a vibrant color from the Palette.

        // Define an asynchronous listener for image loading
        CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                // TODO 1. Instruct Glide to load the bitmap into the holder.ivProfile profile image view
                Glide.with(DetailsActivity.this).load(mContact.getThumbnailDrawable()).centerCrop().into(ivProfile);
                //Glide.with(DetailsActivity.this).asBitmap().load(mContact.getThumbnailDrawable()).centerCrop().into(ivProfile);
                // TODO 2. Use generate() method from the Palette API to get the vibrant color from the bitmap
                // Set the result as the background color for holder.vPalette view containing the contact's name.
                vPalette.setBackgroundColor(Palette.generate(resource).getVibrantColor(0));

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // can leave empty
            }
        };

        // Instruct Glide to load the bitmap into the asynchronous target defined above
        Glide.with(this).asBitmap().load(mContact.getThumbnailDrawable()).centerCrop().into(target);

        // Fill views with data

        tvName.setText(mContact.getName());
        tvPhone.setText(mContact.getNumber());

        // Dial contact's number.
        // This shows the dialer with the number, allowing you to explicitly initiate the call.
        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mContact.getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        mEnterTransitionListener = new android.transition.Transition.TransitionListener() {
            @Override
            public void onTransitionStart(android.transition.Transition transition) {

            }

            @Override
            public void onTransitionEnd(android.transition.Transition transition) {
                enterReveal();
            }

            @Override
            public void onTransitionCancel(android.transition.Transition transition) {

            }

            @Override
            public void onTransitionPause(android.transition.Transition transition) {
                exitReveal();
            }

            @Override
            public void onTransitionResume(android.transition.Transition transition) {

            }

        };
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                exitReveal();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void enterReveal() {
        // previously invisible view
        final View myView = findViewById(R.id.fabCall);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();

    }
    void exitReveal() {
        // previously visible view
        final View myView = findViewById(R.id.fabCall);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
                // Finish the activity after the exit transition completes.
                supportFinishAfterTransition();
            }
        });

        // start the animation
        anim.start();
    }

}
