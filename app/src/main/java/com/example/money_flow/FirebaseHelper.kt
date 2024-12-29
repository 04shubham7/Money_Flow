import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

object FirebaseHelper {

    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun saveFinancePlan(context: Context, totalIncome: Long, needs: Long, wants: Long, savings: Long, onSuccessListener: () -> Unit, onFailureListener: (Exception) -> Unit) {


        // Check if the user is authenticated
        if (currentUser == null) {
            Log.e("FirebaseHelper", "No authenticated user found.")
            Toast.makeText(context, "Please log in to save your plan.", Toast.LENGTH_SHORT).show()
            return // Exit the function early if no user is authenticated
        }

        val data = hashMapOf(
            "total_income" to totalIncome,
            "allocation" to mapOf("needs" to needs, "wants" to wants, "savings" to savings)
        )

        Log.d("FirebaseHelper", "Saving Plan data: $data")
        db.collection("users").document(currentUser.uid).collection("plans").document("plan").set(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Plan saved successfully", Toast.LENGTH_SHORT).show()
                onSuccessListener()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to save plan: ${exception.message}", Toast.LENGTH_SHORT).show()
                onFailureListener(exception)
            }
    }



    fun logExpense(
        context: Context,
        category: String,
        amount: Long,
        onSuccessListener: () -> Unit,
        onFailureListener: (Exception) -> Unit
    ) {
        if (category.isBlank() || amount <= 0) {
            Toast.makeText(context, "Invalid expense details.", Toast.LENGTH_SHORT).show()
            return
        }

        currentUser?.let { user ->
            val data = hashMapOf(
                "userId" to user.uid,
                "category" to category,
                "amount" to amount,
                "date" to Timestamp.now()
            )

            db.collection("users").document(user.uid).collection("expenses").add(data)
                .addOnSuccessListener {
                    Log.d("FirebaseHelper", "Expense logged successfully: $data")
                    Toast.makeText(context, "Expense logged successfully", Toast.LENGTH_SHORT).show()
                    onSuccessListener()
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseHelper", "Error logging expense", exception)
                    Toast.makeText(context, "Failed to log expense: ${exception.message}", Toast.LENGTH_SHORT).show()
                    onFailureListener(exception)
                }
        } ?: run {
            Log.e("FirebaseHelper", "No authenticated user found.")
            Toast.makeText(context, "User not authenticated. Please sign in.", Toast.LENGTH_SHORT).show()
        }
    }


    fun saveReminder(title: String, description: String, date: Timestamp, onSuccessListener: () -> Unit, onFailureListener: (Exception) -> Unit) {
        val data = hashMapOf(
            "title" to title,
            "description" to description,
            "date" to date
        )
        db.collection("reminders").add(data)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
}