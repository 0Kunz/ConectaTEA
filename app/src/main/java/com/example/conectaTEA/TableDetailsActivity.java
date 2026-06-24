package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TableDetailsActivity extends BaseActivity {

    private TextView tvTableTitle, tvCode;
    private Button btnViewPictograms, btnAddByLink, btnPickImage, btnManageAccess;
    private String tableId, tableName, tableCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_details);

        setupBackButton();

        tvTableTitle = findViewById(R.id.tvTableTitle);
        tvCode = findViewById(R.id.tvCode);
        btnViewPictograms = findViewById(R.id.btnViewPictograms);
        btnAddByLink = findViewById(R.id.btnAddByLink);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnManageAccess = findViewById(R.id.btnManageAccess);

        tableName = getIntent().getStringExtra("TABLE_NAME");
        tableId = getIntent().getStringExtra("TABLE_ID");
        tableCode = getIntent().getStringExtra("TABLE_CODE");

        tvTableTitle.setText(tableName);
        tvCode.setText(String.format("Código da tabela: %s", tableCode));

        /*
         * Sem Firebase Storage:
         * escondemos o botão de selecionar imagem da galeria.
         * O cadastro será feito apenas por link direto de imagem.
         */
        btnPickImage.setVisibility(View.GONE);

        checkUserRole();

        btnViewPictograms.setOnClickListener(v -> {
            Intent intent = new Intent(this, PictogramGridActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            intent.putExtra("TABLE_NAME", tableName);
            startActivity(intent);
        });

        btnAddByLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPictogramActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            startActivity(intent);
        });

        btnManageAccess.setOnClickListener(v -> {
            Intent intent = new Intent(this, TableAccessManagementActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            intent.putExtra("TABLE_NAME", tableName);
            startActivity(intent);
        });
    }

    private void checkUserRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("profile");

                        if ("professor".equals(role)) {
                            btnAddByLink.setVisibility(View.GONE);
                            btnPickImage.setVisibility(View.GONE);
                            btnManageAccess.setVisibility(View.GONE);
                        }
                    }
                });
    }
}