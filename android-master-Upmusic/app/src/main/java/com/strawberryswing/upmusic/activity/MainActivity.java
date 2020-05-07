package com.strawberryswing.upmusic.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.reflect.TypeToken;
import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.kakaolink.v4.KakaoLinkResponse;
import com.kakao.kakaolink.v4.KakaoLinkService;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.squareup.picasso.Picasso;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.activity.temp.AudioPlayerActivity;
import com.strawberryswing.upmusic.activity.video.VideoPlayerActivity;
import com.strawberryswing.upmusic.activity.video.VideoPlaylistAdapter;
import com.strawberryswing.upmusic.activity.video.VideoPlaylistInterface;
import com.strawberryswing.upmusic.requestModel.EmailRequest;
import com.strawberryswing.upmusic.requestModel.FacebookRequest;
import com.strawberryswing.upmusic.requestModel.GoogleRequest;
import com.strawberryswing.upmusic.requestModel.KakaoRequest;
import com.strawberryswing.upmusic.requestModel.NaverRequest;
import com.strawberryswing.upmusic.model.APIResponse;
import com.strawberryswing.upmusic.model.Collection;
import com.strawberryswing.upmusic.model.Member;
import com.strawberryswing.upmusic.util.Constants;
import com.strawberryswing.upmusic.model.MusicTrack;
import com.strawberryswing.upmusic.model.Video;
import com.strawberryswing.upmusic.util.image.BlurTransformation;
import com.strawberryswing.upmusic.util.jsinterface.ObservableWebView;
import com.strawberryswing.upmusic.util.jsinterface.WebHandler;
import com.strawberryswing.upmusic.util.jsinterface.WebTrackListener;
import com.strawberryswing.upmusic.util.swipecontrollerdemo.SwipeController;
import com.strawberryswing.upmusic.util.swipecontrollerdemo.SwipeControllerActions;
import com.strawberryswing.upmusic.util.swipemenulistview.SwipeMenuListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;
import static com.kakao.util.helper.Utility.getPackageInfo;
import static com.strawberryswing.upmusic.util.cookieUsage.AddCookiesInterceptor.PREF_COOKIES;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends BaseActivity
        implements
        AdapterView.OnItemClickListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        AudioPlaylistInterface, VideoPlaylistInterface
{

    /**
     * *******************************************
     * [TOP OF VARIABLE]
     * *******************************************
     */

    private static final String TAG = "TEST";
    private static final int RC_SIGN_IN = 9001;
    private static OAuthLogin naverLoginInstance;
    OAuthLoginButton naverLoginButton;
    LoginButton kakaoLoginButton;
    Button kakaoLogoutButton;

    // 기본 설정
    private static final String DEFAULT_URL = UpmuicAPI.BASE_URL; // "http://10.0.2.2:8080";
    private static final String WSS_URL = "wss://" + UpmuicAPI.SOCKET_URL;

    //    private static final String DEFAULT_URL = "https://www.dropzonejs.com/"; // TEST FOR UPLOAD FILE FROM ANDROID
    private static final String USER_AGENT = "UPMusicAndroid";
    private static final String HANDLER_NAME = "AndroidHandler";

    // 웹뷰로부터 전달받는 요청 종류
    private static final String REQUEST_LOGIN_DEFAULT = "REQUEST_LOGIN_DEFAULT";
    private static final String REQUEST_LOGIN_FACEBOOK = "REQUEST_LOGIN_FACEBOOK";
    private static final String REQUEST_LOGIN_GOOGLE = "REQUEST_LOGIN_GOOGLE";
    private static final String REQUEST_LOGIN_KAKAO = "REQUEST_LOGIN_KAKAO";
    private static final String REQUEST_LOGIN_NAVER = "REQUEST_LOGIN_NAVER";
    private static final String REQUEST_PASSWORD_CHANGE = "REQUEST_PASSWORD_CHANGE";
    private static final String REQUEST_PLAYLIST_UPDATE = "REQUEST_PLAYLIST_UPDATE";
    private static final String REQUEST_PLAY_VIDEO = "REQUEST_PLAY_VIDEO";
    private static final String REQUEST_PAUSE_VIDEO = "REQUEST_PAUSE_VIDEO";
    private static final String REQUEST_STOP_VIDEO = "REQUEST_STOP_VIDEO";
    private static final String REQUEST_URL_SHARE = "REQUEST_URL_SHARE";
    private static final String REQUEST_LOGOUT = "REQUEST_LOGOUT";
    private static final String REQUEST_PLAY_AUDIO_SHOW = "REQUEST_PLAY_AUDIO_SHOW";
    private static final String REQUEST_PLAY_AUDIO_HIDE = "REQUEST_PLAY_AUDIO_HIDE";

    // 트랙 구성 요소들
    public static final String KEY_TRACK_ADMIN_URL = "key_track_admin_url";
    public static final String KEY_TRACK_ARTIST_ID = "key_track_artist_id";
    public static final String KEY_TRACK_ARTIST_NICK = "key_track_artist_nick";
    public static final String KEY_TRACK_COVER_IMAGE_URL = "key_track_cover_image_url";
    public static final String KEY_TRACK_DURATION = "key_track_duration";
    public static final String KEY_TRACK_FILE_NAME_URL = "key_track_file_name_url";
    public static final String KEY_TRACK_ID = "key_track_id";
    public static final String KEY_TRACK_LIKED = "key_track_liked";
    public static final String KEY_TRACK_MUSIC_ALBUM_ID = "key_track_music_album_id";
    public static final String KEY_TRACK_REJECTED_REASON = "key_track_rejected_reason";
    public static final String KEY_TRACK_SUBJECT = "key_track_subject";
    public static final String KEY_TRACK_THEME_NAMES = "key_track_theme_names";
    public static final String KEY_TRACK_TITLE_TRACK = "key_track_title_track";
    public static final String KEY_TRACK_URL = "key_track_url";

    public static final String KEY_VIDEO_ADMIN_URL = "KEY_VIDEO_ADMIN_URL".toLowerCase();
    public static final String KEY_VIDEO_ARTIST_NICK = "KEY_VIDEO_ARTIST_NICK".toLowerCase();
    public static final String KEY_VIDEO_ARTIST_URL = "KEY_VIDEO_ARTIST_URL".toLowerCase();
    public static final String KEY_VIDEO_CREATED_AT = "KEY_VIDEO_CREATED_AT".toLowerCase();

    public static final String KEY_VIDEO_DESCRIPTION = "KEY_VIDEO_DESCRIPTION".toLowerCase();
    public static final String KEY_VIDEO_DURATION = "KEY_VIDEO_DURATION".toLowerCase();
    public static final String KEY_VIDEO_FILENAME = "KEY_VIDEO_FILENAME".toLowerCase();
    public static final String KEY_VIDEO_FILENAME_URL = "KEY_VIDEO_FILENAME_URL".toLowerCase();
    public static final String KEY_VIDEO_GENRE_NAME = "KEY_VIDEO_GENRE_NAME".toLowerCase();

    public static final String KEY_VIDEO_HEART_COUNT = "KEY_VIDEO_HEART_COUNT".toLowerCase();
    public static final String KEY_VIDEO_HIT_COUNT = "KEY_VIDEO_HIT_COUNT".toLowerCase();
    public static final String KEY_VIDEO_HOT_POINT = "KEY_VIDEO_HOT_POINT".toLowerCase();
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID".toLowerCase();
    public static final String KEY_VIDEO_LIKED = "KEY_VIDEO_LIKED".toLowerCase();
    public static final String KEY_VIDEO_SUBJECT = "KEY_VIDEO_SUBJECT".toLowerCase();
    public static final String KEY_VIDEO_THUMBNAIL = "KEY_VIDEO_THUMBNAIL".toLowerCase();
    public static final String KEY_VIDEO_THUMBNAIL_URL = "KEY_VIDEO_THUMBNAIL_URL".toLowerCase();
    public static final String KEY_VIDEO_TYPE_NAME = "KEY_VIDEO_TYPE_NAME".toLowerCase();
    public static final String KEY_VIDEO_UPDATED_AT = "KEY_VIDEO_UPDATED_AT".toLowerCase();
    public static final String KEY_VIDEO_URL = "KEY_VIDEO_URL".toLowerCase();
    public static final String KEY_VIDEO_VIDEO_TYPE = "KEY_VIDEO_VIDEO_TYPE".toLowerCase();

    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 1;
    boolean permissionCheck = false;

    private static String NAVER_OAUTH_CLIENT_ID = "NU3wfj54p_eVWz6ZnPBi";
    private static String NAVER_OAUTH_CLIENT_SECRET = "RtcRhgluB7";
    private static String NAVER_OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";

    public static final String WIFI_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";

    public static final String CONNECTION_CONFIRM_CLIENT_URL = UpmuicAPI.BASE_URL;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    DrawerLayout drawer;

    ListView playlistListView;
    SwipeMenuListView swipeMenuListView;

    SwipeController swipeController = null;

    FrameLayout mainFrameLayout;

    private ObservableWebView mWebView;
    private RelativeLayout webViewLayer;
    private ProgressBar mProgressBar;
    private WebHandler mWebHandler;

    List<MusicTrack> itemsList;
    List<MusicTrack> tempList;
    List<MusicTrack> defaultList;

    List<Collection> collectionList;
    List<String> collectionSubjectList;

    ArrayList<MusicTrack> itemsArrayList;
    ArrayList<MusicTrack> tempArrayList;
    List<MusicTrack> defaultArrayList;

    List<Video> itemsListForVideo;
    ArrayList<Video> itemsArrayListForVideo;

    BottomSheetBehavior bottomSheetBehavior;
    // Bottom
    RelativeLayout rlBottomSheet;
    RelativeLayout rlPeekedPlayer;
    RelativeLayout rlPeekedBottomMenu;
    LinearLayout PeekedBottomMenuGrid1;
    LinearLayout PeekedBottomMenuGrid2;

    RelativeLayout rlPeekedBottomMenu2;

    RelativeLayout rlPeekedBottomMenu2_Row0;
    TextView rlPeekedBottomMenu2_Row0_TextView1, rlPeekedBottomMenu2_Row4_TextView1;
    ImageView rlPeekedBottomMenu2_Row0_ImageView1, rlPeekedBottomMenu2_Row4_ImageView1;
    LinearLayout rlPeekedBottomMenu2_Row1, rlPeekedBottomMenu2_Row2, rlPeekedBottomMenu2_Row3
            , rlPeekedBottomMenu2_Row4, rlPeekedBottomMenu2_Row5;

    RelativeLayout rlPlayerBottomMenu;
    RelativeLayout rlPlayerBottomMenu_Row0;
    TextView rlPlayerBottomMenu_Row0_TextView1, rlPlayerBottomMenu_Row4_TextView1;
    ImageView rlPlayerBottomMenu_Row0_ImageView1, rlPlayerBottomMenu_Row4_ImageView1;
    LinearLayout rlPlayerBottomMenu_Row1, rlPlayerBottomMenu_Row2, rlPlayerBottomMenu_Row3,
            rlPlayerBottomMenu_Row4, rlPlayerBottomMenu_Row5;


    RelativeLayout rlPeekedBottomMenu2_Row0_ImageView1_Layout;
    RelativeLayout rlPlayerBottomMenu_Row0_ImageView1_Layout;


    LinearLayout lExpandedPlayer;
    // Player
    private TextView textPeek01, textPeek02, textExpanded01
            , textExpanded02, textExpanded03,textExpanded04,textExpanded05;
    private ImageView imageView01, imageView02, imageViewPeek;
    private Button btnPeekPrev, btnPeekPlay, btnPeekNext, btnPeekPlaylist;

    private Button btnPlay, btnBack, btnFor, btnToggle;
    private Button btnShuffle, btnRepeat, btnHeart, btnMore, btnPlaylist;
    private LinearLayout btnMoreLayout;

    private TextView btnTabDeleteText, btnTabDownText;
    private Button btnTabDeleteButton, btnTabDownButton;

    private LinearLayout layoutReward;
    private Button btnReward;
    private TextView txtReward;


    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;

    Handler updateHandler = new Handler();
    private int realtimeLength;
    private int mediaFileLength;

    private Runnable runnable;
    private Handler handler;
    private static String token = "+PDUvrnGQEPu6LrnJqdA2qd6rHVW7W1haGFsvmrraiI=";

    AudioPlaylistBaseAdapter playlistAdapter;
    VideoPlaylistAdapter playlistAdapterForVideo;

    private boolean isItemSelected = false;
    private boolean isItemDownloaded = false;
    private boolean isPlaylistEditMode = false;

    String tempName = "now.jpg";
    String filename = "upmusicsnowplaying.mp3";
    String objectName = "nowplaying.mp3";
    String objectId = "1";
    String albumId = "1";
    String previousSelectedId = "";

    Runnable updater;

    RelativeLayout rlListViewHeader, rlListViewHeader2;
    Button btnToggleListHeader;
    RelativeLayout btnEditHeader;
    ImageView btnEditHeaderImage;
    TextView btnEditHeaderText;


    ImageView imageViewListHeader;
    View listViewHeader;
    View listViewFooter;
    Intent intent;
    WifiManager.WifiLock wifiLock;

    ProgressBar progressBarPeek;
    AudioFocusHelper mAudioFocusHelper;

    String activityStatus = "";

    private boolean isClicked = false;
    private int lastPlayTrackNumber = 0;

    public static Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    com.facebook.login.widget.LoginButton facebookLoginButton;

    String pmNaverId = "";
    String pmNaverNick = "";
    String pmNaverProfileImage = "";
    String pmNaverEmail = "";

    String accessToken = "";
    String loginMethod = "";
    APIResponse apiResponse = null;

    //    ActivityNavigationDrawerBinding binding; // xml's underbar will capital letter.
    ViewDataBinding binding;

    private boolean kakaoCheck = false;
    private final int KAKAO_RESULT = 3;
    private boolean facebookCheck = false;

    private boolean isClearHistory = false;

    ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;

    private Socket socket;

    private StompClient mStompClient;
    //    private Disposable mRestPingDisposable;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());


    String playMode = ""; // Video, Music, (MusicAll MusicShuffle...)
    private JSONArray mArray;
    private int peekedHeight = 179;


    String localLoginUsername= "";
    String localLoginPassword = "";

    boolean peekedUiWorking = false;
    boolean isFirstLoad = true;


    AudioPlaylistRecyclerAdapter mAdapter;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;

    ListView customDialogListView;
    CustomDiaglogListViewAdapter customDiaglogListViewAdapter;
    Button customDialogButton;


    private View view;
    AlertDialog listViewDialog;
    AlertDialog.Builder listViewDialogBuilder;
    Long[] selectedIdsFromPlaylist;

    CustomCreateDialog customCreateDialog;
    CustomDeleteConfirmDialog customDeleteConfirmDialog;

    private long currentPlayingId = 0;
    private long collectSingleId = 0;

    private String collectionMode = "PLAYER"; // PLAYER, PLAYLIST, PLAYLIST_EDIT

    private static final String TYPE_IMAGE = "image/*";
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> filePathCallbackNormal;
    private ValueCallback<Uri[]> filePathCallbackLollipop;
    private Uri mCapturedImageURI;

    public static  Context context;

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    Timer playerTimer;
    Timer checkTimer;

    kakaoSessionCallback akakaoSessionCallback;
//    CallbackManager callbackManager;

    /**
     * *******************************************
     * [END OF VARIABLE]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF STH]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF LIFE-CYCLE]
     * *******************************************
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first



        setLocale();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
//            // Restore value of members from saved state
//            mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
//            mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);


        } else {
            // Probably initialize members with default values for a new instance
        }


        Log.e(TAG, "[keyHash] " + getKeyHash(this));
        Log.e(TAG, "::::::::::::::::::::::::TOP of onCreate()::::::::::::::::::::::::");
        Log.e(TAG, " >>>> [getPreferencesForIsPlayingOnDestroy]  : " + getPreferencesForIsPlayingOnDestroy());
        verifyStoragePermissions(this);

        if (getWhatKindOfNetwork(mCtx).equals(NONE_STATE)) {
//            alertMessage("네트워크 연결 상태를 확인 하세요.  프로그램 종료 후 다시 시도해 보세요.");
        } else {
            if (isOnline() == false) {
//                alertMessage("서버에 연결에 문제가 있습니다. 프로그램 종료 후 다시 시도해 보세요.");
            }
        }

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

        akakaoSessionCallback = new kakaoSessionCallback();

        mContext = this;
        // [BASIC] setContentView
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // WITHOUT BASEACT, WITH APPCOMPATACT
        setContentView(R.layout.content_navigation_drawer);
        binding = DataBindingUtil.setContentView(this, R.layout.content_navigation_drawer);// setContentView

        // [SET] WEBVIEW : ProgressBar, WebView....with Settings.
        handleAppVersionAndCookie();
        // [init] SESSION
        initSession();
        // [Check] SESSION
        checkSession();

        setProgressBar();
        setWebView();
        // [BASIC] findViewById
        setBasicViews();

        connectRecyclerView();

        if (getPreferencesForIsPlayingOnDestroy().equals("true")) {
            mediaPlayer = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer();
            ;
            // currently USE mAdapter
            List<MusicTrack> temp = readPlaylist();
            if (temp != null) {

                mAdapter.updateList(temp);
                mAdapter.notifyDataSetChanged();
            }


        }

        // [BASIC] Data Init (new, bottomview, .... ,setted s3 up)
        initializeData();
        //  registerBroadcast();

        Log.e(TAG, "::::::::::::::::::::::::MID of onCreate()::::::::::::::::::::::::");
        // [SET] LISTENERS
        handleFacebookLogin();
        handleGoogleLogin();
        handleKakaoLogin();
        handleNaverLogin();

        handleWebView();

        handleLocalSeekbar();
        handleMediaPlayerPlayOrPause();
        handleMediaPlayerForward();
        handleMediaPlayerBackward();

        handleShowPlaylist();
        handleEditPlaylist();

        handleRepeatMode();
        handleShuffleMode();
        handleMoreFunction();
        handleLikeTrack();
        handlePeekedPlayerClick();

        handleCustomDialog();

        context = this;

        // FileUriExposedException exposed beyond app through ClipData.Item.getUri()
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Log.e(TAG, "::::::::::::::::::::::::BTM of onCreate()::::::::::::::::::::::::");
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
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "::::::::::::::::::::::::TOP of onResume()::::::::::::::::::::::::");

//        if (isPlayerFirstLoad) {
//            Log.e(TAG, "::::::::::::::::::::::::isFirstLoad::::::::::::::::::::::::");
//            Intent mainActityCall = new Intent(this, MainActivity.class);
//            mainActityCall.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
//            );
//            startActivity(mainActityCall);
//
//        }
        if (GlobalApplication.getInstance().getServiceInterface().getMediaPlayer() == null) {

            Log.e(TAG, " > [onResume][0][getMediaPlayer()] NULL");
        } else {

            Log.e(TAG, " > [onResume][0][getMediaPlayer()] NOT NULL");
        }


        // TODO
        if (getPreferencesForIsPlayingOnDestroy().equals("true")) {
            Log.e(TAG, " > [onResume][1][getPreferencesForIsPlayingOnDestroy()] TRUE");

            if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
                // SETUP-PREVIOUS SETTING && LOAD-PLAYING STATUS
                // WILL TEST
                //
                Log.e(TAG, " > [onResume][1][getPreferencesForIsPlayingOnDestroy()] isPlaying TRUE");
                setPlayerTimerTask();
            } else {
                // SETUP-PREVIOUS SETTING
                // WILL TEST
                Log.e(TAG, " > [onResume][1][getPreferencesForIsPlayingOnDestroy()] isPlaying FALSE");
            }

        } else {
            Log.e(TAG, " > [onResume][2][getPreferencesForIsPlayingOnDestroy()] FALSE");
            // INIT SETTING
            // DO NOTHING
        }

        if (itemsArrayList == null ) {
            return;
        }

        if(itemsArrayList.size() == 0) {
            // FACEBOOK LOGIN 할 때,플레이리스트 세팅전에 돌아온다.=> 에러 / 플레이 세팅이 되지 않도록!
            return;
        }

        if(GlobalApplication.getInstance().getServiceInterface().getIsFirstPlayed() == false) {
            return;
        }

        if (!activityStatus.equals("notplayed")) {
            mediaPlayer = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer();
        }


        activityStatus = "resume";
        registerBroadcast();

        if (mStompClient != null) {
            mStompClient.connect();
            // connectStomp();
        }

        if (GlobalApplication.getInstance().getServiceInterface().getMediaPlayer() != null) {
            playerUpdateUI();
        }

//        if (!GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
//        updateAppSeekBar();
//        handleAppSeekbar();
//        }


//        Intent mainActityCall = new Intent(this, MainActivity.class);
//        mainActityCall.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                | Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP
//        );
//
//        startActivity(mainActityCall);

        Log.e(TAG, "::::::::::::::::::::::::BTM of onResume()::::::::::::::::::::::::");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "::::::::::::::::::::::::TOP of onDestroy()::::::::::::::::::::::::");

//        if (!getPreferencesForToken().equals("")) {
//            unregisterBroadcast();
//        }

        setPreferencesForDestroy("true");
        setPreferencesForIsPlayingOnDestroy("false");

        if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {

            setPreferencesForIsPlayingOnDestroy("true");

            //  최근 실행목록에서 제거 등의 경우나 갑자기 종료시에 음원을 없애는 기능
//            if (GlobalApplication.getInstance().getServiceInterface().getMediaPlayer() != null) {
//                GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().stop();
//                GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().release();
//            }
//            GlobalApplication.getInstance().getServiceInterface().removeNotificationPlayer();
        }

        try {
            if (sConfigCallback != null)
                sConfigCallback.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Log.e(TAG, "::::::::::::::::::::::::BTM of onDestroy()::::::::::::::::::::::::");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e(TAG, "::::::::::::::::::::::::TOP of onPause()::::::::::::::::::::::::");
        if (mediaPlayer == null) { // GlobalApplication.getInstance().getServiceInterface().getMediaPlayer()
            activityStatus = "notplayed";
        }
        activityStatus = "pause";

        if (!getPreferencesForToken().equals("")) {
            unregisterBroadcast();
        }

        Log.e(TAG, "::::::::::::::::::::::::BTM of onPause()::::::::::::::::::::::::");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.e(TAG, "::::::::::::::::::::::::TOP of onNewIntent()::::::::::::::::::::::::");
        Log.e(TAG, "::::::::::::::::::::::::END of onNewIntent()::::::::::::::::::::::::");

    }


    /**
     * *******************************************
     * [END OF LIFE-CYCLE]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF PERMISSION / NETWORK / PRE-LOAD-FUNCTIONS]
     * *******************************************
     */

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, ": [mBroadcastReceiver] onReceive");
//            playerUpdateUI();
        }
    };
    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PREPARED);
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void unregisterBroadcast() {
        //  mBroadcastReceiver.
        if(itemsArrayList == null) {
            return;
        }

        if(itemsArrayList.size() == 0) {
            return;
        }

        if (GlobalApplication.getInstance().getServiceInterface().getIsFirstPlayed() == false) {
            return;
        }

        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }


    private void alertMessage(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mCtx);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(message);
        alert.show();
    }

    private void toast(String text) {
        Log.i(TAG, text);
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

//        // EXAMPLE
//        savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
//        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

//        // Restore state members from saved instance
//        mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
//        mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
    }

    public void setLocale(String char_select) {

        Locale locale = new Locale(char_select);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

    public void setLocale() {

        Configuration config = new Configuration();
        config.locale = getBaseContext().getResources().getConfiguration().locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

    private static class CheckConnect extends Thread{
        private boolean success;
        private String host;

        public CheckConnect(String host){
            this.host = host;
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)new URL(host).openConnection();
                conn.setRequestProperty("User-Agent","Android");
                conn.setConnectTimeout(1000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if(responseCode == 204) success = true;
                else success = false;
            }
            catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            if(conn != null){
                conn.disconnect();
            }
        }

        public boolean isSuccess(){
            return success;
        }

    }

    private static class PingTask extends Thread {
        private boolean success;
        private String host;

        public PingTask(String host) {
            this.host = host;
        }

        @Override
        public void run() {
            super.run();
            try {
                Runtime runtime = Runtime.getRuntime();
                Process proc = runtime.exec("ping -c 1 -W 100" + host);
                proc.waitFor();
                int exitCode = proc.exitValue();
                if (exitCode == 0) {
                    success = true;
                } else {
                    success = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                success = false;
            }
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public static boolean isOnline() {
        CheckConnect cc = new CheckConnect(CONNECTION_CONFIRM_CLIENT_URL);
        cc.start();
        try{
            cc.join();
            return cc.isSuccess();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static String getWhatKindOfNetwork(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFI_STATE;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE;
            }
        }
        return NONE_STATE;
    }

    public void onRequestPermission() {
        int permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionReadStorage == PackageManager.PERMISSION_DENIED
                || permissionWriteStorage == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_EXTERNAL_STORAGE_CODE);
        } else {
            permissionCheck = true;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode ){
            case REQUEST_EXTERNAL_STORAGE_CODE:
                for (int i=0;i<permissions.length;i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if(permission.equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            permissionCheck = true;
                        } else {
                            permissionCheck = false;
                        }
                    }
                }
                break;
        }
    }


    /**
     * *******************************************
     * [END OF PERMISSION / NETWORK]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF VIEW (UI) / HANDLE-UI-AND-REF-DATA]
     * *******************************************
     */

    @SuppressLint("ResourceAsColor")
    private void setBasicViews() {

        mainFrameLayout = (FrameLayout) findViewById(R.id.main_frame);
        imageView01 = (ImageView) findViewById(R.id.imageView01);
        imageView02 = (ImageView) findViewById(R.id.imageView02);
        imageViewPeek = (ImageView) findViewById(R.id.imageview_peek);

        imageView01.setFocusable(false);
        imageView02.setFocusable(false);

        textPeek01 = (TextView) findViewById(R.id.txt_peek_01);
        textPeek02 = (TextView) findViewById(R.id.txt_peek_02);
        textExpanded01 = (TextView) findViewById(R.id.txt_expanded_01);
        textExpanded02 = (TextView) findViewById(R.id.txt_expanded_02);
        textExpanded03 = (TextView) findViewById(R.id.txt_expanded_03);
        textExpanded04 = (TextView) findViewById(R.id.txt_expanded_04);
        textExpanded05 = (TextView) findViewById(R.id.txt_expanded_05);
        btnPeekNext = (Button) findViewById(R.id.btn_peek_next);
        btnPeekPlay = (Button) findViewById(R.id.btn_peek_play);
        btnPeekPrev = (Button) findViewById(R.id.btn_peek_prev);
        btnPeekPlaylist = (Button) findViewById(R.id.btn_peek_playlist);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnBack = (Button) findViewById(R.id.btnBackward);
        btnFor = (Button) findViewById(R.id.btnForward);
        btnToggle = (Button) findViewById(R.id.btnToggle);
        btnRepeat = (Button) findViewById(R.id.btnRepeat);
        btnShuffle = (Button) findViewById(R.id.btnShuffle);
        btnHeart = (Button) findViewById(R.id.btnHeart);
        btnMore = (Button) findViewById(R.id.btnMore);
        btnMoreLayout = (LinearLayout) findViewById(R.id.btnMoreLayout);
        btnPlaylist = (Button) findViewById(R.id.btnPlaylist);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setFocusable(false);
        seekBar.setFocusableInTouchMode(false);
        seekBar.setBackground(null);

        ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
        thumb.getPaint().setColor(Color.WHITE);
        thumb.setIntrinsicHeight(15);
        thumb.setIntrinsicWidth(15);
        seekBar.setThumb(thumb);
//        seekBar = new SeekBar(mCtx, null, 0);

        progressBarPeek = (ProgressBar) findViewById(R.id.progressbar_peek);

        rlBottomSheet = (RelativeLayout) findViewById(R.id.rl_bottom_sheet);
        rlPeekedPlayer = (RelativeLayout) findViewById(R.id.rl_peeked_player);
        rlPeekedBottomMenu = (RelativeLayout) findViewById(R.id.rl_peeked_bottom_menu);
        PeekedBottomMenuGrid1 = (LinearLayout) findViewById(R.id.peeked_bottom_menu_grid_1);
        PeekedBottomMenuGrid2 = (LinearLayout) findViewById(R.id.peeked_bottom_menu_grid_2);

        rlPeekedBottomMenu2 = (RelativeLayout) findViewById(R.id.rl_peeked_bottom_menu2);
        rlPeekedBottomMenu2_Row0 = (RelativeLayout) findViewById(R.id.bottom_menu2_row0);
        rlPeekedBottomMenu2_Row1 = (LinearLayout) findViewById(R.id.bottom_menu2_row1);
        rlPeekedBottomMenu2_Row2 = (LinearLayout) findViewById(R.id.bottom_menu2_row2);
        rlPeekedBottomMenu2_Row3 = (LinearLayout) findViewById(R.id.bottom_menu2_row3);
        rlPeekedBottomMenu2_Row4 = (LinearLayout) findViewById(R.id.bottom_menu2_row4);
        rlPeekedBottomMenu2_Row5 = (LinearLayout) findViewById(R.id.bottom_menu2_row5);
        rlPeekedBottomMenu2_Row0_TextView1 = (TextView) findViewById(R.id.bottom_menu2_text1);
        rlPeekedBottomMenu2_Row0_ImageView1 = (ImageView) findViewById(R.id.bottom_menu2_close);

        rlPeekedBottomMenu2_Row0_ImageView1_Layout = (RelativeLayout) findViewById(R.id.bottom_menu2_close_layout);

        rlPeekedBottomMenu2_Row4_TextView1 = (TextView) findViewById(R.id.bottom_menu2_row4_text);
        rlPeekedBottomMenu2_Row4_ImageView1 = (ImageView) findViewById(R.id.bottom_menu2_row4_image);

        rlPlayerBottomMenu = (RelativeLayout) findViewById(R.id.rl_player_bottom_menu);
        rlPlayerBottomMenu_Row0 = (RelativeLayout) findViewById(R.id.player_bottom_menu_row0);
        rlPlayerBottomMenu_Row1 = (LinearLayout) findViewById(R.id.player_bottom_menu_row1);
        rlPlayerBottomMenu_Row2 = (LinearLayout) findViewById(R.id.player_bottom_menu_row2);
        rlPlayerBottomMenu_Row3 = (LinearLayout) findViewById(R.id.player_bottom_menu_row3);
        rlPlayerBottomMenu_Row4 = (LinearLayout) findViewById(R.id.player_bottom_menu_row4);
        rlPlayerBottomMenu_Row5 = (LinearLayout) findViewById(R.id.player_bottom_menu_row5);
        rlPlayerBottomMenu_Row0_TextView1 = (TextView) findViewById(R.id.player_bottom_menu_text1);
        rlPlayerBottomMenu_Row0_ImageView1 = (ImageView) findViewById(R.id.player_bottom_menu_close);

        rlPlayerBottomMenu_Row0_ImageView1_Layout = (RelativeLayout) findViewById(R.id.player_bottom_menu_close_layout);

        rlPlayerBottomMenu_Row4_TextView1 = (TextView) findViewById(R.id.player_bottom_menu_row4_text);
        rlPlayerBottomMenu_Row4_ImageView1 = (ImageView) findViewById(R.id.player_bottom_menu_row4_image);

        btnTabDeleteButton = (Button) findViewById(R.id.btn_tab_delete);
        btnTabDeleteText = (TextView) findViewById(R.id.btn_tab_delete_text);

        btnTabDownButton = (Button) findViewById(R.id.btn_tab_down);
        btnTabDownText = (TextView) findViewById(R.id.btn_tab_down_text);

        lExpandedPlayer = (LinearLayout) findViewById(R.id.l_expanded_player);

        rlListViewHeader = (RelativeLayout) findViewById(R.id.rl_listview_header);
        rlListViewHeader2 = (RelativeLayout) findViewById(R.id.rl_listview_header2);

        swipeMenuListView = (SwipeMenuListView) findViewById(R.id.swipe_menu_listview);
        swipeMenuListView.setBackgroundColor(Color.WHITE);

        playlistListView = (ListView) findViewById(R.id.listView_in_player);
        playlistListView.setBackgroundColor(Color.WHITE);

        btnToggleListHeader = (Button) findViewById(R.id.btn_list_header_toggle);
        btnEditHeader = (RelativeLayout) findViewById(R.id.list_header_edit_as_btn);
        btnEditHeaderImage = (ImageView) findViewById(R.id.list_header_edit_as_btn_image);
        btnEditHeaderText = (TextView) findViewById(R.id.list_header_edit_as_btn_text);

        imageViewListHeader = (ImageView) findViewById(R.id.imageview_list_header);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        layoutReward = (LinearLayout) findViewById(R.id.layoutReward);
        btnReward = (Button) findViewById(R.id.btnReward);
        txtReward = (TextView) findViewById(R.id.txtReward);


    }

    private void initializeData() {

        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        playlistListView.setCacheColorHint(Color.TRANSPARENT);
        isItemSelected = false;
        bottomSheetBehavior = BottomSheetBehavior.from(rlBottomSheet);
        peekedHeight = bottomSheetBehavior.getPeekHeight();

        bottomSheetBehavior.setHideable(false);

        // TODO
        tempArrayList = new ArrayList<>();
        if (!getPreferencesForIsPlayingOnDestroy().equals("true")) {

            itemsArrayList = new ArrayList<>();
            defaultArrayList = new ArrayList<>();
            mediaPlayer = new MediaPlayer();
            mediaPlayer = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer();
            handler = new Handler();
            requestCollectionsList();
        } else {
            itemsArrayList = GlobalApplication.getInstance().getServiceInterface().getAudioTracks();
            defaultArrayList = GlobalApplication.getInstance().getServiceInterface().getAudioTracks();



            mediaPlayer = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer();
            handler = new Handler();
        }


        final CoordinatorLayout.LayoutParams[] layoutParams = {new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT)};
        final int[] left = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin))};
        final int[] top = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_top_margin))};
        final int[] right = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin))};
