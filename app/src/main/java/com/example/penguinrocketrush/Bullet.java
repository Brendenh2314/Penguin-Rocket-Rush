package com.example.penguinrocketrush;

import static com.example.penguinrocketrush.GameView.screenRatioX;
import static com.example.penguinrocketrush.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Bullet {
    int x, y, width, height;
    Bitmap bullet;

    Bullet (Resources res){
        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width *= 1;
        height *= .5;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);
    }
    Rect getCollisionShape (){
        return new Rect(x, y, x + width, y + height);
    }
}
