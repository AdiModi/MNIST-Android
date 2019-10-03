package com.adimodi96.mnist;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

//    private int ALL_PERMISSIONS_CODE = 197;
//
//    private String[] PERMISSIONS = {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//    };

    DrawView drawView;
    Button button_clear, button_predict;
    TextView textView_number, textView_probability, textView_time;
    Classifier classifier;
    long startTime, endTime, totalTime;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //Save Bitmap Images
    public void saveBitmap(Bitmap bitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File folder_path = new File(root + "/MNIST/Images");
        folder_path.mkdirs();

        String image_file_name = "Image" + SystemClock.uptimeMillis() + ".png";
        File image_file = new File(folder_path, image_file_name);
        if (image_file.exists())
            image_file.delete();

        try {
            FileOutputStream file_output_stream = new FileOutputStream(image_file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, file_output_stream);
            file_output_stream.flush();
            file_output_stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = findViewById(R.id.drawView);

        button_clear = findViewById(R.id.button_clear);
        button_predict = findViewById(R.id.button_predict);

        textView_number = findViewById(R.id.textView_number);
        textView_probability = findViewById(R.id.textView_probability);
        textView_time = findViewById(R.id.textView_time);

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clear();
                textView_number.setText("-");
                textView_probability.setText("-");
                textView_time.setText("-");
            }
        });

        button_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
//                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, ALL_PERMISSIONS_CODE);
//                } else {
//                    Bitmap bitmapLarge = drawView.getImage();
//                    Bitmap bitmap = Bitmap.createScaledBitmap(bitmapLarge, 28, 28, true);
//                    saveBitmap(bitmap);
//                    startTime = SystemClock.uptimeMillis();
//                    Result result = classifier.classify(bitmap);
//                    endTime = SystemClock.uptimeMillis();
//                    totalTime = endTime - startTime;
//                    textView_number.setText("" + result.getNumber());
//                    textView_probability.setText("" + result.getProbability());
//                    textView_time.setText(totalTime + " ms");
//                }
                startTime = SystemClock.uptimeMillis();

                Bitmap bitmapLarge = drawView.getImage();
                Bitmap bitmap = Bitmap.createScaledBitmap(bitmapLarge, 28, 28, true);

                Result result = classifier.classify(bitmap);
                endTime = SystemClock.uptimeMillis();
                totalTime = endTime - startTime;

                textView_number.setText("" + result.getNumber());
                textView_probability.setText("" + result.getProbability());
                textView_time.setText(totalTime + " ms");

            }
        });

        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to Load Classifier Model", Toast.LENGTH_LONG).show();
        }
    }
}
