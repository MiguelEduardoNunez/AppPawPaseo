import com.example.aplicationpaw.modelos.CrearPeticionRequest
import com.example.aplicationpaw.modelos.RespuestaServidor
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RequestService {
    @POST("peticion")
    fun crearPeticion(@Body request: CrearPeticionRequest): Call<RespuestaServidor>
}