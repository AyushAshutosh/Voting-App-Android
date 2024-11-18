package com.example.votingapplication

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ElectionCandidateListViewHolder;

class ElectionCandidateListAdapter(
        private val candidateNames: Array<String>
) : RecyclerView.Adapter<ElectionCandidateListAdapter.ElectionCandidateListViewHolder>() {



override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
): ElectionCandidateListViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.activity_list_item_candidate_layout, parent, false)
    return ElectionCandidateListViewHolder(view)
}

override fun onBindViewHolder(holder: ElectionCandidateListViewHolder, position: Int) {
    val candidateName = candidateNames[position]
    holder.candidateName.text = candidateName
}

override fun getItemCount(): Int {
    return candidateNames.size
}

inner class ElectionCandidateListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val candidateName: TextView = itemView.findViewById(R.id.candidateName)
}
}