//        final int[] bottom = {getPixelValue(mCtx, bottomSheetBehavior.getPeekHeight() - 20 )};
        final int[] bottom = { bottomSheetBehavior.getPeekHeight()};

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // 해당 작업을 처리함
                        bottomSheetBehavior.setState(STATE_COLLAPSED);

                        layoutParams[0] = new CoordinatorLayout.LayoutParams(
                                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                                CoordinatorLayout.LayoutParams.WRAP_CONTENT);
                        left[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin));
                        top[0] = getPixelValue(mCtx,(int)  getResources().getDimension(R.dimen.webview_top_margin));
                        right[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin));
//                                            bottom[0] = getPixelValue(mCtx, bottomSheetBehavior.getPeekHeight() - 20 );
                        bottom[0] = bottomSheetBehavior.getPeekHeight();


                        layoutParams[0].setMargins(left[0], top[0], right[0], bottom[0]);
                        mainFrameLayout.setLayoutParams(layoutParams[0]);

                    }
                });
            }
        }).start();

        Log.e(TAG, "[bottomSheetBehavior] peekedHeight : " + peekedHeight);
        setPreferencesForEditMode("false");

        loadDataEmptyWhenPlayStatus();

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                // STATE_COLLAPSED; // : height 만큼 보이게 됩니다.
                // STATE_EXPANDED // : 가득 차게 처리합니다.
                // STATE_HIDDEN //  : 숨김 처리됩니다. // 설정 확인 필요... 작동 x
                // PEEK_HEIGHT_AUTO
                // STATE_DRAGGING
                // STATE_SETTLING
                if (newState == STATE_HIDDEN) {
                    Log.e(TAG, ">>> [bottomSheetBehavior] STATE_HIDDEN");
                    return;
                } else {

                    bottomSheetBehavior.setHideable(false);

                    if(isItemSelected == false) {
                        lExpandedPlayer.setVisibility(View.GONE);
                        rlPeekedBottomMenu.setVisibility(View.GONE);
                    }

                    rlPeekedPlayer.setVisibility(View.GONE);
                    rlPeekedBottomMenu.setVisibility(View.GONE);
                    rlPeekedBottomMenu2.setVisibility(View.GONE);
                    rlPlayerBottomMenu.setVisibility(View.GONE);

                    mWebView.setClickable(true);
                    mWebView.setFocusable(true);
                    rlBottomSheet.setClickable(false);
                    btnToggle.setVisibility(View.GONE);
                    layoutReward.setVisibility(View.GONE);

                    lExpandedPlayer.setVisibility(View.VISIBLE);

                    if(newState == STATE_EXPANDED) {

                        Log.e(TAG, "[bottomSheetBehavior][0]");
                        imageViewPeek.setVisibility(View.GONE);
                        lExpandedPlayer.setVisibility(View.VISIBLE);
                        rlPeekedPlayer.setVisibility(View.GONE);
                        rlPeekedBottomMenu.setVisibility(View.GONE);

                        mWebView.setClickable(false);
                        mWebView.setFocusable(false);
                        rlBottomSheet.setClickable(true);
                        btnToggle.setVisibility(View.VISIBLE);
                        layoutReward.setVisibility(View.VISIBLE);

                        if (isPlaylistEditMode == true) {

                            toast(getString(R.string.toast_cancel_edit_playlist));
//                        Toast.makeText(mCtx, "플레이리스트 편집을 취소합니다.", Toast.LENGTH_SHORT).show();
                            setPlaylistEditable(false, false);

                            btnPeekPlay.setClickable(true);
                            btnPeekNext.setClickable(true);
                            btnPeekPrev.setClickable(true);
                            btnPeekPlaylist.setClickable(true);

                            mAdapter.notifyDataSetChanged();

                        }

                    }

                    if (newState == STATE_EXPANDED && isItemSelected) {
                        Log.e(TAG, "[bottomSheetBehavior][1]");
                        imageViewPeek.setVisibility(View.GONE);
                        lExpandedPlayer.setVisibility(View.VISIBLE);
                        rlPeekedPlayer.setVisibility(View.GONE);
                        rlPeekedBottomMenu.setVisibility(View.GONE);

                        mWebView.setClickable(false);
                        mWebView.setFocusable(false);
                        rlBottomSheet.setClickable(true);
                        btnToggle.setVisibility(View.VISIBLE);
                        layoutReward.setVisibility(View.VISIBLE);
                        rlPeekedBottomMenu.setVisibility(View.GONE);
                    }

                    if(newState == STATE_COLLAPSED) {
                        Log.e(TAG, "[bottomSheetBehavior][2]");
                        rlPeekedPlayer.setVisibility(View.VISIBLE);
                        rlPeekedBottomMenu.setVisibility(View.GONE);
                        rlPeekedBottomMenu2.setVisibility(View.GONE);
                        rlPlayerBottomMenu.setVisibility(View.GONE);
                        imageViewPeek.setVisibility(View.VISIBLE);

                        mWebView.setFocusableInTouchMode(true);
                        mWebView.setClickable(true);
                        mWebView.setFocusable(true);
                        if (!mWebView.hasFocus()) {
                            mWebView.requestFocus();
                        }

                        rlPeekedBottomMenu.setVisibility(View.GONE);
                    }

                    if( getPreferencesForToken().equals("")) {
                        Log.e(TAG, "[bottomSheetBehavior][3]");
                        Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 0 : " + getPreferencesForToken());
                        toast(getResources().getString(R.string.no_login_error));
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(STATE_COLLAPSED);
                        rlPeekedBottomMenu.setVisibility(View.GONE);
                        mWebView.setFocusableInTouchMode(true);
                        mWebView.setClickable(true);
                        mWebView.setFocusable(true);
                        if (!mWebView.hasFocus()) {
                            mWebView.requestFocus();
                        }
                        return;
                    }

                    if( getPreferencesForToken().equals(null)) {

                        Log.e(TAG, "[bottomSheetBehavior][4]");
                        Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 1 : " + getPreferencesForToken());
                        toast(getResources().getString(R.string.no_login_error));
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(STATE_COLLAPSED);
                        rlPeekedBottomMenu.setVisibility(View.GONE);
                        mWebView.setFocusableInTouchMode(true);
                        mWebView.setClickable(true);
                        mWebView.setFocusable(true);
                        if (!mWebView.hasFocus()) {
                            mWebView.requestFocus();
                        }
                        return;
                    }

                }




            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    public void setViewsAsPlayStatus() {

        btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_play_04));
        btnPeekPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.music_play_04));
    }

    public void setViewsAsPauseStatus() {

        btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_play_01));
        btnPeekPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.music_play_01));
    }

    public void loadDataEmptyWhenPlayStatus() {
        textExpanded01.setText("재생중인 음악이 없습니다.");
        textPeek01.setText("재생중인 음악이 없습니다.");
        textExpanded02.setText("");
        textPeek02.setText("");
//
//        Picasso.with(this)
//                .load()
//                .into(imageView01);

//        Picasso.with(this)
//                .load(track.getCoverImageUrl())
//                .transform(new BlurTransformation(this, 50))
//                .into(imageView02);

        imageView01.setImageDrawable(null);
        imageView02.setImageDrawable(null);
        setViewsAsPauseStatus();
        textExpanded03.setText("00:00");
        seekBar.setProgress(0);

    }

    public void loadDataAsPlayStatus(final MusicTrack track) {

//        updateAppSeekBar();

        Picasso.with(this)
                .load(track.getCoverImageUrl())
                .into(imageViewListHeader);

        Picasso.with(this)
                .load(track.getCoverImageUrl())
                .into(imageView01);

        Picasso.with(this)
                .load(track.getCoverImageUrl())
                .transform(new BlurTransformation(this, 25))
                .into(imageView02);

        Picasso.with(this)
                .load(track.getCoverImageUrl())
                .transform(new BlurTransformation(this, 25))
                .into(imageViewPeek);

        currentPlayingId = Long.parseLong(track.getId());

        Log.e(TAG, "[loadDataAsPlayStatus][currentPlayingId] : " + currentPlayingId);

        if (!getPreferencesForWebSocketPlaytime().equals("")) {
            long lengthPlaytime = Long.parseLong(getPreferencesForWebSocketPlaytime());
            txtReward.setText(String.format("%02d분 %02d초", lengthPlaytime/60 , lengthPlaytime%60));
        }

        textExpanded01.setText("" + String.valueOf(track.getSubject()));
        textExpanded01.setSingleLine(true); //한줄로 나오게 하기.
        textExpanded01.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        textExpanded01.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기

        textPeek01.setText(""  + String.valueOf(track.getSubject()));
//        textPeek01.setSingleLine(true); //한줄로 나오게 하기.
//        textPeek01.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
//        textPeek01.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기


        textExpanded02.setText(""+ String.valueOf(track.getArtistNick()));

        textExpanded02.setSingleLine(true); //한줄로 나오게 하기.
        textExpanded02.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        textExpanded02.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기

        textPeek02.setText(""+ String.valueOf(track.getArtistNick()));
        textExpanded05.setText("" + track.getHeartCnt()); // 좋아요 갯수

        rlPlayerBottomMenu_Row0_TextView1.setText( ""+ String.valueOf(track.getSubject()) + " - " + String.valueOf(track.getArtistNick()));

        rlPlayerBottomMenu_Row1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSpecificPage(DEFAULT_URL + "/music" + "/artist" + "/" + "" + track.getArtistId());
            }
        });


        rlPlayerBottomMenu_Row2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSpecificPage(DEFAULT_URL + "/music" + "/album" + "/" + "" + track.getMusicAlbumId());
            }
        });


        //  SHARE ::: 공유하기
        rlPlayerBottomMenu_Row3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "[PLAYER] : [ShareModal] javascript:callJS('UPMShareModal.showShareModal("+ track.getMusicAlbumId() +","+ track.getId() +")')");

                // UPMShareModal.showShareModal(trackId)
                mWebView.loadUrl("javascript:callJS('UPMShareModal.showShareModal("+ track.getMusicAlbumId() +","+ track.getId() +")')");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript("UPMShareModal.showShareModal("+ track.getMusicAlbumId() +","+ track.getId() +")", null);
                } else {
                    mWebView.loadUrl("javascript:UPMShareModal.showShareModal("+ track.getMusicAlbumId() +","+ track.getId() +")");
                }
                rlPeekedBottomMenu2.setVisibility(View.GONE);
                rlPlayerBottomMenu.setVisibility(View.GONE);

                // 1. COLLAPSE BOTTOM SHEET
                bottomSheetBehavior.setState(STATE_COLLAPSED);
//                playlistListView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);

//                swipeMenuListView.setVisibility(View.INVISIBLE);
                rlListViewHeader.setVisibility(View.INVISIBLE);
                rlListViewHeader2.setVisibility(View.INVISIBLE);
                lExpandedPlayer.setFocusable(false);
                lExpandedPlayer.setClickable(false);

            }
        });

        // 좋아요
        rlPlayerBottomMenu_Row4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempId = itemsArrayList.get(getPreferencesForLastPlayedTrackNumber()).getId();
                if (!(GlobalApplication.getInstance().getServiceInterface().getAudioItem() == null)) {
                    tempId = GlobalApplication.getInstance().getServiceInterface().getAudioItem().getId();
                }
                Log.e(TAG, "[handleLikeTrack]:::tempId::" + tempId);
                requestHeart(tempId, false, -1);

            }
        });
        // 담기
        rlPlayerBottomMenu_Row5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collectionMode = "PLAYER"; // PLAYER, PLAYLIST, PLAYLIST_EDIT
                collectSingleId = currentPlayingId;

                // 뷰 호출
                view = getLayoutInflater().inflate(R.layout.custom_dialog_listview, null);
                // 해당 뷰에 리스트뷰 호출
                customDialogListView = (ListView)view.findViewById(R.id.listview);

                customDialogButton = (Button) view.findViewById(R.id.listview_confirm);

                customDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        toast(getString(R.string.toast_input_playlist_name));
//                      Log.e(TAG, " > [btnTabDownButton] customDialogButton willbe CreatedList ");

                        customCreateDialog.show();

                    }
                });

                // 리스트뷰에 어뎁터 설정
                customDialogListView.setAdapter(customDiaglogListViewAdapter);
                // 다이얼로그 생성
                listViewDialogBuilder = new AlertDialog.Builder(mCtx);
                listViewDialog = listViewDialogBuilder.create();
                // 리스트뷰 설정된 레이아웃
                listViewDialog.setView(view);
                // 확인버튼
