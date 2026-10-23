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
import com.google.firebase.firestore.FirebaseFirestore

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
            orientation = LinearLayout.HORIZONTAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(18), dp(14), dp(18))
            background = rounded(green, dp(20))
        }
        val logo = ImageView(this).apply {
            setImageResource(com.douma.ala_sho.R.drawable.douma_logo)
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = "شعار مدينة دوما"
        }
       header.addView(logo, LinearLayout.LayoutParams(dp(140), dp(70)).apply { marginStart = dp(10) })
        val heading = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }
        heading.addView(text(title, if (title == "على شو عم تدور بدوما؟") 21f else 23f, Color.WHITE, true))
        if (subtitle != null) {
            val sub = text(subtitle, 13f, Color.rgb(232, 218, 184))
            sub.setPadding(0, dp(7), 0, 0)
            heading.addView(sub)
        }
        header.addView(heading, LinearLayout.LayoutParams(0, -2, 1f))
        root.addView(header, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) })
        content = root
        setContentView(scroll)
    }
private fun showHome() {
    base("على شو عم تدور بدوما؟", "كل شي بدك ياه، بتلاقيه هون")

    search = EditText(this).apply {
        hint = "ابحث عن محل، منتج، طبيب أو خدمة..."
        textSize = 15f
setSingleLine(true)
        gravity = Gravity.RIGHT
        setPadding(dp(14), dp(12), dp(14), dp(12))
        background = rounded(Color.WHITE, dp(16))
        textDirection = View.TEXT_DIRECTION_RTL
    }

    content.addView(
        search,
        LinearLayout.LayoutParams(-1, dp(54)).apply {
            bottomMargin = dp(22)
        }
    )

    content.addView(
        text("الخدمات الرئيسية", 20f, burgundy, true),
        LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = dp(12)
        }
    )

    val grid = GridLayout(this).apply {
        columnCount = 2
        layoutDirection = View.LAYOUT_DIRECTION_RTL
        useDefaultMargins = false
    }

    categories.forEach { (emoji, name) ->
        val tile = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(14), dp(8), dp(14))
            background = rounded(Color.WHITE, dp(22))
            elevation = dp(2).toFloat()
            isClickable = true
            isFocusable = true
            setOnClickListener { showSection(name) }
        }

        tile.addView(
            text(emoji, 29f, burgundy, false).apply {
                gravity = Gravity.CENTER
            }
        )

        tile.addView(
            text(name, 16f, ink, true).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(9), 0, 0)
            }
        )

        val lp = GridLayout.LayoutParams().apply {
            width = 0
            height = dp(112)
            columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED, 1f
            )
            setMargins(dp(5), dp(5), dp(5), dp(5))
        }

        grid.addView(tile, lp)
    }

    content.addView(
        grid,
        LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = dp(24)
        }
    )

    // مساحة الإعلان العنابية
    val adBanner = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutDirection = View.LAYOUT_DIRECTION_RTL
        setPadding(dp(16), dp(16), dp(16), dp(16))

        background = android.graphics.drawable.GradientDrawable(
            android.graphics.drawable.GradientDrawable.Orientation
                .LEFT_RIGHT,
            intArrayOf(
                Color.rgb(83, 8, 25),
                burgundy,
                Color.rgb(105, 39, 57)
            )
        ).apply {
            cornerRadius = dp(28).toFloat()
        }

        elevation = dp(3).toFloat()
        isClickable = true
        isFocusable = true
        setOnClickListener {
            showSection("الإعلانات والعروض")
        }
    }

    val adText = TextView(this).apply {
        text = "ضع إعلانك هنا"
        textSize = 23f
        setTextColor(Color.WHITE)
        setTypeface(typeface, Typeface.BOLD)
        gravity = Gravity.CENTER
        textDirection = View.TEXT_DIRECTION_RTL
    }

    adBanner.addView(
        adText,
        LinearLayout.LayoutParams(0, dp(100), 1f)
    )

    val adImagePlaceholder = TextView(this).apply {
        text = "🏙️\nدوما"
        textSize = 20f
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)
        background = rounded(
            Color.rgb(113, 69, 79),
            dp(18)
        )
    }

    adBanner.addView(
        adImagePlaceholder,
        LinearLayout.LayoutParams(dp(105), dp(100))
    )

    content.addView(
        adBanner,
        LinearLayout.LayoutParams(-1, dp(132)).apply {
            topMargin = dp(4)
            bottomMargin = dp(12)
        }
    )

    content.addView(
        text(
            "نسخة تجريبية أولية • المعلومات والخدمات تحتاج إلى التحقق",
            12f,
            Color.DKGRAY
        ).apply {
            setPadding(0, dp(8), 0, 0)
        }
  );
