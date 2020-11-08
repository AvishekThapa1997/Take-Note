package com.app.takenote.ui


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.app.takenote.R
import com.app.takenote.adapter.NoteAdapter
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.helper.ClickListener
import com.app.takenote.pojo.Note
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewholder.NoteViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.ref.WeakReference

class HomeActivity : BaseActivity(), View.OnClickListener, ClickListener {

    override val layoutResourceId: Int = R.layout.activity_home
    private var currentUser: User? = null
    private lateinit var realTimeListener: ListenerRegistration
    private lateinit var fireStoreAdapter: FirestoreRecyclerAdapter<Note, NoteViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val bundle = intent.getBundleExtra(BUNDLE)
        currentUser = bundle?.getParcelable(CURRENT_USER)
        setUpRecyclerView()
        currentUser?.let {
            if (!it.imageUri.isEmptyOrIsBlank()) {
                startShimmer()
                showProfileImage(it)
            } else {
                if (!it.fullName.isEmptyOrIsBlank()) {
                    showProfileName(it)
                    stopShimmer()
                }
            }
        }
        profileImage.setOnClickListener(this)
        profile.setOnClickListener(this)
        addNote.setOnClickListener {
            startIntentFor(NoteUploadActivity::class.java, currentUser)
        }
    }

    override fun onResume() {
        super.onResume()
        realTimeListener = fireStore.collection(COLLECTION_NAME).document(currentUser?.uid!!)
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, _ ->
                if (documentSnapshot != null && documentSnapshot.data != null) {
                    val updatedFullName =
                        documentSnapshot[FULL_NAME].toString()
                    val updatedImageUri =
                        documentSnapshot[IMAGE_URL].toString()
                    if (currentUser?.imageUri != updatedImageUri) {
                        currentUser = currentUser?.copy(imageUri = updatedImageUri)
                        startShimmer()
                        showProfileImage(currentUser!!)
                    }
                    if (currentUser?.fullName != updatedFullName) {
                        currentUser = currentUser?.copy(fullName = updatedFullName)
                        if (currentUser?.imageUri.isEmptyOrIsBlank())
                            showProfileName(currentUser!!)
                    }
                }
            }
    }

    override fun onPause() {
        super.onPause()
        realTimeListener.remove()
    }

    private fun showProfileImage(currentUser: User) {
        if (profile.isVisible)
            profile.hideView(View.INVISIBLE)
        showImage(currentUser.imageUri!!, profileImage, {
            if (shimmerLayout.isShimmerVisible)
                stopShimmer()
        }, {
            if (shimmerLayout.isShimmerVisible)
                stopShimmer()
            showProfileName(currentUser)
        })
        profileImage.showView()
    }

    private fun startShimmer() {
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
    }

    private fun showProfileName(currentUser: User) {
        profile.text = currentUser.fullName!![0].toString()
        profile.showView()
    }

    private fun setUpRecyclerView() {
        val query = fireStore.collection(NOTE_COLLECTION).whereEqualTo(AUTHOR_ID, currentUser?.uid)
        val recyclerOptions =
            FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note::class.java)
                .setLifecycleOwner(this).build()
        fireStoreAdapter = NoteAdapter(recyclerOptions, WeakReference(this))
        noteList.setHasFixedSize(true)
        noteList.adapter = fireStoreAdapter
    }

    override fun enabledFullScreen(): Boolean = false
    override fun onClick(v: View?) =
        startIntentFor(ProfileActivity::class.java, currentUser)

    override fun onClick(note: Note) {
        val intent = Intent(applicationContext, NoteUploadActivity::class.java)
        intent.putExtra(CURRENT_NOTE, note)
        startActivity(intent)
    }

}
