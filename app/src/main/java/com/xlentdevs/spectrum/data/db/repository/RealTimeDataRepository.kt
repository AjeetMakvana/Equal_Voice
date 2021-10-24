package com.xlentdevs.spectrum.data.db.repository

import android.app.Application
import com.xlentdevs.spectrum.data.Result
import com.xlentdevs.spectrum.data.db.entity.*
import com.xlentdevs.spectrum.data.db.remote.FirebaseDataSource
import com.xlentdevs.spectrum.data.db.remote.FirebaseReferenceChildObserver
import com.xlentdevs.spectrum.data.db.remote.FirebaseReferenceValueObserver
import com.xlentdevs.spectrum.utils.wrapSnapshotToClass

class RealTimeDataRepository(private var application: Application?) {

    private val firebaseDatabaseService = FirebaseDataSource()

    //------------------------------Update Starts---------------------------------
    fun updateNewUser(path: String, user: User) {
        firebaseDatabaseService.updateNewUser(path, user)
    }

    fun updateStatus(path: String, createdBy: String, status: String) {
        firebaseDatabaseService.updateStatus(path, createdBy, status)
    }

    fun updatePartnerId(path: String, createdBy: String, id: String) {
        firebaseDatabaseService.updatePartnerId(path, createdBy, id)
    }

    fun updateNewPartner(path: String, id: String, partner: Partner, b: (Result<Boolean>) -> Unit) {
        firebaseDatabaseService.updateNewPartner(path, id, partner, b)
    }

    fun updateConnectionId(path: String, createdBy: String, connId: String) {
        firebaseDatabaseService.updateConnectionId(path, createdBy, connId)
    }

    fun updateNewMessage(path: String, messagesID: String, message: Message) {
        firebaseDatabaseService.pushNewMessage(path, messagesID, message)
    }

    fun updateChatLastMessage(chatID: String, message: Message) {
        firebaseDatabaseService.updateLastMessage(chatID, message)
    }
    //------------------------------Update Ends-----------------------------------

    //------------------------------Remove Starts---------------------------------
    fun removeRoomIfNotConnected(path: String, id: String) {
        firebaseDatabaseService.removeRoomIfNotConnected(path, id)
    }

    fun removeMessages(path: String, connId: String) {
        firebaseDatabaseService.removeMessages(path, connId)
    }

    fun removeChats(path: String, myId:String, connId: String) {
        firebaseDatabaseService.removeChats(path, connId)
    }

    fun updateNewMyChatRoom( myId:String, chatRoom: ChatRoom){
        firebaseDatabaseService.updateNewMyChatRoom(myId, chatRoom)
    }
    //------------------------------Remove Ends-----------------------------------

    //------------------------------Load & Observe Starts---------------------------------
    fun loadAndObserveUserInfo(
        path: String,
        userID: String,
        observer: FirebaseReferenceValueObserver,
        b: (Result<User>) -> Unit
    ) {
        firebaseDatabaseService.attachUserInfoObserver(path, User::class.java, userID, observer, b)
    }

    fun loadAndObservePartner(
        path: String,
        observer: FirebaseReferenceValueObserver,
        b: (Result<String>) -> Unit
    ) {
        firebaseDatabaseService.attachPartnerObserver(path, observer, b)
    }

    fun loadAndObserveChatPartnerExistence(
        path: String,
        roomOwner: String,
        observer: FirebaseReferenceValueObserver,
        b: (Result<String>) -> Unit
    ) {
        firebaseDatabaseService.attachChatPartnerExistenceObserver(path, roomOwner, observer, b)
    }

    fun loadAndObserveConnectPartnerStatus(
        path: String,
        id: String,
        observer: FirebaseReferenceValueObserver,
        b: (Result<Partner>) -> Unit
    ) {
        firebaseDatabaseService.attachConnectPartnerStatusObserver(
            path,
            Partner::class.java,
            id,
            observer,
            b
        )
    }

    fun loadAndObserveMessagesAdded(
        path: String,
        messagesID: String,
        observer: FirebaseReferenceChildObserver,
        b: ((Result<Message>) -> Unit)
    ) {
        firebaseDatabaseService.attachMessagesObserver(
            path,
            Message::class.java,
            messagesID,
            observer,
            b
        )
    }

    fun loadAndObserveList(path: String, observer: FirebaseReferenceValueObserver, b: ((
        Result<MutableList<ChatRoom>?>) -> Unit)){
        firebaseDatabaseService.attachDiscoverListObserver(path, ChatRoom::class.java, observer, b)
    }
    //------------------------------Load & Observe Ends------------------------------------

    //------------------------------Load Single Starts-------------------------------------
    fun loadChat(path: String, chatID: String, b: ((Result<Chat>) -> Unit)) {
        firebaseDatabaseService.loadChatTask(path, chatID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(Chat::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }
    //------------------------------Load Single Ends---------------------------------------
}