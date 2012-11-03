package tw.kewang.ui.tile;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewDebug;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

/**
 * @author kewang
 */
public class TileGroup extends FrameLayout {
	private Context context;
	private Point location;
	private int maxCells;
	private boolean canCellIn;
	private boolean canCellOut;
	private ArrayList<View> widgets;
	private SparseArray<CellProperty> properties;
	private Paint paint;

	public TileGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context, attrs);
	}

	public TileGroup(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
	}

	public TileGroup(Context context) {
		super(context);

		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.TileAttrs);

			location = new Point((int) a.getDimension(R.styleable.TileAttrs_x,
					0), (int) a.getDimension(R.styleable.TileAttrs_y, 0));
			canCellIn = a.getBoolean(R.styleable.TileAttrs_canCellIn, false);
			canCellOut = a.getBoolean(R.styleable.TileAttrs_canCellOut, false);
			maxCells = a.getInt(R.styleable.TileAttrs_maxCells, 0);

			a.recycle();
		} else {
			location = new Point(0, 0);
			canCellIn = false;
			canCellOut = false;
			maxCells = 0;
		}

		this.context = context;

		widgets = new ArrayList<View>();
		properties = new SparseArray<CellProperty>();
	}

	/**
	 * 新增TileCell至指定的位置
	 * 
	 * @param cell
	 *            要新增的TileCell
	 * @param index
	 *            要新增的TileCell位置
	 */
	public void addCell(TileCell cell, int index) {
		if (properties.size() == index) {
			throw new ArrayIndexOutOfBoundsException("已超過最大Cell數");
		}

		LayoutParams lp = new FrameLayout.LayoutParams(
				properties.get(index).width, properties.get(index).height);

		cell.setLayoutParams(lp);

		addView(cell, cell.getLayoutParams());
	}

	/**
	 * 新增TileCell至最後一個
	 * 
	 * @param cell
	 *            要新增的TileCell
	 */
	public void addCell(TileCell cell) {
		addCell(cell, getCellPropertySize(false));
	}

	/**
	 * 移除TileCell
	 * 
	 * @param cell
	 *            要移除的TileCell
	 */
	public void removeCell(TileCell cell) {
		for (int i = 0; i < properties.size(); i++) {
			CellProperty property = properties.get(i);

			if (property.cell == cell) {
				property.cell = null;

				break;
			}
		}

		removeView(cell);
	}

	@ViewDebug.ExportedProperty
	public int getMaxCellSize() {
		return maxCells;
	}

	/**
	 * 設定TileGroup可以容納的最大TileCell數
	 * 
	 * @param maxCells
	 *            最大的TileCell數量
	 */
	public void setMaxCellSize(int maxCells) {
		this.maxCells = maxCells;
	}

	public void addCellProperty(CellProperty property) {
		if (properties.size() == maxCells) {
			throw new ArrayIndexOutOfBoundsException("已超過最大Cell數");
		}

		property.group = this;

		properties.put(properties.size(), property);

		invalidate();
	}

	/**
	 * 傳回所有Cell
	 * 
	 * @return
	 */
	public ArrayList<TileCell> getCells() {
		ArrayList<TileCell> cells = new ArrayList<TileCell>();

		for (int i = 0; i < properties.size(); i++) {
			TileCell cell = properties.get(i).cell;

			if (cell != null) {
				cells.add(cell);
			}
		}

		return cells;
	}

	/**
	 * 傳回所有CellProperty
	 * 
	 * @return
	 */
	public SparseArray<CellProperty> getCellProperties() {
		return properties;
	}

	/**
	 * @param empty
	 *            CellProperty內的cell是否為empty
	 * @return 傳回所有CellProperty的個數
	 */
	public int getCellPropertySize(boolean empty) {
		int count = 0;

		for (int i = 0; i < properties.size(); i++) {
			if ((properties.get(i).cell == null) == empty) {
				count++;
			}
		}

		return count;
	}

	/**
	 * 新增View
	 * 
	 * @param widget
	 *            要新增的View
	 * @param x
	 *            x軸座標
	 * @param y
	 *            y軸座標
	 */
	public void addWidget(View widget, int x, int y) {
		LayoutParams lp = (FrameLayout.LayoutParams) widget.getLayoutParams();

		lp.leftMargin = x;
		lp.topMargin = y;

		addView(widget, lp);
	}

	/**
	 * @return 傳回所有的Widget
	 */
	public ArrayList<View> getWidgets() {
		return widgets;
	}

	/**
	 * 設定TileGroup的寬高
	 * 
	 * @param width
	 *            寬
	 * @param height
	 *            高
	 */
	public void setSize(int width, int height) {
		if (getLayoutParams() == null) {
			setLayoutParams(new FrameLayout.LayoutParams(width, height));
		} else {
			getLayoutParams().width = width;
			getLayoutParams().height = height;
		}
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;

		((FrameLayout.LayoutParams) getLayoutParams()).leftMargin = location.x;
		((FrameLayout.LayoutParams) getLayoutParams()).topMargin = location.y;

		invalidate();
	}

	public void setLocation(int x, int y) {
		location.set(x, y);

		((FrameLayout.LayoutParams) getLayoutParams()).leftMargin = x;
		((FrameLayout.LayoutParams) getLayoutParams()).topMargin = y;

		invalidate();
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		if (child instanceof TileCell) {
			Point location = null;
			CellProperty property = null;

			if (child.getLayoutParams() == null) {
				property = new CellProperty();

				property.location = ((TileCell) child).getLocation();
				property.width = params.width;
				property.height = params.height;

				addCellProperty(property);

				location = ((TileCell) child).getLocation();
			} else {
				CellProperty tmp;

				for (int i = 0; i < properties.size(); i++) {
					tmp = properties.get(i - 1);
					property = properties.get(i);

					// 只有一個
					if (tmp == null && properties.size() == 1) {
						location = property.location;

						break;
					} else if (tmp == null && property.cell == null
							& properties.size() != 1) {
						location = property.location;

						break;
					} else if (tmp != null && tmp.cell != null
							&& property.cell == null) {
						location = property.location;

						break;
					}
				}
			}
			property.cell = (TileCell) child;

			((FrameLayout.LayoutParams) params).leftMargin = location.x;
			((FrameLayout.LayoutParams) params).topMargin = location.y;
		} else if (child instanceof View) {
			widgets.add(child);
		}

		super.addView(child, params);
	}

	/**
	 * @return 是否允許TileCell進入
	 */
	public boolean canCellIn() {
		return canCellIn;
	}

	/**
	 * @param canCellIn
	 *            是否允許TileCell進入
	 */
	public void setCanCellIn(boolean canCellIn) {
		this.canCellIn = canCellIn;
	}

	/**
	 * @return 是否允許TileCell離開
	 */
	public boolean canCellOut() {
		return canCellOut;
	}

	/**
	 * @param canCellOut
	 *            是否允許TileCell離開
	 */
	public void setCanCellOut(boolean canCellOut) {
		this.canCellOut = canCellOut;
	}

	/**
	 * 開始動畫
	 * 
	 * @param resId
	 *            動畫的Resource Id
	 */
	public void startCellAnimation(int resId) {
		Animation animation = AnimationUtils.loadAnimation(context, resId);

		for (int i = 0; i < properties.size(); i++) {
			TileCell cell = properties.get(i).cell;

			if (cell != null) {
				cell.startAnimation(animation);
			}
		}
	}

	/**
	 * 停止動畫
	 */
	public void stopCellAnimation() {
		for (int i = 0; i < properties.size(); i++) {
			TileCell cell = properties.get(i).cell;

			if (cell != null) {
				cell.clearAnimation();
			}
		}
	}

	/**
	 * @return 是否為Sliding
	 */
	public boolean hasSliding() {
		return (getParent() instanceof Sliding);
	}

	/**
	 * 取得離(x, y)點最近的CellProperty
	 * 
	 * @param x
	 *            x軸座標
	 * @param y
	 *            y軸座標
	 * @return 離(x, y)點最近的CellProperty
	 */
	public CellProperty getNearestCellProperty(int x, int y) {
		int shortest = Integer.MAX_VALUE;
		int index = -1;

		for (int i = 0; i < properties.size(); i++) {
			int centerDistance = properties.get(i).getCenterDistance(x, y);

			if (shortest > centerDistance) {
				shortest = centerDistance;

				index = i;
			}
		}

		return properties.get(index);
	}

	/**
	 * 取得TileGroup與Tile的偏移值
	 * 
	 * @return [0]: x軸座標 [1]: y軸座標
	 */
	public int[] computeOffset() {
		int x;
		int y;

		if (hasSliding()) {
			x = ((Sliding) getParent()).getLeft();
			y = ((Sliding) getParent()).getTop();
		} else {
			x = getLeft();
			y = getTop();
		}

		return new int[] { x, y };
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (paint == null) {
			paint = new Paint();

			paint.setAntiAlias(true);
			paint.setColor(Color.DKGRAY);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(10);
		}

		for (int i = 0; i < properties.size(); i++) {
			CellProperty property = properties.get(i);

			if (property.background != null) {
				canvas.drawBitmap(property.background, property.location.x,
						property.location.y, paint);
			} else {
				canvas.drawRect(property.location.x, property.location.y,
						property.location.x + property.width,
						property.location.y + property.height, paint);
			}
		}
	}

	public static class CellProperty {
		public Bitmap background;
		public TileGroup group;
		public TileCell cell;
		public Point location;
		public int width;
		public int height;

		public int getCenterDistance(int x, int y) {
			int[] offset = group.computeOffset();

			return (int) (Math.pow(location.x / 2 + offset[0] - x, 2) + Math
					.pow(location.y / 2 + offset[1] - y, 2));
		}
	}
}