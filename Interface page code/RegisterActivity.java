package com.example.blood_copy;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.blood_copy.Utils.Endpoints;
import com.example.blood_copy.Utils.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

  private EditText nameEt,  passwordEt, mobileEt;
  Spinner bloodGroupEt;
  Spinner  cityEt;
  private Button submitButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    nameEt = findViewById(R.id.name);
    cityEt = findViewById(R.id.city);
    bloodGroupEt = findViewById(R.id.blood_group);
    passwordEt = findViewById(R.id.password);
    submitButton = findViewById(R.id.submitbutton);
    mobileEt = findViewById(R.id.number);
    ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.DEP));
    myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    cityEt.setAdapter(myAdapter);
    ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.blood_grp));
    myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    bloodGroupEt.setAdapter(myAdapter1);

    submitButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        String name, city, blood_group, password, mobile;
        name = nameEt.getText().toString();
        city = cityEt.getSelectedItem().toString();
        blood_group = bloodGroupEt.getSelectedItem().toString();
        password = passwordEt.getText().toString();
        mobile = mobileEt.getText().toString();
        if (isValid(name, city, blood_group, password, mobile)) {
          register(name, city, blood_group, password, mobile);
        }
      }
    });

  }


  private void register(final String name, final String city, final String blood_group, final String password,
      final String mobile) {
    StringRequest stringRequest = new StringRequest(Method.POST, Endpoints.register_url, new Listener<String>() {
      @Override
      public void onResponse(String response) {
        if(response.equals("Success")){
          PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
              .putString("city", city).apply();
          Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
          startActivity(new Intent(RegisterActivity.this, MainActivity.class));
          RegisterActivity.this.finish();
        }else{
          Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
        }
      }
    }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(RegisterActivity.this, "Something went wrong:(", Toast.LENGTH_SHORT).show();
        if(error.getMessage() != null) {
          Log.d("VOLLEY", error.getMessage());
        }
      }
    }){
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("city", city);
        params.put("blood_group", blood_group);
        params.put("password", password);
        params.put("number", mobile);
        return params;
      }
    };
    VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
  }


  private boolean isValid(String name, String city, String blood_group, String password,
      String mobile) {
    List<String> valid_blood_groups = new ArrayList<>();
    valid_blood_groups.add("A+");
    valid_blood_groups.add("A-");
    valid_blood_groups.add("A1-");
    valid_blood_groups.add("B+");
    valid_blood_groups.add("B-");
    valid_blood_groups.add("AB+");
    valid_blood_groups.add("AB-");
    valid_blood_groups.add("O+");
    valid_blood_groups.add("O-");
    if (name.isEmpty()) {
      showMessage("Name is empty");
      return false;
    } else if (city.isEmpty()) {
      showMessage("City name is required");
      return false;
    } else if (!valid_blood_groups.contains(blood_group)) {
      showMessage("Blood group invalid choose from " + valid_blood_groups);
      return false;
    } else if (mobile.length() != 10) {
      showMessage("Invalid mobile number, number should be of 10 digits");
      return false;
    }
    return true;
  }


  private void showMessage(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }


}
