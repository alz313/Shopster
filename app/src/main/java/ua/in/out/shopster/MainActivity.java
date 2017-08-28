package ua.in.out.shopster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    RecyclerView mPurchasesRecyclerView;

    TextView mNameEditText;
    TextView mDescEditText;
    Button mSendButton;


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mPurchasesDatabaseReference;
    FirebaseRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameEditText = findViewById(R.id.et_main_name);
        mDescEditText = findViewById(R.id.et_main_desc);
        mSendButton = findViewById(R.id.btn_main_add);

        mPurchasesRecyclerView = findViewById(R.id.rv_activity_main_purchases);
        mPurchasesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mPurchasesDatabaseReference = mFirebaseDatabase.getReference().child("purchases");

        mAdapter = new FirebaseRecyclerAdapter<Purchase, PurchaseHolder>(
                Purchase.class,
                R.layout.activity_purchase_list_item,
                PurchaseHolder.class,
                mPurchasesDatabaseReference) {
            @Override
            protected void populateViewHolder(PurchaseHolder viewHolder, Purchase model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setDesc(model.getDesc());
            }


        };

        mPurchasesRecyclerView.setAdapter(mAdapter);


    }

    public void onClickSend(View view) {
        String name = mNameEditText.getText().toString();
        String desc = mDescEditText.getText().toString();

        Purchase pushPurchase = new Purchase(name, desc);
        mPurchasesDatabaseReference.push().setValue(pushPurchase);
        mNameEditText.setText("");
        mDescEditText.setText("");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
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
