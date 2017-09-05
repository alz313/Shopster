package ua.in.out.shopster;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class PurchaseListActivity extends AppCompatActivity {
    RecyclerView mPurchasesRecyclerView;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mPurchasesDatabaseReference;
    FirebaseRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        FloatingActionButton fab = findViewById(R.id.fab_activity_purchase_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        mPurchasesRecyclerView = findViewById(R.id.rv_activity_purchase_list_container);
        mPurchasesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mPurchasesDatabaseReference = mFirebaseDatabase.getReference().child("purchases");

        mAdapter = new PurchaseListAdapter(
                Purchase.class,
                R.layout.activity_purchase_list_item,
                mPurchasesDatabaseReference);

        mPurchasesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }



    public class PurchaseListAdapter extends FirebaseRecyclerAdapter<Purchase, PurchaseHolder> {

        /**
         * @param modelClass  Firebase will marshall the data at a location into an instance of a class that you provide
         * @param modelLayout This is the layout used to represent a single item in the list. You will be responsible for populating an
         *                    instance of the corresponding view with the data from an instance of modelClass.
         * @param ref         The Firebase location to watch for data changes. Can also be a slice of a location, using some
         *                    combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
         */
        public PurchaseListAdapter(Class<Purchase> modelClass, int modelLayout, Query ref) {
            super(modelClass, modelLayout, PurchaseHolder.class, ref);
        }

        @Override
        protected void populateViewHolder(PurchaseHolder viewHolder, Purchase model, int position) {
            viewHolder.setName(model.getName());
            viewHolder.setDesc(model.getDesc());
        }
    }


    public static class PurchaseHolder extends RecyclerView.ViewHolder {
        private final TextView mNameField;
        private final TextView mDescField;

        public PurchaseHolder(View itemView) {
            super(itemView);
            mNameField = itemView.findViewById(R.id.tv_name);
            mDescField = itemView.findViewById(R.id.tv_description);
        }

        public void setName(String name) {
            mNameField.setText(name);
        }

        void setDesc(String description) {
            mDescField.setText(description);
        }
    }
}