//                listViewDialogBuilder.setPositiveButton("내 리스트 만들기", null);
                listViewDialog.show();

            }
        });


        if(track.getLiked().equals("false")) {
            btnHeart.setBackground(getResources().getDrawable(R.drawable.btn_like_off));
            rlPlayerBottomMenu_Row4_ImageView1.setBackground(getResources().getDrawable(R.drawable.btn_like_off));
            rlPlayerBottomMenu_Row4_TextView1.setText("좋아요");
        }

        if(track.getLiked().equals("true")) {
            btnHeart.setBackground(getResources().getDrawable(R.drawable.btn_like_on));
            rlPlayerBottomMenu_Row4_ImageView1.setBackground(getResources().getDrawable(R.drawable.btn_like_on));
            rlPlayerBottomMenu_Row4_TextView1.setText("좋아요 취소");
        }

        objectId = "" + track.getId();
        albumId = "" + track.getMusicAlbumId();
        objectName = track.getFilenameUrl();

    }

    private void updateLocalSeekBar() {
//        Log.e(TAG, "[updateSeekBar] : progress... " );
        //seekBar.setProgress((int)((float)mediaPlayer.getCurrentPosition() / mediaFileLength) * 100);
        int currentProgress = mediaPlayer.getCurrentPosition() / 1000;
        seekBar.setProgress(currentProgress);

        if(mediaPlayer.isPlaying()) {
            updater = new Runnable() {
                @Override
                public void run() {
                    updateLocalSeekBar();
                    int lengthSec = mediaPlayer.getCurrentPosition() / 1000;
                    textExpanded03.setText(String.format("%02d:%02d", lengthSec/60 , lengthSec%60));
                }
            };
            handler.postDelayed(updater,1000); // 1 sec
        }
    }

    private void updateAppSeekBar() {

//        Log.e(TAG, "[updateAppSeekBar], updateAppSeekBar called");

        if (activityStatus.equals("pause") || activityStatus.equals("notplayed")) {
            return;
        }

        mediaPlayer = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer();

        if( mediaPlayer == null ) {

            Log.e(TAG, "[updateAppSeekBar], mediaPlayer null");
        }

        int currentProgress = mediaPlayer.getCurrentPosition() / 1000; //
        seekBar.setProgress(currentProgress);

//        Log.e(TAG, "[updateAppSeekBar], currentProgress :::  " + currentProgress + "");
//        Log.e(TAG, "[updateAppSeekBar], currentProgress :::  " + currentProgress + "");

        if(GlobalApplication.getInstance().getServiceInterface().isPlaying() || mediaPlayer.isPlaying()) { // mediaPlayer.isPlaying()
            updater = new Runnable() {
                @Override
                public void run() {

                    if (getPreferencesForToken().equals("")) {
                        return;
                    }

                    updateAppSeekBar();

                    int lengthSec = mediaPlayer.getCurrentPosition() / 1000;

                    textExpanded03.setText(String.format("%02d:%02d", lengthSec/60 , lengthSec%60));
                    if (!getPreferencesForWebSocketPlaytime().equals("")) {
                        long lengthPlaytime = Long.parseLong(getPreferencesForWebSocketPlaytime());
                        txtReward.setText(String.format("%02d분 %02d초", lengthPlaytime/60 , lengthPlaytime%60));
                    }

                }
            };
            handler.postDelayed(updater,1000); // 1 sec
        }
    }

    private void handleAppSeekbar(){
        mediaPlayer = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void handleLocalSeekbar(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void handleMediaPlayerPlayOrPause() {

        Log.e(TAG, " >>>> [getPreferencesForIsPlayingOnDestroy]  : " + getPreferencesForIsPlayingOnDestroy());

        if (getPreferencesForIsPlayingOnDestroy().equals("true")) {
//            initMediaPlayer()
//            Log.e(TAG, " >>>> [handleMediaPlayerPlayOrPause] initMediaPlayer : ");
//            GlobalApplication.getInstance().getServiceInterface().initMediaPlayer();
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "[btnPlay] [0]");


                if( GlobalApplication.getInstance().getServiceInterface().getPlaylistItemCount() == 0) {
                    Log.e(TAG, "[btnPlay] [0-1]");
                    toast(getString(R.string.toast_please_get_play_item));
                    return;
                }

                if (getPreferencesForIsPlayingOnDestroy().equals("true")) {
                    Log.e(TAG, "[btnPlay] [0-1]");
                    GlobalApplication.getInstance().getServiceInterface().togglePlay();
                    return;
                }

                if (!isClicked ) {
//                    mStompClient.disconnect();
//                    mStompClient.connect();
                    Log.e(TAG, "[btnPlay] [1]");

                    isClicked = true;
//                    toast("한번 더 눌러주세요.");
                    if (GlobalApplication.getInstance().getServiceInterface().getAudioItem() != null) {
                        Log.e(TAG, "[btnPlay] [1-1]");
                        Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: not null :: currentItem : " + GlobalApplication.getInstance().getServiceInterface().getAudioItem().getId() );
                        Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: not null :: currentItem : " + GlobalApplication.getInstance().getServiceInterface().getAudioItem().getArtistNick() );
//
//                        GlobalApplication.getInstance().getServiceInterface().play();

                        if(getPreferencesForLastPlayedTrackNumber() <=0 ) {

                            Log.e(TAG, "[btnPlay] [1-2]");
                            Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: 0");
//                          // ONCE
                            GlobalApplication.getInstance().getServiceInterface().play(0);
                        } else {

                            Log.e(TAG, "[btnPlay] [1-3]");
                            Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: #");
                            GlobalApplication.getInstance().getServiceInterface().play(getPreferencesForLastPlayedTrackNumber());
                        }

                    }

                    if (GlobalApplication.getInstance().getServiceInterface().getAudioItem() == null) {
                        Log.e(TAG, "[btnPlay] [1-4]");
                        Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: null. lastPlayedTrack# ::" +getPreferencesForLastPlayedTrackNumber() );

                        if(getPreferencesForLastPlayedTrackNumber() <=0 ) {
                            Log.e(TAG, "[btnPlay] [1-5]");
                            GlobalApplication.getInstance().getServiceInterface().play(0);
                        } else {
                            Log.e(TAG, "[btnPlay] [1-6]");
                            GlobalApplication.getInstance().getServiceInterface().play(getPreferencesForLastPlayedTrackNumber());
                        }

                    }

                } else {
                    // 재생 또는 일시정지
                    Log.e(TAG, "[btnPlay] [1-7]");
                    GlobalApplication.getInstance().getServiceInterface().togglePlay();
                }

                playerUpdateUI();//add
            }
        });

        //  로그인 유무에 따라 가능 처리
        btnPeekPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "[btnPeekPlay] clicked");

                if( getPreferencesForToken().equals("")) {
                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 0 : " + getPreferencesForToken());
                    toast(getResources().getString(R.string.no_login_error));
                    return;
                }

                if( getPreferencesForToken().equals(null)) {

                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 1 : " + getPreferencesForToken());
                    toast( getResources().getString(R.string.no_login_error));
                    return;
                }

                if( GlobalApplication.getInstance().getServiceInterface().getPlaylistItemCount() == 0) {
                    toast(getString(R.string.toast_please_get_play_item));
                    return;
                }

                if( GlobalApplication.getInstance().getServiceInterface().getPlaylistItemCount() == 0) {
                    toast(getString(R.string.toast_please_get_play_item));
                    return;
                }

                if (getPreferencesForIsPlayingOnDestroy().equals("true")) {
                    Log.e(TAG, "[btnPlay] [0-1]");
                    GlobalApplication.getInstance().getServiceInterface().togglePlay();
                    return;
                }

                if (!isClicked) {
//                    mStompClient.disconnect();
//                    mStompClient.connect();

                    isClicked = true;
//                    toast("한번 더 눌러주세요.");
                    if (GlobalApplication.getInstance().getServiceInterface().getAudioItem() != null) {
                        Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: not null :: currentItem : " + GlobalApplication.getInstance().getServiceInterface().getAudioItem().getId() );
                        Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: not null :: currentItem : " + GlobalApplication.getInstance().getServiceInterface().getAudioItem().getArtistNick() );
//
//                        GlobalApplication.getInstance().getServiceInterface().play();

                        if(getPreferencesForLastPlayedTrackNumber() <=0 ) {

                            Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: 0");
//
                            GlobalApplication.getInstance().getServiceInterface().play(0);
                        } else {

                            Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: #");
                            GlobalApplication.getInstance().getServiceInterface().play(getPreferencesForLastPlayedTrackNumber());
                        }

                    }

                    if (GlobalApplication.getInstance().getServiceInterface().getAudioItem() == null) {
                        Log.e(TAG, "[handleMediaPlayerPlayOrPause]:::getAudioItem():: null. lastPlayedTrack# ::" +getPreferencesForLastPlayedTrackNumber() );

                        if(getPreferencesForLastPlayedTrackNumber() <=0 ) {
                            GlobalApplication.getInstance().getServiceInterface().play(0);
                        } else {
                            GlobalApplication.getInstance().getServiceInterface().play(getPreferencesForLastPlayedTrackNumber());
                        }

                    }

                } else {
                    // 재생 또는 일시정지
                    GlobalApplication.getInstance().getServiceInterface().togglePlay();
                }

                playerUpdateUI();//add
            }
        });

    }

    private void handleMediaPlayerForward() {

        btnFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                GlobalApplication.getInstance().getServiceInterface().forward();

            }
        });
        //  로그인 유무에 따라 가능 처리
        btnPeekNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( getPreferencesForToken().equals("")) {

                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 0 : " + getPreferencesForToken());
                    toast(getResources().getString(R.string.no_login_error));
                    return;
                }
                if( getPreferencesForToken().equals(null)) {

                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 1 : " + getPreferencesForToken());
                    toast(getResources().getString(R.string.no_login_error));
                    return;
                }

//                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                GlobalApplication.getInstance().getServiceInterface().forward();
            }
        });

    }

    private void handleMediaPlayerBackward() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                GlobalApplication.getInstance().getServiceInterface().rewind();
            }
        });
        //  로그인 유무에 따라 가능 처리
        btnPeekPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( getPreferencesForToken().equals("")) {
                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 0 : " + getPreferencesForToken());
                    toast(getResources().getString(R.string.no_login_error));
                    return;
                }


                if( getPreferencesForToken().equals(null)) {
                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 1 : " + getPreferencesForToken());
                    toast(getResources().getString(R.string.no_login_error));
                    return;
                }

//                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                GlobalApplication.getInstance().getServiceInterface().rewind();

            }
        });
    }

    private void setOnWifiLock() {
        wifiLock.acquire();
    }

    private void setOffWifiLock() {
        wifiLock.release();
    }

    private void handleMediaPlayerOnBufferingUpdate() {
        onBufferingUpdate(mediaPlayer, mediaPlayer.getCurrentPosition());
    }

    private void handleMediaPlayerOnCompletion() {
        onCompletion(mediaPlayer);
    }

    private void handlePeekedPlayerClick() {

        //  로그인 유무에 따라 가능 처리
        rlPeekedPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( getPreferencesForToken().equals("")) {

                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 0 : " + getPreferencesForToken());

                    toast(getResources().getString(R.string.no_login_error));
                    return;
                }


                if( getPreferencesForToken().equals(null)) {

                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 1 : " + getPreferencesForToken());
                    toast( getResources().getString(R.string.no_login_error));
                    return;
                }

                if (isPlaylistEditMode) {
                    return;
                }

                bottomSheetBehavior.setState(STATE_EXPANDED);
            }
        });

    }

    private void handleShowPlaylist(){


        Log.e(TAG, "[handleShowPlaylist] : clicked...");
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);
//                playlistListView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                rlListViewHeader.setVisibility(View.VISIBLE);
                rlListViewHeader2.setVisibility(View.VISIBLE);
                lExpandedPlayer.setFocusable(false);
                lExpandedPlayer.setClickable(false);

                rlPeekedBottomMenu2.setVisibility(View.GONE);
                rlPeekedBottomMenu.setVisibility(View.GONE);

            }
        });

        //  로그인 유무에 따라 가능 처리
        btnPeekPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "[btnPeekPlaylist] CLICKED");

                if( getPreferencesForToken().equals("")) {
                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 0 : " + getPreferencesForToken());
                    toast(getResources().getString(R.string.no_login_error));
                    return;
                }

                if( getPreferencesForToken().equals(null)) {
                    Log.e(TAG, "[Peek] getPreferencesForToken () || (null) 1 : " + getPreferencesForToken());
                    toast(getResources().getString(R.string.no_login_error));
                    return;
                }

                if(isPlaylistEditMode){
                    return;
                }

                bottomSheetBehavior.setState(STATE_COLLAPSED);

                if (mRecyclerView.getVisibility() == View.VISIBLE) {
//                    playlistListView.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);

//                    swipeMenuListView.setVisibility(View.INVISIBLE);
                    rlListViewHeader.setVisibility(View.INVISIBLE);
                    rlListViewHeader2.setVisibility(View.INVISIBLE);
                    rlPeekedBottomMenu2.setVisibility(View.GONE);
                    rlPeekedBottomMenu.setVisibility(View.GONE);
                    lExpandedPlayer.setFocusable(true);
                    lExpandedPlayer.setClickable(true);
                } else {

//                    playlistListView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
//                    swipeMenuListView.setVisibility(View.VISIBLE);
                    rlListViewHeader.setVisibility(View.VISIBLE);
                    rlListViewHeader2.setVisibility(View.VISIBLE);
                    lExpandedPlayer.setFocusable(false);
                    lExpandedPlayer.setClickable(false);
                }
            }
        });
        btnToggleListHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);
//                playlistListView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
//                swipeMenuListView.setVisibility(View.INVISIBLE);
                rlListViewHeader.setVisibility(View.INVISIBLE);
                rlListViewHeader2.setVisibility(View.INVISIBLE);
                rlPeekedBottomMenu2.setVisibility(View.GONE);
                rlPeekedBottomMenu.setVisibility(View.GONE);
                lExpandedPlayer.setFocusable(true);
                lExpandedPlayer.setClickable(true);
                if (isPlaylistEditMode == true) {
                    toast(getString(R.string.toast_cancel_edit_playlist));
                    setPlaylistEditable(false, false);

                }
            }
        });

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);
//                playlistListView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
//                swipeMenuListView.setVisibility(View.INVISIBLE);
                rlListViewHeader.setVisibility(View.INVISIBLE);
                rlListViewHeader2.setVisibility(View.INVISIBLE);
                rlPeekedBottomMenu.setVisibility(View.GONE);
                rlPeekedBottomMenu2.setVisibility(View.GONE);
                lExpandedPlayer.setFocusable(true);
                lExpandedPlayer.setClickable(true);

                if (isPlaylistEditMode == true) {
                    toast(getString(R.string.toast_cancel_edit_playlist));
                    setPlaylistEditable(false, false);

                }
            }
        });

    }

    private void setProgressBar() {
        // 진행바 - 필요시
        mProgressBar = (ProgressBar) findViewById(R.id.articleview_progressbar);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(mWebView.VISIBLE);
    }

    private void updatePlayModeUI(){
        if (getPreferencesForShuffle()) {
            btnShuffle.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_random));
        } else {

            btnShuffle.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_random_off));
        }

        if (getPreferencesForRepeat().equals("ONE")) {
            // 한곡 반복은 없음
            btnRepeat.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_rotation_on));
        } else if (getPreferencesForRepeat().equals("ALL")) {

            btnRepeat.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_rotation_on));
        } else {

            btnRepeat.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_rotation));
        }
    }

    public void playerUpdateUI() {

        Log.e(TAG, ": [playerUpdateUI] part 1");

        MusicTrack audioItem = GlobalApplication.getInstance().getServiceInterface().getAudioItem();
        if (audioItem != null) {
            loadDataAsPlayStatus(audioItem);
        } else {
            loadDataEmptyWhenPlayStatus();
        }

        if (rlPeekedBottomMenu.getVisibility() == View.VISIBLE) {

            btnPeekPlay.setClickable(false);
            btnPeekNext.setClickable(false);
            btnPeekPrev.setClickable(false);
            btnPeekPlaylist.setClickable(false);
        }


        if (rlPeekedBottomMenu.getVisibility() == View.GONE) {

            btnPeekPlay.setClickable(true);
            btnPeekNext.setClickable(true);
            btnPeekPrev.setClickable(true);
            btnPeekPlaylist.setClickable(true);
        }

        Log.e(TAG, ": [playerUpdateUI] part 2");

        handleAppSeekbar();
        if (GlobalApplication.getInstance().getServiceInterface().getMediaPlayer() != null) {
            mediaFileLength = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().getDuration();

            int lengthSec = mediaFileLength/1000;
            seekBar.setMax(lengthSec);
            textExpanded04.setText(String.format("%02d:%02d", lengthSec/60 , lengthSec%60));

            if (!getPreferencesForWebSocketPlaytime().equals("")) {
                long lengthPlaytime = Long.parseLong(getPreferencesForWebSocketPlaytime());
                txtReward.setText(String.format("%02d분 %02d초", lengthPlaytime/60 , lengthPlaytime%60));
            }


            updatePlayModeUI();
        }

        if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
            setViewsAsPlayStatus();
//            updateAppSeekBar();
        } else {
            setViewsAsPauseStatus();
        }

    }

    private void loggedInUpdateUI() {
        //FirebaseUser user
//        hideProgressDialog();

        toast(getString(R.string.toast_logging_in));

        isClearHistory = true;
        accessToken = getPreferencesForAccessToken();
        loginMethod = getPreferencesForLoginMethod();

        Log.e(TAG, "loggedInUpdateUI [accessToken] : " + accessToken + ", [provider] : " + loginMethod);
        if (loginMethod.equals("facebook")) {
            reqFacebookLogin(accessToken);
        }

        if (loginMethod.equals("google")) {
            Log.e(TAG, "[google-login] 1-2");
            reqGoogleLogin(accessToken);
            Log.e(TAG, "[google-login] 1-22");
        }

        if (loginMethod.equals("kakao")) {
            reqKakaoLogin(accessToken);
        }

        if (loginMethod.equals("naver")) {
            reqNaverLogin(accessToken);
        }

        // NOT CALLED..
        if (loginMethod.equals("local")) {
//            reqEmailLogin(localLoginUsername, localLoginPassword);
//            requestLocalLogin();

        }
        setPreferencesForLoginMethod(loginMethod);

        Log.e(TAG, "loggedInUpdateUI [accessToken] : " + accessToken + ", [provider] : " + loginMethod);
    }

    private void handleCustomDialog() {
        customCreateDialog = new CustomCreateDialog(mCtx);

        // 뷰 호출
        view = getLayoutInflater().inflate(R.layout.custom_dialog_listview, null);
        // 해당 뷰에 리스트뷰 호출
        customDialogListView = (ListView)view.findViewById(R.id.listview);

        customDialogButton = (Button) view.findViewById(R.id.listview_confirm);
        // 리스트뷰에 어뎁터 설정
        customDialogListView.setAdapter(customDiaglogListViewAdapter);
        // 다이얼로그 생성
        listViewDialogBuilder = new AlertDialog.Builder(mCtx);

        listViewDialog = listViewDialogBuilder.create();
        // 리스트뷰 설정된 레이아웃
        listViewDialog.setView(view);

        customDiaglogListViewAdapter = new CustomDiaglogListViewAdapter(this);

        btnTabDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 뷰 호출
                view = getLayoutInflater().inflate(R.layout.custom_dialog_listview, null);
                // 해당 뷰에 리스트뷰 호출
                customDialogListView = (ListView)view.findViewById(R.id.listview);

                customDialogButton = (Button) view.findViewById(R.id.listview_confirm);

                customDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast(getString(R.string.toast_input_playlist_name));
//                        Toast.makeText(getApplicationContext(), "만들 리스트의 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, " > [btnTabDownButton] customDialogButton willbe CreatedList ");

                        customCreateDialog.show();

                    }
                });

                // 리스트뷰에 어뎁터 설정
                customDialogListView.setAdapter(customDiaglogListViewAdapter);
                // 다이얼로그 생성
                listViewDialogBuilder = new AlertDialog.Builder(mCtx);

                listViewDialog = listViewDialogBuilder.create();
                // 리스트뷰 설정된 레이아웃
                listViewDialog.setView(view);
                // 확인버튼
//                listViewDialogBuilder.setPositiveButton("내 리스트 만들기", null);
                listViewDialog.show();
            }
        });
    }


    /**
     * *******************************************
     * [END OF ]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF WEBSOCKET / STOMP-LIBRARY]
     * *******************************************
     */

    public void connectStomp() {

//        mStompClient = Stomp.over(Stomp.ConnectionProvider.JWS, "ws://" + ANDROID_EMULATOR_LOCALHOST
//                + ":" + RestClient.SERVER_PORT + "/example-endpoint/websocket");

        // 1. [TRIAL with CLIENT]
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);
        OkHttpClient client = builder.build();

//        // 2. [TRIAL with HEADER]
//        final Map<String, String> JSESSIONIDMAP = new HashMap<>();
//        String header = cookieManager.getCookie(DEFAULT_URL);
//        //Log.e(TAG, "[STOMP] :: header >> " + header);
//        String[] cookies = header.split(";", 0);
//        for (String wo : cookies) {
//            if (wo.contains("JSESSIONID")) {
////                Log.e(TAG, "JSESSIONID contains " + wo);
//                JSESSIONIDMAP.put("JSESSIONID", wo.replace("JSESSIONID=",""));
//            }
//        }
        // 3
        if (mStompClient != null) {
            if (mStompClient.isConnected()) {
                mStompClient.disconnect();
            }
        }

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP
                , "wss://upmusic.azurewebsites.net/upm-player-websocket/websocket"
//                , "ws://10.0.2.2:8080/upm-player-websocket/websocket"
                , null
                , client
        );

        // 4
        mStompClient.topic("/user/topic/player")
                .subscribe(topicMessage -> {
                    Log.d(TAG, "[STOMP][!!!!][TOPIC] ::: [Websocket] ::: Received " + topicMessage.getPayload());

                    JSONObject data = new JSONObject(topicMessage.getPayload());
                    // {"playtime":37,"musicTrackId":180,"streamingRewardStep":1,"upPoint":"106.4"}
                    String playtime;
                    String streamingRewardStep;
                    String upPoint;
                    try {
                        playtime = data.getString("playtime");
                        streamingRewardStep = data.getString("streamingRewardStep");
                        upPoint = data.getString("upPoint");

                        Log.e(TAG, "[STOMP][WEBSOCKET] [onMessageReceived] > playtime : " + playtime
                                + ", streamingRewardStep : " + streamingRewardStep
                                + " ,upPoint : " + upPoint);

                        setPreferencesForUpPoint(upPoint);
                        setPreferencesForWebSocketPlaytime(playtime);

                    } catch (JSONException e) {
                        return;
                    }

                });
        mStompClient.connect();

        // Probrem x
        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
//                            toast("Stomp connection opened");
//                            toast(getString(R.string.toast_reward_connect));
                            break;
                        case ERROR:
//                            toast("Stomp connection error");
//                            toast("리워드 기능이 작동을 멈췄습니다.");
                            break;
                        case CLOSED:
//                            toast("Stomp connection closed");
//                            toast("리워드 기능이 작동을 멈췄습니다.");
//                            connectStomp();
                    }
                });

    }

    public void sendSocketPlayTime(long time, String id) {

        if (mStompClient == null) {
            Log.e(TAG, "[STOMP][WEBSOCKET] ::: !mStompClient.isConnected()");
            connectStomp();
        }

        if (!mStompClient.isConnected()) {

            Log.e(TAG, "[STOMP][WEBSOCKET] ::: !mStompClient.isConnected()");
            connectStomp();
        }

        time = 0;
        JSONObject data = new JSONObject();
        try {
            data.put("playtime", time);
            data.put("musicTrackId", id);
            if (mStompClient != null && mStompClient.isConnected()) {
                mStompClient.send("/app/playtime", data.toString()).subscribe();
            }


        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendSocketIncreasePlayTime(long time, String id) {

//        Log.e(TAG, "[STOMP][WEBSOCKET] ::: function [sendSocketIncreasePlayTime] called");
        time = 0;
        JSONObject data = new JSONObject();
        try {
            data.put("playtime", time);
            data.put("musicTrackId", id);
//            Log.e(TAG, "[STOMP][WEBSOCKET] ::: [sendSocketIncreasePlayTime] > playtime : " + time + ", musicTrackId" + id);
//            socket.emit("/app/increase_playtime", data);
            if (mStompClient != null && mStompClient.isConnected()) {
                mStompClient.send("/app/increase_playtime", data.toString()).subscribe();
//                mStompClient.send("/app/increase_playtime", data.toString()).subscribe();
//                mStompClient.send("/app/increase_playtime", data.toString()).subscribe();
//                mStompClient.send("/app/increase_playtime", data.toString()).subscribe();

            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public void startPeriod() {
        setPlayerTimerTask();
    }

    public void setCheckTimerTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // to do something
                if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
                    stopCheckTimer();
                }
                checkWithdrawal();
            }
        };
        checkTimer = new Timer();
        checkTimer.schedule(task, 0, 60000);
    }

    public void stopCheckTimer() {
        //Timer 작업 종료
        if(checkTimer != null) checkTimer.cancel();
    }

    public void setPlayerTimerTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // to do something
                if (!getPreferencesForLastPlayedTrackId().equals("")) {
                    //setPreferencesForToken
//
                    if (GlobalApplication.getInstance().getServiceInterface().getMediaPlayer() == null) {
                        stopPlayerTimer();
                        return;
                    }

                    if (GlobalApplication.getInstance().getServiceInterface().getAudioItem() != null) {
                        int currentInt = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().getCurrentPosition();
                        int currentProgress = currentInt / 1000;
                        seekBar.setProgress(currentProgress);
                        Message msg = seekbarHandler.obtainMessage();
                        seekbarHandler.sendMessage(msg);
                        sendSocketIncreasePlayTime(0,getPreferencesForLastPlayedTrackId());

                        if (currentProgress % 60 == 0) {
                            checkWithdrawal();
                        }

                    }

//                    if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
//
//                    }

                    if (getPreferencesForToken().equals("")) {
                        stopPlayerTimer();
                        return;
                    }

                }
            }
        };
        playerTimer = new Timer();
        playerTimer.schedule(task, 0, 1000);
    }

    public void stopPlayerTimer() {
        //Timer 작업 종료
        if(playerTimer != null) playerTimer.cancel();
    }

    public void releaseStomp() {
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
    }

    final Handler seekbarHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (GlobalApplication.getInstance().getServiceInterface().getMediaPlayer() == null) {
                stopPlayerTimer();
                return;
            }

            if( bottomSheetBehavior.getState() == STATE_HIDDEN) {
                return;
            }
            long lengthSec = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().getCurrentPosition() / 1000;
            textExpanded03.setText(String.format("%02d:%02d", lengthSec/60 , lengthSec%60));

            if (!getPreferencesForWebSocketPlaytime().equals("")) {
                long lengthPlaytime = Long.parseLong(getPreferencesForWebSocketPlaytime());
                txtReward.setText(String.format("%02d분 %02d초", lengthPlaytime/60 , lengthPlaytime%60));
            }
