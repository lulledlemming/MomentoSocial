package com.dandy.momento

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCrop.getOutput
import kotlinx.android.synthetic.main.activity_new_post.*
import java.io.File

class NewPostActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference ?= null
    private lateinit var publishPostButton: ImageView
    private lateinit var closePostPageButton: ImageView
    private lateinit var newPostImageSelector: ImageView

    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCrop = UCrop.of(inputUri, outputUri)
                .withAspectRatio(2F, 1F)
                .withMaxResultSize(1720, 860)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return UCrop.getOutput(intent!!)!!
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        val inputUri = uri.toString().toUri()
        val outputUri = File(filesDir, System.currentTimeMillis().toString()+".jpg").toUri()

        val listUri = listOf<Uri>(inputUri, outputUri)
        cropImage.launch(listUri)
    }

    private val cropImage = registerForActivityResult(uCropContract){
        val uri = intent.data
        newPostImage.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")
        publishPostButton = findViewById(R.id.publishNewPost)
        closePostPageButton = findViewById(R.id.closeAddPost)
        newPostImageSelector = findViewById(R.id.newPostImage)

        publishPostButton.setOnClickListener{
            uploadImage()
        }
        closePostPageButton.setOnClickListener {
            val closeIntent = Intent(this, MainActivity::class.java)
            startActivity(closeIntent)
            finish()
        }
        newPostImageSelector.setOnClickListener {
            getContent.launch("image/*")
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data !=null){
            val result = UCrop.getOutput(intent)
            imageUri = result
            newPostImageSelector.setImageURI(imageUri)
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