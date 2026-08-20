package xyz.jdynb.music.ui.fragment.local

import android.provider.MediaStore
import android.util.Log
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.utils.scope
import com.drake.net.utils.withIO
import xyz.jdynb.music.R
import xyz.jdynb.music.base.BaseMusicNavFragment
import xyz.jdynb.music.databinding.FragmentMusicLocalBinding
import xyz.jdynb.music.model.download.DownloadModel

class LocalMusicFragment: BaseMusicNavFragment<FragmentMusicLocalBinding>(R.layout.fragment_music_local) {

  override fun initView() {
    binding.page.onRefresh {
      scope {
        val data = withIO {
          loadLocal()
        }
        addData(data)
      }
    }.setEnableLoadMore(false)

    binding.rvDownload.linear().setup {
      addType<DownloadModel>(R.layout.item_list_download)

      R.id.btn_pause_resume.onClick {
        val model = getModel<DownloadModel>()
        when (model.status) {
          DownloadModel.STATUS_DOWNLOADING, DownloadModel.STATUS_PENDING -> {
            downloadService?.pauseDownload(model.musicId)
          }

          DownloadModel.STATUS_PAUSED -> {
            downloadService?.resumeDownload(model.musicId)
          }
        }
      }

      R.id.btn_cancel.onClick {
        val model = getModel<DownloadModel>()
        downloadService?.cancelDownload(model.musicId)
        mutable.removeAt(modelPosition)
        notifyItemRemoved(modelPosition)
      }

      R.id.btn_retry.onClick {
        val model = getModel<DownloadModel>()
        downloadService?.retryDownload(model.musicId)
      }

      R.id.btn_delete.onClick {
        val model = getModel<DownloadModel>()
        downloadService?.deleteDownload(model.musicId)
        mutable.removeAt(modelPosition)
        notifyItemRemoved(modelPosition)
      }

      R.id.btn_play.onClick {
        val model = getModel<DownloadModel>()
        addLocalPlay(downloadModel = model)
      }
    }
  }

  override fun onFirstResume() {
    super.onFirstResume()
    binding.page.showLoading()
  }

  override fun onResume() {
    super.onResume()
    if (!isFirstResume) {
      binding.page.refresh()
    }
  }

  override fun initData() {
  }

  private fun loadLocal(): List<DownloadModel> {
    try {
      val cursor = requireContext().contentResolver
        .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null)

      val list = mutableListOf<DownloadModel>()
      cursor?.use {
        val displayName = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
        val path = it.getString(it.getColumnIndexOrThrow("_data"))
        // val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
        val createTime = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
        val downloadModel = DownloadModel(
          name = displayName,
          localPath = "",
          artist = "",
          createAt = 0
        )
        list.add(downloadModel)
      }
      Log.i("LocalMusicFragment", "list: $list")
      return list
    } catch (e: Exception) {
      Log.e("LocalMusicFragment", e.toString())
      return emptyList()
    }
  }
}