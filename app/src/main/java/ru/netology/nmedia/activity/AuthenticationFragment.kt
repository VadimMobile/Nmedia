package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentAuthenticationBinding
import ru.netology.nmedia.viewmodel.LoginViewModel

class AuthenticationFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()

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
            binding.login
        }

        viewModel.success.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

            return binding.root
    }
}