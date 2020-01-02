package io.github.a2nr.jetpakcourse.repository

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = FavoriteMovieData.NAME)
data class FavoriteMovieData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID) var id: Int? = null,
    @ColumnInfo(name = ID_FAVORITE) var idFavorite: Int = -1,
    @ColumnInfo(name = MEDIA_TYPE) var mediaType: String = "",
    @ColumnInfo(name = ORIGINAL_LANGUAGE) var originalLanguage: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        id?.let { writeInt(it) }
        writeInt(idFavorite)
    }

    companion object {
        const val NAME = "FAVORITE_MOVIE"
        const val ID = "id"
        const val ID_FAVORITE = "id_favorite"
        const val ORIGINAL_LANGUAGE = "original_language"
        const val MEDIA_TYPE = "media_type"

        @JvmField
        val CREATOR: Parcelable.Creator<FavoriteMovieData> =
            object : Parcelable.Creator<FavoriteMovieData> {
                override fun createFromParcel(source: Parcel): FavoriteMovieData =
                    FavoriteMovieData(source)

                override fun newArray(size: Int): Array<FavoriteMovieData?> = arrayOfNulls(size)
            }
    }
}