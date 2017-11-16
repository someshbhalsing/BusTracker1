package com.google.bustracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Gourav on 11-10-2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

    private List<Person> list;
    private Context mContext;
    private int flag;

    public Adapter(List<Person> list, Context mContext,int flag) {
        this.list = list;
        this.mContext = mContext;
        this.flag = flag;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Person person = list.get(position);
        holder.Name.setText(person.getName());
        holder.Uname.setText(person.getUserName());
        holder.Password.setText(person.getPassword());
        holder.Bus.setText(person.getBusNo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView Name;
        TextView Uname;
        TextView Password;
        TextView Bus;

        public Holder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.recycler_name);
            Uname = itemView.findViewById(R.id.recycler_user_name);
            Password = itemView.findViewById(R.id.recycler_password);
            Bus = itemView.findViewById(R.id.recycler_bus);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            PopupMenu popupMenu = new PopupMenu(mContext,view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.portal_menu,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    DatabaseReference reference;
                    if(flag==1) {
                        reference = FirebaseDatabase.getInstance().getReference("drivers/" + Uname.getText().toString());
                    }else{
                        reference = FirebaseDatabase.getInstance().getReference("students/" + Uname.getText().toString());
                    }
                    if(item.getItemId()==R.id.delete){
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().removeValue();
                                    list.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        return true;
                    }else if(item.getItemId() == R.id.update){
                        if(flag == 1){
                            ((Activity)mContext).startActivityForResult(new Intent(mContext,AddDriverActivity.class).putExtra("updateDriver",true).putExtra("uname",Uname.getText().toString()),555);
                            ((Activity) mContext).finish();
                        }else {
                            ((Activity)mContext).startActivityForResult(new Intent(mContext,AddStudentActivity.class).putExtra("updateStudent",true).putExtra("uname",Uname.getText().toString()),555);
                            ((Activity) mContext).finish();
                        }
                    }
                    return false;
                }
            });
            popupMenu.show();

            return true;
        }
    }
}
