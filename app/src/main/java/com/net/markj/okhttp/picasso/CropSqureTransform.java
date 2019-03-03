package com.net.markj.okhttp.picasso;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

/**
 * Created by Kron Xu on 2019/3/3 23:45
 * Description:使用picasso来对图片进行裁剪
 */
public class CropSqureTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int min = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - min) / 2;
        int y = (source.getHeight() - min) / 2;
        Bitmap bitmap = Bitmap.createBitmap(source, x, y, min, min);
        return bitmap;
    }

    @Override
    public String key() {
        return "squre";
    }
}
