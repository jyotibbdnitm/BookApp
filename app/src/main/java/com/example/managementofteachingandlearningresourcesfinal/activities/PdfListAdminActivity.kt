package com.example.managementofteachingandlearningresourcesfinal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.managementofteachingandlearningresourcesfinal.adapters.AdapterPdfAdmin
import com.example.managementofteachingandlearningresourcesfinal.databinding.ActivityPdfListAdminBinding
import com.example.managementofteachingandlearningresourcesfinal.models.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfListAdminActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPdfListAdminBinding
    
    private companion object{
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    private var categoryId = ""
    private var category = ""
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListAdminBinding.inflate(layoutInflater) 
        setContentView(binding.root)

        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!
        
        binding.subTitleTv.text = category
        
        loadPdfList()
        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                
            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
               try {
                   adapterPdfAdmin.filter!!.filter(s)
               }
              catch (e: Exception){
                  android.util.Log.d(TAG, "onTextChanged: ${e.message}")
              }

            }

            override fun afterTextChanged(p0: Editable?) {
                
            }
        })

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadPdfList() {
       pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId:").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                  pdfArrayList.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(ModelPdf::class.java)
                        if (model != null) {
                            pdfArrayList.add(model)
                            android.util.Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId} ")
                        }
                
                    }
                    adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity, pdfArrayList)
                    binding.booksRv.adapter = adapterPdfAdmin
                }

                override fun onCancelled(error: DatabaseError) {
                   
                }
            })
    }
}