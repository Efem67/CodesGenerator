package com.example.qrcodev2.ui.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcodev2.R;
import com.example.qrcodev2.databinding.FragmentGalleryBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    private ImageView brCodeIV;
    private EditText dataEdt;
    private Button generateBrBtn;
    private Button  idBtnSaveFile;
    private long currentTimeMillis;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        brCodeIV = binding.idIVBrcode;
        dataEdt = binding.idEdt;
        generateBrBtn = binding.idBtnGenerateBR;
        idBtnSaveFile = binding.idBtnSaveFile;

        generateBrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataEdt.getText().toString().equals("")) return;
                Code128Writer writer = new Code128Writer();
                try {
                    BitMatrix bitMatrix = writer.encode(String.valueOf(dataEdt.getText()), BarcodeFormat.CODE_128, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    brCodeIV.setImageBitmap(bmp);
                    brCodeIV.setTag("jest");

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        idBtnSaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(brCodeIV.getTag().equals("break")){
                    Log.d("esek", String.valueOf(brCodeIV.getTag()));
                }else {
                    brCodeIV.buildDrawingCache();
                    Bitmap bmp = brCodeIV.getDrawingCache();
                    File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //context.getExternalFilesDir(null);
                    currentTimeMillis = System.currentTimeMillis();
                    File file = new File(storageLoc, currentTimeMillis + ".jpg");

                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        scanIntent.setData(Uri.fromFile(file));
                        getContext().sendBroadcast(scanIntent);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}