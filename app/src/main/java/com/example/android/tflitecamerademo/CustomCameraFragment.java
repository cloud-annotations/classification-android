package com.example.android.tflitecamerademo;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.xlythe.fragment.camera.CameraFragment;
import com.xlythe.view.camera.CameraView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

public class CustomCameraFragment extends CameraFragment {
    private ImageClassifier mClassifier;

    private CameraView mCamera;
    private ImageView mPreview;
    private ViewGroup mCameraLayout;
    private ViewGroup mPreviewLayout;
    private Button mCapture;
    private Button mClose;

    private BottomSheetBehavior mBottomSheetBehavior;
    private ClassificationAdapter mAdapter;

    private boolean mShowCamera;

    private View.OnClickListener mBitmapCaptured = (view) -> {
        try {
            // Reflection.
            Field field = mCamera.getClass().getDeclaredField("mCameraView");
            field.setAccessible(true);
            TextureView textureView = (TextureView) field.get(mCamera);

            // Get the image and classify it.
            Bitmap bitmap = textureView.getBitmap();
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, ImageClassifier.DIM_IMG_SIZE_X, ImageClassifier.DIM_IMG_SIZE_Y);
            List<Classification> classifications = mClassifier.classifyFrame(thumbnail);
            mAdapter.updateClassifications(classifications);
            mPreview.setImageBitmap(bitmap);
            showPreview();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    };

    private View.OnClickListener mClosePreview = (view) -> showCamera();

    private void showPreview() {
        mShowCamera = false;
        mCameraLayout.setVisibility(View.GONE);
        mPreviewLayout.setVisibility(View.VISIBLE);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setState(STATE_EXPANDED);
    }

    private void showCamera() {
        mShowCamera = true;
        mCameraLayout.setVisibility(View.VISIBLE);
        mPreviewLayout.setVisibility(View.GONE);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(STATE_HIDDEN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowCamera = true;
        try {
            mClassifier = new ImageClassifier(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.fragment_custom_camera, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCamera = view.findViewById(R.id.camera);
        mPreview = view.findViewById(R.id.image_result);
        mCameraLayout = view.findViewById(R.id.layout_camera);
        mPreviewLayout = view.findViewById(R.id.layout_preview);
        mCapture = view.findViewById(R.id.capture_bitmap);
        mClose = view.findViewById(R.id.close_preview);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayout bottomSheet = getActivity().findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        RecyclerView recyclerView = bottomSheet.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ClassificationAdapter(new ArrayList<>());
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);

        mCapture.setOnClickListener(mBitmapCaptured);
        mClose.setOnClickListener(mClosePreview);
    }

    @Override
    public void onImageCaptured(File file) {}

    @Override
    public void onVideoCaptured(File file) {}

    @Override
    public void onStart() {
        super.onStart();
        if (mShowCamera) {
            showCamera();
        } else {
            showPreview();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClassifier.close();
    }
}