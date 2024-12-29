package com.example.money_flow.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.money_flow.User1

import com.example.money_flow.databinding.FragmentPlanBinding
import com.google.firebase.firestore.FirebaseFirestore

class PlanFragment : Fragment() {
    private var _binding: FragmentPlanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonSavePlan.setOnClickListener {
            saveFinancePlan()

        }

        return root
    }

    private fun saveFinancePlan() {
        val username = User1.username // Assuming User1.username is accessible
        val totalAmount = binding.inputTotalSalary.text.toString().toLongOrNull() ?: 0 // Default to 0 if null
        val savings = binding.inputSavings.text.toString().toLongOrNull() ?: 0 // Default to 0 if null
        val needs = binding.inputNeeds.text.toString().toLongOrNull() ?: 0 // Default to 0 if null
        val wants = binding.inputWants.text.toString().toLongOrNull() ?: 0 // Default to 0 if null

        if(totalAmount==0L||savings==0L||needs==0L||wants==0L){
            Toast.makeText(requireContext(), "Please enter valid values", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseHelper.saveFinancePlan(
            requireContext(), // Pass the context
            totalAmount,
            needs,
            wants,
            savings,
            onSuccessListener = {
                // Data saved successfully, update UI or navigate
                Toast.makeText(requireContext(), "Plan saved successfully", Toast.LENGTH_SHORT).show()

            },
            onFailureListener = { exception ->
                // Handle error, display message or retry
                    Log.d("exception","Error")

            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}