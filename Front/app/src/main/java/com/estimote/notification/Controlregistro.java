package com.estimote.notification;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.Call;

public interface Controlregistro {

    @POST("Registro")
    @FormUrlEncoded
    Call<Registros> insertRegistros(@Field("becons") String becons, @Field("estado") String estado, @Field("userId") Integer userId);
}
