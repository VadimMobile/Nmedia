package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentAuthenticationBinding
import ru.netology.nmedia.viewmodel.LoginViewModel

@AndroidEntryPoint
class AuthenticationFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

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

        viewModel.error.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Snackbar.make(binding.root, "Ошибка входа", Snackbar.LENGTH_LONG)
                    .show()
                viewModel.onErrorShown()
            }
        }

        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }
}