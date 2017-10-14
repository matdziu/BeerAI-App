package com.beerai

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import rx.Observable
import rx.subjects.PublishSubject

class BeerDatabaseRepository {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getBeerInfo(beerId: String): Observable<Beer> {
        val subject: PublishSubject<Beer> = PublishSubject.create()
        database.getReference("beers/$beerId").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val beer: Beer? = dataSnapshot?.getValue(Beer::class.java)
                subject.onNext(beer)
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                Log.e("FirebaseDatabase", databaseError.toString())
            }
        })
        return subject
    }
}