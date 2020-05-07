package com.strawberryswing.upmusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.requestModel.EmailRequest;
import com.strawberryswing.upmusic.databinding.ActivityLoginBinding;
import com.strawberryswing.upmusic.model.APIResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "TEST";
    private static final int RC_SIGN_IN = 9001;

    EditText etEmail;
    EditText etPassword;
    Button emailSignInButton, emailCreateAccountButton,
            emailSignOutButton, emailVerifyButton;

    com.facebook.login.widget.LoginButton facebookLoginButton;
    Button facebookSignOutButton;

    com.google.android.gms.common.SignInButton googleSignInButton;
    Button googleSignOutButton;//field_google_sign_out_and_disconnect

    private static OAuthLogin naverLoginInstance;
    OAuthLoginButton naverLoginButton;
    LoginButton kakaoLoginButton;
    Button kakaoLogoutButton;


    private static Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    String pmNaverId = "";
    String pmNaverNick = "";
    String pmNaverProfileImage = "";
    String pmNaverEmail = "";

    Button facebookCustomButton;
    Button googleCustomButton;
    Button kakaoCustomButton;
    Button naverCustomButton;

    Toolbar toolbar;
    String authResult;

    /**
     * [NAVER]
     * client 정보를 넣어준다.
     */
    private static String NAVER_OAUTH_CLIENT_ID = "NU3wfj54p_eVWz6ZnPBi";
    private static String NAVER_OAUTH_CLIENT_SECRET = "RtcRhgluB7";
    private static String NAVER_OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";


    ActivityLoginBinding binding; // xml's underbar will capital letter.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        mContext = this;
        mCallbackManager = CallbackManager.Factory.create();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);// setContentView

        connectWithViews();

        handleGoogleLogin();
        handleFacebookLogin();
        handleKakaoLogin();
        handleNaverLogin();
        handleWithOtherListeners();
    }

    private void handleFacebookLogin() {

        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
    }

    private void handleGoogleLogin() {
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
                .requestIdToken("190704323943-u81kgkq2e87mtss1d09vsc1vfqm9qbe8.apps.googleusercontent.com") // IF RED, RE-IMPORT FROM FIREBASE
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void handleKakaoLogin() {
        Session.getCurrentSession().addCallback(new SessionCallback());
    }

    private void connectWithViews() {

        //for crate home button

        etEmail = findViewById(R.id.field_email);
        etPassword = findViewById(R.id.field_password);

        emailSignInButton = findViewById(R.id.field_email_sign_in_button);
        emailCreateAccountButton = (Button) findViewById(R.id.field_email_create_account_button);
        emailSignOutButton = (Button) findViewById(R.id.field_sign_out_button);
        emailVerifyButton = (Button) findViewById(R.id.field_verify_email_button);

        facebookLoginButton = (com.facebook.login.widget.LoginButton) findViewById(R.id.field_button_facebook_login);
        facebookSignOutButton = (Button) findViewById(R.id.field_button_facebook_signout);

        googleSignInButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.field_google_sign_in_button);
        googleSignOutButton = (Button) findViewById(R.id.field_google_disconnect_button);

        kakaoLoginButton = (LoginButton) findViewById(R.id.field_kakao_login_button);
        kakaoLogoutButton = (Button) findViewById(R.id.field_kakao_logout_button);

        naverLoginButton = (OAuthLoginButton) findViewById(R.id.field_naver_login_button);
//        naverLoginButton.setOAuthLoginHandler(naverLoginHandler);

        facebookCustomButton = (Button) findViewById(R.id.button_facebook_login);
        googleCustomButton = (Button) findViewById(R.id.google_sign_in_button);
        kakaoCustomButton = (Button) findViewById(R.id.kakao_login_button);
        naverCustomButton = (Button) findViewById(R.id.naver_login_button);
    }

    public void handleWithOtherListeners() {
        emailSignInButton.setOnClickListener(this);
        emailCreateAccountButton.setOnClickListener(this);
        emailSignOutButton.setOnClickListener(this);
        emailCreateAccountButton.setOnClickListener(this);
        facebookLoginButton.setOnClickListener(this);
        facebookSignOutButton.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);
        googleSignOutButton.setOnClickListener(this);
        kakaoLoginButton.setOnClickListener(this);
        kakaoLogoutButton.setOnClickListener(this);
        naverLoginButton.setOnClickListener(this);

        facebookCustomButton.setOnClickListener(this);
        googleCustomButton.setOnClickListener(this);
        kakaoCustomButton.setOnClickListener(this);
        naverCustomButton.setOnClickListener(this);

    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.field_verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.field_verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });

        updateUI(null);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
//            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
//                    user.getEmail(), user.isEmailVerified()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

            findViewById(R.id.field_button_facebook_login).setVisibility(View.GONE);
//            findViewById(R.id.field_button_facebook_signout).setVisibility(View.VISIBLE);

//            findViewById(R.id.field_google_sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.field_google_disconnect_button).setVisibility(View.VISIBLE);

