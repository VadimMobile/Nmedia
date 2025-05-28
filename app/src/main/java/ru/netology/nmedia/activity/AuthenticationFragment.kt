package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.NonCancellable.message
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentAuthenticationBinding
import ru.netology.nmedia.viewmodel.LoginViewModel


class AuthenticationFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    val progressBar: ProgressBar = findViewById(R.id.progress_bar)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAuthenticationBinding.inflate(
            inflater,
            container,
            false
        )

        binding.signIn.setOnClickListener {
            viewModel.login(
                login = requireNotNull(binding.login.editText).text?.toString().orEmpty(),
                password = requireNotNull(binding.password.editText).text?.toString().orEmpty(),
            )
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message ?: return@observe
            Snackbar
                .make(binding.root, message, Snackbar.LENGTH_LONG)
                .show()
        }

        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigateUp()
            }
        }
            return binding.root
    }
}