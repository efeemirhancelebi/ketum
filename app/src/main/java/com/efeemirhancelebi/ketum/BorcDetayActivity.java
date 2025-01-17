package com.efeemirhancelebi.ketum;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Calendar;


public class BorcDetayActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView borcunAdi, borcunMiktari, borcunOdemeTarihi;
    private Button aciklamaDegistir, borcuSil, editKaydet;
    private EditText borcAciklamaDuzenle, borcMiktarDuzenle, borcOdemeTarihiDuzenle;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_borc_detay);

        databaseHelper = new DatabaseHelper(this);

        // Intent'ten verileri alıyoruz
        Intent intent = getIntent();
        String debtName = intent.getStringExtra("debt_name");
        String debtAmount = intent.getStringExtra("debt_amount");
        String debtDueDate = intent.getStringExtra("debt_due_date");

        // TextView'lara verileri set ediyoruz
        borcunAdi = findViewById(R.id.borcunAdi);
        borcunMiktari = findViewById(R.id.borcunMiktari);
        borcunOdemeTarihi = findViewById(R.id.borcunOdemeTarihi);

        borcunAdi.setText(debtName);
        borcunMiktari.setText(debtAmount);
        borcunOdemeTarihi.setText(debtDueDate);

        backButton = findViewById(R.id.geriButonu);
        backButton.setOnClickListener(view -> {
            Intent intent2 = new Intent(BorcDetayActivity.this, MainActivity.class);
            startActivity(intent2);
        });

        borcAciklamaDuzenle = findViewById(R.id.borcAciklamaDegistir);
        borcMiktarDuzenle = findViewById(R.id.borcMiktarDegistir);
        borcOdemeTarihiDuzenle = findViewById(R.id.borcOdemeTarihiDegistir);

        aciklamaDegistir = findViewById(R.id.aciklamaDegistir);
        aciklamaDegistir.setOnClickListener(view -> {
            borcAciklamaDuzenle.setVisibility(View.VISIBLE);
            borcMiktarDuzenle.setVisibility(View.VISIBLE);
            borcOdemeTarihiDuzenle.setVisibility(View.VISIBLE);
        });

        // Borç silme butonunu tanımlıyoruz
        borcuSil = findViewById(R.id.borcuSil);

        // Borç silme butonuna tıklama olayı ekliyoruz
        borcuSil.setOnClickListener(v -> {
            // Borcu veritabanından siliyoruz
            borcuVeritabanindanSil(debtName);
        });

        // Kaydet butonuna tıklama işlemi
        editKaydet = findViewById(R.id.borcEditKaydet);
        editKaydet.setOnClickListener(view -> {
            String debtDescription = borcAciklamaDuzenle.getText().toString().trim();
            String debtAmountString = borcMiktarDuzenle.getText().toString().trim();
            String debtDueDates = borcOdemeTarihiDuzenle.getText().toString().trim();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                // Tarihi parse et
                Date borcTarihi = dateFormat.parse(debtDueDates);
                Date currentDate = new Date();

                // 1 yıl önceyi hesaplamak için Calendar kullan
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.YEAR, -1); // 1 yıl geriye git
                Date oneYearAgo = calendar.getTime();

                // 10 yıl sonrası tarih hesapla
                calendar.setTime(currentDate);
                calendar.add(Calendar.YEAR, 10); // 10 yıl ileri git
                Date tenYearsLater = calendar.getTime();

                // Tarih geçerliliği kontrolü: 1 yıl geriye ve 10 yıl ileriye kadar
                if (borcTarihi.before(oneYearAgo) || borcTarihi.after(tenYearsLater)) {
                    Toast.makeText(BorcDetayActivity.this, "Tarih en fazla 1 yıl geriye ve 10 yıl ileriye kadar olmalıdır.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                Toast.makeText(BorcDetayActivity.this, "Tarih formatı yanlış. Doğru format: dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }

            // Borç miktarını int'e çevir
            int debtAmountValue = 0;
            try {
                debtAmountValue = Integer.parseInt(debtAmountString);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Borç miktarı geçerli bir sayı olmalıdır.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (debtDescription.isEmpty() || debtAmountString.isEmpty() || debtDueDates.isEmpty()) {
                Toast.makeText(this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            } else {
                // Borcu güncelle
                updateDebt(borcunAdi.getText().toString(), debtDescription, debtAmountValue, debtDueDates);
            }
        });
    }
    private void updateDebt(String oldDebtDescription, String newDebtDescription, int newDebtAmount, String newDebtDueDate) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Güncelleme işlemi için ContentValues oluştur
        ContentValues contentValues = new ContentValues();
        contentValues.put("debt_name", newDebtDescription);  // Yeni borç açıklamasını kullanıyoruz
        contentValues.put("debt_amount", newDebtAmount); // Yeni borç miktarını güncelliyoruz
        contentValues.put("debt_due_date", newDebtDueDate);   // Yeni borç ödeme tarihini güncelliyoruz

        // Eski borç adı ile veritabanında güncelleme işlemi
        int rowsUpdated = db.update("debts", contentValues, "debt_name = ?", new String[]{oldDebtDescription});

        // Eğer bir kayıt güncellenirse, başarılı mesajı
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Borç başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Bu borç veritabanında bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    // Veritabanından borcu silen metod
    private void borcuVeritabanindanSil(String debtName) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Borç silme işlemi: debt_name kullanılarak silme yapılır
        int rowsDeleted = db.delete("debts", "debt_name = ?", new String[]{debtName});

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Borç başarıyla silindi.", Toast.LENGTH_SHORT).show();
            // Borç silindikten sonra ana aktiviteye geri dönebiliriz
            Intent intent2 = new Intent(BorcDetayActivity.this, MainActivity.class);
            startActivity(intent2);
        } else {
            Toast.makeText(this, "Borç silinirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Veritabanı bağlantısını kapat
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

}