package com.example.penguinrocketrush;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background {

    int x = 0, y = 0;
    Bitmap background;

    Background (int screenX, int screenY, Resources res) {
        background = BitmapFactory.decodeResource(res, R.drawable.background1);

        // Calculate the scaling factor to maintain the aspect ratio
        float aspectRatio = background.getWidth() / (float) background.getHeight();
        int scaledHeight = screenY; // Maximum height is the height of the screen
        int scaledWidth = Math.round(aspectRatio * scaledHeight);

        if (scaledWidth < screenX) {
            // If width after scaling is still less than screen width, adjust it
            scaledWidth = screenX;
            scaledHeight = Math.round(scaledWidth / aspectRatio);
        }

        // Scale the background to fit the screen while maintaining aspect ratio
        background = Bitmap.createScaledBitmap(background, scaledWidth, scaledHeight, true);
    }
}
