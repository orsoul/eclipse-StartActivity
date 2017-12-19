package com.fanfull.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.fanfull.background.ActivityUtil;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.socket.ReplyParser;

public class DialogUtil {
	private Activity mAct;

	private ProgressDialog mProgressDia;
	private AlertDialog mAlertDialog;

	public DialogUtil(Activity act) {
		mAct = act;
	}

	private void createProgressDialog() {
		mProgressDia = new ProgressDialog(mAct);
		mProgressDia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDia.setIndeterminate(false);
		mProgressDia.setCancelable(false);
		mProgressDia.setCanceledOnTouchOutside(true);
	}

	public void setProgressDialogTitle(CharSequence text) {
		if (null == mProgressDia) {
			createProgressDialog();
		}
		mProgressDia.setTitle(text);
	}

	public void setProgressDialogCancelListener(OnCancelListener listener) {
		if (null == mProgressDia) {
			createProgressDialog();
		}
		mProgressDia.setOnCancelListener(listener);
	}

	public void showProgressDialog() {
		showProgressDialog(null);
	}

	public void showProgressDialog(CharSequence message) {
		if(mAct == null || !ActivityUtil.isForeground(mAct.getLocalClassName())){
			return;
		}
		if (null == mProgressDia) {
			createProgressDialog();
		}
		mProgressDia.setMessage(message);

		if (!mProgressDia.isShowing()) {
			mProgressDia.show();
		}
	}

	public boolean progressDialogIsShowing() {
		if (null == mProgressDia) {
			return false;
		}
		return mProgressDia.isShowing();
	}

	public void dismissProgressDialog() {
		if (mProgressDia != null && mProgressDia.isShowing()) {
			mProgressDia.dismiss();
		}
	}

	/**
	 * 点击 确定后 退出 当前 activity
	 * 
	 * @param context
	 * @param info
	 */
	public void showDialogFinishActivity(String info) {
		if (null == mAct || mAct.isFinishing()) {
			LogsUtil.d("dialog is finish");
			return;
		}
		AlertDialog.Builder builder = new Builder(mAct);
		builder.setTitle("提示");
		builder.setMessage(info);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				mAct.finish();
			}
		});
		builder.show();
	}

	/**
	 * @param info
	 */
	public void showDialog(Object info) {
		if(mAct == null || !ActivityUtil.isForeground(mAct.getLocalClassName())){
			return;
		}
		if (null == mAlertDialog) {
			AlertDialog.Builder builder = new Builder(mAct);
			builder.setIcon(R.drawable.dialog_title_alarm_48)
					.setTitle(MyContexts.TEXT_DIALOG_TITLE)
					.setMessage(String.valueOf(info))
					.setNegativeButton(MyContexts.TEXT_OK, null);
			mAlertDialog = builder.create();
		} else {
			mAlertDialog.setMessage(String.valueOf(info));
		}
		mAlertDialog.show();
	}
	public void dismissAlertDialog() {
		if (mAlertDialog != null && mAlertDialog.isShowing()) {
			mAlertDialog.dismiss();
		}
	}

	/**
	 * @param info 
	 * @param posText 确定按钮 文字
	 * @param negText 取消按钮 文字
	 * @param posListener
	 * @param negListener
	 */
	public void showDialog2Button(String info, String posText, String negText, DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener) {
		if (null == mAct || mAct.isFinishing()) {
			LogsUtil.d("dialog is finish");
			return;
		}
		AlertDialog.Builder builder = new Builder(mAct);
		builder.setTitle(MyContexts.TEXT_DIALOG_TITLE);
		builder.setMessage(info);
		builder.setPositiveButton(posText, posListener);
		builder.setNegativeButton(negText, negListener);
		builder.show();
	}

	/**
	 * 弹出 服务器回复 信息
	 */
	public String showReplyDialog() {
		String info = ReplyParser.parseReply(StaticString.information);
		showDialog(info);
		return info;
	}

	public void showNegativeReplyDialog(String info, String btnText) {
		if (null == mAct || mAct.isFinishing()) {
			LogsUtil.d("dialog is finish");
			return;
		}
		AlertDialog.Builder builder = new Builder(mAct);
		builder.setIcon(R.drawable.dialog_title_alarm_48);
		builder.setTitle(MyContexts.TEXT_DIALOG_TITLE);
		builder.setMessage(info);
		builder.setNegativeButton(btnText, null);
		builder.show();
	}

	/**
	 * 点击 确定后 退出 当前 activity
	 * 
	 * @param context
	 * @param info
	 */
	public void showPostiveReplyDialog(Context context, String info) {
		final Activity activity = (Activity) context;
		if (null == mAct || mAct.isFinishing()) {
			LogsUtil.d("dialog is finish");
			return;
		}
		AlertDialog.Builder builder = new Builder(mAct);
		builder.setTitle("提示");
		builder.setMessage(info);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				activity.finish();
			}
		});
		builder.show();
	}
	public void showSalverInfoDialog(String money, String bagNum, DialogInterface.OnClickListener listener) {
		if (null == mAct || mAct.isFinishing()) {
			LogsUtil.d("dialog is finish");
			return;
		}
		AlertDialog.Builder builder = new Builder(mAct);
		builder.setTitle("托盘信息确认");
		builder.setMessage(money + " 圆	" + bagNum + " 袋");
		builder.setNegativeButton(MyContexts.TEXT_CANCEL, null);
		builder.setPositiveButton(MyContexts.TEXT_OK, listener);
		builder.show();
	}

	public void destroy() {
		dismissProgressDialog();
		dismissAlertDialog();
		mProgressDia = null;
		mAlertDialog = null;
		mAct = null;
	}

}
