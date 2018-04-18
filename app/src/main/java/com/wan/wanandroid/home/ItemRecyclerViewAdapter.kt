package com.wan.wanandroid.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wan.wanandroid.R
import com.wan.wanandroid.home.bean.Item
import com.wan.wanandroid.home.HomeItemFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [Item] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
 class ItemRecyclerViewAdapter(private val mListener:OnListFragmentInteractionListener?):RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {
    private var mValues:MutableList<Item> = ArrayList<Item>()

    fun setValues(values: MutableList<Item>){
        mValues = values
        notifyDataSetChanged()
    }

    fun appValues(values: List<Item>){
        mValues.addAll(values)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): ViewHolder {
        val view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position:Int) {
        holder.mItem = mValues.get(position)
        holder.mSuperChapterName.setText(mValues.get(position).superChapterName)
        holder.mChapterName.setText(mValues.get(position).chapterName)
        holder.mContentView.setText(mValues.get(position).title)

        holder.mView.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v:View) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem!!)
                }
            }
        })
    }

    override fun getItemCount():Int {
        return mValues.size
    }

    inner class ViewHolder( val mView:View):RecyclerView.ViewHolder(mView) {
         val mSuperChapterName:TextView
         val mChapterName:TextView
         val mContentView:TextView
         var mItem:Item? = null

        init{
            mSuperChapterName = mView.findViewById<TextView>(R.id.super_chapter_name)
            mChapterName = mView.findViewById<TextView>(R.id.chapter_name)
            mContentView = mView.findViewById<TextView>(R.id.content)
        }

        public override fun toString():String {
            return super.toString() + " '" + mContentView.getText() + "'"
        }
    }
}
