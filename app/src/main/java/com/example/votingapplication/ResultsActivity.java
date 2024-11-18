package com.example.votingapplication

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class ResultsActivity :AppCompatActivity() {

    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        tableLayout = findViewById(R.id.tableLayoutResults)
        val candidateRef = FirebaseDatabase.getInstance().reference.child("election-candidates")

        candidateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tableLayout.removeAllViews()

                val headerRow = TableRow(this@ResultsActivity).apply {
                    layoutParams = TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                val headerPartyName = createTextView("Party Name")
                val headerVotes = createTextView("Votes")

                headerRow.addView(headerPartyName)
                headerRow.addView(headerVotes)
                tableLayout.addView(headerRow)

                for (candidateSnapshot in snapshot.children) {
                    val partyName = candidateSnapshot.child("pName").getValue(String::class.java)
                    val votes = candidateSnapshot.child("votes").getValue(Int::class.java) ?: 0

                    val row = TableRow(this@ResultsActivity).apply {
                        layoutParams = TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    val textViewParty = createTextView(partyName ?: "N/A")
                    val textViewVotes = createTextView(votes.toString())

                    row.addView(textViewParty)
                    row.addView(textViewVotes)

                    tableLayout.addView(row)
                }
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun createTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            layoutParams = TableLayout.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 8, 16, 8)
            gravity = Gravity.CENTER
        }
    }
}