package uz.instat.jokerup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import uz.instat.jokerup.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(
            this
        ) { }
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("ABCDEF012345"))
                .build()
        )

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        binding.adView.loadAd(adRequest)
    }

    // Called when leaving the activity
    public override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        binding.adView.destroy()
        super.onDestroy()

    }
}