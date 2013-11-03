package tw.kewang.tile.samples.feature;

import tw.kewang.tile.Sliding;
import tw.kewang.tile.Tile;
import tw.kewang.tile.TileCell;
import tw.kewang.tile.TileGroup;
import tw.kewang.tile.TileGroup.CellProperty;
import tw.kewang.tile.samples.BaseActivity;
import tw.kewang.tile.samples.R;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;

public class Basic extends BaseActivity {
	private int i = 0;
	private Tile tile2;
	private TileGroup group1;
	private Sliding s1;
	private Button btnAddGroup;
	private Button btnAddWidget;
	private Button btnAddProperty;
	private Button btnAddCell;
	private Button btnRemoveCell;

	@Override
	public int getLayoutId() {
		return R.layout.basic;
	}

	@Override
	public void findView() {
		tile2 = (Tile) findViewById(R.id.tile_2);
		group1 = (TileGroup) findViewById(R.id.group_1);
		s1 = (Sliding) findViewById(R.id.s1);
		btnAddGroup = (Button) findViewById(R.id.button_add_group);
		btnAddWidget = (Button) findViewById(R.id.button_add_widget);
		btnAddProperty = (Button) findViewById(R.id.button_add_property);
		btnAddCell = (Button) findViewById(R.id.button_add_cell);
		btnRemoveCell = (Button) findViewById(R.id.button_remove_cell);
	}

	@Override
	public void setView() {
	}

	@Override
	public void setListener() {
		tile2.setOnCellLongClickListener(new Tile.OnLongClickListener() {
			@Override
			public void onLongClick(TileGroup group, TileCell cell) {
				tile2.startDrag();
			}
		});

		btnAddWidget.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addWidget(500, 30, 200, 200);
			}
		});

		btnAddProperty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CellProperty property = new TileGroup.CellProperty();

				property.width = 50;
				property.height = 50;
				property.location = new Point(50 * i, 50 * i++);

				// s1.getCurrentGroup().addCellProperty(property);
				group1.addCellProperty(property);
			}
		});

		btnAddCell.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// s1.getCurrentGroup().addCell(getCell());
				group1.addCell(getCell());
			}
		});

		btnRemoveCell.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}

	@Override
	public void doExtra() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());
		}

		ViewServer.get(this).addWindow(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		ViewServer.get(this).setFocusedWindow(this);
	}

	@Override
	protected void onDestroy() {
		ViewServer.get(this).removeWindow(this);

		super.onDestroy();
	}

	private void addWidget(int width, int height, int x, int y) {
		TextView txtView = new TextView(this);

		txtView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
		txtView.setBackgroundColor(Color.MAGENTA);
		txtView.setText("this is a test");
		txtView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(Basic.this, ((TextView) v).getText().toString(),
						Toast.LENGTH_SHORT).show();
			}
		});

		group1.addWidget(txtView, x, y);
	}

	private TileCell getCell() {
		TileCell cell = new TileCell(this);

		cell.setBackgroundColor(Color.rgb((int) (Math.random() * 0xff),
				(int) (Math.random() * 0xff), (int) (Math.random() * 0xff)));

		return cell;
	}
}