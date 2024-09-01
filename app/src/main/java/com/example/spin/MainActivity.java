package com.example.spin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView wheel, arrow;
    TextView footerText;
    Button resetButton, goBackButton;
    int rotation = 0, rotationSpeed = 5;
    int[] stopPosition = {720, 780, 840, 900, 960, 1020}; // 780 position = 10 points
    int[] winPoints = {50, 10, 20, 100, 90, 70};
    int randPosition = 0;
    String userName = ""; // Variable pour stocker le nom de l'utilisateur
    int totalPoints = 0; // Variable pour stocker le total des points gagnés

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wheel = findViewById(R.id.wheel);
        arrow = findViewById(R.id.arrow);
        footerText = findViewById(R.id.footer_text);
        resetButton = findViewById(R.id.reset_button);
        goBackButton = findViewById(R.id.go_back_button);

        // Afficher l'alerte pour entrer le nom de l'utilisateur
        promptForName();

        arrow.setOnClickListener(view -> {
            randPosition = new Random().nextInt(6); // Obtenir un élément aléatoire de la liste de 0 à 5
            startSpin();
        });

        resetButton.setOnClickListener(view -> {
            // Réinitialiser le jeu
            resetGame();
        });

        goBackButton.setOnClickListener(view -> {
            // Action pour le bouton Go Back (si besoin)
            // finish(); // Uncomment if you want to close the activity
        });
    }

    private void promptForName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name");

        final EditText input = new EditText(this);
        input.setHint("Your Name");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String enteredName = input.getText().toString();
            if (isValidName(enteredName)) {
                userName = enteredName;
                updateFooterText("Hello " + userName + "!");
            } else {
                Toast.makeText(this, "Please enter a valid name (only letters and up to 4 words).", Toast.LENGTH_LONG).show();
                promptForName(); // Redemander si le nom est invalide
            }
        });

        builder.setCancelable(false); // Empêcher de fermer la boîte de dialogue sans entrer un nom
        builder.show();
    }

    private boolean isValidName(String name) {
        // Vérifier si le nom contient uniquement des lettres et ne dépasse pas 4 mots
        return name.matches("[a-zA-Z ]+") && name.trim().split("\\s+").length <= 4;
    }

    private void resetGame() {
        rotation = 0;
        rotationSpeed = 5;
        randPosition = 0;
        totalPoints = 0;
        updateFooterText("Hello " + userName + "!");
        promptForName(); // Redemander le nom de l'utilisateur
    }

    public void startSpin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                wheel.setRotation(rotation);
                // Contrôler la vitesse de rotation de la roue
                if (rotation >= 300) {
                    rotationSpeed = 4; // ralentir la roue
                }
                if (rotation >= 400) {
                    rotationSpeed = 3; // ralentir la roue
                }
                if (rotation >= 500) {
                    rotationSpeed = 2; // ralentir la roue
                }

                rotation = rotation + rotationSpeed;
                if (rotation >= stopPosition[randPosition]) {
                    // Arrêter la roue et ajouter une pause avant d'afficher le message
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pointsWon = winPoints[randPosition];
                            totalPoints += pointsWon; // Mettre à jour le total des points
                            showPopup(pointsWon);
                        }
                    }, 1000); // Pause de 1000 millisecondes (1 seconde)
                } else {
                    startSpin(); // Boucle pour continuer la rotation de la roue
                }

            }
        }, 1); // Ce minuteur s'exécute toutes les millisecondes
    }

    public void showPopup(int points) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.win_popup);
        dialog.setCancelable(true);
        dialog.show();

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        TextView winText = dialog.findViewById(R.id.win_text);

        // Create a SpannableString for the message
        String message = userName + ", you won " + points + " points.\n\nTotal: " + totalPoints + " points.\n";
        SpannableString spannableString = new SpannableString(message);

        // Find the start index of the "Total: " part
        int startIndex = message.indexOf("Total: ");
        int endIndex = message.length();

        // Apply the red color to the "Total: " part
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the formatted text to the TextView
        winText.setText(spannableString);

        Button btn = dialog.findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            dialog.cancel();
            // Réinitialiser la rotation pour tourner la roue à nouveau
            rotation = 0;
            rotationSpeed = 5;
            randPosition = 0;
        });

        // Update the footer text with the total points
        updateFooterText(userName + ": " + totalPoints);
    }

    private void updateFooterText(String text) {
        footerText.setText(text);
    }
}
