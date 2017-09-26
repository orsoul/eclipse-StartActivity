package com.fanfull.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SocketConnet;

/**
 * @ClassName: TipDialog
 * @Description: 提示信息的显示
 * @author Keung
 * @date 2014-9-17 上午10:09:51
 */
public class TipDialog {
	private final static String TAG = "TipDialog";
	public Context context;
	public static Activity activity;

	public TipDialog() {
	}

	/**
	 * 
	 * @Title: crateDailog
	 * @Description: 一般的Dialog
	 * @param @param context 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	/**
	 * @param context
	 * @param flag
	 *            = 0表示点击对话框的确定按钮后无 任何操作, = 1 表示 点击确定后退出当前Activity
	 * @Description: 一般的Dialog
	 */
	public void createDialog(final Context context, final int flag) {
		activity = (Activity) context;
		if (!ReplyParser.waitReply() || (null != activity && activity.isFinishing()) ) {
			return;
		}
		final String info = ReplyParser.parseReply(StaticString.information);
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(info);
		builder.setIcon(activity.getResources().getDrawable(R.drawable.dialog_title_alarm_48));
		builder.setTitle("提示");

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (flag == 0) {
					// nothing
				} else if (flag == 1) {
					// Intent intent = new Intent(context,
					// GeneralActivity.class);
					// context.startActivity(intent);
					activity.finish();
				}
			}
		});
		builder.create().show();
	}
	
	public void createDialog(final Context context, final String info,final int flag) {
		activity = (Activity) context;
		if ( null != activity && activity.isFinishing() ) {
			return;
		}
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(info);
		builder.setTitle("提示");
		builder.setIcon(R.drawable.dialog_title_alarm_48);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (flag == 0) {
					arg0.dismiss();
					// nothing
				} else if (flag == 1) {
					// Intent intent = new Intent(context,
					// GeneralActivity.class);
					// context.startActivity(intent);
					activity.finish();
				}
			}
		});
		builder.create().show();
	}

	public void createExitDialog(final Context context, final String info) {
		activity = (Activity) context;
		if ( null != activity && activity.isFinishing() ) {
			return;
		}
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(info);
		builder.setTitle("提示");
		builder.setIcon(R.drawable.dialog_title_alarm_48);
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.finish();
			}
		});
		builder.create().show();
	}
	
	
	/**
	 * @param act
	 * @param click2Finish
	 * @description 根据后台回复信息 弹出相应提示
	 */
	public void showReplyDialog(final Activity act, final int click2Finish) {
		
		String info = null;
		info = ReplyParser.parseReply(StaticString.information);
		AlertDialog.Builder builder = new Builder(act);
		builder.setTitle("提示")
		.setMessage(info)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (click2Finish == 0) {
					// nothing
				} else if (click2Finish == 1) {
					activity.finish();
				}
			}
		});
		builder.create().show();
	}

	/**
	 * 
	 * @Title: crateDailog
	 * @Description: 出库开袋后的提示
	 * @param @param context 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void createDailog() {
		if (!ReplyParser.waitReply()) {
			return;
		}
		// String info = JudgeReplay.judgeScan(StaticString.information);
		String info = ReplyParser.parseReply(StaticString.information);
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(info);
		builder.setIcon(context.getResources().getDrawable(R.drawable.dialog_title_alarm_48));
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// StaticString.soundPool.play(1, 1, 1, 0, 0, 1);
				arg0.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 
	 * @Title: outOpen
	 * @Description: 出库开袋
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void outOpen(final Context context) {
		this.context = context;
		activity = (Activity) context;
		if (!ReplyParser.waitReply()) {
			return;
		}
		String info = StaticString.information;
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("提示");
		int info27 = Integer.valueOf(info.substring(4, 6));
		switch (info27) {
		case 0:
			builder.setMessage("当前批不存在");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// StaticString.soundPool.play(1,1, 1, 0, 0, 1);
							arg0.dismiss();
							activity.finish();
						}
					});

			break;
		case 1:
			builder.setMessage("是否出库并开袋");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// StaticString.soundPool.play(1,1, 1, 0, 0, 1);
							arg0.dismiss();
							StaticString.information = null;
							SocketConnet.getInstance().communication(34);
							createDailog();
						}
					});
			builder.setNegativeButton("否",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// StaticString.soundPool.play(1,1, 1, 0, 0, 1);
							arg0.dismiss();
							activity.finish();
						}
					});
			break;
		case 3:
			builder.setMessage("未达计划数量，不能手持结束批");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// StaticString.soundPool.play(1,1, 1, 0, 0, 1);
							arg0.dismiss();
							createDialog(context, 1);
						}
					});
			break;
		default:
			builder.setMessage("锁定批失败");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// StaticString.soundPool.play(1,1, 1, 0, 0, 1);
							activity.finish();
							arg0.dismiss();
						}
					});
			break;
		}
		builder.create().show();
	}

	/**
	 * 
	 * @Title: changeDailog
	 * @Description: 换袋一般的Dialog
	 * @param @param context 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void changeDailog(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("请先选择换袋原因");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// StaticString.soundPool.play(1, 1, 1, 0, 0, 1);
				arg0.dismiss();
			}
		});
		builder.create().show();
	}
}
