package com.sns.docsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.sns.docsapp.Adapter.ChatAdapter;
import com.sns.docsapp.DB.SQLiteHelper;
import com.sns.docsapp.Model.MessageModel;
import com.sns.docsapp.Model.ResponseModel;
import com.sns.docsapp.NetworkHelper.GetDataService;
import com.sns.docsapp.NetworkHelper.RetrofitBotCharInstance;
import com.sns.docsapp.Utils.AppConstants;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final BroadcastReceiver dateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_DATE_CHANGED)) {

                mSqLiteHelper.addChat(ChatAdapter.getDate(System.currentTimeMillis(), "d MMM yyyy"), false, true);

                MessageModel model = new MessageModel();
                model.setTimeUTC(System.currentTimeMillis());
                model.setMessage(ChatAdapter.getDate(System.currentTimeMillis(), "d MMM yyyy"));
                model.setDate(true);
                models.add(model);
                mChatAdapter.updateDataSet(models);
            }
        }
    };

    private ImageView mSendBtn;
    private EditText mInputView;
    private RecyclerView mChatRecyclerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private NavigationView mNavigationView;
    private Menu mMenu;

    private RecyclerView.LayoutManager mLayoutManager;
    private ChatAdapter mChatAdapter;

    private ArrayList<MessageModel> models = new ArrayList<>();

    private String table_name;

    private SQLiteHelper mSqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerDateChangeListener();

        loadChats();
    }

    private void initDB() {
        ArrayList<String> tableList = new ArrayList<>();
        tableList.add(mMenu.findItem(R.id.item_one).getTitleCondensed().toString());
        tableList.add(mMenu.findItem(R.id.item_two).getTitleCondensed().toString());
        tableList.add(mMenu.findItem(R.id.item_three).getTitleCondensed().toString());
        mSqLiteHelper = SQLiteHelper.getInstance(MainActivity.this, tableList);
    }

    private void initView() {

        // Set title
        getSupportActionBar().setTitle(R.string.activity_title);

        mChatRecyclerView = findViewById(R.id.chat_list);

        mDrawerLayout = findViewById(R.id.activity_main);
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView = findViewById(R.id.navigation_bar);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.item_one:
                        table_name = mMenu.findItem(R.id.item_one).getTitleCondensed().toString();
                        break;
                    case R.id.item_two:
                        table_name = mMenu.findItem(R.id.item_two).getTitleCondensed().toString();
                        break;
                    case R.id.item_three:
                        table_name = mMenu.findItem(R.id.item_three).getTitleCondensed().toString();
                        break;
                }

                loadChats();

                getSupportActionBar().setTitle(table_name);
                return true;
            }
        });

        mMenu = mNavigationView.getMenu();
        table_name = mMenu.findItem(R.id.item_one).getTitleCondensed().toString();

        mChatRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        mChatRecyclerView.setLayoutManager(mLayoutManager);

        mChatAdapter = new ChatAdapter(MainActivity.this);
        mChatRecyclerView.setAdapter(mChatAdapter);

        mInputView = findViewById(R.id.chat_input);

        mSendBtn = findViewById(R.id.send_message);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate then send message to API.
                String inputText = mInputView.getText().toString();
                if (!inputText.isEmpty()) {
                    sendMsgToAPI(inputText);
                    saveChat(inputText);
                }
            }
        });
    }

    private void loadChats() {
        mSqLiteHelper.UpdateCurrentTable(table_name);
        models = (ArrayList<MessageModel>) mSqLiteHelper.getChatFromDates(System.currentTimeMillis(), -10);
        mChatAdapter.updateDataSet(models);
    }

    private void sendMsgToAPI(String msg) {
        GetDataService getDataService = RetrofitBotCharInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseModel> call = getDataService.getBotResponse(AppConstants.apiKey, msg, AppConstants.chatBotID, AppConstants.externalD);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                Log.e("sns", "onResponse");
                ResponseModel responseModel = response.body();
                ResponseModel.SuccessModel successModel = responseModel.getMessage();
                MessageModel model = new MessageModel();
                model.setSelf(false);
                model.setMessage(successModel.getMessage());
                model.setTimeUTC(System.currentTimeMillis());
                models.add(model);
                mChatAdapter.updateDataSet(models);
                mSqLiteHelper.addChat(successModel.getMessage(), false, false);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("sns", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        UnRegisterDateChangeListener();
        super.onPause();
    }

    private void saveChat(String msg) {
        mInputView.setText("");

        mChatRecyclerView.smoothScrollToPosition(models.size());

        mSqLiteHelper.addChat(msg, true, false);

        MessageModel model = new MessageModel();
        Date date = new Date(System.currentTimeMillis());
        model.setTimeUTC(date.getTime());
        model.setMessage(msg);
        /*if (models.size() % 2 == 0) {
            mSqLiteHelper.addChat(msg, false, false);
        } else if (models.size() % 3 == 0) {
            model.setDate(true);
            mSqLiteHelper.addChat(msg, false, true);
            model.setMessage(ChatAdapter.getDate(System.currentTimeMillis(), "d MMM yyyy"));
        } else {
            model.setSelf(true);
            mSqLiteHelper.addChat(msg, true, false);
        }
*/
        models.add(model);

        mChatAdapter.updateDataSet(models);
    }

    private void registerDateChangeListener() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(dateChangeReceiver, intentFilter);
    }

    private void UnRegisterDateChangeListener() {
        unregisterReceiver(dateChangeReceiver);
    }
}