package com.example.penguinrocketrush;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private boolean isMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.Play).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GameActivity.class)));

        TextView highScoreTxt = findViewById(R.id.highScoreText);

        SharedPreferences prefs = getSharedPreferences("game",MODE_PRIVATE);
        highScoreTxt.setText("Highscore: " + prefs.getInt("highscore", 0));

        isMute = prefs.getBoolean("isMute", false);

        final ImageView volumeCtrl = findViewById(R.id.volumeControl);
        volumeCtrl.setImageResource(isMute ? R.drawable.volume_mute : R.drawable.volume_up);
        volumeCtrl.setOnClickListener(v -> {
            isMute = !isMute;
            volumeCtrl.setImageResource(isMute ? R.drawable.volume_mute : R.drawable.volume_up);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isMute", isMute);
            editor.apply();
        });

        findViewById(R.id.creditsButton).setOnClickListener(v -> showCreditsDialog());
    }

    private void showCreditsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Credits");
        builder.setMessage("Game Assets: Nicole Meltesen\nSound Effects: LiamG_SFX\nDeveloped by: Brenden Hancock");
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}