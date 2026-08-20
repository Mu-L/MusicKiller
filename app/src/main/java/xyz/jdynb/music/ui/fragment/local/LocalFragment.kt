package xyz.jdynb.music.ui.fragment.local

import androidx.core.os.bundleOf
import xyz.jdynb.music.R
import xyz.jdynb.music.base.BaseMusicVpFragment
import xyz.jdynb.music.databinding.FragmentLocalBinding
import xyz.jdynb.music.model.PageModel
import xyz.jdynb.music.utils.DownloadHelper

class LocalFragment :
  BaseMusicVpFragment<FragmentLocalBinding>(R.layout.fragment_local) {
  override fun getViewPager() = binding.vp

  override fun getPages() = listOf(
    /*PageModel(
      title = "本地歌曲",
      fragment = LocalMusicFragment::class
    ),*/
    PageModel(
      title = "全部下载",
      fragment = DownloadListFragment::class,
      bundleOf("type" to DownloadListFragment.TYPE_ALL)
    ),
    PageModel(
      title = "已下载",
      fragment = DownloadedFragment::class,
    )
  )

  override fun initView() {
    super.initView()

    binding.tvPath.text = "文件保存在: " + DownloadHelper.getDownloadDirectory(requireContext()).path

    /*binding.vp.post {
      binding.vp.currentItem = 1
    }*/
  }

  override fun initData() {
  }
}