package io.github.a2nr.jetpakcourse.helper

import android.net.Uri

class GetMoviesParams (
    var mediaType: String,
    var timeWindow: String,
    var language: String,
    var link: ((Int)-> Uri)
)