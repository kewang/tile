package tw.kewang.tiledemo;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutId());

		findView();
		setView();
		setListener();
		doExtra();
	}

	public void doExtra() {
	}

	public abstract int getLayoutId();

	public abstract void findView();

	public abstract void setView();

	public abstract void setListener();
}