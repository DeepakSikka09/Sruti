package in.ecomexpress.sruti.utils.common_files;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by parikshittomar on 12-12-2018.
 */

public class BitmapUtils {

    public static Bitmap getNumberedBitmap(Context context, String gText, int resourceID) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, resourceID);

        Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize((int) (25 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.setTextAlign(Paint.Align.CENTER);

        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        canvas.drawText(gText, x * scale, y * scale, paint);

        return bitmap;
    }
    public static Boolean saveBitmap(File file, Bitmap well) {
        try {
            FileOutputStream ostream = new FileOutputStream(file);
            int width = 480;
            int height = (int) (((float) well.getHeight() / well.getWidth()) * width);
            Bitmap save = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Paint paint;
            paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            Rect fullRect = new Rect(0, 0, width, height);
            now.drawRect(fullRect, paint);
            now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), fullRect, null);
            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            CryptoUtils.encryptFile(file.getAbsolutePath(),file.getAbsolutePath(), Constants.ENC_DEC_KEY);
            try {
                ostream.flush();
                ostream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
