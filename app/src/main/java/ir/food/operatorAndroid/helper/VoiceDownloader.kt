package ir.food.operatorAndroid.helper

import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.ViewFlipper
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Progress
import com.warkiz.widget.IndicatorSeekBar
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.okHttp.AuthenticationInterceptor
import ir.food.operatorAndroid.push.AvaCrashReporter
import java.io.File
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class VoiceDownloader {

    var isDownloading = false
    var TOTAL_VOICE_DURATION = 0
    lateinit var vfPlayPause: ViewFlipper
    lateinit var mediaPlayer: MediaPlayer
    private var timer: Timer? = null
    var skbTimer: IndicatorSeekBar? = null

    fun startDownload(urlString: String, fileName: String) {
        try {
            val url = URL(urlString)
            //            String dirPathTemp = MyApplication.DIR_ROOT + "temp/";
            val dirPath: String = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + "operatorParsian/"
            } else {
                MyApplication.DIR_ROOT.toString() + "voice/"
            }
            File(dirPath).mkdirs()
            val file = File(dirPath)
            if (file.isDirectory) {
                val children = file.list()
                for (i in children.indices) {
                    File(file, children[i]).delete()
                }
            }

//      File file = new File(dirPathTemp + fileName);
//      int downloadId = FindDownloadId.execte(urlString);
//      if (file.exists() && downloadId != -1) {
//        PRDownloader.resume(downloadId);
//      } else {
//        downloadId =
            PRDownloader.download(url.toString(), dirPath, fileName)
                .setHeader("Authorization", MyApplication.prefManager.authorization)
                .setHeader("id_token", MyApplication.prefManager.idToken)
                .build()
                .setOnStartOrResumeListener {}
                .setOnPauseListener { isDownloading = false }
                .setOnCancelListener { isDownloading = false }
                .setOnProgressListener { progress: Progress ->
                    isDownloading = true
                    Log.i("TAG", "startDownload: $progress")
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        isDownloading = false
                        //                            FileHelper.moveFile(dirPathTemp, fileName, dirPath);
                        val file = File(dirPath + fileName)
                        MyApplication.handler.postDelayed({
//                            if (view != null) {
                                initVoice(Uri.fromFile(file))
                                playVoice()
//                            } todo
                        }, 200)
                    }

                    override fun onError(error: Error) {
                        isDownloading = false
                        Log.e("PlayConversationDialog", "onError: " + error.responseCode + "")
                        Log.e("getServerErrorMessage", "onError: " + error.serverErrorMessage + "")
                        Log.e(
                            "getConnectionException",
                            "onError: " + error.connectionException + ""
                        )
                        FileHelper.deleteFile(dirPath, fileName)
                        if (error.responseCode == 401) RefreshTokenAsyncTask().execute()
//                        if (error.responseCode == 404) vfVoiceStatus.setDisplayedChild(
//                            1
//                        ) todo
                    }
                })

//        StartDownload.execute(downloadId, url.toString(), dirPathTemp + fileName);
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initVoice(uri: Uri) {
        try {
//            if (view != null) { todo
                mediaPlayer = MediaPlayer.create(MyApplication.context, uri)
                mediaPlayer.setOnCompletionListener {
                    vfPlayPause.displayedChild = 0
//                }
                TOTAL_VOICE_DURATION = mediaPlayer.duration
                skbTimer?.max = TOTAL_VOICE_DURATION.toFloat()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playVoice() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.start()
                vfPlayPause.displayedChild = 2
                startTimer()
            }
        } catch (e: Exception) {
            AvaCrashReporter.send(e, "RecentCallsAdapter in playVoice")
        }
    }

    private fun startTimer() {
        if (timer != null) {
            return
        }
        timer = Timer()
        val task = UpdateSeekBar()
        timer!!.scheduleAtFixedRate(task, 500, 1000)
    }

    fun pauseVoice() {
        try {
            mediaPlayer.pause()
            skbTimer?.setProgress(0F)
            vfPlayPause.displayedChild = 0
        } catch (e: Exception) {
        }
        cancelTimer()
    }

    inner class UpdateSeekBar : TimerTask() {
        override fun run() {
            if (mediaPlayer != null) {
                try {
                    MyApplication.handler.post {
                        skbTimer?.setProgress(mediaPlayer.currentPosition.toFloat())
                        val timeRemaining: Int = mediaPlayer.currentPosition / 1000
                        val strTimeRemaining = String.format(
                            Locale("en_US"),
                            "%02d:%02d",
                            timeRemaining / 60,
                            timeRemaining % 60
                        )
//                        if (txtTimeRemaining != null) txtTimeRemaining.setText(
//                            strTimeRemaining todo
//                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal class RefreshTokenAsyncTask : AsyncTask<Void?, Void?, Boolean?>() {
        override fun doInBackground(vararg p0: Void?): Boolean? {
            AuthenticationInterceptor().refreshToken()
            return null
        }
    }

    fun cancelTimer() {
        try {
            if (timer == null) return
            timer!!.cancel()
            timer = null
        } catch (e: Exception) {
        }
    }

}