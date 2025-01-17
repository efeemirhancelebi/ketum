package com.efeemirhancelebi.ketum;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {

    private int girilenGelirMiktari;
    private final int asgariUcret = 22104;  // Asgari ücret değeri
    private Button gelirBilgiKaydet, yeniBorcEkle, yeniBorcKaydet, gelirBilgisiSifirla;
    private EditText gelirMiktariGir, yeniBorcAdiGir, yeniBorcMiktariGir, yeniOdemeTarihiGir;
    private TextView gelirDerecesi;
    private ImageButton kullaniciSimgesi, geriButonu;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        gelirBilgiKaydet = findViewById(R.id.gelirBilgiKaydet);
        gelirMiktariGir = findViewById(R.id.gelirGir);
        yeniBorcEkle = findViewById(R.id.borcEkle);
        yeniBorcKaydet = findViewById(R.id.borcKaydet);
        yeniBorcAdiGir = findViewById(R.id.borcAdiGir);
        yeniBorcMiktariGir = findViewById(R.id.borcMiktariGir);
        yeniOdemeTarihiGir = findViewById(R.id.odemeTarihiGir);
        gelirDerecesi = findViewById(R.id.gelirDerecesi);
        kullaniciSimgesi = findViewById(R.id.userLogo);
        geriButonu = findViewById(R.id.geriButonu);
        gelirBilgisiSifirla = findViewById(R.id.gelirBilgiSifirla);

        // Gelir bilgisi kaydedildiğinde
        gelirBilgiKaydet.setOnClickListener(view -> {
            String gelirInput = gelirMiktariGir.getText().toString();

            if (gelirInput.isEmpty()) {
                Toast.makeText(this, "Lütfen bir gelir miktarı girin.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    girilenGelirMiktari = Integer.parseInt(gelirInput);

                    // Gelir bilgisini veritabanına kaydet
                    databaseHelper.saveGelir(girilenGelirMiktari);

                    // Gelir derecesini belirle
                    if (girilenGelirMiktari < asgariUcret) {
                        gelirDerecesi.setText("Asgari Seviye Altında");
                        gelirDerecesi.setTextColor(Color.parseColor("#E34234"));
                    } else if (girilenGelirMiktari <= 35000) {
                        gelirDerecesi.setText("Orta Seviye");
                        gelirDerecesi.setTextColor(Color.parseColor("#FFBF00"));
                    } else if (girilenGelirMiktari <= 50000) {
                        gelirDerecesi.setText("İyi Seviye");
                        gelirDerecesi.setTextColor(Color.parseColor("#00FF7F"));
                    } else {
                        gelirDerecesi.setText("Çok İyi Seviye");
                        gelirDerecesi.setTextColor(Color.parseColor("#32CD32"));
                    }
                    Toast.makeText(this, "Gelir bilgisi başarıyla girildi.", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Geçersiz gelir miktarı.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Veritabanından gelir verisini al ve sorgula
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.getGelir();

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("income_amount");
            if (columnIndex != -1) {
                // Sütun doğru, veriyi al
                int girilenGelirMiktari = cursor.getInt(columnIndex);

                // Gelir derecesini belirle
                if (girilenGelirMiktari < asgariUcret) {
                    gelirDerecesi.setText("Asgari Seviye Altında");
                    gelirDerecesi.setTextColor(Color.parseColor("#E34234"));
                } else if (girilenGelirMiktari <= 35000) {
                    gelirDerecesi.setText("Orta Seviye");
                    gelirDerecesi.setTextColor(Color.parseColor("#FFBF00"));
                } else if (girilenGelirMiktari <= 50000) {
                    gelirDerecesi.setText("İyi Seviye");
                    gelirDerecesi.setTextColor(Color.parseColor("#00FF7F"));
                } else {
                    gelirDerecesi.setText("Çok İyi Seviye");
                    gelirDerecesi.setTextColor(Color.parseColor("#32CD32"));
                }
            } else {
                // Sütun adı yanlış
                gelirDerecesi.setText("Gelir Sütunu Bulunamadı");
                gelirDerecesi.setTextColor(Color.RED);
            }
        } else {
            // Kayıt yok
            gelirDerecesi.setText("Gelir Bilgisi Bulunamadı");
            gelirDerecesi.setTextColor(Color.GRAY);
        }

        if (cursor != null) {
            cursor.close();
        }

        Cursor cursor0 = databaseHelper.getGelir();

        if (cursor0 != null && cursor0.moveToFirst()) {
            int columnIndex = cursor0.getColumnIndex("income_amount"); // Sütun adı
            if (columnIndex != -1) {
                int gelirBilgisi = cursor0.getInt(columnIndex);

                // Hint değerini ayarla
                gelirMiktariGir.setHint("Gelir Miktarı: " + gelirBilgisi + "₺");
            } else {
                // Sütun bulunamadıysa varsayılan bir değer ayarla
                gelirMiktariGir.setHint("Gelir Bilgisi Bulunamadı");
            }
        } else {
            // Gelir bilgisi yoksa varsayılan bir değer ayarla
            gelirMiktariGir.setHint("Gelir Bilgisi: Henüz Girilmedi");
        }

        if (cursor0 != null) {
            cursor0.close();
        }


        // Yeni borç ekleme butonu
        yeniBorcEkle.setOnClickListener(view -> {
            yeniBorcAdiGir.setVisibility(View.VISIBLE);
            yeniBorcMiktariGir.setVisibility(View.VISIBLE);
            yeniOdemeTarihiGir.setVisibility(View.VISIBLE);
            yeniBorcKaydet.setVisibility(View.VISIBLE);
        });

        // Borç kaydetme butonu
        yeniBorcKaydet.setOnClickListener(view -> {
            String borcAdi = yeniBorcAdiGir.getText().toString();
            String borcMiktariStr = yeniBorcMiktariGir.getText().toString();
            String odemeTarihi = yeniOdemeTarihiGir.getText().toString();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                // Tarihi parse et
                Date borcTarihi = dateFormat.parse(odemeTarihi);
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
                    Toast.makeText(MenuActivity.this, "Tarih en fazla 1 yıl geriye ve 10 yıl ileriye kadar olmalıdır.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                Toast.makeText(MenuActivity.this, "Tarih formatı yanlış. Doğru format: dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }

            int borcMiktari = Integer.parseInt(borcMiktariStr);

            String[] dateParts = odemeTarihi.split("-");
            int odemeTarihiYear = Integer.parseInt(dateParts[0]); // Yıl
            int odemeTarihiMonth = Integer.parseInt(dateParts[1]); // Ay


            databaseHelper.insertDebt(borcAdi, borcMiktari, odemeTarihi, odemeTarihiMonth, odemeTarihiYear);

            Toast.makeText(this, "Borç kaydedildi!", Toast.LENGTH_SHORT).show();
        });

        geriButonu.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        gelirBilgisiSifirla.setOnClickListener(view -> {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            int rowsDeleted = db.delete("income", null, null);

            if (rowsDeleted > 0) {
                Toast.makeText(this, "Tüm gelir verileri silindi.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Veri silme işlemi başarısız.", Toast.LENGTH_SHORT).show();
            }
        });
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
