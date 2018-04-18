package com.wan.wanandroid.home.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Woody on 2018/3/6.
 */
data class HomeBean(@SerializedName("data") val data:Data)

data class Data(val curPage:Int? = 0,
                @SerializedName("datas") val datas:MutableList<Item>? = null
)

data class Item(val apkLink:String = "",
                val author:String = "",
                val chapterId:Int = -1,
                val chapterName:String = "",
                val collect:Boolean = false,
                val courseId:Int = -1,
                val desc:String = "",
                val envelopePic:String = "",
                val id:Int = -1,
                val link:String = "",
                val niceDate:String = "",
                val origin:String = "",
                val projectLink:String = "",
                val publishTime:Long = -1,
                val superChapterId:Int,
                val superChapterName:String = "",
                val title:String = "",
                val visible:Int = -1,
                val zan:Int = -1
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(apkLink)
        parcel.writeString(author)
        parcel.writeInt(chapterId)
        parcel.writeString(chapterName)
        parcel.writeByte(if (collect) 1 else 0)
        parcel.writeInt(courseId)
        parcel.writeString(desc)
        parcel.writeString(envelopePic)
        parcel.writeInt(id)
        parcel.writeString(link)
        parcel.writeString(niceDate)
        parcel.writeString(origin)
        parcel.writeString(projectLink)
        parcel.writeLong(publishTime)
        parcel.writeInt(superChapterId)
        parcel.writeString(superChapterName)
        parcel.writeString(title)
        parcel.writeInt(visible)
        parcel.writeInt(zan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}