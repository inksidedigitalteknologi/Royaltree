package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Royaltree", appName)
  }

  @Test
  fun `step to coin conversion formula calculation`() {
    val steps = 500
    // 100 steps = 10 coins = Rp 100
    val coinsEarned = (steps / 100) * 10
    val rupiahEarned = coinsEarned * 10.0
    assertEquals(50, coinsEarned)
    assertEquals(500.0, rupiahEarned, 0.001)
  }
}
