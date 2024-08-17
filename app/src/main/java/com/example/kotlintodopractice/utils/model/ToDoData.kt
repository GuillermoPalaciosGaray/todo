package com.example.kotlintodopractice.utils.model

data class UserData(var name:String="", var lastName:String="", var studentId:String="", var projectName:String="", var tutorName:String="" )
{
    // Convert User object to a Map
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "lastName" to lastName,
            "studentId" to studentId,
            "projectName" to projectName,
            "tutorName" to tutorName
        )
    }
}
