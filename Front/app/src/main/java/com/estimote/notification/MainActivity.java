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

import java.util.ArrayList;
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
    private String baseUrl = "http://172.20.10.2:8080/";
    Button send,actualizar;
    TextView status,resServerUser,componentIdAndroid,notifyText,ipConfig;
    EditText nombreEditText,iPEditText;
    Switch activeNotify;
    private Retrofit retrofit;
    User user;
    UserInterface userInterface;
    RegistroInterface registroInterface;
    int uno = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();

        //SET URL
        iPEditText.setText(baseUrl,TextView.BufferType.EDITABLE);
        ipConfig.setText("Url: " + baseUrl);

        //activar eventos
        send.setOnClickListener(this);
        actualizar.setOnClickListener(this);

        userInterface = initServerUser();
        registroInterface = initServerRegistro();

        // obtener Id de android
        String idAndroid = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        componentIdAndroid.setText("Id android: " + idAndroid);

        Call<User> respuestaServerInit = userInterface.getUserByAndroid(idAndroid);

        respuestaServerInit.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    user = response.body();
                    nombreEditText.setText(user.getNombre(),TextView.BufferType.EDITABLE);
                    resServerUser.setText(user.getIdAndroid() + " " + user.getId() + " " + user.getNombre());
                    status.setText("Usuario registrado.");
                    uno = 1;
                    startMonitoring();
                }else{
                    status.setText("Usuario no registrado.");
                    resServerUser.setText(response.toString());
                    user = null;
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                resServerUser.setText("error al registrar usuario");
            }
        });
    }

    private void startMonitoring() {
        final MyApplication application = (MyApplication) getApplication();

        application.setData(notifyText,activeNotify,registroInterface,user.getId());

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

    private UserInterface initServerUser() {
        //captura de datos
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(UserInterface.class);
    }
    private RegistroInterface initServerRegistro() {
        //captura de datos
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RegistroInterface.class);
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
                        sendPut(user.getId(), nombre);
                    }else{
                        uno = 1;
                        sendPost(id_android, nombre);
                    }

                }else{
                    status.setText("faltan datos");
                }
                break;
            case R.id.buttonConfig:
                String auxUrl = iPEditText.getText().toString().trim();
                if(!baseUrl.isEmpty()){
                    baseUrl = auxUrl;
                    ipConfig.setText("Url: " + baseUrl);
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
        Call<User> resp = userInterface.insertUser(id_android, nombre);

        resp.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    user = response.body();
                    status.setText("Usuario registrado");
                    startMonitoring();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if(call.isCanceled()) {
                    Log.e("chao", "request was aborted");
                }else {
                    Log.e("chao", "Unable to submit post to API.");
                }
            }
        });
    }


    public void sendPut(Integer id,  String nombre) {
        Call<User> resp = userInterface.updateUser(id, nombre);

        resp.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    user.setNombre(response.body().getNombre());
                    resServerUser.setText(user.getIdAndroid() + " " + user.getId() + " " + user.getNombre());
                    status.setText("Usuario actualizado");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if(call.isCanceled()) {
                    Log.e("chao", "request was aborted");
                }else {
                    Log.e("chao", "Unable to submit post to API.");
                }
            }
        });
    }
}
