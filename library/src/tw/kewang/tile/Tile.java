package tw.kewang.tile;

import java.util.ArrayList;

import tw.kewang.tile.TileGroup.CellProperty;
import tw.kewang.tile.ViewAnimation.Runner;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author kewang
 */
public class Tile extends FrameLayout {
	public static final int DRAG_UNSPECIFIED = -1;
	public static final int DRAG_START = 0;
	public static final int DRAG_STOP = 1;

	private static final int ANIMATION_DURATION = 500;
	private static final int SCALE_DURATION = 50;

	private ArrayList<TileGroup> groups;
	private ArrayList<View> widgets;
	private ArrayList<Sliding> slidings;
	private ImageView fakeCell;
	private Context context;
	private TileCell focusCell;
	private CellProperty focusCellProperty;
	private int dragStatus;
	private boolean longPress;
	private boolean slidingInQueue;
	private int holderSize;
	private SlidingRunnable slidingRunnable;
	private OnClickListener clickListener;
	private OnLongClickListener longClickListener;
	private OnDragListener dragListener;
	private OnExchangeListener exchangeListener;
	private OnFillSlidingListener fillSlidingListener;

	public Tile(Context context) {
		super(context);

		init(context, null);
	}

	public Tile(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
	}

	public Tile(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.TileAttrs);

			holderSize = (int) a.getDimension(R.styleable.TileAttrs_holderSize,
					10);