//            findViewById(R.id.field_kakao_login_button).setVisibility(View.GONE);
//            findViewById(R.id.field_kakao_logout_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.field_naver_login_button).setVisibility(View.GONE);

            findViewById(R.id.field_verify_email_button).setEnabled(!user.isEmailVerified());

        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);

//            findViewById(R.id.field_button_facebook_login).setVisibility(View.VISIBLE);
//            findViewById(R.id.field_button_facebook_signout).setVisibility(View.GONE);

//            findViewById(R.id.field_google_sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.field_google_disconnect_button).setVisibility(View.GONE);

//            findViewById(R.id.field_kakao_login_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.field_kakao_logout_button).setVisibility(View.GONE);

//            findViewById(R.id.field_naver_login_button).setVisibility(View.VISIBLE);
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
//                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }

    }
    // [END on_activity_result]

    // [START signin]
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void revokeAccessGoogle() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void handleNaverLogin() {
        naverLoginInstance = OAuthLogin.getInstance();
        naverLoginInstance.showDevelopersLog(true);
        naverLoginInstance.init(mContext, NAVER_OAUTH_CLIENT_ID, NAVER_OAUTH_CLIENT_SECRET, NAVER_OAUTH_CLIENT_NAME);

        naverLoginButton.setOAuthLoginHandler(naverLoginHandler);

        /*
         * 2015년 8월 이전에 등록하고 앱 정보 갱신을 안한 경우 기존에 설정해준 callback intent url 을 넣어줘야 로그인하는데 문제가 안생긴다.
         * 2015년 8월 이후에 등록했거나 그 뒤에 앱 정보 갱신을 하면서 package name 을 넣어준 경우 callback intent url 을 생략해도 된다.
         */
        //mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_callback_intent_url);
    }

    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = naverLoginInstance.logoutAndDeleteToken(mContext);

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                Log.d(TAG, "errorCode:" + naverLoginInstance.getLastErrorCode(mContext));
                Log.d(TAG, "errorDesc:" + naverLoginInstance.getLastErrorDesc(mContext));
            }

            return null;
        }

        protected void onPostExecute(Void v) {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            updateUI(currentUser);
            updateUI(null);
        }
    }

    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
//            mApiResul
// tText.setText((String) "");
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = naverLoginInstance.getAccessToken(mContext);
            return naverLoginInstance.requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
//            mApiResultText.setText((String) content);
            Log.e("TOKEN!!", "" + (String) content);
        }
    }

    private class RequestApiTaskWithFirebase extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = naverLoginInstance.getAccessToken(mContext);
            return naverLoginInstance.requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
//            mApiResultText.setText((String) content);

            Log.e(TAG, "[with] : " + content);
            JSONObject item = null;
            try {
                item = new JSONObject(content);
                JSONObject profile = new JSONObject(item.getString("response"));

                pmNaverEmail = profile.getString("email");
                pmNaverId = profile.getString("id");
                pmNaverNick = profile.getString("nickname");
                pmNaverProfileImage = profile.getString("profile_image");


            } catch (JSONException e) {
                e.printStackTrace();
            }


            getFirebaseJwt(naverLoginInstance.getAccessToken(mContext),"naver").continueWithTask(new Continuation<String, Task<AuthResult>>() {
                    @Override
                    public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {
                        String firebaseToken = task.getResult();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        return auth.signInWithCustomToken(firebaseToken);
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to create a Firebase user.", Toast.LENGTH_LONG).show();
                            if (task.getException() != null) {
                                Log.e(TAG, task.getException().toString());
                            }
                        }
                    }
                });

        }
    }

    private class RefreshTokenTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return naverLoginInstance.refreshAccessToken(mContext);
        }

        protected void onPostExecute(String res) {
//            updateNaverView();
            Log.e("REFRESSh", "" + res);
        }
    }


    /**
     * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    private OAuthLoginHandler naverLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = naverLoginInstance.getAccessToken(mContext);
                String refreshToken = naverLoginInstance.getRefreshToken(mContext);
                long expiresAt = naverLoginInstance.getExpiresAt(mContext);
                String tokenType = naverLoginInstance.getTokenType(mContext);


                Log.e(TAG, "accessToken : " + accessToken);
                Log.e(TAG, "refreshToken : " + refreshToken);
                Log.e(TAG, "expiresAt : " + expiresAt);
                Log.e(TAG, "tokenType : " + tokenType);



            } else {
                String errorCode = naverLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = naverLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

    };


    /**
     *[KAKAO & NAVER]
     * @param accessToken Access token retrieved after successful Kakao Login
     * @return Task object that will call validation server and retrieve firebase token
     */
    private Task<String> getFirebaseJwt(final String accessToken, String provider) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();


        String url = getResources().getString(R.string.validation_server_domain) + "/verifyToken";
        Log.e(TAG, "getFirebaseJwt : accessToken : " + accessToken + " [provider] : " + provider);

        if (provider.equals("kakao")) {

            url = getResources().getString(R.string.validation_server_domain) + "/verifyToken";
        } else {
            // NAVER ("naver")
            url = getResources().getString(R.string.validation_server_domain) + "/verifyTokenAsNaver";
        }



        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", accessToken);
        if (provider.equals("naver")) {

            //userId, email, displayName, photoURL
            validationObject.put("id", pmNaverId);
            validationObject.put("email", pmNaverEmail);
            validationObject.put("displayName", pmNaverNick);
            validationObject.put("photoURL", pmNaverProfileImage);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AuthTokenAPI.BASE_URL) // url
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthTokenAPI api =  retrofit.create(AuthTokenAPI.class);

        AuthToken aToken = new AuthToken(accessToken);


        if (provider.equals("naver")) {
            aToken = new AuthToken(accessToken, pmNaverId, pmNaverEmail, pmNaverNick, pmNaverProfileImage);
        }

        Call<String> call = api.getVerifyToken(aToken);

        if (provider.equals("kakao")) {
            call = api.getVerifyToken(aToken);
        } else {
            // NAVER ("naver")
            call = api.getVerifyTokenAsNaver(aToken);
        }


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

