package com.example.penguinrocketrush;

import static com.example.penguinrocketrush.GameView.screenRatioX;
import static com.example.penguinrocketrush.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

public class Bird {

    public int speed = 20;
    public int offsetX = 0, offsetY = 0;
    public boolean wasShot = true;
    public boolean isDying= false;
    int x, y, width, height, birdCounter = 1;
    Bitmap bird1, bird2, bird3, bird4, bird_death1, bird_death2, bird_death3, bird_death4;

    Bird (Resources res) {

        bird1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
        bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
        bird3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
        bird4 = BitmapFactory.decodeResource(res, R.drawable.bird4);

        bird_death1 = BitmapFactory.decodeResource(res, R.drawable.bird_death1);
        bird_death2 = BitmapFactory.decodeResource(res, R.drawable.bird_death2);
        bird_death3 = BitmapFactory.decodeResource(res, R.drawable.bird_death3);
        bird_death4 = BitmapFactory.decodeResource(res, R.drawable.bird_death4);

        width = bird1.getWidth();
        height = bird1.getHeight();

        width /= 3;
        height /= 3;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false);
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false);
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false);
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false);

        bird_death1 = Bitmap.createScaledBitmap(bird_death1, width * 2, height * 2, false);
        bird_death2 = Bitmap.createScaledBitmap(bird_death2, width * 2, height * 2, false);
        bird_death3 = Bitmap.createScaledBitmap(bird_death3, width * 2, height * 2, false);
        bird_death4 = Bitmap.createScaledBitmap(bird_death4, width * 2, height * 2, false);

        y = -height;
    }

    Bitmap getBird() {
        if (isDying) {
            switch (birdCounter) {
                case 1:
                    birdCounter++;
                    offsetX = -100;
                    offsetY = 0;
                    return bird_death1;
                case 2:
                    birdCounter++;
                    return bird_death2;
                case 3:
                    birdCounter++;
                    return bird_death3;
                case 4:
                    birdCounter = 1;
                    isDying = false; // Only reset after last death frame
                    x = -500; // Move off-screen or reset position
                    return bird_death4;
            }
        } else {
            offsetX = 0; // Reset offsets for normal animation
            offsetY = 0;
            // Regular animation sequence
            switch (birdCounter) {
                case 1:
                    birdCounter++;
                    return bird1;
                case 2:
                    birdCounter++;
                    return bird2;
                case 3:
                    birdCounter++;
                    return bird3;
                case 4:
                    birdCounter = 1;
                    return bird4;
            }
        }
        return bird1; // Fallback
    }


    Rect getCollisionShape (){
        return new Rect(x, y, x + width, y + height);
    }
}