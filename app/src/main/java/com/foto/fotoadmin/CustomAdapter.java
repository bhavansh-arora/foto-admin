package com.foto.fotoadmin;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.constants.ListAppsActivityContract;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    ListData listData;
    List<Model> modelList;
    Context context;

    public CustomAdapter(ListData listData, List<Model> modelList) {
        this.listData = listData;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String title = modelList.get(position).getTitle();
                String price = modelList.get(position).getPrice();
                String mfgdate = modelList.get(position).getMfgDate();
                String expirydate = modelList.get(position).getExpiryDate();
                String barcodeId = modelList.get(position).getBarcodeId();
                List<String> ingredients = modelList.get(position).getIngredients();
                String concatenatedIngredientNames = "";
                for (int i = 0; i < ingredients.size(); i++) {
                    concatenatedIngredientNames += ingredients.get(i);
                    if (i < ingredients.size() - 1) concatenatedIngredientNames += ", ";
                }
                Toast.makeText(listData, "Title: "+title+"\n"+"Price: "+price+"\n"+"Barcode Id: "+barcodeId+"\n"
                        +"Mfg. Date: "+mfgdate+"\n"+"Expiry Date: "+expirydate+"\n"+"Ingredients: "+concatenatedIngredientNames, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(listData);
                String[] options= {"Update","Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            String id = modelList.get(position).getId();
                            String title = modelList.get(position).getTitle();
                            String price = modelList.get(position).getPrice();
                            String mfgdate = modelList.get(position).getMfgDate();
                            String expirydate = modelList.get(position).getExpiryDate();
                            String barcodeId = modelList.get(position).getBarcodeId();
                            List<String> ingredients = modelList.get(position).getIngredients();

                            Intent intent = new Intent(listData, AddData.class);
                            intent.putExtra("id",id);
                            intent.putExtra("title",title);
                            intent.putExtra("price",price);
                            intent.putExtra("mfgdate",mfgdate);
                            intent.putExtra("expirydate",expirydate);
                            intent.putExtra("barcodeId",barcodeId);
                            intent.putStringArrayListExtra("ingredients",(ArrayList<String>)ingredients);
                            listData.startActivity(intent);
                        } else if(which == 1) {
                            listData.deleteData(position);
                        }
                    }
                }).create().show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(modelList.get(position).getTitle());
        holder.price.setText(" Php " + modelList.get(position).getPrice());
        holder.mfg_date.setText(modelList.get(position).getMfgDate());
        holder.expiry_date.setText(modelList.get(position).getExpiryDate());
        holder.barcodeId.setText(modelList.get(position).getBarcodeId());
      //  holder.ingredients.setText(modelList.get(position)[);

        String concatenatedIngredientNames = "";
        List<String> ingredients = modelList.get(position).getIngredients(); // I assume the return value is a list of type "Star"!
        for (int i = 0; i < ingredients.size(); i++) {
            concatenatedIngredientNames += ingredients.get(i);
            if (i < ingredients.size() - 1) concatenatedIngredientNames += ", ";
        }
        holder.ingredients.setText(concatenatedIngredientNames);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
