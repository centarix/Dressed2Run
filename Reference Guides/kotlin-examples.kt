/**
 * We declare a package-level function main which returns Unit and takes
 * an Array of strings as a parameter. Note that semicolons are optional.
 */

import java.time.LocalDateTime

fun main(args: Array<String>) {
    println("Hello, world!")
    println(getPeople())
    for(profile in getUserProfiles())
    {
        println(profile)
    }
    stringPlay()
}

enum class UserType {
    COOL, WARM
}
data class Person(var name: String, var age : Int) //basically a getter/setter 
{
    
}

data class UserProfile(var name: String="Crazy Running Person", var gender: String="Unspecified", var birthDate : LocalDateTime=LocalDateTime.now(), var userType : UserType=UserType.COOL)
{
    
}


fun getUserProfiles() : List<UserProfile> {
    return listOf(
    UserProfile(name="Kent", gender="Male"),
    UserProfile("Lois", "Female", userType = UserType.WARM),
    UserProfile("does")
    )
}




fun getPeople(): List<Person> {
    return listOf(Person("My Dear Alice", 24), Person("Mad Hatter", 32))
}

fun stringPlay() {
    var a : String? = "abc"
    a = null
    println(a)
    
    //Condition to check for null; otherwise you get an error with a.length because it could be null
    val l = if (a != null) a.length else -1
    
    //Safe call
    val x = a?.length
    
    
    //List with nulls
    val listWithNulls: List<String?> = listOf("Apple", "Clint Eastwood", null, "Ms. Marvel")
    for (item in listWithNulls)
    {
        item?.let { println(it) } //Prints values and ignores nulls
    }
    
    //Elivs operator ?:  , Elivs op is like : in C#
    val t = a?.length ?: -1
    println(t)
    
}