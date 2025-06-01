package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        private const val MAX_PHOTO_SIZE_PX = 2048
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg
            ?.let(binding.edit::setText)

        requireActivity().addMenuProvider(
            object : MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.new_post, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    if (menuItem.itemId == R.id.newPost){
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }else{
                        false
                    }
            },
            viewLifecycleOwner,
        )

        val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == ImagePicker.RESULT_ERROR){
                Toast.makeText(requireContext(),
                     R.string.photo_pick_error, Toast.LENGTH_SHORT)
                    .show()
                return@registerForActivityResult
            }

           val result = it.data?.data ?: return@registerForActivityResult
            viewModel.changePhoto(result, result.toFile())

        }

        viewModel.photo.observe(viewLifecycleOwner) {photo ->
            if (photo == null){
                binding.previewContainer.isGone = true
                return@observe
            }

            binding.previewContainer.isVisible = true
            binding.preview.setImageURI(photo.uri)
        }

        binding.remove.setOnClickListener{
            viewModel.removePhoto()
        }

        binding.takePhoto.setOnClickListener{
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(MAX_PHOTO_SIZE_PX, MAX_PHOTO_SIZE_PX)
                .createIntent(photoLauncher::launch)

        }

        binding.pickPhoto.setOnClickListener{
            ImagePicker.Builder(this)
            .crop()
            .galleryOnly()
            .maxResultSize(MAX_PHOTO_SIZE_PX, MAX_PHOTO_SIZE_PX)
            .createIntent(photoLauncher::launch)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        return binding.root
    }
}