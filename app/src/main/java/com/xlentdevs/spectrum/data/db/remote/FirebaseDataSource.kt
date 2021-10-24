package com.xlentdevs.spectrum.data.db.remote

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xlentdevs.spectrum.data.db.entity.User
import com.xlentdevs.spectrum.data.Result
import com.xlentdevs.spectrum.data.db.entity.ChatRoom
import com.xlentdevs.spectrum.data.db.entity.Partner
import com.xlentdevs.spectrum.data.db.entity.Message
import com.xlentdevs.spectrum.utils.wrapSnapshotToArrayList
import com.xlentdevs.spectrum.utils.wrapSnapshotToClass

class FirebaseReferenceValueObserver {
    private var valueEventListener: ValueEventListener? = null
    private var dbRef: DatabaseReference? = null
    private var query: Query? = null

    fun start(valueEventListener: ValueEventListener, reference: DatabaseReference) {
        reference.addValueEventListener(valueEventListener)
        this.valueEventListener = valueEventListener
        this.dbRef = reference
    }

    fun query(valueEventListener: ValueEventListener, query: Query) {
        query.addListenerForSingleValueEvent(valueEventListener)
        this.valueEventListener = valueEventListener
        this.query = query
    }

    fun clear() {
        valueEventListener?.let { dbRef?.removeEventListener(it) }
        valueEventListener = null
        dbRef = null
    }
}

class FirebaseReferenceChildObserver {
    private var valueEventListener: ChildEventListener? = null
    private var dbRef: DatabaseReference? = null
    private var isObserving: Boolean = false

    fun start(valueEventListener: ChildEventListener, reference: DatabaseReference) {
        isObserving = true
        reference.addChildEventListener(valueEventListener)
        this.valueEventListener = valueEventListener
        this.dbRef = reference
    }

    fun clear() {
        valueEventListener?.let { dbRef?.removeEventListener(it) }
        isObserving = false
        valueEventListener = null
        dbRef = null
    }

    fun isObserving(): Boolean {
        return isObserving
    }
}

class FirebaseDataSource {

    companion object {
        val dbInstance = Firebase.database
    }

    private fun refToPath(path: String): DatabaseReference {
        return dbInstance.reference.child(path)
    }

    private fun refToPathWithQueryAndLimit(
        path: String,
        query: String,
        queryValue: String,
        limit: Int
    ): Query {
        return dbInstance.reference.child(path).orderByChild(query).equalTo(queryValue)
            .limitToFirst(limit)
    }

    //--------------------Listeners Starts---------------------
    private fun attachValueListenerToTaskCompletion(src: TaskCompletionSource<DataSnapshot>): ValueEventListener {
        return (object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                src.setException(Exception(error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                src.setResult(snapshot)
            }
        })
    }

