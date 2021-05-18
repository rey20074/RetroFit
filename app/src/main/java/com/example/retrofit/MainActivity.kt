package com.example.retrofit

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArticleAdapter
    private val articleList= mutableListOf<Articles>()
    public var country:String = ""
    public var category:String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchNews.setOnQueryTextListener(this)
        initRecyclerView()

        searchView("general")


    }

    public fun cambiarpais1(v: View){
        //us
        country="us"
        searchView(category)
    }

    public fun cambiarpais2(v: View){
        //no
        country="no"
        searchView(category)
    }

    public fun cambiarpais3(v: View){
        //fr
        country="fr"
        searchView(category)
    }

    private fun initRecyclerView(){
        adapter= ArticleAdapter(articleList)
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = adapter
    }

    private fun searchView(category:String){
        val api = RetroFit2()
        CoroutineScope(Dispatchers.IO).launch {


            val call = api.getService()?.getNewsByCategory(country,category,"4b94054dbc6b4b3b9e50d8f62cde4f6c")
            val news: NewsResponse? = call?.body()

            runOnUiThread{
                if (call!!.isSuccessful){
                    if (news?.status.equals("ok")){
                        val articles = news?.articles ?: emptyList()
                        articleList.clear()
                        articleList.addAll(articles)
                        adapter.notifyDataSetChanged()
                    }
                    else{
                        showMessage("Error en webservices")
                    }
                }
                else{
                    showMessage("Error en RetroFit")
                }
                hideKeyBoard()
            }
        }
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showMessage(message:String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            searchView(query.toLowerCase(Locale.ROOT))
            category = query.toLowerCase()
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}