package io.github.a2nr.submissionmodul1.repository

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class MovieData(
    @PrimaryKey                                 var id: Int = -1,
    @ColumnInfo(name = "vote_average")          var vote_average: Float = 0f,
    @ColumnInfo(name = "title")                 var title: String = "",
    @ColumnInfo(name = "release_date")          var release_date: String = "",
    @ColumnInfo(name = "original_language")     var original_language: String = "",
    @ColumnInfo(name = "backdrop_path")         var backdrop_path: String = "",
    @ColumnInfo(name = "overview")              var overview: String = "",
    @ColumnInfo(name = "poster_path")           var poster_path: String = "",
    @ColumnInfo(name = "media_type")            var media_type: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
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
        writeInt(id)
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