//            txtReward.setText(String.format("%02d분 %02d초", lengthSec/60 , lengthSec%60));
            playerUpdateUI();
            updatePlayModeUI();

        }

    };

    /**
     * *******************************************
     * [END OF WEBSOCKET / STOMP-LIBRARY]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF HANDLER-OF-WEB-VIEW / AND INTERFACES]
     * *******************************************
     */
    private static Field sConfigCallback;
    int popupIndex = -1;
    WebView popupView;

    static {
        try {
            sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            sConfigCallback.setAccessible(true);
        } catch (Exception e) {
        }
    }
    private void setWebView() {
        // 웹뷰 정의 및 설정
        mWebView = (ObservableWebView) findViewById(R.id.web_view);
        webViewLayer = (RelativeLayout) findViewById(R.id.main_layer);

        cookieManager.setAcceptCookie(true);

        WebSettings set = mWebView.getSettings();
        set.setAppCacheEnabled(true);
        set.setCacheMode(WebSettings.LOAD_DEFAULT); //LOAD_DEFAULT // LOAD_NO_CACHE
        set.setSupportMultipleWindows(true);
        set.setUserAgentString(USER_AGENT);
        set.setJavaScriptEnabled(true);
        set.setPluginState(WebSettings.PluginState.ON);
        set.setSaveFormData(false);
        set.setJavaScriptCanOpenWindowsAutomatically(true);
        set.setLoadWithOverviewMode(true);
        set.setUseWideViewPort(true);
        set.setSupportZoom(true);
        set.setBuiltInZoomControls(true);
        set.setDisplayZoomControls(false);

        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(false);
        mWebView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int l, int t) {
                InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mWebView.getWindowToken(), 0);
            }
        });

        // 뷰 가속
        if (Build.VERSION.SDK_INT >= 20) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            // Cross origin requests are only supported http, data, https 문제 해결
            set.setAllowFileAccessFromFileURLs(true);
            set.setAllowUniversalAccessFromFileURLs(true);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            // 결제연동
            set.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }

        // 속도개선
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            set.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            set.setTextZoom(100); // Text폰트 고정
        }

        // TODO 디버그 테스트 - chrome://inspect, 최종 배포시에는 소스 주석처리나 제거 후 배포
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.addJavascriptInterface(new AndroidBridge(), "HybridApp"); // Native와 Web간의 통신
        mWebView.setWebViewClient(new WebClient()); // 각종 알림 및 요청을 받게 되는 설정
        mWebView.setWebChromeClient(new FullscreenableChromeClient(MainActivity.this)); // WEBCHROME의 경우, 하나만 설정이므로 커스텀하게 만들어서 운영
        mWebView.setVerticalScrollbarOverlay(true); // 스크롤바 영역생기는 부분 처리

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("url")) {
                mWebView.loadUrl(extras.getString("url"));
            } else if (extras.containsKey("scheme_url")) {
                mWebView.loadUrl(extras.getString("scheme_url"));
            } else {
                mWebView.loadUrl(Constants.LOGIN);
            }

        } else {
            if (getIntent().hasExtra("scheme_url")) {
                mWebView.loadUrl(getIntent().getStringExtra("scheme_url"));
            } else {
                mWebView.loadUrl(Constants.LOGIN);
            }
        }
    }

    public class FullscreenableChromeClient extends WebChromeClient {
        private Activity mActivity = null;

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private FrameLayout mFullscreenContainer;
        private  final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        public FullscreenableChromeClient(Activity activity) {
            this.mActivity = activity;

        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
            builder.setTitle(getString(R.string.app_name));
            String temp= message;

            if (message.contains("NullPointerException")) {
                temp = "입력하신 내용을 확인해 주세요.";
            }

            builder.setMessage(temp)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });
            dialog = builder.create();
            dialog.show();

            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
            builder.setTitle(getString(R.string.app_name));

            String temp= message;

            if (message.contains("NullPointerException")) {
                temp = "입력하신 내용을 확인해 주세요.";
            }

            builder.setMessage(temp)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            dialog = builder.create();
            dialog.show();

            return true;
        }

        // 새창 띄워줄 때
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView childView = new WebView(mCtx);
            childView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            childView.getSettings().setDefaultTextEncodingName("utf-8");
            childView.getSettings().setJavaScriptEnabled(true);
            childView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            childView.getSettings().setSupportMultipleWindows(true);
            childView.getSettings().setAppCacheEnabled(true);
            childView.getSettings().setSupportZoom(true);
            childView.getSettings().setUseWideViewPort(true);
            childView.getSettings().setGeolocationEnabled(true); // 현재위치 연결
            childView.getSettings().setDomStorageEnabled(true); // twitter 등
            childView.getSettings().setDatabaseEnabled(true);
            childView.getSettings().setPluginState(WebSettings.PluginState.ON);
            childView.getSettings().setLoadWithOverviewMode(true);
            childView.getSettings().setLoadsImagesAutomatically(true);
            String userAgent = childView.getSettings().getUserAgentString();
            childView.getSettings().setUserAgentString(userAgent + " connectionType/webview");

            if (Build.VERSION.SDK_INT >= 16) {
                childView.getSettings().setAllowFileAccessFromFileURLs(true);
                childView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            }

            if (Build.VERSION.SDK_INT >= 21) {
                childView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                cookieManager.setAcceptThirdPartyCookies(childView, true);
            }


            childView.setWebChromeClient(this);
            childView.setLayoutParams(view.getLayoutParams());

            // 뷰 가속
            if (Build.VERSION.SDK_INT >= 20) {
                childView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                childView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            // 속도개선
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                childView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            }

//            childView.addJavascriptInterface(new AndroidBridge(), "HybridApp");
            childView.setWebViewClient(new WebClient());
            childView.setWebChromeClient(new FullscreenableChromeClient(MainActivity.this));
            childView.setVerticalScrollbarOverlay(true);

            webViewLayer.addView(childView);

            childView.requestFocus();
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(childView);
            resultMsg.sendToTarget();
            childView.bringToFront();
            popupIndex = webViewLayer.indexOfChild(childView);
            popupView = childView;
            return true;
        }

        // 새창 닫힐 때
        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            mWebView.removeView(window);
            closePopup();
        }


        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }

//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로모드 고정
////                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //화면 센서 활성화
                mOriginalOrientation = mActivity.getRequestedOrientation();
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
                mFullscreenContainer = new FullscreenHolder(mActivity);
//            mFullscreenContainer.setBackgroundColor(Color.BLACK);
                mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
                decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
                mCustomView = view;
                setFullscreen(true);
                mCustomViewCallback = callback;
//          mActivity.setRequestedOrientation(requestedOrientation);

            }

            super.onShowCustomView(view, callback);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
            this.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }

            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setFullscreen(false);
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();

            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
            mActivity.setRequestedOrientation(mOriginalOrientation);

        }

        private void setFullscreen(boolean enabled) {

            Window win = mActivity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            if (enabled) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
                if (mCustomView != null) {
                    mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
            win.setAttributes(winParams);
        }

        private class FullscreenHolder extends FrameLayout {
            public FullscreenHolder(Context ctx) {
                super(ctx);
                setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
            }

            @Override
            public boolean onTouchEvent(MotionEvent evt) {
                return true;
            }
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            filePathCallbackNormal = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Browser"), 1);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            filePathCallbackNormal = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), 1);
        }

        // For Android 4.1+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg, "");
        }

        // For Android 5.0+
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams) {
            if (filePathCallbackLollipop != null) {
                filePathCallbackLollipop = null;
            }
            filePathCallbackLollipop = filePathCallback;


            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs();
            }

            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);

            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

            startActivityForResult(chooserIntent, 2);
            return true;
        }
    }

    public class WebClient extends WebViewClient {
        // 새로운 URL이 webview에 로드되려 할 경우 컨트롤을 대신할 기회를 줌
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {

            Log.e(TAG, " : [shouldOverrideUrlLoading] : url Changed :  " + url);


            // PASS, 본인인증 , intent, market 처리
            if (url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            // url 이동시에 다시 화면 제자리
            final CoordinatorLayout.LayoutParams[] layoutParams = {new CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT)};
            final int[] left = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin))};
            final int[] top = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_top_margin))};
            final int[] right = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin))};
            final int[] bottom = {getPixelValue(mCtx, bottomSheetBehavior.getPeekHeight())};

            bottomSheetBehavior.setHideable(false);
            //BottomSheetBehavior.STATE_COLLAPSED
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            // 해당 작업을 처리함
                            bottomSheetBehavior.setState(STATE_COLLAPSED);

                            layoutParams[0] = new CoordinatorLayout.LayoutParams(
                                    CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                                    CoordinatorLayout.LayoutParams.WRAP_CONTENT);
                            left[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin));
                            top[0] = getPixelValue(mCtx,(int)  getResources().getDimension(R.dimen.webview_top_margin));
                            right[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin));
                            bottom[0] = bottomSheetBehavior.getPeekHeight();


                            layoutParams[0].setMargins(left[0], top[0], right[0], bottom[0]);
                            mainFrameLayout.setLayoutParams(layoutParams[0]);

                        }
                    });
                }
            }).start();


            // LOGOUT HANDLE
            if(url.contains("auth/logout")) {
                Log.e(TAG,"[REQUEST_LOGOUT] 1");

                isClicked = false;

                stopPlayerTimer();
                loadDataEmptyWhenPlayStatus();
                GlobalApplication.getInstance().getServiceInterface().releaseMediaPlayer();

//                if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {

//                    Log.e(TAG,"[REQUEST_LOGOUT] isPlaying");
                    if (GlobalApplication.getInstance().getServiceInterface().getMediaPlayer() != null) {

                        Log.e(TAG,"[REQUEST_LOGOUT] getMediaPlayer");
                        GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().stop();
                        GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().release();
                    }

//                    mediaPlayer = new MediaPlayer();

                    GlobalApplication.getInstance().getServiceInterface().removeNotificationPlayer();

                    if (mStompClient != null) {
                        mStompClient.disconnect();
                    }

                    isFirstLoad = true;
                    savePreferencesForPlayerFirstLoad(true);

//                }

                naverLoginInstance.logout(mCtx);
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                // Google sign out
                mGoogleSignInClient.signOut();


                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance());
                sp.edit().remove(PREF_COOKIES).commit();

                setPreferencesForToken("");
                setPreferencesForRememberMe("false");
                setPreferencesForEmail("");
                setPreferencesForPassword("");
                setPreferencesForAccessToken("");
                setPreferencesForLoginMethod("");

                savePreferencesForRepeat("NOR");
                savePreferencesForShuffle(false);

                initializeData();

            }

            String cookie = "";
            cookie = cookieManager.getCookie(url); //쿠키값 구하기
            Log.e(TAG, "[shouldOverrideUrlLoading] cookie : " + cookie);
            cookieManager.setCookie(url, cookie);//쿠키매니저에 url, 쿠키 추가.

            checkWithdrawal();

            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        // 페이지 로딩 시작할 때
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
        }

        // 페이지 로딩 끝난 후
        @Override
        public void onPageFinished(WebView view, String url) {

            /* NOTICE 로그인을 한 다음 앱을 종료하고, 다시 앱을 실행했을 때 간헐적으로 로그인이 안 된 상태가 된다.
             이는 웹뷰의 RAM과 영구 저장소 사이에 쿠키가 동기화가 안 되어 있기 때문이다. 따라서 강제로 동기화를 해준다. */
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //noinspection deprecation
                CookieSyncManager.getInstance().sync();
            } else {
                // 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
                cookieManager.getInstance().flush();
            }
            mProgressBar.setVisibility(View.INVISIBLE);

            if (url.equals(DEFAULT_URL)){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로모드 고정
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //화면 센서 활성화
                Log.e(TAG, "[HOME PAGE] cookie : \n[\n" + cookieManager.getCookie(DEFAULT_URL) + "\n]\n" );
                isClearHistory = true;

            }

            if (url.equals(DEFAULT_URL + "/")){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로모드 고정
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //화면 센서 활성화
                Log.e(TAG, "[HOME PAGE] cookie : \n[\n" + cookieManager.getCookie(DEFAULT_URL) + "\n]\n" );
                isClearHistory = true;

//                if (mStompClient !=null ) {
//                    mStompClient.disconnect();
//                }
//                connectStomp();
            }

            if(isClearHistory){
                isClearHistory=false;
                view.clearHistory();
            }

            view.requestFocus(View.FOCUS_DOWN);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                            if (!v.hasFocus()) {
                                v.requestFocus();
                            }
                            break;
                    }
                    return false;
                }
            });

            mWebView.requestFocus(View.FOCUS_DOWN);
            mWebView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                            if (!v.hasFocus()) {
                                v.requestFocus();
                            }
                            break;
                    }
                    return false;
                }
            });

            super.onPageFinished(view, url);
        }

        // onReceivedSslError 에러처리, 구글정책문제로 인하여 해당 부분은 WebView를 이용한다면 무조건 들어가야함
        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(getString(R.string.notification_error_ssl_cert_invalid))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                        }
                    });
            dialog = builder.create();
            dialog.show();
        }


        // TODO :: 수정중
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            Log.e(TAG, "[webView][onReceivedError] ::: " + errorCode + " .");

//            int err = errorCode;
//            if (err == -2) {
//                view.loadUrl("about:blank");
//                view.loadUrl("file:///android_asset/de_offline.html");
//            } else {
//                view.reload();
//            }

            switch (errorCode) {
                case ERROR_AUTHENTICATION: // 서버에서 사용자 인증 실패
                case ERROR_BAD_URL: // 잘못된 URL
                case ERROR_CONNECT: // 서버로 연결 실패
                case ERROR_FAILED_SSL_HANDSHAKE: // SSL handshake 수행 실패
                case ERROR_FILE: // 일반 파일 오류
                case ERROR_FILE_NOT_FOUND: // 파일을 찾을 수 없습니다
                case ERROR_HOST_LOOKUP: // 서버 또는 프록시 호스트 이름 조회 실패
                case ERROR_IO: // 서버에서 읽거나 서버로 쓰기 실패
                case ERROR_PROXY_AUTHENTICATION: // 프록시에서 사용자 인증 실패
                case ERROR_REDIRECT_LOOP: // 너무 많은 리디렉션
                case ERROR_TIMEOUT: // 연결 시간 초과
                case ERROR_TOO_MANY_REQUESTS: // 페이지 로드중 너무 많은 요청 발생
                case ERROR_UNKNOWN: // 일반 오류
                case ERROR_UNSUPPORTED_AUTH_SCHEME: // 지원되지 않는 인증 체계
                case ERROR_UNSUPPORTED_SCHEME:

//                    final CoordinatorLayout.LayoutParams[] layoutParams = {new CoordinatorLayout.LayoutParams(
//                            CoordinatorLayout.LayoutParams.WRAP_CONTENT,
//                            CoordinatorLayout.LayoutParams.WRAP_CONTENT)};
//                    final int[] left = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin))};
//                    final int[] top = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_top_margin))};
//                    final int[] right = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin))};
//                    final int[] bottom = {getPixelValue(mCtx, bottomSheetBehavior.getPeekHeight())};
//
//                    bottomSheetBehavior.setHideable(true);
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            runOnUiThread(new Runnable(){
//                                @Override
//                                public void run() {
//                                    // 해당 작업을 처리함
//                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//                                    layoutParams[0] = new CoordinatorLayout.LayoutParams(
//                                            CoordinatorLayout.LayoutParams.WRAP_CONTENT,
//                                            CoordinatorLayout.LayoutParams.WRAP_CONTENT);
//                                    left[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin));
//                                    top[0] = getPixelValue(mCtx,(int)  getResources().getDimension(R.dimen.webview_top_margin));
//                                    right[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin));
//                                    bottom[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_bottom_margin));
//                                    layoutParams[0].setMargins(left[0], top[0], right[0], bottom[0]);
//                                    mainFrameLayout.setLayoutParams(layoutParams[0]);
//
//                                }
//                            });
//                        }
//                    }).start();

                    mWebView.loadUrl("about:blank"); // 빈페이지 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 확인버튼 클릭시 이벤트
                            dialog.dismiss();
//                            activity.moveTaskToBack(true);
//                            activity.finish();
                            moveTaskToBack(true);
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }});
                    builder.setMessage("네트워크 상태가 원활하지 않습니다. 서비스를 종료합니다.");
                    // builder.setMessage("네트워크 상태가 원활하지 않습니다. 페이지를 이동합니다.");
                    builder.setCancelable(false); // 뒤로가기 버튼 차단
                    builder.show(); // 다이얼로그실행
//                    setBottomSheetBehaviorHidden();
                    break;
            }




        }





    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }


    public void closePopup() {
        if (popupView != null) {
            popupView.loadUrl("javascript:self.close();");
            popupView = null;
            try {
                webViewLayer.removeViewAt(popupIndex);
            } catch (Exception e) {
                popupIndex = -1;
                onBackPressed();
            }
        }
        popupIndex = -1;
        mWebView.bringToFront();
    }

    private void handleWebView() {
        // 업뮤직 페이지 불러오기. 리스너를 추가하여 이벤트 출력


        final CoordinatorLayout.LayoutParams[] layoutParams = {new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT)};
        final int[] left = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin))};
        final int[] top = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_top_margin))};
        final int[] right = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin))};
        final int[] bottom = {getPixelValue(mCtx, bottomSheetBehavior.getPeekHeight())};

//        mWebView.setBackgroundColor(Color.BLACK);
        mWebView.requestFocus(View.FOCUS_DOWN);
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        mWebView.loadUrl(DEFAULT_URL);
        mWebHandler = new WebHandler(this, mWebView, new WebTrackListener() {
            @Override
            public void callbackFromJS(String jsonData) {
                Log.d(TAG, "callbackFromJS : jsonData - " + jsonData);
                try {
                    JSONObject jsonObj = new JSONObject(jsonData);
                    String requestType = jsonObj.getString("request_type");
                    Log.d(TAG, "callbackFromJS : requestType - " + requestType);

                    switch (requestType) {
                        case REQUEST_LOGIN_DEFAULT : // 일반 로그인 요청
                            loginMethod = "local";

                            String email = jsonObj.getString("email");
                            String password = jsonObj.getString("password");
                            boolean rememberMe = jsonObj.getBoolean("remember_me");

                            Log.e("[handleLogin]", " Local Login > rememberMe : " + rememberMe);
                            if (rememberMe) {
                                setPreferencesForRememberMe("true");
                                setPreferencesForEmail(email);
                                setPreferencesForPassword(password);
                            } else {
                                setPreferencesForRememberMe("false");
                                setPreferencesForEmail("");
                                setPreferencesForPassword("");
                            }

                            Gson gson = new Gson();
                            Member member = gson.fromJson(jsonObj.get("user").toString(), Member.class);

                            //Member member = (Member) jsonObj.get("user");
                            Log.e(TAG, "[REQUEST_LOGIN_DEFAULT]" + member.getToken());

                            localLoginUsername = email;
                            localLoginPassword = password;
                            //  로그인 처리
                            Log.e("[handleLogin]", " Local Login > Email / Password : " + email + ", " + password);
//                            toast("로그인 요청중입니다.");

                            // [Fixed it by J] LOCAL-LOGIN HANDLE VER1.
                            String header = cookieManager.getInstance().getCookie(DEFAULT_URL);
                            Log.e("[handleLogin]", " Local Login > Cookie : \n [ \n" + header + "\n]\n");
                            List<HttpCookie> cookies = HttpCookie.parse(header);
                            if(cookies.get(0).getName().equals("JSESSIONID")) {
                                // 쿠키 매니저에도 등록
                                setCookieString(cookies.get(0).getValue());
                            }

                            if(cookies.get(0).getName().equals("SESSION")) {
                                // 쿠키 매니저에도 등록
                                setCookieString(cookies.get(0).getValue());
                            }

                            Log.d(TAG, "All the cookies in a string:" + header);
                            setPreferencesForToken(member.getToken());

                            SystemClock.sleep(1000);

                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                //noinspection deprecation
                                CookieSyncManager.getInstance().sync();
                            } else {
                                // 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
                                cookieManager.getInstance().flush();
                            }

                            Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));



                            getOwnPlaylistItems(true);
                            try {
//                                toast("[로그인] 재생 / 재생목록을 가져옵니다.");
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            loggedInUpdateUI();


                            break;
                        case REQUEST_LOGIN_FACEBOOK: // 페이스북 로그인 요청

//                            toast(getString(R.string.toast_request_login));
                            LoginManager.getInstance().logOut();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            facebookLoginButton.performClick();
                                        }
                                    });
                                }
                            }).start();
                            break;
                        case REQUEST_LOGIN_GOOGLE: // 구글 로그인 요청
                            //  로그인 처리
//                            toast(getString(R.string.toast_request_login));

                            Log.e(TAG, "[signInGoogle] 1-0");
                            signInGoogle();

                            Log.e(TAG, "[signInGoogle] 2-0");
                            break;
                        case REQUEST_LOGIN_KAKAO: // 카카오 로그인 요청
                            //  로그인 처리

//                            toast(getString(R.string.toast_request_login));
                            final Session session = Session.getCurrentSession();
//                            session.addCallback(new kakaoSessionCallback());
                            Log.e(TAG, "[REQUEST_LOGIN_KAKAO] in handleWebview");
                            handleKakaoLogin();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            Log.e(TAG, "[REQUEST_LOGIN_KAKAO] in Thread");

//                                            Session.getCurrentSession().addCallback(new kakaoSessionCallback());
                                            session.open(AuthType.KAKAO_ACCOUNT, MainActivity.this);
//                                            kakaoLoginButton.performClick();
                                        }
                                    });
                                }
                            }).start();

                            break;
                        case REQUEST_LOGIN_NAVER: // 네이버 로그인 요청
                            //  로그인 처리
                            // SETTING.. handleNaverLogin (Must.. init)
//                            toast(getString(R.string.toast_request_login));
                            handleNaverLogin();
                            naverLoginInstance.startOauthLoginActivity(MainActivity.this, naverLoginHandler);
//                            new naverRequestApiTask().execute();
                            break;
                        case REQUEST_LOGOUT:

                            //  SERVER-SETTING -> x
                            Log.e(TAG,"[REQUEST_LOGOUT] 1");
                            if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {

                                Log.e(TAG,"[REQUEST_LOGOUT] isPlaying");
                                if (GlobalApplication.getInstance().getServiceInterface().getMediaPlayer() != null) {

                                    Log.e(TAG,"[REQUEST_LOGOUT] getMediaPlayer");
                                    GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().stop();
                                    GlobalApplication.getInstance().getServiceInterface().getMediaPlayer().release();
                                }
                                GlobalApplication.getInstance().getServiceInterface().removeNotificationPlayer();
                                loadDataEmptyWhenPlayStatus();
                            }

                            toast(getString(R.string.toast_request_logout));
                            setPreferencesForToken("");
                            setPreferencesForEmail("");
                            setPreferencesForPassword("");
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance());
                            sp.edit().remove(PREF_COOKIES).commit();

                            break;

                        case REQUEST_PASSWORD_CHANGE: // 비밀번호 변경 요청
                            String newPassword = jsonObj.getString("new_password");
                            // TODO 비밀번호 변경

                            break;
                        case REQUEST_PLAYLIST_UPDATE: // 재생목록 업데이트 요청
                            //  서버에서 재생목록 불러오기
//                            toast(getString(R.string.toast_request_playlist));
                            Log.e(TAG, "[REQUEST_PLAYLIST_UPDATE] load Playlist in handleWebview");
                            getOwnPlaylistItems(false); // 재생 목록가져오고 바로 플레이

                            bottomSheetBehavior.setHideable(false);
                            //BottomSheetBehavior.STATE_COLLAPSED
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            // 해당 작업을 처리함
                                            bottomSheetBehavior.setState(STATE_COLLAPSED);
                                        }
                                    });
                                }
                            }).start();

                            break;

                        case REQUEST_PLAY_VIDEO: // 영상 재생
                            Log.d(TAG, "[REQUEST_PLAY_VIDEO] > " );

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로모드 고정
//                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //화면 센서 활성화

                            if(GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
                                Log.d(TAG, "[REQUEST_PLAY_VIDEO] > isPlaying o" );
                                setPreferencesForAudioPlayed("true");
                            } else {
                                Log.d(TAG, "[REQUEST_PLAY_VIDEO] > isPlaying x" );
                                setPreferencesForAudioPlayed("false");
                            }

                            if (!GlobalApplication.getInstance().getServiceInterface().getIsFirstPlayed()) {
                                Log.d(TAG, "[REQUEST_PLAY_VIDEO] > isFirstPlayed x" );
                                break;
                            }

                            if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {

                                GlobalApplication.getInstance().getServiceInterface().pause();
                            }
                            break;
                        case REQUEST_PAUSE_VIDEO: // 영상 일시정지

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로모드 고정
//                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //화면 센서 활성화
                            Log.d(TAG, "[REQUEST_PAUSE_VIDEO] > " );

                            if (getPreferencesForAudioPlayed().equals("true")){
                                GlobalApplication.getInstance().getServiceInterface().play();
                                setPreferencesForAudioPlayed("true");
                                if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
                                    setViewsAsPlayStatus();
//                                    updateAppSeekBar();
                                } else {
                                    setViewsAsPauseStatus();
                                }
                            }

                            break;
                        case REQUEST_STOP_VIDEO: // 영상 정지 또는 종료

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로모드 고정
//                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //화면 센서 활성화
                            Log.d(TAG, "[REQUEST_STOP_VIDEO] > " );

                            if (getPreferencesForAudioPlayed().equals("true")){
                                GlobalApplication.getInstance().getServiceInterface().play();
                                setPreferencesForAudioPlayed("true");
                                if (GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
                                    setViewsAsPlayStatus();
//                                    updateAppSeekBar();
                                } else {
                                    setViewsAsPauseStatus();
                                }
                            }

                            break;

                        case REQUEST_URL_SHARE:
                            //request_type :  'REQUEST_URL_SHARE',
                            //sns : 'facebook' 또는 'twitter', 'kakao', 'instagram', 'naver'
                            //url_to_share : 공유할 url
                            String sns = jsonObj.getString("sns");
                            String url_to_share = jsonObj.getString("url_to_share");
                            Log.e(TAG, "[REQUEST_URL_SHARE] > " + sns + ", " + url_to_share);

                            dispatchShareMethod(sns,url_to_share);

                            break;

                        case REQUEST_PLAY_AUDIO_SHOW :
                            Log.e(TAG, " >>> [REQUEST_PLAY_AUDIO_SHOW]");

                            bottomSheetBehavior.setHideable(false);
                            //BottomSheetBehavior.STATE_COLLAPSED
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            // 해당 작업을 처리함
                                            bottomSheetBehavior.setState(STATE_COLLAPSED);

                                            layoutParams[0] = new CoordinatorLayout.LayoutParams(
                                                    CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                                                    CoordinatorLayout.LayoutParams.WRAP_CONTENT);
                                            left[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin));
                                            top[0] = getPixelValue(mCtx,(int)  getResources().getDimension(R.dimen.webview_top_margin));
                                            right[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin));
