package com.example.sepatu9;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    Button btnpicture;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnpicture = findViewById(R.id.btncamera_id);
        imageView = findViewById(R.id.imageview1);

        // Memuat OpenCV saat aktivitas dibuat
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initDebug();
        }

        // Menambahkan listener ke tombol kamera
        btnpicture.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Memproses hasil pemotretan dari kamera
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // Konversi Bitmap ke Mat (format OpenCV)
            Mat mat = new Mat();
            Utils.bitmapToMat(photo, mat);

            // Menerapkan filter Gaussian Blur
            Mat blurredMat = new Mat();
            Imgproc.GaussianBlur(mat, blurredMat, new Size(0, 0), 10);

            // Mengurangkan versi yang sudah di-blur dari gambar asli
            Mat sharpenedMat = new Mat();
            Core.addWeighted(mat, 1.5, blurredMat, -0.5, 0, sharpenedMat);

            // Konversi kembali ke Bitmap
            Bitmap processedBitmap = Bitmap.createBitmap(sharpenedMat.cols(), sharpenedMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(sharpenedMat, processedBitmap);

            // Menampilkan gambar hasil pemrosesan di ImageView
            imageView.setImageBitmap(processedBitmap);

            // Menampilkan pesan berhasil
            Toast.makeText(this, "Image sharpening completed successfully!", Toast.LENGTH_SHORT).show();

            // Navigasi ke halaman kedua
            Intent intent = new Intent(MainActivity.this, DisplayImageActivity.class);
            intent.putExtra("photo", photo); // Mengirim foto yang telah dipotret ke activity kedua
            startActivity(intent);
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}




