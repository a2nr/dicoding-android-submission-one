package io.github.a2nr.submissionmodul1.repository

import android.os.Parcel
import android.os.Parcelable

class MovieData(
    var vote_average: Float = 0f,
    var title: String = "",
    var release_date: String = "",
    var original_language: String = "",
    var backdrop_path: String = "",
    var overview: String = "",
    var poster_path: String = "",
    var media_type: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readFloat(),
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeFloat(vote_average)
        writeString(title)
        writeString(release_date)
        writeString(original_language)
        writeString(backdrop_path)
        writeString(overview)
        writeString(poster_path)
        writeString(media_type)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MovieData> = object : Parcelable.Creator<MovieData> {
            override fun createFromParcel(source: Parcel): MovieData = MovieData(source)
            override fun newArray(size: Int): Array<MovieData?> = arrayOfNulls(size)
        }
    }
}
