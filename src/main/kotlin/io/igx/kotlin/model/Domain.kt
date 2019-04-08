package io.igx.kotlin.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * @author vinicius
 *
 */

data class Response(val series: String, val limit: Int, val offset:Int, val total:Int)
data class Driver(val driverId: String, val code: String?, val url: String? = "", val givenName: String, val familyName: String, val dateOfBirth: String?, val nationality: String?)
data class DriverResponse(val response: Response, val season: Int, val drivers: List<Driver>)

data class Location(val lat: Float?, val lon: Float?, val locality: String?, val country: String?)
data class Circuit(val circuitId: String, val url: String?, val name: String, val location: Location)
data class CircuitResponse(val response: Response, val season: Int, val circuits: List<Circuit>)

class CircuitDeserializer : JsonDeserializer<Circuit>{
    override fun deserialize(element: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Circuit {
        val json = element as JsonObject
        val jsonLocation = json.getAsJsonObject("Location")
        val location = Location(jsonLocation.get("lat")?.asFloat, jsonLocation.get("long")?.asFloat, jsonLocation.get("locality")?.asString, jsonLocation.get("country")?.asString)
        return Circuit(json.get("circuitId").asString, json.get("url")?.asString, json.get("circuitName").asString, location)
    }
}

class DriverDeserializer : JsonDeserializer<Driver> {
    override fun deserialize(element: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Driver {
        val json = element as JsonObject
        return Driver(json.get("driverId").asString, json.get("code")?.asString, json.get("url")?.asString, json.get("givenName").asString, json.get("familyName").asString, json.get("dateOfBirth")?.asString, json.get("nationality")?.asString)
    }

}

class CircuitResponseDeserializer : JsonDeserializer<CircuitResponse> {
    override fun deserialize(
        element: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CircuitResponse {
        val wrapper = element as JsonObject
        val json = wrapper.getAsJsonObject("MRData")
        val response = json.toResponse()
        val table = json.get("CircuitTable").asJsonObject
        val season = table.get("season").asInt
        val circuitType = object : TypeToken<List<Circuit>>() {}.type
        return CircuitResponse(response, season, context!!.deserialize(table.get("Circuits").asJsonArray, circuitType))

    }

}

class DriverResponseDeserializer : JsonDeserializer<DriverResponse> {

    override fun deserialize(element:  JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DriverResponse {

        val wrapper = element as JsonObject
        val json = wrapper.getAsJsonObject("MRData")
        val response = json.toResponse()

        val table = json.get("DriverTable").asJsonObject
        val season = table.get("season").asInt
        val driversType = object : TypeToken<List<Driver>>() {}.type

        return DriverResponse(response, season, context!!.deserialize(table.get("Drivers").asJsonArray, driversType))
    }

}

fun JsonObject.toResponse() : Response {
    return Response(this.get("series").asString, this.get("limit").asInt, this.get("offset").asInt, this.get("total").asInt)
}