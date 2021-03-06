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

    fun searchForBeers(query: String): Observable<List<Beer?>> {
        val subject: PublishSubject<List<Beer?>> = PublishSubject.create()
        val queryWordList = query.trim().split(" ")
        val formattedQueryWordList = queryWordList.map { it.toLowerCase().capitalize() }
        val formattedQuery = formattedQueryWordList.joinToString(" ")
        database.getReference("/beers").orderByChild("name")
                .startAt(formattedQuery)
                .endAt(formattedQuery + "\uf8ff")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val beerList = dataSnapshot.children.map { it.getValue(Beer::class.java) }
                        subject.onNext(beerList)
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        Log.e("FirebaseDatabase", databaseError.toString())
                    }
                })
        return subject
    }
}