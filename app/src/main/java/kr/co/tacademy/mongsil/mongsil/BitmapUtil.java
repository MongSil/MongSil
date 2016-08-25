package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-08-14.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.IOException;

/**
 * BitmapUtil Class
 *
 * @Author : mcsong@gmail.com
 * @Date : Mar 11, 2012 9:59:18 AM
 * @Version : 1.0.0
 */
public class BitmapUtil {
    // 현재 보이는 view를 surfaceView를 이용해서 비트맵으로 전환
    public static Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (view instanceof SurfaceView) {
            SurfaceView surfaceView = (SurfaceView) view;
            surfaceView.setZOrderOnTop(true);
            surfaceView.draw(canvas);
            surfaceView.setZOrderOnTop(false);
            return bitmap;
        } else {
            //For ViewGroup & View
            view.draw(canvas);
            return bitmap;
        }
    }

    // 이미지의 Orientation 정보를 얻는 함수입니다.
    public synchronized static int GetExifOrientation(String filepath)
    {
        int degree = 0;
        ExifInterface exif = null;

        try
        {
            exif = new ExifInterface(filepath);
        }
        catch (IOException e)
        {
            Log.e("exif ", "cannot read exif");
            e.printStackTrace();
        }

        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1)
            {
                // We only recognize a subset of orientation tag values.
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }

    // 이미지를 특정 각도로 회전하는 함수입니다.
    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees)
    {
        if ( degrees != 0 && bitmap != null )
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try
            {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2)
                {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError ex)
            {
                // We have no memory to rotate. Return the original bitmap.
            }
        }

        return bitmap;
    }

    //
    public synchronized static Bitmap SafeDecodeBitmapFile(String strFilePath, String select)
    {
        try
        {
            File file = new File(strFilePath);
            if (file.exists() == false)
            {
                Log.e("Error : ", "[ImageDownloader] SafeDecodeBitmapFile : File does not exist !!");

                return null;
            }

            // Max image size
            final int IMAGE_MAX_SIZE 	= 0;
            BitmapFactory.Options bfo 	= new BitmapFactory.Options();
            bfo.inSampleSize            = 4;
            bfo.inJustDecodeBounds 		= true;
            if(select.equals("camera")) {
                bfo.inSampleSize = 8;
            }
            BitmapFactory.decodeFile(strFilePath, bfo);

            if(bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE)
            {
                bfo.inSampleSize = (int)Math.pow(2, (int)Math.round(Math.log(IMAGE_MAX_SIZE
                        / (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
            }
            bfo.inJustDecodeBounds = false;
            bfo.inPurgeable = true;
            bfo.inDither = true;

            final Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, bfo);

            int degree = GetExifOrientation(strFilePath);

            return GetRotatedBitmap(bitmap, degree);
        }
        catch(OutOfMemoryError ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Bitmap을 ratio에 맞춰서 max값 만큼 resize한다.
     *
     * @param bitmap 원본
     * @param max    원하는 크기의 값
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap src, int max) {
        if (src == null)
            return null;

        int width = src.getWidth();
        int height = src.getHeight();
        float rate = 0.0f;

        if (width > height) {
            rate = max / (float) width;
            height = (int) (height * rate);
            width = max;
        } else {
            rate = max / (float) height;
            width = (int) (width * rate);
            height = max;
        }

        return Bitmap.createScaledBitmap(src, width, height, true);
    }

    /**
     * Bitmap을 ratio에 맞춰서 max값 만큼 resize한다.
     *
     * @param src
     * @param max
     * @param isKeep 작은 크기인 경우 유지할건지 체크..
     * @return
     */
    public static Bitmap resize(Bitmap src, int max, boolean isKeep) {
        if (!isKeep)
            return resizeBitmap(src, max);

        int width = src.getWidth();
        int height = src.getHeight();
        float rate = 0.0f;

        if (width > height) {
            if (max > width) {
                rate = max / (float) width;
                height = (int) (height * rate);
                width = max;
            }
        } else {
            if (max > height) {
                rate = max / (float) height;
                width = (int) (width * rate);
                height = max;
            }
        }

        return Bitmap.createScaledBitmap(src, width, height, true);
    }

    /**
     * Bitmap 이미지를 정사각형으로 만든다.
     *
     * @param src 원본
     * @param max 사이즈
     * @return
     */
    public static Bitmap resizeSquare(Bitmap src, int max) {
        if (src == null)
            return null;

        return Bitmap.createScaledBitmap(src, max, max, true);
    }


    /**
     * Bitmap 이미지를 가운데를 기준으로 w, h 크기 만큼 crop한다.
     *
     * @param src 원본
     * @param w   넓이
     * @param h   높이
     * @return
     */
    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if (src == null)
            return null;

        int width = src.getWidth();
        int height = src.getHeight();

        if (width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if (width > w)
            x = (width-w)/2;

        if (height > h)
            y = (height-h)/2;

        int cw = w; // crop width
        int ch = h; // crop height

        if (w > width)
            cw = width;

        if (h > height)
            ch = height;

        return Bitmap.createBitmap(src, x, y, cw, ch);
    }
}