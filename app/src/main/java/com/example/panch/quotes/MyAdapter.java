package com.example.panch.quotes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panch on 16-12-2016.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<list_item> list_items;
    private Context context;


    public MyAdapter(List<list_item> list_items, Context context) {
        this.list_items = list_items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item,parent,false);

        return new ViewHolder(v,context, (ArrayList<list_item>) list_items);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        list_item list_item=list_items.get(position);
        holder.dec.setText(list_item.getDes());
    }

    @Override
    public int getItemCount() {
        return list_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView dec;
        ArrayList<list_item> listItems=new ArrayList<list_item>();
        Context ctx;
        public ViewHolder(View itemView, Context ctx, ArrayList<list_item> list_items)  {
            super(itemView);
            itemView.setOnClickListener(this);
            this.listItems= list_items;
            this.ctx=ctx;
            dec =(TextView)itemView.findViewById(R.id.tvMsg);
        }


        @Override
        public void onClick(View view) {

            int position=getAdapterPosition();
            list_item listItem=this.listItems.get(position);

            Intent intent=new Intent(ctx.getApplicationContext(),DisplayPost.class);
            intent.putExtra("msg",listItem.getDes());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.ctx.startActivity(intent);

            //this.ctx.startActivity(new Intent(ctx.getApplicationContext()));
        }
    }
}
