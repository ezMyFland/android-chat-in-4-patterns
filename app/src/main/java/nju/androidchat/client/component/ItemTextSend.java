package nju.androidchat.client.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import lombok.Setter;
import nju.androidchat.client.R;

public class ItemTextSend extends LinearLayout implements View.OnLongClickListener {
    @StyleableRes
    int index0 = 0;

    private TextView textView;
    private Context context;
    private UUID messageId;
    private ImageView imageView;

    @Setter private OnRecallMessageRequested onRecallMessageRequested;

    public ItemTextSend(Context context, String text, UUID messageId, OnRecallMessageRequested onRecallMessageRequested) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_send, this);
        this.textView = findViewById(R.id.chat_item_content_text);
        this.messageId = messageId;
        this.onRecallMessageRequested = onRecallMessageRequested;

        this.imageView = findViewById(R.id.imageView);


        this.setOnLongClickListener(this);
        setText(text);
    }

    public ItemTextSend(Context context, String text, UUID messageId, OnRecallMessageRequested onRecallMessageRequested, boolean isImage) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_send, this);
        this.textView = findViewById(R.id.chat_item_content_text);
        this.imageView = findViewById(R.id.imageView);
        this.messageId = messageId;
        this.onRecallMessageRequested = onRecallMessageRequested;

        this.setOnLongClickListener(this);
        if (isImage) {
            setImageView(text);
            setText("");
        } else {
            setText(text);
        }
    }

    public void setImageView(String imageURL) {
        new Thread(){
            public void run(){
                Bitmap bitmap = getBitmap(imageURL);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }


    public static Bitmap getBitmap(String urlString) {
        URL url = null;
        Bitmap bitmap = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
        return bitmap;
    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定要撤回这条消息吗？")
                .setPositiveButton("是", (dialog, which) -> {
                    if (onRecallMessageRequested != null) {
                        onRecallMessageRequested.onRecallMessageRequested(this.messageId);
                    }
                })
                .setNegativeButton("否", ((dialog, which) -> {
                }))
                .create()
                .show();

        return true;


    }

}
