package com.kosdiam.epoweredmove.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.Review
import com.kosdiam.epoweredmove.models.enums.ReviewStatus
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class ReviewAdapter(private val reviews: List<Review>, private val loggedInUserId: String): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.review_item_view,
                parent,
                false
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentReview = reviews[position]
        val reviewName = holder.itemView.findViewById<TextView>(R.id.review_name)
        val reviewDate = holder.itemView.findViewById<TextView>(R.id.review_date)
        val reviewComment = holder.itemView.findViewById<TextView>(R.id.review_comment)
        val reviewRating = holder.itemView.findViewById<RatingBar>(R.id.review_rating)

        val df = SimpleDateFormat("dd/MM/yy")
        reviewName.text = if(currentReview.userId == loggedInUserId) "reviewed by you" else currentReview.userObj.name
        reviewComment.text = currentReview.comments
        reviewRating.rating = currentReview.rating.toFloat()
        reviewDate.text = df.format(currentReview.timestamp)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    class ReviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

}