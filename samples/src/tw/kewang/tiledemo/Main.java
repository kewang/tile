package tw.kewang.tiledemo;

import tw.kewang.tiledemo.feature.BasicLayout1;
import tw.kewang.tiledemo.feature.BasicLayout2;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Main extends BaseActivity implements OnItemClickListener {
	private ListView list;

	@Override
	public int getLayoutId() {
		return R.layout.main;
	}

	@Override
	public void findView() {
		list = (ListView) findViewById(R.id.list);
	}

	@Override
	public void setView() {
	}

	@Override
	public void setListener() {
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			startActivity(new Intent(this, BasicLayout1.class));

			break;
		case 1:
			startActivity(new Intent(this, BasicLayout2.class));

			break;
		}
	}
}