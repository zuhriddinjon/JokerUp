package uz.instat.jokerup

import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import uz.instat.jokerup.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var life: Int = 3
    private var rating: Int = 0
    private val cardList: MutableList<Bitmap> = mutableListOf()
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var bitmap = viewModel.drawableToBitmap(getDrawable(R.drawable.card_1))
        if (bitmap != null) cardList.add(bitmap)
        bitmap = viewModel.drawableToBitmap(getDrawable(R.drawable.card_2))
        if (bitmap != null) cardList.add(bitmap)
        bitmap = viewModel.drawableToBitmap(getDrawable(R.drawable.card_3))
        if (bitmap != null) cardList.add(bitmap)
        bitmap = viewModel.drawableToBitmap(getDrawable(R.drawable.card_4))
        if (bitmap != null) cardList.add(bitmap)
        bitmap = viewModel.drawableToBitmap(getDrawable(R.drawable.card_5))
        if (bitmap != null) cardList.add(bitmap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        binding.tvLife.text = getString(R.string.life_player, life)
        binding.tvRating.text = getString(R.string.game_rating, rating)
        binding.btnStart.setOnClickListener {
            startGame()
            binding.btnStart.isVisible = false
        }

    }

    private fun startGame() {
        if (!mAdIsLoading && mInterstitialAd == null) {
            mAdIsLoading = true
            loadAd()
        }

        binding.animView.setCardList(cardList)
        binding.animView.setEventListener(object : IEventListener {

            override fun incrementLife() {
                life++
                binding.tvLife.text = getString(R.string.life_player, life)
            }

            override fun decrementLife() {
                life--
                binding.tvLife.text = getString(R.string.life_player, life)
                if (life == 0) {
                    binding.animView.finish()
                    showDialog()
                    binding.btnStart.isVisible = true
                }

            }

            override fun incrementRating() {
                rating++
                binding.tvRating.text = getString(R.string.game_rating, rating)
            }

            override fun decrementRating() {
                rating--
                binding.tvRating.text = getString(R.string.game_rating, rating)
            }
        })

    }

    private fun showDialog() {
        dialog = AlertDialog.Builder(requireContext())
            .setTitle("Finish Game")
            .setMessage(binding.tvRating.text)
            .setPositiveButton("Start the game over") { dialog, which ->
                life = 3
                rating = 0
                showInterstitial()
                dialog.dismiss()
            }
            .setNegativeButton("Exit the application") { dialog, which ->
                activity?.finish()
            }
            .setCancelable(false)
            .create()
        dialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private var mInterstitialAd: InterstitialAd? = null
    private var mCountDownTimer: CountDownTimer? = null
    private var mAdIsLoading: Boolean = false
    private var TAG = "MainFragment"

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(), "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                }
            }
        )
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    resetGame()
                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(requireActivity())
        } else {
            Toast.makeText(requireContext(), "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetGame() {
        binding.btnStart.isVisible = false
        binding.tvLife.text = getString(R.string.life_player, life)
        binding.tvRating.text = getString(R.string.game_rating, rating)
        binding.animView.reset()
    }


    // Cancel the timer if the game is paused.
    override fun onPause() {
        mCountDownTimer?.cancel()
        super.onPause()
    }

}