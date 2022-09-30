package io.itsydv.vcriatequiz.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.itsydv.vcriatequiz.databinding.ItemQuestionPreviewBinding
import io.itsydv.vcriatequiz.models.Question

// Adapter for the recycler view in the feed fragment
class AllQuestionsAdapter(val listener: (Int) -> Unit): RecyclerView.Adapter<AllQuestionsAdapter.QuestionsViewHolder>() {

    inner class QuestionsViewHolder(val binding: ItemQuestionPreviewBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            // When the user clicks on a question, listener will be called
            itemView.setOnClickListener {
                listener.invoke(adapterPosition)
            }
        }
    }

    // DiffUtil is used to calculate the difference between two lists
    // This improves the performance of the recycler view
    private val diffCallBack = object: DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.lable == newItem.lable
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsViewHolder {
        return QuestionsViewHolder(ItemQuestionPreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: QuestionsViewHolder, position: Int) {
        val question = differ.currentList[position]
        holder.binding.apply {
            val questionNum = (position + 1).toString()
            tvQuestionNumber.text = if (questionNum.length == 1) {
                "0$questionNum"
            } else {
                questionNum
            }
            tvQuestion.text = question.lable
        }
    }

    override fun getItemCount() = differ.currentList.size
}