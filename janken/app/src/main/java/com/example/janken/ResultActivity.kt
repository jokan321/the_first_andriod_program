package com.example.janken

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.janken.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private val gu = 0
    private val choki = 1
    private var pa = 2
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.edit {
            clear()
        }

        //玩家手势决定
        val myHand: Int = when (intent.getIntExtra("MY_HAND", 0)) {
            R.id.guButton -> {
                binding.myHandImage.setImageResource(R.drawable.gu)
                gu
            }
            R.id.chokiButton -> {
                binding.myHandImage.setImageResource(R.drawable.choki)
                choki
            }
            R.id.paButton -> {
                binding.myHandImage.setImageResource(R.drawable.pa)
                pa
            }
            else -> gu
        }

        //电脑手势决定
        val comHand: Int = getComHand()
        when (comHand) {
            gu -> binding.comHandImage.setImageResource(R.drawable.com_gu)
            choki -> binding.comHandImage.setImageResource(R.drawable.com_choki)
            pa -> binding.comHandImage.setImageResource(R.drawable.com_pa)
        }
        val gameResult: Int = (comHand - myHand + 3) % 3
        //结果决定
        when (gameResult) {
            0 -> binding.resultLabel.setText(R.string.result_draw)
            1 -> binding.resultLabel.setText(R.string.result_win)
            2 -> binding.resultLabel.setText(R.string.result_lose)
        }
        //设定返回按钮
        binding.backButton.setOnClickListener { finish() }
        saveDate(myHand,comHand,gameResult)
    }

    private fun saveDate(myHand: Int, comHand: Int, gameResult: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount: Int = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0)
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        val lastGameResult = pref.getInt("GAME_RESULT", 0)
        val edtWinningStreakCount: Int =
            when {
                lastGameResult == 2 && gameResult == 2 -> winningStreakCount + 1
                else -> 0
            }
        pref.edit {
            putInt("GAME_COUNT", gameCount + 1)
            putInt("WINNING_STREAK_COUNT", edtWinningStreakCount)
            putInt("LAST_MY_HAND", myHand)
            putInt("LAST_COM_HAND", comHand)
            putInt("BEFORE_LAST_COM_HAND", lastComHand)
            putInt("GAME_RESULT", gameResult)
        }
    }

    /**
     * 电脑出拳套路指定
     * ·第一回合输了的场合，下一回合出能赢对方的手势
     * ·第一回合赢了的场合，下一回合随机改变出的手势
     * ·同样手势连胜的场合，下一回合随机改变手势
     * ·上记以外的场合，随机手势
     */
    private fun getComHand(): Int {
        var hand = (Math.random() * 3).toInt()
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        //比赛回数
        val gameCount = pref.getInt("GAME_COUNT",0)
        //连胜回数
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0)
        //上一把玩家手势
        val lastMyHand = pref.getInt("LAST_MY_HAND",0)
        //上一把电脑手势
        val lastComHand = pref.getInt("LAST_COM_HAND",0)
        //上上把电脑手势
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND",0)
        //游戏结果
        val gameResult = pref.getInt("",-1)
        if (gameCount == 1) {
            if (gameResult == 2) {
                //第一回合赢了的场合，下一回合随机改变出的手势
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            } else if (gameResult == 1) {
                //第一回合输了的场合，下一回合出能赢对方的手势
                hand = (lastMyHand -1 + 3) % 3
            }
        } else if (winningStreakCount > 0) {
            //同样手势连胜的场合，下一回合随机改变手势
            if (beforeLastComHand == lastComHand) {
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            }
        }
        return hand
    }
}