//                                            bottom[0] = getPixelValue(mCtx, bottomSheetBehavior.getPeekHeight() - 20 );
                                            bottom[0] = bottomSheetBehavior.getPeekHeight();


                                            layoutParams[0].setMargins(left[0], top[0], right[0], bottom[0]);
                                            mainFrameLayout.setLayoutParams(layoutParams[0]);

                                        }
                                    });
                                }
                            }).start();

                            break;

                        case REQUEST_PLAY_AUDIO_HIDE:
                            Log.e(TAG, " >>> [REQUEST_PLAY_AUDIO_HIDE]");

                            bottomSheetBehavior.setHideable(true);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            // 해당 작업을 처리함
                                            bottomSheetBehavior.setState(STATE_HIDDEN);

                                            layoutParams[0] = new CoordinatorLayout.LayoutParams(
                                                    CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                                                    CoordinatorLayout.LayoutParams.WRAP_CONTENT);
                                            left[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin));
                                            top[0] = getPixelValue(mCtx,(int)  getResources().getDimension(R.dimen.webview_top_margin));
                                            right[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin));
                                            bottom[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_bottom_margin));
                                            layoutParams[0].setMargins(left[0], top[0], right[0], bottom[0]);
                                            mainFrameLayout.setLayoutParams(layoutParams[0]);

                                        }
                                    });
                                }
                            }).start();

                            break;

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        mWebView.addJavascriptInterface(mWebHandler, HANDLER_NAME);
    }

    private void goSpecificPage(String url) {

        rlPeekedBottomMenu2.setVisibility(View.GONE);
        rlPlayerBottomMenu.setVisibility(View.GONE);

        // 1. COLLAPSE BOTTOM SHEET
        bottomSheetBehavior.setState(STATE_COLLAPSED);
//                playlistListView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

//                swipeMenuListView.setVisibility(View.INVISIBLE);
        rlListViewHeader.setVisibility(View.INVISIBLE);
        rlListViewHeader2.setVisibility(View.INVISIBLE);
        lExpandedPlayer.setFocusable(false);
        lExpandedPlayer.setClickable(false);

        // 2. LOAD SPECIFIC ALBUM PAGE.;

        String tempUrl = DEFAULT_URL + "/music/album/" + itemsArrayList.get(getPreferencesForLastPlayedTrackNumber()).getMusicAlbumId();
//
//                if (!(GlobalApplication.getInstance().getServiceInterface().getAudioItem() == null)) {
//
//                    tempUrl = DEFAULT_URL + "/music/album/" +
//                            GlobalApplication.getInstance().getServiceInterface().getAudioItem().getMusicAlbumId();
//                }
//
//                Log.e(TAG, "[handleMoreFunction]:::onClick() :: " + tempUrl);
        tempUrl = url;

        mWebView.loadUrl(tempUrl);
    }

    /**
     * *******************************************
     * [END OF HANDLER-OF-WEB-VIEW / AND INTERFACES]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF PLAYLIST / PLAY-ITEM-ASSO]
     * *******************************************
     */


    private void initLoadOnMediaPlayer(ArrayList<MusicTrack> arrayList) {

        loadDataAsPlayStatus(arrayList.get(0));
        GlobalApplication.getInstance().getServiceInterface().setPlayList(arrayList, false, isFirstLoad);
//        GlobalApplication.getInstance().getServiceInterface().play();

        if (isFirstLoad) {
            savePreferencesForPlayerFirstLoad(false);
            isFirstLoad = false;
        }
        GlobalApplication.getInstance().getServiceInterface().setmCurrentPosition(0);

    }

    @SuppressLint("WrongConstant")
    private void setEditableRecyclerView() {
//        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItemClick(new AudioPlaylistRecyclerAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                //클릭시 실행될 함수 작성
            }
        });

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {

                // 1. COLLAPSE BOTTOM SHEET
                bottomSheetBehavior.setState(STATE_COLLAPSED);
//                playlistListView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
//                swipeMenuListView.setVisibility(View.INVISIBLE);
                rlListViewHeader.setVisibility(View.INVISIBLE);
                rlListViewHeader2.setVisibility(View.INVISIBLE);
                lExpandedPlayer.setFocusable(false);
                lExpandedPlayer.setClickable(false);
                // 2. LOAD SPECIFIC ALBUM PAGE.;

                String tempUrl = DEFAULT_URL + "/music/album/" + itemsArrayList.get(position).getMusicAlbumId();

                Log.e(TAG, "[handleMoreFunction]:::onClick() :: " + tempUrl);

                mWebView.loadUrl(tempUrl);
//                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(final int position) {
                toast(getString(R.string.toast_handle_playlist));

                OkHttpClient client = new OkHttpClient();
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//                builder.addInterceptor(loggingInterceptor);
                builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
                builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
                client = builder.build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(DEFAULT_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(client)
                        .build();

                UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

                String ids[] = { itemsArrayList.get(position).getId() };

                final Map<String, String[]> requestBody = new HashMap<>();
                requestBody.put("ids", ids);

                Call<JsonObject> call = api.deleteTracksFromMyPlayer(requestBody);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.e(TAG, "[!!!] [onRightClicked] ::: deleteTracksFromMyPlayer :: response : " + response.body());

                        JsonObject jsonObject = response.body();
                        if (jsonObject.get("status").toString().equals("true")) {

                            mAdapter.items.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                        }

                        if (jsonObject.get("status").toString().equals("false")) {

                            toast(getString(R.string.toast_network_error1));
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

            }
        });

        callback =  new SimpleItemTouchHelperCallback(mAdapter);
        touchHelper = new ItemTouchHelper(callback);
        if(isPlaylistEditMode == true){
            touchHelper.attachToRecyclerView(mRecyclerView);
        } else {
            touchHelper.attachToRecyclerView(null);
        }

    }

    private void connectRecyclerView() {

        //[USAGE]
        mAdapter = new AudioPlaylistRecyclerAdapter(mCtx, MainActivity.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        setEditableRecyclerView();
    }

    public void requestCollectionsList() {

        Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        builder.addInterceptor(loggingInterceptor);
//        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);
        Call<JsonObject> call = api.getCollection();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e(TAG, " > [requestCollectionsList] response : " + response.body());

                if(response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                    if (jsonObject == null) {
                        return;
                    }

                    if (jsonObject.get("status").toString().equals("true")) {

//                    toast(getString(R.string.toast_request_my_list));
//                    Toast.makeText(getApplicationContext(), ,Toast.LENGTH_SHORT);
                        Gson gson= new Gson();
                        JSONObject obj = null;
                        collectionList = new ArrayList<>();
                        try {
                            mArray = new JSONArray(response.body().get("object").toString());

                            for (int i = 0; i < mArray.length(); i ++){
                                try {
                                    obj = mArray.getJSONObject(i);

                                    if (i == 0) {
                                        Log.e(TAG, "[mArray]:::" + obj.toString());
                                    }
                                    Collection collection = gson.fromJson(obj.toString(),Collection.class);

                                    collectionList.add(collection);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            //  Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (collectionList.size() == 0) { // && defaultArrayList.size != 0
                            Log.e(TAG, "[collectionList]:::collectionList.size == 0");
                        }

                        Log.e(TAG, "[collectionList] adapter");

                        ArrayList<Collection> collectionArrayList = new ArrayList<>();
                        collectionArrayList.clear();
                        collectionArrayList.addAll(collectionList);

                        customDiaglogListViewAdapter.setCollectionlist(collectionArrayList);
                        customDialogListView.setAdapter(customDiaglogListViewAdapter);
                        customDiaglogListViewAdapter.notifyDataSetChanged();

                    }

                    if (jsonObject.get("status").toString().equals("false")) {
//                        toast(getString(R.string.toast_network_error1));
//                    Toast.makeText(getApplicationContext(), "나의 리스트를 가져올 수 없습니다.",Toast.LENGTH_SHORT);
                    }

                } else {

//                    toast(getString(R.string.toast_network_error1));
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    public void requestCollectionCreate(String subject) {

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        builder.addInterceptor(loggingInterceptor);
//        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("subject", "" + subject);
        Log.e(TAG, "[createCollection] subject : " + requestBody.get("subject"));


        Call<JsonObject> call = api.createCollection(requestBody);
        //params의 예 {"subject":"내가 담은 리스트 제목"}

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                if(response.isSuccessful()) {

                    Log.e(TAG, " > [createCollection] response : " + response.body());
                    JsonObject jsonObject = response.body();

                    if (jsonObject.get("status").toString().equals("false")) {

                        toast(getString(R.string.toast_network_error1));
//                    Toast.makeText(getApplicationContext(), "생성이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }

                    if (jsonObject.get("status").toString().equals("true")) {

                        Gson gson= new Gson();
                        JSONObject obj = null;
//                    obj = mArray.getJSONObject();

                        Collection collection = gson.fromJson(jsonObject.get("object").getAsJsonObject().toString(),Collection.class);

                        Log.e(TAG, "[collection] created : " + collection.getSubject()  + " , collection : " + collection.getId());

                        collectionList.add(collection);
                        customDiaglogListViewAdapter.addCollection(collection);
                        customDiaglogListViewAdapter.notifyDataSetChanged();
                        customCreateDialog.dismiss();

                    }

                } else {

                    toast(getString(R.string.toast_network_error1));
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    public void requestCollectionAddTrackByID(String collection_id) {

//        Log.e(TAG, "[checkToken]::: getPreferencesForToken() " + getPreferencesForToken());
        Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        builder.addInterceptor(loggingInterceptor);
//        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

        Log.e(TAG, "[requestCollectionAddTrackByID]::: ids >> " + Arrays.toString(selectedIdsFromPlaylist));

        final Map<String, Long[]> requestBody = new HashMap<>();

        if (isPlaylistEditMode == true) {
            requestBody.put("ids", selectedIdsFromPlaylist);
        } else {


            ArrayList<Long> idArray = new ArrayList<Long>();
            idArray.clear();
            if(collectionMode.equals("PLAYER")) {
                idArray.add(currentPlayingId);
            }
            if(collectionMode.equals("PLAYLIST")) {
                idArray.add(collectSingleId);
            }
            Long[] ids = new Long[idArray.size()];
            idArray.toArray(ids);

            Log.e("[TEST]", "ids : " + Arrays.toString(ids));
            requestBody.put("ids", ids);
        }

        Call<JsonObject> call = api.addTracksOnCollectionByID(collection_id, requestBody);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                if(response.isSuccessful()) {

                    Log.e(TAG, "[addTracksOnCollectionByID]::  response : " + response.body());
                    JsonObject jsonObject = response.body();

                    if (jsonObject.get("status").toString().equals("false")) {
                        Log.e(TAG, "[addTracksOnCollectionByID]:: false " + jsonObject.get("object").toString());
                        toast(getString(R.string.toast_network_error1));
//                    Toast.makeText(getApplicationContext(), "플레이리스트에 담기 실패했습니다.",Toast.LENGTH_SHORT).show();

                    }


                    if (jsonObject.get("status").toString().equals("true")) {
                        Log.e(TAG, "[addTracksOnCollectionByID]:: true" + jsonObject.get("object").toString());
//                    toast(getString(R.string.toast_network_error1));
                        toast(getString(R.string.toast_success_collect));
//                    Toast.makeText(getApplicationContext(), "해당 플레이리스트에 담았습니다.",Toast.LENGTH_SHORT).show();

                        listViewDialog.dismiss();

                    }

                } else {

                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    public static int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    private void setPlaylistEditable(boolean editable, boolean willUpdate) {

        requestCollectionsList();
        if (editable) {
            //
//            bottomSheetBehavior.setState(STATE_HIDDEN);

            btnEditHeader.setBackgroundResource(R.color.playlist_edit_color_2);
            btnEditHeaderText.setText("완료");

            rlPeekedBottomMenu2.setVisibility(View.GONE);

            // ATTACH_EDIT_DRAGGABLE_RECYCLER_VIEW
            Log.e(TAG, "[handleEditPlaylist]:::btnEditHeader ATTACH_EDIT_DRAGGABLE_RECYCLER_VIEW");
            mAdapter.setListItemClickable(false);
            touchHelper.attachToRecyclerView(mRecyclerView);

            tempArrayList = itemsArrayList;
            isPlaylistEditMode = true;
            setPreferencesForEditMode("true");



        } else {
            btnEditHeader.setBackgroundResource(R.color.playlist_edit_color_1);
            btnEditHeaderText.setText("편집");
            rlPeekedBottomMenu.setVisibility(View.GONE);
            setPreferencesForEditMode("false");

            touchHelper.attachToRecyclerView(null);

            if(willUpdate) {
                if (!mAdapter.items.equals(itemsArrayList)) {

                    toast(getString(R.string.toast_handle_playlist));
                    requestUpdatePlaylist();
                } else {
                    Log.e(TAG, "[handleEditPlaylist]:::requestUpdatePlaylist:: [x] 변경사항이 없습니다.");

                    mAdapter.notifyDataSetChanged();
                }
            } else {
                if (!mAdapter.items.equals(itemsArrayList)) {
                    mAdapter.updateList(itemsArrayList);
                    mAdapter.notifyDataSetChanged();

                }
            }

            mAdapter.setListItemClickable(true);
            isPlaylistEditMode = false;
        }


    }

    private void handleEditPlaylist() {
        btnEditHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaylistEditable(!isPlaylistEditMode, true);
            }
        });

        PeekedBottomMenuGrid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                collectTrack(null, 0);
            }
        });

        PeekedBottomMenuGrid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                deleteTrack(null, 0 , null);

            }
        });

        btnTabDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectTrack(null, 0);

            }
        });

        btnTabDownText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collectTrack(null, 0);

            }
        });

        customDeleteConfirmDialog = new CustomDeleteConfirmDialog(mCtx);

        btnTabDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                deleteTrack(null, 0 , null);
                customDeleteConfirmDialog.show();

            }
        });

        btnTabDeleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                deleteTrack(null, 0, null);
                customDeleteConfirmDialog.show();
            }
        });

    }

    public void requestPlayerAddPlayCount(String id) {


        Log.e(TAG, "[requestPlayerAddPlayCount]::function called");


        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor);
//        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();


        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);


        Call<JsonObject> call = api.addPlayCountOnTrackByID(id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    Log.e(TAG, "[requestPlayerAddPlayCount] > response :  " + response.body());
                } else {

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void requestUpdatePlaylist() {

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);


        long[] ids = new long[mAdapter.items.size()];
//        String[] ids = new String[mAdapter.items.size()];

        for (int i=0; i<mAdapter.items.size();i++) {
            ids[i] = Long.parseLong(mAdapter.items.get(i).getId());
        }

        String str = Arrays.toString(ids);
        Log.e(TAG, "[requestUpdatePlaylist]::: ids >> " + str);

        final Map<String, long[]> requestBody = new HashMap<>();
        requestBody.put("ids", ids);

        Call<JsonObject> call = api.updatePlayerPlaylist(requestBody);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                if(response.isSuccessful()) {
                    Log.e(TAG, "[requestUpdatePlaylist]::onResponse()::response >> " + response.body());

                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status").toString().equals("true")) {

                        toast(getString(R.string.toast_handle_playlist));
                        Gson gson= new Gson();
                        JSONObject obj = null;
                        tempList = new ArrayList<>();
                        try {
                            mArray = new JSONArray(response.body().get("object").toString());

                            for (int i = 0; i < mArray.length(); i ++){
                                try {
                                    obj = mArray.getJSONObject(i);

                                    if (i == 0) {
                                        Log.e(TAG, "[tempList]:::" + obj.toString());
                                    }
                                    MusicTrack musicTrack= gson.fromJson(obj.toString(),MusicTrack.class);

                                    tempList.add(musicTrack);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            //  Auto-generated catch block
                            e.printStackTrace();
                        }


                        tempArrayList.clear();
                        tempArrayList.addAll(tempList);
//
//                    String[] itemNames = new String[tempList.size()];
//
//                    for (int i = 0; i<tempList.size();i++) {
//                        itemNames[i] = tempList.get(i).getSubject();
//                    }

                        itemsArrayList = tempArrayList;

                        if (tempArrayList.size() == 0) { // && defaultArrayList.size != 0
                            loadDataEmptyWhenPlayStatus();

                            Log.e(TAG, "[itemsArrayList]:::tempArrayList.size == 0, SO AddALL Default.");
                            itemsArrayList.clear();
                            itemsArrayList.addAll(defaultArrayList);
                        }

                        Log.e(TAG, "[tempArrayList] PLAY TEST ");

                        mAdapter.updateList(tempList);
                        mAdapter.notifyDataSetChanged();
//                    Log.e(TAG, "[requestUpdatePlaylist]::onResponse()::itemsArrayList&getAudioItem >> nowPlaying getSubject : " + GlobalApplication.getInstance().getServiceInterface().getAudioItem().getSubject());
                        if (itemsArrayList.contains(GlobalApplication.getInstance().getServiceInterface().getAudioItem() )){
//                        Log.e(TAG, "[requestUpdatePlaylist]::onResponse()::itemsArrayList&getAudioItem >> nowPlaying exists : " + GlobalApplication.getInstance().getServiceInterface().getAudioItem().getSubject());
                            Log.e(TAG, "[requestUpdatePlaylist]::onResponse()::itemsArrayList&getAudioItem >> nowPlaying new index : " + itemsArrayList.indexOf(GlobalApplication.getInstance().getServiceInterface().getAudioItem()));

                            GlobalApplication.getInstance().getServiceInterface().setPlayList(itemsArrayList, true, isFirstLoad);
                            GlobalApplication.getInstance().getServiceInterface().setmCurrentPosition(itemsArrayList.indexOf(GlobalApplication.getInstance().getServiceInterface().getAudioItem()));

                        } else {
                            Log.e(TAG, "[requestUpdatePlaylist]::onResponse()::itemsArrayList&getAudioItem >> nowPlaying doesn't exists");

                            if (!GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
                                if (!itemsArrayList.isEmpty()) {
                                    initLoadOnMediaPlayer(itemsArrayList);
                                    playTrack(itemsArrayList.get(0), 0, true, false); // no Play
                                } else {
                                    toast(getString(R.string.toast_do_not_get_play_item));
                                }
                            } else {
                                if (!itemsArrayList.isEmpty()) {
                                    GlobalApplication.getInstance().getServiceInterface().pause();
                                    initLoadOnMediaPlayer(itemsArrayList);
//                    GlobalApplication.getInstance().getServiceInterface().play(0);
                                    playTrack(itemsArrayList.get(0), 0, false, false);
                                } else {
                                    toast(getString(R.string.toast_do_not_get_play_item));
                                }
                            }
                        }

                    }

                    if (jsonObject.get("status").toString().equals("false")) {
                        toast(getString(R.string.toast_network_error1));
                        mAdapter.updateList(itemsArrayList);
                        mAdapter.notifyDataSetChanged();


                    }
                } else {
                    toast(getString(R.string.toast_network_error1));
                }



            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                // currently USE mAdapter
                mAdapter.updateList(itemsArrayList);
                mAdapter.notifyDataSetChanged();
            }
        });

    }


    private void requestHeart(final String id, final boolean isRow, final int rowNumber) {

        Log.e(TAG, "[reqTrackLikeOrNot]:::id : " + id);

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//         builder.addInterceptor(loggingInterceptor);
//         builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", "" + getPreferencesForToken());
//         Log.e(TAG, "[PI] token : " + requestBody.get("token"));

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);
        Call<JsonObject> call = api.sendLikeOrNotStatus(id, requestBody);
        final Call<MusicTrack> callIn = api.getTrackById(id);


        final MusicTrack[] heartTrack = new MusicTrack[1];

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    Log.e(TAG, "[reqTrackLikeOrNot]:::response.body()::" + response.body());

                    final JsonObject jsonObject = response.body();
                    //getTrackById
                    if (jsonObject.get("status").toString().equals("true")) {

                        callIn.enqueue(new Callback<MusicTrack>() {
                            @Override
                            public void onResponse(Call<MusicTrack> callin, Response<MusicTrack> responseIn) {

                                if(responseIn.isSuccessful()) {
                                    MusicTrack tempTrack = responseIn.body();


                                    Log.e(TAG, "[reqTrackLikeOrNot(callIn)] ::::::  tempTrack.getHeartCnt() ::" + tempTrack.getHeartCnt());

                                    heartTrack[0] = tempTrack;


                                    if (!isRow) {

                                        itemsArrayList.get(getPreferencesForLastPlayedTrackNumber()).setHeartCnt(tempTrack.getHeartCnt());
                                        itemsArrayList.get(getPreferencesForLastPlayedTrackNumber()).setLiked(tempTrack.getLiked());

                                        Log.e(TAG, "[reqTrackLikeOrNot(callIn)] ::::::  Not Row ::" + tempTrack.getHeartCnt());
                                        textExpanded05.setText("" + tempTrack.getHeartCnt()); // 좋아요 갯수

                                        if(jsonObject.get("message").toString().equals("\"true\"")) {
                                            btnHeart.setBackground(getResources().getDrawable(R.drawable.btn_like_on));
                                            rlPlayerBottomMenu_Row4_ImageView1.setBackground(getResources().getDrawable(R.drawable.btn_like_on));
                                            rlPlayerBottomMenu_Row4_TextView1.setText("좋아요 취소");

//                             textExpanded05.setText("" + heartTrack[0].getHeartCnt()); // 좋아요 갯수
                                        }

                                        if(jsonObject.get("message").toString().equals("\"false\"")) {

                                            btnHeart.setBackground(getResources().getDrawable(R.drawable.btn_like_off));
                                            rlPlayerBottomMenu_Row4_ImageView1.setBackground(getResources().getDrawable(R.drawable.btn_like_off));
                                            rlPlayerBottomMenu_Row4_TextView1.setText("좋아요");

//                             textExpanded05.setText("" + heartTrack[0].getHeartCnt()); // 좋아요 갯수
                                        }
                                    }

                                    if (isRow) {


                                        itemsArrayList.get(rowNumber).setHeartCnt(tempTrack.getHeartCnt());
                                        itemsArrayList.get(rowNumber).setLiked(tempTrack.getLiked());

                                        Log.e(TAG, "[reqTrackLikeOrNot(callIn)] ::::::  is Row :: id : " + id + ", currentPlayingId : " + currentPlayingId);

                                        if (id.equals("" + currentPlayingId)) {
//                                    Log.e(TAG, "[reqTrackLikeOrNot(callIn)] ::::::  equal currentPlayingId :: " + tempTrack.getHeartCnt());
                                            if(jsonObject.get("message").toString().equals("\"true\"")) {
                                                btnHeart.setBackground(getResources().getDrawable(R.drawable.btn_like_on));
                                                textExpanded05.setText("" + tempTrack.getHeartCnt()); // 좋아요 갯수
                                            }

                                            if(jsonObject.get("message").toString().equals("\"false\"")) {
                                                btnHeart.setBackground(getResources().getDrawable(R.drawable.btn_like_off));
                                                textExpanded05.setText("" + tempTrack.getHeartCnt()); // 좋아요 갯수
                                            }
                                        }


                                        if(jsonObject.get("message").toString().equals("\"true\"")) {
                                            rlPeekedBottomMenu2_Row4_ImageView1.setBackground(getResources().getDrawable(R.drawable.btn_like_on));
                                            rlPeekedBottomMenu2_Row4_TextView1.setText("좋아요 취소");
                                        }

                                        if(jsonObject.get("message").toString().equals("\"false\"")) {

                                            rlPeekedBottomMenu2_Row4_ImageView1.setBackground(getResources().getDrawable(R.drawable.btn_like_off));
                                            rlPeekedBottomMenu2_Row4_TextView1.setText("좋아요");
                                        }

                                    }
                                } else {

                                    toast(getString(R.string.toast_network_error1));
                                }
                            }

                            @Override
                            public void onFailure(Call<MusicTrack> callin, Throwable t) {

                            }
                        });
                    }
                    if (jsonObject.get("status").toString().equals("false")) {
                        toast(getString(R.string.toast_network_error2));
                    }

                } else {
                    toast(getString(R.string.toast_network_error1));
                }


//                 JsonObject item = response.body();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    private void handleLikeTrack() {

        btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  null check. RE-TEST
                String tempId = itemsArrayList.get(getPreferencesForLastPlayedTrackNumber()).getId();

                if (!(GlobalApplication.getInstance().getServiceInterface().getAudioItem() == null)) {

                    tempId = GlobalApplication.getInstance().getServiceInterface().getAudioItem().getId();
                }

                Log.e(TAG, "[handleLikeTrack]:::tempId::" + tempId);

                requestHeart(tempId, false, -1);
            }
        });
    }

    private void getOwnPlaylistItems(final boolean noPlay) {

        Log.e(TAG, "[PI] called,  noPlay : " + noPlay);
        Log.e(TAG, "[1]");

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);
//        Token token = new Token(getPreferencesForToken());

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", "" + getPreferencesForToken());
        Log.e(TAG, "[PI] token : " + requestBody.get("token"));

        Log.e(TAG, "[2]");
        Call<JsonObject> call = api.getPlayerPlaylist(requestBody);
