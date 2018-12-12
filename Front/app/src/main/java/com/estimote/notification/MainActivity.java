package com.estimote.notification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.EditText;
import android.provider.Settings.Secure;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;

import java.util.List;


import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String baseUrl = "http://192.168.0.100:8080/";
    Button send,actualizar;
    TextView status,resServerUser,componentIdAndroid,notifyText,ipConfig;
    EditText nombreEditText,iPEditText;
    Switch activeNotify;
    private Retrofit retrofit;
    Usuario usuario;
    Controlusuario control_usuario;
    Controlregistro control_registro;
    int uno = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();

        //SET URL
        iPEditText.setText(baseUrl,TextView.BufferType.EDITABLE);
        ipConfig.setText("Url registrada: " + baseUrl);

        //activar eventos
        send.setOnClickListener(this);
        actualizar.setOnClickListener(this);

        control_usuario = initServerUser();
        control_registro = initServerRegistro();

        // obtener Id de android
        String idAndroid = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        componentIdAndroid.setText("Id android: " + idAndroid);

        Call<Usuario> respuestaServerInit = control_usuario.getUsuariosByAndroid(idAndroid);

        respuestaServerInit.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()){
                    usuario = response.body();
                    nombreEditText.setText(usuario.getNombre(),TextView.BufferType.EDITABLE);
                    resServerUser.setText(usuario.getIdAndroid() + " " + usuario.getId() + " " + usuario.getNombre());
                    status.setText("Usuario registrado.");
                    uno = 1;
                    startMonitoring();
                }else{
                    status.setText("Usuario no registrado.");
                    resServerUser.setText(response.toString());
                    usuario = null;
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                resServerUser.setText("error--------");
            }
        });
    }

    private void startMonitoring() {
        final MyApplication application = (MyApplication) getApplication();

        application.setData(notifyText,activeNotify,control_registro,usuario.getId());

        RequirementsWizardFactory
            .createEstimoteRequirementsWizard()
            .fulfillRequirements(this,
                new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        Log.d("app", "requirements fulfilled");
                        application.enableBeaconNotifications();
                        return null;
                    }
                },
                new Function1<List<? extends Requirement>, Unit>() {
                    @Override
                    public Unit invoke(List<? extends Requirement> requirements) {
                        Log.e("app", "requirements missing: " + requirements);
                        return null;
                    }
                },
                new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "requirements error: " + throwable);
                        return null;
                    }
                });
    }

    private Controlusuario initServerUser() {
        //captura de datos
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(Controlusuario.class);
    }
    private Controlregistro initServerRegistro() {
        //captura de datos
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(Controlregistro.class);
    }

    private void initComponent() {
        //inicializacion de interfaz
        send = (Button) findViewById(R.id.sendd);
        componentIdAndroid = (TextView) findViewById(R.id.androidId);
        status = (TextView) findViewById(R.id.status);
        nombreEditText = (EditText) findViewById(R.id.editText);
        resServerUser = (TextView) findViewById(R.id.resServerUser);
        notifyText = (TextView) findViewById(R.id.notify);
        activeNotify = (Switch) findViewById(R.id.ActiveNotify);
        iPEditText = (EditText) findViewById(R.id.editTextIP);
        ipConfig = (TextView) findViewById(R.id.iPConfig);
        actualizar = (Button) findViewById(R.id.buttonConfig);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendd:
                String nombre = nombreEditText.getText().toString().trim();
                String id_android = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
                if(!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(id_android)) {
                    if (uno == 1){
                        sendPut(usuario.getId(), nombre);
                    }else{
                        uno = 1;
                        sendPost(id_android, nombre);
                    }

                }else{
                    status.setText("Datos insuficientes");
                }
                break;
            case R.id.buttonConfig:
                String auxUrl = iPEditText.getText().toString().trim();
                if(!baseUrl.isEmpty()){
                    baseUrl = auxUrl;
                    ipConfig.setText("Dirección registrada: " + baseUrl);
                    iPEditText.setText(baseUrl,TextView.BufferType.EDITABLE);
                }else{
                    ipConfig.setText("Error");
                }
                break;
            default:
                break;
        }
    }

    public void sendPost(String id_android, String nombre) {
        Call<Usuario> resp = control_usuario.insertUsuario(id_android, nombre);

        resp.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()){
                    usuario = response.body();
                    status.setText("Usuario registrado");
                    startMonitoring();
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if(call.isCanceled()) {
                    Log.e("chao", "request was aborted");
                }else {
                    Log.e("chao", "Unable to submit post to API.");
                }
            }
        });
    }


    public void sendPut(Integer id,  String nombre) {
        Call<Usuario> resp = control_usuario.updateUsuario(id, nombre);

        resp.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()){
                    usuario.setNombre(response.body().getNombre());
                    resServerUser.setText(usuario.getIdAndroid() + " " + usuario.getId() + " " + usuario.getNombre());
                    status.setText("Usuario actualizado");
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if(call.isCanceled()) {
                    Log.e("chao", "request was aborted");
                }else {
                    Log.e("chao", "Unable to submit post to API.");
                }
            }
        });
    }
}
