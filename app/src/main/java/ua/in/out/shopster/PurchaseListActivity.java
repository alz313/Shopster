package ua.in.out.shopster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;


public class PurchaseListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int RC_SIGN_IN = 1;

    private RecyclerView mPurchasesRecyclerView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPurchasesDatabaseReference;
    private FirebaseRecyclerAdapter mAdapter;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_activity_purchase_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PurchaseListActivity.this, PurchaseActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                mPurchasesDatabaseReference.orderByChild("bought"));

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((PurchaseListAdapter) mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mPurchasesRecyclerView);

        mPurchasesRecyclerView.setAdapter(mAdapter);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // already signed in
                    onSignedInInitialize();
                } else {
                    // not signed in
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(PurchaseListActivity.this, "You logged in", Toast.LENGTH_SHORT).show();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Snackbar.make(mPurchasesRecyclerView, R.string.sign_in_cancelled, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(mPurchasesRecyclerView, R.string.no_internet_connection, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Snackbar.make(mPurchasesRecyclerView, R.string.unknown_error, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                //Sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }


    private void onSignedInInitialize() {
    }

    private void onSignedOutCleanup() {
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
