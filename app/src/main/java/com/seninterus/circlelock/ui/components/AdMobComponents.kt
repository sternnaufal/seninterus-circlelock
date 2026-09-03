package com.seninterus.circlelock.ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobManager {
    // Ganti dengan Unit ID testing Anda
    private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // Ganti dengan Unit ID production setelah testing
    // private const val BANNER_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/BBBBBBBBBB"
    // private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/IIIIIIIIII"

    fun getBannerAdUnitId() = BANNER_AD_UNIT_ID
    fun getInterstitialAdUnitId() = INTERSTITIAL_AD_UNIT_ID
}

@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.getBannerAdUnitId()
) {
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current

    if (isInPreview) {
        Box(modifier = modifier.fillMaxWidth().height(50.dp))
        return
    }

    var adView by remember { mutableStateOf<AdView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            adView?.destroy()
        }
    }

    AndroidView(
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        // Retry atau handle error
                    }
                }
                loadAd(AdRequest.Builder().build())
                adView = this
            }
        },
        modifier = modifier.fillMaxWidth().height(50.dp)
    )
}

class InterstitialAdHelper(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun loadAd() {
        if (isLoading || interstitialAd != null) return
        isLoading = true

        InterstitialAd.load(
            context,
            AdMobManager.getInterstitialAdUnitId(),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity, onAdDismissed: () -> Unit = {}) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd()
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    loadAd()
                    onAdDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            loadAd()
            onAdDismissed()
        }
    }

    fun isLoaded(): Boolean = interstitialAd != null
}
