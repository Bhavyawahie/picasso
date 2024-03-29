package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;

public class MainActivity extends AppCompatActivity {
	private ImageView imageView;
	private Bitmap bitmapImage;
	private ProgressBar progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imageView = findViewById(R.id.image_view);
	}

	ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				if (result.getResultCode() == Activity.RESULT_OK) {
					Uri uri = result.getData().getData();
					try {
						ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
						FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
						bitmapImage = BitmapFactory.decodeFileDescriptor(fileDescriptor);
						parcelFileDescriptor.close();
						imageView.setImageBitmap(bitmapImage);
					} catch (FileNotFoundException e) {
						throw new RuntimeException(e);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

				}
			});

	public void openYourActivity(View v) {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("image/*");
		launchSomeActivity.launch(intent);
	}

	public void applyFilter(Transformation<Bitmap> filter) {
		Glide
			.with(this)
			.load(bitmapImage)
			.apply(RequestOptions.bitmapTransform(filter))
			.into(imageView);
	}
	public void applySepia(View view) {
		applyFilter(new SepiaFilterTransformation());
	}
	public void applyGrayscale(View view) {
		applyFilter(new GrayscaleTransformation());
	}
	public void applyToon(View view) {
		applyFilter(new ToonFilterTransformation());
	}
	public void applySketch(View view) {
		applyFilter(new SketchFilterTransformation());
	}
}