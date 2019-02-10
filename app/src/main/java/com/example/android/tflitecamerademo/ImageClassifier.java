package com.example.android.tflitecamerademo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

class Classification implements Comparable<Classification> {
    String label;
    Float score;

    Classification(String label, float score) {
        this.label = label;
        this.score = score;
    }

    @Override
    public int compareTo(Classification o) {
        return o.score.compareTo(score);
    }
}

class ImageClassifier {
    private static final String TAG = "TfLiteCameraDemo";

    private static final String MODEL_PATH = "model_android/model.tflite";
    private static final String LABEL_PATH = "model_android/labels.json";

    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    static final int DIM_IMG_SIZE_X = 224;
    static final int DIM_IMG_SIZE_Y = 224;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private Interpreter mTensorFlowLite;
    private List<String> mLabelList;

    ImageClassifier(Context context) throws IOException {
        mTensorFlowLite = new Interpreter(loadModelFile(context));
        mLabelList = loadLabelList(context);
    }

    /** Classifies a frame from the preview stream. */
    List<Classification> classifyFrame(Bitmap bitmap) {
        if (mTensorFlowLite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.");
            return new ArrayList<>();
        }
        ByteBuffer buffer = convertBitmapToByteBuffer(bitmap);

        // The models output expects a shape of [1 x labels_size]
        float[][] _outputPointer_ = new float[1][mLabelList.size()];
        mTensorFlowLite.run(buffer, _outputPointer_);

        return scoresToClassificationList(_outputPointer_[0]);
    }

    void close() {
        mTensorFlowLite.close();
        mTensorFlowLite = null;
    }

    /** Reads label list from Assets. */
    private List<String> loadLabelList(Context context) throws IOException {
        List<String> labelList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(LABEL_PATH)))) {
            StringBuilder json = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                json.append(line);
            }
            JSONArray jsonarray = new JSONArray(json.toString());
            for (int i = 0; i < jsonarray.length(); i++) {
                labelList.add(jsonarray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return labelList;
    }

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /** Writes Image data into a {@code ByteBuffer}. */
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        buffer.order(ByteOrder.nativeOrder());
        buffer.rewind();

        int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        for (final int val : intValues) {
            buffer.putFloat((((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
            buffer.putFloat((((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
            buffer.putFloat((((val) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        }
        return buffer;
    }

    private List<Classification> scoresToClassificationList(float[] scores) {
        List<Classification> classifications = new ArrayList<>();

        int i = 0;
        for (String label : mLabelList) {
            classifications.add(new Classification(label, scores[i]));
            i++;
        }

        Collections.sort(classifications);
        return classifications;
    }
}
