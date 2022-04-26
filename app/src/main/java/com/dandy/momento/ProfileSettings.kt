package com.dandy.momento

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.dandy.momento.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_new_post.*
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettings : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var buttonLogOut: Button
    private lateinit var exitSettingsButton: ImageView
    private lateinit var saveChangesButton: ImageView
    private lateinit var changeImageClicker: TextView
    private var checker = ""
    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storageProfilePicRef: StorageReference?= null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        buttonLogOut = findViewById(R.id.buttonLogOut)
        exitSettingsButton = findViewById(R.id.closeProfileSettings)
        saveChangesButton = findViewById(R.id.saveProfileSettings)
        changeImageClicker = findViewById(R.id.changeImageClickable)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        saveChangesButton.setOnClickListener{
            if (checker == "clicked"){
                uploadImageAndUpdateInfo()
            }  else {
                updateUserInfoOnly()
            }

        }
        exitSettingsButton.setOnClickListener{

        }

        changeImageClicker.setOnClickListener{
            checker = "clicked"
//            CropImage.activity()
//                .setAspectRatio( 1, 1)
//                .start(this)
        }

        buttonLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        userInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data !=null){
            val result = UCrop.getOutput(intent)
            imageUri = result
            newPostImage.setImageURI(imageUri)
        }
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_user_avatar_light).into(changeProfileImage)

                    changeProfileName.setText(user!!.getUsername())
                    changeUserName.setText(user!!.getFullName())
                    changeProfileBio.setText(user!!.getBio())
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun uploadImageAndUpdateInfo() {
        when{
            imageUri == null -> Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(changeProfileName.text.toString()) -> Toast.makeText(this, "Please input your full name.", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(changeUserName.text.toString()) -> Toast.makeText(this, "Please input a username.", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(changeProfileBio.text.toString()) -> Toast.makeText(this, "Please write up your bio.", Toast.LENGTH_SHORT).show()

            else  -> {
                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

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

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullName"] = changeProfileName.text.toString().toLowerCase()
                        userMap["userName"] = changeUserName.text.toString().toLowerCase()
                        userMap["bio"] = changeProfileBio.text.toString().toLowerCase()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Profile information updated successfully!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Unable to update profile information :(", Toast.LENGTH_SHORT).show()

                    }
                })
            }
        }
    }

    private fun updateUserInfoOnly() {

        if (TextUtils.isEmpty(changeProfileName.text.toString())){
            Toast.makeText(this, "Please input your full name.", Toast.LENGTH_SHORT).show()

        } else if (TextUtils.isEmpty(changeUserName.text.toString())){
            Toast.makeText(this, "Please input a username.", Toast.LENGTH_SHORT).show()

        } else if (TextUtils.isEmpty(changeProfileBio.text.toString())){
            Toast.makeText(this, "Please write up your bio.", Toast.LENGTH_SHORT).show()

        } else {
            val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

            val userMap = HashMap<String, Any>()
            userMap["fullName"] = changeProfileName.text.toString().toLowerCase()
            userMap["userName"] = changeUserName.text.toString().toLowerCase()
            userMap["bio"] = changeProfileBio.text.toString().toLowerCase()

            usersRef.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(this, "Profile information updated successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}