//        {"token":"QhFtGA5vL2bydEAYo+qMsytLz9QDb/vWjdfevdXktsc="}

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    Gson gson= new Gson();
                    JSONObject obj = null;
                    tempList = new ArrayList<>();

                    if (response.body().get("object") == null) {
                        return;
                    }

                    try {

                        mArray = new JSONArray(response.body().get("object").toString());

                        for (int i = 0; i < mArray.length(); i ++){
                            try {
                                obj = mArray.getJSONObject(i);

                                if (i == 0) {
                                    // DO NOT DELETE BELOW-LINE
                                    Log.e(TAG, "[tempList]:::" + obj.toString());
                                }
                                MusicTrack musicTrack= gson.fromJson(obj.toString(), MusicTrack.class);

                                tempList.add(musicTrack);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "[2-1]");
                    tempArrayList.clear();
                    tempArrayList.addAll(tempList);
                    itemsArrayList = tempArrayList;

                    Log.e(TAG, "[2-2]");
                    savePlaylist(itemsArrayList);

                    Log.e(TAG, "[2-3]");
                    if (tempArrayList.size() == 0) { // && defaultArrayList.size != 0
//                    Log.e(TAG, "[itemsArrayList]:::tempArrayList.size == 0, SO AddALL Default.");
                        itemsArrayList.clear();
                        itemsArrayList.addAll(defaultArrayList);
                    }

                    Log.e(TAG, "[2-4]");
                    // currently USE mAdapter
                    mAdapter.updateList(tempList);
                    mAdapter.notifyDataSetChanged();

                    //  CONNECT mSTOMPCLIENT TEST
                    if (mStompClient !=null ) {
                        mStompClient.disconnect();
                    }
                    Log.e(TAG, "[2-5]");
                    connectStomp();

                    Log.e(TAG, "[2-6]");
                    if (isFirstLoad) {

                        Log.e(TAG, "[2-7]");
                        if (!itemsArrayList.isEmpty()) {
                            Log.e(TAG, "[2-8]");
                            initLoadOnMediaPlayer(itemsArrayList);
                            Log.e(TAG, "[2-9]");
                            playTrack(itemsArrayList.get(0), 0, noPlay, true);
                            Log.e(TAG, "[2-10]");
                        } else {
//                        toast("음원을 가져올 수 없습니다.");
                        }
//                    isFirstLoad = false;
                        Log.e(TAG, "[2-11]");
                    } else {
                        // TODO!
                        Log.e(TAG, "[2-12]");
                        if (!GlobalApplication.getInstance().getServiceInterface().isPlaying()) {
                            if (!itemsArrayList.isEmpty()) {
                                Log.e(TAG, "[2-13]");
                                initLoadOnMediaPlayer(itemsArrayList);
                                Log.e(TAG, "[2-14]");
                                playTrack(itemsArrayList.get(0), 0, noPlay, false);
                                Log.e(TAG, "[2-15]");
                            } else {

//                            toast("음원을 가져올 수 없습니다.");
                            }
                        } else {
                            Log.e(TAG, "[2-16]");
                            if (!itemsArrayList.isEmpty()) {
                                Log.e(TAG, "[2-17]");
                                GlobalApplication.getInstance().getServiceInterface().pause();
                                Log.e(TAG, "[2-18]");
                                initLoadOnMediaPlayer(itemsArrayList);
                                Log.e(TAG, "[2-19]");
//                    GlobalApplication.getInstance().getServiceInterface().play(0);
                                playTrack(itemsArrayList.get(0), 0, noPlay, false);
                                Log.e(TAG, "[2-20]");
                            } else {

//                            toast("음원을 가져올 수 없습니다.");
                            }
                        }
                    }

                } else {
                    toast(getString(R.string.toast_network_error1));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    public void setBtnPlayClicked() {
        isClicked = true;
    }

    @Override
    public void playTrack(MusicTrack param, int position, boolean noPlay, boolean isFirstLoad) {

        Log.e(TAG, "[playTrack] ::: (noPlay) : " + noPlay );

        // IF USE INTENT, Other Activity.
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        MusicTrack track = param;
        intent.putExtra(KEY_TRACK_ADMIN_URL, track.getAdminUrl());
        intent.putExtra(KEY_TRACK_ARTIST_ID, track.getArtistId());
        intent.putExtra(KEY_TRACK_ARTIST_NICK, track.getArtistNick());
        intent.putExtra(KEY_TRACK_COVER_IMAGE_URL, track.getCoverImageUrl());
        intent.putExtra(KEY_TRACK_DURATION, track.getDuration());
        intent.putExtra(KEY_TRACK_FILE_NAME_URL, track.getFilenameUrl());
        intent.putExtra(KEY_TRACK_ID, track.getId());
        intent.putExtra(KEY_TRACK_LIKED, track.getLiked());
        intent.putExtra(KEY_TRACK_MUSIC_ALBUM_ID, track.getMusicAlbumId());
        intent.putExtra(KEY_TRACK_REJECTED_REASON, track.getRejectedReason());
        intent.putExtra(KEY_TRACK_SUBJECT, track.getSubject());
        intent.putExtra(KEY_TRACK_THEME_NAMES, track.getThemeNames());
        intent.putExtra(KEY_TRACK_TITLE_TRACK, track.getTitleTrack());
        intent.putExtra(KEY_TRACK_URL, track.getUrl());

        GlobalApplication.getInstance().getServiceInterface().setPlayList(itemsArrayList, false, isFirstLoad);

        savePreferencesForLastPlayedTrackNumber(position);
        savePreferencesForLastPlayedTrackId(track.getId());

        Log.e(TAG, "[playTrack] :: (=====================================) ");
        Log.e(TAG, "[playTrack] :: (getSubject) " + track.getSubject());
        Log.e(TAG, "[playTrack] :: (getFilenameUrl) " + track.getFilenameUrl());
        Log.e(TAG, "[playTrack] :: (getLiked) " + track.getLiked());
        Log.e(TAG, "[playTrack] :: (getHeartCnt) " + track.getHeartCnt());
        Log.e(TAG, "[playTrack] :: (=====================================) ");

        sendSocketPlayTime(0, track.getId());

        loadDataAsPlayStatus(track);
//
        if(!noPlay) {
            GlobalApplication.getInstance().getServiceInterface().play(position);
        }
    }



    /**
     * *******************************************
     * [END OF PLAYLIST / PLAY-ITEM-ASSO]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF CLICK-LISTENERS / HANDLER]
     * *******************************************
     */

    private void handlePlaylistBottomMenu() {

//        rlPeekedBottomMenu2_Row3.setVisibility(View.GONE);

        rlPeekedBottomMenu2_Row0_ImageView1_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rlPeekedBottomMenu2.setVisibility(View.GONE);
            }
        });

        rlPeekedBottomMenu2_Row0_ImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rlPeekedBottomMenu2.setVisibility(View.GONE);
            }
        });
    }

    private void handlePlayerBottomMenu() {
//        rlPlayerBottomMenu_Row3.setVisibility(View.GONE);

        rlPlayerBottomMenu_Row0_ImageView1_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlPlayerBottomMenu.setVisibility(View.GONE);
            }
        });


        rlPlayerBottomMenu_Row0_ImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlPlayerBottomMenu.setVisibility(View.GONE);
            }
        });


        rlPlayerBottomMenu_Row5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 뷰 호출
                view = getLayoutInflater().inflate(R.layout.custom_dialog_listview, null);
                // 해당 뷰에 리스트뷰 호출
                customDialogListView = (ListView)view.findViewById(R.id.listview);

                customDialogButton = (Button) view.findViewById(R.id.listview_confirm);

                customDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast(getString(R.string.toast_input_playlist_name));
//                         Toast.makeText(getApplicationContext(), "만들 리스트의 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, " > [btnTabDownButton] customDialogButton willbe CreatedList ");

                        customCreateDialog.show();

                    }
                });

                // 리스트뷰에 어뎁터 설정
                customDialogListView.setAdapter(customDiaglogListViewAdapter);
                // 다이얼로그 생성
                listViewDialogBuilder = new AlertDialog.Builder(mCtx);

                listViewDialog = listViewDialogBuilder.create();
                // 리스트뷰 설정된 레이아웃
                listViewDialog.setView(view);
                // 확인버튼
//                listViewDialogBuilder.setPositiveButton("내 리스트 만들기", null);

                listViewDialog.show();
            }
        });

    }

    private void handleMoreFunction() {

        handlePlaylistBottomMenu();
        handlePlayerBottomMenu();
        btnMore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rlPlayerBottomMenu.setVisibility(View.VISIBLE);
                return false;
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlPlayerBottomMenu.setVisibility(View.VISIBLE);
//                rlPlayerBottomMenu_Row0_TextView1.setText();

            }
        });


        btnMoreLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rlPlayerBottomMenu.setVisibility(View.VISIBLE);
                return false;
            }
        });

        btnMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlPlayerBottomMenu.setVisibility(View.VISIBLE);
//                rlPlayerBottomMenu_Row0_TextView1.setText();

            }
        });

    }

    private void handleShuffleMode() {
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPreferencesForShuffle()) {
                    Log.e(TAG, "[Shuffle] before: " + getPreferencesForShuffle());
                    btnShuffle.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_random_off));
                    GlobalApplication.getInstance().getServiceInterface().shuffleOnOff();
                    Log.e(TAG, "[Shuffle] after: " + getPreferencesForShuffle());
//                    savePreferencesForShuffle(false);
                } else {

                    Log.e(TAG, "[Shuffle] before: " + getPreferencesForShuffle());
                    btnShuffle.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_random));
                    GlobalApplication.getInstance().getServiceInterface().shuffleOnOff();
                    Log.e(TAG, "[Shuffle] after: " + getPreferencesForShuffle());
//                    savePreferencesForShuffle(true);
                }
            }
        });
    }

    private void handleRepeatMode() {
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getPreferencesForRepeat().equals("NOR")) {
                    btnRepeat.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_rotation_on));
                    savePreferencesForRepeat("ALL");
                } else {
                    btnRepeat.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_rotation));
                    savePreferencesForRepeat("NOR");
                }

            }
        });
    }


    /**
     * *******************************************
     * [END OF CLICK-LISTENERS / HANDLER]
     * *******************************************
     */


    /**
     * **************************************************************************************
     * **************************************************************************************
     */


    /**
     * *******************************************
     * [TOP OF SORT-OF-OVERRIDING-FUNCTIONS]
     * *******************************************
     */

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        rlPeekedBottomMenu.setVisibility(View.GONE);
        rlPeekedBottomMenu2.setVisibility(View.GONE);
        rlPlayerBottomMenu.setVisibility(View.GONE);


        if (rlPeekedBottomMenu.getVisibility() == View.GONE) {

            btnPeekPlay.setClickable(true);
            btnPeekNext.setClickable(true);
            btnPeekPrev.setClickable(true);
            btnPeekPlaylist.setClickable(true);
        }

        if (isPlaylistEditMode == true) {

            Log.e(TAG, "[5]");
            toast(getString(R.string.toast_cancel_edit_playlist));
//            Toast.makeText(mCtx, "플레이리스트 편집을 취소합니다.", Toast.LENGTH_SHORT).show();
            setPlaylistEditable(false, false);

            btnPeekPlay.setClickable(true);
            btnPeekNext.setClickable(true);
            btnPeekPrev.setClickable(true);
            btnPeekPlaylist.setClickable(true);

            mAdapter.notifyDataSetChanged();
        }

        if (mRecyclerView.getVisibility() == View.VISIBLE) {
//            playlistListView.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
//            swipeMenuListView.setVisibility(View.INVISIBLE);
            rlListViewHeader.setVisibility(View.INVISIBLE);
            rlListViewHeader2.setVisibility(View.INVISIBLE);
            lExpandedPlayer.setFocusable(false);
            lExpandedPlayer.setClickable(false);
        } else {

            if (bottomSheetBehavior.getState() != STATE_COLLAPSED) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);

            } else {

                if (popupIndex > 0) {
                    //# 새창이 있으면
                    if (popupView.canGoBack()) {
                        popupView.goBack();
                    } else {
                        closePopup();
                    }
                } else if (mWebView.canGoBack()) {
                    //# 새창이 없고, 뒤로갈 페이지가 있다면
                    mWebView.goBack();
                }
//                else {
//                    //# 새창이 없고, 뒤로갈 페이지도 없다면
//                    backPressCloseHandler.onAllBackPressed();
//                }

                else {
                    long tempTime = System.currentTimeMillis();
                    long intervalTime = tempTime - backPressedTime;

                    if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                        super.onBackPressed();
                    } else {
                        backPressedTime = tempTime;

                        Log.e(TAG, "[6]");
                        toast(getString(R.string.toast_touch_again_will_finish));
                    }

                }

            }
        }

    }

    @Override
    public void collectTrack(MusicTrack track, int position) {


    }

    @Override
    public void deleteTrack(MusicTrack track, int position, Long[] ids) {
        // 편집 버튼을 누르면 삭제 완료되도록.
        mAdapter.onItemDismiss(position);

        Log.e(TAG, "[3]");
        toast(getString(R.string.toast_please_save_playlist));

    }

    @Override
    public void retrieveTracks(MusicTrack param, int position, Long[] ids) {
        Log.e(TAG, "[retrieveTracks] ::: (position) : " + position );

        // IF USE INTENT, Other Activity.
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        MusicTrack track = param;
        intent.putExtra(KEY_TRACK_ADMIN_URL, track.getAdminUrl());
        intent.putExtra(KEY_TRACK_ARTIST_ID, track.getArtistId());
        intent.putExtra(KEY_TRACK_ARTIST_NICK, track.getArtistNick());
        intent.putExtra(KEY_TRACK_COVER_IMAGE_URL, track.getCoverImageUrl());
        intent.putExtra(KEY_TRACK_DURATION, track.getDuration());
        intent.putExtra(KEY_TRACK_FILE_NAME_URL, track.getFilenameUrl());
        intent.putExtra(KEY_TRACK_ID, track.getId());
        intent.putExtra(KEY_TRACK_LIKED, track.getLiked());
        intent.putExtra(KEY_TRACK_MUSIC_ALBUM_ID, track.getMusicAlbumId());
        intent.putExtra(KEY_TRACK_REJECTED_REASON, track.getRejectedReason());
        intent.putExtra(KEY_TRACK_SUBJECT, track.getSubject());
        intent.putExtra(KEY_TRACK_THEME_NAMES, track.getThemeNames());
        intent.putExtra(KEY_TRACK_TITLE_TRACK, track.getTitleTrack());
        intent.putExtra(KEY_TRACK_URL, track.getUrl());

        bottomSheetBehavior.setState(STATE_COLLAPSED);

        if (ids.length == 0 ) {
            rlPeekedBottomMenu.setVisibility(View.GONE);
            btnPeekPlay.setClickable(true);
            btnPeekNext.setClickable(true);
            btnPeekPrev.setClickable(true);
            btnPeekPlaylist.setClickable(true);

        } else {
            rlPeekedBottomMenu.setVisibility(View.VISIBLE);
            btnPeekPlay.setClickable(false);
            btnPeekNext.setClickable(false);
            btnPeekPrev.setClickable(false);
            btnPeekPlaylist.setClickable(false);

            selectedIdsFromPlaylist = ids;
        }

        Log.e(TAG, "[retrieveTracks] > selectedIdsFromPlaylist : " + Arrays.toString(selectedIdsFromPlaylist));


        lExpandedPlayer.setFocusable(false);
        lExpandedPlayer.setClickable(false);
    }

    @Override
    public void moreTrack(MusicTrack param, final int position) {

        Log.e(TAG, "[moreTrack] ::: (position) : " + position );

        // IF USE INTENT, Other Activity.
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        final MusicTrack track = param;
        intent.putExtra(KEY_TRACK_ADMIN_URL, track.getAdminUrl());
        intent.putExtra(KEY_TRACK_ARTIST_ID, track.getArtistId());
        intent.putExtra(KEY_TRACK_ARTIST_NICK, track.getArtistNick());
        intent.putExtra(KEY_TRACK_COVER_IMAGE_URL, track.getCoverImageUrl());
        intent.putExtra(KEY_TRACK_DURATION, track.getDuration());
        intent.putExtra(KEY_TRACK_FILE_NAME_URL, track.getFilenameUrl());
        intent.putExtra(KEY_TRACK_ID, track.getId());
        intent.putExtra(KEY_TRACK_LIKED, track.getLiked());
        intent.putExtra(KEY_TRACK_MUSIC_ALBUM_ID, track.getMusicAlbumId());
        intent.putExtra(KEY_TRACK_REJECTED_REASON, track.getRejectedReason());
        intent.putExtra(KEY_TRACK_SUBJECT, track.getSubject());
        intent.putExtra(KEY_TRACK_THEME_NAMES, track.getThemeNames());
        intent.putExtra(KEY_TRACK_TITLE_TRACK, track.getTitleTrack());
        intent.putExtra(KEY_TRACK_URL, track.getUrl());

        rlPeekedBottomMenu2.setVisibility(View.VISIBLE);
        rlPeekedBottomMenu2_Row0_TextView1.setText(track.getSubject() + "" + " - " + track.getArtistNick());

        rlPeekedBottomMenu2_Row1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSpecificPage(DEFAULT_URL + "/music" + "/artist" + "/" + "" + track.getArtistId());
            }
        });

        rlPeekedBottomMenu2_Row2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goSpecificPage(DEFAULT_URL + "/music" + "/album" + "/" + "" + track.getMusicAlbumId());
            }
        });

        //  SHARE ::: 공유하기
        rlPeekedBottomMenu2_Row3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UPMShareModal.showShareModal(trackId)
                Log.e(TAG, "[PLAYLIST] : [ShareModal] javascript:callJS('UPMShareModal.showShareModal("+ track.getMusicAlbumId() + "," + track.getId() +")')");

                // UPMShareModal.showShareModal(trackId)
                mWebView.loadUrl("javascript:callJS('UPMShareModal.showShareModal("+ track.getMusicAlbumId() + "," + track.getId() +")')");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript("UPMShareModal.showShareModal("+ track.getMusicAlbumId() + "," + track.getId() +")", null);
                } else {
                    mWebView.loadUrl("javascript:UPMShareModal.showShareModal("+ track.getMusicAlbumId() + "," + track.getId() +")");
                }


                rlPeekedBottomMenu2.setVisibility(View.GONE);
                rlPlayerBottomMenu.setVisibility(View.GONE);

                // 1. COLLAPSE BOTTOM SHEET
                bottomSheetBehavior.setState(STATE_COLLAPSED);
//                playlistListView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);

//                swipeMenuListView.setVisibility(View.INVISIBLE);
                rlListViewHeader.setVisibility(View.INVISIBLE);
                rlListViewHeader2.setVisibility(View.INVISIBLE);
                lExpandedPlayer.setFocusable(false);
                lExpandedPlayer.setClickable(false);


            }
        });



        if(track.getLiked().equals("false")) {
            btnHeart.setBackground(getResources().getDrawable(R.drawable.btn_like_off));
            rlPeekedBottomMenu2_Row4_ImageView1.setBackground(getResources().getDrawable(R.drawable.btn_like_off));
            rlPeekedBottomMenu2_Row4_TextView1.setText("좋아요");
        }

        if(track.getLiked().equals("true")) {
            btnHeart.setBackground(getResources().getDrawable(R.drawable.btn_like_on));
            rlPeekedBottomMenu2_Row4_ImageView1.setBackground(getResources().getDrawable(R.drawable.btn_like_on));
            rlPeekedBottomMenu2_Row4_TextView1.setText("좋아요 취소");
        }


        // 좋아요
        rlPeekedBottomMenu2_Row4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempId = track.getId();
                Log.e(TAG, "[handleLikeTrack]:::tempId::" + tempId);
                requestHeart(tempId, true, position);

            }
        });

        // 담기
        rlPeekedBottomMenu2_Row5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collectionMode = "PLAYLIST";
                collectSingleId = Long.parseLong("" + track.getId());

                // 뷰 호출
                view = getLayoutInflater().inflate(R.layout.custom_dialog_listview, null);
                // 해당 뷰에 리스트뷰 호출
                customDialogListView = (ListView)view.findViewById(R.id.listview);

                customDialogButton = (Button) view.findViewById(R.id.listview_confirm);

                customDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "[4]");
                        toast(getString(R.string.toast_input_playlist_name));
                        Log.e(TAG, " > [btnTabDownButton] customDialogButton willbe CreatedList ");

                        customCreateDialog.show();

                    }
                });

                // 리스트뷰에 어뎁터 설정
                customDialogListView.setAdapter(customDiaglogListViewAdapter);
                // 다이얼로그 생성
                listViewDialogBuilder = new AlertDialog.Builder(mCtx);

                listViewDialog = listViewDialogBuilder.create();
                // 리스트뷰 설정된 레이아웃
                listViewDialog.setView(view);
                // 확인버튼
