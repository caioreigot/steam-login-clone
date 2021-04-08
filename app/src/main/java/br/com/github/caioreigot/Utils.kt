package br.com.github.caioreigot

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


class Utils : AppCompatActivity()  {

    companion object {
        fun globalDispatchTouchEvent(activity: Activity, parentLayoutID: Int, event: MotionEvent) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val v: View? = activity.currentFocus

                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)

                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        hideKeyboard(activity, parentLayoutID)
                    }
                }
            }
        }

        private fun hideKeyboard(activity: Activity, parentLayoutID: Int) {
            var viewGroup: LinearLayout? = activity.findViewById(parentLayoutID)
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(viewGroup?.windowToken, 0)
        }

        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun createCustomSnackbar(v: View, text: String, drawableR: Int, colorR: Int): Snackbar {
            // Dica: "v" pode ser passado como window.decorView
            val snackbar = Snackbar.make(v.rootView, text, Snackbar.LENGTH_LONG)
            val snackbarView = snackbar.view
            val sbTextView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

            // Setando o estilo do fundo da snackbar
            snackbarView.setBackgroundResource(R.drawable.snackbar_rounded_corner)
            var svDrawable = snackbarView.background as GradientDrawable
            svDrawable.setColor(ContextCompat.getColor(v.context, R.color.snackbarRedBackground))

            // Alinhando o texto ao centro e deixando BOLD
            sbTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            sbTextView.setTypeface(null, Typeface.BOLD)

            return snackbar
        }
    }

}