package com.example.peticion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView txtId, txtApellidoPaterno,txtApellidoMaterno, txtNombre,txtDireccion,txtTelefono,txtEdad;
    private Button btnPost,btnGet,btnPut,btnDelete;
    ListView lstLista;
    String url = "http://192.168.11.21:5000/api/empleados";
    RequestQueue colaPeticion;

    ArrayList<String> datos;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtId = findViewById(R.id.txtId);
        txtApellidoPaterno = findViewById(R.id.txtApellidoPaterno);
        txtApellidoMaterno = findViewById(R.id.txtApellidMaterno);
        txtNombre = findViewById(R.id.txtNombre);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtEdad = findViewById(R.id.txtEdad);

        btnPost = findViewById(R.id.btnPost);
        btnGet = findViewById(R.id.btnGet);
        btnPut = findViewById(R.id.btnPut);
        btnDelete = findViewById(R.id.btnDelete);

        lstLista = findViewById(R.id.lstLista);

        colaPeticion = Volley.newRequestQueue(this);

        eventosButtons();
        eventoLista();
        getAll();
    }

    private void eventosButtons()
    {
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOne();
            }
        });
        btnPost.setOnClickListener(view -> postOne());
        btnPut.setOnClickListener(view -> putData());
        btnDelete.setOnClickListener(view -> deleteOne());
    }
    private void eventoLista() {
        lstLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String [] ide = datos.get(position).split(":");
                txtId.setText(ide[0]);
            }
        });
    }
public void getOne(){
        eventoLista();

    if (!txtId.getText().equals(""))
    {
        String ide = txtId.getText().toString();

        JsonObjectRequest request_getOne = new JsonObjectRequest(Request.Method.GET, url + "/" + ide, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    txtApellidoPaterno.setText(response.getString("apellidoPaterno"));
                    txtApellidoMaterno.setText(response.getString("apellidoMaterno"));
                    txtNombre.setText(response.getString("nombre"));
                    txtDireccion.setText(response.getString("direccion"));
                    txtTelefono.setText(response.getString("telefono"));
                    txtEdad.setText(response.getString("edad"));
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Error on TRY ->"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "getOne error", Toast.LENGTH_SHORT).show();
            }
        });
        colaPeticion.add(request_getOne);
    }
}
    public void getAll(){
        JsonArrayRequest request_getAll = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                datos = new ArrayList<>();
                for (int i = 0; i< response.length(); i++)
                {
                    try {
                        JSONObject datoJson = response.getJSONObject(i);
                        String dato = datoJson.getString("_id")+ " : "+
                                datoJson.get("apellidoPaterno") +" : "+
                                datoJson.get("apellidoMaterno") +" : "+
                                datoJson.get("nombre") +" : "+
                                datoJson.get("direccion") +" : "+
                               datoJson.get("telefono")+ " : "+
                                datoJson.get("edad");
                        datos.add(dato);
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Error en TRy ->" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,datos);
                lstLista.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "onError GET -->" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        colaPeticion.add(request_getAll);
    }
    public void postOne() {
        JSONObject json = new JSONObject();
        try {
            json.put("apellidoPaterno", txtApellidoPaterno.getText().toString());
            json.put("apellidoMaterno", txtApellidoMaterno.getText().toString());
            json.put("nombre",txtNombre.getText().toString());
            json.put("direccion",txtDireccion.getText().toString());
            json.put("telefono",txtTelefono.getText().toString());
            json.put("edad",Integer.parseInt(txtEdad.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonPost = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("status"))
                {
                    Toast.makeText(MainActivity.this,  txtNombre.getText().toString()+" Agregado con exito", Toast.LENGTH_SHORT).show();
                    getAll();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "On error POST-->" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        colaPeticion.add(jsonPost);
        limpiar();
    }
    public void putData(){
        if (!txtId.getText().equals(""))
        {
            String ide = txtId.getText().toString();

            JSONObject json = new JSONObject();
            try {
                json.put("apellidoPaterno", txtApellidoPaterno.getText().toString());
                json.put("apellidoMaterno", txtApellidoMaterno.getText().toString());
                json.put("nombre",txtNombre.getText().toString());
                json.put("direccion",txtDireccion.getText().toString());
                json.put("telefono",txtTelefono.getText().toString());
                json.put("edad",Integer.parseInt(txtEdad.getText().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectPut = new JsonObjectRequest(Request.Method.PUT, url + "/" + ide, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.has("status"))
                    {
                        Toast.makeText(MainActivity.this,  "Acualizado con éxito", Toast.LENGTH_SHORT).show();
                        getAll();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "On error PUT-->" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            colaPeticion.add(jsonObjectPut);
            limpiar();
        }
    }
    public void deleteOne()
    {
        if (!txtId.getText().equals(""))
        {
            String ide = txtId.getText().toString();

            String name_employed = txtNombre.getText().toString();
            JsonObjectRequest jsonObjectDelete = new JsonObjectRequest(Request.Method.DELETE, url + "/" + ide, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.has("status"))
                    {
                        Toast.makeText(MainActivity.this,  " "+ name_employed + " Eliminado ", Toast.LENGTH_SHORT).show();
                        getAll();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "On error PUT-->" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            colaPeticion.add(jsonObjectDelete);
        }
        limpiar();
    }
    private  void limpiar()
    {
        txtId.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEdad.setText("");
    }
}