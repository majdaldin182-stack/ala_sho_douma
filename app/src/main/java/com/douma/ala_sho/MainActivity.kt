package com.douma.ala_sho

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import android.widget.*
import android.text.Editable
import android.text.TextWatcher

class MainActivity : Activity() {
    private val burgundy = Color.rgb(126, 23, 56)
    private val green = Color.rgb(35, 83, 67)
    private val cream = Color.rgb(245, 239, 228)
    private val ink = Color.rgb(48, 43, 41)
    private lateinit var content: LinearLayout
    private lateinit var search: EditText
    private val categories = listOf(
        "🚌" to "المواصلات", "💊" to "الصيدليات", "🏥" to "المشافي",
        "🩺" to "الأطباء", "🛍️" to "المحلات والمنتجات", "🚨" to "أرقام الطوارئ",
        "📢" to "الإعلانات والعروض", "📝" to "الشكاوى للمجلس", "📱" to "تطبيقات وخدمات"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun text(value: String, size: Float, color: Int = ink, bold: Boolean = false): TextView =
        TextView(this).apply {
            this.text = value; textSize = size; setTextColor(color)
            gravity = Gravity.RIGHT
            if (bold) setTypeface(typeface, Typeface.BOLD)
            textDirection = View.TEXT_DIRECTION_RTL
        }
    private fun button(label: String, action: () -> Unit): TextView = text(label, 16f, burgundy, true).apply {
        gravity = Gravity.CENTER
        setPadding(dp(12), dp(14), dp(12), dp(14))
        background = rounded(Color.WHITE, dp(14))
        setOnClickListener { action() }
    }
    private fun rounded(color: Int, radius: Int) = android.graphics.drawable.GradientDrawable().apply {
        setColor(color); cornerRadius = radius.toFloat()
        setStroke(dp(1), Color.rgb(229, 220, 207))
    }
    private fun base(title: String, subtitle: String? = null) {
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(dp(18), dp(18), dp(18), dp(24))
            setBackgroundColor(cream); layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        scroll.addView(root)
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(dp(18), dp(20), dp(18), dp(20))
            background = rounded(green, dp(20))
        }
        header.addView(text(title, 25f, Color.WHITE, true))
        if (subtitle != null) { val sub = text(subtitle, 14f, Color.rgb(232, 218, 184)); sub.setPadding(0, dp(8), 0, 0); header.addView(sub) }
        root.addView(header, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) })
        content = root
        setContentView(scroll)
    }
    private fun showHome() {
        base("على شو عم تدور بدوما؟", "كل شي بدك ياه، بتلاقيه هون")
        search = EditText(this).apply {
            hint = "ابحث عن محل، منتج، طبيب أو خدمة..."
            textSize = 15f; singleLine = true; gravity = Gravity.RIGHT
            setPadding(dp(14), dp(12), dp(14), dp(12)); background = rounded(Color.WHITE, dp(14))
            textDirection = View.TEXT_DIRECTION_RTL
        }
        content.addView(search, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) })
        content.addView(text("الخدمات الرئيسية", 19f, burgundy, true), LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) })
        val grid = GridLayout(this).apply { columnCount = 2; layoutDirection = View.LAYOUT_DIRECTION_RTL }
        categories.forEach { (emoji, name) ->
            val tile = TextView(this).apply {
                text = "$emoji\n$name"; textSize = 16f; gravity = Gravity.CENTER
                setTextColor(ink); setTypeface(typeface, Typeface.BOLD)
                setPadding(dp(8), dp(18), dp(8), dp(18)); background = rounded(Color.WHITE, dp(16))
                setOnClickListener { showSection(name) }
            }
            val lp = GridLayout.LayoutParams().apply {
                width = 0; height = dp(106); columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }
            grid.addView(tile, lp)
        }
        content.addView(grid)
        val note = text("نسخة تجريبية أولية • المعلومات والخدمات تحتاج إلى إدخال وتحقق قبل الإطلاق", 12f, Color.DKGRAY)
        note.setPadding(0, dp(16), 0, 0); content.addView(note)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim().orEmpty()
                if (q.isNotEmpty()) {
                    val match = categories.firstOrNull { it.second.contains(q, true) }
                    if (match != null) { /* category remains accessible in grid */ }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun showSection(name: String) {
        base(name, "خدمة ضمن تطبيق على شو عم تدور بدوما؟")
        content.addView(button("رجوع إلى الرئيسية") { showHome() }, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) })
        when (name) {
            "المواصلات" -> {
                content.addView(text("اختر الخط", 19f, burgundy, true))
                content.addView(button("خط دوما – دمشق") { showInfo("خط دوما – دمشق", "تُضاف المواقف والأجور ومواعيد الانطلاق بعد التحقق من المعلومات المحلية.") }, margin())
                content.addView(button("الخطوط ضمن دوما") { showInfo("المواصلات الداخلية", "تُضاف أسماء الخطوط والمواقف والمسارات والأجور بعد التحقق منها.") }, margin())
            }
            "الشكاوى للمجلس" -> complaintForm()
            "تطبيقات وخدمات" -> {
                content.addView(text("بوابة التطبيقات والخدمات", 19f, burgundy, true))
                content.addView(text("يمكن إضافة BeeOrder أو خدمات أخرى هنا بعد تأكيد الرابط والتغطية داخل دوما.", 15f))
                content.addView(button("فتح صفحة BeeOrder على Google Play") {
                    openUrl("https://play.google.com/store/apps/details?id=com.beeorder.customer")
                }, margin())
            }
            "المشافي" -> showInfo("المشافي", "سيُضاف دليل المشافي وأرقام التواصل والمواقع وساعات العمل بعد التحقق.")
            "الصيدليات" -> showInfo("الصيدليات المناوبة", "ستُعرض الصيدليات المناوبة مع تاريخ آخر تحديث ورقم الاتصال بعد إدخال البيانات والتحقق منها.")
            "الأطباء" -> showInfo("دليل الأطباء والعيادات", "يمكن للطبيب طلب إضافة عيادته، مع مراجعة بياناته قبل النشر.")
            "المحلات والمنتجات" -> showInfo("المحلات والمنتجات", "ابحث عن المحلات حسب النشاط أو المنتج. قاعدة المحلات تحتاج إلى تعبئة.")
            "الإعلانات والعروض" -> showInfo("الإعلانات والعروض", "مساحة للإعلانات المحلية وعروض المحلات بعد تفعيل لوحة الإدارة.")
            "أرقام الطوارئ" -> showInfo("أرقام الطوارئ", "لن نعرض أرقاماً غير مؤكدة. تُضاف الأرقام المحلية بعد التحقق من صحتها.")
            else -> showInfo(name, "هذا القسم قيد التجهيز.")
        }
    }
    private fun margin() = LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(10); bottomMargin = dp(8) }
    private fun showInfo(title: String, desc: String) {
        base(title, "على شو عم تدور بدوما؟")
        content.addView(text(desc, 17f), margin())
        content.addView(button("رجوع للرئيسية") { showHome() }, margin())
    }
    private fun complaintForm() {
        content.addView(text("إرسال شكوى للمجلس", 20f, burgundy, true), margin())
        content.addView(text("نوع الشكوى", 15f, ink, true))
        val type = Spinner(this)
        val options = listOf("طرق وحفر", "نظافة ونفايات", "إنارة", "مياه وصرف صحي", "شكوى أخرى", "مقترح")
        type.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        content.addView(type, margin())
        val details = EditText(this).apply {
    hint = "اشرح المشكلة ومكانها..."
    minLines = 4
    gravity = Gravity.TOP or Gravity.RIGHT
    setPadding(dp(12), dp(12), dp(12), dp(12))
    background = rounded(Color.WHITE, dp(12))
    textDirection = View.TEXT_DIRECTION_RTL
}
content.addView(details, margin())

content.addView(button("إرفاق صورة") {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
    }
    startActivityForResult(intent, 301)
}, margin())

content.addView(button("تحديد الموقع على الخريطة") {
    openUrl("https://www.google.com/maps/search/?api=1&query=Douma")
}, margin())

content.addView(button("تجهيز رسالة الشكوى للمشاركة") {
    val body = "شكوى من تطبيق على شو عم تدور بدوما؟\n" +
        "النوع: ${type.selectedItem}\n" +
        "التفاصيل: ${details.text}\n" +
        "يرجى تحديد جهة الاستلام الرسمية قبل الإرسال."

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        setType("text/plain")
        putExtra(Intent.EXTRA_TEXT, body)
    }
    startActivity(Intent.createChooser(shareIntent, "مشاركة الشكوى"))
}, margin())

val disclaimer = text(
    "ملاحظة: هذه النسخة لا ترسل الشكوى تلقائياً إلى المجلس ولا تحفظها في قاعدة بيانات. يلزم تحديد قناة رسمية وربطها قبل الإطلاق.",
    13f,
    Color.DKGRAY
)
disclaimer.setPadding(0, dp(12), 0, 0)
content.addView(disclaimer)
}

private fun openUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: Exception) {
        Toast.makeText(this, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
    }
}
}
