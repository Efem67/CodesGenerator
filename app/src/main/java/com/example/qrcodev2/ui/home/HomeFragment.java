package com.example.qrcodev2.ui.home;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcodev2.R;
import com.example.qrcodev2.databinding.FragmentHomeBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class HomeFragment extends Fragment {
    private ImageView qrCodeIV;
    private EditText dataEdt;
    private Button generateQrBtn;
    private Button  idBtnSaveFile;
    private long currentTimeMillis;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        qrCodeIV = binding.idIVQrcode;
        dataEdt = binding.idEdt;
        generateQrBtn = binding.idBtnGenerateQR;
        idBtnSaveFile = binding.idBtnSaveFile;


        generateQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRCodeWriter writer = new QRCodeWriter();
                if(dataEdt.getText().toString().equals("")) return;
                try {
                    BitMatrix bitMatrix = writer.encode(String.valueOf(dataEdt.getText()), BarcodeFormat.QR_CODE, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    qrCodeIV.setImageBitmap(bmp);
                    qrCodeIV.setTag("jest");

                } catch (WriterException e) {
                    e.printStackTrace();
                }


            }
        });

        idBtnSaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(qrCodeIV.getTag().equals("break")){
                    Log.d("esek", String.valueOf(qrCodeIV.getTag()));
                }else {
                    qrCodeIV.buildDrawingCache();
                    Bitmap bmp = qrCodeIV.getDrawingCache();
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