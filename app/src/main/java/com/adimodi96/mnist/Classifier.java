package com.adimodi96.mnist;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Classifier {

    private static final int IMG_HEIGHT = 28;
    private static final int IMG_WIDTH = 28;
    private static final int NUM_CLASSES = 10;
    private final String MODEL_FILE_NAME = "MNIST.tflite";
    private Interpreter interpreter;
    private Interpreter.Options interpreterOptions;
    private ByteBuffer imageTensor;
    private int[] imagePixels = new int[IMG_HEIGHT * IMG_WIDTH];

    private float[][] result = new float[1][NUM_CLASSES];

    Classifier(Activity activity) throws IOException {
        interpreterOptions = new Interpreter.Options();
        interpreter = new Interpreter(loadModelFile(activity), interpreterOptions);
        imageTensor = ByteBuffer.allocateDirect(4 * IMG_HEIGHT * IMG_WIDTH);
        imageTensor.order(ByteOrder.nativeOrder());
    }

    private static float convertPixel(int color) {
        return ((((color >> 16) & 0xFF) * 0.299f
                + ((color >> 8) & 0xFF) * 0.587f
                + (color & 0xFF) * 0.114f)) / 255.0f;
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public Result classify(Bitmap bitmap) {
        long startTime, endTime, timeTaken;
        setImageTensor(bitmap);
        startTime = SystemClock.uptimeMillis();
        interpreter.run(imageTensor, result);
        endTime = SystemClock.uptimeMillis();
        timeTaken = endTime - startTime;
        return new Result(result[0], timeTaken);
    }

    private void setImageTensor(Bitmap bitmap) {
        if (imageTensor == null) {
            return;
        }
        imageTensor.rewind();

        bitmap.getPixels(imagePixels, 0, IMG_WIDTH, 0, 0, IMG_WIDTH, IMG_HEIGHT);

        int pixel = 0;
        for (int i = 0; i < IMG_WIDTH; ++i) {
            for (int j = 0; j < IMG_HEIGHT; ++j) {
                int value = imagePixels[pixel++];
                imageTensor.putFloat(convertPixel(value));
            }
        }
    }
}
