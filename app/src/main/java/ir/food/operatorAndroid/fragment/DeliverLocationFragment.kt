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

class DeliverLocationFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentDeliverLocationBinding
    lateinit var myGoogleMap: GoogleMap
    lateinit var myLocationMarker: Marker

    var lat = 0.0
    var lng = 0.0
    lateinit var deliveryLocation: String
    lateinit var deliveryId: String
    lateinit var time: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliverLocationBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)
        binding.map.onCreate(savedInstanceState)
        MapsInitializer.initialize(activity?.applicationContext)
        binding.map.getMapAsync(this)

        val bundle = arguments
        if (bundle != null) {
            deliveryLocation = bundle.getString("deliveryLocation").toString()
            deliveryId = bundle.getString("deliveryId").toString()
            val loc = JSONObject(deliveryLocation)
            lng = loc.getDouble("lng")
            lat = loc.getDouble("lat")
//            binding.txtLastTime.text =
//                StringHelper.toPersianDigits(DateHelper.parseFormat(loc.getString("saveDate")))
        }

        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

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
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val dataObj = jsonObject.getJSONObject("data")
                            if (dataObj.getBoolean("status")) {
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

        animateToLocation(lat, lng)
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
        val smallMarker = Bitmap.createScaledBitmap(b, 60, 100, false)
        if (myGoogleMap != null) {
            myGoogleMap.clear()
            myLocationMarker = myGoogleMap.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
//                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    .position(latlng)
            )
        }
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