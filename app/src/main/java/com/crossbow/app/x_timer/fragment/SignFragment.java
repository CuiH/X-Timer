package com.crossbow.app.x_timer.fragment;

/**
 * Created by kinsang on 16-1-8.
 */
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.util.cloud.CloudBackup;
import com.crossbow.app.x_timer.util.cloud.Sign;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by xiaoyanhao on 16/1/6.
 */
public class SignFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    private EditText signin_user;
    private EditText signin_pass;
    private TextView signin_error;
    private Button signin_btn;

    private EditText signup_user;
    private EditText signup_pass1;
    private EditText signup_pass2;
    private TextView signup_error;
    private Button signup_btn;

    private ProgressDialog dialog;
    private ProgressDialog dialog2;

    private MaterialDialog mMaterialDialog;
    private MaterialDialog mMaterialDialog2;
    private MaterialDialog mMaterialDialog3;
    private View dialogView;

    private String userID;

    private static final int SIGNIN = 1;
    private static final int SIGNUP = 2;
    private static final int UPLOAD = 3;
    private static final int DOWNLOAD = 4;

    private static final String ERROR1 = "user existed";
    private static final String ERROR2 = "user not existed";
    private static final String ERROR3 = "password incorrect";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject object;
            switch (msg.what) {
                case SIGNIN:
                    Log.e("signin", msg.obj.toString());
                    try {
                        object = new JSONObject(msg.obj.toString());
                        if (object.get("state").equals("error")) {
                            String error = object.getString("reason");
                            if (error.equals(ERROR2)) {
                                signin_error.setText("用户名不存在");
                                YoYo.with(Techniques.Shake).duration(700).playOn(signin_user);
                            } else if (error.equals(ERROR3)) {
                                signin_error.setText("密码不正确");
                                YoYo.with(Techniques.Shake).duration(700).playOn(signin_pass);
                            } else {
                                signin_error.setText(error);
                            }
                        } else {
                            userID = object.getString("userID");
                            Toast.makeText(getActivity(), "登陆成功", Toast.LENGTH_LONG).show();
                            dialog.cancel();

                            showUploadDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case SIGNUP:
                    Log.e("signup", msg.obj.toString());
                    try {
                        object = new JSONObject(msg.obj.toString());
                        if (object.get("state").equals("error")) {
                            String error = object.getString("reason");
                            if (error.equals(ERROR1)) {
                                signup_error.setText("用户名已存在");
                            } else {
                                signup_error.setText(error);
                            }
                        } else {
                            Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_LONG).show();
                            dialog.cancel();

                            showUploadDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case UPLOAD:
                    dialog2.dismiss();

                    mMaterialDialog2 = new MaterialDialog(getActivity())
                            .setTitle("备份成功！")
                            .setMessage("")
                            .setPositiveButton("返回", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog2.dismiss();
                                    getActivity().setResult(getActivity().RESULT_OK);
                                    getActivity().finish();
                                }
                            });

                    mMaterialDialog2.show();

                    break;

                case DOWNLOAD:
                    dialog2.dismiss();

                    mMaterialDialog2 = new MaterialDialog(getActivity())
                            .setTitle("同步成功!")
                            .setMessage("")
                            .setPositiveButton("返回", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog2.dismiss();
                                    getActivity().setResult(getActivity().RESULT_OK);
                                    getActivity().finish();
                                }
                            });

                    mMaterialDialog2.show();

                default:
                    break;
            }

            dialog.cancel();
        }
    };



    private View.OnClickListener signinListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = signin_user.getText().toString();
            String password = signin_pass.getText().toString();
            dialog.setMessage("正在登录中...");
            dialog.show();
            new Sign(handler, SIGNIN, username, password);
        }
    };

    private View.OnClickListener signupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = signup_user.getText().toString();
            String password1 = signup_pass1.getText().toString();
            String password2 = signup_pass2.getText().toString();

            if (password1.equals(password2)) {
                dialog.setMessage("正在注册中...");
                dialog.show();
                new Sign(handler, SIGNUP, username, password1);
            } else {
                signup_error.setText("两次输入的密码不一致");
                YoYo.with(Techniques.Shake).duration(700).playOn(signup_pass1);
                YoYo.with(Techniques.Shake).duration(700).playOn(signup_pass2);
            }
        }
    };

    public static SignFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SignFragment fragment = new SignFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("page", "" + mPage);
        View view;
        if (mPage == SIGNIN) {
            view = inflater.inflate(R.layout.signin, container, false);
            setSignInView(view);
        } else {
            mMaterialDialog3 = new MaterialDialog(getActivity())
                    .setTitle("提示")
                    .setMessage("注册暂未开放，请使用测试账号登录，详见说明文档。")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog3.dismiss();
                        }
                    });

            mMaterialDialog3.show();

            view = inflater.inflate(R.layout.signup, container, false);
            setSignUpView(view);
        }
        return view;
    }

    private void showUploadDialog() {
        dialogView = LayoutInflater.from(getActivity())
                .inflate(R.layout.cloud_dialog, null);

        Button click = (Button)dialogView.findViewById(R.id.cloud_upload);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();

                dialog2 = ProgressDialog.show(getActivity(), "正在上传", "请稍等", true, true);
                CloudBackup.getInstance().upload(getActivity(), getAllFileNames(),
                        userID, handler, UPLOAD);
            }
        });

        Button click2 = (Button)dialogView.findViewById(R.id.cloud_download);
        click2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();

                dialog2 = ProgressDialog.show(getActivity(), "正在下载", "请稍等", true, true);
                CloudBackup.getInstance().download(getContext(), handler, DOWNLOAD, userID);
            }
        });

        mMaterialDialog = new MaterialDialog(getActivity())
                .setTitle("云备份")
                .setMessage("")
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

        mMaterialDialog.setContentView(dialogView);
        mMaterialDialog.show();
    }

    private void setSignInView(View view) {
        signin_user = (EditText)view.findViewById(R.id.signin_user);
        signin_pass = (EditText)view.findViewById(R.id.signin_pass);
        signin_error = (TextView)view.findViewById(R.id.signin_error);
        signin_btn = (Button)view.findViewById(R.id.signin_btn);
        signin_btn.setOnClickListener(signinListener);
    }

    public void setSignUpView(View view) {
        signup_user = (EditText)view.findViewById(R.id.signup_user);
        signup_pass1 = (EditText)view.findViewById(R.id.signup_pass1);
        signup_pass2 = (EditText)view.findViewById(R.id.signup_pass2);
        signup_error = (TextView)view.findViewById(R.id.signup_error);
        signup_btn = (Button)view.findViewById(R.id.signup_btn);
        // signup_btn.setOnClickListener(signupListener);
        signup_btn.setClickable(false);
        signup_btn.setBackgroundColor(Color.parseColor("#5C6457"));
    }

    private ArrayList<String> getAllFileNames() {
        ArrayList<String> list = new ArrayList<>();
        File root = new File("/data/data/com.crossbow.app.x_timer/files");
        File[] files = root.listFiles();

        if (files == null || files.length == 0) return list;

        for (File file : files) {
            String name = file.getName();
            if (name.startsWith("flag_") || name.equals("appList")) {
                list.add(name);
            }
        }

        return list;
    }

}
