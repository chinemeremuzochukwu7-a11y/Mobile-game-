package com.example.ads

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Material-compliant AdMob Banner Composable.
 * Uses Google's official sample Banner unit ID.
 */
@SuppressLint("MissingPermission")
@Composable
fun AdMobBannerView(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobHelper.TEST_BANNER_ID
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
