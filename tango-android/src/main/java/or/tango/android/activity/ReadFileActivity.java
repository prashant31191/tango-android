package or.tango.android.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import or.tango.android.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReadFileActivity extends ListActivity {
	public static String PHOTO_PATH_KEY = "pic_path";//
	
	private List<String> items = null;
	private List<String> paths = null;
	private String rootpath = "/";
	private String defaultpath = Environment.getExternalStorageDirectory()+"/tango/insightech";
	private TextView mpath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readfile);
		mpath = (TextView) findViewById(R.id.mpath);
		File file = new File(defaultpath);
		if (!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
		getFileDir(defaultpath);
	}

	public void getFileDir(String filePath) {
		mpath.setText(filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filePath);
		File[] files = f.listFiles();

		if (!filePath.equals(rootpath)) {
			items.add("Back to" + rootpath);
			paths.add(rootpath);
			items.add("Back to ../");
			paths.add(f.getParent());
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			items.add(file.getName());
			paths.add(file.getPath());
		}
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.activity_readfile_item, items);
		setListAdapter(fileList);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(paths.get(position));
		if (file.canRead()) {
			if (file.isDirectory()) {
				getFileDir(paths.get(position));
			} 
			else {
				Intent intent = new Intent(ReadFileActivity.this, PicActivity.class);
				intent.putExtra(PHOTO_PATH_KEY, file.getAbsolutePath());
				startActivity(intent);
			}
		} else {
			new AlertDialog.Builder(this)
					.setTitle("Message")
					.setMessage("权限不够")
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
								}
							}).show();
		}
	}
}
