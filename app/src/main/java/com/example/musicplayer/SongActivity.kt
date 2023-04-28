package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivitySongBinding
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class SongActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongBinding
    private var mediaPlayer: MediaPlayer? = null
    private var seekLength : Int = 0
    private var countPrevious : Int = 0
    private var position : Int = 0
    private var isPlaying : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var songDuration = intent.getStringExtra("songDuration")
        val imageArray = intent.getIntArrayExtra("imageArray")
        val songNameArray = intent.getStringArrayExtra("songNameArray")
        val artistNameArray = intent.getStringArrayExtra("artistNameArray")
        var songIdArray = intent.getIntArrayExtra("songIdArray")
        var result = intent.getIntExtra("result",  R.drawable.item1)
        position = intent.getIntExtra("position", R.drawable.item1)
        isPlaying = intent.getBooleanExtra("isPlaying", true)

        binding.songName.text = songNameArray!![position]
        binding.artistName.text  =artistNameArray!![position]
        binding.imageId.setImageResource(imageArray!![position])
        binding.songDuration.text = songDuration

        mediaPlayer = MediaPlayer.create(this, songIdArray!![position])


        mediaPlayer!!.seekTo(result)
        isPlaying = if(isPlaying){
            mediaPlayer!!.start()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            true
        }else{
            binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
            false
        }
        updateSeekBar()

        binding.play.setOnClickListener {
            playSong()
        }

        binding.forward.setOnClickListener {
            mediaPlayer!!.release()
            mediaPlayer = null

            if (position< songNameArray!!.size - 1) {
                position += 1
            } else{
                position = 0
            }

            binding.songName.text = songNameArray!![position]
            binding.artistName.text  = artistNameArray!![position]
            binding.imageId.setImageResource(imageArray!![position])
            mediaPlayer = MediaPlayer.create(this, songIdArray!![position])
            val duration = mediaPlayer!!.duration
            val durationLong = duration.toLong()
            binding.songDuration.text = durationConverter(durationLong)
            mediaPlayer!!.start()
            updateSeekBar()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            next(songNameArray,  artistNameArray, imageArray, songIdArray)

        }

        binding.rewind.setOnClickListener {
            mediaPlayer!!.release()
            mediaPlayer = null
            if (countPrevious == 0){
                updateSeekBar()
                countPrevious++
            }
            else {
                if (position > 0) {
                    position -= 1
                } else {
                    position = songNameArray.size - 1
                }
                countPrevious = 0
            }
            binding.songName.text = songNameArray!![position]
            binding.artistName.text  = artistNameArray!![position]
            binding.imageId.setImageResource(imageArray!![position])
            mediaPlayer = MediaPlayer.create(this, songIdArray!![position])
            val duration = mediaPlayer!!.duration
            val durationLong = duration.toLong()
            binding.songDuration.text = durationConverter(durationLong)
            mediaPlayer!!.start()
            updateSeekBar()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            next(songNameArray,  artistNameArray, imageArray, songIdArray)

            }

        binding.backButt.setOnClickListener{
            onBackPressed()
        }

        mediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            mediaPlayer!!.release()
            mediaPlayer = null

            if (position< songNameArray!!.size - 1) {
                position += 1
            } else{
                position = 0
            }

            binding.songName.text = songNameArray!![position]
            binding.artistName.text  = artistNameArray!![position]
            binding.imageId.setImageResource(imageArray!![position])
            mediaPlayer = MediaPlayer.create(this, songIdArray!![position])
            val duration = mediaPlayer!!.duration
            val durationLong = duration.toLong()
            binding.songDuration.text = durationConverter(durationLong)
            mediaPlayer!!.start()
            updateSeekBar()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            next(songNameArray,  artistNameArray, imageArray, songIdArray)
        })


    }

    fun next(songNameArray: Array<String>?, artistNameArray: Array<String>?, imageArray: IntArray?, songIdArray: IntArray?){
        mediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            mediaPlayer!!.release()
            mediaPlayer = null

            if (position< songNameArray!!.size - 1) {
                position += 1
            } else{
                position = 0
            }

            binding.songName.text = songNameArray!![position]
            binding.artistName.text  = artistNameArray!![position]
            binding.imageId.setImageResource(imageArray!![position])
            mediaPlayer = MediaPlayer.create(this, songIdArray!![position])
            val duration = mediaPlayer!!.duration
            val durationLong = duration.toLong()
            binding.songDuration.text = durationConverter(durationLong)
            mediaPlayer!!.start()
            updateSeekBar()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
        })
    }



    private fun updateSeekBar(){

        if (mediaPlayer!= null){
            binding.songTime.text = durationConverter(
                mediaPlayer!!.currentPosition.toLong()
            )
        }

        seekBarSetup()
        Handler().postDelayed(runnable, 50)
    }

    private var runnable = Runnable { updateSeekBar() }

    private fun seekBarSetup() {

        if (mediaPlayer!= null){
            binding.seekbar.progress = mediaPlayer!!.currentPosition
            binding.seekbar.max = mediaPlayer!!.duration
        }

        binding.seekbar.setOnSeekBarChangeListener(
            @SuppressLint(/* ...value = */ "AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2){
                    mediaPlayer!!.seekTo(p1)
                    binding.songTime.text = durationConverter(p1.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if(mediaPlayer != null && mediaPlayer!!.isPlaying){

                    if (p0 != null){
                        mediaPlayer!!.seekTo(p0.progress)
                    }
                }

            }

        })
    }

    private fun playSong(){
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.seekTo(seekLength)
            mediaPlayer!!.start()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            updateSeekBar()
        }
        else{
            mediaPlayer!!.pause()
            seekLength = mediaPlayer!!.currentPosition
            binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
        }

    }

    private fun clearMediaPlayer() {
        if(mediaPlayer!!.isPlaying){
            mediaPlayer!!.stop()
        }
        mediaPlayer!!.release()
        mediaPlayer = null
    }

    private fun durationConverter(duration : Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(duration)
                    )
        )
    }
    override fun onBackPressed() {
        intent.putExtra("result", mediaPlayer!!.currentPosition)
        intent.putExtra("newPosition", position)
        intent.putExtra("isPlaying", isPlaying)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
       clearMediaPlayer()
    }

}