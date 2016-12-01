package com.anke.vehicle.camputertest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {


    private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int PHOTO_RESOULT = 3;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";
    File outputImage = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"canjian.jpg");
    File file =  new File(Environment.getExternalStorageDirectory(),"temp.jpg");
    private Uri uri1;
    private RoundImageViews imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgs = (RoundImageViews) findViewById(R.id.imgs);
    }

    /**
     * 从图库调取图片剪切
     * @param view
     */
    public void tuku(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_ZOOM);
    }

    /**
     * 拍照剪切
     * @param view
     */
    public void paizhao(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, PHOTO_GRAPH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 拍照
        if (requestCode == PHOTO_GRAPH) {
            startPhotoZoom(Uri.fromFile(file));
        }
        // 读取相册缩放图片
        if (requestCode == PHOTO_ZOOM) {
            startPhotoZoom(data.getData());
        }
        // 处理结果
        if (requestCode == PHOTO_RESOULT) {
                try {
                 Bitmap   photo = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(uri1));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
                    //此处可以把Bitmap保存到sd卡中，具体请看：http://www.cnblogs.com/linjiqin/archive/2011/12/28/2304940.html
                    imgs.setImageBitmap(photo); //把图片显示在ImageView控件上
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理图片剪切
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        uri1 = Uri.fromFile(outputImage);
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);//// 输出文件
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        startActivityForResult(intent, PHOTO_RESOULT);
    }
}
