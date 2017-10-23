package com.fanfull.activity.setting;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.adapter.CommonAdapter;
import com.fanfull.background.ActivityUtil;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.db.Finger;
import com.fanfull.db.FingerDbService;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.FingerManager;
import com.fanfull.hardwareAction.FingerManager.FingerListener;
import com.fanfull.hardwareAction.FingerPrint;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.TipDialog;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;

public class SettingFingerPrintActivity extends BaseActivity {
	private final String TAG = SettingFingerPrintActivity.class.getSimpleName();

	private final static int MESSAGE_FINGER_PRINT_ADD = 0;
	private final static int MESSAGE_FINGER_REGMODEL_ERROR = 3;
	private final static int MESSAGE_FINGER_CONNECT_FAILED = 4;
	private final static int MESSAGE_FINGER_EMPTY_ERROR = 5;
	private final static int MESSAGE_FINGER_EMPTY_SUCCESS = 6;
	private final static int MESSAGE_FINGER_REGMODEL_NET = 7;
	private final static int MESSAGE_FINGER_EMPTY_CONFIRM = 10;
	private final static int MESSAGE_GET_VERSION_SUCCESS = 11;
	private final static int MESSAGE_GET_VERSION_TIMEOUT = 12;
	private final static int MESSAGE_GET_VERSION_FAILED = 13;
	
	private static final int FINGER_NOT_UPDATE = 18;// 已经是最新版指纹，不需要更新
	private static final int FINGER_UPDATE_PROGRESS = 19;// 更新指纹更新的进度
	private static final int MESS_SHOW_UPDATE_DIALOG = 20;// 显示更新对话框
	private static final int FINGER_UPDATE_HAPPEND_ERROR = 21;// 更新指纹过程中出现错误

	private FingerPrintEmptyTask mFingerPrintEmptyTask;
	private FingerPrintVersionTask mFingerPrintVersionTask;
	private UpdateFingerTask mUpdateFingerTask;// 更新指纹任务

	private ListView mListView;
	private View  mFootView;
	private CommonAdapter<Finger> mFullAdapter;
	private TextView mAddTextView;
	private Button mEmptyButton;
	private Button mUpdateToServerButton;
	private TextView mModfyTextView;

	private Long mCurrentFingerId = (long) -1;
	private boolean haveTask = false;
	private int mCurrentDeletId = 0;

	// 指纹操作
	private FingerPrint mFingerPrint;

	// 2016-04-20 增加指纹操作
	private FingerManager mfManager;

	private List<Finger> mFingerList = new ArrayList<Finger>();

	private int mNumCount = 1;// 记录添加和更新时，需要；连续增加三个指纹

	private int mVersion = 0;

	private FingerDbService mDbService;
	private DialogUtil mDiaUtil;
	
	/* 指纹更新进度条 */
	private ProgressBar mFingerProgress;
	private Dialog mFingerUpdateDialog;
	private int mFingerProgressValue = 0;
	


