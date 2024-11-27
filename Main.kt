import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

fun main() {
    runBlocking {
        val products = fetchProducts()
        products?.forEach { product ->
            println("${product.name} - ${product.currency} ${product.price}")
        }
    }
}

// Define the Product data class
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String,
    val inStock: Boolean
)

suspend fun fetchProducts(): List<Product>? {
    val url = "https://jsonkeeper.com/b/MX0A"
    val client = OkHttpClient()

    return withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            response.body?.string()?.let { responseBody ->
                val json = JSONObject(responseBody)
                val productsJsonArray = json.getJSONArray("products")

                List(productsJsonArray.length()) { index ->
                    val productJson = productsJsonArray.getJSONObject(index)
                    Product(
                        id = productJson.getInt("id"),
                        name = productJson.getString("name"),
                        description = productJson.getString("description"),
                        price = productJson.getDouble("price"),
                        currency = productJson.getString("currency"),
                        inStock = productJson.getBoolean("in_stock")
                    )
                }
            }
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
            null
        }
    }
}
