package tw.kewang.ui.tile;

import java.util.ArrayList;
import tw.kewang.ui.tile.R;
import tw.kewang.ui.tile.GroupAdapter.OnRemoveItemListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;


/**
 * @author kewang
 * 
 */
public class Sliding extends ViewPager implements OnRemoveItemListener {
	private int holderSize;
	private Paint paint;
	private boolean autoFill;

	public Sliding(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
	}

	public Sliding(Context context) {
		super(context);

		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.TileAttrs);

			autoFill = a.getBoolean(R.styleable.TileAttrs_autoFill, false);

			a.recycle();
		}

		setAdapter(new GroupAdapter());

		((GroupAdapter) getAdapter()).setOnRemoveItemListener(this);
	}

	/**
	 * 移除第一個View
	 */
	public void removeAtFirst() {
		((GroupAdapter) getAdapter()).removeViewAtFirst();
	}

	/**
	 * 移除最後一個View
	 */
	public void removeAtLast() {
		((GroupAdapter) getAdapter()).removeViewAtLast();
	}

	/**
	 * 新增View
	 * 
	 * @param child
	 *            要新增的View
	 */
	public void add(TileGroup child) {
		((GroupAdapter) getAdapter()).addView(child);
	}

	/**
	 * 移除第index個View
	 * 
	 * @param index
	 *            第index個View
	 */
	public void removeAt(int index) {
		((GroupAdapter) getAdapter()).removeView(index);
	}

	/**
	 * @return 傳回holder的size
	 */
	public int getHolderSize() {
		return holderSize;
	}

	/**
	 * @param holderSize
	 *            設定holder的size
	 */
	public void setHolderSize(int holderSize) {
		this.holderSize = holderSize;
	}

	/**
	 * 移到下一頁
	 */
	public void next() {
		if (getCurrentItem() < getChildCount() - 1) {
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	/**
	 * 移到上一頁
	 */
	public void previous() {
		if (getCurrentItem() > 0) {
			setCurrentItem(getCurrentItem() - 1);
		}
	}

	public ArrayList<TileGroup> getGroups() {
		return ((GroupAdapter) getAdapter()).getGroups();
	}

	public TileGroup getGroup(int index) {
		return getGroups().get(index);
	}

	public TileGroup getCurrentGroup() {
		return getGroup(getCurrentItem());
	}

	public boolean canAutoFill() {
		return autoFill;
	}

	public void setAutoFill(boolean autoFill) {
		this.autoFill = autoFill;
	}

	public boolean isCellFull() {
		for (TileGroup group : getGroups()) {
			if (group.getCellPropertySize(true) != 0) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (paint == null) {
			paint = new Paint();

			paint.setAntiAlias(true);
			paint.setColor(Color.DKGRAY);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(5);
		}

		canvas.drawRect(0, 0, holderSize, getBottom(), paint);
		canvas.drawRect(getRight() - holderSize, 0, getRight(), getBottom(),
				paint);
	}

	@Override
	public void beforeRemove(int size, int deletingIndex, int currentIndex) {
		if (currentIndex == deletingIndex) {
			if (currentIndex == size - 1) {
				setCurrentItem(currentIndex - 1);
			} else if (currentIndex == 0) {
				setCurrentItem(1);
			}
		}
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		if (((GroupAdapter) getAdapter()).isInstantiate()) {
			((GroupAdapter) getAdapter()).setInstantiate(false);

			super.addView(child, index, params);
		} else {
			((GroupAdapter) getAdapter()).addView((TileGroup) child);
		}
	}
}