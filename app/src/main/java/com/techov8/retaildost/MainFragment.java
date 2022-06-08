package com.techov8.retaildost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainFragment extends Fragment {

    GoogleSignInClient mGoogleSignInClient;

    private String name,email;
    private static final int RC_SIGN_IN =9;

    private FrameLayout parentFrameLayout;
    private FirebaseAuth mAuth;
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_main, container, false);

        parentFrameLayout = requireActivity().findViewById(R.id.register_framelayout);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        Button googleSignInButton = view.findViewById(R.id.sign_in_with_google);
        Button signInWithEmail = view.findViewById(R.id.sign_in_with_email);
        TextView logInBtn = view.findViewById(R.id.log_in_btn);

        signInWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new Sign_up_fragment());
            }
        });
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new Sign_in_fragment());
            }
        });

        TextView term=view.findViewById(R.id.sign_in_term_btn);
        term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent=new Intent("android.intent.action.VIEW", Uri.parse("http://www.retaildost.in/privacy_policy/"));
                startActivity(viewIntent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        return view;
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction= requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    ///Google Sign In
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                name = account.getDisplayName();
                String personPhotoUrl = account.getPhotoUrl().toString();
                email = account.getEmail();

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
           saveUserData(user);
        } else {
            Toast.makeText(getContext(), "Please Sign in again!", Toast.LENGTH_SHORT).show();
        }
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

    private void saveUserData(FirebaseUser user) {

        String uid = user.getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("USERS").document(uid);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {

                if (!documentSnapshot.exists()) {
                    ///new User
                   Intent i = new Intent(getActivity(), UserIdActivity.class);
                    SharedPreferences prefs3 = getActivity().getSharedPreferences("FirebaseData", 0);
                    SharedPreferences.Editor editor3 = prefs3.edit();
                    editor3.putString("name_data",name);
                    editor3.putString("email_data",email);
                    editor3.apply();
                    startActivity(i);
                    getActivity().finish();
                }else{


                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String userId=task.getResult().getString("account_ID");
                                    FirebaseFirestore.getInstance().collection("USER_IDS").document(userId).update("is_logged_in", true)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    SharedPreferences prefs3 = getActivity().getSharedPreferences("LoginData", 0);
                                                    SharedPreferences.Editor editor3 = prefs3.edit();
                                                    editor3.putString("data", "Yes");
                                                    editor3.apply();

                                                   Intent i = new Intent(getActivity(), MainActivity.class);
                                                    startActivity(i);
                                                    getActivity().finish();
                                                }
                                            });

                                }
                            });

                }

            }
        });
    }

    ///Google Sign In
}