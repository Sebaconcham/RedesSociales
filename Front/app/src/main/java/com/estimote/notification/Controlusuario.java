package com.estimote.notification;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Controlusuario {

    @POST("User")
    @FormUrlEncoded
    Call<Usuario> insertUsuario(@Field("id_android") String id_android, @Field("name") String nombre);

    @GET("User/id_android/{idandroid}")
    Call<Usuario> getUsuariosByAndroid(@Path("idandroid") String id_android);

    @PUT("User/{id}")
    @FormUrlEncoded
    Call<Usuario> updateUsuario(@Path("id") Integer id, @Field("name") String nombre);

    @GET("User")
    Call<List<Usuario>> getUsuarios();

}
