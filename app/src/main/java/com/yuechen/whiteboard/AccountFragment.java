package com.yuechen.whiteboard;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flod.loadingbutton.LoadingButton;
import com.yuechen.whiteboard.DataSource.UserInfoDataSource;
import com.yuechen.whiteboard.Service.LoginService;
import com.yuechen.whiteboard.Service.LoginServiceObserver;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener, LoginServiceObserver {

    private EditText mStudentIDEditText;
    private EditText mPasswordEditText;
    private ImageView mCleanIDImageView;
    private ImageView mCleanPasswordImageView;
    private ImageView mShowPasswordImageView;
    private LoadingButton mSaveButton;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserInfoDataSource.initialize(getContext());
        initView();
        initListener();
        initShow();

    }

    private void initShow() {
        pref = getActivity().getSharedPreferences("msgSave", Context.MODE_PRIVATE);
        boolean flag = pref.getBoolean("flag", false);
        if(flag) {
            mStudentIDEditText.setText(UserInfoDataSource.getUsername());
            mPasswordEditText.setText(UserInfoDataSource.getPassword());
        }
    }

    private void initView() {
        mStudentIDEditText = getView().findViewById(R.id.student_id);
        mPasswordEditText = getView().findViewById(R.id.et_password);
        mCleanIDImageView = getView().findViewById(R.id.clean_id);
        mCleanPasswordImageView = getView().findViewById(R.id.clean_password);
        mShowPasswordImageView = getView().findViewById(R.id.iv_show_pwd);
        mSaveButton = getView().findViewById(R.id.btn_save);
    }


    private void initListener() {
        mCleanIDImageView.setOnClickListener(this);
        mCleanPasswordImageView.setOnClickListener(this);
        mShowPasswordImageView.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

        mStudentIDEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && mCleanIDImageView.getVisibility() == View.GONE) {
                    mCleanIDImageView.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    mCleanIDImageView.setVisibility(View.GONE);
                }
            }
        });
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && mCleanPasswordImageView.getVisibility() == View.GONE) {
                    mCleanPasswordImageView.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    mCleanPasswordImageView.setVisibility(View.GONE);
                }
                if (s.toString().isEmpty())
                    return;
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    s.delete(temp.length() - 1, temp.length());
                    mPasswordEditText.setSelection(s.length());
                }
            }
        });
    }

    private boolean flag = false;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.clean_id:
                mStudentIDEditText.setText("");
                break;
            case R.id.clean_password:
                mPasswordEditText.setText("");
                break;
            case R.id.iv_show_pwd:
                if (flag) {
                    mPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mShowPasswordImageView.setImageResource(R.drawable.ic_pass_gone);
                    flag = false;
                } else {
                    mPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mShowPasswordImageView.setImageResource(R.drawable.ic_pass_visable);
                    flag = true;
                }
                String pwd = mPasswordEditText.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    mPasswordEditText.setSelection(pwd.length());
                break;
            case R.id.btn_save:
                mSaveButton.start();
                saveMsg();
                break;
        }
    }

    private void saveMsg() {
        String username = mStudentIDEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        LoginService.subscribe(this);
        LoginService.loginVerify(username, password);

    }

    @Override
    public void notifyLoginVerifyResult(Boolean result, String message) {
        if(result) {
            mSaveButton.complete();
            editor = getActivity().getSharedPreferences("msgSave", Context.MODE_PRIVATE).edit();
            editor.putBoolean("flag", true);
            editor.apply();
        } else {
            mSaveButton.fail();
            editor = getActivity().getSharedPreferences("msgSave", Context.MODE_PRIVATE).edit();
            editor.putBoolean("flag", false);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }



    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }



}
