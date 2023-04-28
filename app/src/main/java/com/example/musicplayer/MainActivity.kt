package com.example.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.databinding.ActivitySongBinding
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var songArrayList : ArrayList<Song>

    private lateinit var mediaPlayer: MediaPlayer

    private var isPlaying: Boolean = true

    private val imageIdArr = intArrayOf(

        R.drawable.item1, R.drawable.item2, R.drawable.item3,
        R.drawable.item4, R.drawable.item5, R.drawable.yellow,
        R.drawable.youngblood, R.drawable.liar, R.drawable.come_as_you_are,
        R.drawable.sun_and_moon
    )
    private val songIdArray = intArrayOf(

        R.raw.music1, R.raw.music2, R.raw.music3,
        R.raw.music4, R.raw.music5, R.raw.yellow,
        R.raw.youngblood, R.raw.liar, R.raw.come_as_you_are,
        R.raw.sun_and_moon
    )

    private val songNameArray = arrayOf(

        "Good in Bed",
        "The Man",
        "Stupid Love",
        "Paper Rings",
        "Exodus Honey",
        "Yellow",
        "Youngblood",
        "Liar",
        "Come As You Are",
        "Sun and Moon"
    )

    private val artistNameArray = arrayOf(

        "Dua Lipa",
        "Taylor Swift",
        "Lady Gaga",
        "Taylor Swift",
        "Honeycut",
        "Coldplay",
        "5SOS",
        "Camila Cabello",
        "Nirvana",
        "Anees"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        songArrayList = ArrayList()

        for (i in songNameArray.indices){

            mediaPlayer = MediaPlayer.create(this, songIdArray[i])
            val duration = mediaPlayer.duration
            val durationLong = duration.toLong()
            val song = Song(songNameArray[i], artistNameArray[i], imageIdArr[i], songIdArray[i], durationConverter(durationLong))
            songArrayList.add(song)

        }

        binding.listView.isClickable = true
        binding.listView.adapter = MyAdapter(this, songArrayList)

        binding.listView.setOnItemClickListener { parent, view, position, id ->
            mediaPlayer.stop()
            mediaPlayer = MediaPlayer.create(this, songArrayList[position].songId)
            openSong(position, mediaPlayer, isPlaying)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                var newPosition = data!!.getIntExtra("newPosition", R.drawable.fading)                // Get the result from intent
                val result = data!!.getIntExtra("result", R.drawable.fading)
                isPlaying = data!!.getBooleanExtra("isPlaying", true)

                mediaPlayer = MediaPlayer.create(this, songArrayList[newPosition].songId)
                mediaPlayer.seekTo(result)
                isPlaying = if(isPlaying){
                    binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
                    false
                }else{
                    mediaPlayer.start()
                    binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
                    true
                }
                binding.musController.visibility = View.VISIBLE
                binding.imageId.setImageResource(songArrayList[newPosition].imageId)
                binding.artistName.text=artistNameArray[newPosition]
                binding.songName.text=songNameArray[newPosition]
                mediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                    mediaPlayer!!.release()
                    //mediaPlayer = null

                    if (newPosition!! < songNameArray!!.size - 1) {
                        newPosition += 1
                    } else{
                        newPosition = 0
                    }

                    binding.imageId.setImageResource(songArrayList[newPosition].imageId)
                    binding.artistName.text=artistNameArray[newPosition]
                    binding.songName.text=songNameArray[newPosition]
                    mediaPlayer = MediaPlayer.create(this, songArrayList[newPosition].songId)
                    mediaPlayer!!.start()
                    //binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
                })


                binding.musController.isClickable = true

                binding.play.setOnClickListener {
                    playSong()
                }

                binding.musController.setOnClickListener {
                    openSong(newPosition, mediaPlayer, isPlaying)
                }
            }
        }
    }


    private fun playSong(){
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            isPlaying = true
        }
        else{
            mediaPlayer!!.pause()
            binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
            isPlaying = false
        }
    }

    private fun openSong(position : Int, mediaPlayer: MediaPlayer, isPlaying : Boolean){
        val duration = mediaPlayer.duration
        val durationLong = duration.toLong()

        var i = Intent(this, SongActivity::class.java)
        val songName = songNameArray[position]
        val artistName = artistNameArray[position]
        val imageId = imageIdArr[position]
        val songId = songIdArray[position]

        i.putExtra("songName", songName)
        i.putExtra("artistName", artistName)
        i.putExtra("imageId", imageId)
        i.putExtra("songId", songId)
        i.putExtra("songDuration", durationConverter(durationLong))
        i.putExtra("position", position)
        i.putExtra("result", mediaPlayer!!.currentPosition)
        i.putExtra("imageArray", imageIdArr)
        i.putExtra("songNameArray", songNameArray)
        i.putExtra("artistNameArray", artistNameArray)
        i.putExtra("songIdArray", songIdArray)
        i.putExtra("isPlaying", isPlaying)


        startActivityForResult(i, 0)

        if(mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
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


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

}