package com.example.shalehatbooking.sendNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAGqTWOUs:APA91bGDqnvbkXYYB_xBH7Da3XxZUGeaYu1zzzu_meukEuXBRQee-9Iel_a9bmkBSRidEJm5EOPtdST4Raez78nhrk7bClZGU_TFWS-ekOdLILN-HkaqzRp1kPiecpeem2PduZkv-OF1"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
