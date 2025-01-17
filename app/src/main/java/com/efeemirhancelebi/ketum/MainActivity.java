package com.efeemirhancelebi.ketum;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ImageButton menuButonu;
    private TextView gelirText, gelirMiktar, borclarText, borclarMiktar;
    private ListView borcListesi;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        menuButonu = findViewById(R.id.menuButton);
        gelirText = findViewById(R.id.gelirText);
        gelirMiktar = findViewById(R.id.gelirMiktar);
        borclarText = findViewById(R.id.borclarText);
        borclarMiktar = findViewById(R.id.borclarMiktar);
        borcListesi = findViewById(R.id.borcListesi);

        loadGelir();
        displayDebts();

        menuButonu.setOnClickListener(view -> {
            Intent intent0 = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent0);
        });

        borcListesi.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            // Tıklanan borcun bilgilerini almak
            HashMap<String, String> selectedDebt = (HashMap<String, String>) borcListesi.getItemAtPosition(position);
            String debtName = selectedDebt.get("debt_name");
            String debtAmount = selectedDebt.get("debt_amount");
            String debtDueDate = selectedDebt.get("debt_due_date");

            // BorcDetayActivity'ye verileri gönderme
            Intent intent = new Intent(MainActivity.this, BorcDetayActivity.class);
            intent.putExtra("debt_name", debtName);
            intent.putExtra("debt_amount", debtAmount);
            intent.putExtra("debt_due_date", debtDueDate);
            startActivity(intent);
        });
    }

    private void saveGelir(int gelir) {
        Cursor cursor = databaseHelper.getGelir();
        if (cursor != null && cursor.getCount() > 0) {
            databaseHelper.updateGelir(gelir);
        } else {
            databaseHelper.saveGelir(gelir);
        }
    }

    private void loadGelir() {
        Cursor cursor = databaseHelper.getGelir();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int gelirIndex = cursor.getColumnIndex("income_amount");

                if (gelirIndex != -1) {
                    int gelir = cursor.getInt(gelirIndex);
                    gelirMiktar.setText(String.valueOf(gelir) + " ₺");
                } else {
                    Toast.makeText(this, "Gelir verisi bulunamadı", Toast.LENGTH_SHORT).show();
                }
            } else {
                gelirMiktar.setText("- ₺");
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Cursor null, veritabanı bağlantısını kontrol et", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayDebts() {
        Cursor cursor = databaseHelper.getDebts();
        ArrayList<HashMap<String, String>> debtList = new ArrayList<>();
        int totalDebtAmount = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int debtNameIndex = cursor.getColumnIndex("debt_name");
                int debtAmountIndex = cursor.getColumnIndex("debt_amount");
                int debtDueDateIndex = cursor.getColumnIndex("debt_due_date");

                if (debtNameIndex != -1 && debtAmountIndex != -1 && debtDueDateIndex != -1) {
                    HashMap<String, String> debt = new HashMap<>();
                    String debtName = cursor.getString(debtNameIndex);
                    int debtAmount = cursor.getInt(debtAmountIndex);
                    String debtDueDate = cursor.getString(debtDueDateIndex);

                    String debtAmountWithCurrency = debtAmount + " ₺";
                    debt.put("debt_name", debtName);
                    debt.put("debt_amount", debtAmountWithCurrency);
                    debt.put("debt_due_date", debtDueDate);
                    debtList.add(debt);

                    totalDebtAmount += debtAmount;
                }
            } while (cursor.moveToNext());

            cursor.close();

            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    debtList,
                    R.layout.debt_list_item,
                    new String[]{"debt_name", "debt_amount", "debt_due_date"},
                    new int[]{R.id.debt_name, R.id.debt_amount, R.id.debt_due_date}
            );

            borcListesi.setAdapter(adapter);
            borclarMiktar.setText(totalDebtAmount + " ₺");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGelir();
    }
}
