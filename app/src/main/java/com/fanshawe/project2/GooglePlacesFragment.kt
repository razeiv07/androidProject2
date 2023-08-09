package com.fanshawe.project2

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class GooglePlacesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var placesClient: PlacesClient
    private lateinit var placesAdapter: PlacesAdapter
    private val DEFAULT_ZOOM = 20f
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        fun newInstance(): GooglePlacesFragment {
            return GooglePlacesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_google_places, container, false)


        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val savedLatitudeString = sharedPreferences.getString("latitude", null)
        val savedLongitudeString = sharedPreferences.getString("longitude", null)

        val savedLatitude = savedLatitudeString?.toFloatOrNull() ?: 43.019199f
        val savedLongitude = savedLongitudeString?.toFloatOrNull() ?: -81.235901f


        recyclerView = rootView.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        placesAdapter = PlacesAdapter()
        recyclerView.adapter = placesAdapter

        Places.initialize(requireContext(), "AIzaSyCJwQM099ucw2AlETD42z_Uaq88r8xdE7A")
        placesClient = Places.createClient(requireContext())

        fetchNearbyPlaces(savedLatitude.toDouble(), savedLongitude.toDouble())

        return rootView
    }

    private fun fetchNearbyPlaces(latitude: Double, longitude: Double) {
        val queries = listOf("establishment", "college", "cafe", "restaurant", "park", "bank", "hospital", "grocery", "gym")

        val placesList = mutableListOf<Place>()
        for (query in queries) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setLocationRestriction(RectangularBounds.newInstance(
                    LatLng(latitude - 0.1, longitude - 0.1),
                    LatLng(latitude + 0.1, longitude + 0.1)
                ))
                .build()

            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions

                val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

                for (prediction in predictions) {
                    val placeId = prediction.placeId
                    val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()

                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener { fetchPlaceResponse ->
                        val place = fetchPlaceResponse.place
                        // Add the place to the list if it's not already present
                        if (!placesList.contains(place)) {
                            placesList.add(place)
                        }

                        // Update the RecyclerView with the list of landmarks
                        placesAdapter.setPlaces(placesList)
                    }.addOnFailureListener { exception ->
                        Log.e("Error", "fetchNearbyPlaces: " + exception.message )
                        // Handle the failure to fetch place details
                    }
                }
            }.addOnFailureListener { exception ->
                // Handle the error if the search fails
                Log.e("Error", "" + exception.message)
            }
        }
    }
}



//package com.fanshawe.project2
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.libraries.places.api.Places
//import com.google.android.libraries.places.api.model.Place
//import com.google.android.libraries.places.api.model.RectangularBounds
//import com.google.android.libraries.places.api.net.FetchPlaceRequest
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
//import com.google.android.libraries.places.api.net.PlacesClient
//
//class GooglePlacesFragment : Fragment() {
//
//    private var latitude = 43.019199
//    private var longitude = -81.235901
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var placesClient: PlacesClient
//    private lateinit var placesAdapter: PlacesAdapter
//
//    companion object {
//        fun newInstance(latitude: Double, longitude: Double): GooglePlacesFragment {
//            val fragment = GooglePlacesFragment()
//            val args = Bundle()
//            args.putDouble("lat", latitude)
//            args.putDouble("long", longitude)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val rootView = inflater.inflate(R.layout.fragment_google_places, container, false)
//
//        arguments?.let {
//            latitude = it.getDouble("lat", 43.019199)
//            longitude = it.getDouble("long", -81.235901)
//        }
//
//        recyclerView = rootView.findViewById(R.id.recycler_view)
//        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        placesAdapter = PlacesAdapter()
//        recyclerView.adapter = placesAdapter
//
//        Places.initialize(requireContext(), "AIzaSyCJwQM099ucw2AlETD42z_Uaq88r8xdE7A")
//        placesClient = Places.createClient(requireContext())
//
//        fetchNearbyPlaces()
//
//        return rootView
//    }
//
//    private fun fetchNearbyPlaces() {
//        val queries = listOf("establishment", "college", "cafe", "restaurant", "park", "bank", "hospital", "grocery", "gym")
//
//        val placesList = mutableListOf<Place>()
//        for (query in queries) {
//            val request = FindAutocompletePredictionsRequest.builder()
//                .setQuery(query)
//                .setLocationRestriction(RectangularBounds.newInstance(
//                    LatLng(latitude - 0.1, longitude - 0.1),
//                    LatLng(latitude + 0.1, longitude + 0.1)
//                ))
//                .build()
//
//            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
//                val predictions = response.autocompletePredictions
//
//                val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
//
//                for (prediction in predictions) {
//                    val placeId = prediction.placeId
//                    val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
//
//                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener { fetchPlaceResponse ->
//                        val place = fetchPlaceResponse.place
//                        // Add the place to the list if it's not already present
//                        if (!placesList.contains(place)) {
//                            placesList.add(place)
//                        }
//
//                        // Check if we have processed all the predicted places
//
//                        // Update the RecyclerView with the list of landmarks
//
//                        println("PLaces"+ placesList)
//                        placesAdapter.setPlaces(placesList)
//
//                    }.addOnFailureListener { exception ->
//                        Log.e("Error", "fetchNearbyPlaces: " +exception.message )
//                        // Handle the failure to fetch place details
//                    }
//                }
//            }.addOnFailureListener { exception ->
//                // Handle the error if the search fails
//                Log.e("Error123",""+exception.message)
//            }
//        }
//    }
//}