    private fun <T> attachValueListenerToBlock(
        resultClassName: Class<T>,
        b: ((Result<T>) -> Unit)
    ): ValueEventListener {
        return (object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                b.invoke(Result.Error(error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (wrapSnapshotToClass(resultClassName, snapshot) == null) {
                    b.invoke(Result.Error(msg = snapshot.key))
                } else {
                    b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)))
                }
            }
        })
    }

    private fun <T> attachValueListenerToBlockWithList(
        resultClassName: Class<T>,
        b: ((Result<MutableList<T>>) -> Unit)
    ): ValueEventListener {
        return (object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                b.invoke(Result.Error(error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    b.invoke(Result.Success(mutableListOf()))
                } else {
                    b.invoke(Result.Success(wrapSnapshotToArrayList(resultClassName, snapshot)))
                }
            }
        })
    }

    private fun attachValueListenerToSnapShotKey(
        b: ((Result<String>) -> Unit)
    ): ValueEventListener {
        return (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    b.invoke(Result.Success("NA"))
                } else {
                    for (child in snapshot.children) {
                        b.invoke(Result.Success(child.key))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                b.invoke(Result.Error(error.message))
            }

        })
    }

    private fun attachValueListenerToCheckHasChild(
        roomOwner: String,
        b: ((Result<String>) -> Unit)
    ): ValueEventListener{
        return (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(roomOwner)){
                    b.invoke(Result.Success("AV"))
                } else{
                    b.invoke(Result.Success("NA"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                b.invoke(Result.Error(error.message))
            }

        })
    }

    private fun <T> attachChildListenerToBlock(
        resultClassName: Class<T>,
        b: ((Result<T>) -> Unit)
    ): ChildEventListener {
        return (object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)))
            }

            override fun onCancelled(error: DatabaseError) {
                b.invoke(Result.Error(error.message))
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }
    //-------------------Listeners Ends-------------------------

    //-------------------Updates Starts-------------------------
    fun updateNewUser(path: String, user: User) {
        refToPath("${path}/${user.uid}").setValue(user)
    }

    fun updateStatus(path: String, createdBy: String, status: String) {
        refToPath("${path}/${createdBy}/status").setValue(status)
    }

    fun updatePartnerId(path: String, createdBy: String, id: String) {
        refToPath("${path}/${createdBy}/partnerId").setValue(id)
    }

    fun updateNewPartner(path: String, id: String, partner: Partner, b:(Result<Boolean>)-> Unit) {
        refToPath("${path}/${id}").setValue(partner).addOnSuccessListener {
            b.invoke(Result.Success(true))
        }.addOnFailureListener {
            b.invoke(Result.Error("Update New Chat Partner Failed"))
        }
    }

    fun updateConnectionId(path:String, createdBy: String, connId: String){
        refToPath("${path}/${createdBy}/connId").setValue(connId)
    }

    fun pushNewMessage(path: String, messagesID: String, message: Message) {
        refToPath("${path}/$messagesID").push().setValue(message)
    }

    fun updateLastMessage(chatID: String, message: Message) {
        refToPath("chats/$chatID/lastMessage").setValue(message)
    }

    fun updateNewMyChatRoom(myId: String, chatRoom: ChatRoom){
        refToPath("chatRooms/${myId}/${chatRoom.uid}").setValue(chatRoom)
    }
    //-------------------Updates Ends---------------------------

    //-------------------Remove Starts--------------------------
    fun removeRoomIfNotConnected(path: String, id: String){
        refToPath("${path}/${id}").removeValue()
    }

    fun removeMessages(path: String, connId: String){
        refToPath("${path}/${connId}").removeValue()
    }

    fun removeChats(path: String, connId: String){
        refToPath("${path}/${connId}").removeValue()
    }
    //-------------------Remove Ends----------------------------

    //-------------------Load Starts----------------------------
    fun loadChatTask(path: String, chatID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("${path}/$chatID").addListenerForSingleValueEvent(listener)
        return src.task
    }
    //-------------------Load Ends------------------------------

    //-------------------Observers Starts-----------------------
    fun <T> attachUserInfoObserver(
        path: String,
        resultClassName: Class<T>,
        userID: String,
        refObs: FirebaseReferenceValueObserver,
        b: ((Result<T>) -> Unit)
    ) {
        val listener = attachValueListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("${path}/${userID}"))
    }

    fun attachPartnerObserver(
        path: String,
        refObs: FirebaseReferenceValueObserver,
        b: ((Result<String>) -> Unit)
    ) {
        val listener = attachValueListenerToSnapShotKey(b)
        refObs.query(listener, refToPathWithQueryAndLimit(path, "status", "0", 1))
    }

    fun attachChatPartnerExistenceObserver(
        path: String,
        roomOwner: String,
        refObs: FirebaseReferenceValueObserver,
        b: ((Result<String>) -> Unit)
    ){
        val listener = attachValueListenerToCheckHasChild(roomOwner, b)
        refObs.start(listener, refToPath(path))
    }

    fun <T> attachConnectPartnerStatusObserver(
        path: String,
        resultClassName: Class<T>,
        id: String,
        refObs: FirebaseReferenceValueObserver,
        b: ((Result<T>) -> Unit)
    ){
        val listener = attachValueListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("${path}/${id}"))
    }

    fun <T> attachMessagesObserver(
        path: String,
        resultClassName: Class<T>,
        messagesID: String,
        refObs: FirebaseReferenceChildObserver,
        b: ((Result<T>) -> Unit)
    ) {
        val listener = attachChildListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("${path}/$messagesID"))
    }

    fun <T> attachDiscoverListObserver(
        path: String,
        resultClassName: Class<T>,
        firebaseReferenceValueObserver: FirebaseReferenceValueObserver,
        b: ((Result<MutableList<T>?>) -> Unit)
    ) {
        val listener = attachValueListenerToBlockWithList(resultClassName, b)
        firebaseReferenceValueObserver.start(listener, refToPath(path))
    }
    //------------------Observers End--------------------------
}