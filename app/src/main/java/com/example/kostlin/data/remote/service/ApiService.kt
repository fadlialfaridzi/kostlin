package com.example.kostlin.data.remote.service

import com.example.kostlin.data.remote.dto.BaseResponse
import com.example.kostlin.data.remote.dto.auth.AuthResponseDto
import com.example.kostlin.data.remote.dto.auth.LoginRequestDto
import com.example.kostlin.data.remote.dto.auth.RegisterRequestDto
import com.example.kostlin.data.remote.dto.auth.RequestResetPasswordDto
import com.example.kostlin.data.remote.dto.auth.ResetPasswordDto
import com.example.kostlin.data.remote.dto.auth.UserDto
import com.example.kostlin.data.remote.dto.auth.VerifyOtpDto
import com.example.kostlin.data.remote.dto.booking.BookingDto
import com.example.kostlin.data.remote.dto.booking.BookingRequestDto
import com.example.kostlin.data.remote.dto.favorite.FavoriteDto
import com.example.kostlin.data.remote.dto.kos.KosDto
import com.example.kostlin.data.remote.dto.kos.KosImageDto
import com.example.kostlin.data.remote.dto.review.ReviewDto
import com.example.kostlin.data.remote.dto.review.ReviewRequestDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): BaseResponse<AuthResponseDto>

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): BaseResponse<AuthResponseDto>

    @POST("api/auth/request-reset")
    suspend fun requestResetPassword(@Body body: RequestResetPasswordDto): BaseResponse<Unit>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpDto): BaseResponse<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordDto): BaseResponse<Unit>

    @GET("api/users/me")
    suspend fun getProfile(): BaseResponse<UserDto>

    @PUT("api/users/me/password")
    suspend fun changePassword(@Body body: Map<String, String>): BaseResponse<Any?>

    // Kos endpoints
    @GET("api/kos/popular")
    suspend fun getPopularKos(): BaseResponse<List<KosDto>>

    @GET("api/kos/recommended")
    suspend fun getRecommendedKos(): BaseResponse<List<KosDto>>

    @GET("api/kos")
    suspend fun getKosList(
        @Query("type") type: String? = null,
        @Query("minPrice") minPrice: Int? = null,
        @Query("maxPrice") maxPrice: Int? = null,
        @Query("search") search: String? = null
    ): BaseResponse<List<KosDto>>

    @GET("api/kos/{id}")
    suspend fun getKosDetail(@Path("id") kosId: String): BaseResponse<KosDto>

    @GET("api/kos/my")
    suspend fun getMyKos(): BaseResponse<List<KosDto>>

    @POST("api/kos")
    suspend fun createKos(@Body body: com.example.kostlin.data.remote.dto.kos.CreateKosRequestDto): BaseResponse<KosDto>

    @PUT("api/kos/{id}")
    suspend fun updateKos(@Path("id") kosId: Int, @Body body: com.example.kostlin.data.remote.dto.kos.CreateKosRequestDto): BaseResponse<KosDto>

    @PATCH("api/kos/{id}/toggle")
    suspend fun toggleKosActive(@Path("id") kosId: Int, @Body body: Map<String, Boolean>): BaseResponse<KosDto>

    @DELETE("api/kos/{id}")
    suspend fun deleteKos(@Path("id") kosId: Int): BaseResponse<Any?>

    // Kos Image Upload
    @Multipart
    @POST("api/upload/kos/{kosId}/images")
    suspend fun uploadKosImages(
        @Path("kosId") kosId: Int,
        @Part images: List<MultipartBody.Part>
    ): BaseResponse<List<KosImageDto>>

    // Favorite
    @GET("api/favorites")
    suspend fun listFavorites(@Query("includeHistory") includeHistory: Boolean = false): BaseResponse<List<FavoriteDto>>

    @POST("api/favorites")
    suspend fun addFavorite(@Body body: Map<String, Int>): BaseResponse<FavoriteDto>

    @DELETE("api/favorites/{kosId}")
    suspend fun removeFavorite(@Path("kosId") kosId: Int): BaseResponse<Any?>

    // Bookings
    @GET("api/bookings")
    suspend fun getBookings(): BaseResponse<List<BookingDto>>

    @GET("api/bookings/requests")
    suspend fun getBookingRequests(): BaseResponse<List<BookingDto>>

    @POST("api/bookings")
    suspend fun createBooking(@Body body: BookingRequestDto): BaseResponse<BookingDto>

    @PATCH("api/bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") bookingId: Int,
        @Body body: Map<String, String>
    ): BaseResponse<BookingDto>

    // Reviews
    @GET("api/reviews/{kosId}")
    suspend fun getReviews(
        @Path("kosId") kosId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): BaseResponse<List<ReviewDto>>

    @POST("api/reviews/{kosId}")
    suspend fun createReview(
        @Path("kosId") kosId: String,
        @Body body: ReviewRequestDto
    ): BaseResponse<ReviewDto>
    
    // FCM Token
    @POST("api/users/fcm-token")
    suspend fun updateFcmToken(@Body body: Map<String, String>): BaseResponse<Any?>
}
