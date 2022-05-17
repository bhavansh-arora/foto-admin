package com.foto.fotoadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListData extends AppCompatActivity {
    List<Model> modelList = new ArrayList<>();
    RecyclerView list_data;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore firebaseFirestore;
    CustomAdapter customAdapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        list_data = findViewById(R.id.list_recycler_view);
        list_data.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list_data.setLayoutManager(layoutManager);
        showData();
    }

    private void showData() {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Retrieving  Data......");
        progressDialog.show();
        firebaseFirestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc: task.getResult()) {
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("price"),
                                    doc.getString("mfgDate"),
                                    doc.getString("expiryDate"),
                                    doc.getString("barcodeId"),
                                    (List<String>) doc.get("ingredients")
                            );
                            modelList.add(model);
                        }
                        customAdapter = new CustomAdapter(ListData.this, modelList);
                        list_data.setAdapter(customAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ListData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteData(int index) {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Deleting  Data......");
        progressDialog.show();

        firebaseFirestore.collection("products")
                .document(modelList.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ListData.this, "Product data was successfully deleted.", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ListData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addData(View view) {
        Intent intent = new Intent(this, AddData.class);
        startActivity(intent);
    }
}