search.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?, start: Int,
            count: Int, after: Int
        ) {}

        override fun onTextChanged(
            s: CharSequence?, start: Int,
            before: Int, count: Int
        ) {
            val q = s?.toString()?.trim().orEmpty()
            if (q.isNotEmpty()) {
                val match = categories.firstOrNull {
                    it.second.contains(q, true)
                }
                // تبقى جميع الخدمات متاحة في الشبكة.
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
            "الصيدليات" -> showPharmacies()
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
    private fun showPharmacies() {
    base("الصيدليات المناوبة", "معلومات المناوبات في دوما")

    content.addView(
        button("رجوع للرئيسية") { showHome() },
        margin()
    )

    val loading = text("جارٍ تحميل الصيدليات...", 16f).apply {
        gravity = Gravity.CENTER
    }
    content.addView(loading, margin())

    FirebaseFirestore.getInstance()
        .collection("pharmacies")
        .whereEqualTo("active", true)
        .get()
        .addOnSuccessListener { snapshot ->
            content.removeView(loading)

            if (snapshot.isEmpty) {
                content.addView(
                    text("لا توجد صيدليات مناوبة مضافة حالياً.", 16f),
                    margin()
                )
            } else {
                snapshot.documents.forEach { doc ->
                    val name = doc.getString("name")
                        ?.takeIf { it.isNotBlank() }
                        ?: "صيدلية بدون اسم"

                    val address = doc.getString("address")
                        ?.takeIf { it.isNotBlank() }
                        ?: "العنوان غير متوفر"

                    val phone = doc.getString("phone")
                        ?.trim()
                        .orEmpty()

                    val dutyHours = doc.getString("dutyHours")
                        ?.takeIf { it.isNotBlank() }
                        .orEmpty()

                    val card = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutDirection = View.LAYOUT_DIRECTION_RTL
                        setPadding(dp(16), dp(14), dp(16), dp(14))
                        background = rounded(Color.WHITE, dp(16))
                    }

                    card.addView(
                        text(name, 19f, burgundy, true)
                    )

                    card.addView(
                        text("📍 $address", 15f),
                        margin()
                    )

                    if (dutyHours.isNotBlank()) {
                        card.addView(
                            text("🕒 المناوبة: $dutyHours", 15f),
                            margin()
                        )
                    }

                    if (phone.isNotBlank()) {
                        card.addView(
                            button("اتصال بالصيدلية") {
                                val dialIntent = Intent(
                                    Intent.ACTION_DIAL,
                                    Uri.fromParts("tel", phone, null)
                                )
                                startActivity(dialIntent)
                            },
                            margin()
                        )
                    }

                    content.addView(
                        card,
                        LinearLayout.LayoutParams(-1, -2).apply {
                            topMargin = dp(7)
                            bottomMargin = dp(7)
                        }
                    )
                }
            }
        }
        .addOnFailureListener { error ->
            content.removeView(loading)

            content.addView(
                text(
                    "تعذر تحميل الصيدليات: ${error.localizedMessage ?: "تحقق من اتصال الإنترنت وإعدادات Firebase."}",
                    16f,
                    burgundy
                ),
                margin()
            )
        }
}

     private fun complaintForm() 
    {   content.addView(text("إرسال شكوى للمجلس", 20f, burgundy, true), margin())
        content.addView(text("نوع الشكوى", 15f, ink, true))
        val type = Spinner(this)
        val options = listOf("طرق وحفر", "نظافة ونفايات", "إنارة", "مياه وصرف صحي", "شكوى أخرى", "مقترح")
        type.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        content.addView(type, margin())
        val details = EditText(this).apply {
            hint = "اشرح المشكلة ومكانها..."; minLines = 4; gravity = Gravity.TOP or Gravity.RIGHT
            setPadding(dp(12), dp(12), dp(12), dp(12)); background = rounded(Color.WHITE, dp(12))
            textDirection = View.TEXT_DIRECTION_RTL
        }
        content.addView(details, margin())
        content.addView(button("إرفاق صورة") {
           val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
    setType("image/*")
}
            startActivityForResult(intent, 301)
        }, margin())
        content.addView(button("تحديد الموقع على الخريطة") {
            openUrl("https://www.google.com/maps/search/?api=1&query=Douma")
        }, margin())
        content.addView(button("تجهيز رسالة الشكوى للمشاركة") {
            val body = "شكوى من تطبيق على شو عم تدور بدوما؟\\nالنوع: ${type.selectedItem}\\nالتفاصيل: ${details.text}\\nيرجى تحديد جهة الاستلام الرسمية قبل الإرسال."
            val send = Intent(Intent.ACTION_SEND).apply {
    setType("text/plain")
    putExtra(Intent.EXTRA_TEXT, body)
}
            startActivity(Intent.createChooser(send, "مشاركة الشكوى"))
        }, margin())
        val disclaimer = text("ملاحظة: هذه النسخة لا ترسل الشكوى تلقائياً إلى المجلس ولا تحفظها في قاعدة بيانات. يلزم تحديد قناة رسمية وربطها قبل الإطلاق.", 13f, Color.DKGRAY)
        disclaimer.setPadding(0, dp(12), 0, 0); content.addView(disclaimer)
    }
    private fun openUrl(url: String) {
        try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
        catch (_: Exception) { Toast.makeText(this, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show() }
    }
}