			a.recycle();
		}

		this.context = context;

		groups = new ArrayList<TileGroup>();
		widgets = new ArrayList<View>();
		slidings = new ArrayList<Sliding>();
		dragStatus = DRAG_UNSPECIFIED;

		clearCellFocus();
	}

	/**
	 * 新增TileGroup
	 * 
	 * @param group
	 *            要新增的TileGroup
	 * @param x
	 *            x軸座標
	 * @param y
	 *            y軸座標
	 */
	public void addGroup(TileGroup group, int x, int y) {
		group.setLocation(x, y);

		addView(group, group.getLayoutParams());
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
	 * 移除TileGroup
	 * 
	 * @param group
	 *            要移除的TileGroup
	 */
	public void removeGroup(TileGroup group) {
		groups.remove(group);

		removeView(group);
	}

	/**
	 * @return 傳回所有的TileGroup
	 */
	public ArrayList<TileGroup> getGroups() {
		return getGroups(false);
	}

	/**
	 * @param includeSliding
	 *            是否包含Sliding內的TileGroup
	 * @return 傳回所有的TileGroup
	 */
	public ArrayList<TileGroup> getGroups(boolean includeSliding) {
		if (includeSliding) {
			ArrayList<TileGroup> tmpGroups = new ArrayList<TileGroup>();

			tmpGroups.addAll(groups);

			for (Sliding sliding : getSlidings()) {
				tmpGroups.addAll(sliding.getGroups());
			}

			return tmpGroups;
		} else {
			return groups;
		}
	}

	/**
	 * @return 傳回所有的Sliding
	 */
	public ArrayList<Sliding> getSlidings() {
		return slidings;
	}

	/**
	 * @return 傳回所有的Widget
	 */
	public ArrayList<View> getWidgets() {
		return widgets;
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		if (child instanceof TileGroup) {
			// TileGroup不為sliding，則直接新增TileGroup
			((FrameLayout.LayoutParams) params).leftMargin = ((TileGroup) child)
					.getLocation().x;
			((FrameLayout.LayoutParams) params).topMargin = ((TileGroup) child)
					.getLocation().y;

			groups.add((TileGroup) child);
		} else if (child instanceof Sliding) {
			((Sliding) child).setHolderSize(holderSize);

			slidings.add((Sliding) child);
		} else if (child instanceof View) {
			widgets.add(child);
		}

		super.addView(child, params);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// 決定是否將TouchEvent傳至child，或在這層處理
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			for (Sliding sliding : slidings) {
				if (isInSliding(event, sliding)) {
					for (int i = 0; i < sliding.getCurrentGroup()
							.getCellProperties().size(); i++) {
						CellProperty property = sliding.getCurrentGroup()
								.getCellProperties().get(i);

						if (isInCellProperty(event, property)
								&& property.cell != null) {
							// 目標點為CellProperty
							return true;
						}
					}

					// 目標點不為CellProperty
					return false;
				}
			}

			for (TileGroup group : groups) {
				if (isInGroup(event, group)) {
					for (int i = 0; i < group.getCellProperties().size(); i++) {
						CellProperty property = group.getCellProperties()
								.get(i);

						if (isInCellProperty(event, property)
								&& property.cell != null) {
							// 目標點為CellProperty
							return true;
						}
					}
				}
			}

			// 判斷是否點下Widget
			return !isInAnyWidget(event);
		} else {
			return super.onInterceptTouchEvent(event);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean inGroup = false;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			inGroup = false;

			for (Sliding sliding : slidings) {
				if (isInSliding(event, sliding)) {
					inGroup = downGroup(event, inGroup,
							sliding.getCurrentGroup());

					break;
				}
			}

			if (!inGroup) {
				for (TileGroup group : groups) {
					inGroup = downGroup(event, inGroup, group);
				}
			}

			return true;
		case MotionEvent.ACTION_MOVE:
			// 如果有FocusCell
			if (isFocusCell()) {
				// 如果為拖拉
				if (dragStatus == DRAG_START) {
					removeCallbacks(longPressRunnable);

					buildFakeCell();

					moveFakeCell(event);

					moveCell(event);

					fillSlidingCell(event);

					moveSliding(event);
				} else {
					// 如果不為拖拉，且目標點不在Cell內
					if (!isInCellProperty(event, focusCellProperty)) {
						removeCallbacks(longPressRunnable);

						clearCellFocus();
					}
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			// 如果有FocusCell
			if (isFocusCell()) {
				inGroup = false;

				for (Sliding sliding : slidings) {
					if (isInSliding(event, sliding)) {
						inGroup = upGroup(event, inGroup,
								sliding.getCurrentGroup());

						break;
					}
				}

				for (TileGroup group : groups) {
					// if (!inGroup) {
					inGroup = upGroup(event, inGroup, group);
					// }
				}

				// 目標點不在Group內
				if (!inGroup) {
					if (dragStatus == DRAG_START) {
						rejectMove();

						if (dragListener != null) {
							dragListener.onDragStop(null, focusCell, false);
						}
					}
				}

				removeSlidingRunnable();
			}

			break;
		}

		return super.onTouchEvent(event);
	}

	private void fillSlidingCell(MotionEvent event) {
		for (Sliding sliding : slidings) {
			// 目標點在Sliding內，並且可以autoFill
			if (isInSliding(event, sliding) && sliding.canAutoFill()) {
				TileGroup current = sliding.getCurrentGroup();

				for (TileGroup sibling : sliding.getGroups()) {
					// 目前Group可存放Cell為0個，且鄰近Group可存放Cell非0個
					if (current != sibling
							&& current.getCellProperties().size() != 0
							&& current.getCellPropertySize(true) == 0
							&& sibling.getCellPropertySize(true) != 0) {
						// 取出離目標點最近的CellProperty
						CellProperty nearest = current.getNearestCellProperty(
								(int) event.getX(), (int) event.getY());
						// 取出目前Group的最後一個Cell
						TileCell cell = current.getCellProperties().get(
								current.getCellProperties().size() - 1).cell;

						if (cell != null) {
							// 依序將鄰近Group的Cell往後移一個
							for (int j = sibling.getCellPropertySize(false) - 1; j >= 0; j--) {
								CellProperty to = sibling.getCellProperties()
										.get(j + 1);

								switchCell(sibling.getCellProperties().get(j),
										to);
								setCellLayoutParams(
										(FrameLayout.LayoutParams) to.cell
												.getLayoutParams(),
										to);
							}

							// 移除目前Group的最後一個Cell，並加至鄰近Group的第一個Cell
							current.removeCell(cell);

							sibling.addCell(cell, 0);

							for (int j = current.getCellProperties()
									.indexOfValue(nearest); j < current
									.getCellPropertySize(false); j++) {
								switchCellTo(
										current.getCellProperties().get(j),
										current.getCellProperties().get(j + 1));
							}
						}

						break;
					}
				}

				break;
			}
		}
	}

	private void switchCell(CellProperty from, CellProperty to) {
		to.cell = from.cell;
		from.cell = null;
	}

	private void moveSliding(MotionEvent event) {
		boolean inSliding = false;

		for (Sliding sliding : slidings) {
			if (isInSliding(event, sliding)) {
				inSliding = true;

				if (slidingRunnable == null) {
					slidingRunnable = new SlidingRunnable();
				}

				// 將slidingRunnable排入Thread內
				if (event.getX() > (sliding.getRight() - holderSize)
						&& event.getX() < sliding.getRight()) {
					slidingRunnableToQueue(sliding,
							SlidingRunnable.DIRECTION_NEXT);
				} else if (event.getX() > sliding.getLeft()
						&& event.getX() < (sliding.getLeft() + holderSize)) {
					slidingRunnableToQueue(sliding,
							SlidingRunnable.DIRECTION_PREVIOUS);
				} else {
					removeSlidingRunnable();
				}

				break;
			}
		}

		if (!inSliding) {
			removeSlidingRunnable();
		}
	}

	private void removeSlidingRunnable() {
		removeCallbacks(slidingRunnable);

		slidingInQueue = false;
	}

	private void slidingRunnableToQueue(Sliding sliding, int direction) {
		slidingRunnable.direction = direction;
		slidingRunnable.sliding = sliding;

		if (!slidingInQueue) {
			postDelayed(slidingRunnable,
					ViewConfiguration.getLongPressTimeout());

			slidingInQueue = true;
		}
	}

	private boolean downGroup(MotionEvent event, boolean inGroup,
			TileGroup group) {
		boolean inCellProperty;

		// 目標點在Group內
		if (isInGroup(event, group)) {
			inGroup = true;

			inCellProperty = false;

			for (int i = 0; i < group.getCellProperties().size(); i++) {
				CellProperty property = group.getCellProperties().get(i);

				// 目標點在該Group的CellProperty內，且有cell
				if (isInCellProperty(event, property) && property.cell != null) {
					inCellProperty = true;

					focusCell = property.cell;
					focusCellProperty = property;

					if (dragStatus != DRAG_START) {
						// 將longPress排入Thread內
						postDelayed(longPressRunnable,
								ViewConfiguration.getLongPressTimeout());
					} else {
						// 開始做drag
						if (dragListener != null) {
							dragListener.onDragStart(group, focusCell);
						}
					}

					break;
				}
			}

			if (!inCellProperty) {
				clearCellFocus();
			}
		}

		// 目標點不在Group內
		if (!inGroup) {
			Log.d("Tile", "outerGroup");
		}

		return inGroup;
	}

	private boolean upGroup(MotionEvent event, boolean inGroup, TileGroup group) {
		boolean originalInGroup = inGroup;

		if (isInGroup(event, group)) {
			inGroup = true;

			// 如果為拖拉
			if (dragStatus == DRAG_START) {
				stopMoveCell(group);

				if (dragListener != null) {
					dragListener.onDragStop(group, focusCell, false);
				}
			} else {
				if (!originalInGroup) {
					// 如果不為拖拉
					if (clickListener != null) {
						clickListener.onClick(focusCell.getGroup(), focusCell);
					}
				}
			}
		}

		return inGroup;
	}

	private void clearCellFocus() {
		focusCell = null;
		focusCellProperty = null;
		longPress = false;
	}

	private void rejectMove() {
		if (fakeCell != null) {
			int[] offset = focusCellProperty.group.computeOffset();
			int startX = Math.min(getWidth() - fakeCell.getLeft(), 0);
			int startY = Math.min(getHeight() - fakeCell.getTop(), 0);

			ViewAnimation.run(new TranslateAnimation(startX,
					focusCellProperty.location.x - fakeCell.getLeft()
							+ offset[0], startY, focusCellProperty.location.y
							- fakeCell.getTop() + offset[1]), fakeCell,
					ANIMATION_DURATION, new Runner() {
						@Override
						public void onAnimationEnd(View v) {
							if (dragListener != null) {
								dragListener.onDragStop(focusCell.getGroup(),
										focusCell, true);
							}

							removeFakeCell();

							focusCell.setVisibility(VISIBLE);

							clearCellFocus();
						}
					});

			// 因為fakeCell在畫面外時無法重繪，所以要強制重繪使動畫啟動
			invalidate();
		}
	}

	private void removeFakeCell() {
		removeView(fakeCell);

		fakeCell = null;
	}

	private void stopMoveCell(TileGroup group) {
		TileGroup focusGroup = focusCell.getGroup();

		// 目標點的Group與按下時的Group相同
		if (group == focusGroup) {
			resetCellLayoutParams();

			rejectMove();
		} else {
			// 目標點的Group與按下時的Group不同，且TileGroup可進出
			if (focusGroup.canCellOut() && group.canCellIn()) {
				// 目標點的Group可放置的PropertySize為0
				if (group.getCellPropertySize(true) == 0) {
					rejectMove();
				} else {
					// 直接變更cell的Group
					changeCellGroup(focusGroup, group);
				}
			} else {
				// 目標點的Group與按下時的Group不同，且TileGroup不可進出
				rejectMove();
			}
		}
	}

	private void changeCellGroup(final TileGroup from, final TileGroup to) {
		CellProperty property = null;
		SparseArray<CellProperty> properties = to.getCellProperties();

		// 找出第一個沒有Cell的CellProperty
		for (int i = 0; i < properties.size(); i++) {
			property = properties.get(i);

			if (property.cell == null) {
				break;
			}
		}

		int[] offset = property.group.computeOffset();

		ViewAnimation.run(new TranslateAnimation(0, property.location.x
				- fakeCell.getLeft() + offset[0], 0, property.location.y
				- fakeCell.getTop() + offset[1]), fakeCell, ANIMATION_DURATION,
				new Runner() {
					@Override
					public void onAnimationEnd(View v) {
						removeFakeCell();

						from.removeCell(focusCell);

						to.addCell(focusCell);

						focusCell.setVisibility(VISIBLE);

						if (dragListener != null) {
							dragListener.onDragStop(to, focusCell, true);
						}
					}
				});
	}

	private void resetCellLayoutParams() {
		LayoutParams lp = (FrameLayout.LayoutParams) focusCell
				.getLayoutParams();

		setCellLayoutParams(lp, focusCellProperty);

		focusCell.setLayoutParams(lp);
	}

	private void buildFakeCell() {
		if (fakeCell == null) {
			focusCell.setDrawingCacheEnabled(true);

			fakeCell = new ImageView(context);

			if (focusCell.getDrawable() != null) {
				fakeCell.setImageBitmap(Bitmap
						.createBitmap(((BitmapDrawable) focusCell.getDrawable())
								.getBitmap()));
			} else {
				fakeCell.setImageBitmap(Bitmap.createBitmap(focusCell
						.getDrawingCache()));
			}

			fakeCell.setLayoutParams(new FrameLayout.LayoutParams(focusCell
					.getLayoutParams()));

			focusCell.setVisibility(INVISIBLE);

			addView(fakeCell);
		}
	}

	private void moveFakeCell(MotionEvent event) {
		LayoutParams lp = (FrameLayout.LayoutParams) fakeCell.getLayoutParams();

		lp.leftMargin = (int) event.getX() - focusCellProperty.width / 2;
		lp.topMargin = (int) event.getY() - focusCellProperty.height / 2;

		fakeCell.setLayoutParams(lp);

		invalidate();
	}

	private void moveCell(MotionEvent event) {
		for (Sliding sliding : slidings) {
			if (isInSliding(event, sliding)) {
				moveCellInGroup(event, sliding.getCurrentGroup());
			}
		}

		for (TileGroup group : groups) {
			moveCellInGroup(event, group);
		}
	}

	private void moveCellInGroup(MotionEvent event, TileGroup group) {
		TileGroup to = group;
		TileGroup from = focusCell.getGroup();

		if (isInGroup(event, to)) {
			int toEndPos = -1;
			int toStartPos = -1;
			int fromEndPos = -1;
			int fromStartPos = -1;

			for (int i = 0; i < from.getCellProperties().size(); i++) {
				CellProperty property = from.getCellProperties().get(i);

				if (isInCellProperty(event, property) && property.cell != null
						&& property.cell != focusCell) {
					// 取得終點
					fromEndPos = i;
				} else if (focusCellProperty == property) {
					// 取得起點
					fromStartPos = i;
				}

				if (fromEndPos != -1 && fromStartPos != -1) {
					break;
				}
			}

			// 如果來源及目的TileGroup不同
			if (to != from) {
				for (int i = 0; i < to.getCellProperties().size(); i++) {
					if (to.getCellProperties().get(i).cell == focusCell) {
						toStartPos = i;

						break;
					}
				}

				toEndPos = to.getCellProperties().size() - 1;

				if (to.canCellIn() && to.getCellPropertySize(true) != 0) {
					fromEndPos = from.getCellPropertySize(false) - 1;
				}
			}

			// 如果有終點
			if (fromEndPos != -1 && fromStartPos != -1) {
				// 暫存focusCell
				TileCell tempCell = focusCell;

				// 終點大於起點
				if (fromEndPos > fromStartPos) {
					for (int i = fromStartPos; i < fromEndPos; i++) {
						switchCellTo(from.getCellProperties().get(i + 1), from
								.getCellProperties().get(i));
					}
				} else {
					// 終點小於起點
					for (int i = fromStartPos; i > fromEndPos; i--) {
						switchCellTo(from.getCellProperties().get(i - 1), from
								.getCellProperties().get(i));
					}
				}

				focusCell = tempCell;

				focusCellProperty = from.getCellProperties().get(fromEndPos);

				focusCellProperty.cell = focusCell;

				resetCellLayoutParams();

				if (focusCellProperty.width != fakeCell.getWidth()
						|| focusCellProperty.height != fakeCell.getHeight()) {
					// FIXME: 換至不同Group時會回(0,0)
					scaleFakeCell();
				}
			}
		}
	}

	private void switchCellTo(CellProperty from, final CellProperty to) {
		AnimationSet animSet = new AnimationSet(true);
		float xMul = (float) from.width / to.width;
		float yMul = (float) from.height / to.height;

		animSet.addAnimation(new TranslateAnimation(0,
				(to.location.x - from.location.x) * xMul, 0,
				(to.location.y - from.location.y) * yMul));
		animSet.addAnimation(new ScaleAnimation(1, 1 / xMul, 1, 1 / yMul));

		switchCell(from, to);

		ViewAnimation.run(animSet, to.cell, ANIMATION_DURATION, new Runner() {
			@Override
			public void onAnimationEnd(View v) {
				setCellLayoutParams(
						(FrameLayout.LayoutParams) v.getLayoutParams(), to);

				to.group.requestLayout();
				to.group.invalidate();

				invalidate();
			}
		});
	}

	private void scaleFakeCell() {
		ViewAnimation.run(new ScaleAnimation(1, (float) focusCellProperty.width
				/ fakeCell.getWidth(), 1, (float) focusCellProperty.height
				/ fakeCell.getHeight()), fakeCell, SCALE_DURATION,
				new Runner() {
					@Override
					public void onAnimationEnd(View v) {
						v.setLayoutParams(new FrameLayout.LayoutParams(
								focusCell.getLayoutParams()));

						focusCell.setVisibility(INVISIBLE);
					}
				});
	}

	private void setCellLayoutParams(LayoutParams lp, CellProperty property) {
		lp.width = property.width;
		lp.height = property.height;
		lp.leftMargin = property.location.x;
		lp.topMargin = property.location.y;
	}

	private boolean isInAnyWidget(MotionEvent event) {
		for (TileGroup group : groups) {
			if (isInGroup(event, group)) {
				for (View widget : group.getWidgets()) {
					if (isInWidget(event, widget)) {
						return true;
					}
				}
			}
		}

		for (View widget : widgets) {
			if (isInner(event, widget.getLeft(), widget.getTop(),
					widget.getRight(), widget.getBottom())) {
				return true;
			}
		}

		return false;
	}

	private boolean isInner(MotionEvent event, int l, int t, int r, int b) {
		return l <= event.getX() && t <= event.getY() && r >= event.getX()
				&& b >= event.getY();
	}

	private boolean isInWidget(MotionEvent event, View widget) {
		int offsetX = ((TileGroup) widget.getParent()).getLeft();
		int offsetY = ((TileGroup) widget.getParent()).getTop();

		return isInner(event, offsetX + widget.getLeft(),
				offsetY + widget.getTop(), offsetX + widget.getRight(), offsetY
						+ widget.getBottom());
	}

	private boolean isInCellProperty(MotionEvent event, CellProperty property) {
		int[] offset = property.group.computeOffset();
		int offsetX = offset[0] + property.location.x;
		int offsetY = offset[1] + property.location.y;

		return isInner(event, offsetX, offsetY, offsetX + property.width,
				offsetY + property.height);
	}

	private boolean isInGroup(MotionEvent event, TileGroup group) {
		int left;
		int top;
		int right;
		int bottom;

		if (group.getVisibility() == View.VISIBLE) {
			if (group.hasSliding()) {
				left = ((Sliding) group.getParent()).getLeft();
				top = ((Sliding) group.getParent()).getTop();
				right = ((Sliding) group.getParent()).getRight();
				bottom = ((Sliding) group.getParent()).getBottom();
			} else {
				left = group.getLeft();
				top = group.getTop();
				right = group.getRight();
				bottom = group.getBottom();
			}

			return isInner(event, left, top, right, bottom);
		} else {
			return false;
		}
	}

	private boolean isInSliding(MotionEvent event, Sliding sliding) {
		return isInner(event, sliding.getLeft(), sliding.getTop(),
				sliding.getRight(), sliding.getBottom());
	}

	private boolean isFocusCell() {
		return focusCell != null;
	}

	/**
	 * 開始拖拉
	 */
	public void startDrag() {
		dragStatus = DRAG_START;
	}

	/**
	 * 停止拖拉
	 */
	public void stopDrag() {
		dragStatus = DRAG_STOP;
	}

	/**
	 * 設定TileCell的Drag Listener
	 * 
	 * @param listener
	 *            設定TileCell的Drag Listener
	 */
	public void setOnCellDragListener(OnDragListener listener) {
		dragListener = listener;
	}

	/**
	 * 設定TileCell的LongClick Listener
	 * 
	 * @param listener
	 *            設定TileCell的LongClick Listener
	 */
	public void setOnCellLongClickListener(OnLongClickListener listener) {
		longClickListener = listener;
	}

	/**
	 * 設定TileCell的Click Listener
	 * 
	 * @param listener
	 *            設定TileCell的Click Listener
	 */
	public void setOnCellClickListener(OnClickListener listener) {
		clickListener = listener;
	}

	/**
	 * 設定TileCell的Exchange Listener
	 * 
	 * @param listener
	 *            設定TileCell的Click Listener
	 */
	public void setOnExchangeListener(OnExchangeListener listener) {
		exchangeListener = listener;
	}

	private Runnable longPressRunnable = new Runnable() {
		@Override
		public void run() {
			if (!longPress) {
				if (isFocusCell() && !focusCell.isLongClickable()
						&& longClickListener != null) {
					performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

					longClickListener.onLongClick(focusCell.getGroup(),
							focusCell);

					longPress = true;
				}
			}
		}
	};

	private class SlidingRunnable implements Runnable {
		private static final int DIRECTION_PREVIOUS = -1;
		private static final int DIRECTION_NEXT = 1;

		private int direction;
		private Sliding sliding;

		@Override
		public void run() {
			if (direction == DIRECTION_PREVIOUS && sliding.getCurrentItem() > 0) {
				performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

				sliding.previous();
			} else if (direction == DIRECTION_NEXT
					&& sliding.getCurrentItem() < sliding.getChildCount() - 1) {
				performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

				sliding.next();
			}
		}
	}

	public interface OnClickListener {
		/**
		 * @param group
		 * @param cell
		 */
		public void onClick(TileGroup group, TileCell cell);
	}

	public interface OnLongClickListener {
		/**
		 * @param group
		 * @param cell
		 */
		public void onLongClick(TileGroup group, TileCell cell);
	}

	public interface OnDragListener {
		/**
		 * @param group
		 * @param cell
		 */
		public void onDragStart(TileGroup group, TileCell cell);

		/**
		 * @param group
		 * @param cell
		 * @param animation
		 */
		public void onDragStop(TileGroup group, TileCell cell, boolean animation);
	}

	public interface OnExchangeListener {
		public boolean onExchange(TileCell from, TileCell to);
	}

	public interface OnFillSlidingListener {
		public TileGroup onAddTileGroup(TileGroup current);

		public TileCell onAddTileCell(TileGroup current);
	}
}