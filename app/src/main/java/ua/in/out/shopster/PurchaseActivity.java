package ua.in.out.shopster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class PurchaseActivity extends AppCompatActivity {
    public static final String EXTRA_PURCHASE_ID = "purchase_push_id";

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("purchase");

    String mPurchasePushId;

    Purchase mPurchase;

    EditText mName;
    EditText mQty;
    EditText mUnit;
    CheckBox mIsBought;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        mPurchasePushId = getIntent().getStringExtra(EXTRA_PURCHASE_ID);

        mName = findViewById(R.id.et_activity_purchase_name);
        mQty = findViewById(R.id.et_activity_purchase_qty);
        mUnit = findViewById(R.id.et_activity_purchase_unit);
        mIsBought = findViewById(R.id.cb_activity_purchase_bought);

        if (mPurchasePushId != null) {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPurchase = dataSnapshot.getValue(Purchase.class);

                    mName.setText(mPurchase.getName());
                    mQty.setText(String.format(Locale.getDefault(), "%10.2f", mPurchase.getQty()));
                    mUnit.setText(mPurchase.getUnit());
                    mIsBought.setChecked(mPurchase.getBought());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseReference.child(mPurchasePushId).addListenerForSingleValueEvent(valueEventListener);

        } else {
            mPurchase = new Purchase();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mName.getText().toString().equals("")) {
            Toast.makeText(PurchaseActivity.this, "Not saved\n...at least \"Title\" should be settled", Toast.LENGTH_SHORT).show();
            return;
        }

        mPurchase.setName(mName.getText().toString());
        mPurchase.setUnit(mUnit.getText().toString());
        mPurchase.setQty(Double.valueOf(mQty.getText().toString()));
        mPurchase.setBought(mIsBought.isChecked());

        if (mPurchasePushId != null) {
            mDatabaseReference.child(mPurchasePushId).setValue(mPurchase);
        } else {
            mDatabaseReference.push().setValue(mPurchase);
        }

    }
}
