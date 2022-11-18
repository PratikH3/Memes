package com.example.memes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout bg;
    ImageView imageView;
    ProgressBar progressBar;
    Button btnNext;
    TextView title;
    TextView subreddit, tvError;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bg = findViewById(R.id.background);
        bg.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                loadMeme();
            }
        });

        btnNext = findViewById(R.id.nextBtn);
        btnNext.setOnClickListener(v -> loadMeme());

        loadMeme();
    }

    private void loadMeme(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://meme-api.herokuapp.com/gimme";

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);

        title = findViewById(R.id.title);
        subreddit = findViewById(R.id.subreddit);
        tvError = findViewById(R.id.tvError);

        subreddit.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        String urlString = response.getString("url");
                        String actualTitle = response.getString("title");
                        String actualSubreddit = response.getString("subreddit");
                        Glide.with(getApplicationContext()).load(urlString).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                tvError.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                imageView.setVisibility(View.VISIBLE);
                                subreddit.setVisibility(View.VISIBLE);
                                subreddit.setText(actualSubreddit.toUpperCase(Locale.ROOT));

                                if(!actualTitle.equals(actualSubreddit.toLowerCase())){
                                    title.setVisibility(View.VISIBLE);
                                    title.setText(actualTitle);
                                }

                                return false;
                            }
                        }).into(imageView);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to get Meme", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(this, "Failed to get Meme", Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);

    }
}