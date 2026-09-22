package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages Google AdMob initialization, interstitial ads, and rewarded video ads.
 * Uses official Google sample test ad units for safe testing.
 */
class AdMobHelper(private val context: Context) {

    // Test Ad Unit IDs provided by Google for AdMob
    companion object {
        const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
        private const val TAG = "AdMobHelper"
    }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private val _isInterstitialReady = MutableStateFlow(false)
    val isInterstitialReady: StateFlow<Boolean> = _isInterstitialReady.asStateFlow()

    private val _isRewardedReady = MutableStateFlow(false)
    val isRewardedReady: StateFlow<Boolean> = _isRewardedReady.asStateFlow()

    fun initialize() {
        try {
            MobileAds.initialize(context) { initializationStatus ->
                Log.d(TAG, "AdMob initialized: ${initializationStatus.adapterStatusMap}")
                loadInterstitialAd()
                loadRewardedAd()
            }
        } catch (e: Exception) {
            Log.e(TAG, "AdMob initialization failed", e)
        }
    }

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    _isInterstitialReady.value = true
                    Log.d(TAG, "Interstitial ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    _isInterstitialReady.value = false
                    Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showInterstitial(
        activity: Activity,
        onAdDismissedOrFailed: () -> Unit
    ) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    _isInterstitialReady.value = false
                    loadInterstitialAd()
                    onAdDismissedOrFailed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    _isInterstitialReady.value = false
                    loadInterstitialAd()
                    onAdDismissedOrFailed()
                }
            }
            ad.show(activity)
        } else {
            // If ad not loaded yet, proceed seamlessly without blocking user
            loadInterstitialAd()
            onAdDismissedOrFailed()
        }
    }

    fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            TEST_REWARDED_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    _isRewardedReady.value = true
                    Log.d(TAG, "Rewarded ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    _isRewardedReady.value = false
                    Log.w(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdClosed: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    _isRewardedReady.value = false
                    loadRewardedAd()
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    _isRewardedReady.value = false
                    loadRewardedAd()
                    onAdClosed()
                }
            }
            ad.show(activity) {
                onUserEarnedReward()
            }
        } else {
            loadRewardedAd()
            // Provide reward in test environment if ad is not cached yet
            onUserEarnedReward()
            onAdClosed()
        }
    }
}
