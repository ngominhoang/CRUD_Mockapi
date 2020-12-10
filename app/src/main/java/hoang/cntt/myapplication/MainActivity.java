package hoang.cntt.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<PhuKien> phuKiens = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private PhuKienAdapter phuKienAdapter;

    Dialog dialog;

    private String url="https://5fd1ff27b485ea0016eef243.mockapi.io/phukien";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh= findViewById(R.id.swipedown);
        recyclerView= findViewById(R.id.rcv_phukien);

        dialog= new Dialog(this);
        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                phuKiens.clear();
                GetData();
            }
        });

    }

    private void GetData(){

        refresh.setRefreshing(true);

        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject= null;
                for (int  i = 0;i<response.length(); i++){
                    try {
                        jsonObject= response.getJSONObject(i);

                        PhuKien s= new PhuKien();
                        s.setId(jsonObject.getInt("id"));
                        s.setName(jsonObject.getString("name"));
                        phuKiens.add(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapterPush(phuKiens);
                refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error make by API server!", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<PhuKien> phuKiens) {
        phuKienAdapter = new PhuKienAdapter(this, phuKiens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(phuKienAdapter);

    }

    public void addPhuKien(View v){
        TextView close;
        EditText edt_name;
        Button btnSubmit;

        dialog.setContentView(R.layout.activity_modcat);


        close= dialog.findViewById(R.id.txt_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edt_name= dialog.findViewById(R.id.name);
        btnSubmit= dialog.findViewById(R.id.submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data= edt_name.getText().toString();
                Submit(data);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void Submit(String data) {
        StringRequest request= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                refresh.post(new Runnable() {
                    @Override
                    public void run() {
                        phuKiens.clear();
                        GetData();
                    }
                });
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<>();
                params.put("name", data);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onRefresh() {
        phuKiens.clear();
        GetData();
    }
}