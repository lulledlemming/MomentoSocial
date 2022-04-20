package com.dandy.momento

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import com.canhub.cropper.CropImage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_new_post.*

class NewPostActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference ?= null
    private lateinit var publishPostButton: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")
        publishPostButton = findViewById(R.id.publishNewPost)

        CropImage.launch()

        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this)

        publishPostButton.setOnClickListener{
            uploadImage()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getPickImageResultUriContent(this, data)
            imageUri = result.uri
            newPostImage.setImageURI(imageUri)
        }

    }

    private fun uploadImage() {
        when{
            imageUri == null -> Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(newPostCaption.text.toString()) -> Toast.makeText(this, "Write something...", Toast.LENGTH_SHORT).show()

            else -> {
                var fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri> { task ->
                    if  (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push()

                        val postMap = HashMap<String, Any>()
                        postMap["postId"] = postId!!
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postCaption"] = newPostCaption.text.toString().toLowerCase()
                        postMap["postImage"] = myUrl

                        ref.child(postId.toString()).updateChildren(postMap)

                        Toast.makeText(this, "Post saved!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Unable to save post to database.", Toast.LENGTH_SHORT).show()

                    }
                })


            }
        }

    }
}