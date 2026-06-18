package com.example.conectaTEA;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPictogramActivity extends BaseActivity {

    private EditText etPictogramName, etPictogramLink, etPictogramCategory, etPictogramBorderColor;
    private Button btnSavePictogram;
    private String tableId, passedImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pictogram);

        setupBackButton();

        etPictogramName = findViewById(R.id.etPictogramName);
        etPictogramLink = findViewById(R.id.etPictogramLink);
        etPictogramCategory = findViewById(R.id.etPictogramCategory);
        etPictogramBorderColor = findViewById(R.id.etPictogramBorderColor);
        btnSavePictogram = findViewById(R.id.btnSavePictogram);

        tableId = getIntent().getStringExtra("TABLE_ID");
        passedImageUrl = getIntent().getStringExtra("IMAGE_URL");

        if (passedImageUrl != null) {
            etPictogramLink.setText(passedImageUrl);
            etPictogramLink.setEnabled(false); // Link vindo do Storage não deve ser editado manualmente
        }

        btnSavePictogram.setOnClickListener(v -> {
            String name = etPictogramName.getText().toString().trim();
            String link = etPictogramLink.getText().toString().trim();
            String category = formatCategory(etPictogramCategory.getText().toString());
            String borderColor = normalizeColor(etPictogramBorderColor.getText().toString());

            if (name.isEmpty() || link.isEmpty()) {
                Toast.makeText(this, "Preencha nome e link do pictograma", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.isEmpty()) {
                Toast.makeText(this, "Informe a categoria do pictograma", Toast.LENGTH_SHORT).show();
                return;
            }

            if (borderColor == null) {
                Toast.makeText(this, "Informe uma cor válida. Exemplo: vermelho, azul ou #FF0000", Toast.LENGTH_LONG).show();
                return;
            }

            if (tableId == null) {
                Toast.makeText(this, "Erro: Tabela não identificada.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSavePictogram.setEnabled(false);
            btnSavePictogram.setText("Verificando cor...");

            validateColorAndSave(name, link, category, borderColor);
        });
    }

    private void validateColorAndSave(String name, String link, String category, String borderColor) {
        FirebaseFirestore.getInstance().collection("pictograms")
                .whereEqualTo("tableId", tableId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    String newCategoryKey = categoryKey(category);

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String existingCategory = doc.getString("category");
                        String existingColor = doc.getString("borderColor");

                        if (existingCategory == null || existingColor == null) {
                            continue;
                        }

                        String existingCategoryFormatted = formatCategory(existingCategory);
                        String existingCategoryKey = categoryKey(existingCategoryFormatted);
                        String existingColorNormalized = normalizeColor(existingColor);

                        if (existingColorNormalized == null) {
                            continue;
                        }

                        boolean sameCategory = existingCategoryKey.equals(newCategoryKey);
                        boolean sameColor = existingColorNormalized.equalsIgnoreCase(borderColor);

                        if (!sameCategory && sameColor) {
                            resetSaveButton();
                            Toast.makeText(
                                    this,
                                    "A cor " + borderColor + " já está sendo usada pela categoria " + existingCategoryFormatted + ". Escolha outra cor.",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        if (sameCategory && !sameColor) {
                            resetSaveButton();
                            Toast.makeText(
                                    this,
                                    "A categoria " + existingCategoryFormatted + " já usa a cor " + existingColorNormalized + ". Use essa cor para manter o padrão.",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }
                    }

                    savePictogram(name, link, category, borderColor);
                })
                .addOnFailureListener(e -> {
                    resetSaveButton();
                    Toast.makeText(this, "Erro ao validar cor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void savePictogram(String name, String link, String category, String borderColor) {
        btnSavePictogram.setText("Adicionando...");

        Map<String, Object> pictogram = new HashMap<>();
        pictogram.put("name", name);
        pictogram.put("imageUrl", link);
        pictogram.put("tableId", tableId);
        pictogram.put("category", category);
        pictogram.put("borderColor", borderColor);

        FirebaseFirestore.getInstance().collection("pictograms")
                .add(pictogram)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Pictograma adicionado!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    resetSaveButton();
                    Toast.makeText(this, translateError(e), Toast.LENGTH_SHORT).show();
                });
    }

    private void resetSaveButton() {
        btnSavePictogram.setEnabled(true);
        btnSavePictogram.setText("Salvar pictograma");
    }

    private String formatCategory(String rawCategory) {
        if (rawCategory == null) return "";

        String text = rawCategory.trim();
        if (text.isEmpty()) return "";

        String key = text.toLowerCase(Locale.ROOT);

        switch (key) {
            case "verbo":
            case "verbos":
                return "Verbo";

            case "substantivo":
            case "substantivos":
                return "Substantivo";

            case "adjetivo":
            case "adjetivos":
                return "Adjetivo";

            case "pronome":
            case "pronomes":
                return "Pronome";

            case "pessoa":
            case "pessoas":
                return "Pessoa";

            case "lugar":
            case "lugares":
                return "Lugar";

            case "alimento":
            case "alimentos":
                return "Alimento";

            case "sentimento":
            case "sentimentos":
                return "Sentimento";

            case "rotina":
            case "rotinas":
                return "Rotina";

            default:
                return capitalizeWords(text);
        }
    }

    private String categoryKey(String category) {
        if (category == null) return "";
        return formatCategory(category).toLowerCase(Locale.ROOT).trim();
    }

    private String capitalizeWords(String text) {
        String[] words = text.toLowerCase(Locale.ROOT).trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            if (result.length() > 0) {
                result.append(" ");
            }

            result.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
            if (word.length() > 1) {
                result.append(word.substring(1));
            }
        }

        return result.toString();
    }

    private String normalizeColor(String rawColor) {
        if (rawColor == null) return null;

        String colorText = rawColor.trim();
        if (colorText.isEmpty()) return null;

        String key = colorText.toLowerCase(Locale.ROOT);

        switch (key) {
            case "vermelho":
                return "#F44336";
            case "azul":
                return "#2196F3";
            case "verde":
                return "#4CAF50";
            case "amarelo":
                return "#FFEB3B";
            case "laranja":
                return "#FF9800";
            case "roxo":
                return "#9C27B0";
            case "lilás":
            case "lilas":
                return "#B39DDB";
            case "rosa":
                return "#E91E63";
            case "marrom":
                return "#795548";
            case "cinza":
                return "#9E9E9E";
            case "preto":
                return "#000000";
            case "branco":
                return "#FFFFFF";
        }

        if (colorText.matches("^[0-9a-fA-F]{6}$")) {
            colorText = "#" + colorText;
        }

        try {
            int parsedColor = Color.parseColor(colorText);
            return String.format(Locale.ROOT, "#%06X", (0xFFFFFF & parsedColor));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}