//                    System.out.println("[OUT] : " + response.body());
                    JSONObject jsonObj = new JSONObject(response.body());
                    String firebaseToken = jsonObj.getString("firebase_token");
                    source.setResult(firebaseToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });



//        call.enqueue(new Callback<JSONObject>() {
//
//            @Override
//            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
//
//                String firebaseToken = null;
//                try {
//                    firebaseToken = response.body().getString("firebase_token");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                source.setResult(firebaseToken);
//            }
//
//            @Override
//            public void onFailure(Call<JSONObject> call, Throwable t) {
//                Log.e(TAG, t.getCause().toString());
////                source.setException();
//            }
//        });


        return source.getTask();
    }

    /**
     * Session callback class for Kakao Login. OnSessionOpened() is called after successful login.
     */
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Toast.makeText(getApplicationContext(), "Successfully logged in to Kakao. Now creating or updating a Firebase User.", Toast.LENGTH_LONG).show();
            String accessToken = Session.getCurrentSession().getAccessToken();
            getFirebaseJwt(accessToken,"kakao").continueWithTask(new Continuation<String, Task<AuthResult>>() {
                @Override
                public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {
                    String firebaseToken = task.getResult();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    return auth.signInWithCustomToken(firebaseToken);
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        updateUI(currentUser);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to create a Firebase user.", Toast.LENGTH_LONG).show();
                        if (task.getException() != null) {
                            Log.e(TAG, task.getException().toString());
                        }
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Log.e(TAG, exception.toString());
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//        updateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.field_email_create_account_button) {
            createAccount(etEmail.getText().toString(), etPassword.getText().toString());
        } else if (i == R.id.field_email_sign_in_button) {
            Log.e("[CLICKED]", " : " + etEmail.getText().toString());
            signIn(etEmail.getText().toString(), etPassword.getText().toString());

//            reqEmailLogin();

        } else if (i == R.id.field_sign_out_button) {
            signOut();
        } else if (i == R.id.field_verify_email_button) {
            sendEmailVerification();
        } else if (i == R.id.field_button_facebook_signout) {
            signOut();
        } else if (i == R.id.field_google_sign_in_button) {
            signInGoogle();
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.field_google_disconnect_button) {
            revokeAccessGoogle();
        } else if (i == R.id.field_kakao_login_button) {

        } else if (i == R.id.field_kakao_logout_button) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    FirebaseAuth.getInstance().signOut();

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUI(null);
                        }
                    });
                }
            });
        } else if (i == R.id.field_naver_login_button) {
            naverLoginInstance.startOauthLoginActivity(LoginActivity.this, naverLoginHandler);
            new RequestApiTaskWithFirebase().execute();
//            new RequestApiTask().execute();
//            new DeleteTokenTask().execute();
//            new RefreshTokenTask().execute();
        } else if (i == R.id.button_facebook_login) {
            facebookLoginButton.performClick();
        } else if (i == R.id.google_sign_in_button) {
//            googleSignInButton.performClick();
            signInGoogle();
        } else if (i == R.id.kakao_login_button) {
            kakaoLoginButton.performClick();
        } else if (i == R.id.naver_login_button) {
            naverLoginButton.performClick();
        }
    }



    private void reqEmailLogin() {

        Log.e(TAG, " [reqEmailLogin] : " +  etEmail.getText().toString() + ", " + etPassword.getText().toString() );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UpmuicAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

//        HashMap<String, String> validationObject = new HashMap<>();
//        validationObject.put("email", etEmail.getText().toString());
//        validationObject.put("password", etPassword.getText().toString());

        EmailRequest emailRequest = new EmailRequest(etEmail.getText().toString(), etPassword.getText().toString());
        Call<APIResponse> call = api.authEmailLogin(emailRequest);


    }

}
