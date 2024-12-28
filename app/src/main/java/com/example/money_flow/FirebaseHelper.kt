import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

object FirebaseHelper {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun saveFinancePlan(context: Context, totalIncome: Long, needs: Long, wants: Long, savings: Long, onSuccessListener: () -> Unit, onFailureListener: (Exception) -> Unit) {
        val data = hashMapOf(
            "total_income" to totalIncome,
            "allocation" to mapOf("needs" to needs, "wants" to wants, "savings" to savings)
        )
        currentUser?.let {
            db.collection("users").document(it.uid).set(data)
                .addOnSuccessListener {
                    Toast.makeText(context, "Plan saved successfully", Toast.LENGTH_SHORT).show()
                    onSuccessListener()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to save plan: ${exception.message}", Toast.LENGTH_SHORT).show()
                    onFailureListener(exception)
                }
        }
    }

    fun logExpense(context: Context, category: String, amount: Long, onSuccessListener: () -> Unit, onFailureListener: (Exception) -> Unit) {
        val data = hashMapOf(
            "userId" to currentUser?.uid,
            "category" to category,
            "amount" to amount,
            "date" to Timestamp.now()
        )
        db.collection("expenses").add(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Expense logged successfully", Toast.LENGTH_SHORT).show()
                onSuccessListener()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to log expense: ${exception.message}", Toast.LENGTH_SHORT).show()
                onFailureListener(exception)
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