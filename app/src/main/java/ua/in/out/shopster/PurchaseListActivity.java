package ua.in.out.shopster;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.CheckBox;
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
                Intent intent = new Intent(PurchaseListActivity.this, PurchaseActivity.class);
                startActivity(intent);
            }
        });

        mPurchasesRecyclerView = findViewById(R.id.rv_activity_purchase_list_container);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPurchasesRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mPurchasesRecyclerView.addItemDecoration(dividerItemDecoration);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mPurchasesDatabaseReference = mFirebaseDatabase.getReference().child("purchase");

        mAdapter = new PurchaseListAdapter(
                Purchase.class,
                R.layout.activity_purchase_list_item,
                mPurchasesDatabaseReference);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((PurchaseListAdapter) mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mPurchasesRecyclerView);

        mPurchasesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }


    public class PurchaseListAdapter extends FirebaseRecyclerAdapter<Purchase, PurchaseHolder> implements ItemTouchHelperAdapter {

        public PurchaseListAdapter(Class<Purchase> modelClass, int modelLayout, Query ref) {
            super(modelClass, modelLayout, PurchaseHolder.class, ref);
        }

        @Override
        protected void populateViewHolder(PurchaseHolder viewHolder, Purchase model, int position) {


            viewHolder.setName(model.getName());
            viewHolder.setQty(model.getQty());
            viewHolder.setUnit(model.getUnit());
            viewHolder.setBought(model.getBought());
            viewHolder.setDatabaseReference(getRef(position));

        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            return false;
        }

        @Override
        public boolean onItemMoved(int fromPosition, int toPosition) {
            return false;
        }

        @Override
        public void onItemDismiss(int position) {
            getRef(position).removeValue();
        }
    }


    public static class PurchaseHolder extends RecyclerView.ViewHolder {
        private final TextView mNameField;
        private final TextView mQtyField;
        private final TextView mUnitField;
        private final CheckBox mBoughtField;

        private DatabaseReference mDatabaseReference;

        public PurchaseHolder(View itemView) {
            super(itemView);
            mNameField = itemView.findViewById(R.id.tv_activity_purchase_list_item_name);

            mNameField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PurchaseActivity.class);
                    intent.putExtra(PurchaseActivity.EXTRA_PURCHASE_ID, mDatabaseReference.getKey());
                    v.getContext().startActivity(intent);
                }
            });

            mQtyField = itemView.findViewById(R.id.tv_activity_purchase_list_item_qty);
            mUnitField = itemView.findViewById(R.id.tv_activity_purchase_list_item_unit);

            mBoughtField = itemView.findViewById(R.id.cb_activity_purchase_list_item_bought);
            mBoughtField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabaseReference.child("bought").setValue(((CheckBox) view).isChecked());
                }
            });
        }

        public void setDatabaseReference(DatabaseReference ref) {
            mDatabaseReference = ref;
        }

        public void setName(String name) {
            mNameField.setText(name);
        }

        void setQty(Double qty) {
            // TODO: Use formatter.format https://developer.android.com/reference/java/util/Formatter.html
            mQtyField.setText(Double.toString(qty));
        }

        void setUnit(String unit) {
            mUnitField.setText(unit);
        }

        void setBought(Boolean bought) {
            mBoughtField.setChecked(bought);
        }


    }
}
