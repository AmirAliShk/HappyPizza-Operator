package ir.food.operatorAndroid.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemRecentCallsBinding
import ir.food.operatorAndroid.helper.DateHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.helper.VoiceDownloader
import ir.food.operatorAndroid.model.RecentCallsModel
import java.io.File
import java.util.*

class RecentCallsAdapter(private val recentCallsModels: ArrayList<RecentCallsModel>) :
    RecyclerView.Adapter<RecentCallsAdapter.ViewHolder?>() {

    private var position = 0


    class ViewHolder(val binding: ItemRecentCallsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentCallsBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSLight)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model: RecentCallsModel = recentCallsModels[position]
        holder.binding.txtDate.text = DateHelper.parseFormatToString(model.txtDate)
        holder.binding.txtTime.text = DateHelper.parseFormat(model.txtDate)
        if (model.phone == null) {
            holder.binding.llPhone.visibility = View.GONE
        } else {
            holder.binding.txtPassengerTell.text = model.phone
        }
        if (model.destinationOperator?.isEmpty() == true) {
            holder.binding.llDestinationOperator.visibility = View.GONE
        } else {
            holder.binding.txtDestinationOperator.text = model.destinationOperator
        }
        holder.binding.imgPlay.setOnClickListener {
            if (VoiceDownloader().mediaPlayer.isPlaying) {
                VoiceDownloader().pauseVoice()
            }
            this.position = position
            VoiceDownloader().skbTimer = holder.binding.skbTimer
            holder.binding.vfPlayPause.displayedChild = 1
            Log.i("URL", "show: " + EndPoints.GET_ORDER_DETAILS + model.voipId)//todo
            val voiceName: String = model.voipId + ".mp3"
            val file: File = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + File.separator + "operatorParsian/" + voiceName
                )
            } else {
                File(MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME + "/" + voiceName)
            }
            val voipId: String = model.voipId
            if (file.exists()) {
                VoiceDownloader().initVoice(Uri.fromFile(file))
                VoiceDownloader().playVoice()
            } else if (voipId == "0") {
                MyApplication.Toast("صوتی برای این تماس وجود ندارد", Toast.LENGTH_SHORT)
                holder.binding.vfPlayPause.displayedChild = 0
            } else {
                VoiceDownloader().startDownload(EndPoints.GET_ORDER_DETAILS + model.voipId, voiceName)//todo
            }
            holder.binding.skbTimer.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    val timeRemaining: Int = seekParams.progress / 1000
                    val strTimeRemaining = String.format(
                        Locale("en_US"),
                        "%02d:%02d",
                        timeRemaining / 60,
                        timeRemaining % 60
                    )
                    holder.binding.txtTimeRemaining.text = strTimeRemaining
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    if (VoiceDownloader().mediaPlayer != null) {
                        if (seekBar != null) {
                            VoiceDownloader().mediaPlayer.seekTo(seekBar.progress)
                        }
                    }
                }
            }
        }
        holder.binding.imgPause.setOnClickListener { VoiceDownloader().pauseVoice() }
    }

    override fun getItemCount(): Int {
        return recentCallsModels.size
    }
}