package com.example.qrcodev2.ui.slideshow;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.qrcodev2.R;
import com.example.qrcodev2.databinding.FragmentSlideshowBinding;
import com.google.zxing.Result;


public class SlideshowFragment extends Fragment {
    private int CAMERA_REQUEST_CODE = 101;
    private SlideshowViewModel slideshowViewModel;
    private FragmentSlideshowBinding binding;
    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private Button copyToClipBtn;
    private TextView scannResultTv;
    private CodeScanner codeScanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        scannerView = binding.scannerView;
        copyToClipBtn = binding.copy;
        scannResultTv = binding.resultTv;

//        codeScanner();

        final Activity activity = getActivity();

        mCodeScanner = new CodeScanner(activity, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannResultTv.setText(result.getText());
                        mCodeScanner.startPreview();
                    }
                });
            }
        });
        copyToClipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scannResultTv.getText().equals("")) return;
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", scannResultTv.getText());
                clipboard.setPrimaryClip(clip);
            }
        });

        mCodeScanner.startPreview();

//        scannerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mCodeScanner.startPreview();
//            }
//        });


        return root;
    }

    private void codeScanner() {
        final Activity activity = getActivity();
        assert activity != null;
        codeScanner = new CodeScanner(activity, scannerView);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}