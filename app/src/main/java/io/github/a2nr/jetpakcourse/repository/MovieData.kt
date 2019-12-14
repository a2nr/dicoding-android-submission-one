package io.github.a2nr.jetpakcourse.repository

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = MovieData.TABLE_NAME)
data class MovieData(
    @PrimaryKey var id: Int = -1,
    @ColumnInfo(name = VOTE_AVERAGE) var voteAverage: Float = 0f,
    @ColumnInfo(name = TITLE) var title: String = "",
    @ColumnInfo(name = RELEASE_DATE) var releaseDate: String = "",
    @ColumnInfo(name = ORIGINAL_LANGUAGE) var originalLanguage: String = "",
    @ColumnInfo(name = BACKDROP_PATH) var backdropPath: String = "",
    @ColumnInfo(name = OVERVIEW) var overview: String = "",
    @ColumnInfo(name = POSTER_PATH) var posterPath: String = "",
    @ColumnInfo(name = MEDIA_TYPE) var mediaType: String = ""
) : Parcelable {
    companion object {
        const val NAME = "MovieData"
        const val TABLE_NAME = NAME
        const val VOTE_AVERAGE = "vote_average"
        const val TITLE = "title"
        const val RELEASE_DATE = "release_date"
        const val ORIGINAL_LANGUAGE = "original_language"
        const val BACKDROP_PATH = "backdrop_path"
        const val OVERVIEW = "overview"
        const val POSTER_PATH = "poster_path"
        const val MEDIA_TYPE = "media_type"

        @JvmField
        val CREATOR: Parcelable.Creator<MovieData> = object : Parcelable.Creator<MovieData> {
            override fun createFromParcel(source: Parcel): MovieData = MovieData(source)
            override fun newArray(size: Int): Array<MovieData?> = arrayOfNulls(size)
        }
    }

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
        writeFloat(voteAverage)
        writeString(title)
        writeString(releaseDate)
        writeString(originalLanguage)
        writeString(backdropPath)
        writeString(overview)
        writeString(posterPath)
        writeString(mediaType)
    }

}