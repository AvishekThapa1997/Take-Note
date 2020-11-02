package com.app.takenote.utility

sealed class CustomError(val message: String)
class ImageUploadError(message: String) : CustomError(message)
class NameUpdateError(message: String) : CustomError(message)