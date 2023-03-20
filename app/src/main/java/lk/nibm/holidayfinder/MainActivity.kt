package lk.nibm.holidayfinder

import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject



class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    lateinit var sel_Country: Spinner
    lateinit var sel_Year : Spinner
    lateinit var recyclerView: RecyclerView
    var holidays = JSONArray()
    var countryApi= JSONArray();
    var countryLength:Int?=null
    var i:Int=0

    val systemYear = Calendar.getInstance().get(Calendar.YEAR).toString()


    var selectedCountryCode: String? = "AF"
    var selectedYear: String? = systemYear
    //val handler = android.os.Handler()

    var countrynameslist= Array<String>(230){"Afghanistan"}
    var countrycodeslist= Array<String>(230){"0"}



    val yearslist = arrayOf<String>("2020", "2021", "2022", "2023", "2024","2025","2026","2027")







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)

        if (supportActionBar != null) {

            supportActionBar!!.hide()

        }

        sel_Country = findViewById(R.id.spin_country)
        sel_Year = findViewById(R.id.spin_year)
        recyclerView = findViewById(R.id.recycleview)

        getCountryList()
        //must replace below code with the new code
        val countryadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            countrynameslist)
        sel_Country.adapter = countryadapter

        val yearsadapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,yearslist)
        sel_Year.adapter = yearsadapter


        sel_Country.onItemSelectedListener = this
        sel_Year.onItemSelectedListener = this

        recyclerView.layoutManager  = LinearLayoutManager(applicationContext,
            LinearLayoutManager.VERTICAL,false)

        recyclerView.adapter = HolidayApapter()

        // Setting default year based on device date
        sel_Year.setSelection(yearsadapter.getPosition(systemYear))







        getHolidaydata(selectedCountryCode!!,selectedYear!!)






        // getLocation()


    }




       override  fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {


           when (parent.id) {
               R.id.spin_country -> {
                   selectedCountryCode = countrycodeslist[position]
                  // Log.e("Spinner", "Selected country code: $selectedCountryCode")
                   getHolidaydata(selectedCountryCode!!,selectedYear!! )
               }
               R.id.spin_year -> {
                   selectedYear = parent.getItemAtPosition(position) as String
                  // Log.e("Spinner", "Selected year: $selectedYear")
                   getHolidaydata(selectedCountryCode!!,selectedYear!! )
               }

           }



       }



    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    fun getCountryList(){

        val c_url="https://calendarific.com/api/v2/countries?&api_key=afd135c1d18af776c23617bb89d0b0f63651bc89&"
        val c_result = StringRequest(Request.Method.GET,c_url,
            Response.Listener { response ->
                try {
                    countryApi = JSONObject(response).getJSONObject("response").getJSONArray("countries")

                    Log.e("arraylength",countrynameslist.size.toString())
                    for(i in 0..getCountrylength()-1)
                    {
                        countrynameslist.set(i,countryApi.getJSONObject(i).getString("country_name"))
                        Log.e("country",countrynameslist.get(i).toString())
                        countrycodeslist.set(i,countryApi.getJSONObject(i).getString("iso-3166"))
                        Log.e("country code",countrycodeslist.get(i).toString())
                    }



                }
                catch (e : Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()

                }

            }
            ,Response.ErrorListener{ error->


            })

        Volley.newRequestQueue(this).add(c_result)

    }

    fun getHolidaydata(selectedCountryCode: String, selectedYear: String) {



         val url = "https://calendarific.com/api/v2/holidays?&api_key=46e3945526bc23db7243e69adb235ad2e1f8b7fa&country=" + selectedCountryCode + "&year=" + selectedYear + ""
      Log.e("url",url)


        val result = StringRequest(Request.Method.GET,url,
            Response.Listener { response ->
                try {
                    holidays = JSONObject(response).getJSONObject("response").getJSONArray("holidays")
                    recyclerView.adapter ?. notifyDataSetChanged()

//
                }
                catch (e : Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()

                }

            }
            ,Response.ErrorListener{ error->


            })

        Volley.newRequestQueue(this).add(result)




    }


    fun getCountrylength():Int
    {


        Log.e("country length ",countryApi.length().toString())
        return countryApi.length()

    }


    inner class HolidayApapter : RecyclerView.Adapter<HolidayView>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayView {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.hol_recycleview,parent,false)

            return HolidayView(view)
        }

        override fun getItemCount(): Int {
           return holidays.length()
        }

        override fun onBindViewHolder(holder: HolidayView, position: Int) {
            try {
                holder.holidayname.text = holidays.getJSONObject(position).getString("name")
                holder.holidaymonth.text = holidays.getJSONObject(position).getJSONObject("date").getJSONObject("datetime").getString("month")
                holder.holidaydate.text = holidays.getJSONObject(position).getJSONObject("date").getString("iso")
            }catch (e:java.lang.Error){


            }
        }


    }



    inner class HolidayView(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{


        val holidayname : TextView = itemView.findViewById(R.id.txt_holname)
        val holidaymonth : TextView = itemView.findViewById(R.id.txt_month)
        val holidaydate : TextView = itemView.findViewById(R.id.txt_holdate)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val holiday_name = holidays.getJSONObject(position)
                val intent = Intent(itemView.context, h_details::class.java)
              intent.putExtra("holiday", holiday_name.toString())


                itemView.context.startActivity(intent)


            }
        }


    }

}




