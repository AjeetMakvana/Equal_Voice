package com.xlentdevs.spectrum.ui.authentication.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.GoogleAuthProvider
import com.xlentdevs.spectrum.R
import com.xlentdevs.spectrum.databinding.FragmentSignInBinding
import com.xlentdevs.spectrum.ui.authentication.MainActivity
import com.xlentdevs.spectrum.ui.dashboard.DashBoardActivity
import com.xlentdevs.spectrum.utils.showSnackBar

class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels {
        SignInViewModelFactory(
            requireNotNull(this.activity).application
        )
    }

    private lateinit var binding: FragmentSignInBinding
    private lateinit var googleLoginClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.fragment = this

        initGoogleSignInClient()
        setObservers()

        return binding.root
    }

    private fun setObservers() {
        viewModel.snackBarText.observe(viewLifecycleOwner, { text->
            view?.showSnackBar(text, R.id.containerMainActivity)
        })

        viewModel.dataLoading.observe(viewLifecycleOwner, { value->
            (activity as MainActivity).showGlobalProgressBar(value)
        })

        viewModel.isLoggedIn.observe(viewLifecycleOwner, {user->
            if (user != null){
                moveToDashboardScreen()
            }
        })
    }

    private fun moveToDashboardScreen() {
        val intent = Intent(context, DashBoardActivity::class.java)
        startActivity(intent)
        (activity as MainActivity).finish()
    }

    fun moveToSignUpPage(){
        findNavController().popBackStack(R.id.signUpFragment, false)
    }

    //-------------------------Google Login code starts---------------------------

    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleLoginClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    fun loginUsingGoogle() {
        val signInGoogleIntent = googleLoginClient.signInIntent
        resultLauncher.launch(signInGoogleIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    getGoogleAuthCredential(account.idToken!!)
                } catch (e: ApiException) {
                    viewModel.snackBarText.value = "Unknown Error, Try Again !"
                }
            }
        }

    private fun getGoogleAuthCredential(idToken: String) {
        val googleAuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        googleLoginClient.signOut().addOnCompleteListener(OnCompleteListener {
            viewModel.googleLogin(googleAuthCredential)
        })
    }

    //------------------------------Google Login code ends-----------------------------
}