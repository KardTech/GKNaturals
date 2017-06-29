package com.kard.gknaturals;


        import android.app.Activity;
        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.zxing.integration.android.IntentIntegrator;
        import com.google.zxing.integration.android.IntentResult;

        import org.json.JSONException;
        import org.json.JSONObject;
        import android.widget.Toast;

        import java.util.ArrayList;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;

    //View Objects
    private Button buttonScan;
    private TextView textViewProductName;
    private TextView textViewQuantityValue;
    private TextView textViewItemCode;

    //qr code scanner object
    private IntentIntegrator qrScan;

    //list
    Button btnDisplay;
    ImageButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //displaying logged in user name
        textViewUserEmail.setText(user.getEmail());

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewProductName = (TextView) findViewById(R.id.textViewProductName);
        textViewQuantityValue = (TextView) findViewById(R.id.textViewQuantity);
        textViewItemCode = (TextView) findViewById(R.id.textViewItemCode);


        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);

        //list
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);

        add(this, btnAdd);
        display(this, btnDisplay);
        //qrToScan(this, buttonScan);
    }
    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qr code has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewProductName.setText(obj.getString("ProductName"));
                    textViewQuantityValue.setText(obj.getString("Quantity"));
                    textViewItemCode.setText(obj.getString("ItemCode"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Please scan a valid QR.Scanned QR is not for GK naturals app", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == buttonScan){
            Toast.makeText(this, "In buttonScan", Toast.LENGTH_LONG).show();
            qrScan.initiateScan();

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public static void display(final Activity activity, Button btn)
    {
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LinearLayout scrollViewlinerLayout = (LinearLayout) activity.findViewById(R.id.linearLayoutForm);

                java.util.ArrayList<String> msg = new ArrayList<String>();
                java.util.ArrayList<String> items = new ArrayList<String>();

                for (int i = 0; i < scrollViewlinerLayout.getChildCount(); i++)
                {
                    LinearLayout innerLayout = (LinearLayout) scrollViewlinerLayout.getChildAt(i);
                    EditText edit = (EditText) innerLayout.findViewById(R.id.editDescricao);
                    TextView textViewProductName = (TextView) innerLayout.findViewById(R.id.textViewProductName);
                    TextView textViewQuantity = (TextView) innerLayout.findViewById(R.id.textViewQuantity);
                    TextView textViewItemCode = (TextView) innerLayout.findViewById(R.id.textViewItemCode);
                    Button buttonScan = (Button) innerLayout.findViewById(R.id.buttonScan);

                    msg.add(edit.getText().toString());
                    items.add (textViewItemCode.getText().toString());

                }

                Toast t = Toast.makeText(activity.getApplicationContext(), items.toString(), Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }

    public static void add(final Activity activity, ImageButton btn)
    {
        final LinearLayout linearLayoutForm = (LinearLayout) activity.findViewById(R.id.linearLayoutForm);;

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final LinearLayout newView = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.rowdetail, null);

                newView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                ImageButton btnRemove = (ImageButton) newView.findViewById(R.id.btnRemove);
                btnRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        linearLayoutForm.removeView(newView);
                        System.out.println("in add funct");

                    }
                });

                linearLayoutForm.addView(newView);
            }
        });

    }

    public static void qrToScan (final Activity activity, Button btn){
        final LinearLayout linearLayoutForm = (LinearLayout) activity.findViewById(R.id.linearLayoutForm);;
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final LinearLayout newView = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.rowdetail, null);

                newView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                Button btnScan = (Button) newView.findViewById(R.id.buttonScan);
                btnScan.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        System.out.println("Button Clicked");

                    }
                });

                linearLayoutForm.addView(newView);
            }
        });
    }
}