//                listViewDialogBuilder.setPositiveButton("내 리스트 만들기", null);

                listViewDialog.show();


            }
        });

    }

    @Override
    public void setItemSelected() {
        isItemSelected = true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        seekBar.setSecondaryProgress(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        setViewsAsPauseStatus();
    }


    /**
     * *******************************************
     * [END OF SORT-OF-OVERRIDING-FUNCTIONS]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF SHARE-FUNCTION]
     * *******************************************
     */

    /**
     * URL SHARE....
     * @param method
     * @param url_to_share
     */
    private void dispatchShareMethod(String method, String url_to_share) {

        if(method.equals("facebook")) {
            shareFacebook(url_to_share);
        }

        if(method.equals("twitter")) {
            shareTwitter(url_to_share);
        }

        if(method.equals("kakao")) {
            shareKakaoStory(url_to_share);
        }

        if(method.equals("instagram")) {
            shareInstagram(url_to_share);
        }

        if(method.equals("naver")) {
            shareNaver(url_to_share);
        }

        if(method.equals("upmusic")) {
            shareUpmusic(url_to_share);
        }

    }

    /**
     *
     * @param url_to_share
     */
    private void shareFacebook(String url_to_share) {

        Log.e(TAG, "[shareFacebook] > " + url_to_share);

        ShareDialog shareDialog = new ShareDialog(this);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url_to_share))
                .build();

        shareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                // > /api/point_transaction/checkStatus
                sharePointTransaction("true");
            }
            @Override
            public void onCancel() {
//                sharePointTransaction("false");
            }

            @Override
            public void onError(FacebookException error) {
//                sharePointTransaction("false");
            }
        });

        if(ShareDialog.canShow(ShareLinkContent.class)){
            shareDialog.show(content);
        }


    }

    private void sharePointTransaction(String isSuccessful) {
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor);
//        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("status", "" + isSuccessful); // requestBody

        Call<JsonObject> call = api.sharePointTransaction();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    JsonObject jsonObject = response.body();

                    if (jsonObject == null ) {

                    }

                    if (jsonObject.get("status").toString().equals("false")) {
//                        Log.e(TAG, " > sharePointTransaction : false ");
                    }

                    if (jsonObject.get("status").toString().equals("true")) {
//                        Log.e(TAG, " > sharePointTransaction : true ");
                        toast("페이스북 공유로 포인트 적립 되었습니다.");
                    }
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Log.e(TAG, "[checkSession]::authCheckSession> status : ???  onFailure" );
            }
        });

    }


    /**
     *
     * @param url_to_share
     */
    private void shareTwitter(String url_to_share) {
        Log.e(TAG, "[shareTwitter] > " + url_to_share);
        String strLink = null;

        try {
            strLink = String.format("http://twitter.com/intent/tweet?text=%s"
                    , URLEncoder.encode("공유할 텍스트를 입력하세요.\n\n"+ url_to_share, "utf-8")
            );
        } catch( UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strLink));
        startActivity(intent);

    }

    /**
     *
     * @param url_to_share
     */
    private void shareInstagram(String url_to_share) {
        //  이미지 가져오기
        Toast.makeText(mCtx, getString(R.string.toast_share_instagram),Toast.LENGTH_SHORT).show();
        Log.e(TAG, "[shareInstagram] > " + url_to_share);

        onRequestPermission();
//
        if(!permissionCheck) {
            Log.e(TAG, "[10]");
            Toast.makeText(mCtx, getString(R.string.toast_permission_check),Toast.LENGTH_SHORT).show();
        }

        if(permissionCheck) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            String storage = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "temp.png";
            String folderName = "/upmusic/";
            String fullPath = storage + folderName;

            File filePath;

            try {
                filePath = new File(fullPath);
                if(!filePath.isDirectory()) {
                    filePath.mkdir();
                }
                FileOutputStream fos = new FileOutputStream(fullPath + fileName);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();

            }


            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");

            Uri uri = Uri.fromFile(new File(fullPath, fileName));

            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.putExtra(Intent.EXTRA_TEXT, "공유");// 미지원.
                share.setPackage("com.instagram.android");
                startActivity(share);
            } catch (ActivityNotFoundException ex) {
                Log.e(TAG, "[11]");
                Toast.makeText(mCtx, getString(R.string.toast_share_instagram_error1)
                        ,Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }



    }
    // 블로그
    /**
     *
     * @param url_to_share
     */
    private void shareNaver(String url_to_share) {
        Log.e(TAG, "[shareNaver] > " + url_to_share);

        int version = 1;
        String title = getString(R.string.toast_share_upmusic_title);
        String content = getString(R.string.toast_share_upmusic_text) + "\n" + url_to_share;

        List<String> imageUrls = new ArrayList<>();
//        imageUrls.add("http://logoproject.naver.com/img/img_about.gif");

        imageUrls.clear();

        List<String> videoUrls = new ArrayList<>();
//        videoUrls.add("http://tvcast.naver.com/v/791662");
//        videoUrls.add("http://tvcast.naver.com/v/791660");

        videoUrls.clear();

        List<String> ogTagUrls = new ArrayList<>();
//        ogTagUrls.add("http://blog.naver.com");
        ogTagUrls.clear();
//        ogTagUrls.add(url_to_share);

        List<String> tags = new ArrayList<>();
        tags.add("블로그앱");
        tags.add("scheme");

        new NaverBlog(MainActivity.this).write(version, title, content, imageUrls, videoUrls, ogTagUrls, tags);

    }

    // 업뮤직 피드 : 웹에서 처리
    /**
     *
     * @param url_to_share
     */
    private void shareUpmusic(String url_to_share){
        Log.e(TAG, "[shareUpmusic] > " + url_to_share);
    }

    /**
     *
     * @param url_to_share
     */
    private void shareKakaoLink(String url_to_share) {

        //  카카오톡 유무
        TextTemplate params = TextTemplate.newBuilder(
                getString(R.string.toast_share_upmusic_text),
                LinkObject.newBuilder()
                        .setWebUrl(url_to_share)
                        .setMobileWebUrl(url_to_share)
                        .build()
        )
                .setButtonTitle(getString(R.string.toast_go_direct))
                .build();

        KakaoLinkService.getInstance().send(this, "12345", serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());

                Log.e(TAG, "[12]");
                Toast.makeText(mCtx, getString(R.string.toast_share_kakao_error1),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });



    }

    /**
     *
     * @param url_to_share
     */
    private void shareKakaoStory(String url_to_share) {

        Log.e(TAG, "[13]");
        Toast.makeText(mCtx, getString(R.string.toast_share_kakao_error2)
                ,Toast.LENGTH_SHORT).show();

        String shareBody = "" + url_to_share;
        String appId = getApplicationContext().getPackageName();
        String appName = "MyApp";
        String urlInfo = "{title:'[업뮤직] 좋은 음악 공유해요.',desc:'들어보세요!',imageurl:['url_to_share']}";

        String url = String.format(
                "storylink://posting?post=%s&appid=%s&appver=1.0&apiver=1.0&appname=%s&urlinfo=%s",
                shareBody, appId, appName, urlInfo);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        getApplicationContext().startActivity(intent);

        try {
            getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(mCtx, getString(R.string.toast_share_kakao_error2)
                    ,Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(mCtx, getString(R.string.toast_share_kakao_error2)
                    ,Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private Map<String, String> getServerCallbackArgs() {
        Map<String, String> callbackParameters = new HashMap<>();
        callbackParameters.put("user_id", "1234");
        callbackParameters.put("title", "업뮤직");
        return callbackParameters;
    }

    private ResponseCallback<KakaoLinkResponse> kakaolinkcallback;

    private Map<String, String> serverCallbackArgs = getServerCallbackArgs();

    /**
     * *******************************************
     * [END OF SHARE-FUNCTION]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF ACTIVITYRESULT]
     * *******************************************
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // [Facebook] Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        //
        // [Google] Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // METHOD 2
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);

            // METHOD 1
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                accessToken = account.getIdToken();
                setPreferencesForAccessToken(accessToken);
                loginMethod = "google";
                setPreferencesForLoginMethod(loginMethod);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Log.e(TAG, "[16]");
                toast(getString(R.string.toast_log_in_failed));
                // [START_EXCLUDE]
                loggedInUpdateUI(); //null
                // [END_EXCLUDE]
            }
        }


        if (requestCode == 1) {
            if (filePathCallbackNormal == null) return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            filePathCallbackNormal.onReceiveValue(result);
            filePathCallbackNormal = null;
        } else if (requestCode == 2) {
            Uri[] result = new Uri[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (resultCode == RESULT_OK) {
                    result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                }
                filePathCallbackLollipop.onReceiveValue(result);
            }
        }
//        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (mFilePathCallback == null) {
//                    super.onActivityResult(requestCode, resultCode, data);
//                    return;
//                }
//                Uri[] results = new Uri[]{getResultUri(data)};
//
//                mFilePathCallback.onReceiveValue(results);
//                mFilePathCallback = null;
//            } else {
//                if (mUploadMessage == null) {
//                    super.onActivityResult(requestCode, resultCode, data);
//                    return;
//                }
//                Uri result = getResultUri(data);
//
//                Log.d(getClass().getName(), "openFileChooser : "+result);
//                mUploadMessage.onReceiveValue(result);
//                mUploadMessage = null;
//            }
//        } else
////            if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode != RESULT_OK)
//            {
//            if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);
//            if (mUploadMessage != null) mUploadMessage.onReceiveValue(null);
//            mFilePathCallback = null;
//            mUploadMessage = null;
//            super.onActivityResult(requestCode, resultCode, data);
//        }
    }

    /**
     * *******************************************
     * [END OF ACTIVITYRESULT]
     * *******************************************
     */


    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF MANAGE-THINGS of LOGIN]
     * *******************************************
     */
    /**
     *
     * @param value
     */
    private void setCookieString(String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cookie",value);
        editor.apply();
    }

    /**
     *
     * @return
     */
    private String getCookieString(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String value = preferences.getString("cookie", "");
        return value;
    }

    /**
     *
     */
    private void removeCookiesFromPreferences() {

        // REMOVE PREFERENCES
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance());
        sp.edit().remove(PREF_COOKIES).commit();
    }

    /**
     *
     * @return
     */
    private String getTokenFromMemberInfo() {
        return getPreferencesForToken();
    }

    /**
     *
     * @param responseValue
     */
    private void saveMemberInfo(String responseValue) {

        JsonParser parser = new JsonParser();
        JsonElement mJson =  parser.parse(responseValue);
        Gson gson = new Gson();
        Member object = gson.fromJson(mJson, Member.class);

        Log.e(TAG, "[Response] : Member (token) : " + object.getToken());
        setPreferencesForToken(object.getToken());
        Log.e(TAG, "[Preferences] : Member (token) : " + getPreferencesForToken());

    }


    /**
     * *******************************************
     * [END OF MANAGE-THINGS of LOGIN]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF HANDLER of LOGIN]
     * *******************************************
     */

    private void handleAppVersionAndCookie() {

        try {
            int prefVersion = 0, version = 0;
            String device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version = TextUtils.isEmpty(device_version) ? 0 : Integer.parseInt(device_version.replace(".", ""));
            prefVersion = TextUtils.isEmpty(pref.getAppVersion()) ? 0 : Integer.parseInt(pref.getAppVersion().replace(".", ""));

            Log.e(TAG, "version : " + version + ", prefVersion : " + prefVersion);

            if (version > prefVersion) {
                pref.setAppVersion(device_version);
                cookieManager.setCookie(DEFAULT_URL, "app_version=" + (TextUtils.isEmpty(device_version) ? "1.0" : device_version));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void handleFacebookLogin() {

        mCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (com.facebook.login.widget.LoginButton) findViewById(R.id.field_button_facebook_login);
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
                loggedInUpdateUI();//null
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                loggedInUpdateUI();//null
                // [END_EXCLUDE]
            }
        });
    }

    private void handleGoogleLogin() {

        Log.e(TAG, "[handleGoogleLogin] CALLED");
        // METHOD 1
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
                .requestIdToken("190704323943-u81kgkq2e87mtss1d09vsc1vfqm9qbe8.apps.googleusercontent.com") // IF RED, RE-IMPORT FROM FIREBASE
                .requestEmail()
                .build();
        // [END config_signin]

//        // METHOD 2
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
////                .requestEmail()
//                .requestIdToken("190704323943-gqf74rscig1u69l4lib6i1aj59dbhs08.apps.googleusercontent.com")
//                .build();

        // RETAIN  BELOW LINE
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void handleKakaoLogin() {
        kakaoLoginButton = (LoginButton) findViewById(R.id.field_kakao_login_button);
        Log.e(TAG, "[REQUEST_LOGIN_KAKAO] in handleKakaoLogin");
//        Session.getCurrentSession().addCallback(new kakaoSessionCallback());
        Session.getCurrentSession().addCallback(akakaoSessionCallback);
    }

    private void handleNaverLogin() {

        Log.e(TAG, "[handleNaverLogin]");

        naverLoginButton = (OAuthLoginButton) findViewById(R.id.field_naver_login_button);
        naverLoginInstance = OAuthLogin.getInstance();
        naverLoginInstance.showDevelopersLog(false);
        naverLoginInstance.init(mContext, NAVER_OAUTH_CLIENT_ID, NAVER_OAUTH_CLIENT_SECRET, NAVER_OAUTH_CLIENT_NAME);

        naverLoginButton.setOAuthLoginHandler(naverLoginHandler);

        /*
         * 2015년 8월 이전에 등록하고 앱 정보 갱신을 안한 경우 기존에 설정해준 callback intent url 을 넣어줘야 로그인하는데 문제가 안생긴다.
         * 2015년 8월 이후에 등록했거나 그 뒤에 앱 정보 갱신을 하면서 package name 을 넣어준 경우 callback intent url 을 생략해도 된다.
         */
        //mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_callback_intent_url);
    }

    /**
     * *******************************************
     * [END OF HANDLER of LOGIN]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF REQUEST / NETWORK of LOGIN]
     * *******************************************
     */

    private void checkWithdrawal() {

        // 로그인 하지 않았을 때
        // [checkWithdrawal]::checkWithdrawal> response : {"status":false,"message":"로그인 후 이용하세요","object":null}
        // [checkWithdrawal]::checkWithdrawal> status : ???  false

        // 로그인 되어 있을 때
        // checkWithdrawal> response : {"status":true,"message":"true","object":null}
        // >  [checkWithdrawal]::checkWithdrawal> response : !!!  true

        // 탈퇴 후에 찍히는 로그
        // checkWithdrawal> response : {"status":true,"message":"true","object":null}
        // >  [checkWithdrawal]::checkWithdrawal> response : !!!  true


        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        builder.addInterceptor(loggingInterceptor);
//        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);
//        Token token = new Token(getPreferencesForToken());
//
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("token", "" + getPreferencesForToken());
//        Log.e(TAG, "[PI] token : " + requestBody.get("token"));

        Call<JsonObject> call = api.authCheckSession();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    Log.e(TAG, "[!!][checkWithdrawal]::checkWithdrawal> response : " + response.body());

                    JsonObject jsonObject = response.body();

                    if (jsonObject == null ) {
                        Log.e(TAG, "  >  [checkWithdrawal]::checkWithdrawal> status : ???  null" );
                        return;
                    }

                    if (jsonObject.get("status").toString().equals("false")) {
                        Log.e(TAG, "  >  [checkWithdrawal]::checkWithdrawal> status : ???  " + jsonObject.get("status").toString());
                    }

                    if (jsonObject.get("status").toString().equals("true")) {
                        Log.e(TAG, "  >  [checkWithdrawal]::checkWithdrawal> response : !!!  " + jsonObject.get("status").toString());
//

                    }
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Log.e(TAG, "[checkSession]::authCheckSession> status : ???  onFailure" );
            }
        });

    }

    private void initSession() {

        Log.e(TAG, "[checkForMiniPlayer] :::: [!!!!] > initSession");
        // TODO 재생중일때 상태 들어오지 않도록 [checkForMiniPlayer]

        if (getPreferencesForIsPlayingOnDestroy().equals("true")) {
            return;
        }


        if(getPreferencesForLoginMethod().equals("local")
                && getPreferencesForRememberMe().equals("false")) {

            Log.e(TAG, "[checkSession]::: Local && NO-REMEMBER");

            setPreferencesForToken("");
            setPreferencesForRememberMe("false");
            setPreferencesForEmail("");
            setPreferencesForPassword("");
            savePreferencesForRepeat("NOR");
            savePreferencesForShuffle(false);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance());
            sp.edit().remove(PREF_COOKIES).commit();

            cookieManager.removeSessionCookie();
            cookieManager.removeAllCookie();

            return;
        }

//        bottomSheetBehavior.setHideable(true);
//
//        final CoordinatorLayout.LayoutParams[] layoutParams = {new CoordinatorLayout.LayoutParams(
//                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
//                CoordinatorLayout.LayoutParams.WRAP_CONTENT)};
//        final int[] left = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin))};
//        final int[] top = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_top_margin))};
//        final int[] right = {getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin))};
////        final int[] bottom = {getPixelValue(mCtx, bottomSheetBehavior.getPeekHeight() - 20 )};
//        final int[] bottom = { bottomSheetBehavior.getPeekHeight()};
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable(){
//                    @Override
//                    public void run() {
//                        // 해당 작업을 처리함
//                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//                        layoutParams[0] = new CoordinatorLayout.LayoutParams(
//                                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
//                                CoordinatorLayout.LayoutParams.WRAP_CONTENT);
//                        left[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_left_margin));
//                        top[0] = getPixelValue(mCtx,(int)  getResources().getDimension(R.dimen.webview_top_margin));
//                        right[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_right_margin));
//                        bottom[0] = getPixelValue(mCtx, (int) getResources().getDimension(R.dimen.webview_bottom_margin));
//                        layoutParams[0].setMargins(left[0], top[0], right[0], bottom[0]);
//                        mainFrameLayout.setLayoutParams(layoutParams[0]);
//
//                    }
//                });
//            }
//        }).start();


    }

    private void checkSession() {

        Log.e(TAG, "[checkForMiniPlayer] :::: [!!!!] > checkSession");

        Log.e(TAG, "[checkSession]::: getPreferencesForToken() " + getPreferencesForToken());
        Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor);
//        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);
//        Token token = new Token(getPreferencesForToken());
//
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("token", "" + getPreferencesForToken());
//        Log.e(TAG, "[PI] token : " + requestBody.get("token"));

        Call<JsonObject> call = api.authCheckSession();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
//                Log.e(TAG, "[checkSession] " + jsonObject.toString());

                    if (jsonObject == null ) {
                        Log.e(TAG, "[checkSession]::authCheckSession> status : ???  null" );
                        return;
                    }

                    if (jsonObject.get("status").toString().equals("false")) {
                        Log.e(TAG, "[checkSession]::authCheckSession> status : ???  " + jsonObject.get("status").toString());
                        setPreferencesForToken("");
                        savePreferencesForRepeat("NOR");
                        savePreferencesForShuffle(false);

                        if(getPreferencesForRememberMe().equals("true")) {
                            reqEmailLogin(getPreferencesForEmail(), getPreferencesForPassword());
                        } else {
                            loggedInUpdateUI();
                        }

                    }

                    if (jsonObject.get("status").toString().equals("true")) {
                        Log.e(TAG, "[checkSession]::authCheckSession> response : !!!  " + jsonObject.get("status").toString());
                        setPreferencesForToken(getPreferencesForToken());

                        if (getPreferencesForIsPlayingOnDestroy().equals("true")) {

                            // TODO 세션 체크 후에 미니플레이어 변경?? // BROADCAST 정보도 더 생각.
                            Log.e(TAG, "[checkForMiniPlayer] :::: [!!!!] > checkSession");

//                        if (mStompClient !=null ) {
//                            mStompClient.disconnect();
//                        }
//                        connectStomp();
                            setPlayerTimerTask();
                        } else {

                            if (!getPreferencesForToken().equals(null) && !getPreferencesForToken().equals("")) {
                                getOwnPlaylistItems(true);
                            }
                        }

                    }
                } else {
                    toast(getString(R.string.toast_network_error1));
                }



            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Log.e(TAG, "[checkSession]::authCheckSession> status : ???  onFailure" );
            }
        });

    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
//            etEmail.setError("Required.");
            valid = false;
        } else {
//            etEmail.setError(null);
        }


        if (TextUtils.isEmpty(password)) {
//            etPassword.setError("Required.");
            valid = false;
        } else {
//            etPassword.setError(null);
        }
        return valid;
    }

    /**
     * [LOCAL, EMAIL, LOGIN]
     */
    private void reqEmailLogin(String email, String password) {

//        email = "tester0@gmail.com";
//        password = "tester!!!";

        Log.e(TAG, " called [reqEmailLogin]");

        // REMOVE PREFERENCES
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance());
        sp.edit().remove(PREF_COOKIES).commit();

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

//        HashMap<String, String> validationObject = new HashMap<>();
//        validationObject.put("email", etEmail.getText().toString());
//        validationObject.put("password", etPassword.getText().toString());

        EmailRequest emailRequest = new EmailRequest(email, password);
        Call<APIResponse> call = api.authEmailLogin(emailRequest);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if (response.isSuccessful()) {

                    apiResponse = response.body();
                    Log.e(TAG, "[Response] : getMessage : " + apiResponse.getMessage());
                    Log.e(TAG, "[Response] : getObject : " + apiResponse.getObject());
                    Log.e(TAG, "[Response] : isStatus : " + apiResponse.isStatus());

                    saveMemberInfo(apiResponse.getObject().toString());
                    // MUST ADDED.
//                cookieManager.setCookie(DEFAULT_URL, getCookieString());
                    cookieManager.setCookie(DEFAULT_URL, "JSESSIONID=" + apiResponse.getMessage());
                    Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

                    SystemClock.sleep(1000);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        //noinspection deprecation
                        CookieSyncManager.getInstance().sync();
                    } else {
                        // 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
                        cookieManager.getInstance().flush();
                    }

                    if (apiResponse.isStatus() == true) {
//
                        Log.e(TAG, "[apiResponse.isStatus()] : " + apiResponse.isStatus());

//                    mWebView.loadUrl(UpmuicAPI.BASE_URL + "/auth/facebook/complete");

                        mWebView.loadUrl(DEFAULT_URL);
                        Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

                        if (!getPreferencesForIsPlayingOnDestroy().equals("true")) {
                            getOwnPlaylistItems(true);
                        } else {
                            mediaPlayer = GlobalApplication.getInstance().getServiceInterface().getMediaPlayer();
                            loadDataAsPlayStatus(GlobalApplication.getInstance().getServiceInterface().getAudioItem());
                            setPlayerTimerTask();


                        }

                        try {
//                        toast("[로그인] 재생 / 재생목록을 가져옵니다.");
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    if (apiResponse.isStatus() == false) {
                        Log.e(TAG, "[apiResponse.isStatus()] : " + apiResponse.isStatus());
                    }
                } else {
                    toast(getString(R.string.toast_network_error1));
                }

            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {

            }
        });
    }


    /**
     * [FACEBOOK]
     */
    private void reqFacebookLogin(String access_token) {

        removeCookiesFromPreferences();

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // VERY VERY IMPORTANT
                .build();

        final UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

        FacebookRequest request = new FacebookRequest(access_token);

        Log.e(TAG,"[FacebookRequest]:::(access_token) : " + access_token);

        Call<APIResponse> call = api.authFacebookLoginOrRegister(request);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if (response.isSuccessful()) {

                    apiResponse = response.body();
                    Log.e(TAG, "[Response] : getMessage : " + apiResponse.getMessage());
                    Log .e(TAG, "[Response] : getObject : " + apiResponse.getObject());
                    Log.e(TAG, "[Response] : isStatus : " + apiResponse.isStatus());

                    saveMemberInfo(apiResponse.getObject().toString());

                    // MUST ADDED.
//                cookieManager.setCookie(DEFAULT_URL, getCookieString());
                    cookieManager.setCookie(DEFAULT_URL, "JSESSIONID=" + apiResponse.getMessage());
                    Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

                    SystemClock.sleep(1000);


                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        //noinspection deprecation
                        CookieSyncManager.getInstance().sync();
                    } else {
                        // 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
                        cookieManager.getInstance().flush();
                    }

                    if (apiResponse.isStatus() == true) {
//                    Log.e(TAG, "[apiResponse.isStatus()] : " + apiResponse.isStatus());

//                    mWebView.loadUrl(UpmuicAPI.BASE_URL + "/auth/facebook/complete");

                        mWebView.loadUrl(DEFAULT_URL);
                        Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

                        getOwnPlaylistItems(true);

                        try {

//                        toast("[로그인] 재생 / 재생목록을 가져옵니다.");
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }

                    if (apiResponse.isStatus() == false) {
//                    Log.e(TAG, "[apiResponse.isStatus()] : " + apiResponse.isStatus());
//                        toast(apiResponse.getMessage());

                        toast(getString(R.string.toast_network_error1));
                    }

                } else {
                    toast(getString(R.string.toast_network_error1));
                }

            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {

            }
        });
    }

    /**
     * [GOOGLE]
     */
    private void reqGoogleLogin(final String access_token) {

        Log.e(TAG, "[google-login] 1-3");
        removeCookiesFromPreferences();

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(); // 로그 확인
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        client = builder.build();

        Log.e(TAG, "[reqGoogleLogin] : " + access_token);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client) // VERY VERY IMPORTANT
                .build();

        UpmuicAPI api = retrofit.create(UpmuicAPI.class);
        GoogleRequest request = new GoogleRequest(access_token);

        Call<APIResponse> call = api.authGoogleLoginOrRegister(request);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if (response.isSuccessful()) {
                    apiResponse = response.body();
                    Log.e(TAG, "[Response] : response (onResponse) " + apiResponse);

                    Log.e(TAG, "[Response] : getMessage : " + apiResponse.getMessage());
                    Log.e(TAG, "[Response] : getObject : " + apiResponse.getObject());
                    Log.e(TAG, "[Response] : isStatus : " + apiResponse.isStatus());
                    saveMemberInfo(apiResponse.getObject().toString());

                    cookieManager.setCookie(DEFAULT_URL, "JSESSIONID=" + apiResponse.getMessage());
                    Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

                    SystemClock.sleep(1000);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        //noinspection deprecation
                        CookieSyncManager.getInstance().sync();
                    } else {
                        // 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
                        cookieManager.getInstance().flush();
                    }

                    if (apiResponse.isStatus() == true) {
                        mWebView.loadUrl(DEFAULT_URL);
                        Log.e(TAG, "[cookieManager] getCookie : " + cookieManager.getCookie(DEFAULT_URL));

                        getOwnPlaylistItems(true);

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (apiResponse.isStatus() == false) {
                    }

                } else {
                    toast(getString(R.string.toast_network_error1));
                }

            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {

            }
        });

    }

    /**
     * [KAKAO]
     */
    private void reqKakaoLogin(String access_token) {

        removeCookiesFromPreferences();

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);
        client = builder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // VERY VERY IMPORTANT
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

        KakaoRequest request = new KakaoRequest(access_token);
        Call<APIResponse> call = api.authKakaoLoginOrRegister(request);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if (response.isSuccessful()) {
                    apiResponse = response.body();
                    Log.e(TAG, "[Response] : response (onResponse) " + apiResponse);

                    Log.e(TAG, "[Response] : getMessage : " + apiResponse.getMessage());
                    Log.e(TAG, "[Response] : getObject : " + apiResponse.getObject());
                    Log.e(TAG, "[Response] : isStatus : " + apiResponse.isStatus());
                    saveMemberInfo(apiResponse.getObject().toString());

                    cookieManager.setCookie(DEFAULT_URL, "JSESSIONID=" + apiResponse.getMessage());
                    Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

                    SystemClock.sleep(1000);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        //noinspection deprecation
                        CookieSyncManager.getInstance().sync();
                    } else {
                        // 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
                        cookieManager.getInstance().flush();
                    }

                    if (apiResponse.isStatus() == true) {

                        mWebView.loadUrl(DEFAULT_URL);
                        Log.e(TAG, "[cookieManager] getCookie : " + cookieManager.getCookie(DEFAULT_URL));

                        getOwnPlaylistItems(true);

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (apiResponse.isStatus() == false) {
                    }

                } else {
                    toast(getString(R.string.toast_network_error1));
                }


            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {

            }
        });

    }

    /**
     * [NAVER]
     */
    private void reqNaverLogin(String access_token) {

        Log.e(TAG, "[reqNaverLogin] called");
        removeCookiesFromPreferences();

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(receivedCookiesInterceptor); // VERY VERY IMPORTANT
        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // VERY VERY IMPORTANT
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

        NaverRequest request = new NaverRequest(access_token);
        Call<APIResponse> call = api.authNaverLoginOrRegister(request);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if (response.isSuccessful()) {
                    apiResponse = response.body();
                    Log.e(TAG, "[Response] : response (onResponse) " + apiResponse);

                    Log.e(TAG, "[Response] : getMessage : " + apiResponse.getMessage());
                    Log.e(TAG, "[Response] : getObject : " + apiResponse.getObject());
                    Log.e(TAG, "[Response] : isStatus : " + apiResponse.isStatus());
                    saveMemberInfo(apiResponse.getObject().toString());

                    cookieManager.setCookie(DEFAULT_URL, "JSESSIONID=" + apiResponse.getMessage());
                    Log.e(TAG, "[cookieManager]" + cookieManager.getCookie(DEFAULT_URL));

                    SystemClock.sleep(1000);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        //noinspection deprecation
                        CookieSyncManager.getInstance().sync();
                    } else {
                        // 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
                        cookieManager.getInstance().flush();
                    }

                    if (apiResponse.isStatus() == true) {
                        mWebView.loadUrl(DEFAULT_URL);
                        Log.e(TAG, "[cookieManager] getCookie : " + cookieManager.getCookie(DEFAULT_URL));

                        getOwnPlaylistItems(true);

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (apiResponse.isStatus() == false) {
                    }

                } else {
                    toast(getString(R.string.toast_network_error1));
                }

            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.e(TAG, "[reqNaverLogin] response (onFailure) " + t.getMessage());
                Log.e(TAG, "[reqNaverLogin] response (onFailure) " + t.getCause());
                Log.e(TAG, "[reqNaverLogin] response (onFailure) " + t.getStackTrace());
            }
        });

    }


    /**
     * [KAKAO] without FIREBASE
     */
    public void getLoginInfoKakaoWithoutFirebase() {

        Log.e(TAG, "kakaoSessionCallback, getLoginInfoKakaoWithoutFirebase");
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    // 에러로 인한 로그인 실패
                    // finish();
                } else {
                    //redirectMainActivity();
                }

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                // 로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url 등을 리턴합니다.
                // 사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.
                Log.e(TAG, "UserProfile : " + userProfile.toString());
                Log.e(TAG, "UserProfile : " + userProfile.getId());


                accessToken = Session.getCurrentSession().getAccessToken();
                setPreferencesForAccessToken(accessToken);
                loginMethod = "kakao";
                setPreferencesForLoginMethod(loginMethod);
                Log.e(TAG,"[wwwww]");
                loggedInUpdateUI();

            }
        });
    }

    /**
     * [KAKAO, FIREBASE]
     */
    public void getLoginInfoKakaoWithFirebase() {
        /**
         * [FIREBASE, KAKAO]
         *  : IF USE FIREBASE LOGIN WITH KAKAO
         */
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
//                        FirebaseUser currentUser = mAuth.getCurrentUser();
                    Log.e(TAG, "[isSuccessful] kakao Firebase Login...");
                    loggedInUpdateUI();//currentUser
                } else {

                    Log.e(TAG, "[18]");
                    Toast.makeText(getApplicationContext(), "Failed to create a Firebase user.", Toast.LENGTH_LONG).show();
                    if (task.getException() != null) {
                        Log.e(TAG, task.getException().toString());
                    }
                }
            }
        });
    }

    /**
     * [KAKAO]
     * Session callback class for Kakao Login. OnSessionOpened() is called after successful login.
     */
    private class kakaoSessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {

            Log.e(TAG, "kakaoSessionCallback, Session Opened");

//            Toast.makeText(getApplicationContext(), "Successfully logged in to Kakao. Please Little Bit Wait...", Toast.LENGTH_LONG).show();
            String accessToken = Session.getCurrentSession().getAccessToken();
            setPreferencesForAccessToken(accessToken);

            Log.e(TAG, "[SessionCallback] for kakao Login (accessToken) : " + accessToken);

            /**
             * [KAKAO ORIGINAL]
             *  : IF NOT USE FIREBASE LOGIN WITH KAKAO
             *  KAKAO ONLY.
             */
            getLoginInfoKakaoWithoutFirebase();

            /**
             * [FIREBASE, KAKAO]
             *  : IF USE FIREBASE LOGIN WITH KAKAO
             */
//            getLoginInfoKakaoWithFirebase();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Log.e(TAG, exception.toString());
            }
        }
    }


    /**
     *[FIREBASE, KAKAO & NAVER]
     * @param token Access token retrieved after successful Kakao Login
     * @return Task object that will call validation server and retrieve firebase token
     */
    private Task<String> getFirebaseJwt(final String token, String provider) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();


        String url = getResources().getString(R.string.validation_server_domain) + "/verifyToken";
        Log.e(TAG, "[getFirebaseJwt]  accessToken : " + token + " [provider] : " + provider);
        accessToken = token;
        setPreferencesForAccessToken(accessToken);

        if (provider.equals("kakao")) {

            loginMethod = "kakao";
            setPreferencesForLoginMethod(loginMethod);
            url = getResources().getString(R.string.validation_server_domain) + "/verifyToken";
        } else {
            // NAVER ("naver")
            loginMethod = "naver";
            setPreferencesForLoginMethod(loginMethod);
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

        return source.getTask();
    }


    /**
     * [NAVER]
     */
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
     * [NAVER]
     */
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
            loggedInUpdateUI();//null
        }
    }


    /**
     * [NAVER]
     *  : IF NOT USE FIREBASE LOGIN WITH NAVER
     *  NAVER ONLY
     */
    public void getLoginInfoNaverWithoutFirebase() {
        loginMethod = "naver";
        setPreferencesForLoginMethod(loginMethod);
        loggedInUpdateUI();
    }

    /**
     * [FIREBASE, NAVER]
     *  : IF USE FIREBASE LOGIN WITH NAVER
     */
    public void getLoginInfoNaverWithFirebase() {
        getFirebaseJwt(naverLoginInstance.getAccessToken(mContext),"naver").continueWithTask(new Continuation<String, Task<AuthResult>>() {
            @Override
            public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {
                String firebaseToken = task.getResult();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                Log.e(TAG, "[firebaseToken]" + firebaseToken);
//                    accessToken = firebaseToken;

                return auth.signInWithCustomToken(firebaseToken);
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                        FirebaseUser currentUser = mAuth.getCurrentUser();
//                        mAuth.getAccessToken(true);
                    loginMethod = "naver";
                    setPreferencesForLoginMethod(loginMethod);
                    loggedInUpdateUI();//currentUser
                } else {

                    Log.e(TAG, "[17]");
                    Toast.makeText(getApplicationContext(), "Failed to create a Firebase user.", Toast.LENGTH_LONG).show();
                    if (task.getException() != null) {
                        Log.e(TAG, task.getException().toString());
                    }
                }
            }
        });
    }

    /**
     * [NAVER] CUSTOM AS CIRCUMSTANCES...
     */
    private class naverRequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = naverLoginInstance.getAccessToken(mContext);

