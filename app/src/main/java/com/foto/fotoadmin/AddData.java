package com.foto.fotoadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddData extends AppCompatActivity {

    private EditText mfgDate, expiryDate, ingredient, price, title, barcodeId;
    private int mYear, mMonth, mDay, eYear, eMonth, eDay;
    private ChipGroup chipGroup;
    private List<String> ingredients_list;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private TextView screenName;
    Button save;
    String intent_title, intent_price, intent_mfgdate, intent_expirydate, intent_id, intent_barcodeId;
    ArrayList<String> intent_ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        initializeViews();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                if(bundle != null ) {
                    intent_id = bundle.getString("id");
                    updateData(intent_id);
                } else {
                    saveNewData();
                }
            }
        });
    }

    private void initializeViews() {
        mfgDate = findViewById(R.id.mfgDate);
        expiryDate = findViewById(R.id.expiryDate);
        chipGroup = findViewById(R.id.chip_group);
        ingredient = findViewById(R.id.ingredient);
        price = findViewById(R.id.price);
        title = findViewById(R.id.title);
        barcodeId = findViewById(R.id.barcodeId);
        screenName = findViewById(R.id.screen_name);
        save = findViewById(R.id.save);
        progressDialog = new ProgressDialog(this);
        firebaseFirestore = firebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if( bundle != null) {
            screenName.setText("Update Data to");
            save.setText("Update Data");
            intent_title = bundle.getString("title");
            intent_price = bundle.getString("price");
            intent_mfgdate = bundle.getString("mfgdate");
            intent_expirydate = bundle.getString("expirydate");
            intent_barcodeId = bundle.getString("barcodeId");

            title.setText(intent_title);
            price.setText(intent_price);
            mfgDate.setText(intent_mfgdate);
            expiryDate.setText(intent_expirydate);
            barcodeId.setText(intent_barcodeId);
            intent_ingredients = getIntent().getStringArrayListExtra("ingredients");
            int chipsCount = intent_ingredients.size();

            int i = 0;
            while (i < chipsCount) {
                Chip intent_chip = new Chip(this);
                ChipDrawable drawable = ChipDrawable.createFromAttributes(this,null,
                        0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry);
                intent_chip.setChipDrawable(drawable);
                intent_chip.setCheckable(false);
                intent_chip.setClickable(false);
                intent_chip.setIconStartPadding(3f);
                intent_chip.setPadding(60, 10, 60, 10);
                intent_chip.setText(intent_ingredients.get(i));
                intent_chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chipGroup.removeView(intent_chip);
                    }
                });
                chipGroup.addView(intent_chip);
                i++;
            };
        }

    }

    private void updateData(String id) {
        getIngredients();
        mfgDate.setError(null);
        expiryDate.setError(null);
        if(title.getText().toString().trim().isEmpty()) {
            title.setError("Please enter a valid title");
            title.requestFocus();
        } else if(price.getText().toString().trim().isEmpty()) {
            price.setError("Please enter a valid price");
            price.requestFocus();
        } else if(barcodeId.getText().toString().trim().isEmpty()) {
            barcodeId.setError("Please enter a valid barcode Id");
            barcodeId.requestFocus();
        } else if(mfgDate.getText().toString().trim().isEmpty()) {
            mfgDate.setError("Please enter a valid manufacturing date");
            mfgDate.requestFocus();
        } else if(expiryDate.getText().toString().trim().isEmpty()) {
            expiryDate.setError("Please enter a valid expiry date");
            expiryDate.requestFocus();
        } else if(ingredients_list.isEmpty()) {
            ingredient.setError("Please enter atleast one ingredient");
            ingredient.requestFocus();
        } else {
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Updating Data......");
            progressDialog.show();
            Map<String, Object> doc = new HashMap<>();
            doc.put("id",id);
            doc.put("title",title.getText().toString());
            doc.put("search",title.getText().toString().toLowerCase());
            doc.put("price",price.getText().toString());
            doc.put("mfgDate",mfgDate.getText().toString());
            doc.put("expiryDate",expiryDate.getText().toString());
            doc.put("barcodeId",barcodeId.getText().toString());
            doc.put("ingredients",ingredients_list);

            firebaseFirestore.collection("products").document(id).update(doc)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Toast.makeText(AddData.this, "Product data was successfully updated.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddData.this, ListData.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveNewData() {
        getIngredients();
        mfgDate.setError(null);
        expiryDate.setError(null);
        if(title.getText().toString().trim().isEmpty()) {
            title.setError("Please enter a valid title");
            title.requestFocus();
        } else if(price.getText().toString().trim().isEmpty()) {
            price.setError("Please enter a valid price");
            price.requestFocus();
        } else if(barcodeId.getText().toString().trim().isEmpty()) {
            barcodeId.setError("Please enter a valid barcode Id");
            barcodeId.requestFocus();
        } else if(mfgDate.getText().toString().trim().isEmpty()) {
            mfgDate.setError("Please enter a valid manufacturing date");
            mfgDate.requestFocus();
        } else if(expiryDate.getText().toString().trim().isEmpty()) {
            expiryDate.setError("Please enter a valid expiry date");
            expiryDate.requestFocus();
        } else if(ingredients_list.isEmpty()) {
            ingredient.setError("Please enter atleast one ingredient");
            ingredient.requestFocus();
        } else {
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Adding Data......");
            progressDialog.show();
            String id = UUID.randomUUID().toString();
            Map<String, Object> doc = new HashMap<>();
            doc.put("id",id);
            doc.put("title",title.getText().toString());
            doc.put("search",title.getText().toString().toLowerCase());
            doc.put("price",price.getText().toString());
            doc.put("mfgDate",mfgDate.getText().toString());
            doc.put("expiryDate",expiryDate.getText().toString());
            doc.put("barcodeId",barcodeId.getText().toString());
            doc.put("ingredients",ingredients_list);

            firebaseFirestore.collection("products").document(id).set(doc)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Toast.makeText(AddData.this, "Product data was successfully saved.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddData.this, ListData.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void getIngredients() {
        ingredients_list = new ArrayList<String>();
        int chipsCount = chipGroup.getChildCount();

        int i = 0;
        while (i < chipsCount) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            ingredients_list.add(chip.getText().toString().trim());
           // Toast.makeText(getApplicationContext(),chip.getText().toString(),Toast.LENGTH_LONG).show();
            i++;
        };
    }

    public void selectExpiry(View view) {
        if (view == expiryDate) {
            final Calendar calendar = Calendar.getInstance ();
            eYear = calendar.get ( Calendar.YEAR );
            eMonth = calendar.get ( Calendar.MONTH );
            eDay = calendar.get ( Calendar.DAY_OF_MONTH );

            //show dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog ( this, new DatePickerDialog.OnDateSetListener () {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    expiryDate.setText ( dayOfMonth + "/" + (month + 1) + "/" + year );
                }
            }, eYear, eMonth, eDay );
            datePickerDialog.show ();
        }
    }

    public void selectMfg(View view) {
        if (view == mfgDate) {
            final Calendar calendar = Calendar.getInstance ();
            mYear = calendar.get ( Calendar.YEAR );
            mMonth = calendar.get ( Calendar.MONTH );
            mDay = calendar.get ( Calendar.DAY_OF_MONTH );

            //show dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog ( this, new DatePickerDialog.OnDateSetListener () {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mfgDate.setText ( dayOfMonth + "/" + (month + 1) + "/" + year );
                }
            }, mYear, mMonth, mDay );
            datePickerDialog.show ();
        }
    }

    public void addIngredient(View view) {
        ingredient.setError(null);
        if(!ingredient.getText().toString().trim().isEmpty()) {
            final Chip chip = new Chip(this);
            ChipDrawable drawable = ChipDrawable.createFromAttributes(this,null,
                    0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry);
            chip.setChipDrawable(drawable);
            chip.setCheckable(false);
            chip.setClickable(false);
            chip.setIconStartPadding(3f);
            chip.setPadding(60, 10, 60, 10);
            chip.setText(ingredient.getText().toString().trim());
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroup.removeView(chip);
                }
            });
            chipGroup.addView(chip);
            ingredient.setText("");
        } else {
            ingredient.setError("Please enter an ingredient name.");
            ingredient.requestFocus();
        }
    }
}