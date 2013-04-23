package tw.kewang.tiledemo.feature;

import tw.kewang.tiledemo.BaseActivity;
import tw.kewang.tiledemo.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BasicLayout2 extends BaseActivity {
	private Button btnOuter;
	private Button btnInner;

	@Override
	public int getLayoutId() {
		return R.layout.basic_layout_2;
	}

	@Override
	public void findView() {
		btnOuter = (Button) findViewById(R.id.button_outer);
		btnInner = (Button) findViewById(R.id.button_inner);
	}

	@Override
	public void setView() {
	}

	@Override
	public void setListener() {
		btnOuter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(BasicLayout2.this, "This button name is Outer",
						Toast.LENGTH_SHORT).show();
			}
		});

		btnInner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(BasicLayout2.this, "This button name is Inner",
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}