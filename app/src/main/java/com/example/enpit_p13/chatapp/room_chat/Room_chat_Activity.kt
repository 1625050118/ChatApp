package com.example.enpit_p13.chatapp.room_chat

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.enpit_p13.chatapp.Message
import com.example.enpit_p13.chatapp.R
import com.example.enpit_p13.chatapp.messages.ChatToItem
import com.example.enpit_p13.chatapp.messages.ChatfromItem
import com.example.enpit_p13.chatapp.messages.LatestMessagesActivity
import com.example.enpit_p13.chatapp.models.User
import com.example.enpit_p13.chatapp.quetion.QuestiontempActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_room_chat_.*

class Room_chat_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_chat_)
        val userdata = intent.getParcelableExtra<Room_chat_messager>(LatestMessagesActivity.USER_KE)
        supportActionBar?.title = userdata.kadaimeiText
        explain_textview.text = userdata.messageText
        createFirebaseListener()
        send_Button_room_chat.setOnLongClickListener(){
            template()
        }
        send_Button_room_chat.setOnClickListener {
            template_button.visibility = View.INVISIBLE
            template_button.isClickable = false
            if (!room_chat_edittext.text.toString().isEmpty()) {
                sendData()
                createFirebaseListener()


            } else {

                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()

            }
        }
        template_button.setOnClickListener{
            template_button.visibility = View.INVISIBLE
            template_button.isClickable = false
            intent = Intent(this,QuestiontempActivity::class.java)
            startActivity(intent)
        }
    }
    private fun sendData() {

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val userdata = intent.getParcelableExtra<Room_chat_messager>(LatestMessagesActivity.USER_KE)
                    val userData = data.getValue<User>(User::class.java)
                    var i = 0
                    val user = userData?.let { it } ?: continue
                    if(user?.uid == FirebaseAuth.getInstance().uid){
                        val reference = FirebaseDatabase.getInstance().getReference()?.child("/Room_Chat/${userdata.uid.toString()}/${userdata.kadaimeiText.toString()}").push()
                        reference.setValue(Message(room_chat_edittext.text.toString(),user?.username.toString()))
                        room_chat_edittext.setText("")
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun createFirebaseListener() {
        val userdata = intent.getParcelableExtra<Room_chat_messager>(LatestMessagesActivity.USER_KE)
        Log.d("Main","${userdata.uid}${userdata.kadaimeiText}")
        val ref =  FirebaseDatabase.getInstance().getReference()?.child("/Room_Chat/${userdata.uid.toString()}")?.child("/${userdata.kadaimeiText.toString()}")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var count = 0;

                val adapter = GroupAdapter<ViewHolder>()

                // val toReturn: ArrayList<Message> = ArrayList();

                for (data in dataSnapshot.children) {

                    val messageData = data.getValue<Message>(Message::class.java)
                    var i = 0
                    val message = messageData?.let { it } ?: continue

                    if (message.Uid == FirebaseAuth.getInstance().uid) {
                            count += 1

                            adapter.add(ChatToItem(message.text!!))

                    }
                    else {
                        count += 1
                            adapter.add(ChatfromItem(message.text!!,message.username!!))
                    }

                }


                recycler_chat_room.adapter = adapter
                // setupAdapter(toReturn)
                recycler_chat_room.scrollToPosition(count-1)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        })


    }
    private fun template(): Boolean {
       template_button.visibility = View.VISIBLE
        template_button.isClickable = true
        return false
    }
}