//            accessToken = at;
            return naverLoginInstance.requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
//            mApiResultText.setText((String) content);

            loginMethod = "naver";
            setPreferencesForLoginMethod(loginMethod);
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

            /**
             * [NAVER]
             *  : IF NOT USE FIREBASE LOGIN WITH NAVER
             *  NAVER ONLY
             */
            getLoginInfoNaverWithoutFirebase();

            /**
             * [FIREBASE, NAVER]
             *  : IF USE FIREBASE LOGIN WITH NAVER
             */
//            getLoginInfoNaverWithFirebase();
        }
    }


    /**
     * [NAVER] ORIGIN, BASE (CURRENTLY NOT USED)
     */
    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
//            mApiResultText.setText((String) "");
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

    /**
     * [NAVER]
     * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    private OAuthLoginHandler naverLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {

                String naverToken = naverLoginInstance.getAccessToken(mContext);
                String refreshToken = naverLoginInstance.getRefreshToken(mContext);
                long expiresAt = naverLoginInstance.getExpiresAt(mContext);
                String tokenType = naverLoginInstance.getTokenType(mContext);


                Log.e(TAG, "accessToken : " + naverToken);
                Log.e(TAG, "refreshToken : " + refreshToken);
                Log.e(TAG, "expiresAt : " + expiresAt);
                Log.e(TAG, "tokenType : " + tokenType);

                Log.e(TAG, "[naverLoginHandler] : "  + naverToken);
                accessToken = naverToken;
                setPreferencesForAccessToken(accessToken);
                loginMethod = "naver";
                setPreferencesForLoginMethod(loginMethod);
                new naverRequestApiTask().execute();

            } else {
                String errorCode = naverLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = naverLoginInstance.getLastErrorDesc(mContext);
            }
        }

    };

    /**
     * [SIGN, AUTH, LOGIN, GOOGLE]
     */
    private void signInGoogle() {
        Log.e(TAG, "[signInGoogle] 1-1");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * [SIGN, AUTH, ACCESS, LOGIN, GOOGLE]
     */
    private void revokeAccessGoogle() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loggedInUpdateUI();//null
                    }
                });
    }

    /**
     * METHOD2 OF GOOGLE-LOGIN / WITHOUT FIREBASE
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            accessToken = account.getIdToken();
            setPreferencesForAccessToken(accessToken);
            loginMethod = "google";
            setPreferencesForLoginMethod(loginMethod);
            loggedInUpdateUI();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Log.e(TAG, "[16]");
            toast(getString(R.string.toast_log_in_failed));
            loggedInUpdateUI(); //null
        }

    }


    /**
     * [FIREBASE, GOOGLE, AUTH, LOGIN]
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            loggedInUpdateUI();//user
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            loggedInUpdateUI();//null
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }


    /**
     * [FIREBASE, EMAIL, LOGIN]
     * @param email
     * @param password
     */
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm(email,password)) {
            return;
        }

//        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            loggedInUpdateUI();//user
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Log.e(TAG, "[14]");
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loggedInUpdateUI();//null
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
//                            mStatusTextView.setText(R.string.auth_failed);
                        }
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    /**
     * [FACEBOOK, AUTH, FIREBASE, LOGIN]
     * @param token
     */
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            loginMethod = "facebook";
                            setPreferencesForLoginMethod(loginMethod);
                            accessToken = token.getToken();
                            setPreferencesForAccessToken(accessToken);
                            loggedInUpdateUI();//user
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Log.e(TAG, "[15]");
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loggedInUpdateUI();//null
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }


    /**
     * [FIREBASE, AUTH, EMAIL VERIFY, vErification]
     */
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

                            Log.e(TAG, "[7]");
                            Toast.makeText(getApplicationContext(),
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            Log.e(TAG, "[8]");
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

    /**
     * [FIREBASE, AUTH, SIGHOUT]
     */
    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loggedInUpdateUI();//null
                    }
                });

        loggedInUpdateUI();//null
    }

    /**
     * [FIREbASE, EMAIL , AUTH]
     * @param email
     * @param password
     */
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm(email,password)) {
            return;
        }
//        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            loggedInUpdateUI();//user
                        } else {
                            // If
                            // sign in fails, display a message to the user.

                            Log.e(TAG, "[9]");
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loggedInUpdateUI();//null
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    /**
     * *******************************************
     * [END OF REQUEST of LOGIN]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */

    /**
     * *******************************************
     * [TOP OF GETTER-SETTER of PREFERENCES-SAVING]
     * *******************************************
     */

    private void savePlaylist(ArrayList<MusicTrack> arrayList) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(arrayList);

        editor.putString(getString(R.string.list_name), json);
        editor.commit();
    }

    private List<MusicTrack> readPlaylist() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(getString(R.string.list_name), "");
        Type type = new TypeToken<List<MusicTrack>>() {}.getType();
        List<MusicTrack> arrayList = gson.fromJson(json, type);

        Log.e(TAG, "[readPlaylist] " + arrayList.toString());

        return arrayList;
    }

    /**
     *
     * @return
     */
    public String getPreferencesForAccessToken() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("accessToken", "");
        return temp;
    }

    /**
     *
     * @param accessToken
     */
    public void setPreferencesForAccessToken(String accessToken) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("accessToken", accessToken);
        editor.commit();
    }

    private boolean getPreferencesForPlayerFirstLoad(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getBoolean("playerFirstLoad", false); //key, value(defaults)

    }

    private void savePreferencesForPlayerFirstLoad(boolean temp){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("playerFirstLoad", temp);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForWebSocketPlaytime() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("playtime", "");
        return temp;
    }

    /**
     *
     * @param playtime
     */
    public void setPreferencesForWebSocketPlaytime(String playtime) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("playtime", playtime);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForUpPoint() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("upPoint", "");
        return temp;
    }

    /**
     *
     * @param upPoint
     */
    public void setPreferencesForUpPoint(String upPoint) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("upPoint", upPoint);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForIsPlayingOnDestroy() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("isPlayingOnDestroy", "");
        return temp;
    }

    /**
     *
     * @param isPlayingOnDestroy
     */
    public void setPreferencesForIsPlayingOnDestroy(String isPlayingOnDestroy) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("isPlayingOnDestroy", isPlayingOnDestroy);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForDestroy() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("destroy", "");
        return temp;
    }

    /**
     *
     * @param destroy
     */
    public void setPreferencesForDestroy(String destroy) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("destroy", destroy);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForEmail() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("email", "");
        return temp;
    }

    /**
     *
     * @param email
     */
    public void setPreferencesForEmail(String email) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", email);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForPassword() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("password", "");
        return temp;
    }

    /**
     *
     * @param password
     */
    public void setPreferencesForPassword(String password) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("password", password);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForLoginMethod() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("loginMethod", "");
        return temp;
    }

    /**
     *
     * @param loginMethod
     */
    public void setPreferencesForLoginMethod(String loginMethod) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("loginMethod", loginMethod);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForRememberMe() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("rememberMe", "");
        return temp;
    }

    /**
     *
     * @param rememberMe
     */
    public void setPreferencesForRememberMe(String rememberMe) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("rememberMe", rememberMe);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForAudioPlayed() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("audioPlayed", "");
        return temp;
    }

    /**
     *
     * @param audioPlayed
     */
    public void setPreferencesForAudioPlayed(String audioPlayed) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("audioPlayed", audioPlayed);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public String getPreferencesForEditMode() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("editMode", "");
        return temp;
    }

    /**
     *
     * @param editMode
     */
    //FROM AudioInterFace
    public void setPreferencesForEditMode(String editMode) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("editMode", editMode);
        editor.commit();
    }

    /**
     *
     * @return
     */
    @Override
    public String getPreferencesForToken() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("token", "");
        return temp;
    }

    /**
     *
     * @param token
     */
    @Override
    public void setPreferencesForToken(String token) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    /**
     *
     * @return
     */
    private boolean getPreferencesForShuffle(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getBoolean("isOnShuffle", false); //key, value(defaults)

    }

    /**
     *
     * @param temp
     */
    private void savePreferencesForShuffle(boolean temp){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isOnShuffle", temp);
        editor.commit();
    }

    /**
     *
     * @return
     */
    private String getPreferencesForRepeat(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getString("REPEAT_MODE", ""); //key, value(defaults)

    }

    /**
     *
     * @param temp
     */
    private void savePreferencesForRepeat(String temp){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("REPEAT_MODE", temp);
        editor.commit();
    }

    /**
     *
     * @return
     */
    private String getPreferencesForLastPlayedTrackId(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getString("lastPlayedTrackId", ""); //key, value(defaults)

    }

    /**
     *
     * @param id
     */
    private void savePreferencesForLastPlayedTrackId(String id){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lastPlayedTrackId", id);
        editor.commit();
    }

    /**
     *
     * @return
     */
    private int getPreferencesForLastPlayedTrackNumber(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getInt("lastPlayedTrackNumber", 0); //key, value(defaults)

    }

    /**
     *
     * @param position
     */
    private void savePreferencesForLastPlayedTrackNumber(int position){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("lastPlayedTrackNumber", position);
        editor.commit();
    }

    /**
     *
     */
    private void removePreferencesForLastPlayedTrackNumber(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("lastPlayedTrackNumber");
        editor.commit();
    }

    /**
     *
     */
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * USE WHEN GLOBAL VALUES FOR LIST.
     * FOR STORE : setStringArrayPref
     * FOR RETRIEVE : getStringArrayPref
     // store preference
     * @Example
     * ArrayList<String> list = new ArrayList<String>(Arrays.asList(urls));
     * setStringArrayPref(this, "urls", list);
     */
    private void setStringArrayPref(Context context,String key,ArrayList<String> values){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray a = new JSONArray();
        for (int i=0;i<values.size();i++) {
            a.put(values.get(i));
        }

        if(!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key,null);
        }
        editor.commit();
    }

    /**
     * // retrieve preference
     * @Example
     * list = getStringArrayPref(this, "urls");
     * urls = (String[]) list.toArray();
     */
    private ArrayList<String> getStringArrayPref(Context context,String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key,null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i=0;i<a.length();i++) {
                    String url = a.optString(i);
                    urls.add(url);;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    /**
     * *******************************************
     * [END OF GETTER-SETTER of PREFERENCES-SAVING]
     * *******************************************
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     */


    /**
     *  Please Do not delete.
     * [TOP OF OUT-OF-ALGORITHM-LOGIC, NOT-USED, currently]
     * getAllTrackItems
     * getAllVideoItems
     * onOptionsItemSelected
     * onCreateOptionsMenu
     * onItemClick
     * playVideo
     * ****************[ ]***************************
     */
    private void getAllTrackItems() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);
        Call<List<MusicTrack>> call = api.getAllTracks();
        call.enqueue(new Callback<List<MusicTrack>>() {
            @Override
            public void onResponse(Call<List<MusicTrack>> call, Response<List<MusicTrack>> response) {

                if (response.isSuccessful()) {

                    defaultList = response.body();
                    defaultArrayList.addAll(defaultList.subList(0,9));// 10개
                    mAdapter.updateList(defaultList.subList(0,9));

                } else {
                    toast(getString(R.string.toast_network_error1));
                }

            }

            @Override
            public void onFailure(Call<List<MusicTrack>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void getAllVideoItems() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

        Call<List<Video>> call = api.getAllVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful()) {

                    itemsListForVideo = response.body();
                    itemsArrayListForVideo.addAll(itemsListForVideo);
                    playlistAdapterForVideo = new VideoPlaylistAdapter(getApplicationContext(), itemsArrayListForVideo);
//                listView.setAdapter(playlistAdapter);
                } else {
                    toast(getString(R.string.toast_network_error1));
                }

            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        intent = new Intent(this, AudioPlayerActivity.class);

        MusicTrack track = itemsList.get(i);
//        Log.d("RETROFITTTTT :::::: ", "" + track.toString());
        intent.putExtra(KEY_TRACK_ADMIN_URL, track.getAdminUrl());
        intent.putExtra(KEY_TRACK_ARTIST_ID, track.getArtistId());
        intent.putExtra(KEY_TRACK_ARTIST_NICK, track.getArtistNick());
        intent.putExtra(KEY_TRACK_COVER_IMAGE_URL, track.getCoverImageUrl());
        intent.putExtra(KEY_TRACK_DURATION, track.getDuration());
        intent.putExtra(KEY_TRACK_FILE_NAME_URL, track.getFilenameUrl());
        intent.putExtra(KEY_TRACK_ID, track.getId());
        intent.putExtra(KEY_TRACK_LIKED, track.getLiked());
        intent.putExtra(KEY_TRACK_MUSIC_ALBUM_ID, track.getMusicAlbumId());
        intent.putExtra(KEY_TRACK_REJECTED_REASON, track.getRejectedReason());
        intent.putExtra(KEY_TRACK_SUBJECT, track.getSubject());
        intent.putExtra(KEY_TRACK_THEME_NAMES, track.getThemeNames());
        intent.putExtra(KEY_TRACK_TITLE_TRACK, track.getTitleTrack());
        intent.putExtra(KEY_TRACK_URL, track.getUrl());

        Picasso.with(this)
                .load(String.valueOf(intent.getStringExtra(MainActivity.KEY_TRACK_COVER_IMAGE_URL)))
                .into(imageView01);
        Picasso.with(this)
                .load(String.valueOf(intent.getStringExtra(MainActivity.KEY_TRACK_COVER_IMAGE_URL)))
                .into(imageViewListHeader);

        Picasso.with(this)
                .load(String.valueOf(intent.getStringExtra(MainActivity.KEY_TRACK_COVER_IMAGE_URL)))
                .transform(new BlurTransformation(this, 50))
                .into(imageView02);

//        startActivity(intent); // IF WANT NEW ACTIVITY.
    }

    @Override
    public void playVideo(Video param, int position) {

        playMode = "Video";

        Intent intent = new Intent(this, VideoPlayerActivity.class);
        Video track = param;
        intent.putExtra(KEY_VIDEO_ADMIN_URL, track.getAdminUrl());
        intent.putExtra(KEY_VIDEO_ARTIST_NICK, track.getArtistNick());
        intent.putExtra(KEY_VIDEO_ARTIST_URL, track.getArtistUrl());
        intent.putExtra(KEY_VIDEO_CREATED_AT, track.getCreatedAt());
        intent.putExtra(KEY_VIDEO_DESCRIPTION, track.getDescription());
        intent.putExtra(KEY_VIDEO_DURATION, track.getDuration());
        intent.putExtra(KEY_VIDEO_FILENAME, track.getFilename());
        intent.putExtra(KEY_VIDEO_FILENAME_URL, track.getFilenameUrl());
        intent.putExtra(KEY_VIDEO_GENRE_NAME, track.getGenreName());
        intent.putExtra(KEY_VIDEO_HEART_COUNT, track.getHeartCnt());
        intent.putExtra(KEY_VIDEO_HIT_COUNT, track.getHitCnt());
        intent.putExtra(KEY_VIDEO_HOT_POINT, track.getHotPoint());
        intent.putExtra(KEY_VIDEO_ID, track.getId());
        intent.putExtra(KEY_VIDEO_LIKED, track.getLiked());
        intent.putExtra(KEY_VIDEO_SUBJECT, track.getSubject());
        intent.putExtra(KEY_VIDEO_THUMBNAIL, track.getThumbnail());
        intent.putExtra(KEY_VIDEO_THUMBNAIL_URL, track.getThumbnailUrl());
        intent.putExtra(KEY_VIDEO_UPDATED_AT, track.getUpdatedAt());
        intent.putExtra(KEY_VIDEO_URL, track.getUrl());
        intent.putExtra(KEY_VIDEO_VIDEO_TYPE, track.getVideoType());

        startActivity(intent);

//        Log.e(TAG, "PLAYITEM : " + AudioApplication.getInstance().getServiceInterface().getAudioItem());
    }

    private static float dp2px(int dip, Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        return dip * scale + 0.5f;
    }

    public void playMP3FileWithAsyncTask() {
        AsyncTask<String,String,String> mp3Play = new AsyncTask<String, String, String>() {

            ProgressDialog mDialog = new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute() {
                mDialog.setMessage("Please Wait.....");
                mDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {

                try {
                    mediaPlayer.setDataSource(strings[0]);
                    mediaPlayer.prepare();
                    mediaFileLength = mediaPlayer.getDuration();

                    int lengthSec = mediaPlayer.getDuration()/1000;
                    seekBar.setMax(lengthSec);
                    textExpanded04.setText(String.format("%02d:%02d", lengthSec/60 , lengthSec%60));

                    handleLocalSeekbar();
                    handleMediaPlayerPlayOrPause();
                    handleMediaPlayerForward();
                    handleMediaPlayerBackward();
                    handleMediaPlayerOnCompletion();

                } catch (Exception ex) {

                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                mDialog.dismiss();
                mediaPlayer.start();
                setOnWifiLock();
                updateLocalSeekBar();

            }
        };

        mp3Play.execute(getFilesDir() + "/" + filename); // direct link for mp3 file
        setViewsAsPlayStatus();

    }

    private class AndroidBridge {
        // 로그아웃
        @JavascriptInterface
        public void logout() {
        }
    }


    /**
     * CURRENTLY NOT USEd : 현재는 사용하지 않음
     * 안드로이드 자체 비디오플레이어 사용시.
     * @param jsonObj
     */
    private void playVideoOnNative(JSONObject jsonObj) {

        Log.e(TAG, "[REQUEST_PLAY_VIDEO] load Video!");
        JSONObject jsonVideo = null;
        try {
            jsonVideo = jsonObj.getJSONObject("video");
            Log.d(TAG, "callbackFromJS : video id - " + jsonVideo.getString("id"));
            Log.d(TAG, "callbackFromJS : video subject - " + jsonVideo.getString("subject"));
            Log.d(TAG, "callbackFromJS : video thumbnail - " + jsonVideo.getString("thumbnail"));
            Log.d(TAG, "callbackFromJS : video duration - " + jsonVideo.getString("duration"));
            Log.d(TAG, "callbackFromJS : video filename - " + jsonVideo.getString("filename"));
            Log.d(TAG, "callbackFromJS : video filenameUrl - " + jsonVideo.getString("filenameUrl"));

            //  영상 재생
            Video newVideo = new Video();
            newVideo.setId(jsonVideo.getString("id"));
            newVideo.setLiked(jsonVideo.getString("liked"));
            newVideo.setVideoType(jsonVideo.getString("videoType"));
            newVideo.setSubject(jsonVideo.getString("subject"));
            newVideo.setThumbnail(jsonVideo.getString("thumbnail"));

            newVideo.setThumbnailUrl(jsonVideo.getString("thumbnailUrl"));
            newVideo.setDuration(jsonVideo.getString("duration"));
            newVideo.setFilename(jsonVideo.getString("filename"));
            newVideo.setFilenameUrl(jsonVideo.getString("filenameUrl"));
            newVideo.setHitCnt(jsonVideo.getString("hitCnt"));

            newVideo.setHotPoint(jsonVideo.getString("hotPoint"));
            newVideo.setHeartCnt(jsonVideo.getString("heartCnt"));
            newVideo.setCreatedAt(jsonVideo.getString("createdAt"));
            newVideo.setUpdatedAt(jsonVideo.getString("updatedAt"));
            newVideo.setAdminUrl(jsonVideo.getString("adminUrl"));

            newVideo.setGenreName(jsonVideo.getString("genreName"));
            newVideo.setTypeName(jsonVideo.getString("typeName"));
            newVideo.setArtistNick(jsonVideo.getString("artistNick"));
            newVideo.setArtistUrl(jsonVideo.getString("artistUrl"));
            newVideo.setAdminUrl(jsonVideo.getString("adminUrl"));

            playVideo(newVideo,0);// position-param currently no matter : eg..1,2,3, => equal function doing.

//                            Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
//                            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * [END OF OUT-OF-ALGORITHM-LOGIC, NOT-USED, currently]
     * Please Do not delete.
     */

    /**
     * **************************************************************************************
     * **************************************************************************************
     * **************************************************************************************
     * **************************************************************************************
     * **************************************************************************************
     */




}
