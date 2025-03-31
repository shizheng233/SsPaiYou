package com.shihcheeng.sspaiyou.activities.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import coil.load
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleData
import com.shihcheeng.sspaiyou.databinding.LayoutDetailBinding
import com.shihcheeng.sspaiyou.utils.ImageResolveConverter
import com.shihcheeng.sspaiyou.utils.NetStateListener
import com.shihcheeng.sspaiyou.utils.collectWithState
import com.shihcheeng.sspaiyou.utils.imageUrlFor
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), NetStateListener<ArticleData>,
    OnApplyWindowInsetsListener {

    private lateinit var binding: LayoutDetailBinding

    private val viewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.data.collectWithState(this, this)
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolBar.setNavigationOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, this)
    }

    override fun success(content: ArticleData) {
        val options: MutableDataSet = MutableDataSet()
            .set(Parser.EXTENSIONS, listOf(ImageResolveConverter))
        val wonText = FlexmarkHtmlConverter.builder(options)
            .build().convert(content.body)
        val markwon = Markwon.builder(this)
            .usePlugin(DetailMarkwonThemePlugin(this))
            .usePlugin(CoilImagesPlugin.create(this))
            .usePlugin(HtmlPlugin.create())
            .build()
        binding.error.root.visibility = View.GONE
        binding.progressHorizontal.hide()
        binding.collapsingToolbarLayout.title = content.title
        binding.toolBar.title = content.title
        binding.avatarImage.load(imageUrlFor(content.author.avatar))
        binding.username.text = content.author.nickname
        markwon.setMarkdown(binding.content, wonText)
    }

    override fun loading() {
        binding.progressHorizontal.show()
        binding.error.root.visibility = View.GONE
    }

    override fun error(throwable: Throwable) {
        binding.progressHorizontal.hide()
        binding.error.root.visibility = View.VISIBLE
        binding.error.errorMessage.text = throwable.localizedMessage
        binding.error.buttonRetry.setOnClickListener {
            viewModel.load()
        }
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        val insetsSystem = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        binding.appBar.updatePadding(
            top = insetsSystem.top
        )
        return WindowInsetsCompat.CONSUMED
    }


    companion object {

        fun ComponentActivity.toDetail(id: String) {
            val tent = Intent(this, DetailActivity::class.java)
                .apply {
                    putExtra(INTENT_ID_STRING, id)
                }
            startActivity(tent)
        }


        const val INTENT_ID_STRING = "INTENT_ID_STRING"

    }

}