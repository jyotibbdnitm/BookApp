package com.example.managementofteachingandlearningresourcesfinal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.managementofteachingandlearningresourcesfinal.MyApplication
import com.example.managementofteachingandlearningresourcesfinal.R
import com.example.managementofteachingandlearningresourcesfinal.databinding.RowCommentBinding
import com.example.managementofteachingandlearningresourcesfinal.models.ModelComment
import com.example.managementofteachingandlearningresourcesfinal.models.ModelPdf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterComment: RecyclerView.Adapter<AdapterComment.HolderComment> {
    val context: Context

    val commentArrayList: ArrayList<ModelComment>

    private lateinit var binding: RowCommentBinding

    private lateinit var firebaseAuth: FirebaseAuth

    constructor(context: Context, commentArrayList: ArrayList<ModelComment>) {
        this.context = context
        this.commentArrayList = commentArrayList

        firebaseAuth = FirebaseAuth.getInstance()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {
        binding = RowCommentBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderComment(binding.root)

    }

    override fun onBindViewHolder(holder: HolderComment, position: Int) {

        val model = commentArrayList[position]

        val id = model.id
        val bookId = model.bookId
        val comment = model.uid
        val uid = model.uid
        val timestamp = model.timestamp

        val date =  MyApplication.formetTimeStamp(timestamp.toLong())

        holder.dateTv.text = date
        holder.commentTv.text = comment

        loadUserDetails(model, holder)

        holder.itemView.setOnClickListener {

            if (firebaseAuth.currentUser != null && firebaseAuth.uid == uid){
                deleteCommentDialog(model, holder)
            }
        }
    }

    private fun deleteCommentDialog(model: ModelComment, holder: HolderComment) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("DELETE"){d, e->

                val bookId = model.bookId
                val commentId = model.id


                val ref = FirebaseDatabase.getInstance().getReference("Books")
                ref.child(bookId).child("Comments").child(commentId)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Comment deleted...", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e->
                        Toast.makeText(context, "Failed to delete comment due to ${e.message}", Toast.LENGTH_SHORT).show()

                    }

            }
            .setNegativeButton("CANCEL"){d, e->
                d.dismiss()
            }
            .show()
    }

    private fun loadUserDetails(model: ModelComment, holder: AdapterComment.HolderComment) {
        val uid = model.uid
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"

                    holder.nameTv.text = name
                    try {
                       Glide.with(context)
                           .load(profileImage)
                           .placeholder(R.drawable.ic_person_gray)
                           .into(holder.profileIv)
                    }
                    catch (e: Exception){
                    holder.profileIv.setImageResource(R.drawable.ic_person_gray)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }

    override fun getItemCount(): Int {
        return commentArrayList.size

    }


    inner class HolderComment(itemView: View): RecyclerView.ViewHolder(itemView){
        val profileIv = binding.profileIv
        val nameTv = binding.nameTv
        val dateTv = binding.dateTv
        val commentTv = binding.commentTv


    }

}