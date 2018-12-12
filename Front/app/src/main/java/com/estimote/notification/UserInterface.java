package com.estimote.notification;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserInterface {

    @GET("User/id_android/{idandroid}")
    Call<User> getUserByAndroid(@Path("idandroid") String id_android);

    @POST("User")
    @FormUrlEncoded
    Call<User> insertUser(@Field("id_android") String id_android, @Field("nombre") String nombre);

    @PUT("User/{id}")
    @FormUrlEncoded
    Call<User> updateUser(@Path("id") Integer id, @Field("nombre") String nombre);
}
