package com.example.penguinrocketrush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{
    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;
    private float speedModifier = 1.0f;
    private float flightVerticalSpeed = 20;
    private Paint paint;
    private Bird [] birds;
    private List<Bullet> bullets;
    private Flight flight;
    private Background background1, background2;
    private Random random;
    private SoundPool soundPool;
    private int sound;
    private SharedPreferences prefs;
    private GameActivity activity;

    private Rect shootButton;
    private Bitmap shootButtonBitmap;
    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else{
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        sound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        flight = new Flight(this, screenY, getResources());

        shootButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire_button);


        int buttonWidth = shootButtonBitmap.getWidth();
        int buttonHeight = shootButtonBitmap.getHeight();
        int buttonMargin = 50;
        shootButton = new Rect(screenX - buttonWidth - buttonMargin,
                screenY - buttonHeight - buttonMargin,
                screenX - buttonMargin,
                screenY - buttonMargin);


        bullets = new ArrayList<>();

        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);


        birds = new Bird[4];

        for (int i = 0; i < 4; i++) {
            Bird bird = new Bird(getResources());
            birds[i] = bird;
        }

        random = new Random();

    }

    @Override
    public void run() {
        while(isPlaying){
            update ();
            draw ();
            sleep ();
        }

    }

    private void update() {
        int backgroundSpeed = (int) (10 * screenRatioX);

        background1.x -= backgroundSpeed;
        background2.x -= backgroundSpeed;

        if (background1.x + background1.background.getWidth() <= 0) {
            background1.x = background2.x + background2.background.getWidth();
        }
        if (background2.x + background2.background.getWidth() <= 0) {
            background2.x = background1.x + background1.background.getWidth();
        }

        if (flight.isGoingUp) {
            flight.y -= (int) (flightVerticalSpeed * screenRatioY);  // Use the dynamic speed variable
        } else {
            flight.y += (int) (flightVerticalSpeed * screenRatioY);
        }

        if (flight.y < 0) {
            flight.y = 0;
        }
        if (flight.y >= screenY - flight.height) {
            flight.y = screenY - flight.height;
        }

        List<Bullet> trash = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.x += 80 * screenRatioX;
            if (bullet.x > screenX) {
                trash.add(bullet);
            }

            for (Bird bird : birds) {
                if (Rect.intersects(bird.getCollisionShape(), bullet.getCollisionShape()) && !bird.isDying) {
                    score++; // Increment score because a bird has been hit
                    updateScoreAndSpeed(); // Update the score and check speed adjustment
                    bird.wasShot = true;
                    bird.isDying = true; // Set the bird to dying state
                    bird.birdCounter = 1; // Reset the frame counter for death animation
                    bullet.x = screenX + 500; // Move bullet off screen
                }
            }
        }
        bullets.removeAll(trash); // Efficient way to remove bullets

        for (Bird bird : birds) {
            if (!bird.isDying) {
                bird.x -= bird.speed;
                if (bird.x + bird.width < 0) {
                    if (!bird.wasShot) {
                        isGameOver = true;
                        return;
                    }
                    // Reset the bird if it was not shot
                    resetBird(bird);
                }
            } else if (bird.isDying && bird.birdCounter == 4) { // Check if death animation is complete
                resetBird(bird);
                bird.isDying = false; // End dying state
                bird.wasShot = false; // Reset wasShot status
            }
            if (Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())) {
                isGameOver = true;
                return;
            }
        }
    }

    private void resetBird(Bird bird) {
        int baseSpeed = 10; // Base speed before applying modifier
        int speedRange = 5; // Additional random speed range
        bird.speed = (int) ((baseSpeed + random.nextInt(speedRange)) * screenRatioX * speedModifier);
        bird.x = screenX;
        bird.y = random.nextInt(screenY - bird.height - 10) + 10;
        bird.birdCounter = 1;
    }
    private void updateScoreAndSpeed() {
        score++;
        if (score % 50 == 0) {
            speedModifier += 0.15; // Increase speed for the birds
            for (Bird bird : birds) {
                bird.speed = (int) (bird.speed * speedModifier);  // Update existing birds' speed
            }
        }
        if (score % 50 == 0) {
            flightVerticalSpeed += 5;  // Increase flight's vertical movement speed
        }
    }
    private void draw (){

        if (getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for (Bird bird :birds)

                canvas.drawBitmap(bird.getBird(), bird.x + bird.offsetX, bird.y + bird.offsetY, paint);

            canvas.drawText(score + "", screenX / 2f, 164, paint);

            if (isGameOver){
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting();
                return;

            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);
            canvas.drawBitmap(shootButtonBitmap, null, shootButton, null);

            for(Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }

    }

    private void waitBeforeExiting() {

        try{
            Thread.sleep(2000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {

        if(prefs.getInt("highscore", 0)< score){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }

    }

    private void sleep (){
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public void resume(){
        // Reset background positions
        background1.x = 0;
        background2.x = background1.background.getWidth();  // Assuming background2 should initially be right after background1

        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }
    public void pause(){
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int index = event.getActionIndex();
        int x = (int) event.getX(index);
        int y = (int) event.getY(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: // New touch pointer goes down.
                if (shootButton.contains(x, y)) {
                    flight.toShoot++;
                } else {
                    flight.isGoingUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: // A touch pointer goes up.
                if (shootButton.contains(x, y)) {
                    // If lifting finger from the shoot button, we might add logic here if needed.
                } else {
                    flight.isGoingUp = false;
                }
                break;
            case MotionEvent.ACTION_MOVE: // A touch pointer is moved.
                // Handle move if necessary, especially if you have dragging controls.
                break;
        }
        return true;
    }

    public void newBullet() {

        if(!prefs.getBoolean("isMute", false)){
            soundPool.play(sound, 1, 1, 0, 0, 1);
        }

        Bullet bullet = new Bullet(getResources());

        // Calculate the offset for the bullet to emerge from the gun's nozzle
        int gunOffsetX = flight.width; // Assuming gun is 3/4th of the way across the sprite width
        int gunOffsetY = flight.height / 6;    // Assuming gun is around the vertical middle of the sprite

        // Setting the initial position of the bullet
        bullet.x = flight.x + gunOffsetX; // Position bullet to start from the gun's nozzle
        bullet.y = flight.y + gunOffsetY; // Align bullet vertically with the gun's position

        bullets.add(bullet);
    }
}