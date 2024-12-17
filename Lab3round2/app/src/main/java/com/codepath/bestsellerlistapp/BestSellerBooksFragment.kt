package com.codepath.bestsellerlistapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.codepath.bestsellerlistapp.R
import com.google.gson.Gson
import okhttp3.Headers
import org.json.JSONObject

// --------------------------------//
// CHANGE THIS TO BE YOUR API KEY  //
// --------------------------------//
private const val API_KEY = "P8s6IJEbPQss6Pht0c3rUYZ6JztJoLWY"

/*
 * The class for the only fragment in the app, which contains the progress bar,
 * recyclerView, and performs the network calls to the NY Times API.
 */
class BestSellerBooksFragment : Fragment(), OnListFragmentInteractionListener {

    /*
     * Constructing the view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_best_seller_books_list, container, false)
        val progressBar = view.findViewById<View>(R.id.progress) as ContentLoadingProgressBar
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        val context = view.context
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        updateAdapter(progressBar, recyclerView)
        return view
    }

    /*
     * Updates the RecyclerView adapter with new data.  This is where the
     * networking magic happens!
     */
    private fun updateAdapter(progressBar: ContentLoadingProgressBar, recyclerView: RecyclerView) {
        progressBar.show()

        // Create and set up an AsyncHTTPClient() here
        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api-key"] = API_KEY

// Using the client, perform the HTTP request
        client[
            "https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json",
            params,
            object : JsonHttpResponseHandler() { //connect these callbacks to your API call


        // Using the client, perform the HTTP request

     // Uncomment me once you complete the above sections!

            /*
             * The onSuccess function gets called when
             * HTTP response status is "200 OK"
             */
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                // The wait for a response is over
                progressBar.hide()
                val resultsJSON: JSONObject = json.jsonObject.get("results") as JSONObject
                val booksRawJSON: String = resultsJSON.get("books").toString()

                //TODO - Parse JSON into Models
                val models = parseJsonToModel(booksRawJSON)

                recyclerView.adapter = BestSellerBooksRecyclerViewAdapter(models, this@BestSellerBooksFragment)

                // Look for this in Logcat:
                Log.d("BestSellerBooksFragment", "response successful")
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                t: Throwable?
            ) {
                // The wait for a response is over
                progressBar.hide()

                // If the error is not null, log it!
                t?.message?.let {
                    Log.e("BestSellerBooksFragment", errorResponse)
                }
            }
        }]


    }

    /*
     * What happens when a particular book is clicked.
     */
    override fun onItemClick(item: BestSellerBook) {
        Toast.makeText(context, "test: " + item.title, Toast.LENGTH_LONG).show()
    }
    private fun parseJsonToModel(booksRawJSON: String): List<BestSellerBook> {
        val gson = Gson()

        // Convert the raw JSON string into a list of BestSellerBook objects
        val booksArray: Array<BestSellerBook> = gson.fromJson(booksRawJSON, Array<BestSellerBook>::class.java)

        // Convert the array to a list and return it
        return booksArray.toList()
    }

}
