package ir.food.operatorAndroid.fragment

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentDeliverLocationBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.helper.DateHelper
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONObject

class DeliverLocationFragment(id: String, location: LatLng, lastTime: String) : Fragment(),
    OnMapReadyCallback {

    lateinit var binding: FragmentDeliverLocationBinding
    lateinit var myGoogleMap: GoogleMap
    lateinit var myLocationMarker: Marker
    val deliveryLocation = location
    val lastLocation = lastTime

    val deliveryId = id
    lateinit var time: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliverLocationBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)
        binding.map.onCreate(savedInstanceState)
        MapsInitializer.initialize(MyApplication.context)
        binding.map.getMapAsync(this)

        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        binding.txtLastTime.text = (StringHelper.toPersianDigits(
            DateHelper.strPersianEghit(
                DateHelper.parseFormat(lastLocation + "", null)
            )
        ))

        binding.imgRefresh.setOnClickListener { getLocation(deliveryId) }

        return binding.root
    }

    private fun getLocation(id: String) {
        RequestHelper.builder(EndPoints.GET_DELIVERY_LOCATION + id)
            .listener(locationCallBack)
            .get()
    }

    private val locationCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
// data: {status: true, deliveryLocation: {_id: "60b72a70e353f0385c2fe5af",city: "Mashhad",geo : [geo -49.555555, 39.555555],speed : 80, bearing: 32,saveDate: "2021-08-01T09:26:22.320Z" }}}
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val dataObj = jsonObject.getJSONObject("data")
                            if (dataObj.getBoolean("status")) {
                                val deliveryLocationObj = dataObj.getJSONObject("deliveryLocation")
                                val lat = deliveryLocationObj.getJSONArray("geo").get(1)
                                val lng = deliveryLocationObj.getJSONArray("geo").get(0)
                                val saveDate = deliveryLocationObj.getString("saveDate")
                                animateToLocation(lat as Double, lng as Double)
                                binding.txtLastTime.text = (StringHelper.toPersianDigits(
                                    DateHelper.strPersianEghit(
                                        DateHelper.parseFormat(saveDate + "", null)
                                    )
                                ))
                            }
                        } else {
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                }
            }
        }

    override fun onMapReady(p0: GoogleMap) {
        myGoogleMap = p0

        animateToLocation(deliveryLocation.latitude, deliveryLocation.longitude)
    }

    private fun animateToLocation(latitude: Double, longitude: Double) {
        if ((latitude == 0.0 || longitude == 0.0)) {
            MyApplication.Toast("موقعیت پیک در دسترس نمیباشد.", Toast.LENGTH_SHORT)
            return
        }
        val latlng = LatLng(latitude, longitude)
        val cameraPosition = CameraPosition.builder()
            .target(latlng)
            .zoom(14f)
            .build()
        if (myGoogleMap != null) myGoogleMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                cameraPosition
            )
        )
        val bitmapdraw = resources.getDrawable(R.mipmap.pin) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false)
        myGoogleMap.clear()
        myLocationMarker = myGoogleMap.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
//                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .position(latlng)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }
}