	/**
	 * 指纹增加成功，三个指纹添加还没实现
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting_fingerprint);
		ActivityUtil.getInstance().addActivityToList(this);
		initview();
		initdata();
	}

	protected void initview() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.finger_list);
		mFootView = View.inflate(getApplicationContext(), R.layout.xlistview_footer, null);
		
		mAddTextView = (TextView)mFootView.findViewById(R.id.add_finger_tv);
		mModfyTextView = (TextView) mFootView.findViewById(R.id.modfy_finger_tv);
		ViewUtil.requestFocus(mAddTextView);

		mEmptyButton = (Button) findViewById(R.id.btn_setting_empty_finger);
		mUpdateToServerButton = (Button) findViewById(R.id.btn_setting_up_from_server_finger);

		mAddTextView.setOnClickListener(mOnClickListener);
		mEmptyButton.setOnClickListener(mOnClickListener);
		mUpdateToServerButton.setOnClickListener(mOnClickListener);
		mModfyTextView.setOnClickListener(mOnClickListener);
	}

	protected void initdata() {
		// TODO Auto-generated method stub
		mDbService = FingerDbService.getInstance(this);
		mDiaUtil = new DialogUtil(this);
		mFingerPrint = FingerPrint.getInstance();

		mFingerPrintEmptyTask = new FingerPrintEmptyTask();
		mFingerPrintVersionTask = new FingerPrintVersionTask();
		mUpdateFingerTask = new UpdateFingerTask();
		mFullAdapter = new CommonAdapter<Finger>(getApplicationContext(),
				mFingerList, R.layout.item_fingerprint) {

			@Override
			public void convert(final com.android.adapter.ViewHolder helper,
					final Finger item, boolean isSelect) {
				// TODO Auto-generated method stub
				helper.setText(R.id.tv_setting_item_set_id, item.getFinger_id()
						+ "");
				helper.setText(R.id.tv_setting_item_set_name, item.getUser_id());
				helper.setText(R.id.tv_setting_item_set_version,
						item.getFinger_version() + "");
			}
		};
		if(mFingerList.size() < 3){
			mListView.addFooterView(mFootView);
		}
		mListView.setAdapter(mFullAdapter);
		mfManager = FingerManager.getInstance();
		mfManager.registListener(mFingerListener);

		ThreadPoolFactory.getNormalPool().execute(mFingerPrintVersionTask);

		if (!mFingerPrint.getIsinit()) {
			ThreadPoolFactory.getNormalPool().execute(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mFingerPrint.open();
				}
			});
		}

	}

	public void onClickBack(View v) {
		finish();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new Builder(
					SettingFingerPrintActivity.this);
			switch (msg.what) {
			case MESSAGE_FINGER_PRINT_ADD:
				mDiaUtil.dismissProgressDialog();
				String strText = "";
				Finger fingerPrint = new Finger();
				fingerPrint.setUser_id(StaticString.orgId
						+ StaticString.userLast3Id);
				fingerPrint.setFinger_id(Integer
						.parseInt("" + mCurrentFingerId));
				fingerPrint.setFinger_version(mVersion + 1);
				fingerPrint.setFinger_sno(mNumCount);
				mDbService.saveFinger(fingerPrint);
				mCurrentFingerId = (long) -1;
				strText = "第" + (mNumCount) + "个指纹注册成功";
				mNumCount++;

				// 更新最新列表
				mFingerList.clear();
				mFingerList
						.addAll(mDbService
								.queryFingerByUserId((StaticString.orgId + StaticString.userLast3Id)));
				mFullAdapter.notifyDataSetChanged();

				builder.setTitle("完成");
				builder.setMessage(strText);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								mCurrentFingerId = mDbService.getKeyIdLong();
								arg0.dismiss();
							}
						});
				builder.create().show();
				if (4 == mNumCount) {
					mAddTextView.setVisibility(View.GONE);
					mModfyTextView.setVisibility(View.VISIBLE);
				}
				break;
			case MESSAGE_FINGER_REGMODEL_NET:
				ToastUtil.showToastInCenter("可以松开手指，后台确认");
				break;

			case MESSAGE_FINGER_REGMODEL_ERROR:
				mDiaUtil.dismissProgressDialog();
				haveTask = false;
				createDialog("添加指纹失败", "重试", MESSAGE_FINGER_REGMODEL_ERROR);
				break;

			case MESSAGE_FINGER_EMPTY_CONFIRM:
				if (SettingFingerPrintActivity.this.isFinishing()) {
					LogsUtil.d(TAG, "activity is fininshed!");
					return;
				}
				if (mFingerList.size() < 1)
					return;
				mFingerPrintEmptyTask.setdelFinger(mFingerList
						.get(mCurrentDeletId));
				builder.setTitle("提示");
				builder.setMessage("是否清空指纹！");
				builder.setPositiveButton("是",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								ThreadPoolFactory.getNormalPool().execute(
										mFingerPrintEmptyTask);
								arg0.dismiss();
							}
						});
				builder.setNegativeButton("否",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
							}
						});
				builder.create().show();
				break;

			case MESSAGE_FINGER_EMPTY_ERROR:
				mEmptyButton.setText("清空");
				createDialog("清空指纹失败", "重试", MESSAGE_FINGER_EMPTY_ERROR);
				break;

			case MESSAGE_FINGER_CONNECT_FAILED:
				LogsUtil.d(TAG, "----指纹模块启动失败-----");
				// crateDialog ("指纹模块启动失败");
				break;

			case MESSAGE_FINGER_EMPTY_SUCCESS:
				mCurrentDeletId = 0;
				mNumCount = 1;

				mFingerList.clear();
				mFingerList
						.addAll(mDbService
								.queryFingerByUserId((StaticString.orgId + StaticString.userLast3Id)));
				mFullAdapter.notifyDataSetChanged();
				if (mFingerList.size() == 0) {
					mModfyTextView.setVisibility(View.GONE);
					mAddTextView.setVisibility(View.VISIBLE);
				}
				break;

			case MESSAGE_GET_VERSION_SUCCESS:
				mVersion = Integer
						.parseInt(StaticString.information.split(" ")[2]);
				mFingerList.clear();
				mFingerList
						.addAll(mDbService
								.queryFingerByUserId((StaticString.orgId + StaticString.userLast3Id)));
				LogsUtil.d("mFingerList.size=" + mFingerList.size()
						+ " mVersion = " + mVersion);
				if (mFingerList.size() >= 1 && mVersion == 0) {
					// 冲突，本地有数据，但服务器提供的版本又为0；
					mDbService.deleteFingerByCardid(mFingerList);
					mFingerList.clear();
					mFingerList
							.addAll(mDbService
									.queryFingerByUserId((StaticString.orgId + StaticString.userLast3Id)));
				} else if (mFingerList.size() >= 1 && mVersion > 0) {
					// 服务器有数据,本地也有数据
					mModfyTextView.setVisibility(View.VISIBLE);
					mAddTextView.setVisibility(View.GONE);
				}
				mFullAdapter.notifyDataSetChanged();
				break;

			case MESSAGE_GET_VERSION_TIMEOUT:
				 new TipDialog().createDialog(SettingFingerPrintActivity.this,"从服务器获取指纹数据失败！", 0);
				break;
			case MESSAGE_GET_VERSION_FAILED:
				 new TipDialog().createDialog(SettingFingerPrintActivity.this,"电脑数据出错！", 0);
				break;
			case FINGER_NOT_UPDATE:
				 new TipDialog().createDialog(SettingFingerPrintActivity.this,"当前指纹是最新版，无需更新！", 0);
				break;
			default:
				break;
			}
		}
	};

	private void createDialog(String info, String info2, final int n) {
		if (this.isFinishing()) {
			LogsUtil.d(TAG, "activity is fininshed!");
			return;
		}
		AlertDialog.Builder builder = new Builder(
				SettingFingerPrintActivity.this);
		builder.setMessage(info);
		builder.setTitle("提示");
		builder.setPositiveButton(info2, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch (n) {
				case MESSAGE_FINGER_REGMODEL_ERROR:
					if (!haveTask) {
						mDiaUtil.showProgressDialog();
						ThreadPoolFactory.getNormalPool().execute(
								new FingerPrintAddTask());
					}
					break;
				case MESSAGE_FINGER_EMPTY_ERROR:
					if (!haveTask) {
						mFingerPrintEmptyTask.setdelFinger(mFingerList
								.get(mCurrentDeletId));
						ThreadPoolFactory.getNormalPool().execute(
								mFingerPrintEmptyTask);
					}
					break;
				default:
					break;
				}
				arg0.dismiss();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		LogsUtil.d(TAG, "mNumCount=" + mNumCount);
		if (mNumCount < 3)
			new TipDialog().createExitDialog(this, "尚未注册完三个指纹，是否继续完成注册？");
		else
			super.onBackPressed();
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_setting_up_from_server_finger:
				if (!haveTask) {
					ThreadPoolFactory.getNormalPool().execute(mUpdateFingerTask);
					haveTask = true;
				}
				break;
			case R.id.add_finger_tv:
				if (!haveTask) {
					mDiaUtil.showProgressDialog();
					ThreadPoolFactory.getNormalPool().execute(
							new FingerPrintAddTask());
				}
				break;
			case R.id.btn_setting_empty_finger:
				if (mFingerList.size() == 0) {
					ToastUtil.showToastInCenter("尚未注册指纹");
					return;
				}
				mHandler.sendEmptyMessage(MESSAGE_FINGER_EMPTY_CONFIRM);
				break;
			case R.id.modfy_finger_tv:
				mFingerPrintEmptyTask.setdelFinger(mFingerList
						.get(mCurrentDeletId));
				ThreadPoolFactory.getNormalPool()
						.execute(mFingerPrintEmptyTask);
				mModfyTextView.setVisibility(View.GONE);
				mAddTextView.setVisibility(View.VISIBLE);
				mAddTextView.performClick();
				break;
				
			case FINGER_UPDATE_PROGRESS:
				// 指纹进度更新
				// 设置进度条位置
				LogsUtil.d(TAG, "value=" + mFingerProgressValue);
				mFingerProgress.setProgress(mFingerProgressValue);
				if (100 == mFingerProgressValue) {
					mFingerUpdateDialog.dismiss();
					mFingerPrint.startSearchFinger();
				}
				break;
			case MESS_SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case FINGER_UPDATE_HAPPEND_ERROR:
				new TipDialog().createDialog(SettingFingerPrintActivity.this,
						"未完成，更新指纹出错！", 0);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*
		 * 修改 back键的默认事件
		 */
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_4) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityUtil.getInstance().removeActivityFromList(this);
		if (null != mFingerPrint) {
			mfManager.unregistListener(mFingerListener);
		}
	}

	private FingerListener mFingerListener = new FingerListener() {

		@Override
		public void openFingerSerialPortSuccess(boolean flag) {
			// TODO Auto-generated method stub
			LogsUtil.d(TAG, "打开指纹串口:" + flag);
			if (!flag) {
				mHandler.sendEmptyMessage(MESSAGE_FINGER_CONNECT_FAILED);
			}
		}

		@Override
		public void getLocalFingerSucess(int n) {
			// TODO Auto-generated method stub
		}

		@Override
		public void getLocalFingerError() {
			// TODO Auto-generated method stub
		}

		@Override
		public void getLocalFingerNoData() {
			// TODO Auto-generated method stub
		}

		@Override
		public void deleteFingerNmber(boolean flag) {
			// TODO Auto-generated method stub
			if (flag) {
				mDbService.deleteFinger(mFingerList.get(mCurrentDeletId));
				if (++mCurrentDeletId < mFingerList.size()) {
					mFingerPrintEmptyTask.setdelFinger(mFingerList
							.get(mCurrentDeletId));
					ThreadPoolFactory.getNormalPool().execute(
							mFingerPrintEmptyTask);
				} else {
					mHandler.sendEmptyMessage(MESSAGE_FINGER_EMPTY_SUCCESS);
				}
			} else {
				mHandler.sendEmptyMessage(MESSAGE_FINGER_EMPTY_ERROR);
			}
		}

		@Override
		public void emptyFinger(boolean flag) {
			// TODO Auto-generated method stub
		}

		@Override
		public void addFingerData(int flag, String info) {
			// TODO Auto-generated method stub
			if (flag == 1) {
				for (int i = 0; i < 2; i++) {// 如果第一次超时则发送两次
					StaticString.information = null;
					SocketConnet.getInstance()
							.communication(
									999,
									new String[] { mNumCount + "",
											mVersion + "", info });
					if (!ReplyParser.waitReply()) {
						continue;
					}
					String str[] = StaticString.information.split(" ");
					if ("01".equals(str[2])) {// 注册成功
						mHandler.sendEmptyMessage(MESSAGE_FINGER_PRINT_ADD);
						return;
					} else {
						mHandler.sendEmptyMessage(MESSAGE_FINGER_REGMODEL_ERROR);
						return;
					}
				}
				mHandler.sendEmptyMessage(MESSAGE_FINGER_REGMODEL_ERROR);
				return;
			} else if (2 == flag) {
				// 可以松开手指，后台确认
				SoundUtils.playInitSuccessSound();
			} else {
				// 获取缓冲区指纹特征码失败
				mHandler.sendEmptyMessage(MESSAGE_FINGER_REGMODEL_ERROR);
			}
		}

		@Override
		public void stopSearchFinger(boolean flag) {
			// TODO Auto-generated method stub

		}
	};

	class FingerPrintEmptyTask implements Runnable {
		private Finger finger;

		public void setdelFinger(Finger finger) {
			this.finger = finger;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 逐一删除持卡人的指纹
			haveTask = true;
			mFingerPrint.deleteFingerNmber(finger.getFinger_id());
			haveTask = false;
		}
	}

	// 查询服务器当前版本
	class FingerPrintVersionTask implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			haveTask = true;
			for (int i = 0; i < 2; i++) {
				SocketConnet.getInstance().communication(998, null);// 得到版本信息
				if (!ReplyParser.waitReply()) {
					continue;
				}
				haveTask = false;
				if (StaticString.information.startsWith("*02")) {
					// 电脑处理数据异常
					mHandler.sendEmptyMessage(MESSAGE_GET_VERSION_FAILED);
					break;
				}
				mHandler.sendEmptyMessage(MESSAGE_GET_VERSION_SUCCESS);
				return;
			}
			haveTask = false;
			mHandler.sendEmptyMessage(MESSAGE_GET_VERSION_TIMEOUT);
		}
	}

	class FingerPrintGetAllTask implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			haveTask = true;
			int t = mFingerPrint.getAllFingerNmber();
			if (-1 != t) {
			} else {
				mHandler.sendEmptyMessage(MESSAGE_FINGER_EMPTY_ERROR);
			}
			haveTask = false;
		}
	}

	public class FingerPrintDeleteTask implements Runnable {
		private byte delId;

		public FingerPrintDeleteTask(byte id) {
			super();
			// TODO Auto-generated constructor stub
			this.delId = id;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			haveTask = true;
			for (int i = 0; i < 2; i++) {// 如果第一次超时则发送两次
				StaticString.information = null;
				SocketConnet.getInstance().communication(1002,
						new String[] { mVersion + "", mNumCount + "" });
				if (!ReplyParser.waitReply()) {
					continue;
				}
				mFingerPrint.deleteFingerNmber(delId);
				haveTask = false;
				break;
			}
		}
	}

	class FingerPrintAddTask implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			haveTask = true;
			if (-1 == mCurrentFingerId) {
				mCurrentFingerId = mDbService.getKeyIdLong();
			}
			LogsUtil.d(TAG, "---FingerPrintAddTask---mCurrentFingerId="
					+ mCurrentFingerId);
			mFingerPrint
					.addFingerPrint(Integer.parseInt(mCurrentFingerId + ""));
			haveTask = false;
		}

	}
	
	/**
	 * 显示指纹更新对话框
	 */
	private void showUpdateDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(SettingFingerPrintActivity.this);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(SettingFingerPrintActivity.this);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mFingerProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		builder.setPositiveButton("确定", null);
		builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if (mUpdateFingerTask != null) {
					mUpdateFingerTask.stop();// 取消更新
				}
			}
		});
		mFingerUpdateDialog = builder.create();
		mFingerUpdateDialog.show();
	}
	
	/**
	 * 比较从服务器获得到用户信息，与本地数据库中的信息，得到差异的部分
	 * 
	 * @param serverFingers
	 * @param clist
	 * @return
	 */
	private List<Finger> compareServerToClient(List<Finger> serverFingers,
			List<Finger> clist) {
		// 本地没有指纹，则全使用服务器的信息
		if (clist.size() == 0)
			return serverFingers;

		List<Finger> list = new ArrayList<Finger>();
		int i = 0;
		for (i = 0; i < serverFingers.size(); i++) {
			Finger sFinger = serverFingers.get(i);
			int j = 0;
			for (j = 0; j < clist.size(); j++) {
				Finger cFinger = clist.get(j);
				if (sFinger.getUser_id().equals(cFinger.getUser_id())
						&& sFinger.getFinger_version() == cFinger
								.getFinger_version())
					break;
			}
			if (j == clist.size()) {
				// 表示本地没有该用户的该版本
				list.add(sFinger);
			}
		}
		return list;
	}

	
	/**
	 * 
	 * @ClassName: UpdateFingerTask
	 * @Description: 更新指纹信息
	 * @author Keung
	 * @date 2015-3-2 下午02:39:14
	 * 
	 */
	class UpdateFingerTask implements Runnable {
		private boolean stoped;
		private int version;// 总版本数

		public void stop() {
			stoped = true;
		}

		@SuppressLint("SdCardPath")
		@Override
		public void run() {
			/** 最先检测，需不需要更新指纹 */
			int fingerUpdateVersionId = SPUtils.getInt(
					MyContexts.KEY_FINGER_UPDATE_VERSION, 0);
			/** 将当前本地的版本编号发给服务器 */
			StaticString.information = null;
			List<Finger> serverFingers = new ArrayList<Finger>();// 保存服务器的用户指纹信息
			int x = 0;
			for ( x = 0; x < 2; x++) {
				SocketConnet.getInstance().communication(1000,
						new String[] { fingerUpdateVersionId + "" });
				if (!ReplyParser.waitReply()) {
					continue;
				}
				if(StaticString.information.startsWith("*14 00 00")
						|| StaticString.information.startsWith("*14 00 66")){
					//服务器客户端版本一致 ，需要更新
					mHandler.sendEmptyMessage(FINGER_NOT_UPDATE);
					return;
				}
				/** 需要更新的情况下，则将获得服务器所有用户信息的最高版本 */
				// [{"userId":"userIddata1","version":"versionData1"},{"userId":"userIddata2","version":"versionData2"}]
				fingerUpdateVersionId = Integer
						.parseInt(StaticString.information.split(" ")[3]);// 拿到服务器的版本。
				try {
					JSONArray array;
					array = new JSONArray(
							StaticString.information.split(" ")[2]);
					for (int i = 0; i < array.length(); i++) {
						Finger finger = new Finger();
						finger.setUser_id(array.getJSONObject(i)
								.getString("UserID"));
						finger.setFinger_version(array.getJSONObject(i)
								.getInt("Version"));
						serverFingers.add(finger);
					}
					break;// 跳出循环
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(x == 2){
				/** 连接失败 */
			//更新视频
//				mHandler.sendEmptyMessage(CONNECT_FAILED);
				return;
			}

			/** 这是本地数据库中，已经存在的指纹，加载本地所有指纹和服务器匹配，挑选出，需要更新的用户指纹信息 */
			List<Finger> list = mDbService.loadAllFinger();
			List<Finger> comparList = compareServerToClient(serverFingers, list);
			LogsUtil.i(TAG, "comparList.size=" + comparList.size());
			if (comparList.size() == 0) {
				/** 不需要更新的情况下，不需要更新 */
				mHandler.sendEmptyMessage(FINGER_NOT_UPDATE);
				return;
			} else {
				mHandler.sendEmptyMessage(MESS_SHOW_UPDATE_DIALOG);
				/** 开始取差异的指纹信息 将差异的用户信息，一个一个发送给服务器 */
				for (int i = 0; i < comparList.size() && !stoped; i++) {
					/** -----------start------handle one finger-------- */
					LogsUtil.d(TAG, "--start:差异部分开始下载---");
					/** 循环发送差异部分 */
					int k = 1, m = 0, n = 0;
					for (k = 1; k < 4; k++) {// 一个人有三个指纹
						SocketConnet.getInstance().communication(
								1001,
								new String[] {
										comparList.get(i).getUser_id(),
										comparList.get(i).getFinger_version()
												+ "", k + "" });
						if (ReplyParser.waitReply()) {
							if (StaticString.information.startsWith("*14 00")) {
								LogsUtil.d("--no finger---" + "userid="
										+ comparList.get(i).getUser_id()
										+ " --k=" + k);
								continue;
							}
							LogsUtil.d("-----get one finger-success-----start downloader to fingerstore----");

							/**
							 * 从本地数据库中，获取一个唯一的指纹编号， 将服务器得到的指纹下载到手持中。
							 * 这里使用了一个临时的文件就是中间中转
							 */
							Long fingerId = mDbService.getKeyIdLong();
							LogsUtil.d(TAG, "指纹编号:"+fingerId);
							/** 开始下载到手持中 */
							if (mFingerPrint.downLoadChar(
									getApplicationContext(),
									Integer.parseInt(fingerId + ""),k+"",StaticString.information.split(" ")[6])) {
								/** 下载成功 */
								// *14 01 2(总数) 1（序号） 087101001002 1（版本）
								// 0301561500
								Finger finger = new Finger();
								finger.setFinger_id(Integer
										.parseInt(fingerId + ""));
								finger.setFinger_version(Integer
										.parseInt(StaticString.information
												.split(" ")[5]));
								finger.setUser_id(StaticString.information
										.split(" ")[4]);
								finger.setFinger_sno(Integer
										.parseInt(StaticString.information
												.split(" ")[3]));
								mDbService.saveFinger(finger);

								m = Integer
										.parseInt(StaticString.information
												.split(" ")[2]);
								n = Integer
										.parseInt(StaticString.information
												.split(" ")[3]);
								if (m == n) {
									break;
								}
							} else {
								/** 当次下载失败，进行失败处理 */
								mHandler.removeMessages(FINGER_UPDATE_HAPPEND_ERROR);
								mHandler.sendEmptyMessage(FINGER_UPDATE_HAPPEND_ERROR);
								return;
							}
						} else {
							continue;// 超时
						}
					}// end 每一个用户三个指纹
					/** 计算进度条位置 */
					mFingerProgressValue = (int) (((float) (i + 1) / comparList
							.size()) * 100);
					mHandler.removeMessages(FINGER_UPDATE_PROGRESS);
					mHandler.sendEmptyMessage(FINGER_UPDATE_PROGRESS);
				}
				/** -----------end------handle all users-------- */
				LogsUtil.d(TAG, "fingerUpdateVersionId="
						+ fingerUpdateVersionId);
				SPUtils.putInt(MyContexts.KEY_FINGER_UPDATE_VERSION,
						fingerUpdateVersionId);
			}

		}// end run()
	}

}
