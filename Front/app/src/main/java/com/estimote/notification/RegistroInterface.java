package com.estimote.notification;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Field;
import java.util.List;
import retrofit2.Call;

public interface RegistroInterface {

    @POST("Registro")
    @FormUrlEncoded
    Call<Registro> insertRegistro(@Field("beacon") String beacon, @Field("estado") String estado, @Field("userId") Integer userId);
}
