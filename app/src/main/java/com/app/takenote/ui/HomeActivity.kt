package com.app.takenote.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import com.app.takenote.R
import com.app.takenote.adapter.NoteAdapter
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.helper.ClickListener
import com.app.takenote.helper.RecyclerViewSwipeCallBack
import com.app.takenote.pojo.Note
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.HomeViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.ref.WeakReference

class HomeActivity : BaseActivity(), View.OnClickListener, ClickListener {

    override val layoutResourceId: Int = R.layout.activity_home
    private var currentUser: User? = null
    private lateinit var realTimeListener: ListenerRegistration
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var profile: TextView
    private lateinit var profileImage: CircularImageView
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var mainQuery: Query
    private var isFireStoreOptionChanged = false
    private val homeViewModel: HomeViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val bundle = intent.getBundleExtra(BUNDLE)
        currentUser = bundle?.getParcelable(CURRENT_USER)
        setUpRecyclerView()
        setSupportActionBar(toolbar)
        addNote.setOnClickListener {
            noteAdapter.refresh()
            toNoteUploadActivity()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        val view = menu?.findItem(R.id.profileMenu)?.actionView!!
        val searchView = menu.findItem(R.id.search)
            .setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    if (isFireStoreOptionChanged) {
                        allDataQuery()
                        isFireStoreOptionChanged = false
                    }
                    return true
                }

                override fun onMenuItemActionExpand(item: MenuItem?) = true
            }).actionView!! as SearchView
        view.setOnClickListener {
            startIntentFor(ProfileActivity::class.java, currentUser)
        }
        initViews(view)
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
        setUpSearchView(searchView)
        return true
    }

    private fun allDataQuery() {
        val recyclerOptions = makeFirebaseRecyclerOptions(mainQuery)
        noteAdapter.updateOptions(recyclerOptions)
        isFireStoreOptionChanged = false
    }

    private fun setUpSearchView(searchView: SearchView) {
        searchView.setOnCloseListener {
            return@setOnCloseListener true
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isEmptyOrIsBlank()) {
                    filter(query!!)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isEmptyOrIsBlank()) {
                    filter(newText!!)
                } else {
                    isFireStoreOptionChanged = false
                    noteAdapter.refresh()
                    allDataQuery()
                }
                return true
            }
        })
    }

    private fun filter(searchText: String) {
        noteAdapter.filter.filter(searchText)
        isFireStoreOptionChanged = true
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
        stopShimmer()
    }

    private fun setUpRecyclerView() {
        itemTouchHelper = ItemTouchHelper(RecyclerViewSwipeCallBack())
        itemTouchHelper.attachToRecyclerView(noteList)
        mainQuery =
            fireStore.collection(NOTE_COLLECTION)
                .orderBy(GENERATED_DATE, Query.Direction.DESCENDING)
                .whereEqualTo(AUTHOR_ID, currentUser?.uid)
        val recyclerOptions = makeFirebaseRecyclerOptions(mainQuery)
        FirestoreRecyclerOptions.Builder<Note>().setQuery(mainQuery, Note::class.java)
            .setLifecycleOwner(this)
            .build()
        noteAdapter = NoteAdapter(recyclerOptions, WeakReference(this))
        noteList.setHasFixedSize(true)
        noteList.adapter = noteAdapter
    }

    private fun initViews(view: View) {
        profile = view.profile
        profileImage = view.profileImage
        shimmerLayout = view.shimmerLayout
    }

    private fun makeFirebaseRecyclerOptions(query: Query) =
        FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note::class.java)
            .setLifecycleOwner(this)
            .build()


    private fun toNoteUploadActivity(mIntent: Intent? = null) {
        if (mIntent == null) {
            val intent = Intent(applicationContext, NoteUploadActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CURRENT_USER, currentUser)
            intent.putExtra(BUNDLE, bundle)
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            startActivity(intent)
        }
    }

    override fun enabledFullScreen(): Boolean = false
    override fun onClick(v: View?) =
        startIntentFor(ProfileActivity::class.java, currentUser)

    override fun onClick(note: Note) {
        val intent = Intent(applicationContext, NoteUploadActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(CURRENT_NOTE, note)
        bundle.putParcelable(CURRENT_USER, currentUser)
        intent.putExtra(BUNDLE, bundle)
        toNoteUploadActivity(intent)
    }

    override fun deleteNote(position: Int) {
        val currentNoteId = noteAdapter.snapshots[position].id
        // val currentNoteId = notesAdapter.currentNoteId(position)
        homeViewModel.deleteNote(currentNoteId)
    }

//    fun updateOptions(searchText: String) {
//        val query =
//            fireStore.collection(NOTE_COLLECTION)
//                .orderBy(GENERATED_DATE, Query.Direction.DESCENDING)
//                .whereEqualTo(AUTHOR_ID, currentUser?.uid)
//                .whereEqualTo(NOTE_TITLE, searchText)
//        val fireStoreRecyclerOptions = makeFirebaseRecyclerOptions(query)
//        noteAdapter.updateOptions(fireStoreRecyclerOptions)
//        isFireStoreOptionChanged = true
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE && data != null) {
            showMessage(data.getStringExtra(MESSAGE)!!)
        }
    }
}
